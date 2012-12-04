drop table db2gse.gse_srs_replicated_ast;
-- MQT to replicate the SRS information across all nodes in a DPF environment
CREATE TABLE db2gse.gse_srs_replicated_ast AS
   ( SELECT srs_name, srs_id, x_offset, x_scale, y_offset, z_offset, z_scale,
            m_offset, m_scale, definition
     FROM   db2gse.gse_spatial_reference_systems )
   DATA INITIALLY DEFERRED
   REFRESH IMMEDIATE
   ENABLE QUERY OPTIMIZATION
   REPLICATED
   IN SPATIALTBS32K
--   IN TSPACE0

;

REFRESH TABLE db2gse.gse_srs_replicated_ast;

CREATE  INDEX db2gse.gse_srs_id_ast
   ON db2gse.gse_srs_replicated_ast ( srs_id )
;

SET INTEGRITY FOR db2gse.gse_srs_replicated_ast ALL IMMEDIATE UNCHECKED;

RUNSTATS ON TABLE db2gse.gse_srs_replicated_ast and indexes all;
