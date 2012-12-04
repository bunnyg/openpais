package edu.emory.cci.pais.api;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import edu.emory.cci.pais.dataloader.db2helper.PAISDBHelper;

/**
* @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
* @version 1.0 

*/



public class APIHelper {
	
	
	
	static String SVG_STYLE ="stroke:lime;stroke-width:1";
	
/*	public APIHelper() {
		if (db == null ) 
			db =  new PAISDBHelper(host, port, username, passwd, database);
	}
*/
	
	public static String fixPath(String backslashPath){
		return backslashPath.replace('\\', File.separatorChar);
	}
	
	public static String getContentType(String format){
		if (format.equalsIgnoreCase("jpg") ||format.equalsIgnoreCase("jpeg") )  
			return "image/jpeg";
		if (format.equalsIgnoreCase("png") )  
			return "image/png";
		if (format.equalsIgnoreCase("tif") ||format.equalsIgnoreCase("tiff") )  
			return "image/tiff";
		if (format.equalsIgnoreCase("gif") )  
			return "image/gif";
		if (format.equalsIgnoreCase("svg") )  
			return "image/svg+xml";
		if (format.equalsIgnoreCase("html") )  
			return "text/html";
		if (format.equalsIgnoreCase("xml") )  
			return "text/xml";
		if (format.equalsIgnoreCase("text") )  
			return "text/plain";
		if (format.equalsIgnoreCase("json") )  
			return "application/json";
		if (format.equalsIgnoreCase("zip") )  
			return "application/zip";
		if (format.equalsIgnoreCase("eps") )  
			return "application/eps";
		return "text/html";
	}
	
	
	
