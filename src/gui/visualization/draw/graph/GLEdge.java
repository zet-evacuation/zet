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
package gui.visualization.draw.graph;

import gui.visualization.QualityPreset;
import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.graph.GLEdgeControl;
import gui.visualization.util.VisualizationConstants;
import java.util.ArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import opengl.drawingutils.GLColor;
import opengl.framework.abs.AbstractDrawable;
import de.tu_berlin.math.coga.math.vectormath.Vector3;

//public class GLEdge extends AbstractDrawable<CullingShapeCube, GLEdge, GLEdgeControl, GLEdgeControl> {
public class GLEdge extends AbstractDrawable<GLEdge, GLEdgeControl, GLEdgeControl> {
	/** The length of single flow units. If set to 1 no single units are displayed. */
	static double factor = 0.7;
	static int edgeDisplayMode = GLU.GLU_FILL;
	static GLColor edgeColor;
	static GLColor flowColor;
	static GLColor flowUnitColor;
	/* The thickness of the edges and pieces of flow according to their capacities. */
	static double thickness = 5 /* *1.5*/ /* 5 */ * VisualizationConstants.SIZE_MULTIPLICATOR; // factor of 1.5 used for test evacuation report
	static double flowThickness = 7 /* * 1.5 */ /* 7 */* VisualizationConstants.SIZE_MULTIPLICATOR; //// factor of 1.5 used for test evacuation report
	static double minFlowThickness = 10; /* 10 */ // original 3
	static double maxFlowThickness = 10;
	static double flowThicknessOfOneCapacityStep;
	int maxFlowRate = control.getMaxFlowRate();
	/* Telling the flow on the edge for each time step. */
	private ArrayList<Integer> flowOnEdge;
	/* The capacity of the edge */
	double capacity;
	/** The transit time of the edge */
	int transitTime;
	/* not used */
	double maxCapacity;
	/* The length of the edge in {@code OpenGL} scaling. */
	double length;
	private static QualityPreset qualityPreset = VisualizationOptionManager.getQualityPreset();

	public GLEdge( GLEdgeControl control ) {
		super( control );
//		super( control, new CullingShapeCube() );
		update();
		glu.gluQuadricDrawStyle( quadObj, edgeDisplayMode );		// Fill, points, lines
	}

	/**
	 * Draws a piece of flow. The start and end of the piece can be drawn or not.
	 * If they are drawn their color is flowUnitColor.
	 * @param length length of the flow piece to draw.
	 * @param diskAtStart tells whether there shall be a disk at the start of the piece.
	 * @param diskAtEnd tells whether there shall be a disk at the end of the piece.
	 */
	private void drawPieceOfFlow( GL gl, double length, double calculatedFlowThickness, boolean diskAtStart, boolean diskAtEnd ) {
		diskAtStart = diskAtStart && (factor < 1);
		diskAtEnd = diskAtEnd && (factor < 1);
		if( diskAtEnd ) {
			flowUnitColor.draw( gl );
			glu.gluDisk( quadObj, thickness, calculatedFlowThickness, qualityPreset.edgeSlices, 1 );
		}
		flowColor.draw( gl );
		glu.gluCylinder( quadObj, calculatedFlowThickness, calculatedFlowThickness, length, qualityPreset.edgeSlices, 1 );
		if( diskAtStart ) {
			gl.glPushMatrix();
			gl.glTranslated( 0.0, 0.0, length );
			flowUnitColor.draw( gl );
			glu.gluDisk( quadObj, thickness, calculatedFlowThickness, qualityPreset.edgeSlices, 1 );
			gl.glPopMatrix();
		}
	}

