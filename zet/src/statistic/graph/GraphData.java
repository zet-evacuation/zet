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
 * Data.java
 *
 */
package statistic.graph;

import algo.graph.shortestpath.Dijkstra;
import algo.graph.util.PathComposition;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.graph.flow.FlowOverTimePath;
import ds.graph.Edge;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import ds.graph.network.AbstractNetwork;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import ds.graph.Node;
import ds.graph.flow.FlowOverTimeEdge;
import java.util.HashMap;
import java.util.Map;

import statistic.common.Data;

/**
 *
 * @author Martin Groß
 */
public class GraphData extends Data{

    private int[][] distances;
    private IdentifiableIntegerMapping<Edge> edgeCapacities;
    private Map<Edge, IntegerDoubleMapping> edgeFlows;
    private AbstractNetwork network;
    private Iterable<Node> sinks;
    private IdentifiableIntegerMapping<Node> nodeCapacities;
    private PathBasedFlowOverTime pathFlows;
    private IdentifiableIntegerMapping<Node> supplies;
    private int timeHorizon;
    private IdentifiableIntegerMapping<Edge> transitTimes;

    @Deprecated
    public GraphData(NetworkFlowModel networkFlowModel, PathBasedFlowOverTime pathFlows) {
        this((AbstractNetwork)networkFlowModel.graph(), networkFlowModel.edgeCapacities(),
                networkFlowModel.nodeCapacities(), networkFlowModel.transitTimes(),
                networkFlowModel.currentAssignment(),
                networkFlowModel.graph().predecessorNodes(networkFlowModel.getSupersink()),
                pathFlows);
    }

    public GraphData(AbstractNetwork network, IdentifiableIntegerMapping<Edge> edgeCapacities,
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
        distances = new int[network.getNodeCapacity()][network.getNodeCapacity()];
        for (Node from : network.nodes()) {
            Dijkstra dijkstra = new Dijkstra(network, transitTimes, from);
            dijkstra.run();
            IdentifiableIntegerMapping<Node> nodeDistances = dijkstra.getDistances();
            for (Node to : network.nodes()) {
                distances[from.id()][to.id()] = nodeDistances.get(to);
            }
        }
    }

    protected void calculateEdgeFlows() {
        PathComposition fc = new PathComposition(network, transitTimes, pathFlows);
        fc.run();
        this.edgeFlows = new HashMap(network.getEdgeCapacity());
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

    public AbstractNetwork getNetwork() {
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
