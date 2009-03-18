/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * EditorTest.java
 * Created on 4. Dezember 40007, 17:08
 */
package gui;

import batch.load.BatchProject;
import batch.load.BatchProjectEntry;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import ds.Project;
import ds.PropertyContainer;
import ds.z.Assignment;
import ds.z.AssignmentType;
import ds.z.Floor;
import gui.editor.GUIOptionManager;
import gui.editor.properties.JOptionsWindow;
import gui.editor.properties.JPropertySelectorWindow;
import gui.editor.properties.PropertyLoadException;
import io.IOTools;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import localization.Localization;
import util.DebugStream;
import util.random.distributions.NormalDistribution;

/**
 * The <code>EditorStart</code> class is the main entry for the graphical user
 * interface of the evacuation tool. It creates an editor window and displays
 * en empty project.
 * @author Jan-Philipp Kappmeier
 */
public class EditorStart {
	/** The version of zet. */
	public static String version = "1.0.1";
	/** The localization class. */
	private static Localization loc = Localization.getInstance();
	/** The project file that should be loaded (submitted via commandline). */
	static String loadedProject = "";
	/** The property file that is loaded when the program starts. Can be changed via commandline. */
	static String propertyFilename = "./properties/properties.xml";
	/** The filename that contains the program options. */
	static final String optionFilename = "zetoptions.xml";
	/** The file to which the log is written (if specified via commandline). */
	static String logFile = "";
	/** The file to which the error log is written (if specified via commandline). */
	static String errFile = "";
	/** The batch project which is loaded if -batch command option is used. */
	static BatchProject bp = null;
	/** States if visualization is used, or not. Can be changed via commandline. */
	public static boolean useVisualization = true;
	/** States if statistic is used, or not. Can be changed via commandline. */
	public static boolean useStatistic = true;
	
	/** Creates a new instance of <code>EditorStart</code> */
	private EditorStart() { }

	public static void main( String[] args ) throws JSAPException {
		JSAP jsap = new JSAP();
		loc.setPrefix( "help." );

	UnflaggedOption optProjectFile = new UnflaggedOption( "project" )
						.setStringParser( JSAP.STRING_PARSER )
						.setRequired( false );
		optProjectFile.setHelp( loc.getString("projectFile") );
		jsap.registerParameter( optProjectFile );

		FlaggedOption optProperty = new FlaggedOption( "property" ).setStringParser( JSAP.STRING_PARSER )
						.setRequired( false )
						.setShortFlag( 'p' )
						.setLongFlag( "property" );
		optProperty.setHelp( loc.getString( "propertyFile" ) );
		jsap.registerParameter( optProperty );

		FlaggedOption optBatchFile = new FlaggedOption( "batch" ).setStringParser( JSAP.STRING_PARSER )
						.setRequired( false )
						.setShortFlag( 'b' )
						.setLongFlag( "batch" );
		optBatchFile.setHelp( loc.getString( "batchFile" ) );
		jsap.registerParameter( optBatchFile );

		FlaggedOption optLogFile = new FlaggedOption( "log" ).setStringParser( JSAP.STRING_PARSER )
						.setRequired( false )
						.setShortFlag( 'l' )
						.setLongFlag( "log" );
		optLogFile.setHelp( loc.getString( "logFile" ) );
		jsap.registerParameter( optLogFile );
		
		FlaggedOption optErrFile = new FlaggedOption( "err" ).setStringParser( JSAP.STRING_PARSER )
						.setRequired( false )
						.setShortFlag( 'e' )
						.setLongFlag( "error" );
		optErrFile.setHelp( loc.getString( "errFile" ) );
		jsap.registerParameter( optErrFile );

		Switch optAutoLog = new Switch( "autolog" ).setLongFlag( "autolog" );
		optAutoLog.setHelp( loc.getString( "autoLog" ) );
		jsap.registerParameter( optAutoLog );

		Switch optVerbose = new Switch( "verbose" ).setLongFlag( "verbose" )
						.setShortFlag( 'v' );
		optVerbose.setHelp( loc.getString( "verbose" ) );
		jsap.registerParameter( optVerbose );

		Switch optNoVisualization = new Switch( "noVis" ).setLongFlag( "noVisualization" );
		optNoVisualization.setHelp( loc.getString( "noVisualization" ) );
		jsap.registerParameter( optNoVisualization );

		Switch optNoStatistic = new Switch( "noStat" ).setLongFlag( "noStatistic" );
		optNoStatistic.setHelp( loc.getString( "noStatistic" ) );
		jsap.registerParameter( optNoStatistic );

		Switch optHelp = new Switch( "help" ).setShortFlag( 'h' ).setLongFlag( "help" );
		optHelp.setHelp( loc.getString( "help" ) );
		jsap.registerParameter( optHelp );

		JSAPResult config = jsap.parse( args );
		if( !config.success() ) {
			System.err.println();
			for( java.util.Iterator errs = config.getErrorMessageIterator();
							errs.hasNext();) {
				System.err.println( loc.getString( "error" ) + errs.next() );
			}
			System.err.println();
			System.err.println( loc.getString( "usage" ) + " java zet" + jsap.getUsage() );
			System.err.println();
			System.err.println( jsap.getHelp() );
			System.err.println();
			System.exit( 1 );
		}

		if( config.getBoolean( "help" ) ) {
			System.out.println();
			System.out.println( loc.getString( "usage" ) + " java zet" + jsap.getUsage() );
			System.out.println();
			System.out.println( jsap.getHelp() );
			if( !config.contains( "project" ) ) {
				System.exit( 0 );
			}
		}
		loc.setPrefix( "" );
		
		if( config.contains( "log" ) )
			logFile = config.getString( "log" );
		if( config.contains( "err" ) )
			errFile = config.getString( "err" );
		setUpLog( config.contains( "log" ), config.contains(  "err" ), config.getBoolean( "autolog" ), config.getBoolean( "verbose" ) );

		if( config.getBoolean( "noVis" ) ) {
			System.err.println( "Visualisierung wird ausgeschaltet." );
			useVisualization = false;
		}

		if( config.getBoolean( "noStat" ) ) {
			System.err.println( "Statistik wird ausgeschaltet." );
			useStatistic = false;
		}

		if( config.contains( "batch" ) )
			loadBatchProject( config.getString( "batch" ) );

		if( config.contains( "project" ) )
			loadedProject = config.getString( "project" );

		if( config.contains( "property" ) )
			propertyFilename = config.getString( "property" );

		createEditor();
	}

