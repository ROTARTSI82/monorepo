import math
import time
import torch
from charmodel import CharModel, ModelArgs

# ==============================================================================
# Hyperparameters
# ==============================================================================
batch_size = 16  # Reduced to fit 4096 ctx in VRAM
gradient_accumulation_steps = 4  # 16 * 4 = 64 effective batch size
max_seq_len = 4096  # Context window size
learning_rate = 1e-3
weight_decay = 0.1
beta1 = 0.9
beta2 = 0.95
max_iters = 5000
eval_interval = 200
eval_iters = 50
warmup_iters = 200
compile_model = True

device = 'cuda' if torch.cuda.is_available() else 'cpu'
if not torch.cuda.is_available() and torch.backends.mps.is_available():
    device = 'mps'

print(f"Using device: {device}")

# ==============================================================================
# Data Loading
# ==============================================================================
data_path = 'train.txt'

with open(data_path, 'rb') as f:
    data = f.read()

# Convert raw bytes directly to a PyTorch tensor without creating a giant Python list
train_data = torch.frombuffer(bytearray(data), dtype=torch.uint8).long()

print(f"Loaded data: ({len(train_data)} train")

def get_batch():
    data_source = train_data
    max_idx = len(data_source) - max_seq_len - 1
    
    if max_idx <= 0:
        # Fallback if the dataset is smaller than context length
        ix = torch.zeros(batch_size, dtype=torch.long)
    else:
        ix = torch.randint(max_idx, (batch_size,))
    
    x = torch.stack([data_source[i : i + max_seq_len] for i in ix])
    y = torch.stack([data_source[i + 1 : i + 1 + max_seq_len] for i in ix])
    return x.to(device), y.to(device)

# ==============================================================================
# Model Initialization
# ==============================================================================
args = ModelArgs(max_seq_len=max_seq_len, n_layers=24)
model = CharModel(args)
model.to(device)

print(f"Model parameters: {sum(p.numel() for p in model.parameters()) / 1e6:.2f} M")

if compile_model and device != 'mps':
    print("Compiling model (this may take a minute)...")
    try:
        model = torch.compile(model)
        print("Model compiled successfully.")
    except Exception as e:
        print(f"torch.compile failed (running eagerly): {e}")

# ==============================================================================
# Optimizer (AdamW)
# ==============================================================================
# Separate parameters into those that should be weight decayed and those that shouldn't
decay = set()
no_decay = set()
for mn, m in model.named_modules():
    for pn, p in m.named_parameters(recurse=False):
        fpn = f'{mn}.{pn}' if mn else pn
        if fpn.endswith('weight') and (isinstance(m, torch.nn.Linear) or isinstance(m, torch.nn.Embedding)):
            decay.add(fpn)
        elif fpn.endswith('weight') and 'norm' in fpn.lower():
            no_decay.add(fpn)

param_dict = {pn: p for pn, p in model.named_parameters()}
# Filter out tied weights that were removed by deduplication in named_parameters
decay = {pn for pn in decay if pn in param_dict}
no_decay = {pn for pn in no_decay if pn in param_dict}

optim_groups = [
    {"params": [param_dict[pn] for pn in sorted(list(decay))], "weight_decay": weight_decay},
    {"params": [param_dict[pn] for pn in sorted(list(no_decay))], "weight_decay": 0.0},
]

optimizer = torch.optim.AdamW(optim_groups, lr=learning_rate, betas=(beta1, beta2))

def get_lr(it):
    # 1) Linear warmup for warmup_iters
    if it < warmup_iters:
        return learning_rate * it / warmup_iters
    # 2) If it > max_iters, return min learning rate
    if it > max_iters:
        return 0.0
    # 3) In between, use cosine decay
    decay_ratio = (it - warmup_iters) / (max_iters - warmup_iters)
    assert 0 <= decay_ratio <= 1
    coeff = 0.5 * (1.0 + math.cos(math.pi * decay_ratio))
    return learning_rate * coeff

# ==============================================================================
# Training Loop
# ==============================================================================
@torch.no_grad()
def estimate_loss():
    model.eval()
    losses = torch.zeros(eval_iters)
    for k in range(eval_iters):
        X, Y = get_batch()
        # Use mixed precision if on CUDA
        ctx = torch.amp.autocast(device_type=device, dtype=torch.bfloat16) if device == 'cuda' else torch.amp.autocast(device_type=device, enabled=False)
        with ctx:
            _, loss = model(X, Y)
        losses[k] = loss.item()
    out = losses.mean().item()
    model.train()
    return out

X, Y = get_batch()

print(f"Starting training for {max_iters} iterations...")
print(f"{sum(tens.numel() for tens in model.parameters()) / 1000000} million parameters", flush=True)

csv_file = open('loss.csv', 'w')
csv_file.write("step,batch_loss,eval_train_loss,lr,time_ms_per_step,tokens_per_sec\n")
current_eval_train = ""

for iter in range(max_iters):
    
    # Adjust learning rate
    lr = get_lr(iter)
    for param_group in optimizer.param_groups:
        param_group['lr'] = lr
        
    # Evaluate the loss on train/val sets and report
    if iter % eval_interval == 0 or iter == max_iters - 1:
        losses = estimate_loss()
        current_eval_train = f"{losses:.6f}"
        print(f"step {iter:04d} | train loss {losses:.4f} | lr {lr:.4e}")
        
    # Start timer for this step's forward/backward
    step_t0 = time.time()
    
    # Forward and backward pass
    ctx = torch.amp.autocast(device_type=device, dtype=torch.bfloat16) if device == 'cuda' else torch.amp.autocast(device_type=device, enabled=False)
    with ctx:
        logits, loss = model(X, Y)
        # Scale loss by gradient accumulation steps
        loss = loss / gradient_accumulation_steps
        
    # Prefetch next batch while backward pass computes
    X, Y = get_batch() 
    
    loss.backward()
    
    # Step optimizer only after accumulating gradients
    if (iter + 1) % gradient_accumulation_steps == 0:
        # Gradient clipping for stability
        torch.nn.utils.clip_grad_norm_(model.parameters(), 1.0)
        optimizer.step()
        optimizer.zero_grad(set_to_none=True)
    
    if iter % 100 == 0 or True:
        dt = time.time() - step_t0
        time_ms_per_step = dt * 1000
        tokens_per_sec = (batch_size * max_seq_len) / dt if dt > 0 else 0
        batch_loss = loss.item() * gradient_accumulation_steps
        
        print(f"iter {iter:04d} | loss {batch_loss:.4f} | time/step {time_ms_per_step:.2f}ms | tok/s {tokens_per_sec:.0f}", flush=True)
        
        csv_file.write(f"{iter},{batch_loss:.6f},{current_eval_train},{lr:.6e},{time_ms_per_step:.2f},{tokens_per_sec:.2f}\n")
        csv_file.flush()
        
        # Clear the eval losses so they only print on the row they were calculated
        current_eval_train = ""

csv_file.close()

print("Training finished! Saving model to model_final.pt...")
torch.save(model.state_dict(), 'model_final.pt')
print("Done.")
