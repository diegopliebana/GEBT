#!/usr/bin/env python

import os
import sys

startRnd = 666

def main():
	if len(sys.argv) < 5:
		print "Usage: " + sys.argv[0] + " <BT1> ... <BTn> <vis> <fps> <nbrSeeds>"
		sys.exit(0)
	print "vis =", sys.argv[-3],  "vis =", sys.argv[-2], "nbrSeeds =", sys.argv[-1]
	for tree in range(1, len(sys.argv) - 3):
		print "Doing file " + sys.argv[tree] + " which is tree number " + str(tree)
		#output = open("stats-" + str(tree) + ".txt", 'w')
		print "Creating file " + sys.argv[tree] + ".txt"
		output = open(sys.argv[tree] + ".txt", 'w')
		for seed in range(startRnd, startRnd + int(sys.argv[-1])):
			print "Seed " + str(seed)
			os.system("cd ../benchmark_0_1_5/MarioAI+Benchmark"
				+ " && java -classpath \"out/production\""
				+ " grammaticalbehaviorsNoAstar.GEBT_Mario.EvoMain"
				+ " -tl 100 -ld 0 1 2 3 4 5 6 7 8 -lt 0 1 -ll 320"
				+ " -ce 1 -vis " + sys.argv[-3] + " -fps " + sys.argv[-2]
				+ " -rnd " + str(seed)
				+ " -if ../" + sys.argv[tree]
				+ " -of /dev/null "
				+ " -os ../../BTs/tempStats.txt > /dev/null")
			#f = open("tempStats.txt", 'r')
			#if seed == startRnd:
			#	output.write(f.readline())
			#else: f.readline()
			#output.write(f.readline() + "\n")
			#f.close()
			if sys.argv[-3] == "0":
				with open("../BTs/tempStats.txt", 'r') as f:
					if seed == startRnd:
						output.write(f.readline())
					else: f.readline()
					output.write(f.readline() + "\n")
		output.close()

if __name__ == "__main__":
	main()

