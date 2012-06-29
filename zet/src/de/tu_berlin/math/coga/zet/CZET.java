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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.SwingWorker;

/**
 * A command line interface to ZET
 *
 * @author Jan-Philipp Kappmeier
 */
public class CZET {
	private static final Logger log = Logger.getLogger( CZET.class.getName() );

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
	private int ignore = 0;
	private int runs = 0;
	private long seed = System.nanoTime();

	public static void main( String[] arguments ) throws JSAPException {
		Handler handler = new SysoutHandler();
		
		//handler.setFormatter( new SimpleFormatter() );
		handler.setFormatter( new SysoutFormatter() );
		handler.setLevel( Level.CONFIG );

		log.setLevel( Level.CONFIG );
		
		log.addHandler( handler );
		log.setUseParentHandlers(false);

		
//		log.log( Level.SEVERE, "severe" );
//		log.log( Level.WARNING, "warning" );
//		log.log( Level.INFO, "info" );
//		log.log( Level.CONFIG, "config" );
//		log.log( Level.FINE, "fine" );
//		log.log( Level.FINER, "finer" );
//		log.log( Level.FINEST, "finest" );
		
		log.info( "Command Line Interface for ZET " + gui.ZETMain.version );

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

		FlaggedOption optIgnore = new FlaggedOption( "ignore" ).setStringParser( JSAP.INTEGER_PARSER ).setRequired( false ).setLongFlag( "ignore" );
		optIgnore.setHelp( "The number of runs that are performed and ignored." );
		jsap.registerParameter( optIgnore );

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
					log.warning( "Unknown file type: " + czet.getFileEnding() );
					printHelp( jsap );
					System.exit( 1 );
			}

		// Set up the problem to solve. Set to default value if no computation is given
		if( config.contains( "mode" ) ) {
			czet.setComputationMode( ComputationMode.parse( config.getString( "mode" ).toLowerCase() ) );
			if( czet.getComputationMode() == null ) {
				log.warning( "Unknown computation mode: " + config.getString( "mode" ) );
				printHelp( jsap );
				System.exit( 1 );
			}
		} else
			switch( czet.getInputFileType() ) {
				case ZET:
					czet.setComputationMode( ComputationMode.EvacuationSimulation );
			}
		if( !czet.getInputFileType().isSupported( czet.getComputationMode() ) ) {
			log.warning( "Computation mode " + czet.getComputationMode() + " is not supported for input files of type " + czet.getInputFileType() );
			printHelp( jsap );
			System.exit( 1 );
		}

		// If a building plan converter is set, load it
		if( config.contains( "buildingPlanConverter" ) ) {
			czet.setBuildingPlanConverter( parseGraphConverterAlgorithm( config.getString( "buildingPlanConverter" ).toLowerCase() ) );
			if( czet.getComputationMode() == null ) {
				log.warning( "Unknown building converter: " + config.getString( "buildingPlanConverter" ) );
				printHelp( jsap );
				System.exit( 1 );
			}
		} else
			czet.setBuildingPlanConverter( GraphConverterAlgorithms.NonGridGraph );

		// Try to get seed and runs
		
