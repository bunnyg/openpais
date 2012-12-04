-- db2 -td@ -f sp_gensubregionmarkup.sql
-- db2 "CALL validation.gensubregionmarkup('oligoIII.2_40x_20x_NS-MORPH_1')"
DROP PROCEDURE validation.gensubregionmarkup @
CREATE PROCEDURE validation.gensubregionmarkup( IN  i_uid  VARCHAR(64) )
LANGUAGE SQL
BEGIN
  DECLARE i_tile VARCHAR(64);
  DECLARE SQLSTATE CHAR(5) DEFAULT '00000';
  DECLARE at_end 	INTEGER DEFAULT 0;
  DECLARE not_found 	CONDITION for SQLSTATE '02000';

  DECLARE c1 CURSOR WITH HOLD FOR
     SELECT distinct tilename FROM VALIDATION.SUBREGION  WHERE pais_uid =  i_uid;
  DECLARE CONTINUE HANDLER for not_found 
    SET at_end = 1;
  OPEN c1;  
  fetch_loop:
   LOOP
    FETCH c1 INTO  i_tile;
    IF at_end <> 0 THEN LEAVE fetch_loop; END IF;

    INSERT INTO validation.subregion_markup(pais_uid, tilename, subregionname, markup_id)
    SELECT r.pais_uid, r.tilename, r.subregionname, m.markup_id
    FROM pais.markup_polygon m, validation.subregion r, validation.subregion_density d 
    WHERE r.pais_uid = d.pais_uid AND r.tilename = d.tilename AND r.subregionname = d.subregionname AND    
       d.nucleicount >= 10 AND  m.pais_uid = r.pais_uid AND m.tilename = r.tilename AND
       DB2GSE.ST_Contains(r.polygon, m.polygon) = 1  selectivity 0.015625 AND 
       r.tilename =i_tile and r.pais_uid = i_uid;

    COMMIT;
  END LOOP fetch_loop;
  COMMIT;
  CLOSE c1 WITH RELEASE;
END @
