-- db2 -td@ -f sp_validation_precompute.sql
-- db2 "CALL PAIS.VALIDATION_PRECOMPUTE('oligoIII.2_20x_20x_NS-MORPH_1')"

DROP PROCEDURE PAIS.VALIDATION_PRECOMPUTE@
CREATE PROCEDURE PAIS.VALIDATION_PRECOMPUTE(IN  i_uid  VARCHAR(64))
LANGUAGE SQL
BEGIN
  DECLARE i_uid2 VARCHAR(64);           
  DECLARE i_tile VARCHAR(64);
  DECLARE SQLSTATE CHAR(5) DEFAULT '00000';
  DECLARE at_end 	INTEGER DEFAULT 0;
  DECLARE not_found 	CONDITION for SQLSTATE '02000';

  DECLARE c1 CURSOR WITH HOLD FOR  
     SELECT r.name FROM PAIS.REGION r, PAIS.COLLECTION c where r.pais_uid = c.pais_uid and c.role ='algorithm' and sequencenumber=1 and r.PAIS_UID = i_uid;

  DECLARE c2 CURSOR WITH HOLD FOR  
     SELECT c2.pais_uid FROM PAIS.COLLECTION c1, PAIS.COLLECTION c2 WHERE c1.name = c2.name and c1.PAIS_UID = i_uid and
     c2.sequencenumber = 2;

  DECLARE CONTINUE HANDLER for not_found 
    SET at_end = 1;

    OPEN c2;
    FETCH c2 INTO i_uid2;
--    INSERT INTO TESTSTR VALUES(i_uid2);    
	
  OPEN c1;  
  fetch_loop:
   LOOP
      	FETCH c1 INTO  i_tile;
--        INSERT INTO TESTSTR VALUES(i_tile);
        COMMIT;
        IF at_end <> 0 THEN LEAVE fetch_loop; END IF;
        INSERT INTO PAIS.VALIDATION_PRECOMPUTE(pais_uid, tilename, markup_id, AREA_OVERLAP_RATIO, centroid_distance) 
	SELECT A.pais_uid, A.tilename, A.markup_id,  CAST(  db2gse.ST_Area(db2gse.ST_Intersection(a.polygon, b.polygon) )/db2gse.ST_Area(db2gse.ST_UNION( a.polygon, b.polygon))  AS DECIMAL(5,4) )
AS area_overlap_ratio,  CAST( DB2GSE.ST_DISTANCE( db2gse.ST_Centroid(b.polygon), db2gse.ST_Centroid(a.polygon) )  AS DECIMAL(5,2) ) AS centroid_distance
FROM pais.markup_polygon A, pais.markup_polygon B
WHERE  A.pais_uid = i_uid and A.tilename = i_tile and 
       B.pais_uid = i_uid2 and B.tilename = i_tile and
       DB2GSE.ST_intersects(A.polygon, B.polygon) = 1 
SELECTIVITY 0.0001 WITH UR; 
       COMMIT;

   END LOOP fetch_loop;
 
  CLOSE c1 WITH RELEASE;
  CLOSE c2 WITH RELEASE;
  COMMIT;
  
END @
