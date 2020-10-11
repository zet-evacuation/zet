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

import java.util.HashMap;
import java.util.Iterator;

import de.tu_berlin.math.coga.graph.io.xml.visualization.FlowVisualization;
import de.zet_evakuierung.visualization.network.draw.GLFlowGraph;
import ds.GraphVisualizationResults;
import gui.visualization.control.AbstractZETVisualizationControl;
import org.zetool.graph.Node;
import org.zetool.opengl.framework.abs.HierarchyNode;

/**
 * @author Jan-Philipp Kappmeier
 */
public class GLFlowGraphControl extends AbstractZETVisualizationControl<GLGraphFloorControl, GLFlowGraph, NetworkVisualizationModel> implements HierarchyNode {

    private HashMap<Integer, GLGraphFloorControl> allFloorsByID;
    private boolean supportsFloors = false;
    double scaling = 1;
    double defaultFloorHeight = 25;
    private GraphVisualizationResults graphVisResult;

    public GLFlowGraphControl(GraphVisualizationResults graphVisResult, NetworkVisualizationModel visualizationModel) {
        super(visualizationModel);
        this.graphVisResult = graphVisResult;
    }

    public void build() {
        //AlgorithmTask.getInstance().setProgress( 0, DefaultLoc.getSingleton().getStringWithoutPrefix( "batch.tasks.progress.createGraphVisualizationDataStructure" ), "" );
        visualizationModel.init(graphVisResult.getNetwork().nodes().size(), graphVisResult.getSupersink().id());
        allFloorsByID = new HashMap<>();
        supportsFloors = true;
        int floorCount = graphVisResult.getFloorToNodeMapping().size();
        clear();

        for (int i = 0; i < floorCount; i++) {
            GLGraphFloorControl floorControl = new GLGraphFloorControl(graphVisResult, graphVisResult.getFloorToNodeMapping().get(i), i, visualizationModel);
            add(floorControl);
            allFloorsByID.put(i, floorControl);
        }
        this.setView(new GLFlowGraph(this, visualizationModel));
        for (GLGraphFloorControl floor : this) {
            view.addChild(floor.getView());
        }
    }

    public GLFlowGraphControl(FlowVisualization fv, NetworkVisualizationModel visualizationModel) {
        super(visualizationModel);

        //AlgorithmTask.getInstance().setProgress( 0, DefaultLoc.getSingleton().getStringWithoutPrefix( "batch.tasks.progress.createGraphVisualizationDataStructure" ), "" );
        visualizationModel.init(fv.getNetwork().nodes().size(), fv.getSinks().get(0).id());

        Iterator<Node> it = fv.getNetwork().nodes().iterator();
        Node supersink = fv.getSinks().get(0);  // graphVisResult.getSupersink();

        GLGraphFloorControl floorControl = new GLGraphFloorControl(fv, fv.getNetwork().nodes(), visualizationModel);
        add(floorControl);

        allFloorsByID = new HashMap<>();
        allFloorsByID.put(0, floorControl);

        this.setView(new GLFlowGraph(this, visualizationModel));
        for (GLGraphFloorControl floor : this) {
            view.addChild(floor.getView());
        }
    }

    @Override
    public void clear() {
        allFloorsByID.clear();
        childControls.clear();
    }

    @Override
    public Iterator<GLGraphFloorControl> fullIterator() {
        return allFloorsByID.values().iterator();
    }

    GLGraphFloorControl getFloorControl(Integer floorID) {
        return this.allFloorsByID.get(floorID);
    }

    public void showOnlyFloor(Integer floorID) {
        childControls.clear();
        childControls.add(allFloorsByID.get(floorID));
        view.clear();
        for (GLGraphFloorControl floor : this) {
            view.addChild(floor.getView());
        }
    }

    public void showAllFloors() {
        childControls.clear();
        childControls.addAll(allFloorsByID.values());
        view.clear();
        for (GLGraphFloorControl floor : this) {
            view.addChild(floor.getView());
        }
    }

    @Override
    public void delete() {
        view.delete();
    }

    public void stepUpdate() {
        int step = (int) visualizationModel.getStep();
        for (GLGraphFloorControl g : this) {
            for (GLNodeControl node : g) {
                for (GLFlowEdgeControl edge : node) {
                    edge.stepUpdate();
                }
                node.stepUpdate((int) step);
            }
        }
    }
}
