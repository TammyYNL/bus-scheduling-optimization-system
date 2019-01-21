package core;

import java.util.Calendar;
import java.util.Vector;
import helper.*;

public class Network {
	static Vector<Bus> buses1 = new Vector<Bus>();
	static String tripID1;
	static int miniRestTime;
	static Calendar startTime;
	static Calendar endTime;
	static double weight1;
	static double weight2;
	static double weight3;
	static Session ses;
	
	public static boolean update(Vector<Bus> buses, String tripId) {

		// update each stop's departure time, by travel time from stop k to stop k+1
		for (int i = 0; i + 1 < buses.size(); i++) {
			Bus b1 = buses.get(i);
			Bus b2 = buses.get(i + 1);
			Vector<Stop> trip1 = b1.getTrip();
			//System.out.println(Integer.toString(b1.getTripStartTime().get(Calendar.MINUTE)));
			Vector<Stop> trip2 = b2.getTrip();
			for (int j = 0; j + 1 < trip1.size(); j++) {
				Stop s_1_1 = trip1.get(j);
				Stop s_2_1 = trip2.get(j);
				Calendar pTime = getPtime(s_1_1.departureTime, s_2_1.departureTime);
				if (i == 0) {
					Stop s_1_2 = trip1.get(j + 1);
					Calendar departureTime_1_1 = (Calendar) s_1_1.getDepartureTime().clone();
					int travelTime = (int) PredictionHandler.getStats_TTS_k2K_(tripId, s_1_1.getStopId(),
							s_1_2.getStopId(), pTime, ses);
					departureTime_1_1.add(Calendar.MINUTE, travelTime);
					s_1_2.setDepartureTime(departureTime_1_1);
					
					Stop s_2_2 = trip2.get(j + 1);
					Calendar departureTime_2_1 = (Calendar) s_2_1.getDepartureTime().clone();
					int travelTime2 = (int) PredictionHandler.getStats_TTS_k2K_(tripId, s_2_1.getStopId(),
							s_2_2.getStopId(), pTime, ses);
					departureTime_2_1.add(Calendar.MINUTE, travelTime2);
					s_2_2.setDepartureTime(departureTime_2_1);
				} else {
					Stop s_2_2 = trip2.get(j + 1);
					Calendar departureTime_2_1 = (Calendar) s_2_1.getDepartureTime().clone();
					int travelTime3 = (int) PredictionHandler.getStats_TTS_k2K_(tripId, s_2_1.getStopId(),
							s_2_2.getStopId(), pTime, ses);
					departureTime_2_1.add(Calendar.MINUTE, travelTime3);
					s_2_2.setDepartureTime(departureTime_2_1);
				}
			}
		}
		
		// update headway
		for (int i = 0; i + 1 < buses.size(); i++) {
			Bus b = buses.get(i);
			Vector<Stop> trip = b.getTrip();
			Bus b1 = buses.get(i + 1);
			Vector<Stop> trip1 = b1.getTrip();
			if (i == 0) {
				for (int j = 0; j < trip1.size(); j++) { 
					int headway = (int) ((trip1.get(j).getDepartureTime().getTimeInMillis()
							- trip.get(j).getDepartureTime().getTimeInMillis()) /1000/60);
					trip1.get(j).setHeadWay(headway);
					trip.get(j).setHeadWay(headway); // assume the first bus's headway is the same as the second
				}
			} else {
				for (int j = 0; j < trip1.size(); j++) {
					//System.out.println(Integer.toString(trip.get(j).getDepartureTime().get(Calendar.MINUTE)));
					//System.out.println(Integer.toString(trip1.get(j).getDepartureTime().get(Calendar.MINUTE)));
					
					int headway = (int) ((trip1.get(j).getDepartureTime().getTimeInMillis()
							- trip.get(j).getDepartureTime().getTimeInMillis()) /1000/60);
					trip1.get(j).setHeadWay(headway);
					// System.out.println(headway);
				}
			}
		}
		
		// update trip
		for (int i = 0; i < buses.size(); i++) {
			Bus b = buses.get(i);
			Calendar start = b.getTripStartTime();
			Vector<Stop> trip = b.getTrip();
			Vector<Stop> utrip = PredictionHandler.updateTrip(tripId, start, trip, ses);
			Vector<Stop> ntrip = new Vector<Stop>();
			for (int j = 0; j < utrip.size(); j++) {
				ntrip.add(utrip.get(j).clone());
			}
			b.setTrip(ntrip);
		}

		// update loads
		for (int i = 0; i < buses.size(); i++) {
			Bus b = buses.get(i);
			Vector<Stop> trip = b.getTrip();
			for (int j = 0; j + 1 < trip.size(); j++) {
				Stop s1 = trip.get(j);
				Stop s2 = trip.get(j + 1);
				int load = (int) (s2.getHeadWay() * s2.getArrivalRate() + s1.getLoad() * 
					(1 - s2.getAlightingRate()));
				s2.setLoad(load);
			}
		}
		return true;
	}
	
