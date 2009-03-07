/*
 * DynamicEdgeStatistic.java
 *
 */
package statistic.graph;

import ds.graph.Edge;

/**
 *
 * @author Martin Groß
 */
public enum DynamicEdgeStatistic implements Statistic<Edge, IntegerDoubleMapping, GraphData> {

    FLOW_RATE("Flussrate") {

        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, Edge edge) {
            return statistics.getData().getEdgeFlow(edge);
        }
    },
    FLOW_AMOUNT("Flussmenge") {

        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, Edge edge) {
            return statistics.get(FLOW_RATE, edge).integral();
        }
    },
    LOAD("Auslastung") {

        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, Edge edge) {
            return statistics.get(FLOW_RATE, edge).divide(statistics.getData().getCapacity(edge));
        }
    };
    private String description;

    private DynamicEdgeStatistic(String description) {
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
