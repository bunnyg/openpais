

/* Validate boundaries and output wrong boundaries
Boundaries are passed from a file represented as markup_uid; boundarypoints. e.g.,: 1; 110,120 110,140 130,140 130,120 110,120
A session_uid is to identify current dataset. 

The program needs tables and stored procedure to be created first in a DB2 database (with spatial extender).
To run the scripts:
db2 -tf table_spatialvalidation.sql
db2 -td@ -f migrateplgn.sql

The file is parsed and inserted into GIS.BOUNDARY table first. Then a stored procedure is called to convert boundary into spatial types, 
and failed boundaries are kept in a table GIS.GEOMFAIL. The content is then retrieved and returned as a file. 

The program needs to be first initialized with database info:
BoundaryValidator validator = new BoundaryValidator("europa.cci.emory.edu","50000", "db2user", "xxxxx", "devdb");
To validate a batch of boundaries,  e.g.:
  validator.validate("1", inputFile, outputFile);
To validate a single boundary, e.g.:
  String result =validator.validateOne("1", "10", "1,2 2,2  2,3  3,2 3,2 1,2");
To clean up backend tables:      
		validator.cleanup();
* */


package edu.emory.cci.pais.dataloader.boundaryvalidator;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.io.*;
import java.util.*;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import edu.emory.cci.pais.dataloader.db2helper.DBConfig;
import edu.emory.cci.pais.dataloader.db2helper.PAISDBHelper;



/**
* @author Fusheng Wang
* Center for Comprehensive Informatics, Emory University
* @version 1.1
*/
public class PAISBoundaryValidator {

	private PAISDBHelper db = null;
	private static String cleanDocSQL = "DELETE FROM GIS.TEMP_DOC";
	private static String cleanBoundarySQL = "DELETE FROM GIS.TEMP_BOUNDARY";


	private static String insertBoundarySQL = "INSERT INTO GIS.BOUNDARY(SESSION_UID, MARKUP_UID, BOUNDARY) VALUES(?, ?, ?)";	
	private static String[] cleanupSQLs = {"delete from gis.geomfail","delete from GIS.BOUNDARY", "delete from GIS.BOUNDARY_POLYGON"};

	private static String insertLargeBoundarySQL = "INSERT INTO GIS.LARGEBOUNDARY(SESSION_UID, MARKUP_UID, BOUNDARY,  DB2GSE_BOUNDARY) VALUES(?, ?, ?, ?)";	
	private static String[] cleanupLargeSQLs = {"delete from gis.largegeomfail","delete from GIS.LARGEBOUNDARY", "delete from GIS.LARGEBOUNDARY_POLYGON"};

	//private static String getErrorBoundariesSQL = "SELECT * FROM GIS.GEOMFAIL";

	private static String getErrorBoundariesSQL = "SELECT * FROM GIS.GEOMFAIL WHERE SESSION_UID = ? ";
	private static String getErrorBoundarySQL = "SELECT * FROM GIS.GEOMFAIL WHERE SESSION_UID = ? and MARKUP_UID = ? ";

	private static String getErrorLargeBoundariesSQL = "SELECT * FROM GIS.LARGEGEOMFAIL WHERE SESSION_UID = ? ";
	private static String getErrorLargeBoundarySQL = "SELECT * FROM GIS.LARGEGEOMFAIL WHERE SESSION_UID = ? and MARKUP_UID = ? ";	

	public PAISBoundaryValidator(PAISDBHelper db) {
		this.db = db;
	}


	/** 	
	 * @param host Database hostname or IP 
	 * @param port Database port number. Default is 50000
	 * @param username Database login name. Default is db2user
	 * @param passwd  Database passwd
	 * @param database Database name. Default is devdb
	 */
	public PAISBoundaryValidator (String host, String port, String username, String passwd, String database) {
		PAISDBHelper db = new PAISDBHelper(host, port, username, passwd, database);
		this.db = db;
	}


	public PAISBoundaryValidator (String file) {
		PAISDBHelper db = new PAISDBHelper(new DBConfig(new File( file )));
		this.db = db;
	}	

	/** This function validates a batch of boundaries stored in an input file, and return error messages in an output file. 
	 * This function supports quick validation of small boundaries ( <30K in size).
	 * @param session_uid A unique string to identify the current dataset to be validated
	 * @param inputFile A file contains one or multiple lines of markup_uid (a string to identify a boundary), ";" and boundary (x,y x,y...)
	 * @param outputFile
	 * @return
	 */	
	public boolean validate(String session_uid, String inputFile, String outputFile){
		boolean result = true; 
		result  = result && batchInsertBoundary(session_uid, inputFile);
		result  = result && convertBoundary2Polygon(session_uid);
		result  = result && getErrorBoundaries(session_uid, outputFile);
		return result;
	}


