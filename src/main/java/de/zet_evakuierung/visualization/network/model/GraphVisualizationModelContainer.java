/*
 * zet evacuation tool copyright © 2007-21 zet evacuation team
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
package de.zet_evakuierung.visualization.network.model;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.StreamSupport;

import ds.GraphVisualizationResults;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;

/**
 * Container class for all model elements of a graph visualization includinga dynamic flow (flow over time).
 *
 * @author Jan-Philipp Kappmeier
 */
public class GraphVisualizationModelContainer {

    private final static String ILLEGAL_NULL_OBJECT_ERROR = "Graph model container builder returned null.";
    private final GLFlowGraphModel network;
    private final List<GLGraphFloorControl> floors;
    private final Map<Node, GLNodeModel> nodeModelMap;
    private final Map<Edge, GLFlowEdgeModel> edgeModelMap;

    private GraphVisualizationModelContainer(Builder builder) {
        this.network = Objects.requireNonNull(builder.network, ILLEGAL_NULL_OBJECT_ERROR);
        this.floors = Objects.requireNonNull(builder.floors, ILLEGAL_NULL_OBJECT_ERROR);
        this.nodeModelMap = Objects.requireNonNull(builder.nodeMap, ILLEGAL_NULL_OBJECT_ERROR);
        this.edgeModelMap = Objects.requireNonNull(builder.edgeMap, ILLEGAL_NULL_OBJECT_ERROR);
    }

    /**
     * Returns the root model instance for the graph visualization hierarchy.
     *
     * @return the graph root instance
     */
    public GLFlowGraphModel getGraphModel() {
        return network;
    }

    /**
     * Returns floor model instances for the graph visualization hierarchy. The {@code floor} is in the range {@code 0}
     * to the {@link #getFloorCount() floor count} exclusive.
     *
     * @see #floors() to iterate all floor model instances
     * @param floor the floor number for which the model is returned
     * @return the floor visualization model instance
     */
    public GLGraphFloorControl getFloorModel(int floor) {
        return floors.get(floor);
    }

    /**
     * Returns the number of floors.
     *
     * @see #getFloorModel(int) to retrieve individual floor model instances
     * @return the number of floors, at least {@code 0}
     */
    public int getFloorCount() {
        return floors.size();
    }

    /**
     * Returns node model instances for graph visualization hierarchy. The {@code node} specifies the corresponding
     * instance in network.
     *
     * @see #nodes() to iterate all node model instances
     * @param node the instane for which the corresponding model should be returned; not {@code null}
     * @return the node visualization model instance
     */
    public GLNodeModel getNodeModel(Node node) {
        return nodeModelMap.get(node);
    }

    /**
     * Returns edge model instances for the graph visualization hierarchy. The {@code edge} specifies the corresponding
     * instance in network.
     *
     * @see #edges() to iterate all edge model instances
     * @param edge the instance for which the corresponding model should be returned; not {@code null}
     * @return the edge visualization model instance
     */
    public GLFlowEdgeModel getEdgeModel(Edge edge) {
        return edgeModelMap.get(edge);
    }

    /**
     * Iterates over all floor visualization model instances.
     *
     * @see #getFloorModel(int)
     * @return iterable of all floor visualization model instances
     */
    public Iterable<GLGraphFloorControl> floors() {
        return floors;
    }

    /**
     * Iterates over all node visualization model instances.
     *
     * @see #getNodeModel(org.zetool.graph.Node)
     * @return iterable of all node visualization model instances
     */
    public Iterable<GLNodeModel> nodes() {
        return nodeModelMap.values();
    }

    /**
     * Iterates over all edge visualization model instances.
     *
     * @see #getEdgeModel(org.zetool.graph.Edge)
     * @return iterable of all edge visualization model instances
     */
    public Iterable<GLFlowEdgeModel> edges() {
        return edgeModelMap.values();
    }

    /**
     * Creates instances of the {@link GraphVisualizationModelContainer}. The builder is not thread safe.
     */
    public static class Builder {

        private final GraphVisualizationResults visualizationResults;
        private final NetworkVisualizationModel visualizationModel;

