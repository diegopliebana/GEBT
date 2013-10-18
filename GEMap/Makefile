#CC_FLAGS=${CC_FLAGS} -fopenmp
#CC_FLAGS+=-fopenmp
#CC_LIBS+=-lpthread
all:	GE-five GE-slide GE-single GE-change GE-change5 GE-noAstar-five GE-noAstar-slide GE-noAstar-single GE-noAstar-change GE-noAstar-change5
GE-five:	qGA.o GEMap.o main-five.o
	g++ ${CC_FLAGS} -O2 -Wall -g -o $@ qGA.o GEMap.o main-five.o ${CC_LIBS} 

GE-slide:	qGA.o GEMap.o main-slide.o
	g++ ${CC_FLAGS} -O2 -Wall -g -o $@ qGA.o GEMap.o main-slide.o ${CC_LIBS} 

GE-single:	qGA.o GEMap.o main-single.o
	g++ ${CC_FLAGS} -O2 -Wall -g -o $@ qGA.o GEMap.o main-single.o ${CC_LIBS} 

GE-change:	qGA.o GEMap.o main-change.o
	g++ ${CC_FLAGS} -O2 -Wall -g -o $@ qGA.o GEMap.o main-change.o ${CC_LIBS} 

GE-change5:	qGA.o GEMap.o main-change5.o
	g++ ${CC_FLAGS} -O2 -Wall -g -o $@ qGA.o GEMap.o main-change5.o ${CC_LIBS} 

main-five.o:	main-five.cpp GEMap.h qGA.h
	g++ ${CC_FLAGS} -O2 -Wall -g -c main-five.cpp

main-slide.o:	main-slide.cpp GEMap.h qGA.h
	g++ ${CC_FLAGS} -O2 -Wall -g -c main-slide.cpp

main-single.o:	main-single.cpp GEMap.h qGA.h
	g++ ${CC_FLAGS} -O2 -Wall -g -c main-single.cpp

main-change.o:	main-change.cpp GEMap.h qGA.h
	g++ ${CC_FLAGS} -O2 -Wall -g -c main-change.cpp

main-change5.o:	main-change5.cpp GEMap.h qGA.h
	g++ ${CC_FLAGS} -O2 -Wall -g -c main-change5.cpp

GE-noAstar-five:	qGA.o GEMap.o main-noAstar-five.o
	g++ ${CC_FLAGS} -O2 -Wall -g -o $@ qGA.o GEMap.o main-noAstar-five.o ${CC_LIBS} 

GE-noAstar-slide:	qGA.o GEMap.o main-noAstar-slide.o
	g++ ${CC_FLAGS} -O2 -Wall -g -o $@ qGA.o GEMap.o main-noAstar-slide.o ${CC_LIBS} 

GE-noAstar-single:	qGA.o GEMap.o main-noAstar-single.o
	g++ ${CC_FLAGS} -O2 -Wall -g -o $@ qGA.o GEMap.o main-noAstar-single.o ${CC_LIBS} 

GE-noAstar-change:	qGA.o GEMap.o main-noAstar-change.o
	g++ ${CC_FLAGS} -O2 -Wall -g -o $@ qGA.o GEMap.o main-noAstar-change.o ${CC_LIBS} 

GE-noAstar-change5:	qGA.o GEMap.o main-noAstar-change5.o
	g++ ${CC_FLAGS} -O2 -Wall -g -o $@ qGA.o GEMap.o main-noAstar-change5.o ${CC_LIBS} 

main-noAstar-five.o:	main-noAstar-five.cpp GEMap.h qGA.h
	g++ ${CC_FLAGS} -O2 -Wall -g -c main-noAstar-five.cpp

main-noAstar-slide.o:	main-noAstar-slide.cpp GEMap.h qGA.h
	g++ ${CC_FLAGS} -O2 -Wall -g -c main-noAstar-slide.cpp

main-noAstar-single.o:	main-noAstar-single.cpp GEMap.h qGA.h
	g++ ${CC_FLAGS} -O2 -Wall -g -c main-noAstar-single.cpp

main-noAstar-change.o:	main-noAstar-change.cpp GEMap.h qGA.h
	g++ ${CC_FLAGS} -O2 -Wall -g -c main-noAstar-change.cpp

main-noAstar-change5.o:	main-noAstar-change5.cpp GEMap.h qGA.h
	g++ ${CC_FLAGS} -O2 -Wall -g -c main-noAstar-change5.cpp

qGA.o:	qGA.h qGA.cpp
	g++ ${CC_FLAGS} -O2 -Wall -g -c qGA.cpp

GEMap.o:GEMap.h GEMap.cpp
	g++ ${CC_FLAGS} -O2 -Wall -g -c GEMap.cpp

