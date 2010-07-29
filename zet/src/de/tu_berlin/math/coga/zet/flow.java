/**
 * flow.java
 * Created: 17.03.2010, 14:33:35
 */
package de.tu_berlin.math.coga.zet;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.LongestShortestPathTimeHorizonEstimator;
import algo.graph.dynamicflow.eat.SEAAPAlgorithm;
import algo.graph.nashflow.NodePartition;
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
import de.tu_berlin.math.coga.graph.io.xml.GraphView;
import de.tu_berlin.math.coga.graph.io.xml.XMLReader;
import de.tu_berlin.math.coga.graph.io.xml.XMLWriter;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import ds.GraphVisualizationResults;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Node;
import ds.graph.flow.PathBasedFlowOverTime;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class flow implements AlgorithmListener {
	/** The instance of the earliest arrival problem that should be solved. */
	EarliestArrivalFlowProblem eafp = null;
	static flow theInstance;
	GraphVisualizationResults graphVisResult;
	IdentifiableIntegerMapping<Node> xPos;
	IdentifiableIntegerMapping<Node> yPos;
	IdentifiableObjectMapping<Node, Vector3> nodePositionMapping;
	PathBasedFlowOverTime df;
	int neededTimeHorizon;
	int percentInterval = 100;
	GraphView graphView = null;

	public static void main ( String[] args ) throws JSAPException, IOException {
		System.out.println( "flow 0.2.0" );

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

		theInstance = new flow();

		// Scan the input file type by its ending
		final String filename = config.getString( "flowfile" );
		if( filename.endsWith( ".dat" ) ) {
			// Use internal dat format
			System.out.println( "Reading from dat-file." );
			try {
				// .dat files must contain node positions
				theInstance.nodePositionMapping = new IdentifiableObjectMapping<Node, Vector3>( 0, Vector3.class );
				theInstance.xPos = new IdentifiableIntegerMapping<Node>( 0 );
				theInstance.yPos = new IdentifiableIntegerMapping<Node>( 0 );
				theInstance.eafp = DatFileReaderWriter.read( filename, theInstance.nodePositionMapping );
				theInstance.graphView = new GraphView( theInstance.eafp, theInstance.nodePositionMapping );
				// version without x and y positions:
				//				theInstance.eafp = DatFileReaderWriter.read( filename );
			} catch( FileNotFoundException ex ) {
				System.err.println( "File '" + filename + "' not found." );
				System.exit( 1 );
			}
		} else if( filename.endsWith( ".xml" ) ) {
			// Use xml-format
			System.out.println( "Reading from xml-file." );
			XMLReader reader = new XMLReader( filename );
			theInstance.eafp = reader.readFlowInstance();
			theInstance.graphView = reader.getXmlData().getGraphView();
		}

		theInstance.performAction( config.getString( "mode" ) );

		if( config.contains( "outputFormat" ) ) {
			final String format = config.getString( "outputFormat" );
			String outFilename;
			if( !config.contains( "outputfile" ) ) {
				System.out.println( "No output file specified. Using input filename as output." );
				outFilename = filename.substring( 0, filename.length()-4 ) + "." + format;
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
					System.err.println( "Error writing the file '" + filename + "'");
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
			System.out.println( "No output." );
	}

	public void compute( ) {
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

	int index = 1;

	long start = 0;
	long pathDecompositionStart = 0;
	public void eventOccurred( AlgorithmEvent event ) {
		if( event instanceof AlgorithmProgressEvent ) {
			if( index++ == percentInterval ) {
				System.out.println( '\n' + Formatter.formatPercent( ((AlgorithmProgressEvent)event).getProgress() ));
				index = 1;
			} else
				System.out.print( '.' );
			if( (int)(((AlgorithmProgressEvent)event).getProgress() * 100) == 100 )
				System.out.println( "\n100%" );
		} else if( event instanceof AlgorithmStartedEvent )
			//System.out.println( "Algorithm starts." );
			start = event.getEventTime();
		else if( event instanceof AlgorithmTerminatedEvent ) {
			System.out.println( "" );
			final long end = event.getEventTime();
			System.out.println( "PathDecomposition runtime: " + Formatter.formatTimeMilliseconds( end - pathDecompositionStart ) );
			try {
				System.out.println( "Overall runtime flow computation: " + Formatter.formatTimeMilliseconds( event.getAlgorithm().getRuntime() ) );
			} catch( IllegalStateException ex ) {
				System.out.println( "The illegal state exception occured once again." );
			}
			System.out.println( "Fraction of path decomposition: " + ( Formatter.formatPercent( (end-pathDecompositionStart)/(double)event.getAlgorithm().getRuntime() )) );
		} else if( event instanceof AlgorithmStatusEvent ) {
			if( ((AlgorithmStatusEvent)event).getMessage().equals( "INIT_PATH_DECOMPOSITION" ) ) {
				pathDecompositionStart = event.getEventTime();
				System.out.println( "\nSEAAP runtime: " + Formatter.formatTimeMilliseconds( pathDecompositionStart - start ) );
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


//
//java.lang.NullPointerException
//        at ds.graph.flow.ChainDecomposition2.split(ChainDecomposition2.java:140)
//        at ds.graph.flow.ChainDecomposition2.uncrossPaths(ChainDecomposition2.java:204)
//        at ds.graph.flow.ChainDecomposition2.uncrossPaths(ChainDecomposition2.java:124)
//        at ds.graph.flow.FlowOverTime.<init>(FlowOverTime.java:55)
//        at algo.graph.dynamicflow.eat.SEAAPAlgorithm.runAlgorithm(SEAAPAlgorithm.java:85)
//        at algo.graph.dynamicflow.eat.SEAAPAlgorithm.runAlgorithm(SEAAPAlgorithm.java:37)
//        at de.tu_berlin.math.coga.common.algorithm.Algorithm.run(Algorithm.java:393)
//        at zet.flow.compute(flow.java:155)
//        at zet.flow.main(flow.java:106)
