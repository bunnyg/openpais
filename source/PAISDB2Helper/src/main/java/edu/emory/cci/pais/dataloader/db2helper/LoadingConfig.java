package edu.emory.cci.pais.dataloader.db2helper;


/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * Read database configuration
 * Apache commons-lang package needs to be included in the library 
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;


public class LoadingConfig {
	private String tableNameKey = "tables.table[@name]";
	
	private XMLConfiguration config = null;
	private boolean emptyFile = false;

	
	private HashMap<String, ArrayList<MappingRecord> > map = new HashMap<String, ArrayList<MappingRecord> >();
	
	private Map<String, String> schemaTableNames = new HashMap<String, String>();
	public Map<String, String> getSchemaTableNames() {
		return schemaTableNames;
	}

	public LoadingConfig(File configFile) {
		try
		{
			if ( configFile != null){
				AbstractConfiguration.setDefaultListDelimiter('\0');
				config = new XMLConfiguration( configFile );
				int tabCount = getTableCount();
				for (int i = 0; i < tabCount; i ++){ 
					String tableKey = "tables.table(" + i +  ")[@name]";
					String schemaTableNameAttribute = "tables.table(" + i +  ")[@tableName]";
					String localTableName = config.getString(tableKey);
					String schemaTableName = config.getString(schemaTableNameAttribute);
					map.put(localTableName, getValues(i));
					schemaTableNames.put(localTableName, schemaTableName);
				}
			}
			else emptyFile = true;
		}
		catch(ConfigurationException cex)
		{ cex.printStackTrace(); }
	}
	
	public LoadingConfig(String configFileName) {
		this(new File(configFileName));
	}
	
	
	public String[][] getTableMatrix(String tableName ){
		//TODO Not working yet!
		ArrayList <MappingRecord> tableArray = map.get(tableName);
		int numOfRows = tableArray.size();
		//System.out.println(numOfRows);
		String[][] matrix = new String [numOfRows][3];
		for (int i = 0; i < numOfRows; i ++){
			matrix[i][0] = tableArray.get(i).path; 
			//System.out.println(matrix[i][0]);
			matrix[i][1] = tableArray.get(i).name;
			matrix[i][2] = tableArray.get(i).type;
		}
		return matrix;
	}
	
	
	public HashMap<String, ArrayList <MappingRecord> > getMap(){
		return map;
	}
	
	
	private String generateKey(String name, int tableIndex){
		return "tables.table(" + tableIndex +  ").attribute[@" + name + "]";
	}
	
	private ArrayList <MappingRecord> getValues(int i){
		ArrayList<MappingRecord> recordList= new ArrayList<MappingRecord>();
		String nameKey = generateKey("name", i) ;
		
		String pathKey = generateKey("path", i) ;
		String typeKey = generateKey("type", i) ;
		ArrayList <String> nameList =  (ArrayList<String>)  config.getList(nameKey); 
		ArrayList <String> pathList =  (ArrayList<String>)  config.getList(pathKey); 
		ArrayList <String>  typeList = (ArrayList<String>)  config.getList(typeKey); 
		
		for (int k = 0; k < nameList.size(); k++){
			if (! pathList.isEmpty() ) 
				recordList.add(  new MappingRecord(nameList.get(k), pathList.get(k), typeList.get(k) ) );
			else 
				recordList.add(  new MappingRecord(nameList.get(k), null, typeList.get(k) ) );
		}
		 return recordList;
	}
	
	
	private int getTableCount(){
		int count = 0; 
		//return config.getList("tests.test").size();
		Object prop = config.getProperty(tableNameKey);
		if(prop instanceof Collection)
			count = ((Collection) prop).size();
		return count;
	}

	public boolean isEmptyConfigFile(){
		return emptyFile;
	}

	
	public static class MappingRecord{
		public String name;
		public String path;
		public String type;
		public  MappingRecord(String iName, String iPath, String iType){
			name = iName;
			path = iPath;
			type = iType;
		}
	};

	public static void main(String[] args) {
		//String file = "c:\\temp\\dbconfig.xml";
		String file = "conf/loadingconfig.xml";
		//LoadingConfig config = new LoadingConfig(new File(file) );
		LoadingConfig config = new LoadingConfig( file );
/*		HashMap<String, ArrayList<MappingRecord> > tile_partition_map = config.getMap();
		for (int i = 0; i < tile_partition_map.size(); i++){
			System.out.println( tile_partition_map.toString() );
		}*/
		
		String [][] result = config.getTableMatrix("featureTable");
		for (int i = 0; i < result.length; i++){
			System.out.println(result[i][0] + ", " + result[i][1] + ", " + result[i][2] );
		}
	}

}
