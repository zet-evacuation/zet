/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
 * GLFlowEdge.java
 */

package gui.visualization.draw.graph;

import gui.visualization.QualityPreset;
import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.graph.GLFlowEdgeControl;
import java.util.ArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import org.zetool.opengl.drawingutils.GLColor;
import org.zetool.math.vectormath.Vector3;

public class GLFlowEdge extends GLEdge {
	GLFlowEdgeControl fcontrol;

	/** The edgeLength of single flow units. If set to 1 no single units are displayed. */
	static double factor = 0.7;
	static int edgeDisplayMode = GLU.GLU_FILL;
	static GLColor flowColor;
	static GLColor flowUnitColor;
	static double flowThickness = 7*0.1; /* * 1.5 */ /* 7 */ //* GLFlowGraphControl.sizeMultiplicator; //// factor of 1.5 used for test evacuation report
	static double minFlowThickness = 10*0.1; /* 10 */ // original 3

	static double maxFlowThickness = 10*0.1;
	static double flowThicknessOfOneCapacityStep;
	int maxFlowRate;
	/* Telling the flow on the edge for each time step. */
	private ArrayList<Integer> flowOnEdge;
	/* The capacity of the edge */
	double capacity;
	/** The transit time of the edge */
	int transitTime;
	/* not used */
	//double maxCapacity;
	// TODO read quality from VisualOptionManager
	//private static QualityPreset qualityPreset = VisualizationOptionManager.getQualityPreset();
	private static QualityPreset qualityPreset = QualityPreset.MediumQuality;

	public GLFlowEdge( GLFlowEdgeControl control ) {
		super( control );
		fcontrol = control;
		maxFlowRate = control.getMaxFlowRate();
		update();
		glu.gluQuadricDrawStyle( quadObj, edgeDisplayMode );		// Fill, points, lines
	}

