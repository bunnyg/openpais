Release1.1 version is modified by Zhengwen Zhou @CCI 12/4/2012

Content:
	1> "Admin" folder is the default SQL folder of creating dpf database. 
	2> Add "create_tables_dpf.sql" for creating tables with tilename partitioned key.
	3> Add "create_talbes_dpf_withCentroid.sql" for creating tables with Centroid attributes in pais.markup_polygon talbe.
	4> Add "create_tables_signle_withCentroid.sql" -- same as above but for partitioning feature.
	5> Add "create_LOBtbs.sql" for creating lob tablespace for staging table.
	6> Add "index.sql" for modifying the spatial index by only using polygon column. 