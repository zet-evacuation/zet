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

import de.tu_berlin.math.coga.algorithm.shortestpath.Dijkstra;
import algo.graph.util.PathDecomposition;
import de.tu_berlin.math.coga.algorithm.networkflow.maximumflow.PushRelabelHighestLabelGlobalGapRelabelling;
import de.tu_berlin.math.coga.algorithm.networkflow.mincostflow.SuccessiveShortestPath;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import ds.graph.Edge;
import de.tu_berlin.coga.container.collection.IdentifiableCollection;
import ds.graph.Node;
import ds.graph.flow.MaximumFlow;
import ds.graph.flow.PathBasedFlow;
import ds.graph.flow.StaticPathFlow;
import ds.graph.network.AbstractNetwork;
import ds.graph.network.Network;
import ds.graph.problem.MaximumFlowProblem;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Groß
 */
public class MinimumCostTransshipmentExitAssignment extends Algorithm<NetworkFlowModel, ExitAssignment> implements Assignable {

    @Override
    protected ExitAssignment runAlgorithm(NetworkFlowModel model) {
        ExitAssignment solution = new ExitAssignment(model.graph().nodes());

        AbstractNetwork network =(AbstractNetwork) model.graph();
        IdentifiableCollection<Node> sinks = network.predecessorNodes(model.getSupersink());

        Dijkstra dijkstra = new Dijkstra(network, model.transitTimes(), null, true);
        int[][] distances = new int[network.numberOfNodes()][network.numberOfNodes()];
        for (Node sink : sinks) {
            dijkstra.setSource(sink);
            dijkstra.run();
            for (Node source : model.getSources()) {
                distances[source.id()][sink.id()] = dijkstra.getDistance(source);
            }
        }

        AbstractNetwork reducedNetwork = new Network(sinks.size() + model.getSources().size(), sinks.size() * model.getSources().size());
        IdentifiableIntegerMapping<Edge> reducedTransitTimes = new IdentifiableIntegerMapping<>(sinks.size() * model.getSources().size());
        IdentifiableIntegerMapping<Edge> reducedCapacities = new IdentifiableIntegerMapping<>(sinks.size() * model.getSources().size());
        IdentifiableIntegerMapping<Node> reducedBalances = new IdentifiableIntegerMapping<>(sinks.size() + model.getSources().size());
        List<Node> reducedSources = new LinkedList<>();
        List<Node> reducedSinks = new LinkedList<>();
        int index = 0;
        for (Node source : model.getSources()) {
            int sinkIndex = 0;
            for (Node sink : sinks) {
                if (distances[source.id()][sink.id()] == 0) {
                    continue;
                }
                Edge edge = reducedNetwork.createAndSetEdge(reducedNetwork.getNode(index), reducedNetwork.getNode(model.getSources().size() + sinkIndex));
                reducedTransitTimes.set(edge, distances[source.id()][sink.id()]);
                reducedCapacities.set(edge, Integer.MAX_VALUE);
                sinkIndex++;
            }
            reducedBalances.set(reducedNetwork.getNode(index), model.currentAssignment().get(source));
            reducedSources.add(reducedNetwork.getNode(index));
            index++;
        }
        int totalCapacities = 0;
        IdentifiableIntegerMapping<Node> estimatedCapacities = new IdentifiableIntegerMapping<>(model.graph().nodes());
        for (Node sink : sinks) {
            estimatedCapacities.set(sink, estimateCapacityByMaximumFlow(model, sink));
            totalCapacities += estimatedCapacities.get(sink);
        }
        int totalSupplies = 0;
        for (Node source : model.getSources()) {
            totalSupplies += model.currentAssignment().get(source);
        }
        int sinkIndex = 0;
        for (Node sink : sinks) {
            reducedBalances.set(reducedNetwork.getNode(index + sinkIndex), (int) -Math.ceil(estimatedCapacities.get(sink) * 1.0 / totalCapacities * totalSupplies));
            reducedSinks.add(reducedNetwork.getNode(index + sinkIndex));
            sinkIndex++;
        }
        SuccessiveShortestPath ssp = new SuccessiveShortestPath(reducedNetwork, reducedBalances, reducedCapacities, reducedTransitTimes, true);
        ssp.run();

        PathBasedFlow pathDecomposition = PathDecomposition.calculatePathDecomposition(reducedNetwork, reducedBalances, reducedSources, reducedSinks, ssp.getFlow());
        LinkedList<Node> sinks2 = new LinkedList<Node>();
        for (Node sink : sinks) {
            sinks2.add(sink);
        }
        for (StaticPathFlow path : pathDecomposition) {
            Edge edge = path.firstEdge();
            Node source = model.getSources().get(edge.start().id());
            Node sink = sinks2.get(edge.end().id() - model.getSources().size());
            for (int i = 0; i < path.getAmount(); i++) {
                solution.assignIndividualToExit(source, sink);
            }
        }
        return solution;
    }

    protected int estimateCapacityByIncomingEdges(NetworkFlowModel model, Node sink) {
        IdentifiableCollection<Node> sinks = model.graph().predecessorNodes(model.getSupersink());
        int result = 0;
        for (Edge edge : model.graph().incomingEdges(sink)) {
            if (sinks.contains(edge.start())) {
                continue;
            }
            result += model.getEdgeCapacity(edge);
        }
        return result;
    }

    protected int estimateCapacityByMaximumFlow(NetworkFlowModel model, Node sink) {
        IdentifiableCollection<Node> sinks = model.graph().predecessorNodes(model.getSupersink());
        IdentifiableIntegerMapping<Edge> newCapacities = new IdentifiableIntegerMapping<>(model.edgeCapacities());
        for (Node s : sinks) {
            for (Edge edge : model.graph().outgoingEdges(s)) {
                //if (sinks.contains(edge.start())) {
                newCapacities.set(edge, 0);
            //}
            }
        }
        MaximumFlowProblem problem = new MaximumFlowProblem((AbstractNetwork)model.graph(), newCapacities, model.getSources(), sink);
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