	// WARNING: Debug-Out is disabled due to the fact that the method getEdge() is not available in GLEdgeControl (MVC design!)
	/**
	 * Draws the flow on the edge.
	 * @gl the {@code OpenGL} context
	 */
	private void drawFlow( GL gl ) {
		control.stepUpdate();

		flowColor.draw( gl );
		flowOnEdge = control.getFlowOnEdge();

		int offset = transitTime;
		int step = (int) Math.floor( control.getTime() );

		int pointer = offset + step;

		gl.glPushMatrix();
		Vector3 b = new Vector3( 0, 0, 1 );
		Vector3 a = control.getDifferenceVectorInOpenGlScaling();
		Vector3 axis = control.getRotationAxis( a, b );
		gl.glRotated( control.getAngleBetween( a, b ), axis.x, axis.y, axis.z );

		double unit = length / transitTime;
		double delta = control.getDeltaStep() * unit;
		if( pointer < flowOnEdge.size() ) {
			if( flowOnEdge.get( pointer ) != 0 ) {
				//if( DebugFlags.FLOWWRONG_LONG ) {
					//System.out.println( "(" + control.getEdge().start() + " " + control.getEdge().end() + ") " );
				//}
				drawPieceOfFlow( gl, delta * factor, flowThickness + flowOnEdge.get( pointer ) * flowThicknessOfOneCapacityStep, true, false );
			}
			if (pointer > 0) pointer--;
			gl.glTranslated( 0.0, 0.0, delta );
			for( int i = 1; i < transitTime; i++ ) {
				if( flowOnEdge.get( pointer ) != 0 ) {
					//if( DebugFlags.FLOWWRONG_LONG ) {
						//System.out.println( "(" + control.getEdge().start() + " " + control.getEdge().end() + ") " );
					//}
					drawPieceOfFlow( gl, unit * factor, flowThickness + flowOnEdge.get( pointer ) * flowThicknessOfOneCapacityStep, true, true );
				}
				gl.glTranslated( 0.0, 0.0, unit );
				pointer--;
			}
			if( flowOnEdge.get( pointer ) != 0 ) {
				//if( DebugFlags.FLOWWRONG_LONG ) {
					//System.out.println( "(" + control.getEdge().start() + " " + control.getEdge().end() + ") " );
				//}
				drawPieceOfFlow( gl, (unit - delta) * factor, flowThickness + flowOnEdge.get( pointer ) * flowThicknessOfOneCapacityStep, false, true );
			}
		}
		gl.glPopMatrix();
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

		glu.gluCylinder( quadObj, thickness, thickness, length, qualityPreset.edgeSlices, 1 );
		//gl.glDisable( GL.GL_BLEND );
		gl.glPopMatrix();
	}

	/**
	 * Draws the flow on the edge. The edge is already drawn using the {@link #performStaticDrawing(javax.media.opengl.GLAutoDrawable)} method.
	 * @param drawable the {@code OpenGL} drawable object
	 */
	@Override
	public void performDrawing( GL gl ) {
		drawFlow( gl );
	}

	/**
	 * Draws the static structure of the edge that means the edge, if it is the first one
	 * of the two edges. The flow is not painted.
	 * {@see GLEdgeControl#isFirstEdge()}
	 * @param drawable the {@code OpenGL} drawable object
	 */
	@Override
	public void performStaticDrawing( GL gl ) {
		beginDraw( gl );
		edgeColor.draw( gl );
		if( control.isFirstEdge() ) {
			drawStaticStructure( gl );
		}
		endDraw( gl );
	}
	
	@Override
	public void update() {
		transitTime = control.getTransitTime();
		length = control.get3DLength() * VisualizationConstants.SIZE_MULTIPLICATOR;
		capacity = control.getCapacity();
		maxCapacity = control.getMaxCapacity();
		edgeColor = VisualizationOptionManager.getEdgeColor();
		flowColor = VisualizationOptionManager.getFlowUnitColor();
		flowUnitColor = VisualizationOptionManager.getFlowUnitEndColor();
		maxFlowRate = control.getMaxFlowRate();
		minFlowThickness = 3 * VisualizationConstants.SIZE_MULTIPLICATOR * 1.7;
		maxFlowThickness = 10 * VisualizationConstants.SIZE_MULTIPLICATOR * 1.7;
		flowThicknessOfOneCapacityStep = (maxFlowThickness - minFlowThickness) / maxFlowRate;
	}
}
