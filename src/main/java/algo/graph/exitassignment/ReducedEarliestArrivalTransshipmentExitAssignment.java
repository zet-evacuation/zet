/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package algo.graph.exitassignment;

import java.util.LinkedList;
import java.util.List;

import de.zet_evakuierung.network.model.NetworkFlowModel;
import org.zetool.algorithm.shortestpath.Dijkstra;
import org.zetool.algorithm.shortestpath.IntegralShortestPathSolution;
import org.zetool.algorithm.shortestpath.IntegralSingleSourceShortestPathProblem;
import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.common.algorithm.Algorithm;
import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.DefaultDirectedGraph;
import org.zetool.graph.DirectedGraph;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import org.zetool.netflow.classic.maxflow.PushRelabelHighestLabelGlobalGapRelabelling;
import org.zetool.netflow.classic.problems.MaximumFlowProblem;
import org.zetool.netflow.ds.flow.FlowOverTimeImplicit;
import org.zetool.netflow.ds.flow.MaximumFlow;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import org.zetool.netflow.ds.structure.FlowOverTimePath;
import org.zetool.netflow.dynamic.LongestShortestPathTimeHorizonEstimator;
import org.zetool.netflow.dynamic.TimeHorizonBounds;
import org.zetool.netflow.dynamic.earliestarrival.SEAAPAlgorithm;
import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;

/**
 *
 * @author Martin Groß
 */
public class ReducedEarliestArrivalTransshipmentExitAssignment extends AbstractAlgorithm<NetworkFlowModel, ExitAssignment> implements Assignable {

    @Override
    protected ExitAssignment runAlgorithm(NetworkFlowModel model) {
        ExitAssignment solution = new ExitAssignment(model.graph().nodes());

        DirectedGraph network = model.graph();
        IdentifiableCollection<Node> sinks = network.predecessorNodes(model.getSupersink());

        Dijkstra dijkstra = new Dijkstra(true);
        int[][] distances = new int[network.nodeCount()][network.nodeCount()];
        int[][] caps = new int[network.nodeCount()][network.nodeCount()];
        for (Node sink : sinks) {
            dijkstra.setProblem(new IntegralSingleSourceShortestPathProblem(network, model.transitTimes(), sink));
            dijkstra.run();
            IntegralShortestPathSolution shortestPathSolution = dijkstra.getSolution();
            for (Node source : model.getSources()) {
                distances[source.id()][sink.id()] = shortestPathSolution.getDistance(source);
                caps[source.id()][sink.id()] = model.edgeCapacities().minimum(shortestPathSolution.getForest().getPathToRoot(source));
            }
        }

        DefaultDirectedGraph reducedNetwork = new DefaultDirectedGraph(sinks.size() + model.getSources().size() + 1, sinks.size() * model.getSources().size() + sinks.size());
        IdentifiableIntegerMapping<Edge> reducedTransitTimes = new IdentifiableIntegerMapping<>(sinks.size() * model.getSources().size());
        IdentifiableIntegerMapping<Edge> reducedCapacities = new IdentifiableIntegerMapping<>(sinks.size() * model.getSources().size());
        IdentifiableIntegerMapping<Node> reducedNodeCapacities = new IdentifiableIntegerMapping<>(sinks.size() + model.getSources().size() + 1);
        IdentifiableIntegerMapping<Node> reducedBalances = new IdentifiableIntegerMapping<>(sinks.size() + model.getSources().size());
        List<Node> reducedSources = new LinkedList<>();
        List<Node> reducedSinks = new LinkedList<>();
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
            reducedBalances.set(reducedNetwork.getNode(index), model.currentAssignment().get(source));
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
            totalSupplies += model.currentAssignment().get(source);
        }
        for (Node node : reducedNetwork.nodes()) {
            reducedNodeCapacities.set(node, Integer.MAX_VALUE);
        }
        reducedBalances.set(supersink, -totalSupplies);

        EarliestArrivalFlowProblem problem = new EarliestArrivalFlowProblem(reducedCapacities, reducedNetwork, reducedNodeCapacities, supersink, reducedSources, 0, reducedTransitTimes, reducedBalances);
        AbstractAlgorithm<EarliestArrivalFlowProblem, TimeHorizonBounds> estimator = new LongestShortestPathTimeHorizonEstimator();
        estimator.setProblem(problem);
        estimator.run();

        problem = new EarliestArrivalFlowProblem(reducedCapacities, reducedNetwork, reducedNodeCapacities, supersink, reducedSources, estimator.getSolution().getUpperBound(), reducedTransitTimes, reducedBalances);
        AbstractAlgorithm<EarliestArrivalFlowProblem, FlowOverTimeImplicit> algorithm = new SEAAPAlgorithm();
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

    protected long  estimateCapacityByMaximumFlow(NetworkFlowModel model, Node sink) {
        IdentifiableCollection<Node> sinks = model.graph().predecessorNodes(model.getSupersink());
        IdentifiableIntegerMapping<Edge> newCapacities = new IdentifiableIntegerMapping<Edge>(model.edgeCapacities());
        for (Node s : sinks) {
            for (Edge edge : model.graph().outgoingEdges(s)) {
                //if (sinks.contains(edge.start())) {
                newCapacities.set(edge, 0);
            //}
            }
        }
        MaximumFlowProblem problem = new MaximumFlowProblem(model.graph(), newCapacities, model.getSources(), sink);
        Algorithm<MaximumFlowProblem, MaximumFlow> algorithm = new PushRelabelHighestLabelGlobalGapRelabelling();
        algorithm.setProblem(problem);
        algorithm.run();
        return algorithm.getSolution().getFlowValue();
    }

    /**
     * Returns the calculated exit assignment.
     * @return the calculated exit assignment.
     */
    @Override
    public ExitAssignment getExitAssignment() {
        return getSolution();
    }
  
  public static void main( String args[] ) {
    System.out.println( "Ford Fulkerson kompilieren bitte" );
  }
}
