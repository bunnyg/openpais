package edu.emory.cci.pais.PAISIdentifierGenerator;
import java.math.BigInteger;
import java.util.Properties;



public class PAISIdentifierGenerator {
	private static char COORDINATE_SEPERATOR ='-';
	private static BigInteger DECIMAL_LIMIT = new BigInteger("100000000000000000000000000000"); //e30
	private Properties props = null;
	private static String scanningResolution = null; 
	private static String zoomResolution = null; 
	private static String methodSeqNo = null;
	private static String methodName = null;
	private String studyName = null; 	
	private String studyUid = null;
	
	private static int markupPrefix = 1000000;
	private static int geometricShapePrefix = 2000000;
	private static int annotationPrefix = 3000000;
	private static int calculationPrefix = 100; //4000000;
	private static int subjectPrefix = 100000;
	
	String [] redundantUids = {"TCGA-"};
	
	//private volatile static PAISIdentifierGenerator paisIdentifierGenerator;
	
	public PAISIdentifierGenerator (DataGeneratorConfig conf){
		try {
			props = conf.getProperties();
			initValues();
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("The configuration could not be initialized.", e);
		}
	}
	
	public String getPAISVersion() {
		return props.getProperty("paisVersion", "1.0");
	}
	
	public String getPAISUid(String name){
		StringBuffer uid = new StringBuffer();
		if (studyUid == null || "".equals(studyUid)  )
			uid.append( getSpecimenUid(name) + "_" + scanningResolution + "_" + zoomResolution + "_" +  methodName + "_" +  methodSeqNo );
		else 
			uid.append( getSpecimenUid(name) + "_" + scanningResolution + "_" + zoomResolution + "_" + studyUid +  "_" +  methodName + "_" +  methodSeqNo);
		return uid.toString();
	}
	
	public BigInteger normalizeBig(BigInteger big){
		return big.remainder(DECIMAL_LIMIT);
	}
	
	public BigInteger getPAISId(String name){
		String paisUid = getPAISUid(name);
		paisUid = filterUid(paisUid);
		BigInteger id = new BigInteger( IdentifierGenerator.compactStringToNumber(paisUid) );
		return normalizeBig(id);		
	}
	
	private String filterUid(String uid){
		for (String str:redundantUids)
			uid = uid.replaceFirst(str, "");
		return uid;
	}
	
	public String getImageFileReference(String name){
		String nameEndToken = props.getProperty("imageNameEndToken", "");
		int pos = name.indexOf(nameEndToken);
		return name.substring(0, pos + nameEndToken.length() );		
	}
	
	public String getImageReferenceUid(String name){
		String specimenUid = getSpecimenUid(name);
		String scanningRes = props.getProperty("imageScanningResolution", "");
		return specimenUid + "_" + scanningRes;
	}
	
	public BigInteger getImageReferenceId(String name){
		String specimenUid = getSpecimenUid(name);
		String scanningRes = props.getProperty("imageScanningResolution", "");
		String resNum = scanningRes.substring(0,2);
		String id = IdentifierGenerator.compactStringToNumber(filterUid(specimenUid) )  + resNum;
		return normalizeBig(new BigInteger(id) );
	}
	
	public String getImageType() {
		return props.getProperty("imageType", "");
	}
	
	public double getImageScanningResolution() {
		String result = props.getProperty("imageScanningResolution", "");
		result = result.replaceAll("[a-zA-Z]", "");
		return Double.parseDouble(result);
	}
	
	// Attributes for Collection
	
	public String getMethodName() {
		return props.getProperty("methodName", "");
	}
	
	public String getMethodRole() {
		return props.getProperty("methodRole", "");
	}
	
	public String getMethodSequenceNumber() {
		return props.getProperty("methodSequenceNumber", "");
	}
	
	public String getSpecimenUid(String name){
		try {
			String nameStartToken = null; 
			//if ( props.containsKey("specimenNameStartToken") ) 
			nameStartToken = props.getProperty("specimenNameStartToken");
			int startTokenLength = 0;
			int startPosition = 0;
			if (nameStartToken != null){
				if (! "".equals(nameStartToken) )
					startPosition = name.indexOf(nameStartToken);
				startTokenLength = nameStartToken.length();
			}
			String nameEndToken = props.getProperty("specimenNameEndToken");
			int endPosition = name.indexOf(nameEndToken);
			return  name.substring(startPosition + startTokenLength, endPosition);
		} catch(Exception e) {
			throw new SpecimenNotFoundException("Failed to parse the Specimen UID from the file name. Please make sure that the nameEndToken attribute of the <specimen> element in the configuration is set correctly.", e);
		}	
	}
	
