package edu.emory.cci.pais.documentgenerator;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import edu.emory.cci.pais.PAISIdentifierGenerator.DataGeneratorConfig;
import edu.emory.cci.pais.PAISIdentifierGenerator.IdentifierGenerator;
import edu.emory.cci.pais.PAISIdentifierGenerator.PAISIdentifierGenerator;
import edu.emory.cci.pais.model.*;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class initializes common parameters from configuration, and generates PAIS objects. 
 * Some instance level PAIS objects like ImageReferences need name of the tile/region.        
 */

public class ConfigInitializer {
	private static final double baseRes = 0.46; //20X resolution microns per pixel

	private String paisVersion = null; 
	private String markupName = null;  
	private String markupFormat = null; 
	private String coordinateReference = null; 
	
/*	private String studyName = null; 	
	private String studyUid = null;*/
	
	private String collectionRole = null; 
	private String collectionMethodName = null; 
	private String collectionSequenceNumber = null; 

	private String anatomicEntityCodeValue = null;
	private String anatomicEntityCodeMeaning = null;
	private String anatomicEntityCodingSchemeDesignator = null;
	private String anatomicEntityCodingSchemeVersion = null;
	
	// #equipment manufacturer = "Dummy manufacturer" manufacturerModelName= "Dummy Model" softwareVersion=" ver 1.0" 
	private String manufacturer = null;
	private String manufacturerModelName = null;
	private String softwareVersion = null;
	
	private Properties props;
	private HashMap<String, ArrayList<Properties> > parameterMap = null;
	private HashMap<String, Properties> algorithmMap = null;

	private static PAISIdentifierGenerator paisIdentifierGenerator = null; 

	public ConfigInitializer(DataGeneratorConfig config){
		props = config.getProperties();
		paisIdentifierGenerator = new PAISIdentifierGenerator(config);
		//props.list(System.out);
		parameterMap = config.getParameterMap();
		algorithmMap = config.getAlgorithmMap();
		initValues();
	}
	
	
	public Project getProject(){
		String projectName = props.getProperty("projectName");
		String projectUid = props.getProperty("projectUid");
		String projectUri = props.getProperty("projectUri");
		BigInteger projectId  = paisIdentifierGenerator.getProjectId(projectName);
		//new BigInteger( IdentifierGenerator.compactStringToNumber(projectUid) );
		ProjectGenerator projGen  = new ProjectGenerator(projectId, projectUid, projectName, projectUri); 
		return projGen.getProject();
	}
	
	
	public User getUser(){
		String userLoginName = props.getProperty("userLoginName");
		String userUid = props.getProperty("userUid");
		BigInteger userId = paisIdentifierGenerator.getUserId(userUid);
			//new BigInteger( IdentifierGenerator.compactStringToNumber(userUid) );
		String userName = props.getProperty("userName");
		UserGenerator userGen = new UserGenerator(userId, userLoginName, userName);
		return userGen.getUser();
	}
	
	public Group getGroup(){
		String groupName = props.getProperty("groupName");
		String groupUid = props.getProperty("groupUid");
		BigInteger groupId = paisIdentifierGenerator.getGroupId(groupUid);
		String groupUri = props.getProperty("groupUri");
		GroupGenerator groupGen = new GroupGenerator(groupId, groupUid, groupName, groupUri);
		return groupGen.getGroup();
	}
	
	
	public ArrayList <Provenance> getProvenances(){
		ArrayList <Provenance> provList = new ArrayList <Provenance>();
		Set <String> keys = algorithmMap.keySet();
		Set <String> paramKeys = parameterMap.keySet();
		Iterator<String> it = keys.iterator();
		Iterator<String> paramIt = paramKeys.iterator();
		
		while (it.hasNext()) { 
			paramIt.hasNext();
			String key = (String) it.next();
			String paramKey = (String) paramIt.next();
			Properties prop = algorithmMap.get(key);
			String algorithmName = prop.getProperty("algorithmName");
			String algorithmScope = prop.getProperty("algorithmScope");
			String paramSeqNum = prop.getProperty("parametersSequenceNumber");
			//String provenanceUid = paisIdentifierGenerator.getProvenanceUid(algorithmName, paramSeqNum);  //algorithmName + paramSeqNum;
			BigInteger provenanceId =  paisIdentifierGenerator.getProvenanceId(algorithmName, paramSeqNum); // IdentifierGenerator.stringToBigInteger(provenanceUid);			
			ProvenanceGenerator provGen = new ProvenanceGenerator(provenanceId, algorithmScope);
			ArrayList <Properties> params = parameterMap.get(paramKey); 
			ArrayList <Parameter> paramList = getParameters(paramKey, params );
			provGen.setParameters(paramList);
			Algorithm alg = getAlgorithm(algorithmName);
			provGen.setAlgorithm(alg);
			provList.add(provGen.getProvenance());
		}					
		return provList; 
	}
	
	
	public ArrayList<ImageReference> getImageReferences(String name){
		ArrayList<ImageReference> list = new ArrayList<ImageReference> ();
		list.add( getImageReference(name) );
		return list; 
	}
	
