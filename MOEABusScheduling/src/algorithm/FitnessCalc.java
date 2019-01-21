package algorithm;

import java.util.Vector;
import core.*;

public class FitnessCalc {
	public static Vector<Bus> buses = null;
	public static String tripId= null;

    static Solution getFitness(Individual individual) {
    	int[] frontGenes = new int[buses.size()-1];
    	for(int i=0; i<frontGenes.length;i++)
    	{
    		frontGenes[i] = individual.getGene(i); 
    	}
        Solution s =  Network.getObjectiveValue(frontGenes,buses,tripId);
        return s;
    }
    
}