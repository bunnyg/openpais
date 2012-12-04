-- create: db2 -td@ -f udf_rgn2plgn.sql
-- test: 
-- select  PAIS.rgn2plgn(x, y, width, height) from pais.region 
-- where pais_uid='gbm0.1_20x_20x_NS-MORPH_1' and name='gbm0.1.ndpi-0000028672-0000032768';

DROP FUNCTION PAIS.rgn2plgn@

CREATE FUNCTION PAIS.rgn2plgn(x DECIMAL(30,1), y DECIMAL(30,1), width DECIMAL(30,1), height DECIMAL(30,1))  
RETURNS DB2GSE.ST_POLYGON
--RETURNS VARCHAR(512)
LANGUAGE SQL
DETERMINISTIC
NO EXTERNAL ACTION
BEGIN ATOMIC  
--DECLARE x VARCHAR(64);
--DECLARE y VARCHAR(64);
--DECLARE width VARCHAR(64);
--DECLARE height VARCHAR(64);
DECLARE points VARCHAR(512) default '';

--SET x = CAST (ix as VARCHAR(64) );
--SET y = CAST (iy as VARCHAR(64) );
--SET width  = CAST (iwidth as VARCHAR(64) );
--SET height = CAST (iheight as VARCHAR(64) );


--'polygon((28672 32768, 32768 32768,  32768 36864, 28672 36864, 28672 32768))'
SET points =  CAST(x AS VARCHAR(64) ) || ' ' || CAST(y AS VARCHAR(64)) || ',' || CAST( (x + width) AS VARCHAR(64)) || ' ' || CAST(y AS VARCHAR(64)) || ',' || CAST( (x + width) AS VARCHAR(64) ) || ' ' || CAST( (y + height) AS VARCHAR(64)) || ',' || CAST(x AS VARCHAR(64)) || ' ' || CAST( (y +height)  AS VARCHAR(64)) || ',' || CAST(x AS VARCHAR(64)) || ' ' || CAST(y AS VARCHAR(64));

--SET points = x || ' ' || y || ',' || x + width || ' ' || y || ',' || x + width || ' ' || y + height || ',' || x || ' ' || y +height || ',' || x || ' ' || y;

-- SET points = '''' || varchar(x);

set points = 'polygon((' || points || '))';

RETURN DB2GSE.ST_Polygon(points, 100);
--RETURN points;

END@


