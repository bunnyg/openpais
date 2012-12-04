package edu.emory.cci.pais.documentgenerator;
import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;

import edu.emory.cci.pais.PAISIdentifierGenerator.IdentifierGenerator;
/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class generates information of PAIS root element to be used to construct PAIS document. 
 */

public class PAISRoot {
    public BigInteger id;
    public String name;
    public String uid;
    public XMLGregorianCalendar dateTime;
    public String paisVersion;
    public String codeValue;
    public String codeMeaning;
    public String codingSchemeDesignator;
    public String codingSchemeVersion;
    public String comment;
    
    public PAISRoot(BigInteger id, String uid, String name,   XMLGregorianCalendar dateTime, 
    		String paisVersion,  String codeValue,  String codeMeaning,  String codingSchemeDesignator,  String codingSchemeVersion,
    	    String comment){
    		this.id = id;
    		this.name = name;
    		this.uid = uid;
    		this.dateTime = dateTime;
    		this.paisVersion = paisVersion;
    		this.codeValue = codeValue;
    		this.codeMeaning = codeMeaning;
    		this.codingSchemeDesignator = codingSchemeDesignator;
    		this.codingSchemeVersion = codingSchemeVersion;
    		this.comment = comment;
    }
    
    public PAISRoot(BigInteger id, String uid, String name,  XMLGregorianCalendar dateTime,  String paisVersion, String comment){
    		this.id = id;
    		this.name = name;
    		this.uid = uid;
    		this.dateTime = dateTime;
    		this.paisVersion = paisVersion;
    		this.comment = comment;
    }    
    
    /**
     * Automatically generates timestamp for the document 
     */
    public PAISRoot(BigInteger id, String uid, String paisVersion){
    	XMLGregorianCalendar dateTime = IdentifierGenerator.getCurrentTimestamp();   
    	this.id = id;
		this.uid = uid;
		this.dateTime = dateTime;
		this.paisVersion = paisVersion;
}   
    
}
