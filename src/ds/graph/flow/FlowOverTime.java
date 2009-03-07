/*
 * FlowOverTime.java
 *
 */
package ds.graph.flow;

import algo.graph.dynamicflow.ChainDecomposition2;
import algo.graph.dynamicflow.eat.EarliestArrivalAugmentingPath;
import ds.graph.DynamicResidualNetwork;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Martin Groß
 */
public class FlowOverTime {

    private static final boolean DEBUG = false;
    
    private EdgeBasedFlowOverTime edgeBased;
    private PathBasedFlowOverTime pathBased;
    private int flowAmount;
    private int timeHorizon;

    public FlowOverTime(DynamicResidualNetwork network, Queue<EarliestArrivalAugmentingPath> eaaPaths) {
        edgeBased = new EdgeBasedFlowOverTime(network.flow());
        pathBased = new PathBasedFlowOverTime();
        LinkedList<FlowOverTimeEdgeSequence> paths = new LinkedList<FlowOverTimeEdgeSequence>();
        int index = 0;
        for (EarliestArrivalAugmentingPath eaaPath : eaaPaths) {
            if (DEBUG) System.out.println( (index++) + ":\n " + eaaPath + "\n " + eaaPath.getFlowOverTimeEdgeSequence(network).toText(network.transitTimes()));
            paths.add(eaaPath.getFlowOverTimeEdgeSequence(network));
            flowAmount += eaaPath.getCapacity();
            timeHorizon = Math.max(timeHorizon, eaaPath.getArrivalTime() + 1);
        }
        ChainDecomposition2 pd = new ChainDecomposition2();
        pd.pathBased = pathBased;
        pd.uncrossPaths(network, paths);
    }
    
    public EdgeBasedFlowOverTime getEdgeBased() {
        return edgeBased;
    }

    public int getFlowAmount() {
        return flowAmount;
    }

    public PathBasedFlowOverTime getPathBased() {
        return pathBased;
    }

    public int getTimeHorizon() {
        return timeHorizon;
    }
}
