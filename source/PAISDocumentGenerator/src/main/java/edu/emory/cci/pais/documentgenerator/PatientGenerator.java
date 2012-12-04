package edu.emory.cci.pais.documentgenerator;
import java.math.BigInteger;
import edu.emory.cci.pais.model.*;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class is used  to construct Patient object.
 */

public class PatientGenerator {
	private Patient patient = new Patient(); 
	
	public PatientGenerator(){		
	}	
    
	public PatientGenerator(BigInteger id, String patientID, String name, String sex, String birthDate, String ethnicGroup  ){
		setPatient(id, patientID, name, sex, birthDate, ethnicGroup );
	}	

	public PatientGenerator(BigInteger id, String patientID){
		setPatient(id, patientID, null, null, null, null);
	}		
	
	public Patient getPatient(){
		return patient;
	}
	
	private void setPatient(BigInteger id, String patientID, String name, String sex, String birthDate, String ethnicGroup){
		patient.setId(id);
		patient.setPatientID(patientID);
		if (name != null)
			patient.setName(name);		
		if (sex != null) 
			patient.setSex(sex);
		if (birthDate != null)
			patient.setBirthDate(birthDate);
		if (ethnicGroup != null)
		patient.setEthnicGroup(ethnicGroup);		
	}	
}
