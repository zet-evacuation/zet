/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.algorithm.scheduling;

import de.tu_berlin.math.coga.algorithm.networkflow.maximumflow.EdmondsKarp;
import de.tu_berlin.math.coga.datastructure.Tuple;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.network.Network;
import ds.graph.problem.MaximumFlowProblem;
import de.tu_berlin.coga.container.mapping.IdentifiableDoubleMapping;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import de.tu_berlin.coga.container.mapping.IdentifiableObjectMapping;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;
import java.util.TimeZone;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TCTOTest {

	HashMap<String, Ressource> ressources = new HashMap<>();
	ArrayList<Job> jobs = new ArrayList<>();

	public static void main( String[] arguments ) throws IOException {
		Ressource el = new Ressource( 40, 30, 0.6, true );
		Job j1 = new Job( 2, 2, 6, 3, 10, 1440, el, 1 );

		System.out.println( "Duration für 1:" + j1.getDuration( 1 ) );
		System.out.println( "Duration für 2:" + j1.getDuration( 2 ) );
		System.out.println( "Duration für 4:" + j1.getDuration( 4 ) );

		System.out.println( 0.6*720 );

		TCTOTest t = new TCTOTest();
		t.run();

		testAoN();
	}

	public void run() throws IOException {
		readRessources( "./testinstanz/tcto/t1_28res.csv" );
		readJobs( "./testinstanz/tcto/t1_28jobs.csv" );
	}

	public void readJobs( String file ) throws IOException {
		FileSystem fs = FileSystems.getDefault();
		Path p = fs.getPath( file );

		System.out.printf( "Datei '%s' mit Länge %d Byte(s) hat folgendes Zeilen:%n", p.getFileName(), Files.size( p ) );

		boolean first = true;
		int count = 0;
		for( String line : Files.readAllLines( p, StandardCharsets.UTF_8 ) ) {
			if( first ) {
				first = false;
				continue;
			}
			count++;
			ArrayList<String> elements = readLine( line );

			Ressource res = ressources.get( elements.get( 5 ) );

			long startTime = getTime( elements.get( 6 ) );

			long endTime = getTime( elements.get( 7 ) );

			int work = Integer.parseInt( elements.get( 8 ) );

			int initialRessources = Integer.parseInt( elements.get( 9 ) );
			int minRessources = Integer.parseInt( elements.get( 10 ) );
			int maxRessources = Integer.parseInt( elements.get( 11 ) );

			Job job = new Job( initialRessources, minRessources, maxRessources, startTime, endTime, work, res, count );
			jobs.add( job );

			String[] predecessors = elements.get( 1 ).split( ";" );
			for( String pred : predecessors ) {
				if( pred.isEmpty() )
					continue;
				System.out.print( pred+ " " );
				int jobindex = Integer.parseInt( pred ) -1;
				job.addPredecessor( jobs.get( jobindex ) );
				jobs.get( jobindex ).addSuccessor( job );
			}
			System.out.println( "" );


//			int maxCapacity = Integer.parseInt( elements.get( 4 ) );
//			boolean levelling = elements.get( 3 ).equals( "1" );
//			int cost = Integer.parseInt( elements.get( 5 ) );
//			double stretchFactor = Double.parseDouble( elements.get( 6 ) );
//			Ressource res = new Ressource( cost, maxCapacity, stretchFactor, levelling );
//			ressources.put( elements.get( 0 ), res );
		}
	}
	Calendar c = Calendar.getInstance( TimeZone.getDefault(), Locale.getDefault() );

	private long getTime( String time ) {
		if( time.isEmpty() ) {
			c.setTimeInMillis( Long.MAX_VALUE );
			//System.out.println( "Maxi: " + c.getTime() );
			return (c.getTimeInMillis() / 1000) * 1000;
		}
			int day = Integer.parseInt( time.substring( 0, 2 ) );
			int month = Integer.parseInt( time.substring( 3, 5 ) )-1;
			int year = Integer.parseInt( time.substring( 6, 10 ) );
			int hour = Integer.parseInt( time.substring( 11, 13 ) );
			int minute = Integer.parseInt( time.substring( 14, 16 ) );

			//System.out.println( "Start:" + day + "." + (month+1) + "." + year + " "+hour + ":" + minute );
			c.set( year, month, day, hour, minute, 35 );
			c.setTimeInMillis( (c.getTimeInMillis() / 1000) * 1000 );
			return (c.getTimeInMillis() / 1000) * 1000;
	}

	public void readRessources( String file ) throws IOException {
		FileSystem fs = FileSystems.getDefault();
		Path p = fs.getPath( file );

		System.out.printf( "Datei '%s' mit Länge %d Byte(s) hat folgendes Zeilen:%n", p.getFileName(), Files.size( p ) );

		boolean first = true;
		for( String line : Files.readAllLines( p, StandardCharsets.UTF_8 ) ) {
			if( first ) {
				first = false;
				continue;
			}
			ArrayList<String> elements = readLine( line );
			int maxCapacity = Integer.parseInt( elements.get( 4 ) );
			boolean levelling = elements.get( 3 ).equals( "1" );
			int cost = Integer.parseInt( elements.get( 5 ) );
			double stretchFactor = Double.parseDouble( elements.get( 6 ) );
			Ressource res = new Ressource( cost, maxCapacity, stretchFactor, levelling );
			ressources.put( elements.get( 0 ), res );
		}
	}

	private ArrayList<String> readLine( String line ) {
		ArrayList<String> elements = new ArrayList<>();
		System.out.println( line );
		int pos = 0;
		int startPos = 0;
		int endPos = 0;
		boolean start = true;
		boolean ignore = false;
		boolean check = false;
		while( pos < line.length() ) {
			if( check ) {
				if( line.charAt( pos ) != ';' )
					throw new IllegalArgumentException( "; expected" );
				check = false;
				pos++;
				start = true;
				startPos = pos;
			} else if( line.charAt( pos ) == '"' && start ) {
				pos++;
				start = false;
				ignore = true;
				startPos++;
			} else if( line.charAt( pos ) == '"' && !start && ignore ) {
				endPos = pos;
				String found = line.substring( startPos, endPos );
				pos++;
				check = true;
				ignore = false;
				//System.out.println( found );
				elements.add( found );
			} else if( line.charAt( pos ) == ';' && !ignore ) {
				if( start == true ) {
					elements.add( "" );
					pos++;
					startPos = pos;
					//System.out.println( "" );
				} else {
					endPos = pos;
					String found = line.substring( startPos, endPos );
					pos++;
					startPos = pos;
					start = true;
					//System.out.println( found );
					elements.add( found );
				}
			} else if( pos == line.length() - 1 ) {
				String found = line.substring( startPos );
				//System.out.println( found );
				elements.add( found );
				pos++;
			} else {
				pos++;
				start = false;
			}
		}
		return elements;
	}

	public static void testAoN() {
		HashMap<String, Ressource> ressources = new HashMap<>();
		ArrayList<Job> originalJobs = new ArrayList<>();

		Ressource el = new Ressource( 7, 30, 1, true );
		Job s = new Job( 0 );
		Job A = new Job( 2, 2, 6, 3, 10, 12, el, 1 );
		Job	B= new Job( 2, 2, 6, 3, 10, 8, el, 2 );
		Job C = new Job( 2, 2, 6, 3, 10, 2, el, 3 );
		Job D = new Job( 2, 2, 6, 3, 10, 4, el, 4 );
		Job E = new Job( 2, 2, 6, 3, 10, 6, el, 5 );
		Job t = new Job( 6 );

		System.out.println( "Max Duration A: " + A.getMaxDuration() );
		System.out.println( "Max Duration DUMMY: " + Job.DUMMY.getMaxDuration() );
		System.out.println( "Min Duration DUMMY: " + Job.DUMMY.getMinDuration() );

//		s.addSuccessor( A );
//		A.addPredecessor( s );
//		s.addSuccessor( B );
//		B.addPredecessor( s );
//		C.addPredecessor( A );
//		A.addSuccessor( C );
//		E.addPredecessor( B );
//		B.addSuccessor( E );
//		E.addPredecessor( C );
//		C.addSuccessor( E );
//		D.addPredecessor( C );
//		C.addSuccessor( D );
//		D.addSuccessor( t );
//		t.addPredecessor( D );
//		E.addSuccessor( t );
//		t.addPredecessor( E );
//
//		jobs.add( s );
//
//		jobs.add( A );
//		jobs.add( B );
//		jobs.add( C );
//		jobs.add( D );
//		jobs.add( E );
//		jobs.add( t );
		originalJobs.clear();

		el = new Ressource( 20/3., 30, 0.6, true );
		Ressource e3 = new Ressource( 8/6., 30, 0.6, true );
		Ressource e2 = new Ressource( 20000, 30, 0.50001, true );
		s = new Job( 0 );
		A = new Job( 3, 3, 6, 0, 0, 5*3, el, 1);
		B = new Job( 3, 3, 6, 0, 0, 10*3, e3, 2);
		C = new Job( 3, 3, 6, 0, 0, 10*3, e3, 3);
		D = new Job( 3, 3, 6, 0, 0, 2*3, e2, 4);
		E = new Job( 3, 3, 6, 0, 0, 5*3, el, 5);
		t = new Job( 6 );

		System.out.println( "A: [" + A.getMinDuration() + "," + A.getMaxDuration() + "]" );
		System.out.println( "A: " + A.getCostRate() );

		System.out.println( "B: [" + B.getMinDuration() + "," + B.getMaxDuration() + "]" );
		System.out.println( "B: " + B.getCostRate() );

		System.out.println( "B: [" + C.getMinDuration() + "," + C.getMaxDuration() + "]" );
		System.out.println( "B: " + C.getCostRate() );

		System.out.println( "D: [" + D.getMinDuration() + "," + D.getMaxDuration() + "]" );
		System.out.println( "D: " + D.getCostRate() );

		System.out.println( "E: [" + E.getMinDuration() + "," + E.getMaxDuration() + "]" );
		System.out.println( "E: " + E.getCostRate() );

		originalJobs.add( s );
		originalJobs.add( A );
		originalJobs.add( B );
		originalJobs.add( C );
		originalJobs.add( D );
		originalJobs.add( E );
		originalJobs.add( t );

		s.addSuccessor( A );
		s.addSuccessor( B );
		A.addPredecessor( s );
		B.addPredecessor( s );
		A.addSuccessor( C );
		C.addPredecessor( A );
		A.addSuccessor( D );
		D.addPredecessor( A );
		B.addSuccessor( E );
		E.addPredecessor( B );
		D.addSuccessor( E );
		E.addPredecessor( D );
		C.addSuccessor( t );
		E.addSuccessor( t );
		t.addPredecessor( C );
		t.addPredecessor( E );



		System.out.println( "Jobs und Ressourcen geladen." );
		System.out.println( "Erstelle Activity on Node Graph" );

		int successors = 0;

		for( Job j : originalJobs )
			successors += j.getSuccessors().size();

		Network AoN = new Network( originalJobs.size(), successors );
		for( Job j : originalJobs ) {
			Node node = new Node( j.id() ); // Node and Job id are equal
			AoN.setNode( node );
		}

		for( Job j : originalJobs ) {
			for( Job k : j.getSuccessors() )
				AoN.createAndSetEdge( AoN.getNode( j.id() ), AoN.getNode( k.id() ) );
		}
		System.out.println( AoN );

		System.out.println( "Konvertiere zu Activity on Edge" );
		int nodeCount = (originalJobs.size())*2;
		int edgeCount = (AoN.getEdgeCapacity()) + (originalJobs.size()); // AoN.getEdgeCapacity() = successors
		AoE = new Network( nodeCount, edgeCount );

		for( Job j : originalJobs ) {
			Node start = new Node( 2*j.id() );
			Node end = new Node( 2*j.id() + 1 );
			AoE.setNode( start );
			AoE.setNode( end );
			AoE.createAndSetEdge( start, end );
		}
		for( Job j : originalJobs ) {
			for( Job k : j.getSuccessors() )
				AoE.createAndSetEdge( AoE.getNode( j.id()*2+1 ), AoE.getNode( k.id()*2 ) );
		}
		System.out.println( AoE );

		System.out.println( "Create Dummy Jobs" );
		IdentifiableObjectMapping<Edge,Job> jobs = new IdentifiableObjectMapping<>( AoE.getEdgeCapacity(), Job.class );

		for( Edge e : AoE.edges() ) {
			if( e.id() < originalJobs.size() )
				jobs.set( e, originalJobs.get( e.id() ) );
			else
				jobs.set( e, Job.DUMMY );
		}

		System.out.println( "Berechne topologische Sortierung" );
		Queue<Node> q = new LinkedList<>();
		topSort = new LinkedList<>();
		int[] predCount = new int[AoE.getNodeCapacity()];
		for( Node n : AoE ) {
			predCount[n.id()] = AoE.inDegree( n );
		}
		q.add( AoE.getNode( 0 ) );

		while( !q.isEmpty() ) {
			for( Edge e : AoE.outgoingEdges( q.poll() ) ) {
				//if( e.id() < originalJobs.size() )
				topSort.add( e );
				if( --predCount[e.end().id()] == 0 )
					q.add( e.end() );
			}
		}
		System.out.println( topSort );

		// set up used ressources for TCTO-computation
		System.out.println( "Set Durations to max durations" );
		durations = new IdentifiableDoubleMapping<>( AoE.getEdgeCapacity() );
		for( Edge e : AoE.edges() ) {
			durations.set( e, jobs.get( e ).getMaxDuration() );
		}

		jobMapping = jobs;

		System.out.println( "\n\n\n Iteration 1" );
		TCTOIteration();


//		System.out.println( "Outgoing von Knoten 3: " + AoE.outgoingEdges( AoE.getNode( 3 ) ) );
//		AoE.setHidden( AoE.allGetEdge( AoE.getNode( 3 ), AoE.getNode( 8 ) ), false );
//		System.out.println( "Outgoing von Knoten 3: " + AoE.outgoingEdges( AoE.getNode( 3 ) ) );

		System.out.println( "\n\n\n Iteration 2" );
		TCTOIteration();

		System.out.println( "\n\n\n Iteration 3" );
		TCTOIteration();

		System.out.println( "\n\n\n Iteration 4" );
		TCTOIteration();

		System.out.println( "\n\n\n Iteration 5" );
		TCTOIteration();

		System.out.println( curve );
	}

	static Network AoE;
	static LinkedList<Edge> topSort;
	static IdentifiableDoubleMapping<Edge> durations;
	static IdentifiableObjectMapping<Edge,Job> jobMapping;
	static IdentifiableIntegerMapping<Edge> upperCapacities;
	static IdentifiableIntegerMapping<Edge> lowerCapacities;

	static MaximumFlowProblem mfp;

	static EdmondsKarp ek = new EdmondsKarp();

	static ArrayList<Tuple<Integer,Integer>> curve = new ArrayList<>();

		public static void TCTOIteration(  ) {

		AoE.showAllEdges();

		System.out.println( "Compute earliest start times" );
		IdentifiableIntegerMapping<Node> est = new IdentifiableIntegerMapping<>( AoE.getNodeCapacity() );
		est.set( AoE.getNode( 0 ), 0 );
		for( Edge e : topSort ) {
			int max = est.get( e.start() );
			for( Edge pred : AoE.incomingEdges( e.start() ) ) {
				//max = Math.max( max, est.get( pred.start() ) + (int)(jobs.get( pred ).getMaxDuration() ) );
				max = Math.max( max, est.get( pred.start() ) + (int)(durations.getDouble( pred ) ) );
			}
			est.set( e.start(), max );
		}
		System.out.println( est );

		System.out.println( "Compute latest start times" );
		IdentifiableIntegerMapping<Node> lst = new IdentifiableIntegerMapping<>( AoE.getNodeCapacity() );
		Iterator<Edge> iter = topSort.descendingIterator();
		for( Node n : AoE )
			lst.set( n, Integer.MAX_VALUE );
		lst.set( AoE.getNode( AoE.getNodeCapacity() - 2), est.get( AoE.getNode( AoE.getNodeCapacity()- 2 ) ) );
		while( iter.hasNext() ) {
			Edge e = iter.next();
			int min = Integer.MAX_VALUE;
			for( Edge pred : AoE.incomingEdges( e.start() ) ) {
				//min = Math.min( min, lst.get( e.start() ) - (int)(jobs.get( pred ).getMaxDuration() ) );
				min = Math.min( min, lst.get( e.start() ) - (int)( durations.getDouble( pred ) ) );
				lst.set( pred.start(), min );
			}
		}
		lst.set( AoE.getNode( AoE.getNodeCapacity() - 1), lst.get( AoE.getNode( AoE.getNodeCapacity()- 2 ) ) );
		System.out.println( lst );

		// TODO assert check est <= lst

		double eps = 0.01;



		System.out.println( "Check for critical edges" );
		for( Edge e : AoE.allEdges() ) {
			if( Math.abs ( est.get( e.start() ) + durations.get( e ) - lst.get( e.end() ) ) < eps ) {
				System.out.println( "Critical: " + e );
				//AoE.setHidden( e, false );
			} else {
				System.out.println( "Hiding: " + e );
				AoE.setHidden( e, true );
			}

		}
		System.out.println( AoE );



		System.out.println( "Set up upper capacities" );
		if( upperCapacities == null )
			upperCapacities = new IdentifiableIntegerMapping<>(AoE.getEdgeCapacity() );
		for( Edge e : AoE.edges() ) { // only iterate over critical network
			double t = Math.abs( durations.get( e ) - jobMapping.get( e ).getMinDuration() );
			if(  t < eps )
				upperCapacities.set( e, Integer.MAX_VALUE );
			else
				upperCapacities.set( e, (int)jobMapping.get( e ).getCostRate() );
		}
		System.out.println( upperCapacities );

		System.out.println( "Set up lower capacities" );
		if( lowerCapacities == null )
			lowerCapacities = new IdentifiableIntegerMapping<>(AoE.getEdgeCapacity() );
		for( Edge e : AoE.edges() ) { // only iterate over critical network
			double t = Math.abs( durations.get( e ) - jobMapping.get( e ).getMaxDuration() );
			if( t < eps  )
				lowerCapacities.set( e, 0 );
			else
				lowerCapacities.set( e, (int)jobMapping.get( e ).getCostRate() );
		}
		System.out.println( lowerCapacities );



		System.out.println( "Computing max flow/min cut" );
		if( mfp == null ) {
			mfp = new MaximumFlowProblem( AoE, upperCapacities, AoE.getNode( 0 ), AoE.getNode( AoE.getNodeCapacity()-1 ) );
			ek = new EdmondsKarp();
			ek.setProblem( mfp );
			ek.setLowerCapacities( lowerCapacities );
		}

		ek.run();
		ek.getSolution().check();

		System.out.println( "Flow: " + ek.getSolution().getFlowValue() );
		System.out.println( ek.getSolution().toString() );

		ek.computeCutEdges();
		System.out.println( "Outgoing edges in cut:" );
		System.out.println( ek.getOutgoingCut() );
		System.out.println( "Incoming edges in cut:" );
		System.out.println( ek.getIncomingCut() );
		System.out.println( "Compute hidden cut" );

		LinkedList<Edge> cutHidden = new LinkedList<>();
		for( Node n : ek.getCut() ) {
			iter = AoE.allOutgoingEdges( n );
			while( iter.hasNext() ) {
				Edge e = iter.next();
				// find outgoing edges
				if( !ek.isInCut( e.end() ) && AoE.isHidden( e ) )
					cutHidden.add( e );
			}
		}

		System.out.println( "Updating delta" );
		double delta1 = Double.MAX_VALUE;
		for( Edge e : cutHidden ) {
			double min = lst.get( e.end() ) - durations.get( e ) - est.get( e.start() );
			delta1 = Math.min( delta1, min );
		}
		System.out.println( "Delta 1: " + delta1 );

		double delta2 = Double.MAX_VALUE;
		for( Edge e : ek.getOutgoingCut() ) {
			if( Math.abs( durations.get( e ) - jobMapping.get( e ).getMinDuration() ) > eps )
				delta2 = Math.min( delta2, durations.get( e ) - jobMapping.get( e ).getMinDuration() );
		}
		System.out.println( "Delta 2: " + delta2 );

		double delta3 = Double.MAX_VALUE;
		for( Edge e : ek.getIncomingCut() ) {
			if( Math.abs( jobMapping.get( e ).getMaxDuration() - durations.get( e ) ) > eps )
				delta3 = Math.min( delta3, jobMapping.get( e ).getMaxDuration() - durations.get( e ) );
		}
		System.out.println( "Delta 3: " + delta3 );

		double delta = Math.min( delta1, Math.min( delta2, delta3 ) );
		System.out.println( "Verkürzung um: " + delta );


		System.out.println( "Calculating Steigung der Kostenrate" );
		double cost = 0;
		for( Edge e : ek.getOutgoingCut() ) {
			if( Math.abs( durations.get( e ) - jobMapping.get( e ).getMinDuration() ) > eps ) {
				cost += jobMapping.get( e ).getCostRate();
			}
		}
		for( Edge e : ek.getIncomingCut() ) {
			if( Math.abs( jobMapping.get( e ).getMaxDuration() - durations.get( e ) ) > eps ) {
				cost -= jobMapping.get( e ).getCostRate();
			}
		}
		System.out.println( "Kostenrate des Schnitts: " + cost );


		System.out.println( "Aktualisiere dauern:" );
		System.out.println( durations );
		//for( Edge e : AoE.allEdges() ) {
		//
		//}
		for( Edge e : ek.getOutgoingCut() ) { // hier sind nur kritische drin, das heißt, überprüfung muss nicht durchgeführt werden
			durations.set( e, durations.get( e ) - delta );
		}
		for( Edge e : ek.getIncomingCut() ) {
			if( Math.abs ( durations.get( e ) - jobMapping.get( e ).getMaxDuration() ) > eps )
				durations.set( e, durations.get( e ) + delta );
		}

		System.out.println( durations );

		curve.add( new Tuple<> ( lst.get( AoE.getNode( AoE.numberOfNodes()-1) ), (int)cost ) );
	}
}
