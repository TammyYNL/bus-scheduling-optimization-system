package core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import helper.*;

public class Planner {

	public static void main(String[] args) {
		// construct trip
		// 22 is number of stops
		Vector<Stop> trip = new Vector<Stop>();
		for (int i=0; i<22; i++) {
			trip.add(new Stop(i));
		}

		/*
		// Experiment 2
		// set startTime
		Calendar startTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        try {
        	startTime.setTime(sdf.parse("2016-03-25 17:00:00"));
        }
        catch (ParseException pe) {
            System.err.println(pe);
        }

        // construct buses
        Vector<Bus> buses = new Vector<Bus>();		
		// 14 is number of buses
		for(int i=0;i<14;i++) {
			Calendar c = (Calendar)startTime.clone();
			c.add(Calendar.MINUTE, -i * 10);
			// busIDs are from 1761 to 1774, capacity is 40, cost is 250
			Bus b = new Bus(Integer.toString(i+1761), 40, 250, false, c);
			buses.add(b);
		}
		
		// set end time
		Calendar endTime = Calendar.getInstance();
        try {
        	endTime.setTime(sdf.parse("2016-03-25 19:00:00"));
        }
        catch (ParseException pe) {
            System.err.println(pe);
        }
        */
        
		// set startTime
		Calendar startTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        try {
        	startTime.setTime(sdf.parse("2016-03-14 04:30:00"));
        }
        catch (ParseException pe) {
            System.err.println(pe);
        }

        // construct buses
        Vector<Bus> buses = new Vector<Bus>();		
		// 14 is number of buses
		for(int i=0;i<14;i++) {
			// busIDs are from 1761 to 1774, capacity is 40, cost is 250
			Bus b = new Bus(Integer.toString(i+1761), 40, 250, true, null);
			buses.add(b);
		}
		
		// set end time
		Calendar endTime = Calendar.getInstance();
        try {
        	endTime.setTime(sdf.parse("2016-03-14 23:00:00"));
        }
        catch (ParseException pe) {
            System.err.println(pe);
        }
		
        // load statistical data into session
		Session ses = PredictionHandler.createSession();
		PredictionHandler.loadModels("36", ses);
		
		Optimizer opt = new Optimizer(ses, buses, trip, "36", startTime, endTime,
						Config.minGap, Config.maxGap, Config.minRestTime, Config.weight1, Config.weight2, Config.weight3);
		opt.run();
	}
}

