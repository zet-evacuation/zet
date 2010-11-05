/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
 * MaxFlowOverTime.java
 * Created on 22. Januar 2008, 03:39
 */

package algo.graph.dynamicflow.maxflow;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.graph.DynamicPath;
import ds.graph.flow.FlowOverTimePath;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.flow.PathBasedFlow;
import ds.graph.StaticPath;
import ds.graph.flow.StaticPathFlow;
import java.util.LinkedList;
import java.util.List;
import algo.graph.Flags;
import algo.graph.staticflow.mincost.MinimumMeanCycleCancelling;
import algo.graph.util.PathDecomposition;
import de.tu_berlin.math.coga.common.localization.Localization;
import ds.graph.problem.MinimumCostFlowProblem;

/**
 * The class <code>MaxFlowOverTime</code> solves the max flow over time 
 * problem.
 * 
 * @author Gordon Schlechter
 */
public class MaxFlowOverTime extends Algorithm<MaximumFlowOverTimeProblem, PathBasedFlowOverTime> {

    private Network network;
    private IdentifiableIntegerMapping<Edge> edgeCapacities;
    private List<Node> sinks;
    private List<Node> sources;
    private IdentifiableIntegerMapping<Node> zeroSupplies;
    private Node superNode;
    private Node superSink;
    private Node superSource;
    private int timeHorizon;
    private IdentifiableIntegerMapping<Edge> transitTimes;
    private LinkedList<Node> newNodes;
    private LinkedList<Edge> newEdges;
    protected PathBasedFlowOverTime maxFlowOT;

    /** Creates a new instance of MaxFlowOverTime */
    public MaxFlowOverTime() {

        newNodes = new LinkedList<Node>();
        newEdges = new LinkedList<Edge>();
    }

    /** Creates the supplies for all nodes in the network. The value of the 
     * supplies of each node is 0. */
    private void CreateZeroSupply() {
        int supplies = 0;
        zeroSupplies = new IdentifiableIntegerMapping<Node>(supplies);

        for (Node n : network.nodes()) {
            zeroSupplies.set(n, 0);
        }
    }

    /** Creates a new super source. A super source is connected with all
     * sources in the original network. These connecting edges have a transit 
     * time of 0 and a capacity of MAX_VALUE for an integer. */
    private void createSuperSource() {
        int nodeCount = network.getNodeCapacity();
        superSource = new Node(nodeCount);
        nodeCount++;
        network.setNodeCapacity(nodeCount);
        network.setNode(superSource);
        newNodes.add(superSource);


        int edgeCount = network.getEdgeCapacity();
        network.setEdgeCapacity(edgeCount + sources.size());

        for (Node source : sources) {
            Edge newEdge = new Edge(edgeCount, superSource, source);
            edgeCapacities.set(newEdge, Integer.MAX_VALUE);
            transitTimes.set(newEdge, 0);
            network.setEdge(newEdge);
            newEdges.add(newEdge);
            edgeCount++;
        }

    }

    /** Creates a new super sink. A super sink is connected with all
     * sinks in the original network. These connecting edges have a transit 
     * time of 0 and a capacity of MAX_VALUE for an integer. */
    private void createSuperSink() {
        int nodeCount = network.getNodeCapacity();
        superSink = new Node(nodeCount);
        nodeCount++;
        network.setNodeCapacity(nodeCount);
        network.setNode(superSink);
        newNodes.add(superSink);

        int edgeCount = network.getEdgeCapacity();
        network.setEdgeCapacity(edgeCount + sinks.size());

        for (Node sink : sinks) {
            Edge newEdge = new Edge(edgeCount, sink, superSink);
            edgeCapacities.set(newEdge, Integer.MAX_VALUE);
            transitTimes.set(newEdge, 0);
            network.setEdge(newEdge);
            newEdges.add(newEdge);
            edgeCount++;

        }


    }

    /** Creates an edge between the super sink and the super source.
     * This edge has a capacity of MAX_VALUE for an integer. The transit 
     * time of this edge is -( the given time horizin + 1). 
     */
    private void createEdgeBetween() {

        int edgeCount = network.getEdgeCapacity();
        network.setEdgeCapacity(edgeCount + 1);

        Edge edgeBetween = new Edge(edgeCount, superSink, superSource);

        edgeCapacities.set(edgeBetween, Integer.MAX_VALUE);
        transitTimes.set(edgeBetween, -(timeHorizon + 1));

        network.setEdge(edgeBetween);

        newEdges.add(edgeBetween);
    }

