CC = g++
CPPFLAGS = -I/opt/local/include 
LDFLAGS = -L/opt/local/lib 

OBJS = Boundary2PAIS.o	

Boundary2PAIS: ${OBJS}		
	${CC} -o Boundary2PAIS ${CPPFLAGS} ${OBJS} ${LDFLAGS}		

Boundary2PAIS.o: Boundary2PAIS.cpp 	
	${CC} ${CPPFLAGS} -c Boundary2PAIS.cpp	
clean:	
	rm -f Boundary2PAIS ${OBJS}	
	@echo "all cleaned up!"	
	