	/**
	 * Creates the editor window and displays it on the screen. This method should
	 * be called by a scheduler, so it could be executed in the event-dispatch
	 * thread.
	 * @see SwingUtilities.invokeLater()
	 * @see Runnable
	 */
	public static void createEditor() {
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				// Load default parameters and options
				File propertyFile = new File( propertyFilename );
				File optionFile = new File( optionFilename );
				checkFile( propertyFile, "Property file" );
				checkFile( optionFile, "Option file" );
				try {
					PropertyContainer.getInstance().applyParameters( propertyFile );
					PropertyContainer.getInstance().applyParameters( optionFile );
				} catch( PropertyLoadException ex ) {
					exit( ex.getMessage() );
				}
				// Change look and feel to native
				GUIOptionManager.changeLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
				//Start our editor in the event-dispatch-thread
				JEditor edit = JEditor.getInstance();
				File iconFile = new File( "./icon.gif" );
				checkFile( iconFile );
				try {
					Image img = ImageIO.read( iconFile );
					edit.setIconImage( img );
				} catch( IOException e ) {
					exit( "Error loding icon." );
				}
				edit.setOptionsFile( optionFilename );
				edit.setPropertiesFile( propertyFilename );
				JPropertySelectorWindow a = new JPropertySelectorWindow( edit, "", 100, 100, propertyFilename );
				a.saveWorking();
				a = null;
				JOptionsWindow b = new JOptionsWindow( edit, "", 100, 100, optionFilename );
				b.saveWorking();
				b = null;
				edit.addMainComponents();
				System.out.println( "ZET-Fenster geladen." );
				if( bp != null ) {
					for( BatchProjectEntry bpe : bp ) {
						edit.addBatchEntry( bpe );
					}
				} else if( !loadedProject.equals( "" ) ) {
					File f = new File( loadedProject );
					checkFile( f, "Project file" );
					edit.loadProjectFile( f );
					System.out.println( "Projekt " + f.getAbsolutePath() + " geladen." );
				}
				edit.setVisible( true );
			}
		} );
	}

	private static void loadBatchProject( String string ) {
		System.out.println( "Loading file " + string );
		File batchFile = new File( string );
		checkFile( batchFile, "Batch task file" );
		BatchProject loadedBatchProject = null;
		try {
			XStream xstream = new XStream();
			Annotations.configureAliases( xstream, BatchProject.class );
			Annotations.configureAliases( xstream, BatchProjectEntry.class );
			loadedBatchProject = (BatchProject) xstream.fromXML( new FileReader( batchFile ) );
		} catch( Exception ex ) {
			exit( "Error loading batch task file." );
		}
		bp = loadedBatchProject;
	}

	/**
	 * This method exits the program and prints an error message on the standard
	 * error stream. The return value is 1.
	 * @param errorMessage the error message
	 */
	public static void exit( String errorMessage ) {
		System.err.println( errorMessage );
		System.exit( 1 );
	}

	/**
	 * Checks if a file exists and quits with an error message and return code
	 * 1 if not.
	 * @param file the file that is to be checked
	 * @param fileType the file type that is checked. The error output will start with fileType
	 */
	private static void checkFile( File file, String fileType ) {
		if( fileType.equals( "" ) )
			fileType = "File '";
		else
			fileType += " '";
		try {
			if( !file.exists() )
				exit( fileType + file.getCanonicalPath() + "' does not exist." );
		} catch( IOException e ) {
			exit( "Error loading file." );
		}
	}

	/**
	 * Checks if a file exists and quits with an error message and return code
	 * 1 if not.
	 * @param file the file that is to be checked
	 */
	private static void checkFile( File file ) {
		checkFile( file, "" );
	}

	/**
	 * Creates a new project file with default settings
	 * @return
	 */
	public static Project newProject() {
		Project p = new Project();
		Floor fl = new Floor( loc.getString( "ds.z.DefaultName.Floor" ) + " 1" );
		p.getPlan().addFloor( fl );
		Assignment assignment = new Assignment( loc.getString( "ds.z.DefaultName.DefaultAssignment" ) );
		p.addAssignment( assignment );
		NormalDistribution d = new NormalDistribution( 0.5, 1.0, 0.4, 0.7 );
		NormalDistribution a = new NormalDistribution( 16, 1, 14, 80 );
		NormalDistribution f = new NormalDistribution( 0.8, 1.0, 0.7, 1.0 );
		NormalDistribution pa = new NormalDistribution( 0.5, 1.0, 0.0, 1.0 );
		NormalDistribution de = new NormalDistribution( 0.3, 1.0, 0.0, 1.0 );
		AssignmentType assignmentType = new AssignmentType( loc.getString( "ds.z.DefaultName.DefaultAssignmentType" ), d, a, f, pa, de, 10 );
		assignment.addAssignmentType( assignmentType );
		return p;
	}

	/**
	 * Sets up the standard output and the error output to some logging files.
	 * @param log indicates wheather standard out is redirected to a file
	 * @param err indicates wheather error out is redirected to a file
	 * @param auto indicates wheather the filenames are automatically created
	 * @param verbose indicates wheather the default output is also used, or not
	 */
	public static void setUpLog( boolean log, boolean err, boolean auto, boolean verbose ) {
		PrintStream errStream = System.err;
		PrintStream logStream = System.out;
		Calendar cal = Calendar.getInstance();
		if( auto ) {
			logFile = "zet_";
			errFile = "zet_";
			logFile += cal.get( Calendar.YEAR ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.MONTH ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.DAY_OF_MONTH ), 2 ) + "_" + IOTools.fillLeadingZeros( cal.get( Calendar.HOUR_OF_DAY ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.MINUTE ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.SECOND ), 2 ) + ".log";
			errFile += cal.get( Calendar.YEAR ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.MONTH ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.DAY_OF_MONTH ), 2 ) + "_" + IOTools.fillLeadingZeros( cal.get( Calendar.HOUR_OF_DAY ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.MINUTE ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.SECOND ), 2 ) + "_err.log";
			log = true;
			err = true;
		}
		if( log )
			try {
				logStream = verbose ? new DebugStream( new FileOutputStream( logFile ), System.out ) : new PrintStream( new FileOutputStream( logFile ) );
			} catch( FileNotFoundException ex ) {
				System.err.println( "Error creating out." );
			}
		if( err )
			if( log && logFile.equals( errFile ) )
				errStream = logStream;
			else
				try {
					errStream = verbose ? new DebugStream( new FileOutputStream( errFile ), System.err ): new PrintStream( new FileOutputStream( errFile ) );
				} catch( FileNotFoundException ex ) {
					System.err.println( "Error creating error out." );
				}
		System.setOut( logStream );
		System.setErr( errStream );
		if( log )
			System.out.println( "Log of " + cal.get( Calendar.YEAR ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.MONTH ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.DAY_OF_MONTH ), 2 ) + " " + IOTools.fillLeadingZeros( cal.get( Calendar.HOUR_OF_DAY ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.MINUTE ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.SECOND ), 2 ) );
		if( log && !logFile.equals( errFile ) )
			System.err.println( "Error log of " + cal.get( Calendar.YEAR ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.MONTH ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.DAY_OF_MONTH ), 2 ) + " " + IOTools.fillLeadingZeros( cal.get( Calendar.HOUR_OF_DAY ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.MINUTE ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.SECOND ), 2 ) );
	}
}

