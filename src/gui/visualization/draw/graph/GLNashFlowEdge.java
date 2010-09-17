/**
 * GLNashFlowEdge.java
 * Created: 30.08.2010, 16:26:12
 */
package gui.visualization.draw.graph;

import opengl.drawingutils.RainbowGradient;
import de.tu_berlin.math.coga.math.Conversion;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import de.tu_berlin.math.coga.zet.viewer.FlowData;
import gui.visualization.control.graph.GLEdgeControl;
import gui.visualization.control.graph.GLNashFlowEdgeControl;
import javax.media.opengl.GL;
import opengl.drawingutils.GLColor;

/**
 * Draws an edge and a Nash flow. The Nash flow is stored in values for the
 * different iterations of the algorithm.
 * @author Jan-Philipp Kappmeier
 */
public class GLNashFlowEdge extends GLEdge {
	/** The control class for the Nash edge. */
	GLNashFlowEdgeControl ncontrol;
	/** An acceleration factor. */
	final static double acceleration = 1;
	/** The current time during visualization. */
	double currentTime;
	/** The capacity of the edge. */
	private double capacity;
	/** The transit time of the edge. */
	private double transitTime;
	/** The relative exit position on the edge. */
	double corridorExitPosition = 0.8;
	/** The relative start position on the edge. */
	double corridorStartPosition = 0.2;
	/** The capacity of the corridor. */
	double corridorCapacity = 5;
	/** The width of the corridor wall. Not used */
	double corridorWallWidth = 0;
	/** A scale factor. */
	final static double scale = 0.6;
	/** The color for the wall. */
	final static GLColor wallColor = new GLColor( 139, 69, 19, 0.1 );
	/** The rainbow colors used for the color effect on the edges. */
	RainbowGradient rainbowGradient;
	/** The axis used to rotate to display the edges. */
	final Vector3 rotateAxis;
	/** The angle used to rotate to display the edges. */
	final double rotateAngle;

	public GLNashFlowEdge( GLEdgeControl control ) {
		super( control );
		ncontrol = (GLNashFlowEdgeControl) control;
		edgeColor = GLColor.gray;
		final Vector3 b = new Vector3( 0, 0, 1 );
		final Vector3 a = control.getDifferenceVectorInOpenGlScaling();
		rotateAxis = control.getRotationAxis( a, b );
		rotateAngle = control.getAngleBetween( a, b );
		update();
	}

	/**
	 * Draws the flow on the flow, the edge and the corridor.
	 * @param gl the {@code OpenGL} drawable object
	 */
	@Override
	public void performDrawing( GL gl ) {
		currentTime = Conversion.nanoSecondsToSec * ncontrol.getTimeSinceStart() * acceleration;

		for( FlowData flowData : ncontrol.getNashFlowEdgeData() )
			displayFlow( gl, flowData );

		gl.glEnable( GL.GL_CULL_FACE );
		displayEdge( gl );
		gl.glDisable( GL.GL_CULL_FACE );

		displayCorridor( gl );
	}

	/**
	 * Draws the edge. The edge is a solid, semi transparent cylinder. At a first
	 * step the coordinate system is rotated such that the edge goes in {@code z}
	 * direction. Then the edge is drawn.
	 * @param gl the graphics object
	 */
	private void displayEdge( GL gl ) {
		gl.glPushMatrix();
		edgeColor.draw( gl );
		gl.glRotated( rotateAngle, rotateAxis.x, rotateAxis.y, rotateAxis.z );
		final double eps = 0.999;
		gl.glEnable( gl.GL_BLEND );
		gl.glBlendFunc( gl.GL_ONE_MINUS_DST_COLOR, gl.GL_ONE );
		glu.gluCylinder( quadObj, scale * capacity * 0.5 * eps, scale * capacity * 0.5 * eps, control.get3DLength(), 16, 1 );
		gl.glDisable( GL.GL_BLEND );
		gl.glPopMatrix();
	}

