/**
 * GLNashFlowEdge.java
 * Created: 30.08.2010, 16:26:12
 */
package gui.visualization.draw.graph;

import de.tu_berlin.math.coga.math.Conversion;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import de.tu_berlin.math.coga.zet.viewer.FlowData;
import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.graph.GLEdgeControl;
import gui.visualization.control.graph.GLNashFlowEdgeControl;
import java.awt.Color;
import javax.media.opengl.GL;
import opengl.drawingutils.GLColor;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLNashFlowEdge extends GLEdge {
	GLNashFlowEdgeControl ncontrol;
	final static double exitPos = 0.8;
	double corStartPos = 0.2;
	/** An accelerating factor. */
	final static double acceleration = 1;
	/** The width of one flow unit. */
	double fu = 0.15;
	/** The capacity of the corridor. */
	double corCap = 5;
	double corWallWidth = 0;
	double distWallFromEdge = corCap;
	private double cap;
	private double transitTime;
	double currentTime;
	double scale = 4;

	public GLNashFlowEdge( GLEdgeControl control ) {
		super( control );
		ncontrol = (GLNashFlowEdgeControl) control;
		edgeColor = VisualizationOptionManager.getEdgeColor();
		update();
	}

	/**
	 * Draws the flow on the edge. The edge is already drawn using the {@link #performStaticDrawing(javax.media.opengl.GLAutoDrawable)} method.
	 * @param gl the {@code OpenGL} drawable object
	 */
	@Override
	public void performDrawing( GL gl ) {
		currentTime = Conversion.nanoSecondsToSec * ncontrol.getTimeSinceStart() * acceleration;

		displayEdge( gl );

		for( FlowData flowData : ncontrol.getNashFlowEdgeData() )
			displayFlow( flowData, gl );

		displayCorridor( gl );
	}

	@Override
	public void performStaticDrawing( GL gl ) {
		// nothing
	}

	private void displayEdge( GL gl ) {
		// Draw the edge
		gl.glPushMatrix();

		edgeColor.draw( gl );

		Vector3 b = new Vector3( 0, 0, 1 );
		Vector3 a = control.getDifferenceVectorInOpenGlScaling();
		Vector3 axis = control.getRotationAxis( a, b );
		gl.glRotated( control.getAngleBetween( a, b ), axis.x, axis.y, axis.z );
		//gl.glRotated( 90, 0, 1, 0 );

		final double eps = 0.999;
		gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_LINE );
		glu.gluCylinder( quadObj, scale * cap * fu * 0.5 * eps, scale * cap * fu * 0.5 * eps, control.get3DLength(), 16, 1 );
		gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_FILL );

		gl.glPopMatrix();
	}

	private void displayCorridor( GL gl ) {
		GLColor wallColor = new GLColor( 139, 69, 19 );
		wallColor.draw( gl );
		final double len = control.get3DLength();

		gl.glPushMatrix();

		Vector3 b = new Vector3( 0, 0, 1 );
		Vector3 a = control.getDifferenceVectorInOpenGlScaling();
		Vector3 axis = control.getRotationAxis( a, b );
		gl.glRotated( control.getAngleBetween( a, b ), axis.x, axis.y, axis.z );
		//gl.glRotated( 90, 0, 1, 0 );

		final double eps = 1.001;
		gl.glEnable( gl.GL_BLEND );
		gl.glBlendFunc( gl.GL_SRC_ALPHA, gl.GL_ONE );

		gl.glTranslated( 0, 0, corStartPos * len );
		//gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_LINE );
		double width = eps * scale * distWallFromEdge*fu*0.5;
		double drawnLength = exitPos * len - corStartPos * len;
		glu.gluCylinder( quadObj, width, width, drawnLength * eps, 16, 1 );
		//gl.glPolygonMode( GL.GL_FRONT, GL.GL_FILL );

		//glu.gluDisk( quadObj, 0, width, 16, 1 );
		gl.glTranslated( 0, 0, drawnLength * eps );
		glu.gluDisk( quadObj, scale * cap * fu * 0.5 * eps, width, 16, 1 );


		gl.glDisable( GL.GL_BLEND );
		gl.glPopMatrix();

		gl.glLineWidth( 3 );

		// draw the corridors
//		gl.glBegin( GL.GL_LINE_STRIP );
//		GLVector x1 = new GLVector( corStartPos * len, distWallFromEdge, 0 );
//		GLVector y1 = new GLVector( exitPos * len + 0.5 * corWallWidth, distWallFromEdge, 0 );
//		GLVector z1 = new GLVector( exitPos * len + 0.5 * corWallWidth, cap * fu * 0.5, 0 );
//		x1.draw( gl );
//		y1.draw( gl );
//		z1.draw( gl );
//		gl.glEnd();

//		gl.glBegin( GL.GL_LINE_STRIP );
//		GLVector x2 = new GLVector( corStartPos * len, -distWallFromEdge, 0 );
//		GLVector y2 = new GLVector( exitPos * len + 0.5 * corWallWidth, -distWallFromEdge, 0 );
//		GLVector z2 = new GLVector( exitPos * len + 0.5 * corWallWidth, -cap * fu * 0.5, 0 );
//		x2.draw( gl );
//		y2.draw( gl );
//		z2.draw( gl );
//		gl.glEnd();
//		gl.glLineWidth( 1 );
	}

	/**
	 * A helping method that gives the length of the queue depending of the flow
	 * data and the current time of visualization.
	 * @param flowData the flow data which is currently drawn
	 * @return the length of the queue
	 */
	private double getPhysicalQueueLength( FlowData flowData ) {
		if( currentTime >= flowData.firstAfterEnterExit && currentTime <= flowData.lastBeforeEnterExit )
			return (flowData.queueLengthForFirst + (currentTime - flowData.firstEnterExit) * (flowData.inflow - cap) / corCap) / transitTime;
		if( currentTime >= flowData.lastAfterEnterExit && currentTime <= flowData.lastBeforeLeaveExit )
			return (flowData.queueLengthForLast - (currentTime - flowData.lastEnterExit) * cap / corCap) / transitTime;
		return flowData.queueLengthForFirst / transitTime;
	}

	private void displayFlow( FlowData flowData, GL gl ) {
		double positionFront = 1;
		double positionEnd = 0;

		if( flowData.waittimePositive || !flowData.capGreaterThanInflow ) {
			final double physicalQueueLength = getPhysicalQueueLength( flowData );
			final double shownQueueLength = physicalQueueLength * exitPos / (physicalQueueLength + exitPos);

			// Zeichnen des Flussstückes der ungehindert in die Kante fließen kann
			if( currentTime >= flowData.firstAfterTail && currentTime <= flowData.lastBeforeEnterExit ) {
				if( currentTime >= flowData.lastAfterTail ) {
					final double shownQueueLengthForLast = flowData.queueLengthForLast / transitTime * exitPos / (flowData.queueLengthForLast / transitTime + exitPos);
					positionEnd = (currentTime - flowData.lastAtTail) / (flowData.lastEnterExit - flowData.lastAtTail) * (exitPos - shownQueueLengthForLast);
				}
				double rectOney = exitPos - shownQueueLength;
				if( currentTime <= flowData.firstBeforeEnterExit )
					rectOney = (currentTime - flowData.firstAtTail) / (flowData.firstEnterExit - flowData.firstAtTail) * (exitPos - shownQueueLength);
				drawFlow( gl, flowData.color, positionEnd, rectOney, flowData.inflow );
			}

			// Zeichnen des dicken bereiches in der Warteschlange
			if( currentTime >= flowData.firstAfterEnterExit && currentTime <= flowData.lastBeforeLeaveExit ) {
				double rectTwoy = exitPos;
				if( currentTime <= flowData.firstBeforeLeaveExit ) {
					final double physicalRemainingQueueLength = (flowData.queueLengthForFirst - (currentTime - flowData.firstEnterExit) * cap / corCap) / transitTime;
					final double shownRemainingQueueLength = physicalRemainingQueueLength * exitPos / (physicalRemainingQueueLength + exitPos);
					rectTwoy = exitPos - shownRemainingQueueLength;
				}
				drawFlow( gl, flowData.color, exitPos - shownQueueLength, rectTwoy, distWallFromEdge );
			}

			// Zeichnen des Abschnitts, der nach dem Ausgang kommt
			if( currentTime >= flowData.firstAfterLeaveExit && currentTime <= flowData.lastBeforeHead ) {
				if( currentTime >= flowData.lastAfterLeaveExit )
					positionEnd = exitPos + (currentTime - flowData.lastLeaveExit) / (flowData.lastAtHead - flowData.lastLeaveExit) * (1 - exitPos);
				else if( currentTime <= flowData.lastBeforeLeaveExit )
					positionEnd = exitPos - shownQueueLength;
				final double rectThreex = currentTime <= flowData.lastBeforeHead ? positionEnd : exitPos;
				if( currentTime <= flowData.firstBeforeHead )
					positionFront = exitPos + (currentTime - flowData.firstLeaveExit) / (flowData.firstAtHead - flowData.firstLeaveExit) * (1 - exitPos);
				drawFlow( gl, flowData.color, rectThreex, positionFront, cap );
			}
		} else
			if( currentTime >= flowData.firstAfterTail && currentTime <= flowData.lastBeforeHead ) {
				if( currentTime <= flowData.firstBeforeHead )
					positionFront = (currentTime - flowData.firstAtTail) / (flowData.firstAtHead - flowData.firstAtTail);
				if( currentTime >= flowData.lastAfterTail )
					positionEnd = (currentTime - flowData.lastAtTail) / (flowData.lastAtHead - flowData.lastAtTail);
				drawFlow( gl, flowData.color, positionEnd, positionFront, flowData.inflow );
			}
	}

	private void drawFlow( GL gl, GLColor color, double posFirst, double posLast, double flowThickness ) {
		final double drawnLength = control.get3DLength() * (posLast - posFirst);
//		if( drawnLength <= 0 )
//			return;

		color.draw( gl );

		gl.glPushMatrix();
		Vector3 b = new Vector3( 0, 0, 1 );
		Vector3 a = control.getDifferenceVectorInOpenGlScaling();
		Vector3 axis = control.getRotationAxis( a, b );
		gl.glRotated( control.getAngleBetween( a, b ), axis.x, axis.y, axis.z );
		gl.glTranslated( 0, 0, control.get3DLength() * posFirst );

		final double width = scale * fu * flowThickness * 0.5;

		glu.gluCylinder( quadObj, width, width, drawnLength, 16, 1 );

		glu.gluDisk( quadObj, 0, width, 16, 1 );
		gl.glTranslated( 0, 0, drawnLength );
		glu.gluDisk( quadObj, 0, width, 16, 1 );

		gl.glPopMatrix();
	}

	@Override
	public void update() {
		cap = ncontrol.getNashFlowEdgeData().getCapacity();
		transitTime = ncontrol.getNashFlowEdgeData().getTransitTime();
		corCap = ncontrol.getNashFlowEdgeData().getCorCapacity();
	}
}