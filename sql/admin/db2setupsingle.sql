--Create an instance with single partition
-- db2:  /db/sp/db2
-- data: /db/sp/data
-- log:  /db/sp/log
--database path: /db/sp/database/testdb
--chown, chgrp

sudo /usr/sbin/groupadd -g 1009 db2iadm2
sudo /usr/sbin/groupadd -g 1008 db2fadm2
sudo /usr/sbin/groupadd -g 1007 dasadm2
sudo /usr/sbin/useradd -u 1204 -g db2iadm2 -m -d /home/db2inst2 db2inst2
sudo /usr/sbin/useradd -u 1203 -g db2fadm2 -m -d /home/db2fenc2 db2fenc2
sudo /usr/sbin/useradd -u 1202 -g dasadm2  -m -d /home/dasusr2  dasusr2

This will enable das to cover db2inst2:
sudo /usr/sbin/usermod  -a -G dasadm1 db2inst2

sudo passwd db2inst2
sudo passwd db2fenc2
sudo passwd dasusr2

sudo mkdir /db/sp
sudo mkdir /db/sp/database
sudo mkdir /db/sp/data
sudo mkdir /db/sp/log

sudo chown -R  db2inst2 /db/sp
sudo chgrp -R  db2iadm2 /db/sp


Setup DB2: ./db2setup
Setup DB2 spatial extender: ./db2setup (using existing db2inst2)
Apply fixpack: ./install_fixpack


Create database:

db2 -tf createdb.sql

Enable spatial db:
db2 -tf enablespatialdb.sql 

Create spatial coordindate system:
db2 -tf create_srs_utm16n.sql 

Create tables:
db2 -tf create_tables_sp.sql


Loading data from europa to pais2:
java -jar PAISDocumentUploader.jar -f /develop/imageanalysis/GBMvalidation/VALIDATION_MORPH_RS01 -ft collection -dbc /tmp/pais/1.0/dbconfig.xml 

One result set of 18 images: Total time: 1771.255 seconds

java -jar PAISDocumentUploader.jar -f /develop/imageanalysis/GBMvalidation/VALIDATION_MORPH_RS02 -ft collection -dbc /tmp/pais/1.0/dbconfig.xml 
Total time (seconds):1861.306



Mapping:
java -jar PAISDataLoadingManager.jar -dbc /data2/fwang/db2dpf/paisrelease/1.0/dbconfig.xml -lc /data2/fwang/db2dpf/paisrelease/1.0/loadingconfig.xml > /tmp/loading.out &
Total loading time: 21489.752 seconds.
2nd dataset: Total Parsing and Loading Time = 26505.198 seconds.

gseidx connect to testdb user db2inst2 using dddd GET GEOMETRY STATISTICS FOR INDEX  PAIS.MAPIDX detail advice > /tmp/spatialidx.out

Create indexes:
db2 -tf keys.sql
db2 -tf index.sql

Create UDF:
db2 -td@ -f udf_plgn2str.sql