	public BigInteger getSpecimenId(String name){
		String specimenUid = getSpecimenUid(name);
		String id = IdentifierGenerator.compactStringToNumber(filterUid(specimenUid ) );
		return new BigInteger(id);
	}
	
	public String getSpecimenStain() {
		return props.getProperty("specimenStain", "");
	}
	
	public String getSpecimenType() {
		return props.getProperty("specimenType", "");
	}

	// Attributes for Anatomic Entity
	
	public BigInteger getAnatomicEntityId() {
		if(getAnatomicEntityCodeValue().equals(""))
			return new BigInteger("1");
		return new BigInteger(IdentifierGenerator.compactStringToNumber(getAnatomicEntityCodeValue()));
	}
	
	public String getAnatomicEntityCodingSchemeDesignator() {
		return props.getProperty("anatomicEntityCodingSchemeDesignator", "");
	}
	
	public String getAnatomicEntityCodeValue() {
		return props.getProperty("anatomicEntityCodeValue", "");
	}
	
	public String getAnatomicEntityCodeMeaning() {
		return props.getProperty("anatomicEntityCodeMeaning", "");
	}
	
	public String getAnatomicEntityCodingSchemeVersion() {
		return props.getProperty("anatomicEntityCodingSchemeVersion", "");
	}
	
	public String getRegionCoordinates(String name){
		String start = props.getProperty("regionCoordinateStartToken");
		int startIdx = name.indexOf(start);
		String end = props.getProperty("regionCoordinateEndToken");
		int endIdx = name.indexOf(end);
		String coords = name.substring(startIdx + start.length(), endIdx );
		return coords;	
	}
	
	public String getRegionName(String name) {
		String specimenUid = null;
		try {
			specimenUid = getSpecimenUid(name);
		} catch(SpecimenNotFoundException e) {
			throw new UnableToGetRegionNameException("Unable to get the name of the region. Failed to get the name while trying to get the SpecimenUid.", e);
		}
		String regionCoordinates = null;
		try {
			regionCoordinates = getRegionCoordinates(name);
		} catch(Exception e) {
			throw new UnableToGetRegionNameException("Unable to get the name of the region. Failed to get the name while trying to get the region coordinates.", e);
		}
		return specimenUid + "-" + regionCoordinates; 
	}
	
	public int getRegionXCoordinate(String coords) {
		int sepIdx = coords.indexOf(COORDINATE_SEPERATOR);
		String xCoord = coords.substring(0, sepIdx);
		return Integer.parseInt(xCoord);
	}
	
	public int getRegionYCoordinate(String coords){
		int sepIdx = coords.indexOf(COORDINATE_SEPERATOR);
		String yCoord = coords.substring(sepIdx + 1);
		return Integer.parseInt(yCoord);
	}
	
	public int getRegionWidth() {
		return Integer.parseInt(props.getProperty("regionWidth"));
	}
	
	public int getRegionHeight() {
		return Integer.parseInt(props.getProperty("regionHeight"));
	}
	
	public String getZoomResolution() {
		return props.getProperty("regionZoomResolution", "");
	}
	
	public String getCoordinateResolution() {
		return props.getProperty("regionCoordinateResolution", "");
	}
	
	public String getCoordinateReference() {
		return props.getProperty("regionCoordinateReference", "");
	}
	
	public int getRegionSequenceNumber(String name){
		String width = props.getProperty("regionWidth");
		if (width == null) return 1;
		if ("".equals(width) ) return 1;
		String height = props.getProperty("regionHeight");
		String coords = getRegionCoordinates(name);
		int x = getRegionXCoordinate(coords);
		int y = getRegionYCoordinate(coords) ;
		int widthSeq = x/Integer.parseInt(width) + 10;
		int heightSeq = y/Integer.parseInt(height) + 10;
		return Integer.parseInt(widthSeq + "" + heightSeq);
	}
	
	public BigInteger getRegionId(String name){
		return BigInteger.valueOf( getRegionSequenceNumber(name) );
	}
	
