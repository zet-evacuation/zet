package de.tu_berlin.math.coga.zet;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;
import de.tu_berlin.math.coga.batch.input.reader.ZETProjectFileReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A command line interface to ZET
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
	private static enum ProjectConverter {
		DefaultConverter,
		Spanner;
	}
	public static enum InputFileType {
		XML( new ComputationMode[] {ComputationMode.EarliestArrivalFlow} ),
		DimacsMaxFlow(new ComputationMode[] {ComputationMode.StaticMaximumFlow} ),
		ZET(new ComputationMode[] {ComputationMode.EarliestArrivalFlow, ComputationMode.EvacuationSimulation});
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

		Switch switchZeroNodeCapacities = new Switch( "zero" ).setShortFlag( 'z' ).setLongFlag( "zeroNodeCapacities" );
		switchZeroNodeCapacities.setHelp( "Use zero node capacities." );
		jsap.registerParameter( switchZeroNodeCapacities );
	
		JSAPResult config = jsap.parse( arguments );
		if( !config.success() ) {
			System.err.println();
			for( java.util.Iterator errs = config.getErrorMessageIterator();
							errs.hasNext();) {
				System.err.println( "Error: " + errs.next() );
			}
			System.err.println();
			printHelp( jsap );
			System.exit( 1 );
		}
		
		CZET czet = new CZET();
		czet.setFile( config.getString( "inputFile" ) );
		// Try to load input file
		
		
		// Here, we can be sure that arguments have been read correctly
		// try to autodetect file type if no type is set
		if( config.contains( "inputFormat" ) ) {
			switch( config.getString( "inputFormat" ) ) {
				case "MAX":
					break;
				case "ZET":
					czet.setInputFileType( InputFileType.ZET );
					break;
			}
		} else {
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
		}
		
		// Set up the problem to solve. Set to default value if no computation is given
		if( config.contains( "mode" ) ) {
			czet.setComputationMode( ComputationMode.parse( config.getString( "mode" ).toLowerCase() ) );
			if( czet.getComputationMode() == null ) {
				System.out.println( "Unknown computation mode: " + config.getString( "mode" ) );
				printHelp( jsap );
				System.exit( 1 );
			}
		} else {
			switch( czet.getInputFileType() ) {
				case ZET:
					czet.setComputationMode( ComputationMode.EvacuationSimulation );
			}
		}
		if( !czet.getInputFileType().isSupported( czet.getComputationMode() ) ) {
			System.out.println( "Computation mode " + czet.getComputationMode() + " is not supported for input files of type " + czet.getInputFileType() );
			printHelp( jsap );
			System.exit( 1 );
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
		return inputFile.toString().substring( inputFile.toString().lastIndexOf( '.' )+1 );
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
	
	public void compute() {
		switch( inputFileType ) {
			case ZET:
				computeZET();
		}
	}
	
	private void computeZET() {
		ZETProjectFileReader fr = new ZETProjectFileReader();
		System.out.println( "ZET " + inputFile.toFile().toString() );
		fr.setProblem( inputFile.toFile() );
		fr.run();
		System.out.println( "LOADED" );
	}
	
	
}
