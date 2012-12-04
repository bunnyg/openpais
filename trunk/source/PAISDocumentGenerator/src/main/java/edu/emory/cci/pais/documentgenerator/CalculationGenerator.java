package edu.emory.cci.pais.documentgenerator;
import java.math.BigInteger;
import edu.emory.cci.pais.model.*;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class provides interfaces to construct Calculation object
 */
public class CalculationGenerator {
	private Calculation calculation = new Calculation(); 
	
	public CalculationGenerator(){		
	}
	
	public CalculationGenerator(BigInteger id, String name, String codeValue, String codeMeaning, String codingSchemeDesignator, String codingSchemeVersion, String description, String unitOfMeasure){
		setCalculation(id, name, codeValue, codeMeaning, codingSchemeDesignator, codingSchemeVersion, description, unitOfMeasure);
	}	

	public CalculationGenerator(BigInteger id, String name, String description){
		setCalculation(id, name, null, null, null, null, description, null);
	}		
	
	public Calculation getCalculation(){
		return calculation;
	}
	
	private void setCalculation(BigInteger id, String name, String codeValue, String codeMeaning, String codingSchemeDesignator, String codingSchemeVersion, String description, String unitOfMeasure){
		calculation.setId(id);
		calculation.setName(name);
		calculation.setDescription(description);
		if (codeValue != null)
			calculation.setCodeValue(codeValue);
		if (codeMeaning != null)
			calculation.setCodeMeaning(codeMeaning);
		if (codingSchemeDesignator != null)
			calculation.setCodingSchemeDesignator(codingSchemeDesignator);
		if (codingSchemeVersion != null)
			calculation.setCodingSchemeVersion(codingSchemeVersion);
		if (unitOfMeasure != null)
			calculation.setUnitOfMeasure(unitOfMeasure);
	}	
	
	
	public Calculation createScalarCalculation(BigInteger id, String dataType, double value){
		Scalar scalar = new Scalar();
		scalar.setId(id);
		scalar.setDataType(dataType);
		scalar.setValue( Double.valueOf(value) );
		
		Calculation.CalculationResult calculationResult = new Calculation.CalculationResult();
		calculationResult.setCalculationResult(scalar);
		calculation.setCalculationResult(calculationResult);
		return calculation;
	}
	
	public Calculation createArrayCalculation(BigInteger id, String dataType, String value){
		Array array = new Array();
		array.setId(id);
		array.setDataType(dataType);
		array.setValue(value);
		Calculation.CalculationResult calculationResult = new Calculation.CalculationResult();
		calculationResult.setCalculationResult(array);
		calculation.setCalculationResult(calculationResult);
		return calculation;
	}
	
	public Calculation createHistogramCalculation(BigInteger id, String dataType, String value){
		Histogram histogram = new Histogram();
		histogram.setId(id);
		histogram.setDataType(dataType);
		histogram.setValue(value);
		Calculation.CalculationResult calculationResult = new Calculation.CalculationResult();
		calculationResult.setCalculationResult(histogram);
		calculation.setCalculationResult(calculationResult);
		return calculation;
	}
	
	public Calculation createFileReferenceCalculation(BigInteger id, String dataType, String value){
		FileReference fileRef = new FileReference();
		fileRef.setId(id);
		fileRef.setDataType(dataType);
		fileRef.setValue(value);
		Calculation.CalculationResult calculationResult = new Calculation.CalculationResult();
		calculationResult.setCalculationResult(fileRef);
		calculation.setCalculationResult(calculationResult);
		return calculation;
	}
	
	
	public Calculation createURICalculation(BigInteger id, String dataType, String value){
		URI uri = new URI();
		uri.setId(id);
		uri.setDataType(dataType);
		uri.setValue(value);
		Calculation.CalculationResult calculationResult = new Calculation.CalculationResult();
		calculationResult.setCalculationResult(uri);
		calculation.setCalculationResult(calculationResult);
		return calculation;
	}	
	
	public Calculation createBinaryCalculation(BigInteger id,  String value, String encoding, String compression){
		Binary binary = new Binary();
		binary.setId(id);
		binary.setValue(value);
		binary.setEncoding(encoding);
		binary.setCompression(compression);
		Calculation.CalculationResult calculationResult = new Calculation.CalculationResult();
		calculationResult.setCalculationResult(binary);
		calculation.setCalculationResult(calculationResult);
		return calculation;
	}
	
	public Calculation createMatrixCalculation(BigInteger id,  String value, BigInteger numOfColumns,  BigInteger numOfRows){
		Matrix matrix = new Matrix();
		matrix.setId(id);
		matrix.setValue(value);
		matrix.setNumberOfColumns(numOfColumns);
		matrix.setNumberOfRows(numOfRows);
		Calculation.CalculationResult calculationResult = new Calculation.CalculationResult();
		calculationResult.setCalculationResult(matrix);
		calculation.setCalculationResult(calculationResult);
		return calculation;
	}
	
	public void setProvenance(Provenance provenance){
		Calculation.Provenance cPro = new Calculation.Provenance();
		cPro.setProvenance(provenance);
		calculation.setProvenance(cPro);
	}

	public static void main(String[] args) {
		int TRY_SIZE = 200000;

		String id = "4321111212121212121";
		String name = "area";
		String description = "area";
		double value = 343434.20;
		CalculationGenerator calGen = new CalculationGenerator(new BigInteger(id), name, description);
		Calculation cal = calGen.createScalarCalculation(new BigInteger(id), "double", value);

		XMLMarshaller tester = new XMLMarshaller();
		String outputFile = "c:\\temp\\dummycalculation.xml";        
		tester.generateXML(Calculation.class, "Calculation", outputFile, cal);	
	}
	
}