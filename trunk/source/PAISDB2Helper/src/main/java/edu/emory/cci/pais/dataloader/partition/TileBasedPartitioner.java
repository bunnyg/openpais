/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.emory.cci.pais.dataloader.partition;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;


public class TileBasedPartitioner {

    public int min(long a[])
    {
        int temp=0;

        for(int i=0;i<a.length;i++)
        {
            if(a[i]<a[temp])
            {
                temp=i;
            }
        }

        //System.out.println("temp"+temp);

        return temp;

    }

    /*
     * Weian: please revise the code to make it generic for any number of partitions
     */
    public Map<String, Integer> partition(List<String[]> compositeList, int numOfPartitions)
    {

    	Map<String, Integer> result = new HashMap<String, Integer>();


        long [] partitionList= new long[numOfPartitions];

        int i=0;

        ListIterator<String[]> compositeListIterator = compositeList.listIterator(compositeList.size());
        //ListIterator<Integer> filesizelist = s.listIterator(s.size());
        //ListIterator<String> tilenamelist = tilename.listIterator(tilename.size());


        for (;compositeListIterator.hasPrevious() ; )
              {
                String[] tileSizePair = compositeListIterator.previous();

                if(!result.keySet().contains(tileSizePair[0]))
                {

                    int partitionIndex=min(partitionList);
                    partitionList[partitionIndex]+=Integer.parseInt(tileSizePair[1]);
                    result.put(tileSizePair[0], min(partitionList));
                }
                else
                {
                    partitionList[result.get(tileSizePair[0])]+=Integer.parseInt(tileSizePair[1]);

                }
                 i++;

              }

        return result;

    }

}
