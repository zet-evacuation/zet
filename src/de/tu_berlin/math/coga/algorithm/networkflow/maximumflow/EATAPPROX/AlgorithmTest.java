/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.tu_berlin.math.coga.algorithm.networkflow.maximumflow.EATAPPROX;

import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.network.AbstractNetwork;
import ds.graph.network.Network;
import ds.mapping.IdentifiableIntegerMapping;
import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author kapman
 */
public class AlgorithmTest {
	public HidingResidualGraph g;

	public void testInstance() {
		AbstractNetwork network = new Network( 4, 5 );
		network.createAndSetEdge( network.getNode( 0 ), network.getNode( 1 ) );
		network.createAndSetEdge( network.getNode( 0 ), network.getNode( 2 ) );
		network.createAndSetEdge( network.getNode( 1 ), network.getNode( 2 ) );
		network.createAndSetEdge( network.getNode( 1 ), network.getNode( 3 ) );
		network.createAndSetEdge( network.getNode( 2 ), network.getNode( 3 ) );

		IdentifiableIntegerMapping<Edge> capacities = new IdentifiableIntegerMapping<>( 5 );
		capacities.add( network.getEdge( 0 ), 1 );
		capacities.add( network.getEdge( 1 ), 1 );
		capacities.add( network.getEdge( 2 ), 1 );
		capacities.add( network.getEdge( 3 ), 1 );
		capacities.add( network.getEdge( 4 ), 1 );

		IdentifiableIntegerMapping<Edge> transitTimes = new IdentifiableIntegerMapping<>( 5 );
		transitTimes.add( network.getEdge( 0 ), 0 );
		transitTimes.add( network.getEdge( 1 ), 1 );
		transitTimes.add( network.getEdge( 2 ), 0 );
		transitTimes.add( network.getEdge( 3 ), 1 );
		transitTimes.add( network.getEdge( 4 ), 0 );

		ArrayList<Node> sinks = new ArrayList<>();
		ArrayList<Node> sources = new ArrayList<>();
		sources.add( network.getNode( 0 ) );
		sinks.add( network.getNode( 3 ) );

		int timeHorizon = 2;

		g = new HidingResidualGraph(network, capacities, transitTimes, timeHorizon, sources, sinks );


		g.build();

		System.out.println( g );

//		MaximumFlowProblem mfp = new MaximumFlowProblem( network, capacities, network.getNode( 0 ), network.getNode( 3 ) );
//
//		PushRelabelHighestLabel hipr = new PushRelabelHighestLabel();
//		hipr.setProblem( mfp );
//		hipr.run();
//
//		System.out.println( "Flow: " + hipr.getSolution().getFlowValue() );
//		System.out.println( hipr.getSolution().toString() );
//		hipr.getSolution().check();
	}

	public static void main( String... args ) {
		AlgorithmTest t = new AlgorithmTest();
		t.testInstance();
		System.out.println( "Nodes: " + t.g.nodes() );
		System.out.println( "Edges: " + t.g.edges() );
		System.out.println( "Super-Source-Index: " + t.g.SUPER_SOURCE );
		System.out.println( "Super-Sink-Index: " + t.g.SUPER_SINK );
		System.out.println( "Base-Source-Index: " + t.g.BASE_SOURCE );
		System.out.println( "Base-Sink-Index: " + t.g.BASE_SINK );
		System.out.println( "First-Node-Index: " + t.g.NODES );

		for( int i = 0; i < t.g.nodes().size(); ++i ) {
			if( i >= t.g.NODES && i < t.g.BASE_SINK )
				System.out.println( "Node with ID " + i + " is on layer " + t.g.getLayer( i ) + " for original Node " + t.g.getOriginalNode( i ) );
			else
				System.out.println( "Node with ID " + i + " is on layer " + t.g.getLayer( i ) + " and is special node without corresponding original node." );
		}

		System.out.println( t.g.first );
		System.out.println( t.g.last );


		System.out.println( "\nEdge-Array zum Start:" );
		printEdgeList( t );

		//
		FakeMaximumFlowProblem fmfp = new FakeMaximumFlowProblem( t.g, t.g );

		NetworkFlowAlgorithm nf = new NetworkFlowAlgorithm();
		nf.setProblem( fmfp );
		nf.run();

		System.out.println( "\nEdge-Array nach dem ersten Lauf von Phase 1:" );
		printEdgeList( t );

		System.out.println( "\nEdge-Array nach Freischaltung von Layer 1:" );
		Set<Edge> newEdges = t.g.activateTimeLayer( 1 );
		for( Edge e : newEdges ) {
			System.out.println( "New edge visible: " + e );
		}
		printEdgeList( t );


		nf.updateDistances( newEdges );
		nf.run2();

		System.out.println( "\nEdge-Array nach dem zweiten Lauf von Phase 1:" );
		printEdgeList( t );

//		System.out.println( "\nEdge-Array nach Freischaltung von Layer 2:" );
//		Set<Edge> newEdges = t.g.activateTimeLayer( 2 );
//		for( Edge e : newEdges ) {
//			System.out.println( "New edge visible: " + e );
//		}
//		printEdgeList( t );

	}

	private static void printEdgeList( AlgorithmTest t ) {
		for( int i = 0; i < t.g.edges().size(); ++i ) {
			StringBuilder outLine = new StringBuilder();
			//System.out.println( t.g.edges().get( i ) );
			Edge e = t.g.edges().get( i );

			assert e.id() == i;

			outLine.append( e );
			Node v = e.start();
			Node w = e.end();

			outLine.append( " Target Layer " ).append( t.g.getLayer( w.id() )).append( ". " );

			outLine.append( " Residual capacity: " + t.g.getResidualCapacity( i ) + " - " );

			if( t.g.first.get( v ) == e.id() )
				outLine.append( " first " );
			if( t.g.last.get( v ) == e.id()+1 )
				outLine.append( " last " );

			System.out.println( outLine );
		}
	}
}
