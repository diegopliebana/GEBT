#!/usr/bin/env python

import os
import sys
import csv

def main():
	if len(sys.argv) < 3:
		print "Usage: " + sys.argv[0] + " <field1>,...,<fieldn> <CSV1> ... <CSVn>"
		sys.exit(0)
	fields = [f for f in sys.argv[1].rsplit(",")]
	for csvFile in range(2, len(sys.argv)):
		#print "Examining file " + sys.argv[csvFile]
		stats = csv.reader(open(sys.argv[csvFile]))
		labels = stats.next()
		values = [float(val) for val in stats.next()]
		total = 1
		for line in stats:
			for index in range(0, len(line)):
				values[index] += float(line[index])
			total += 1
		print csvFile - 1,
		for f in fields:
			print "\t" + str(values[int(f)] / total) + "\t\"" + labels[int(f)] + "\"",
		print

if __name__ == "__main__":
	main()

