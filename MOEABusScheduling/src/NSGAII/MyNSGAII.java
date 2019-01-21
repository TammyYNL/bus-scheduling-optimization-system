package NSGAII;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import algorithm.FitnessCalc;
import algorithm.Individual;
import algorithm.Population;
import core.Bus;

public class MyNSGAII {
    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.015;
    public static int mingap = 10;
    public static int maxgap = 20;
    public static Vector<Bus> buses1 = new Vector<Bus>();
    public static int gaplength;
    public static String tripId1=null;
    
    private static Individual crossover(Individual indiv1, Individual indiv2) {
        Individual newSol = new Individual();
        for (int i = 0; i < indiv1.size(); i++) {
            if (Math.random() <= uniformRate) {
                newSol.setGene(i, indiv1.getGene(i));
            } else {
                newSol.setGene(i, indiv2.getGene(i));
            }
        }
        return newSol;
    }

    private static void mutate(Individual indiv) {
        Random rnd = new Random();
        for (int i = 0; i < indiv.size(); i++) {
            if (Math.random() <= mutationRate) {
            		int gene = rnd.nextInt(maxgap-mingap+1) + mingap; // fixed
                indiv.setGene(i, gene);
            }
        }
    }

    // binary tournamentSelection, tournamentSize = 2
    private static Individual tournamentSelection(Population pop) {
    		int randomId1 = (int) (Math.random() * pop.size());	
    		Individual indiv1 = pop.getIndividual(randomId1);
    		
    		int randomId2 = (int) (Math.random() * pop.size());	
    		Individual indiv2 = pop.getIndividual(randomId2);
    		
    		if (indiv1.getRank() > indiv2.getRank()) {
                return indiv2;
    		}
        else if(indiv1.getRank() == indiv2.getRank()) {
            if(indiv1.getDistance() < indiv2.getDistance()) {
            		return indiv2;
            	}
            else {
            		return indiv1;
            }
        	}
        else {
        		return indiv1;
        }
    }
    
    public static Population nextParent(Population pop, Population offSpring, Vector<Bus> bs, String Tid) {
		FitnessCalc.buses = bs;
		FitnessCalc.tripId = Tid;
		Population combination = new Population((pop.size())*2, true);
		for(int i=0; i<pop.size(); i++) {
			combination.saveIndividual(i, pop.getIndividual(i));
		}
		for(int i=0; i<offSpring.size(); i++) {
			combination.saveIndividual(pop.size()+i, offSpring.getIndividual(i));
		}
		LinkedList<LinkedList<Individual>> fronts = fastNonDominatedSort(combination);
		Population newPopulation = new Population(pop.size(), true);
		int currentSize = 0;
		Iterator<LinkedList<Individual>> iterator = fronts.iterator();
		while(iterator.hasNext()) {
			LinkedList<Individual> front = iterator.next();
			crowdingDistanceAssignment(front);
			if ((currentSize + front.size()) <= pop.size()) {
				for(int i=0; i<front.size(); i++) {
					newPopulation.saveIndividual(currentSize+i, front.get(i));
				}
				currentSize+=front.size();
			}
			else {
				Collections.sort(front, new MyDistanceSorter());
				for(int i=0; i<(pop.size()-currentSize); i++) {
					newPopulation.saveIndividual(currentSize+i, front.get(i));
				}
				break;				
			}
		}	
    		return newPopulation;
    }
    
    public static void crowdingDistanceAssignment(LinkedList<Individual> front) {
		Collections.sort(front, new MyO1Sorter());
		front.getFirst().distance = Double.POSITIVE_INFINITY;
		front.getLast().distance = Double.POSITIVE_INFINITY;
		double maxMinGap1 = front.getLast().geto1() - front.getFirst().geto1();
		for(int i=1; i<front.size()-1; i++) {
			front.get(i).distance += (front.get(i+1).geto1() - front.get(i-1).geto1()) / maxMinGap1;
		}
		
		Collections.sort(front, new MyO3Sorter());
		front.getFirst().distance = Double.POSITIVE_INFINITY;
		front.getLast().distance = Double.POSITIVE_INFINITY;
		double maxMinGap3 = front.getLast().geto3() - front.getFirst().geto3();
		for(int i=1; i<front.size()-1; i++) {
			front.get(i).distance += (front.get(i+1).geto3() - front.get(i-1).geto3()) / maxMinGap3;
		}		
}
    
    public static Population makeNewPop(Population pop, Vector<Bus> bs, String Tid) {
    		FitnessCalc.buses=bs;
		FitnessCalc.tripId=Tid;
    		Population newPopulation = new Population(pop.size(), true);
        for (int i = 0; i < pop.size(); i++) {
            Individual indiv1 = tournamentSelection(pop);
            Individual indiv2 = tournamentSelection(pop);
            Individual newIndiv = crossover(indiv1, indiv2);
            newPopulation.saveIndividual(i, newIndiv);
        }

        for (int i = 0; i < newPopulation.size(); i++) {
            mutate(newPopulation.getIndividual(i));
        }
        return newPopulation;
    }
     
    public static boolean dominate(Individual indiv1, Individual indiv2) {
    		if(indiv1.geto1() < indiv2.geto1())
    		{
    			if(indiv1.geto3() <= indiv2.geto3())
    			{
    				return true;
    			}
    			else 
    			{
    				return false;
    			}
    		}
    		else if(indiv1.geto1() == indiv2.geto1())
    		{
    			if(indiv1.geto3() < indiv2.geto3())
    			{
    				return true;
    			}
    			else
    			{
    				return false;
    			}
    		}
    		else
    		{
    			return false;
    		}
    }
    
    // test
    public static LinkedList<LinkedList<Individual>> fastNonDominatedSort(Population pop) {
    		LinkedList<LinkedList<Individual>> fronts = new LinkedList<LinkedList<Individual>>();
    		fronts.add(new LinkedList<Individual>());
    		for(int i=0; i<pop.size(); i++) {
    			Individual indiv = pop.getIndividual(i);
    			System.out.println("------" + i);
    	        indiv.indivDominated = new LinkedList<Individual>();  
    	        indiv.numDominates = 0;
    	        for(int j=0; j<pop.size(); j++) {
    	        		if(dominate(indiv, pop.getIndividual(j))) {
    	        			System.out.printf("o1 is %.0f\n", indiv.geto1());
    	        			indiv.indivDominated.add(pop.getIndividual(j));
    	        		}
    	        		else if(dominate(pop.getIndividual(j), indiv)) {
    	        			indiv.numDominates+=1;
    	        		}
    	        }
    	        if(indiv.numDominates==0) {
    	        		pop.getIndividual(i).setRank(1);
    	        		fronts.get(0).add(indiv);
    	        }
    		}
    		
    		int i = 0;
    		while(!fronts.get(i).isEmpty()) {
    			LinkedList<Individual> qs = new LinkedList<Individual>();
    			Iterator<Individual> iterator = fronts.get(i).iterator();
    			while(iterator.hasNext()){
    				Individual p = iterator.next();
    				Iterator<Individual> iterator2 = p.indivDominated.iterator();
    				while(iterator2.hasNext()) {
    		    			Individual q = iterator2.next();
    		    			q.numDominates -=1;
    		    			if(q.numDominates == 0) {
    		    				q.setRank(i+2);
    		    				qs.add(q);
    		    			}
    				}
    			}
    			i++;
    			fronts.add(qs);
    		}    		
    		return fronts;
    }
    
}
