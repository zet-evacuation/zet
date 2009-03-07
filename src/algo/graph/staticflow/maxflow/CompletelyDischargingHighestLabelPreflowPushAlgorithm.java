/*
 * PreflowPush.java
 *
 */
package algo.graph.staticflow.maxflow;

import ds.graph.Edge;
import ds.graph.Node;

/**
 *
 * @author Martin Groß
 */
public class CompletelyDischargingHighestLabelPreflowPushAlgorithm extends PreflowPushAlgorithm {

    @Override
    protected void preflowPush() {
        while (!activeNodes.isEmpty()) {
            Node node = activeNodes.getMaximumObject();
            do {
                boolean hasAdmissibleEdge = false;
                for (Edge edge : residualNetwork.outgoingEdges(node)) {
                    if (isAdmissible(edge)) {
                        push(edge);
                        hasAdmissibleEdge = true;
                        break;
                    }
                }
                if (!hasAdmissibleEdge) {
                    relabel(node);
                }
            } while (excess.get(node) > 0);
            if (excess.get(node) == 0) {
                activeNodes.extractMax();
            }
        }
    }
}
