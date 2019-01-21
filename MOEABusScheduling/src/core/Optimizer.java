package core;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Vector;

import javax.swing.WindowConstants;

import org.jfree.ui.RefineryUtilities;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.variable.EncodingUtils;

import algorithm.*;
import helper.ChartPainter;
import helper.Config;
import helper.ScatterPainter;
import MOEA.*;

public class Optimizer {
	Session ses;
	Vector<Bus> buses1;
	Vector<Stop> trip1;
	String tripID1;
	Calendar startTime;
	Calendar endTime;
	int gap1_min;
	int gap1_max;
	int miniRestTime;
	double weight1;
	double weight2;
	double weight3;
	Vector<Bus> updatedBuses1;
	
	public Optimizer(Session ses, Vector<Bus> buses1, Vector<Stop> trip1,
			String tripID1, Calendar startTime, Calendar endTime, int gap1_min,
			int gap1_max, int miniRestTime, double weight1,
			double weight2, double weight3) {
		super();
		this.ses = ses;
		this.buses1 = buses1;
		this.trip1 = trip1;
		this.tripID1 = tripID1;
		this.startTime = startTime;
		this.endTime = endTime;
		this.gap1_min = gap1_min;
		this.gap1_max = gap1_max;
		this.miniRestTime = miniRestTime;
		this.weight1 = weight1;
		this.weight2 = weight2;
		this.weight3 = weight3;
	}
	
	public Vector<Bus> buses1copy(Vector<Bus> buses) {
		Vector<Bus> editablebuses1 = new Vector<Bus>();
		for (int i = 0; i < buses.size(); i++) {
			Bus bus = buses.get(i).clone();
			editablebuses1.addElement(bus);
		}
		return editablebuses1;
	}	
	
	public Vector<Stop> tripcopy(Vector<Stop> trip) {
		Vector<Stop> tripnew = new Vector<Stop>();
		for (int i = 0; i < trip.size(); i++) {
			Stop stop = trip.get(i).clone();
			tripnew.addElement(stop);
		}
		return tripnew;
	}
	
	/**
	 * @param editablebuses1
	 * @return buses which are already dispatched
	 */
	public Vector<Bus> seegaps(Vector<Bus> editablebuses1) {
		Vector<Bus> starts = new Vector<Bus>();
		for (int i = 0; i < editablebuses1.size(); i++) {
			if (editablebuses1.elementAt(i).depotStatus == false) {
				starts.addElement(editablebuses1.elementAt(i));
			}
		}
		return starts;
	}
	
	/**
	 * initialize buses between startTime and endTime use minGap and minRestTime
	 */
	public Vector<Bus> initialbuses(Vector<Bus> buses, String tripID) {
		Vector<Bus> busList = seegaps(buses);
		Vector<Bus> newbus = new Vector<Bus>();
		Calendar BaseClone;
		if(busList.size() != 0) {
			// sort by departure time
			Collections.sort(busList);
			// find the last bus leaves the depot
			Bus firstOne = busList.lastElement();
			// use this bus's departure time as a base line
			Calendar departureTimeBase = firstOne.departureTime;
			BaseClone = (Calendar) departureTimeBase.clone();
		}
		else {
			BaseClone = (Calendar) startTime.clone();
			BaseClone.add(Calendar.MINUTE, -gap1_min);
		}
		// initialize the departure time for the in depot buses
		int count = 1;
		for (int i = 0; i < buses.size(); i++) {
			Bus bus1 = buses.get(i);
			if (bus1.depotStatus == true) {
				Calendar c = (Calendar) BaseClone.clone();
				c.add(Calendar.MINUTE, count * gap1_min); // initialize with minimal gap
				bus1.setTrip(tripcopy(trip1));
				bus1.setDepartureTime(c);
				count++;
			}
		}
		
		for (int i = 0; i < buses.size(); i++) {
			Calendar departureTime = buses.get(i).departureTime;
			Calendar c = (Calendar) departureTime.clone();
			double period = PredictionHandler.getTravelTime(tripID, departureTime, ses);
			int timeSlot = (int) period + miniRestTime; // calculate timeSlot with miniRestTime
			if (c.before(startTime)) {
				Calendar c1 = (Calendar) c.clone();
				c1.add(Calendar.MINUTE, timeSlot);
				if (c1.after(endTime)) {
					buses.get(i).setDepartureTime(c1); 
				} else if (c1.before(startTime)) {
					while (c1.before(startTime)) {
						period = PredictionHandler.getTravelTime(tripID, c1, ses);
						timeSlot = (int) period + miniRestTime;
						c1.add(Calendar.MINUTE, timeSlot);
						buses.get(i).setDepartureTime(c1);
					}
					if(c1.before(endTime)) {
						newbus.addElement(buses.get(i));
					}
				} else {
					buses.get(i).setDepartureTime(c1);
					newbus.addElement(buses.get(i));
				}
			} else if (c.after(endTime)) {
				buses.get(i).setDepartureTime(c);
			} else if ((c.after(startTime) || c.equals(startTime)) && c.before(endTime)) {
				newbus.addElement(buses.get(i));
			}
		}
		
		Vector<Bus> fBuses1 = new Vector<Bus>();
		for (int i = 0; i < newbus.size(); i++) {
			Bus bus = newbus.get(i).clone();
			int timeSlot = (int) PredictionHandler.getTravelTime(tripID1, bus.departureTime, ses) + miniRestTime;
			int shift = 1;
			for (Calendar c1=(Calendar)bus.departureTime.clone(); c1.before(endTime); ) {
				Bus bus1 = (Bus) bus.clone();
				bus1.setTrip(tripcopy(trip1));
				Calendar c2 = (Calendar) c1.clone();
				bus1.setDepartureTime(c2);
				bus1.setShift(shift);
				fBuses1.addElement(bus1);
				shift++;
				timeSlot = (int) PredictionHandler.getTravelTime(tripID1, c1, ses) + miniRestTime;
				c1.add(Calendar.MINUTE, timeSlot);
			}
		}
		Collections.sort(fBuses1);
		return fBuses1;
	}
	
	
	public void prePareNetWork() {
		Vector<Bus> trip1buses = buses1copy(buses1);
		Vector<Bus> inibuses = initialbuses(trip1buses, tripID1);
		updatedBuses1 = inibuses;
	}
	
