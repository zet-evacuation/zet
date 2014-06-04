/**
 * DimacsReader.java
 * Created: Oct 5, 2010,3:24:55 PM
 */
package de.tu_berlin.math.coga.graph.io.dimacs;

import de.tu_berlin.coga.netflow.classic.maxflow.EdmondsKarp;
import de.tu_berlin.coga.netflow.classic.maxflow.PushRelabel;
import de.tu_berlin.coga.netflow.classic.maxflow.PushRelabelHighestLabel;
import de.tu_berlin.coga.common.algorithm.AlgorithmEvent;
import de.tu_berlin.coga.common.algorithm.AlgorithmListener;
import de.tu_berlin.coga.common.algorithm.AlgorithmProgressEvent;
import de.tu_berlin.coga.common.util.Formatter;
import de.tu_berlin.coga.common.util.units.BinaryUnits;
import de.tu_berlin.coga.common.util.units.TimeUnits;
import de.tu_berlin.math.coga.graph.generator.RMFGEN;
import de.tu_berlin.math.coga.rndutils.distribution.discrete.UniformDistribution;
import de.tu_berlin.coga.graph.Edge;import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import de.tu_berlin.coga.graph.Node;
import de.tu_berlin.coga.netflow.ds.flow.MaximumFlow;
import de.tu_berlin.coga.graph.DefaultDirectedGraph;
import de.tu_berlin.coga.graph.DirectedGraph;
import de.tu_berlin.coga.netflow.classic.problems.MaximumFlowProblem;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class DimacsReader implements AlgorithmListener {
	BufferedReader reader;
	BufferedWriter writer;
	protected DefaultDirectedGraph network;
	ArrayList<Node> vertices = null;
	String filename = "network.max";
	boolean simpleTerminals = false;
	boolean verbose = false;
  private IdentifiableIntegerMapping<Edge> capacities;
	MaximumFlowProblem mfp;

	/**
	 * Creates a new instance of {@code DimacsLoader}.
	 * @param filename
	 * @param network
	 */
	public DimacsReader( String filename ) {
		this.filename = filename;
	}

	boolean problemOccured = false;
	int nodes = 0;
	int edges = 0;
	int source = -1;
	int sink = -1;
	int edgesAlreadyRead = 0;

	public void load() {
		try {
			reader = new BufferedReader( new FileReader( new File( filename ) ), 10000);
		} catch( IOException ex ) {
			System.out.println( "Datei konnte nicht zum lesen geöffnet werden!" );
			return;
		}

		try {
			String line = reader.readLine();
			while( line  != null ) {
				final StringTokenizer st = new StringTokenizer( line );
				//Format.sprintf( line, os );


				final String indicator = st.hasMoreTokens() ? st.nextToken() : "";
				if( indicator.equals( "c" ) ) {
					readComment( line );
				} else if( indicator.equals( "p" ) ) {
					readProblem( st );
				} else if( indicator.equals( "n" ) ) {
					readNode( st );
				} else if( indicator.equals( "a" ) ) {
					readEdge( st );
				}
				line = reader.readLine();
			}
		} catch( IOException ex ) {
			System.err.println( "Exception during DimacsLoader loaded file from " + filename );
		}
		if( edgesAlreadyRead != edges )
			throw new IllegalArgumentException( "Edges not of the specified size" );

		long memStart = rt.totalMemory() - rt.freeMemory();
		for( int i = 0; i < edges; ++i ) {
			//if( caps[i] != 0 ) {
				final Edge e = network.createAndSetEdge( network.getNode( starts[i] ), network.getNode( ends[i] ) );
				capacities.add( e, caps[i] );
			//}
			if( caps[i] == 0 ) {
				//network.setHidden( e, true );
			}
		}
		long memEnd = rt.totalMemory() - rt.freeMemory();

		if( verbose ) {
			System.out.println( "Memory for edges: " + Formatter.formatUnit( memEnd-memStart, BinaryUnits.Byte ) ) ;
			System.out.println( "Estimated: " + Formatter.formatUnit( edges*(40+40), BinaryUnits.Byte ) );
			System.out.println( "Space per edge: " + (memEnd-memStart) / edges );
		}

		caps = null;
		ends = null;
		starts = null;

		//network.setEdges( edgesList );
		//for( Edge e : edgesList ) {
		//	network.createAndSetEdge( e.start(), e.end() );
		//}

		System.out.println();
		createInstance();
	}

	/**
	 * Called if a comment line is read. The comment is given out without the
	 * leading comment 'c'.
	 * @param line the complete line read from the file
	 */
	protected void readComment( String line ) {
		if( verbose )
			System.out.println( line.substring( 1 ).trim() );
	}

	protected void readProblem( StringTokenizer st ) {
		if( problemOccured )
			throw new IllegalArgumentException( "More than one problem line" );
		problemOccured = true;
		if( !st.nextToken().equals( "max" ) )
			throw new IllegalArgumentException( "No max flow file" );
		nodes = Integer.parseInt( st.nextToken() );
		edges = Integer.parseInt( st.nextToken() );
		//System.out.print( "Create AbstractNetwork..." );


		long memStart = rt.totalMemory() - rt.freeMemory();
		network = new DefaultDirectedGraph( nodes, edges );
		long memEnd = rt.totalMemory() - rt.freeMemory();
		if( verbose )
			System.out.println( "Memory for network: " + Formatter.formatUnit( memEnd-memStart, BinaryUnits.Byte ) ) ;


		memStart = rt.totalMemory() - rt.freeMemory();
		capacities = new IdentifiableIntegerMapping<>( edges );
		memEnd = rt.totalMemory() - rt.freeMemory();
		if( verbose )
			System.out.println( "Memory for capacities: " + Formatter.formatUnit( memEnd-memStart, BinaryUnits.Byte ) ) ;

		memStart = rt.totalMemory() - rt.freeMemory();
		caps = new int[edges];
		starts = new int[edges];
		ends = new int[edges];
		memEnd = rt.totalMemory() - rt.freeMemory();
		if( verbose ) {
			System.out.println( "Memory for arrays: " + Formatter.formatUnit( memEnd-memStart, BinaryUnits.Byte ) ) ;
			System.out.println( "Estimated for arrays: " + Formatter.formatUnit( 8*edges , BinaryUnits.Byte) );
		}


		//System.out.println( " done." );
	}

	protected void readNode( StringTokenizer st ) {
		if( !problemOccured )
			throw new IllegalArgumentException( "No problem defined before node descriptor." );
		final int id = Integer.parseInt( st.nextToken() );
		if( id < 1 || id > nodes )
			throw new IllegalArgumentException( "Illegal ID '" + id + "' in node line ");
		String next = st.nextToken();
		if( next.equals( "s" ) ) {
			if( source != -1 )
				throw new IllegalArgumentException( "Two source node descriptors" );
			source = id;
		} else if( next.equals( "t" ) ) {
			if( sink != -1 )
				throw new IllegalArgumentException( "Two sink node descriptors" );
			sink = id;
		} else
			throw new IllegalArgumentException( "Illegal node descriptor" );
	}

	int last = 0;

	int[] caps;
	int[] starts;
	int[] ends;

	protected void readEdge( StringTokenizer st ) {
		if( !problemOccured || source == -1 || sink == -1 )
			throw new IllegalArgumentException( "No problem line or source or sink defined before arc." );
		final int start = Integer.parseInt( st.nextToken() );
		final int end = Integer.parseInt( st.nextToken() );
		final int capacity = Integer.parseInt( st.nextToken() );
		if( capacity < 0 )
			throw new IllegalArgumentException( "No infinity capacity is allowed" );
		if( simpleTerminals && start == sink )
			throw new IllegalArgumentException( "Sink has outgoing edge" );
		if( simpleTerminals && end == source )
			throw new IllegalArgumentException( "Source has incoming edge" );
		if( start < 1 || start > nodes )
			throw new IllegalArgumentException( "Illegal start vertex '" + start + "'" );
		if( end < 1 || end > nodes )
			throw new IllegalArgumentException( "Illegal end vertex '" + end + "'" );
		if( start == end )
			throw new IllegalArgumentException( "Self arcs are not allowed: '" + start + "'" );
		//if( network.existsEdge( network.getNode( start-1 ), network.getNode( end-1 ) ) )
		//	throw new IllegalArgumentException( "No parallel arcs allowed." );
		caps[edgesAlreadyRead] = capacity;
		starts[edgesAlreadyRead] = start-1;
		ends[edgesAlreadyRead] = end-1;
		edgesAlreadyRead++;
	}

	protected void createInstance() {
		mfp = new MaximumFlowProblem( network, capacities, network.getNode( source-1 ), network.getNode( sink-1 ) );
	}

	public DirectedGraph getGraph() {
		return network;
	}

	public IdentifiableIntegerMapping<Edge> getCapacities() {
		return capacities;
	}

	public MaximumFlowProblem getMaximumFlowProblem() {
		return mfp;
	}

	long solution;

	public void loadSolution() {
		try {
			reader = new BufferedReader( new FileReader( new File( filename.substring( 0, filename.length()-3 ) + "sol" ) ), 10000);
		} catch( IOException ex ) {
			//System.out.println( "Datei konnte nicht zum lesen geöffnet werden!" );
			solution = -1;
			return;
		}

		try {
			String line = reader.readLine();
			while( line  != null ) {
				final StringTokenizer st = new StringTokenizer( line );
				//Format.sprintf( line, os );


				final String indicator = st.hasMoreTokens() ? st.nextToken() : "";
				if( indicator.equals( "c" ) ) {
					readComment( line );
				} else if( indicator.equals( "s" ) ) {
					// solution line, like "c 3"
					long sol = Long.parseLong( st.nextToken() );
					solution = sol;
				} 
				line = reader.readLine();
			}
		} catch( IOException ex ) {
			System.err.println( "Exception during DimacsLoader loaded file from " + filename + " (solution)" );
		}
	}

	public long getSolution() {
		return solution;
	}

	

	/**
	 * Writes the specified network to the specified file.
	 */
//	public void write() {
//		try {
//			if( writer == null )
//				openWriter();
//
//			// problem line
//			writeLine( "p max " + network.vertexCount() + " " + network.edgesAlreadyRead() );
//
//			// node descriptors
//			writeLine( "n " + (network.getSource().getId() + 1) + " Seconds" );
//			writeLine( "n " + (network.getSink().getId() + 1) + " t" );
//
//			// edges
//			Iterator<E> iter = network.edgeIterator();
//			while( iter.hasNext() ) {
//				final E e = iter.next();
//				writeLine( "a " + (e.getStart().getId() + 1) + " " + (e.getEnd().getId() + 1) + " " + e.getMaxCapacity() );
//			}
//
//			writer.flush();
//		} catch( IOException ex ) {
//			System.out.println( "Fehler beim Zugriff auf " + filename + "!" );
//		}
//	}
//
//	public void writeComment( String text ) {
//		try {
//			if( writer == null )
//				openWriter();
//
//			writeLine( "c " + text );
//
//			writer.flush();
//		} catch( IOException ex ) {
//			System.out.println( "Fehler beim Zugriff auf " + filename + "!" );
//		}
//	}
//
//	private void openWriter() throws IOException {
//		writer = new BufferedWriter( new FileWriter( new File( filename ) ) );
//		writeInitText();
//	}
//
//	private void writeInitText() throws IOException {
//		writeLine( "c written by DimacsLoader from Kap Graph Framework" );
//	}
//
//	/**
//	 * Writes a line to the {@link writer }
//	 * @param text the text that is written
//	 * @throws java.io.IOException if anything goes wrong
//	 */
//	private void writeLine( String text ) throws IOException {
//		writer.write( text );
//		writer.newLine();
//	}
	static Runtime rt = Runtime.getRuntime();

	public static void main( String[] arguments ) {
		long end;
		DimacsReader dl = new DimacsReader( "./testinstanz/blatt08.max" );
		//DimacsReader dl = new DimacsReader( "./testinstanz/maxflow/BVZ-tsukuba/BVZ-tsukuba0.max" );
		//DimacsReader dl = new DimacsReader( "./testinstanz/maxflow/BVZ-venus/BVZ-venus12.max" );
		//DimacsReader dl = new DimacsReader( "./testinstanz/maxflow/KZ2-tsukuba/KZ2-tsukuba12.max" );
		//DimacsReader dl = new DimacsReader( "./testinstanz/maxflow/BL06-gargoyle-sml/BL06-gargoyle-sml.max" );
		long memStart = rt.totalMemory() - rt.freeMemory();
		System.out.println( "Free Memory: " + Formatter.formatUnit( rt.freeMemory(), BinaryUnits.Byte ) );
		long start = System.nanoTime();

		dl.verbose = true;
		dl.load();
		rt.gc();
		end = System.nanoTime();
		long memEnd = rt.totalMemory() - rt.freeMemory();
		System.out.println( Formatter.formatUnit( end-start, TimeUnits.NanoSeconds ) );
		System.out.println( "Memory: " + Formatter.formatUnit(memEnd - memStart, BinaryUnits.Byte ) );
		System.out.println( "Loading complete" );
		System.out.println( "Free Memory: " + Formatter.formatUnit( rt.freeMemory(), BinaryUnits.Byte ) );

		UniformDistribution dist = new UniformDistribution( 1, 20);
		//BinomialDistribution dist = new BinomialDistribution( 10, 50, 0.1 );

		RMFGEN gen = new RMFGEN();
		gen.setDistribution( dist );
		//gen.generateCompleteGraph( 35,15 );
		gen.generateCompleteGraph( 3,3 ); // smallest example where algorithm fails is 4,6
		System.out.println( "RMFGEN Knoten: " + gen.getNodeCount() + ", Kanten: " + gen.getEdgeCount() );

		DirectedGraph network;
		network = dl.getGraph();
		//network = gen.getGraph();

		//System.out.println( network.toString() );
		MaximumFlowProblem mfp;
		mfp = dl.getMaximumFlowProblem();
		//mfp = new MaximumFlowProblem( network, gen.getCapacities(), gen.getSource(), gen.getSink() );

		MaximumFlow mf;

//		System.out.println();
//		System.out.println( "Start algorithm 1" );
//		PreflowPushAlgorithm ppa = new PreflowPushAlgorithm();
//		ppa.setProblem( mfp );
//		//ppa.setLogLevel( true );
//		start = System.nanoTime();
//		ppa.run();
//		end = System.nanoTime();
//		mf = ppa.getSolution();
////
//		System.out.println( "Flow value: " + mf.getFlowValue() );
//		System.out.println( "Pushes: " + ppa.getPushes() + ", Relabels: " + ppa.getRelabels() );
//		System.out.println( Formatter.formatTimeNanoseconds( end-start) );
//		//System.out.println( mf.toString() );

//		System.out.println();
//		System.out.println( "Start algorithm 2" );
//		GenericPushRelabelAlgorithm gpra = new GenericPushRelabelAlgorithm();
//		gpra.setProblem( mfp );
//		gpra.setLogLevel( false );
//		start = System.nanoTime();
//		gpra.run();
//		end = System.nanoTime();
//		mf = gpra.getSolution();
//		System.out.println( "Flow value: " + mf.getFlowValue() );
//		System.out.println( "Pushes: " + gpra.getPushes() + ", Relabels: " + gpra.getRelabels() );
//		System.out.println( Formatter.formatTimeNanoseconds( end-start) );
//		//System.out.println( mf.toString() );

		int ekf = 0;
		System.out.println();
		System.out.println( "Start edmonds karp" );
		EdmondsKarp ek = new EdmondsKarp();
		ek.setAccuracy( 1.0 );
		ek.addAlgorithmListener( dl );
		ek.setProblem( mfp );
		start = System.nanoTime();
		ek.run();
		end = System.nanoTime();
		mf = ek.getSolution();
		System.out.println( "Flow value: " + mf.getFlowValue() );
		ekf = mf.getFlowValue();
		System.out.println( "Augmentations: " + ek.getAugmentations() );
		System.out.println( "Pushes: " + ek.getPushes() );
		System.out.println( Formatter.formatUnit( end-start, TimeUnits.NanoSeconds ) );
		

		long hiprf = 0;
		// new algorithm
		System.out.println();
		System.out.println( "Start HIPR" );
		//PushRelabel hipr = new PushRelabelHighestLabelGlobalGapRelabelling();
		//PushRelabel hipr = new PushRelabelHighestLabelGlobalRelabelling();
		//PushRelabel hipr = new PushRelabelHighestLabel();
		PushRelabel hipr = new PushRelabelHighestLabel();
		hipr.setProblem( mfp );
		start = System.nanoTime();
		hipr.run();
		end = System.nanoTime();
		mf = hipr.getSolution();

		System.out.println( "Flow value: " + mf.getFlowValue() );
		System.out.println( "Sink-Arrival value: " + hipr.getFlowValue() );
		hiprf = hipr.getFlowValue();
		System.out.println( "Pushes: " + hipr.getPushes() + ", Relabels: " + hipr.getRelabels() );
		//System.out.println( "Global relabels: " + hipr.getGlobalRelabels() );
		//System.out.println( "Gaps : " + hipr.getGaps() + " Gap nodes: " + hipr.getGapNodes() );
		System.out.println( Formatter.formatUnit( end-start, TimeUnits.NanoSeconds ) );
		System.out.println( "Checking..." );
		hipr.getSolution().check();

		System.out.println();
//
//			// new algorithm
//		System.out.println();
//		System.out.println( "Start HIPR neu" );
//		//PushRelabel hipr = new PushRelabelHighestLabelGlobalGapRelabelling();
//		//hipr = new PushRelabelHighestLabel();
//		hipr = new PushRelabelHighestLabelGlobalGapRelabelling();
//		hipr.setProblem( mfp );
//		start = System.nanoTime();
//		hipr.run();
//		end = System.nanoTime();
//		mf = hipr.getSolution();
//
//		System.out.println( "Flow value: " + mf.getFlowValue() );
//		System.out.println( "Sink-Arrival value: " + hipr.getFlowValue() );
//		hiprf = hipr.getFlowValue();
//		System.out.println( "Pushes: " + hipr.getPushes() + ", Relabels: " + hipr.getRelabels() );
//		//System.out.println( "Global relabels: " + hipr.getGlobalRelabels() );
//		//System.out.println( "Gaps : " + hipr.getGaps() + " Gap nodes: " + hipr.getGapNodes() );
//		System.out.println( Formatter.formatTimeUnit( end - start, TimeUnits.NanoSeconds ) );
//
//		System.out.println();
		}

	@Override
	public void eventOccurred( AlgorithmEvent event ) {
		if( event instanceof AlgorithmProgressEvent ) {
			System.out.println( ((AlgorithmProgressEvent)event).getProgress() );
		} else {
			System.out.println( event.toString() );
		}
	}
}
