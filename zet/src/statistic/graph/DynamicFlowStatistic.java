/*
 * DynamicFlowStatistic.java
 *
 */
package statistic.graph;

import ds.graph.flow.FlowOverTimePath;
import ds.graph.Edge;

/**
 *
 * @author Martin Groß
 */
public enum DynamicFlowStatistic implements Statistic<FlowOverTimePath, IntegerDoubleMapping, GraphData> {

    MOVED_TIME("Gesamtfahrzeit") {

        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            IntegerDoubleMapping result = new IntegerDoubleMapping(true);
            int time = 0;
            int value = 0;
            for (Edge edge : flow) {
                time += flow.delay(edge);
                result.set(time, value);
                time += statistics.getData().getTransitTime(edge);
                value += statistics.getData().getTransitTime(edge);
                result.set(time, value);
            }
            return result;
        }
    },
    WAITED_TIME("Gewartete Zeit") {

        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            IntegerDoubleMapping result = new IntegerDoubleMapping(true);
            int time = 0;
            int value = 0;
            result.set(time, value);
            for (Edge edge : flow) {
                time += flow.delay(edge);
                value += statistics.getData().getTransitTime(edge);
                result.set(time, value);
                time += statistics.getData().getTransitTime(edge);
                result.set(time, value);
            }
            return result;
        }
    };
    private String description;

    private DynamicFlowStatistic(String description) {
        this.description = description;
    }

    public Class<IntegerDoubleMapping> range() {
        return IntegerDoubleMapping.class;
    }

    @Override
    public String toString() {
        return description;
    }
}
