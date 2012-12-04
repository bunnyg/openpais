
echo 'cal_mid';
CREATE INDEX PAIS.CAL_MID ON PAIS.CALCULATION_FLAT(PAIS_UID, MARKUP_ID);

echo 'mkp_plgn_tile';
--CREATE INDEX PAIS.mkp_plgn_tile  ON PAIS.MARKUP_POLYGON(pais_uid,tilename);
CREATE INDEX PAIS.mkp_plgn_tile ON PAIS.MARKUP_POLYGON(POLYGON) 
	extend using db2gse.spatial_index(100,0,0);
COMIIT WORK;

--CREATE INDEX PAIS.mkp_plgn_hmn_tile  ON PAIS.MARKUP_POLYGON_HUMAN(pais_uid,tilename);

echo 'obs_nom';
--CREATE INDEX PAIS.obs_nom_name  ON  pais.observation_quantification_nominal(OBSERVATION_NAME);
--CREATE INDEX PAIS.obs_nom_quant_value ON  pais.observation_quantification_nominal(quantification_value);

CREATE INDEX PAIS.obs_nom_name  ON  pais.observation_quantification_nominal(OBSERVATION_NAME, quantification_value);
CREATE INDEX PAIS.obs_nom_mid  ON  pais.observation_quantification_nominal(PAIS_UID, MARKUP_ID);

echo 'obs_ord';
--CREATE INDEX PAIS.obs_ord_name  ON  pais.observation_quantification_ordinal(OBSERVATION_NAME);
--CREATE INDEX PAIS.obs_ord_quant_value ON  pais.observation_quantification_ordinal(quantification_value);

CREATE INDEX PAIS.obs_ord_name  ON  pais.observation_quantification_ordinal(OBSERVATION_NAME, quantification_value);
CREATE INDEX PAIS.obs_ord_mid  ON  pais.observation_quantification_ordinal(PAIS_UID, MARKUP_ID);


echo 'MAPIDX';
--CREATE INDEX PAIS.MAPIDX ON PAIS.MARKUP_POLYGON(POLYGON) EXTEND USING DB2GSE.SPATIAL_INDEX (15, 30, 110);
-- Updated for AVP data:
CREATE INDEX PAIS.MAPIDX ON PAIS.MARKUP_POLYGON(POLYGON) EXTEND USING DB2GSE.SPATIAL_INDEX (110, 0, 0);

RUNSTATS ON TABLE pais.markup_polygon with distribution AND sampled detailed INDEXES ALL;
RUNSTATS ON TABLE pais.markup_polygon_human AND INDEXES ALL;
RUNSTATS ON TABLE PAIS.CALCULATION_FLAT AND INDEXES ALL;
RUNSTATS ON TABLE pais.observation_quantification_nominal AND INDEXES ALL;
RUNSTATS ON TABLE pais.observation_quantification_ordinal AND INDEXES ALL;
RUNSTATS ON TABLE pais.region AND INDEXES ALL;
RUNSTATS ON TABLE pais.collection AND INDEXES ALL;
RUNSTATS ON TABLE pais.patient AND INDEXES ALL;

RUNSTATS ON TABLE pais.provenance AND INDEXES ALL;
RUNSTATS ON TABLE pais.algorithm AND INDEXES ALL;  
RUNSTATS ON TABLE pais.parameter AND INDEXES ALL; 

COMMIT WORK;

 
