package helper;

public class Config {
    public static final String x_statsDir = "data/stats/";
    public static final String x_tts_statsDir = x_statsDir + "tts/";
    public static final String x_arrival_statsDir = x_statsDir + "arrival/";
    public static final String x_alighting_statsDir = x_statsDir + "alighting/";

    // --- parameter settings ---
    public static final int intervalDur = 30; // 5min, 10min, 15min, 20min, 30min
    public static final int minGap = 1;
    public static final int maxGap = 10;
    public static final int minRestTime = 30;
    // the number of passenger can not excess overloadRate*capacity
    public static final double overloadRate = 1.5;
    public static final int MOEAMaxEvaluation = 500;
    public static final String algorithmName = "NSGAII"; // can change algorithm, NSGAII, SPEA2, PAES, etc.
    // weights of three objectives (used in original algorithm)
    public static final double weight1 = 0.6;
    public static final double weight2 = 0.1;
    public static final double weight3 = 0.3;
        
}
