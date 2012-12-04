package edu.emory.cci.pais.dataloader.db2helper;


/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * Read database configuration
 * Apache commons-lang package needs to be included in the library 
 */

import java.io.File;
import java.util.Properties;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;


public class DBConfig {
	private String [] configKeys = {"db.host", "db.port", "db.database", "db.username", 
			"db.passwd", "db.autocommit", "markup.spatialtablename", "markup.largeboundary" };
	private String [] keys = {"host", "port", "database", "username", "passwd", "autocommit", "spatialtablename", "largeboundary"};	
	private XMLConfiguration config = null;
	private boolean emptyFile = false;
	private Properties props = null;

	public DBConfig(File configFile) {
		try
		{
			if ( configFile != null){
				config = new XMLConfiguration( configFile );
				setProperties();
			}
			else emptyFile = true;
		}
		catch(ConfigurationException cex)
		{
		   cex.printStackTrace();		   
		}
	}
	
	public DBConfig(String configFileName) {
		File configFile = new File(configFileName);
		try
		{
			if ( configFile != null){
				config = new XMLConfiguration( configFile );
				setProperties();
			}
			else emptyFile = true;
		}
		catch(ConfigurationException cex)
		{
		   cex.printStackTrace();		   
		}
	}
	
	private void setProperties(){
		 props = new Properties();
		for (int i = 0; i < keys.length; i++){
			String value = config.getString( configKeys[i]);
			if(value != null)
				props.setProperty(keys[i], value);
		}
	}

	public boolean isEmptyConfigFile(){
		return emptyFile;
	}

	public Properties getProperties(){	
		if (emptyFile == true) return null;
		else  return props;
	}


	public static void main(String[] args) {
		//String file = "c:\\temp\\dbconfig.xml";
		String file = "conf/dbconfig.xml";
		DBConfig config = new DBConfig(new File(file) );
		Properties props = config.getProperties();
		props.list(System.out);
	}

}
