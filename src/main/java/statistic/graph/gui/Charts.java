/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
/*
 * Charts.java
 *
 */
package statistic.graph.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Martin Groß
 */
public class Charts {

    public static JFreeChart createXYAreaChart(DiagramData data, XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYAreaChart(data.getTitle(), data.getXAxisLabel(), data.getYAxisLabel(), dataset, PlotOrientation.VERTICAL, true, true, false);
        initXAxis(chart.getXYPlot(), dataset);
        return chart;
    }

    public static JFreeChart createXYBarChart(DiagramData data, IntervalXYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYBarChart(data.getTitle(), data.getXAxisLabel(), false, data.getYAxisLabel(), dataset, PlotOrientation.VERTICAL, true, true, false);
        initXAxis(chart.getXYPlot(), dataset);
        return chart;
    }

    public static JFreeChart createXYLineChart(DiagramData data, XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(data.getTitle(), data.getXAxisLabel(), data.getYAxisLabel(), dataset, PlotOrientation.VERTICAL, true, true, false);
        initXAxis(chart.getXYPlot(), dataset);
        return chart;
    }

    public static JFreeChart createPieChart(DiagramData data, PieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(data.getTitle(), dataset, true, true, false);
        return chart;
    }

    public static JFreeChart createPieChart3D(DiagramData data, PieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart3D(data.getTitle(), dataset, true, true, false);
        return chart;
    }

    public static JFreeChart createRingChart(DiagramData data, PieDataset dataset) {
        JFreeChart chart = ChartFactory.createRingChart(data.getTitle(), dataset, true, true, false);
        return chart;
    }

    public static JFreeChart createXYStepChart(DiagramData data, XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYStepChart(data.getTitle(), data.getXAxisLabel(), data.getYAxisLabel(), dataset, PlotOrientation.VERTICAL, true, true, false);
        initXAxis(chart.getXYPlot(), dataset);
        return chart;
    }

    public static JFreeChart createXYStepAreaChart(DiagramData data, XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYStepAreaChart(data.getTitle(), data.getXAxisLabel(), data.getYAxisLabel(), dataset, PlotOrientation.VERTICAL, true, true, false);
        initXAxis(chart.getXYPlot(), dataset);
        return chart;
    }

    private static void initXAxis(XYPlot plot, XYDataset dataset) {
        plot.setDomainAxis(new NumberAxis(plot.getDomainAxis().getLabel()));
        XYSeriesCollection collection = (XYSeriesCollection) dataset;
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        if (collection != null) {
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
            if (min < max) {
                plot.getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
                plot.getDomainAxis().setRange(min - 0.5, max + 0.5);
                for (int s = 0; s < collection.getSeriesCount(); s++) {
                    XYSeries series = collection.getSeries(s);
                    if (series.indexOf(Integer.MIN_VALUE) >= 0) {
                        XYDataItem item = series.remove((Number) Integer.MIN_VALUE);
                        if (series.indexOf(min) < 0) {
                            series.add(min, item.getY());
                        }
                    }
                    if (series.indexOf(Integer.MAX_VALUE) >= 0) {
                        XYDataItem item = series.remove((Number) Integer.MAX_VALUE);
                        if (series.indexOf(max) < 0) {
                            series.add(max, item.getY());
                        }                        
                    }                
                }
            } else {
                plot.getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
                plot.getDomainAxis().setRange(0 - 0.5, 1 + 0.5);                
            }
        } else {
            plot.getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            plot.getDomainAxis().setRange(0 - 0.5, 1 + 0.5);
        }
    }
}
