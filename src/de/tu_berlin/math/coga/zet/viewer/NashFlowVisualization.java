/**
 * NashFlowVisualization.java
 * Created: Jul 14, 2010,11:50:11 AM
 */
package de.tu_berlin.math.coga.zet.viewer;

import de.tu_berlin.math.coga.graph.io.xml.FlowVisualization;
import de.tu_berlin.math.coga.graph.io.xml.GraphView;
import de.tu_berlin.math.coga.math.Conversion;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import ds.graph.Network;
import ds.graph.Node;
import gui.visualization.Visualization;
import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.graph.GLEdgeControl;
import gui.visualization.control.graph.GLFlowGraphControl;
import gui.visualization.control.graph.GLGraphControl;
import gui.visualization.draw.graph.GLEdge;
import java.awt.Color;
import java.util.ArrayList;
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
public class NashFlowVisualization extends Visualization<GLFlowGraphControl> {

	private class FlowData {
		GLColor color;
		double starttime = 2;
		double endtime = 3.5;
		double inflow = 1.5;
		double waittime = 1;
		private double corCap = 4;
		private double exitPos = 0.75;
		private double taue;
		private double cap;
		private double firstAtTail;
		private double firstEnterExit;
		private double firstLeaveExit;
		private double firstAtHead;
		private double lastAtTail;
		private double lastEnterExit;
		private double lastLeaveExit;
		private double lastAtHead;
		double tu = 30;
		private double queueLengthForFirst;
		private double queueLengthForLast;
		private double firstAfterTail;
		private double firstBeforeEnterExit;
		private double firstAfterEnterExit;
		private double firstBeforeLeaveExit;
		private double firstAfterLeaveExit;
		private double firstBeforeHead;
		private double lastAfterTail;
		private double lastBeforeEnterExit;
		private double lastAfterEnterExit;
		private double lastBeforeLeaveExit;
		private double lastAfterLeaveExit;
		private double lastBeforeHead;

		public FlowData( double startTime, double endTime, double inFlow, double waitTime, double cap, double tau, GLColor color ) {
			this.starttime = startTime;
			this.endtime = endTime;
			this.inflow = inFlow;
			this.waittime = waitTime;
			this.taue = tau;
			this.cap = cap;
			this.color = color;
			computeFixData();
		}

		private void computeFixData() {
			firstAtTail = starttime;
			firstEnterExit = firstAtTail + exitPos * taue;
			firstLeaveExit = firstEnterExit + waittime;
			firstAtHead = firstLeaveExit + (1 - exitPos) * taue;
			lastAtTail = endtime;
			lastEnterExit = lastAtTail + exitPos * taue;
			lastLeaveExit = lastEnterExit + (waittime + (endtime - starttime) * (inflow - cap) / cap);
			lastAtHead = lastLeaveExit + (1 - exitPos) * taue;

			// queue stuff
			queueLengthForFirst = waittime * cap / corCap;
			queueLengthForLast = queueLengthForFirst + (endtime - starttime) * (inflow - cap) / corCap;

			firstAfterTail = firstAtTail * tu + 2;
			firstBeforeEnterExit = firstEnterExit * tu + 1;
			firstAfterEnterExit = firstEnterExit * tu + 2;
			firstBeforeLeaveExit = firstLeaveExit * tu + 1;
			firstAfterLeaveExit = firstLeaveExit * tu + 2;
			firstBeforeHead = firstAtHead * tu + 1;

			lastAfterTail = lastAtTail * tu + 2;

			lastBeforeEnterExit = lastEnterExit * tu + 1;
			lastAfterEnterExit = lastEnterExit * tu + 2;
			lastBeforeLeaveExit = lastLeaveExit * tu + 1;
			lastAfterLeaveExit = lastLeaveExit * tu + 2;
			lastBeforeHead = lastAtHead * tu + 1;
		}
	}

	protected GLUquadric quadObj = glu.gluNewQuadric();
	double figNr = 0;
	double lastFigNr = -1;
	GLColor edgeColor = VisualizationOptionManager.getEdgeColor();
	GLColor flowColor = new GLColor( Color.red );

	// fixed properties for the edge:
	double cap = 2;
	double taue = 1;
	double tu = 30;
	double exitPos = 0.75;
	double acceleration = 5;	// the time in seconds that we want to display ist...

	// sizes of corridor and flow unit
	double corStartPos = 0.2;
	double fu = 0.15;
	double corCap = 4;
	double corWallWidth = 0;
	double distWallFromEdge = corCap * fu * 0.5 + 0.5 * corWallWidth;


