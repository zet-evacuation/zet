package de.tu_berlin.math.coga.zet;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.SEAAPAlgorithm;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;
import de.tu_berlin.math.coga.batch.input.reader.ZETProjectFileReader;
import de.tu_berlin.math.coga.rndutils.RandomUtils;
import de.tu_berlin.math.coga.zet.converter.graph.GraphAssignmentConverter;
import ds.PropertyContainer;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.z.ConcreteAssignment;
import ds.z.Project;
import gui.AlgorithmControl;
import gui.GraphConverterAlgorithms;
import gui.ZETMain;
import gui.editor.properties.PropertyLoadException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 * A command line interface to ZET
 *
 * @author Jan-Philipp Kappmeier
 */
public class CZET {
	public static enum ComputationMode {
		EarliestArrivalFlow,
		StaticMaximumFlow,
		StaticMinCostFlow,
		EvacuationSimulation;

		static ComputationMode parse( String string ) {
			switch( string ) {
				case "evac":
					return ComputationMode.EvacuationSimulation;
				case "max":
					return ComputationMode.StaticMaximumFlow;
				case "mc":
					return ComputationMode.StaticMinCostFlow;
				case "eat":
					return ComputationMode.EarliestArrivalFlow;
				default:
					return null;
			}
		}
	}

	static GraphConverterAlgorithms parseGraphConverterAlgorithm( String string ) {
		switch( string ) {
			case "rect":
				return GraphConverterAlgorithms.NonGridGraph;
			case "ds":
				return GraphConverterAlgorithms.DijkstraNonGrid;
			case "gs":
				return GraphConverterAlgorithms.GreedyTSpannerNonGrid;
			case "sp":
				return GraphConverterAlgorithms.RepeatedShortestPaths;
			case "thin":
				return GraphConverterAlgorithms.ThinNetwork;
			default:
				return null;
		}
	}
	
	HashMap m = new HashMap(30);

	public static enum InputFileType {
		XML( new ComputationMode[]{ComputationMode.EarliestArrivalFlow} ),
		DimacsMaxFlow( new ComputationMode[]{ComputationMode.StaticMaximumFlow} ),
		ZET( new ComputationMode[]{ComputationMode.EarliestArrivalFlow, ComputationMode.EvacuationSimulation} );
		ArrayList<ComputationMode> supportedModes = new ArrayList<>();

		private InputFileType( ComputationMode[] supportedModes ) {
			this.supportedModes.addAll( Arrays.asList( supportedModes ) );
		}

		public boolean isSupported( ComputationMode computationMode ) {
			return supportedModes.contains( computationMode );
		}
	}

	private static enum OutputFileType {
		XML,
		flow,
		dot;
	}
	private Path inputFile;
	private Path outputFile;
	private InputFileType inputFileType;
	private ComputationMode computationMode;
	private GraphConverterAlgorithms projectConverter;
	private int runs = 0;
	private long seed = System.nanoTime();

