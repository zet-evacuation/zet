/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/*
 * ZETMain.java
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
import de.tu_berlin.math.coga.common.debug.DebugStream;
import de.tu_berlin.math.coga.common.debug.DebugStreamVerbose;
import de.tu_berlin.math.coga.common.debug.Log;
import de.tu_berlin.math.coga.common.localization.Localization;
import ds.PropertyContainer;
import event.EventServer;
import event.MessageEvent;
import event.MessageEvent.MessageType;
import gui.editor.GUIOptionManager;
import gui.editor.properties.JPropertySelectorWindow;
import gui.editor.properties.PropertyLoadException;
import gui.editor.properties.PropertyTreeModel;
import de.tu_berlin.math.coga.common.util.IOTools;
import ds.z.ZControl;
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

/**
 * The <code>ZETMain</code> class is the main entry for the graphical user
 * interface of the evacuation tool. It creates an editor window and displays
 * an empty project.
 * @author Jan-Philipp Kappmeier
 */
public class ZETMain {
	/** The version of zet. */
	public static String version = "1.1.0";
	/** The localization class. */
	private static Localization loc = Localization.getInstance();
	/** Indicates wheather debug mode is active, or not. */
	private static boolean debug;
	/** The project file that should be loaded (submitted via commandline). */
	static String loadedProject = "";
	/** The property file that is loaded when the program starts. Can be changed via commandline. */
	static String propertyFilename = "./properties/properties.xml";
	/** The filename for the file that contains the program options. */
	static final String optionFilename = "zetoptions.xml";
	/** The filename for the file that contains additional options. */
	static final String informationFilename = "options.xml";
	/** The file to which the log is written (if specified via commandline). */
	static String logFile = "output.log";
	/** The file to which the error log is written (if specified via commandline). */
	static String errFile = "error.log";
	/** The batch project which is loaded if -batch command option is used. */
	static BatchProject bp = null;
	/** States if visualization is used, or not. Can be changed via commandline. */
	public static boolean useVisualization = true;
	/** States if statistic is used, or not. Can be changed via commandline. */
	public static boolean useStatistic = true;
	/** The properties in the information file. */
	public static PropertyTreeModel ptmInformation;
	/** The properties in the information file. */
	public static PropertyTreeModel ptmOptions;
	/** The log of the application. */
	public static Log log = new Log();
	
	/**
	 * Creates a new instance of <code>ZETMain</code>
	 */
	private ZETMain() { }

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

