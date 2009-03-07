/*
 * StaticNodeStatistic.java
 *
 */
package statistic.graph;

import ds.graph.Node;
import static statistic.graph.DynamicNodeStatistic.*;

/**
 *
 * @author Martin Groß
 */
public enum StaticNodeStatistic implements Statistic<Node, Double, GraphData> {

    TOTAL_LOAD("Gemittelte Auslastung") {

        public Double calculate(Statistics<GraphData> statistics, Node node) {
            return statistics.get(STORED_FLOW_AMOUNT, node).get(statistics.getData().getTimeHorizon()) / (statistics.getData().getCapacity(node) * statistics.getData().getTimeHorizon());
        }
    };
    private String description;

    private StaticNodeStatistic(String description) {
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
