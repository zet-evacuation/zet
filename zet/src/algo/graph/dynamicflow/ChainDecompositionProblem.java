/*
 * ChainDecompositionProblem.java
 *
 */

package algo.graph.dynamicflow;

import ds.graph.DynamicResidualNetwork;
import ds.graph.flow.FlowOverTimeEdgeSequence;
import java.util.List;

/**
 *
 * @author Martin Groß
 */
public class ChainDecompositionProblem {

    private List<FlowOverTimeEdgeSequence> edgeSequences;
    private DynamicResidualNetwork network;

    public ChainDecompositionProblem(List<FlowOverTimeEdgeSequence> edgeSequences, DynamicResidualNetwork network) {
        this.edgeSequences = edgeSequences;
        this.network = network;
    }

    public List<FlowOverTimeEdgeSequence> getEdgeSequences() {
        return edgeSequences;
    }

    public void setEdgeSequences(List<FlowOverTimeEdgeSequence> edgeSequences) {
        this.edgeSequences = edgeSequences;
    }

    public DynamicResidualNetwork getNetwork() {
        return network;
    }

    public void setNetwork(DynamicResidualNetwork network) {
        this.network = network;
    }
}
