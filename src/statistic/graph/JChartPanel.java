/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * JChart.java
 *
 */
package statistic.graph;

import java.awt.Color;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import statistic.graph.gui.DisplayProfile;
import statistic.graph.gui.DisplayableStatistic;

/**
 *
 * @author Martin Groß
 */
public class JChartPanel extends ChartPanel {

    private static JFreeChart chart;
    private static XYSeriesCollection stepDataset;
    private static XYSeriesCollection linearDataset;
    private static XYPlot plot;

    public JChartPanel() {
        super(createChart());
    }

    public void add(String key, double value) {
        XYSeries series = new XYSeries(key);
        stepDataset.addSeries(series);
        series.add(Integer.MIN_VALUE, 0);
        series.add(0, value);
        series.add(Integer.MAX_VALUE, value);
    }

    public void add(String key, IntegerDoubleMapping data) {
        XYSeries series = new XYSeries(key);
        if (data.isPiecewiseLinear()) {
            linearDataset.addSeries(series);
        } else {
            stepDataset.addSeries(series);
        }
        for (IntegerDoubleMapping.TimeValuePair tvp : data) {
            series.add(tvp.time(), tvp.value());
        }
    }

    public void clear() {
        linearDataset.removeAllSeries();
        stepDataset.removeAllSeries();
    }

    protected static JFreeChart createChart() {
        stepDataset = new XYSeriesCollection();
        linearDataset = new XYSeriesCollection();

        chart = ChartFactory.createXYLineChart(
                "Fluss",
                "Zeiteinheiten",
                "Flusseinheiten",
                stepDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        chart.setBackgroundPaint(Color.WHITE);

        plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.WHITE);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        plot.setDataset(0, stepDataset);
        plot.setDataset(1, linearDataset);

        XYItemRenderer r = plot.getRenderer();
        //r.setBaseOutlinePaint(Color.BLACK);
        //r.setBasePaint();
        //r.setItemLabelPaint();
        //r.setOutlinePaint(Color.BLACK);
        //r.setSeriesItemLabelPaint();
        //r.setSeriesOutlinePaint();
        r.setSeriesPaint(0, Color.GREEN.darker());
        r.setSeriesPaint(1, Color.MAGENTA.darker());
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
        }
        //XYStepRenderer r3 = new XYStepRenderer();
        //r.setBaseOutlinePaint(Color.BLACK);
        //r.setBasePaint();
        //r.setItemLabelPaint();
        //r.setOutlinePaint(Color.BLACK);
        //r.setSeriesItemLabelPaint();
        //r.setSeriesOutlinePaint();
        //r3.setSeriesPaint(0, Color.BLACK);
        //r3.setShapesVisible(true);
        //r3.setShapesFilled(true);
        //r3.setSeriesShapesFilled(0,true);
        //r3.set
        //r3.setSeriesStroke(0, new BasicStroke(2.0f));

        XYItemRenderer r2 = new XYStepRenderer();
        r2.setSeriesPaint(2, Color.BLACK);
        plot.setRenderer(r2);
        plot.setRenderer(1, r);
        //plot.setRenderer(2, r3);

        plot.getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        plot.getDomainAxis().setRange(-0.5, 10);

        plot.getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        plot.getRangeAxis().setRange(-0.5, 8.5);

        return chart;
    }
}
