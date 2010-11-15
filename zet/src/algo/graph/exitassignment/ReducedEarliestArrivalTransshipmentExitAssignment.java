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
 * MinimumCostTransshipmentExitAssignment.java
 *
 */
package algo.graph.exitassignment;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.LongestShortestPathTimeHorizonEstimator;
import algo.graph.dynamicflow.eat.SEAAPAlgorithm;
import algo.graph.dynamicflow.eat.TimeHorizonBounds;
import algo.graph.shortestpath.Dijkstra;
import algo.graph.staticflow.maxflow.PushRelabelHighestLabelGlobalGapRelabelling;
import ds.graph.problem.MaximumFlowProblem;
import ds.graph.Edge;
import ds.graph.Forest;
import ds.graph.IdentifiableCollection;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.graph.Node;
import ds.graph.flow.FlowOverTime;
import ds.graph.flow.FlowOverTimePath;
import ds.graph.flow.MaximumFlow;
import ds.graph.flow.PathBasedFlowOverTime;
import java.util.LinkedList;
import java.util.List;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;

/**
 *
 * @author Martin Groß
 */
public class ReducedEarliestArrivalTransshipmentExitAssignment extends Algorithm<NetworkFlowModel, ExitAssignment> implements Assignable {

    @Override
    protected ExitAssignment runAlgorithm(NetworkFlowModel model) {
        ExitAssignment solution = new ExitAssignment(model.getNetwork().nodes());

        Network network = model.getNetwork();
        IdentifiableCollection<Node> sinks = network.predecessorNodes(model.getSupersink());

        Dijkstra dijkstra = new Dijkstra(network, model.getTransitTimes(), null, true);
        int[][] distances = new int[network.numberOfNodes()][network.numberOfNodes()];
        int[][] caps = new int[network.numberOfNodes()][network.numberOfNodes()];
        for (Node sink : sinks) {
            dijkstra.setSource(sink);
            dijkstra.run();
            Forest shortestPathTree = dijkstra.getShortestPathTree();
            for (Node source : model.getSources()) {
                distances[source.id()][sink.id()] = dijkstra.getDistance(source);
                caps[source.id()][sink.id()] = model.getEdgeCapacities().minimum(shortestPathTree.getPathToRoot(source)); 
            }
        }

        Network reducedNetwork = new Network(sinks.size() + model.getSources().size() + 1, sinks.size() * model.getSources().size() + sinks.size());
        IdentifiableIntegerMapping<Edge> reducedTransitTimes = new IdentifiableIntegerMapping<Edge>(sinks.size() * model.getSources().size());
        IdentifiableIntegerMapping<Edge> reducedCapacities = new IdentifiableIntegerMapping<Edge>(sinks.size() * model.getSources().size());
        IdentifiableIntegerMapping<Node> reducedNodeCapacities = new IdentifiableIntegerMapping<Node>(sinks.size() + model.getSources().size() + 1);
        IdentifiableIntegerMapping<Node> reducedBalances = new IdentifiableIntegerMapping<Node>(sinks.size() + model.getSources().size());
        List<Node> reducedSources = new LinkedList<Node>();
        List<Node> reducedSinks = new LinkedList<Node>();
        Node supersink = reducedNetwork.getNode(sinks.size() + model.getSources().size());
        reducedSinks.add(supersink);
        int index = 0;
        for (Node source : model.getSources()) {
            int sinkIndex = 0;
            for (Node sink : sinks) {
                if (distances[source.id()][sink.id()] == 0) {
                    continue;
                }
                Edge edge = reducedNetwork.createAndSetEdge(reducedNetwork.getNode(index), reducedNetwork.getNode(model.getSources().size() + sinkIndex));
                reducedTransitTimes.set(edge, distances[source.id()][sink.id()]);
                reducedCapacities.set(edge, caps[source.id()][sink.id()]);
                sinkIndex++;
            }
            reducedBalances.set(reducedNetwork.getNode(index), model.getCurrentAssignment().get(source));
            reducedSources.add(reducedNetwork.getNode(index));
            index++;
        }
        int sinkIndex = 0;
        for (Node sink : sinks) {
            Edge edge = reducedNetwork.createAndSetEdge(reducedNetwork.getNode(model.getSources().size() + sinkIndex), supersink);
            reducedTransitTimes.set(edge, 0);
            reducedCapacities.set(edge, Integer.MAX_VALUE);
            sinkIndex++;            
        }
        int totalSupplies = 0;
        for (Node source : model.getSources()) {
            totalSupplies += model.getCurrentAssignment().get(source);
        }
        for (Node node : reducedNetwork.nodes()) {
            reducedNodeCapacities.set(node, Integer.MAX_VALUE);
        }
        reducedBalances.set(supersink, -totalSupplies);        
        
        EarliestArrivalFlowProblem problem = new EarliestArrivalFlowProblem(reducedCapacities, reducedNetwork, reducedNodeCapacities, supersink, reducedSources, 0, reducedTransitTimes, reducedBalances);            
        Algorithm<EarliestArrivalFlowProblem, TimeHorizonBounds> estimator = new LongestShortestPathTimeHorizonEstimator();
        estimator.setProblem(problem);
        estimator.run();
        
        problem = new EarliestArrivalFlowProblem(reducedCapacities, reducedNetwork, reducedNodeCapacities, supersink, reducedSources, estimator.getSolution().getUpperBound(), reducedTransitTimes, reducedBalances);       
        Algorithm<EarliestArrivalFlowProblem, FlowOverTime> algorithm = new SEAAPAlgorithm();
        algorithm.setProblem(problem);
        algorithm.run();
        
        PathBasedFlowOverTime paths = algorithm.getSolution().getPathBased();
        LinkedList<Node> sinks2 = new LinkedList<Node>();
        for (Node sink : sinks) {
            sinks2.add(sink);
        }
        for (FlowOverTimePath path : paths) {
            Edge edge = path.firstEdge();
            Node source = model.getSources().get(edge.start().id());
            Node sink = sinks2.get(edge.end().id() - model.getSources().size());            
            for (int i = 0; i < path.getRate(); i++) {
                solution.assignIndividualToExit(source, sink);
            }
        }        
        return solution;
    }