		Switch optDebug = new Switch( "debug" ).setLongFlag( "debug" )
						.setShortFlag( 'd' );
		optDebug.setHelp( loc.getString( "debug" ) );
		jsap.registerParameter( optDebug );

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
			System.err.println( loc.getString( "log.disableVisualization" ) );
			useVisualization = false;
		}

		if( config.getBoolean( "noStat" ) ) {
			System.err.println( loc.getString( "log.disableStatistic" ) );
			useStatistic = false;
		}

		if( config.contains( "batch" ) )
			loadBatchProject( config.getString( "batch" ) );

		if( config.contains( "project" ) )
			loadedProject = config.getString( "project" );

		if( config.contains( "property" ) )
			propertyFilename = config.getString( "property" );

		debug = config.getBoolean( "debug" );

		createEditor();
	}

	/**
	 * Creates the editor window and displays it on the screen. This method should
	 * be called by a scheduler, so it can be executed in the event-dispatch
	 * thread. That allows to have user interaction if the program is working in
	 * other threads.
	 * @see SwingUtilities#invokeLater(java.lang.Runnable)
	 * @see Runnable
	 */
	public static void createEditor() {
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				// First load base parameters
				File optionFile = new File( "./basezetoptions.xml" );
				File informationFile = new File( "./baseoptions.xml" );
				File propertyFile = new File( propertyFilename );
				checkFile( informationFile, "Information file" );
				checkFile( optionFile, "Option file" );
				checkFile( propertyFile, "Property file" );
				// Load properties
				try {
					PropertyContainer.getInstance().applyParameters( propertyFile );
				} catch( PropertyLoadException ex ) {
					exit( ex.getMessage() );
				}
				// Load default values
				try {
					ptmOptions = PropertyContainer.getInstance().applyParameters( optionFile );
				} catch( PropertyLoadException ex1 ) {
					exit( ex1.getMessage() );
				}
				try { // Load from default-file if no user-specific is available
					ptmInformation = PropertyContainer.getInstance().applyParameters( informationFile );
				} catch( PropertyLoadException ex1 ) {
					exit( ex1.getMessage() );
				}
				// Load user defined parameters, do not check if they exist
				optionFile = new File( optionFilename );
				informationFile = new File( informationFilename );
				try {
					PropertyContainer.getInstance().applyParameters( optionFile );
				} catch( PropertyLoadException ex ) { }
				try {
					PropertyContainer.getInstance().applyParameters( informationFile );
				} catch( PropertyLoadException ex ) { }
				// Update the values in the ptms
				ZETMain.ptmInformation.getRoot().reloadFromPropertyContainer();
				ZETMain.ptmOptions.getRoot().reloadFromPropertyContainer();

				// Change look and feel to native
				GUIOptionManager.changeLookAndFeel( UIManager.getSystemLookAndFeelClassName() );

				// Start our editor in the event-dispatch-thread
				JEditor edit = JEditor.getInstance();
				// The control object for projects
				ZControl zcontrol = null;

				File iconFile = new File( "./icon.gif" );
				checkFile( iconFile );
				try {
					Image img = ImageIO.read( iconFile );
					edit.setIconImage( img );
				} catch( IOException e ) {
					exit( "Error loding icon." );
				}

				JPropertySelectorWindow a = new JPropertySelectorWindow( edit, "", 100, 100, propertyFilename );
				a.saveWorking();
				a = null;

				edit.addMainComponents();
				System.out.println( "ZET-Fenster geladen." );
				if( bp != null ) {
					for( BatchProjectEntry bpe : bp ) {
						edit.addBatchEntry( bpe );
					}
				} if( !loadedProject.equals( "" ) ) {
					File f = new File( loadedProject );
					checkFile( f, "Project file" );
					zcontrol = new ZControl( f );
					System.out.println( "Projekt " + f.getAbsolutePath() + " geladen." );
				} else {
					zcontrol = new ZControl();
				}
				edit.setZControl( zcontrol );

				edit.setVisible( true );
			}
		} );
	}

	/**
	 * Loads an xml-file containing several tasks with projects to evacuate
	 * (simulation and optimization).
	 * @param string a batch file
	 */
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
	 * Sets up the standard output and the error output to some logging files.
	 * @param log indicates wheather standard out is redirected to a file
	 * @param err indicates wheather error out is redirected to a file
	 * @param auto indicates wheather the filenames are automatically created
	 * @param verbose indicates wheather the default output is also used, or not
	 */
	public static void setUpLog( boolean log, boolean err, boolean auto, boolean verbose ) {
		log = true;
		err = true;
		PrintStream errStream = System.err;
		PrintStream logStream = System.out;
		Calendar cal = Calendar.getInstance();
		if( auto ) {
			logFile = "zet_";
			errFile = "zet_";
			logFile += cal.get( Calendar.YEAR ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.MONTH )+1, 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.DAY_OF_MONTH ), 2 ) + "_" + IOTools.fillLeadingZeros( cal.get( Calendar.HOUR_OF_DAY ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.MINUTE ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.SECOND ), 2 ) + ".log";
			errFile += cal.get( Calendar.YEAR ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.MONTH )+1, 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.DAY_OF_MONTH ), 2 ) + "_" + IOTools.fillLeadingZeros( cal.get( Calendar.HOUR_OF_DAY ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.MINUTE ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.SECOND ), 2 ) + "_err.log";
			log = true;
			err = true;
		}
		if( log )
			try {
				logStream = verbose ? new DebugStreamVerbose( new FileOutputStream( logFile ), System.out ) : new DebugStream( new FileOutputStream( logFile ) );
			} catch( FileNotFoundException ex ) {
				System.err.println( "Error creating debug out." );
			}
		if( err )
			if( log && logFile.equals( errFile ) )
				errStream = logStream;
			else
				try {
					errStream = verbose ? new DebugStreamVerbose( new FileOutputStream( errFile ), System.err, MessageType.LogError ): new DebugStream( new FileOutputStream( errFile ), MessageType.LogError );
				} catch( FileNotFoundException ex ) {
					System.err.println( "Error creating debug error out." );
				}
		System.setOut( logStream );
		System.setErr( errStream );
		if( log )
			System.out.println( "Log of " + cal.get( Calendar.YEAR ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.MONTH )+1, 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.DAY_OF_MONTH ), 2 ) + " " + IOTools.fillLeadingZeros( cal.get( Calendar.HOUR_OF_DAY ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.MINUTE ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.SECOND ), 2 ) );
		if( log && !logFile.equals( errFile ) )
			System.err.println( "Error log of " + cal.get( Calendar.YEAR ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.MONTH )+1, 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.DAY_OF_MONTH ), 2 ) + " " + IOTools.fillLeadingZeros( cal.get( Calendar.HOUR_OF_DAY ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.MINUTE ), 2 ) + "-" + IOTools.fillLeadingZeros( cal.get( Calendar.SECOND ), 2 ) );
	}

	/**
	 * Displays an error message in the left edge of the status bar
	 * @param msg the message
	 */
	public static void sendError( String msg ) {
		EventServer.getInstance().dispatchEvent( new MessageEvent<JEditor>( JEditor.getInstance(), MessageType.Error, msg ) );
	}

	/**
	 * Displays an error message in the middle of the status bar
	 * @param msg the message
	 */
	public static void sendMessage( String msg ) {
		EventServer.getInstance().dispatchEvent( new MessageEvent<JEditor>( JEditor.getInstance(), MessageType.Status, msg ) );
	}

	/**
	 * Indicates wheather the program is in debug mode, or not.
	 * @return <code>true</code> if debug mode is on, <code>false</code> otherwise.
	 */
	public static boolean isDebug() {
		return debug;
	}
}