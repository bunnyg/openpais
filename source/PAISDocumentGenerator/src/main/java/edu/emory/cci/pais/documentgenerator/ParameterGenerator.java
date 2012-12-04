package edu.emory.cci.pais.documentgenerator;
import java.math.BigInteger;
import edu.emory.cci.pais.model.*;


/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class is used to construct Parameter object.
 */

public class ParameterGenerator {
	private Parameter parameter = new Parameter(); 
	
	public ParameterGenerator(){		
	}
	
	public ParameterGenerator(BigInteger id,  String name, String value, String dataType){
		setParameter(id,  name, value, dataType);
	}	

	public Parameter getParameter(){
		return parameter;
	}
	
	private void setParameter(BigInteger id,  String name, String value, String dataType){
		parameter.setId(id);
		parameter.setName(name);
		parameter.setValue(value);
		parameter.setDataType(dataType);
	}	
}
