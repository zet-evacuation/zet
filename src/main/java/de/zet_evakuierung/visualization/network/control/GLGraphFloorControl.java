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
package de.zet_evakuierung.visualization.network.control;

import de.tu_berlin.math.coga.graph.io.xml.visualization.FlowVisualization;
import de.zet_evakuierung.visualization.VisualizationModel;
import de.zet_evakuierung.visualization.VisualizationNodeModel;
import ds.GraphVisualizationResults;
import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.graph.Node;

/**
 * The {@code GLGraphFloorControl} class represents an floor of the graph network in an MVC-design. It guarantees that
 * the nodes belonging to the floor are created and submitted to the view object.
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLGraphFloorControl extends VisualizationModel<NetworkVisualizationModel> implements VisualizationNodeModel {

    /**
     * Creates a new instance of {@code GLGraphFloorControl}. Therefore for any node in the list {@code nodesOnTheFloor}
     * the related control and visualization objects are created.
     *
     * @param graphVisResult the graph visualization results
     * @param nodesOnTheFloor the nodes that lie on this floor
     * @param floor the number of the floor
     * @param visualizationModel the general control object for visualization
     */
    public GLGraphFloorControl(GraphVisualizationResults graphVisResult, Iterable<Node> nodesOnTheFloor, Integer floor, NetworkVisualizationModel visualizationModel) {
        super(visualizationModel);
    }

    GLGraphFloorControl(FlowVisualization fv, IdentifiableCollection<Node> nodes, NetworkVisualizationModel visualizationModel) {
        super(visualizationModel);
    }

    /**
     * Returns the x-offset of the floor.
     *
     * @return the x-offset of the floor
     */
    @Override
    public double getXPosition() {
        return 0.0d;
    }

    /**
     * Returns the y-offset of the floor.
     *
     * @return the y-offset of the floor
     */
    @Override
    public double getYPosition() {
        return 0.0d;
    }
}
