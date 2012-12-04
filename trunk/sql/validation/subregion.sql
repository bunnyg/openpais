SELECT current timestamp FROM sysibm.sysdummy1;
select count(*) from pais.markup_polygon
WHERE  tilename ='gbm1.1.ndpi-0000040960-0000040960' and pais_uid = 'gbm1.1_20x_20x_NS-MORPH_1' and DB2GSE.ST_Contains( DB2GSE.ST_Polygon('polygon((40960 40960, 41984 40960,  41984 41984, 40960 41984, 40960 40960))', 1),polygon ) = 1 selectivity 0.0625 WITH UR;
SELECT current timestamp FROM sysibm.sysdummy1;
