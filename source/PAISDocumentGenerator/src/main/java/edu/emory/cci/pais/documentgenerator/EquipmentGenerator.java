package edu.emory.cci.pais.documentgenerator;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAttribute;
import edu.emory.cci.pais.model.*;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class provides interfaces to construct Annotation object
 */

public class EquipmentGenerator {
	private Equipment equipment = new Equipment(); 
	
	public EquipmentGenerator(){		
	}
    
	public EquipmentGenerator(BigInteger id, String manufacturer, String manufacturerModelName, String softwareVersion){
		setEquipment(id, manufacturer, manufacturerModelName, softwareVersion);
	}	
	
	public Equipment getEquipment(){
		return equipment;
	}
	
    
	private void setEquipment(BigInteger id, String manufacturer, String manufacturerModelName, String softwareVersion){
		equipment.setId(id);
		equipment.setManufacturer(manufacturer);
		equipment.setManufacturerModelName(manufacturerModelName);
		equipment.setSoftwareVersion(softwareVersion);
	}	
	
	public static void main(String args[]){
		Equipment equip = new Equipment(); 
		BigInteger equipID = BigInteger.valueOf(100);
		String manufacturer = "Dummy manufacturer";
		String manufacturerModelName = "Dummy model";
		String softwareVersion = "ver 0.1";
		
	
		equip.setId(equipID);
		equip.setManufacturer(manufacturer);
		equip.setManufacturerModelName(manufacturerModelName);
		equip.setSoftwareVersion(softwareVersion);
		
		XMLMarshaller tester = new XMLMarshaller();
		String outputFile = "Equipment.xml";
		tester.generateXML(Equipment.class, "Equipment", outputFile, equip);		
	}
}
