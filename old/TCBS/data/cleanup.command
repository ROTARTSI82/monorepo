#!/bin/sh

cd /Users/Grant/Desktop/TCBS
rm -f ./.git/index.lock
rm -f .DS_Store
rm -f .idea/.DS_Store

cd /Users/Grant/Desktop/TCBS/data
rm -f resources/options.pkl

rm -rf logs
rm -rf screenshots

find . -name '*.pyc' -delete
find . -name '*.decomp' -delete