    /** In the first step, a super source is created, if there is more 
     * than one source. After that, a super sink is created, if it 
     * is necessary. At the end the edge between these two nodes is created.
     */
    private void createSuperNodes() {

        if (sources.size() > 1) {
            createSuperSource();
        } else {
            superSource = sources.get(0);
        }

        if (sinks.size() > 1) {
            createSuperSink();
        } else {
            superSink = sinks.get(0);
        }

        createEdgeBetween();
    }

    /** andere Reduktion, noch nicht fertig implementiert und getestet.
     * Methode zum Entfernen fehlt noch...
     */
    public void reduction() {
        int nodeCount = network.getNodeCapacity();
        superNode = new Node(nodeCount);
        nodeCount++;
        network.setNodeCapacity(nodeCount);
        network.setNode(superNode);

        int edgeCount1 = network.getEdgeCapacity();
        network.setEdgeCapacity(edgeCount1 + sources.size());

        for (Node source : sources) {
            Edge newEdge = new Edge(edgeCount1, superNode, source);
            edgeCapacities.set(newEdge, Integer.MAX_VALUE);
            transitTimes.set(newEdge, 0);
            network.setEdge(newEdge);
            newEdges.add(newEdge);
            edgeCount1++;
        }

        int edgeCount2 = network.getEdgeCapacity();
        network.setEdgeCapacity(edgeCount2 + sinks.size());

        for (Node sink : sinks) {
            Edge newEdge = new Edge(edgeCount2, sink, superNode);
            edgeCapacities.set(newEdge, Integer.MAX_VALUE);
            transitTimes.set(newEdge, -(timeHorizon + 1));
            network.setEdge(newEdge);
            newEdges.add(newEdge);
            edgeCount2++;
        }
    }

    /** Hides the added super node and edges in the network. After this you got 
     * back the original network.
     */
    private void reconstruction() {
        int i = network.getNodeCapacity();
        network.setNodeCapacity(i - 1);

        int g = network.getEdgeCapacity();
        network.setEdgeCapacity(g - newEdges.size());
    }

    /** Hides the added nodes and edges in the network. After this you got 
     * back the original network.
     */
    private void hideAddedInNetwork() {
        /**for (Edge e : newEdges){
        network.setHidden(e, true);
        }
        for (Node n : newNodes) {
        network.setHidden(n, true);
        }*/
        int i = network.getNodeCapacity();
        network.setNodeCapacity(i - newNodes.size());

        int g = network.getEdgeCapacity();
        network.setEdgeCapacity(g - newEdges.size());
    }

    /** Hides the added egdes in the flow. After this the flow contains
     * only edges from the original network.
     */
    private void hideAddedInFlow(IdentifiableIntegerMapping<Edge> flow) {

        int i = flow.getDomainSize();
        flow.setDomainSize(i - newEdges.size());

    }

    /** Hides the added nodes und edges in the network and also hides
     * the added edges in the flow.
     */
    private void hideAdded(IdentifiableIntegerMapping<Edge> flow) {
        hideAddedInNetwork();
        hideAddedInFlow(flow);
    }

    /** Creates dynamic Flow out of the given static flow. At first static 
     * flow is divided in the different pathes and then it is added to the 
     * dynamic flow,  if the conditions are met. */
    private PathBasedFlowOverTime translateIntoMaxFlow(PathBasedFlow minCostFlow) {
        PathBasedFlowOverTime mFlow = new PathBasedFlowOverTime();

        for (StaticPathFlow staticPathFlow : minCostFlow) {
            if (staticPathFlow.getAmount() == 0) {
                continue;
            }
            StaticPath staticPath = staticPathFlow.getPath();
            int path_transit_time = 0;
            for (Edge e : staticPath) {
                path_transit_time += transitTimes.get(e);
            }

            // Add this path only in case that our given time is long 
            // enough to send anything at all over this path
            if (timeHorizon > path_transit_time) {
                DynamicPath dynamicPath = new DynamicPath(staticPath);
                FlowOverTimePath dynamicPathFlow = new FlowOverTimePath(dynamicPath,
                        staticPathFlow.getAmount(), (timeHorizon - path_transit_time) *
                        staticPathFlow.getAmount());
                mFlow.addPathFlow(dynamicPathFlow);
            }
        }

        return mFlow;
    }

    public PathBasedFlowOverTime getDynamicFlow() {
        return maxFlowOT;
    }

