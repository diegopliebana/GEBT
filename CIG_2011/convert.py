#!/usr/bin/env python

import sys

def main():
	if len(sys.argv) < 4:
		print "Usage: " + sys.argv[0] + " <statsFile> <GenMultFactor> <ObjMultFactor>"
		print "Multiplies the number of generations and objective fitness calls field"
		print "in the data file by the factors specified. Standard output."
		sys.exit(0)
	dataFile = open(sys.argv[1], 'r')
	for line in dataFile:
		index = 0
		for field in line.rsplit("\t"):
			index += 1
			if index == 1: print int(field) * int(sys.argv[2]),
			if index == 2: print int(field) * int(sys.argv[3]),
			else: print field,

if __name__ == "__main__":
	main()


