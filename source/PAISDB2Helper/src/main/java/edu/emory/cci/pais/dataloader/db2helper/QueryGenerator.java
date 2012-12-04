package edu.emory.cci.pais.dataloader.db2helper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.emory.cci.pais.dataloader.partition.PartitioningKeyGenerator;
import edu.emory.cci.pais.dataloader.partition.PartitioningKeyGenerator.Partitioner;

public class QueryGenerator {
	public static String DELIM ="\\^";
	
	
	private static String markupPolygonTableName;
	private int CentFlag =0;
	
	protected PartitioningKeyGenerator partitioningKeyGenerator;
	
	// QueryGenerator is implemented as a singleton
	private static QueryGenerator queryGenerator = null;
	protected LoadingConfig loadingConfig = null;
	
	private QueryGenerator(String file) {
		loadingConfig = new LoadingConfig(file);
		markupPolygonTableName = loadingConfig.getSchemaTableNames().get("markupPolygonTable");
	}
	
	public static QueryGenerator getQueryGenerator() {
		if(queryGenerator == null) {
			throw new IllegalStateException("The query generator has not been initialized yet. " +
					"The query generator can be initialized by calling getQueryGenerator(String dataLoaderConfigFile). " +
					"After the QueryGenerator has been initialized once, this method can be used to get the singleton instance.");
		}
		return queryGenerator;
	}
	
	public static QueryGenerator getQueryGenerator(String file) {
		if(queryGenerator == null) {
			queryGenerator = new QueryGenerator(file);
			return queryGenerator;
		} else {
			throw new IllegalStateException("The query generator has already been initialized. " +
					"The existing singleton can be retrieved with getQueryGenerator() or reseted and created again with " +
					"reset() and getQueryGenerator(String dataLoaderConfigFile).");
		}
	}
	
	public static void reset() {
		queryGenerator = null;
	}
	
	public void createPartitonKeyGenerator(PAISDBHelper db, int numOfPartitions) {
		this.partitioningKeyGenerator = PartitioningKeyGenerator.getInstance(Partitioner.TILE_BASED, db, numOfPartitions);
	}
	
	public static boolean isStringType(String type){
		if(type == null) 			return false;
		if(type.toUpperCase().contains("VARCHAR")) 	return true;
		if(type.toUpperCase().contains("CHAR")) 	return true;
		if(type.toUpperCase().contains("CLOB")) 	return true;
		if(type.toUpperCase().contains("DB2GSE"))	return true;
		if(type.toUpperCase().contains("DECIMAL")) 	return false;
		if(type.toUpperCase().contains("DOUBLE"))  	return false;
		if(type.toUpperCase().contains("INTEGER")) 	return false;
		if(type.toUpperCase().contains("FLOAT")) 	return false;
		return true;
	}

	
	public String generatePreparedRegionSQL(){
		return generatePreparedSQL(loadingConfig.getSchemaTableNames().get("regionTable"), loadingConfig.getTableMatrix("regionTable")).toString();		
	}	
	
	public String generatePreparedMarkupSQL(){
		return generatePreparedSQL(markupPolygonTableName, loadingConfig.getTableMatrix("markupPolygonTable")).toString();		
	}
	
	public String generatePreparedAnnotationMarkupSQL(){
		return generatePreparedSQL(loadingConfig.getSchemaTableNames().get("annotationMarkupTable"), loadingConfig.getTableMatrix("annotationMarkupTable")).toString();
	}	

	public boolean addBatchMarkupQuery(PreparedStatement pstmt, HashMap <String, String> valueMap){
		String markupType = valueMap.get("GeometricShape/@xsitype");
		if("Polygon".equals(markupType)){
			return setQueryParams(pstmt, markupPolygonTableName, valueMap, loadingConfig.getTableMatrix("markupPolygonTable"));
		}
		return false;
	}	
	
	public boolean addBatchAnnotationMarkupQuery(PreparedStatement pstmt, HashMap <String, String> valueMap){
			return setQueryParams(pstmt, loadingConfig.getSchemaTableNames().get("annotationMarkupTable"), valueMap, loadingConfig.getTableMatrix("annotationMarkupTable"));
	}		
	
