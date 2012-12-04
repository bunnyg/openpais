package edu.emory.cci.pais.dataloader.db2helper;

import java.util.regex.Pattern;

public class GISHelper {

	/**
	 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
	 * @version 1.0 
	 */
	
	public GISHelper(){		
	}
	
	public static String svg2wkt(String svg){
		String start = "POLYGON((";
		String end = "))";
		if (svg == null) return null;
		else return start + svg.replace(' ', ';').replace(',', ' ').replace(';', ',') + end;
	}
	
	public static String chaincode2wkt(String points){
		String start = "POLYGON((";
		String end = "))";
		if (points == null)
			return null;
		String[] pointsArray = points.split(Pattern.quote(","));
		trimAll(pointsArray);
		// The first point is the reference coordinate
		// The first component of the point is the x coordinate
		int xReferenceCoordinate = Integer.parseInt(pointsArray[0].split(Pattern.quote(" "))[0]);
		// The second component is the y coordinate
		int yReferenceCoordinate = Integer.parseInt(pointsArray[0].split(Pattern.quote(" "))[1]);
		String result = pointsArray[0];
		// All other points are relative to the reference coordinate
		int xRelativeCoordinate = xReferenceCoordinate;
		int yRelativeCoordinate = yReferenceCoordinate;
		for(int i=1;i < pointsArray.length;i++) {
			xRelativeCoordinate += Integer.parseInt(pointsArray[i].split(Pattern.quote(" "))[0]);
			yRelativeCoordinate += Integer.parseInt(pointsArray[i].split(Pattern.quote(" "))[1]);
			result += "," + xRelativeCoordinate + " " + yRelativeCoordinate;
		}
		
		return start + result + end;
	}
	
	public static String db2gse2wkt(String db2gse){
		String start = "POLYGON(";
		String end = ")";
		if (db2gse == null) return null;
		else return start + db2gse + end;		
	}
	
	public static String convert(String points, String representation){
		if ("db2gse".equals(representation.toLowerCase() ) )
			return db2gse2wkt(points);
		return svg2wkt(points);
	}

	public static String convert(String points){
		points = points.trim();
		String representation = "svg";
		if ( points.indexOf('(') != -1 ) representation = "db2gse";
		if(points.indexOf(" ") < points.indexOf(","))
			representation = "chaincode";
		if ("db2gse".equals(representation.toLowerCase() ) )
			return db2gse2wkt(points);
		else if(representation.equals("chaincode"))
			return chaincode2wkt(points);
		else
			return svg2wkt(points);
	}	
	
	public static void main(String[] args) {

		String db2gses = "(110 120, 110 140, 130 140, 130 120, 110 120), (115 125, 115 135, 125 135, 125 135, 115 125)";
		String svg = "110, 120 110,140 130,140 130,120 110, 120";
		System.out.println(db2gse2wkt(db2gses));
		System.out.println(svg2wkt(svg));
		//System.out.println(db2gse.length());
	}
	
	// Eliminate spaces in the strings
	static void trimAll(String[] strings) {
		for(int i=0;i < strings.length;i++) {
			strings[i] = strings[i].trim();
		}
	}
}


