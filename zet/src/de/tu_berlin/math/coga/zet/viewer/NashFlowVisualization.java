/**
 * NashFlowVisualization.java
 * Created: Jul 14, 2010,11:50:11 AM
 */
package de.tu_berlin.math.coga.zet.viewer;

import de.tu_berlin.math.coga.math.vectormath.Vector3;
import ds.graph.Edge;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Network;
import ds.graph.Node;
import gui.visualization.Visualization;
import gui.visualization.control.graph.GLFlowGraphControl;
import gui.visualization.control.graph.GLGraphControl;
import gui.visualization.control.graph.GLNashGraphControl;
import gui.visualization.draw.graph.GLEdge;
import java.awt.Color;
import java.util.ArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import opengl.drawingutils.GLColor;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class NashFlowVisualization extends Visualization<GLFlowGraphControl> {

	NashFlowEdgeData flowDatas0 = new NashFlowEdgeData( 2, 1 );
	NashFlowEdgeData flowDatas1 = new NashFlowEdgeData( 1, 6.75 );
	NashFlowEdgeData flowDatas2 = new NashFlowEdgeData( 1, 1.75 );
	NashFlowEdgeData flowDatas3 = new NashFlowEdgeData( 2, 9.125 );
	NashFlowEdgeData flowDatas4 = new NashFlowEdgeData( 1, 1 );
	ArrayList<GLEdge> edges = new ArrayList<GLEdge>();
	GLGraphControl graphControl;

	public NashFlowVisualization( GLCapabilities capabilities ) {
		super( capabilities );

		// set up data and stacks.
		FlowData fd = new FlowData( 0, 2, 3, 0, 2, 1, new GLColor( Color.red ) );
		flowDatas0.add( fd );
		fd = new FlowData( 2, 3.5, 1.5, 1, 2, 1, new GLColor( Color.blue ) );
		flowDatas0.add( fd );
		fd = new FlowData( 3.5, 5.375, 2.4, 0.625, 2, 1, new GLColor( Color.yellow ) );
		flowDatas0.add( fd );
		fd = new FlowData( 5.375, 14.375, 2.66667, 1, 2, 1, new GLColor( Color.green ) );
		flowDatas0.add( fd );
		fd = new FlowData( 14.375, 24.375, 2, 4, 2, 1, new GLColor( Color.cyan ) );
		flowDatas0.add( fd );

		fd = new FlowData( 2, 3.5, 1.5, 0, 1, 6.75, new GLColor( Color.blue ) );
		flowDatas1.add( fd );
		fd = new FlowData( 3.5, 5.375, 0.6, 0.75, 1, 6.75, new GLColor( Color.yellow ) );
		flowDatas1.add( fd );
		fd = new FlowData( 5.375, 14.375, 1/3., 0, 1, 6.75, new GLColor( Color.green ) );
		flowDatas1.add( fd );
		fd = new FlowData( 14.375, 24.375, 1, 0, 1, 6.75, new GLColor( Color.cyan ) );
		flowDatas1.add( fd );

		fd = new FlowData( 1, 4, 2, 0, 1, 1.75, new GLColor( Color.red ) );
		flowDatas2.add( fd );
		fd = new FlowData( 4, 5.125, 2, 3, 1, 1.75, new GLColor( Color.blue ) );
		flowDatas2.add( fd );
		fd = new FlowData( 5.125, 7.375, 0.5, 4.125, 1, 1.75, new GLColor( Color.yellow ) );
		flowDatas2.add( fd );
		fd = new FlowData( 7.375, 19.375, 0.75, 3, 1, 1.75, new GLColor( Color.green ) );
		flowDatas2.add( fd );

		fd = new FlowData( 5.125, 7.375, 1.5, 0, 2, 9.125, new GLColor( Color.yellow ) );
		flowDatas3.add( fd );
		fd = new FlowData( 7.375, 19.375, 1.25, 0, 2, 9.125, new GLColor( Color.green ) );
		flowDatas3.add( fd );
		fd = new FlowData( 19.375, 29.375, 2, 0, 2, 9.125, new GLColor( Color.cyan ) );
		flowDatas3.add( fd );

		fd = new FlowData( 2.75, 8.75, 1, 0, 1, 1, new GLColor( Color.red ) );
		flowDatas4.add( fd );
		fd = new FlowData( 8.75, 11, 2, 0, 1, 1, new GLColor( Color.blue ) );
		flowDatas4.add( fd );
		fd = new FlowData( 11, 12.125, 2, 2.25, 1, 1, new GLColor( Color.yellow ) );
		flowDatas4.add( fd );
		fd = new FlowData( 12.125, 21.125, 1+1/3., 3.325, 1, 1, new GLColor( Color.green ) );
		flowDatas4.add( fd );
		fd = new FlowData( 21.125, 31.175, 1, 6.325, 1, 1, new GLColor( Color.cyan ) );
		flowDatas4.add( fd );

		// set up some edges
		Network network = new Network( 4, 5 );
		network.setNode( new Node( 0 ) );
		network.setNode( new Node( 1 ) );
		network.setNode( new Node( 2 ) );
		network.setNode( new Node( 3 ) );
		Edge e0 = network.createAndSetEdge( network.getNode( 0 ), network.getNode( 1 ) );
		Edge e1 = network.createAndSetEdge( network.getNode( 0 ), network.getNode( 2 ) );
		Edge e2 = network.createAndSetEdge( network.getNode( 1 ), network.getNode( 2 ) );
		Edge e3 = network.createAndSetEdge( network.getNode( 1 ), network.getNode( 3 ) );
		Edge e4 = network.createAndSetEdge( network.getNode( 2 ), network.getNode( 3 ) );

		NodePositionMapping nodePositionMapping = new NodePositionMapping( 4 );
		nodePositionMapping.set( network.getNode( 0 ), new Vector3( -12, 0, 0 ) );
		nodePositionMapping.set( network.getNode( 1 ), new Vector3( 0, 6, 0 ) );
		nodePositionMapping.set( network.getNode( 2 ), new Vector3( 0, -6, 0 ) );
		nodePositionMapping.set( network.getNode( 3 ), new Vector3( 12, 0, 0 ) );


		IdentifiableObjectMapping<Edge, NashFlowEdgeData> mapping = new IdentifiableObjectMapping<Edge,NashFlowEdgeData>( 5, NashFlowEdgeData.class );
		mapping.set( e0, flowDatas0 );
		mapping.set( e1, flowDatas1 );
		mapping.set( e2, flowDatas2 );
		mapping.set( e3, flowDatas3 );
		mapping.set( e4, flowDatas4 );

		graphControl = new GLNashGraphControl( network, nodePositionMapping, mapping, this );

		//GLEdgeControl ec1 = new GLEdgeControl( nodePositionMapping, network.getEdge( 0 ) );
	}
	boolean first = false;

	@Override
	public void display( GLAutoDrawable drawable ) {
		gl.glClear( clearBits );
		computeFPS();

		if( !first ) {
			startAnimation();
			first = true;
		}


		gl.glMatrixMode( GL.GL_MODELVIEW );
		gl.glLoadIdentity();

//		Camera camera = getCamera();
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
//		for( FlowData flowData : flowDatas0 )
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


}
