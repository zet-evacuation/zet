/**
 * flow.java
 * Created: 17.03.2010, 14:33:35
 */
package de.tu_berlin.math.coga.zet;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.LongestShortestPathTimeHorizonEstimator;
import algo.graph.dynamicflow.eat.SEAAPAlgorithm;
import algo.graph.staticflow.maxflow.PushRelabelHighestLabelGlobalGapRelabelling;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import com.thoughtworks.xstream.XStream;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmListener;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmProgressEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmStartedEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmStatusEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmTerminatedEvent;
import de.tu_berlin.math.coga.common.util.Formatter;
import de.tu_berlin.math.coga.common.util.Formatter.TimeUnits;
import de.tu_berlin.math.coga.graph.io.dimacs.DimacsReader;
import de.tu_berlin.math.coga.graph.io.xml.GraphView;
import de.tu_berlin.math.coga.graph.io.xml.XMLReader;
import de.tu_berlin.math.coga.graph.io.xml.XMLWriter;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import ds.GraphVisualizationResults;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Node;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.graph.problem.MaximumFlowProblem;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class flow implements AlgorithmListener {
	private static enum ComputationMode {
		EarliestArrivalFlow,
		StaticMaximumFlow;
	}
	/** The instance of the earliest arrival problem that should be solved. */
	EarliestArrivalFlowProblem eafp = null;
	static flow theInstance;
	GraphVisualizationResults graphVisResult;
	IdentifiableIntegerMapping<Node> xPos;
	IdentifiableIntegerMapping<Node> yPos;
	NodePositionMapping nodePositionMapping;
	PathBasedFlowOverTime df;
	int neededTimeHorizon;
	int percentInterval = 100;
	GraphView graphView = null;

	// General stuff
	String inputFileName;
	ComputationMode computationMode;

	public static void main ( String[] args ) throws JSAPException, IOException {
		System.out.println( "flow 0.3.1" );

		JSAP jsap = new JSAP();

		UnflaggedOption inputFile = new UnflaggedOption( "flowfile" )
						.setStringParser( JSAP.STRING_PARSER )
						.setRequired( true );
		inputFile.setHelp( "A file with the problem specification (a graph or network) that is read." );
		jsap.registerParameter( inputFile );

		UnflaggedOption outputFile = new UnflaggedOption( "outputfile" )
						.setStringParser( JSAP.STRING_PARSER )
						.setRequired( false );
		outputFile.setHelp( "Specifies an output file for the results." );
		jsap.registerParameter( outputFile );

		FlaggedOption optProperty = new FlaggedOption( "outputFormat" ).setStringParser( JSAP.STRING_PARSER )
						.setRequired( false )
						.setShortFlag( 'o' );
		optProperty.setHelp( "The output file format. Can be 'dot' or 'flow' or 'xml'." );
		jsap.registerParameter( optProperty );

		FlaggedOption optMode = new FlaggedOption( "mode" ).setStringParser( JSAP.STRING_PARSER )
						.setRequired( true )
						.setLongFlag( "mode" )
						.setDefault( "compute" );
		optMode.setHelp( "The mode. 'compute' computes a flow, 'convert' only reads and maybe converts to a different format." );
		jsap.registerParameter( optMode );

		JSAPResult config = jsap.parse( args );
		if( !config.success() || !(config.getString( "mode").equals( "convert") || config.getString( "mode" ).equals( "compute" ) ) ) {
			System.err.println();
			for( java.util.Iterator errs = config.getErrorMessageIterator();
							errs.hasNext();) {
				System.err.println( "Error: " + errs.next() );
			}
			System.err.println();
			System.err.println( "Usage: " + " java flow " + jsap.getUsage() );
			System.err.println();
			System.err.println( jsap.getHelp() );
			System.err.println();
			System.exit( 1 );
		}

		theInstance = new flow( config.getString( "flowfile" ) );

		// Scan the input file type by its ending
		
		if( theInstance.inputFileName.endsWith( ".dat" ) ) {
			// Use internal dat format
			System.out.println( "Reading from dat-file." );
			try {
				// .dat files must contain node positions
				theInstance.nodePositionMapping = new NodePositionMapping();
				theInstance.xPos = new IdentifiableIntegerMapping<Node>( 0 );
				theInstance.yPos = new IdentifiableIntegerMapping<Node>( 0 );
				theInstance.eafp = DatFileReaderWriter.read( theInstance.inputFileName, theInstance.nodePositionMapping );
				theInstance.graphView = new GraphView( theInstance.eafp, theInstance.nodePositionMapping );
				theInstance.computationMode = ComputationMode.EarliestArrivalFlow;
				// version without x and Years positions:
				//				theInstance.eafp = DatFileReaderWriter.read( filename );
			} catch( FileNotFoundException ex ) {
				System.err.println( "File '" + theInstance.inputFileName + "' not found." );
				System.exit( 1 );
			}
		} else if( theInstance.inputFileName.endsWith( ".xml" ) ) {
			// Use xml-format
			System.out.println( "Reading from xml-file." );
			XMLReader reader = new XMLReader( theInstance.inputFileName );
			theInstance.eafp = reader.readFlowInstance();
			theInstance.graphView = reader.getXmlData().getGraphView();
		} else if(theInstance.inputFileName.endsWith( ".max" ) ) {
			// use a dimacs challenge max flow
			theInstance.computationMode = ComputationMode.StaticMaximumFlow;
			
		}

		theInstance.performAction( config.getString( "mode" ) );

		if( config.contains( "outputFormat" ) ) {
			final String format = config.getString( "outputFormat" );
			String outFilename;
			if( !config.contains( "outputfile" ) ) {
				System.out.println( "No output file specified. Using input filename as output." );
				outFilename = theInstance.inputFileName.substring( 0, theInstance.inputFileName.length()-4 ) + "." + format;
			} else
				outFilename = config.getString( "outputfile" );
			if( format.equals( "flow" ) ) {
				theInstance.graphVisResult = new GraphVisualizationResults( theInstance.eafp, theInstance.xPos, theInstance.yPos, theInstance.df );
				theInstance.graphVisResult.setNeededTimeHorizon( theInstance.neededTimeHorizon );
				try {
//					if( !outFilename.endsWith( ".flow" ) )
//						outFilename += ".flow";
					PrintWriter output = new PrintWriter( new File( outFilename ) );
					XStream xml_convert = new XStream();
					xml_convert.toXML( theInstance.graphVisResult, output );
				} catch( IOException e ) {
					System.err.println( "Error writing the file '" + theInstance.inputFileName + "'");
					System.exit( 1 );
				}
			} else if( format.equals( "xml" ) ) {
				// try to give out an xml-file. if flow computation was performed, give out the result.
				// if convert only was active, give out the graph.
				if( config.getString( "mode" ).equals( "convert" ) ) {
					XMLWriter writer = new XMLWriter( outFilename );
					if( theInstance.graphView != null ) {
						writer.writeLayoutedNetwork( theInstance.graphView );
					} else
						writer.writeNetwork( theInstance.eafp );
				}
			} else if( format.equals( "dot" ) ) {
				System.err.println( "dot-out not supported yet." );
			}
		} else
			;//System.out.println( "No output." );
	}

	public flow( String inputFileName ) {
		this.inputFileName = inputFileName;
	}

	public void compute( ) {
		switch( computationMode ) {
			case EarliestArrivalFlow:
				computeEarliestArrivalFlow();
				break;
			case StaticMaximumFlow:
				computeMaximumFlow();
				break;
		}
	}

	private void computeEarliestArrivalFlow() {
		if( eafp.getTimeHorizon() <= 0 ) {
			System.out.print( "Estimating time horizon..." );
			LongestShortestPathTimeHorizonEstimator estimator = new LongestShortestPathTimeHorizonEstimator();
			estimator.setProblem( eafp );
			estimator.run();
			eafp.setTimeHorizon( estimator.getSolution().getUpperBound() );
			System.out.println( " done.\nEstimated time horizon: " + estimator.getSolution().getUpperBound() );
		}

		// Fluss bestimmen
		System.out.println( "Compute earliest arrival flow..." );
		SEAAPAlgorithm algo = new SEAAPAlgorithm();
		algo.setProblem( eafp );
		algo.addAlgorithmListener( this );
		try {
			algo.run();
		} catch( IllegalStateException e ) {
			System.err.println( "The illegal state exception occured." );
		}

		df = algo.getSolution().getPathBased();
		neededTimeHorizon = algo.getSolution().getTimeHorizon()-1;
		System.out.println( "Total cost: " + algo.getSolution().getTotalCost() );
	}

	private void computeMaximumFlow() {

		DimacsReader dl = new DimacsReader( inputFileName );
		start = System.nanoTime();

		dl.load();
		end = System.nanoTime();
		System.out.println( "Loading: " + Formatter.formatTimeUnit( end-start, TimeUnits.NanoSeconds ) );

		MaximumFlowProblem mfp = dl.getMaximumFlowProblem();
		PushRelabelHighestLabelGlobalGapRelabelling hipr = new PushRelabelHighestLabelGlobalGapRelabelling();
		hipr.setProblem( mfp );
		start = System.nanoTime();
		hipr.run();
		end = System.nanoTime();
		System.out.println( "Init: " + Formatter.formatTimeUnit( hipr.getInitTime(), TimeUnits.NanoSeconds ) );
		System.out.println( "MaxFlow: " + Formatter.formatTimeUnit( hipr.getPhase1Time(), TimeUnits.NanoSeconds ) );
		System.out.println( "Cut: " + Formatter.formatTimeUnit( hipr.getPhase2Time(), TimeUnits.NanoSeconds ) );
		System.out.println( "" );
		System.out.println( "Overall: " + Formatter.formatTimeUnit( end-start, TimeUnits.NanoSeconds ) );
		System.out.println();
		System.out.println( "Flow value: " + hipr.getFlowValue() );
		System.out.println();
		System.out.println( "Pushes: " + hipr.getPushes() );
		System.out.println( "Relabels: " + hipr.getRelabels() );
		System.out.println( "Global updates: " + hipr.getGlobalRelabels() );
		System.out.println( "Gaps: " + hipr.getGaps() );
		System.out.println( "Gap nodes: " + hipr.getGapNodes() );

		System.out.println();
		dl.loadSolution();
		// check solution and compare with .sol if exists
		if( dl.getSolution() != -1 ) {
			if( dl.getSolution() == hipr.getFlowValue() )
				System.out.println( "Solution checked with .sol and correct." );
			else
				System.out.println( "Solution incorrect!" );
		} else {
			System.out.println( "No solution .sol available." );
		}
	}



	int index = 1;

	long end;
	long start = 0;
	long pathDecompositionStart = 0;
	public void eventOccurred( AlgorithmEvent event ) {
		if( event instanceof AlgorithmProgressEvent ) {
			if( index++ == percentInterval ) {
				System.out.println( '\n' + Formatter.formatPercent( ((AlgorithmProgressEvent)event).getProgress() ));
				index = 1;
			} else {
				System.out.print( '.' );
			} if( (int)(((AlgorithmProgressEvent)event).getProgress() * 100) == 100 )
				System.out.println( "\n100%" );
		} else if( event instanceof AlgorithmStartedEvent )
			//System.out.println( "Algorithm starts." );
			start = event.getEventTime();
		else if( event instanceof AlgorithmTerminatedEvent ) {
			System.out.println( "" );
			final long end = event.getEventTime();
			System.out.println( "PathDecomposition runtime: " + Formatter.formatTimeUnit( end - pathDecompositionStart, TimeUnits.MilliSeconds ) );
			try {
				System.out.println( "Overall runtime flow computation: " + Formatter.formatTimeUnit( event.getAlgorithm().getRuntime(), TimeUnits.MilliSeconds ) );
			} catch( IllegalStateException ex ) {
				System.out.println( "The illegal state exception occured once again." );
			}
			System.out.println( "Fraction of path decomposition: " + ( Formatter.formatPercent( (end-pathDecompositionStart)/(double)event.getAlgorithm().getRuntime() )) );
		} else if( event instanceof AlgorithmStatusEvent ) {
			if( ((AlgorithmStatusEvent)event).getMessage().equals( "INIT_PATH_DECOMPOSITION" ) ) {
				pathDecompositionStart = event.getEventTime();
				System.out.println( "\nSEAAP runtime: " + Formatter.formatTimeUnit( pathDecompositionStart - start, TimeUnits.MilliSeconds ) );
			}
		} else
			System.out.println( event.toString() );
	}


	private void performAction( String mode ) {
		if( mode.equals( "compute" ) ) {
			theInstance.compute();
		} else if( mode.equals( "convert" ) ) {
			System.out.println( "Only converting..." );
		}
	}

}