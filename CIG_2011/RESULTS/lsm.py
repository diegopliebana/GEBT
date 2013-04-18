#!/usr/bin/python

import sys

if __name__ == "__main__":
	if len(sys.argv) != 6:
		print "USAGE: " + sys.argv[0] + " <datafile> <xcol> <ycol> <xmult> <ymult>"
		print "Read tab-separated datafile, calculate least mean squares alpha and beta,"
		print "using datapoints (xcol * xmult, ycol * ymult)."
		print "(cols start at zero)"
		sys.exit(0)
	data = open(sys.argv[1], "r")
	xcol = int(sys.argv[2])
	ycol = int(sys.argv[3])
	xmult = float(sys.argv[4])
	ymult = float(sys.argv[5])
	x = []
	y = []
	for line in data:
		line = line.rstrip("\n")
		if len(line) == 0: continue
		line = line.rsplit("\t")
		x.append(float(line[xcol]) * xmult)
		y.append(float(line[ycol]) * ymult)
		print x[-1],y[-1]
	data.close()
	sumx = sum(x)
	sumy = sum(y)
	sumx2 = sum(i * i for i in x)
	sumy2 = sum(i * i for i in y)
	sumxy = sum(x[i] * y[i] for i in range(0,len(x)))
	print "sumx =", sumx
	print "sumy =", sumy
	print "sumx2 =", sumx2
	print "sumy2 =", sumy2
	print "sumxy =", sumxy
	BETA = (len(x) * sumxy - sumx * sumy) / (len(x) * sumx2 - (sumx * sumx))
	ALPHA = sumy / len(x) - BETA * sumx / len(x)
	print "ALPHA =", ALPHA
	print "BETA =", BETA

