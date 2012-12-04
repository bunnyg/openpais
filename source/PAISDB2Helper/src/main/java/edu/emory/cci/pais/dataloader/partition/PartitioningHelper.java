/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.emory.cci.pais.dataloader.partition;

/**
 *
 * @author chenweian2008
 */
/*gbm0.1.ndpi-0000036864-0000000000.tif.grid4.mat.xml.zip       ->  gbm0.1-0000036864-0000000000

*/
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PartitioningHelper {

	public String gettilename(String s) {
		Pattern regionPattern = Pattern.compile("-\\d{10}-\\d{10}");
		Matcher matcher = regionPattern.matcher(s);
		// Check all occurance
		if (matcher.find()) {
			s = s.substring(s.lastIndexOf(File.separator) + 1, matcher.end());
			s = s.replaceFirst("\\.[a-zA-Z0-9_]+\\-", "-");
			return s;
		} else {
			throw new WrongTileNameException("Tile name found: '" + s + "'.");
		}
	}
}
