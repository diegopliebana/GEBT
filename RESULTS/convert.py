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
		newline = ""
		for field in line.rstrip("\n").rsplit("\t"):
			index += 1
			if index == 1: newline += str(int(field) * float(sys.argv[2]))
			elif index == 2: newline += "\t" + str(int(field) * float(sys.argv[3]))
			else: newline += "\t" + field
		print newline

if __name__ == "__main__":
	main()


