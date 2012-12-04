package edu.emory.cci.pais.documentgenerator;
import java.math.BigInteger;
import edu.emory.cci.pais.model.*;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class provides interfaces to construct AnatomicEntity object
 */

public class AnatomicEntityGenerator {
	private AnatomicEntity anatomicEntity = new AnatomicEntity(); 

	public AnatomicEntityGenerator(){		
	}

	public AnatomicEntityGenerator(BigInteger id, String codeValue, String codeMeaning, 
			String codingSchemeDesignator, String codingSchemeVersion){
		setAnatomicEntity(id, codeValue, codeMeaning, codingSchemeDesignator, codingSchemeVersion);
	}	

	public AnatomicEntity getAnatomicEntity(){
		return anatomicEntity;
	}
		

	private void setAnatomicEntity(BigInteger id, String codeValue, String codeMeaning, 
			String codingSchemeDesignator, String codingSchemeVersion){
		anatomicEntity.setId(id);
		anatomicEntity.setCodeValue(codeValue);
		anatomicEntity.setCodeMeaning(codeMeaning);
		anatomicEntity.setCodingSchemeDesignator(codingSchemeDesignator);
		anatomicEntity.setCodingSchemeVersion(codingSchemeVersion);
	}	
}
