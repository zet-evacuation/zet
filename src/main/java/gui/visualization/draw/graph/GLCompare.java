/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author schwengf
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
