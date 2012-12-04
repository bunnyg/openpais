package edu.emory.cci.pais.converter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

/**
 * 
 */

/**
 * @author Fusheng Wang
 * @version 1.0
 * 
 * This function provides a set of functions for Sun Java Streaming XML Parser (SJSXP) 
 *
 */
public class StAXUtils {
	
	
    /**
     * Returns the String representation of the given integer constant.
     *
     * @param eventType Type of event.
     * @return String representation of the event
     */    
    public final static String getEventTypeString(int eventType) {
        switch (eventType){
            case XMLEvent.START_ELEMENT:
                return "START_ELEMENT";
            case XMLEvent.END_ELEMENT:
                return "END_ELEMENT";
            case XMLEvent.PROCESSING_INSTRUCTION:
                return "PROCESSING_INSTRUCTION";
            case XMLEvent.CHARACTERS:
                return "CHARACTERS";
            case XMLEvent.COMMENT:
                return "COMMENT";
            case XMLEvent.START_DOCUMENT:
                return "START_DOCUMENT";
            case XMLEvent.END_DOCUMENT:
                return "END_DOCUMENT";
            case XMLEvent.ENTITY_REFERENCE:
                return "ENTITY_REFERENCE";
            case XMLEvent.ATTRIBUTE:
                return "ATTRIBUTE";
            case XMLEvent.DTD:
                return "DTD";
            case XMLEvent.CDATA:
                return "CDATA";
            case XMLEvent.SPACE:
                return "SPACE";
        }
        return "UNKNOWN_EVENT_TYPE , " + eventType;
    }
    
    public static void printEventType(int eventType) {        
        //System.out.println("EVENT TYPE("+eventType+") = " + getEventTypeString(eventType));
    }
    
    public static void printStartDocument(XMLStreamReader xmlr){
        if(xmlr.START_DOCUMENT == xmlr.getEventType()){
            //System.out.println("<?xml version=\"" + xmlr.getVersion() + "\"" + " encoding=\"" + xmlr.getCharacterEncodingScheme() + "\"" + "?>");
        }
    }
    
    public static void printComment(XMLStreamReader xmlr){
        if(xmlr.getEventType() == xmlr.COMMENT){
            //System.out.print("<!--" + xmlr.getText() + "-->");
        }
    }
            
    public static void printText(XMLStreamReader xmlr){
        if(xmlr.hasText()){
            //System.out.print(xmlr.getText());
        }
    }
    
    public static void printPIData(XMLStreamReader xmlr){
        if (xmlr.getEventType() == XMLEvent.PROCESSING_INSTRUCTION){
            //System.out.print("<?" + xmlr.getPITarget() + " " + xmlr.getPIData() + "?>") ;
        }
    }
    
    public static void printStartElement(XMLStreamReader xmlr){
        if(xmlr.isStartElement()){        
            //System.out.print("<" + xmlr.getLocalName().toString());
            printAttributes(xmlr);
            //System.out.print(">");
        }
    }
    
    public static void printEndElement(XMLStreamReader xmlr){
        if(xmlr.isEndElement()){
            //System.out.print("</" + xmlr.getLocalName().toString() + ">");
        }
    }
    
    
    public static StringBuffer printCoords2Buffer(XMLStreamReader xmlr){
    	StringBuffer buf = new StringBuffer();
    	String attrX ="X";
    	String attrY ="Y";
        int count = xmlr.getAttributeCount() ;
        if(count > 0){
            for(int i = 0 ; i < count ; i++) {
                buf.append( xmlr.getAttributeValue( xmlr.getNamespaceURI(), attrX) );
                buf.append(",");
                buf.append( xmlr.getAttributeValue( xmlr.getNamespaceURI(), attrY) );
            }            
        }    
        return buf;        
    }
        
    
    public static void printAttributes(XMLStreamReader xmlr){
        int count = xmlr.getAttributeCount() ;
        if(count > 0){
            for(int i = 0 ; i < count ; i++) {
                System.out.print(" ");
                System.out.print(xmlr.getAttributeLocalName(i).toString());
                System.out.print("=");
                System.out.print("\"");
                System.out.print(xmlr.getAttributeValue(i));
                System.out.print("\"");
            }            
        }
        
        count = xmlr.getNamespaceCount();
        if(count > 0){
            for(int i = 0 ; i < count ; i++) {
                //System.out.print(" ");
                //System.out.print("xmlns");
                if(xmlr.getNamespacePrefix(i) != null ){
                    //System.out.print(":" + xmlr.getNamespacePrefix(i));
                }                
                //System.out.print("=");
                //System.out.print("\"");
                //System.out.print(xmlr.getNamespaceURI(i));
                //System.out.print("\"");
            }            
        }
    }
    
    
    /** Functions to print strings into buffers */

