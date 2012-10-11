/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

/**
 * This class decomposes a flow over time given by an
 * {@code ImplicitTimeExpandedResidualNetwork} and decomposes it into a
 * {@code PathBasedFlowOverTime}.
 *
 * @author Martin Groß
 */
public class FlowOverTimePathDecomposition extends Algorithm<ImplicitTimeExpandedResidualNetwork, PathBasedFlowOverTime> {

    protected transient EdgeBasedFlowOverTime flow;
    protected transient ImplicitTimeExpandedResidualNetwork network;
    protected transient IdentifiableIntegerMapping<Edge> superSourceFlow;
    protected transient IdentifiableIntegerIntegerMapping<Node> waitingFlow;

    @Override
    protected PathBasedFlowOverTime runAlgorithm(ImplicitTimeExpandedResidualNetwork network) {
        this.network = network;
        flow = new EdgeBasedFlowOverTime(network.flow().clone());
        superSourceFlow = network.superSourceFlow().clone();
        waitingFlow = new IdentifiableIntegerIntegerMapping<>(network.waitingFlow());

        PathBasedFlowOverTime result = new PathBasedFlowOverTime();
        FlowOverTimePath path = null;
        do {
            // Find a source-sink-path
            IdentifiableIntegerMapping<Node> arrivalTimes = new IdentifiableIntegerMapping<>(network.numberOfNodes());
            IdentifiableIntegerObjectMapping<Node, Edge> preceedingEdges = new IdentifiableIntegerObjectMapping<>(network.numberOfNodes(), Edge.class);
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
                    // I
                    if (node == network.superSource() && network.hasArtificialSuperSource()) {
                        time = (superSourceFlow.get(edge) > 0) ? 0 : Integer.MAX_VALUE;
                    } else {
                        time = flow.get(edge).nextPositiveValue(distance);
                    }
                    if (time == Integer.MAX_VALUE) {
                        continue;
                    }
                    Node w = edge.opposite(node);
                    if (queue.contains(w) && (long) queue.priority(w) > (long) time + (long) network.transitTime(edge)) {
                        queue.decreasePriority(w, time + network.transitTime(edge));
                        //preceedingEdges.set(w, edge);
                    }
                }
            }
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
                result.addPathFlow(path);
            }
        } while (path != null);
        return result;
    }

    /**
     * Traces back a flow over time path from super source to sink and returns
     * it.
     *
     * @param preceedingEdges an in-forest specified by entering edges.
     * @param arrivalTimes
     * @return a flow over time path from super source to sink.
     */
    protected FlowOverTimePath constructPath(IdentifiableIntegerObjectMapping<Node, Edge> preceedingEdges, IdentifiableIntegerMapping<Node> arrivalTimes) {
        FlowOverTimePath path = new FlowOverTimePath();
        Node node = network.getProblem().getSink();
        int time = arrivalTimes.get(node);
        while (node != network.superSource()) {
            Edge edge = preceedingEdges.get(node, time);
            if (edge == null) {
                return null;
            }
            Node start = edge.opposite(node);
            int delay = time - network.transitTime(edge) - arrivalTimes.get(start);
            path.addFirst(new FlowOverTimeEdge(edge, delay, arrivalTimes.get(start) + delay));
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
        // Determine the bottleneack capacity
        int capacity = Integer.MAX_VALUE;
        int lastArrival = 0;
        for (FlowOverTimeEdge edge : path) {
            capacity = Math.min(waitingFlow.minimum(edge.getEdge().start(), lastArrival, edge.getTime()), capacity);
            if (edge.getEdge().start() == network.superSource() && network.hasArtificialSuperSource()) {
                capacity = Math.min(superSourceFlow.get(edge.getEdge()), capacity);
            } else {
                capacity = Math.min(flow.get(edge.getEdge()).get(edge.getTime()), capacity);
            }
            lastArrival = edge.getTime() + network.transitTime(edge.getEdge());
        }
        // Subtract the bottleneck flow value from the path
        lastArrival = 0;
        for (FlowOverTimeEdge edge : path) {
            waitingFlow.decrease(edge.getEdge().start(), lastArrival, edge.getTime());
            if (edge.getEdge().start() == network.superSource() && network.hasArtificialSuperSource()) {
                superSourceFlow.decrease(edge.getEdge(), capacity);
            } else {
                flow.get(edge.getEdge()).decrease(edge.getTime(), capacity);
            }
            lastArrival = edge.getTime(); 
        }
    }
}
