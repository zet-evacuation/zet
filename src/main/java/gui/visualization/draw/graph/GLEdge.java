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
package gui.visualization.draw.graph;

import javax.media.opengl.GL2;

import gui.visualization.QualityPreset;
import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.graph.GLEdgeControl;
import gui.visualization.control.graph.GLFlowEdgeControl;
import org.zetool.math.vectormath.Vector3;
import org.zetool.opengl.drawingutils.GLColor;
import org.zetool.opengl.framework.abs.AbstractDrawable;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLEdge extends AbstractDrawable<GLEdge, GLEdgeControl> {
    static GLColor edgeColor;
    /* The thickness of the edges and pieces of flow according to their capacities. */
  static double thickness = 1.5;// factor of 1.5 used for test evacuation report
    // TODO read quality from VisualOptionManager
    //private static QualityPreset qualityPreset = VisualizationOptionManager.getQualityPreset();
    private static QualityPreset qualityPreset = QualityPreset.MediumQuality;

    /* The edgeLength of the edge in {@code OpenGL} scaling. */
    double edgeLength;

    public GLEdge( GLEdgeControl control ) {
        super( control );
        //update();
    }


    /**
     * Draws the static structure of the edge that means the edge, if it is the first one
     * of the two edges. The flow is not painted.
     * @see GLFlowEdgeControl#isFirstEdge()
     * @param gl the {@code OpenGL} drawable object
     */
    @Override
    public void performStaticDrawing( GL2 gl ) {
        beginDraw( gl );
        edgeColor.draw( gl );
        if( control.isFirstEdge() )
            drawStaticStructure( gl );
        endDraw( gl );
    }

    /**
     * Draws all edges (without flow).
     * Therefore, the coordinate system is rotated in such a  way that the cylinder is drawn into the direction
     * of the difference vector of start and end node. Usually {@code OpenGL} draws cylinders into the direction
     * (0,0,1), so the difference vector has to be rotated into this vector.
     * @param drawable a {@code GLAutoDrawable} on which the edges are drawn.
     */
    private void drawStaticStructure( GL2 gl ) {
        gl.glPushMatrix();
        //gl.glEnable( gl.GL_BLEND );
        //gl.glBlendFunc( gl.GL_SRC_ALPHA, gl.GL_ONE );

        Vector3 b = new Vector3( 0, 0, 1 );
        Vector3 a = control.getDifferenceVectorInOpenGlScaling();
        Vector3 axis = control.getRotationAxis( a, b );
        gl.glRotated( control.getAngleBetween( a, b ), axis.x, axis.y, axis.z );

        GLU_INSTANCE.gluCylinder( GLU_QUADRIC, thickness, thickness, edgeLength, qualityPreset.edgeSlices, 1 );
        //gl.glDisable( GL.GL_BLEND );
        gl.glPopMatrix();
    }


    @Override
    public void update() {
        edgeLength = control.get3DLength();
        edgeColor = VisualizationOptionManager.getEdgeColor();
        thickness = control.getThickness();//5;

    }

}
