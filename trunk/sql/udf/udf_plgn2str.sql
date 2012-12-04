-- db2 -td@ -f udf_plgn2str.sql
-- db2 -td@ -f udf_plgn2str.sql
-- db2 -td@ -f udf_plgn2str.sql
DROP FUNCTION PAIS.plgn2str@

CREATE FUNCTION PAIS.plgn2str(polygon DB2GSE.ST_POLYGON)  
RETURNS VARCHAR(30000) 
LANGUAGE SQL
DETERMINISTIC
NO EXTERNAL ACTION
BEGIN ATOMIC  
DECLARE del       VARCHAR(30000) default ')';
DECLARE plgnstr   VARCHAR(30000);
DECLARE length    INT;
DECLARE position  INT default 0;

SET plgnstr = CAST (polygon..ST_AsText AS  varchar(30000) );
SET length = length(plgnstr);
RETURN substr (plgnstr, 11, length - 12); 

END@