	// visualize result
	public void printMOEAresult(NondominatedPopulation result, Vector<Bus> updatedBuses1) {
		for (int i = 0; i < result.size(); i++) {
			org.moeaframework.core.Solution solution = result.get(i);
			double[] objectives = solution.getObjectives();	
			System.out.println("Solution " + (i+1) + ":");
			int[] gaps = EncodingUtils.getInt(solution);
			System.out.println("    Gap: " + Arrays.toString(gaps));
			System.out.printf("    Objective 1 (waiting time): %.0f\n", objectives[0]);
			System.out.printf("    Objective 2 (cost): %.0f\n", objectives[1]);

			Network.gapsToBuses(gaps, updatedBuses1);
			Network.update(updatedBuses1, tripID1);
			double averageWaitingTime = Network.getAverageWaitingTime(updatedBuses1);
			int tripNum = Network.getTripNum(updatedBuses1);
    			System.out.printf("Average waiting time (minutes): %.0f\n", averageWaitingTime);
    			System.out.printf("The number of trips: %d\n", tripNum);
    			
    			double[][] rates = new double[22][tripNum];
        		int[][] headways = new int[22][tripNum];
        		String[][] times = new String[22][tripNum];
        		for(int k=0; k<22; k++) {
        			for(int x=0; x<tripNum;x++) {
        				Bus b=updatedBuses1.get(x);
        				Vector<Stop> trip=b.getTrip();
        				Stop s=trip.get(k);
        				rates[k][x] = s.getArrivalRate();
        				headways[k][x] = s.getHeadWay();
            			int hour = s.getDepartureTime().get(Calendar.HOUR_OF_DAY);
            		    int min = s.getDepartureTime().get(Calendar.MINUTE);
            		    times[k][x] = Integer.toString(hour) +":" + Integer.toString(min);
        	        }
        		}
        		
        		
        		
        		ChartPainter chart = new ChartPainter("Solution" + Integer.toString(i+1), "Headways", times[0],rates[0],headways[0]);
            	chart.pack( );
            	RefineryUtilities.centerFrameOnScreen( chart );
            chart.setVisible( true );  
             			
        		
		}
		
	}
	
	public void compareModels(NondominatedPopulation result_three, NondominatedPopulation result_two) {
		double[] three_objective1 = new double[result_three.size()];
		double[] three_objective3 = new double[result_three.size()];
		double[] two_objective1 = new double[result_two.size()];
		double[] two_objective2 = new double[result_two.size()];
		
		for (int i = 0; i < result_three.size(); i++) {
			org.moeaframework.core.Solution solution = result_three.get(i);
			double[] objectives = solution.getObjectives();
			three_objective1[i] = objectives[0];
			three_objective3[i] = objectives[2];
		}
		
		for (int i = 0; i < result_two.size(); i++) {
			org.moeaframework.core.Solution solution = result_two.get(i);
			double[] objectives = solution.getObjectives();
			two_objective1[i] = objectives[0];
			two_objective2[i] = objectives[1];
		}
		
		ScatterPainter scatter = new ScatterPainter(three_objective1, three_objective3, two_objective1, two_objective2);
		scatter.setSize(800, 400);
		scatter.setLocationRelativeTo(null);
		scatter.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		scatter.setVisible(true);
			
	}
	
	public void run() {
		prePareNetWork();

		Network.ses = ses;
		Network.buses1 = updatedBuses1;
		Network.startTime = startTime;
		Network.endTime = endTime;
		Network.tripID1 = tripID1;
		Network.miniRestTime = miniRestTime;
		Network.weight1 = weight1;
		Network.weight2 = weight2;
		Network.weight3 = weight3;

		GA.mingap = gap1_min;
		GA.maxgap = gap1_max;
		GA.buses1 = updatedBuses1;
		GA.tripId1 = tripID1;
				
		// To use GA, uncomment the next line
		// GA.run();
				
		/*
		// use MOEA and three objectives model	
		NondominatedPopulation result_three = new Executor()
				.withProblemClass(BusProjectThreeO.class, updatedBuses1, tripID1, gap1_min, gap1_max)
				.withAlgorithm(Config.algorithmName) // can change algorithm, NSGAII, SPEA2, PAES, etc.
				.withMaxEvaluations(Config.MOEAMaxEvaluation)
				.run();
		System.out.println("Three objective: ");
		printMOEAresult(result_three, updatedBuses1);
		*/
		
		// use MOEA and two objectives model
		NondominatedPopulation result_two = new Executor()
				.withProblemClass(BusProject.class, updatedBuses1, tripID1, gap1_min, gap1_max)
				.withAlgorithm(Config.algorithmName) // can change algorithm, NSGAII, SPEA2, PAES, etc.
				.withMaxEvaluations(Config.MOEAMaxEvaluation)
				.run();
		System.out.println("Two objective: ");
		printMOEAresult(result_two, updatedBuses1);
		
		//compareModels(result_three, result_two);
		
			
	}
		
	
}
