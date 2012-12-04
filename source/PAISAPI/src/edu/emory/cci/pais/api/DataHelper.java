package edu.emory.cci.pais.api;

import java.util.StringTokenizer;

public class DataHelper {

	/** 
	 * wkt: x1 y1, x2 y2, ... xn yn, x1 y1
	 * svg: x1,y1 x2,y2, ... xn,yn x1,y1
	 * */
	
	public static String wkt2svg(String wkt, int samplingRate){
		//if (samplingRate ==0)
		return wkt.replace(' ', ';').replace(',', ' ').replace(';', ',');
	}
	
	
	public static String wkt2svg(String wkt){
		return wkt2svg(wkt, 0);
	}
	
	public static String svg2wkt(String svg){
		String start = "POLYGON((";
		String end = "))";
		if (svg == null) return null;
		else return start + svg.replace(' ', ';').replace(',', ' ').replace(';', ',') + end;
	}

	
	public static String getFirstPoint(String wkt, int x0, int y0, String format){
		String initSeperator = ",";
		String coordSeperator = ",";
		StringBuffer buf = new StringBuffer(); 
		if ("wkt".equalsIgnoreCase(format) ) {
			coordSeperator = " ";
		}
		StringTokenizer st = new StringTokenizer(wkt, initSeperator);
		if (st.hasMoreTokens()) {
			String token = st.nextToken().trim();
			int idx = token.indexOf(' ');
			int x = Integer.parseInt( token.substring(0, idx) ) - x0;
			int y = Integer.parseInt( token.substring(idx+1) ) - y0;
			buf.append(String.valueOf(x) + coordSeperator + String.valueOf(y) );

		}
		return buf.toString();
	}
	
	public static String normalizeCoords(String wkt, int samplingRate, String format){
		return normalizeCoords(wkt, 0, 0, samplingRate, format);
	}
	
	public static String normalizeCoords(String wkt, int x0, int y0, int samplingRate, String format){
	   String initSeperator = ",";
	   String coordSeperator = ",";
	   String pointSeperator = " ";
	   boolean isFirst = true; 
	   int firstX = 0, firstY = 0;
	   int lastX = 1, lastY = 1;
	   int count = 0;
	   int sampleCount = 0;
	   
	   if ("wkt".equalsIgnoreCase(format) ) {
		   coordSeperator = " ";
		   pointSeperator = ",";
	   }
	   
	   StringTokenizer st = new StringTokenizer(wkt, initSeperator);
	   StringBuffer buf = new StringBuffer(); 

	   while (st.hasMoreTokens()) {
		   //System.out.println(count + " " + count % samplingRate);
		   //Skip points based on sampling rate
		   if ( (count % samplingRate ) == 0 ) {
			   if (isFirst != true) buf.append(pointSeperator);
			   String token = st.nextToken().trim();
			   int idx = token.indexOf(' ');
			   int x = Integer.parseInt( token.substring(0, idx) ) - x0;
			   int y = Integer.parseInt( token.substring(idx+1) ) - y0;
			   buf.append(String.valueOf(x) + coordSeperator + String.valueOf(y) );
			   if (isFirst == true) {
				   firstX = x; firstY = y;
			   }
			   lastX = x; lastY = y;
			  sampleCount++;
		   }
		   else st.nextToken();
		   count++;
		   isFirst = false;
	   }
	   
	   //A polygon smaller than 4 points is not a valid. If so, no sampling will be done. 
	   //System.out.println("count: " + count + " sample count:" + sampleCount + " Sampling rate: " + samplingRate);
	   if ( sampleCount <= 4 ) {
		 samplingRate = count / 4;  
		 //System.out.println("recur" +  samplingRate);
		 return  normalizeCoords(wkt, x0, y0, samplingRate, format);
	   } 

	   if ("wkt".equalsIgnoreCase(format) )
		   if (! ( (lastX == firstX) && (lastY == firstY) )  ) 
			   buf.append(coordSeperator + firstX + pointSeperator + firstY);
	   
	   return buf.toString();
	}
	
	public static String str2svgpolygon(String points, String style){
		return "<polygon points=\"" + points + "\" style=\"" + style + "\" />\n";		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String wkt = "4097 16487, 4098 16487, 4099 16487, 4099 16486, 4100 16486, 4101 16486, 4101 16487, 4102 16487, 4102 16488, 4102 16489, 4103 16489, 4103 16490, 4104 16490, 4104 16491, 4104 16492, 4105 16492, 4106 16492, 4107 16492, 4107 16493, 4108 16493, 4108 16494, 4109 16494, 4109 16495, 4109 16496, 4109 16497, 4109 16498, 4108 16498, 4108 16499, 4107 16499, 4107 16500, 4106 16500, 4105 16500, 4105 16499, 4105 16498, 4104 16498, 4104 16497, 4103 16497, 4103 16496, 4102 16496, 4102 16495, 4101 16495, 4101 16494, 4100 16494, 4099 16494, 4099 16493, 4098 16493, 4097 16493, 4097 16492, 4097 16491, 4097 16490, 4097 16489, 4097 16488, 4097 16487";
		//String wkt = "4177 16457, 4178 16457, 4178 16456, 4179 16456, 4179 16455, 4180 16455, 4180 16456, 4181 16456, 4181 16457, 4182 16457, 4182 16458, 4183 16458, 4184 16458, 4185 16458, 4185 16459, 4185 16460, 4185 16461, 4184 16461, 4183 16461, 4182 16461, 4181 16461, 4181 16462, 4180 16462, 4179 16462, 4179 16461, 4179 16460, 4178 16460, 4178 16459, 4177 16459, 4177 16458, 4177 16457";
		//System.out.println(DataHelper.wkt2svg(wkt, 0) );

		//String wkt = "4097 16461, 4098 16461, 4098 16462, 4099 16462, 4100 16462";
		
		String svg = normalizeCoords(wkt, 4096, 16308, 20, "wkt");
		System.out.println(svg);		
		//String point = getFirstPoint(wkt, 4096, 16308, "wkt");
		//System.out.println(point);	
		//System.out.println( str2svgpolygon(svg, "fill:lime;stroke:purple;stroke-width:5;fill-rule:nonzero;") );
		
	}

}