	public static String [] getSingleRecordFromResultSet(ResultSet rs){
		String[] rst = null;
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();
			rst = new String[numberOfColumns];
			if ( rs != null && rs.next() ){
				for (int i = 0; i <numberOfColumns; i++ ){
					rst[i] = rs.getString(i + 1);
					//System.out.println(rst[i]);
				}
			    rs.close();	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rst;
	}
	
	
	public static String getSingleValueFromResultSet(ResultSet rs){
		String rst = null;
		try {
			rs.next();
			rst = rs.getString(1);
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return rst;
		}
		return rst;
	}
	
	public static ResultSet getResultSet(PAISDBHelper db, String query){
		ResultSet rs = null; 
		try {
			rs = db.getSqlQueryResult(query);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return rs;
	}
	
	public static ResultSet getResultSetFromPreparedStatement(PreparedStatement pstmt){
		ResultSet rs = null; 
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return rs;
	}
	
	
	public static Blob getBlobFromPreparedStatement(PreparedStatement pstmt){
		ResultSet rs = getResultSetFromPreparedStatement(pstmt);
		Blob blob = null;
		try {
			rs.next();
			blob = rs.getBlob(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return blob;
	}
	
	
	public  static String resultSetValuePair2xml(ResultSet rs){
	     StringBuffer rstStrBuf = new StringBuffer();
	     
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
		     int numberOfColumns = rsmd.getColumnCount();
		     //System.out.println(numberOfColumns);
		     String[] colNames = new String [numberOfColumns];
		     
		     for (int i = 0 ; i < numberOfColumns; i++){
		    	 colNames [i] = rsmd.getColumnName(i+1);		    	 
		     }
		     //<row>
		     //<col name ="" value ="" />
		     //</row>
		     
		     rstStrBuf.append("<result>\n");
		     while (rs.next() ){
		    	 rstStrBuf.append("<row>");
			     for (int i = 0; i< numberOfColumns; i++){
			    	 rstStrBuf.append( "<column name=\"" + colNames[i] + "\" value=\""  + 
			    			 rs.getString(i+1) + "\" />");			    	 
			     }			    
			     rstStrBuf.append("</row>\n");
		     }
		     rstStrBuf.append("</result>\n");
		     
		     
			
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}
		
		return rstStrBuf.toString();
		
	}
	
	
	public  static String resultSet2xml(ResultSet rs){
	     StringBuffer rstStrBuf = new StringBuffer();
	     
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
		     int numberOfColumns = rsmd.getColumnCount();
		     String[] colNames = new String [numberOfColumns];
		     
		     for (int i = 0 ; i < numberOfColumns; i++){
		    	 colNames [i] = rsmd.getColumnName(i+1);		    	 
		     }
		     
		     rstStrBuf.append("<result>\n");
		     while (rs.next() ){
		    	 rstStrBuf.append("<row ");
			     for (int i = 0; i< numberOfColumns; i++){
			    	 rstStrBuf.append(colNames[i] + "=\""  + 
			    			 rs.getString(i+1) + "\" "); 
			     }			    
			     rstStrBuf.append(" />\n");
		     }
		     rstStrBuf.append("</result>\n");
		     
			
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}
		
		return rstStrBuf.toString();
		
	}	
	
	public  static String resultSet2svg(ResultSet rs, int x0, int y0, int samplingRate, String style){
	     StringBuffer rstStrBuf = new StringBuffer();
	     
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
		     int numberOfColumns = rsmd.getColumnCount();
		     String[] colNames = new String [numberOfColumns];
		     
		     for (int i = 0 ; i < numberOfColumns; i++){
		    	 colNames [i] = rsmd.getColumnName(i+1);		    	 
		     }
		     
		     rstStrBuf.append("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n");

		     while (rs.next() ){
			     for (int i = 0; i< numberOfColumns; i++){
			    	 String str = DataHelper.normalizeCoords(rs.getString(i+1), x0, y0, samplingRate, "svg");
			    	 rstStrBuf.append( DataHelper.str2svgpolygon(str, style ) ); 
			     }			    
		     }
		     rstStrBuf.append("</svg>\n");
		     
			
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}
		
		return rstStrBuf.toString();
		
	}	
		
	
	public  static double [][] resultSet2matrix(ResultSet rs){
		ArrayList<double[]> list = new ArrayList<double[]>();
		int numberOfColumns = 0; 
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			numberOfColumns = rsmd.getColumnCount();

			while (rs.next() ){
				double [] row = new double [numberOfColumns];

				for (int i = 0; i< numberOfColumns; i++){
					row[i] = rs.getDouble(i+1); 	 
				}	
				list.add(row);
			}
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}
		double [][] rstMatrix = new double [list.size()][numberOfColumns];
		for (int i = 0; i < list.size(); i++){
			for (int j = 0; j < numberOfColumns; j ++ ){
				rstMatrix[i][j] =  list.get(i) [j];
				//System.out.println(rstMatrix[i][j]);
			}
		}

		return rstMatrix;

	}	
	
	public  static String resultSet2html(ResultSet rs){
	     StringBuffer rstStrBuf = new StringBuffer();
	     
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
		     int numberOfColumns = rsmd.getColumnCount();
		     String[] colNames = new String [numberOfColumns];
		     
		     for (int i = 0 ; i < numberOfColumns; i++){
		    	 colNames [i] = rsmd.getColumnName(i+1);		    	 
		     }

		     rstStrBuf.append("<html><body><table border=\"1\" cellspacing=\"0\" cellpadding=\"5\">");
		     if (rs == null ) 
		    	 rstStrBuf.append("<html><body><tr><td>No result.</td></tr></body></html>");
		     
		     rstStrBuf.append("<tr style=\"background-color:#E2F5FE\"> ");
		     for (int i = 0 ; i < numberOfColumns; i++){
		    	 rstStrBuf.append("<th> " + colNames [i] + "</th>"); 		    	 
		     }
		     rstStrBuf.append("</tr> ");
		    	 
		     while (rs.next() ){
		    	 rstStrBuf.append("<tr style=\"background-color:#F9F9F9\"> ");
			     for (int i = 0; i< numberOfColumns; i++){
			    	 rstStrBuf.append("<td> " + rs.getString(i+1) + "</td>"); 
			     }			    
			     rstStrBuf.append("</tr>\n");
		     }
		     rstStrBuf.append("</table></body></html>");	
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}
		
