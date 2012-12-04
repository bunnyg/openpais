package edu.emory.cci.pais.documentgenerator;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import edu.emory.cci.pais.PAISIdentifierGenerator.DataGeneratorConfig;
import edu.emory.cci.pais.model.*;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class provides APIs to build the PAIS XML object. 
 */
public class PAISXMLGenerator {
	private PAIS pais = new PAIS();
	
	
	public PAISXMLGenerator(){		
	}
		
	public PAISXMLGenerator(PAISRoot root, Project project, ArrayList<Collection> collection, Group group, User user, 
			ArrayList<ImageReference> imageReferenceList, ArrayList<Provenance> provenanceList){
		setRoot(root);
		setProject(project);
		setCollections(collection);
		setGroup(group);
		setUser(user);	
		setImageReferenceCollection(imageReferenceList);
		setProvenanceCollection(provenanceList);
	}
	
	public void setRoot(PAISRoot root){
		pais.setId(root.id);
		pais.setName(root.name);
		pais.setUid(root.uid);
		pais.setDateTime(root.dateTime);
		pais.setPaisVersion(root.paisVersion);
		pais.setCodeValue(root.codeValue);
		pais.setCodeMeaning(root.codeMeaning);
		pais.setCodingSchemeDesignator(root.codingSchemeDesignator);
		pais.setCodingSchemeVersion(root.codingSchemeVersion);
		pais.setComment(root.comment);
	}
	
	public void setProject(Project project){
		PAIS.Project pProject= new PAIS.Project();
		pProject.setProject(project);
		pais.setProject(pProject);
	}
	
	public void setCollections(ArrayList<Collection> collection  ){
		PAIS.Collections collections = new PAIS.Collections();
		ArrayList<Collection> collectionList = (ArrayList<Collection>) collections.getCollection();
		collectionList.addAll(collection);		
		pais.setCollections(collections);
	}
	
	public void setUser(User user){
		PAIS.User pUser = new PAIS.User();
		pUser.setUser(user);	
		pais.setUser(pUser);
	}
	
	public void setGroup(Group group){
		PAIS.Group pGroup = new PAIS.Group();
		pGroup.setGroup(group);
		pais.setGroup(pGroup);
	}
	
	public void setImageReferenceCollection(ArrayList<ImageReference> imageReferenceList){
		PAIS.ImageReferenceCollection imageReferenceCollection = new PAIS.ImageReferenceCollection();
		ArrayList<ImageReference> pImageReferenceList = (ArrayList<ImageReference>) imageReferenceCollection.getImageReference();
		pImageReferenceList.addAll( imageReferenceList);		
		pais.setImageReferenceCollection(imageReferenceCollection);
	}
	
	public void setMarkupCollection(ArrayList<Markup> markupList){
		PAIS.MarkupCollection markupCollection = new PAIS.MarkupCollection();
		ArrayList<Markup> pMarkupList = (ArrayList<Markup>) markupCollection.getMarkup();
		pMarkupList.addAll( markupList);		
		pais.setMarkupCollection(markupCollection);
	}
	

	public void setAnnotationCollection(ArrayList<Annotation> AnnotationList){
		PAIS.AnnotationCollection AnnotationCollection = new PAIS.AnnotationCollection();
		ArrayList<Annotation> pAnnotationList = (ArrayList<Annotation>) AnnotationCollection.getAnnotation();
		pAnnotationList.addAll( AnnotationList);		
		pais.setAnnotationCollection(AnnotationCollection);
	}	
	
	public void setProvenanceCollection(ArrayList<Provenance> provenanceList){
		PAIS.ProvenanceCollection ProvenanceCollection = new PAIS.ProvenanceCollection();
		ArrayList<Provenance> pProvenanceList = (ArrayList<Provenance>) ProvenanceCollection.getProvenance();
		pProvenanceList.addAll( provenanceList);		
		pais.setProvenanceCollection(ProvenanceCollection);
	}	

	public void toXML(String fileName){
        XMLMarshaller xml = new XMLMarshaller();
        String outputFile =  fileName;        
        xml.generateXML(PAIS.class, "PAIS", outputFile, pais);
	}
	
