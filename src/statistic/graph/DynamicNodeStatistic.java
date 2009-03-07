/*
 * DynamicNodeStatistic.java
 *
 */
package statistic.graph;

import ds.graph.Edge;
import ds.graph.Node;
import static statistic.graph.DynamicEdgeStatistic.*;

/**
 *
 * @author Martin Groß
 */
public enum DynamicNodeStatistic implements Statistic<Node, IntegerDoubleMapping, GraphData> {

    INCOMING_FLOW_RATE("Eingehende Flussrate") {

        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, Node node) {
            IntegerDoubleMapping result = new IntegerDoubleMapping();
            for (Edge edge : statistics.getData().getNetwork().incomingEdges(node)) {
                result = result.add(statistics.get(FLOW_RATE, edge).shift(statistics.getData().getTransitTime(edge)));
            }
            return result;
        }
    },
    INCOMING_FLOW_AMOUNT("Eingehende Flussmenge") {

        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, Node node) {
            return statistics.get(INCOMING_FLOW_RATE, node).integral();
        }
    },
    OUTGOING_FLOW_RATE("Ausgehende Flussrate") {

        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, Node node) {
            IntegerDoubleMapping result = new IntegerDoubleMapping();
            for (Edge edge : statistics.getData().getNetwork().outgoingEdges(node)) {
                result = result.add(statistics.get(FLOW_RATE, edge));
            }
            return result;
        }
    },
    OUTGOING_FLOW_AMOUNT("Ausgehende Flussmenge") {

        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, Node node) {
            return statistics.get(OUTGOING_FLOW_RATE, node).integral();
        }
    },
    FLOW_STORAGE_RATE("Speicherrate") {

        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, Node node) {
            IntegerDoubleMapping result = new IntegerDoubleMapping();
            result = result.add(statistics.get(INCOMING_FLOW_RATE, node));
            result.subtractMapping(statistics.get(OUTGOING_FLOW_RATE, node));
            return result;
        }
    },
    SUPPLY("Gespeicherter Fluss") {

        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, Node node) {
            return statistics.get(FLOW_STORAGE_RATE, node).integral().add(statistics.getData().getSupply(node));
        }
    },
    STORED_FLOW_AMOUNT("Gespeicherte Flussmenge") {

        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, Node node) {
            return statistics.get(SUPPLY, node).integral();
        }
    },
    LOAD("Auslastung") {

        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, Node node) {
            return statistics.get(SUPPLY, node).divide(statistics.getData().getCapacity(node));
        }
    };
    private String description;

    private DynamicNodeStatistic(String description) {
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
