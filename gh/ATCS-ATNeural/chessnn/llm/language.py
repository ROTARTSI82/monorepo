import numpy as np
from torchviz import make_dot
import torch
import random
import math
import struct
import json
from custom_transform import *

ctx = 512
lab_smooth = 0.1
vocab_size = 256

temp = 0.5
top_k = int(32)

model_file = "charlvl.bin"
conf = Config()
conf.d_model = 256
conf.n_head = 8
conf.d_hid = 512
conf.n_enc = 12
conf.qk_size = 32
conf.v_size = 32
conf.drop = 0.1
mats = make_rot_mats(conf.d_model, ctx)
pos_enc = torch.tensor(
    [[(math.sin if i % 2 == 0 else math.cos)(pos / 10000**(i / conf.d_model))
      for i in range(conf.d_model)] for pos in range(ctx)], device=device)


def tokenize(string):
    return [i for i in string]


with open("discord.log", 'rb') as fp:
    data = fp.read()

npdtype = float
dtype = torch.float32


class LanguageModel(torch.nn.Module):
    def __init__(self):
        super().__init__()
        self.vocab_embed = torch.nn.Embedding(vocab_size, conf.d_model)
        self.encoder = torch.nn.ModuleList([Block(conf, causal=True) for _ in range(conf.n_enc)])
        self.fnorm = RMSNorm(conf.d_model)
        self.prob_out = torch.nn.Sequential(torch.nn.Linear(conf.d_model, vocab_size, bias=False))
        # self.pos_emb = torch.nn.Parameter(pos_enc, requires_grad=True)
        self.vocab_embed.weight = self.prob_out[0].weight \
            = torch.nn.Parameter(torch.eye(256, device=device))

    def forward(self, txt, mats, length):
        # print(self.vocab_embed(txt).shape, pos_enc.shape)
        t_in = rope(self.vocab_embed(txt), mats)
        # t_in = self.vocab_embed(txt) + self.pos_emb[:length]
        for layer in self.encoder:
            t_in = layer(t_in)
        return self.prob_out(self.fnorm(t_in))


lrn = lr = 2e-3
model = LanguageModel().to(device).train(True)
opt = torch.optim.AdamW(model.parameters(), lr=lr, eps=1e-5, betas=(0.9, 0.95), weight_decay=0.1)
loss_fn = torch.nn.CrossEntropyLoss(label_smoothing=lab_smooth)

print(f"{sum(tens.numel() for tens in model.parameters()) / 1000000} million parameters")

load = torch.load(model_file, map_location=device)
model.load_state_dict(load['model'], strict=False)
print(model)
# visualize(model.named_parameters(recurse=True), "vissmol/")

# start = load['start']
start = 24108197

# latin begins 23216730 to 23260685
# code begins 24108197 to 24149110

tot_steps = 1
warmup = 2000
losses = []
running = False
while running:
    opt.zero_grad()

    # lr = conf.d_model**-0.5 * min(tot_steps**-0.5, tot_steps*warmup**-1.5)
    # lrn = lr * min(1, tot_steps * warmup**-1.5)
    # for g in opt.param_groups:
    #     g['lr'] = lrn
    vec = []
    for minibatch in range(10):
        rng = True  # random.randint(0, 100) < 20
        cur = random.randint(0, len(data) - ctx - 2) if rng else start
        start = cur
        toks = tokenize(data[cur:cur+2*ctx])
        vec.append(toks[:ctx+1])
        if not rng:
            start += random.randint(ctx // 2, ctx)
        # if start >= len(data) - 2*ctx:
        #     start %= len(data) - 2*ctx
        #     running = False

    case = torch.tensor(vec, device=device)
    out = model(case[:, :ctx], mats, ctx)

    targ = case[:, 1:].reshape(-1)
    loss = loss_fn(out.view(-1, vocab_size), targ)
    loss.backward()
    torch.nn.utils.clip_grad_norm_(model.parameters(), 1)
    opt.step()
    print(f"{tot_steps}\t{100*start/len(data):.2f}%\t{lrn:.2E}\t{loss.item()}\t{data[start:start+ctx]}")
    losses.append(loss.item())

    if tot_steps % 64 == 0 or not running:
        print("============ saved checkpoint =================")
        torch.save({'model': model.state_dict(), 'start': start}, model_file)
        with open('loss.csv', 'a') as fp:
            fp.write("".join([", " + str(a) for a in losses]))
            losses = []
    tot_steps += 1

write_vis = False
if write_vis:
    visualize(model.named_parameters())

torch.autograd.set_grad_enabled(False)
generate = True
txt = """
theserperiorone 1711356098
Hello everyone,
 
 We are excited to announce that Medical Club is hosting the first Diagnosis Event of the year on Wednesday 03/27 @ 12:45pm in Dr. Harley's room!
 
 This event is a great way to experience the life of a doctor. Working in teams, you will diagnose our officers based on the symptoms they present. This year, we will be able to host everyone who's interested, so we highly encourage all club members to sign up!
 
 Please register using this form. We hope to see you there!
 
 Thanks,
 Harker Medical Club

chopinfan239 1711356098
like y''-y^2=0 is homogeneous
""".encode('utf-8')

if generate:
    model.eval()
    prompt = tokenize(txt)
    print(prompt)

    print(txt.decode('utf-8'), end='\n=====\n')
    case = torch.tensor([prompt[-ctx:]], device=device)
    out = torch.softmax(model(case, mats, len(prompt))[0], dim=1)
    # print(b''.join(tokens[i][0] for i in torch.argmax(out, dim=1)).decode('utf-8', 'ignore'))

    for i in range(1024):
        case = torch.tensor([prompt[-ctx + 10:]], device=device)
        logits = model(case, mats, min(len(prompt), ctx - 10))[0] / temp
        out = torch.softmax(logits, dim=1)

        # if i == 300:
        #     make_dot(logits, params=dict(model.named_parameters()), show_attrs=True, show_saved=True).render("attached", format="svg")

        # from nanogpt
        if top_k is not None:
            v, _ = torch.topk(logits, min(top_k, logits.size(-1)))
            logits[logits < v[:, [-1]]] = -float('Inf')
        new = torch.multinomial(out[-1], num_samples=1)[0]
        # new = torch.argmax(out[-1])
        prompt.append(new.item())
        print(new.item().to_bytes(1, 'big').decode('utf-8', 'ignore'), end='', flush=True)
    # print(f'\n\n{"="*16}\n\n')
    # print(b''.join(tokens[i][0] for i in prompt).decode('utf-8', 'ignore'))

