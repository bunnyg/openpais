/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.emory.cci.pais.dataloader.partition;

import java.sql.ResultSet;
import java.util.*;


import edu.emory.cci.pais.dataloader.db2helper.DBConfig;
import edu.emory.cci.pais.dataloader.db2helper.PAISDBHelper;
/**
 *
 * @author chenweian2008
 */
public class TileBasedPartitioningKeyGenerator extends PartitioningKeyGenerator {
	
    

    protected TileBasedPartitioningKeyGenerator(PAISDBHelper db, int numOfPartitions){
        super(db, numOfPartitions);
    }

     protected void generateKeyMap(){
    	 List<Integer> list = new ArrayList<Integer>();
         List<String>  tilenamelist=new ArrayList<String>();
         List<Integer> sequenceNoList=new ArrayList<Integer>();

         List<String[]>compositeList= new ArrayList<String[]>();



    	 try {
    		 ResultSet rs = db.getSqlQueryResult("select length(blob) as t, file_name, sequence_number as s from pais.stagingdoc where status='INCOMPLETE'");

    		 while (rs.next ())
             {
                 String nameVal = rs.getString ("t");
                 list.add(Integer.parseInt(nameVal));


                 String tilename= rs.getString ("file_name");
                 //System.out.println(nameVal+'\t'+tilename);
                 tilename=(new PartitioningHelper()).gettilename(tilename);

                 tilenamelist.add(tilename);

                 String[] tileAndSize={tilename, nameVal.toString()};

                 compositeList.add(tileAndSize);

                 Integer sequence_number=rs.getInt("s");
                 sequenceNoList.add(sequence_number);                //System.out.println ("BLOB = " + nameVal);
                //here we create tile_partition_map to
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

           Collections.sort(compositeList, new Comparator<String[]>()
                {
                public int compare(String[] o1, String[] o2) {
                return Integer.parseInt(o2[1]) - Integer.parseInt(o1[1]);
                }});


		TileBasedPartitioner newindex=new TileBasedPartitioner();

       // this.map = newindex.partition(list, tilenamelist, numOfPartitions);
                super.tile_partition_map=newindex.partition(compositeList, numOfPartitions);
     }

    

    public static void main(String[] args) {
    	TileBasedPartitioningKeyGenerator partitoningKeyGenerator = new TileBasedPartitioningKeyGenerator(new PAISDBHelper(new DBConfig("/Users/chenweian2008/dbconfig.xml")), 3);
    	System.out.println("Map: "+partitoningKeyGenerator.tile_partition_map);
    }

}