    protected int estimateCapacityByIncomingEdges(NetworkFlowModel model, Node sink) {
        IdentifiableCollection<Node> sinks = model.getNetwork().predecessorNodes(model.getSupersink());
        int result = 0;
        for (Edge edge : model.getNetwork().incomingEdges(sink)) {
            if (sinks.contains(edge.start())) {
                continue;
            }
            result += model.getEdgeCapacity(edge);
        }
        return result;
    }

    protected int estimateCapacityByMaximumFlow(NetworkFlowModel model, Node sink) {
        IdentifiableCollection<Node> sinks = model.getNetwork().predecessorNodes(model.getSupersink());
        IdentifiableIntegerMapping<Edge> newCapacities = new IdentifiableIntegerMapping<Edge>(model.getEdgeCapacities());
        for (Node s : sinks) {
            for (Edge edge : model.getNetwork().outgoingEdges(s)) {
                //if (sinks.contains(edge.start())) {
                newCapacities.set(edge, 0);
            //}
            }
        }
        MaximumFlowProblem problem = new MaximumFlowProblem(model.getNetwork(), newCapacities, model.getSources(), sink);
        Algorithm<MaximumFlowProblem, MaximumFlow> algorithm = new PushRelabelHighestLabelGlobalGapRelabelling();
        algorithm.setProblem(problem);
        algorithm.run();
        return algorithm.getSolution().getFlowValue();
    }

    /**
     * Returns the calculated exit assignment.
     * @return the calculated exit assignment.
     */
    public ExitAssignment getExitAssignment() {
        return getSolution();
    }
}
