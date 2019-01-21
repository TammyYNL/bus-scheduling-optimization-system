package helper;

import java.awt.Color;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

@SuppressWarnings("serial")
public class ScatterPainter extends JFrame {

	public ScatterPainter(
			double[] three_objective1, double[] three_objective3, double[] two_objective1, double[] two_objective2) {

		// Create dataset
		XYDataset dataset = createDataset(three_objective1, three_objective3, two_objective1, two_objective2);

		JFreeChart chart = ChartFactory.createScatterPlot("Three-objective Model VS Two-objective Model", 
						"Total Waiting Time", "Operation Cost", dataset,
						PlotOrientation.VERTICAL,true,true,false);

		// Changes background color
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(new Color(255, 228, 196));

		// Create Panel
		ChartPanel panel = new ChartPanel(chart);
		setContentPane(panel);
	}

	private XYDataset createDataset(
			double[] three_objective1, double[] three_objective3, double[] two_objective1, double[] two_objective2) {
		XYSeriesCollection dataset = new XYSeriesCollection();

		// Three-objective Model
		XYSeries series1 = new XYSeries("Three-objective Model");
		for(int i=0; i<three_objective1.length; i++) {
			series1.add(three_objective1[i], three_objective3[i]);
		}

		dataset.addSeries(series1);

		// Two-objective Model
		XYSeries series2 = new XYSeries("Two-objective Model");
		for(int i=0; i<two_objective1.length; i++) {
			series2.add(two_objective1[i], two_objective2[i]);
		}

		dataset.addSeries(series2);

		return dataset;
	}
}
