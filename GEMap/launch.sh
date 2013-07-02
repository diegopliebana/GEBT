for ((i = $1; i < $1 + $2; i=$i + 1)); do
	nohup ./GE-five -p 500 -g 50 -x .5 -w .5 -a 2 -m 1 -M 1 -n 2 -e .02 -F marioxp-run5-$i -G ../GRAMMARS/aStar-01.bnf -d 20 -D 30 -S $i 0 100 &
	nohup ./GE-single -p 500 -g 250 -x .5 -w .5 -a 2 -m 1 -M 1 -n 2 -e .02 -F marioxp-run1-$i -G ../GRAMMARS/aStar-01.bnf -d 20 -D 30 -S $i 0 100 &
	nohup ./GE-change -p 500 -g 125 -x .5 -w .5 -a 2 -m 1 -M 1 -n 2 -e .02 -F marioxp-change1-$i -G ../GRAMMARS/aStar-01.bnf -d 20 -D 30 -S $i 0 100 &
	nohup ./GE-slide -p 500 -g 50 -x .5 -w .5 -a 2 -m 1 -M 1 -n 2 -e .02 -F marioxp-slide5-$i -G ../GRAMMARS/aStar-01.bnf -d 20 -D 30 -S $i 0 100 &
done

