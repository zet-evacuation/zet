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

import java.awt.Color;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import de.zet_evakuierung.visualization.network.control.GLSimpleNodeControl;
import gui.visualization.QualityPreset;
import org.zetool.opengl.drawingutils.GLColor;
import org.zetool.opengl.drawingutils.GLVector;
import org.zetool.opengl.framework.abs.AbstractDrawable;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLSimpleNode extends AbstractDrawable<GLEdge, GLSimpleNodeControl> {

    int nodeDisplayMode = GLU.GLU_FILL;
    GLColor nodeColor = new GLColor(154, 154, 147);
    double radius;
    private static final QualityPreset QUALITY_PRESET = QualityPreset.MediumQuality;

    public GLSimpleNode(GLSimpleNodeControl model) {
        super(model);
        this.radius = 13 * 0.1;
        position = new GLVector(model.getPosition());
    }

    @Override
    public void performDrawing(GL2 gl) {
        super.performDrawing(gl);
    }

    @Override
    public void performStaticDrawing(GL2 gl) {
        beginDraw(gl);
        drawNode(gl);
        staticDrawAllChildren(gl);
        endDraw(gl);
    }

    final GLColor lineColor = new GLColor(Color.black);

    protected void drawNode(GL2 gl) {
        GLU_INSTANCE.gluQuadricDrawStyle(GLU_QUADRIC, nodeDisplayMode);

        double xOffset = -this.getModel().getXPosition();
        double yOffset = this.getModel().getYPosition();

        nodeColor.draw(gl);

        //}
        nodeDisplayMode = GLU.GLU_FILL;//GLU.GLU_SILHOUETTE;
        System.out.println("Textur s_1 beim Zeichnen benutzt");
        GLU_INSTANCE.gluSphere(GLU_QUADRIC, radius, QUALITY_PRESET.nodeSlices, QUALITY_PRESET.nodeStacks);

        lineColor.draw(gl);

        //glu.gluQuadricDrawStyle( quadObj, GLU.GLU_SILHOUETTE );
        //glu.gluSphere( quadObj, radius, qualityPreset.nodeSlices, qualityPreset.nodeStacks );
        //glu.gluQuadricDrawStyle( quadObj, nodeDisplayMode );
    }

    @Override
    public void update() {

    }

}
