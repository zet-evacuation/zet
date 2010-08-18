/**
 * GLEdge.java
 * Created: Aug 17, 2010,3:17:51 PM
 */
package gui.visualization.draw.graph;

import de.tu_berlin.math.coga.math.vectormath.Vector3;
import gui.visualization.QualityPreset;
import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.graph.GLEdgeControl;
import gui.visualization.control.graph.GLFlowEdgeControl;
import gui.visualization.control.graph.GLFlowGraphControl;
import javax.media.opengl.GL;
import opengl.drawingutils.GLColor;
import opengl.framework.abs.AbstractDrawable;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLEdge extends AbstractDrawable<GLEdge, GLEdgeControl> {
	static GLColor edgeColor;
	/* The thickness of the edges and pieces of flow according to their capacities. */
	static double thickness = 5 /* *1.5*/ /* 5 */ * GLFlowGraphControl.sizeMultiplicator; // factor of 1.5 used for test evacuation report
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
	 * {@see GLFlowEdgeControl#isFirstEdge()}
	 * @param gl the {@code OpenGL} drawable object
	 */
	@Override
	public void performStaticDrawing( GL gl ) {
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
	 * @param drawable a <code>GLAutoDrawable</code> on which the edges are drawn.
	 */
	private void drawStaticStructure( GL gl ) {
		gl.glPushMatrix();
		//gl.glEnable( gl.GL_BLEND );
		//gl.glBlendFunc( gl.GL_SRC_ALPHA, gl.GL_ONE );

		Vector3 b = new Vector3( 0, 0, 1 );
		Vector3 a = control.getDifferenceVectorInOpenGlScaling();
		Vector3 axis = control.getRotationAxis( a, b );
		gl.glRotated( control.getAngleBetween( a, b ), axis.x, axis.y, axis.z );

		glu.gluCylinder( quadObj, thickness, thickness, edgeLength, qualityPreset.edgeSlices, 1 );
		//gl.glDisable( GL.GL_BLEND );
		gl.glPopMatrix();
	}


	@Override
	public void update() {
		edgeLength = control.get3DLength();
		edgeColor = VisualizationOptionManager.getEdgeColor();
		
	}

}
