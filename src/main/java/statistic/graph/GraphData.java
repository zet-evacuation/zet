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
package statistic.graph;

import java.util.HashMap;
import java.util.Map;

import de.zet_evakuierung.network.model.NetworkFlowModel;
import org.zetool.algorithm.shortestpath.Dijkstra;
import org.zetool.algorithm.shortestpath.IntegralSingleSourceShortestPathProblem;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.container.mapping.IntegerDoubleMapping;
import org.zetool.graph.DirectedGraph;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import org.zetool.netflow.classic.PathComposition;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import org.zetool.netflow.ds.structure.FlowOverTimeEdge;
import org.zetool.netflow.ds.structure.FlowOverTimePath;

/**
 *
 * @author Martin Groß
 */
public class GraphData {

    private int[][] distances;
    private IdentifiableIntegerMapping<Edge> edgeCapacities;
    private Map<Edge, IntegerDoubleMapping> edgeFlows;
    private DirectedGraph network;
    private Iterable<Node> sinks;
    private IdentifiableIntegerMapping<Node> nodeCapacities;
    private PathBasedFlowOverTime pathFlows;
    private IdentifiableIntegerMapping<Node> supplies;
    private int timeHorizon;
    private IdentifiableIntegerMapping<Edge> transitTimes;

    @Deprecated
    public GraphData(NetworkFlowModel networkFlowModel, PathBasedFlowOverTime pathFlows) {
        this(networkFlowModel.graph(), networkFlowModel.edgeCapacities(),
                networkFlowModel.nodeCapacities(), networkFlowModel.transitTimes(),
                networkFlowModel.currentAssignment(),
                networkFlowModel.graph().predecessorNodes(networkFlowModel.getSupersink()),
                pathFlows);
    }

    public GraphData(DirectedGraph network, IdentifiableIntegerMapping<Edge> edgeCapacities,
            IdentifiableIntegerMapping<Node> nodeCapacities,
            IdentifiableIntegerMapping<Edge> transitTimes,
            IdentifiableIntegerMapping<Node> supplies,
            Iterable<Node> sinks,
            PathBasedFlowOverTime pathFlows) {
        this.network = network;
        this.edgeCapacities = edgeCapacities;
        this.nodeCapacities = nodeCapacities;
        this.supplies = supplies;
        this.transitTimes = transitTimes;
        this.sinks = sinks;
        this.pathFlows = pathFlows;
        calculateDistances();
        calculateEdgeFlows();
        calculateTimeHorizon();
    }

    protected void calculateDistances() {
        distances = new int[network.nodeCount()][network.edgeCount()];
        for (Node from : network.nodes()) {
            Dijkstra dijkstra = new Dijkstra();
            dijkstra.setProblem(new IntegralSingleSourceShortestPathProblem(network, transitTimes, from));
            dijkstra.run();
            for (Node to : network.nodes()) {
                distances[from.id()][to.id()] = dijkstra.getSolution().getDistance(to);
            }
        }
    }

    protected void calculateEdgeFlows() {
        PathComposition fc = new PathComposition(network, transitTimes, pathFlows);
        fc.run();
        this.edgeFlows = new HashMap(network.edgeCount());
        for (Edge edge : network.edges()) {
            edgeFlows.put(edge, new IntegerDoubleMapping(fc.getEdgeFlows().get(edge)));
        }
    }

    protected void calculateTimeHorizon() {
        double max = Integer.MIN_VALUE;
        for (FlowOverTimePath flow : pathFlows) {
            double time = 0;
            for (FlowOverTimeEdge edge : flow) {
                time += edge.getDelay();
                time += transitTimes.get(edge.getEdge());
            }
            if (time > max) {
                max = time;
            }
        }
        timeHorizon = (int) max;
    }

    public int getCapacity(Edge edge) {
        return edgeCapacities.get(edge);
    }

    public int getCapacity(Node node) {
        return nodeCapacities.get(node);
    }

    public int getDistance(Node from, Node to) {
        return distances[from.id()][to.id()];
    }

    public IntegerDoubleMapping getEdgeFlow(Edge edge) {
        return edgeFlows.get(edge);
    }

    public DirectedGraph getNetwork() {
        return network;
    }

    public Iterable<FlowOverTimePath> getPathFlows() {
        return pathFlows;
    }

    public Iterable<Node> getSinks() {
        return sinks;
    }

    public int getSupply(Node node) {
        return supplies.get(node);
    }

    public int getTimeHorizon() {
        return timeHorizon;
    }

    public int getTransitTime(Edge edge) {
        return transitTimes.get(edge);
    }
}
