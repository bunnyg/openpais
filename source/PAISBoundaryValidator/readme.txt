=============================================================
Examples on how to run the program: 

Validate a single polygon:
java -jar PAISBoundaryValidator.jar  -dbc D:\Projects\PAIS\PAISBoundaryValidator\conf\dbconfig.xml  -txt "110,120 110,140 130,140 130,120 110,120"

validate a set of polygons:
java -jar PAISBoundaryValidator.jar -dbc D:\Projects\PAIS\PAISBoundaryValidator\conf\dbconfig.xml  -i D:\Projects\PAIS\PAISBoundaryValidator\conf\testBoundaries.txt -o  D:\Projects\PAIS\PAISBoundaryValidator\conf\out.txt

validate a set of large polygons:
java -jar PAISBoundaryValidator.jar -ls  -dbc D:\Projects\PAIS\PAISBoundaryValidator\conf\dbconfig.xml  -i D:\Projects\PAIS\PAISBoundaryValidator\conf\testBoundaries.txt -o  D:\Projects\PAIS\PAISBoundaryValidator\conf\out.txt

Example polygons can be found at conf.

=============================================================
To recompile the package:

mvn install:install-file -DgroupId=db2jcc -DartifactId=db2jcc -Dversion=1.0 -Dfile=D:\develop\IBM\SQLLIB\java\db2jcc.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=db2jcc -DartifactId=db2jcc_license -Dversion=1.0 -Dfile=D:\develop\IBM\SQLLIB\java\db2jcc_license_cu.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=PAIS -DartifactId=PAISDB2Helper -Dversion=1.0.0-SNAPSHOT -Dpackaging=jar -Dfile=D:\Projects\PAIS\PAISDB2Helper\target\PAISDB2Helper-1.0.0-SNAPSHOT-jar-with-dependencies.jar

mvn clean
mvn assembly:assembly

