-- db2 -td@ -f sp_gensubregionall.sql
-- db2 "CALL validation.gensubregionall(4)"
DROP PROCEDURE validation.gensubregionall @
CREATE PROCEDURE validation.gensubregionall(IN i_grid SMALLINT)	
LANGUAGE SQL
BEGIN
  DECLARE i_uid VARCHAR(64);
  DECLARE SQLSTATE CHAR(5) DEFAULT '00000';
  DECLARE at_end 	INTEGER DEFAULT 0;
  DECLARE not_found 	CONDITION for SQLSTATE '02000';

  DECLARE c1 CURSOR WITH HOLD FOR
     SELECT pais_uid FROM PAIS.COLLECTION  WHERE methodname = 'NS-MORPH' and sequencenumber ='1';
  DECLARE CONTINUE HANDLER for not_found 
    SET at_end = 1;
  OPEN c1;  
  fetch_loop:
   LOOP
    FETCH c1 INTO  i_uid;
    IF at_end <> 0 THEN LEAVE fetch_loop; END IF;
    CALL validation.gensubregion(i_uid, i_grid);
  END LOOP fetch_loop;
  CLOSE c1 WITH RELEASE;
END @