		return rstStrBuf.toString();
		
	}	
	
	
	
	
	public  static String resultSet2csv(ResultSet rs){
	     StringBuffer rstStrBuf = new StringBuffer();
	     
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
		     int numberOfColumns = rsmd.getColumnCount();
		     String[] colNames = new String [numberOfColumns];
		     
		     for (int i = 0 ; i < numberOfColumns; i++){
		    	 colNames [i] = rsmd.getColumnName(i+1);		    	 
		     }

		     for (int i = 0 ; i < numberOfColumns; i++){ 
		    	 if (i == (numberOfColumns - 1 ) ) 
		    		 rstStrBuf.append(colNames [i] + "\n");
		    	 else 
		    	 rstStrBuf.append(colNames [i] + ","); 		    	 
		     }
		    	 
		     while (rs.next() ){
		    	 for (int i = 0; i< numberOfColumns; i++){
		    		 if (i == (numberOfColumns - 1 ) ) 			    	 
		    			 rstStrBuf.append( rs.getString(i+1) + "\n" ); 
		    		 else 
		    			 rstStrBuf.append( rs.getString(i+1) + ",");
		    	 }			    
		     }

		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}	
		return rstStrBuf.toString();
		
	}		
	
	
	
	public static JSONArray resultSet2jsonarray(ResultSet rs){

        JSONArray json = new JSONArray();

        try {
             java.sql.ResultSetMetaData rsmd = rs.getMetaData();
             int numberOfColumns = rsmd.getColumnCount();
		     String[] colNames = new String [numberOfColumns];		
		     
		     for (int i = 0 ; i < numberOfColumns; i++){
		    	 colNames[i] = rsmd.getColumnName(i+1);		    	 
		     }
             while(rs.next()){
 
                 JSONObject obj = new JSONObject();

                 for (int i= 1; i<numberOfColumns + 1; i++) {

                     if(rsmd.getColumnType(i)==java.sql.Types.ARRAY){
                         obj.put(colNames[i-1], rs.getArray(i));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
                         obj.put(colNames[i-1], rs.getInt(i));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
                         obj.put(colNames[i-1], rs.getBoolean(i));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.BLOB){
                         obj.put(colNames[i-1], rs.getBlob(i));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
                         obj.put(colNames[i-1], rs.getDouble(i)); 
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
                         obj.put(colNames[i-1], rs.getFloat(i));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
                         obj.put(colNames[i-1], rs.getInt(i));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.NVARCHAR){
                         obj.put(colNames[i-1], rs.getNString(i));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
                         obj.put(colNames[i-1], rs.getString(i));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
                         obj.put(colNames[i-1], rs.getInt(i));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
                         obj.put(colNames[i-1], rs.getInt(i));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.DATE){
                         obj.put(colNames[i-1], rs.getDate(i));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
                        obj.put(colNames[i-1], rs.getTimestamp(i));   
                     }
                     else{
                         obj.put(colNames[i-1], rs.getObject(i));
                     } 

                    }//end foreach
                 json.put(obj);

             }//end while


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return json;
    }
	
	public static String resultSet2json(ResultSet rs){
		JSONArray array = resultSet2jsonarray(rs);
		StringWriter out = new StringWriter();
		try {
			array.write(out);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String jsonText = out.toString();
		return jsonText;
	}

	
	public static Response getImageFileResponse(String filePath, String format){
		
		File imgFile = new File(filePath); 
		//System.out.println(filePath);
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(imgFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			String errContent = "Image doesn't exist.";
			return Response.ok(errContent).type(MediaType.TEXT_PLAIN).build(); 
		}
		
		ResponseBuilder response = Response.ok((Object) in);
		response.header("Content-type", getContentType(format) );
		if (format.equalsIgnoreCase("tif")){
			String filename = imgFile.getName();
			response.header("Content-Disposition", "attachment; filename=" + filename);
		}
		return response.build();
	}
	
	public static Response getImageStreamResponse(InputStream in, String name, String format ){
		if (name == null || "".equals(name) ) 
			return getImageStreamResponse(in, format );
		ResponseBuilder response = Response.ok((Object) in);
		//response.header("Content-Disposition", "attachment; filename=" + name  +"." + format.toLowerCase());
		response.header("Content-type", getContentType(format) );
		return response.build();
	}
	
	
	public static Response errorResponse(String msg){
		String content = "<html><body>" + msg + "</body></html>";
		 return Response.ok(content).type(MediaType.TEXT_HTML_TYPE).build();
	}
	
	public static Response getImageBlobResponse(Blob blob, String name, String format ){
		InputStream in;
		try {
			in = blob.getBinaryStream();
		} catch (SQLException e) {
			e.printStackTrace();
			return errorResponse("There is a problem on reading a blob.");
		}
		ResponseBuilder response = Response.ok((Object) in);
		response.header("Content-type", getContentType(format) );
		if (format.equalsIgnoreCase("tif")){
			response.header("Content-Disposition", "attachment; filename=" + name);
		}
		return response.build();
	}
	
	
	public static Response getImageStreamResponse(InputStream in, String format ){
		String filename = "image_from_server." + format.toLowerCase();
		if (format.equalsIgnoreCase("jpg")) filename = "image_from_server.jpg";
		if (format.equalsIgnoreCase("gif")) filename = "image_from_server.gif";
		if (format.equalsIgnoreCase("tif")) filename = "image_from_server.tif";
		ResponseBuilder response = Response.ok((Object) in);
		response.header("Content-Disposition", "attachment; filename=" + filename);
		return response.build();
	}
	
	//Not finished yet
	public static Response getImageResponse(InputStream in, String format ){
/*		String filename = "image_from_server." + format.toLowerCase();
		if (format.equalsIgnoreCase("jpg") ||format.equalsIgnoreCase("jpeg") ) { filename = "image_from_server.jpg"; }
		if (format.equalsIgnoreCase("gif")) filename = "image_from_server.gif";
		if (format.equalsIgnoreCase("tif")) filename = "image_from_server.tif";*/
		ResponseBuilder response = Response.ok((Object) in);
		response.header("Content-type", getContentType(format) );
		return response.build();
	}
		
	public static Response setSVGResponse(ResultSet rs, int x, int y, int samplingRate){
		String content = resultSet2svg(rs, x, y, samplingRate, SVG_STYLE);
		ResponseBuilder response = Response.ok(content);
		response.header("Content-type", getContentType("svg") );
		return response.build();
	}
	
	
	public static Response setHistogramResponse(ResultSet rs, String title, String subTitle, int width, int height, String format){
		double [][] histogramMatrix = resultSet2matrix(rs);
		//System.out.println("Size: " + histogramMatrix.length );
		
		if (format.equalsIgnoreCase("PNG") || format.equalsIgnoreCase("JPG") || format.equalsIgnoreCase("JPEG") || format.equalsIgnoreCase("SVG")
				|| format.equalsIgnoreCase("EPS") || format.equalsIgnoreCase("PDF") ){
			File file = FeatureBarChart.getFeatureBarChart(title, subTitle, width, height, histogramMatrix, format);
			//System.out.println(file.getAbsolutePath());
			FileInputStream in;
			try {
				in = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				in = null;
			}
			ResponseBuilder response = Response.ok( (Object) in);
			response.header("Content-type", getContentType(format) );
			return response.build();
		} 
		else {
			return setResponseByFormat(format, rs);
		}
	}
	
	
	public static Response setResponseByFormat(String format, ResultSet rs){
		String content = null;
		if (format == null || "xml".equalsIgnoreCase(format)) {
			content = resultSet2xml(rs);
			Response response = Response.ok(content).type(MediaType.APPLICATION_XML).build();
			return response;
			
		} else if ("json".equalsIgnoreCase(format)) {
			content = resultSet2json(rs);
			return Response.ok(content).type(MediaType.APPLICATION_JSON).build();
			
		} else if ("html".equalsIgnoreCase(format)) {
			content = resultSet2html(rs);
			return Response.ok(content).type(MediaType.TEXT_HTML_TYPE).build();
			
			
		} else if ("csv".equalsIgnoreCase(format)) {
			content = resultSet2csv(rs);
			return Response.ok(content).type(MediaType.TEXT_PLAIN).build();
		} 

		return Response.notAcceptable(Variant.mediaTypes(MediaType.APPLICATION_XML_TYPE,
				MediaType.APPLICATION_JSON_TYPE).add()
				.build()).build();

	}
	


	public static Response getIconImageResponse (ResultSet rs, int rowSize, String imgFormat, String contentType){

		if ("HTML".equalsIgnoreCase(contentType ) )  {
			ArrayList<String[]> list = CachingManager.cacheBlob (rs, imgFormat, true);
			int totalSize = list.size();
			StringBuffer  buf = new StringBuffer("<html><body><table>\n");
			int numOfRows = 1;
			if (totalSize%rowSize == 0  ) numOfRows = totalSize /rowSize;
			else  numOfRows = totalSize /rowSize + 1; 
			//System.out.println("Total images: " + totalSize +  " num of rows: " + numOfRows);

			for (int i = 0; i < numOfRows; i++){
				buf.append("<tr>\n");
				for (int j = 0; j < rowSize; j++) {	
					int curIdx = i * rowSize + j;
					//System.out.println(curIdx + " " + i + " " + j);
					if (curIdx < totalSize){ 
						String filePath = list.get( curIdx ) [1];
						//System.out.println(filePath);
						buf.append("<td>\n");
						buf.append("<img src=\"getImageFromFile;name=" + encodePath(filePath) + ";format=" + imgFormat + "\" />" );
						buf.append("</td>");
					}
					else {
						buf.append("<td /> \n");
					}
				} //end for j
				buf.append("</tr>\n");			
			} //end for i
			buf.append("</table></body></html>\n");		
			ResponseBuilder response = Response.ok(buf.toString());
			response.header("Content-type", getContentType("HTML") );
			return response.build();	

		} else if ("ZIP".equalsIgnoreCase(contentType ) ){
			OutputStream out = CachingManager.zipBlobsAsStream(rs, imgFormat, true);
			ResponseBuilder response = Response.ok(out);
			response.header("Content-type", getContentType("ZIP") );
			return response.build();
		}
		return null;
	}

	public static  String encodePath(String path){
		StringBuffer strb = new StringBuffer();
		for (int i = 0; i < path.length(); i ++ ){
			char c = path.charAt(i);
			int ascii = (int) c; 
			String hex = Integer.toHexString(ascii);
			//int asc = Integer.parseInt(hex, 16);  
			//String aChar =  new Character( (char) ascii).toString() ;
			//System.out.println("i:" + asc +  " h: " + hex + " a: " + aChar);
			strb.append( hex );
		}
		return strb.toString();
	}
	
	
	
	public static  String decodePath(String path){
		StringBuffer strb = new StringBuffer();
		for (int i = 0; i < path.length()/2; i++ ){
			char c1 = path.charAt(i*2);
			char c2 = path.charAt(i*2+1);
			//int ascii =  Integer.parseInt( String.valueOf(c1) + String.valueOf(c2) ,16);
			int ascii = Integer.valueOf(String.valueOf(c1) + String.valueOf(c2), 16).intValue();
			//System.out.println("a: " + ascii);
			String aChar =  new Character( (char) ascii).toString() ;
			//System.out.println("asc:" + ascii + "  ac: " + aChar);
			strb.append(String.valueOf( aChar ) ) ;
			
		}
		return strb.toString();
	}
	

	public boolean isQueryEqual(String query1, Properties props1, String query2, Properties props2){
		if (! query1.equalsIgnoreCase(query2) ) return false;
		if (props1.size() != props1.size() ) return false;
		for (int i = 0; i < props1.size(); i ++){
			
		}
		
		return false;
	}

}
