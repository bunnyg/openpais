package edu.emory.cci.pais.documentgenerator;
import java.math.BigInteger;
import edu.emory.cci.pais.model.*;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class is used  to construct Project object.
 */

public class ProjectGenerator {
	private Project project = new Project(); 
	
	public ProjectGenerator(){		
	}
	
	public ProjectGenerator(BigInteger id, String uid, String name, String uri){
		setProject(id, uid, name, uri);
	}	

	public Project getProject(){
		return project;
	}
	
	private void setProject(BigInteger id, String uid, String name, String uri){
		project.setId(id);
		project.setUid(uid);
		project.setName(name);
		project.setUri(uri);
	}	
}
