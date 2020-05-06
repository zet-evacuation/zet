/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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
package gui.visualization.draw.graph;

import gui.visualization.QualityPreset;
import gui.visualization.control.CompareControl;

import java.awt.Color;

import javax.media.opengl.GL2;

import org.zetool.opengl.drawingutils.GLColor;
import org.zetool.opengl.framework.abs.AbstractDrawable;

/**
 *
 * @author Marlen Schwengfelder
 */
public class GLCompare extends AbstractDrawable<GLCompare, CompareControl> {

    static GLColor axisColor = new GLColor(Color.RED);
    static double thickness = 5;
    double axisLength = 10.0;
    private static final QualityPreset QUALITY_PRESET = QualityPreset.MediumQuality;

    public GLCompare(CompareControl control) {
        super(control);
    }

    @Override
    public void performStaticDrawing(GL2 gl) {
        beginDraw(gl);
        axisColor.draw(gl);
        //drawStaticStructure( gl );
        endDraw(gl);
    }

    private void drawStaticStructure(GL2 gl) {
        gl.glPushMatrix();
        //gl.glEnable( gl.GL_BLEND );
        //gl.glBlendFunc( gl.GL_SRC_ALPHA, gl.GL_ONE );

        /*Vector3 b = new Vector3( 0, 0, 1 );
        Vector3 a = control.getDifferenceVectorInOpenGlScaling();
        Vector3 axis = control.getRotationAxis( a, b );
        gl.glRotated( control.getAngleBetween( a, b ), axis.x, axis.y, axis.z );*/
        GLU_INSTANCE.gluCylinder(GLU_QUADRIC, thickness, thickness, axisLength, QUALITY_PRESET.edgeSlices, 1);
        //gl.glDisable( GL.GL_BLEND );
        gl.glPopMatrix();
    }

    @Override
    public void update() {
    }
}
