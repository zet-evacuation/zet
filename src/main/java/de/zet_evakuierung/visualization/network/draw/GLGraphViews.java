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
package de.zet_evakuierung.visualization.network.draw;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import java.util.Set;

import de.zet_evakuierung.visualization.network.control.GLFlowEdgeControl;
import de.zet_evakuierung.visualization.network.control.GLFlowGraphControl;
import de.zet_evakuierung.visualization.network.control.GLGraphFloorControl;
import de.zet_evakuierung.visualization.network.control.GLNodeControl;
import de.zet_evakuierung.visualization.network.control.GraphVisualizationModelContainer;
import de.zet_evakuierung.visualization.network.control.NetworkVisualizationModel;
import ds.GraphVisualizationResults;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLGraphViews {

    private final GLFlowGraph rootView;
    private final Map<GLGraphFloorControl, GLGraphFloor> floorViews;
    private final Set<GLNode> nodeViews;
    private final Set<GLEdge> edgeViews;

    private GLGraphViews(GLGraphViewFactory factory) {
        rootView = Objects.requireNonNull(factory.rootView);
        floorViews = Objects.requireNonNull(factory.floorViews);
        nodeViews = Objects.requireNonNull(factory.nodeViews);
        edgeViews = Objects.requireNonNull(factory.edgeViews);
    }

    /**
     * Returns the {@link GLFlowGraph root view} for the {@link GLFlowGraphControl model root} of the visualization
     * hierarchy.
     *
     * @return the root view object
     */
    public GLFlowGraph getView() {
        return rootView;
    }

    public GLGraphFloor getView(GLGraphFloorControl model) {
        return floorViews.get(model);
    }

    public Iterable<GLNode> nodeViews() {
        return nodeViews;
    }
    
    public Iterable<GLEdge> edgeViews() {
        return edgeViews;
    }

    /**
     * Factory method creating a {@link GLGraphViews graph views container} instance.
     *
     * @param networkVisualizationModel the visualization model with basic visualization settings
     * @param graphVisualizationResults the actual network model
     * @param graphModel the input visualization models for graph visualization
     * @param showEdgesBetweenFloors enables or disables creation of inter floor edge view instances
     * @return the created instance
     */
    public static GLGraphViews createInstance(NetworkVisualizationModel networkVisualizationModel,
            GraphVisualizationResults graphVisualizationResults, GraphVisualizationModelContainer graphModel,
            boolean showEdgesBetweenFloors) {
        GLGraphViewFactory factory = new GLGraphViewFactory(networkVisualizationModel, graphVisualizationResults,
                graphModel);
        factory.withEdgesBetweenFloors(showEdgesBetweenFloors).createViews();
        return new GLGraphViews(factory);
    }

    /**
     * Utility factory class creating the view instances.
     */
    private static class GLGraphViewFactory {

        private final NetworkVisualizationModel visualizationModel;
        private final GraphVisualizationModelContainer modelContainer;
        private final GraphVisualizationResults visualizationResults;
        /**
         * Wether edge model instances for inter floor edges should be created.
         */
        private boolean showEdgesBetweenFloors = false;

        /**
         * The created {@link GLFlowGraph hierarchy root} view instance; valid after views
         * {@link #createViews() have been created}.
         */
        private GLFlowGraph rootView;
        private Map<GLGraphFloorControl, GLGraphFloor> floorViews;
        private Set<GLNode> nodeViews;
        private Set<GLEdge> edgeViews;

        private GLGraphViewFactory(NetworkVisualizationModel visualizationModel,
                GraphVisualizationResults graphVisualizationResults,
                GraphVisualizationModelContainer graphModelContainer) {
            this.visualizationModel = visualizationModel;
            this.modelContainer = graphModelContainer;
            this.visualizationResults = graphVisualizationResults;
        }

        /**
         * Decides wether edge model instances for inter floor edges should be created. By default this is set to
         * {@code false}.
         *
         * @param showEdgesBetweenFloors enables or disables creation of inter floor edge view instances
         * @return this instance of the builder
         */
        private GLGraphViewFactory withEdgesBetweenFloors(boolean showEdgesBetweenFloors) {
            this.showEdgesBetweenFloors = showEdgesBetweenFloors;
            return this;
        }

        private void createViews() {
            this.floorViews = new HashMap<>(modelContainer.getFloorCount());
            this.nodeViews = new HashSet<>(visualizationModel.getNodeCount());
            this.edgeViews = new HashSet<>(visualizationModel.getNodeCount() * 2);

            // Set this view.
            rootView = new GLFlowGraph(modelContainer.getGraphModel(), visualizationModel);

            // Create the view hierarchy
            for (int i = 0; i < modelContainer.getFloorCount(); ++i) {
                GLGraphFloorControl floorModel = modelContainer.getFloorModel(i);
                GLGraphFloor floorView = new GLGraphFloor(floorModel);
                floorViews.put(floorModel, floorView);
                rootView.addChild(floorView);
                createNodeViews(i, floorView);
            }
        }

        private void createNodeViews(int floorId, GLGraphFloor parentFloor) {
            Node supersink = visualizationResults.getSupersink();
            for (Node node : visualizationResults.getNodesOnFloor(floorId)) {
                if (node.equals(supersink)) {
                    continue;
                }
                GLNodeControl nodeModel = modelContainer.getNodeModel(node);
                GLNode nodeView = new GLNode(nodeModel);
                parentFloor.addChild(nodeView);
                nodeViews.add(nodeView);
                createEdgeViews(node, nodeView);
            }
        }

        private void createEdgeViews(Node nodeModel, GLNode parentNode) {
            for (Edge edge : visualizationResults.getNetwork().outgoingEdges(nodeModel)) {
                if (edge.end().id() != visualizationModel.superSinkID()) {
                    int nodeFloor1 = visualizationResults.getNodeToFloorMapping().get(edge.start());
                    int nodeFloor2 = visualizationResults.getNodeToFloorMapping().get(edge.end());
                    if (nodeFloor1 == nodeFloor2 || showEdgesBetweenFloors) {
                        GLFlowEdgeControl edgeModel = modelContainer.getEdgeModel(edge);
                        GLFlowEdge edgeView = new GLFlowEdge(edgeModel);
                        parentNode.addChild(edgeView);
                        edgeViews.add(edgeView);
                    }
                }
            }
        }
    }
}
