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
package de.zet_evakuierung.visualization.network.model;

import java.util.ArrayList;
import java.util.Optional;

import de.zet_evakuierung.visualization.AbstractVisualizationModel;
import de.zet_evakuierung.visualization.VisualizationNodeModel;
import de.zet_evakuierung.visualization.network.FlowHistroryTriple;
import de.zet_evakuierung.visualization.network.GraphVisualizationData;
import de.zet_evakuierung.visualization.network.GraphVisualizationProperties;
import de.zet_evakuierung.visualization.network.util.FlowCalculator;
import ds.graph.NodeRectangle;
import org.zetool.graph.Node;
import org.zetool.math.vectormath.Vector3;

public class GLNodeModel extends AbstractVisualizationModel<NetworkVisualizationModel> implements VisualizationNodeModel {

    private static final NodeRectangle DEFAULT_NODE_RECTANGLE = new NodeRectangle(0, 0, 0, 0);

    /**
     * Access to properties of the visualization run.
     */
    private final GraphVisualizationProperties properties;
    private final int id;

    private final double xPosition;
    private final double yPosition;
    private final double zPosition;

    private final int capacity;
    private final FlowCalculator flowCalculator;
    double nwX;
    double nwY;
    double seX;
    double seY;
    private double time;
    private int index;
    private ArrayList<FlowHistroryTriple> graphHistory;
    private boolean isEvacuationNode, isSourceNode, isDeletedSourceNode = false;
    private int duration;
    private int startTime;
    private int floor;
    private boolean gridVisible = true;

    public GLNodeModel(GraphVisualizationData visualizationData, Node node, GraphVisualizationProperties properties,
            NetworkVisualizationModel visualizationModel) {
        super(visualizationModel);
        this.properties = properties;

        Optional<NodeRectangle> rectangle = visualizationData.getNodeRectangle(node);
        nwX = rectangle.orElse(DEFAULT_NODE_RECTANGLE).get_nw_point().getX() * properties.getScaling();
        nwY = rectangle.orElse(DEFAULT_NODE_RECTANGLE).get_nw_point().getY() * properties.getScaling();
        seX = rectangle.orElse(DEFAULT_NODE_RECTANGLE).get_se_point().getX() * properties.getScaling();
        seY = rectangle.orElse(DEFAULT_NODE_RECTANGLE).get_se_point().getY() * properties.getScaling();

        Vector3 position = visualizationData.getPosition(node);
        xPosition = position.x * properties.getScaling();
        yPosition = position.y * properties.getScaling();
        capacity = visualizationData.getCapacity(node);

        id = node.id();
        isEvacuationNode = visualizationData.isSink(node);
        isSourceNode = visualizationData.isSource(node);
        isDeletedSourceNode = visualizationData.isDeletedSource(node);

        floor = visualizationData.getLayer(node);

        // set bottom graph 10% above the ground
        double positionOffset = properties.getFloorHeight() * 0.1 * properties.getScaling();
        zPosition = positionOffset + floor * properties.getFloorHeight() * properties.getScaling();

        flowCalculator = new FlowCalculator();
        visualizationModel.nodeProgress();
    }

    public boolean isEvacuationNode() {
        return isEvacuationNode;
    }

    public boolean isSourceNode() {
        return isSourceNode;
    }

    public boolean isDeletedSourceNode() {
        return isDeletedSourceNode;
    }

    public double getNwX() {
        return nwX;
    }

    public double getNwY() {
        return nwY;
    }

    public double getSeX() {
        return seX;
    }

    public double getSeY() {
        return seY;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public double getXPosition() {
        return xPosition;
    }

    @Override
    public double getYPosition() {
        return yPosition;
    }

    public double getZPosition() {
        return zPosition;
    }

    public FlowCalculator getFlowCalculator() {
        return flowCalculator;
    }

    public void stepUpdate(int step) {
        time = visualizationModel.getStep();
        graphHistory = flowCalculator.getCalculatedFlow();

        if (graphHistory.size() <= 0) {
            return;
        }
        if (index < graphHistory.size() && this.graphHistory.get(index).getFirstValue() <= step) {
            this.startTime = this.graphHistory.get(index).getTime();
            this.duration = this.graphHistory.get(index).getDuration();
            index++;
        }
    }

    public final boolean isCurrentlyOccupied() {
        return startTime < time && startTime + duration > time;
    }

    public void setRectangleVisible(boolean val) {
        this.gridVisible = val;
    }

    /**
     * Returns {@code true} if the rectangular area belonging to the node is visible.
     *
     * @return {@code true} if the rectangular node area is visible, {@code false} otherwise
     */
    public boolean isRectangleVisible() {
        return gridVisible;
    }

    /**
     * Returns the default floor height. Thus, the rectangles can be drawn under the nodes by exactly this amount
     *
     * @return
     */
    public double getFloorHeight() {
        return properties.getFloorHeight() * properties.getScaling() * 0.1;
    }

    public int getNumber() {
        return id;
    }
}