	public void toXMLZip(String fileName){
        XMLMarshaller xml = new XMLMarshaller();
        String outputFile =  fileName;        
        xml.generateXMLZip(PAIS.class, "PAIS", outputFile, pais);
	}
	
	public String getShortFileName(String fileName){
		File file = new File(fileName);
		String shortName = file.getName();
		return shortName;
	}	
	
	public static void main(String[] args) {
		PAISXMLGenerator gen = new PAISXMLGenerator();
		String file = "Z:\\Projects\\Workspace\\PAIS\\DocumentGenerator\\conf\\docgenerator.xml";
		DataGeneratorConfig config = new DataGeneratorConfig( file );
/*		Properties allProp = config.getProperties();
		allProp.list(System.out);*/
		ConfigInitializer init  = new ConfigInitializer(config);
		
		String outputFile = "c:\\temp\\dummypais.xml";  
		String inputFileName = "c:\\temp\\TCGA-02-0001-01Z-00-DX1.svs-0000016384-0000008192.ppm.grid4.mat.grid4.xml.zip";
		String name = gen.getShortFileName(inputFileName);
		
		PAISRoot root = init.getPAISRoot(name);
		gen.setRoot(root);
		
		Project project = init.getProject();
		gen.setProject(project);	
		
		Group group = init.getGroup();
		gen.setGroup(group);		
		
		User user = init.getUser();
		gen.setUser(user);
		
		ArrayList <ImageReference> imgs = init.getImageReferences(name);
		gen.setImageReferenceCollection(imgs);
		
		ArrayList <Collection> cols = init.getCollections(name);
		gen.setCollections(cols);
		
		ArrayList <Provenance> provs = init.getProvenances();
		gen.setProvenanceCollection(provs);
		
		gen.toXML(outputFile);
		
	}
	
	
	
	
	public static void main1(String[] args) {
		int TRY_SIZE = 100000;
		String outputFile = "c:\\temp\\dummypais.xml";       
		DatatypeFactory dataFac = null;
		try {
			dataFac = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		 XMLGregorianCalendar dateTime = dataFac.newXMLGregorianCalendar("2010-08-30T18:02:48Z");
		
		PAISXMLGenerator gen = new PAISXMLGenerator();
		
		PAISRoot root = new PAISRoot(new BigInteger("79765111131"),  "oligoIII.1_40x_40x_RG-HUMAN_1", "PAIS Document 1", 
				dateTime, "1.0", "XML comment..."   );
		gen.setRoot(root);
		
		String projectUid = "100";
		BigInteger projectId = new BigInteger("100");

		ProjectGenerator projectGen = new ProjectGenerator(new BigInteger("79765111131"), "100", 
				"In Silico Brain Tumor Study", "cci.emory.edu");
		gen.setProject(projectGen.getProject());		
		
        ArrayList <Markup> markupList =  new ArrayList <Markup>();
        ArrayList <Annotation> annotationList = new ArrayList <Annotation>();
        ArrayList <Provenance> provenanceList = new ArrayList <Provenance>(); 
        
        for (int i = 0; i < TRY_SIZE; i++) {
        	MarkupGenerator markupGen = new  MarkupGenerator(BigInteger.valueOf(100), i +"", "nucleis", BigInteger.valueOf(i), null);
        	String points = "1914,31685 1887,31684 1877,31685 1871,31687 1868,31689 1862,31691 1857,31692 1853,31694 1852,31696 1846,31698 1845,31700 1841,31701 1837,31703 1836,31705 1830,31707 1827,31709 1823,31710 1820,31712 1816,31714 1814,31716 1812,31719 1811,31721 1809,31725 1807,31730 1811,31735 1812,31737 1834,31739 1852,31741 1859,31739 1868,31737 1877,31735 1895,31734 1900,31735 1902,31746 1896,31750 1895,31751 1891,31753 1882,31757 1880,31759 1875,31760 1871,31762 1870,31764 1866,31767 1862,31769 1861,31771 1859,31773 1855,31775 1853,31776 1852,31778 1850,31780 1848,31784 1846,31789 1845,31791 1843,31798 1841,31819 1846,31826 1848,31828 1852,31826 1855,31825 1857,31823 1861,31821 1864,31817 1868,31816 1870,31812 1875,31810 1878,31807 1884,31805 1889,31803 1893,31801 1898,31800 1905,31801 1907,31803 1909,31805 1911,31807 1912,31810 1914,31817 1916,31821 1918,31823 1920,31825 1921,31826 1925,31828 1927,31830 1932,31832 1941,31834 1962,31835 1966,31834 1973,31832 1978,31830 1980,31828 1986,31826 1991,31823 1995,31809 1993,31805 1989,31801 1986,31800 1984,31796 1978,31792 1977,31791 1975,31789 1973,31785 1971,31773 1973,31771 1975,31769 1977,31767 1980,31764 1986,31762 1989,31757 1991,31753 1993,31748 1989,31730 1987,31728 1986,31726 1984,31725 1982,31723 1977,31717 1975,31716 1973,31714 1971,31712 1970,31710 1964,31707 1959,31703" + i + ","+  i;
        	Markup markup = markupGen.createPolygonMarkup(BigInteger.valueOf(i), points, "svg");
        	Annotation mAnn = new AnnotationGenerator(BigInteger.valueOf(i)).getAnnotation();
        	markupGen.setAnnotation(mAnn);
        	markupList.add(markup);      
        	
        	//Add annotation        	
        	AnnotationGenerator annGen = new AnnotationGenerator(BigInteger.valueOf(i), i + "_1", "feature");
        	for (int j = 0; j < 1; j++){
        		String id = "432111121212121212165588";
        		String name = j +"th_feature" ;
        		String description = "dummy feature " + j;
        		double calValue = j*10.0;
        		CalculationGenerator calGen = new CalculationGenerator(new BigInteger(id + j), name, description);
        		Calculation cal = calGen.createScalarCalculation(BigInteger.valueOf(j), "double", calValue);    
        		annGen.addCalculation(cal);
        		Markup annMarkup = new MarkupGenerator(BigInteger.valueOf(i)).getMarkup();
        		ArrayList <Markup> markups =  new ArrayList <Markup>();
        		markups.add(annMarkup);
        		annGen.setMarkupCollection(markups);
        	}
        	annotationList.add( annGen.getAnnotation() );        	
        }	
        
        gen.setMarkupCollection(markupList);
		gen.setAnnotationCollection(annotationList);

        
        //Add Provenance
        ProvenanceGenerator provGen = new ProvenanceGenerator();
        AlgorithmGenerator algGen =  new AlgorithmGenerator(BigInteger.valueOf(1000),  "NS-MORPH", "1.0", "http://scm.cci.emory.edu/svn/imageanalysis");
        ParameterGenerator paraGen1 = new ParameterGenerator(BigInteger.valueOf(5001),  "THR", "0.9", "double");
        ParameterGenerator paraGen2 = new ParameterGenerator(BigInteger.valueOf(5002),  "T1", "5", 	"double");
        ParameterGenerator paraGen3 = new ParameterGenerator(BigInteger.valueOf(5003),  "T2", "4", 	"double");
        ParameterGenerator paraGen4 = new ParameterGenerator(BigInteger.valueOf(5004),  "G1", "80", "double");
        ParameterGenerator paraGen5 = new ParameterGenerator(BigInteger.valueOf(5005),  "G1", "45", "double");
        ArrayList <Parameter> params = new ArrayList <Parameter>();
        params.add(paraGen1.getParameter());
        params.add(paraGen2.getParameter());
        params.add(paraGen3.getParameter());
        params.add(paraGen4.getParameter());
        params.add(paraGen5.getParameter());
        provGen.setAlgorithm(algGen.getAlgorithm());
        provGen.setParameters(params);
        provenanceList.add(provGen.getProvenance());
        gen.setProvenanceCollection(provenanceList);		
		gen.toXML(outputFile);
	}

}
