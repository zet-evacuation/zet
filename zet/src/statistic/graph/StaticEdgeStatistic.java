/*
 * StaticEdgeStatistic.java
 *
 */
package statistic.graph;

import ds.graph.Edge;
import static statistic.graph.DynamicEdgeStatistic.*;

/**
 *
 * @author Martin Groß
 */
public enum StaticEdgeStatistic implements Statistic<Edge, Double, GraphData> {

    CAPACITY("Kapazität") {

        public Double calculate(Statistics<GraphData> statistics, Edge edge) {
            return new Double(statistics.getData().getCapacity(edge));
        }        
    },
    TOTAL_LOAD("Gemittelte Auslastung") {

        public Double calculate(Statistics<GraphData> statistics, Edge edge) {
            return statistics.get(FLOW_AMOUNT, edge).get(statistics.getData().getTimeHorizon()) / (statistics.getData().getCapacity(edge) * statistics.getData().getTimeHorizon());
        }
    };
    private String description;

    private StaticEdgeStatistic(String description) {
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