    public static void printText(XMLStreamReader xmlr, StringBuffer buf){
        if(xmlr.hasText()){
        	buf.append(xmlr.getText());
        }
    }
    
    
    public static void printStartElement(XMLStreamReader xmlr, StringBuffer buf){
        if(xmlr.isStartElement()){        
        	buf.append("<" + xmlr.getLocalName().toString());
            printAttributes(xmlr, buf);
            buf.append(">");
        }
    }
    
    

    
    
    public static void printEndElement(XMLStreamReader xmlr, StringBuffer buf){
        if(xmlr.isEndElement()){
        	buf.append("</" + xmlr.getLocalName().toString() + ">");
        }
    }

    
    public static String fixIdAttribute(String attributeName, String idValue){
    	if (attributeName.equals("id") ){
    		//int dotIdx = idValue.indexOf(".ndpi");
    		int dxIdx =  idValue.indexOf("DX");
    		if (dxIdx > 0 ) { 
    			//System.out.println("Old value:" + idValue);
    			String newValue =  idValue.substring(0, dxIdx);
    			//System.out.println("New ID:" + newValue);
    			return newValue;
    		};
    	}
    	return idValue;    	
    }
    
    
    public static void printAttributes(XMLStreamReader xmlr, StringBuffer buf){
        int count = xmlr.getAttributeCount() ;
        if(count > 0){
            for(int i = 0 ; i < count ; i++) {
            	buf.append(" ");
            	String localName = xmlr.getAttributeLocalName(i).toString();
            	buf.append(localName);
            	buf.append("=");
            	buf.append("\"");
            	String attrValue = xmlr.getAttributeValue(i);
            	buf.append( fixIdAttribute(localName, attrValue)  );
            	buf.append("\"");
            }            
        }
        
        count = xmlr.getNamespaceCount();
        if(count > 0){
            for(int i = 0 ; i < count ; i++) {
            	buf.append(" ");
            	buf.append("xmlns");
                if(xmlr.getNamespacePrefix(i) != null ){
                	buf.append(":" + xmlr.getNamespacePrefix(i));
                }                
                buf.append("=");
                buf.append("\"");
                buf.append(xmlr.getNamespaceURI(i));
                buf.append("\"");
            }            
        }
    }
    
    public static void printStartDocument(XMLStreamReader xmlr, StringBuffer buf){
        if(xmlr.START_DOCUMENT == xmlr.getEventType()){
            buf.append("<?xml version=\"" + xmlr.getVersion() + "\"" + " encoding=\"" + xmlr.getCharacterEncodingScheme() + "\"" + "?>");
        }
    }
    
    public static void printComment(XMLStreamReader xmlr, StringBuffer buf){
        if(xmlr.getEventType() == xmlr.COMMENT){
        	buf.append("<!--" + xmlr.getText() + "-->");
        }
    }
     
