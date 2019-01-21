package helper;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
//import org.jfree.chart.renderer.category.CategoryItemRenderer;
//import org.jfree.chart.renderer.category.LineAndShapeRenderer;
//import org.jfree.chart.plot.CategoryPlot;
//import org.jfree.chart.axis.NumberAxis;

@SuppressWarnings("serial")
public class ChartPainter extends ApplicationFrame {

   public ChartPainter( String applicationTitle , String chartTitle, String[] times, double[] rates,int[] headways) {
      super(applicationTitle);
      JFreeChart lineChart = ChartFactory.createLineChart(
         chartTitle,
         "time","headway",
         createDataset2(times, headways),
         PlotOrientation.VERTICAL,
         true,true,false);
      /*
      CategoryPlot plot=lineChart.getCategoryPlot();
      NumberAxis numberaxis2 = new NumberAxis("headway");
      plot.setRangeAxis(1, numberaxis2);
      plot.setDataset(1, createDataset2(times, headways));
      plot.mapDatasetToRangeAxis(1, 1);
      
      CategoryItemRenderer renderer2 = new LineAndShapeRenderer();
      plot.setRenderer(1, renderer2);
      */
      
      ChartPanel chartPanel = new ChartPanel( lineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 1560 , 167 ) );
      setContentPane( chartPanel );
      
   }

   /*
   private DefaultCategoryDataset createDataset1(String[] times, double[] rates) {
      DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
      for(int i=0; i<times.length; i++) {
          dataset.addValue( rates[i] , "arrival rate" , times[i] );
      }
      return dataset;
   }
   */
   
   private DefaultCategoryDataset createDataset2(String[] times, int[] headways) {
	      DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
	      for(int i=0; i<times.length; i++) {
	          dataset.addValue( headways[i] , "headway" , times[i] );
	      }
	      return dataset;
   }
}
