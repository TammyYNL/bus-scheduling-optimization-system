package core;

import java.util.Calendar;
import java.util.Vector;

public class Bus implements Cloneable, Comparable<Bus>{

	String busId = null;
	int capacity;
	Vector<Stop> trip;
	int shift=1; // the number of runs of this bus between startTime and endTime
	double cost;
	boolean depotStatus;
	Calendar departureTime;
	
	public Bus(String busId, int capacity, double cost, boolean depotStatus,
			Calendar departureTime) {
		super();
		this.busId = busId;
		this.capacity = capacity;
		this.cost = cost;
		this.depotStatus = depotStatus;
		this.departureTime = departureTime;
	}
	
	public String getBusId() {
		return busId;
	}

	public void setBusId(String busId) {
		this.busId = busId;
	}

	public int getShift() {
		return shift;
	}

	public void setShift(int shift) {
		this.shift = shift;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public Vector<Stop> getTrip() {
		return trip;
	}

	public void setTrip(Vector<Stop> trip) {
		this.trip = trip;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public void setDepotStatus(boolean status){
		this.depotStatus = status;
	}
	
	public void setTripStartTime(Calendar c){
		this.departureTime = c;
		this.trip.get(0).setDepartureTime(c);
	}
	
	public Calendar getTripStartTime(){
		return this.trip.get(0).getDepartureTime();
	}
	
	public void setTripEndTime(Calendar c){
		this.trip.get(trip.size()-1).setDepartureTime(c);
	}
	
	public Calendar getTripEndTime(){
		return this.trip.get(trip.size()-1).getDepartureTime();
	}

	@Override
	public String toString() {
		return "Bus [busId=" + busId + ", shift=" + shift + ", capacity="
				+ capacity + ", cost=" + cost
				+ "]"+"\n" + printTrip(trip);
	}
	
	public String printTrip(Vector<Stop> trip){
		String s = "";
		for(int i=0; i<trip.size(); i++){
			s += trip.get(i).toString()+"\n";
		}
		return s;
	}

	public Calendar getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(Calendar departureTime) {
		this.departureTime = departureTime;
		//this.trip.get(0).setDepartureTime(departureTime);
	}

	public Bus clone(){
		try {
			return (Bus)super.clone();
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	/**
	 * @return -1 if this bus leaves before b (this < b), 1 if this bus leaves after b (this > b)
	 */
	@Override
	public int compareTo(Bus b) {
		if(this.departureTime.before(b.getDepartureTime())){
			return -1;
		}else if(this.departureTime.after(b.getDepartureTime())){
			return 1;
		}
		return 0;
	}

}

