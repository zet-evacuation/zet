/*
 * NodeFlowStatistic.java
 *
 */
package statistic.graph;

import ds.graph.flow.FlowOverTimePath;
import ds.graph.Node;

/**
 *
 * @author Martin Groß
 */
public enum NodeFlowStatistic implements Statistic<FlowOverTimePath, Node, GraphData> {

    SOURCE {

        public Node calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            return flow.getPath().first().start();
        }
    },
    SINK {

        public Node calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            return flow.getPath().last().end();
        }
    },
    CLOSEST_SINK {

        public Node calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            int min = Integer.MAX_VALUE;
            Node result = null;
            for (Node sink : statistics.getData().getSinks()) {
                if (statistics.getData().getDistance(statistics.get(SOURCE, flow), sink) < min) {
                    min = statistics.getData().getDistance(statistics.get(SOURCE, flow), sink);
                    result = sink;
                }
            }
            return result;
        }
    };

    public Class<Node> range() {
        return Node.class;
    }
}
