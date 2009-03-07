/*
 * XYChart.java
 *
 */

package statistic.graph;

import java.awt.Color;
import java.util.HashMap;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author Martin Groß
 */
public class XYChart extends JFreeChart {

    private XYSeriesCollection collection;
    private XYPlot plot;
    
    public XYChart(String title, String xAxisLabel, String yAxis, XYSeriesCollection collection, boolean linear) {
        super(title, JFreeChart.DEFAULT_TITLE_FONT, new XYPlot(), true);
        this.collection = collection;
        setBackgroundPaint(Color.WHITE);
        setTitle(title);
        plot = (XYPlot) getPlot();
        plot.setDataset(collection);
        if (linear) {
            plot.setRenderer(new DefaultXYItemRenderer());
        } else {
            plot.setRenderer(new XYStepRenderer());
        }
        plot.setOrientation(PlotOrientation.VERTICAL);
        initXAxis(xAxisLabel);        
        plot.setRangeAxis(new NumberAxis(yAxis));
        plot.setBackgroundPaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.WHITE);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);    
    }
    
    private void initXAxis(String label) {
        plot.setDomainAxis(new NumberAxis(label));
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        for (int s = 0; s < collection.getSeriesCount(); s++) {
            for (int d = 0; d < collection.getItemCount(s); d++) {
                XYDataItem data = collection.getSeries(s).getDataItem(d);
                if (data.getX().longValue() == Integer.MAX_VALUE || data.getX().longValue() == Integer.MIN_VALUE) {
                    continue;
                }
                if (data.getX().doubleValue() > max) {
                    max = data.getX().doubleValue();
                }
                if (data.getX().doubleValue() < min) {
                    min = data.getX().doubleValue();
                }                
            }
        }
        plot.getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        plot.getDomainAxis().setRange(min - 0.5, max + 0.5);        
    }

}
