-- db2 -tf create_srs_utm16n.sql 
connect to testdb;
!db2se drop_srs testdb  -srsName UTM16N;

!db2se create_srs  testdb
  -srsId   100
  -srsName UTM16N
  -xOffset       0
  -yOffset       0
  -xScale        1
  -coordsysName WGS_1984_UTM_ZONE_16N
 ;
