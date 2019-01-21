package core;

import java.util.Calendar;

public class Stop {
	int stopId; 
	double arrivalRate;
	double alightingRate;
	int headWay = 0;
	int load = 10; 
	Calendar departureTime;
	
	public Stop(int stopId) {
		super();
		this.stopId = stopId;
	}

	public int getStopId() {
		return stopId;
	}

	public void setStopId(int stopId) {
		this.stopId = stopId;
	}

	public void setArrivalRate(double arrivalRate) {
		this.arrivalRate = arrivalRate;
	}

	public double getArrivalRate() {
		return arrivalRate;
	}
	
	public double getAlightingRate() {
		return alightingRate;
	}
	
	public void setAlightingRate(double alightingRate) {
		this.alightingRate = alightingRate;
	}
	
	public int getHeadWay() {
		return headWay;
	}
	
	public void setHeadWay(int headWay) {
		this.headWay = headWay;
	}
	
	public int getLoad() {
		return load;
	}
	
	public void setLoad(int load) {
		this.load = load;
	}
	
	public Calendar getDepartureTime() {
		return departureTime;
	}
	
	public void setDepartureTime(Calendar departureTime) {
		this.departureTime = departureTime;
	}
	
	@Override
	public String toString() {
		return "Stop [stopId=" + stopId + ", arrivalRate=" + arrivalRate +", alightingRate=" + alightingRate
				+ ", headWay=" + headWay
				+ ", load=" + load + ", departureTime=" + calendarToString(departureTime) + "]\n";
	}
	
	public Stop clone() {
		Stop s = new Stop(stopId);
		s.stopId = this.stopId;
		s.alightingRate = this.alightingRate;
		s.arrivalRate = this.arrivalRate;
		s.headWay = this.headWay;
		s.load = this.load;
		s.departureTime = this.departureTime;
		return s;
	}
	
	public  String calendarToString(Calendar c) {
		return "(" + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1)
				+ "-" + c.get(Calendar.DATE) + "|"
				+ c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE)
				+ ")";
	}
	
}

