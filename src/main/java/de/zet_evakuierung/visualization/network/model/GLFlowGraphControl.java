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

import de.zet_evakuierung.visualization.network.draw.GLFlowGraph;
import de.zet_evakuierung.visualization.network.draw.GLGraphViews;
import de.zet_evakuierung.visualization.network.draw.GLNode;
import gui.visualization.control.AbstractZETVisualizationControl;
import org.zetool.opengl.framework.abs.Drawable;
import org.zetool.opengl.framework.abs.HierarchyNode;

/**
 * @author Jan-Philipp Kappmeier
 */
public class GLFlowGraphControl extends AbstractZETVisualizationControl<GLGraphFloorModel, GLFlowGraph, NetworkVisualizationModel>
        implements HierarchyNode<GLGraphFloorModel>, Drawable {

    /**
     * Gives access to the model objects used by visualization views.
     */
    private GraphVisualizationModelContainer graphModel;
    /**
     * Gives access to all view objects drawing the OpenGL scene.
     */
    private GLGraphViews views;

    double scaling = 1;
    double defaultFloorHeight = 25;

    public GLFlowGraphControl(NetworkVisualizationModel visualizationModel, GraphVisualizationModelContainer graphModel,
            GLGraphViews views) {
        super(visualizationModel);

        this.graphModel = graphModel;
        this.views = views;
        setView(views.getView());
    }

    @Override
    public void clear() {
        childControls.clear();
    }

    public void showOnlyFloor(Integer floorId) {
        childControls.clear();
        childControls.add(graphModel.getFloorModel(floorId));
        view.clear();
        for (GLGraphFloorModel floor : this) {
            view.addChild(views.getView(floor));
        }
    }

    public void showAllFloors() {
        childControls.clear();
        graphModel.floors().forEach(childControls::add);
        view.clear();
        for (GLGraphFloorModel floor : this) {
            view.addChild(views.getView(floor));
        }
    }

    @Override
    public void delete() {
        view.delete();
    }

    public void showNodeRectangles(boolean selected) {
        graphModel.nodes().forEach(n -> n.setRectangleVisible(selected));
        for (GLNode nodeView : views.nodeViews()) {
            nodeView.update();
        }
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported.");
    }
}