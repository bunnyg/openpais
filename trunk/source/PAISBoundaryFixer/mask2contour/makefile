CC = g++
CPPFLAGS = -I/opt/local/include -I/opt/local/include/opencv -I. -I../features
LDFLAGS = -L/opt/local/lib -lopencv_core -lopencv_imgproc -lopencv_highgui -lopencv_ml -lopencv_video -lopencv_features2d -lopencv_calib3d -lopencv_objdetect -lopencv_contrib -lopencv_legacy -lopencv_flann

OBJS = BoundaryConvPBM.o

BoundaryConvPBM: ${OBJS}
	${CC} -o BoundaryConvPBM ${CPPFLAGS} ${OBJS} ${LDFLAGS}

BoundaryConvPBM.o: BoundaryConvPBM.cpp 
	${CC} ${CPPFLAGS} -c BoundaryConvPBM.cpp	

clean:
	rm -f BoundaryConvPBM ${OBJS}
	@echo "all cleaned up!"
	