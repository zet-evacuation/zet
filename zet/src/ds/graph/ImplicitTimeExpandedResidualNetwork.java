/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * ImplicitTimeExpandedResidualNetwork.java
 *
 */
package ds.graph;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import ds.graph.flow.EarliestArrivalAugmentingPath;
import ds.graph.flow.EarliestArrivalAugmentingPath.NodeTimePair;

/**
 * A class representing an implicit time-expanded residual network, i.e. a 
 * residual network of a time-expanded network, in which the time-copies are not
 * created explicitly.
 * 
 * @author Martin Groß
 */
public class ImplicitTimeExpandedResidualNetwork extends Network {

    /**
     * References the underlying earliest arrival flow problem.
     */
    private EarliestArrivalFlowProblem problem;
    /**
     * Stores the flow over time corresponding to this network.
     */
    private IdentifiableObjectMapping<Edge, IntegerIntegerMapping> flow;
    /**
     * Keeps track of waiting of flow in nodes.
     */
    private IdentifiableObjectMapping<Node, IntegerIntegerMapping> waiting;

    /**
     * Creates an implicit time-expanded residual network for the specified 
     * problem.
     * @param problem the problem for which the implicit time-expanded residual 
     * network is to be created.
     */
    public ImplicitTimeExpandedResidualNetwork(EarliestArrivalFlowProblem problem) {
        super(problem.getNetwork().numberOfNodes() + 1, 
                problem.getNetwork().numberOfEdges() * 2 
                + problem.getSources().size());
        this.problem = problem;
        setEdges(problem.getNetwork().edges());
        for (Edge edge : problem.getNetwork().edges()) {
            createAndSetEdge(edge.end(), edge.start());
        }
        flow = new IdentifiableObjectMapping<Edge, IntegerIntegerMapping>(problem.getNetwork().edges(), IntegerIntegerMapping.class);
        for (Edge edge : problem.getNetwork().edges()) {
            flow.set(edge, new IntegerIntegerMapping());
        }
        waiting = new IdentifiableObjectMapping<Node, IntegerIntegerMapping>(problem.getNetwork().nodes(), IntegerIntegerMapping.class);
        for (Node node : problem.getNetwork().nodes()) {
            waiting.set(node, new IntegerIntegerMapping());
        }
    }

    private void augmentEdge(NodeTimePair first, NodeTimePair second, int amount) {
        Edge edge = findEdge(first.getNode(), second.getNode(), second.getStart() - first.getEnd());
        if (isReverseEdge(edge)) {
            flow.get(reverseEdge(edge)).decrease(first.getEnd(), amount);
        } else {
            flow.get(edge).increase(first.getEnd(), amount);
        }
    }

    private void augmentNode(Node node, int start, int end, int amount) {
        if (start < end) {
            waiting.get(node).increase(start, end, amount);
        } else if (start > end) {
            waiting.get(node).decrease(end, start, amount);
        }
    }

    /**
     * Augments the specified path to the flow represented by this network. 
     * @param path the path to be augmented.
     */
    public void augmentPath(EarliestArrivalAugmentingPath path) {
        if (path.isEmpty()) {
            return;
        }
        NodeTimePair first = path.getFirst();
        augmentNode(first.getNode(), 0, first.getStart(), path.getCapacity());
        for (NodeTimePair ntp : path) {
            if (ntp.getStart() != ntp.getEnd()) {
                augmentNode(ntp.getNode(), ntp.getStart(), ntp.getEnd(), path.getCapacity());
            }
            if (ntp == first) {
                continue;
            } else {
                augmentEdge(first, ntp, path.getCapacity());
                first = ntp;
            }
        }
    }

    /**
     * Returns the residual capacity of the specified edge at the specified 
     * point in time. For a normal edge, this is the capacity of the edge minus
     * the value of flow on the edge at the specified time. For reverse edges, 
     * it is the value of flow send on the corresponding normal edge.
     * @param edge the edge for which the residual capacity is to be returned.
     * @param time the point in time for which the residual capacity of the edge
     * is to be returned.
     * @return the residual capacity of the specified edge at the specified 
     * point in time.
     */
    public int capacity(Edge edge, int time) {
        if (isReverseEdge(edge)) {
            return flow.get(reverseEdge(edge)).get(time);
        } else {
            return problem.getEdgeCapacities().get(edge) - flow.get(edge).get(time);
        }
    }

