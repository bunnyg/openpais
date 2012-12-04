package edu.emory.cci.pais.documentgenerator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

/**
 * This class parses a text file with Matlab results with calculations and observations of nuclei. 
 * The format was defined by fusheng.wang@emory.edu and it was designed having performance and 
 * the optimization of space requirement in mind. The layout of the format consists of five sections,
 * delimited by Labels starting with a '#'. Each line starting with a '#' indicates the beginning of
 * a new section, every other line represents a record meant to be parsed. The five labels used at the 
 * moment are:
 * 
 * #CalculationMetadata
 * #ObservationMetadata
 * #Markups [format={"chaincode" | "svg" | "db2"}]
 * #Calculations
 * #Observations
 * 
 * The default value for the markup format is chaincode.
 * Each line in the Markups, Calculations, and Observations sections represents a single result for one
 * entity (nucleus), i.e. The first line of the Markups section, the first line of the Calculations section,
 * and the first line of the Observations section, they all together describe the results for one single nucleus.
 * One Markup line describes all the points that compose the markup. One line in the #Calculations section
 * stores the results for all the different types of calculations performed to a nucleus. The result values are
 * separated by a comma (','). The meaning or type of the calculation that a value represents, is defined by the
 * #CalculationMetadata section. Each line in this section describes one comma separated value in a #Calculations 
 * line. Consecuently, each line in the #Calculations section must have exactly the same amount of comma separated
 * values as lines present in the #CalculationMetadata. 
 * Each line in the #CalculationMetadata section is composed of a comma separated set of values describing 1) the name
 * of the calculation, 2) a description of the calculation, and 3) the data type of the result of the calculation
 * written in capitals, i.e. DOUBLE.
 * The sections #Observations and #ObservationMetadata work exactly the same way as #Calculations and
 * #CalculationMetadata. The only difference is that each line of the #ObservationMetadata must be composed of four
 * comma separated values instead of only three and the values represent: 1) Observation name, 2) Quantification name, 
 * 3) Quantification type (Ordinal or Nominal), and 4) the data type of the ordinal quantification.
 * 
 * For more information, please take a look at the template in the svn repository:
 * 
 * TCGA-02-0001-01Z-00-DX1.svs-0000045056-0000016384.ppm.grid4.mat.txt
 * 
 * @author cristobal
 *
 */
public class TextFileInputDataReader extends InputDataReader {

	java.util.logging.Logger logger = java.util.logging.Logger.getLogger(this.getClass().getPackage().getName());
	
	ArrayList<CalculationMetadata> calculationMetadata = new ArrayList<CalculationMetadata>();
	ArrayList<ObservationMetadata> observationMetadata = new ArrayList<ObservationMetadata>();
	ArrayList<String> markups = new ArrayList<String>();
	String markupFormat = "";
	public String getMarkupFormat() {
		return markupFormat;
	}
	ArrayList<String[]> calculations = new ArrayList<String[]>();
	ArrayList<String[]> observations = new ArrayList<String[]>();
	
	enum ReadingWhat {
		READING_CALCULATION_METADATA, READING_OBSERVATION_METADATA,
		READING_MARKUP, READING_CALCULATION, READING_OBSERVATION, READING_NOTHING
	}
	
	/**
	 * 
	 * @param inputFileName: this is the absolute path to the file containing the algorithms results.
	 */
	public TextFileInputDataReader(String inputFileName) {
		super(inputFileName);
		init();
	}
	
