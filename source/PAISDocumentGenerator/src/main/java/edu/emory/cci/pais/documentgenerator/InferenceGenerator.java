package edu.emory.cci.pais.documentgenerator;

import java.math.BigInteger;
import edu.emory.cci.pais.model.*;

public class InferenceGenerator {
	
	private Inference inference = new Inference();
	
	public InferenceGenerator(BigInteger id, String codeValue, String codeMeaning, String codingSchemeDesignator, String codingSchemeVersion, Double annotatorConfidence, Boolean imageEvidence){
		inference.setId(id);
		inference.setCodeValue(codeValue);
		inference.setCodeMeaning(codeMeaning);
		inference.setCodingSchemeDesignator(codingSchemeDesignator);
		inference.setCodingSchemeVersion(codingSchemeVersion);
		inference.setAnnotatorConfidence(annotatorConfidence);
		inference.setImageEvidence(imageEvidence);
	}
	
	public static void main(String args[])
	{
		// Inference Testing
		
		BigInteger id = BigInteger.valueOf(100);
		String codeValue = "codeValue";
		String codeMeaning = "codeMeaning";
		String codingSchemeDesignator = "codingSchemeDesignator";
		String setCodingSchemeVersion = "setCodingSchemeVersion";
		Double annotatorConfidence = 2.0;
		Boolean imageEvidence = true;
		
		
		Inference inference = new Inference();
		inference.setId(id);
		inference.setCodeValue(codeValue);
		inference.setCodeMeaning(codeMeaning);
		inference.setCodingSchemeDesignator(codingSchemeDesignator);
		inference.setCodingSchemeVersion(setCodingSchemeVersion);
		inference.setAnnotatorConfidence(annotatorConfidence);
		inference.setImageEvidence(imageEvidence);
		
		XMLMarshaller tester = new XMLMarshaller();
		String outputFile = "inference.xml";
		tester.generateXML(Inference.class, "Inference", outputFile, inference);
	}
}
