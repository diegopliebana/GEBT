#!/bin/bash

NBRRUNS=4

####################
##### PATHFOLLOWER #
####################
# Generalisation test for A* alone
#./marioTestAstar.py ../benchmark_0_1_5/MarioAI+Benchmark/pathFollower.xml 0 100 20
./createStatifiableBasedOnCVSField.py 0 stats-pathFollower-Astar.dat ../benchmark_0_1_5/MarioAI+Benchmark/pathFollower.xml.txt
echo -e "1\t125000\t`cat stats-pathFollower-Astar.dat | cut -f 3-5`" >> stats-pathFollower-Astar.dat
~/Documents/WORK/bds/Statify/statify 100000000 max pathFollower-Astar stats-pathFollower-Astar.dat

####################
############ SLIDE #
####################
# Generalisation test every 5000 evaluations
#for gen in 2 4 6 7 9 11 12 14 16 17 19 21 22 24 26 27 29 31 32 34 36 37 39 41 42; do
#	./marioTestAstar.py ../BTs/best-slide-*-$gen-*.xml 0 100 20
#done;

for ((run=1; run<=$NBRRUNS; run=$run+1)) ; do
	bts=""
	for gen in 2 4 6 7 9 11 12 14 16 17 19 21 22 24 26 27 29 31 32 34 36 37 39 41 42; do
		bts="$bts ../BTs/best-slide-$run-$gen-*.txt"
	done;
	./createStatifiableBasedOnCVSField.py 0 newstatsfile.dat $bts
	./convert.py newstatsfile.dat 1.68 5000 > stats-slide-Astar-$run.dat
	rm newstatsfile.dat
done;
~/Documents/WORK/bds/Statify/statify 100000000 max slide-Astar stats-slide-Astar-*

#####################
########### CHANGE5 #
#####################
# Generalisation test every 5000 evaluations
#for gen in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25; do
#	./marioTestAstar.py ../BTs/best-change5-*-$gen-*.xml 0 100 20
#done;

for ((run=1; run<=$NBRRUNS; run=$run+1)) ; do
	bts=""
	for gen in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25; do
		bts="$bts ../BTs/best-change5-$run-$gen-*.txt"
	done;
	./createStatifiableBasedOnCVSField.py 0 newstatsfile.dat $bts
	./convert.py newstatsfile.dat 1 5000 > stats-change5-Astar-$run.dat
	rm newstatsfile.dat
done;
~/Documents/WORK/bds/Statify/statify 100000000 max change5-Astar stats-change5-Astar-*

####################
############# FIVE #
####################
# Generalisation test every 5000 evaluations
#for gen in 2 4 6 8 10 12 14 16 18 20 22 24 26 28 30 32 34 36 38 40 42 44 46 48 50; do
#	./marioTestAstar.py ../BTs/best-five-*-$gen-*.xml 0 100 20
#done;

for ((run=1; run<=$NBRRUNS; run=$run+1)) ; do
	bts=""
	for gen in 2 4 6 8 10 12 14 16 18 20 22 24 26 28 30 32 34 36 38 40 42 44 46 48 50; do
		bts="$bts ../BTs/best-five-$run-$gen-*.txt"
	done;
	./createStatifiableBasedOnCVSField.py 0 newstatsfile.dat $bts
	./convert.py newstatsfile.dat 2 5000 > stats-five-Astar-$run.dat
	rm newstatsfile.dat
done;
~/Documents/WORK/bds/Statify/statify 100000000 max five-Astar stats-five-Astar-*
exit

####################
########### CHANGE #
####################
# Generalisation test every 5000 evaluations
#for gen in 5 10 15 20 25 30 35 40 45 50 55 60 65 70 75 80 85 90 95 100 105 110 115 120 125; do
#	./marioTestAstar.py ../BTs/best-change-*-$gen-*.xml 0 100 20
#done;

for ((run=1; run<=$NBRRUNS; run=$run+1)) ; do
	bts=""
	for gen in 5 10 15 20 25 30 35 40 45 50 55 60 65 70 75 80 85 90 95 100 105 110 115 120 125; do
		bts="$bts ../BTs/best-change-$run-$gen-*.txt"
	done;
	./createStatifiableBasedOnCVSField.py 0 newstatsfile.dat $bts
	./convert.py newstatsfile.dat 5 5000 > stats-change-Astar-$run.dat
	rm newstatsfile.dat
done;
~/Documents/WORK/bds/Statify/statify 100000000 max change-Astar stats-change-Astar-*

####################
########### SINGLE #
####################
# Generalisation test every 5000 evaluations
#for gen in 10 20 30 40 50 60 70 80 90 100 110 120 130 140 150 160 170 180 190 200 210 220 230 240 250; do
#	./marioTestAstar.py ../BTs/best-single-*-$gen-*.xml 0 100 20
#done;

for ((run=1; run<=$NBRRUNS; run=$run+1)) ; do
	bts=""
	for gen in 10 20 30 40 50 60 70 80 90 100 110 120 130 140 150 160 170 180 190 200 210 220 230 240 250; do
		bts="$bts ../BTs/best-single-$run-$gen-*.txt"
	done;
	./createStatifiableBasedOnCVSField.py 0 newstatsfile.dat $bts
	./convert.py newstatsfile.dat 10 5000 > stats-single-Astar-$run.dat
	rm newstatsfile.dat
done;
~/Documents/WORK/bds/Statify/statify 100000000 max single-Astar stats-single-Astar-*

