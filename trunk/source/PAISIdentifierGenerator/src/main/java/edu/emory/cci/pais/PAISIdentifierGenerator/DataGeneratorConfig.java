package edu.emory.cci.pais.PAISIdentifierGenerator;
//TODO provenance@UID needed

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * Read database configuration from dataloadingconfig.xml file.
 * The configuration file defines common high level metadata, and naming conventions to derive additional metadata.
 * For example, Specimen/Image/Patient information may be derived from image filenames.
 * 
 * Apache commons-lang package needs to be included in the library 
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;


public class DataGeneratorConfig {
	private XMLConfiguration config = null;
	private Properties props = new Properties();
	
	private String algorithmKey = "algorithms.algorithm[@name]";
	
	private String [][] paisKeys = {{"pais[@version]", "paisVersion" }};
	
	private String [][] projectKeys = {{"project[@name]", "projectName"}, {"project[@uid]","projectUid"}, {"project[@uri]","projectUri"}}; 
	private String [][] studyKeys = {{"study[@name]", "studyName"}, {"study[@uid]", "studyUid"} };
	
	private String [][] userKeys = {{"user[@loginName]", "userLoginName"}, {"user[@name]", "userName"}, {"user[@uid]","userUid"} };
	private String [][] groupKeys = {{"group[@name]", "groupName"},{"group[@uid]", "groupUid"}, {"group[@uri]", "groupUri"}  };
	
	private String [][] patientKeys = {{"patient[@noid]", "patientNoid"}, {"patient[@idStartPosition]", "patientIdStartPosition"}, 
			 {"patient[@idEndPosition]", "patientIdEndPosition"}, {"patient[@ethnicGroup]", "patientEthnicGroup"}};	

	private String [][] imageKeys = {{"image[@type]", "imageType"}, {"image[@scanningResolution]", "imageScanningResolution"}, 
				{"image[@nameEndToken]", "imageNameEndToken"} };
	
	private String [][] equipmentKeys = {{"equipment[@manufacturer]", "equipmentManufacturer"}, 
			{"equipment[@manufacturerModelName]", "equipmentManufacturerModelName"},
			{"equipment[@softwareVersion]", "equipmentSoftwareVersion"} };	
	
	private String [][] anatomicEntityKeys = {{"anatomicEntity[@codeMeaning]", "anatomicEntityCodeMeaning"}, 
			{"anatomicEntity[@codeValue]", "anatomicEntityCodeValue"}, {"anatomicEntity[@codingSchemeDesignator]", "anatomicEntityCodingSchemeDesignator"}, 
			{"anatomicEntity[@codingSchemeVersion]", "anatomicEntityCodingSchemeVersion"} };	

	private String [][] specimenKeys = {{"specimen[@type]", "specimenType"}, {"specimen[@stain]", "specimenStain"}, 
			{"specimen[@nameStartToken]", "specimenNameStartToken"}, {"specimen[@nameEndToken]", "specimenNameEndToken"} };
	
	private String [][] regionKeys = {{"region[@coordinateStartToken]", "regionCoordinateStartToken"},
			{"region[@zoomResolution]", "regionZoomResolution"},
			{"region[@coordinateResolution]", "regionCoordinateResolution"},
			{"region[@coordinateEndToken]", "regionCoordinateEndToken"}, {"region[@width]", "regionWidth"}, 
			{"region[@height]", "regionHeight"}, {"region[@coordinateReference]", "regionCoordinateReference"},};
	
	private String [][] methodKeys = {{"method[@role]", "methodRole"},  {"method[@name]", "methodName"}, 
			{"method[@sequenceNumber]", "methodSequenceNumber"} };
	
	private String [][] markupKeys = {{"markup[@name]", "markupName"},  {"markup[@format]", "markupFormat"} };
	
	private String [][] calculationKeys = {{"calculation[@type]", "calculationType"}}; 
	
	private String [][] algorithmKeys = { {"[@name]", "algorithmName"},  {"[@uid]", "algorithmUid"}, {"[@uri]", "algorithmUri"}, 
			{"algorithm[@version]", "algorithmVersion"}, {"algorithm[@scope]", "algorithmScope"} };
	
	
	private String [][] parameterKeys = { {"[@name]", "parameterName"},  {"[@dataType]", "parameterDataType"}, {"[@value]", "parameterValue"} };
	
	private String[][] serverKeys = {{"server[@name]", "serverName"}, {"server[@capacity]", "serverCapacity"}, {"server[@hostname]", "serverHostname"}, {"server[@ipaddress]", "serverIpaddress"}, {"server[@port]", "serverPort"}};
	
	//private String parameterSeqKey = "parametersSequenceNumber";
	
	private HashMap<String, ArrayList<Properties> > parameterMap = new HashMap<String, ArrayList<Properties> >();
	
	//private HashMap<String, Properties> parameterMap = new HashMap<String, Properties>(1);
	private HashMap<String, Properties> algorithmMap = new HashMap<String, Properties>(1);

	public DataGeneratorConfig(File configFile) {
		if(!configFile.canRead()) {
			throw new RuntimeException("Cannot read the config file: '"+configFile.getAbsolutePath()+"'");
		}
		initConfigFile(configFile);
	}
	
	public DataGeneratorConfig(String configFileName) {
		this(new File(configFileName));
	}
	