	public boolean addBatchRegionQuery(PreparedStatement pstmt, HashMap <String, String> valueMap){
		return setQueryParams(pstmt, loadingConfig.getSchemaTableNames().get("regionTable"), valueMap, loadingConfig.getTableMatrix("regionTable"));
	}	
	
	
	public void setCentFlag(){
		this.CentFlag = 1;
	}
	public int getCentFlag(){
		return this.CentFlag;
	}
	
	
	public StringBuffer generatePreparedSQL(String tableName, String[][] table){
		StringBuffer queryBuf = new StringBuffer("INSERT INTO " + tableName + "(");
		String polygonKey = "GeometricShape/@points";
		
		
		for (int i = 0; i < table.length; i++ ){
			String [] map = table[i];
			String attribute = map[1];
			if (i != 0) queryBuf.append(", ");
			queryBuf.append(attribute);
		}

		/* add centroid point to the Markup Talbe*/
		if (this.CentFlag == 1 && tableName.equals(markupPolygonTableName) )
		{
			queryBuf.append(", CENTROID_X, ");
			queryBuf.append("CENTROID_Y");
		}		
		queryBuf.append(") VALUES(");
		
		for (int i = 0; i < table.length; i++ ){
			String [] map = table[i];
			String key = map[0];
			String value = "?";
			if(tableName.equals(markupPolygonTableName) && key.equals(polygonKey) ){
				//value = "DB2GSE.ST_Polygon(PAIS.coords2wktpoly(?) , 100)";
				//B2GSE.ST_Polygon('polygon((29500 35500, 34500 35500, 34500 49000, 29500 49000, 29500 35500))', 100)
				//value = "DB2GSE.ST_Polygon(?, 100)";  //This one does not work
				value = "DB2GSE.ST_Polygon( CAST (? AS VARCHAR(30000) ), 100)";
				//value = "DB2GSE.ST_Polygon( CAST (? AS CLOB(2M) ), 100)";
			}  			
			if (i != 0) queryBuf.append(", ");
			queryBuf.append(value);
		}
		
		
		if (CentFlag == 1 && tableName.equals(markupPolygonTableName))
		{
			queryBuf.append(", ?");			//for Centroid_X
			queryBuf.append(", ?");			//for Centroid_y
		}
		
		queryBuf.append(")");
		//System.out.println("statement: "+queryBuf);
		return queryBuf;
	}	