    public static void main(String args[]) {
/*
        Network network = new Network(9, 10);
        Node source1 = network.getNode(0);
        Node source2 = network.getNode(1);
        Node source3 = network.getNode(4);
        Node sink1 = network.getNode(5);
        Node sink2 = network.getNode(6);
        Node sink3 = network.getNode(8);
        Node a = network.getNode(2);
        Node b = network.getNode(3);
        Node c = network.getNode(7);
        Edge e = new Edge(0, source1, a);
        Edge f = new Edge(1, source1, b);
        Edge g = new Edge(2, source2, a);
        Edge h = new Edge(3, source2, source3);
        Edge i = new Edge(4, a, b);
        Edge j = new Edge(5, a, source3);
        Edge k = new Edge(6, b, sink1);
        Edge l = new Edge(7, b, sink2);
        Edge m = new Edge(8, source3, c);
        Edge n = new Edge(9, sink2, sink3);
        network.setEdge(e);
        network.setEdge(f);
        network.setEdge(g);
        network.setEdge(h);
        network.setEdge(i);
        network.setEdge(j);
        network.setEdge(k);
        network.setEdge(l);
        network.setEdge(m);
        network.setEdge(n);
        IdentifiableIntegerMapping<Edge> capacities = new IdentifiableIntegerMapping<Edge>(network.numberOfEdges());
        capacities.set(e, 1);
        capacities.set(f, 1);
        capacities.set(g, 2);
        capacities.set(h, 2);
        capacities.set(i, 3);
        capacities.set(j, 3);
        capacities.set(k, 1);
        capacities.set(l, 2);
        capacities.set(m, 1);
        capacities.set(n, 1);
        IdentifiableIntegerMapping<Edge> transitTimes = new IdentifiableIntegerMapping<Edge>(network.numberOfEdges());
        transitTimes.set(e, 1);
        transitTimes.set(f, 1);
        transitTimes.set(g, 2);
        transitTimes.set(h, 2);
        transitTimes.set(i, 3);
        transitTimes.set(j, 3);
        transitTimes.set(k, 1);
        transitTimes.set(l, 2);
        transitTimes.set(m, 1);
        transitTimes.set(n, 1);
        List<Node> sources = new LinkedList<Node>();
        sources.add(source1);
        sources.add(source2);
        sources.add(source3);
        List<Node> sinks = new LinkedList<Node>();
        sinks.add(sink1);
        sinks.add(sink2);
        sinks.add(sink3);

        MaxFlowOverTime algo = new MaxFlowOverTime(network, capacities, sinks, sources, 25, transitTimes);
        algo.run();*/
    }

    @Override
    protected PathBasedFlowOverTime runAlgorithm(MaximumFlowOverTimeProblem problem) {


        if ((sources == null) || (sinks == null)) {
            throw new IllegalArgumentException(Localization.getInstance().getString(
                    "algo.graph.MaxFlowOverTime.SpecifySourceSinkFirst"));
        }

        if ((sources.size() == 0) || (sinks.size() == 0)) {
            maxFlowOT = new PathBasedFlowOverTime();
             return maxFlowOT;
        }

        reduction();
        CreateZeroSupply();

        if (Flags.GORDON) {
            System.out.print("Network: ");
            System.out.println(network);
            System.out.print("transit times");
            System.out.println(transitTimes);
            System.out.print("edge capicties: ");
            System.out.println(edgeCapacities);
            System.out.print("supplies: ");
            System.out.println(zeroSupplies);
        }


        IdentifiableIntegerMapping<Edge> flow = null;

        MinimumCostFlowProblem p = new MinimumCostFlowProblem(network, edgeCapacities, transitTimes, zeroSupplies);
        Algorithm<MinimumCostFlowProblem, IdentifiableIntegerMapping<Edge>> algorithm = new MinimumMeanCycleCancelling();
        algorithm.setProblem(p);
        algorithm.run();
        flow = algorithm.getSolution();

        //SuccessiveShortestPath algo = new SuccessiveShortestPath(network, zeroSupplies, edgeCapacities, transitTimes);
        //algo.run();
        //flow = algo.getFlow();

        if (Flags.GORDON) {
            System.out.print("flow 1: ");
            System.out.println(flow);
        }

        reconstruction();
        hideAddedInFlow(flow);

        if (Flags.GORDON) {
            System.out.print("flow 2: ");
            System.out.println(flow);
        }

        PathBasedFlow minCostFlow = PathDecomposition.calculatePathDecomposition(network, sources, sinks, flow);

        if (Flags.GORDON) {
            System.out.print("min cost flow: ");
            System.out.println(flow);
        }

        maxFlowOT = translateIntoMaxFlow(minCostFlow);

        System.out.print("Max flow over time:");
        System.out.println(maxFlowOT);
        return maxFlowOT;
    }
}
