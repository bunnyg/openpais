package edu.emory.cci.pais.documentgenerator;
import java.math.BigInteger;
import edu.emory.cci.pais.model.*;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class is used  to construct Specimen object.
 */

public class SpecimenGenerator {
	private Specimen specimen = new Specimen(); 
	
	public SpecimenGenerator(){		
	}
    
	public SpecimenGenerator(BigInteger id, String uid, String type, String stain){
		setSpecimen(id, uid, type, stain );
	}	
	
	public Specimen getSpecimen(){
		return specimen;
	}
	
	private void setSpecimen(BigInteger id, String uid, String type, String stain){
		specimen.setId(id);
		specimen.setUid(uid);
		specimen.setType(type);
		specimen.setStain(stain);
	}	
}
