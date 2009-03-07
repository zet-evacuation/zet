package statistic.ca.gui;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleInsets;


public class Charts {
	
	private static JFreeChart chart;

	public static JFreeChart createBarChart(ChartData data) {
		if(data.getDiagramType().equals("bar")){
			chart = ChartFactory.createBarChart(data.getTitle(), // Title
					data.getYAxisLabel(), // X-Axis label
					"", // Y-Axis label
					data.getCDataSet(), // CategoryDataset
					PlotOrientation.VERTICAL, false, true, false // Show legend
			);
			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setBackgroundPaint(Color.LIGHT_GRAY);
			plot.setDomainGridlinePaint(Color.WHITE);
			plot.setRangeGridlinePaint(Color.WHITE);
			plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		}
		
		if(data.getDiagramType().equals("pie")){
			chart = ChartFactory.createPieChart(
					data.getTitle(), // Title
					data.getPieDataSet(),
					false, true, false
			);
		}

		chart.setBackgroundPaint(Color.WHITE);

		
		return chart;
	}
}