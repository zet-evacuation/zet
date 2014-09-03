/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
 * XYChartCreator.java
 *
 */

package statistic.graph.dataset;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import statistic.graph.IntegerDoubleMapping;
import statistic.graph.Operation;
import statistic.graph.gui.Charts;
import statistic.graph.gui.DiagramData;
import statistic.graph.gui.DiagramType;
import statistic.graph.gui.DisplayableStatistic;

/**
 *
 * @author Martin Groß
 */
public class XYChartCreator {
    
    private static final Logger LOGGER = Logger.getLogger("statistic.graph.dataset.PieDatasetCreator");
    private JFreeChart chart;
    private XYSeriesCollection dataset;
    private DiagramData diagram;
    private Map<Object, String> labels;
    private List<Object> selectedObjects;
    private Map<DisplayableStatistic, XYSeries> seriesForStatistic;
    private List<DisplayableStatistic> statistics;

    public XYChartCreator(DiagramData diagram, List<DisplayableStatistic> statistics, List<Object> selectedObjects, Map<Object, String> labels) {
        this.diagram = diagram;
        this.labels = labels;
        this.selectedObjects = selectedObjects;
        this.seriesForStatistic = new HashMap<DisplayableStatistic, XYSeries>();
        this.statistics = statistics;
        validateStatistics();
        validateObjects();
        validateLabels();
        createDataset();
        validateSeries();                
        createChart();
    }

    public JFreeChart getChart() {
        return chart;
    }
    
    private void validateStatistics() {
        if (statistics == null) {
            statistics = new LinkedList<DisplayableStatistic>();
        }
        LinkedList<DisplayableStatistic> incomplete = new LinkedList<DisplayableStatistic>();
        for (DisplayableStatistic statistic : statistics) {
            if (!statistic.isInitialized()) {
                incomplete.add(statistic);
            }            
        }
        if (!incomplete.isEmpty()) {
            statistics = new LinkedList<DisplayableStatistic>(statistics);
            for (DisplayableStatistic statistic : incomplete) {
                statistics.remove(statistic);
            }
        }        
    }
    
    private void validateObjects() {
        if (selectedObjects == null) {
            selectedObjects = new LinkedList<Object>();
        }
    }

    private void validateLabels() {
        if (labels == null) {
            labels = new HashMap<Object, String>();
        }
        Map<Class, Integer> counts = new HashMap<Class, Integer>();
        for (Object object : selectedObjects) {
            if (!labels.containsKey(object)) {
                if (object.toString().matches(object.getClass().getName() + "@[0-9a-f]+")) {
                    if (!counts.containsKey(object.getClass())) {
                        counts.put(object.getClass(), 1);
                    }
                    labels.put(object, object.getClass().getSimpleName() + counts.get(object.getClass()));
                    counts.put(object.getClass(), counts.get(object.getClass()) + 1);
                } else {
                    labels.put(object, object.toString());
                }
            }
        }
    }

    private void createDataset() {
        dataset = new XYSeriesCollection();
        for (DisplayableStatistic statistic : statistics) {
            if (statistic.getStatistic().getStatisticsCollection() != null && !statistic.getStatistic().getStatisticsCollection().isEmpty()) {
                addStatistic(statistic);
            }
        }
    }
    
    private void addStatistic(DisplayableStatistic statistic) {
        Operation objOp = statistic.getStatistic().getObjectOperation();
        Operation runOp = statistic.getStatistic().getRunOperation();
        String name = statistic.getAttributes().getName();
        if (selectedObjects.isEmpty()) {
            if (runOp == null || runOp.isComparing()) {
                List values = statistic.getStatistic().getListPerRun(selectedObjects);
                //int index = 0;                
                //XYSeries series = new XYSeries(name);
                for (Object value : values) {
                    //series.add(index++, (Number) value);
                    ///new
                    XYSeries series = createSeries(name, value);
                    seriesForStatistic.put(statistic, series);
                    dataset.addSeries(series);
                    ///end new
                }
                //seriesForStatistic.put(statistic, series);
                //dataset.addSeries(series);
            } else {
                Object value = statistic.getStatistic().get(new Object());
                XYSeries series = createSeries(name, value);
                seriesForStatistic.put(statistic, series);
                dataset.addSeries(series);
            }
        } else {
            if (objOp == null || objOp.isComparing()) {
                if (runOp == null || runOp.isComparing()) {
                    List<List> values = statistic.getStatistic().getListOfLists(selectedObjects);
                    for (List valueList : values) {
                        for (Object value : valueList) {
                            XYSeries series = createSeries(name, value);
                            dataset.addSeries(series);
                        }
                    }
                } else {
                    List values = statistic.getStatistic().getListPerObject(selectedObjects);
                    for (Object value : values) {
                        XYSeries series = createSeries(name, value);
                        dataset.addSeries(series);
                    }
                }
            } else {
                if (runOp == null || runOp.isComparing()) {
                    List values = statistic.getStatistic().getListPerRun(selectedObjects);
                    for (Object value : values) {
                        XYSeries series = createSeries(name, value);
                        dataset.addSeries(series);
                    }
                } else {
                    Object value = statistic.getStatistic().get(selectedObjects);
                    XYSeries series = createSeries(name, value);
                    seriesForStatistic.put(statistic, series);
                    dataset.addSeries(series);
                }
            }
        }
    }

