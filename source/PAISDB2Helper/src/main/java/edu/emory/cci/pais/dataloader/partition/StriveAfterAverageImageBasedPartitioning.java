package edu.emory.cci.pais.dataloader.partition;

import java.util.AbstractMap;
import java.util.Set;

import edu.emory.cci.pais.dataloader.db2helper.PAISDBHelper;

public class StriveAfterAverageImageBasedPartitioning extends ImageBasedPartitioningKeyGenerator {

	protected StriveAfterAverageImageBasedPartitioning(PAISDBHelper db, int numOfPartitions) {
		super(db, numOfPartitions);
	}
	
	@Override
	protected void generateKeyMap() {
		Set<AbstractMap.SimpleEntry<String, Integer>> tiles = getTileNames_SizesFomStagingdoc();
		
		initializeMaps(tiles);
		
		int averageSizeOfPartition = Math.round(totalDataSize / numOfPartitions);
		
		for(int i=0;i < numOfPartitions;i++) {
			
		}
	}

}
