package edu.emory.cci.pais.dataloader.partition;

import java.sql.ResultSet;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.emory.cci.pais.dataloader.db2helper.PAISDBHelper;

public class ImageBasedPartitioningKeyGenerator extends PartitioningKeyGenerator {
	
	protected Map<Integer, Integer> partition_totalSize;
	protected Map<String, List<String>> imageName_tileNames;
	protected Map<String, Integer> imageName_totalSize;
	protected long totalDataSize;
	
	protected ImageBasedPartitioningKeyGenerator(PAISDBHelper db, int numOfPartitions) {
		super(db, numOfPartitions);
		imageName_tileNames = new HashMap<String, List<String>>();
		imageName_totalSize = new HashMap<String, Integer>();
		partition_totalSize = new HashMap<Integer, Integer>();
		totalDataSize = 0;
	}

	@Override
	protected void generateKeyMap() {
		Set<AbstractMap.SimpleEntry<String, Integer>> tiles = getTileNames_SizesFomStagingdoc();
		
		initializeMaps(tiles);
		
		/* Traverse the images in imageName_totalSize in reverse order (from bigger to smallest) and add the tiles to the
		 * partition in partition_totalSize with the smallest totalSize. Then update the totalSize of the partition
		 * where we just added all the tiles for the image. 
		 */
		while(!imageName_totalSize.isEmpty()) {
			// Get the image with the biggest total size
			String image = getKeyWithMaxValue(imageName_totalSize);
			Integer imageTotalSize = imageName_totalSize.get(image);
			// Remove the image since we don't need it in the tile_partition_map anymore
			imageName_totalSize.remove(image);
			// Get the partition with the smallest total size
			Integer smallestPartition = getKeyWithMinValue(partition_totalSize);
			// Add all the tiles from the image we got to the partition with the smallest size
			for(String tile: imageName_tileNames.get(image)) {
				super.tile_partition_map.put(tile, smallestPartition);
			}
			// Remove the image to decrease the size of the tiles tile_partition_map.
			imageName_tileNames.remove(image);
			
			// Update the partition we just used with the increased size
			partition_totalSize.put(smallestPartition, new Integer(partition_totalSize.get(smallestPartition).intValue() + imageTotalSize.intValue()));
		}
		assert(imageName_tileNames.isEmpty());
	}
	
	/* Create a imageName_totalSize_map that represents the total sum of the sizes of the tiles for each image,
	 * a imageName_tileNames_map that holds all the tiles for each image and a partition_totalSize_map that represents each 
	 * partition and the total sum of all the tiles (from all the images) in each partition.
	 */
	protected void initializeMaps(Set<AbstractMap.SimpleEntry<String, Integer>> tiles) {
		for(AbstractMap.SimpleEntry<String, Integer> tile: tiles) {
			String imageName = getImageName(tile.getKey());
			
			if(imageName_tileNames.containsKey(imageName)) {
				imageName_tileNames.get(imageName).add(tile.getKey());
			} else {
				List<String> imageTiles = new ArrayList<String>();
				imageTiles.add(tile.getKey());
				imageName_tileNames.put(imageName, imageTiles);
			}
			
			int tileSize = tile.getValue().intValue();
			if(imageName_totalSize.containsKey(imageName)) {
				int totalSize = imageName_totalSize.get(imageName).intValue() + tileSize;
				imageName_totalSize.put(imageName, new Integer(totalSize));
			} else {
				imageName_totalSize.put(imageName, new Integer(tileSize));
			}
			
			// Add the tileSize to the totalDataSize
			totalDataSize += tileSize;
		}
		
		//Initialize the tile_partition_map of partitions with the total size of each partition to 0
		for(int i=0;i < super.numOfPartitions;i++) {
			partition_totalSize.put(new Integer(i), new Integer(0));
		}
	}

	protected Set<AbstractMap.SimpleEntry<String, Integer>> getTileNames_SizesFomStagingdoc() {
		Set<AbstractMap.SimpleEntry<String, Integer>> result = new HashSet<AbstractMap.SimpleEntry<String, Integer>>();
		
		try {
			ResultSet rs = db.getSqlQueryResult("select length(blob) as tileSize, file_name from pais.stagingdoc where status='INCOMPLETE'");

			while (rs.next()) {
				String tileFileName = rs.getString("file_name");
				String tileName = (new PartitioningHelper()).gettilename(tileFileName);
				int tileSize = rs.getInt("tileSize");
				result.add(new AbstractMap.SimpleEntry<String, Integer>(tileName, new Integer(tileSize)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	protected String getImageName(String tileName) {
		return tileName.replaceAll("-[0-9]{10}-[0-9]{10}", "");
	}
	
	private static String getKeyWithMaxValue(Map<String, Integer> map) {
		Set<String> keys = map.keySet();
		if(keys.isEmpty())
			return null;
		String max = keys.iterator().next();
		for(String k: keys) {
			Integer v = map.get(k);
			if(map.get(max).compareTo(v) < 0)
				max = k;
		}
		return max;
	}
	
	private static Integer getKeyWithMinValue(Map<Integer, Integer> map) {
		Set<Integer> keys = map.keySet();
		if(keys.isEmpty())
			return null;
		Integer max = keys.iterator().next();
		for(Integer k: keys) {
			Integer v = map.get(k);
			if(map.get(max).compareTo(v) > 0)
				max = k;
		}
		return max;
	}
	
	public Map<Integer, Integer> getPartitionsSizes() {
		return partition_totalSize;
	}
	
	public Map<String, List<String>> getImageName_tileNames() {
		return imageName_tileNames;
	}

	public Map<String, Integer> getImageName_totalSize() {
		return imageName_totalSize;
	}
}