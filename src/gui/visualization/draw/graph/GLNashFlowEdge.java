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

	final static double tu = 30;
	final static double exitPos = 0.75;
	final static double acceleration = 1;	// the time in seconds that we want to display ist...
	GLNashFlowEdgeControl ncontrol;
	double figNr = 0;
	double lastFigNr = -1;
	GLColor flowColor = new GLColor( Color.red );
	// sizes of corridor and flow unit
	double corStartPos = 0.2;
	double fu = 0.15;
	double corCap = 4;
	double corWallWidth = 0;
	double distWallFromEdge = corCap * fu * 0.5 + 0.5 * corWallWidth;
	double numberOfFrames = tu / acceleration;
	// berechne faktor:
	double factor = numberOfFrames;
	private double cap;
	private double taue;

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
		//drawFlow( gl );

		figNr = Conversion.nanoSecondsToSec * ncontrol.getTimeSinceStart() * factor;

		double currentTime = (figNr - 1) / tu;


		displayEdge( gl );

		for( FlowData flowData : ncontrol.getNashFlowEdgeData() )
			displayFlow( flowData, gl );

		displayCorridor( gl );
	}

	@Override
	public void performStaticDrawing( GL gl ) {
		// nothing
	}
	double scale = 4;

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
		double width = eps * scale * distWallFromEdge;
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

	private void displayFlow( FlowData flowData, GL gl ) {
		boolean giveOut = false;
		if( lastFigNr < figNr ) {
			//System.out.println( figNr );
			lastFigNr = figNr;
			giveOut = false;
		}

		boolean capGreaterThanInflow = cap > flowData.inflow;
		boolean waittimePositive = flowData.waittime > 0;

		double currentTime = (figNr - 1) / tu;
		currentTime = Conversion.nanoSecondsToSec * ncontrol.getTimeSinceStart();

		double posFirst = 1;
		double posLast = 0;

		if( waittimePositive || !capGreaterThanInflow ) {


			//currentTime = ((Conversion.nanoSecondsToSec * getTimeSinceStart() * 2)-1) / tu;

			double physicalQueueLength = flowData.queueLengthForFirst / taue;

			if( figNr >= flowData.firstAfterEnterExit && figNr <= flowData.lastBeforeEnterExit )
//				if( giveOut )
//					System.out.println( figNr + ": Fall 1 für physicalQueueLength" );
				physicalQueueLength = (flowData.queueLengthForFirst + (currentTime - flowData.firstEnterExit) * (flowData.inflow - cap) / corCap) / taue;
			if( figNr >= flowData.lastAfterEnterExit && figNr <= flowData.lastBeforeLeaveExit ) {
				if( giveOut )
					System.out.println( figNr + ": Fall 2 für physicalQueueLength" );
				physicalQueueLength = (flowData.queueLengthForLast - (currentTime - flowData.lastEnterExit) * cap / corCap) / taue;
			}

			double shownQueueLength = physicalQueueLength * exitPos / (physicalQueueLength + exitPos);
			if( giveOut )
				System.out.println( "Shown queue length: " + shownQueueLength );


			if( figNr >= flowData.firstAfterTail && figNr <= flowData.firstBeforeEnterExit ) {
				if( giveOut )
					System.out.println( figNr + ": posFirst wird neue definiert, fall 1" );
				posFirst = (currentTime - flowData.firstAtTail) / (flowData.firstEnterExit - flowData.firstAtTail) * (exitPos - shownQueueLength);
			}
			if( figNr >= flowData.firstAfterEnterExit && figNr <= flowData.firstBeforeLeaveExit ) {
				if( giveOut )
					System.out.println( figNr + ": posFirst wird neue definiert, Fall 2" );
				double physicalRemainingQueueLength = (flowData.queueLengthForFirst - (currentTime - flowData.firstEnterExit) * cap / corCap) / taue;
				double shownRemainingQueueLength = physicalRemainingQueueLength * exitPos / (physicalRemainingQueueLength + exitPos);
				posFirst = exitPos - shownRemainingQueueLength;
			}

			if( figNr >= flowData.firstAfterLeaveExit && figNr <= flowData.firstBeforeHead ) {
				if( giveOut )
					System.out.println( figNr + ": posFirst wird neue definiert, Fall 3" );
				posFirst = exitPos + (currentTime - flowData.firstLeaveExit) / (flowData.firstAtHead - flowData.firstLeaveExit) * (1 - exitPos);
			}

			if( figNr >= flowData.lastAfterTail && figNr <= flowData.lastBeforeEnterExit ) {
				if( giveOut )
					System.out.println( figNr + ": posLast wird neue definiert, Fall 1" );
				final double shownQueueLengthForLast = flowData.queueLengthForLast / taue * exitPos / (flowData.queueLengthForLast / taue + exitPos);
				posLast = (currentTime - flowData.lastAtTail) / (flowData.lastEnterExit - flowData.lastAtTail) * (exitPos - shownQueueLengthForLast);
			}
			if( figNr >= flowData.lastAfterEnterExit && figNr <= flowData.lastBeforeLeaveExit ) {
				if( giveOut )
					System.out.println( figNr + ": posLast wird neue definiert, Fall 2" );
				posLast = exitPos - shownQueueLength;
			}
			if( figNr >= flowData.lastAfterLeaveExit && figNr <= flowData.lastBeforeHead ) {
				if( giveOut )
					System.out.println( figNr + ": posLast wird neue definiert, Fall 3" );
				posLast = exitPos + (currentTime - flowData.lastLeaveExit) / (flowData.lastAtHead - flowData.lastLeaveExit) * (1 - exitPos);
			}

			// computing start and end positions of the flow
			double rectOnex = posLast;
			double rectOney = exitPos - shownQueueLength;

			double rectTwox = exitPos - shownQueueLength;
			double rectTwoy = exitPos;

			double rectThreex = exitPos;
			double rectThreey = posFirst;

			if( figNr >= flowData.firstAfterTail && figNr <= flowData.firstBeforeEnterExit ) {
				if( giveOut )
					System.out.println( "rectOney is changed to " + posFirst );
				rectOney = posFirst;
			}

			if( figNr >= flowData.firstAfterEnterExit && figNr <= flowData.firstBeforeLeaveExit )
				rectTwoy = posFirst;

			if( figNr >= flowData.lastAfterLeaveExit && figNr <= flowData.lastBeforeHead )
				rectThreex = posLast;


			// zeichnen
			flowData.color.draw( gl );
			if( figNr >= flowData.firstAfterTail && figNr <= flowData.lastBeforeEnterExit ) {
				if( giveOut )
					System.out.println( "DRAW 1: from " + rectOnex + " to " + rectOney );
				// draw the flow
				//dgeColor.draw( gl );
				gl.glPushMatrix();
				Vector3 b = new Vector3( 0, 0, 1 );
				Vector3 a = control.getDifferenceVectorInOpenGlScaling();
				Vector3 axis = control.getRotationAxis( a, b );
				gl.glRotated( control.getAngleBetween( a, b ), axis.x, axis.y, axis.z );
//				gl.glRotated( 90, 0, 1, 0 );
				gl.glTranslated( 0, 0, control.get3DLength() * rectOnex );
				double drawnLength = control.get3DLength() * (rectOney - rectOnex);
				double width = scale * fu * flowData.inflow * 0.5;
				glu.gluCylinder( quadObj, width, width, drawnLength, 16, 1 );

				glu.gluDisk( quadObj, 0, width, 16, 1 );
				gl.glTranslated( 0, 0, drawnLength );
				glu.gluDisk( quadObj, 0, width, 16, 1 );


				gl.glPopMatrix();

			}

			if( figNr >= flowData.firstAfterEnterExit && figNr <= flowData.lastBeforeLeaveExit ) {
				if( giveOut )
					System.out.println( "DRAW 2" );
				//edgeColor.draw( gl );
				gl.glPushMatrix();
				Vector3 b = new Vector3( 0, 0, 1 );
				Vector3 a = control.getDifferenceVectorInOpenGlScaling();
				Vector3 axis = control.getRotationAxis( a, b );
				gl.glRotated( control.getAngleBetween( a, b ), axis.x, axis.y, axis.z );
//				gl.glRotated( 90, 0, 1, 0 );
				gl.glTranslated( 0, 0, control.get3DLength() * rectTwox );
				double drawnLength = control.get3DLength() * (rectTwoy - rectTwox);
				double width = scale * distWallFromEdge;
				glu.gluCylinder( quadObj, width, scale * distWallFromEdge, drawnLength, 16, 1 );

				if( drawnLength > 0.001 ) {
					glu.gluDisk( quadObj, 0, width, 16, 1 );
					gl.glTranslated( 0, 0, drawnLength );
					glu.gluDisk( quadObj, 0, width, 16, 1 );
				}


				gl.glPopMatrix();
			}

			if( figNr >= flowData.firstAfterLeaveExit && figNr <= flowData.lastBeforeHead ) {
				if( giveOut )
					System.out.println( "DRAW 3" );
				//edgeColor.draw( gl );
				gl.glPushMatrix();
				Vector3 b = new Vector3( 0, 0, 1 );
				Vector3 a = control.getDifferenceVectorInOpenGlScaling();
				Vector3 axis = control.getRotationAxis( a, b );
				gl.glRotated( control.getAngleBetween( a, b ), axis.x, axis.y, axis.z );
//				gl.glRotated( 90, 0, 1, 0 );
				gl.glTranslated( 0, 0, control.get3DLength() * rectThreex );

				double drawnLength = control.get3DLength() * (rectThreey - rectThreex);
				double width = scale * cap * fu * 0.5;
				glu.gluCylinder( quadObj, width, width, drawnLength, 16, 1 );

				glu.gluDisk( quadObj, 0, width, 16, 1 );
				gl.glTranslated( 0, 0, drawnLength );
				glu.gluDisk( quadObj, 0, width, 16, 1 );


				gl.glPopMatrix();
			}

		} else {
			flowData.firstAtTail = flowData.starttime;
			flowData.firstAtHead = flowData.firstAtTail + flowData.taue;
			flowData.lastAtTail = flowData.endtime;
			flowData.lastAtHead = flowData.lastAtTail + flowData.taue;
			flowData.firstAfterTail = flowData.firstAtTail * flowData.tu + 1;
			flowData.firstBeforeHead = flowData.firstAtHead * flowData.tu + 1;
			flowData.lastAfterTail = flowData.lastAtTail * tu + 1;
			flowData.lastBeforeHead = flowData.lastAtHead * flowData.tu + 1;


			if( figNr >= flowData.firstAfterTail && figNr <= flowData.firstBeforeHead )
				posFirst = (currentTime - flowData.firstAtTail) / (flowData.firstAtHead - flowData.firstAtTail);

			if( figNr >= flowData.lastAfterTail && figNr <= flowData.lastBeforeHead )
				posLast = (currentTime - flowData.lastAtTail) / (flowData.lastAtHead - flowData.lastAtTail);

			if( figNr >= flowData.firstAfterTail && figNr <= flowData.lastBeforeHead ) {
				if( giveOut )
					System.out.println( "DER LETZTE FALL IST AKTIV" );
				//edgeColor.draw( gl );
				flowData.color.draw( gl );

				gl.glPushMatrix();
				Vector3 b = new Vector3( 0, 0, 1 );
				Vector3 a = control.getDifferenceVectorInOpenGlScaling();
				Vector3 axis = control.getRotationAxis( a, b );
				gl.glRotated( control.getAngleBetween( a, b ), axis.x, axis.y, axis.z );
//				gl.glRotated( 90, 0, 1, 0 );
				gl.glTranslated( 0, 0, control.get3DLength() * posFirst );

				double drawnLength = control.get3DLength() * (posLast - posFirst);
				double width = scale * fu * flowData.inflow * 0.5;

				glu.gluCylinder( quadObj, width, width, drawnLength, 16, 1 );

				glu.gluDisk( quadObj, 0, width, 16, 1 );
				gl.glTranslated( 0, 0, drawnLength );
				glu.gluDisk( quadObj, 0, width, 16, 1 );

				gl.glPopMatrix();
			}

		}
	}

	@Override
	public void update() {
		cap = ncontrol.getNashFlowEdgeData().getCapacity();
		taue = ncontrol.getNashFlowEdgeData().getTransitTime();
	}
}
