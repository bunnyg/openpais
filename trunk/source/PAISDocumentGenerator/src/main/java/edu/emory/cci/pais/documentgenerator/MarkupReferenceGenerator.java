package edu.emory.cci.pais.documentgenerator;
import java.math.BigInteger;
import edu.emory.cci.pais.model.*;


public class MarkupReferenceGenerator {
		
	public MarkupReferenceGenerator(BigInteger id){
		
	}
	
	// markupBinary Setting
	public MarkupReferenceGenerator(BigInteger id, String value, String encoding, String compression){
		MarkupBinary markupbinary = new MarkupBinary();
		markupbinary.setValue(value);
		markupbinary.setEncoding(encoding);
		markupbinary.setCompression(compression);
	}
	// markupFileReference setting
	public MarkupReferenceGenerator(BigInteger id, String fileReference){
		MarkupFileReference markupFileRef = new MarkupFileReference();
		markupFileRef.setFileReference(fileReference);
	}
	
	// markupURI setting
	public MarkupReferenceGenerator(String uri, BigInteger id){
		MarkupURI markupURI = new MarkupURI();
		markupURI.setUri(uri);
	}
	
	// Get markupBinary object
	public MarkupBinary getMarkupBinary(String value, String encoding, String compression)
	{
		MarkupBinary markupbinary = new MarkupBinary();
		markupbinary.setValue(value);
		markupbinary.setEncoding(encoding);
		markupbinary.setCompression(value);
		return markupbinary;
	}
	
	// Get markupFileReference object
	public MarkupFileReference getMarkupFileReference(String value){
		MarkupFileReference markupFileRef = new MarkupFileReference();
		markupFileRef.setFileReference(value);
		return markupFileRef;
	}
	
	// Get markupURI object
	public MarkupURI getMarkupURI(String value){
		MarkupURI markupURI = new MarkupURI();
		markupURI.setUri(value);
		return markupURI;
	}

}
