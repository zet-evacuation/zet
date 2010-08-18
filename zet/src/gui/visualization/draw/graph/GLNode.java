/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/**
 * GLNode.java
 */
package gui.visualization.draw.graph;

import gui.visualization.QualityPreset;
import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.graph.GLFlowGraphControl;
import gui.visualization.control.graph.GLNodeControl;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import opengl.drawingutils.GLColor;
import opengl.framework.abs.AbstractDrawable;

public class GLNode extends AbstractDrawable<GLFlowEdge, GLNodeControl> {
	//private GLNodeControl control;
	// TODO read value from VisualizationOptionManager
	//private double graphHeight = VisualizationOptionManager.getGraphHeight() * GLFlowGraphControl.sizeMultiplicator;
	private double graphHeight = 7.0;
	private double radius;
	int nodeDisplayMode = GLU.GLU_FILL;//GLU.GLU_SILHOUETTE;
	int flowDisplayMode = GLU.GLU_FILL;
	GLColor nodeColor = VisualizationOptionManager.getNodeColor();
	GLColor evacuationColor = VisualizationOptionManager.getEvacuationNodeColor();
	GLColor sourceColor = VisualizationOptionManager.getSourceNodeColor();
	GLColor deletedSourceColor = VisualizationOptionManager.getDeletedSourceNodeColor();
	GLColor nodeBorderColor = VisualizationOptionManager.getNodeBorderColor();
	static double nodeRadius = 1.3 /* 2.2*/; // 13 // factor of 2.2 used for test evacuation report
	// TODO read quality preset from VisualizatonOptionManager
	//private static QualityPreset qualityPreset = VisualizationOptionManager.getQualityPreset();
	private static QualityPreset qualityPreset = QualityPreset.MediumQuality;

	public GLNode( GLNodeControl control ) {
		super( control );

		this.control = control;
		this.position.x = control.getXPosition();
		this.position.y = control.getYPosition();
		this.position.z = control.getZPosition();
		this.radius = nodeRadius;
		// not neccesary here!
		//glu = new GLU();
		//quadObj = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle( quadObj, nodeDisplayMode );
		glu.gluQuadricOrientation( quadObj, GLU.GLU_OUTSIDE );

	}

	@Override
	public void performDrawing( GL gl ) {
		super.performDrawing( gl );
		if( getControl().isCurrentlyOccupied() ) {
			performFlowDrawing( gl );
		}
	}

	/**
	 * Draws a node as a solid sphere. The number of slices and stacks is defined
	 * by the given quality preset.
	 * @param gl the context on which the node is drawn
	 */
	public void performFlowDrawing( GL gl ) {
		super.performDrawing( gl );
		glu.gluQuadricDrawStyle( quadObj, flowDisplayMode );

		System.out.print( "Node " + this.toString() + " is drawn." );

		gl.glColor4d( 1.0, 0.0, 0.0, 1.0 );

		//gl.glEnable( gl.GL_BLEND );
		//gl.glBlendFunc( gl.GL_SRC_ALPHA, gl.GL_ONE );
		glu.gluSphere( quadObj, radius * 0.8, qualityPreset.nodeSlices, qualityPreset.nodeStacks );
		//gl.glDisable( GL.GL_BLEND );
	}

	@Override
	public void update() { }
	
	@Override
	public void performStaticDrawing( GL gl ) {
		beginDraw( gl );

//		if( getControl().isCurrentlyOccupied() ) {
//			performFlowDrawing( drawable );
//		}
		glu.gluQuadricDrawStyle( quadObj, nodeDisplayMode );

		//gl.glColor4d( 1.0, 1.0, 0.0, 0.3 );
		nodeBorderColor.draw( gl );
		double xOffset = -this.getControl().getXPosition();// * GLFlowGraphControl.sizeMultiplicator;
		double yOffset = this.getControl().getYPosition();// * GLFlowGraphControl.sizeMultiplicator;
		if( control.isRectangleVisible() ) {
			gl.glBegin( GL.GL_LINES );
			gl.glVertex3d( this.getControl().getNwX() * GLFlowGraphControl.sizeMultiplicator + xOffset, this.getControl().getNwY() * GLFlowGraphControl.sizeMultiplicator + yOffset, -graphHeight + 1.0 * GLFlowGraphControl.sizeMultiplicator );
			gl.glVertex3d( this.getControl().getSeX() * GLFlowGraphControl.sizeMultiplicator + xOffset, this.getControl().getNwY() * GLFlowGraphControl.sizeMultiplicator + yOffset, -graphHeight + 1.0 * GLFlowGraphControl.sizeMultiplicator );

			gl.glVertex3d( this.getControl().getSeX() * GLFlowGraphControl.sizeMultiplicator + xOffset, this.getControl().getNwY() * GLFlowGraphControl.sizeMultiplicator + yOffset, -graphHeight + 1.0 * GLFlowGraphControl.sizeMultiplicator );
			gl.glVertex3d( this.getControl().getSeX() * GLFlowGraphControl.sizeMultiplicator + xOffset, this.getControl().getSeY() * GLFlowGraphControl.sizeMultiplicator + yOffset, -graphHeight + 1.0 * GLFlowGraphControl.sizeMultiplicator );

			gl.glVertex3d( this.getControl().getSeX() * GLFlowGraphControl.sizeMultiplicator + xOffset, this.getControl().getSeY() * GLFlowGraphControl.sizeMultiplicator + yOffset, -graphHeight + 1.0 * GLFlowGraphControl.sizeMultiplicator );
			gl.glVertex3d( this.getControl().getNwX() * GLFlowGraphControl.sizeMultiplicator + xOffset, this.getControl().getSeY() * GLFlowGraphControl.sizeMultiplicator + yOffset, -graphHeight + 1.0 * GLFlowGraphControl.sizeMultiplicator );

			gl.glVertex3d( this.getControl().getNwX() * GLFlowGraphControl.sizeMultiplicator + xOffset, this.getControl().getSeY() * GLFlowGraphControl.sizeMultiplicator + yOffset, -graphHeight + 1.0 * GLFlowGraphControl.sizeMultiplicator );
			gl.glVertex3d( this.getControl().getNwX() * GLFlowGraphControl.sizeMultiplicator + xOffset, this.getControl().getNwY() * GLFlowGraphControl.sizeMultiplicator + yOffset, -graphHeight + 1.0 * GLFlowGraphControl.sizeMultiplicator );
			gl.glEnd();
		}
		//gl.glEnable( gl.GL_BLEND );
		//gl.glBlendFunc( gl.GL_SRC_ALPHA, gl.GL_ONE );

		if( control.isEvacuationNode() ) {
			evacuationColor.draw( gl );
		} else {
			if( control.isSourceNode() ) {
				sourceColor.draw( gl );
			} else {
				if( control.isDeletedSourceNode() ) {
					deletedSourceColor.draw( gl );
				} else {
					nodeColor.draw( gl );
				}
			}
		}

		glu.gluSphere( quadObj, radius, qualityPreset.nodeSlices, qualityPreset.nodeStacks );
		staticDrawAllChildren( gl );
		endDraw( gl );
	}
}