	private double formatResolution(double res){
		DecimalFormat df = new DecimalFormat("#.###");
		String resStr = df.format(res);
		System.out.print(df.format(res));
		return Double.parseDouble(resStr);
	}

	public ImageReference getImageReference(String name){
		String imageType = props.getProperty("imageType");
		if ("WholeSlideImageReference".equals(imageType) || imageType == null || imageType.equals("")){
			String uid = paisIdentifierGenerator.getImageReferenceUid(name);
			BigInteger id = paisIdentifierGenerator.getImageReferenceId(name);
			String fileRef = paisIdentifierGenerator.getImageFileReference(name);			
			double resolution = formatResolution ( getScanningResolution() );
			WholeSlideImageReferenceGenerator wsiGen = new WholeSlideImageReferenceGenerator(
					id, uid, fileRef, resolution);
			
			wsiGen.setRegion( getRegion(name) );
			wsiGen.setSpecimen( getSpecimen(name) );
			wsiGen.setAnatomicEntityCollection(getAnatomicEntityCollection());
			wsiGen.setSubject(getPatient(name) );
			wsiGen.setEquipment(null); //TODO To be added.			
			return wsiGen.getWholeSlideImageReference();
		}
		else if("TMAImageReference".equals(imageType)){ 
			String uid = paisIdentifierGenerator.getImageReferenceUid(name);
			BigInteger id = paisIdentifierGenerator.getImageReferenceId(name);
			String fileRef = paisIdentifierGenerator.getImageFileReference(name);			
			double resolution = formatResolution ( getScanningResolution() );
			
			TMAImageReferenceGenerator TMAGen = new TMAImageReferenceGenerator(
					id, uid, fileRef, resolution);
			TMAGen.setRegion( getRegion(name) );
			TMAGen.setSpecimen( getSpecimen(name) );
			TMAGen.setAnatomicEntityCollection(getAnatomicEntityCollection());
			TMAGen.setSubject(getPatient(name) );
			TMAGen.setEquipment(null); //TODO To be added.			
			return TMAGen.getTMAImageReference();
		}
			
		else return null;  
	}	
	
	
	private ArrayList <Parameter> getParameters(String algName, ArrayList <Properties> params){
		ArrayList <Parameter> paramList = new ArrayList <Parameter>();
		for (int paramIdx = 0; paramIdx < params.size(); paramIdx ++ ){
			Properties prop = params.get(paramIdx);
			String parameterName = prop.getProperty("parameterName");
			String parameterValue = prop.getProperty("parameterValue");
			String parameterDataType = prop.getProperty("parameterDataType");
			String paramUid = algName + parameterName; 
			BigInteger paramId = IdentifierGenerator.stringToBigInteger(parameterName);
			ParameterGenerator paramGen = new ParameterGenerator(paramId,  parameterName, parameterValue, parameterDataType);
			paramList.add(paramGen.getParameter());
		}
		return paramList; 
	}
	

	private Algorithm getAlgorithm(String algName){
		Properties prop = algorithmMap.get(algName);
		String version = prop.getProperty("algorithmVersion");
		String uri = prop.getProperty("algorithmUri");
		//String algUid = prop.getProperty("algorithmUid"); //Not used in PAIS V1.0
		BigInteger id = paisIdentifierGenerator.getAlgorithmId(algName); 
		AlgorithmGenerator algGen = new AlgorithmGenerator(id,  algName, version, uri);
		return algGen.getAlgorithm();
	}
	
	public ArrayList<Collection> getCollections(String name){
		ArrayList <Collection> list = new ArrayList<Collection>();
		list.add(getCollection(name));
		return list; 
	}
	
	
	private Collection getCollection(String name){
		BigInteger id = paisIdentifierGenerator.getCollectionId(name);
		String uid = paisIdentifierGenerator.getCollectionUid(name);
		String colName = paisIdentifierGenerator.getCollectionName(name);
		CollectionGenerator colGen = new CollectionGenerator(id, uid, colName, collectionRole, collectionMethodName, 
				collectionSequenceNumber);
		return colGen.getCollection();
	}

	private ArrayList<AnatomicEntity> getAnatomicEntityCollection(){
		ArrayList <AnatomicEntity> list = new ArrayList<AnatomicEntity>();
		list.add(getAnatomicEntity());
		return list; 
	}
	
	private AnatomicEntity getAnatomicEntity(){
		BigInteger id = new BigInteger( IdentifierGenerator.compactStringToNumber(anatomicEntityCodeValue) );
		AnatomicEntityGenerator aeGen = new AnatomicEntityGenerator( 
			id, anatomicEntityCodeValue, anatomicEntityCodeMeaning, anatomicEntityCodingSchemeDesignator, anatomicEntityCodingSchemeVersion);			
		return aeGen.getAnatomicEntity();
	}
	