	public static void main( String[] arguments ) throws JSAPException {
		System.out.println( "Command Line Interface for ZET " + gui.ZETMain.version );

		JSAP jsap = new JSAP();

		UnflaggedOption inputFileOption = new UnflaggedOption( "inputFile" ).setStringParser( JSAP.STRING_PARSER ).setRequired( true );
		inputFileOption.setHelp( "A file with the problem specification (a graph or network) that is read." );
		jsap.registerParameter( inputFileOption );

		FlaggedOption inputType = new FlaggedOption( "inputFormat" ).setStringParser( JSAP.STRING_PARSER ).setRequired( false ).setShortFlag( 'i' );
		inputType.setHelp( "The input file format." );
		jsap.registerParameter( inputType );

		UnflaggedOption outputFile = new UnflaggedOption( "outputFile" ).setStringParser( JSAP.STRING_PARSER ).setRequired( false );
		outputFile.setHelp( "Specifies an output file for the results." );
		jsap.registerParameter( outputFile );

		FlaggedOption outputType = new FlaggedOption( "outputFormat" ).setStringParser( JSAP.STRING_PARSER ).setRequired( false ).setShortFlag( 'o' );
		outputType.setHelp( "The output file format. Can be 'dot' or 'flow' or 'xml'." );
		jsap.registerParameter( outputType );

		FlaggedOption optMode = new FlaggedOption( "mode" ).setStringParser( JSAP.STRING_PARSER ).setRequired( false ).setLongFlag( "mode" );
		optMode.setHelp( "The mode. 'compute' computes a flow, 'convert' only reads and maybe converts to a different format." );
		jsap.registerParameter( optMode );

		FlaggedOption optNetworkConverter = new FlaggedOption( "buildingPlanConverter" ).setStringParser( JSAP.STRING_PARSER ).setRequired( false ).setLongFlag( "bpc" );
		optNetworkConverter.setHelp( "The converter that creates a network out of a building plan." );
		jsap.registerParameter( optNetworkConverter );

		Switch switchZeroNodeCapacities = new Switch( "zero" ).setShortFlag( 'z' ).setLongFlag( "zeroNodeCapacities" );
		switchZeroNodeCapacities.setHelp( "Use zero node capacities." );
		jsap.registerParameter( switchZeroNodeCapacities );

		FlaggedOption optRuns = new FlaggedOption( "runs" ).setStringParser( JSAP.INTEGER_PARSER ).setRequired( false ).setLongFlag( "runs" ).setShortFlag( 'r' );
		optRuns.setHelp( "The number of runs." );
		jsap.registerParameter( optRuns );

		FlaggedOption optSeed = new FlaggedOption( "seed" ).setStringParser( JSAP.LONG_PARSER ).setRequired( false ).setLongFlag( "seed" ).setShortFlag( 's' );
		optSeed.setHelp( "An initial seed. Used to initialize the random generators for different runs." );
		jsap.registerParameter( optSeed );
		
		JSAPResult config = jsap.parse( arguments );
		if( !config.success() ) {
			System.err.println();
			for( java.util.Iterator errs = config.getErrorMessageIterator();
							errs.hasNext(); )
				System.err.println( "Error: " + errs.next() );
			System.err.println();
			printHelp( jsap );
			System.exit( 1 );
		}

		CZET czet = new CZET();
		czet.setFile( config.getString( "inputFile" ) );
		// Try to load input file


		// Here, we can be sure that arguments have been read correctly
		// try to autodetect file type if no type is set
		if( config.contains( "inputFormat" ) )
			switch( config.getString( "inputFormat" ) ) {
				case "MAX":
					break;
				case "ZET":
					czet.setInputFileType( InputFileType.ZET );
					break;
			}
		else
			// try to autodetect
			switch( czet.getFileEnding().toLowerCase() ) {
				case "zet":
					// Try to load a zet file
					czet.setInputFileType( InputFileType.ZET );
					break;
				default:
					System.out.println( "Unknown file type: " + czet.getFileEnding() );
					printHelp( jsap );
					System.exit( 1 );
			}

		// Set up the problem to solve. Set to default value if no computation is given
		if( config.contains( "mode" ) ) {
			czet.setComputationMode( ComputationMode.parse( config.getString( "mode" ).toLowerCase() ) );
			if( czet.getComputationMode() == null ) {
				System.out.println( "Unknown computation mode: " + config.getString( "mode" ) );
				printHelp( jsap );
				System.exit( 1 );
			}
		} else
			switch( czet.getInputFileType() ) {
				case ZET:
					czet.setComputationMode( ComputationMode.EvacuationSimulation );
			}
		if( !czet.getInputFileType().isSupported( czet.getComputationMode() ) ) {
			System.out.println( "Computation mode " + czet.getComputationMode() + " is not supported for input files of type " + czet.getInputFileType() );
			printHelp( jsap );
			System.exit( 1 );
		}

		// If a building plan converter is set, load it
		if( config.contains( "buildingPlanConverter" ) ) {
			czet.setBuildingPlanConverter( parseGraphConverterAlgorithm( config.getString( "buildingPlanConverter" ).toLowerCase() ) );
			if( czet.getComputationMode() == null ) {
				System.out.println( "Unknown building converter: " + config.getString( "buildingPlanConverter" ) );
				printHelp( jsap );
				System.exit( 1 );
			}
		} else
			czet.setBuildingPlanConverter( GraphConverterAlgorithms.NonGridGraph );

		// Try to get seed and runs
		
		if( config.contains( "runs" ) ) {
			int runs = config.getInt( "runs" );
			System.out.println( "Averaging over " + runs + " runs" );
			czet.setRuns( runs );
		}
		
		
		if( config.contains( "seed" ) ) {
			long seed = config.getLong( "seed" );
			System.out.println( "Using main seed " + seed );
			czet.setSeed( seed );
		}
		
		
		czet.compute();
	}

	private static void printHelp( JSAP jsap ) {
		System.err.println( "Usage: " + " java flow " + jsap.getUsage() );
		System.err.println();
		System.err.println( jsap.getHelp() );
		System.err.println();
	}

	public void setFile( String file ) {
		inputFile = FileSystems.getDefault().getPath( file );
	}

	public String getFileEnding() {
		return inputFile.toString().substring( inputFile.toString().lastIndexOf( '.' ) + 1 );
	}

	public InputFileType getInputFileType() {
		return inputFileType;
	}

	public void setInputFileType( InputFileType ift ) {
		this.inputFileType = ift;
	}

	public ComputationMode getComputationMode() {
		return computationMode;
	}

	public void setComputationMode( ComputationMode computationMode ) {
		this.computationMode = computationMode;
	}

	public GraphConverterAlgorithms getProjectConverter() {
		return projectConverter;
	}

	private void setBuildingPlanConverter( GraphConverterAlgorithms projectConverter ) {
		this.projectConverter = projectConverter;
	}

	public int getRuns() {
		return runs;
	}