	/**
	 * Displays the corridor. The corridor is a transparent cylinder. The back
	 * of the cylinder is open, the front of the cylinder is closed but contains
	 * a hole of the size of the edge as an exit.
	 * @param gl the graphics object
	 */
	private void displayCorridor( GL gl ) {
		gl.glPushMatrix();
		wallColor.draw( gl );
		final double len = control.get3DLength();
		gl.glRotated( rotateAngle, rotateAxis.x, rotateAxis.y, rotateAxis.z );
		final double eps = 1.001;
		gl.glEnable( gl.GL_BLEND );
		gl.glBlendFunc( gl.GL_SRC_ALPHA, gl.GL_ONE );
		gl.glTranslated( 0, 0, corridorStartPosition * len );
		double width = eps * scale * corridorCapacity * 0.5;
		double drawnLength = corridorExitPosition * len - corridorStartPosition * len;
		glu.gluCylinder( quadObj, width, width, drawnLength * eps, 32, 1 );
		gl.glTranslated( 0, 0, drawnLength * eps );
		glu.gluDisk( quadObj, scale * capacity * 0.5 * eps, width, 32, 1 );
		gl.glDisable( GL.GL_BLEND );
		gl.glPopMatrix();
		gl.glLineWidth( 3 );
	}

	/**
	 * An auxiliary method that gives the length of the queue depending of the flow
	 * data and the current time of visualization.
	 * @param flowData the flow data which is currently drawn
	 * @return the length of the queue
	 */
	private double getPhysicalQueueLength( FlowData flowData ) {
		if( currentTime >= flowData.firstEnterExit && currentTime <= flowData.lastEnterExit )
			return (flowData.queueLengthForFirst + (currentTime - flowData.firstEnterExit) * (flowData.inflow - capacity) / corridorCapacity) / transitTime;
		if( currentTime >= flowData.firstEnterExit && currentTime <= flowData.lastLeaveExit )
			return (flowData.queueLengthForLast - (currentTime - flowData.lastEnterExit) * capacity / corridorCapacity) / transitTime;
		return flowData.queueLengthForFirst / transitTime;
	}

	/**
	 * <p>Draws the flow (based on the flow data). That means, the flow on one
	 * specific edge computed in one iteration of the Nash flow algorithm.</p>
	 * <p>The flow consists of three phases.
	 * <ul>
	 * <li>The first phase consists of the flow
	 * entering the edge up to the point where it queues up or leaves the exit.</li>
	 * <li>The second phase consists of the flow in the queue.</li>
	 * <li>The third path is the flow coming out of the exit of the corridor.</li>
	 * </ul>
	 * There is one special case: when the inflow rate is smaller than the
	 * capacity and the flow never queues up and has not to wait, than it is
	 * simply drawn as one single piece of flow. </p>
	 * @param gl the graphics object
	 * @param flowData the flow data displayed
	 */
	private void displayFlow( GL gl, FlowData flowData ) {
		if( flowData.waittimePositive || !flowData.capGreaterThanInflow ) {	// the hard part: includes wait times and a visible queue
			final double physicalQueueLength = getPhysicalQueueLength( flowData );
			final double shownQueueLength = physicalQueueLength * corridorExitPosition / (physicalQueueLength + corridorExitPosition);

			// precompute the color value at the exit in case it is needed for the color gradient at the crossing of queue and inflow
			final double startColorValuePhase3 = flowData.globalStart + ((currentTime - flowData.firstLeaveExit) / (flowData.lastLeaveExit - flowData.firstLeaveExit)) * flowData.colorDifference;

			// compute the colors for phase 1 (inflowing part of flow)
			double startColorValuePhase1 = flowData.globalEnd;
			double endColorValuePhase1 = flowData.globalStart;
			if( currentTime >= flowData.startTime && currentTime <= flowData.lastEnterExit ) {
				if( currentTime > flowData.firstEnterExit && shownQueueLength > 0 )
					endColorValuePhase1 = -1;
				else	// here the queue length is 0
					endColorValuePhase1 = currentTime > flowData.firstLeaveExit ? startColorValuePhase3 : flowData.globalStart;
				startColorValuePhase1 = currentTime > flowData.endTime ? flowData.globalEnd : flowData.globalStart + ((currentTime - flowData.startTime) / (flowData.endTime - flowData.startTime)) * flowData.colorDifference;
			}

			// compute the colors for phase 2 (queue part of flow)
			double startColorValuePhase2 = flowData.globalEnd;
			double endColorValuePhase2 = flowData.globalStart;
			if( currentTime >= flowData.firstEnterExit && currentTime <= flowData.lastLeaveExit ) {
				endColorValuePhase2 = currentTime >= flowData.firstLeaveExit ? startColorValuePhase3 : flowData.globalStart;
				startColorValuePhase2 = currentTime <= flowData.lastEnterExit ? -1 : flowData.globalEnd;
			}

			// in case we have both, phase 1 and phase 2, they meet at some point. compute the color at that specific point.
			if( startColorValuePhase2 == -1 || endColorValuePhase1 == -1 ) {
				final double numberUpToNow = flowData.inflow * (Math.min( currentTime, flowData.endTime ) - flowData.startTime);
				final double numberPart3 = capacity * (currentTime - flowData.firstAtHead);
				final double numberPart2 = flowData.inflow * (Math.min( flowData.lastEnterExit, currentTime ) - flowData.firstEnterExit);
				final double numberPart1 = Math.max( numberUpToNow - numberPart3 - numberPart2, 0 );
				final double f = numberPart1 / (numberPart1 + numberPart2);
				if( startColorValuePhase2 == -1 )
					startColorValuePhase2 = endColorValuePhase2 + (1 - f) * (startColorValuePhase1 - endColorValuePhase2);
				if( endColorValuePhase1 == -1 )
					endColorValuePhase1 = startColorValuePhase1 - f * (startColorValuePhase1 - endColorValuePhase2);
			}
			// draw the actual flow forall three phases
			drawPhase1( gl, flowData, shownQueueLength, startColorValuePhase1, endColorValuePhase1 );
			drawPhase2( gl, flowData, shownQueueLength, startColorValuePhase2, endColorValuePhase2 );
			if( currentTime >= flowData.firstLeaveExit && currentTime <= flowData.lastAtHead )	// the third phase. quite easy.
				draw( gl, flowData, currentTime, flowData.firstLeaveExit, flowData.lastAtHead, flowData.lastLeaveExit, flowData.firstAtHead, corridorExitPosition );
		} else	// the easy part: no wait time and no queue: the flow can directly flow through the edge
			draw( gl, flowData, currentTime, flowData.startTime, flowData.lastAtHead, flowData.endTime, flowData.firstAtHead, 0 );
	}