/*		
		/* File format conversion code for changing XStream versions
		 * 
		 List<File> exampleFiles = getExamples("C:\\Users\\Timon\\Documents\\Studium\\Projektgruppe\\examples");
		for (File f : exampleFiles) {
			try {
				/*ObjectOutputStream oos = new ObjectOutputStream (new FileOutputStream (
						f.getAbsolutePath ().replaceAll("xml", "sss")));
				
				try {
					Project p = Project.load (f);
					oos.writeObject (p);
					oos.flush ();
				} catch (Exception ex) {
					System.out.println ("Didn't work with " + f.getName ());
					continue;
				}
				oos.close ();
						
				ObjectInputStream oos = new ObjectInputStream (new FileInputStream (f));
				Project p = null;
				try {
					p = (Project)oos.readObject ();
					oos.close ();
					p.save (new File (f.getAbsolutePath ().replaceAll("sss", "xml")));
				} catch (Exception ex) {
					System.out.println ("Didn't work with " + f.getName ());
					(new File (f.getAbsolutePath ().replaceAll("sss", "xml"))).delete ();
					continue;
				}
				
			} catch (IOException ex) {
				ex.printStackTrace ();
			}
		}
//	/** Helper method for changing XStream versions.
//	private static List<File> getExamples (String string) {
//		File f = new File (string);
//		
//		List<File> res = new LinkedList<File>();
//		if (f.isDirectory ()) {
//			for (File s : f.listFiles ()) {
//				if (s.getName ().endsWith(".xml")) {
//					s.delete ();
//				}
//				if (s.isDirectory () || s.getName ().endsWith(".sss")) {
//					res.addAll (getExamples (s.getAbsolutePath ()));
//				}
//			}
//		} else {
//			res.add (f);
//		}
//		return res;
//	}

*/
//	private static void test() {
//		BatchProject bp = new BatchProject();
//		BatchProjectEntry bpe = new BatchProjectEntry();
//		bpe.setAssignment( "Standardbelegung" );
//		bpe.setCellularAutomanMaximalTime( 600.0 );
//		bpe.setCellularAutomatonAlgorithm( CellularAutomatonAlgorithm.Swap  );
//		bpe.setCellularAutomatonRuns( 1 );
//		bpe.setEvacuationOptimizationRuns( 1 );
//		bpe.setEvacuationOptimizationType( EvacuationOptimizationType.MinCost );
//		bpe.setGraphAlgorithm( GraphAlgorithm.SuccessiveEarliestArrivalAugmentingPathOptimized  );
//		bpe.setName( "Neuer Eintrag" );
//		bpe.setProjectFile( "./examples/ca-demo3.zet" );
//		bpe.setProperty( "PaperProperties" );
//		bp.add( bpe );
//		XStream xstream = new XStream();
//		Annotations.configureAliases( xstream, BatchProject.class );
//		Annotations.configureAliases( xstream, BatchProjectEntry.class );
//		//PropertyTreeNode root = propertyTreeModel.getRoot();
//		//List<AbstractPropertyValue> props = root.getProperties();
//		//if( props.size() > 0 ) {
//		//	StringProperty name = (StringProperty)props.get( 0 );
//		//	propertyTreeModel.setPropertyName(name.getValue() );
//		//}
//		File file = new File( "./batch.xml" );
//		try {
//			xstream.toXML( bp, new FileWriter( file ) );
//		} catch( IOException ex ) {
//			ex.printStackTrace();
//			exit( "Error loading Batch" );
//		}
//	}