	public void setRuns( int runs ) {
		this.runs = runs;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed( long seed ) {
		this.seed = seed;
	}	

	public void compute() {
		switch( inputFileType ) {
			case ZET:
				computeZET();
		}
	}

	private void computeZET() {
		// Try to load some properties
		File propertyFile = new File( "./properties/properties.xml" );
		try {
			PropertyContainer.getInstance().applyParameters( propertyFile );
		} catch( PropertyLoadException ex ) {
			ZETMain.exit( ex.getMessage() );
		}
		
		ZETProjectFileReader fr;
		fr = new ZETProjectFileReader();
		System.out.println( "ZET " + inputFile.toFile().toString() );
		fr.setProblem( inputFile.toFile() );
		fr.run();
		System.out.println( "LOADED" );


		// Now check if simulation or optimization is needed and call the methods
		assert (computationMode == ComputationMode.EarliestArrivalFlow || computationMode == ComputationMode.EvacuationSimulation);
		if( computationMode == ComputationMode.EarliestArrivalFlow ) {
			System.out.println( "Perform EAT" );
			assert( runs >= 0 );
			if( runs == 0 ) {
		// Try to load the project
				computeZETEAT( fr.getSolution(), seed );
			} else {
				// Test run
				System.out.println( "START TEST RUN" );
				computeZETEAT( fr.getSolution(), seed );
				// Reset times
				timeEAT = new ArrayList<>();
				timeConvert = new ArrayList<>();
				for( int i = 1; i <= runs; ++i ) {
					System.out.println( "START REAL RUN " + i );
					computeZETEAT( fr.getSolution(), seed+i );
				}
				
				// Compute averages
				System.out.println("\n\n" );
				System.out.println( "Runtimes for conversion:" );
				System.out.println( timeConvert );
				System.out.println( "Runtimes for EAT:" );
				System.out.println( timeEAT );
				System.out.println( "Average conversion:" );
				long total = 0;
				for( Long r : timeConvert )
					total += r;
				System.out.println( total );
				total = 0;
				System.out.println( "Average EAT:" );
				for( Long r : timeEAT )
					total += r;
				System.out.println( total );
				
			}
		} else
			System.out.println( "Perform Simulation" );

	}
	boolean working = true;
	ArrayList<Long> timeEAT = new ArrayList<>();
	ArrayList<Long> timeConvert = new ArrayList<>();

	private void computeZETEAT( Project p, long seed ) {
		//System.out.println( "Computation starts" );
		RandomUtils.getInstance().setSeed( seed );
		//try {
		AlgorithmControl a = new AlgorithmControl( p );
		a.convertBuildingPlan();
		PropertyChangeListener pr = new PropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent evt ) {
				if( evt.getNewValue() == SwingWorker.StateValue.DONE )
					working = false;
			}
		};
		RunnableFuture<Void> thread = a.convertGraph( pr, projectConverter );

		try { // Wait for the thread to end
			//System.out.println( "Waiting or result" );
			thread.get();
			//System.out.println( "Result received." );

			while( a.getNetworkFlowModel() == null ) {
				//System.out.println( "Waiting a bit" );
				Thread.sleep( 500 );
			}
			
			
			timeConvert.add( a.getConversionRuntime() );
		} catch( InterruptedException | ExecutionException ex ) {
			Logger.getLogger( CZET.class.getName() ).log( Level.SEVERE, null, ex );
			System.err.println( "Severe error." );
			ex.printStackTrace( System.err );
			System.exit( 1 );
		}

		assert (a.getNetworkFlowModel() != null);
		if( a.getNetworkFlowModel() == null )
			throw new IllegalStateException();
		
		//System.out.println( "Computation converted" );

		ConcreteAssignment concreteAssignment = p.getCurrentAssignment().createConcreteAssignment( 400 );

		GraphAssignmentConverter cav = new GraphAssignmentConverter( a.getNetworkFlowModel() );

		cav.setProblem( concreteAssignment );
		cav.run();
		//System.out.println( "Computation concrete assignment done" );
		NetworkFlowModel nfm = cav.getSolution();

		EarliestArrivalFlowProblem eafp = new EarliestArrivalFlowProblem( nfm.edgeCapacities, nfm.getNetwork(), nfm.getNodeCapacities(), nfm.getSupersink(), nfm.getSources(), 254, nfm.getTransitTimes(), nfm.currentAssignment );

		System.out.println( "Earliest Arrival computation starts..." );

		SEAAPAlgorithm algo = new SEAAPAlgorithm();

		algo.setProblem( eafp );
		//algo.addAlgorithmListener( this );
		try {
			algo.run();
		} catch( IllegalStateException e ) {
			//System.err.println( "The illegal state exception occured." );
		}

		PathBasedFlowOverTime df = algo.getSolution().getPathBased();
		int neededTimeHorizon = algo.getSolution().getTimeHorizon() - 1;
		System.out.println( "Total cost: " + algo.getSolution().getTotalCost() );
		System.out.println( "Time horizon:" + neededTimeHorizon );
		System.out.println( "Flow amount: " + algo.getSolution().getFlowAmount() );
		System.out.println( "Runtime: " + algo.getRuntimeAsString() );
		timeEAT.add( algo.getRuntime() );
		//System.out.println( "Computation over" );
	}
}
