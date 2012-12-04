SELECT current timestamp FROM sysibm.sysdummy1;

select count(A.markup_id) from pais.markup_polygon A, pais.markup_polygon B
where  A.pais_uid ='oligoIII.2_20x_20x_NS-MORPH_1' and A.tilename ='oligoIII.2.ndpi-0000090112-0000024576' and 
       B.pais_uid ='oligoIII.2_20x_20x_NS-MORPH_2' and B.tilename ='oligoIII.2.ndpi-0000090112-0000024576' and
       DB2GSE.ST_overlaps(A.polygon, B.polygon) = 1;

SELECT current timestamp FROM sysibm.sysdummy1;   


SELECT db2gse.ST_Area(db2gse.ST_Intersection(a.polygon, b.polygon) )/db2gse.ST_Area(db2gse.ST_UNION( a.polygon, b.polygon))
AS area_ratio, DB2GSE.ST_DISTANCE( db2gse.ST_Centroid(b.polygon), db2gse.ST_Centroid(a.polygon) ) AS centroid_distance
FROM pais.markup_polygon A, pais.markup_polygon B
WHERE  A.pais_uid ='oligoIII.2_20x_20x_NS-MORPH_1' and A.tilename ='oligoIII.2.ndpi-0000090112-0000024576' and 
       B.pais_uid ='oligoIII.2_20x_20x_NS-MORPH_2' and B.tilename ='oligoIII.2.ndpi-0000090112-0000024576' and
       DB2GSE.ST_intersects(A.polygon, B.polygon) = 1;



SELECT current timestamp FROM sysibm.sysdummy1;   

SELECT A.pais_uid, A.tilename, A.markup_id, CAST(  db2gse.ST_Area(db2gse.ST_Intersection(a.polygon, b.polygon) )/db2gse.ST_Area(db2gse.ST_UNION( a.polygon, b.polygon))  AS DECIMAL(4,2) )
AS area_ratio,  CAST( DB2GSE.ST_DISTANCE( db2gse.ST_Centroid(b.polygon), db2gse.ST_Centroid(a.polygon) )  AS DECIMAL(5,2) ) AS centroid_distance
FROM pais.markup_polygon A, pais.markup_polygon B
WHERE  A.pais_uid ='oligoIII.2_20x_20x_NS-MORPH_1' and A.tilename ='oligoIII.2.ndpi-0000090112-0000024576' and 
       B.pais_uid ='oligoIII.2_20x_20x_NS-MORPH_2' and B.tilename ='oligoIII.2.ndpi-0000090112-0000024576' and
       DB2GSE.ST_intersects(A.polygon, B.polygon) = 1;


INSERT INTO PAIS.VALIDATION_PRECOMPUTE(pais_uid, tilename, markup_id, 
      AREA_OVERLAP_RATIO, centroid_distance) 
SELECT A.pais_uid, A.tilename, A.markup_id, 
  CAST(db2gse.ST_Area(db2gse.ST_Intersection(a.polygon,b.polygon))/db2gse.ST_Area
  (db2gse.ST_Union( a.polygon, b.polygon)) AS DECIMAL(4,2)) AS area_ratio,  
  CAST( db2gse.ST_Distance(db2gse.ST_Centroid(b.polygon),db2gse.ST_Centroid(a.polygon)) 
  AS DECIMAL(5,2) ) AS centroid_distance
FROM   pais.markup_polygon A, pais.markup_polygon B
WHERE  A.pais_uid ='oligoIII.2_20x_20x_NS-MORPH_1' AND 
       A.tilename='oligoIII.2.ndpi-0000090112-0000024576' AND
       B.pais_uid ='oligoIII.2_20x_20x_NS-MORPH_2' AND 
       B.tilename ='oligoIII.2.ndpi-0000090112-0000024576' AND
       db2gse.ST_Intersects(A.polygon, B.polygon) = 1;


