from dataclasses import dataclass

import torch
import torch.nn as nn
import torch.nn.functional as F

import cv2
import os
import numpy as np

@dataclass
class ModelArgs:
    vocab_size: int = 256
    d_model: int = 256
    n_heads: int = 8
    n_layers: int = 24
    hidden_dim: int = 768
    max_seq_len: int = 4096
    dropout: float = 0.0

def dump_tensor(tens, name = "unknown"):
    nparr = tens.detach().cpu().numpy()
    print(f"{name}: {nparr.shape}, dtype={nparr.dtype}")
    return nparr.tobytes()

class RMSNorm(nn.Module):
    def __init__(self, dim: int, eps: float = 1e-6):
        super().__init__()
        self.eps = eps
        self.weight = nn.Parameter(torch.ones(dim))

    def dump_bytes(self):
        return dump_tensor(self.weight.data, "rmsnorm")

    def forward(self, x):
        # Calculate RMSNorm: x / sqrt(mean(x^2) + eps) * weight
        norm = torch.rsqrt(x.pow(2).mean(-1, keepdim=True) + self.eps)
        return x * norm * self.weight

def precompute_freqs_cis(dim: int, end: int, theta: float = 10000.0):
    # RoPE precomputation (Real numbers for Torchinductor compatibility)
    freqs = 1.0 / (theta ** (torch.arange(0, dim, 2)[: (dim // 2)].float() / dim))
    t = torch.arange(end, device=freqs.device, dtype=torch.float32)
    freqs = torch.outer(t, freqs)
    # Stack cos and sin to replace complex representation
    freqs_cis = torch.stack([torch.cos(freqs), torch.sin(freqs)], dim=-1)
    return freqs_cis

def apply_rotary_emb(xq, xk, freqs_cis):
    # Extract real and imaginary parts
    xq_r, xq_i = xq.float().reshape(*xq.shape[:-1], -1, 2).unbind(-1)
    xk_r, xk_i = xk.float().reshape(*xk.shape[:-1], -1, 2).unbind(-1)
    
    # Extract cos and sin from freqs_cis and broadcast
    freqs_cos, freqs_sin = freqs_cis[:xq.shape[1]].unbind(-1)
    freqs_cos = freqs_cos.view(1, xq.shape[1], 1, xq_r.shape[-1])
    freqs_sin = freqs_sin.view(1, xq.shape[1], 1, xq_r.shape[-1])
    
    # Apply rotation
    xq_out_r = xq_r * freqs_cos - xq_i * freqs_sin
    xq_out_i = xq_r * freqs_sin + xq_i * freqs_cos
    xk_out_r = xk_r * freqs_cos - xk_i * freqs_sin
    xk_out_i = xk_r * freqs_sin + xk_i * freqs_cos
    
    # Recombine and flatten
    xq_out = torch.stack([xq_out_r, xq_out_i], dim=-1).flatten(3)
    xk_out = torch.stack([xk_out_r, xk_out_i], dim=-1).flatten(3)
    
    return xq_out.type_as(xq), xk_out.type_as(xk)

class Attention(nn.Module):
    def __init__(self, args: ModelArgs):
        super().__init__()
        self.n_heads = args.n_heads
        self.head_dim = args.d_model // args.n_heads
        self.dropout_p = args.dropout
        
        self.wq = nn.Linear(args.d_model, args.d_model, bias=False)
        self.wk = nn.Linear(args.d_model, args.d_model, bias=False)
        self.wv = nn.Linear(args.d_model, args.d_model, bias=False)
        self.wo = nn.Linear(args.d_model, args.d_model, bias=False)

    def dump_bytes(self):
        return (dump_tensor(self.wq.weight, "wq") +
            dump_tensor(self.wk.weight, "wk") +
            dump_tensor(self.wv.weight, "wv") +
            dump_tensor(self.wo.weight, "wo"))

    def forward(self, x, freqs_cis):
        B, T, C = x.shape
        
        # Project Q, K, V
        xq = self.wq(x).view(B, T, self.n_heads, self.head_dim)
        xk = self.wk(x).view(B, T, self.n_heads, self.head_dim)
        xv = self.wv(x).view(B, T, self.n_heads, self.head_dim)

        # Apply Rotary Positional Embeddings
        xq, xk = apply_rotary_emb(xq, xk, freqs_cis)

        # Transpose for attention: (B, H, T, D)
        xq = xq.transpose(1, 2)
        xk = xk.transpose(1, 2)
        xv = xv.transpose(1, 2)

        # Flash attention or standard scaled dot product with causal mask
        output = F.scaled_dot_product_attention(xq, xk, xv, is_causal=True, dropout_p=self.dropout_p if self.training else 0.0)
        
        # Reshape back to (B, T, C)
        output = output.transpose(1, 2).contiguous().view(B, T, C)
        return self.wo(output)

class MLP(nn.Module):
    def __init__(self, args: ModelArgs):
        super().__init__()
        # SwiGLU requires 3 weight matrices
        self.w1 = nn.Linear(args.d_model, args.hidden_dim, bias=False)
        self.w2 = nn.Linear(args.hidden_dim, args.d_model, bias=False)
        self.w3 = nn.Linear(args.d_model, args.hidden_dim, bias=False)

    def dump_bytes(self):
        return (dump_tensor(self.w1.weight, "w1") +
            dump_tensor(self.w2.weight, "w2") +
            dump_tensor(self.w3.weight, "w3"))

    def forward(self, x):
        # SwiGLU activation: F.silu(w1(x)) * w3(x)
        return self.w2(F.silu(self.w1(x)) * self.w3(x))

class TransformerBlock(nn.Module):
    def __init__(self, args: ModelArgs):
        super().__init__()
        self.attention_norm = RMSNorm(args.d_model)
        self.attention = Attention(args)
        self.mlp_norm = RMSNorm(args.d_model)
        self.mlp = MLP(args)
        self.dropout = nn.Dropout(args.dropout)

    def dump_bytes(self):
        return self.attention_norm.dump_bytes() + self.attention.dump_bytes() + \
            self.mlp_norm.dump_bytes() + self.mlp.dump_bytes()

    def forward(self, x, freqs_cis):
        # Pre-norm architecture
        x = x + self.dropout(self.attention(self.attention_norm(x), freqs_cis))
        x = x + self.dropout(self.mlp(self.mlp_norm(x)))
        return x

class CharModel(nn.Module):
    def __init__(self, args: ModelArgs):
        super().__init__()
        self.args = args
        self.tok_embeddings = nn.Embedding(args.vocab_size, args.d_model)
        self.dropout = nn.Dropout(args.dropout)
        
        self.layers = nn.ModuleList([TransformerBlock(args) for _ in range(args.n_layers)])
        self.norm = RMSNorm(args.d_model)
        
        # Output projection
        self.output = nn.Linear(args.d_model, args.vocab_size, bias=False)
        
        # Tie embedding and unembedding weights
        self.output.weight = self.tok_embeddings.weight
        
        # Precompute RoPE frequencies (not treated as a parameter)
        freqs_cis = precompute_freqs_cis(args.d_model // args.n_heads, args.max_seq_len)
        self.register_buffer("freqs_cis", freqs_cis, persistent=False)
        
        self.apply(self._init_weights)

    def dump_bytes(self):
        return dump_tensor(self.tok_embeddings.weight, "embed") + \
            b"".join(layer.dump_bytes() for layer in self.layers) + self.norm.dump_bytes()

    def _init_weights(self, module):
        # Break symmetry: randomly initialize instead of identity matrix
        if isinstance(module, nn.Linear):
            torch.nn.init.normal_(module.weight, mean=0.0, std=0.02)
        elif isinstance(module, nn.Embedding):
            torch.nn.init.normal_(module.weight, mean=0.0, std=0.02)

    def forward(self, tokens, targets=None):
        B, T = tokens.shape
        h = self.tok_embeddings(tokens)
        h = self.dropout(h)
        
        freqs_cis = self.freqs_cis[:T]
        
        for layer in self.layers:
            h = layer(h, freqs_cis)
            
        h = self.norm(h)
        logits = self.output(h)
        
        loss = None
        if targets is not None:
            # Flatten predictions to calculate cross entropy loss
            loss = F.cross_entropy(logits.view(-1, logits.size(-1)), targets.view(-1), label_smoothing=0.01)
            
        return logits, loss

def statsof(arr):
    return f"{np.mean(arr):.6f} {np.std(arr):.6f} [{np.min(arr):.6f} {np.max(arr):.6f}]"


def visualize(params, fname="vis/"):
    def vis_dump(raw, name, size):
        print(f"\t{size} einsumming {name}! stats: {statsof(raw)}")
        mask = (raw < 0).astype(float)
        negs = np.array([0, 0, 1], dtype=">f")
        pos = np.array([0, 1, 0], dtype=">f")
        images = np.einsum("xy,xy,c->xyc", -mask, raw, negs) + np.einsum("xy,xy,c->xyc", 1-mask, raw, pos)

        write_matrices = True
        if write_matrices:
            mx = np.max(np.abs(images.flatten()))
            images *= 256 / (mx + 1e-4)
            name = f"{fname}{name}.png"
            print(name, os.getcwd())
            cv2.imwrite(name, images)
    
    for name, param in params:
        size = param.data.shape
        raw = param.data.cpu().detach().numpy()
        if len(size) == 1:
            raw = raw.reshape(1, size[0])
        if len(size) > 2:
            raw = raw.reshape(-1, size[0])
        if name == "tok_embeddings.weight":
            vis_dump(raw.T @ raw, name + ".corr_cols", size)
            vis_dump(raw @ raw.T, name + ".corr_rows", size)
        vis_dump(raw, name, size)

