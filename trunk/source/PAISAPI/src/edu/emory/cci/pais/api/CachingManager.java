package edu.emory.cci.pais.api;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import edu.emory.cci.pais.dataloader.db2helper.PAISDBHelper;

public class CachingManager {

	/**
	 * This class tries to manage prepared statements to optimize repeatable queries
	 */
	
	private static final int TEMP_DIR_ATTEMPTS = 10000;
	private static final int ZIP_LEVEL = 9;
	static final int ZIP_BUFFER = 4096;

	
	PAISDBHelper db = null;
	private HashMap <String, PreparedStatement> pstmtMap = null;
	
	public CachingManager(PAISDBHelper db2){
		this.db = db2;
		pstmtMap = new HashMap <String, PreparedStatement>();
	}
	
	
	
	public boolean setPreparedStatement(String queryName, String sql){
		if (checkPreparedStatement(queryName) == true)
			return true;

		PreparedStatement pstmt = db.createPreparedStatement(sql);
		pstmtMap.put(queryName, pstmt);
		return true;
	}
	
	public boolean checkPreparedStatement(String queryName){
		if(pstmtMap.containsKey(queryName)){
			return true;
		}	
		return false;
	}
	
	public PreparedStatement getPreparedStatement (String queryName){
		if(pstmtMap.containsKey(queryName)){
			return pstmtMap.get(queryName);
		}	
		return null;
	}
	
	/** List of parameters has to follow the sequence of prepared statement parameters */
	public boolean setParams(String queryName, Properties props){
		if (props == null) return true;
		PreparedStatement pstmt = getPreparedStatement(queryName);
		try {		
			ParameterMetaData meta = pstmt.getParameterMetaData(); 
			int paramCount = meta.getParameterCount();
			int propSize = props.size();
			if( propSize != paramCount) return false;
			for (int i = 0; i < propSize; i++) {
				pstmt.setString(i+1, props.getProperty(Integer.toString(i+1)) );
			}	
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}	
		return true;
	}
	
	public PreparedStatement setPreparedStatementAndParams(String queryName, String sql, Properties props){
		setPreparedStatement(queryName, sql);
		if (props != null) setParams(queryName, props);
		return getPreparedStatement(queryName);
	}
	
	
	public static String zipBlobsAsFile(ResultSet rs, String format, boolean overwrite){
		ArrayList<String[]> fileList = cacheBlob (rs, format, overwrite);
		return zipFiles(fileList);
	}
	
	public static OutputStream zipBlobsAsStream(ResultSet rs, String format, boolean overwrite){
		ArrayList<String[]> fileList = cacheBlob (rs, format, overwrite);
		return zipFilesAsStream(fileList);
	}
	
	/**
	 * This function dumps all results with BLOB images into a file system. 
	 * We assume there is a labeling attribute, and a BLOB attribute in the result set.
	 * The result will be cached in a temporary folder. 
	 * @param rs      Result set
	 * @param format  Image format
	 * @return
	 */
	public static ArrayList<String[]> cacheBlob (ResultSet rs, String format, boolean overwrite){
		
		ArrayList <String[]> list = new ArrayList <String[]>();
		File tempDir = createTempDir();
		String fileName = null;
		int seqnum = 0;
		try {	
		while (rs.next()){		
				seqnum++;
				String label = rs.getString(1);
				Blob blob = rs.getBlob(2);
				byte[] array = blob.getBytes(1, (int)blob.length());				
				File labelDir = new File(tempDir.getAbsolutePath(), label);
				if (! labelDir.exists() ) {
					labelDir.mkdir();
				}
				fileName = labelDir.getAbsolutePath() + File.separator + seqnum + "." +  format ;
				File child = new File(fileName);
				if (!child.exists()) {
					FileOutputStream out = new FileOutputStream (child);
					out.write(array);
					out.close();
				}
				
				String[] item = new String[2];
				item[0] = label;
				item[1]= fileName;
				list.add(item);			
		}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
		//System.out.println("Records: " + seqnum + " list size: " +  list.size());
		return list;
	}
	
	
	
	
	/**
	 * Atomically creates a new directory somewhere beneath the system's
	 * temporary directory (as defined by the {@code java.io.tmpdir} system
	 * property), and returns its name.

	 * @return the newly-created directory
	 * @throws IllegalStateException if the directory could not be created
	 */
	private static File createTempDir() {
		File baseDir = new File(System.getProperty("java.io.tmpdir"));
		String baseName = "paisAPI-TempStorage" + "-";

		for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
			File tempDir = new File(baseDir, baseName + counter);
			if (!tempDir.exists()) {
				if (tempDir.mkdir())
				  return tempDir;
			}
			else
				return tempDir;
		}
		throw new IllegalStateException("Failed to create directory within "
				+ TEMP_DIR_ATTEMPTS + " attempts (tried "
				+ baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
	}



	
	public static String zipFiles(ArrayList<String[]> list){
		String [] sampleEntry = list.get(0);
		String samplePath = sampleEntry[1];
		int lastIdx = samplePath.lastIndexOf(File.separator);
		String leafPath = samplePath.substring(0, lastIdx);
		String outputFileName = new File(leafPath).getParentFile().getAbsolutePath() + File.separator + "download.zip";
		ZipOutputStream out = null;
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new 
			FileOutputStream(outputFileName);
			out = new ZipOutputStream(new 
					BufferedOutputStream(dest));
			//out.setMethod(ZipOutputStream.DEFLATED);
			byte data[] = new byte[ZIP_BUFFER];

			out.setLevel(ZIP_LEVEL);
			for (int i = 0; i < list.size(); i++){
				String enntryPath = list.get(i)[1];
				File tempFile = new File(enntryPath);
//				String entryName = tempFile.getParentFile().getName() + File.separator + tempFile.getName() ;
				String entryName = tempFile.getAbsolutePath();
				FileInputStream fi = new FileInputStream(entryName);
				origin = new 
				BufferedInputStream(fi, ZIP_BUFFER);
				ZipEntry entry = new ZipEntry(entryName);
				out.putNextEntry(entry);
				int count;
				while((count = origin.read(data, 0, 
						ZIP_BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return outputFileName;

	}
	
	
	public static OutputStream zipFilesAsStream(ArrayList<String[]> list){
		String [] sampleEntry = list.get(0);
		String samplePath = sampleEntry[1];
		int lastIdx = samplePath.lastIndexOf(File.separator);
		String leafPath = samplePath.substring(0, lastIdx);
		String outputFileName = new File(leafPath).getParentFile().getAbsolutePath() + File.separator + "download.zip";
		ZipOutputStream out = null;
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new 
			FileOutputStream(outputFileName);
			out = new ZipOutputStream(new 
					BufferedOutputStream(dest));
			//out.setMethod(ZipOutputStream.DEFLATED);
			byte data[] = new byte[ZIP_BUFFER];

			out.setLevel(ZIP_LEVEL);
			for (int i = 0; i < list.size(); i++){
				String enntryPath = list.get(i)[1];
				File tempFile = new File(enntryPath);
//				String entryName = tempFile.getParentFile().getName() + File.separator + tempFile.getName() ;
				String entryName = tempFile.getAbsolutePath();
				FileInputStream fi = new FileInputStream(entryName);
				origin = new 
				BufferedInputStream(fi, ZIP_BUFFER);
				ZipEntry entry = new ZipEntry(entryName);
				out.putNextEntry(entry);
				int count;
				while((count = origin.read(data, 0, 
						ZIP_BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
			//out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return out;

	}

}