	ArrayList<FlowData> flowDatas = new ArrayList<FlowData>();
	ArrayList<GLEdge> edges = new ArrayList<GLEdge>();
	GLGraphControl graphControl;

	public NashFlowVisualization( GLCapabilities capabilities ) {
		super( capabilities );

		// set up data and stacks.
		FlowData flowData = new FlowData( 0, 2, 3, 0, 2, 1, new GLColor( Color.red ) );
		flowDatas.add( flowData );
		flowData = new FlowData( 2, 3.5, 1.5, 1, 2, 1, new GLColor( Color.blue ) );
		flowDatas.add( flowData );
		flowData = new FlowData( 3.5, 5.375, 2.4, 0.625, 2, 1, new GLColor( Color.yellow ) );
		flowDatas.add( flowData );
		flowData = new FlowData( 5.375, 14.375, 2.66667, 1, 2, 1, new GLColor( Color.green ) );
		flowDatas.add( flowData );
		flowData = new FlowData( 14.375, 24.375, 2, 4, 2, 1, new GLColor( Color.cyan ) );
		flowDatas.add( flowData );

		// set up some edges
		Network network = new Network(2,1);
		network.setNode( new Node( 0 ) );
		network.setNode( new Node( 1 ) );
		network.createAndSetEdge( network.getNode( 0 ), network.getNode( 1 ) );

		NodePositionMapping nodePositionMapping = new NodePositionMapping( 2 );
		nodePositionMapping.set( network.getNode( 0 ), new Vector3( 0, 0, 0 ) );
		nodePositionMapping.set( network.getNode( 1 ), new Vector3( 4, 0, 0 ) );

		graphControl = new GLGraphControl( network, nodePositionMapping );

		GLEdgeControl ec1 = new GLEdgeControl( nodePositionMapping, network.getEdge( 0 ) );
	}
	boolean first = false;

	@Override
	public void display( GLAutoDrawable drawable ) {
		gl.glClear( clearBits );
		computeFPS();

		gl.glMatrixMode( GL.GL_MODELVIEW );
		gl.glLoadIdentity();

		Camera camera = getCamera();
//		camera.setPos( new Vector3( 0, 0, 10 ) );
//		camera.setUp( new Vector3( 0, 1, 0 ) );
//		camera.setView( new Vector3( 0, 0, -1 ) );
		look();

		graphControl.getView().draw( gl );

		drawFPS();

		printErrors();
		gl.glFlush();


//		if( true )
//			return;
//		if( !first ) {
//			startAnimation();
//			first = true;
//		}
//		gl.glClear( clearBits );
//
//		this.computeFPS();
//
//		// value for this edge
////		double cap = 2;
////		double taue = 1;
////		double starttime = 0;
////		double endtime = 2;
////		double inflow = 3;
////		double waittime = 0;
//
//
//
//		//double numberOfFrames = flowData.lastAtHead * tu / acceleration;
//		double numberOfFrames = tu / acceleration;
//		// berechne faktor:
//		double factor = numberOfFrames;
//
//		figNr = Conversion.nanoSecondsToSec * getTimeSinceStart() * factor;
//
//		gl.glMatrixMode( GL.GL_MODELVIEW );
//		gl.glLoadIdentity();
//		float[] light_position = new float[4];
//		light_position[0] = (float) getCamera().getView().x;
//		light_position[1] = (float) getCamera().getView().y;
//		light_position[2] = (float) getCamera().getView().z;
//		//light_position[0] = 0;
//		//light_position[1] = 1;
//		//light_position[2] = 0;
//		light_position[3] = 1.0f;
//		gl.glEnable( GL.GL_LIGHTING );
//
//		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light_position, 0);
////		if( is3D )
////			look();
////		else {
////			if( pvm != ParallelViewMode.Orthogonal ) {	// Isometric view
////				if( pvm == ParallelViewMode.Isometric )
////					gl.glRotatef( 35.264f, 1.0f, 0.0f, 0.0f );
////				else
////					gl.glRotatef( 30f, 1.0f, 0.0f, 0.0f );
////				gl.glRotatef( -45.0f, 0.0f, 1.0f, 0.0f );
////				gl.glRotated( -90, 1, 0., 0. );
////			} else	// Orthogonal view
////				gl.glLoadIdentity();
////		}
////
////		if( control != null )
////
////		control.draw( gl );
//
//
//		// Draw here
//		Camera camera = getCamera();
//		camera.setPos( new Vector3( 0, 0, 10 ) );
//		camera.setUp( new Vector3( 0, 1, 0 ) );
//		camera.setView( new Vector3( 0, 0, -1 ) );
//		look();
//
//		gl.glPushMatrix();
//		gl.glTranslated( -2, 0, 0 );
//
//		// draw the edge
//		displayEdge();
//
//		for( FlowData flowData : flowDatas )
//			displayFlow( flowData );
//
//		displayCorridor();
//
//		gl.glPopMatrix();
//
//
//		gl.glClear( GL.GL_DEPTH_BUFFER_BIT );
//		drawFPS();
//
//		printErrors();
//		gl.glFlush();
	}