--ibm index suggestions:
SELECT A.pais_uid, A.tilename,  CAST(  db2gse.ST_Area(db2gse.ST_Intersection(a.polygon, b.polygon) )/db2gse.ST_Area(db2gse.ST_UNION( a.polygon, b.polygon))  AS DECIMAL(4,2) )
AS area_ratio,  CAST( DB2GSE.ST_DISTANCE( db2gse.ST_Centroid(b.polygon), db2gse.ST_Centroid(a.polygon) )  AS DECIMAL(5,2) ) AS centroid_distance
FROM pais.markup_polygon A, pais.markup_polygon B
WHERE  A.pais_uid ='oligoIII.2_20x_20x_NS-MORPH_1' and A.tilename ='oligoIII.2.ndpi-0000090112-0000024576' and 
       B.pais_uid ='oligoIII.2_20x_20x_NS-MORPH_2' and B.tilename ='oligoIII.2.ndpi-0000090112-0000024576' and
       DB2GSE.ST_intersects(A.polygon, B.polygon) = 1 selectivity 0.0001
WITH UR; 
--90; 7668 records


-- Find the number difference in A and not in B, group by pais_uid, tilename
SELECT   m.pais_uid, m.tilename, count(*) AS count FROM pais.markup_polygon m, pais.collection c
WHERE    c.role='algorithm' and c.sequencenumber = 1 and c.pais_uid and m.pais_uid
GROUP BY m.pais_uid, m.tilename;

-- Use two temporary tables
WITH A as( 
SELECT   m.pais_uid, m.tilename, count(*) AS count FROM pais.markup_polygon m, pais.collection c
WHERE    c.role='algorithm' and c.sequencenumber = 1 and c.pais_uid = m.pais_uid
GROUP BY m.pais_uid, m.tilename),
B as( 
SELECT   m.pais_uid, m.tilename, count(*) AS count FROM pais.markup_polygon m, pais.collection c
WHERE    c.role='algorithm' and c.sequencenumber = 2 and c.pais_uid = m.pais_uid
GROUP BY m.pais_uid, m.tilename)
SELECT a.pais_uid, a.tilename, a.count, b.count, abs(a.count -b.count)/a.count as DIFF_RATIO FROM A, B where a.tilename = b.tilename;


-- Use a single temporary table:

WITH COUNT_TABLE as( 
  SELECT   m.pais_uid, m.tilename, count(*) AS count 
  FROM pais.markup_polygon m, pais.collection c
  WHERE    c.role='algorithm' and c.pais_uid = m.pais_uid
  GROUP BY m.pais_uid, m.tilename)
SELECT a.pais_uid, a.tilename, a.count, b.count, 
    CAST(CAST(abs(a.count-b.count) AS DECIMAL(6,0))/CAST(a.count AS DECIMAL(7,0))*100 
    AS DECIMAL(5,2) ) AS DIFF_RATIO 
FROM  COUNT_TABLE A , COUNT_TABLE B 
WHERE a.tilename = b.tilename AND A.pais_uid LIKE '%_1' AND B.pais_uid LIKE '%_2';




-- Find how many markups in Set A with multiple intersects in Set B:

SELECT COUNT(*) FROM(
SELECT pais_uid, tilename, markup_id, MIN(centroid_distance) 
FROM   PAIS.VALIDATION_PRECOMPUTE 
GROUP BY pais_uid, tilename, markup_id HAVING COUNT(markup_id) > 1
);

-- result:  224518 ; Total markups for set 1: 9034750



-- Query to do filtering:

SELECT pais_uid, tilename, markup_id, area_overlap_ratio, MIN(centroid_distance) 
FROM   PAIS.VALIDATION_PRECOMPUTE 
WHERE  area_overlap_ratio > 0.60 AND ABS(centroid_distance) < 10
GROUP BY pais_uid, tilename, markup_id;


SELECT pais_uid, tilename, markup_id, MAX(area_overlap_ratio) as ratio
FROM   PAIS.VALIDATION_PRECOMPUTE 
WHERE  area_overlap_ratio > 0.80 AND ABS(centroid_distance) < 10
GROUP BY pais_uid, tilename, markup_id;

-- statistics:
-- SELECT AVG(area_overlap_ratio),STDEV(area_overlap_ratio)

