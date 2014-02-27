package de.tu_berlin.math.coga.algorithm.networkflow.maximumflow.EATAPPROX;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import de.tu_berlin.math.coga.algorithm.networkflow.maximumflow.PushRelabelHighestLabel;
import de.tu_berlin.math.coga.zet.DatFileReaderWriter;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.network.AbstractNetwork;
import ds.graph.network.Network;
import ds.graph.problem.MaximumFlowProblem;
import ds.mapping.IdentifiableIntegerMapping;
import java.io.IOException;
import java.util.ArrayList;
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

	public static void main( String... args ) throws IOException {

		EarliestArrivalFlowProblem mfot = readFromDatFile( "../../input/flow/swiss_1_10s.dat" );
		//EarliestArrivalFlowProblem mfot = readFromDatFile( "../../input/flow/siouxfalls_5_10s-original.dat" );
		EarliestArrivalApproximationAlgorithm algo = new EarliestArrivalApproximationAlgorithm();
		mfot.setTimeHorizon( 1676 ); // 64: zeitpunkt 42 ist falsch, // 1676 for max flow
		algo.setProblem( mfot );
		algo.run();

		System.out.println( algo.getSolution() );
		System.out.println( "Runtime: " + algo.getRuntimeAsString() );
	}

	public static EarliestArrivalFlowProblem readFromDatFile( String filename ) throws IOException {
		NodePositionMapping nodePositionMapping = new NodePositionMapping();

		EarliestArrivalFlowProblem  eafp = DatFileReaderWriter.read( filename, nodePositionMapping ); // new .dat-format

		return eafp;
	}


}
