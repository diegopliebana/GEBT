#!/bin/bash
#./GE-single -p 500 -g 250 -x .5 -w .5 -a 2 -m 1 -M 1 -n 2 -e .02 -F marioxp-single-$1 -G aStar-01.bnf -d 20 -D 30 -S $1 0 100
./GE-single -p 500 -g 500 -x .5 -w .5 -a 2 -m 1 -M 1 -n 2 -e .02 -F marioxp-single-$1 -G aStar-01.bnf -d 20 -D 30 -S $1 0 100

