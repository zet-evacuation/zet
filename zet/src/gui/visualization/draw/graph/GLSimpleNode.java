/**
 * GLSimpleNode.java
 * Created: Aug 18, 2010,3:31:03 PM
 */
package gui.visualization.draw.graph;

import gui.visualization.QualityPreset;
import gui.visualization.control.graph.GLFlowGraphControl;
import gui.visualization.control.graph.GLSimpleNodeControl;
import java.awt.Color;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import opengl.drawingutils.GLColor;
import opengl.drawingutils.GLVector;
import opengl.framework.abs.AbstractDrawable;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
//public class GLNode extends AbstractDrawable<GLEdge, GLSimpleNodeControl> {
public class GLSimpleNode extends AbstractDrawable<GLEdge, GLSimpleNodeControl> {
	int nodeDisplayMode = GLU.GLU_FILL;//GLU.GLU_SILHOUETTE;
	GLColor nodeColor = new GLColor( Color.red );
	double radius;
	private static QualityPreset qualityPreset = QualityPreset.MediumQuality;
	
	public GLSimpleNode( GLSimpleNodeControl control ) {
		super( control );
		this.radius = 13 * 0.1;
		position = new GLVector( control.getPosition() );
	}


	@Override
	public void performDrawing( GL gl ) {
		super.performDrawing( gl );
	}

	@Override
	public void performStaticDrawing( GL gl ) {
		beginDraw( gl );
		drawNode( gl );
		System.out.println( "Draw node at " + position.toString() );
		staticDrawAllChildren( gl );
		endDraw( gl );
	}

	final GLColor lineColor = new GLColor( Color.black );

	protected void drawNode( GL gl ) {
//		if( getControl().isCurrentlyOccupied() ) {
//			performFlowDrawing( drawable );
//		}
		glu.gluQuadricDrawStyle( quadObj, nodeDisplayMode );

		//gl.glColor4d( 1.0, 1.0, 0.0, 0.3 );

		double xOffset = -this.getControl().getXPosition() * GLFlowGraphControl.sizeMultiplicator;
		double yOffset = this.getControl().getYPosition() * GLFlowGraphControl.sizeMultiplicator;
		//gl.glEnable( gl.GL_BLEND );
		//gl.glBlendFunc( gl.GL_SRC_ALPHA, gl.GL_ONE );

		nodeColor.draw( gl );

		//}
		nodeDisplayMode = GLU.GLU_FILL;//GLU.GLU_SILHOUETTE;
		glu.gluSphere( quadObj, radius, qualityPreset.nodeSlices, qualityPreset.nodeStacks );

		lineColor.draw( gl );

		glu.gluQuadricDrawStyle( quadObj, GLU.GLU_SILHOUETTE );
		glu.gluSphere( quadObj, radius, qualityPreset.nodeSlices, qualityPreset.nodeStacks );
		glu.gluQuadricDrawStyle( quadObj, nodeDisplayMode );

	}

	@Override
	public void update() {
		
	}

}
