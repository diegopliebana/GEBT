#CC_FLAGS=${CC_FLAGS} -fopenmp
#CC_FLAGS+=-fopenmp
#CC_LIBS+=-lpthread
all:	GE-five GE-slide GE-single GE-change
GE-five:	qGA.o GEMap.o main-five.o
	g++ ${CC_FLAGS} -O2 -Wall -g -o $@ qGA.o GEMap.o main-five.o ${CC_LIBS} 

GE-slide:	qGA.o GEMap.o main-slide.o
	g++ ${CC_FLAGS} -O2 -Wall -g -o $@ qGA.o GEMap.o main-slide.o ${CC_LIBS} 

GE-single:	qGA.o GEMap.o main-single.o
	g++ ${CC_FLAGS} -O2 -Wall -g -o $@ qGA.o GEMap.o main-single.o ${CC_LIBS} 

GE-change:	qGA.o GEMap.o main-change.o
	g++ ${CC_FLAGS} -O2 -Wall -g -o $@ qGA.o GEMap.o main-change.o ${CC_LIBS} 

qGA.o:	qGA.h qGA.cpp
	g++ ${CC_FLAGS} -O2 -Wall -g -c qGA.cpp

GEMap.o:GEMap.h GEMap.cpp
	g++ ${CC_FLAGS} -O2 -Wall -g -c GEMap.cpp

main-five.o:	main-five.cpp GEMap.h qGA.h
	g++ ${CC_FLAGS} -O2 -Wall -g -c main-five.cpp

main-slide.o:	main-slide.cpp GEMap.h qGA.h
	g++ ${CC_FLAGS} -O2 -Wall -g -c main-slide.cpp

main-single.o:	main-single.cpp GEMap.h qGA.h
	g++ ${CC_FLAGS} -O2 -Wall -g -c main-single.cpp

main-change.o:	main-change.cpp GEMap.h qGA.h
	g++ ${CC_FLAGS} -O2 -Wall -g -c main-change.cpp

