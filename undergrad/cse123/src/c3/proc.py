import json
from datasets import load_dataset
from transformers import pipeline
import random
from dateutil.parser import parse

sentiment_pipeline = pipeline("sentiment-analysis")
ds = load_dataset("notesbymuneeb/epstein-emails").shuffle()

kont = ""
for j in ds['train']:
    msgs = json.loads(j['messages'])
    random.shuffle(msgs)
    for i in msgs:
        try:
            timestamp = int(parse(i['timestamp']).timestamp())
            f = lambda x: x.replace("\n", " ").replace("\r", " ").replace(" ~ ", "-")
            sendFrom = f(i['sender'])
            sendTo = f(",".join(i['recipients']))
            subject = f(i['subject'])
            body = f(i['body'])
            sentanal = i['subject'] + "\n\n" + i['body']
            sentimentDat = sentiment_pipeline([sentanal])[0]
            sentiment = (1 if sentimentDat['label'] == 'POSITIVE' else -1) * sentimentDat['score']
            print(f"{sentiment} {subject} {body}")
            kont += f"{timestamp} ~ {int(100 * sentiment)} ~ {subject} ~ {sendFrom} ~ {sendTo} ~ {body}\n"
        except Exception as e:
            continue

with open(f"emails.txt", "w") as fp:
    fp.write(kont)
