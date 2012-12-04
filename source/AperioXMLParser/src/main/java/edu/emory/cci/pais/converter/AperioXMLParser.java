package edu.emory.cci.pais.converter;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;


import edu.emory.cci.pais.converter.StAXUtils;



/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0
 * This class provides functions to parse XML data
 *
 */
public class AperioXMLParser {


	private String filterAttrName = "Description";
	private String filterAttrValue = "Vessel"; 

	private XMLInputFactory xmlif = null ;
	private XMLStreamReader xmlr  = null;
	
	
	
	public AperioXMLParser(){
	}

	public AperioXMLParser(String filter){
		setFilterAttr(filter);
	}	
	
	
	public boolean setXMLInputFactory(String filename){
        try{
            xmlif = XMLInputFactory.newInstance();
            xmlif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,Boolean.TRUE);
            xmlif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,Boolean.FALSE);        
            xmlif.setProperty(XMLInputFactory.IS_COALESCING , Boolean.FALSE);            
            xmlr = xmlif.createXMLStreamReader(filename, new FileInputStream(filename));
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;
	}
	
	public XMLStreamReader getXmlStreamReader(){
		return xmlr;
	}
	
	public void setFilterAttr(String attrValue){
		filterAttrValue = attrValue;
	}

	/**
	 * @param filename XML file to be parsed
	 *  */
	public boolean parse(String filename, String targetFolder)  {

		long starttime = System.currentTimeMillis();
     
		File targetFolderFile = null;
		BufferedWriter writer = null;


		String outFilename;

		if (targetFolder == null ){
			outFilename = StAXUtils.getTargetFilename(filename, "txt");
			System.out.println(outFilename);	
		} 		
		else{		
			targetFolderFile = new File(targetFolder);
			if ( !targetFolderFile.exists() ){
				targetFolderFile.mkdir();
			}
			outFilename = StAXUtils.getTargetFilename(filename, 0, targetFolder);
			System.out.println(outFilename);		
		}		
		
		writer =StAXUtils.getBufWtr(outFilename);

			
		setXMLInputFactory(filename);
        XMLStreamReader xmlr = getXmlStreamReader();
        

        int idx = 1;
        
        try{          
        	
        	//  /Annotations/Annotation/Regions/Region/Vertices
        	while(xmlr.hasNext()){
        		xmlr.next();
        		if ( StAXUtils.isStartElement("Annotations", xmlr) ) {
        			while(xmlr.hasNext()){
        				xmlr.next();
        	       		if ( StAXUtils.isEndElement("Annotations", xmlr) ) break;
        	       		
        				if ( StAXUtils.isStartElement("Annotation", xmlr) ) {
        					//System.out.println("Start ant: ");
        					
        					while(xmlr.hasNext()){
        						xmlr.next();          					
        						if ( StAXUtils.isEndElement("Annotation", xmlr) )  { break; } //System.out.println("End ann");

        						if ( StAXUtils.isStartElement("Region", xmlr) ){  
        							//System.out.println("Start Region:"); 
        							
        							if ( (filterAttrValue== null) || StAXUtils.hasAttribute(filterAttrName, filterAttrValue, xmlr ) ) {        							
        								StringBuffer buf = new StringBuffer(idx + "; ");
        								idx ++;
        								buf.append( StAXUtils.getRegionCoords(xmlr) );
        								buf.append("\n");
        								//System.out.println(buf);
        								try {
        									writer.write(buf.toString() );
        								} catch (IOException e) {
        									// TODO Auto-generated catch block
        									e.printStackTrace();
        								}
        							}
        						}//start Region
        						//if ( StAXUtils.isEndElement("Region", xmlr) )  {System.out.println("End Region");  }
        						
        					}
        				}//start Annotation
        			}
        		}

        	}//end while
        	
        }catch(XMLStreamException ex){
        	System.out.println(ex.getMessage());
        	if(ex.getNestedException() != null)ex.getNestedException().printStackTrace();
        	throw new RuntimeException(ex);
        }finally {  
	        //Close input file
	        try {
				xmlr.close();
			} catch (XMLStreamException e1) {
				e1.printStackTrace(); 
			}
	        
			//Close new files
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }

        long endtime = System.currentTimeMillis();
		double loadingTime = (endtime - starttime)/1000.0;
		//System.out.println("Total Parsing and Loading Time = " + loadingTime + " seconds." );	
      //Close new files
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
    }
	
	
	/** Batch convert Aperio files in a folder */
	public boolean batchConvert(String rootPath, String targetFolder){
		File folderFile = new File(rootPath);
		FilenameFilter xmlOnly = new OnlyExt("xml");
		String files[] = folderFile.list(xmlOnly);
		int failedCount = 0;
		System.out.println("# of files: " + files.length);
		for (int i = 0; i < files.length; i++){
			String file = files[i];
			String zipFilePath = rootPath + File.separatorChar + file;
			boolean result = parse(zipFilePath, targetFolder);
			if (result == false) failedCount++;			
		}
		System.out.println( failedCount +  " of files failed. ");
		return true;	
	}	

	
	class OnlyExt implements FilenameFilter{
		String ext;
		public OnlyExt(String ext){		  
			this.ext="." + ext;
		}

		public boolean accept(File dir,String name){		  
			return name.endsWith(ext);
		}
	}
	
	
	public static void main(String[] args) {

		/** Example:
		 * Single file:	java -jar  AperioXMLParser.jar -ft file -i d:\projects\data\lumens\TCGA-02-0001-01C-01-BS1.xml  -o c:\temp\data
		 * All files in a folder: 
		 * java -jar  AperioXMLParser.jar -ft folder -i d:\projects\data\lumens  -o c:\temp\data	
		 * Or:
		 * java -jar  AperioXMLParser.jar -i d:\projects\data\lumens  -o c:\temp\data	
		 * Or:
		 * java -jar  AperioXMLParser.jar -i d:\projects\data\lumens  -o c:\temp\data	-ft Vessel
		 */
		
		String filename = "d:\\Projects\\Data\\Lumens\\TCGA-02-0003-01A-01-BS1.xml";
		//String filename = "d:\\Projects\\Data\\Lumens\\TCGA-02-0034-01A-01-BS1.xml";
		String inFolder = "d:\\Projects\\Data\\Lumens";
		String targetFolder = "C:\\temp\\data";
		String filterObject ="Vessel";

		
		
		Option help = new Option("h", "help", false, "display this help and exit.");
		help.setRequired(false);
		
		Option folderType = new Option("ft", "folderType", true, "if the input is a file or folder. Default is a folder.");
		help.setRequired(false);
		
		Option input = new Option("i", "inputPath",true, "the path of the file or folder containing files to be converted.");		
		input.setRequired(true);
		input.setArgName("input");
		
		
		Option output = new Option("o", "outputPath", true, "the path of the target folder to store converted file/files.");
		//outputFile.setRequired(true);
		output.setArgName("output");
		
		
		Option filter = new Option("fl", "filterObject", true, "the name of objects to be extracted, e.g, \"Vessel\".");
		output.setArgName("filter");
		
		
		Options options = new Options();
		options.addOption(folderType);
		options.addOption(help);
		options.addOption(input);
		options.addOption(output);	
		options.addOption(filter);	
		
		CommandLineParser CLIparser = new GnuParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine line = null;
		
		try {
			line = CLIparser.parse(options, args);
			if(line.hasOption("h")) {
				formatter.printHelp("AperioXMLParser", options, true);
				System.exit(0);
			}
		} catch(org.apache.commons.cli.ParseException e) {
			formatter.printHelp("AperioXMLParser", options, true);
			System.exit(1);
		}	
		
		long startCurrentTime = 0;
	    long endCurrentTime = 0;
	    long totalTime = 0;
	    startCurrentTime = System.currentTimeMillis();
	    


		
	    inFolder = line.getOptionValue("inputPath");	    	    	    
	    targetFolder = line.getOptionValue("outputPath");	    
	    if (targetFolder == null || "".equals(targetFolder) )   	targetFolder = null;
	    
	    filterObject = line.getOptionValue("filterObject");
	    if (filterObject == null ) filterObject = "Vessel";
	    
	    System.out.println("Input: " + inFolder);
	    System.out.println("output: " + targetFolder);
	    
	    String folderTypeValue = line.getOptionValue("folderType");
	    
		AperioXMLParser parser = new AperioXMLParser(filterObject);
	    
	    if (folderTypeValue == null) parser.batchConvert(inFolder, targetFolder);			
	    else 	    
	    	if (line.getOptionValue("folderType").trim().toLowerCase().equals("file")){
	    		parser.parse(inFolder, targetFolder);
	    	} 
	    	else if(line.getOptionValue("folderType").trim().toLowerCase().equals("folder")  ){
	    		parser.batchConvert(inFolder, targetFolder);			
	    	}
	    
	    endCurrentTime = System.currentTimeMillis();
	    
        totalTime = endCurrentTime - startCurrentTime;
        System.out.println("Total time (seconds):" + totalTime/1000.0);	 	

		//AperioXMLParser loader = new AperioXMLParser(filename);
		//boolean result = loader.parse(filename, targetFolder);
		//boolean result = loader.parse(filename, null);
		

	}

}


