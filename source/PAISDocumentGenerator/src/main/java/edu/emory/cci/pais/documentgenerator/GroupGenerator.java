package edu.emory.cci.pais.documentgenerator;
import java.math.BigInteger;
import edu.emory.cci.pais.model.*;


/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class provides interfaces to construct Annotation object
 */

public class GroupGenerator {
	private Group group = new Group(); 
	
	public GroupGenerator(){		
	}
	
	public GroupGenerator(BigInteger id, String uid, String name, String uri){
		setGroup(id, uid, name, uri);
	}	

	public Group getGroup(){
		return group;
	}
	
	private void setGroup(BigInteger id, String uid, String name, String uri){
		group.setId(id);
		group.setUid(uid);
		group.setName(name);
		group.setUri(uri);
	}	
}
