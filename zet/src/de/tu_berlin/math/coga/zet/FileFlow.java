/**
 * FileFlow.java
 * Created: Mar 11, 2010,10:59:01 AM
 */
package de.tu_berlin.math.coga.zet;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.SEAAPAlgorithm;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmListener;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmStartedEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmTerminatedEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmProgressEvent;
import de.tu_berlin.math.coga.common.util.Formatter;
import de.tu_berlin.math.coga.common.util.units.TimeUnits;
import ds.graph.Edge;
import ds.graph.IdentifiableCollection;
import ds.mapping.IdentifiableIntegerMapping;
import ds.graph.network.AbstractNetwork;
import ds.graph.Node;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.graph.network.Network;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FileFlow implements AlgorithmListener {

	public static EarliestArrivalFlowProblem read( String filename ) throws FileNotFoundException, IOException {
		BufferedReader read = new BufferedReader( new FileReader( new File( filename ) ) );
		String s;

		int nodeCount = 0;
		int estimatedTimeHorizon = 0;

		ArrayList<Long> source_id = new ArrayList<Long>();
		ArrayList<Integer> source_sup = new ArrayList<Integer>();

		ArrayList<Long> sink_id = new ArrayList<Long>();
		ArrayList<Integer> sink_sup = new ArrayList<Integer>();

		ArrayList<Long> edge_start = new ArrayList<Long>();
		ArrayList<Long> edge_end = new ArrayList<Long>();
		ArrayList<Integer> edge_cap = new ArrayList<Integer>();
		ArrayList<Integer> edge_len = new ArrayList<Integer>();

		HashMap<Long, Integer> nodeMap = new HashMap<Long, Integer>();

		int currentNodeID = 0;

		while((s = read.readLine()) != null) {
			if( s.charAt( 0 ) == '%' )	// comment
				continue;
			if( s.charAt( 0 ) == 'N' ) {
				nodeCount = Integer.parseInt( s.substring( 2 ) );
				continue;
			}
			if( s.startsWith( "TIME" ) ) {
				estimatedTimeHorizon = Integer.parseInt( s.substring( 5 ) );
				continue;
			}
			if( s.startsWith( "HOLDOVER" ) )
				continue;
			if( s.charAt( 0 ) == 'S' ) {
				final String[] split = s.split( " " );
				final long nodeID = Long.parseLong( split[1] );
				nodeMap.put( nodeID, currentNodeID++ );
				final int nodeSupply = Integer.parseInt( split[2] );
				source_id.add( nodeID );
				source_sup.add( nodeSupply );
				continue;
			}
			if( s.charAt( 0 ) == 'T' ) {
				final String[] split = s.split( " " );
				final long nodeID = Long.parseLong( split[1] );
				nodeMap.put( nodeID, currentNodeID++ );
				final int nodeSupply = Integer.parseInt( split[2] );
				sink_id.add( nodeID );
				sink_sup.add( -nodeSupply );
				continue;
			}
			if( s.charAt( 0 ) == 'E' ) {
				final String[] split = s.split( " " );
				final long start = Long.parseLong( split[1] );
				final long end = Long.parseLong( split[2] );
				if( !nodeMap.containsKey( start ) )
					nodeMap.put( start, currentNodeID++ );
				if( !nodeMap.containsKey( end ) )
					nodeMap.put( end, currentNodeID++ );
				edge_start.add( start );
				edge_end.add( end );
				edge_cap.add( Integer.parseInt( split[3] ) );
				edge_len.add( Integer.parseInt( split[4] ) );
				continue;
			}
			throw new IllegalStateException( "Unbekannte Zeile: " + s );
		}

		int edgeCount = edge_start.size();

		System.out.println( "Knotenzahl: " + nodeCount );
		System.out.println( "Hasheinträge: " + nodeMap.size() );
		System.out.println( "Kantenzahl: " + edge_start.size() );
		System.out.println( "Zeithorizont (geschätzt): " + estimatedTimeHorizon );
		System.out.println( "Quellen: " + source_id.toString() );
		System.out.println( "mit Angeboten " + source_sup.toString() );
		System.out.println( "Senken: " + sink_id.toString() );
		System.out.println( "mit Bedarfen" + sink_sup.toString() );
		System.out.println( "Kanten von " + edge_start.toString() );
		System.out.println( "nach " + edge_end.toString() );
		System.out.println( "Mit Kapazitäten " + edge_cap.toString() );
		System.out.println( "Mit Länge " + edge_len.toString() );
		System.out.println( "Nächste Knotennummer: " + currentNodeID );

		System.out.println();
		System.out.println( "Erzeuge Netzwerk..." );


		AbstractNetwork network = new Network( nodeCount, edgeCount );
		for( int i = 0; i < edgeCount; ++i )
			network.createAndSetEdge( network.getNode( nodeMap.get( edge_start.get( i ) ) ), network.getNode( nodeMap.get( edge_end.get( i ) ) ) );
		Long t;
		IdentifiableIntegerMapping<Edge> edgeCapacities = new IdentifiableIntegerMapping<>( network.edges() );
		IdentifiableIntegerMapping<Node> nodeCapacities = new IdentifiableIntegerMapping<>( network.nodes() );
		IdentifiableIntegerMapping<Edge> transitTimes = new IdentifiableIntegerMapping<>( network.edges() );
		IdentifiableIntegerMapping<Node> currentAssignment = new IdentifiableIntegerMapping<>( network.nodes() );

		for( int i = 0; i < edgeCount; ++i ) {
			edgeCapacities.set( network.getEdge( network.getNode( nodeMap.get( edge_start.get( i ) ) ), network.getNode( nodeMap.get( edge_end.get( i ) ) ) ), edge_cap.get( i ) );
			transitTimes.set( network.getEdge( network.getNode( nodeMap.get( edge_start.get( i ) ) ), network.getNode( nodeMap.get( edge_end.get( i ) ) ) ), edge_len.get( i ) );
		}

		for( int i = 0; i < nodeCount; ++i )
			nodeCapacities.set( network.getNode( i ), 1000 );

		for( int i = 0; i < source_id.size(); ++i )
			currentAssignment.set( network.getNode( nodeMap.get( source_id.get( i ) ) ), source_sup.get( i ) );
		for( int i = 0; i < sink_id.size(); ++i )
			currentAssignment.set( network.getNode( nodeMap.get( sink_id.get( i ) ) ), sink_sup.get( i ) );

		System.out.println( network.toString() );


		Node sink = network.getNode( nodeMap.get( sink_id.get( 0 ) ) );
		ArrayList<Node> sources = new ArrayList<>();
		for( int i = 0; i < source_id.size(); ++i )
			sources.add( network.getNode( nodeMap.get( source_id.get( i ) ) ) );

		return new EarliestArrivalFlowProblem( edgeCapacities, network, nodeCapacities, sink, sources, estimatedTimeHorizon, transitTimes, currentAssignment );
	}

	public Algorithm computeFlow( EarliestArrivalFlowProblem eat ) throws FileNotFoundException, IOException {

		//SuccessiveEarliestArrivalAugmentingPathAlgorithm algo = new SuccessiveEarliestArrivalAugmentingPathAlgorithm();
		SEAAPAlgorithm algo = new SEAAPAlgorithm();
		algo.setProblem( eat );
		algo.addAlgorithmListener( this );
		algo.run();

                //Network n = new TimeExpandedNetwork(eat.getNetwork(), eat.getEdgeCapacities(), eat.getTransitTimes(), 100, eat.getSupplies(), true);



    System.out.println( Formatter.formatUnit( algo.getRuntime(), TimeUnits.MilliSeconds ) );
		long start = System.nanoTime();
		PathBasedFlowOverTime df = algo.getSolution().getPathBased();
		String result = String.format( "Sent %1$s of %2$s flow units in %3$s time units successfully.", algo.getSolution().getFlowAmount(), eat.getTotalSupplies(), algo.getSolution().getTimeHorizon() );
		System.out.println( result );
		//AlgorithmTask.getInstance().publish( 100, result, "" );
		long end = System.nanoTime();
		//System.out.println( String.format( "Sending the flow units required %1$s MilliSeconds.", algo.getRuntime() / 1000000 ) );
//		System.out.println( df.toString() );
		System.err.println( Formatter.formatUnit( end - start, TimeUnits.NanoSeconds ) );
		//return algo;
                return null;
	}

	@Override
	public void eventOccurred( AlgorithmEvent event ) {
		if( event instanceof AlgorithmProgressEvent )
			System.out.println( ((AlgorithmProgressEvent)event).getProgress() );
		else if( event instanceof AlgorithmStartedEvent )
			System.out.println( "Algorithmus startet." );
		else if( event instanceof AlgorithmTerminatedEvent )
			System.out.println( "Laufzeit Flussalgorithmus: " + Formatter.formatUnit( event.getAlgorithm().getRuntime(), TimeUnits.MilliSeconds ) );
		else
			System.out.println( event.toString() );
	}

	public static void main( String[] arguments ) throws FileNotFoundException, IOException {
		// MÖgliche Dateien:
		// problem.dat
		// siouxfalls_5_10s.dat
		// siouxfalls_50_10s.dat
		// siouxfalls_500_10s.dat
		// padang_10p_10s_flow01.dat
		// swiss_500_10s.dat

		System.out.println( "Version 1.0" );
		System.out.println( "Reads a file with an earliest arrival flow problem and solves the problem." );
		System.out.println( "Param #1: filename/path");
		System.out.println();

		try {
			FileFlow ff = new FileFlow();
			EarliestArrivalFlowProblem eat = read( arguments[0] );
			ff.computeFlow( eat );
			//ff.writeFile( "problem.dat", eat, "./testinstanz/output.dat" );
		} catch( FileNotFoundException e ) {
			System.out.println( "Datei nicht gefunden." );
			printFiles();
		} catch( ArrayIndexOutOfBoundsException e ) {
			System.out.println( "Kein Dateiname angegeben." );
			printFiles();
		}
	}

	public static void printFiles() {
			System.out.println( "Mögliche Dateien:" );
			System.out.println( "problem.dat" );
			System.out.println( "siouxfalls_5_10s.dat" );
			System.out.println( "siouxfalls_5_10s.dat" );
			System.out.println( "siouxfalls_50_10s.dat" );
			System.out.println( "padang_10p_10s_flow01.dat" );
			System.out.println( "swiss_500_10s.dat" );
	}

	public static void writeFile( String original, EarliestArrivalFlowProblem eafp, String filename ) throws FileNotFoundException, IOException {
		//writeFile( filename, eafp.getNetwork().numberOfNodes(), eafp.getTimeHorizon(), eafp.getSources(), eafp.getSink(), eafp.getNetwork().edges(), eafp.getEdgeCapacities(), eafp.getTransitTimes(), eafp.getSupplies() );
		writeFile( original, filename, eafp.getNetwork().numberOfNodes(), -1, eafp.getSources(), eafp.getSink(), eafp.getNetwork().edges(), eafp.getEdgeCapacities(), eafp.getTransitTimes(), eafp.getSupplies() );
	}

	public static void writeFile( String original, String filename, int nodeCount, int timeHorizon, List<Node> sources, Node sink, IdentifiableCollection<Edge> edges, IdentifiableIntegerMapping<Edge> edgeCapacities, IdentifiableIntegerMapping<Edge> transitTimes, IdentifiableIntegerMapping<Node> currentAssignment ) throws FileNotFoundException, IOException {
		BufferedWriter writer = new BufferedWriter( new FileWriter( new File( filename ) ) );
		String s;

		writer.write( "% Written by ZET FlowWriter\n" );
		writer.write( "% original file: " + original + '\n' );
		writer.write( "N " + nodeCount + '\n' );
		writer.write( "TIME " + timeHorizon + '\n' );

		for( Node source : sources ) {
			writer.write( "S " + source.id() + ' ' + currentAssignment.get( source ) + '\n' );
		}

		writer.write( "T " + sink.id() + ' ' + (-currentAssignment.get( sink )) + '\n' );

		for( Edge edge : edges ) {
			writer.write( "E " + edge.start().id() + ' ' + edge.end().id() + ' ' + edgeCapacities.get( edge ) + ' ' + transitTimes.get( edge ) + '\n' );
		}

		writer.close();
	}
}