	/**
	 * Displays the first phase. The colors used at the start and end point of
	 * the phase have to be computed first as they may need information about the
	 * other phases colors. The positions of the flow are computed based on the
	 * current time and the times where flow queues up from the flow data.
	 * @param gl the graphics object
	 * @param flowData the flow data for the current iteration
	 * @param shownQueueLength the length of the queue at this time for this iteration
	 * @param startColorValue the color at the start of the flow (this is where it comes out of the node, which in meanings of time is the end of the flow!)
	 * @param endColorValue the color at the end of the flow (this is the piece of flow furthest away!)
	 */
	private void drawPhase1( GL gl, FlowData flowData, double shownQueueLength, double startColorValue, double endColorValue ) {
		double positionStart = 0; // start position is the last piece of flow that has entered the edge up to now
		if( currentTime >= flowData.startTime && currentTime <= flowData.lastEnterExit ) {
			if( currentTime >= flowData.endTime ) {
				final double shownQueueLengthForLast = flowData.queueLengthForLast / transitTime * corridorExitPosition / (flowData.queueLengthForLast / transitTime + corridorExitPosition);
				positionStart = (currentTime - flowData.endTime) / (flowData.lastEnterExit - flowData.endTime) * (corridorExitPosition - shownQueueLengthForLast);
			}
			final double positionEnd = currentTime <= flowData.firstEnterExit ? (currentTime - flowData.startTime) / (flowData.firstEnterExit - flowData.startTime) * (corridorExitPosition - shownQueueLength) : corridorExitPosition - shownQueueLength;
			drawFlow( gl, startColorValue, endColorValue, control.get3DLength() * (positionEnd - positionStart), control.get3DLength() * (positionStart), flowData.inflow );
		}
	}

	/**
	 * Draws the second phase of the flow, that is the queue (if it exists). The
	 * colors used at the start and end point of the phase have to be computed
	 * first as they may need information about the other phases colors. The
	 * positions of the flow are computed based on the current time and the times
	 * where flow queues up from the flow data.
	 * @param gl the graphics object
	 * @param flowData the flow data for the current iteration
	 * @param shownQueueLength the length of the queue at this time for this iteration
	 * @param startColorValue the color at the start of the flow (this is where it comes out of the node, which in meanings of time is the end of the flow!)
	 * @param endColorValue the color at the end of the flow (this is the piece of flow furthest away!)
	 */
	private void drawPhase2( GL gl, FlowData flowData, double shownQueueLength, double startColorValue, double endColorValue ) {
		if( currentTime >= flowData.firstEnterExit && currentTime <= flowData.lastLeaveExit ) {
			double positionStart = shownQueueLength;
			if( currentTime <= flowData.firstLeaveExit ) {
				final double physicalRemainingQueueLength = (flowData.queueLengthForFirst - (currentTime - flowData.firstEnterExit) * capacity / corridorCapacity) / transitTime;
				final double shownRemainingQueueLength = physicalRemainingQueueLength * corridorExitPosition / (physicalRemainingQueueLength + corridorExitPosition);
				positionStart -= shownRemainingQueueLength;
			}
			drawFlow( gl, startColorValue, endColorValue, control.get3DLength() * positionStart, control.get3DLength() * (corridorExitPosition - shownQueueLength), corridorCapacity );
		}
	}

