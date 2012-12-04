package edu.emory.cci.pais.documentgenerator;
import java.math.BigInteger;
import edu.emory.cci.pais.model.*;


/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class is used  to construct User object.
 */

public class UserGenerator {
	private User user= new User(); 
	
	public UserGenerator(){		
	}
	
	public UserGenerator(BigInteger id, String loginName, String name, int numberWithinRoleOfClinicalTrial, String roleInTrial){
		setUser(id, loginName, name, numberWithinRoleOfClinicalTrial, roleInTrial);
	}	

	public UserGenerator(BigInteger id, String loginName, String name){
		setUser(id, loginName, name, -1, null);
	}		
	
	public User getUser(){
		return user;
	}
	
	private void setUser(BigInteger id, String loginName, String name, int numberWithinRoleOfClinicalTrial, String roleInTrial){
		user.setId(id);
		user.setLoginName(loginName);
		user.setName(name);		
		if (numberWithinRoleOfClinicalTrial > 0 )
			user.setNumberWithinRoleOfClinicalTrial(BigInteger.valueOf( numberWithinRoleOfClinicalTrial) );
		if (roleInTrial != null)
			user.setRoleInTrial(roleInTrial);
	}	
}
