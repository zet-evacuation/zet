/*
 * StaticFlowStatistic.java
 *
 */
package statistic.graph;

import ds.graph.flow.FlowOverTimePath;
import ds.graph.Edge;
import static statistic.graph.NodeFlowStatistic.*;

/**
 *
 * @author Martin Groß
 */
public enum StaticFlowStatistic implements Statistic<FlowOverTimePath, Double, GraphData> {

    TOTAL_MOVED_TIME("Gesamtfahrzeit") {

        public Double calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            double time = 0;
            for (Edge edge : flow.getDynamicPath()) {
                time += statistics.getData().getTransitTime(edge);
            }
            return time;
        }
    },
    TOTAL_WAITED_TIME("Gewartete Zeit") {

        public Double calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            double time = 0;
            for (Edge edge : flow.getDynamicPath()) {
                time += flow.delay(edge);
            }
            return time;
        }
    },
    TOTAL_NEEDED_TIME("Gesamtzeit") {

        public Double calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            double time = 0;
            for (Edge edge : flow.getDynamicPath()) {
                time += flow.delay(edge);
                time += statistics.getData().getTransitTime(edge);
            }
            return time;
        }
    },
    DISTANCE_TO_SINK("Entfernung zur Senke") {

        public Double calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            return new Double(statistics.getData().getDistance(statistics.get(SOURCE, flow), statistics.get(SINK, flow)));
        }
    },
    DISTANCE_TO_CLOSEST_SINK("Entfernung zur nächsten Senke") {

        public Double calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            return new Double(statistics.getData().getDistance(statistics.get(SOURCE, flow), statistics.get(CLOSEST_SINK, flow)));
        }
    };
    private String description;

    private StaticFlowStatistic(String description) {
        this.description = description;
    }

    public Class<Double> range() {
        return Double.class;
    }

    @Override
    public String toString() {
        return description;
    }
}
