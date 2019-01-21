package helper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

public class StatsDataParser {
	// load data into df, each element(double[]) is one line
    private Vector<double[]> df;
    private String statsFileName;
    private int skipColumn;
    private int intervalN;

    public StatsDataParser(String statsFileName, int skipColumn, int intervalN){
        df = new Vector<>();
        this.statsFileName = statsFileName;
        this.skipColumn = skipColumn;
        this.intervalN = intervalN;
        loadDataFromFile();
    }

    private void loadDataFromFile() {
        try {
            BufferedReader xr = new BufferedReader(new FileReader(statsFileName));
            xr.readLine(); // skip the heading
            while (true) {
                String line = xr.readLine();
                if (line == null) {
                    break;
                }
                StringTokenizer st = new StringTokenizer(line, ",");
                for (int k = 0; k < skipColumn; k++) {
                    st.nextToken();
                }
                
                int i = 0;
                double[] perSegData = new double[intervalN];
                while (st.hasMoreTokens()) {
                    perSegData[i] = Double.parseDouble(st.nextToken());
                    i++;
                }
                df.add(perSegData);
            }
            xr.close();
        } catch (FileNotFoundException fnfe) {
            System.err.println("Stats file not existed!");
            System.exit(1);
        } catch (IOException ioe) {
            System.err.println("File reading error");
            System.exit(1);
        }
    }

    public double getStatsForSegmentAtInterval(int segIndex, int intervalIndex){
        return df.get(segIndex)[intervalIndex];
    }

    public int size(){
        return df.size();
    }

    public void printAll(){
        int intN;
        int N = size();
        for(int i = 0; i < N; i ++){
            double[] perSegData = df.get(i);
            intN = perSegData.length;
            for(int j = 0; j < intN; j ++){
                System.out.print(perSegData[j] + ",");
            }
            System.out.println();
        }
    }
}