	public String getCollectionUid(String name){
		if (studyName == null || "".equals(studyName) )
			return getSpecimenUid(name);
		else 
			return studyUid + "_" +  getSpecimenUid(name);
	}
	
	public BigInteger  getCollectionId(String name){
		return getSpecimenId(name);
	}
	
	public String getCollectionName(String name){
		if (studyName == null || "".equals(studyName) )
			return getSpecimenUid(name);
		else 
			return studyName + " " +  getSpecimenUid(name);
	}
	
	public String getPatientId(String name){
		String noId = props.getProperty("patientNoid");
		if ( noId == null || "".equals(noId) || "true".equals(noId) ){
			return getSpecimenUid(name);
		}
		int startIdx  = Integer.parseInt( props.getProperty("patientIdStartPosition") );		
		int endIdx = Integer.parseInt(  props.getProperty("patientIdEndPosition") );
		return name.substring(startIdx - 1,  endIdx - 1);
	}
	
	public String getPatientEthnicGroup() {
		return props.getProperty("patientEthnicGroup");
	}
	
	public String getEquipmentManufacturer() {
		return props.getProperty("equipmentManufacturer");
	}
	
	public BigInteger getSubjectId(String name){
		String patientId = getPatientId(name);
		if ( IdentifierGenerator.isNumber(patientId) ){
			int pid = Integer.parseInt(patientId);
			pid += subjectPrefix;
			return BigInteger.valueOf(pid);
		}			 
		return new BigInteger (IdentifierGenerator.compactStringToNumber(patientId) );
	}
	
	public BigInteger getProjectId(String projectName){
		String name = filterUid(projectName);
		BigInteger id = new BigInteger( IdentifierGenerator.compactStringToNumber(name) );
		return normalizeBig(id);	 
	}
	
	public String getStudyName() {
		return props.getProperty("studyName", "");
	}
	
	public String getProjectName() {
		return props.getProperty("projectName", "");
	}
	
	public String getProjectUid() {
		return props.getProperty("projectUid", "");
	}
	
	public BigInteger getUserId(String userName){
		String name = filterUid(userName);
		BigInteger id = new BigInteger( IdentifierGenerator.compactStringToNumber(name) );
		return normalizeBig(id);	 
	}
	
	public String getUserName() {
		return props.getProperty("userName", "");
	}
	
	public String getProjectUri() {
		return props.getProperty("projectUri", "");
	}
	
	public String getUserUid() {
		return props.getProperty("userUid", "");
	}
	
	public String getUserLoginName() {
		return props.getProperty("userLoginName", "");
	}
	
	public BigInteger getGroupId(String groupName){
		String name = filterUid(groupName);
		BigInteger id = new BigInteger( IdentifierGenerator.compactStringToNumber(name) );
		return normalizeBig(id);	 
	}
	
	public String getGroupName() {
		return props.getProperty("groupName", "");
	}
	
	public String getGroupUid() {
		return props.getProperty("groupUid", "");
	}
	
	public String getGroupUri() {
		return props.getProperty("groupUri", "");
	}
	
	public BigInteger getMarkupId(String name, int seqNo){
		String mid = getRegionSequenceNumber(name) + "" + (markupPrefix + seqNo);
		return new BigInteger(mid);
	}
	
	public String getMarkupUid(String name, int seqNo){
		return getRegionSequenceNumber(name) + "" + (markupPrefix + seqNo);		
	}
	
	public String getMarkupName() {
		return props.getProperty("markupName", "");
	}
	
	public String getMarkupFormat() {
		return props.getProperty("markupFormat", "");
	}
	
	public String getCalculationType() {
		return props.getProperty("calculationType", "");
	}
	
	public String getAnnotationUid(String name, int seqNo){
		return getRegionSequenceNumber(name) + "" + (annotationPrefix + seqNo);		
	}	

	public BigInteger getAnnotationId(String name, int seqNo){
		String aid = getRegionSequenceNumber(name) + "" + (annotationPrefix + seqNo);
		return new BigInteger(aid);
	}
	
	public BigInteger getgeometricShapeId(String name, int seqNo){
		String gid = getRegionSequenceNumber(name) + "" + (geometricShapePrefix + seqNo);
		return new BigInteger(gid);
	}
	
