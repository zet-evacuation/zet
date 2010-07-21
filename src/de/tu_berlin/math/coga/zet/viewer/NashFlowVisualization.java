/**
 * NashFlowVisualization.java
 * input:
 * output:
 *
 * method:
 *
 * Created: Jul 14, 2010,11:50:11 AM
 */
package de.tu_berlin.math.coga.zet.viewer;

import de.tu_berlin.math.coga.math.Conversion;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import gui.visualization.Visualization;
import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.graph.GLGraphControl;
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

	GLColor edgeColor = VisualizationOptionManager.getEdgeColor();;
	GLColor flowColor = new GLColor( 1, 0, 0 );

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
		System.out.println( figNr );

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
		double tu = 22;



		//gl.glColor3f( 1,0,0 );
		edgeColor.draw( gl );


		gl.glPushMatrix();
		gl.glTranslated(-2,0,0);


		gl.glPushMatrix();
		// Draw the edge
		gl.glRotated( 90, 0, 1, 0 );
		glu.gluCylinder( quadObj, 0.01, 0.01, 4, 16, 1 );
		gl.glPopMatrix();

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

			// queue stuff
			double queueLengthForFirst = waittime * cap / corCap;
			double queueLengthForLast = queueLengthForFirst + (endtime - starttime) * (inflow - cap) / corCap;

			
		}





		gl.glPopMatrix();


		gl.glClear( GL.GL_DEPTH_BUFFER_BIT );
		drawFPS();

		printErrors();
		gl.glFlush();
	}


	



}
