CC = g++
CPPFLAGS = -I/opt/local/include 
LDFLAGS = -L/opt/local/lib

OBJS = BoundaryFix2.o

BoundaryFix2: ${OBJS}
	${CC} -o BoundaryFix2 ${CPPFLAGS} ${OBJS} ${LDFLAGS}

BoundaryFix2.o: BoundaryFix2.cpp
	${CC} ${CPPFLAGS} -c BoundaryFix2.cpp
	
clean:
	rm -f BoundaryFix2 ${OBJS}
	@echo "all cleaned up!"
	