    public static void printPIData(XMLStreamReader xmlr, StringBuffer buf){
        if (xmlr.getEventType() == XMLEvent.PROCESSING_INSTRUCTION){
        	buf.append("<?" + xmlr.getPITarget() + " " + xmlr.getPIData() + "?>") ;
        }
    }

    
    /** Functions to print strings into buffers */
    public static void printText(XMLStreamReader xmlr, BufferedWriter buf){
        if(xmlr.hasText()){
        	try {
				buf.append(xmlr.getText());
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    
    public static void printStartElement(XMLStreamReader xmlr, BufferedWriter buf){
    	if(xmlr.isStartElement()){      
    		try{
    			buf.append("<" + xmlr.getLocalName().toString());
    			printAttributes(xmlr, buf);
    			buf.append(">");
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    public static void printEndElement(XMLStreamReader xmlr, BufferedWriter buf){
        if(xmlr.isEndElement()){
        	try {
				buf.append("</" + xmlr.getLocalName().toString() + ">");
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    public static void printAttributes(XMLStreamReader xmlr, BufferedWriter buf){
    	int count = xmlr.getAttributeCount() ;
    	if(count > 0){
    		try{
    			for(int i = 0 ; i < count ; i++) {
    				buf.append(" ");
    				//buf.append(xmlr.getAttributeLocalName(i).toString());
    				String prefix = xmlr.getAttributePrefix(i);
    				//String prefix = xmlr.getPrefix();
    				if (prefix != null && ! "".equals(prefix)  ) {
    					buf.append(prefix);
    					buf.append(":");
    				}
    				buf.append(xmlr.getAttributeLocalName(i).toString());
    				buf.append("=");
    				buf.append("\"");
    				buf.append(xmlr.getAttributeValue(i));
    				buf.append("\"");
    			}   
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}

    	count = xmlr.getNamespaceCount();
    	if(count > 0){
    		try{
    			for(int i = 0 ; i < count ; i++) {
    				buf.append(" ");
    				buf.append("xmlns");
    				if(xmlr.getNamespacePrefix(i) != null ){
    					buf.append(":" + xmlr.getNamespacePrefix(i));
    				}                
    				buf.append("=");
    				buf.append("\"");
    				buf.append(xmlr.getNamespaceURI(i));
    				buf.append("\"");
    			}  
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }


	

    /**
     * Get the split filename in the new folder 
     */
    public static String getTargetFilename(String filename, int num, String newFoldername){
    	//Get local filename
    	int sepPos = filename.lastIndexOf(File.separatorChar);
    	String localFilename = filename.substring(sepPos + 1);
    	
    	//Get new local filename
    	int extPos = localFilename.indexOf(".xml");
    	String newLocalFilename = localFilename.substring(0, extPos) +  ".txt";
    	
    	//Get final full filename
    	return newFoldername + File.separator + newLocalFilename;
    }
    
    
    /**
     * Get the split filename in the new folder 
     */
    public static String getTargetFilename(String filename, String ext){
    	String fileExt = "xml";
    	int sepPos = filename.lastIndexOf(fileExt);
    	//System.out.println("pos: " + sepPos);
    	String newLocalFilename = filename.substring(0, sepPos - 1) + "." +  ext;
    	//System.out.println("new filename:" + newLocalFilename);
    	return  newLocalFilename;
    }
        
    
    /**
     * Get a BufferWriter for a split file based on the current index and new target folder, 
     * and add the corresponding BufferWriter and File in their list 
     */
        public static BufferedWriter getBufWtr(String targetFilename){     	
        	File file = new File(targetFilename);
        	BufferedWriter bufWtr = null;
    		try {
    			bufWtr = new BufferedWriter(new FileWriter(file));
    		} catch (IOException e) {
    			System.out.println("Can't create file...");    			
    			e.printStackTrace();
    			return null;
    		}
        	return bufWtr;
        }  
        
    
    /**
     * Get a BufferWriter for a split file based on the current index and new target folder, 
     * and add the corresponding BufferWriter and File in their list 
     */
        public static BufferedWriter getBufWtr(String filename, String newFolderName){
        	String pFilename = StAXUtils.getTargetFilename(filename, 0, newFolderName);        	
        	File file = new File(pFilename);
        	BufferedWriter bufWtr = null;
    		try {
    			bufWtr = new BufferedWriter(new FileWriter(file));
    		} catch (IOException e) {
    			e.printStackTrace();
    			return null;
    		}
        	return bufWtr;
        }    
    
    /**
     * Get a BufferWriter for a split file based on the current index and new target folder, 
     * and add the corresponding BufferWriter and File in their list 
     */
        public static BufferedWriter getBufWtr(String filename, int num, String newFolderName,  List<BufferedWriter> bufList,  List <File> list){
        	String pFilename = StAXUtils.getTargetFilename(filename, num, newFolderName);
        	
        	File file = new File(pFilename);
        	list.add(file);
        	BufferedWriter bufWtr = null;
    		try {
    			bufWtr = new BufferedWriter(new FileWriter(file));
    			bufList.add(bufWtr);
    		} catch (IOException e) {
    			e.printStackTrace();
    			return null;
    		}
        	return bufWtr;
        }
        
        
        
        public static String simpleCoord(String in){
        	int idx = in.indexOf('.');
        	if (idx == -1) return in;
        	//System.out.println(idx);
        	String simpStr = in.substring(0, idx);
        	return simpStr;
        }
        
    	/**
    	 *  Get the coordinates of a region 
    	 */
    	public static StringBuffer getAllRegionCoords(XMLStreamReader xmlr){
    		//StAXUtils.printStartElement(xmlr, buf);  
    		StringBuffer buf = new StringBuffer();
    		String elemName ="Region";
	    	String attrX ="X";
	    	String attrY ="Y";
    		while (! isEndElement(elemName, xmlr)  ){
    			try {
    				if ( xmlr.hasNext() ){
    					xmlr.next();
    					if ( isStartElement(xmlr)  )
    						if (xmlr.getLocalName().equals("Vertex")) {
    					        int count = xmlr.getAttributeCount() ;
    					        //System.out.println("count:" + count);
    					        if(count > 0){
    					            //for(int i = 0 ; i < count ; i++) {
    					                buf.append( simpleCoord( xmlr.getAttributeValue( xmlr.getNamespaceURI(), attrX) ) );
    					                buf.append(",");
    					                buf.append( simpleCoord( xmlr.getAttributeValue( xmlr.getNamespaceURI(), attrY) ) );
    					            //} 
    					            buf.append(" ");
    					        }
    						}               
    				}
    			} catch (XMLStreamException e) {
    				e.printStackTrace();
    			}
    		}
    		return buf;
    	}

    	
    	
    	/**
    	 *  Assume the current event is the starting of the element, then return everything in between. 
    	 */
    	public static StringBuffer getRegionCoords(XMLStreamReader xmlr){

    		StringBuffer buf = new StringBuffer();
    		String elemName ="Region";
    		String attrX ="X";
    		String attrY ="Y";
    		boolean isFirst = true;
    		int count = 0;
    		
    		while (! isEndElement(elemName, xmlr)  ){
    			try {
    				if ( xmlr.hasNext() ){    					
    					if (count > 0 ) isFirst = false; 
    					xmlr.next();
    					if ( isStartElement(xmlr)  )
    						if (xmlr.getLocalName().equals("Vertex")) {  							
    								int acount = xmlr.getAttributeCount() ;
    								//System.out.println("count:" + count);    							
    								if(acount > 0){
    									if (isFirst == false) buf.append(" ");
    									buf.append( simpleCoord( xmlr.getAttributeValue( xmlr.getNamespaceURI(), attrX) ) );
    									buf.append(",");
    									buf.append( simpleCoord( xmlr.getAttributeValue( xmlr.getNamespaceURI(), attrY) ) );    									
    								}
    						}//end Vertex 
    					count++;
    				}
    			} catch (XMLStreamException e) {
    				e.printStackTrace();
    			}
    		}
    		return buf;
    	}    	


   
    	/**
    	 *  Assume the current event is the starting of the element, then return everything in between. 
    	 */
    	public static void getElement(String name, StringBuffer buf, XMLStreamReader xmlr){
    		StAXUtils.printStartElement(xmlr, buf);  
    		while (!isEndElement(name, xmlr)  ){
    			try {
    				if ( xmlr.hasNext() ){
    					xmlr.next();
    					StAXUtils.printStartElement(xmlr, buf);                    
    					StAXUtils.printEndElement(xmlr, buf);                    
    					StAXUtils.printText(xmlr, buf);                    
    				}
    			} catch (XMLStreamException e) {
    				e.printStackTrace();
    			}
    		}
    	}


/**
 * Check if an element has an attribute with expected value, e.g., Region has attribute Description with value Vessel.
 * @param attrName
 * @param expAttrValue
 * @param xmlr
 * @return
 */
    	public static boolean hasAttribute(String attrName, String expAttrValue,  XMLStreamReader xmlr){    		
				int count = xmlr.getAttributeCount() ;
				String attrValue = null;    							
				if(count > 0) attrValue = xmlr.getAttributeValue(xmlr.getNamespaceURI(), attrName);
				if (expAttrValue.equalsIgnoreCase(attrValue) ) return true;
				return false;
    	}
    	
    	
    	/**
    	 * deprecated
    	 * @param attrName
    	 * @param expAttrValue
    	 * @param xmlr
    	 * @return
    	 */
    	public static boolean isAttribute(String attrName, String expAttrValue,  XMLStreamReader xmlr){
    		int eventType = xmlr.getEventType();	
    		System.out.println("Event type:" + eventType);
    		if (eventType != XMLStreamConstants.ATTRIBUTE ) return false;
    		String attrValue = xmlr.getAttributeValue(xmlr.getNamespaceURI(), attrName);
    		System.out.println("Att Value: " + attrValue);
    		if (expAttrValue.equalsIgnoreCase(attrValue) ) {
    			//System.out.println("Vessel.");
    			return true;
    		}
    		//System.out.println("Not Vessel.");
    		return false;
    	}
    	  	
    	
    	
    	/**  Check if the current event is the staring of an element of the name 
    	 */
    	public static boolean isStartElement(String name, XMLStreamReader xmlr){
    		int eventType = xmlr.getEventType();	
    		if (! (eventType == XMLStreamConstants.START_ELEMENT) ) return false;
    		if( name.equals(xmlr.getLocalName() ) ){			
    			return true;
    		}
    		return false;
    	}
    	
    	
    	/**  Check if the current event is the staring of an element of the name 
    	 */
    	public static boolean isStartElement(XMLStreamReader xmlr){
    		int eventType = xmlr.getEventType();	
    		if ((eventType == XMLStreamConstants.START_ELEMENT) ) 
    			return true;
    		else return false;
    	}
    	
    	

    	
    	
    	/**  Check if the current event is the ending of an element of the name 
    	 */	
    	public static boolean isEndElement(String name, XMLStreamReader xmlr){
    		int eventType = xmlr.getEventType();
    		if (eventType != XMLEvent.END_ELEMENT) return false;
    		if( name.equals(xmlr.getLocalName() ) )	return true;
    		return false;
    	}

    	

        

    	public static void printMap(HashMap <String, String> map){
    		Set <String> keys = map.keySet();
    		Iterator<String> it = keys.iterator();
    		//System.out.println("Begin tile_partition_map:");
    		while (it.hasNext()) { 
    			String key = (String) it.next();
    			String value = map.get(key);
    			System.out.println(key +  ": " + value);
    			System.out.println(key);
    		}
    		//System.out.println("End tile_partition_map.");
    	}

    	
    	
        public static HashMap <String, String>  printMapStartElement(XMLStreamReader xmlr, StringBuffer buf){
        	HashMap <String, String> map = new HashMap <String, String>();
            if(xmlr.isStartElement()){        
            	buf.append("<" + xmlr.getLocalName().toString());
                StAXUtils.printAttributes(xmlr, buf);
                buf.append(">");
            	String elementName = xmlr.getLocalName();
                map.putAll(mapAttributes(elementName, xmlr));
            }
            return map;
        }	
    	
    	
    	/** Get start element and its attributes as a tile_partition_map  */
        public static HashMap <String, String>  mapStartElement(XMLStreamReader xmlr){
        	HashMap <String, String> map = new HashMap <String, String>();
            if(StAXUtils.isStartElement(xmlr)){  
            	String elementName = xmlr.getLocalName();
                map.putAll(mapAttributes(elementName, xmlr));
            }
            return map;
        }
    	

        /** Get attributes as a tile_partition_map  */
        public static HashMap <String, String>  mapAttributes(String elementName, XMLStreamReader xmlr){
        	int count = xmlr.getAttributeCount() ;
        	HashMap <String, String> map = new HashMap <String, String>();
        	if(count > 0){
        		for(int i = 0 ; i < count ; i++) {
        			String key =  elementName + "/@";
        			String value = null;
        			//buf.append(xmlr.getAttributeLocalName(i).toString());
        			String prefix = xmlr.getAttributePrefix(i);
        			if (prefix != null && ! "".equals(prefix)  ) {
        				key = key + prefix;
        			}
        			String attrName = xmlr.getAttributeLocalName(i);
        			key = key + attrName;
//        			value = xmlr.getAttributeValue(i);
        			value = fixIdAttribute(attrName, xmlr.getAttributeValue(i) );
        			//System.out.println("Fixed: " + value);
        			map.put(key, value);
        		}
        	}
        	return map;
        }

    	
    	
    	public static HashMap <String, String>  mapElement(String name, XMLStreamReader xmlr){
    		HashMap <String, String> map = mapStartElement(xmlr);
    		while (!StAXUtils.isEndElement(name, xmlr)  ){
    			try {
    				if ( xmlr.hasNext() ){
    					xmlr.next();
    					if(StAXUtils.isStartElement(xmlr)){ 
    						HashMap <String, String> map1 = mapStartElement(xmlr);
    						map.putAll(map1);						
    					}                   
    				}
    			} catch (XMLStreamException e) {
    				e.printStackTrace();
    			}
    		}
    		return map;
    	}    
      
    	public static HashMap<String, String> mergeKeys(ArrayList <HashMap<String, String>> calculationList){
    		HashMap<String, String> newMap = new HashMap<String, String>(); 
    		for (int j = 0; j < calculationList.size(); j++ ){
    			HashMap<String, String> moreMap = calculationList.get(j);
    			newMap.putAll(moreMap);
    		}   
    		return newMap;
    	}


    	
    	public static HashMap<String, String> mergeMaps(ArrayList <HashMap<String, String>> calculationList){
    		HashMap<String, String> tempMap = mergeKeys(calculationList);
    		HashMap<String, String> newMap = new HashMap<String, String>();
    		Set <String> keys = tempMap.keySet();		
    		Iterator<String> it = keys.iterator();
    		//for (HashMap<String, String> m: calculationList ) printMap(m);
    		while (it.hasNext()) { 
    			String key = (String) it.next();			
    			StringBuffer bufValue = new StringBuffer();
    			for (int j = 0; j < calculationList.size(); j++ ){
    				HashMap<String, String> map = calculationList.get(j);
    				String value = map.get(key);
    				if(j != 0) bufValue.append("^" + value);
    				else bufValue.append(value);

    			}   
    			newMap.put(key, bufValue.toString());
    		}		
    		return newMap;
    	}
    	
    	public static HashMap <String, String>  mapAnnotationElement(String name, XMLStreamReader xmlr){
    		HashMap <String, String> map = mapStartElement(xmlr);
    		
    		while (!StAXUtils.isEndElement(name, xmlr)  ){
    			try {
    				if ( xmlr.hasNext() ){
    					xmlr.next();

    					if(StAXUtils.isStartElement("calculationCollection", xmlr )){ 
    						ArrayList <HashMap<String, String>>annotationList = new ArrayList <HashMap<String, String>> ();
    						while(xmlr.hasNext()){    			        		
    							xmlr.next();
    							if(StAXUtils.isStartElement("Calculation", xmlr )){ 
    								HashMap<String, String> calculationMap = StAXUtils.mapElement("Calculation", xmlr);
    								String value =calculationMap.get("Calculation/@name");
    								annotationList.add(calculationMap);
    							}
    							if ( StAXUtils.isEndElement("calculationCollection", xmlr) ) {
    								//for (HashMap<String, String> m: annotationList ) printMap(m);
    								map.putAll( mergeMaps(annotationList) );
    								break;
    							}
    						}//End while
    					}

    					if(StAXUtils.isStartElement("observationCollection", xmlr )){ 
    						ArrayList <HashMap<String, String>>annotationList = new ArrayList <HashMap<String, String>> ();
    						while(xmlr.hasNext()){    			        		
    							xmlr.next();
    							if ( StAXUtils.isEndElement("observationCollection", xmlr) ) {
    								map.putAll( mergeMaps(annotationList) );
    								break;
    							}
    							if(StAXUtils.isStartElement("Observation", xmlr )){ 
    								HashMap<String, String> calculationMap = StAXUtils.mapElement("Observation", xmlr);
    								annotationList.add(calculationMap);
    							}
    						}//End while
    					}    					
    					
    					if(StAXUtils.isStartElement(xmlr)){ 
    						HashMap <String, String> map1 = mapStartElement(xmlr);
    						map.putAll(map1);						
    					} 
    					//}//End of if calculationCollection
    					
    					
    				}//End if hasNext
    			} catch (XMLStreamException e) {
    				e.printStackTrace();
    			}
    		}
    		return map;
    	}       	

    	
    	
    	
    	/**
    	 *  Assume the current event is the starting of the element, then return everything in between. 
    	 */
    	public static HashMap <String, String> mapElement(String name, StringBuffer buf, XMLStreamReader xmlr){
    		StAXUtils.printStartElement(xmlr, buf);  
    		HashMap <String, String> map = mapStartElement(xmlr);
    		while (!isEndElement(name, xmlr)  ){
    			try {
    				if ( xmlr.hasNext() ){
    					xmlr.next();
    					StAXUtils.printStartElement(xmlr, buf);                    
    					StAXUtils.printEndElement(xmlr, buf);                    
    					StAXUtils.printText(xmlr, buf);    
						HashMap <String, String> map1 = mapStartElement(xmlr);
						map.putAll(map1);
    				}
    			} catch (XMLStreamException e) {
    				e.printStackTrace();
    			}
    		}
    		return map;
    	}    	
    	
    	
    	public static  HashMap <String, String> addMap(HashMap <String, String> targetMap, HashMap <String, String> rootMap, HashMap <String, String> imgRefMap ){
    		String key = "PAIS/@uid";
    		String value = rootMap.get(key);
    		targetMap.put(key, value);
    		key = "Region/@name";
    		value = imgRefMap.get(key);
    		targetMap.put(key, value);
    		return targetMap;
    	}	
    	
    	
    	public static  HashMap <String, String> addPaisUid(HashMap <String, String> targetMap, HashMap <String, String> rootMap){
    		String key = "PAIS/@uid";
    		String value = rootMap.get(key);
    		targetMap.put(key, value);	
    		return targetMap;
    	}	    	


}