	// #equipment 
	private Equipment getEquipment(){
		// random equipmentID 
		Random r = new Random();
		long num = r.nextLong();
		BigInteger equipId = BigInteger.valueOf(num);
		EquipmentGenerator equip = new EquipmentGenerator(equipId, manufacturer, manufacturerModelName, softwareVersion);
		return equip.getEquipment();
		
	}
	
	private Specimen getSpecimen(String name){
		BigInteger id = paisIdentifierGenerator.getSpecimenId(name);
		String uid =  paisIdentifierGenerator.getSpecimenUid(name);
		String type = props.getProperty("specimenType");
		String stain = props.getProperty("specimenStain");
		SpecimenGenerator gen =  new SpecimenGenerator(id, uid, type, stain);
		return gen.getSpecimen();
	}
	

	private Patient getPatient(String name){
		BigInteger id = paisIdentifierGenerator.getSubjectId(name);
		String patientID =  paisIdentifierGenerator.getPatientId(name);
		PatientGenerator gen =  new PatientGenerator(id, patientID);
		return gen.getPatient();
	}

	private double getRegionHeight(){
		String heightStr = props.getProperty("regionHeight");
		double height = 0;
		if ( heightStr != null &&  (! "".equals(heightStr) ) ) 
			height = Double.parseDouble( heightStr);
		return height;
	}
	
	private double getRegionwidth(){
		String widthStr = props.getProperty("regionwidth");
		double width = 0;
		if ( widthStr != null &&  (! "".equals(widthStr) ) ) 
			width = Double.parseDouble( widthStr);
		return width;
	}
	
	/**
	 * 
	 * @return absolute resolution in microns per pixel 
	 */
	private double getScanningResolution(){
		String resStr = props.getProperty("imageScanningResolution");
		String resStrNum = resStr.substring(0, resStr.length() - 1);
		int resNum = Integer.parseInt(resStrNum);
		double res = baseRes*20.0/resNum;
		return res;
	}
	
	private double getZoomResolution(){
		String resStr = props.getProperty("imageScanningResolution");
		String resStrNum = resStr.substring(0, resStr.length() - 1);
		int resNum = Integer.parseInt(resStrNum);
		
		String magResStr = props.getProperty("imageZoomResolution");  
		String magResStrNum = magResStr.substring(0, magResStr.length() - 1);
		int magResNum = Integer.parseInt(magResStrNum);
		
		return magResNum*1.0/resNum;	
	}
	
	private double getCoordinateResolution(){
		String resStr = props.getProperty("imageScanningResolution");
		String resStrNum = resStr.substring(0, resStr.length() - 1);
		int resNum = Integer.parseInt(resStrNum);
		
		String refResStr = props.getProperty("imageCoordinateResolution");
		String refResStrNum = refResStr.substring(0, refResStr.length() - 1);
		int refResNum = Integer.parseInt(refResStrNum);

		return refResNum*1.0/resNum;	
	}	
	
	private Region getRegion(String name){
		BigInteger id = paisIdentifierGenerator.getRegionId(name);
		String regName = paisIdentifierGenerator.getRegionName(name);
		String coords = paisIdentifierGenerator.getRegionCoordinates(name);
		double x = paisIdentifierGenerator.getRegionXCoordinate(coords);
		double y = paisIdentifierGenerator.getRegionYCoordinate(coords);

		double width = getRegionwidth();
		double height = getRegionHeight();

		double coordinateResolution = getCoordinateResolution();
		double zoomResolution = getZoomResolution();

		RegionGenerator gen = new RegionGenerator(id, regName, x, y, height, width, 
				zoomResolution, coordinateResolution, coordinateReference);
		return gen.getRegion(); 
	}	
	
	public PAISRoot getPAISRoot(String name){
		String uid 	= paisIdentifierGenerator.getPAISUid(name);
		BigInteger id = paisIdentifierGenerator.getPAISId(name);
		return new PAISRoot(id, uid, paisVersion);
	}
	
	private void initValues(){
		paisVersion = 			props.getProperty("paisVersion");
		markupName = 			props.getProperty("markupName");
		markupFormat = 			props.getProperty("markupFormat");
		coordinateReference = 	props.getProperty("regionCoordinateReference");
		collectionRole = 		props.getProperty("collectionRole"); 
		collectionMethodName = 	props.getProperty("collectionMethodName"); 
		collectionSequenceNumber = props.getProperty("collectionSequenceNumber"); 
				
		anatomicEntityCodeValue = props.getProperty("anatomicEntityCodeValue");
		anatomicEntityCodeMeaning = props.getProperty("anatomicEntityCodeMeaning");
		anatomicEntityCodingSchemeDesignator = props.getProperty("anatomicEntityCodingSchemeDesignator");
		anatomicEntityCodingSchemeVersion = props.getProperty("anatomicEntityCodingSchemeVersion");
		
		// add equipmenet
		manufacturer = props.getProperty(manufacturer);
		manufacturerModelName = props.getProperty(manufacturerModelName);
		softwareVersion = props.getProperty(softwareVersion);
		
	}
	
	public static void main(String[] args) {
	}

}
