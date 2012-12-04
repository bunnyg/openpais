package edu.emory.cci.pais.dataloader.partition;

import java.util.HashMap;
import java.util.Map;

import edu.emory.cci.pais.dataloader.db2helper.PAISDBHelper;

public abstract class PartitioningKeyGenerator {
	protected PAISDBHelper db = null;
	protected int numOfPartitions = 0;
	protected Map<String, Integer> tile_partition_map = new HashMap <String, Integer> ();
	
	public enum Partitioner {
		TILE_BASED,
		IMAGE_BASED
	}
	
	public static PartitioningKeyGenerator getInstance(Partitioner partitioner, PAISDBHelper db, int numberOfPartitions) {
		switch(partitioner) {
			case TILE_BASED: return new TileBasedPartitioningKeyGenerator(db, numberOfPartitions);
			case IMAGE_BASED: return new ImageBasedPartitioningKeyGenerator(db, numberOfPartitions);
			default: throw new IllegalArgumentException("No such partitioner: '"+partitioner.name()+"'");
		}
	}
	
	public int getPartitionKeyByTilename(String tilename){
        if(numOfPartitions == 0)
            throw new NoPartitioningException("The constructor was initialized with 0 partitions.");
       return tile_partition_map.get(tilename).intValue();
    }
	
	protected PartitioningKeyGenerator(PAISDBHelper db, int numOfPartitions){
        this.db = db;
        this.numOfPartitions = numOfPartitions;

        generateKeyMap();
    }
	
	abstract protected void generateKeyMap();
	
	public Map<String, Integer> getTile_partition_map() {
		return tile_partition_map;
	}
}
