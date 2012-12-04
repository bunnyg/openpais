package edu.emory.cci.pais.documentgenerator;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0
 * 
 * This abstract class defines common functions to be implemented.  
 *
 */
public abstract class InputDataReader {
	protected String inputFileName;
	public String getInputFileName() {
		return inputFileName;
	}
	
	/**
	 * @param inputFileName The name of the input file to be parsed
	 */
	public  InputDataReader(String inputFileName){
		this.inputFileName = inputFileName;
	}
	
	/**
	 * @return Names of regions.
	 * One input file may contain multiple regions or a single one. 
	 */
	public abstract String [] getRegionNames();
	
	/**
	 * @return an array of metadata for calculations of features. 
	 * e.g., "area" is a feature that generates the area of a polygon of type double: "area", "area of polygon", "double"  
	 */
	public abstract ArrayList<CalculationMetadata> getCalculationMetadata();
	
	/** 
	 * @return an array of metadata of observations
	 */
	public abstract ArrayList<ObservationMetadata> getObservationMetadata();
	
	/**
	 * @return Iterator to Markups in string
	 */
	public abstract  Iterator<String> getMarkupIterator();
	
	/**
	 * @return Iterator to calculation
	 */
	public abstract  Iterator<String[]> getCalculationIterator();
	
	
	/**
	 * @return Iterator to observations.  
	 */
	public abstract Iterator<String[]> getObservationIterator();
	
	public static class CalculationMetadata {
		private String name;
		private String description;
		private String dataType;
		public CalculationMetadata(String name,String description, String dataType){
			this.name = name;
			this.description= description;
			this.dataType = dataType;
		}
		public  String getName(){
			return name;			
		}
		
		public String getdescription(){
			return description;
		}
		
		public String getDataType(){
			return dataType;
		}
	}
	
	public static class ObservationMetadata {
		private String name;
		private String quantificationName;
		private String quantificationType;
		private String dataType;
		
		public ObservationMetadata(String name, String quantificationName, String quantificationType, String dataType){
			this.name = name;
			this.quantificationName = quantificationName;
			this.quantificationType = quantificationType;
			this.dataType = dataType;
		}
		public  String getName(){
			return name;			
		}
		
		public String getQuantificationName(){
			return quantificationName;
		}
		
		public String getQuantificationType(){
			return quantificationType;
		}
		
		public String getDataType(){
			return dataType;
		}
		
	}	
	
	
}
