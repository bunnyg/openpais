-- db2 -td@ -f migrateplgn'.sql
-- SQLSTATE: 0D000; 38SSK
-- To call: db2 "call gis.migrateplgn(?,?)"
-- DROP PROCEDURE gis.migrateplgn@

CREATE PROCEDURE gis.migrateplgn(IN i_session_uid VARCHAR(32),
  OUT result 	VARCHAR(256))
LANGUAGE SQL
BEGIN

  DECLARE v_sqlstate CHAR(5);
  DECLARE v_sqlcode INT;
  DECLARE SQLCODE INT DEFAULT 0;
  DECLARE SQLSTATE CHAR(5) DEFAULT '00000';
 
  DECLARE V_SESSION_UID		VARCHAR(32);
  DECLARE V_MARKUP_UID		VARCHAR(64);
  DECLARE V_BOUNDARY		CLOB(2M);
  DECLARE V_DB2GSE_BOUNDARY     CLOB(2M);          
  DECLARE v_rows 	INTEGER DEFAULT 0;
  DECLARE v_failedrows 	SMALLINT DEFAULT 0;
  DECLARE at_end 	INTEGER DEFAULT 0;
  DECLARE geom_failed 	INTEGER DEFAULT 0;

  DECLARE not_found 	CONDITION for SQLSTATE '02000';
  --DECLARE not_closed 	CONDITION for SQLSTATE '38SSL';	
  --DECLARE bad_wkt 	CONDITION for SQLSTATE '38SV8';
  --DECLARE not_valid 	CONDITION for SQLSTATE '0D000';
  --DECLARE few_points 	CONDITION for SQLSTATE '38SSK';
	

  DECLARE c1 CURSOR WITH HOLD FOR 
  SELECT SESSION_UID, MARKUP_UID, BOUNDARY, DB2GSE_BOUNDARY FROM GIS.LARGEBOUNDARY WHERE SESSION_UID = I_SESSION_UID;
	
  DECLARE CONTINUE HANDLER for not_found 
    SET at_end = 1;
  DECLARE CONTINUE HANDLER for SQLEXCEPTION
    SELECT SQLSTATE, SQLCODE
	  INTO v_sqlstate, v_sqlcode
	  FROM sysibm.sysdummy1;  
  -- initialize OUT parameter
  SET result = '';

--  DELETE FROM GIS.BOUNDARY WHERE SESSION_UID = I_SESSION_UID;
  OPEN c1;
  --using UID, ID;
  fetch_loop:
  LOOP
    FETCH c1 INTO 
      V_SESSION_UID, V_MARKUP_UID, V_BOUNDARY, V_DB2GSE_BOUNDARY;
    IF at_end <> 0 THEN LEAVE fetch_loop;
    END IF;
	INSERT INTO GIS.LARGEBOUNDARY_POLYGON(SESSION_UID, MARKUP_UID, POLYGON)
        VALUES (V_SESSION_UID, V_MARKUP_UID, DB2GSE.ST_Polygon( V_DB2GSE_BOUNDARY, 100) );

	IF v_sqlcode <> 0 THEN
	   INSERT INTO GIS.LARGEGEOMFAIL(SESSION_UID, MARKUP_UID, SQLSTATE, BOUNDARY) VALUES(V_SESSION_UID, V_MARKUP_UID, v_sqlstate, V_BOUNDARY);
	   SET v_sqlcode = 0;
	   SET v_sqlstate = '00000';
	   SET v_failedrows = v_failedrows + 1;	   
	END IF;
	   
    SET v_rows = v_rows + 1;
	IF MOD(v_rows, 2000) = 0 THEN
	   COMMIT;
	END IF;
  END LOOP fetch_loop; 
  CLOSE c1 WITH RELEASE;
  COMMIT;
  SET result = 'Total rows: ' || v_rows || '; failed rows: ' || v_failedrows;
END @

-- Testing: 
-- delete from gis.largegeomfail;
-- delete from GIS.LARGEBOUNDARY;
-- INSERT INTO GIS.LARGEBOUNDARY VALUES('1', '1', '110,120 110,140 130,140 130,120 110,120', '(110 120, 110 140, 130 140, 130 120, 110 120)');
-- call gis.migrateplgn('1',?);