	public DataGeneratorConfig() {}
	
	protected void initConfigFile(File configFile) {
		try {
			if ( configFile != null){
				config = new XMLConfiguration( configFile );
				setAllProperties();
				int algorithmCount = getAlgorithmCount();
				for (int i = 0; i < algorithmCount; i ++){ 
					String algorithmKey = "algorithms.algorithm(" + i +  ")[@name]";
					String algorithmName = config.getString(algorithmKey);
					parameterMap.put(algorithmName, getParameters(i));					
					algorithmMap.put( algorithmName, getAlgorithmValues(i) );
				}
			} else {
				throw new ConfigurationException("No file was specified to initialize the configuration.");
			}
		} catch(ConfigurationException cex) {
			cex.printStackTrace();
		}
	}
	
	private void setAllProperties(){
		setProperties(paisKeys);
		setProperties(projectKeys);
		setProperties(studyKeys);
		setProperties(userKeys);
		setProperties(groupKeys);
		setProperties(methodKeys);
		
		setProperties(patientKeys);		
		setProperties(imageKeys);
		setProperties(equipmentKeys); //Add equipment entity
		setProperties(anatomicEntityKeys);
		setProperties(specimenKeys);
		setProperties(regionKeys);
		setProperties(markupKeys);
		setProperties(calculationKeys);
		setProperties(serverKeys);
	}
	
	
	public HashMap<String, ArrayList <Properties> > getParameterMap(){
		return parameterMap;
	}
	
	
	public HashMap<String, Properties> getAlgorithmMap(){
		return algorithmMap;
	}
	
	private String generateParameterKeyByIndex(String name, int algorithmIndex){
		String key = "algorithms.algorithm(" + algorithmIndex +  ").parameters.parameter[@" + name + "]";
		return key;
	}
	
	private String[] generateParameterKeysByIndex(int algorithmIndex, int parameterIndex){
		String [] keys = new String[parameterKeys.length];
		for (int i = 0; i < parameterKeys.length; i++){
			keys[i] = "algorithms.algorithm(" + algorithmIndex +  ").parameters.parameter(" 
				+ parameterIndex +  ")" + parameterKeys[i][0];
			//System.out.println(keys[i]);
		}
		return keys;
	}
	
	private String[] generateAlgorithmKeysByIndex(int algorithmIndex){
		String [] keys = new String[algorithmKeys.length];
		for (int i = 0; i < algorithmKeys.length; i++){
			keys[i] = "algorithms.algorithm(" + algorithmIndex +  ")" + algorithmKeys[i][0];
		}
		//keys[4] = "algorithms.algorithm(" + algorithmIndex +  ").parameters[@sequenceNumber]" ;
		return keys;
	}
	
	
	private Properties getAlgorithmValues(int i){
		Properties algProp = new Properties();
		String[] keys = generateAlgorithmKeysByIndex(i);
		for (int j = 0; j < algorithmKeys.length; j++){
			String value = config.getString(keys[j]);
			algProp.put(algorithmKeys[j][1], value);
		}
/*		String seq = config.getString(keys[4]);
		algProp.put(parameterSeqKey, seq);*/
		return algProp;
	}
	

	private ArrayList <Properties> getParameters(int algIdx){
		ArrayList<Properties> parameterList= new ArrayList<Properties>();
		String nameKey = generateParameterKeyByIndex("name", algIdx) ;	
		int paramCount = config.getList(nameKey).size();
		for (int paramIdx = 0; paramIdx < paramCount; paramIdx++){
			Properties prop = new Properties();
			String keys[] = generateParameterKeysByIndex(algIdx, paramIdx);
			for (int keyIdx = 0; keyIdx < parameterKeys.length; keyIdx ++){
				String value = config.getString(keys[keyIdx]);
				prop.put(parameterKeys[keyIdx][1], value);
			}
			parameterList.add(prop);			 
		}
		return parameterList;
	}
	
	

	private int getAlgorithmCount(){
		return config.getList(algorithmKey).size();
	}

	
	private void setProperties(String[][] keys){
		for (int i = 0; i < keys.length; i++){
			String value = config.getString( keys[i][0]);
			//System.out.println( keys[i][0] + ":" + value);
			if(value != null){
				String key = keys[i][1];
				props.setProperty(key, value);
			}
		}
	}

	public Properties getProperties(){
		return props;
	}

	
	public static void printMap(HashMap <String, Properties> map){
		Set <String> keys = map.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) { 
			String key = (String) it.next();
			Properties value = map.get(key);
			value.list(System.out);
		}
	}
	
	public static void printListMap(HashMap<String, ArrayList<Properties> >  map){
		Set <String> keys = map.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) { 
			String key = (String) it.next();
			ArrayList<Properties> value = map.get(key);
			for (int i = 0; i < value.size(); i++){
				Properties prop = value.get(i);
				System.out.println(key);
				prop.list(System.out);
			}
		}
	}	


	public static void main(String[] args) {
		
		// Change this path based on working environment
		String file = "//Users//taewooko//Documents//workspace//paisdocumentgenerator//conf//docgenerator.xml";
		DataGeneratorConfig config = new DataGeneratorConfig( file );
		Properties allProp = config.getProperties();
		allProp.list(System.out);
		// config.printMap (algMap);		
		// config.printListMap(paramMap);
	}

}