SELECT AVG(ratio),STDDEV(ratio) FROM (
 SELECT pais_uid, tilename, markup_id, MAX(area_overlap_ratio) as ratio
 FROM   PAIS.VALIDATION_PRECOMPUTE 
 WHERE  area_overlap_ratio > 0.80 AND ABS(centroid_distance) < 10
 GROUP BY pais_uid, tilename, markup_id);

-- AVG RATIO: 0.844  STDDEV: 0.086





--========================================================
SELECT COUNT(*) 
FROM  PAIS.MARKUP_POLYGON m, PAIS.COLLECTION c
WHERE c.PAIS_UID = m.PAIS_UID and c.role = 'algorithm' AND c.sequencenumber =1;
--9034750

SELECT COUNT(DISTINCT markup_id) 
FROM  PAIS.VALIDATION_PRECOMPUTE;
--9000590

--Markups with multiple overlaps:
--224518

-- number of markps diff by slide:
WITH COUNT_TABLE as( 
  SELECT  c.name, m.pais_uid, count(*) AS count 
  FROM pais.markup_polygon m, pais.collection c
  WHERE    c.role='algorithm' and c.pais_uid = m.pais_uid
  GROUP BY c.name, m.pais_uid)
SELECT a.pais_uid, CAST(a.count AS DECIMAL(9,0)) AS seta_count, CAST(b.count AS DECIMAL(9,0) ) AS setb_count, 
    CAST(CAST(abs(a.count-b.count) AS DECIMAL(9,0))/CAST(a.count AS DECIMAL(9,0))*100 
    AS DECIMAL(9,2) ) AS DIFF_RATIO 
FROM  COUNT_TABLE A , COUNT_TABLE B 
WHERE A.name = B.name AND A.pais_uid LIKE '%_1' AND B.pais_uid LIKE '%_2';

PAIS_UID                                                         SETA_COUNT  SETB_COUNT  DIFF_RATIO 
---------------------------------------------------------------- ----------- ----------- -----------
astroII.1_20x_20x_NS-MORPH_1                                          28436.      28733.        1.04
astroII.2_20x_20x_NS-MORPH_1                                         159654.     161445.        1.12
gbm0.1_20x_20x_NS-MORPH_1                                            461318.     460785.        0.11
gbm0.2_20x_20x_NS-MORPH_1                                            416215.     420695.        1.07
gbm1.1_20x_20x_NS-MORPH_1                                            534964.     575623.        7.60
gbm1.2_20x_20x_NS-MORPH_1                                            179916.     181986.        1.15
gbm2.1_20x_20x_NS-MORPH_1                                            294272.     294457.        0.06
gbm2.2_20x_20x_NS-MORPH_1                                            105297.     107004.        1.62
normal.2_20x_20x_NS-MORPH_1                                          388301.     399355.        2.84
normal.3_20x_20x_NS-MORPH_1                                          510443.     519977.        1.86
oligoastroII.1_20x_20x_NS-MORPH_1                                    222774.     221718.        0.47
oligoastroII.2_20x_20x_NS-MORPH_1                                    793349.     835314.        5.28
oligoastroIII.1_20x_20x_NS-MORPH_1                                   462016.     458878.        0.67
oligoastroIII.2_20x_20x_NS-MORPH_1                                   523639.     551920.        5.40
oligoII.1_20x_20x_NS-MORPH_1                                         712973.     714437.        0.20
oligoII.2_20x_20x_NS-MORPH_1                                         425724.     434322.        2.01
oligoIII.1_20x_20x_NS-MORPH_1                                        783911.     813284.        3.74
oligoIII.2_20x_20x_NS-MORPH_1                                       2031548.    2054944.        1.15

-- tiles are same:
WITH COUNT_TABLE as( 
    SELECT  c.name, m.pais_uid, m.tilename, count (*) AS count 
    FROM pais.markup_polygon m, pais.collection c
    WHERE    c.role='algorithm' and c.pais_uid = m.pais_uid
    GROUP BY c.name, m.pais_uid, m.tilename)
