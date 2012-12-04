package edu.emory.cci.pais.api;

import java.util.HashMap;


public class Queries {
	
	private String[] featureArray = {

			  "AREA",
			  "PERIMETER",
			  "ECCENTRICITY",
			  "CIRCULARITY",
			  "MAJOR_AXIS",
			  "MINOR_AXIS",  
			  "EXTENT_RATIO",
			  "MEAN_INTENSITY",
			  "MAX_INTENSITY ",
			  "MIN_INTENSITY ",
			  "STD_INTENSITY ",
			  "ENTROPY",
			  "ENERGY",
			  "SKEWNESS",
			  "KURTOSIS",
			  "MEAN_GRADIENT_MAGNITUDE",
			  "STD_GRADIENT_MAGNITUDE",   
			  "ENTROPY_GRADIENT_MAGNITUDE",   
			  "ENERGY_GRADIENT_MAGNITUDE",   
			  "SKEWNESS_GRADIENT_MAGNITUDE", 
			  "KURTOSIS_GRADIENT_MAGNITUDE",   
			  "SUM_CANNY_PIXEL",
			  "MEAN_CANNY_PIXEL",
			"CYTO_H_MeanIntensity", 
			"CYTO_H_MeanMedianDifferenceIntensity", 
			"CYTO_H_MaxIntensity", 
			"CYTO_H_MinIntensity", 
			"CYTO_H_StdIntensity",
			"CYTO_H_Entropy", 
			"CYTO_H_Energy", 
			"CYTO_H_Skewness", 
			"CYTO_H_Kurtosis",
			"CYTO_H_MeanGradMag", 
			"CYTO_H_StdGradMag", 
			"CYTO_H_EntropyGradMag", 
			"CYTO_H_EnergyGradMag",
			"CYTO_H_SkewnessGradMag", 
			"CYTO_H_KurtosisGradMag", 
			"CYTO_H_SumCanny", 
			"CYTO_H_MeanCanny",
			"CYTO_E_MeanIntensity", 
			"CYTO_E_MeanMedianDifferenceIntensity", 
			"CYTO_E_MaxIntensity", 
			"CYTO_E_MinIntensity", 
			"CYTO_E_StdIntensity",
			"CYTO_E_Entropy", 
			"CYTO_E_Energy", 
			"CYTO_E_Skewness", 
			"CYTO_E_Kurtosis",
			"CYTO_E_MeanGradMag", 
			"CYTO_E_StdGradMag", 
			"CYTO_E_EntropyGradMag", 
			"CYTO_E_EnergyGradMag",
			"CYTO_E_SkewnessGradMag", 
			"CYTO_E_KurtosisGradMag", 
			"CYTO_E_SumCanny", 
			"CYTO_E_MeanCanny",
			"CYTO_G_MeanIntensity", 
			"CYTO_G_MeanMedianDifferenceIntensity", 
			"CYTO_G_MaxIntensity", 
			"CYTO_G_MinIntensity", 
			"CYTO_G_StdIntensity",
			"CYTO_G_Entropy", 
			"CYTO_G_Energy", 
			"CYTO_G_Skewness", 
			"CYTO_G_Kurtosis",
			"CYTO_G_MeanGradMag", 
			"CYTO_G_StdGradMag", 
			"CYTO_G_EntropyGradMag", 
			"CYTO_G_EnergyGradMag",
			"CYTO_G_SkewnessGradMag", 
			"CYTO_G_KurtosisGradMag", 
			"CYTO_G_SumCanny", 
			"CYTO_G_MeanCanny" };
			

	private static HashMap<String, String> map = new HashMap<String, String>();
	
	String  query = "SELECT distinct imagereference_uid " +
	"FROM pais.wholeslideimagereference";
	
	public void addQuery(String name, String query){
		map.put(query, query);
	}
	
	public String getQuery(String name){
		return map.get(name);
	}
	
	
	public Queries(){
		addAllQueries();		
	}
	
	
/*	
	public String getImageUIDFromPAISUID(String paisUid){
		
	}*/
	
