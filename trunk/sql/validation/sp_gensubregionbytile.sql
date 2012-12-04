-- db2 -td@ -f sp_gensubregionbytile.sql
-- db2 "call validation.gensubregionbytile('gbm1.1_40x_20x_NS-MORPH_1', 'gbm1.1-0000040960-0000040960', 4)"
DROP PROCEDURE validation.gensubregionbytile@
CREATE PROCEDURE  validation.gensubregionbytile(
  IN  i_uid      VARCHAR(64),
  IN  i_tilename VARCHAR(64),  
  IN  i_grid     SMALLINT   
)
LANGUAGE SQL
BEGIN

  DECLARE v_sqlstate CHAR(5);
  DECLARE v_sqlcode INT;
  DECLARE SQLCODE INT DEFAULT 0;
  DECLARE SQLSTATE CHAR(5) DEFAULT '00000';

  DECLARE v_subregionname  VARCHAR(64);

-- Original tile info:  
  DECLARE V_WIDTH	  	DOUBLE;
  DECLARE V_HEIGHT		DOUBLE;
  DECLARE V_OX			DOUBLE;
  DECLARE V_OY			DOUBLE;

-- new subregion info:
  DECLARE V_X0			DOUBLE;
  DECLARE V_Y0			DOUBLE;
  DECLARE V_X1			DOUBLE;
  DECLARE V_Y1			DOUBLE;
  
  DECLARE i			SMALLINT DEFAULT 1;
  DECLARE j			SMALLINT DEFAULT 1;
  DECLARE V_POLYGONSTR		VARCHAR(512);

  DECLARE v_rows 	INTEGER  DEFAULT 0;
  DECLARE v_failedrows 	SMALLINT DEFAULT 0;
  DECLARE at_end 	INTEGER  DEFAULT 0;
  DECLARE not_found 	CONDITION for SQLSTATE '02000';
  
  DECLARE c1 CURSOR WITH HOLD FOR 
      SELECT X, Y, WIDTH, HEIGHT 
    FROM PAIS.REGION  WHERE PAIS_UID = i_uid and NAME = i_tilename;

	
  DECLARE CONTINUE HANDLER for not_found 
    SET at_end = 1;
  DECLARE CONTINUE HANDLER for SQLEXCEPTION
    SELECT SQLSTATE, SQLCODE
	  INTO v_sqlstate, v_sqlcode
	  FROM sysibm.sysdummy1;  


  OPEN c1;  
  fetch_loop:
  LOOP
      FETCH c1 INTO  V_OX, V_OY, V_WIDTH, V_HEIGHT;
      LEAVE fetch_loop;
  END LOOP fetch_loop;
  
  CLOSE c1 WITH RELEASE;
 
--  DELETE FROM pais.tmpoverlap;
  
  SET j = 1;  
  SET i = 1;  
  
   WHILE ( i <= i_grid) DO 
    WHILE ( j <= i_grid) DO 
      -- CALL DBMS_OUTPUT.PUT_LINE('i: ' || i || ' j: ' || j);

      SET v_subregionname =  i || '_' || j;
      SET V_X0 = V_OX + V_WIDTH/i_grid * (i-1) ;
      SET V_Y0 = V_OY + V_HEIGHT/i_grid * (j-1) ;
      SET V_X1 = V_OX + V_WIDTH/i_grid * i;
      SET V_Y1 = V_OY + V_HEIGHT/i_grid * j;
      
      -- CALL DBMS_OUTPUT.PUT_LINE('V_X1: ' || CAST( V_X1 AS INTEGER) );
      -- CALL DBMS_OUTPUT.PUT_LINE('V_Y1: ' || CAST( V_Y1 AS INTEGER) );
      
      SET V_POLYGONSTR = 'polygon((' ||  V_X0 || ' '|| V_Y0 ||',' || V_X1 || ' '|| V_Y0 ||',' || V_X1 || ' '|| V_Y1 
||',' || V_X0 || ' '|| V_Y1 ||',' || V_X0 || ' '|| V_Y0 ||'))';
      
      -- CALL DBMS_OUTPUT.PUT_LINE('Linestring: ' || V_POLYGONSTR);
            
      INSERT INTO validation.subregion(
         pais_uid,   tilename,   subregionname,   x,    y,    width,   height,   polygon) VALUES(
         i_uid,      i_tilename, v_subregionname, V_X0, V_Y0, V_WIDTH, V_HEIGHT, DB2GSE.ST_Polygon(V_POLYGONSTR, 100) 
      );
     SET j = j + 1;  
    END WHILE;     
    SET i = i + 1; 
    SET j = 1;
   END WHILE;

  IF v_sqlcode <> 0 THEN
   SET v_sqlcode = 0;
   SET v_sqlstate = '00000';
   SET v_failedrows = v_failedrows + 1;	   
  END IF;



  COMMIT;
  
END @
