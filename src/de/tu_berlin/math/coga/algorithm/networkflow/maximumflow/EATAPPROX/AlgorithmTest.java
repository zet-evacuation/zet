package de.tu_berlin.math.coga.algorithm.networkflow.maximumflow.EATAPPROX;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import de.tu_berlin.math.coga.algorithm.flowovertime.maxflow.MaximumFlowOverTimeProblem;
import de.tu_berlin.math.coga.algorithm.networkflow.maximumflow.PushRelabelHighestLabel;
import de.tu_berlin.math.coga.zet.DatFileReaderWriter;
import de.tu_berlin.math.coga.zet.flow;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import ds.GraphVisualizationResults;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.graph.network.AbstractNetwork;
import ds.graph.network.Network;
import ds.graph.network.NetworkInterface;
import ds.graph.problem.MaximumFlowProblem;
import ds.mapping.IdentifiableIntegerMapping;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author kapman
 */
public class AlgorithmTest {
	public HidingResidualGraph g;

	public static void MaxFlowTestInstance2Test() {
		AbstractNetwork network = new Network( 6, 1 );
		network.createAndSetEdge( network.getNode( 0 ), network.getNode( 1 ) );

		IdentifiableIntegerMapping<Edge> capacities = new IdentifiableIntegerMapping<>( 4 );
		capacities.add( network.getEdge( 0 ), 5 );

		MaximumFlowProblem mfp = new MaximumFlowProblem( network, capacities, network.getNode( 0 ), network.getNode( 5 ) );

		PushRelabelHighestLabel hipr = new PushRelabelHighestLabel();
		hipr.setProblem( mfp );
		hipr.run();

		System.out.println( "Flow: " + hipr.getSolution().getFlowValue() );
		System.out.println( hipr.getSolution().toString() );
		hipr.getSolution().check();

		//ArrayList<Node> sinks = new ArrayList<>();
		//ArrayList<Node> sources = new ArrayList<>();
		//sources.add( network.getNode( 0 ) );
		//sinks.add( network.getNode( 3 ) );

		//int timeHorizon = 2;

		//IdentifiableIntegerMapping<Node> supplies = new IdentifiableIntegerMapping<> ( 4 );
		//supplies.set( network.getNode( 0 ), 10 );
		//supplies.set( network.getNode( 1 ), 0 );
		//supplies.set( network.getNode( 2 ), 0 );
		//supplies.set( network.getNode( 3 ), 10 );

		//EarliestArrivalFlowProblem mfot = new EarliestArrivalFlowProblem(capacities, network, null, network.getNode( 3 ), sources, timeHorizon, transitTimes, supplies );
	}