	// waiting time
	public static double getObjectiveValue1(Vector<Bus> buses) {
		double O1 = 0.0;
		for (int i = 0; i < buses.size(); i++) {
			Bus b = buses.get(i);
			Vector<Stop> stop = b.getTrip();
			for (int k = 0; k < stop.size()-1; k++) {
				Stop st = stop.get(k);
				double ar = st.getArrivalRate();
				int g = st.getHeadWay();
				if (b.getTripStartTime().before(endTime)) {
					O1 = O1 + ar * g * g / 2;
				} else {
					;
				}
			}
		}
		return O1;
	}
	
	// overload
	public static double getObjectiveValue2(Vector<Bus> buses) {
		double O2 = 0.0;
		for (int i = 0; i < buses.size(); i++) {
			Bus b = buses.get(i);
			Vector<Stop> stop = b.getTrip();
			for (int k = 0; k + 1 < stop.size(); k++) {
				Stop st = stop.get(k);
				Stop stAfter = stop.get(k + 1);
				Calendar t = st.getDepartureTime();
				Calendar tAfter = stAfter.getDepartureTime();
				double TripTime = (tAfter.getTimeInMillis() - t.getTimeInMillis())/1000/60;
				double ar = st.getArrivalRate();
				double al = st.getAlightingRate();
				int c = b.getCapacity();
				int l = st.getLoad();
				int g = st.getHeadWay();
				double overload = ar * g + (1 - al) * l - c;
				if (overload >= 0 && b.getTripStartTime().before(endTime)) {
					O2 = O2 + TripTime * overload;
				} else {
					;
				}
			}
		}
		return O2;
	}
	
	// cost
	public static double getObjectiveValue3(Vector<Bus> buses) {
		double O3 = 0.0;
		for (int i = 0; i < buses.size(); i++) {
			Bus b = buses.get(i);
			double fee = b.getCost(); 
			if (b.getTripStartTime().before(endTime)) {
				O3 = O3 + fee;
			}
		}
		return O3;
	}
	
	// average passenger waiting time
	public static double getAverageWaitingTime(Vector<Bus> buses) {
		double totalWaitingTime = 0;
		int waitingPassengerNum = 0;
		for (int i = 0; i < buses.size(); i++) {
			Bus b = buses.get(i);
			Vector<Stop> stop = b.getTrip();
			for (int k = 0; k < stop.size()-1; k++) {
				Stop st = stop.get(k);
				double ar = st.getArrivalRate();
				int g = st.getHeadWay();
				if (b.getTripStartTime().before(endTime)) {
					totalWaitingTime += ar * g * g / 2;
					waitingPassengerNum += ar * g;
				}
			}
		}
		return totalWaitingTime/waitingPassengerNum;
	}
	
	// the number of trips in scheduled time period
	public static int getTripNum(Vector<Bus> buses) {
		int num = 0;
		for(int i = 0; i < buses.size(); i++) {
			Bus b = buses.get(i);
			if (b.getTripStartTime().before(endTime)) {
				num += 1;
			}
		}
		return num;
	}
	
	
	public static Solution getObjectiveValue(int[] gaps, Vector<Bus> buses, String tripId) {
		Vector<Bus> updatedbuses = gapsToBuses(gaps, buses);
		boolean feasible = update(updatedbuses, tripId);
		double TotalObj = 0.0;
		double w1 = weight1;
		double w2 = weight2;
		double w3 = weight3;
		double o1 = getObjectiveValue1(updatedbuses);
		double o2 = getObjectiveValue2(updatedbuses);
		double o3 = getObjectiveValue3(updatedbuses);
		double averageWaitingTime = getAverageWaitingTime(updatedbuses);
		int tripNum = getTripNum(updatedbuses);
		TotalObj = w1 * o1 + w2 * o2 + w3 * o3;
		if (!feasible) {
			TotalObj += 1000000;
		}
		Solution s = new Solution(o1, o2, o3, TotalObj, averageWaitingTime, tripNum, updatedbuses);
		return s;
	}
		
