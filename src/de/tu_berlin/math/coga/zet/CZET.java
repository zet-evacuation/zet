package de.tu_berlin.math.coga.zet;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.LongestShortestPathTimeHorizonEstimator;
import algo.graph.dynamicflow.eat.SEAAPAlgorithm;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;
import de.tu_berlin.math.coga.batch.input.reader.ZETProjectFileReader;
import de.tu_berlin.math.coga.common.debug.Debug;
import de.tu_berlin.math.coga.graph.io.xml.XMLWriter;
import de.tu_berlin.math.coga.graph.io.xml.visualization.GraphVisualization;
import de.tu_berlin.math.coga.rndutils.RandomUtils;
import de.tu_berlin.math.coga.zet.converter.graph.GraphAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import ds.GraphVisualizationResults;
import ds.PropertyContainer;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.StaticPath;
import ds.graph.flow.FlowOverTimeImplicit;
import ds.graph.flow.FlowOverTimePath;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.mapping.IdentifiableDoubleMapping;
import ds.mapping.IdentifiableIntegerMapping;
import ds.z.ConcreteAssignment;
import ds.z.Project;
import gui.AlgorithmControl;
import gui.GraphConverterAlgorithms;
import gui.ZETLoader;
import gui.editor.properties.PropertyLoadException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;


/**
 * A command line interface to ZET. Allows to call all important algorithms.
 *
 * @author Jan-Philipp Kappmeier
 */
public class CZET {
	private static final Logger log = Logger.getGlobal();

	private static enum ComputationMode {
		EarliestArrivalFlow( OutputFileType.ap ),
		StaticMaximumFlow( OutputFileType.txt ),
		StaticMinCostFlow( OutputFileType.txt ),
		EvacuationSimulation( OutputFileType.txt ),
		Conversion( OutputFileType.XML );
		final OutputFileType defaultOutputFileType;

