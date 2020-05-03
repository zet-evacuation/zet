/**
 * GLSimpleNode.java
 * Created: Aug 18, 2010,3:31:03 PM
 */
package gui.visualization.draw.graph;

import java.awt.Color;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import gui.visualization.QualityPreset;
import gui.visualization.control.graph.GLSimpleNodeControl;
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

    public GLSimpleNode(GLSimpleNodeControl control) {
        super(control);
        this.radius = 13 * 0.1;
        position = new GLVector(control.getPosition());
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

        double xOffset = -this.getControl().getXPosition();
        double yOffset = this.getControl().getYPosition();

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
