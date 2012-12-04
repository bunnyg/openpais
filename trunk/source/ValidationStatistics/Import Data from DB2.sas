*======================================================================
* This Program Shows How to Connect PAIS DB2 Database Via SAS Proc SQL
*======================================================================;
libname cci "C:\"; * Path of local folder in which datasets will be stored;

options nodate nonumber; 

*=================================================
* Connect to DB2 database
*=================================================;
LIBNAME pais DB2 DATASRC=PAIS USER=db2user PASSWORD=password SCHEMA=PAIS; 

***Import SUBREGION_TEXTURE_FEATURE***;

proc sql;
	connect to db2 (database=PAIS USER=db2user PASSWORD=password);
	create table cci.SUBREGION_TEXTURE_FEATURE as 
	select * from connection to db2(select * from VALIDATION.SUBREGION_TEXTURE_FEATURE);
	%put &SQLXMSG;
	disconnect from db2;
quit;

proc print data=cci.SUBREGION_TEXTURE_FEATURE (obs=10); run;

***Import SUBREGION***;
proc sql;
	connect to db2 (database=PAIS USER=db2user PASSWORD=password);
	create table cci.SUBREGION as 
	select * from connection to db2(select * from VALIDATION.SUBREGION);
	%put &SQLXMSG;
	disconnect from db2;
quit;

proc print data=cci.SUBREGION (obs=10); run;


*** Keep only non-empty subregions ***;
proc sql;
	connect to db2 (database=PAIS USER=db2user PASSWORD=password);
	create table cci.subregion_texture_nonnull as 
	select * from connection to db2(select * from validation.subregion_texture_feature f, 
									VALIDATION.SUBREGION s where f.SUBREGIONTILENAME = s.SUBREGIONTILENAME);
	%put &SQLXMSG;
	disconnect from db2;
quit;

proc print data=cci.subregion_texture_nonnull (obs=10); run;

*** Include only useful variables: ID variables (for further merging) and strata info;
data cci.subregion_texture_nonnull_small; 
	retain  PAIS_UID TILENAME SUBREGIONNAME SUBREGIONTILENAME STRATUMLABEL;
	set cci.subregion_texture_nonnull;
	keep PAIS_UID TILENAME SUBREGIONNAME SUBREGIONTILENAME STRATUMLABEL;
run;

proc print data=cci.subregion_texture_nonnull_small (obs=10); run;	

proc sort data=cci.subregion_texture_nonnull_small;
	by PAIS_UID TILENAME SUBREGIONNAME SUBREGIONTILENAME;
run;
