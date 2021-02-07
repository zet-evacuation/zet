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
package ds;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.tu_berlin.math.coga.graph.io.xml.visualization.FlowVisualization;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphMapping;
import de.zet_evakuierung.network.model.NetworkFlowModel;
import de.zet_evakuierung.visualization.network.GraphVisualizationData;
import ds.graph.NodeRectangle;
import gui.visualization.VisualizationOptionManager;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.container.mapping.IdentifiableObjectMapping;
import org.zetool.graph.DirectedGraph;
import org.zetool.graph.Node;
import org.zetool.graph.visualization.NodePositionMapping;
import org.zetool.math.vectormath.Vector3;
import org.zetool.netflow.classic.PathComposition;
import org.zetool.netflow.ds.flow.EdgeBasedFlowOverTime;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;

/**
 * The class {@code GraphVisualizationResults} contains all information necessary to visualize the result of a dynamic
 * flow algorithm. Therefore the network itself is included, as well as the result flow and a mapping giving the
 * rectangle in the real world that each node is covering. Also the floor that each node belongs to is saved. The floors
 * have indices according to their position in the list of floors in the z-format.
 */
public class GraphVisualizationResults extends FlowVisualization implements GraphVisualizationData {
	/** A mapping saving a rectangle in the real world for each node. */
	private IdentifiableObjectMapping<Node, NodeRectangle> nodeRectangles;
	/** Mapping of nodes located on which floor. */
	private ArrayList<ArrayList<Node>> floorToNodeMapping;
	/** A mapping giving the number of the floor (this node lies in) in the list in the z-format. */
	private IdentifiableIntegerMapping<Node> nodeToFloorMapping;
	/** A mapping telling whether each node has been a source node that has been deleted. */
	private IdentifiableObjectMapping<Node, Boolean> isDeletedSourceNode;

    public GraphVisualizationResults(double d) {
        super(null); // TODO
    }

    public GraphVisualizationResults(EarliestArrivalFlowProblem earliestArrivalFlowProblem, IdentifiableIntegerMapping<Node> xPos, IdentifiableIntegerMapping<Node> yPos, PathBasedFlowOverTime flowOverTime) {
        super(earliestArrivalFlowProblem, new NodePositionMapping<Vector3>(3, earliestArrivalFlowProblem.getNetwork().nodeCount()));
        // TODO: set up node position mapping

        int nodeCount = getNetwork().nodeCount();
        this.nodeRectangles = new IdentifiableObjectMapping<>(nodeCount);
        for (Node node : getNetwork().nodes()) {
            int x = xPos.get(node);
            int y = yPos.get(node);
// TODO:
//            getNodePositionMapping().set(node, new Vector3(x, y, 0));
            NodeRectangle nodeRectangle = new NodeRectangle(x, y, x, y);
            nodeRectangles.set(node, nodeRectangle);
        }

        setFlowOverTime(flowOverTime);
    }

    public GraphVisualizationResults(NetworkFlowModel networkFlowModel, NodePositionMapping nodePositionMapping) {
        super(networkFlowModel.getEAFP(), createNodeCoordinates(networkFlowModel));

        ZToGraphMapping mapping = networkFlowModel.getZToGraphMapping();
        this.nodeRectangles = mapping.getNodeRectangles();
        this.nodeToFloorMapping = mapping.getNodeFloorMapping();
        this.isDeletedSourceNode = mapping.getIsDeletedSourceNode();

        this.setContainsSuperSink(true);

        this.floorToNodeMapping = new ArrayList<>();

        DirectedGraph network = networkFlowModel.getEAFP().getNetwork();

        for (Node node : network.nodes()) {
            int floor = this.nodeToFloorMapping.get(node);

            if (floor != -1) {
                while (this.floorToNodeMapping.size() < floor) {
                    this.floorToNodeMapping.add(new ArrayList<>());
                }
                if (this.floorToNodeMapping.size() <= floor) {
                    this.floorToNodeMapping.add(floor, new ArrayList<>());
                }
                this.floorToNodeMapping.get(floor).add(node);
            }
        }
        setMaximumFlowValue(0);
        setFlow(new EdgeBasedFlowOverTime(getNetwork()));
    }

    public GraphVisualizationResults(NetworkFlowModel nfm, PathBasedFlowOverTime dynamicFlow) {
        this(nfm, createNodeCoordinates(nfm));
        this.setFlowOverTime(dynamicFlow);

    }

    public static NodePositionMapping<Vector3> createNodeCoordinates(NetworkFlowModel model) {
        NodePositionMapping<Vector3> nodePositionMapping = new NodePositionMapping<>(3, model.graph().nodeCount());
        for (Node n : model.graph().nodes()) {
            final Vector3 v;
            NodeRectangle rect = model.getZToGraphMapping().getNodeRectangles().get(n);
            final double zs = model.getZToGraphMapping().getNodeFloorMapping().get(n) * VisualizationOptionManager.getFloorDistance();
            v = new Vector3(rect.getCenterX(), rect.getCenterY(), zs);
            nodePositionMapping.set(n, v);
        }
        return nodePositionMapping;
    }

    /**
     * Returns a mapping that assigns nodes to rectangles in the real world.
     *
     * @param node
     * @return a mapping that assigns nodes to rectangles in the real world.
     */
    @Override
    public Optional<NodeRectangle> getNodeRectangle(Node node) {
        return Optional.of(nodeRectangles.get(node));
    }

    /**
     * Returns a mapping that assigns a floor number to each node.
     *
     * @param node the node
     * @return a mapping that assigns a floor number to each node.
     */
    @Override
    public int getLayer(Node node) {
        return nodeToFloorMapping.get(node);
    }

    @Override
    public int getLayerCount() {
        return floorToNodeMapping.size();
    }

    /**
     * Returns a mapping that contains all nodes lying on a floor.
     *
     * @param floorId the id of the floor
     * @return a list of all nodes connected to a floor
     */
    @Override
    public List<Node> getNodesOnLayer(int floorId) {
        return floorToNodeMapping.get(floorId);
    }

    /**
     * Returns whether {@code node} has been a source node that was deleted.
     *
     * @param node a node
     * @return whether {@code node} has been a source node that was deleted.
     */
    @Override
    public boolean isDeletedSource(Node node) {
        return isDeletedSourceNode.get(node);
    }

    private void setFlowOverTime(PathBasedFlowOverTime flowOverTime) {
        if (flowOverTime != null) {
            PathComposition pathComposition = new PathComposition(getNetwork(), getTransitTimes(), flowOverTime);
            pathComposition.run();
            this.setFlow(pathComposition.getEdgeFlows());
            setMaximumFlowValue(pathComposition.getMaxFlowRate());
        } else {
            setFlow(new EdgeBasedFlowOverTime(getNetwork()));
            setMaximumFlowValue(0);
        }
    }

}
