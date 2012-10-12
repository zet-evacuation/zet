/*
 * FlowOverTimePathDecomposition.java
 * 
 */
package de.tu_berlin.math.coga.algorithm.flowovertime;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.datastructure.mapping.IdentifiableIntegerIntegerMapping;
import de.tu_berlin.math.coga.datastructure.mapping.IdentifiableIntegerObjectMapping;
import de.tu_berlin.math.coga.datastructure.priorityQueue.MinHeap;
import ds.graph.Edge;
import ds.graph.ImplicitTimeExpandedResidualNetwork;
import ds.graph.Node;
import ds.graph.flow.EdgeBasedFlowOverTime;
import ds.graph.flow.FlowOverTimeEdge;
import ds.graph.flow.FlowOverTimePath;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.mapping.IdentifiableIntegerMapping;
import java.util.Iterator;

/**
 * This class decomposes a flow over time given by an
 * {@code ImplicitTimeExpandedResidualNetwork} and decomposes it into a
 * {@code PathBasedFlowOverTime}.
 *
 * @author Martin Groß
 */
public class FlowOverTimePathDecomposition extends Algorithm<ImplicitTimeExpandedResidualNetwork, PathBasedFlowOverTime> {

    /**
     * Stores the flow this algorithm works on. Only used internally during the 
     * computation.
     */
    protected transient EdgeBasedFlowOverTime flow;
    /**
     * The input for algorithm. Only used internally during the 
     * computation.
     */
    protected transient ImplicitTimeExpandedResidualNetwork network;
    /**
     * Stores the flow this algorithm works on. Only used internally during the 
     * computation.
     */
    protected transient IdentifiableIntegerMapping<Edge> superSourceFlow;
    /**
     * Stores the flow this algorithm works on. Only used internally during the 
     * computation.
     */
    protected transient IdentifiableIntegerIntegerMapping<Node> waitingFlow;

    @Override
    protected PathBasedFlowOverTime runAlgorithm(ImplicitTimeExpandedResidualNetwork network) {
        // Store the reference to the network for other methods
        this.network = network;
        // Clone the input because we are going to modify it
        flow = new EdgeBasedFlowOverTime(network.flow().clone());
        superSourceFlow = network.superSourceFlow().clone();
        waitingFlow = new IdentifiableIntegerIntegerMapping<>(network.waitingFlow());
        // Initialize the data structure for the result
        PathBasedFlowOverTime result = new PathBasedFlowOverTime();
        // Iteratively extract source-sink paths from the flow
        FlowOverTimePath path;
        do {
            // Find a source-sink-path
            IdentifiableIntegerMapping<Node> arrivalTimes = new IdentifiableIntegerMapping<>(network.numberOfNodes());
            IdentifiableIntegerObjectMapping<Node, FlowOverTimeEdge> preceedingEdges = new IdentifiableIntegerObjectMapping<>(network.numberOfNodes(), FlowOverTimeEdge.class);
            initialize(preceedingEdges, arrivalTimes);
            // Trace back a path from the sink to the super source
            path = constructPath(preceedingEdges, arrivalTimes);
            // If a path has been found...
            if (path != null) {
                // Subtract as much flow as possible from the path
                subtractPath(path);
                // The first edge might be artifical and should be removed for the
                // path decomposition, if it is.
                if (network.hasArtificialSuperSource()) {
                    path.removeFirst();
                }
                removeLoops(path);
                result.addPathFlow(path);
            }
        } while (path != null);
        // Return the path flow found
        return result;
    }