        /**
         * The {@link #build() built} root visualization model instance.
         */
        private GLFlowGraphModel network;
        /**
         * The {@link #build() built} floor visualization model instances.
         */
        private List<GLGraphFloorControl> floors;
        /**
         * Map of the {@link #build() built} node visualization model instances.
         */
        private Map<Node, GLNodeModel> nodeMap;
        /**
         * Map of the {@link #build() built} edge visualization model instances.
         */
        private Map<Edge, GLFlowEdgeModel> edgeMap;

        public Builder(GraphVisualizationResults graphVisResult, NetworkVisualizationModel networkVisualizationModel) {
            this.visualizationResults = Objects.requireNonNull(graphVisResult);
            this.visualizationModel = Objects.requireNonNull(networkVisualizationModel);
        }

        /**
         * Builds the complete model instances for the {@link GLFlowGraphModel graph root}, the
         * {@link GLGraphControl floors}, the {@link GLNodeModel nodes}, and the {@link GLFlowEdgeModel edges}.
         *
         * @return the container object instance for all the built graph visualization model instances
         */
        public GraphVisualizationModelContainer build() {
            this.floors = buildFloorModels();
            this.nodeMap = createNodeMapping();
            this.edgeMap = createEdgeMapping(nodeMap.keySet());
            this.network = new GLFlowGraphModel(visualizationModel, nodeMap, edgeMap.values());

            return new GraphVisualizationModelContainer(this);
        }

        private List<GLGraphFloorControl> buildFloorModels() {
            int floorCount = visualizationResults.getFloorToNodeMapping().size();

            ArrayList<GLGraphFloorControl> floorModels = new ArrayList<>(floorCount);
            for (int i = 0; i < floorCount; ++i) {
                GLGraphFloorControl floorModel = new GLGraphFloorControl(visualizationResults,
                        visualizationResults.getFloorToNodeMapping().get(i), i, visualizationModel);
                floorModels.add(floorModel);
            }
            return floorModels;
        }

        /**
         * Builds a map of nodes to their respective GL models. The resulting mapping does not contain an entry for the
         * supersink.
         *
         * @return a mapping of all nodes for visualization (i.e. without supersink)
         */
        private Map<Node, GLNodeModel> createNodeMapping() {
            int floorCount = visualizationResults.getFloorToNodeMapping().size();
            List<Map<Node, GLNodeModel>> nodeModels = new ArrayList<>();
            for (int i = 0; i < floorCount; ++i) {
                Collection<Node> nodesOnTheFloor = visualizationResults.getNodesOnFloor(i);
                Map<Node, GLNodeModel> nodesOnFloor = buildNodeModels(nodesOnTheFloor);
                nodeModels.add(nodesOnFloor);
            }
            return nodeModels.stream()
                    .map(Map::entrySet)
                    .flatMap(Set::stream)
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        private Map<Node, GLNodeModel> buildNodeModels(Collection<Node> nodesOnTheFloor) {
            Map<Node, GLNodeModel> result = nodesOnTheFloor.stream()
                    .collect(toMap(identity(), n -> new GLNodeModel(visualizationResults, n, visualizationModel)));
            return result;
        }

        /**
         * Builds a map of edges to their respective GL model instances. The input nodes are expected to not contain the
         * supersink. The output contains instances for all outgoing edges adjacent to the nodes.
         *
         * @param nodes an iterable of nodes (without supersink)
         * @throws ArithmeticException if the total number of edges is larger than 2147483647
         * ({@code Integer.MAX_VALUE})
         * @return a map of the incident edges of the nodes to the created edge model instances
         */
        private Map<Edge, GLFlowEdgeModel> createEdgeMapping(Iterable<Node> nodes) {
            long edgeCount = StreamSupport.stream(nodes.spliterator(), false)
                    .map(visualizationResults.getNetwork()::outDegree)
                    .count();
            HashMap<Edge, GLFlowEdgeModel> result = new HashMap<>(Math.toIntExact(edgeCount));

            for (Node node : nodes) {
                for (Edge edge : visualizationResults.getNetwork().outgoingEdges(node)) {
                    result.put(edge, new GLFlowEdgeModel(visualizationResults, edge, visualizationModel));
                }
            }
            return result;
        }
    }
}