	private void displayEdge() {
		// Draw the edge
		gl.glPushMatrix();
		edgeColor.draw( gl );
		gl.glRotated( 90, 0, 1, 0 );
		final double eps = 0.999;
		gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_LINE );
		glu.gluCylinder( quadObj, cap * fu * 0.5 * eps, cap * fu * 0.5 * eps, 4, 16, 16 );
		gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_FILL );

		gl.glPopMatrix();
	}

	private void displayCorridor() {
		GLColor wallColor = new GLColor( 139, 69, 19 );
		wallColor.draw( gl );
		int len = 4;

		gl.glPushMatrix();
		gl.glRotated( 90, 0, 1, 0 );
		final double eps = 0.999;
		gl.glEnable( gl.GL_BLEND );
		gl.glBlendFunc( gl.GL_SRC_ALPHA, gl.GL_ONE );

		gl.glTranslated( 0, 0, corStartPos * len );
		//gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_LINE );
		glu.gluCylinder( quadObj, distWallFromEdge, distWallFromEdge, exitPos * len - corStartPos * len, 16, 16 );
		//gl.glPolygonMode( GL.GL_FRONT, GL.GL_FILL );
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

		gl.glBegin( GL.GL_LINE_STRIP );
		GLVector x2 = new GLVector( corStartPos * len, -distWallFromEdge, 0 );
		GLVector y2 = new GLVector( exitPos * len + 0.5 * corWallWidth, -distWallFromEdge, 0 );
		GLVector z2 = new GLVector( exitPos * len + 0.5 * corWallWidth, -cap * fu * 0.5, 0 );
		x2.draw( gl );
		y2.draw( gl );
		z2.draw( gl );
		gl.glEnd();
		gl.glLineWidth( 1 );
	}

	private void displayFlow( FlowData flowData ) {
		boolean giveOut = false;
		if( lastFigNr < figNr ) {
			System.out.println( figNr );
			lastFigNr = figNr;
			giveOut = true;
		}

		boolean capGreaterThanInflow = cap > flowData.inflow;
		boolean waittimePositive = flowData.waittime > 0;
		if( waittimePositive || !capGreaterThanInflow ) {

			double currentTime = (figNr - 1) / tu;

			//currentTime = ((Conversion.nanoSecondsToSec * getTimeSinceStart() * 2)-1) / tu;

			double posFirst = 1;
			double posLast = 0;

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
				gl.glRotated( 90, 0, 1, 0 );
				gl.glTranslated( 0, 0, 4 * rectOnex );
				glu.gluCylinder( quadObj, fu*flowData.inflow*0.5, fu*flowData.inflow*0.5, 4 * (rectOney - rectOnex), 16, 1 );

				gl.glPopMatrix();

			}

			if( figNr >= flowData.firstAfterEnterExit && figNr <= flowData.lastBeforeLeaveExit ) {
				if( giveOut )
					System.out.println( "DRAW 2" );
				//edgeColor.draw( gl );
				gl.glPushMatrix();
				gl.glRotated( 90, 0, 1, 0 );
				gl.glTranslated( 0, 0, 4 * rectTwox );
				glu.gluCylinder( quadObj, distWallFromEdge, distWallFromEdge, 4 * (rectTwoy - rectTwox), 16, 1 );
				gl.glPopMatrix();
			}

			if( figNr >= flowData.firstAfterLeaveExit && figNr <= flowData.lastBeforeHead ) {
				if( giveOut )
					System.out.println( "DRAW 3" );
				//edgeColor.draw( gl );
				gl.glPushMatrix();
				gl.glRotated( 90, 0, 1, 0 );
				gl.glTranslated( 0, 0, 4 * rectThreex );
				glu.gluCylinder( quadObj, cap * fu * 0.5, cap * fu * 0.5, 4 * (rectThreey - rectThreex), 16, 1 );
				gl.glPopMatrix();
			}

		}
	}
}
