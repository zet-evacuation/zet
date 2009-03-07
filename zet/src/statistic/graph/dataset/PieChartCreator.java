/*
 * PieChartCreator.java
 *
 */
package statistic.graph.dataset;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import statistic.graph.IntegerDoubleMapping;
import statistic.graph.Operation;
import statistic.graph.gui.Charts;
import statistic.graph.gui.DiagramData;
import statistic.graph.gui.DisplayableStatistic;

/**
 *
 * @author Martin Groß
 */
public class PieChartCreator {

    private static final Logger LOGGER = Logger.getLogger("statistic.graph.dataset.PieDatasetCreator");
    private JFreeChart chart;
    private DefaultPieDataset dataset;
    private DiagramData diagram;
    private Map<Object, String> labels;
    private List<Object> selectedObjects;
    private DisplayableStatistic statistic;

    public PieChartCreator(DiagramData diagram, DisplayableStatistic statistic, List<Object> selectedObjects, Map<Object, String> labels) {
        this.diagram = diagram;
        this.labels = labels;
        this.selectedObjects = selectedObjects;
        this.statistic = statistic;
        validateStatistic();
        validateObjects();
        validateLabels();
        createDataset();
        createChart();
    }

    public JFreeChart getChart() {
        return chart;
    }
    
    private void validateStatistic() {
        if (statistic != null && !statistic.isInitialized()) {
            statistic = null;
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
        dataset = new DefaultPieDataset();
        if (statistic == null) {
            return; 
        }
        if (statistic.getStatistic().getStatisticsCollection() == null || statistic.getStatistic().getStatisticsCollection().isEmpty()) {        
            return;
        }
        Operation objOp = statistic.getStatistic().getObjectOperation();
        Operation runOp = statistic.getStatistic().getRunOperation();
        if (selectedObjects.isEmpty()) {
            if (runOp == null || runOp.isComparing()) {
                List values = statistic.getStatistic().getListPerObject(selectedObjects);
                for (int i = 0; i < selectedObjects.size(); i++) {
                    Double v = getDoubleValue(values.get(i));
                    dataset.setValue(i, (Number) v);
                }
            } else {
                throw new AssertionError("This should not happen.");
            }
        } else {
            if (objOp == null || objOp.isComparing()) {
                if (runOp == null || runOp.isComparing()) {
                    throw new AssertionError("This should not happen.");
                } else {
                    List values = statistic.getStatistic().getListPerObject(selectedObjects);
                    for (int i = 0; i < selectedObjects.size(); i++) {
                        Double v = getDoubleValue(values.get(i));
                        dataset.setValue(i, (Number) v);
                    }
                }
            } else {
                if (runOp == null || runOp.isComparing()) {
                    List values = statistic.getStatistic().getListPerRun(selectedObjects);
                    for (int i = 0; i < selectedObjects.size(); i++) {
                        Double v = getDoubleValue(values.get(i));
                        dataset.setValue(i, (Number) v);
                    }
                } else {
                    throw new AssertionError("This should not happen.");
                }
            }
        }
    }
    
    private double getDoubleValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof IntegerDoubleMapping) {
            return ((IntegerDoubleMapping) value).getLastValue();
        } else {
            throw new AssertionError("This should not happen.");
        }
    }

    private void createChart() {
        switch (diagram.getType()) { 
            case PIE_CHART: {
                chart = Charts.createPieChart(diagram, dataset);
                break;
            }
            case PIE_CHART_3D: {
                chart = Charts.createPieChart3D(diagram, dataset);
                break;
            }
            case RING_CHART: {
                chart = Charts.createRingChart(diagram, dataset);
                break;
            }
            default: {
                throw new AssertionError("This should not happen.");
            }
        }
    }
}