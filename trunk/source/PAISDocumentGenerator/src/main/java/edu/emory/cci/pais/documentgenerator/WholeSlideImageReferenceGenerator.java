package edu.emory.cci.pais.documentgenerator;
import java.math.BigInteger;
import java.util.ArrayList;
import edu.emory.cci.pais.model.*;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class is used  to construct WholeSlideImageReference object.
 */

public class WholeSlideImageReferenceGenerator {

	private WholeSlideImageReference wsi = new WholeSlideImageReference();
	
	public WholeSlideImageReferenceGenerator(BigInteger id, String uid, String fileReference, String uri, 
			double resolution, double zaxisResolution, double zaxisCoordinate ){				
		setWholeSlideImageReference(id, uid, fileReference, uri, 
				resolution, zaxisResolution, zaxisCoordinate);
	}
		
	
	public WholeSlideImageReferenceGenerator(BigInteger id, String uid, String fileReference,  
			double resolution){
		String uri = null; 
		setWholeSlideImageReference(id, uid, fileReference, uri, resolution, -1, -1000000);		
	}
	
	private void setWholeSlideImageReference(BigInteger id, String uid, String fileReference, String uri, 
			double resolution, double zaxisResolution, double zaxisCoordinate){
		wsi.setId(id);
		wsi.setUid(uid);
    	wsi.setFileReference(fileReference);
    	wsi.setUri(uri);
    	wsi.setResolution( Double.valueOf(resolution) );
    	if (zaxisResolution > 0 )
    		wsi.setZaxisResolution( Double.valueOf(zaxisResolution) );
    	if (zaxisCoordinate > -1000000)
    	wsi.setZaxisCoordinate( Double.valueOf(zaxisCoordinate) );    
	}
	
	
	public void setAnatomicEntityCollection(ArrayList <AnatomicEntity> aeCollection  ){
		ImageReference.AnatomicEntityCollection imgaeCollection = new ImageReference.AnatomicEntityCollection();
		ArrayList <AnatomicEntity> aeList = (ArrayList <AnatomicEntity> )  imgaeCollection.getAnatomicEntity();
		aeList.addAll(aeCollection);
		wsi.setAnatomicEntityCollection(imgaeCollection);
	}
	
	public void setSubject(Subject subject){
		ImageReference.Subject imgSubject = new ImageReference.Subject();
		imgSubject.setSubject(subject);
		wsi.setSubject(imgSubject);
	}
	
	public void setSpecimen(Specimen specimen){
		ImageReference.Specimen imgSpecimen = new ImageReference.Specimen();
		imgSpecimen.setSpecimen(specimen);
		wsi.setSpecimen(imgSpecimen);
	}
	
	public void setEquipment(Equipment equipment ) {
		if (equipment == null) return;
		ImageReference.Equipment imgEquipment = new ImageReference.Equipment();
		imgEquipment.setEquipment(equipment);
		wsi.setEquipment(imgEquipment);		
	}

	
	public void setRegion(Region region){
		ImageReference.Region imgRegion = new ImageReference.Region();
		imgRegion.setRegion(region);
		wsi.setRegion(imgRegion);
	}
	
	
	public WholeSlideImageReference getWholeSlideImageReference(){
		return wsi;
	}


	public static void main(String[] args) {
		int TRY_SIZE = 200000;

		BigInteger id = new BigInteger("4321111212121212121");
		String uid = "MYSLIDE111";
		String fileReference = "myfile.img";
		String uri = "";
		double resolution = 0.002345;

		WholeSlideImageReferenceGenerator wsiGen = new WholeSlideImageReferenceGenerator(id, uid, fileReference, resolution);
		PatientGenerator patientGen = new PatientGenerator(id, uid);		
		wsiGen.setSubject(patientGen.getPatient());
		AnatomicEntityGenerator aeGen = new AnatomicEntityGenerator(id, "brain", "", "",  "");
		ArrayList<AnatomicEntity> aeCollection = new ArrayList<AnatomicEntity>();
		aeCollection.add(aeGen.getAnatomicEntity());
		wsiGen.setAnatomicEntityCollection(aeCollection);
		
		
		WholeSlideImageReference wsi = wsiGen.getWholeSlideImageReference(); 
		
		
		XMLMarshaller tester = new XMLMarshaller();
		String outputFile = "c:\\temp\\dummyimagereference.xml";        
		tester.generateXML(WholeSlideImageReference.class, "ImageReference", outputFile, wsi);	
	}	
	
}
