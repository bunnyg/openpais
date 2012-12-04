mvn clean
mvn assembly:assembly

mvn install:install-file -DgroupId=db2jcc -DartifactId=db2jcc -Dversion=1.0 -Dfile=lib/db2jcc.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=db2jcc -DartifactId=db2jcc_license -Dversion=1.0 -Dfile=lib/db2jcc_license_cu.jar -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -DgroupId=PAIS -DartifactId=PAISDB2Helper -Dversion=1.0.0-SNAPSHOT -Dpackaging=jar -Dfile=../PAISDB2Helper/target/PAISDB2Helper-1.0.0-SNAPSHOT-jar-with-dependencies.jar

cd ../PAISDa*

./mvn.sh