		if( config.contains( "runs" ) ) {
			int runs = config.getInt( "runs" );
			log.config( "Averaging over " + runs + " runs" );
			czet.setRuns( runs );
		}
		if( config.contains( "ignore" ) ) {
			int ignore = config.getInt( "ignore" );
			log.config( "Performing " + ignore + " runs that are ignored." );
			czet.setIgnore( ignore );
		}
		
		
		if( config.contains( "seed" ) ) {
			long seed = config.getLong( "seed" );
			log.config( "Using main seed " + seed );
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

	public int getIgnore() {
		return ignore;
	}

	private void setIgnore( int ignore ) {
		this.ignore = ignore;
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
				MedianCalculator<Long> m;

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
		log.fine( "ZET " + inputFile.toFile().toString() );
		fr.setProblem( inputFile.toFile() );
		fr.run();
		log.finer( "LOADED" );

				m = new MedianCalculator<>( 2 );

		// Now check if simulation or optimization is needed and call the methods
		assert (computationMode == ComputationMode.EarliestArrivalFlow || computationMode == ComputationMode.EvacuationSimulation);
		if( computationMode == ComputationMode.EarliestArrivalFlow ) {
			log.fine( "Perform EAT" );
			assert( runs >= 0 );
			if( runs == 0 ) {
		// Try to load the project
				computeZETEAT( fr.getSolution(), seed );
			} else {
				// Test run
				log.finer( "START TEST RUNs" );
				for( int i = 1; i <= ignore; ++i ) {
					log.finest( "Ignore run " + i );
					computeZETEAT( fr.getSolution(), seed );
					
				}
				// Reset times
				int count = 1;
				m = new MedianCalculator<>( 2 );
				do {
					log.fine( "START REAL RUN " + count );
					computeZETEAT( fr.getSolution(), seed+count );
					count++;
					m.run();
					log.finer( "Anzahl outlier: " + m.getNumberOfOutlier() + " - Anzahl valid: " + m.valid() );
				} while( m.valid() < runs );
				
				// Compute averages
				log.info("\n\n" );
				log.info( "Runtimes for conversion:" );
				String out = "";
				for( long j : m.getValues( 1 ) )
					out += j + "\t";
				log.info( out );
				log.info("");
				
				log.fine( "Outliers:" );
				out = "";
				for( long l : m.getOutlier( 1 ) )
					out += l + "\t";
				log.fine( "" );
				
				log.finer( "Valid Runtimes for Conversion:" );
				out = "";
				for( long l : m.getValid( 1 ) )
					out += l + "\t";
				log.finer( out );
				log.finer( "" );

				log.info( "Runtimes for EAT:" );
				out = "";
				for( long l : m.getValues( 0 ) )
					out += l + "\t";
				log.info( out );
				log.info("");
				
				log.fine( "Outliers:" );
				out = "";
				for( long l : m.getOutlier( 0 ) )
					out += l + "\t";
				log.fine( out );
				log.fine("");
				
				log.finer( "Valid Runtimes for EAT:" );
				out = "";
				for( long l : m.getValid( 0 ) )
					out += l + "\t";
				log.finer( out );
				log.finer( "" );
				
				log.info( "Average conversion:" );
				long total = 0;
				for( Long r : m.getValid( 1 ) )
					total += r;
				log.info( "" + total/(double)m.valid() );
				total = 0;
				log.info( "Average EAT:" );
				for( Long r : m.getValid( 0 ) )
					total += r;
				log.log( Level.INFO, "{0}", total/(double)m.valid());
			}
		} else
			System.out.println( "Perform Simulation" );

	}
	boolean working = true;

	private void computeZETEAT( Project p, long seed ) {
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

		long cr = 0;
		try { // Wait for the thread to end
			thread.get();

			while( a.getNetworkFlowModel() == null ) {
				Thread.sleep( 500 );
			}

			cr = a.getConversionRuntime();
		} catch( InterruptedException | ExecutionException ex ) {
			Logger.getLogger( CZET.class.getName() ).log( Level.SEVERE, null, ex );
			System.err.println( "Severe error." );
			ex.printStackTrace( System.err );
			System.exit( 1 );
		}

		assert (a.getNetworkFlowModel() != null);
		if( a.getNetworkFlowModel() == null )
			throw new IllegalStateException();
		
		ConcreteAssignment concreteAssignment = p.getCurrentAssignment().createConcreteAssignment( 400 );

		GraphAssignmentConverter cav = new GraphAssignmentConverter( a.getNetworkFlowModel() );

		cav.setProblem( concreteAssignment );
		cav.run();
		NetworkFlowModel nfm = cav.getSolution();

		EarliestArrivalFlowProblem eafp = new EarliestArrivalFlowProblem( nfm.edgeCapacities, nfm.getNetwork(), nfm.getNodeCapacities(), nfm.getSupersink(), nfm.getSources(), 254, nfm.getTransitTimes(), nfm.currentAssignment );

		log.fine( "Earliest Arrival computation starts..." );

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
		log.info( "Total cost: " + algo.getSolution().getTotalCost() );
		log.info( "Time horizon:" + neededTimeHorizon );
		log.info( "Flow amount: " + algo.getSolution().getFlowAmount() );
		log.info( "Runtime: " + algo.getRuntimeAsString() );
		Long[] rt = new Long[2];
		rt[0] = algo.getRuntime();
		rt[1] = cr;
		m.addData( rt );
	}
}