	/** This function validates a batch of boundaries stored in an input file, and return error messages in an output file. 
	 * This function supports validation of large boundaries.
	 * @param session_uid A unique string to identify the current dataset to be validated
	 * @param inputFile A file contains one or multiple lines of markup_uid (a string to identify a boundary), ";" and boundary (x,y x,y...)
	 * @param outputFile
	 * @return
	 */	
	public boolean validateLarge(String session_uid, String inputFile, String outputFile){
		boolean result = true; 
		result  = result && batchInsertBoundary(session_uid, inputFile, true);
		result  = result && convertBoundary2Polygon(session_uid, true);
		result  = result && getErrorBoundaries(session_uid, outputFile, true);
		return result;
	}	

	/** This function validates a batch of boundaries as an array of string, and return error messages in a string.  
	 * This function supports validation of large boundaries.
	 * @param session_uid A unique string to identify the current dataset to be validated
	 * @param input String containing multiple large boundaries. Each record should be terminated by '\n'.  
	 * @return Error string
	 */
	public String validateLargeArray(String session_uid, String[] input){
		boolean result = true; 
		result  = result && batchInsertBoundaryString(session_uid, input);
		result  = result && convertBoundary2Polygon(session_uid, true);
		if ( result == false) return null;
		return getErrorBoundariesString(session_uid, true);
	}		
	
	/** This function validates a batch of boundaries as string, and return error messages in a string.  
	 * This function supports validation of large boundaries.
	 * @param session_uid A unique string to identify the current dataset to be validated
	 * @param input String containing multiple large boundaries. Each record should be terminated by '\n'.  
	 * @return Error string
	 */
	public String validateLargeString(String session_uid, String input){
		boolean result = true; 
		result  = result && batchInsertBoundaryString(session_uid, input);
		result  = result && convertBoundary2Polygon(session_uid, true);
		if ( result == false) return null;
		return getErrorBoundariesString(session_uid, true);
	}			

	/**
	 * This function validates a single boundary (size not exceeding 30K). 	
	 * @param session_uid A unique string to identify the current dataset to be validated
	 * @param markup_uid A unique string to identify a boundary
	 * @param boundary (x,y x,y...) based representation of a boundary
	 * @return Invalid boundary, in the format of "markup_uid; SQLSTATE; boundary". SQLSTATE is an error code linking to an error message.
	 */
	public String validateOne(String session_uid, String markup_uid, String boundary){
		boolean result = true; 
		result  = result && insertBoundary(session_uid, markup_uid, boundary);
		result  = result && convertBoundary2Polygon(session_uid);
		if (result != false) return getErrorBoundary(session_uid, markup_uid);
		else return null;
	}

	/**
	 * 	This function validates a single boundary (size could be larger than 30K). 	
	 * @param session_uid A unique string to identify the current dataset to be validated
	 * @param markup_uid A unique string to identify a boundary
	 * @param boundary (x,y x,y...) based representation of a boundary. Boundary can also be db2gse like representation. 
	 * @return Invalid boundary, in the format of "markup_uid; SQLSTATE; boundary". SQLSTATE is an error code linking to an error message.
	 */	
	public String validateLargeOne(String session_uid, String markup_uid, String boundary){
		boolean result = true; 
		result  = result && insertBoundary(session_uid, markup_uid, boundary, true);
		result  = result && convertBoundary2Polygon(session_uid, true);
		if (result != false) return getErrorBoundary(session_uid, markup_uid, true);
		else return null;
	}	
	
	/** 
	 * Clean up temporary result in tables. This is for small boundaries (< 30K).
	 * @return true if successful. 
	 */
	public boolean cleanup(){
		Statement stmt = db.getStatement();
		try{
			for (int i = 0; i < cleanupSQLs.length; i++){
				stmt.execute(cleanupSQLs[i]);
			}
			db.commit();

		} catch (SQLException e) {
			//e.printStackTrace();
			System.out.println("Failed to cleanup tables.");
			return false;			
		}						   
		return true;
	}