    /**
     * Returns the residual waiting capacity at the specified node at the
     * specified point in time. This is the waiting capacity of the node minus
     * the value of flow currently waiting at the node at the time. If no 
     * waiting capacities for nodes have been specified at the construction of 
     * the network, the waiting capacities are assumed to be infinite.
     * @param node the node for which the residual capacity is to be returned.
     * @param time the point in time for which the residual capacity of the node
     * is to be returned.
     * @return the residual capacity of the specified node at the specified 
     * point in time, or <code>Integer.MAX_VALUE</code> if waiting capacities 
     * are assumed to be infinite.
     */
    public int capacity(Node node, int time) {
        return capacity(node, time, false);
    }

    /**
     * Returns the residual capacity for undoing waiting at the specified node 
     * at the specified point in time. This is the amount of flow that has been 
     * waiting at the node at time <code>time-1</code>.
     * @param node the node for which the residual capacity for undoing waiting
     * is to be returned.
     * @param time the point in time for which the residual capacity for undoing 
     * waiting at the node is to be returned.
     * @return the residual capacity for undoing waiting at the specified node
     * at the specified point in time.
     */
    public int capacity(Node node, int time, boolean reverse) {
        if (reverse) {
            return waiting.get(node).get(time - 1);
        } else {
            if (problem.getNodeCapacities() == null) {
                return Integer.MAX_VALUE;
            } else {
                return problem.getNodeCapacities().get(node) - waiting.get(node).get(time);
            }
        }
    }

    /**
     * Returns an edge between the specified start and end node with the 
     * specified transit time. If multiple edges fulfilling these criteria 
     * exist, the edge found first is returned. The normal <code>getEdge</code>
     * method should be preferred over this one.
     * @param start the start node of desired edge.
     * @param end the end node of desired edge.
     * @param transitTime the transit time of desired edge.
     * @return an edge between the specified start and end node with the 
     * specified transit time, or <code>null</code> if no such edge exists in 
     * the network.
     */
    @Deprecated
    public Edge findEdge(Node start, Node end, int transitTime) {
        Iterable<Edge> candidates = getEdges(start, end);
        Edge result = null;
        for (Edge edge : candidates) {
            if (transitTime(edge) == transitTime) {
                result = edge;
                break;
            }
        }
        return result;
    }

    /**
     * Returns the flow over time this residual network is corresponding to.
     * @return the flow over time this residual network is corresponding to.
     */
    public IdentifiableObjectMapping<Edge, IntegerIntegerMapping> flow() {
        return flow;
    }
    
    /**
     * Returns the underlying earliest arrival flow problem.
     * @return the underlying earliest arrival flow problem. 
     */
    public EarliestArrivalFlowProblem getProblem() {
        return problem;
    }

    /**
     * Returns whether the specified edge is an reverse edge.
     * @param edge the edge to be tested for being an reverse edge.
     * @return <code>true</code>, if the specified edge is an reverse edge, 
     * <code>false</code> otherwise.
     */
    public boolean isReverseEdge(Edge edge) {
        return edge.id() >= problem.getNetwork().numberOfEdges();
    }

    /**
     * Returns the reverse edge of the specified edge. Note that the reverse 
     * edge of an reverse edge is again the normal edge.
     * @param edge the edge for which the reverse edge is to be returned. 
     * @return the reverse edge of the specified edge.
     */
    public Edge reverseEdge(Edge edge) {
        if (edge.id() < problem.getNetwork().numberOfEdges()) {
            return edges.getEvenIfHidden(edge.id() + problem.getNetwork().numberOfEdges());
        } else {
            return edges.getEvenIfHidden(edge.id() - problem.getNetwork().numberOfEdges());
        }
    }
    
    private Node superSource;
    /**
     * Returns the super source of this network.
     * @return the super source of this network.
     */
    public Node superSource() {
        return superSource;
    }

    /**
     * Returns the time horizon of this network.
     * @return the time horizon of this network.
     */
    public int timeHorizon() {
        return problem.getTimeHorizon();
    }

    /** 
     * Returns the transit time of the specified edge. For an reverse edge, this
     * is the inverse of the transit time of its corresponding normal edge; for
     * an artificial edge, it is 0.
     * @param edge the edge for which the transit time is to be returned.
     * @return the transit time of the specified edge.
     */
    public int transitTime(Edge edge) {
        if (isReverseEdge(edge)) {
            return -problem.getTransitTimes().get(reverseEdge(edge));
        } else {
            return problem.getTransitTimes().get(edge);
        }
    }
}
