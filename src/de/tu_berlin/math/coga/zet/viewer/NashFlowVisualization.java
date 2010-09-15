/**
 * NashFlowVisualization.java
 * Created: Jul 14, 2010,11:50:11 AM
 */
package de.tu_berlin.math.coga.zet.viewer;

import de.tu_berlin.math.coga.math.Conversion;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import ds.graph.Edge;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Network;
import ds.graph.Node;
import gui.visualization.Visualization;
import gui.visualization.control.graph.GLNashGraphControl;
import gui.visualization.draw.graph.GLEdge;
import java.util.ArrayList;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class NashFlowVisualization extends Visualization<GLNashGraphControl> {

	NashFlowEdgeData flowDatas0 = new NashFlowEdgeData( 2, 1 );
	NashFlowEdgeData flowDatas1 = new NashFlowEdgeData( 1, 6.75 );
	NashFlowEdgeData flowDatas2 = new NashFlowEdgeData( 1, 1.75 );
	NashFlowEdgeData flowDatas3 = new NashFlowEdgeData( 2, 9.125 );
	NashFlowEdgeData flowDatas4 = new NashFlowEdgeData( 1, 1 );
	ArrayList<GLEdge> edges = new ArrayList<GLEdge>();
	GLNashGraphControl graphControl;

	public NashFlowVisualization( GLCapabilities capabilities ) {
		super( capabilities );

		// set up data and stacks.
		//FlowData fd = new FlowData( 0, 2, 3, 0, 2, 1, new GLColor( Color.red ) );
		flowDatas0.add( 0, 2, 3, 0, 0, 0, 2 );
//		//fd = new FlowData( 2, 3.5, 1.5, 1, 2, 1, new GLColor( Color.blue ) );
//		flowDatas0.add( 2, 3.5, 1.5, 1, 1, 2, 3.5 );
//		//flowDatas0.add( fd );
//		//fd = new FlowData( 3.5, 5.375, 2.4, 0.625, 2, 1, new GLColor( Color.yellow ) );
//		flowDatas0.add( 3.5, 5.375, 2.4, 0.625, 2, 3.5, 5.375 );
//		//fd = new FlowData( 5.375, 14.375, 2.66667, 1, 2, 1, new GLColor( Color.green ) );
//		flowDatas0.add( 5.375, 14.375, 2.66667, 1, 3, 5.375, 14.375 );
//		//fd = new FlowData( 14.375, 24.375, 2, 4, 2, 1, new GLColor( Color.cyan ) );
//		flowDatas0.add( 14.375, 24.375, 2, 4, 4, 14.375, 24.375 );
//
		//fd = new FlowData( 2, 3.5, 1.5, 0, 1, 6.75, new GLColor( Color.blue ) );
//		flowDatas1.add( 2, 3.5, 1.5, 0, 1, 2, 3.5 );
		//fd = new FlowData( 3.5, 5.375, 0.6, 0.75, 1, 6.75, new GLColor( Color.yellow ) );
		//flowDatas1.add( 3.5, 5.375, 0.6, 0.75, 2, 3.5, 5.375 );
		//fd = new FlowData( 5.375, 14.375, 1/3., 0, 1, 6.75, new GLColor( Color.green ) );
		flowDatas1.add( 5.375, 14.375, 1/3., 0, 3, 5.375, 14.375 );
		//fd = new FlowData( 14.375, 24.375, 1, 0, 1, 6.75, new GLColor( Color.cyan ) );
		//flowDatas1.add( 14.375, 24.375, 1, 0, 4, 14.375, 24.375 );
//
//		//fd = new FlowData( 1, 4, 2, 0, 1, 1.75, new GLColor( Color.red ) );
//		flowDatas2.add( 1, 4, 2, 0, 0, 0, 2  );
//		//fd = new FlowData( 4, 5.125, 2, 3, 1, 1.75, new GLColor( Color.blue ) );
//		flowDatas2.add( 4, 5.125, 2, 3, 1, 2, 3.5 );
//		//fd = new FlowData( 5.125, 7.375, 0.5, 4.125, 1, 1.75, new GLColor( Color.yellow ) );
//		flowDatas2.add( 5.125, 7.375, 0.5, 4.125, 2, 3.5, 5.375 );
//		//fd = new FlowData( 7.375, 19.375, 0.75, 3, 1, 1.75, new GLColor( Color.green ) );
//		flowDatas2.add( 7.375, 19.375, 0.75, 3, 3, 5.375, 14.375 );

		//fd = new FlowData( 5.125, 7.375, 1.5, 0, 2, 9.125, new GLColor( Color.yellow ) );
		flowDatas3.add( 5.125, 7.375, 1.5, 0, 2, 3.5, 5.375 );
		//fd = new FlowData( 7.375, 19.375, 1.25, 0, 2, 9.125, new GLColor( Color.green ) );
		flowDatas3.add(7.375, 19.375, 1.25, 0, 3, 5.375, 14.375 );
		//fd = new FlowData( 19.375, 29.375, 2, 0, 2, 9.125, new GLColor( Color.cyan ) );
		//flowDatas3.add( 19.375, 29.375, 2, 0, 4, 14.375, 24.375 );

//		//fd = new FlowData( 2.75, 8.75, 1, 0, 1, 1, new GLColor( Color.red ) );
//		flowDatas4.add( 2.75, 8.75, 1, 0, 0, 0, 2  );
//		//fd = new FlowData( 8.75, 11, 2, 0, 1, 1, new GLColor( Color.blue ) );
//		flowDatas4.add( 8.75, 11, 2, 0, 1, 2, 3.5 );
//		//fd = new FlowData( 11, 12.125, 2, 2.25, 1, 1, new GLColor( Color.yellow ) );
//		flowDatas4.add( 11, 12.125, 2, 2.25, 2, 3.5, 5.375 );
//		//fd = new FlowData( 12.125, 21.125, 1+1/3., 3.325, 1, 1, new GLColor( Color.green ) );
//		flowDatas4.add( 12.125, 21.125, 1+1/3., 3.325, 3, 5.375, 14.375 );
//		//fd = new FlowData( 21.125, 31.175, 1, 6.325, 1, 1, new GLColor( Color.cyan ) );
//		flowDatas4.add( 21.125, 31.175, 1, 6.325, 4, 14.375, 24.375 );

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

		setControl( graphControl );

		this.set3DView();

		//GLEdgeControl ec1 = new GLEdgeControl( nodePositionMapping, network.getEdge( 0 ) );
	}
	boolean first = false;

	@Override
	public void startAnimation() {
		graphControl.setEndTime( 39 * Conversion.secToNanoSeconds );
		super.startAnimation();
	}

	@Override
	public void display( GLAutoDrawable drawable ) {
		if( !first ) {
			//startAnimation();
			first = true;
		}
		super.display( drawable );
		// TODO: richtig machen mit dem update :D
		// Status-Variablen die angezeigte Elemente steuern


		// here begins old stuff...

//		super.display( drawable );
//		gl.glClear( clearBits );
//		computeFPS();
//
//		if( !first ) {
//			startAnimation();
//			first = true;
//		}
//
//
//		gl.glMatrixMode( GL.GL_MODELVIEW );
//		gl.glLoadIdentity();
//
////		Camera camera = getCamera();
////		camera.setPos( new Vector3( 0, 0, 10 ) );
////		camera.setUp( new Vector3( 0, 1, 0 ) );
////		camera.setView( new Vector3( 0, 0, -1 ) );
//		look();
//
//		graphControl.getView().draw( gl );
//
//		drawFPS();
//
//		printErrors();
//		gl.glFlush();

	}
}
