/*
 * EarliestArrivalAugmentingPathProblem.java
 *
 */

package algo.graph.dynamicflow.eat;

import ds.graph.DynamicResidualNetwork;
import ds.graph.Node;

/**
 *
 * @author Martin Groß
 */
public class EarliestArrivalAugmentingPathProblem {
    
    private DynamicResidualNetwork network;
    private Node source;
    private Node sink;
    private int timeHorizon;

    public EarliestArrivalAugmentingPathProblem(DynamicResidualNetwork network, Node source, Node sink, int timeHorizon) {
        this.network = network;
        this.source = source;
        this.sink = sink;
        this.timeHorizon = timeHorizon;
    }

    public DynamicResidualNetwork getNetwork() {
        return network;
    }

    public void setNetwork(DynamicResidualNetwork network) {
        this.network = network;
    }

    public Node getSink() {
        return sink;
    }

    public void setSink(Node sink) {
        this.sink = sink;
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public int getTimeHorizon() {
        return timeHorizon;
    }

    public void setTimeHorizon(int timeHorizon) {
        this.timeHorizon = timeHorizon;
    }    
}
