#!/usr/bin/env python

import os
import sys
import csv

def main():
	if len(sys.argv) < 4:
		print "Usage: " + sys.argv[0] + " <field> <newStatsFile> <CSV1> ... <CSVn>"
		print "Grabs specific field from each of the cvs files, creates newStatsFile with"
		print "data in statifiable format, with gen and obj as the order of the cvs"
		print "files, and all fitness fields as the average of all data in"
		print "corresponding cvs file."
		sys.exit(0)
	field = int(sys.argv[1])
	newStatsFile = open(sys.argv[2], 'w')
	for csvFile in range(3, len(sys.argv)):
		print "Doing file " + sys.argv[csvFile]
		stats = csv.reader(open(sys.argv[csvFile]))
		# Grab labels (first line of CVS file)
		labels = stats.next()
		# Read first cvs data line
		values = [float(val) for val in stats.next()]
		total = 1
		# Read remaining lines
		for line in stats:
			for index in range(0, len(line)):
				values[index] += float(line[index])
			total += 1
		newStatsFile.write(str(csvFile - 2) + "\t" + str(csvFile - 2) + "\t" + \
		str(values[field] / total) + "\t" + str(values[field] / total) + "\t" + \
		str(values[field] / total) + "\n")
	newStatsFile.close()

if __name__ == "__main__":
	main()

