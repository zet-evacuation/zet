/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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

import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import org.zetool.netflow.dynamic.LongestShortestPathTimeHorizonEstimator;
import org.zetool.netflow.dynamic.earliestarrival.SEAAPAlgorithm;
import org.zetool.algorithm.shortestpath.Dijkstra;
import org.zetool.netflow.classic.maxflow.PushRelabelHighestLabelGlobalGapRelabelling;
import org.zetool.common.algorithm.AbstractAlgorithm;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import org.zetool.graph.Edge;
import org.zetool.graph.structure.Forest;
import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.graph.Node;
import org.zetool.graph.structure.Path;
import org.zetool.netflow.ds.flow.FlowOverTimeImplicit;
import org.zetool.netflow.ds.structure.FlowOverTimePath;
import org.zetool.netflow.ds.flow.MaximumFlow;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import org.zetool.netflow.classic.problems.MaximumFlowProblem;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.container.mapping.IdentifiableObjectMapping;
import org.zetool.graph.DirectedGraph;
import org.zetool.netflow.dynamic.TimeHorizonBounds;

/**
 *
 * @author Martin Groß
 */
public class ShortestPathGraphEarliestArrivalTransshipmentExitAssignment
        extends AbstractAlgorithm<NetworkFlowModel, ExitAssignment> implements Assignable {

    @Override
    protected ExitAssignment runAlgorithm(NetworkFlowModel model) {
        ExitAssignment solution = new ExitAssignment(model.graph().nodes());

        DirectedGraph network = model.graph();
        IdentifiableCollection<Node> sinks = network.predecessorNodes(model.getSupersink());

        Dijkstra dijkstra = new Dijkstra(network, model.transitTimes(), null);
        IdentifiableObjectMapping<Edge, Boolean> shortestEdges = new IdentifiableObjectMapping<>(model.graph().edges());
        for (Node source : model.getSources()) {
            dijkstra.setSource(source);
            dijkstra.run();
            Forest shortestPathTree = dijkstra.getShortestPathTree();
            for (Node sink : sinks) {
                Path path = shortestPathTree.getPathToRoot(sink);
                for (Edge edge : path) {
                    shortestEdges.set(network.getEdge(edge.start(), edge.end()), true);
                }
            }
        }
        for (Edge edge : network.incomingEdges(model.getSupersink())) {
            shortestEdges.set(edge, true);
        }
        IdentifiableIntegerMapping<Edge> reducedCapacities = new IdentifiableIntegerMapping<Edge>(model.edgeCapacities());
        for (Edge edge : network.edges()) {
            if (!shortestEdges.isDefinedFor(edge)) {
                reducedCapacities.set(edge, 0);
            }
        }

        EarliestArrivalFlowProblem problem;// = new EarliestArrivalFlowProblem(reducedCapacities, model.graph(), model.getNodeCapacities(), model.getSupersink(), model.getSources(), 0, model.getTransitTimes(), model.getCurrentAssignment());
        problem = model.getEAFP();
        AbstractAlgorithm<EarliestArrivalFlowProblem, TimeHorizonBounds> estimator = new LongestShortestPathTimeHorizonEstimator();
        estimator.setProblem(problem);
        estimator.run();

        //problem = new EarliestArrivalFlowProblem(reducedCapacities, model.getNetwork(), model.getNodeCapacities(), model.getSupersink(), model.getSources(), estimator.getSolution().getUpperBound(), model.getTransitTimes(), model.getCurrentAssignment());
        problem = model.getEAFP(estimator.getSolution().getUpperBound());
        AbstractAlgorithm<EarliestArrivalFlowProblem, FlowOverTimeImplicit> algorithm = new SEAAPAlgorithm();
        algorithm.setProblem(problem);
        algorithm.run();
        PathBasedFlowOverTime paths = algorithm.getSolution().getPathBased();
        for (FlowOverTimePath path : paths) {
            Node start = path.firstEdge().start();
            Node exit = path.lastEdge().start();
            for (int i = 0; i < path.getRate(); i++) {
                solution.assignIndividualToExit(start, exit);
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

    protected long estimateCapacityByMaximumFlow(NetworkFlowModel model, Node sink) {
        IdentifiableCollection<Node> sinks = model.graph().predecessorNodes(model.getSupersink());
        IdentifiableIntegerMapping<Edge> newCapacities = new IdentifiableIntegerMapping<>(model.edgeCapacities());
        for (Node s : sinks) {
            for (Edge edge : model.graph().outgoingEdges(s)) {
                newCapacities.set(edge, 0);
            }
        }
        MaximumFlowProblem problem = new MaximumFlowProblem(model.graph(), newCapacities, model.getSources(), sink);
        AbstractAlgorithm<MaximumFlowProblem, MaximumFlow> algorithm = new PushRelabelHighestLabelGlobalGapRelabelling();
        algorithm.setProblem(problem);
        algorithm.run();
        return algorithm.getSolution().getFlowValue();
    }

    /**
     * Returns the calculated exit assignment.
     *
     * @return the calculated exit assignment.
     */
    @Override
    public ExitAssignment getExitAssignment() {
        return getSolution();
    }
}