	public EarliestArrivalFlowProblem testInstance() {
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

		int timeHorizon = 0;

		IdentifiableIntegerMapping<Node> supplies = new IdentifiableIntegerMapping<> ( 4 );
		supplies.set( network.getNode( 0 ), 10 );
		supplies.set( network.getNode( 1 ), 0 );
		supplies.set( network.getNode( 2 ), 0 );
		supplies.set( network.getNode( 3 ), -10 );

		EarliestArrivalFlowProblem mfot = new EarliestArrivalFlowProblem(capacities, network, null, network.getNode( 3 ), sources, timeHorizon, transitTimes, supplies );
		return mfot;
		//g = new HidingResidualGraph(network, capacities, transitTimes, timeHorizon, sources, sinks );
		//g.build();
		//System.out.println( g );

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

	public void simpleText() {

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

		System.out.println( "\nThe distnaces are: " );
		for( Node n : t.g.nodes() )
			System.out.println( n.id() + ": " + nf.distanceLabels.get( n ) );

		nf.updateDistances( newEdges );
		nf.run2();

		System.out.println( "\nEdge-Array nach dem zweiten Lauf von Phase 1:" );
		printEdgeList( t );

		System.out.println( "\nEdge-Array nach Freischaltung von Layer 2:" );
		newEdges = t.g.activateTimeLayer( 2 );
		for( Edge e : newEdges ) {
			System.out.println( "New edge visible: " + e );
		}
		printEdgeList( t );

		System.out.println( "\nThe distnaces are: " );
		for( Node n : t.g.nodes() )
			System.out.println( n.id() + ": " + nf.distanceLabels.get( n ) );

		System.out.println( "\nThe excesses are: " );
		for( Node n : t.g.nodes() )
			System.out.println( n.id() + ": " + nf.excess.get( n ) );

		nf.updateDistances( newEdges );
		nf.run2();

		System.out.println( "\nEdge-Array nach dem dritten Lauf von Phase 1:" );
		printEdgeList( t );
	}

	public void performTest( NetworkInterface network, IdentifiableIntegerMapping<Edge> capacities, IdentifiableIntegerMapping<Edge> transitTimes, int timeHorizon,  List<Node> sources, List<Node> sinks, IdentifiableIntegerMapping<Node> supplies ) {
		// Create Hiding residual graph
		timeHorizon = 1676;
		//1676
		HidingResidualGraph g = new HidingResidualGraph(network, capacities, transitTimes, timeHorizon, sources, sinks, supplies );
		g.build();
		this.g = g;
		System.out.println( g );

//		System.out.println( "Nodes: " + g.nodes() );
//		System.out.println( "Edges: " + g.edges() );
		System.out.println( "Super-Source-Index: " + g.SUPER_SOURCE );
		System.out.println( "Super-Sink-Index: " + g.SUPER_SINK );
		System.out.println( "Base-Source-Index: " + g.BASE_SOURCE );
		System.out.println( "Base-Sink-Index: " + g.BASE_SINK );
		System.out.println( "First-Node-Index: " + g.NODES );
//		for( int i = 0; i < g.nodes().size(); ++i ) {
//			if( i >= g.NODES && i < g.BASE_SINK )
//				System.out.println( "Node with ID " + i + " is on layer " + g.getLayer( i ) + " for original Node " + g.getOriginalNode( i ) );
//			else
//				System.out.println( "Node with ID " + i + " is on layer " + g.getLayer( i ) + " and is special node without corresponding original node." );
//		}
//		System.out.println( g.first );
//		System.out.println( g.last );

		// Set up instance
		FakeMaximumFlowProblem fmfp = new FakeMaximumFlowProblem( g, g );
		//NetworkFlowAlgorithm nf = new NetworkFlowAlgorithm();
		NetworkFlowAlgorithm nf = new NetworkFlowAlgorithmGlobalRelabelling();
		nf.setProblem( fmfp );

//		for( Edge e : g.edges ) {
//			System.out.println( "Reverse for " + e + " is " + g.reverseEdge.get( e ) );
//			if( g.reverseEdge.get( e ) == null )
//				System.out.println( g.isReverseEdge( e ) ? "reverse edge" : "normal edge" );
//		}

		// Run first
		try {
			System.out.println();
			//printEdgeList( this );
			System.out.println();
			nf.run();
		} catch( Exception e ) {
			throw e;
		} finally {
			//printEdgeList( this );
		}
		//System.out.println( "Flow arrived so far: " + nf.residualGraph.residualCapacity.get( nf.residualGraph.edges.get( nf.residualGraph.edges.size()-1 ) ) );
		System.out.println( 0 + " " + nf.residualGraph.residualCapacity.get( nf.residualGraph.edges.get( nf.residualGraph.edges.size()-1 ) ) );

		//if( true )
		//	throw new IllegalStateException( "We are here where we have ap roblem" );

		for( int currentTimeLayer = 1; currentTimeLayer <= timeHorizon; ++currentTimeLayer ) {
			//System.out.println( "\n\n\n-----------------------------------------------" );
			//System.out.println( "Iteration " + currentTimeLayer );
			Set<Edge> newEdges = g.activateTimeLayer( currentTimeLayer );
			for( Edge e : newEdges ) {
			//	System.out.println( "New edge visible: " + e );
			}


			nf.updateDistances( newEdges );
			nf.run2();
			//System.out.println( "Flow arrived so far: " + nf.residualGraph.residualCapacity.get( nf.residualGraph.edges.get( nf.residualGraph.edges.size()-1 ) ) );
			System.out.println( currentTimeLayer + " " + nf.residualGraph.residualCapacity.get( nf.residualGraph.edges.get( nf.residualGraph.edges.size()-1 ) ) );
			//System.out.println( "\nThe distnaces are: " );
			//for( Node n : g.nodes() )
			//	System.out.println( n.id() + ": " + nf.distanceLabels.get( n ) );

		//printEdgeList( this );

			//System.out.println( "-----------------------------------------------" );
		}

		System.out.println( nf.getRuntimeAsString() );

	}

	public static void main( String... args ) throws IOException {
		//AlgorithmTest.MaxFlowTestInstance2Test();
		//if( true ) return;

		AlgorithmTest t = new AlgorithmTest();
		EarliestArrivalFlowProblem mfot = t.testInstance();
		List<Node> sinks = new LinkedList<>();
		//sinks.add( mfot.getSink() );
		//t.performTest( mfot.getNetwork(), mfot.getEdgeCapacities(), mfot.getTransitTimes(), mfot.getTimeHorizon(), mfot.getSources(), sinks, mfot.getSupplies() );

		mfot = t.readFromDatFile();
		sinks = new LinkedList<>();
		sinks.add( mfot.getSink() );

		t.performTest( mfot.getNetwork(), mfot.getEdgeCapacities(), mfot.getTransitTimes(), mfot.getTimeHorizon(), mfot.getSources(), sinks, mfot.getSupplies() );

	}

	public EarliestArrivalFlowProblem readFromDatFile() throws IOException {
		NodePositionMapping nodePositionMapping = new NodePositionMapping();

		//EarliestArrivalFlowProblem  eafp = DatFileReaderWriter.read( "../../input/flow/siouxfalls_5_10s.dat", nodePositionMapping ); // new .dat-format
		EarliestArrivalFlowProblem  eafp = DatFileReaderWriter.read( "../../input/flow/swiss_1_10s.dat", nodePositionMapping ); // new .dat-format


		//MaximumFlowOverTimeProblem mfot = new MaximumFlowOverTimeProblem( eafp.getNetwork(), eafp.getEdgeCapacities(), eafp.getTransitTimes(), eafp.getSources(), sinks, eafp.getTimeHorizon()  );
		//return mfot;
		return eafp;
	}

	private static void printEdgeList( AlgorithmTest t ) {
		for( int i = 0; i < t.g.edges().size(); ++i ) {
			StringBuilder outLine = new StringBuilder();
			//System.out.println( t.g.edges().get( i ) );
			Edge e = t.g.edges().get( i );

			System.out.println( "Edge " + i + " with id " + e.id() );
			
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