	/** 
	 * Clean up temporary result in tables. This supports both large boundary and small boundary tables
	 * @return true if successful. 
	 */	
	public boolean cleanup(boolean isLarge){
		Statement stmt = db.getStatement();
		String [] sqls = null;
		if (isLarge) sqls = cleanupLargeSQLs;
		else sqls = cleanupSQLs;
		
		try{
			for (int i = 0; i < sqls.length; i++){
				stmt.execute(sqls[i]);
			}
			db.commit();

		} catch (SQLException e) {
			//e.printStackTrace();
			System.out.println("Failed to cleanup tables.");
			return false;			
		}						   
		return true;
	}	

	private boolean getErrorBoundaries(String session_uid, String resultFile, boolean isLarge){
		FileWriter out = null; 
		String boundary = null;
		String sqlState = null;
		String markup_uid = null;
		String sql = getErrorBoundariesSQL;
		if (isLarge ) sql = getErrorLargeBoundariesSQL;

		try {
			try {
				out = new FileWriter( new File(resultFile)  );
			} catch (Exception e1) {
				System.out.println("Open file error.");
				e1.printStackTrace();
				return false;
			}

			PreparedStatement pstmt = db.getDBConnection().prepareStatement(sql);
			pstmt.setString(1, session_uid);			
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()){
				markup_uid = rs.getString("markup_uid");
				sqlState = rs.getString("sqlstate");
				boundary = rs.getString("boundary");
				String record = markup_uid + "; " + sqlState + "; " + boundary;
				//System.out.println(record);
				out.write(record  +"\n");
			}		 		
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (out != null )
			try {
				out.close();
			} catch (IOException e) {				
				e.printStackTrace();
			}
			return true;
	}

	//Get error boundaries as string
	private String getErrorBoundariesString(String session_uid, boolean isLarge){
		StringWriter out = null; 
		String boundary = null;
		String sqlState = null;
		String markup_uid = null;
		String sql = getErrorBoundariesSQL;
		if (isLarge ) sql = getErrorLargeBoundariesSQL;

		try {
			try {
				out = new StringWriter();
			} catch (Exception e1) {
				System.out.println("Open file error.");
				e1.printStackTrace();
				return null;
			}

			PreparedStatement pstmt = db.getDBConnection().prepareStatement(sql);
			pstmt.setString(1, session_uid);			
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()){
				markup_uid = rs.getString("markup_uid");
				sqlState = rs.getString("sqlstate");
				boundary = rs.getString("boundary");
				String record = markup_uid + "; " + sqlState + "; " + boundary;
				//System.out.println(record);
				out.write(record  +"\n");
			}		 		
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (out != null )
			try {
				out.close();
			} catch (IOException e) {				
				e.printStackTrace();
			}
			return out.toString();
	}	

	private boolean getErrorBoundaries(String session_uid, String resultFile){
		return getErrorBoundaries(session_uid, resultFile,  false);
	}

	private String getErrorBoundary(String session_uid, String markup_uid){
		return getErrorBoundary(session_uid, markup_uid, false);
	}	

	private String getErrorBoundary(String session_uid, String markup_uid, boolean isLarge){
		try {
			String boundary = null;
			String sqlState = null;
			String sql = getErrorBoundarySQL;
			if (isLarge ) sql = getErrorLargeBoundarySQL;
			PreparedStatement pstmt = db.getDBConnection().prepareStatement(sql);
			pstmt.setString(1, session_uid);
			pstmt.setString(2, markup_uid);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				markup_uid = rs.getString("markup_uid");
				sqlState = rs.getString("sqlstate");
				boundary = rs.getString("boundary");
				return markup_uid + "; " + sqlState + "; " + boundary;
			}		 		
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	



	/** Call stored procedure to convert points to polygon objects */	
	private boolean convertBoundary2Polygon(String session_uid, boolean isLarge){
		CallableStatement cstmt = null; 
		Connection con = db.getDBConnection();
		String sql = "CALL gis.plgncnvt(?,?)";
		if (isLarge ) sql = "CALL gis.migrateplgn(?,?)" ;
		String result = null;
		try {			
			cstmt = con.prepareCall(sql);
			cstmt.setString(1, session_uid);
			cstmt.registerOutParameter (2, Types.VARCHAR);  
			cstmt.executeUpdate();
			result = cstmt.getString(2);
			cstmt.close();
		} catch (SQLException e) {
			//e.printStackTrace();
			System.out.println("Failed to convert points to polygons. ");
			return false;			
		}						   
		return true;
	}

	private boolean convertBoundary2Polygon(String session_uid){
		return convertBoundary2Polygon(session_uid, false);
	}

	private boolean batchInsertBoundary(String session_uid, String filename, boolean isLarge){
		String markup_uid;
		String boundary;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filename));
			String strLine = "";
			StringTokenizer st = null;

