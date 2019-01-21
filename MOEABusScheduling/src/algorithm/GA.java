package algorithm;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.Vector;

import org.jfree.ui.RefineryUtilities;

import core.*;
import helper.ChartPainter;

public class GA {
    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.015;
    private static final int tournamentSize = 2;
    private static final int generation = 20;
    private static final int popsize = 20;
    private static final boolean elitism = true;
    
    public static int mingap;
    public static int maxgap;
    public static Vector<Bus> buses1;
    public static int gaplength;
    public static String tripId1;

    public static Population evolvePopulation(Population pop, Vector<Bus> bs, String Tid) {
    		FitnessCalc.buses = bs;
    		FitnessCalc.tripId = Tid;
        Population newPopulation = new Population(pop.size(), true);
        // elitism
        int elitismOffset;
        if (elitism) {
            newPopulation.saveIndividual(0, pop.getFittest());
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }
        // crossover
        for (int i = elitismOffset; i < pop.size(); i++) {
            Individual indiv1 = tournamentSelection(pop);
            Individual indiv2 = tournamentSelection(pop);
            Individual newIndiv = crossover(indiv1, indiv2);
            newPopulation.saveIndividual(i, newIndiv);
        }
        // mutate
        for (int i = elitismOffset; i < newPopulation.size(); i++) {
            mutate(newPopulation.getIndividual(i));
        }
        return newPopulation;
    }

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
            	int gene = rnd.nextInt(maxgap-mingap+1) + mingap;
                indiv.setGene(i, gene);
            }
        }
    }

    /**
     * choose the best individual from randomly selected population whose size is tournamentSize
     * @param pop
     * @return
     */
    private static Individual tournamentSelection(Population pop) {
        Population tournament = new Population(tournamentSize, false);
        for (int i = 0; i < tournamentSize; i++) {
        	int randomId = (int) (Math.random() * pop.size());
            tournament.saveIndividual(i, pop.getIndividual(randomId));
        }
        Individual fittest = tournament.getFittest();
        return fittest;
    }
    
    public static void run() {
    		gaplength = buses1.size() - 1;  		
    		Population myPop = new Population(popsize, true);
    		for (int i = 0; i <generation; i++) {
    			System.out.println(i);
    			myPop = evolvePopulation(myPop, buses1, tripId1);
    		}
    		Individual ft = new Individual();
    		ft = myPop.getFittest();
    	
    		// print information
    		System.out.println(gaplength);
    		System.out.println("GA:");
    		System.out.print("Best Gap: ");
    		int[] bestGaps=new int[gaplength];
    		for (int i = 0; i <gaplength; i++){
    			bestGaps[i]=ft.getGene(i);
    		}
    		System.out.print(Arrays.toString(bestGaps));
    		double obj1=ft.geto1();
    		double obj2=ft.geto2();
    		double obj3=ft.geto3();
    		double bestFit=-ft.getFitness();
    		double averageWaitingTime = ft.getAverageWaitingTime();
    		int tripNum = ft.getTripNum();
    		System.out.println();
    		System.out.printf("Total objective value: %.0f\n", bestFit);
    		System.out.printf("Objective 1 (waiting time): %.0f\n", obj1);
    		System.out.printf("Objective 2 (overload): %.0f\n", obj2);
    		System.out.printf("Objective 3 (operation cost): %.0f\n", obj3);
    		System.out.printf("Average waiting time (minutes): %.0f\n", averageWaitingTime);
    		System.out.printf("The number of trips: %d\n", tripNum);
    		System.out.println();
    		Vector<Bus> ftbuses=ft.getbus();	
    		double[][] rates = new double[22][tripNum];
    		int[][] headways = new int[22][tripNum];
    		String[][] times = new String[22][tripNum];
    		for(int k=0; k<22; k++) {
    			for(int i=0; i<tripNum;i++) {
    				Bus b=ftbuses.get(i);
    				Vector<Stop> trip=b.getTrip();
    				Stop s=trip.get(k);
    				rates[k][i] = s.getArrivalRate();
    				headways[k][i] = s.getHeadWay();
        			int hour = s.getDepartureTime().get(Calendar.HOUR_OF_DAY);
        		    int min = s.getDepartureTime().get(Calendar.MINUTE);
        		    times[k][i] = Integer.toString(hour) +":" + Integer.toString(min);
    	        }
    		}
    		
    		ChartPainter chart = new ChartPainter("Stop 0", "Arrival Rate vs Headway", times[0],rates[0],headways[0]);
    		chart.pack( );
    		RefineryUtilities.centerFrameOnScreen( chart );
    		chart.setVisible( true );
    }
    
}