	/**
	 * Draws a piece of flow. The start and end of the piece can be drawn or not.
	 * If they are drawn their color is flowUnitColor.
	 * @param edgeLength edgeLength of the flow piece to draw.
	 * @param diskAtStart tells whether there shall be a disk at the start of the piece.
	 * @param diskAtEnd tells whether there shall be a disk at the end of the piece.
	 */
	private void drawPieceOfFlowComplete( GL gl, double length, double calculatedFlowThickness, boolean diskAtStart, boolean diskAtEnd ) {
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

	private void drawPieceOfFlowIndividual( GL gl, double radius ) {
		flowColor.draw( gl );
		glu.gluSphere( quadObj, radius, qualityPreset.nodeSlices, qualityPreset.nodeStacks );
	}

	/**
	 * <p>Draws the flow on the edge. An edge is divided into parts with equal length,
	 * the number of parts equals the (integral) length of the edge. The flow
	 * is displayed in different widths, if the flow rate is larger. Flow parts
	 * move along the edge with equal speed.</p>
	 * <p>Note that flow particles only use the same speed, if the length of the
	 * edges is correct also in the graphical representation. The speed of flow
	 * is computed based on the network flow model length of the edge, so the
	 * positions of the start- and end node for the edge should fit to the model
	 * to get a smooth and homogeneous flow.</p>
	 * @gl the {@code OpenGL} context
	 */
	private void drawFlow( GL gl ) {
		fcontrol.stepUpdate();

		flowColor.draw( gl );
		flowOnEdge = fcontrol.getFlowOnEdge();

		// compute the correct position in the flow-value-array for each time step.
		// the position consists of an offset by transitTime and the current time
		// of the flow
		int pointer = transitTime + (int) Math.floor( fcontrol.getTime() );

		gl.glPushMatrix();

		// Rotate the coordinates in a way, that the edge lies on the z-axis with
		// the start point in the origin. This is needed as flow particles are
		// drawn as GLU cylinder and these are automatically drawn along the z-axis.
		final Vector3 b = new Vector3( 0, 0, 1 );
		final Vector3 a = fcontrol.getDifferenceVectorInOpenGlScaling();
		final Vector3 axis = fcontrol.getRotationAxis( a, b );
		gl.glRotated( fcontrol.getAngleBetween( a, b ), axis.x, axis.y, axis.z );

		// the length of a flow unit (without spacing factor)
		double flowUnitLength = edgeLength / transitTime;
		// the start position of the first edge. delta is the fraction of the current step that is already over
		double delta = fcontrol.getDeltaStep() * flowUnitLength;
		if( pointer < flowOnEdge.size() ) {
			// the real visible length of an edge (spaces are taken into account)
			final double visibleLen = (flowUnitLength * factor);
			// the correct start position of the first part of flow (including spaces)
			final double start = delta - (flowUnitLength - visibleLen) * 0.5;

			// the position where the visible part of the flow unit begins...
			// should not be drawn if it lies before the edge (< 0)
			final double translateDiff = start - visibleLen;
			// draw the first flow element, if flow is running into the edge at the moment
			if( flowOnEdge.get( pointer ) != 0 ) {
				// if positive, translate to the appropriate position
				if( translateDiff > 0 )
					gl.glTranslated( 0, 0, translateDiff );
				// draw only a delta-part if translate was negative (flow would start before the edge start),
				// otherwise draw the complete length (visibleLen)
				if( start > 0 )
					drawPieceOfFlowComplete( gl, translateDiff < 0 ? visibleLen + translateDiff : visibleLen, flowThickness + flowOnEdge.get( pointer ) * flowThicknessOfOneCapacityStep, true, false );
				// move further on the z-axis (depending if a translation already has been done)
				gl.glTranslated( 0, 0, translateDiff > 0 ? flowUnitLength-translateDiff : flowUnitLength );
			} else
				// move further on the z-axis
				gl.glTranslated( 0, 0, flowUnitLength );

			// if there is more flow on the edge go one step further
			if( pointer > 0 )
				pointer--;

			// draw all other elements (except for the last one), if flow is on the edge in that moment
			for( int i = 1; i < transitTime; i++ ) {
				if( flowOnEdge.get( pointer ) != 0 ) {
					// translate to the correct starting position. can be negative here,
					// thats no problem as we are in the middle of the edge!
					gl.glTranslated( 0, 0, translateDiff );
					// draw a complete flow element with lengh visibleLen
					drawPieceOfFlowComplete( gl, visibleLen, flowThickness + flowOnEdge.get( pointer ) * flowThicknessOfOneCapacityStep, true, true );
					gl.glTranslated( 0.0, 0.0, flowUnitLength-translateDiff );
				} else
					gl.glTranslated( 0, 0, flowUnitLength );
				// move on the pointer
				pointer--;
			}

			// draw the last part of flow, if it exists
			if( flowOnEdge.get( pointer ) != 0 )
				// do not draw anything if translation is positive (would lie behind the edge)
				if( translateDiff < 0 ) {
					// translate to the correct starting position. can be negative here,
					// thats no problem as we are at the end of the edge!
					gl.glTranslated( 0, 0, translateDiff );
					// if start is negative, enough space for the complete visibleLen is left
					// on the edge, if it is positive, it should be reduced
					drawPieceOfFlowComplete( gl, start < 0 ? visibleLen : visibleLen - start, flowThickness + flowOnEdge.get( pointer ) * flowThicknessOfOneCapacityStep, false, true );
				}
		}
		gl.glPopMatrix();
	}

	private void drawFlow2( GL gl ) {
		fcontrol.stepUpdate();

		flowColor.draw( gl );
		flowOnEdge = fcontrol.getFlowOnEdge();

		// compute the correct position in the flow-value-array for each time step.
		// the position consists of an offset by transitTime and the current time
		// of the flow
		int pointer = transitTime + (int) Math.floor( fcontrol.getTime() );

		gl.glPushMatrix();

		// Rotate the coordinates in a way, that the edge lies on the z-axis with
		// the start point in the origin. This is needed as flow particles are
		// drawn as GLU cylinder and these are automatically drawn along the z-axis.
		final Vector3 b = new Vector3( 0, 0, 1 );
		final Vector3 a = fcontrol.getDifferenceVectorInOpenGlScaling();
		final Vector3 axis = fcontrol.getRotationAxis( a, b );
		gl.glRotated( fcontrol.getAngleBetween( a, b ), axis.x, axis.y, axis.z );

		// the length of a flow unit (without spacing factor)
		double flowUnitLength = edgeLength / transitTime;
		// the start position of the first edge. delta is the fraction of the current step that is already over
		double delta = fcontrol.getDeltaStep() * flowUnitLength;
		if( pointer < flowOnEdge.size() ) {
			// draw the first flow element, if flow is running into the edge at the moment
			//gl.glTranslated( 0, 0, flowUnitLength );
			gl.glTranslated( 0, 0, delta );
			if( flowOnEdge.get( pointer ) != 0 ) {
				drawPieceOfFlowIndividual( gl, flowThickness + flowOnEdge.get( pointer ) * flowThicknessOfOneCapacityStep );
			} else
				// move further on the z-axis
				;//
			gl.glTranslated( 0, 0, flowUnitLength );

			// if there is more flow on the edge go one step further
			if( pointer > 0 )
				--pointer;

			// draw all other elements (except for the last one), if flow is on the edge in that moment
			for( int i = 1; i < transitTime; i++ ) {
				if( flowOnEdge.get( pointer ) != 0 ) {
					// translate to the correct starting position. can be negative here,
					// thats no problem as we are in the middle of the edge!
					//gl.glTranslated( 0, 0, delta );
					// draw a complete flow element with lengh visibleLen
					drawPieceOfFlowIndividual( gl, flowThickness + flowOnEdge.get( pointer ) * flowThicknessOfOneCapacityStep );
					//gl.glTranslated( 0.0, 0.0, -delta  );
					gl.glTranslated( 0, 0, flowUnitLength );
				} else
					gl.glTranslated( 0, 0, flowUnitLength );
				// move on the pointer
				pointer--;
			}

			// draw the last part of flow, if it exists
	//		if( flowOnEdge.get( pointer ) != 0 )
				// do not draw anything if translation is positive (would lie behind the edge)
	//				gl.glTranslated( 0, 0, delta );
					// if start is negative, enough space for the complete visibleLen is left
					// on the edge, if it is positive, it should be reduced
	//				drawPieceOfFlowIndividual( gl, flowThickness + flowOnEdge.get( pointer ) * flowThicknessOfOneCapacityStep );
				}
		gl.glPopMatrix();
		}

	/**
	 * Draws the flow on the edge. The edge is already drawn using the {@link #performStaticDrawing(javax.media.opengl.GL)} method.
	 * @param gl the {@code OpenGL} drawable object
	 */
	@Override
	public void performDrawing( GL gl ) {
    if( fcontrol.edge.id() == 0 )
      return;
		drawFlow( gl );
	}

	@Override
	public void update() {
		super.update();
		GLFlowEdgeControl control = (GLFlowEdgeControl)super.control;
		transitTime = control.getTransitTime();
		capacity = control.getCapacity();
		//maxCapacity = control.getMaxCapacity();
		flowColor = VisualizationOptionManager.getFlowUnitColor();
		flowUnitColor = VisualizationOptionManager.getFlowUnitEndColor();
		maxFlowRate = control.getMaxFlowRate();
		minFlowThickness = 3*0.01;// * GLFlowGraphControl.sizeMultiplicator * 1.7;
		maxFlowThickness = 10*0.01;// * GLFlowGraphControl.sizeMultiplicator * 1.7;
		flowThicknessOfOneCapacityStep = (maxFlowThickness - minFlowThickness) / maxFlowRate;
	}
}