			while ((strLine = br.readLine()) != null) {
				st = new StringTokenizer(strLine, ";");
				markup_uid = st.nextToken(";").trim();
				boundary = st.nextToken(";").trim();
				insertBoundary(session_uid, markup_uid, boundary, isLarge);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return true;
	}

	//Insert a string of multiple large boundaries 
	private boolean batchInsertBoundaryString(String session_uid, String input){
		String markup_uid;
		String boundary;
		BufferedReader br;
		try {
			br = new BufferedReader( new StringReader(input) );
			String strLine = "";
			StringTokenizer st = null;

			while ((strLine = br.readLine()) != null) {
				st = new StringTokenizer(strLine, ";");
				markup_uid = st.nextToken(";").trim();
				boundary = st.nextToken(";").trim();
				insertBoundary(session_uid, markup_uid, boundary, true);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return true;
	}	
	
	//Insert an array of string of multiple large boundaries 
	private boolean batchInsertBoundaryString(String session_uid, String[] input){
		String markup_uid;
		String boundary;
		StringTokenizer st = null;
		for (int i = 0; i < input.length; i ++){
			st = new StringTokenizer(input[i], ";");
			markup_uid = st.nextToken(";").trim();
			boundary = st.nextToken(";").trim();
			insertBoundary(session_uid, markup_uid, boundary, true);
		}
		return true;
	}		

	private boolean batchInsertBoundary(String session_uid, String filename){
		return batchInsertBoundary(session_uid, filename, false);
	}


	private boolean insertBoundary(String session_uid, String markup_uid, String boundary, boolean isLarge){		
		PreparedStatement pstmt = null; 
		Connection con = null; 
		String sql = insertBoundarySQL;
		if ( isLarge ) sql = insertLargeBoundarySQL;

		try {
			con = db.getDBConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, session_uid);	
			pstmt.setString(2, markup_uid.trim());
			pstmt.setString(3, boundary.trim() );
			String representation = "svg";
			if ( boundary.indexOf('(') != -1 ) representation = "db2gse";
			if ( isLarge ) {				
				pstmt.setString(4, GISHelper.convert(boundary, representation));
			}
			pstmt.executeUpdate();	
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (pstmt != null) pstmt.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
				return false;
			}			
		}
		return true;	
	}

	private boolean insertBoundary(String session_uid, String markup_uid, String boundary){		
		return insertBoundary(session_uid, markup_uid, boundary, false);
	}	


