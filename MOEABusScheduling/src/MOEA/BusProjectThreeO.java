package MOEA;

import java.util.Vector;
import org.moeaframework.core.Problem;
import org.moeaframework.core.variable.EncodingUtils;
import core.*;

public class BusProjectThreeO implements Problem {
	int gaplength;
	int numOfObjectives = 3;
	int numOfConstraints = 1;
	int mingap;
	int maxgap;
	Vector<Bus> buses;
	String tripID;

	public BusProjectThreeO(Vector<Bus> buses1, String tripID1, int mingap, int maxgap) {
		this.gaplength=buses1.size()-1;
		this.buses = buses1;
		this.tripID = tripID1;
		this.mingap = mingap;
		this.maxgap = maxgap;
	}
	
	@Override
	public void evaluate(org.moeaframework.core.Solution solution) {		
		int[] gaps = EncodingUtils.getInt(solution);	
		double[] f = new double[numOfObjectives]; // objectives
		double[] g = new double[numOfConstraints]; // constraints
		
		Network.gapsToBuses(gaps, buses);
		Network.update(buses, tripID);
		
		
		f[0] = Network.getObjectiveValue1(buses); 
		f[1] = Network.getObjectiveValue2(buses);
		f[2] = Network.getObjectiveValue3(buses); 
		
		
		if(Network.satisfyMinRestTime(buses)) {
			g[0] = 0;			
		}
		else {
			g[0] = 1;
		}

		solution.setObjectives(f);
		solution.setConstraints(g);
	}

	@Override
	public String getName() {
		return "BusProjectThreeO";
	}

	@Override
	public int getNumberOfConstraints() {
		return numOfConstraints;
	}

	@Override
	public int getNumberOfObjectives() {
		return numOfObjectives;
	}

	@Override
	public int getNumberOfVariables() {
		return 1;
	}

	@Override
	public org.moeaframework.core.Solution newSolution() {
		org.moeaframework.core.Solution solution = new org.moeaframework.core.Solution(gaplength, numOfObjectives, numOfConstraints);
		for (int i=0; i<gaplength; i++)
		{
			solution.setVariable(i, EncodingUtils.newInt(mingap, maxgap));
		}
		return solution;
	}

	@Override
	public void close() {
		//do nothing
	}
}

