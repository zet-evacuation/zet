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

import javax.media.opengl.GL2;

import org.zetool.graph.DirectedGraph;
import org.zetool.graph.visualization.NodePositionMapping;
import org.zetool.math.vectormath.Vector3;
import org.zetool.opengl.framework.abs.Drawable;
import org.zetool.opengl.helper.Frustum;
import org.zetool.opengl.framework.abs.VisualizationModel;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLNashGraphModel extends GLGraphModel implements Drawable, VisualizationModel {

    public GLNashGraphModel(DirectedGraph graph, NodePositionMapping<Vector3> nodePositionMapping) {
        super(graph, nodePositionMapping);
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

    @Override
    public void draw(GL2 gl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
