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
package de.zet_evakuierung.visualization.network.draw;

import javax.media.opengl.GL2;

import de.zet_evakuierung.visualization.network.model.GLFlowGraphModel;
import de.zet_evakuierung.visualization.network.model.NetworkVisualizationModel;
import org.zetool.opengl.framework.abs.HierarchyRoot;

public class GLFlowGraph extends HierarchyRoot<GLGraphFloor> {

    private final NetworkVisualizationModel networkVisualizationModel;
    private long lastStep = 0;
    private final GLFlowGraphModel model;

    public GLFlowGraph(GLFlowGraphModel model, NetworkVisualizationModel networkVisualizationModel) {
        super();
        this.model = model;
        this.networkVisualizationModel = networkVisualizationModel;
    }

    /**
     * @see opengl.framework.abs.AbstractDrawable#update()
     */
    @Override
    public void update() {
    }

    @Override
    public void performDynamicDrawing(GL2 gl) {
        long step = (long) networkVisualizationModel.getStep();
        if (step != lastStep) {
            model.stepUpdate();
            lastStep = step;
        }
    }
}
