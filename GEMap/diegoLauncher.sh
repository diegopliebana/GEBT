#!/bin/bash
for (( i=1 ; $i<6 ; i=$i+1 )) ; do
	nohup ./launch-noAstar-change.sh $i &
	nohup ./launch-noAstar-change5.sh $i &
	nohup ./launch-noAstar-five.sh $i &
	nohup ./launch-noAstar-single.sh $i &
	nohup ./launch-noAstar-slide.sh $i &
done

