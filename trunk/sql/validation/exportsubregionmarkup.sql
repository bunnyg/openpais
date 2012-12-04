CONNECT TO PAISEXT;
EXPORT TO "/tmp/subregionmarkupexport.txt" OF DEL MODIFIED BY COLDEL, MESSAGES "/tmp/export.msg" 
SELECT r.pais_uid, r.tilename, r.subregionname, m.markup_id
FROM pais.markup_polygon m, validation.subregion r, validation.subregion_density d 
WHERE r.pais_uid = d.pais_uid AND r.tilename = d.tilename AND r.subregionname = d.subregionname AND d.nucleicount >= 10 AND  
  m.pais_uid = r.pais_uid AND m.tilename = r.tilename AND
  DB2GSE.ST_Contains(r.polygon, m.polygon) = 1  selectivity 0.015625;
CONNECT RESET;
