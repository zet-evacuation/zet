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

import de.tu_berlin.math.coga.zet.viewer.NashFlowEdgeData;
import de.tu_berlin.math.coga.zet.viewer.NashFlowVisualization;
import de.zet_evakuierung.visualization.network.draw.GLNashGraph;
import org.zetool.container.mapping.IdentifiableObjectMapping;
import org.zetool.graph.DirectedGraph;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import org.zetool.graph.visualization.NodePositionMapping;
import org.zetool.math.vectormath.Vector3;
import org.zetool.opengl.framework.abs.Drawable;
import org.zetool.opengl.framework.abs.VisualizationModel;
import org.zetool.opengl.helper.Frustum;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLNashGraphModel extends GLGraphModel implements Drawable, VisualizationModel {

    private final IdentifiableObjectMapping<Edge, NashFlowEdgeData> nashFlowMapping;
    private final NashFlowVisualization nfv;

    public GLNashGraphModel(DirectedGraph graph, NodePositionMapping<Vector3> nodePositionMapping, IdentifiableObjectMapping<Edge, NashFlowEdgeData> nashFlowMapping, NashFlowVisualization nfv) {
        super(graph, nodePositionMapping, false);
        this.nashFlowMapping = nashFlowMapping;
        this.nfv = nfv;
        setUpNodes();
    }

    @Override
    protected void setUpNodes() {
        for (Node n : graph.nodes()) {
            GLNashNodeModel nodeControl = new GLNashNodeModel(graph, n, nodePositionMapping, nashFlowMapping, nfv);
            add(nodeControl);
        }

        this.setView(new GLNashGraph(this));
        for (GLSimpleNodeModel nodeControl : this) {
            view.addChild(nodeControl.getView());
        }
    }

    @Override
    public boolean isFinished() {
        return time > endTime;
    }

    double time = 0;
    double endTime = 0;

    @Override
    public void addTime(long timeNanoSeconds) {
        time += timeNanoSeconds;
    }

    public void setEndTime(long timeNanoSeconds) {
        endTime = timeNanoSeconds;
    }

    @Override
    public void setFrustum(Frustum frustum) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTime(long time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void resetTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Frustum getFrustum() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
