(=
 Q. Find the nuclear density for each tile from algorithm 'NS-MORPH' with first parameter set:
echo 'slidedensitysingle';

delete from bchmk.querytime where  queryname =  'slidedensitysingle';
insert into bchmk.querytime SELECT 'slidedensitysingle', current timestamp, null  FROM sysibm.sysdummy1;
SELECT  COUNT(*)  
    FROM   pais.markup_polygon m,  pais.collection c
    WHERE  m.pais_uid=c.pais_uid AND c.methodname ='NS-MORPH' AND
       c.sequencenumber ='1' and c.pais_uid ='gbm1.1_40x_20x_NS-MORPH_1';
    
update bchmk.querytime set endtime = current timestamp where queryname = 'slidedensitysingle';
select (endtime - starttime) as cost from bchmk.querytime where queryname =  'slidedensitysingle';
=)

-- Find how many markups in Set A with multiple intersects in Set B:
-- echo 'multiintersectatile';
(=
delete from bchmk.querytime where  queryname = 'multiintersectatile';
insert into bchmk.querytime SELECT 'multiintersectatile', current timestamp, null  FROM sysibm.sysdummy1;

SELECT  A.pais_uid, A.tilename,  A.markup_id, count(*)
FROM pais.markup_polygon A, pais.markup_polygon B
WHERE  A.tilename ='gbm1.1-0000040960-0000040960' and A.pais_uid = 'gbm1.1_40x_20x_NS-MORPH_1' and 
       B.tilename ='gbm1.1-0000040960-0000040960' and B.pais_uid = 'gbm1.1_40x_20x_NS-MORPH_2' and
       DB2GSE.ST_intersects(A.polygon, B.polygon) = 1 selectivity 0.0001 
GROUP BY (A.pais_uid, A.tilename, A.markup_id) HAVING COUNT(*) > 1 WITH UR;

update bchmk.querytime set endtime = current timestamp where queryname = 'multiintersectatile';
select (endtime - starttime) as cost from bchmk.querytime where queryname =  'multiintersectatile';
=)

-- Find all intersected segmented nuclei (with intersection ratio and distance) between parameter set 1 and 2 of algorithm algorithm 'NS-MORPH' 
-- on tile 'oligoIII.2.ndpi-0000090112-0000024576':

-- echo 'intersectednucleiforatile';
delete from bchmk.querytime where  queryname = 'intersectednucleiforatile';
insert into bchmk.querytime SELECT 'intersectednucleiforatile', current timestamp, null  FROM sysibm.sysdummy1;

select count(*) from (
SELECT A.pais_uid, A.tilename,  CAST(  db2gse.ST_Area(db2gse.ST_Intersection(a.polygon, b.polygon) )/db2gse.ST_Area(db2gse.ST_UNION( a.polygon, 
b.polygon))  AS DECIMAL(4,2) )
AS area_ratio,  CAST( DB2GSE.ST_DISTANCE( db2gse.ST_Centroid(b.polygon), db2gse.ST_Centroid(a.polygon) )  AS DECIMAL(5,2) ) AS centroid_distance
FROM pais.markup_polygon A, pais.markup_polygon B
WHERE  A.tilename ='gbm1.1-0000040960-0000040960' and A.pais_uid = 'gbm1.1_40x_20x_NS-MORPH_1' and 
       B.tilename ='gbm1.1-0000040960-0000040960' and B.pais_uid = 'gbm1.1_40x_20x_NS-MORPH_2' and
       DB2GSE.ST_intersects(A.polygon, B.polygon) = 1 selectivity 0.0001 WITH UR
); 

update bchmk.querytime set endtime = current timestamp where queryname = 'intersectednucleiforatile';
select (endtime - starttime) as cost from bchmk.querytime where queryname = 'intersectednucleiforatile';



