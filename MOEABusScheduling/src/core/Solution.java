package core;

import java.util.Vector;

public class Solution {
	double o1; // waiting time
	double o2; // overloads
	double o3; // cost
	double averageWaitingTime;
	int tripNum;
	double totalObj;
	Vector<Bus> updatedbuses;
	int[] bestGaps;
	
	public Solution(double o1, double o2, double o3, double totalObj, double averageWaitingTime, int tripNum,
			Vector<Bus> updatedbuses) {
		super();
		this.o1 = o1;
		this.o2 = o2;
		this.o3 = o3;
		this.totalObj = totalObj;
		this.updatedbuses = updatedbuses;
		this.averageWaitingTime = averageWaitingTime;
		this.tripNum = tripNum;
	}
	
	public double getO1() {
		return o1;
	}
	
	public void setO1(double o1) {
		this.o1 = o1;
	}
	
	public double getO2() {
		return o2;
	}
	
	public void setO2(double o2) {
		this.o2 = o2;
	}
	
	public double getO3() {
		return o3;
	}
	
	public void setO3(double o3) {
		this.o3 = o3;
	}
	
	public double getTotalObj() {
		return totalObj;
	}
	
	public void setTotalObj(double totalObj) {
		this.totalObj = totalObj;
	}
	
	public Vector<Bus> getUpdatedbuses() {
		return updatedbuses;
	}
	
	public void setUpdatedbuses(Vector<Bus> updatedbuses) {
		this.updatedbuses = updatedbuses;
	}
	
	public int[] getBestGaps() {
		return bestGaps;
	}
	
	public void setBestGaps(int[] bestGaps) {
		this.bestGaps = bestGaps;
	}
	
	public double getAverageWaitingTime() {
		return averageWaitingTime;
	}
	
	public int getTripNum() {
		return tripNum;
	}
}