	public boolean setQueryParams(PreparedStatement pstmt, String tableName, HashMap<String,String> valueMap, String[][] table ){
		String polygonKey = "GeometricShape/@points";
		String tokens[]; 
		String demi = "[(), ]+";
		int j;
		int i;
		ArrayList<Double> cent = new ArrayList<Double>(); 
		
		for ( i = 0; i < table.length; i++ ){
			String [] map = table[i];
			String key = map[0];
			String value = valueMap.get(key);
			
			try {
				if(tableName.equals(markupPolygonTableName) && key.equals(polygonKey) ) {
					
					String polygonValue = GISHelper.convert(value);
					
					
					/*Code block to calculate the centroid of a polygon */
					if (this.CentFlag == 1)
					{
						//The first element is not a polygon point -- "POLYGON"
						//The LAST pair of (x,y) is the same as the first pair, showing that it's a closed polygon.
						tokens = polygonValue.split(demi);
	
						/* output the original array of polygon points
						 * 
						System.out.println("INSERT POLYGON:  ");
						System.out.println(polygonValue);
					
						for (j=0;j<tokens.length;j++)
						{
							System.out.print(tokens[j] + " ");
							if (j%2 == 0)
								System.out.print(", ");
						}
						System.out.println("");
						 */
						
			
						if ((cent = CentOfPoly(tokens, (tokens.length-3)/2)) == null)
						{
							System.out.println("CENTROID is bad!");
						}
						
					}
					
					
					pstmt.setString(i+1, polygonValue );
				}
				else if(key.contains("partitionKey")) {
					String tileName = valueMap.get("Region/@name");
					pstmt.setInt(i+1, partitioningKeyGenerator.getPartitionKeyByTilename(tileName));
				}
				else 
					pstmt.setString(i+1, value);
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}			
		}

		
		try {
			if (tableName.equals(markupPolygonTableName) && CentFlag == 1){
				pstmt.setDouble(i+1, cent.get(0));
				pstmt.setDouble(i+2, cent.get(1));
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		
		try {
			pstmt.addBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/* calculating the centroid of a polygon string */
	/*Reference: http://paulbourke.net/geometry/polyarea/ */
	
	public ArrayList<Double>  CentOfPoly(String[] input, int N) {
		
		/*
		 * To fit our input split array, the first element is "POLYGON".
		 * Then it starts a sequence of pairs of X and Y (N+1 pairs).
		 * Finally, the last pair (N+1) should be the same pair of the first, showing that it's a closed polygon.
		 * 
		 * So N should be the number of polygon point pairs (x, y), not including the (N+1)th pair.
		 * Then the length of this input array should be "1+2N+2"
		 * 
		 */
		
		int i,j;
		
		double cx = 0;				//	(Xi + Xi+1) * factor 
		double cy = 0;				//	(Yi + Yi+1) * factor
		double factor; 			// 	XiYi+1 - Xi+1Yi 
		double area = 0;
		
		ArrayList<Double> cent = new ArrayList<Double>();
		
		if  ( !(Integer.parseInt(input[1]) == Integer.parseInt(input[2*N+1]) && Integer.parseInt(input[2]) == Integer.parseInt(input[2*N+2])))
		{
			System.out.println("CENTROID ERROR: NOT A CLOSED POLYGON!");
			System.out.println("Head: "+ Integer.parseInt(input[1]) + "," + Integer.parseInt(input[2]) + " Tail: " + Integer.parseInt(input[2*N+1]) + "," + Integer.parseInt(input[2*N+2]));
			
			return null;
		}
		
		
		for (i=0;i<N; i++)
		{
			factor = Integer.parseInt(input[2*i+1])*Integer.parseInt(input[2*(i+1)+2]) - Integer.parseInt(input[2*(i+1)+1])*Integer.parseInt(input[2*i+2]);
			area += factor;
			
			cx += (Integer.parseInt(input[2*i+1]) + Integer.parseInt(input[2*(i+1)+1])) * factor;
			cy += (Integer.parseInt(input[2*i+2]) + Integer.parseInt(input[2*(i+1)+2])) * factor;
			
		}
		
		area = area/2;

		
		cx = cx/(6*area);
		cy = cy/(6*area);
		
		cent.add(cx);
		cent.add(cy);
		
			
		return cent; 
		
	} 
	
	
	
	
	
	
	
	
	
	
	
	public int getQuantificationTypeCount(String []  quantificationTypes, String type){
		int typeCount = 0;
		for(String quantificationType: quantificationTypes){
			if(type.equals(quantificationType) )typeCount++;
		}
		return typeCount;
	}
	
	
	
	public String generatePareparedObservationSQL(String quantificationType){

			StringBuffer queryBuf = new StringBuffer("INSERT INTO "+loadingConfig.getSchemaTableNames().get("observationOrdinalMultiTable")+"(");;
			String [][] table = loadingConfig.getTableMatrix("observationOrdinalMultiTable"); 

			if("Nominal".equals(quantificationType)){
				queryBuf = new StringBuffer("INSERT INTO "+loadingConfig.getSchemaTableNames().get("observationNominalMultiTable")+"(");
				table = loadingConfig.getTableMatrix("observationNominalMultiTable"); 
			}

			for (int i = 0; i < loadingConfig.getTableMatrix("observationSingleTable").length; i++ ){
				String attribute = loadingConfig.getTableMatrix("observationSingleTable")[i][1];
				if (i != 0) queryBuf.append(", ");
				queryBuf.append(attribute);
			}				
			queryBuf.append(",");
			for (int i = 0; i < table.length; i++ ){
				String attribute = table[i][1];
				if (i != 0) queryBuf.append(", ");
				queryBuf.append(attribute);
			}
			queryBuf.append(") VALUES(");
			//Add single value attributes
			for (int i = 0; i < loadingConfig.getTableMatrix("observationSingleTable").length; i++ ){			
				if (i != 0) queryBuf.append(", ");
				queryBuf.append("?");
			}				
			queryBuf.append(",");

			//Add multi-value attributes
			for (int i = 0; i < table.length; i++ ){
				if (i != 0) queryBuf.append(", ");
				queryBuf.append("?");
			}
			queryBuf.append(")");

		return queryBuf.toString();			
	}		
	
	
	public boolean addBatchObservationQueries(PreparedStatement opstmt, PreparedStatement npstmt,  HashMap <String, String> annMap){
		PreparedStatement pstmt = null; 
		String quantificationTypeStr = annMap.get("Quantification/@xsitype");  
		if (quantificationTypeStr == null) return false;
		
		String []  quantificationTypes =  quantificationTypeStr.split(DELIM);
		String [] singleValues = new String[loadingConfig.getTableMatrix("observationSingleTable").length];
		String [][] multiOrdinalValues = new String[loadingConfig.getTableMatrix("observationOrdinalMultiTable").length][quantificationTypes.length];
		String [][] multiNominalValues = new String[loadingConfig.getTableMatrix("observationNominalMultiTable").length][quantificationTypes.length];
		String [][] multiValues = null;  
		
		for (int i = 0; i < loadingConfig.getTableMatrix("observationSingleTable").length; i++ ){
			String key = loadingConfig.getTableMatrix("observationSingleTable")[i][0];
			singleValues[i] = annMap.get(key);
		}	
		
		
		for (int i = 0; i < loadingConfig.getTableMatrix("observationOrdinalMultiTable").length; i++ ){ 
			String [] table = loadingConfig.getTableMatrix("observationOrdinalMultiTable")[i];
			String key = table[0];
			String valueStr = annMap.get(key);
			if (valueStr == null) multiOrdinalValues[i] = null;
			else  multiOrdinalValues[i] = valueStr.split(DELIM);		
		}
		
		for (int i = 0; i < loadingConfig.getTableMatrix("observationNominalMultiTable").length; i++ ){ 
			String [] table = loadingConfig.getTableMatrix("observationNominalMultiTable")[i];
			String key = table[0];
			String valueStr = annMap.get(key);
			if (valueStr == null) multiNominalValues[i] = null;
			else  multiNominalValues[i] = valueStr.split(DELIM);		
		}		
		
		for (int count = 0; count < quantificationTypes.length; count ++){ //For each observation instance <Observation>, generate a new query
			String quantificationType = quantificationTypes[count];
			String [][] table = loadingConfig.getTableMatrix("observationOrdinalMultiTable"); 
			multiValues = multiOrdinalValues;

			if("Nominal".equals(quantificationType)){
				pstmt = npstmt;
				table = loadingConfig.getTableMatrix("observationNominalMultiTable"); 
				multiValues = multiNominalValues;
			} else 
				pstmt = opstmt;


			//Add single value attributes
			for (int i = 0; i < loadingConfig.getTableMatrix("observationSingleTable").length; i++ ){
				String [] map = loadingConfig.getTableMatrix("observationSingleTable")[i];
				String key = map[0];
				String value = annMap.get(key);
				try {
					pstmt.setString(i+1, value);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}				

			//Add multi-value attributes
			for (int i = 0; i < table.length; i++ ){	
				if(table[i][0].contains("partitionKey")) {
					String tileName = annMap.get("Region/@name");
					try {
						pstmt.setInt(i+1+loadingConfig.getTableMatrix("observationSingleTable").length, partitioningKeyGenerator.getPartitionKeyByTilename(tileName));
					} catch (SQLException e) {
						e.printStackTrace();
						return false;
					} 
				} else {
					String value = null;
					if (multiValues[i] != null)
						value = multiValues[i][count];
					try {
						pstmt.setString(i+1+loadingConfig.getTableMatrix("observationSingleTable").length, value);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				pstmt.addBatch();
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;			
	}		
	
	
	public String generatePreparedFlatCalculationSQL(HashMap <String, String> valueMap){
		String tableName = loadingConfig.getSchemaTableNames().get("featureTable");
		String [][] table = loadingConfig.getTableMatrix("flatCalculationTable");
		String nameKey ="Calculation/@name";
		String nameStr = valueMap.get(nameKey);
		//Note that calculation can be null, thus nameStr can be null
		String [] names = {"area"}; 
		
		if(nameStr != null) names = nameStr.split(DELIM);
		
		
		StringBuffer queryBuf = new StringBuffer("INSERT INTO " + tableName + "(");
		for (int i = 0; i < table.length; i++ ){
			String [] map = table[i];
			String attribute = map[1];
			if (i != 0) queryBuf.append(", ");
			queryBuf.append(attribute);			
		}
		
		for (int i = 0; i < names.length; i ++)
			queryBuf.append(", " + names[i]);	
		
		queryBuf.append(") VALUES(");
		for (int i = 0; i < table.length; i++ ){
			String value = "?";		
			if (i != 0) queryBuf.append(", ");
			queryBuf.append(value);
		}
		for (int i = 0; i < names.length; i ++)
			queryBuf.append(",?");	
		queryBuf.append(")");			
		return queryBuf.toString();
	}
	
	
	public boolean addBatchFlatCalculationQuery(PreparedStatement pstmt, HashMap <String, String> valueMap){
		
		String [][] table = loadingConfig.getTableMatrix("flatCalculationTable");
		String nameKey ="Calculation/@name";
		String valueKey ="CalculationResult/@value";
		String nameStr = valueMap.get(nameKey);
		if (nameStr == null) return true;
		
		String valueStr = valueMap.get(valueKey);
		String [] names = nameStr.split(DELIM);
		String [] values = valueStr.split(DELIM);
		if (names.length != values.length ) return false;
		
		for (int i = 0; i < table.length; i++ ){
			String [] map = table[i];
			String key = map[0];
			if(key.contains("partitionKey")) {
				String tileName = valueMap.get("Region/@name");
				try {
					pstmt.setInt(i+1, partitioningKeyGenerator.getPartitionKeyByTilename(tileName));
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
			} else {
				String value = valueMap.get(key);
				try {
					pstmt.setString(i+1, value);
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		for (int i = 0; i < values.length; i ++)
			try {
				String value = values[i];
				if (value.equals("NaN")) value = "0";
				pstmt.setString(i + table.length + 1, value );
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		try {
			pstmt.addBatch();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean  addBatchAnnotationQueries(PreparedStatement[] pstmts, HashMap <String, String> annMap){
		addBatchAnnotationMarkupQuery(pstmts[0], annMap);
		addBatchFlatCalculationQuery(pstmts[1], annMap); 
		addBatchObservationQueries(pstmts[2], pstmts[3], annMap); 
		return true;
	}		
	
	public List<String> generateDropTableStatements() {
		List<String> result = new ArrayList<String>();
		Map<String,String> tablesNames = loadingConfig.getSchemaTableNames();
		for(String tableName: tablesNames.values()) {
			if(!tableName.equals(""))
				result.add("DROP TABLE " + tableName);
		}
		
		return result;
	}
	
	
	
	public List<String> generateDropStagingdocQuery() {
		List<String> result = new ArrayList<String>();
		result.add("DELETE FROM PAIS.STAGINGDOC");
		
		return result;
	}
	
	public List<String> generateCreateTableStatements() {
		List<String> result = new ArrayList<String>();
		Map<String,String> tablesNames = loadingConfig.getSchemaTableNames();
		
		/*
		 * An entry in tablesNames maps a name for the table which is not important in this method
		 * to the real name of the table in the database. A table can have many definitions, this 
		 * means, the real table name in the database appears as a value for many keys. The
		 * final query contains the aggregation of all the columns in all definitions.
		 */
		Map<String, String> processedDefinitions = new HashMap<String, String>();
		for(Iterator<Map.Entry<String, String>> tablesNamesIterator = tablesNames.entrySet().iterator();tablesNamesIterator.hasNext();) {
			Map.Entry<String, String> oneDefinition = tablesNamesIterator.next();
			// Remove the definition to find all other definitions for the same table
			tablesNamesIterator.remove();
			// Process this definition
			if(!oneDefinition.getValue().equals("") && !processedDefinitions.containsKey(oneDefinition.getKey())) {
				// Find all other definitions for this table
				Map<String, String> allDefinitions = new HashMap<String, String>();
				allDefinitions.put(oneDefinition.getKey(), oneDefinition.getValue());
				// restOfTheDefinitionsIterator = tablesNamesIterator without the definition being processed
				for(Iterator<Map.Entry<String, String>> restOfTheDefinitionsIterator = tablesNames.entrySet().iterator();restOfTheDefinitionsIterator.hasNext();) {
					Map.Entry<String, String> table = restOfTheDefinitionsIterator.next();
					if(table.getValue().equals(oneDefinition.getValue())) {
						allDefinitions.put(table.getKey(), table.getValue());
						processedDefinitions.put(table.getKey(), table.getValue());
					}
				}
				String query = "";
				query += "CREATE TABLE " + oneDefinition.getValue() +"(";
				for(Iterator<Map.Entry<String, String>> allDefinitionsIterator = allDefinitions.entrySet().iterator();allDefinitionsIterator.hasNext();) {
					Map.Entry<String, String> definition = allDefinitionsIterator.next();
					// Process the columns
					String[][] columns = loadingConfig.getTableMatrix(definition.getKey());
					for(int i=0;i < columns.length;i++) {
						query += "\n\t" + columns[i][1] + "\t\t\t" + columns[i][2];
						if(i < (columns.length - 1) || allDefinitions.size() > 1)
							query += ",";
					}
					allDefinitionsIterator.remove();
				}
				query += "\n)";
				result.add(query);
			}
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		List<String> queries = QueryGenerator.getQueryGenerator("../PAISDataLoadingManager/conf/loadingconfig.xml").generateCreateTableStatements();
		for(String query: queries) {
			System.out.println(query);
		}
		//loader.parse(filename, partionSize, targetFolder);
	}

}
