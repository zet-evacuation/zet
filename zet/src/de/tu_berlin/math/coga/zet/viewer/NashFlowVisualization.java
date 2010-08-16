/**
 * NashFlowVisualization.java
 * Created: Jul 14, 2010,11:50:11 AM
 */
package de.tu_berlin.math.coga.zet.viewer;

import de.tu_berlin.math.coga.math.Conversion;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import gui.visualization.Visualization;
import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.graph.GLGraphControl;
import java.awt.Color;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.glu.GLUquadric;
import opengl.drawingutils.GLColor;
import opengl.drawingutils.GLVector;
import opengl.framework.Camera;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class NashFlowVisualization extends Visualization<GLGraphControl> {
	protected GLUquadric quadObj = glu.gluNewQuadric();

	double figNr = 0;
	double lastFigNr = -1;

	GLColor edgeColor = VisualizationOptionManager.getEdgeColor();;
	GLColor flowColor = new GLColor( Color.red );

	public NashFlowVisualization( GLCapabilities capabilities ) {
		super( capabilities );
		
	}

	boolean first = false;

	@Override
	public void display( GLAutoDrawable drawable ) {
		if( !first ) {
			startAnimation();
			first = true;
		}
		gl.glClear( clearBits );

		this.computeFPS();


		figNr = Math.floor( Conversion.nanoSecondsToSec * getTimeSinceStart() * 2 ) ;
		boolean giveOut = false;
		if( lastFigNr < figNr ) {
			System.out.println( figNr );
			lastFigNr = figNr;
			giveOut = true;
		}

		gl.glMatrixMode( GL.GL_MODELVIEW );
		gl.glLoadIdentity();
		float[] light_position = new float[4];
		light_position[0] = (float)getCamera().getView().x;
		light_position[1] = (float)getCamera().getView().y;
		light_position[2] = (float)getCamera().getView().z;
		//light_position[0] = 0;
		//light_position[1] = 1;
		//light_position[2] = 0;
		light_position[3] = 1.0f;
		gl.glDisable( GL.GL_LIGHTING );

		//gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light_position, 0);
//		if( is3D )
//			look();
//		else {
//			if( pvm != ParallelViewMode.Orthogonal ) {	// Isometric view
//				if( pvm == ParallelViewMode.Isometric )
//					gl.glRotatef( 35.264f, 1.0f, 0.0f, 0.0f );
//				else
//					gl.glRotatef( 30f, 1.0f, 0.0f, 0.0f );
//				gl.glRotatef( -45.0f, 0.0f, 1.0f, 0.0f );
//				gl.glRotated( -90, 1, 0., 0. );
//			} else	// Orthogonal view
//				gl.glLoadIdentity();
//		}
//
//		if( control != null )
//
//		control.draw( gl );


		// Draw here
		Camera camera = getCamera();
		camera.setPos( new Vector3( 0, 0, 10 ) );
		camera.setUp( new Vector3( 0, 1, 0 ) );
		camera.setView( new Vector3( 0, 0, -1 ) );
		look();

//		gl.glBegin( GL.GL_TRIANGLES );
//			gl.glVertex3f( -1, -1, 0 );
//			gl.glVertex3f( 1, -1, 0 );
//			gl.glVertex3f( 0, 1, 0 );
//		gl.glEnd();


		// value for this edge
		double cap = 2;
		double taue = 1;
		double starttime = 0;
		double endtime = 2;
		double inflow = 3;
		double waittime = 0;
		double tu = 20;



		//gl.glColor3f( 1,0,0 );
		edgeColor.draw( gl );


		gl.glPushMatrix();
		gl.glTranslated(-2,0,0);



		// draw the border thing
		double corStartPos = 0.2;
		double exitPos = 0.75;
		double fu = 0.15;
		double corCap = 4;
		double corWallWidth = 0;

		GLColor wallColor = new GLColor( 139, 69, 19 );

		gl.glLineWidth( 3 );
		wallColor.draw( gl );
		int len = 4;
		double distWallFromEdge = corCap * fu * 0.5 + 0.5*corWallWidth;

		// draw the corridors
		gl.glBegin( GL.GL_LINE_STRIP );
			GLVector x1 = new GLVector ( corStartPos * len, distWallFromEdge, 0 );
			GLVector y1 = new GLVector( exitPos * len + 0.5 * corWallWidth, distWallFromEdge, 0 );
			GLVector z1 = new GLVector( exitPos*len + 0.5 * corWallWidth, cap * fu * 0.5 ,0);
			x1.draw( gl );
			y1.draw( gl );
			z1.draw( gl );
		gl.glEnd();

		gl.glBegin( GL.GL_LINE_STRIP );
			GLVector x2 = new GLVector ( corStartPos * len, -distWallFromEdge, 0 );
			GLVector y2 = new GLVector( exitPos * len + 0.5 * corWallWidth, -distWallFromEdge, 0 );
			GLVector z2 = new GLVector( exitPos*len + 0.5 * corWallWidth, -cap * fu * 0.5 ,0);
			x2.draw( gl );
			y2.draw( gl );
			z2.draw( gl );
		gl.glEnd();



		// draw the flow somehow
		boolean capGreaterThanInflow = cap > inflow;
		boolean waittimePositive = waittime > 0;
		if( waittimePositive || !capGreaterThanInflow ) {
			double firstAtTail = starttime;
			double firstEnterExit = firstAtTail + exitPos * taue;
			double firstLeaveExit = firstEnterExit + waittime;
			double firstAtHead = firstLeaveExit + (1-exitPos)*taue;
			double lastAtTail = endtime;
			double lastEnterExit = lastAtTail + exitPos*taue;
			double lastLeaveExit = lastEnterExit + (waittime + (endtime - starttime)*(inflow-cap) / cap );
			double lastAtHead = lastLeaveExit + (1-exitPos) * taue;

			if( figNr < 1 && giveOut ) {
				System.out.println( "firstAtTail: " + firstAtTail );
				System.out.println( "firstEnterExit: " + firstEnterExit );
				System.out.println( "firstLeaveExit: " + firstLeaveExit );
				System.out.println( "firstAtHead: " + firstAtHead );
				System.out.println( "lastAtTail: " + lastAtTail );
				System.out.println( "lastEnterExit: " + lastEnterExit );
				System.out.println( "lastLeaveExit: " + lastLeaveExit );
				System.out.println( "lastAtHead: " + lastAtHead );
			}

			// queue stuff
			double queueLengthForFirst = waittime * cap / corCap;
			double queueLengthForLast = queueLengthForFirst + (endtime - starttime) * (inflow - cap) / corCap;

			double firstAfterTail = firstAtTail * tu + 2;
			double firstBeforeEnterExit = firstEnterExit*tu + 1;
			double firstAfterEnterExit = firstEnterExit * tu + 2;
			double firstBeforeLeaveExit = firstLeaveExit * tu + 1;
			double firstAfterLeaveExit = firstLeaveExit * tu + 2;
			double firstBeforeHead = firstAtHead * tu + 1;

			double lastAfterTail = lastAtTail * tu + 2;

			double lastBeforeEnterExit = lastEnterExit * tu + 1;
			double lastAfterEnterExit = lastEnterExit * tu+2;
			double lastBeforeLeaveExit = lastLeaveExit*tu + 1;
			double lastAfterLeaveExit = lastLeaveExit*tu + 2;
			double lastBeforeHead = lastAtHead*tu + 1;

			double currentTime = (figNr -1) / tu;

			double posFirst = 1;
			double posLast = 0;

			double physicalQueueLength = queueLengthForFirst / taue;
			if( figNr >= firstAfterEnterExit && figNr <= lastBeforeEnterExit ) {
//				if( giveOut )
//					System.out.println( figNr + ": Fall 1 für physicalQueueLength" );
				physicalQueueLength = (queueLengthForFirst + (currentTime - firstEnterExit)*(inflow-cap)/corCap)/taue;
			}
			if( figNr >= lastAfterEnterExit && figNr <= lastBeforeLeaveExit ) {
				if( giveOut )
					System.out.println( figNr + ": Fall 2 für physicalQueueLength" );
				physicalQueueLength = (queueLengthForLast - (currentTime - lastEnterExit)*cap / corCap) / taue;
			}

			double shownQueueLength = physicalQueueLength * exitPos / (physicalQueueLength + exitPos);
			if( giveOut ) {
				System.out.println( "Shown queue length: " + shownQueueLength );
			}


			if( figNr >= firstAfterTail && figNr <= firstBeforeEnterExit ) {
				if( giveOut )
					System.out.println( figNr + ": posFirst wird neue definiert, fall 1" );
				posFirst = (currentTime-firstAtTail) / (firstEnterExit - firstAtTail) * (exitPos - shownQueueLength );
			}
			if( figNr >= firstAfterEnterExit && figNr <= firstBeforeLeaveExit ) {
				if( giveOut )
					System.out.println( figNr + ": posFirst wird neue definiert, Fall 2" );
				double physicalRemainingQueueLength = (queueLengthForFirst - (currentTime - firstEnterExit)*cap/corCap)/taue;
				double shownRemainingQueueLength = physicalRemainingQueueLength*exitPos / (physicalRemainingQueueLength+exitPos);
				posFirst = exitPos - shownRemainingQueueLength;
			}

			if( figNr >= firstAfterLeaveExit && figNr <= firstBeforeHead ) {
				if( giveOut )
					System.out.println( figNr + ": posFirst wird neue definiert, Fall 3" );
				posFirst = exitPos + (currentTime - firstLeaveExit) / (firstAtHead - firstLeaveExit) * (1-exitPos);
			}

			if( figNr >= lastAfterTail && figNr <= lastBeforeEnterExit ) {
				if( giveOut )
					System.out.println( figNr + ": posLast wird neue definiert, Fall 1" );
				double shownQueueLengthForLast = queueLengthForLast/taue*exitPos/(queueLengthForLast/taue+exitPos);
				posLast = (currentTime-lastAtTail)/(lastEnterExit-lastAtTail) * (exitPos-shownQueueLengthForLast);
			}
			if( figNr >= lastAfterEnterExit && figNr <= lastBeforeLeaveExit) {
				if( giveOut )
					System.out.println( figNr + ": posLast wird neue definiert, Fall 2" );
				posLast = exitPos - shownQueueLength;
			}
			if( figNr >= lastAfterLeaveExit && figNr <= lastBeforeHead) {
				if( giveOut )
					System.out.println( figNr + ": posLast wird neue definiert, Fall 3" );
				posLast = exitPos + (currentTime - lastLeaveExit) / (lastAtHead-lastLeaveExit) * (1-exitPos);
			}

			// computing start and end positions of the flow
			double rectOnex = posLast;
			double rectOney = exitPos - shownQueueLength;

			double rectTwox = exitPos - shownQueueLength;
			double rectTwoy = exitPos;

			double rectThreex = exitPos;
			double rectThreey = posFirst;

			if( figNr >= firstAfterTail && figNr <= firstBeforeEnterExit ) {
				if( giveOut )
					System.out.println( "rectOney is changed to " + posFirst );
				rectOney = posFirst;
			}

			if( figNr >= firstAfterEnterExit && figNr <= firstBeforeLeaveExit) {
				rectTwoy = posFirst;
			}

			if( figNr >= lastAfterLeaveExit && figNr <= lastBeforeHead ) {
				rectThreex = posLast;
			}


		// Draw the edge
		gl.glPushMatrix();
			edgeColor.draw( gl );
			gl.glRotated( 90, 0, 1, 0 );
			glu.gluCylinder( quadObj, 0.01, 0.01, 4, 16, 1 );

		gl.glPopMatrix();




			// zeichnen
			if( figNr >= firstAfterTail && figNr <= lastBeforeEnterExit ) {
				if( giveOut )
					System.out.println( "DRAW 1: from " + rectOnex + " to " + rectOney );
		// draw the flow
		gl.glPushMatrix();
			edgeColor.draw( gl );
			gl.glRotated( 90, 0, 1, 0 );
			gl.glTranslated( 0, 0, 4*rectOnex );
			glu.gluCylinder( quadObj, 0.05, 0.05, 4*(rectOney-rectOnex), 16, 1 );

		gl.glPopMatrix();

			}

			if( figNr >= firstAfterEnterExit && figNr <= lastBeforeLeaveExit ) {
				if( giveOut )
					System.out.println( "DRAW 2" );
			}

			if( figNr >= firstAfterLeaveExit && figNr <= lastBeforeHead ) {
				if( giveOut )
					System.out.println( "DRAW 3" );
			}

		}





		gl.glPopMatrix();


		gl.glClear( GL.GL_DEPTH_BUFFER_BIT );
		drawFPS();

		printErrors();
		gl.glFlush();
	}


	



}
