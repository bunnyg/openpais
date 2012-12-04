package edu.emory.cci.pais.documentgenerator;
import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;
import edu.emory.cci.pais.model.*;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class provides interfaces to construct Collection object
 */

public class CollectionGenerator {
	private Collection collection = new Collection(); 
	
	public CollectionGenerator(){		
	}
	
	public CollectionGenerator(BigInteger id, String uid, String name, String role, String methodName, 
			String sequenceNumber, XMLGregorianCalendar studyDateTime){
		setCollection(id, uid, name, role, methodName, sequenceNumber, studyDateTime);
	}	

	public CollectionGenerator(BigInteger id, String uid, String name, String role, String methodName, 
			String sequenceNumber){
		setCollection(id, uid, name, role, methodName, sequenceNumber, null);
	}	
	
	public Collection getCollection(){
		return collection;
	}
	
	private void setCollection(BigInteger id, String uid, String name, String role, String methodName, 
			String sequenceNumber, XMLGregorianCalendar studyDateTime){
		collection.setId(id);
		collection.setUid(uid);
		collection.setName(name);
		collection.setRole(role);
		collection.setMethodName(methodName);
		collection.setSequenceNumber(sequenceNumber);
		if (studyDateTime != null)
			collection.setStudyDateTime(studyDateTime);		
	}	
}
