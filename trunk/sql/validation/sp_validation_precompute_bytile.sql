-- db2 -td@ -f sp_validation_precompute.sql
-- db2 "CALL PAIS.VALIDATION_PRECOMPUTE_BYTILE('oligoIII.2_20x_20x_NS-MORPH_1','oligoIII.2.ndpi-0000090112-0000024576')"

DROP PROCEDURE PAIS.VALIDATION_PRECOMPUTE_BYTILE@
CREATE PROCEDURE PAIS.VALIDATION_PRECOMPUTE_BYTILE(
IN  i_uid      VARCHAR(64),
IN  i_tilename VARCHAR(64)
)
LANGUAGE SQL
BEGIN
  DECLARE i_uid2 VARCHAR(64);           
  DECLARE i_tile VARCHAR(64);
  DECLARE SQLSTATE CHAR(5) DEFAULT '00000';
  DECLARE at_end 	INTEGER DEFAULT 0;
  DECLARE not_found 	CONDITION for SQLSTATE '02000';


  DECLARE c2 CURSOR WITH HOLD FOR  
     SELECT c2.pais_uid FROM PAIS.COLLECTION c1, PAIS.COLLECTION c2 WHERE c1.name = c2.name and c1.PAIS_UID = i_uid and
     c2.sequencenumber = 2;

  DECLARE CONTINUE HANDLER for not_found 
    SET at_end = 1;

  OPEN c2;  
    FETCH c2 INTO i_uid2;

        INSERT INTO PAIS.VALIDATION_PRECOMPUTE(pais_uid, tilename, AREA_OVERLAP_RATIO, centroid_distance) 
	SELECT A.pais_uid, A.tilename,  CAST(  db2gse.ST_Area(db2gse.ST_Intersection(a.polygon, b.polygon) )/db2gse.ST_Area(db2gse.ST_UNION( a.polygon, b.polygon))  AS DECIMAL(5,4) )
AS area_overlap_ratio,  CAST( DB2GSE.ST_DISTANCE( db2gse.ST_Centroid(b.polygon), db2gse.ST_Centroid(a.polygon) )  AS DECIMAL(5,2) ) AS centroid_distance
FROM pais.markup_polygon A, pais.markup_polygon B
WHERE  A.pais_uid = i_uid and A.tilename = i_tile and 
       B.pais_uid = i_uid2 and B.tilename = i_tile and
       DB2GSE.ST_intersects(A.polygon, B.polygon) = 1;


  CLOSE c2 WITH RELEASE;
  COMMIT;
  
END @