	/**
	 * Draws a "simple" flow. That means, a flow that starts at a point at a
	 * specific time and ends at an end point at another time. In between the
	 * colors scale from the global start value up to the global end value.
	 * @param gl the graphics object
	 * @param flowData the flow data for the current iteration
	 * @param current the current time
	 * @param firstVisible the first time when flow is visible at the start position
	 * @param lastVisible the time when the last piece of flows has reached the destination point
	 * @param lastInflow the last time when flow comes out of the start position
	 * @param firstArrival the first time when flow reaches destination point
	 * @param relativeStart the (relative) start position on the edge.
	 */
	private void draw( GL gl, FlowData flowData, double current, double firstVisible, double lastVisible, double lastInflow, double firstArrival, double relativeStart ) {
		if( currentTime >= firstVisible && currentTime <= lastVisible ) {
			double startPos;
			double endPos;
			double endColorValue;
			double startColorValue;

			// first: computation of the start color and position
			if( current >= lastInflow ) {
				startPos = ((current - lastInflow) / (lastVisible - lastInflow)) * (1 - relativeStart) + relativeStart;
				startColorValue = flowData.globalEnd;
			} else {
				startPos = relativeStart;
				startColorValue = flowData.globalStart + (current - firstVisible) / (lastInflow - firstVisible) * flowData.colorDifference;
			}

			// second: computation of the end color and position
			if( current <= firstArrival ) {
				endPos = ((current - firstVisible) / (firstArrival - firstVisible)) * (1 - relativeStart) + relativeStart;
				endColorValue = flowData.globalStart;
			} else {
				endPos = 1;
				endColorValue = flowData.globalStart + (current - firstArrival) / (lastInflow - firstVisible) * flowData.colorDifference;
			}

			drawFlow( gl, startColorValue, endColorValue, control.get3DLength() * (endPos - startPos), control.get3DLength() * startPos, flowData.inflow );
		}
	}

	/**
	 * Draws a cylinder representing a flow. The cylinder will be colored with
	 * a color gradient, whose start and end values have to be submitted.
	 * @param gl the graphics object
	 * @param baseColorValue the color value at the base of the cylinder (within the range of the rainbow gradient)
	 * @param topColorValuethe color value at the top of the cylinder (within the range of the rainbow gradient)
	 * @param length the length of the cylinder (piece of flow)
	 * @param offset the offset, where to draw the base of the cylinder
	 * @param flowValue the value of the flow. used to compute the thickness
	 */
	private void drawFlow( GL gl, double baseColorValue, double topColorValue, double length, double offset, double flowValue ) {
		if( length <= 0 )
			return;
		gl.glPushMatrix();
		gl.glRotated( rotateAngle, rotateAxis.x, rotateAxis.y, rotateAxis.z );
		final double width = scale * flowValue * 0.5;
		gl.glTranslated( 0, 0, offset );
		rainbowGradient.drawCylinder( gl, width, length, baseColorValue, topColorValue );
		rainbowGradient.getColorForTime( baseColorValue ).draw( gl );
		glu.gluDisk( quadObj, 0, width, 32, 1 );
		gl.glTranslated( 0, 0, length );
		rainbowGradient.getColorForTime( topColorValue ).draw( gl );
		glu.gluDisk( quadObj, 0, width, 32, 1 );
		gl.glPopMatrix();
	}

	/**
	 * Updates the values used to draw the edge. The values are gathered from the
	 * associated control class.
	 */
	@Override
	public void update() {
		capacity = ncontrol.getNashFlowEdgeData().getCapacity();
		transitTime = ncontrol.getNashFlowEdgeData().getTransitTime();
		corridorCapacity = ncontrol.getNashFlowEdgeData().getCorridorCapacity();
		corridorExitPosition = ncontrol.getNashFlowEdgeData().getExitPosition();
		corridorStartPosition = 1 - corridorExitPosition;
		rainbowGradient = ncontrol.getRainbowGradient();
	}
}