	private void addAllQueries(){
		String name ="";
		String query ="";
		
		/**
		 * Return All PAIS DOCUMENT UIDS 
		 */
		name = "getPaisUids";
		query = 
		" SELECT pais_uid " + 
		" FROM pais.collection " + 
		" WHERE role = ? ";		
		map.put(name,query);
		
		/**
		 * Return All image reference UIDS 
		 */
		name = "getImageReferenceUIDs";
		query = "SELECT distinct imagereference_uid " +
		"FROM pais.wholeslideimagereference";				
		map.put(name, query);
		
		/**
		 * Return All SLIDEUIDS
		 */
		name = "getSlideUids";
		query = 
		" SELECT DISTINCT NAME FROM PAIS.COLLECTION ";
		map.put(name,query);
		
		/**
		 * Return All Patient UIDS in the PAIS schema 
		 */
		name = "getPatientUIDs";
		query = "SELECT distinct patientid " +
		"FROM pais.patient";				
		map.put(name, query);
		
		/**  
		 * Return nuclear density of a Tile
		 * @tilename  e.g.: TCGA-27-1836-01Z-DX2-0000004096-0000016384 
		 */
		name = "getNuclearDensityByTile";
		query= "SELECT COUNT(*) AS count " +
				"FROM pais.markup_polygon " +
				"WHERE  tilename = ? ";	
		map.put(name, query);
		
		/**
		 * Return nuclear density of a image 
		 * @param imagereference_uid  e.g.: 'TCGA-27-1836-01Z-DX2_20x'
		 * @param  methodname
		 * @param sequencenumber
		 */
		
		name = "getNuclearDensityByImage";
		query =
		" SELECT m.tilename, COUNT(*) AS count " +
		" FROM   pais.markup_polygon m, " +
		" PAIS.WHOLESLIDEIMAGEREFERENCE i, PAIS.COLLECTION c " +
		" WHERE  i.PAIS_UID = c.PAIS_UID AND " +
		" m.pais_uid=c.pais_uid AND " +
		" i.IMAGEREFERENCE_UID = ? AND " +
		" c.methodname = ? AND " +
		" c.sequencenumber = ? " +
		" GROUP BY m.tilename ";
		map.put(name, query);		
		
		/** 
		 * Return nuclear density of a PAIS document group by tile name
		 * @param pais_uid  e.g.: 'TCGA-27-1836-01Z-DX2_20x_20x_NS-MORPH_1' 
		 */
		name = "getNuclearDensityByDoc";
		query= "SELECT TILENAME, COUNT(*) AS count " +
				"FROM   pais.markup_polygon " +
				"WHERE  pais_uid = ? " +
				"GROUP BY tilename";	
		map.put(name, query);

		/** 
		 * Return all nuclear density group by tile name and imagereference_uid
		 * @param  methodname
		 * @param sequencenumber
		 */	
		name = "getNuclearDensityGroupByImageuid";
		query = 
		" SELECT i.imagereference_uid, m.tilename, COUNT(*) AS count " +
		" FROM  pais.markup_polygon m, " +
		" PAIS.WHOLESLIDEIMAGEREFERENCE i, PAIS.COLLECTION c " +
		" WHERE i.PAIS_UID = c.PAIS_UID AND " +
		" m.pais_uid=c.pais_uid AND " +
		" c.methodname = ? AND " +
		" c.sequencenumber =? " +
		" GROUP BY i.imagereference_uid, m.tilename ";
		map.put(name, query);
		
		/** 
		 * Return all nuclear density group by tile name and PAIS document UID
		 */
		name = "getNuclearDensityGroupByDoc";
		query = "SELECT pais_uid, tilename, COUNT(*) AS count " +
				"FROM   pais.markup_polygon " +
				"GROUP BY pais_uid, tilename";
		map.put(name, query);	

		
		/** @param pais_uid e.g.: 'TCGA-27-1836-01Z-DX2_20x_20x_NS-MORPH_1'
		 *  @tilename  e.g.: TCGA-27-1836-01Z-DX2-0000004096-0000016384 */
		name = "getBoundariesFromTile";
		query = "SELECT PAIS.plgn2str(m.POLYGON) AS boundary " +
				"FROM   pais.markup_polygon m " +
				"WHERE  m.pais_uid = ?  AND " +
				"m.tilename = ? ";			
		map.put(name, query);
		
		name = "getTilePosition";
		query = "SELECT x, y from PI.TILEDIMAGE where tilename = ?";
		map.put(name, query);		
		
		/** 
		 * @param pais_uid  e.g.: 'TCGA-27-1836-01Z-DX2_20x_20x_NS-MORPH_1'
		 * @param tilename e.g.: 'TCGA-27-1836-01Z-DX2-0000004096-0000016384'
		 * @param  methodname e.g.: 'NS-MORPH'
		 * @param sequencenumber e.g.: '1'
		 */
		
/*		name = "getBoundariesFromPaisUidFromTileWithParam";
		
		query = 
		" SELECT PAIS.plgn2str(m.POLYGON) AS boundary " +
		" FROM  pais.markup_polygon m, PAIS.WHOLESLIDEIMAGEREFERENCE i, PAIS.COLLECTION c " +
		" WHERE i.PAIS_UID = c.PAIS_UID AND " +
		" m.pais_uid=c.pais_uid AND " +
		" m.pais_uid = ?  AND " +
		" m.tilename = ? AND " +
		" c.methodname = ? AND " +
		" c.sequencenumber = ? AND " +
		" c.pais_uid = m.pais_uid ";

		map.put(name, query);*/
		
		/** 
		* @param pais_uid e.g.: 'TCGA-27-1836-01Z-DX2_20x_20x_NS-MORPH_1'
		* @param tilename e.g.: 'TCGA-27-1836-01Z-DX2-0000004096-0000016384'
		* @param points   e.g.:  'polygon((4096 16384, 4496 16384, 4496 20384, 4096 20384, 4096 16384)) " 
		* Example query: 
		SELECT pais.plgn2str(polygon) AS boundary FROM pais.markup_polygon WHERE  pais_uid ='TCGA-27-1836-01Z-DX2_20x_20x_NS-MORPH_1' 
	 	AND tilename ='TCGA-27-1836-01Z-DX2-0000004096-0000016384' and DB2GSE.ST_Contains(DB2GSE.ST_Polygon(
		'polygon((4096 16384, 4496 16384, 4496 20384, 4096 20384, 4096 16384))', 
	 	100), polygon) = 1;
		 * */

		name ="getBoundariesFromRectangleFromTile";
		query = 
			"SELECT pais.plgn2str(m.polygon) AS boundary " +
			"FROM   pais.markup_polygon m, pais.WHOLESLIDEIMAGEREFERENCE i " +
			"WHERE  i.imagereference_uid=?  AND " +
			"       m.pais_uid = i.pais_uid  AND " +
			"       DB2GSE.ST_Contains(DB2GSE.ST_Polygon( CAST (? AS VARCHAR(30000) ), 100), polygon ) =1";		
		map.put(name, query);
		
		/**
		 * @param pais_uid e.g.: 'TCGA-27-1836-01Z-DX2_20x_20x_NS-MORPH_1'
		 * @param tilename e.g.: 'TCGA-27-1836-01Z-DX2-0000004096-0000016384'
		 * @param polygon e.g.: 'polygon((4096 16384, 4496 16384, 4496 20384, 4096 20384, 4096 16384))'
		 */
		name ="getBoundariesFromPolygon";
		query =
			" SELECT pais.plgn2str(polygon) AS boundary " +
			" FROM   pais.markup_polygon " +
			" WHERE  pais_uid = ?  AND " +
			" tilename = ? AND " +
			" DB2GSE.ST_Contains( DB2GSE.ST_Polygon( CAST (? AS VARCHAR(30000) ), 100), polygon ) = 1 ";
		map.put(name, query);
		
		/**
		 * Return tilename and boundaries containing a point
		 * @param tilename
		 * @param point coordinates
		 */
		name ="getBoundariesOfPointFromTile";
		query = 
			"SELECT m.tilename, pais.plgn2str(m.polygon) AS boundary " +
			"FROM   pais.markup_polygon m, " +
			" PAIS.WHOLESLIDEIMAGEREFERENCE i, PAIS.COLLECTION c " +
			" WHERE i.PAIS_UID = c.PAIS_UID AND " +
			" m.pais_uid=c.pais_uid AND " +
			" i.imagereference_uid=? AND " +
			" c.methodname = ? AND " +
			" c.sequencenumber =? AND " +
			" DB2GSE.ST_Contains(m.polygon, DB2GSE.ST_POINT( CAST (? AS VARCHAR(30000) ), 100)) =1";		
		map.put(name, query);
		
		/**
		 * Return intersection ratio and centroid distances of two result sets for a Tile T for two PAIS documents
		 * @param tilename e.g. 'gbm1.1-0000040960-0000040960'
		 * @param pais_uid1 e.g. 'gbm1.1_40x_20x_NS-MORPH_1'
		 * @param paid_uid2 e.g. 'gbm1.1_40x_20x_NS-MORPH_2'
		 */
		name = "getIntersectionRatio";
		query = 
			"SELECT A.pais_uid, A.tilename, " + 
			"CAST(db2gse.ST_Area(db2gse.ST_Intersection(a.polygon, b.polygon) )/db2gse.ST_Area(db2gse.ST_UNION( a.polygon, b.polygon))  AS DECIMAL(4,2) ) AS area_ratio," +
			"CAST( DB2GSE.ST_DISTANCE( db2gse.ST_Centroid(b.polygon), db2gse.ST_Centroid(a.polygon) )  AS DECIMAL(5,2) ) AS centroid_distance " +
			"FROM pais.markup_polygon A, pais.markup_polygon B " +
			"WHERE  A.tilename=? and A.pais_uid=? and " +
			"B.tilename=? and B.pais_uid=? and " +
			"DB2GSE.ST_intersects(A.polygon, B.polygon) = 1 selectivity 0.0001 WITH UR";
		map.put(name, query);
		/**
		 * @param tilename e.g.: 'TCGA-27-1836-01Z-DX2-0000004096-0000016384'
		 * @param methodname e.g.: 'NS-MORPH'
		 * @param sequencenumber e.g.: 1
		 */
		
		name = "getPolygonCentroidsFromTile";
		query = 
			" SELECT DB2GSE.ST_X( DB2GSE.ST_CENTROID(m.POLYGON) ) AS CENTROID_X, " +
			" DB2GSE.ST_Y( DB2GSE.ST_CENTROID(m.POLYGON) ) AS CENTROID_Y " +
			" FROM  pais.markup_polygon m, PAIS.WHOLESLIDEIMAGEREFERENCE i, PAIS.COLLECTION c " +
			" WHERE i.PAIS_UID = c.PAIS_UID AND " +
			" m.pais_uid=c.pais_uid AND " +
			" m.tilename = ? AND " +
			" c.methodname = ? AND " +
			" c.sequencenumber = ? AND " +
			" c.pais_uid = m.pais_uid";

		map.put(name,query);
		
		/**
		 * @param tilename e.g.: 'TCGA-27-1836-01Z-DX2-0000004096-0000016384'
		 * @param x: 4096 y:4096 l: 300 h:300
		 */
		
		name = "getPolygonCentroidsFromWindow";
		query = 
			" SELECT  DB2GSE.ST_X( DB2GSE.ST_CENTROID(m.POLYGON) ) AS CENTROID_X, " +
			" DB2GSE.ST_Y( DB2GSE.ST_CENTROID(m.POLYGON) ) AS CENTROID_Y " +
			" FROM pais.markup_polygon m, pais.WHOLESLIDEIMAGEREFERENCE i " + 
			" where i.imagereference_uid = ? AND m.pais_uid=i.pais_uid AND " +
			" DB2GSE.ST_Contains(DB2GSE.ST_Polygon( CAST (? AS VARCHAR(30000) ), 100), polygon ) =1";

		map.put(name,query);
		
		name = "getCentroidsFromPolygon";
		query = 
			" SELECT  DB2GSE.ST_X( DB2GSE.ST_CENTROID(m.POLYGON) ) AS CENTROID_X, " +
			" DB2GSE.ST_Y( DB2GSE.ST_CENTROID(m.POLYGON) ) AS CENTROID_Y " +
			"FROM   pais.markup_polygon m" +
			" WHERE  pais_uid = ?  AND " +
			" tilename = ? AND " +
			" DB2GSE.ST_Contains( DB2GSE.ST_Polygon( CAST (? AS VARCHAR(30000) ), 100), polygon )=1";

		map.put(name,query);
		
		/**
		 * @param paisuid e.g.: 'TCGA-27-1836-01Z-DX2_20x_20x_NS-MORPH_1'
		 * @param methodname e.g.: 'NS-MORPH'
		 * @param sequencenumber e.g.: 1
		 */
		
		/*name = "getPolygonCentroidsFromPaisuid";
		query = 
			" SELECT DB2GSE.ST_X( DB2GSE.ST_CENTROID(m.POLYGON) ) AS CENTROID_X, " +
			" DB2GSE.ST_Y( DB2GSE.ST_CENTROID(m.POLYGON) ) AS CENTROID_Y " +
			" FROM  pais.markup_polygon m, PAIS.WHOLESLIDEIMAGEREFERENCE i, PAIS.COLLECTION c " +
			" WHERE i.PAIS_UID = c.PAIS_UID AND " +
			" m.pais_uid=c.pais_uid AND " +
			" m.pais_uid = ? AND " +
			" c.methodname = ? AND " +
			" c.sequencenumber = ? AND " +
			" c.pais_uid = m.pais_uid";
		
		map.put(name,query); */
		
		/**
		 * @param tilename e.g.: 'TCGA-02-0001-01Z-00-DX1-0-0'
		 */

		name = "getPaisDocFromTile";
		query =
			" SELECT BLOB " + 
			" FROM PAIS.STAGINGDOC " +
			" WHERE UID = ? AND TILENAME = ? ";
		
		map.put(name,query);
		
		/**
		 * @param paisuid e.g.: 'TCGA-15-1448-01Z-00-DX1_20x_20x_NS-MORPH_1'
		 * @param tilename e.g.: 'TCGA-15-1448-01Z-00-DX1-0000040960-0000032768'
		 * 
		 */
		
		name = "getFeaturesFromTile";
		StringBuffer f = new StringBuffer("SELECT PAIS_UID, ");
		StringBuffer features = new StringBuffer();
		int lengthOfArray = featureArray.length;
		for (int i=0; i < lengthOfArray-1; i++){
			features.append(featureArray[i] + " AS " + featureArray[i] );
				features.append(",");				
		}
		features.append(featureArray[lengthOfArray-1]);
		
		f.append(features);
		f.append(" FROM PAIS.CALCULATION_FLAT WHERE PAIS_UID = ? AND TILENAME = ? ");
		query = f.toString();
		map.put(name, query);
		
		name = "getRegionImageDummy";
		query = 
			"SELECT PI.getRegionImage('TCGA-08-0358-01Z-00-DX1_20X', 'png', 7706, 1444, 500, 500 ) " +
			"FROM SYSIBM.SYSDUMMY1";
		map.put(name, query);
		
		/**
		 * @param paisuid e.g.: 'TCGA-15-1448-01Z-00-DX1_20x_20x_NS-MORPH_1'
		 * @param tilename e.g.: 'TCGA-15-1448-01Z-00-DX1-0000040960-0000032768'
		 * 
		 */
		
		name = "getFeaturesFromRectangleOrPolygon";
		query = "SELECT c.* FROM pais.markup_polygon m, PAIS.CALCULATION_FLAT c WHERE m.PAIS_UID = ? AND m.TILENAME = ? " +
		        "and m.tilename = c.tilename and m.pais_uid = c.pais_uid and " +
				"DB2GSE.ST_Contains( DB2GSE.ST_Polygon( CAST (? AS VARCHAR(30000) ), 100),polygon ) = 1";
		
		map.put(name, query);
		
		
		/**
		 * @param paisuid e.g.: 'TCGA-27-1836-01Z-DX2_20x_20x_NS-MORPH_1'
		 */		
		name = "getMeanFeatureVectorByPAISUID";
		StringBuffer fb = new StringBuffer("SELECT ");
		StringBuffer avgf = new StringBuffer();
		int length = featureArray.length;
		for (int i=0; i < length; i++){
			avgf.append("AVG(" + featureArray[i] + ")" + " AS " + featureArray[i] );
			if (i != (length - 1) )
				avgf.append(",");				
		}
		fb.append(avgf);
		fb.append(" FROM PAIS.CALCULATION_FLAT WHERE PAIS_UID = ? ");
		query = fb.toString();
		map.put(name, query);

		
		name = "getMeanFeatureVectorForAllPAISUID";
		fb = new StringBuffer("SELECT PAIS_UID, ");		
		fb.append(avgf);
		fb.append(" FROM PAIS.CALCULATION_FLAT GROUP BY PAIS_UID");
		query = fb.toString();
		map.put(name, query);			
	
		/**
		 * @param paisuid e.g.: 'TCGA-27-1836-01Z-DX2_20x_20x_NS-MORPH_1'
		 */		
		name = "getPreMeanFeatureVectorByPAISUID";
		query = "SELECT * FROM PAIS.MEAN_FEATURE_VECTOR WHERE PAIS_UID = ? ";
		map.put(name, query);

		
		name = "getPreMeanFeatureVectorForAllPAISUID";
		query = "SELECT * FROM PAIS.MEAN_FEATURE_VECTOR";
		map.put(name, query);	
		
		
		/**
		 * @param paisuid e.g.: 'TCGA-27-1836-01Z-DX2_20x_20x_NS-MORPH_1'
		 */		
		name = "getMeanFeatureVectorAsTableByPAISUID";
		query = "SELECT *  from pais.feature_aggregate WHERE PAIS_UID = ? ORDER BY PAIS_UID";
		map.put(name, query);			
		
		name = "getPreMeanFeatureVectorForImage";
		query = "SELECT f.* " +
		" FROM  pais.MEAN_FEATURE_VECTOR f, " +
		" PAIS.WHOLESLIDEIMAGEREFERENCE i, PAIS.COLLECTION c " +
		" WHERE  i.PAIS_UID = c.PAIS_UID AND " +
		" f.pais_uid=c.pais_uid AND " +
		" i.IMAGEREFERENCE_UID = ? AND " +
		" c.methodname = ? AND " +
		" c.sequencenumber = ? ";
		map.put(name, query);
		
		name = "getPreMeanFeatureVectorForPatient";
		query = "SELECT f.* " +
		" FROM  pais.MEAN_FEATURE_VECTOR f, " +
		" PAIS.PATIENT p, PAIS.COLLECTION c " +
		" WHERE  p.PAIS_UID = c.PAIS_UID AND " +
		" f.pais_uid=c.pais_uid AND " +
		" p.patientid = ? AND " +
		" c.methodname = ? AND " +
		" c.sequencenumber = ? ";
		map.put(name, query);
		
		name = "getFeatureHistogram";
		query = 
		" SELECT frequency, binstart, binend" + 
		" FROM pais.feature_histogram " + 
		" WHERE  pais_uid = ? and feature = ? AND set_id = 1 " +
		" ORDER BY binstart";		
		map.put(name,query);
		
		name = "getFeatureMean";
		query = 
		" SELECT avg, stddev" + 
		" FROM pais.feature_aggregate " + 
		" WHERE  pais_uid = ? and feature = ? ";		
		map.put(name,query);		
		
		/*******************************************************************************************/
		/************************************PIDB image queries*************************************/
		/*******************************************************************************************/
		
		name = "getImageUids";
		query = 
		" SELECT I.IMAGEREFERENCE_UID " +
		" FROM PI.IMAGE I ";
		map.put(name, query);	
	
		/**
		 * @param PATIENTID
		 *  */
		name = "getImageUidsByPatientId";
		query = 
		" SELECT I.IMAGEREFERENCE_UID " +
		" FROM PI.IMAGE I, PI.PATIENT P " +
		" WHERE P.ID = I.PATIENT_ID AND " +
		" P.PATIENTID = ? ";
		map.put(name, query);		
		
		name = "getImageUidsByStudyName";
		query = 
		" SELECT I.IMAGEREFERENCE_UID " +
		" FROM  PI.EXPERIMENTALSTUDY ES, PI.DATASET D, PI.IMAGE I " +
		" WHERE " +
		" ES.DATASET_ID = D.ID AND " +
		" I.DATASET_ID = D.ID AND " + 
		" ES.NAME = ?";
		map.put(name,query);
		
		name = "getTileNames";
		query = 
		" SELECT T.TILENAME " +
		" FROM PI.TILEDIMAGE T ";
		map.put(name,query);
		
		/**
		 * @param IMAGEREFERENCE_UID
		 *  */
		
		name = "getTileNamesByImageUid";
		query = 
		" SELECT T.TILENAME " +
		" FROM PI.TILEDIMAGE T, PI.IMAGE I " +
		" WHERE " +
		" T.IMAGE_ID = I.ID AND " +
		" I.IMAGEREFERENCE_UID = ? ";
		map.put(name,query);
	
		/**
		 * @param TILESETNAME
		 *  */
		name = "getTileListsByTilesetName";
		query = 
		" SELECT TI.NAME " +
		" FROM PI.TILEDIMAGE TI " + 
		" WHERE TI.TILESET_ID = ?";
		map.put(name,query);
		
		name = "getImagePatientUids";
		query = 
		" SELECT PATIENTID " +
		" FROM PI.PATIENT";		
		map.put(name,query);		
		
		/**
		 * @param image_uid
		 * @param PointX
		 * @param PointY
		 *  */
		name = "getTileNameOfPoint";
		query = 
		" SELECT T.TILENAME " + 
		" FROM PI.TILEDIMAGE T, PI.IMAGE I" +
		" WHERE " + 
		" T.IMAGE_ID = I.ID AND " +
		" I.IMAGEREFERENCE_UID = ? AND" +
		" T.X = FLOOR(?/4096)*4096 AND" +
		" T.Y = FLOOR(?/4096)*4096";
		map.put(name, query);	
		
		name = "getTiledImageLocationPath";
		query = 
		" SELECT CONCAT (L.FOLDER, T.NAME) " +
	    " FROM PI.TILEDIMAGE T, PI.LOCATION L " +
	    " WHERE T.LOCATION_ID = L.ID AND " +
	    " T.TILENAME = ? AND " +
	    //" T.TILESET_ID = ? AND " +
	    " T.RESOLUTION =? ";		
		map.put(name, query);
		
		/**
		 * @param image_uid
		 * @param format
		 * @param x
		 * @param y
		 * @param width
		 * @height height
		 * Example SQL: SELECT PI.GETREGIONIMAGE('TCGA-06-0166-01Z-00-DX4_20X', 'PNG', 20000, 20000, 200, 200) FROM SYSIBM.SYSDUMMY1;
		 *  */
		name = "getSubRegionImage";
		query = "SELECT PI.GETREGIONIMAGE(?, ?, ?, ?, ?, ?) " + 
		"FROM SYSIBM.SYSDUMMY1";
		map.put(name, query);
	
		
		/**
		 * @param format
		 * @param x
		 * @param y
		 * @param width
		 * @height height
		 * @param pais_uid
		 * Example SQL:  
		 *  SELECT PI.getRegionImage(imagereference_uid, 'png', 7706, 1444, 500, 500 ) 
			FROM  PAIS.WHOLESLIDEIMAGEREFERENCE  
			WHERE pais_uid = 'TCGA-27-1836-01Z-DX2_20x_20x_NS-MORPH_1'
		 *  */
		name = "getSubRegionImageFromPAISUID";
		query = "SELECT PI.GETREGIONIMAGE(imagereference_uid, ?, ?, ?, ?, ?) " + 
				"FROM PAIS.WHOLESLIDEIMAGEREFERENCE " +
				"WHERE PAIS_UID = ? ";
		map.put(name, query);
		
/*		name = "getRegionImageDummy";
		query = 
			"SELECT PI.getRegionImage('TCGA-08-0358-01Z-00-DX1_20X', 'png', 7706, 1444, 500, 500 ) " +
			"FROM SYSIBM.SYSDUMMY1";
		map.put(name, query);
		
		name = "getRegionImageDummy";
		query = 
			"SELECT PI.getRegionImage('TCGA-08-0358-01Z-00-DX1_20X', 'png', 7706, 1444, 500, 500 ) " +
			"FROM SYSIBM.SYSDUMMY1";
		map.put(name, query); */				

		name = "getImageLocationPath";
		query = 
		" SELECT CONCAT (L.FOLDER, I.NAME) " +
	    " FROM PI.IMAGE I, PI.LOCATION L " +
	    " WHERE I.LOCATION_ID = L.ID AND " +
	    " I.IMAGEREFERENCE_UID = ? ";
		map.put(name, query);		
		
		name = "getThumbnailImageByTilename";
		query = 
		" SELECT T.THUMBNAIL " +
		" FROM PI.TILEDIMAGE T " +
		" WHERE T.TILENAME = ? ";
		map.put(name,query);
		
		name = "getThumbnailImageByImageUid";
		query = 
		" SELECT THUMBNAIL " +
		" FROM PI.IMAGE " +
		" WHERE imagereference_uid = ? ";
		map.put(name,query);
				
		
		/**
		 * @param TILENAME
		 * @param FORMAT
		 * @param X
		 * @param Y
		 * @param WIDTH
		 * @height HEIGHT
		 *  
		name = "getSubRegionTileImage";
		query = "SELECT PI.GETGLOBALREGIONIMAGETILE(?, ?, ?, ?, ?, ?) " + 
		"FROM SYSIBM.SYSDUMMY1";
		map.put(name, query); */
		

		name = "getSimilarNucleiIcons";
		query = 
		" SELECT pais_uid, image FROM  VIEWER.NUCLEUS_IMG2 " + // test with VIEWER.NUCLEUS_IMG2 for smaller images(50x); to be changed to _SLIDE2 
		" WHERE  pais_uid = ? ";
		map.put(name,query);	
		
		
		name = "getSimilarNucleiIconsAll";
		query = 
		" SELECT pais_uid, image FROM VIEWER.NUCLEUS_IMG2 ";	//test with VIEWER.NUCLEUS_IMG2 for smaller images(50x); to be changed to _SLIDE2 	
		
		map.put(name,query);		
		
		name = "getSimilarNucleiByCluster";
		query = 
		" SELECT IMAGEREFERENCE_UID, SUBREGION_IMG FROM TCGA.NUCLEI_CLUSTER_TOPK_SUBREGION " + 
		" WHERE  IMAGEREFERENCE_UID = ? and CLUSTER_LABEL = ? ORDER BY rank";
		map.put(name,query);	
		
		name = "getPatientBarcode";
		query = "select bcr_patient_barcode from tcga.patient_characteristic";
		map.put(name,query);	
		
		name="getPatientChar";
		query="select * from pais.patient,tcga.patient_characteristic where pais.patient.patientid=tcga.patient_characteristic.cpatientid and tcga.patient_characteristic.bcr_patient_barcode= ? ";
		map.put(name,query);
		
		name="patientidfrompaisuid";
		query="select bcr_patient_barcode,pais.patient.patientid from pais.patient,tcga.patient_characteristic where pais.patient.patientid=tcga.patient_characteristic.cpatientid and pais.patient.pais_uid= ? ";
		map.put(name,query);
		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
