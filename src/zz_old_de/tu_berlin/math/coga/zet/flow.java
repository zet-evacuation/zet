/**
 * flow.java
 * Created: 17.03.2010, 14:33:35
 */
package zz_old_de.tu_berlin.math.coga.zet;

import org.zetool.netflow.io.DatFileReaderWriter;
import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import org.zetool.netflow.dynamic.LongestShortestPathTimeHorizonEstimator;
import org.zetool.netflow.dynamic.earliestarrival.SEAAPAlgorithm;
import batch.tasks.graph.SuccessiveEarliestArrivalAugmentingPathOptimizedTask;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;
import com.thoughtworks.xstream.XStream;
import org.zetool.netflow.dynamic.eatapprox.EarliestArrivalFlowPattern;
import org.zetool.netflow.dynamic.eatapprox.EarliestArrivalFlowPatternBuilder;
import org.zetool.netflow.classic.maxflow.PushRelabelHighestLabelGlobalGapRelabelling;
import org.zetool.common.algorithm.Algorithm;
import org.zetool.common.algorithm.AlgorithmEvent;
import org.zetool.common.algorithm.AlgorithmListener;
import org.zetool.common.algorithm.AlgorithmProgressEvent;
import org.zetool.common.algorithm.AlgorithmStartedEvent;
import org.zetool.common.algorithm.AlgorithmStatusEvent;
import org.zetool.common.algorithm.AlgorithmTerminatedEvent;
import org.zetool.common.util.Formatter;
import org.zetool.common.util.units.Quantity;
import org.zetool.common.util.units.TimeUnits;
import zz_old_de.tu_berlin.math.coga.graph.io.dimacs.DimacsReader;
import de.tu_berlin.math.coga.graph.io.xml.XMLReader;
import de.tu_berlin.math.coga.graph.io.xml.XMLWriter;
import de.tu_berlin.math.coga.graph.io.xml.visualization.GraphVisualization;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import ds.GraphVisualizationResults;
import org.zetool.graph.Node;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import org.zetool.netflow.classic.problems.MaximumFlowProblem;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.netflow.dynamic.earliestarrival.old.EATransshipmentSSSP;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import zet.tasks.GraphAlgorithmEnumeration;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class flow implements AlgorithmListener {
	private static enum ComputationMode {
		EarliestArrivalFlow,
		StaticMaximumFlow;
	}
	private static enum ProjectConverter {
		DefaultConverter,
		Spanner;
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
	GraphVisualization graphView = null;

	// General stuff
	String inputFileName;
	ComputationMode computationMode;

	public static void main ( String[] args ) throws JSAPException, IOException {
		System.out.println( "flow 0.3.2 based on ZET 1.1.0 beta" );

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

		Switch switchZeroNodeCapacities = new Switch( "zero" ).setShortFlag( 'z' ).setLongFlag( "zeroNodeCapacities" );
		switchZeroNodeCapacities.setHelp( "Use zero node capacities." );
		jsap.registerParameter( switchZeroNodeCapacities );


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

		if( config.contains( "zero" ) ) {
			DatFileReaderWriter.zeroNodeCapacities = config.getBoolean( "zero" );
		}

		// Scan the input file type by its ending

		if( theInstance.inputFileName.endsWith( ".dat" ) ) {
			// Use internal dat format
			System.out.println( "Reading from dat-file." );
			try {
				// .dat files must contain node positions
				theInstance.nodePositionMapping = new NodePositionMapping();
				theInstance.xPos = new IdentifiableIntegerMapping<>( 0 );
				theInstance.yPos = new IdentifiableIntegerMapping<>( 0 );
				theInstance.eafp = DatFileReaderWriter.read( theInstance.inputFileName, theInstance.nodePositionMapping ); // new .dat-format
				theInstance.graphView = new GraphVisualization( theInstance.eafp, theInstance.nodePositionMapping );
				theInstance.computationMode = ComputationMode.EarliestArrivalFlow;
				// version without x and Years positions:
				//theInstance.eafp = DatFileReaderWriter.readOld( theInstance.inputFileName ); // old .dat-format
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
			theInstance.computationMode = ComputationMode.EarliestArrivalFlow;
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
				theInstance.graphVisResult.setTimeHorizon( theInstance.neededTimeHorizon );
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

		System.out.println( "Evacuees: " + eafp.getTotalSupplies() );

//		// Try to compute a quickest transshipment
//		QuickestTransshipment qt = new QuickestTransshipment(  );
//		qt.setProblem( eafp );
//		qt.run();
//
//		System.out.println( "\nMinCost:" );
//
//		EATransshipmentMinCost eatMin = new EATransshipmentMinCost( );
//		eatMin.setProblem( eafp );
//		eatMin.run();
//
		System.out.println( "\nSSSPCost:" );
		EATransshipmentSSSP eatSSSP = new EATransshipmentSSSP( );
		eatSSSP.setProblem( eafp );
		eatSSSP.run();
//
//		if( 1 == 1 )
//			return;

		System.out.println( eafp.getNetwork().toString() );

		SEAAPAlgorithm algo = new SEAAPAlgorithm();
    //SuccessiveEarliestArrivalAugmentingPathAlgorithm algo = new SuccessiveEarliestArrivalAugmentingPathAlgorithm();

    eafp.setTimeHorizon( 1000 );

		algo.setProblem( eafp );
		//algo.addAlgorithmListener( this );
		try {
			algo.run();
		} catch( IllegalStateException e ) {
			//System.err.println( "The illegal state exception occured." );
		}

		PathBasedFlowOverTime df = algo.getSolution().getPathBased();
		int neededTimeHorizon = algo.getSolution().getTimeHorizon() - 1;
		Logger.getGlobal().info( "Total cost: " + algo.getSolution().getTotalCost() );
		Logger.getGlobal().info( "Time horizon:" + neededTimeHorizon );
		Logger.getGlobal().info( "Flow amount: " + algo.getSolution().getFlowAmount() );
		Logger.getGlobal().info( "Runtime: " + algo.getRuntimeAsString() );

		System.out.println( "Path-Decomposition: " );
		//System.out.println( df.toString( eafp.getTransitTimes() ) );

		EarliestArrivalFlowPattern pattern = EarliestArrivalFlowPatternBuilder.fromPathBased( df, eafp.getTransitTimes(), neededTimeHorizon );

		//System.out.println( pattern );

		if( 1 == 1 )
			return;

		// Fluss bestimmen
		System.out.println( "Compute earliest arrival flow..." );

		GraphAlgorithmEnumeration graphAlgorithm = GraphAlgorithmEnumeration.SuccessiveEarliestArrivalAugmentingPathOptimized;

		//int maxTime = (int) PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" );
		int maxTime = 600;
		Algorithm<NetworkFlowModel, PathBasedFlowOverTime> gt = null;
		gt = new SuccessiveEarliestArrivalAugmentingPathOptimizedTask();
		//gt = graphAlgorithm.createTask( maxTime );

		//gt.setProblem( cav.getSolution() );
		gt.addAlgorithmListener( this );
		gt.run();

			//NetworkFlowModel nfm = new NetworkFlowModel();


		//System.out.println( "Noides: " + eafp.getGraph().numberOfNodes() );
		//System.out.println( "Edges: " + eafp.getGraph().numberOfEdges() );

		System.out.println( "NFM: " );
		System.out.println( "EAFP: " + pattern.toString() );


		boolean seaapa = false;



//		if( seaapa ) {
			//SEAAPAlgorithm algo = new SEAAPAlgorithm();
			algo.setProblem( eafp );
			algo.addAlgorithmListener( this );
			try {
				algo.run();
			} catch( IllegalStateException e ) {
				System.err.println( "The illegal state exception occured." );
			}

			df = algo.getSolution().getPathBased();
			neededTimeHorizon = algo.getSolution().getTimeHorizon();
			System.out.println( "Total cost: " + algo.getSolution().getTotalCost() );
			System.out.println( "Time horizon:" + neededTimeHorizon );
			System.out.println( "Flow amount: " + algo.getSolution().getFlowAmount() );


//			System.out.println( "\nNext Algorithm:" );
//			SuccessiveEarliestArrivalAugmentingPathAlgorithm algo2 = new SuccessiveEarliestArrivalAugmentingPathAlgorithm();
//			algo2.setProblem( eafp );
//			algo2.addAlgorithmListener( this );
//			try {
//				algo2.run();
//			} catch( IllegalStateException e ) {
//				System.err.println( "The illegal state exception occured." );
//			}
//
//			df = algo2.getSolution().getPathBased();
//			neededTimeHorizon = algo2.getSolution().getTimeHorizon()-1;
//			System.out.println( "Total cost: " + algo2.getSolution().getTotalCost() );
//			System.out.println( "Time horizon:" + neededTimeHorizon );
//			System.out.println( "Flow amount: " + algo2.getSolution().getFlowAmount() );

			//		} else {
//			SuccessiveEarliestArrivalAugmentingPathAlgorithmTH algo = new SuccessiveEarliestArrivalAugmentingPathAlgorithmTH( eafp.getGraph(), eafp.getTransitTimes(), eafp.getEdgeCapacities(), eafp.getNodeCapacities(), eafp.getSupplies(), 16 );

//
//				EATransshipmentWithTHSSSP staticalgo = new EATransshipmentWithTHSSSP();
//
//				DynamicFlowProblem dfp = new DynamicFlowProblem( eafp.getEdgeCapacities(), eafp.getGraph(), eafp.getTransitTimes() );
//				//dfp.setCapacities( eafp.getEdgeCapacities() );
//				//dfp.setNetwork( eafp.getGraph() );
//				//dfp.setTransitTimes( eafp.getTransitTimes() );
//
//				eafp.setTimeHorizon( 43 );
//
//				staticalgo.setProblem( eafp );
//
//				//staticalgo.setProblem( dfp );
//
//				staticalgo.run();
//
//				Algorithm<?extends DynamicTransshipmentProblem,? extends FlowOverTimeInterface> a;
//				a = algo;
//				//a = algo2;
//				a = staticalgo;
//
////      Algorithm with implicit time expanded network
////			SuccessiveEarliestArrivalAugmentingPathAlgorithm algo = new SuccessiveEarliestArrivalAugmentingPathAlgorithm();
////			algo.setProblem( eafp );
////			algo.addAlgorithmListener( this );
////			try {
////				algo.run();
////			} catch( IllegalStateException e ) {
////				System.err.println( "The illegal state exception occured." );
////			}
//
////		}
	}

	private void computeMaximumFlow() {

		DimacsReader dl = new DimacsReader( inputFileName );
		start = new Quantity<>( System.nanoTime(), TimeUnits.NanoSeconds );

		dl.load();
		end = new Quantity<>( System.nanoTime(), TimeUnits.NanoSeconds );
		System.out.println( "Loading: " + end.sub( start ) );

		MaximumFlowProblem mfp = dl.getMaximumFlowProblem();
		PushRelabelHighestLabelGlobalGapRelabelling hipr = new PushRelabelHighestLabelGlobalGapRelabelling();
		hipr.setProblem( mfp );
		start = new Quantity<>( System.nanoTime(), TimeUnits.NanoSeconds );
		hipr.run();
		end =  new Quantity<>( System.nanoTime(), TimeUnits.NanoSeconds );
		System.out.println( "Init: " + Formatter.formatUnit( hipr.getInitTime(), TimeUnits.NanoSeconds ) );
		System.out.println( "MaxFlow: " + Formatter.formatUnit( hipr.getPhase1Time(), TimeUnits.NanoSeconds ) );
		System.out.println( "Cut: " + Formatter.formatUnit( hipr.getPhase2Time(), TimeUnits.NanoSeconds ) );
		System.out.println( "" );
		System.out.println( "Overall: " + end.sub( start ) );
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

	Quantity<TimeUnits> end;
	Quantity<TimeUnits> start = null;
	Quantity<TimeUnits> pathDecompositionStart = null;
	@Override
	public void eventOccurred( AlgorithmEvent event ) {
		if( event instanceof AlgorithmProgressEvent ) {
			if( index++ == percentInterval ) {
				System.out.println( '\n' + Formatter.formatPercent( ((AlgorithmProgressEvent)event).getProgress() ));
				index = 1;
			} else {
				System.out.print( '.' );
			} if( (int)(((AlgorithmProgressEvent)event).getProgress() * 100) == 100 )
				System.out.println( "\n100%" );
		} else if( event instanceof AlgorithmStartedEvent ) {
			System.out.println( "Algorithm starts." );
			start = event.getEventTime();
		} else if( event instanceof AlgorithmTerminatedEvent ) {
			System.out.println( "" );
			final Quantity<TimeUnits> end = event.getEventTime();
			System.out.println( "PathDecomposition runtime: " + end.sub( pathDecompositionStart ) );
			try {
				System.out.println( "Overall runtime flow computation: " + event.getAlgorithm().getRuntime() );
			} catch( IllegalStateException ex ) {
				System.out.println( "The illegal state exception occured once again." );
			}
			System.out.println( "Fraction of path decomposition: " + ( Formatter.formatPercent( (end.sub( pathDecompositionStart ).getValue())/(double)event.getAlgorithm().getRuntime().getValue() ) ) );
		} else if( event instanceof AlgorithmStatusEvent ) {
			if( ((AlgorithmStatusEvent)event).getMessage().equals( "INIT_PATH_DECOMPOSITION" ) ) {
				pathDecompositionStart = event.getEventTime();
				System.out.println( "\nSEAAP runtime: " + pathDecompositionStart.sub( start ) );
			}
		} else
			;//System.out.println( event.toString() );
	}

	private void performAction( String mode ) {
		if( mode.equals( "compute" ) ) {
			theInstance.compute();
		} else if( mode.equals( "convert" ) ) {
			System.out.println( "Only converting..." );
		}
	}

}
