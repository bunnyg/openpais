package edu.emory.cci.pais.documentgenerator;
import java.math.BigInteger;
import edu.emory.cci.pais.model.*;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0 
 * This class is used  to construct Region object.
 */

public class RegionGenerator {
	private Region region = new Region(); 
	public RegionGenerator(){		
	}
	
	public RegionGenerator(BigInteger id, String name, double x, double y, double height, double width, 
			double zoomResolution, double coordinateResolution, String coordinateReference){
		setRegion(id, name, x, y, height, width, 
				zoomResolution, coordinateResolution, coordinateReference);
	}	

	public Region getRegion(){
		return region;
	}
	
	private void setRegion(BigInteger id, String name, double x, double y, double height, double width, 
			double zoomResolution, double coordinateResolution, String coordinateReference){
		region.setId(id);
		region.setName(name);
		region.setX(Double.valueOf(x));
		region.setY(Double.valueOf(y));
		region.setHeight(Double.valueOf(height) );
		region.setWidth(Double.valueOf(width) );
		region.setZoomResolution(Double.valueOf(zoomResolution));
		region.setCoordinateReference(coordinateReference);
	}
	
}
