package edu.emory.cci.pais.dataloader.boundaryvalidator;

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
		String representation = "svg";
		if ( points.indexOf('(') != -1 ) representation = "db2gse";		
		if ("db2gse".equals(representation.toLowerCase() ) )
			return db2gse2wkt(points);
		return svg2wkt(points);
	}	
	
	public static void main(String[] args) {

		String db2gses = "(110 120, 110 140, 130 140, 130 120, 110 120), (115 125, 115 135, 125 135, 125 135, 115 125)";
		String svg = "110, 120 110,140 130,140 130,120 110, 120";
		GISHelper gis = new GISHelper();
		System.out.println(gis.db2gse2wkt(db2gses));
		System.out.println(gis.svg2wkt(svg));
		//System.out.println(db2gse.length());
	}
}



