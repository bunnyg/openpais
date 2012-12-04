package edu.emory.cci.pais.api;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import java.util.StringTokenizer;
import java.net.URI;
import java.net.URISyntaxException;

/**  A Jersey ContainerRequestFilter that validates the query path of PAIS database 
 * 
 * @author twu37
 *
 */
public class QueryPathFilter implements ContainerRequestFilter {

	public ContainerRequest filter(ContainerRequest arg0) {
		String requestPath = arg0.getPath();
		String[] legalPath = {"pais/list/paisuids", "pais/list/imageuids", "pais/list/slideuids", "pais/list/patientuids", 
				"pais/markups/density/tile", "pais/markups/density/image", "pais/markups/density/document", "pais/markups/density/groupbyimage","pais/markups/density/groupbydocument", "pais/markups/boundaries/tile", "pais/markups/boundaries/window", "pais/markups/boundaries/polygon", "pais/markups/boundaries/containingpoint", "pais/markups/boundaries/intersection", "pais/markups/centroids/tile", "pais/markups/centroids/window", "pais/markups/centroids/polygon", 
				"pais/documents/tile", "pais/features/tile", "pais/features/aggregation/document", "pais/features/aggregation2/document", "pais/features/aggregation/image", "pais/features/aggregation/patient", "pais/features/histogram", 
				"images/list/imageuids", "images/list/tilenames", "images/list/patients", "images/list/tilenameofpoint", 
				"images/image/tile", "images/image/window", "images/image/wsi", "images/thumbnail/tile", "images/thumbnail/wsi", "images/overlay/tile", "images/overlay/window", "images/similarity/feature", "images/similarity/cluster", "images/similarity/getImageFromFile"};
		if (!requestPath.equals("")) {
			StringTokenizer st = new StringTokenizer(requestPath,";");
			String requestPathNoPara = st.nextToken();
			for (String path: legalPath) {
				if (requestPath.equalsIgnoreCase(path)) 
					return arg0;
				if (requestPathNoPara.equalsIgnoreCase(path)) 
					return arg0;
			}
		}
		
		String requestURIStr = arg0.getBaseUri().toString() + "errpath";
		//System.out.println(requestURIStr);
		try {
			arg0.setUris(arg0.getBaseUri(), new URI(requestURIStr));
		} catch (URISyntaxException e) {
			System.out.println("Couldn't parse string into URI.");
			e.printStackTrace();
		}
		System.out.println(arg0.getAbsolutePath().toString());
		return arg0; //return a customized error report page
	}

}