	void init() {
		BufferedReader inputFile = null;
		try {
			inputFile = new BufferedReader(new FileReader(inputFileName));
			String line;
			ReadingWhat readingWhat = ReadingWhat.READING_NOTHING;
			while((line = inputFile.readLine()) != null) {
				if(line.startsWith("#")) {
					if(line.startsWith("#CalculationMetadata"))
						readingWhat = ReadingWhat.READING_CALCULATION_METADATA;
					else if(line.startsWith("#ObservationMetadata"))
						readingWhat = ReadingWhat.READING_OBSERVATION_METADATA;
					else if(line.startsWith("#Markups")) {
						int indexOfFormat = line.indexOf("format=\"");
						if(indexOfFormat != -1) {
							markupFormat = line.substring(indexOfFormat + 8, line.lastIndexOf("\""));
						} else {
							indexOfFormat = line.indexOf("format=\'");
							if(indexOfFormat != -1) {
								markupFormat = line.substring(indexOfFormat + 8, line.lastIndexOf("'"));
							}else{
								indexOfFormat = line.indexOf("format=");
								if(indexOfFormat != -1) {
									markupFormat = line.substring(indexOfFormat + 7, line.length());
								} else {
									logger.log(Level.INFO, "\"format\" option not found in #Markups tag. Using markupFormat attribute from the configuration file.");
								}
							}
						}
						markupFormat = markupFormat.trim();
						readingWhat = ReadingWhat.READING_MARKUP;
					}
					else if(line.startsWith("#Calculations"))
						readingWhat = ReadingWhat.READING_CALCULATION;
					else if(line.startsWith("#Observations"))
						readingWhat = ReadingWhat.READING_OBSERVATION;
				} else {
					switch(readingWhat) {
						case READING_CALCULATION_METADATA: parseCalculationMetadata(line);break;
						case READING_OBSERVATION_METADATA: parseObservationMetadata(line);break;
						case READING_MARKUP: parseMarkup(line);break;
						case READING_CALCULATION: parseCalculation(line);break;
						case READING_OBSERVATION: parseObservation(line);break;
						default:break;
					}
				}
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if(inputFile != null) {
				try {
					inputFile.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	void parseCalculationMetadata(String line) {
		String[] metadata = line.split(",");
		trimAll(metadata);
		
		if(metadata.length == 2) {
			// We don't have description
			calculationMetadata.add(new CalculationMetadata(metadata[0], "", metadata[1]));
		} else if(metadata.length == 3) {
			calculationMetadata.add(new CalculationMetadata(metadata[0], metadata[1], metadata[2]));
		} else {
			throw new ParseCalculationMetadataException("Found "+metadata.length+" elements while parsing calculation metadata.\nLine found: '"+line+"'. Only 2 or 3 parameters are allowed.");
		}
	}
	
	void parseObservationMetadata(String line) {
		String[] metadata = line.split(",");
		trimAll(metadata);
		
		if(metadata.length != 4) {
			throw new ParseObservationMetadataException("Found "+metadata.length+" elements while parsing observation metadata.\nLine found: '"+line+"'. Only 4 parameters are allowed.");
		} else {
			observationMetadata.add(new ObservationMetadata(metadata[0], metadata[1], metadata[2], metadata[3]));
		}
	}
	
	void parseMarkup(String line) {
		markups.add(line.trim());
	}
	
	void parseCalculation(String line) {
		String[] calculation = line.split(",");
		trimAll(calculation);
		calculations.add(calculation);
	}
	
	void parseObservation(String line) {
		String[] observation = line.split(",");
		trimAll(observation);
		observations.add(observation);
	}

	// Eliminate spaces in the strings
	void trimAll(String[] strings) {
		for(int i=0;i < strings.length;i++) {
			strings[i] = strings[i].trim();
		}
	}

	@Override
	public String[] getRegionNames() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Calculation metadata consists of three comma separated fields:
	 * 1) the name of the calculation
	 * 2) a description of the calculation
	 * 3) the data type of the result of the calculation written in capitals, i.e. DOUBLE
	 */
	@Override
	public ArrayList<CalculationMetadata> getCalculationMetadata() {
		return calculationMetadata;
	}

	/**
	 * Observation metadata consists of four comma separated fields:
	 * 1) Observation name
	 * 2) Quantification name 
	 * 3) Quantification type (in this case it must be Ordinal)
	 * 4) the data type of the ordinal quantification
	 * 
	 * See the class documentation for more details.
	 */
	@Override
	public ArrayList<ObservationMetadata> getObservationMetadata() {
		return observationMetadata;
	}

	@Override
	public Iterator<String> getMarkupIterator() {
		return markups.iterator();
	}

	@Override
	public Iterator<String[]> getCalculationIterator() {
		return calculations.iterator();
	}

	@Override
	public Iterator<String[]> getObservationIterator() {
		return observations.iterator();
	}

}
