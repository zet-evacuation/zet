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
	double exitPos = 0.8;
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
	final static GLColor[] colors = { new GLColor( Color.red ), new GLColor( Color.blue ), new GLColor( Color.yellow ), new GLColor( Color.green ), new GLColor( Color.cyan ), new GLColor( Color.magenta ) };
	//final static GLColor[] rainbow = { GLColor.red, GLColor.orange, GLColor.yellow, GLColor.green, GLColor.blue, GLColor.indigo, GLColor.violet };
	final static GLColor[] rainbow = { GLColor.red, GLColor.yellow, GLColor.green, GLColor.magenta, GLColor.blue, GLColor.orange, GLColor.indigo, GLColor.cyan, GLColor.violet };
	double maxTime = 36;
	double timeInterval = maxTime / 9;

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

		final double eps = 0.999;
		gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_LINE );
		glu.gluCylinder( quadObj, scale * cap * fu * 0.5 * eps, scale * cap * fu * 0.5 * eps, control.get3DLength(), 16, 1 );
		gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_FILL );

		gl.glPopMatrix();
	}

	private void displayCorridor( GL gl ) {
		if( true ) return;
		GLColor wallColor = new GLColor( 139, 69, 19, 0.1 );

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
	}

	/**
	 * A helping method that gives the length of the queue depending of the flow
	 * data and the current time of visualization.
	 * @param flowData the flow data which is currently drawn
	 * @return the length of the queue
	 */
	private double getPhysicalQueueLength( FlowData flowData ) {
		if( currentTime >= flowData.firstEnterExit && currentTime <= flowData.lastEnterExit )
			return (flowData.queueLengthForFirst + (currentTime - flowData.firstEnterExit) * (flowData.inflow - cap) / corCap) / transitTime;
		if( currentTime >= flowData.firstEnterExit && currentTime <= flowData.lastLeaveExit )
			return (flowData.queueLengthForLast - (currentTime - flowData.lastEnterExit) * cap / corCap) / transitTime;
		return flowData.queueLengthForFirst / transitTime;
	}

	private void displayFlow( FlowData flowData, GL gl ) {
		double positionEnd = 0;

		// compute number of people
		if( flowData.waittimePositive || !flowData.capGreaterThanInflow ) {

			if( currentTime > flowData.firstLeaveExit ) {
				int k = 0;
				k++;
			}

			final double physicalQueueLength = getPhysicalQueueLength( flowData );
			final double shownQueueLength = physicalQueueLength * exitPos / (physicalQueueLength + exitPos);

			double fraction = (currentTime - flowData.firstLeaveExit) / (flowData.lastLeaveExit-flowData.firstLeaveExit); // this is the one used by the third part
			double outColorValue = flowData.globalStart + fraction * (flowData.globalEnd - flowData.globalStart);

			double flowStartColorValuePhase1 = flowData.globalEnd;
			double flowEndColorValuePhase1 = flowData.globalStart;
			if( currentTime >= flowData.startTime && currentTime <= flowData.lastEnterExit ) {
				//if( shownQueueLength > 0 ) {
				if( currentTime > flowData.firstEnterExit && shownQueueLength > 0 ) {
					flowEndColorValuePhase1 = -1;
				} else if( currentTime > flowData.firstLeaveExit ) { // here the queue length is = 0!
					flowEndColorValuePhase1 = outColorValue;
				} else {
					flowEndColorValuePhase1 = flowData.globalStart;
				}

				if( currentTime > flowData.endTime ) {
					flowStartColorValuePhase1 = flowData.globalEnd;
				} else {
					double f = (currentTime - flowData.startTime)/(flowData.endTime - flowData.startTime);
					flowStartColorValuePhase1 = flowData.globalStart + f * (flowData.globalEnd - flowData.globalStart);
				}
			}

			double flowStartColorValuePhase2 = flowData.globalEnd;
			double flowEndColorValuePhase2 = flowData.globalStart;
			if( currentTime >= flowData.firstEnterExit && currentTime <= flowData.lastLeaveExit ) {
				// two cases:
				if( currentTime >= flowData.firstLeaveExit ) {
					flowEndColorValuePhase2 = outColorValue;
				} else {
					flowEndColorValuePhase2 = flowData.globalStart;
				}

				if( currentTime <= flowData.lastEnterExit ) {
					flowStartColorValuePhase2 = -1;
				} else {
					flowStartColorValuePhase2 = flowData.globalEnd;
				}
			}
			if( flowStartColorValuePhase2 == -1 ) {
				flowStartColorValuePhase2 = 0.5 * (flowStartColorValuePhase1 + flowEndColorValuePhase2);
			}
			if( flowEndColorValuePhase1 == -1 ) {
				flowEndColorValuePhase1 = 0.5 * (flowStartColorValuePhase1 + flowEndColorValuePhase2);
			}

			// Zeichnen des Flussstückes das ungehindert in die Kante fließen kann
			if( currentTime >= flowData.startTime && currentTime <= flowData.lastEnterExit ) {
				// bestimmen der Farben

				if( currentTime >= flowData.endTime ) {
					final double shownQueueLengthForLast = flowData.queueLengthForLast / transitTime * exitPos / (flowData.queueLengthForLast / transitTime + exitPos);
					positionEnd = (currentTime - flowData.endTime) / (flowData.lastEnterExit - flowData.endTime) * (exitPos - shownQueueLengthForLast);

				}
				double rectOney = exitPos - shownQueueLength;
				if( currentTime <= flowData.firstEnterExit )
					rectOney = (currentTime - flowData.startTime) / (flowData.firstEnterExit - flowData.startTime) * (exitPos - shownQueueLength);

				//drawFlow( gl, flowData.globalEnd, flowData.globalStart, positionEnd, rectOney, flowData.inflow );
				drawFlow( gl, flowStartColorValuePhase1, flowEndColorValuePhase1, positionEnd, rectOney, flowData.inflow );
			}

			// Zeichnen des dicken bereiches in der Warteschlange
			if( currentTime >= flowData.firstEnterExit && currentTime <= flowData.lastLeaveExit ) {
				double rectTwoy = exitPos;
				if( currentTime <= flowData.firstLeaveExit ) {
					final double physicalRemainingQueueLength = (flowData.queueLengthForFirst - (currentTime - flowData.firstEnterExit) * cap / corCap) / transitTime;
					final double shownRemainingQueueLength = physicalRemainingQueueLength * exitPos / (physicalRemainingQueueLength + exitPos);
					rectTwoy = exitPos - shownRemainingQueueLength;
				}

				try {
					drawFlow( gl, flowStartColorValuePhase2, flowEndColorValuePhase2, exitPos - shownQueueLength, rectTwoy, distWallFromEdge );
				} catch( IllegalArgumentException ex ) {
					ex.printStackTrace( System.err );
				}
				//drawFlow( gl, flowStartColorValue, flowEndColorValue, xPos, endPos, flowData.inflow );
			}
			// Zeichnen des Abschnitts, der nach dem Ausgang kommt
			if( currentTime >= flowData.firstLeaveExit && currentTime <= flowData.lastAtHead ) {
				draw( flowData, gl, currentTime, flowData.firstLeaveExit, flowData.lastAtHead, flowData.lastLeaveExit, flowData.firstAtHead, exitPos );
			}
		} else
			draw( flowData, gl, currentTime, flowData.startTime, flowData.lastAtHead, flowData.endTime, flowData.firstAtHead, 0 );
	}

	private void draw( FlowData flowData, GL gl, double current, double firstVisible, double lastVisible, double lastInflow, double firstArrival, double startPos ) {
		if( currentTime >= firstVisible && currentTime <= lastVisible ) {
			double xPos;
			double endPos;

			double flowEndColorValue;
			double flowStartColorValue;

			// first: berechnung der position
			if( current >= lastInflow ) {
				xPos = ((current - lastInflow) / (lastVisible - lastInflow)) * (1 - startPos) + startPos;
				flowStartColorValue = flowData.globalEnd;
			} else {
				xPos = startPos;

				final double fraction = (current - firstVisible) / (lastInflow - firstVisible);
				flowStartColorValue = flowData.globalStart + fraction * (flowData.globalEnd - flowData.globalStart);
			}

			// second: berechnung des endwertes
			if( current <= firstArrival ) { // same as flowData.startTime + transitTime
				endPos = ((current - firstVisible) / (firstArrival - firstVisible)) * (1 - startPos) + startPos;
				flowEndColorValue = flowData.globalStart;
			} else {
				endPos = 1;
				final double fraction = (current - firstArrival) / (lastInflow - firstVisible);
				flowEndColorValue = flowData.globalStart + fraction * (flowData.globalEnd - flowData.globalStart);
			}

			//System.out.println( "Flow start: " + flowStartColorValue + " Flow end: " + flowEndColorValue );
			drawFlow( gl, flowStartColorValue, flowEndColorValue, xPos, endPos, flowData.inflow );
		}
	}

	private GLColor getColorForTime( double time ) {
		int colorIndex = (int)Math.floor( time/timeInterval );
		GLColor color = GLColor.blend( rainbow[colorIndex], rainbow[colorIndex+1], time/timeInterval - colorIndex );
		return color;
	}

	private int getColorIndex( double time ) {
		int colorIndex = (int)Math.floor( time/timeInterval );
		return colorIndex;
	}

	private double getColorIndexEndTime( int index ) {
		return index * timeInterval;
	}

	private void drawFlow( GL gl, double colorFirst, double colorLast, double posFirst, double posLast, double flowThickness ) {
		double drawnLength = control.get3DLength() * (posLast - posFirst);
		if( drawnLength <= 0 )
			return;

		gl.glPushMatrix();
		Vector3 b = new Vector3( 0, 0, 1 );
		Vector3 a = control.getDifferenceVectorInOpenGlScaling();
		Vector3 axis = control.getRotationAxis( a, b );
		gl.glRotated( control.getAngleBetween( a, b ), axis.x, axis.y, axis.z );

		final double width = scale * fu * flowThickness * 0.5;

		gl.glTranslated( 0, 0, control.get3DLength() * posFirst );

		gl.glPushMatrix();
		double flowStart = colorFirst;
		double flowEnd = colorLast;

		double sum = 0;
		if( flowEnd > flowStart )
			throw new IllegalArgumentException( "colorLast < colorFirst" );
//		System.out.print( "DRAW:" );
		while( true ) {
			final int startIndex = getColorIndex( flowStart );
			final int endIndex = getColorIndex( flowEnd );
			if( startIndex == endIndex ) {
				// easy peasy
				Cylinder.gluCylinder( width, width, drawnLength, 16, 1, gl, getColorForTime( flowStart), getColorForTime( flowEnd ) );
//				System.out.print( flowStart + "-" + flowEnd );
//				System.out.println( ": " + getColorForTime( flowStart ).toString() + "-" + getColorForTime( flowEnd ).toString() );
				break;
			} else {
				// bestimme farbwechselpunkt
				final double breakPointPosition = getColorIndexEndTime( startIndex );
				final double len = (flowStart-breakPointPosition)/(flowStart-flowEnd) * drawnLength;
				Cylinder.gluCylinder( width, width, len, 16, 1, gl, getColorForTime( flowStart), getColorForTime( breakPointPosition ) );
//				System.out.print( flowStart + "-" + breakPointPosition );
//				System.out.println( ": " + getColorForTime( flowStart ).toString() + "-" + getColorForTime( breakPointPosition ).toString() );
				drawnLength -= len;
				gl.glTranslated( 0, 0, len );
				flowStart = breakPointPosition-0.000001;
			}
		}
		gl.glPopMatrix();

		getColorForTime( colorFirst ).draw( gl );
		glu.gluDisk( quadObj, 0, width, 16, 1 );
		gl.glTranslated( 0, 0, control.get3DLength() * (posLast - posFirst) );
		getColorForTime( colorLast ).draw( gl );
		glu.gluDisk( quadObj, 0, width, 16, 1 );

		gl.glPopMatrix();
	}


	private void drawFlow( GL gl, GLColor colorFirst, GLColor colorLast, double posFirst, double posLast, double flowThickness ) {
		final double drawnLength = control.get3DLength() * (posLast - posFirst);
		if( drawnLength <= 0 )
			return;

		colorFirst.draw( gl );

		gl.glPushMatrix();
		Vector3 b = new Vector3( 0, 0, 1 );
		Vector3 a = control.getDifferenceVectorInOpenGlScaling();
		Vector3 axis = control.getRotationAxis( a, b );
		gl.glRotated( control.getAngleBetween( a, b ), axis.x, axis.y, axis.z );
		gl.glTranslated( 0, 0, control.get3DLength() * posFirst );

		final double width = scale * fu * flowThickness * 0.5;

		//glu.gluCylinder( quadObj, width, width, drawnLength, 16, 1 );
		Cylinder.gluCylinder( width, width, drawnLength, 16, 1, gl, colorFirst, colorLast );

		glu.gluDisk( quadObj, 0, width, 16, 1 );
		gl.glTranslated( 0, 0, drawnLength );
		glu.gluDisk( quadObj, 0, width, 16, 1 );

		gl.glPopMatrix();
	}

	@Override
	public void update() {
		cap = ncontrol.getNashFlowEdgeData().getCapacity();
		transitTime = ncontrol.getNashFlowEdgeData().getTransitTime();
		corCap = ncontrol.getNashFlowEdgeData().getCorridorCapacity();
		exitPos = ncontrol.getNashFlowEdgeData().getExitPosition();

	}
}