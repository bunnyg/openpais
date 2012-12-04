package edu.emory.cci.pais.documentgenerator;
import java.math.BigInteger;
import java.util.ArrayList;
import edu.emory.cci.pais.model.*;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class is used  to construct Provenance object.
 */
public class ProvenanceGenerator {
	private Provenance provenance = new Provenance(); 
	
	public ProvenanceGenerator(){		
	}
	
	public ProvenanceGenerator(BigInteger id,  String scope){
		setProvenance(id,  scope);
	}	
	
	public ProvenanceGenerator(BigInteger id,  String scope, ArrayList<Parameter> params){
		setProvenance(id,  scope);
		setParameters(params);
		//setInputFileReferences(refs);
	}	
	
	public ProvenanceGenerator(BigInteger id,  String scope, ArrayList<Parameter> params, ArrayList<InputFileReference> refs){
		setProvenance(id,  scope);
		setParameters(params);
		setInputFileReferences(refs);
	}	

	public void setAlgorithm(Algorithm algorithm){
		Provenance.Algorithm pAlg = new Provenance.Algorithm();
		pAlg.setAlgorithm(algorithm);
		provenance.setAlgorithm(pAlg);
	}

	public void setParameters(ArrayList<Parameter> params){
		Provenance.ParameterCollection paraCollection = new Provenance.ParameterCollection();
		ArrayList<Parameter> paramList = ( ArrayList<Parameter> )  paraCollection.getParameter();
		paramList.addAll(params);
		provenance.setParameterCollection(paraCollection);
	}
	
	public void setInputFileReferences(ArrayList<InputFileReference> refs){
		Provenance.InputFileReferenceCollection refCollection = new Provenance.InputFileReferenceCollection();
		ArrayList<InputFileReference> refList = ( ArrayList<InputFileReference> )  refCollection.getInputFileReference();
		refList.addAll(refs);
		provenance.setInputFileReferenceCollection(refCollection);
	}
	
	
	public Provenance getProvenance(){
		return provenance;
	}
	
	private void setProvenance(BigInteger id,  String scope){
		provenance.setId(id);
		provenance.setScope(scope);
	}	
}