    /**
     * Computes a shortest super-source to sink path and stores the preceeding
     * edges and arrival times in the supplied data structures.
     * @param preceedingEdges data structure to store the results.
     * @param arrivalTimes data structure to store the results. 
     */
    protected void initialize(IdentifiableIntegerObjectMapping<Node, FlowOverTimeEdge> preceedingEdges, IdentifiableIntegerMapping<Node> arrivalTimes) {
        MinHeap<Node, Integer> queue = new MinHeap<>(network.numberOfNodes());
        for (int v = 0; v < network.numberOfNodes(); v++) {
            queue.insert(network.getNode(v), Integer.MAX_VALUE);
        }
        arrivalTimes.set(network.superSource(), 0);
        queue.decreasePriority(network.superSource(), 0);
        while (!queue.isEmpty()) {
            MinHeap<Node, Integer>.Element min = queue.extractMin();
            Node node = min.getObject();
            Integer distance = min.getPriority();
            arrivalTimes.set(node, distance);
            if (distance == Integer.MAX_VALUE) {
                continue;
            }
            for (Edge edge : network.outgoingEdges(node)) {
                if (network.isReverseEdge(edge)) {
                    continue;
                }
                int time;
                if (node == network.superSource() && network.hasArtificialSuperSource()) {
                    time = (superSourceFlow.get(edge) > 0) ? 0 : Integer.MAX_VALUE;
                } else {
                    time = flow.get(edge).nextPositiveValue(distance);
                }
                if (time == Integer.MAX_VALUE) {
                    continue;
                }
                if (waitingFlow.minimum(node, distance, time) == 0) {
                    continue;
                }
                if (time > distance) {
                    preceedingEdges.set(node, time, new FlowOverTimeEdge(new Edge(-1,node,node), time-distance, distance));
                }
                Node w = edge.opposite(node);
                if (queue.contains(w) && (long) queue.priority(w) > (long) time + (long) network.transitTime(edge)) {
                    queue.decreasePriority(w, time + network.transitTime(edge));
                    preceedingEdges.set(w, time + network.transitTime(edge), new FlowOverTimeEdge(edge,0,time));
                }
            }
        }
    }

    /**
     * Traces back a flow over time path from super source to sink and returns
     * it.
     *
     * @param preceedingEdges an in-forest specified by entering edges.
     * @param arrivalTimes
     * @return a flow over time path from super source to sink.
     */
    protected FlowOverTimePath constructPath(IdentifiableIntegerObjectMapping<Node, FlowOverTimeEdge> preceedingEdges, IdentifiableIntegerMapping<Node> arrivalTimes) {
        FlowOverTimePath path = new FlowOverTimePath();
        Node node = network.getProblem().getSink();
        int time = arrivalTimes.get(node);
        while (node != network.superSource()) {
            FlowOverTimeEdge edge = preceedingEdges.get(node, time);
            if (edge == null) {
                return null;
            }
            Node start = edge.getEdge().opposite(node);
            int delay = time - network.transitTime(edge.getEdge()) - arrivalTimes.get(start);
            path.addFirst(new FlowOverTimeEdge(edge.getEdge(), delay, arrivalTimes.get(start) + delay));
            time = arrivalTimes.get(start);
            node = start;
        }
        return path;
    }

    /**
     * Determines the bottleneck capacity of the specified path and subtracts
     * flow from this path equal to the bottleneck capacity.
     *
     * @param path the path from which flow is subtracted.
     */
    protected void subtractPath(FlowOverTimePath path) {
        // Determine the bottleneck capacity
        int capacity = Integer.MAX_VALUE;
        int lastArrival = 0;
        for (FlowOverTimeEdge edge : path) {
            capacity = Math.min(waitingFlow.minimum(edge.getEdge().start(), lastArrival, edge.getTime()), capacity);
            if (edge.getEdge().start() == network.superSource() && network.hasArtificialSuperSource()) {
                capacity = Math.min(superSourceFlow.get(edge.getEdge()), capacity);
            } else if (edge.getEdge().isLoop()) {                
            } else {
                capacity = Math.min(flow.get(edge.getEdge()).get(edge.getTime()), capacity);
            }
            lastArrival = edge.getTime() + network.transitTime(edge.getEdge());
        }
        // Subtract the bottleneck flow value from the path
        lastArrival = 0;
        for (FlowOverTimeEdge edge : path) {
            waitingFlow.decrease(edge.getEdge().start(), lastArrival, edge.getTime(), capacity);
            if (edge.getEdge().start() == network.superSource() && network.hasArtificialSuperSource()) {
                superSourceFlow.decrease(edge.getEdge(), capacity);
            } else if (edge.getEdge().isLoop()) {                
            } else {
                flow.get(edge.getEdge()).decrease(edge.getTime(), capacity);
            }
            lastArrival = edge.getTime();
        }
    }

    /**
     * Removes loops from the flow over time path.
     * @param path the path from which the loops are to be removed.
     */
    protected void removeLoops(FlowOverTimePath path) {
        for (Iterator<FlowOverTimeEdge> iterator = path.iterator(); iterator.hasNext();) {
            FlowOverTimeEdge edge = iterator.next();
            if (edge.getEdge().isLoop()) {
                iterator.remove();
            }
        }
    }
}