	public BigInteger getCalculationId(String name, int seqNo, int calSeqNo){
		String gid = getRegionSequenceNumber(name) + "" + (annotationPrefix + seqNo) + "" + (calculationPrefix + calSeqNo);
		return new BigInteger(gid);
	}	
	
	public BigInteger getAlgorithmId(String algName){
		return new BigInteger( IdentifierGenerator.compactStringToNumber(algName) );
	}
	
	public String getProvenanceUid(String algorithmName, String paramSeqNum){
		if (studyUid == null || "".equals(studyUid)  )
			return algorithmName + paramSeqNum; 
		else return studyUid + algorithmName + paramSeqNum;
	}
	
	public BigInteger getProvenanceId(String algorithmName, String paramSeqNum){
		StringBuffer strb = new StringBuffer();
		if (studyUid == null || "".equals(studyUid)  )
			strb.append( algorithmName + paramSeqNum ); 
		else 
			strb.append(studyUid + algorithmName + paramSeqNum);
		return new BigInteger( IdentifierGenerator.compactStringToNumber(strb.toString()) );
	}
	
	public String getPAISDocComment(String name){
		return null;
	}
	
	public String getServerName() {
		return props.getProperty("serverName");
	}
	
	public String getServerCapacity() {
		return props.getProperty("serverCapacity");
	}
	
	public String getServerHostname() {
		return props.getProperty("serverHostname");
	}
	
	public String getServerIpaddress() {
		return props.getProperty("serverIpaddress");
	}
	
	public String getServerPort() {
		return props.getProperty("serverPort");
	}
	
	public void initValues(){
		studyName = 		props.getProperty("studyName");
		studyUid = 			props.getProperty("studyUid");
		scanningResolution = props.getProperty("imageScanningResolution");
		zoomResolution = props.getProperty("regionZoomResolution");
		methodName = props.getProperty("methodName");
		methodSeqNo = props.getProperty("methodSequenceNumber");
	}
	
	
	public static void main(String[] args) {
		String tileName ="TCGA-02-0001-01Z-00-DX1.svs-0000016384-0000008192.ppm.grid4.mat.grid4.xml";
		String confFile = "Z:\\Projects\\Workspace\\PAIS\\DocumentGenerator\\conf\\docgenerator.xml";
		DataGeneratorConfig config = new DataGeneratorConfig( confFile );
		
		PAISIdentifierGenerator gen = new PAISIdentifierGenerator(config);
		
/*		int result = gen.getRegionSequenceNumber(tileName);
		System.out.println(result);
		
		String slideName = gen.getSpecimenUid(tileName);
		System.out.println(slideName);
		
		String regionName = gen.getRegionName(tileName);
		System.out.println(regionName);
		
		String paisUid = gen.getPAISUid(tileName);
		System.out.println(paisUid);
		
		String imageFileReference = gen.getImageFileReference(tileName);
		System.out.println(imageFileReference);
		
		String imageReferenceUid = gen.getImageReferenceUid(tileName);
		System.out.println(imageReferenceUid);
		
		String imageReferenceId = gen.getImageReferenceId(tileName);
		System.out.println(imageReferenceId);
		
		BigInteger mid = gen.getMarkupId(tileName, 10000);
		System.out.println(mid);
		
		String patientId = gen.getPatientId(tileName);
		System.out.println(patientId);

		BigInteger subjectId = gen.getSubjectId(tileName);
		System.out.println(subjectId);

*/
/*		BigInteger aid = gen.getAnnotationId(tileName, 10000);
		System.out.println(aid);

		
		BigInteger cid = gen.getCalculationId(tileName, 10000, 22);
		System.out.println(cid);*/
		
/*		BigInteger big = new BigInteger("1234567890");  
		if (big.doubleValue() > 10e30 )
			System.out.println( big.bitLength() );  */

		BigInteger id = gen.getPAISId(tileName);
		
		System.out.println(id+ "\n" +  gen.normalizeBig(id)  );  
		
		int a = 10%100;
		System.out.println(a);
		
/*		String uid2 = "TCGA-02-0001-01Z-00-DX1_40X_20X_AVP_2";
		BigInteger id2 = gen.getPAISId(uid2);
		
		System.out.println( gen.normalizeBig(id2)  );  		*/
		
	}
	
}