		private ComputationMode( OutputFileType defaultOutputFileType ) {
			this.defaultOutputFileType = defaultOutputFileType;
		}

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
				case "conv":
					return ComputationMode.Conversion;
				default:
					return null;
			}
		}
	}

	private static GraphConverterAlgorithms parseGraphConverterAlgorithm( String string ) {
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

	private static enum InputFileType {
		XML( new ComputationMode[]{ComputationMode.EarliestArrivalFlow} ),
		DimacsMaxFlow( new ComputationMode[]{ComputationMode.StaticMaximumFlow} ),
		DAT( new ComputationMode[]{ComputationMode.EarliestArrivalFlow} ),
		ZET( new ComputationMode[]{ComputationMode.EvacuationSimulation, ComputationMode.EarliestArrivalFlow, ComputationMode.Conversion} );
		ArrayList<ComputationMode> supportedModes = new ArrayList<>();

		private InputFileType( ComputationMode[] supportedModes ) {
			assert( supportedModes.length > 0 );
			this.supportedModes.addAll( Arrays.asList( supportedModes ) );
		}

		public boolean isSupported( ComputationMode computationMode ) {
			return supportedModes.contains( computationMode );
		}

		public ComputationMode getDefaultComputation() {
			return supportedModes.get( 0 );
		}
	}

	private static enum OutputFileType {
		XML( "xml" ),
		/** A text file containing a flow (over time) in a network. */
		flow( "flow" ),
		/** Arrival pattern at the sink(s). */
		ap( "ap" ),
		dot( "dot" ),
		txt ( "txt" );
		String ending;

		private OutputFileType( String ending ) {
			this.ending = ending;
		}

	}
	private Path inputFile;
	private Path outputFile;
	private InputFileType inputFileType;
	private OutputFileType outputFileType;
	private ComputationMode computationMode;
	private GraphConverterAlgorithms projectConverter;
	private int ignore = 0;
	private int runs = 0;
	private long seed = System.nanoTime();
	private boolean median = false;

	public static void main( String[] arguments ) throws JSAPException, IOException {
		Debug.setDefaultLogLevel( Level.FINER );
		Debug.setUpLogging();

		log.log( Level.INFO, "Command Line Interface for ZET {0}", gui.ZETMain.version);

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
		optRuns.setHelp( "The number of valid runs (excluding outliers in median mode)." );
		jsap.registerParameter( optRuns );

		FlaggedOption optIgnore = new FlaggedOption( "ignore" ).setStringParser( JSAP.INTEGER_PARSER ).setRequired( false ).setLongFlag( "ignore" );
		optIgnore.setHelp( "The number of runs that are performed and ignored." );
		jsap.registerParameter( optIgnore );

		Switch optMedian = new Switch( "median" ).setLongFlag( "median" );
		optMedian.setHelp( "Computes a median and ignores outlieer." );
		jsap.registerParameter( optMedian );

		FlaggedOption optSeed = new FlaggedOption( "seed" ).setStringParser( JSAP.LONG_PARSER ).setRequired( false ).setLongFlag( "seed" ).setShortFlag( 's' );
		optSeed.setHelp( "An initial seed. Used to initialize the random generators for different runs." );
		jsap.registerParameter( optSeed );

		JSAPResult config = jsap.parse( arguments );
		if( !config.success() ) {
			log.severe( "" );
			for( Iterator<?> errs = config.getErrorMessageIterator(); errs.hasNext(); )
				log.log( Level.SEVERE, "Error: {0}", errs.next());
			log.severe( "" );
			printHelp( jsap );
			System.exit( 1 );
		}

		CZET czet = new CZET();
		czet.setFile( config.getString( "inputFile" ) );
		// Try to load input file

		czet.setOutputFile( config.getString( "outputFile" ) );


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
				case "dat":
					czet.setInputFileType( InputFileType.DAT );
					break;
				default:
					log.log( Level.WARNING, "Unknown file type: {0}", czet.getFileEnding());
					printHelp( jsap );
					System.exit( 1 );
			}

		// Set up the problem to solve. Set to default value if no computation is given
		if( config.contains( "mode" ) ) {
			czet.setComputationMode( ComputationMode.parse( config.getString( "mode" ).toLowerCase() ) );
			if( czet.getComputationMode() == null ) {
				log.log( Level.WARNING, "Unknown computation mode: {0}", config.getString( "mode" ));
				printHelp( jsap );
				System.exit( 1 );
			}
		} else
			czet.setComputationMode( czet.getInputFileType().getDefaultComputation() );
		if( !czet.getInputFileType().isSupported( czet.getComputationMode() ) ) {
			log.log( Level.WARNING, "Computation mode {0} is not supported for input files of type {1}", new Object[]{czet.getComputationMode(), czet.getInputFileType()});
			printHelp( jsap );
			System.exit( 1 );
		}

		// If a building plan converter is set, load it
		if( config.contains( "buildingPlanConverter" ) ) {
			czet.setBuildingPlanConverter( parseGraphConverterAlgorithm( config.getString( "buildingPlanConverter" ).toLowerCase() ) );
			if( czet.getComputationMode() == null ) {
				log.log( Level.WARNING, "Unknown building converter: {0}", config.getString( "buildingPlanConverter" ));
				printHelp( jsap );
				System.exit( 1 );
			}
		} else
			czet.setBuildingPlanConverter( GraphConverterAlgorithms.NonGridGraph );

		// Try to get seed and runs

		if( config.contains( "runs" ) ) {
			int runs = config.getInt( "runs" );
			log.log( Level.CONFIG, "Averaging over {0} runs", runs);
			czet.setRuns( runs );
		}
		if( config.contains( "ignore" ) ) {
			int ignore = config.getInt( "ignore" );
			log.log( Level.CONFIG, "Performing {0} runs that are ignored.", ignore);
			czet.setIgnore( ignore );
		}
		if( config.contains( "median" ) && config.getBoolean( "median" ) ) {
			czet.setMedian( true );
			log.config( "Use median mode." );
		}

		if( config.contains( "seed" ) ) {
			long seed = config.getLong( "seed" );
			log.log( Level.CONFIG, "Using main seed {0}", seed);
			czet.setSeed( seed );
		}

		czet.directoryToFile();

		try {
			czet.compute();
		} catch( FileNotFoundException ex ) {
			log.log( Level.SEVERE, "File not found: {0}", ex.getMessage());
		}
	}

	private static void printHelp( JSAP jsap ) {
		log.log( Level.INFO, "Usage: " + " java CZET {0}\n" , jsap.getUsage() );
		log.log( Level.INFO, "{0}\n", jsap.getHelp());
	}

	public void setFile( String file ) {
		inputFile = FileSystems.getDefault().getPath( file );
	}

	public String getFileEnding() {
		return inputFile.toString().substring( inputFile.toString().lastIndexOf( '.' ) + 1 );
	}

	private void setOutputFile( String file ) {
		if( file == null ) {
			String f = "./";
			outputFile = FileSystems.getDefault().getPath( f );
		} else {
			outputFile = FileSystems.getDefault().getPath( file );
		}
	}

	private void directoryToFile() {
		if( !outputFile.toFile().isDirectory() )
			return;
		Path fileName = inputFile.getFileName();
		String ending = getFileEnding();
		String file = fileName.toString().substring( 0, fileName.toString().length()-ending.length() );
		outputFile = outputFile.resolve( file + outputFileType.ending );
	}

	private InputFileType getInputFileType() {
		return inputFileType;
	}

	private void setInputFileType( InputFileType ift ) {
		this.inputFileType = ift;
	}

	private ComputationMode getComputationMode() {
		return computationMode;
	}

	private void setComputationMode( ComputationMode computationMode ) {
		this.computationMode = computationMode;
		outputFileType = computationMode.defaultOutputFileType;
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

	public boolean isMedian() {
		return median;
	}

	public void setMedian( boolean median ) {
		this.median = median;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed( long seed ) {
		this.seed = seed;
	}

	public void compute() throws IOException {
		switch( inputFileType ) {
			case ZET:
				computeZET();
				break;
			case DAT:
				computeDAT();
		}
	}

	MedianCalculator<Long> m;

	private void computeZET() throws IOException {
		// Try to load some properties
		File propertyFile = new File( "./properties/properties.xml" );
		try {
			PropertyContainer.getInstance().applyParameters( propertyFile );
		} catch( PropertyLoadException ex ) {
			ZETLoader.exit( ex.getMessage() );
		}

		ZETProjectFileReader fr;
		fr = new ZETProjectFileReader();
		log.log( Level.FINE, "ZET {0}", inputFile.toFile().toString());
		fr.setProblem( inputFile.toFile() );
		fr.run();
		log.finer( "LOADED" );

		if( fr.isRunning() )
			throw new AssertionError( "Is running!" );

		if( fr.getSolution() == null )
			throw new AssertionError( "Solution null. Das sollte nicht passieren." );

		m = new MedianCalculator<>( 2 );

		// Now check if simulation or optimization is needed and call the methods
		assert (computationMode == ComputationMode.EarliestArrivalFlow || computationMode == ComputationMode.EvacuationSimulation || computationMode == ComputationMode.Conversion );
		switch( computationMode ) {
			case EarliestArrivalFlow:
				log.fine( "Perform EAT" );
				assert (runs >= 0);
				if( runs == 0 )
					// Try to load the project
					computeZETEAT( fr.getSolution(), seed );
				else {
					// Test run
					log.fine( "Start test runs" );
					for( int i = 1; i <= ignore; ++i ) {
						log.finer( "Ignore run " + i );
						computeZETEAT( fr.getSolution(), seed );

					}
					// Reset times
					int validCount = 0;
					m = new MedianCalculator<>( 2 );
					do {
						log.log( Level.FINE, "START REAL RUN {0}", validCount);
						computeZETEAT( fr.getSolution(), seed + validCount );
						if( median )
							m.run();
						log.log( Level.FINER, "Anzahl outlier: {0} - Anzahl valid: {1}", new Object[]{m.getNumberOfOutlier(), m.valid()});
						validCount = median ? m.valid() : validCount + 1;
					} while( validCount < runs );

					// Compute averages
					log.info( "\n\n" );
					log.info( "Runtimes for conversion:" );
					String out = "";
					for( long j : m.getValues( 1 ) )
						out += j + "\t";
					log.info( out );
					log.info( "" );

					log.fine( "Outliers:" );
					out = "";
					for( long l : m.getOutlier( 1 ) )
						out += l + "\t";
					log.fine( out );
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
					log.info( "" );

					log.fine( "Outliers:" );
					out = "";
					for( long l : m.getOutlier( 0 ) )
						out += l + "\t";
					log.fine( out );
					log.fine( "" );

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
					log.info( "" + total / (double)validCount );
					total = 0;
					assert validCount == m.getValid( 1 ).size();
					log.info( "Average EAT:" );
					for( Long r : m.getValid( 0 ) )
						total += r;
					log.log( Level.INFO, "{0}", total / (double)validCount );
				}
				break;
			case EvacuationSimulation:
				log.info( "Perform Simulation" );
				break;
			case Conversion:
				log.info( "Perform conversion" );

				log.log( Level.INFO, "Writing to {0}", outputFile.toString());

				// TODO: doubled code. move to method or something like that

				log.log( Level.CONFIG, "Using seed: {0}", seed);

				RandomUtils.getInstance().setSeed( seed );
				//try {
				AlgorithmControl a = new AlgorithmControl( fr.getSolution() );

				assert( fr.getSolution() != null );

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

					while( a.getNetworkFlowModel() == null )
						Thread.sleep( 500 );

					cr = a.getConversionRuntime();
				} catch( InterruptedException | ExecutionException ex ) {
					Logger.getLogger( CZET.class.getName() ).log( Level.SEVERE, null, ex );
					log.log( Level.SEVERE, "Severe error.", ex );
					System.exit( 1 );
				}

				assert (a.getNetworkFlowModel() != null);
				if( a.getNetworkFlowModel() == null )
					throw new IllegalStateException();

				ConcreteAssignment concreteAssignment = fr.getSolution().getCurrentAssignment().createConcreteAssignment( 400 );

				NetworkFlowModel nfm = a.getNetworkFlowModel();

				GraphAssignmentConverter cav = new GraphAssignmentConverter( nfm );

				cav.setProblem( concreteAssignment );
				cav.run();

				nfm = cav.getSolution();

				XMLWriter writer = new XMLWriter( outputFile.toFile() );
				throw new UnsupportedOperationException( "Not implemented yet" );
				//writer.writeGraph( nfm.network.getAsStaticNetwork(), nfm.edgeCapacities, nfm.transitTimes, nfm.currentAssignment );
		}

	}

	GraphVisualizationResults graphVisResult;
	IdentifiableIntegerMapping<Node> xPos;
	IdentifiableIntegerMapping<Node> yPos;
	NodePositionMapping nodePositionMapping;
	PathBasedFlowOverTime df;
	int neededTimeHorizon;
	int percentInterval = 100;
	GraphVisualization graphView = null;
	EarliestArrivalFlowProblem eafp = null;

	private void computeDAT() throws FileNotFoundException, IOException {
		// Use internal dat format
		log.log( Level.INFO, "Reading from dat-file ''{0}''", inputFile.toString() );
		// .dat files must contain node positions
		nodePositionMapping = new NodePositionMapping();
		xPos = new IdentifiableIntegerMapping<>( 0 );
		yPos = new IdentifiableIntegerMapping<>( 0 );
		long start = System.nanoTime();
		eafp = DatFileReaderWriter.read( inputFile.toString(), nodePositionMapping ); // new .dat-format
		graphView = new GraphVisualization( eafp, nodePositionMapping );
		// version without x and Years positions:
		//theInstance.eafp = DatFileReaderWriter.readOld( theInstance.inputFileName ); // old .dat-format

		long end = System.nanoTime();

		assert( computationMode == ComputationMode.EarliestArrivalFlow );
		m = new MedianCalculator<>( 2 );
		switch( computationMode ) {
			case EarliestArrivalFlow:
				log.fine( "Perform EAT" );
				Long[] rt = new Long[2];
				FlowOverTimeImplicit fot = eat( eafp, rt );
				rt[1] = end-start;
				m.addData( rt );


				// output

				PathBasedFlowOverTime pb = fot.getPathBased();

				int[] arrivalPattern = new int[fot.getTimeHorizon()+1];

				int maxArrival = fot.getTimeHorizon();
				for( FlowOverTimePath a : pb ) {
					arrivalPattern[a.getArrival( eafp.getTransitTimes() )]++;
					System.out.println( a.toString( eafp.getTransitTimes() ) );
				}

				System.out.println( Arrays.toString( arrivalPattern ) );

				// cumulate
				int[] cumulatedAarrivalPattern = new int[fot.getTimeHorizon()+1];
				cumulatedAarrivalPattern[0] = arrivalPattern[0];
				for( int i = 1; i < arrivalPattern.length; ++i )
					cumulatedAarrivalPattern[i] += cumulatedAarrivalPattern[i-1];
				System.out.println( Arrays.toString( cumulatedAarrivalPattern ) );


				System.out.println( "Output as LP" );
				// kanten: ei
				// pfade: pij, i=pfadnummer, j=zeitpunkt, zu dem der pfad ankommt

				HashSet<StaticPath> staticPaths = new HashSet<>();
				HashMap<StaticPath,Integer> firstUse = new HashMap<>();
				HashMap<StaticPath,Integer> pathID = new HashMap<>();

				int id = 0;
				for( FlowOverTimePath a : pb ) {
					StaticPath sp = a.asStatic();
					staticPaths.add( sp );
					pathID.put( sp, id++ );
					int min = Integer.MAX_VALUE;
					if( firstUse.containsKey( sp ) )
						min = firstUse.get( sp );
					min = Math.min( min, a.getArrival( eafp.getTransitTimes() ) );
					firstUse.put( sp, min );
				}

				System.out.println( "Static paths: " + staticPaths.size() );

				System.out.println( "Maximize" );
				System.out.println( "matching:" );
				System.out.println( "z" );
				System.out.println( "Subject to" );

				for( Edge e : eafp.getNetwork().edges() ) {
					String s = "e_" + e.id() + ": ";
					boolean first = true;
					for( StaticPath p : staticPaths ) {
						if( p.contains( e ) ) {
							if( first != true ) {
								s = s + "+";
							} else {
								first = false;
							}
							s = s + "p" + pathID.get( p ) + firstUse.get( p );
						}
					}
					s += " <= 1";
					if( first != true )
						System.out.println( s );
				}

				// p_ij , i pfadnummer, j = arrivaltime, beginnt mit min-time + 1
				for( StaticPath sp : staticPaths ) {
					for( int j = firstUse.get( sp )+1; j <= fot.getTimeHorizon(); ++j ) {
						System.out.println( "p_" + pathID.get( sp ) + j + ": " + "p"+pathID.get(sp) + firstUse.get( sp ) + " - " + "p" + pathID.get(sp) + j + " = 0" );
					}
				}

				for( int t = 0; t <= fot.getTimeHorizon(); ++t ) {
					String s = "t_" + t + ": ";
					boolean first = true;
					for( StaticPath sp : staticPaths ) {
						if( firstUse.get( sp ) <= t ) {
							if( first ) {
								first = false;
							} else {
								s += " + ";
							}
							s += "p" + pathID.get( sp ) + t;
						}
					}
					s += " - " + arrivalPattern[t] + "z>=0";
					System.out.println( s );

				}

				System.out.println( "Bounds" );
				// variablen-beschränkungen
				// pfad-variablen >= 0
				for( StaticPath sp : staticPaths ) {
					for( int j = firstUse.get( sp ); j <= fot.getTimeHorizon(); ++j ) {
						System.out.println( "p" + pathID.get( sp ) + j + " >= 0" );
					}
				}
				System.out.println( "End" );

				System.out.println( "Compute Approximation" );

				HashMap<StaticPath,Double> c = new HashMap<>();

				Double eps = 1/3.;

				for( int t = 0; t <= maxArrival; ++t ) {
					System.out.println( "Computing Value for t=" + t );

					// ignore edge capacities at first glance as no edges share paths of same length

					// get all paths that arrive at this time
					Set<StaticPath> arrivalSet = new HashSet<>();

					Set<StaticPath> unused = new HashSet<>();
					Set<StaticPath> used = new HashSet<>();

					double cap = 0;
					for( FlowOverTimePath a : pb ) {
						StaticPath temp1 = a.asStatic();
						StaticPath temp2 = a.asStatic();
						assert temp1.equals(temp2);
						assert temp1 != temp2;

						if( a.getArrival( eafp.getTransitTimes() ) > t )
							continue;
						//System.out.println( "Path: " + a );
						if( !c.containsKey( a.asStatic() ) ) {
							// we may have a new path
							unused.add( a.asStatic() );
							if( a.getArrival( eafp.getTransitTimes() ) == t )
								arrivalSet.add( a.asStatic() );
						} else {
							used.add( a.asStatic() );
							if( a.getArrival( eafp.getTransitTimes() ) <= t ) {
								cap += ( (t - a.getArrival( eafp.getTransitTimes() ))+1 ) * c.get( a.asStatic() );
							}
						}
					}

					// calculate
					System.out.println( "Should arrive: " + (eps * arrivalPattern[t] ) );
					System.out.println( "Arrived: " + cap );

					// try to send remaining amount
					int numberOfPaths = arrivalSet.size();
					System.out.println( "Add new Paths. Available: " + numberOfPaths );

					double onPath = ((eps * arrivalPattern[t] ) - cap)/numberOfPaths;


					if( onPath < 0.0003 ) {
						System.out.println( "Do not add new paths, already enough from old ones." );
						if( numberOfPaths == 0 )
							System.out.println( " ---- anyway, number of paths is 0" );
					} else {
//						if( t == 0 )
//							onPath = 1/3.;
//						else
//							onPath = 1/6.;
						for( StaticPath a : arrivalSet ) {

							if( c.containsKey( a ) )
								throw new AssertionError();
							System.out.println( "ADD " + a + " with capacity " + onPath );
							c.put( a, onPath );
						}
					}


					IdentifiableDoubleMapping<Edge> assignedEdgeCapacity = new IdentifiableDoubleMapping<>( eafp.getNetwork().getEdgeCapacity() );
					for( Edge e : eafp.getNetwork().edges() ) {
						assignedEdgeCapacity.set( e, 0 );
					}
					for( StaticPath sp : c.keySet() ) {
						for( Edge e : sp ) {
							double old = assignedEdgeCapacity.get( e );
							double nc = old + c.get( sp );
							assignedEdgeCapacity.set( e, nc );
							if( nc > 1 )
								System.out.println( nc + " on edge " + e.toString() );
							assert nc <= 1;
						}
					}



				}

			break;
		}



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
			log.log( Level.SEVERE, "Severe error.", ex );
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

		EarliestArrivalFlowProblem eafp = nfm.getEAFP();
		eafp.setTimeHorizon( 254 );

		Long[] rt = new Long[2];
		eat( eafp, rt );
		//rt[0] = algo.getRuntime();
		rt[1] = cr;
		m.addData( rt );

	}

	/**
	 * Saves the runtime of the EAT computation at index 0 in the array.
	 * @param eafp
	 * @param rt
	 * @return
	 */
	private FlowOverTimeImplicit eat( EarliestArrivalFlowProblem eafp, Long[] rt ) {
		log.fine( "Earliest Arrival computation starts..." );

		if( eafp.getTimeHorizon() <= 0 ) {
			System.out.println( "Schätze Zeithorizont" );
			LongestShortestPathTimeHorizonEstimator estimator = new LongestShortestPathTimeHorizonEstimator();
			estimator.setProblem( eafp );
			estimator.run();
			eafp.setTimeHorizon( estimator.getSolution().getUpperBound() );
			System.out.println( "Geschätzter Zeithorizont: " + estimator.getSolution().getUpperBound() );
		}

		SEAAPAlgorithm algo = new SEAAPAlgorithm();

		algo.setProblem( eafp );
		//algo.addAlgorithmListener( this );
		try {
			algo.run();
		} catch( IllegalStateException e ) {
			//System.err.println( "The illegal state exception occured." );
		}

		PathBasedFlowOverTime df = algo.getSolution().getPathBased();
		int neededTimeHorizon = algo.getSolution().getTimeHorizon();
		log.log( Level.INFO, "Total cost: {0}", algo.getSolution().getTotalCost());
		log.log( Level.INFO, "Time horizon:{0}", neededTimeHorizon);
		log.log( Level.INFO, "Flow amount: {0}", algo.getSolution().getFlowAmount());
		log.log( Level.INFO, "Runtime: {0}", algo.getRuntimeAsString());
		rt[0] = algo.getRuntime();
		return algo.getSolution();
	}

}
