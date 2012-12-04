-- db2 -td@ -f sp_gensubregion.sql
-- db2 "CALL validation.gensubregion('oligoIII.2_40x_20x_NS-MORPH_1', 4)"
DROP PROCEDURE validation.gensubregion @
CREATE PROCEDURE validation.gensubregion(
   IN  i_uid  VARCHAR(64),
   IN i_grid SMALLINT)	
LANGUAGE SQL
BEGIN
  DECLARE i_tile VARCHAR(64);
  DECLARE SQLSTATE CHAR(5) DEFAULT '00000';
  DECLARE at_end 	INTEGER DEFAULT 0;
  DECLARE not_found 	CONDITION for SQLSTATE '02000';

  DECLARE c1 CURSOR WITH HOLD FOR
     SELECT name FROM PAIS.REGION  WHERE pais_uid =  i_uid;
  DECLARE CONTINUE HANDLER for not_found 
    SET at_end = 1;
  OPEN c1;  
  fetch_loop:
   LOOP
    FETCH c1 INTO  i_tile;
    IF at_end <> 0 THEN LEAVE fetch_loop; END IF;
    CALL validation.gensubregionbytile(i_uid, i_tile, i_grid);
  END LOOP fetch_loop;
  CLOSE c1 WITH RELEASE;
END @
