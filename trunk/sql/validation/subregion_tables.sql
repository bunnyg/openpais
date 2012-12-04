-- drop table validation.subregion;
-- drop table validation.subregion_markup;
-- drop table validation.subregion_density;
-- drop table TABLE VALIDATION.MARKUP_POLYGON_HUMAN;

(=
create table validation.subregion(
pais_uid VARCHAR(64),
tilename VARCHAR(64),
subregionname  VARCHAR(64),
subregiontilename VARCHAR(64),
x	DOUBLE,
y	DOUBLE,
width   DOUBLE,
height  DOUBLE,
polygon DB2GSE.ST_POLYGON
) in spatialtbs32k; 

--INSERT INTO validation.subregion 
--SELECT pais_uid, tilename, subregionname, tilename || '-' || subregionname, x, y, width, height, polygon) 
--FROM validation.subregion0;

create  table validation.subregion_markup(
pais_uid VARCHAR(64),
tilename VARCHAR(64),
subregionname  VARCHAR(64),
subregiontilename VARCHAR(64),
markup_id  DECIMAL(30,0)
);

create  table validation.subregion_density(
pais_uid VARCHAR(64),
tilename VARCHAR(64),
subregionname  VARCHAR(64),
nucleicount   INT
);


create  table validation.slide_density(
pais_uid VARCHAR(64),
nucleicount   BIGINT
);




CREATE TABLE VALIDATION.MARKUP_POLYGON_HUMAN  (
   MARKUP_ID			DECIMAL(30,0) NOT NULL,      
   NAME				VARCHAR(64),
   GEOMETRICSHAPE_ID		DECIMAL(30,0) NOT NULL,  
   ANNOTATION_ID		DECIMAL(30,0) NOT NULL,  
   POLYGON  			DB2GSE.ST_POLYGON INLINE LENGTH 2000,
   PROVENANCE_ID		DECIMAL(30,0),   
   TILENAME			VARCHAR(64) NOT NULL,
   PAIS_UID 			VARCHAR(64) NOT NULL 
--,    PRIMARY KEY(PAIS_UID, MARKUP_ID)	   
) 
COMPRESS YES
IN spatialtbs32k; 

=)

CREATE TABLE PAIS.MARKUP_POLYGON_NUCLEUS_HUMAN  (
   MARKUP_ID			DECIMAL(30,0) NOT NULL,      
   NAME				VARCHAR(64),
   GEOMETRICSHAPE_ID		DECIMAL(30,0) NOT NULL,  
   ANNOTATION_ID		DECIMAL(30,0) NOT NULL,  
   POLYGON  			DB2GSE.ST_POLYGON INLINE LENGTH 2000,
   PROVENANCE_ID		DECIMAL(30,0),   
   TILENAME			VARCHAR(64) NOT NULL,
   PAIS_UID 			VARCHAR(64) NOT NULL 
--,    PRIMARY KEY(PAIS_UID, MARKUP_ID)	   
) 
COMPRESS YES
IN spatialtbs32k; 

