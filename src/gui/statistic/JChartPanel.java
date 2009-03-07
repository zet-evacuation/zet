package gui.statistic;

import java.awt.Color;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author Martin Groß
 */
public class JChartPanel extends ChartPanel {

	private  JFreeChart chart;
	private  DefaultCategoryDataset stepDataset;
	private  XYSeriesCollection linearDataset;
	private  CategoryPlot plot;
	private Integer comp;
	private  static DefaultCategoryDataset categoryDataset;
	private static DefaultPieDataset pieDataset;

	public JChartPanel() {
		super(createChart());
	}

	public void addValues(ArrayList<Double> value, ArrayList<String> assignment){
		categoryDataset.clear();
		for(int i = 0; i < value.size(); i++){
			categoryDataset.addValue(value.get(i).doubleValue(),"",assignment.get(i));
		}
		//categoryDataset.validateObject();		
	}
	
	public void addPieValues(ArrayList<Double> value, ArrayList<String> assignment){
		pieDataset.clear();
//        dataset.setValue("One", new Double(43.2));
//        dataset.setValue("Two", new Double(10.0));
//        dataset.setValue("Three", new Double(27.5));
//        dataset.setValue("Four", new Double(17.5));
//        dataset.setValue("Five", new Double(11.0));
//        dataset.setValue("Six", new Double(19.4));
		for(int i = 0; i < value.size(); i++){
			pieDataset.setValue(assignment.get(i),value.get(i).doubleValue());
		}
	}
	
	protected static JFreeChart createChart() {
		categoryDataset = new DefaultCategoryDataset();
		//categoryDataset.addValue(20, "", "A");
		//categoryDataset.addValue(35, "", "A");
		//categoryDataset.addValue(40, "", "B");
		//categoryDataset.addValue(60, "", "B");
		
		JFreeChart chart = ChartFactory.createBarChart
		                     ("", // Title
		                      "Belegungen",              // X-Axis label
		                      "Werte",                 // Y-Axis label
		                      categoryDataset,         // Dataset
		                      PlotOrientation.VERTICAL, 
		                      false,true,false                     // Show legend
		                     );

		chart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.WHITE);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        //plot.setDomainCrosshairVisible(true);
        //plot.setRangeCrosshairVisible(true);

		
		return chart;

	}
	
	protected static JFreeChart createPieChart() {
		pieDataset = new DefaultPieDataset();
		
		JFreeChart chart = ChartFactory.createPieChart
		                     ("", // Title
		                      pieDataset,
		                      false, true, false
		                     );

		chart.setBackgroundPaint(Color.WHITE);

//        CategoryPlot plot = (CategoryPlot) chart.getPlot();
//        plot.setBackgroundPaint(Color.LIGHT_GRAY);
//        plot.setDomainGridlinePaint(Color.WHITE);
//        plot.setRangeGridlinePaint(Color.WHITE);
//        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        //plot.setDomainCrosshairVisible(true);
        //plot.setRangeCrosshairVisible(true);

		
		return chart;

	}
}
