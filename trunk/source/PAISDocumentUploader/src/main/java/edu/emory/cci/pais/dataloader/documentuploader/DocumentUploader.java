package edu.emory.cci.pais.dataloader.documentuploader;


import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import edu.emory.cci.pais.dataloader.db2helper.DBConfig;
import edu.emory.cci.pais.dataloader.db2helper.PAISDBHelper;


/**
 * 
 * @author Fusheng Wang
 * 
 * This class uploads a set of zipped XML to the database. 
 * Initially, all uploaded document records will have an INCOMPLETE status, also an INSERTIME. 
 */


public class DocumentUploader {	

	private PAISDBHelper db = null;
	//GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE), 
	 
	private static String preparedDocUploadSql =  "INSERT INTO PAIS.STAGINGDOC(BLOB, STATUS, INSERT_TIME, FILE_NAME) VALUES (?, 'INCOMPLETE', CURRENT_TIMESTAMP, ?) " ;

	public DocumentUploader(PAISDBHelper db) {
		this.db = db;
	}
	
	/** Upload all documents of a set, e.g., a validation set. Assume documents are stored in a root folder, which 
	 * contains folders for multiple PAIS documents. e.g., root folder "/GBMvalidation/SR20_AR20_NS-MORPH_Sq01", with subfolders
	 * astroII.1, ..., oligoIII.2.ndpi... */
	public boolean uploadDataSet(String rootPath){
		File rootDir = new File(rootPath);
		FileFilter fileFilter = new FileFilter() { public boolean accept(File file) { return file.isDirectory(); } }; 
		File[] childDirs = rootDir.listFiles(fileFilter); 
		boolean success = true;
		if (childDirs == null){
			System.out.println("No folder to upload.");
			return false;
		}
		for (int i = 0; i < childDirs.length; i++){
			String file = childDirs[i].getAbsolutePath();
			success = success && batchDocUpload(file);			
		}
		if (success == true) return true;
		else {
			System.out.println("Data set loaded with errors.");
			return false;
		}
	}
	
	/** Batch upload zip files in a folder */
	public boolean batchDocUpload(String rootPath){
		File folderFile = new File(rootPath);
		FilenameFilter zipOnly = new OnlyExt("zip");
		String files[] = folderFile.list(zipOnly);
		int failedCount = 0;
		for (int i = 0; i < files.length; i++){
			String file = files[i];
			String zipFilePath = rootPath + File.separatorChar + file;
			boolean result = docUpload(zipFilePath);
			if (result == false) failedCount++;
			System.out.println("File " + file + "  upload complete.");
		}
		return true;	
	}
	
	/** Submit zip based XML to the database as BLOB. */
	public boolean docUpload(String zipFilePath){
		PreparedStatement pstmt = null; 
		File blobFile = new File(zipFilePath);
		String sql = preparedDocUploadSql;
		//INSERT INTO PAIS.STAGINGDOC(BLOB, STATUS, INSERT_TIME) VALUES (?, 'INCOMPLETE', CURRENT_TIMESTAMP) 
		try {
			pstmt = db.getDBConnection().prepareStatement(sql);
			InputStream inBlob = new FileInputStream(blobFile);	
			pstmt.setBinaryStream(1, inBlob, inBlob.available());
			pstmt.setString(2, zipFilePath);
			pstmt.executeUpdate();	
			pstmt.close();
			inBlob.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (pstmt != null) pstmt.close();
			} catch (SQLException e1) {
			}
			return false;
		}		
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
		Option help = new Option("h", "help", false, "display this help and exit.");
		help.setRequired(false);
		Option folderType = new Option("ft", "folderType", true, "the type of the Folder. It can be either 'slide' or 'collection'. " +
				"If it is 'slide', all the zip files contained in the folder will be loaded. If it is 'collection', all the zip " +
				"files contained in the immediate child subfolders will be uploaded, files contained directly in the folder will be ignored. " +
				"Notice that both methods don't add zip files in subfolders recursively.");
		folderType.setRequired(true);
		folderType.setArgName("folderType");
		Option folder = new Option("f", "folder", true, "in the case 'slide', folder containing the zipped documents. In the case of 'collection' " +
				"it's the folder containing the subfolders with zipped documents.");
		folder.setRequired(true);
		folder.setArgName("folder");
		Option dbConfigFile = new Option("dbc", "dbConfigFile", true, "xml file with the configuration of the database.");
		dbConfigFile.setRequired(true);
		dbConfigFile.setArgName("dbConfigFile");
		
		Options options = new Options();
		options.addOption(help);
		options.addOption(folderType);
		options.addOption(folder);
		options.addOption(dbConfigFile);
		
		CommandLineParser CLIparser = new GnuParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine line = null;
		try {
			line = CLIparser.parse(options, args);
			if(line.hasOption("h")) {
				formatter.printHelp("PAISDocumentUploader", options, true);
				System.exit(0);
			}
		} catch(org.apache.commons.cli.ParseException e) {
			formatter.printHelp("PAISDocumentUploader", options, true);
			System.exit(1);
		}	
		
		long startCurrentTime = 0;
        long endCurrentTime = 0;
        long totalTime = 0;
        startCurrentTime = System.currentTimeMillis();
        
		PAISDBHelper db = new PAISDBHelper(new DBConfig(new File(line.getOptionValue("dbConfigFile"))));
        
		DocumentUploader uploader = new DocumentUploader(db);

		String folderString = line.getOptionValue("folder");
		if (line.getOptionValue("folderType").trim().toLowerCase().equals("slide")){
			uploader.batchDocUpload(folderString);
		} 
		else if(line.getOptionValue("folderType").trim().toLowerCase().equals("collection")){
			uploader.uploadDataSet(folderString);				
		}
		else {
			formatter.printHelp("PAISDocumentUploader", options, true);
			System.exit(1);
		}

		endCurrentTime = System.currentTimeMillis();
        totalTime = endCurrentTime - startCurrentTime;
        System.out.println("Total time (seconds):" + totalTime/1000.0);	 	
	}
}
	
