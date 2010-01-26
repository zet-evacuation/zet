/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
package gui.visualization.draw.graph;

import gui.visualization.QualityPreset;
import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.graph.GLEdgeControl;
import gui.visualization.control.graph.GLNodeControl;
import gui.visualization.util.VisualizationConstants;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import opengl.drawingutils.GLColor;
import opengl.framework.abs.AbstractDrawable;

public class GLNode extends AbstractDrawable<GLEdge, GLNodeControl, GLEdgeControl> {
//public class GLNode extends AbstractDrawable<CullingShapeSphere, GLEdge, GLNodeControl, GLEdgeControl> {
	//private GLNodeControl control;
	private double graphHeight = VisualizationOptionManager.getGraphHeight() * VisualizationConstants.SIZE_MULTIPLICATOR;
	private double radius;
	int nodeDisplayMode = GLU.GLU_FILL;//GLU.GLU_SILHOUETTE;
	int flowDisplayMode = GLU.GLU_FILL;
	GLColor nodeColor = VisualizationOptionManager.getNodeColor();
	GLColor evacuationColor = VisualizationOptionManager.getEvacuationNodeColor();
	GLColor sourceColor = VisualizationOptionManager.getSourceNodeColor();
	GLColor deletedSourceColor = VisualizationOptionManager.getDeletedSourceNodeColor();
	GLColor nodeBorderColor = VisualizationOptionManager.getNodeBorderColor();
	static double nodeRadius = 13 * 2.2; // 13 
	private static QualityPreset qualityPreset = VisualizationOptionManager.getQualityPreset();

	public GLNode( GLNodeControl control ) {
		super( control );
//			super( control, new CullingShapeSphere() );
		this.control = control;
		this.position.x = control.getXPosition() * VisualizationConstants.SIZE_MULTIPLICATOR;
		this.position.y = control.getYPosition() * VisualizationConstants.SIZE_MULTIPLICATOR;
		this.position.z = control.getZPosition() * VisualizationConstants.SIZE_MULTIPLICATOR;
		this.radius = nodeRadius * VisualizationConstants.SIZE_MULTIPLICATOR;
		glu = new GLU();
		quadObj = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle( quadObj, nodeDisplayMode );
		glu.gluQuadricOrientation( quadObj, GLU.GLU_OUTSIDE );

	}

	@Override
	public void performDrawing( GLAutoDrawable drawable ) {
		super.performDrawing( drawable );
		if( getControl().isCurrentlyOccupied() ) {
			performFlowDrawing( drawable );
		}
	}

	/**
	 * Draws a node as a solid sphere. The number of slices and stacks is defined
	 * by the given quality preset.
	 * @param drawable the context on which the node is drawn
	 */
	public void performFlowDrawing( GLAutoDrawable drawable ) {
		super.performDrawing( drawable );
		GL gl = drawable.getGL();

		glu.gluQuadricDrawStyle( quadObj, flowDisplayMode );

		gl.glColor4d( 1.0, 0.0, 0.0, 1.0 );

		//gl.glEnable( gl.GL_BLEND );
		//gl.glBlendFunc( gl.GL_SRC_ALPHA, gl.GL_ONE );
		glu.gluSphere( quadObj, radius * 0.8, qualityPreset.nodeSlices, qualityPreset.nodeStacks );
		//gl.glDisable( GL.GL_BLEND );
	}

	@Override
	public void update() { }
	
	@Override
	public void performStaticDrawing( GLAutoDrawable drawable ) {
		beginDraw( drawable );
		GL gl = drawable.getGL();

//		if( getControl().isCurrentlyOccupied() ) {
//			performFlowDrawing( drawable );
//		}
		glu.gluQuadricDrawStyle( quadObj, nodeDisplayMode );

		//gl.glColor4d( 1.0, 1.0, 0.0, 0.3 );
		nodeBorderColor.performGL( gl );
		double xOffset = -this.getControl().getXPosition() * VisualizationConstants.SIZE_MULTIPLICATOR;
		double yOffset = this.getControl().getYPosition() * VisualizationConstants.SIZE_MULTIPLICATOR;
		if( control.isRectangleVisible() ) {
			gl.glBegin( GL.GL_LINES );
			gl.glVertex3d( this.getControl().getNwX() * VisualizationConstants.SIZE_MULTIPLICATOR + xOffset, this.getControl().getNwY() * VisualizationConstants.SIZE_MULTIPLICATOR + yOffset, -graphHeight + 1.0 * VisualizationConstants.SIZE_MULTIPLICATOR );
			gl.glVertex3d( this.getControl().getSeX() * VisualizationConstants.SIZE_MULTIPLICATOR + xOffset, this.getControl().getNwY() * VisualizationConstants.SIZE_MULTIPLICATOR + yOffset, -graphHeight + 1.0 * VisualizationConstants.SIZE_MULTIPLICATOR );

			gl.glVertex3d( this.getControl().getSeX() * VisualizationConstants.SIZE_MULTIPLICATOR + xOffset, this.getControl().getNwY() * VisualizationConstants.SIZE_MULTIPLICATOR + yOffset, -graphHeight + 1.0 * VisualizationConstants.SIZE_MULTIPLICATOR );
			gl.glVertex3d( this.getControl().getSeX() * VisualizationConstants.SIZE_MULTIPLICATOR + xOffset, this.getControl().getSeY() * VisualizationConstants.SIZE_MULTIPLICATOR + yOffset, -graphHeight + 1.0 * VisualizationConstants.SIZE_MULTIPLICATOR );

			gl.glVertex3d( this.getControl().getSeX() * VisualizationConstants.SIZE_MULTIPLICATOR + xOffset, this.getControl().getSeY() * VisualizationConstants.SIZE_MULTIPLICATOR + yOffset, -graphHeight + 1.0 * VisualizationConstants.SIZE_MULTIPLICATOR );
			gl.glVertex3d( this.getControl().getNwX() * VisualizationConstants.SIZE_MULTIPLICATOR + xOffset, this.getControl().getSeY() * VisualizationConstants.SIZE_MULTIPLICATOR + yOffset, -graphHeight + 1.0 * VisualizationConstants.SIZE_MULTIPLICATOR );

			gl.glVertex3d( this.getControl().getNwX() * VisualizationConstants.SIZE_MULTIPLICATOR + xOffset, this.getControl().getSeY() * VisualizationConstants.SIZE_MULTIPLICATOR + yOffset, -graphHeight + 1.0 * VisualizationConstants.SIZE_MULTIPLICATOR );
			gl.glVertex3d( this.getControl().getNwX() * VisualizationConstants.SIZE_MULTIPLICATOR + xOffset, this.getControl().getNwY() * VisualizationConstants.SIZE_MULTIPLICATOR + yOffset, -graphHeight + 1.0 * VisualizationConstants.SIZE_MULTIPLICATOR );
			gl.glEnd();
		}
		//gl.glEnable( gl.GL_BLEND );
		//gl.glBlendFunc( gl.GL_SRC_ALPHA, gl.GL_ONE );

		if( control.isEvacuationNode() ) {
			evacuationColor.performGL( gl );
		} else {
			if( control.isSourceNode() ) {
				sourceColor.performGL( gl );
			} else {
				if( control.isDeletedSourceNode() ) {
					deletedSourceColor.performGL( gl );
				} else {
					nodeColor.performGL( gl );
				}
			}
		}

		glu.gluSphere( quadObj, radius, qualityPreset.nodeSlices, qualityPreset.nodeStacks );
		staticDrawAllChildren( drawable );
		endDraw( drawable );
	}
}