    private XYSeries createSeries(String key, Object data) {
        LOGGER.entering("statistic.graph.dataset.XYChartCreator", "createSeries", new Object[] { key, data });
        XYSeries series = new XYSeries(key);
        if (data instanceof Number) {
            Double value = ((Number) data).doubleValue();
            series.add(Integer.MIN_VALUE, 0);
            series.add(0, value);
            series.add(Integer.MAX_VALUE, value);
        } else if (data instanceof IntegerDoubleMapping) {
            IntegerDoubleMapping idm = (IntegerDoubleMapping) data;
            if (diagram.getType() == DiagramType.BAR_CHART) {
                IntegerDoubleMapping.TimeValuePair start = idm.getFirst();
                IntegerDoubleMapping.TimeValuePair end = idm.getLast();                
                if (start.time() > end.time()) {
                    return series;
                }
                for (int i = start.time(); i <= end.time(); i++) {
                    series.add(i, idm.get(i));
                }
            } else {
                for (IntegerDoubleMapping.TimeValuePair tvp : idm) {
                    series.add(tvp.time(), tvp.value());
                }
            }
        }
        return series;
    }    
    
    private void createChart() {
        switch (diagram.getType()) { 
            case AREA_CHART: {
                chart = Charts.createXYAreaChart(diagram, dataset);
                break;
            }
            case BAR_CHART: {
                chart = Charts.createXYBarChart(diagram, dataset);
                break;
            }
            case LINE_CHART: {
                chart = Charts.createXYLineChart(diagram, dataset);
                break;
            }
            case STEP_AREA_CHART: {
                chart = Charts.createXYStepAreaChart(diagram, dataset);
                break;
            }
            case STEP_CHART: {
                chart = Charts.createXYStepChart(diagram, dataset);
                break;
            }
            default: {
                throw new AssertionError("This should not happen.");
            }
        }
        for (DisplayableStatistic statistic : statistics) {
            if (seriesForStatistic.containsKey(statistic) && statistic.getAttributes().getColor() != null) {
                XYSeries series = seriesForStatistic.get(statistic);
                int index = dataset.indexOf(series.getKey());
                chart.getXYPlot().getRenderer().setSeriesPaint(index, statistic.getAttributes().getColor());
            }
        }
    }
    
    private void validateSeries() {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            for (int j = 0; j < dataset.getSeries(i).getItemCount(); j++) {
                int x = dataset.getSeries(i).getDataItem(j).getX().intValue();
                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
            }
        }               
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            if (dataset.getSeries(i).getItemCount() == 0) {
                continue;
            }
            if (minX != Integer.MIN_VALUE && dataset.getSeries(i).indexOf(minX) < 0) {
                if (diagram.getType() == DiagramType.BAR_CHART) {
                    for (int t = minX; t < dataset.getSeries(i).getX(0).intValue(); t++) {
                        dataset.getSeries(i).add(t, dataset.getSeries(i).getY(0));
                    }
                } else {
                    dataset.getSeries(i).add(minX, dataset.getSeries(i).getY(0));
                }                
            }
            if (maxX != Integer.MAX_VALUE && dataset.getSeries(i).indexOf(maxX) < 0) {
                if (diagram.getType() == DiagramType.BAR_CHART) {
                    for (int t = dataset.getSeries(i).getX(dataset.getItemCount(i) - 1).intValue() + 1; t <= maxX; t++) {
                        dataset.getSeries(i).add(t, dataset.getSeries(i).getY(dataset.getItemCount(i) - 1));
                    }                    
                } else {
                    dataset.getSeries(i).add(maxX, dataset.getSeries(i).getY(dataset.getItemCount(i) - 1));
                }
            }
        }        
    }    
}