	public static Calendar getPtime(Calendar c1, Calendar c2) {
		Calendar c = (Calendar) c1.clone();
		int gap = (int) ((c2.getTimeInMillis() - c1.getTimeInMillis()) / 1000);
		c.add(Calendar.SECOND, gap / 2);
		return c;
	}
	
	/**
	 * use gaps to reset buses' start time
	 * @param gaps
	 * @param buses
	 * @return set buses
	 */
	public static Vector<Bus> gapsToBuses(int[] gaps, Vector<Bus> buses) {
		// create a new gaps2, first element is set to 0
		int[] gaps2 = new int[gaps.length + 1];
		gaps2[0] = 0;
		for (int i = 0; i < gaps.length; i++) {
			gaps2[i + 1] = gaps[i];
		}
		// update buses' start time according to gaps
		int gapSum = 0;
		for (int i = 0; i < gaps2.length; i++) {
			gapSum += gaps2[i];
			Bus b = buses.get(i);
			Calendar c = (Calendar) startTime.clone();
			c.add(Calendar.MINUTE, gapSum);
			b.setTripStartTime(c);
		}
		
		return buses;
	}
	
	// constraint 2
	public static boolean IsOverload(Vector<Bus> buses) {
		int tripNum = getTripNum(buses);
		for (int i = 0; i < tripNum; i++) {
			Bus b = buses.get(i);
			Vector<Stop> stops = b.getTrip();
			for (int k = 0; k < stops.size(); k++) {
				Stop st = stops.get(k);
				double ar = st.getArrivalRate();
				double al = st.getAlightingRate();
				int c = b.getCapacity();
				int l = st.getLoad();
				int g = st.getHeadWay();
				double numOfPassengers = ar * g + (1 - al) * l;
				if(numOfPassengers > Config.overloadRate*c) {
					return true;
				}
			}
		}
		return false;
	}
	
	// constraint 1
	public static boolean satisfyMinRestTime(Vector<Bus> buses) {
		int tripNum = getTripNum(buses);
		int firstShiftIndex = 1;
		boolean IsSatisfy = true;
		// abuses is buses which run for the first time
		Vector<Bus> abuses = new Vector<Bus>();
		for (int i = 0; i < tripNum; i++) {
			Bus b = buses.get(i);
			if (b.getShift() == firstShiftIndex) {
				abuses.add(b);
			}
		}
		
		// sbuses has several tbuses
		Vector<Vector<Bus>> sbuses = new Vector<Vector<Bus>>();
		for (int i = 0; i < abuses.size(); i++) {
			Bus b = abuses.get(i);
			String busId = b.getBusId();
			Vector<Bus> tbuses = new Vector<Bus>(); // each bus (unique busId) has one tbuses 
			tbuses.add(b);
			for (int j = 0; j < tripNum; j++) {
				Bus b1 = buses.get(j);
				String busId1 = b1.getBusId();
				int shift = b1.getShift();
				if (busId.equals(busId1) && shift > firstShiftIndex) {
					tbuses.add(b1);
				}
			}
			if (tbuses.size() > 1) {
				sbuses.add(tbuses);
			}
		}

		// check minRestTime constraint
		for (int i = 0; i < sbuses.size(); i++) {
			Vector<Bus> asbuses = sbuses.get(i);
			for (int j = 0; j + 1 < asbuses.size(); j++) {
				Bus b1 = asbuses.get(j);
				Bus b2 = asbuses.get(j + 1);
				Calendar formerEnd = b1.getTripEndTime();
				/*
				int hour = formerEnd.get(Calendar.HOUR_OF_DAY);
    		    		int min = formerEnd.get(Calendar.MINUTE);
    		    		System.out.print(Integer.toString(hour) +":" + Integer.toString(min));
    		    		*/
    		    		
				Calendar latterStart = b2.getTripStartTime();
	    			
				int plannedRestTime = (int) ((latterStart.getTimeInMillis() - formerEnd.getTimeInMillis()) / 1000/60);
				if (plannedRestTime < miniRestTime) {
					IsSatisfy = false;
					break;
				}
			}
		}
		return IsSatisfy;
	}
	
}
