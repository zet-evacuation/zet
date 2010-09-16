/**
 * GLNashFlowEdge.java
 * Created: 30.08.2010, 16:26:12
 */
package gui.visualization.draw.graph;

import opengl.drawingutils.Cylinder;
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
		edgeColor = GLColor.gray;
		//edgeColor = new GLColor( GLColor.gray, 0.9 );
		update();
	}

	/**
	 * Draws the flow on the edge. The edge is already drawn using the {@link #performStaticDrawing(javax.media.opengl.GLAutoDrawable)} method.
	 * @param gl the {@code OpenGL} drawable object
	 */
	@Override
	public void performDrawing( GL gl ) {
		currentTime = Conversion.nanoSecondsToSec * ncontrol.getTimeSinceStart() * acceleration;


		for( FlowData flowData : ncontrol.getNashFlowEdgeData() )
			displayFlow( flowData, gl );
		displayEdge( gl );

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
		//gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_LINE );
		gl.glEnable( gl.GL_BLEND );
		gl.glBlendFunc( gl.GL_ONE_MINUS_DST_COLOR, gl.GL_ONE );
		glu.gluCylinder( quadObj, scale * cap * fu * 0.5 * eps, scale * cap * fu * 0.5 * eps, control.get3DLength(), 16, 1 );
		//gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_FILL );
		gl.glDisable( GL.GL_BLEND );

		gl.glPopMatrix();
	}

	private void displayCorridor( GL gl ) {
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
	 * An auxiliary method that gives the length of the queue depending of the flow
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
		if( flowData.waittimePositive || !flowData.capGreaterThanInflow ) {
			final double physicalQueueLength = getPhysicalQueueLength( flowData );
			final double shownQueueLength = physicalQueueLength * exitPos / (physicalQueueLength + exitPos);

			double fraction = (currentTime - flowData.firstLeaveExit) / (flowData.lastLeaveExit-flowData.firstLeaveExit); // this is the one used by the third part
			double flowStartColorValuePhase3 = flowData.globalStart + fraction * flowData.colorDifference;

			double flowStartColorValuePhase1 = flowData.globalEnd;
			double flowEndColorValuePhase1 = flowData.globalStart;
			if( currentTime >= flowData.startTime && currentTime <= flowData.lastEnterExit ) {
				if( currentTime > flowData.firstEnterExit && shownQueueLength > 0 )
					flowEndColorValuePhase1 = -1;
				else if( currentTime > flowData.firstLeaveExit ) // here the queue length is = 0!
					flowEndColorValuePhase1 = flowStartColorValuePhase3;
				else
					flowEndColorValuePhase1 = flowData.globalStart;

				if( currentTime > flowData.endTime )
					flowStartColorValuePhase1 = flowData.globalEnd;
				else {
					final double f = (currentTime - flowData.startTime)/(flowData.endTime - flowData.startTime);
					flowStartColorValuePhase1 = flowData.globalStart + f * flowData.colorDifference;
				}
			}

			double flowStartColorValuePhase2 = flowData.globalEnd;
			double flowEndColorValuePhase2 = flowData.globalStart;
			if( currentTime >= flowData.firstEnterExit && currentTime <= flowData.lastLeaveExit ) {
				flowEndColorValuePhase2 = currentTime >= flowData.firstLeaveExit ? flowStartColorValuePhase3 : flowData.globalStart;
				flowStartColorValuePhase2 = currentTime <= flowData.lastEnterExit ? -1 : flowData.globalEnd;
			}


			// berechne den faktor, der hier benötigt wird:
			double numberPart3 = cap * (currentTime - flowData.firstAtHead);
			double numberUpToNow = flowData.inflow * (Math.min( currentTime, flowData.endTime) - flowData.startTime);
			double numberPart2 = flowData.inflow * (Math.min( flowData.lastEnterExit, currentTime) - flowData.firstEnterExit );
			double numberPart1 = Math.max( numberUpToNow - numberPart3 - numberPart2, 0);
			double f = numberPart1/(numberPart1+numberPart2);

			if( flowStartColorValuePhase2 == -1 )
				//flowStartColorValuePhase2 = (1-f) * (flowStartColorValuePhase1 + flowEndColorValuePhase2);
				flowStartColorValuePhase2 = flowEndColorValuePhase2 + (1-f) * (flowStartColorValuePhase1 - flowEndColorValuePhase2);
//				flowStartColorValuePhase2 = 0.5 * (flowStartColorValuePhase1 + flowEndColorValuePhase2);
			if( flowEndColorValuePhase1 == -1 )
				//flowEndColorValuePhase1 = (1-f) * (flowStartColorValuePhase1 + flowEndColorValuePhase2);
				flowEndColorValuePhase1 = flowStartColorValuePhase1 - f * (flowStartColorValuePhase1-flowEndColorValuePhase2);
//				flowEndColorValuePhase1 = 0.5 * (flowStartColorValuePhase1 + flowEndColorValuePhase2);

			try {
			drawPhase1( gl, flowData, shownQueueLength, flowStartColorValuePhase1, flowEndColorValuePhase1 );
			} catch( Exception e ) {
				System.out.println( "ERR" );
			}

			try {
				drawPhase2( gl, flowData, shownQueueLength, flowStartColorValuePhase2, flowEndColorValuePhase2 );
			} catch( Exception e ) {
				System.out.println( "ERR" );
			}
			// Zeichnen des Abschnitts, der nach dem Ausgang kommt
			if( currentTime >= flowData.firstLeaveExit && currentTime <= flowData.lastAtHead )
				draw( flowData, gl, currentTime, flowData.firstLeaveExit, flowData.lastAtHead, flowData.lastLeaveExit, flowData.firstAtHead, exitPos );
		} else
			draw( flowData, gl, currentTime, flowData.startTime, flowData.lastAtHead, flowData.endTime, flowData.firstAtHead, 0 );
	}

	private void drawPhase1( GL gl, FlowData flowData, double shownQueueLength, double startColorValue, double endColorValue  ) {
		double positionEnd = 0;
		// Zeichnen des Flussstückes das ungehindert in die Kante fließen kann
			if( currentTime >= flowData.startTime && currentTime <= flowData.lastEnterExit ) {
				if( currentTime >= flowData.endTime ) {
					final double shownQueueLengthForLast = flowData.queueLengthForLast / transitTime * exitPos / (flowData.queueLengthForLast / transitTime + exitPos);
					positionEnd = (currentTime - flowData.endTime) / (flowData.lastEnterExit - flowData.endTime) * (exitPos - shownQueueLengthForLast);
				}
				double rectOney = exitPos - shownQueueLength;
				if( currentTime <= flowData.firstEnterExit )
					rectOney = (currentTime - flowData.startTime) / (flowData.firstEnterExit - flowData.startTime) * (exitPos - shownQueueLength);

				drawFlow( gl, startColorValue, endColorValue, control.get3DLength()*(rectOney-positionEnd), control.get3DLength()*(positionEnd), flowData.inflow );
			}
	}

	private void drawPhase2( GL gl, FlowData flowData, double shownQueueLength, double startColorValue, double endColorValue ) {
			if( currentTime >= flowData.firstEnterExit && currentTime <= flowData.lastLeaveExit ) {
				double rectTwoy = exitPos;
				if( currentTime <= flowData.firstLeaveExit ) {
					final double physicalRemainingQueueLength = (flowData.queueLengthForFirst - (currentTime - flowData.firstEnterExit) * cap / corCap) / transitTime;
					final double shownRemainingQueueLength = physicalRemainingQueueLength * exitPos / (physicalRemainingQueueLength + exitPos);
					rectTwoy = exitPos - shownRemainingQueueLength;
				}
				drawFlow( gl, startColorValue, endColorValue, control.get3DLength()*(rectTwoy-exitPos+shownQueueLength), control.get3DLength()*(exitPos-shownQueueLength), distWallFromEdge );
				//drawFlow( gl, startColorValue, endColorValue, exitPos - shownQueueLength, rectTwoy, distWallFromEdge );
			}
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
				flowStartColorValue = flowData.globalStart + fraction * flowData.colorDifference;
			}

			// second: berechnung des endwertes
			if( current <= firstArrival ) { // same as flowData.startTime + transitTime
				endPos = ((current - firstVisible) / (firstArrival - firstVisible)) * (1 - startPos) + startPos;
				flowEndColorValue = flowData.globalStart;
			} else {
				endPos = 1;
				final double fraction = (current - firstArrival) / (lastInflow - firstVisible);
				flowEndColorValue = flowData.globalStart + fraction * flowData.colorDifference;
			}

			drawFlow( gl, flowStartColorValue, flowEndColorValue, control.get3DLength()*(endPos-xPos), control.get3DLength()*xPos, flowData.inflow );
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

	private void drawFlow( GL gl, double baseColorValue, double topColorValue, double length, double offset, double flowThickness ) {
		double temporaryLength = length;
		if( temporaryLength <= 0 )
			return;

		gl.glPushMatrix();
		Vector3 b = new Vector3( 0, 0, 1 );
		Vector3 a = control.getDifferenceVectorInOpenGlScaling();
		Vector3 axis = control.getRotationAxis( a, b );
		gl.glRotated( control.getAngleBetween( a, b ), axis.x, axis.y, axis.z );

		final double width = scale * fu * flowThickness * 0.5;

		gl.glTranslated( 0, 0, offset );

		gl.glPushMatrix();
		double temporaryBaseColorValue = baseColorValue;

		if( topColorValue > temporaryBaseColorValue )
			throw new IllegalArgumentException( "colorLast < colorFirst" );
		while( true ) {
			final int startIndex = getColorIndex( temporaryBaseColorValue );
			final int endIndex = getColorIndex( topColorValue );
			if( startIndex == endIndex ) {
				// easy peasy
				Cylinder.drawCylinder( gl, width, temporaryLength, 16, getColorForTime( temporaryBaseColorValue), getColorForTime( topColorValue ) );
				break;
			} else {
				// bestimme farbwechselpunkt
				final double breakPointPosition = getColorIndexEndTime( startIndex );
				final double len = (temporaryBaseColorValue-breakPointPosition)/(temporaryBaseColorValue-topColorValue) * temporaryLength;
				Cylinder.drawCylinder( gl, width, len, 16, getColorForTime( temporaryBaseColorValue), getColorForTime( breakPointPosition ) );
				temporaryLength -= len;
				gl.glTranslated( 0, 0, len );
				temporaryBaseColorValue = breakPointPosition-0.000001;
			}
		}
		gl.glPopMatrix();

		getColorForTime( baseColorValue ).draw( gl );
		glu.gluDisk( quadObj, 0, width, 16, 1 );
		gl.glTranslated( 0, 0, length );
		getColorForTime( topColorValue ).draw( gl );
		glu.gluDisk( quadObj, 0, width, 16, 1 );

		gl.glPopMatrix();
	}

	@Override
	public void update() {
		cap = ncontrol.getNashFlowEdgeData().getCapacity();
		transitTime = ncontrol.getNashFlowEdgeData().getTransitTime();
		corCap = ncontrol.getNashFlowEdgeData().getCorridorCapacity();
		distWallFromEdge = corCap;
		exitPos = ncontrol.getNashFlowEdgeData().getExitPosition();
	}
}