package edu.emory.cci.pais.documentgenerator;

import java.math.BigInteger;
import java.util.ArrayList;
// import edu.emory.cci.pais.documentgenerator.*;
import edu.emory.cci.pais.model.*;


public class TMAImageReferenceGenerator {

	private TMAImageReference TMAimageRef = new TMAImageReference();
	
	public TMAImageReferenceGenerator(BigInteger id, String uid, String fileReference, String uri, 
			double resolution, double zaxisResolution, double zaxisCoordinate ){		
		setTMAImageReference(id, uid, fileReference, uri, 
				resolution, zaxisResolution, zaxisCoordinate);
	}
		
	
	public TMAImageReferenceGenerator(BigInteger id, String uid, String fileReference,  
			double resolution){
		String uri = null; 
		setTMAImageReference(id, uid, fileReference, uri, resolution, -1, -1000000);		
	}
	
	private void setTMAImageReference(BigInteger id, String uid, String fileReference, String uri, 
			double resolution, double zaxisResolution, double zaxisCoordinate){
		TMAimageRef.setId(id);
		TMAimageRef.setUid(uid);
		TMAimageRef.setFileReference(fileReference);
		TMAimageRef.setUri(uri);
		TMAimageRef.setResolution(Double.valueOf(resolution));
		TMAimageRef.setZaxisResolution(zaxisResolution);
		TMAimageRef.setZaxisCoordinate(zaxisCoordinate);
    	if (zaxisResolution > 0 )
    		TMAimageRef.setZaxisResolution( Double.valueOf(zaxisResolution) );
    	if (zaxisCoordinate > -1000000)
    		TMAimageRef.setZaxisCoordinate( Double.valueOf(zaxisCoordinate) );    
	}
	
	
	public void setAnatomicEntityCollection(ArrayList <AnatomicEntity> aeCollection  ){
		ImageReference.AnatomicEntityCollection imgaeCollection = new ImageReference.AnatomicEntityCollection();
		ArrayList <AnatomicEntity> aeList = (ArrayList <AnatomicEntity> )  imgaeCollection.getAnatomicEntity();
		aeList.addAll(aeCollection);
		TMAimageRef.setAnatomicEntityCollection(imgaeCollection);
	}
	
	public void setSubject(Subject subject){
		ImageReference.Subject imgSubject = new ImageReference.Subject();
		imgSubject.setSubject(subject);
		TMAimageRef.setSubject(imgSubject);
	}
	
	public void setSpecimen(Specimen specimen){
		ImageReference.Specimen imgSpecimen = new ImageReference.Specimen();
		imgSpecimen.setSpecimen(specimen);
		TMAimageRef.setSpecimen(imgSpecimen);
	}
	
	public void setEquipment(Equipment equipment ) {
		if (equipment == null) return;
		ImageReference.Equipment imgEquipment = new ImageReference.Equipment();
		imgEquipment.setEquipment(equipment);
		TMAimageRef.setEquipment(imgEquipment);		
	}

	
	public void setRegion(Region region){
		ImageReference.Region imgRegion = new ImageReference.Region();
		imgRegion.setRegion(region);
		TMAimageRef.setRegion(imgRegion);
	}
	
	
	public TMAImageReference getTMAImageReference(){
		return TMAimageRef;
	}


	public static void main(String[] args) {
		int TRY_SIZE = 200000;

		BigInteger id = new BigInteger("4321111212121212121");
		String uid = "MYSLIDE222";
		String fileReference = "myfile.img";
		String uri = "";
		double resolution = 0.002345;

		TMAImageReferenceGenerator TMAimageRefGen = new TMAImageReferenceGenerator(id, uid, fileReference, resolution);
		PatientGenerator patientGen = new PatientGenerator(id, uid);		
		TMAimageRefGen.setSubject(patientGen.getPatient());
		AnatomicEntityGenerator aeGen = new AnatomicEntityGenerator(id, "brain", "", "",  "");
		ArrayList<AnatomicEntity> aeCollection = new ArrayList<AnatomicEntity>();
		aeCollection.add(aeGen.getAnatomicEntity());
		TMAimageRefGen.setAnatomicEntityCollection(aeCollection);
		
		TMAImageReference TMAimageRef = TMAimageRefGen.getTMAImageReference(); 
		
		XMLMarshaller tester = new XMLMarshaller();
		String outputFile = "dummyimagereference_TMA.xml";        
		tester.generateXML(TMAImageReference.class, "ImageReference", outputFile, TMAimageRef);	
	}	
}