SELECT a.pais_uid, CAST(SUM(a.count) AS DECIMAL(9,0)) AS seta_count, CAST(SUM(b.count) AS DECIMAL(9,0) ) AS setb_count, 
    CAST(CAST((SUM(a.count-b.count) AS DECIMAL(9,0))/CAST(SUM(a.count) AS DECIMAL(9,0))*100 
    AS DECIMAL(9,2) ) AS DIFF_RATIO 
FROM  COUNT_TABLE A , COUNT_TABLE B 
WHERE A.name = B.name AND A.pais_uid LIKE '%_1' AND B.pais_uid LIKE '%_2' and A.tilename = b.tilename
GROUP BY A.pais_uid;

PAIS_UID                                                         SETA_COUNT  SETB_COUNT  DIFF_RATIO 
---------------------------------------------------------------- ----------- ----------- -----------
astroII.1_20x_20x_NS-MORPH_1                                          28436.      28733.       -1.04
astroII.2_20x_20x_NS-MORPH_1                                         159654.     161445.       -1.12
gbm0.1_20x_20x_NS-MORPH_1                                            461318.     460784.        0.11
gbm0.2_20x_20x_NS-MORPH_1                                            416215.     420693.       -1.07
gbm1.1_20x_20x_NS-MORPH_1                                            534964.     573222.       -7.15
gbm1.2_20x_20x_NS-MORPH_1                                            179916.     181986.       -1.15
gbm2.1_20x_20x_NS-MORPH_1                                            294272.     294453.       -0.06
gbm2.2_20x_20x_NS-MORPH_1                                            105297.     107001.       -1.61
normal.2_20x_20x_NS-MORPH_1                                          388301.     399355.       -2.84
normal.3_20x_20x_NS-MORPH_1                                          510443.     519977.       -1.86
oligoastroII.1_20x_20x_NS-MORPH_1                                    222774.     221718.        0.47
oligoastroII.2_20x_20x_NS-MORPH_1                                    793349.     835314.       -5.28
oligoastroIII.1_20x_20x_NS-MORPH_1                                   462016.     458878.        0.67
oligoastroIII.2_20x_20x_NS-MORPH_1                                   523639.     546998.       -4.46
oligoII.1_20x_20x_NS-MORPH_1                                         712973.     714437.       -0.20
oligoII.2_20x_20x_NS-MORPH_1                                         425724.     434322.       -2.01
oligoIII.1_20x_20x_NS-MORPH_1                                        783911.     813284.       -3.74
oligoIII.2_20x_20x_NS-MORPH_1                                       2031548.    2054944.       -1.15

  18 record(s) selected.



Total markups in set B:
--9234877
A: 9034750
SELECT COUNT(*) 
FROM  PAIS.MARKUP_POLYGON m, PAIS.COLLECTION c
WHERE c.PAIS_UID = m.PAIS_UID and c.role = 'algorithm' AND c.sequencenumber = 2;

Total markups in set B (tiles are also in set A);
SELECT COUNT(*) 
FROM  PAIS.MARKUP_POLYGON m, PAIS.COLLECTION c, PAIS.REGION r, PAIS.COLLECTION c2
WHERE m.tilename = r.name AND c.PAIS_UID = m.PAIS_UID AND
      c.role = 'algorithm' AND c.sequencenumber = 2 AND
      c2.pais_uid = r.pais_uid and c2.sequencenumber = 1;
-- 9227544


-- Overlap avg and stddev (filter multiple overlaps):
SELECT CAST( AVG(ratio) AS DECIMAL(4,3)) AS OVERLAPRATIO_AVG, CAST(STDDEV(ratio) AS DECIMAL(4,3) ) AS OVERLAPRATIO_STDDEV FROM (
 SELECT pais_uid, tilename, markup_id, MAX(area_overlap_ratio) as ratio
 FROM   PAIS.VALIDATION_PRECOMPUTE 
 GROUP BY pais_uid, tilename, markup_id);

OVERLAPRATIO_AVG OVERLAPRATIO_STDDEV
---------------- -------------------
           0.774               0.189


-- Overlap avg and stddev (filtered multi-overlap) per slide:
SELECT pais_uid, CAST( AVG(ratio) AS DECIMAL(4,3)) AS OVERLAPRATIO_AVG, CAST(STDDEV(ratio) AS DECIMAL(4,3) ) AS OVERLAPRATIO_STDDEV 
FROM (
 SELECT pais_uid, tilename, markup_id, MAX(area_overlap_ratio) as ratio
 FROM   PAIS.VALIDATION_PRECOMPUTE 
 GROUP BY pais_uid, tilename, markup_id)
GROUP BY pais_uid;


PAIS_UID                                                         OVERLAPRATIO_AVG OVERLAPRATIO_STDDEV
---------------------------------------------------------------- ---------------- -------------------
astroII.1_20x_20x_NS-MORPH_1                                                0.797               0.170
astroII.2_20x_20x_NS-MORPH_1                                                0.796               0.176
gbm0.1_20x_20x_NS-MORPH_1                                                   0.805               0.169
gbm0.2_20x_20x_NS-MORPH_1                                                   0.772               0.202
gbm1.1_20x_20x_NS-MORPH_1                                                   0.755               0.196
gbm1.2_20x_20x_NS-MORPH_1                                                   0.718               0.241
gbm2.1_20x_20x_NS-MORPH_1                                                   0.818               0.163
gbm2.2_20x_20x_NS-MORPH_1                                                   0.745               0.221
normal.2_20x_20x_NS-MORPH_1                                                 0.790               0.189
normal.3_20x_20x_NS-MORPH_1                                                 0.786               0.173
oligoastroII.1_20x_20x_NS-MORPH_1                                           0.815               0.165
oligoastroII.2_20x_20x_NS-MORPH_1                                           0.754               0.208
oligoastroIII.1_20x_20x_NS-MORPH_1                                          0.774               0.198
oligoastroIII.2_20x_20x_NS-MORPH_1                                          0.765               0.208
oligoII.1_20x_20x_NS-MORPH_1                                                0.803               0.173
oligoII.2_20x_20x_NS-MORPH_1                                                0.804               0.156
oligoIII.1_20x_20x_NS-MORPH_1                                               0.775               0.190
oligoIII.2_20x_20x_NS-MORPH_1                                               0.755               0.185


-- Distance based avg, stddev
SELECT CAST( AVG(distance) AS DECIMAL(4,2)) AS DISTANCE_AVG, CAST(STDDEV(distance) AS DECIMAL(4,2) ) AS DISTANCE_STDDEV 
FROM (
 SELECT pais_uid, tilename, markup_id, MIN(centroid_distance) AS distance
 FROM   PAIS.VALIDATION_PRECOMPUTE 
 GROUP BY pais_uid, tilename, markup_id);

DISTANCE_AVG DISTANCE_STDDEV
------------ ---------------
        1.37            2.38

-- by slide:
SELECT pais_uid, CAST( AVG(distance) AS DECIMAL(4,2)) AS DISTANCE_AVG, CAST(STDDEV(distance) AS DECIMAL(4,2) ) AS DISTANCE_STDDEV 
FROM (
 SELECT pais_uid, tilename, markup_id, MIN(centroid_distance) AS distance
 FROM   PAIS.VALIDATION_PRECOMPUTE 
 GROUP BY pais_uid, tilename, markup_id)
GROUP BY pais_uid;


PAIS_UID                                                         DISTANCE_AVG DISTANCE_STDDEV
---------------------------------------------------------------- ------------ ---------------
astroII.1_20x_20x_NS-MORPH_1                                             1.24            2.18
astroII.2_20x_20x_NS-MORPH_1                                             1.13            2.13
gbm0.1_20x_20x_NS-MORPH_1                                                1.25            2.23
gbm0.2_20x_20x_NS-MORPH_1                                                1.77            2.94
gbm1.1_20x_20x_NS-MORPH_1                                                1.40            2.40
gbm1.2_20x_20x_NS-MORPH_1                                                2.04            3.10
gbm2.1_20x_20x_NS-MORPH_1                                                1.06            2.04
gbm2.2_20x_20x_NS-MORPH_1                                                1.68            2.68
normal.2_20x_20x_NS-MORPH_1                                              1.19            2.26
normal.3_20x_20x_NS-MORPH_1                                              1.14            2.12
oligoastroII.1_20x_20x_NS-MORPH_1                                        1.17            2.14
oligoastroII.2_20x_20x_NS-MORPH_1                                        1.47            2.36
oligoastroIII.1_20x_20x_NS-MORPH_1                                       1.64            2.75
oligoastroIII.2_20x_20x_NS-MORPH_1                                       1.47            2.41
oligoII.1_20x_20x_NS-MORPH_1                                             1.13            1.98
oligoII.2_20x_20x_NS-MORPH_1                                             0.88            1.61
oligoIII.1_20x_20x_NS-MORPH_1                                            1.35            2.34
oligoIII.2_20x_20x_NS-MORPH_1                                            1.49            2.48



-- Filtering:
SELECT COUNT(*) FROM(
  SELECT pais_uid, markup_id, MAX(area_overlap_ratio) as ratio, MIN(centroid_distance)
  FROM   PAIS.VALIDATION_PRECOMPUTE 
  WHERE  area_overlap_ratio > 0.80 AND ABS(centroid_distance) < 10
  GROUP BY pais_uid, markup_id);

ratio    distance   left (Total: 9227544)
=========================
80%        10      5506735  
70%        10      7003246
60%        10      7713910

70%         5      6983290


--For collecting tile information for spatial sampling

------------------------------ Commands Entered 1------------------------------

SELECT A.A_pais_uid, A.tilename, B.nuclei_per_slide,CAST(AVG(ratio) AS DECIMAL(6,3)) AS avg_ratio, CAST(STDDEV(ratio) AS DECIMAL(6,3)) AS STDDEV_ratio, CAST(AVG(distance) AS DECIMAL(6,3)) AS avg_distance, CAST(STDDEV(distance) AS DECIMAL(6,3)) AS STDDEV_distance, count(*) AS NUCLEI_PER_TILE,  CAST(CAST(count(*) AS DECIMAL(8,1))/CAST(B.nuclei_per_slide AS DECIMAL(9,1)) AS DECIMAL(6,5)) AS density_perc
FROM (
SELECT pais_uid AS A_pais_uid, tilename, MAX(area_overlap_ratio) AS ratio, MIN(centroid_distance) AS distance 
FROM pais.validation_precompute 
GROUP BY pais_uid, tilename, markup_id) A,
(SELECT m.pais_uid AS B_pais_uid, count(*) AS nuclei_per_slide 
FROM pais.markup_polygon m, pais.collection c
WHERE c.pais_uid = m.pais_uid AND c.role='algorithm' AND c.sequencenumber = 1
GROUP BY m.pais_uid) B
WHERE A.A_pais_uid = B.B_pais_uid
GROUP BY A.A_pais_uid, A.tilename, B.nuclei_per_slide;
--;
------------------------------ Commands Entered 2------------------------------
SELECT A.A_pais_uid, A.tilename, CAST(AVG(ratio) AS DECIMAL(6,3)) AS avg_ratio, CAST(STDDEV(ratio) AS DECIMAL(6,3)) AS STDDEV_ratio, CAST(AVG(distance) AS DECIMAL(6,3)) AS avg_distance, CAST(STDDEV(distance) AS DECIMAL(6,3)) AS STDDEV_distance, count(*) AS NUCLEI_PER_TILE, MAX(B.nuclei_per_slide) AS NUCLEI_PER_SLIDE, CAST(CAST(count(*) AS DECIMAL(8,1))/CAST(MAX(B.nuclei_per_slide) AS DECIMAL(9,1)) AS DECIMAL(6,5)) AS density_perc
FROM (
SELECT pais_uid AS A_pais_uid, tilename, MAX(area_overlap_ratio) AS ratio, MIN(centroid_distance) AS distance 
FROM pais.validation_precompute 
GROUP BY pais_uid, tilename, markup_id) A,
(SELECT m.pais_uid AS B_pais_uid, count(*) AS nuclei_per_slide 
FROM pais.markup_polygon m, pais.collection c
WHERE c.pais_uid = m.pais_uid AND c.role='algorithm' AND c.sequencenumber = 1
GROUP BY m.pais_uid) B
WHERE A.A_pais_uid = B.B_pais_uid
GROUP BY A.A_pais_uid, A.tilename;
--;

