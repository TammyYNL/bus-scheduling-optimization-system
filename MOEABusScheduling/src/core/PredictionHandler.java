package core;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Vector;
import helper.*;

public class PredictionHandler {
    private static int intervalDur = Config.intervalDur;
    public static Session createSession(){
        return new Session();
    }

    public static void loadModels(String routeID, Session ses){
        int intervalN = 24 * (60 / intervalDur);
        int skipCol = 1;

        int dayN = 7;
        StatsDataParser[] parsers_TTS_k2K = new StatsDataParser[dayN];
        StatsDataParser[] parsers_AR = new StatsDataParser[dayN];
        StatsDataParser[] parsers_AL = new StatsDataParser[dayN];
        String statsFileName_TTS_k2K = null;
        String statsFileName_AR = null;
        String statsFileName_AL = null;
        for(int i = 0; i < dayN; i ++){
            // k->k+1 travel time
        	// dataX/stats/tts/tts_k2K_day.csv
            statsFileName_TTS_k2K = Config.x_tts_statsDir + "/" + Integer.toString(i) + ".csv";
            parsers_TTS_k2K[i] = new StatsDataParser(statsFileName_TTS_k2K, skipCol, intervalN);

            // AR
            // dataX/stats/arrival/day.csv
            statsFileName_AR = Config.x_arrival_statsDir + "/" + Integer.toString(i) + ".csv";
            parsers_AR[i] = new StatsDataParser(statsFileName_AR, skipCol, intervalN);

            // AL
            // dataX/stats/alighting/day.csv
            statsFileName_AL = Config.x_alighting_statsDir + "/" + Integer.toString(i) + ".csv";
            parsers_AL[i] = new StatsDataParser(statsFileName_AL, skipCol, intervalN);
        }
        // full travel time
        // dataX/stats/tts/full.csv
        String statsFileName_TTS_full = Config.x_tts_statsDir + "/" + "full.csv";
        StatsDataParser parser_TTS_full = new StatsDataParser(statsFileName_TTS_full, skipCol, intervalN);

        ses.add("stats_TTS_k2K." + routeID, parsers_TTS_k2K);
        ses.add("stats_TTS_full." + routeID, parser_TTS_full);
        ses.add("stats_AR." + routeID, parsers_AR);
        ses.add("stats_AL." + routeID, parsers_AL);
    }
    
    public static double getStats_TTS_k2K_(String routeID, int stopFrom, int stopTo, Calendar depTime, Session ses) {
        double ttsStats = Double.NaN;
        try {
            int dow = Utils.getDayOfWeek(depTime);
            int interval = Utils.getIntervalIndex(depTime, intervalDur);
            StatsDataParser parser_TTS_k2K_thisDay = ((StatsDataParser[]) ses.get("stats_TTS_k2K." + routeID))[dow];
            ttsStats = parser_TTS_k2K_thisDay.getStatsForSegmentAtInterval(stopFrom, interval);
        }
        catch(ParseException pe) {
            System.out.println(pe);
            System.exit(1);
        }
        return ttsStats;
    }
    
    public static double getTravelTime(String routeID, Calendar depTime, Session ses) {
        double tts = Double.NaN;
        try {
            StatsDataParser parsers_TTS_full = (StatsDataParser) ses.get("stats_TTS_full." + routeID);
            tts = parsers_TTS_full.getStatsForSegmentAtInterval(Utils.getDayOfWeek(depTime), Utils.getIntervalIndex(depTime, intervalDur));
        }
        catch(Exception e){
            System.out.println(e);
            System.exit(1);
        }
        return tts;
    }
    
    public static Vector<Stop> updateTrip(String routeID, Calendar nouse, Vector<Stop> trip, Session ses) {
        int N = trip.size();
        Vector<Stop> newTrip = new Vector<>(N);
        try {
            StatsDataParser[] parsers_AR = null;
            StatsDataParser[] parsers_AL = null;
            parsers_AR = (StatsDataParser[]) ses.get("stats_AR." + routeID);
            parsers_AL = (StatsDataParser[]) ses.get("stats_AL." + routeID);
            
            for (int i = 0; i < N; i++) {
                Stop s = trip.get(i);
                int id = i;
                Calendar depTime = s.getDepartureTime();
                int dow = Utils.getDayOfWeek(depTime);
                int interval = Utils.getIntervalIndex(depTime, intervalDur);
                
                double arrivalRate;
                arrivalRate = parsers_AR[dow].getStatsForSegmentAtInterval(id, interval);  
                s.setArrivalRate(arrivalRate);
                
                double alightingRatio;
                alightingRatio = parsers_AL[dow].getStatsForSegmentAtInterval(id, interval);
                s.setAlightingRate(alightingRatio);

                newTrip.add(i, s);
            }
        }
        catch(Exception e){
            System.exit(1);
        }
        return newTrip;
    }
    
}
