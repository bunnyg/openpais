
-- UDF to convert SVG like representation to db2's point representations

CREATE FUNCTION PAIS.coords2wktpoly(str VARCHAR(30000) )  
RETURNS VARCHAR(30000) 
SPECIFIC PAIS.coords2wktpoly
LANGUAGE SQL
DETERMINISTIC
NO EXTERNAL ACTION
BEGIN ATOMIC  
DECLARE del VARCHAR(30000) default ','; 
DECLARE rstr VARCHAR(30000) default ''; 
DECLARE tstr VARCHAR(30000) default '';
DECLARE gisstr VARCHAR(30000) default '';
DECLARE iposition INT default 0;
DECLARE position  INT default 999;
DECLARE count     INT default 0;
DECLARE istr VARCHAR(24) default '';

SET gisstr = REPLACE( REPLACE( REPLACE(str, ' ', ';'), ',', ' '),  ';', ',' );
SET tstr = gisstr;
SET rstr = gisstr;

WHILE (position != 0)  DO
   SET position = locate(del, tstr);
   SET tstr = substr(tstr, position + 1);   
   IF (count = 0)  THEN
   	SET istr = substr (gisstr, 1, position-1); 
   END IF;	
   SET iposition = position;
   SET count = count+1;
END WHILE;

-- add first point as closing point if first and last point are different
--IF (trim(istr) != trim(tstr) ) THEN 
--  SET rstr = concat( concat(gisstr, del), istr);
--END IF;   

SET rstr = 'POLYGON ((' || rstr || '))';

RETURN rstr;

END@

