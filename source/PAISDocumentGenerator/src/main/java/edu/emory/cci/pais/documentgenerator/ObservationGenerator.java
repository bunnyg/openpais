package edu.emory.cci.pais.documentgenerator;

import java.math.BigInteger;
import edu.emory.cci.pais.model.*;
import edu.emory.cci.pais.model.Observation.ObservationCharacteristicCollection;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class is used  to construct Observation object.
 */

public class ObservationGenerator {
	
	
	private Observation observation = new Observation();
	
	
	public ObservationGenerator(BigInteger id, String name, BigInteger quantificationId, String codeValue, String codeMeaning, String codingSchemeDesignator, String codingSchemeVersion, 
			String comment, Double annotatorConfidence, String quantificationType){

		
		//this.quantificationId = quantificationId;
		observation.setId(id);
		observation.setName(name);
		observation.setCodeValue(codeValue);
		observation.setCodeMeaning(codeMeaning);
		observation.setCodingSchemeDesignator(codingSchemeDesignator);
		observation.setCodingSchemeVersion(codingSchemeVersion);
		observation.setComment(comment);
		observation.setAnnotatorConfidence(annotatorConfidence);
	}
	
	public Observation getObservation() {
		return observation;
	}
	
	public void setNominalQuantification(Nominal nominal){
		Observation.Quantification oQuantification = new Observation.Quantification();
		oQuantification.setQuantification(nominal);
		observation.setQuantification(oQuantification);
	}
	
	public void setOrdinalQuantification(Ordinal ordinal){
		Observation.Quantification oQuantification = new Observation.Quantification();
		oQuantification.setQuantification(ordinal);
		observation.setQuantification(oQuantification);
	}
	
	public void setRatioQuantification(Ratio ratio){
		Observation.Quantification oQuantification = new Observation.Quantification();
		oQuantification.setQuantification(ratio);
		observation.setQuantification(oQuantification);
	}
	
	
	public void setIntervalQuantification(Interval interval){
		Observation.Quantification oQuantification = new Observation.Quantification();
		oQuantification.setQuantification(interval);
		observation.setQuantification(oQuantification);
	}
	

	
	public void setObservationCharacteristicCollection( ObservationCharacteristicCollection obsCollection ){
		observation.setObservationCharacteristicCollection(obsCollection);		
/*		Observation.ObservationCharacteristicCollection oobsCollection = new Observation.ObservationCharacteristicCollection();
		ArrayList <ObservationCharacteristic>  obsChrList  =  (ArrayList <ObservationCharacteristic>)  oobsCollection.getObservationCharacteristic();
		obsChrList.addAll(obsCollection.getObservationCharacteristic());
		observation.setObservationCharacteristicCollection(oobsCollection);*/
	}

	
	// Need to confirm 
	Observation.Provenance createObservationProvenance(BigInteger observationProvenanceId )
	{
		// TODO
		observation.setId(observationProvenanceId);
		return observation.getProvenance();
	}
	
//Need to confirm 
	Observation.Quantification createObservationQuantification(BigInteger observationQuantificationId)
	{
		// TODO
		observation.setId(observationQuantificationId);
		return observation.getQuantification();
	}

}
