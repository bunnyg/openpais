package edu.emory.cci.pais.documentgenerator;
import java.math.BigInteger;
import edu.emory.cci.pais.model.*;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class provides interfaces to construct Algorithm objects. 
 */

public class AlgorithmGenerator {
	private Algorithm algorithm = new Algorithm(); 
	
	public AlgorithmGenerator(){		
	}
	
	public AlgorithmGenerator(BigInteger id,  String name, String version, String uri){
		setAlgorithm(id,  name, version, uri);
	}	

	public Algorithm getAlgorithm(){
		return algorithm;
	}
	
	private void setAlgorithm(BigInteger id,  String name, String version, String uri){
		algorithm.setId(id);
		algorithm.setVersion(version);
		algorithm.setName(name);
		algorithm.setUri(uri);
	}	
}
