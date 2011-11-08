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
     * Enumeration constants for the different types of edges in this network:
     * <code>NORMAL</code> for edges of the original network, 
     * <code>ARTIFICIAL</code> for edges from the super-source,
     * <code>REVERSE</code> for reverse edges and
     * <code>ARTIFICIAL_REVERSE</code> for reverse artificial edges.
     */
    private enum EdgeType { NORMAL, REVERSE, ARTIFICIAL, ARTIFICIAL_REVERSE; }

    /**
     * Stores the type of edges.
     */
    private IdentifiableObjectMapping<Edge, EdgeType> edgeTypes;
    /**
     * Stores the flow over time corresponding to this network.
     */
    private IdentifiableObjectMapping<Edge, IntegerIntegerMapping> flow;
    /**
     * References the underlying earliest arrival flow problem.
     */
    private EarliestArrivalFlowProblem problem;
    /**
     * Stores the corresponding reverse edges of edges.
     */
    private IdentifiableObjectMapping<Edge, Edge> reverseEdges;
    /**
     * References the super source of the problem.
     */
    private Node superSource;
    /**
     * Stores flow leaving the super source at time 0.
     */
    private IdentifiableIntegerMapping<Edge> superSourceFlow;
    /**
     * Keeps track of waiting of flow in nodes.
     */
    private IdentifiableObjectMapping<Node, IntegerIntegerMapping> waiting;

    /**
     * Creates an implicit time-expanded residual network for the specified 
     * problem. This means that reverse edges are introduced for all edges of
     * the network. If the underlying network has multiple sources, this also 
     * involves adding a super source that is connected to the sources at time 0
     * by an edge of zero transit time and capacity according to the supply of 
     * the node.
     * @param problem the problem for which the implicit time-expanded residual 
     * network is to be created.
     */
    public ImplicitTimeExpandedResidualNetwork(EarliestArrivalFlowProblem problem) {        
        super(problem.getNetwork().numberOfNodes() + ((problem.getSources().size() > 1)? 1 : 0), 
                problem.getNetwork().numberOfEdges() * 2 + ((problem.getSources().size() > 1)? problem.getSources().size() : 0));
        this.edgeTypes = new IdentifiableObjectMapping<Edge, EdgeType>(getEdgeCapacity(), EdgeType.class);
        this.problem = problem;
        this.reverseEdges = new IdentifiableObjectMapping<Edge, Edge>(getEdgeCapacity(), Edge.class);
        this.superSource = getNode(numberOfNodes() - 1);
        this.superSourceFlow = new IdentifiableIntegerMapping<Edge>(getEdgeCapacity());
        setEdges(problem.getNetwork().edges());
        for (Edge edge : problem.getNetwork().edges()) {
            Edge e = createAndSetEdge(edge.end(), edge.start());
            edgeTypes.set(edge, EdgeType.NORMAL);
            edgeTypes.set(e, EdgeType.REVERSE);
            reverseEdges.set(edge, e);
            reverseEdges.set(e, edge);
        }
        if (problem.getSources().size() > 1) {
            for (Node source : problem.getSources()) {
                Edge edge = createAndSetEdge(superSource, source);
                Edge reverse = createAndSetEdge(source, superSource);
                edgeTypes.set(edge, EdgeType.ARTIFICIAL);
                edgeTypes.set(reverse, EdgeType.ARTIFICIAL_REVERSE);
                reverseEdges.set(edge, reverse);
                reverseEdges.set(reverse, edge);
            }
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

    /**
     * Utility method for augmenting flow along an edge. Flow augmented on 
     * normal edges is added to the flow, flow augmented on reverse edges is 
     * subtracted from the flow.
     * @param first the node-time pair specifying the start of the edge.
     * @param second the node-time pair specifying the end of the edge.
     * @param amount the amount of flow that is to be augmented.
     * @throws AssertionError if augmenting the specified flow along the path
     * violates edge capacities. Requires assertions to be enabled.
     */
    protected void augmentEdge(NodeTimePair first, NodeTimePair second, int amount) {
        Edge edge = findEdge(first.getNode(), second.getNode(), second.getStart() - first.getEnd());
        assert amount >= 0 : "Edge augmentations are assumed to be non-negative.";        
        assert amount <= capacity(edge, first.getEnd()) : "Edge augmentations are assumed to respect capacities.";
        switch (edgeTypes.get(edge)) {
            case NORMAL:
                flow.get(edge).increase(first.getEnd(), amount);
                if (capacity(edge, first.getEnd()) < 0 || capacity(reverseEdge(edge),second.getStart()) < 0) {
                    System.out.println("Boo!");
                }
                return; 
            case REVERSE:
                flow.get(reverseEdge(edge)).decrease(first.getEnd(), amount);
                return; 
            case ARTIFICIAL:
                superSourceFlow.increase(edge, amount);
                return; 
            case ARTIFICIAL_REVERSE:
                superSourceFlow.decrease(reverseEdge(edge), amount);
                return; 
        }
    }

    /**
     * Utility method for augmenting waiting flow in a node. Flow waiting for a
     * positive amount is added to waiting flow, flow waiting for a negative
     * amount of time is subtracted from waiting flow.
     * @param node the node at which waiting occurs.
     * @param start the time at which waiting starts.
     * @param end the time at which waiting ends (which can be smaller than
     * start).
     * @param amount the amount of flow that is to be augmented.
     */
    protected void augmentNode(Node node, int start, int end, int amount) {
        if (start < end) {
            waiting.get(node).increase(start, end, amount);
        } else if (start > end) {
            waiting.get(node).decrease(end, start, amount);
        }
    }

    /**
     * Augments the specified path to the flow represented by this network. 
     * @param path the path to be augmented.
     * @throws AssertionError if augmenting the specified flow along the path
     * violates edge capacities. Requires assertions to be enabled.
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
        switch (edgeTypes.get(edge)) {
            case NORMAL:
                if (problem.getEdgeCapacities().get(edge) - flow.get(edge).get(time) < 0) {
                    System.out.println("Edge " + edge + " at " + time + ", Cap = " + problem.getEdgeCapacities().get(edge) + " " + flow.get(edge).get(time));
                }
                return problem.getEdgeCapacities().get(edge) - flow.get(edge).get(time);
            case REVERSE:
                if (flow.get(reverseEdge(edge)).get(time) < 0) {
                    System.out.println("Edge " + edge + " at " + time + ", Flow = " + flow.get(reverseEdge(edge)).get(time));
                }
                return flow.get(reverseEdge(edge)).get(time);
            case ARTIFICIAL:                
                if (time > 0) {
                    return 0;
                } else {
                    if (problem.getSupplies().get(edge.end()) - superSourceFlow.get(edge) < 0) {
                        System.out.println("Supply");
                    }
                    return problem.getSupplies().get(edge.end()) - superSourceFlow.get(edge);
                }
            case ARTIFICIAL_REVERSE:
                System.out.println("Z");
                if (time > 0) {
                    return 0;
                } else {
                    return superSourceFlow.get(edge);
                }
            default:
                throw new AssertionError("Should not happen.");
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
        return edgeTypes.get(edge) == EdgeType.REVERSE || edgeTypes.get(edge) == EdgeType.ARTIFICIAL_REVERSE;
    }

    /**
     * Returns the reverse edge of the specified edge. Note that the reverse 
     * edge of an reverse edge is again the normal edge.
     * @param edge the edge for which the reverse edge is to be returned. 
     * @return the reverse edge of the specified edge.
     */
    public Edge reverseEdge(Edge edge) {
        return reverseEdges.get(edge);
    }    
    
    /**
     * Returns the super source of this network. If the underlying problem has
     * only one source, this source is returned. Otherwise, the super source 
     * introduced during creation of this network is returned.
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
        switch (edgeTypes.get(edge)) {
            case NORMAL:
                return problem.getTransitTimes().get(edge);
            case REVERSE:
                return -problem.getTransitTimes().get(reverseEdge(edge));
            default:
                return 0;
        }
    }
}