	public static void main1(String[] args) {
		//String inputFile ="/tmp/testboundaries.txt";
		//String outputFile ="/tmp/wrongboundaries.txt";
		String inputFile ="c:/temp/testboundaries.txt";
		String outputFile ="c:/temp/wrongboundaries.txt";
		String db2gse ="(110 120, 110 140, 130 140, 130 120, 110 120), (115 125, 115 135, 125 135, 125 135, 115 125)";
		String baddb2gse ="(110 120, 110 140, 130 140, 130 120, 110 120), (115 125, 115 135, 125 135, 125 135, 115 125)";

		String svg = "110,120 110,140 130,140 130,120 110,120";
		String badsvg = "110,120 110,140 130,140 130,120 ";

		PAISBoundaryValidator validator = new PAISBoundaryValidator("europa.cci.emory.edu","50000", "db2user", "userdb1234", "devdb");
		//Test validation for small boundaries
		/*		validator.cleanup();
		validator.validate("1", inputFile, outputFile);
		validator.cleanup();
		String result =validator.validateOne("1", "10", svg);
		System.out.println(result);*/

		//Test validation for large boundaries
		/*		validator.cleanup(true);
		String result =validator.validateLargeOne("1", "11", db2gse);
		System.out.println(result);		
		validator.cleanup(true);
		validator.validateLarge("1", inputFile, outputFile);*/

		//Test large boundaries in string
/*		String input = "100; (110 120, 110 140, 130 140, 130 120, 110 120) \n" + 
		"0; (110 120, 110 140, 130 140, 130 120, 110 120), (115 125, 115 135, 125 135, 125 135, 115 125)\n" +
		"1; 110,120 110,140 130,140 130,120 110,120\n" +
		"2; 110,120 110,140 130,140 130,120\n" +
		"3; 110,120 110,140 130,140  \n" +
		"4; 110 120, 110 140, 130 140, 130 120, 110 120";
		validator.cleanup(true);
		String error = validator.validateLargeString("testdataset", input);
		System.out.append(error);*/
		
		//Test large boundaries as an array of string
		String[] input = {"100; (110 120, 110 140, 130 140, 130 120, 110 120)", 
		"0; (110 120, 110 140, 130 140, 130 120, 110 120), (115 125, 115 135, 125 135, 125 135, 115 125)",
		"1; 110,120 110,140 130,140 130,120 110,120",
		"2; 110,120 110,140 130,140 130,120",
		"3; 110,120 110,140 130,140",
		"4; 110 120, 110 140, 130 140, 130 120, 110 120"};
		
		validator.cleanup(true);
		//String error = validator.validateLargeArray("testdataset", input);
		String b = "110,120 110,140 130,140 130,120";
		String error = validator.validateOne("1", "10", b);
		System.out.append(error);
	}




public static void main(String[] args) {
	
	/*
	 java -jar PAISBoundaryValidator -ls -i in.txt -o out.txt
	 java -jar PAISBoundaryValidator     -i in.txt -o out.txt
	 java -jar PAISBoundaryValidator -txt "text"	
	*/
	
	Option help = new Option("h", "help", false, "display this help and exit.");
	help.setRequired(false);
	Option isLargeBoundary = new Option("ls", "largeboudary", false, "the size of the boundaries to be validated. It can be either 'small' or 'large'. ");
	//folderType.setRequired(true);


	Option inputFile = new Option("i", "inputFile", true, "file with the matlab results or a folder with text files with matlab results. Sub-folders are processed recursively.");
	//inputFile.setRequired(true);
	inputFile.setArgName("inputFile");
	
	Option outputFile = new Option("o", "outputFile", true, "result file with the generated PAIS document. If the extension is \".zip\", the file will be compressed. If the input File is a folder, " +
			"this output parameter will be treated as a folder and the results will be stored here.");
	//outputFile.setRequired(true);
	outputFile.setArgName("outputFile");

	
	
	Option dbConfigFile = new Option("dbc", "dbConfigFile", true, "xml file with the configuration of the database.");
	dbConfigFile.setRequired(true);
	dbConfigFile.setArgName("dbConfigFile");
	
	Option txt = new Option("txt", "boundaryString", true, "boundary string to be parsed. e.g.: \"1,2 2,2  2,3  3,2 3,2 1,2\" ");
	//inputFile.setRequired(true);
	inputFile.setArgName("inputFile");
	
	
	
	Options options = new Options();
	options.addOption(help);
	options.addOption(isLargeBoundary);
	options.addOption(inputFile);
	options.addOption(outputFile);	
	options.addOption(dbConfigFile);
	options.addOption(txt);
	
	CommandLineParser CLIparser = new GnuParser();
	HelpFormatter formatter = new HelpFormatter();
	CommandLine line = null;
	
	
	try {
		line = CLIparser.parse(options, args);
		if(line.hasOption("h")) {
			formatter.printHelp("PAISBoundaryValidator", options, true);
			System.exit(0);
		}
	} catch(org.apache.commons.cli.ParseException e) {
		formatter.printHelp("PAISBoundaryValidator", options, true);
		System.exit(1);
	}	
	
	long startCurrentTime = 0;
    long endCurrentTime = 0;
    long totalTime = 0;
    startCurrentTime = System.currentTimeMillis();
    	
    String dbFile = line.getOptionValue("dbConfigFile");
	PAISBoundaryValidator validator = new PAISBoundaryValidator(dbFile);
	
	//cleanup db
	
	boolean isLarge = false; 
	if (line.hasOption("ls") ) {
		isLarge = true;
	 	validator.cleanup(isLarge);
	}
	else 
		validator.cleanup(isLarge);
	
	
	//Single boundary?
	if (line.hasOption("txt") ) {	
		String boundaryTxt = line.getOptionValue("txt").trim();
		System.out.println(boundaryTxt);
		String result =validator.validateOne("1", "1", boundaryTxt);
		System.out.println(result);		
	} else
	{
		String fileinput =  line.getOptionValue("i").trim();
		String fileout = line.getOptionValue("o").trim();
		if (isLarge)
			validator.validateLarge("1", fileinput, fileout);
		else 
			validator.validate("1", fileinput, fileout);
	}	
	
	endCurrentTime = System.currentTimeMillis();
	totalTime = endCurrentTime - startCurrentTime;
	System.out.println("Total time (seconds):" + totalTime/1000.0);	 
	
	
 }




}




//D:\Projects\PAIS\PAISBoundaryValidator\target>java -jar PAISBoundaryValidator.jar  -dbc D:\Projects\PAIS\PAISBoundaryValidator\conf\dbconfig.xml  -txt "110,120 110,140 130,140 130,120 110,120"
