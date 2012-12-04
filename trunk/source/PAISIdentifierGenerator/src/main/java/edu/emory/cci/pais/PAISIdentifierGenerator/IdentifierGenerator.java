package edu.emory.cci.pais.PAISIdentifierGenerator;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.UUID;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class provides functions to generate identifiers
 */

/* 
 * I DO like the proposed design from EJB Design Patterns book, 
 * but I have a different implementation that I believe solves the 50-day 
 * cycle problem identified by Thomas Pare, where chopping only the significant 8 
 * bytes from the result of CurrentTimeMillis() produces about 50 days worth of unique values. 
 * A better approach would be to keep all significant 12 bytes of the CurrentTimeMillis() and 
 * keep only 4 bytes from the SecureRandom() function (see below):
 * http://www.javafaq.nu/java-article1092.html
 *   1. (0 - 7) IPAddress as HEX - 8 bytes
 *   2. (8 - 19) CurrentTimeMillis() as HEX - Display all 12 bytes
 *   3. (20 - 23) SecureRandom() as HEX - Keep only 4 significant bytes. 
 *   	Since this is "random" it doesn't really matter how many bytes you keep or eliminate
 *   4. (24 - 31) System.identityHashCode as Hex - 8 bytes
 *   
 *   Other options: http://jug.safehaus.org/Home
 */

public class IdentifierGenerator {
	private static final char INIT_CHAR =' '; 

	public IdentifierGenerator () {
	}

	public static BigInteger stringToBigInteger(String in){
		String out = stringToNumber(in);
		return new BigInteger(out);
	}
	
	/**
	 * Only number or alphabet are included 
	 */
	public static String filterStr(String in){
		StringBuffer strb = new StringBuffer();
		for (int i = 0; i < in.length(); i ++ ){
			char c = in.charAt(i);
			int ascii = (int) c; 
			if (ascii > 47 && ascii < 58 ||  ascii > 64 && ascii < 91 || ascii > 96 && ascii < 123 )
				strb.append(c);
		}
		return strb.toString();
	}
	
	/**
	 * A function to generate a compact number based representation of a string. 
	 * @param in Input parameter, normally a mix of letters and numbers, and/or special characters.
	 * @return a string that converts letters into their corresponding ascii codes minus a base code ( ascii code of INIT_CHAR).   
	 */
	public static String compactStringToNumber(String in){
		if (isNumber (in) ) return in;
		String uppercaseStr = in.toUpperCase();
		StringBuffer strb = new StringBuffer();
		for (int i = 0; i < uppercaseStr.length(); i ++ ){
			char c = uppercaseStr.charAt(i);
			int ascii = (int) c; 
			if (ascii > 47 && ascii < 58){
				strb.append(c);
			} else if ( ascii > 64 && ascii < 91 || ascii > 96 && ascii < 123 )
				strb.append(c  - INIT_CHAR ); 
		}
		return strb.toString();
	}
	
	/** 
	 * @param str input string
	 * @return convert a single to number by using the ascii codes of its constituent characters
	 */
	public static String stringToNumber(String str){
		if (isNumber (str) ) return str;
		String uppercaseStr = str.toUpperCase();
		StringBuffer numStr = new StringBuffer();
		for (int i = 0; i < uppercaseStr.length(); i ++ ){
			numStr.append(uppercaseStr.charAt(i)  - INIT_CHAR ); 
		}
		return numStr.toString();
	}	
	
	/** 
	 * @param in input string
	 * @return if the input string is a number.
	 */
	public static boolean isNumber(String in){
		Scanner scanner = new Scanner(in);  
		if (scanner.hasNextDouble()) {  
			return true;
		}  
		return false;
	}
	

	    
	public String getUUID(){
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
	    
	    
    public String getUID() {
        String strRetVal = "";
        String strTemp = "";
        try {
            // Get IPAddress Segment
            InetAddress addr = InetAddress.getLocalHost();

            byte[] ipaddr = addr.getAddress();
            for (int i = 0; i < ipaddr.length; i++) {
                Byte b = new Byte(ipaddr[i]);

                strTemp = Integer.toHexString(b.intValue() & 0x000000ff);
                while (strTemp.length() < 2) {
                    strTemp = '0' + strTemp;
                }
                strRetVal += strTemp;
            }

            strRetVal += ':';

            //Get CurrentTimeMillis() segment
            strTemp = Long.toHexString(System.currentTimeMillis());
            while (strTemp.length() < 12) {
                strTemp = '0' + strTemp;
            }
            strRetVal += strTemp + ':';

            //Get Random Segment
            SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");

            strTemp = Integer.toHexString(prng.nextInt());
            while (strTemp.length() < 8) {
                strTemp = '0' + strTemp;
            }

            strRetVal += strTemp.substring(4) + ':';

            //Get IdentityHash() segment
            strTemp = Long.toHexString(System.identityHashCode((Object) this));
            while (strTemp.length() < 8) {
                strTemp = '0' + strTemp;
            }
            strRetVal += strTemp;

        }
        catch (java.net.UnknownHostException ex) {
            System.out.println("Unknown Host Exception Caught: " + ex.getMessage());
        }
        catch (java.security.NoSuchAlgorithmException ex) {
        	System.out.println("No Such Algorithm Exception Caught: " + ex.getMessage());
        }

        return strRetVal.toUpperCase();
    }

/**
 * Generate an XMLGregorianCalendar object to represent current timestamp
 * @return XMLGregorianCalendar of current timestamp.
 */
    public static XMLGregorianCalendar getCurrentTimestamp(){
    	Date now = new Date();
        GregorianCalendar gcal = (GregorianCalendar) GregorianCalendar.getInstance();
        gcal.setTime(now);
        XMLGregorianCalendar xgcal;
		try {
			xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			return null;
		}
    	return xgcal;
    }
    
    /**
     * Generate an XMLGregorianCalendar string to represent current timestamp.
     * @return XMLGregorianCalendar string of current timestamp.
     */
    public static String getCurrentTimestampString(){
    	return getCurrentTimestamp().toString();
    }
    
    public static void main(String[] args) throws Exception {
        String in = "a_10000001"; //"ns_morph1"; //"NS_MORPH1";
    	String out = IdentifierGenerator.compactStringToNumber(in);
    	System.out.println(out);
    	/*        for (int i = 0; i < 10; i++) {
            long lngStart = System.currentTimeMillis();
            System.out.println( objUID.getUID() );
            System.out.println("Elapsed time: " + (System.currentTimeMillis() - lngStart));
        }*/
    	
    	XMLGregorianCalendar xgcal = getCurrentTimestamp();
    	String currentTimestamp = xgcal.toString();
    	System.out.println(currentTimestamp);
    }	
	
}
