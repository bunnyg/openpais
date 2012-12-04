package edu.emory.cci.pais.documentgenerator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import edu.emory.cci.pais.model.*;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class generates XML document based on a PAIS object. 
 */

public class XMLMarshaller {

	/** 
	 * Generate XML document. 
	 * @param xmlObjectClass Class of an XML object class
	 * @param rootName name of XML root element
	 * @param outputFile output XML file name
	 * @param xmlObject XML object
	 */
	@SuppressWarnings("unchecked")
	public void generateXML(Class xmlObjectClass, String rootName, String outputFile, Object xmlObject){
		long starttime = System.currentTimeMillis() ;
		String ns = "gme://caCORE.caCORE/3.2/edu.emory.cci.pais";
	
		try {
			JAXBContext context = JAXBContext.newInstance(xmlObjectClass);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");


			FileWriter fileWriter =  new FileWriter(outputFile);
			marshaller.marshal(new JAXBElement(new QName(ns, rootName, ""), xmlObjectClass, xmlObject), fileWriter);
			//marshaller.marshal(new JAXBElement(new QName(rootName), xmlObjectClass, xmlObject), fileWriter);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		long endtime = System.currentTimeMillis();
		System.out.println("Writing time = " + (endtime - starttime)/1000.0 + " seconds." );	
	}
	
	/** 
	 * Generate zipped XML document.  
	 * @param xmlObjectClass Class of an XML object class
	 * @param rootName name of XML root element
	 * @param outputFile output XML file name
	 * @param xmlObject XML object
	 */
	@SuppressWarnings("unchecked")
	public void generateXMLZip(Class xmlObjectClass, String rootName, String outputFile, Object xmlObject){
		long starttime = System.currentTimeMillis() ;
		String ns = "gme://caCORE.caCORE/3.2/edu.emory.cci.pais";

		try {
			JAXBContext context = JAXBContext.newInstance(xmlObjectClass);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");

			int level = 9;
			String finalOutputFile = outputFile;
			if(!finalOutputFile.endsWith(".zip")) {
				finalOutputFile += ".zip";
			}
		    FileOutputStream fout = new FileOutputStream(finalOutputFile);
		    ZipOutputStream zout = new ZipOutputStream(fout);
		    zout.setLevel(level);
		    String entryName = outputFile;
		    if(entryName.endsWith(".zip")) {
		    	entryName = entryName.substring(0, entryName.length() - 4);
		    }
		    ZipEntry ze  = new ZipEntry( getZipEntry(entryName) );
			zout.putNextEntry(ze);
			marshaller.marshal(new JAXBElement(new QName(ns, rootName, ""), xmlObjectClass, xmlObject), zout);
			//marshaller.marshal(new JAXBElement(new QName(rootName), xmlObjectClass, xmlObject), fileWriter);
			zout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
		long endtime = System.currentTimeMillis();
		System.out.println("Writing time = " + (endtime - starttime)/1000.0 + " seconds." );	
	}	
	
	private String getZipEntry(String fileName){
		File file = new File(fileName);
		String zipEntryName = file.getName();
		return zipEntryName;
	}
	
	public static void main(String[] args) {
		 int TRY_SIZE = 200;
        PAIS doc = new PAIS();		        
        doc.setUid("fakeduid");
        PAIS.MarkupCollection markupCollection = new PAIS.MarkupCollection();
        doc.setMarkupCollection(markupCollection);
        
        ArrayList<Markup> markupList =  (ArrayList<Markup>) markupCollection.getMarkup();
      
        
        for (int i = 0; i < TRY_SIZE; i++) {
        	Polygon plgn = new Polygon();
        	plgn.setFormat("svg");
        	plgn.setId( BigInteger.valueOf(i) );
        	String value = "1914,31685 1887,31684 1877,31685 1871,31687 1868,31689 1862,31691 1857,31692 1853,31694 1852,31696 1846,31698 1845,31700 1841,31701 1837,31703 1836,31705 1830,31707 1827,31709 1823,31710 1820,31712 1816,31714 1814,31716 1812,31719 1811,31721 1809,31725 1807,31730 1811,31735 1812,31737 1834,31739 1852,31741 1859,31739 1868,31737 1877,31735 1895,31734 1900,31735 1902,31746 1896,31750 1895,31751 1891,31753 1882,31757 1880,31759 1875,31760 1871,31762 1870,31764 1866,31767 1862,31769 1861,31771 1859,31773 1855,31775 1853,31776 1852,31778 1850,31780 1848,31784 1846,31789 1845,31791 1843,31798 1841,31819 1846,31826 1848,31828 1852,31826 1855,31825 1857,31823 1861,31821 1864,31817 1868,31816 1870,31812 1875,31810 1878,31807 1884,31805 1889,31803 1893,31801 1898,31800 1905,31801 1907,31803 1909,31805 1911,31807 1912,31810 1914,31817 1916,31821 1918,31823 1920,31825 1921,31826 1925,31828 1927,31830 1932,31832 1941,31834 1962,31835 1966,31834 1973,31832 1978,31830 1980,31828 1986,31826 1991,31823 1995,31809 1993,31805 1989,31801 1986,31800 1984,31796 1978,31792 1977,31791 1975,31789 1973,31785 1971,31773 1973,31771 1975,31769 1977,31767 1980,31764 1986,31762 1989,31757 1991,31753 1993,31748 1989,31730 1987,31728 1986,31726 1984,31725 1982,31723 1977,31717 1975,31716 1973,31714 1971,31712 1970,31710 1964,31707 1959,31703" + i + ","+  i;
        	plgn.setPoints(value);
        	value = null;

        	Markup markup = new Markup();
        	markup.setGeometricShape(new Markup.GeometricShape());			        	
        	Markup.GeometricShape mshape = markup.getGeometricShape();
        	mshape.setGeometricShape(plgn);
        	markupList.add(markup);
        	
        	
        }		
		        
        XMLMarshaller tester = new XMLMarshaller();
        String outputFile = "c:\\temp\\dummypais.xml";   
        tester.generateXML(PAIS.class, "PAIS", outputFile, doc);
        tester.generateXMLZip(PAIS.class, "PAIS", outputFile, doc);
			
	}
}

