#!/bin/bash
cd GEMap
./GE-single -p 500 -g 250 -x .5 -w .5 -a 2 -m 1 -M 1 -n 2 -e .02 -F marioxp-run1-$1 -G ../GRAMMARS/aStar-01.bnf -d 20 -D 30 -S $1 0 100

