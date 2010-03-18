/**
 * flow.java
 * Created: 17.03.2010, 14:33:35
 */
package zet;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.LongestShortestPathTimeHorizonEstimator;
import algo.graph.dynamicflow.eat.SEAAPAlgorithm;
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
import de.tu_berlin.math.coga.common.algorithm.AlgorithmTerminatedEvent;
import de.tu_berlin.math.coga.common.util.Formatter;
import ds.GraphVisualizationResult;
import ds.graph.IdentifiableIntegerMapping;
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
	GraphVisualizationResult graphVisResult;
	IdentifiableIntegerMapping<Node> xPos;
	IdentifiableIntegerMapping<Node> yPos;
	PathBasedFlowOverTime df;
	int neededTimeHorizon;
	int percentInterval = 100;

	public static void main ( String[] args ) throws JSAPException, IOException {
		System.out.println( "flow 0.1.4" );

		JSAP jsap = new JSAP();

		UnflaggedOption inputFile = new UnflaggedOption( "flowfile" )
						.setStringParser( JSAP.STRING_PARSER )
						.setRequired( false );
		inputFile.setHelp( "" );
		jsap.registerParameter( inputFile );

		UnflaggedOption outputFile = new UnflaggedOption( "outputfile" )
						.setStringParser( JSAP.STRING_PARSER )
						.setRequired( false );
		outputFile.setHelp( "" );
		jsap.registerParameter( outputFile );

		FlaggedOption optProperty = new FlaggedOption( "outputFormat" ).setStringParser( JSAP.STRING_PARSER )
						.setRequired( false )
						.setShortFlag( 'o' );
		optProperty.setHelp( "The output file format. Can be dot or flow or xml" );
		jsap.registerParameter( optProperty );

		JSAPResult config = jsap.parse( args );
		if( !config.success() || !config.contains( "flowfile" ) ) {
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
				theInstance.xPos = new IdentifiableIntegerMapping<Node>( 0 );
				theInstance.yPos = new IdentifiableIntegerMapping<Node>( 0 );
				theInstance.eafp = DatFileReaderWriter.read( filename, theInstance.xPos, theInstance.yPos );
				// version without x and y positions:
				//				theInstance.eafp = DatFileReaderWriter.read( filename );
			} catch( FileNotFoundException ex ) {
				System.err.println( "File '" + filename + "' not found." );
				System.exit( 1 );
			}
		} else if( filename.endsWith( ".xml" ) ) {
			// Use xml-format
			System.err.println( "XML not supported yet." );
		}

		theInstance.compute();

		theInstance.graphVisResult = new GraphVisualizationResult( theInstance.eafp, theInstance.xPos, theInstance.yPos, theInstance.df );
		theInstance.graphVisResult.setNeededTimeHorizon( theInstance.neededTimeHorizon );

		if( config.contains( "outputFormat" ) ) {
			String outFilename;
			if( !config.contains( "outputfile" ) ) {
				System.out.println( "No output file specified. Using input filename as output." );
				outFilename = filename.substring( 0, filename.length()-4 ) + ".flow";
			} else
				outFilename = config.getString( "outputfile" );
			final String format = config.getString( "outputFormat" );
			if( format.equals( "flow" ) ) {
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
				System.err.println( "XML-out not supported yet." );
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

	public void eventOccurred( AlgorithmEvent event ) {
		if( event instanceof AlgorithmProgressEvent ) {
			if( index++ == percentInterval ) {
				System.out.println( '\n' + Formatter.formatPercent( ((AlgorithmProgressEvent)event).getProgress() ));
				index = 1;
			} else
				System.out.print( '.' );
		} else if( event instanceof AlgorithmStartedEvent )
			System.out.println( "Algorithm starts." );
		else if( event instanceof AlgorithmTerminatedEvent ) {
			System.out.println( "" );
			try {
				System.out.println( "Runtime flow computation: " + Formatter.formatTimeMilliseconds( event.getAlgorithm().getRuntime() ) );
			} catch( IllegalStateException ex ) {
				System.out.println( "The illegal state exception occured once again." );
			}
		} else
			System.out.println( event.toString() );
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
