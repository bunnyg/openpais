package edu.emory.cci.pais.documentgenerator;

import java.math.BigInteger;
import edu.emory.cci.pais.model.*;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class is used to construct Quantification object.
 */

public class QuantificationGenerator {
	
	public QuantificationGenerator(){
	}
	
	public Nominal createNominalQuantification(BigInteger id, String name, String value){
		Nominal normal = new Nominal();
		normal.setValue(value);
		normal.setName(name);
		normal.setId(id);
		return normal;
	}	
	
    
	public Ordinal createOrdinalQuantification(BigInteger id, String name, String dataType, String unitOfMeasure){
		Ordinal ordinal = new Ordinal();
		ordinal.setId(id);
		ordinal.setName(name);
		ordinal.setDataType(dataType);
		ordinal.setUnitOfMeasure(unitOfMeasure);
		return ordinal;
	}	
	
	public Interval createIntervalQuantification(BigInteger id, String name, Double value, Double minValue, Double maxValue, String dataType, String unitOfMeasure){
		Interval interval = new Interval();
		interval.setId(id);
		interval.setName(name);
		interval.setValue(value);
		interval.setMinValue(minValue);
		interval.setMaxValue(maxValue);
		interval.setDataType(dataType);
		interval.setUnitOfMeasure(unitOfMeasure);
		return interval;
	}
	
	public Ratio createRatioQuantification(BigInteger id, String name, Double value, String dataType, String unitOfMeasure){
		Ratio ratio = new Ratio();
		ratio.setId(id);
		ratio.setName(name);
		ratio.setValue(value);
		ratio.setDataType(dataType);
		ratio.setUnitOfMeasure(unitOfMeasure);
		return ratio;
	}
}
