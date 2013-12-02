/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import de.tu_berlin.math.coga.common.debug.Debug;
import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.components.framework.Menu;
import ds.PropertyContainer;
import event.EventServer;
import event.MessageEvent;
import event.MessageEvent.MessageType;
import gui.editor.properties.PropertyLoadException;
import gui.propertysheet.PropertyTreeModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import zet.gui.GUILocalization;
import zet.gui.main.JZetWindow;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ZETLoader {

	/** The localization class. */
	private static Localization loc = GUILocalization.getSingleton();
	/** Indicates whether debug mode is active, or not. */
	private static boolean debug;
	/** The project file that should be loaded (submitted via command line). */
	static String loadedProject = "";
	/** The filename for the file that contains the program options. */
	public static final String optionFilename = "zetoptions.xml";
	/** The filename for the file that contains additional options. */
	public static final String informationFilename = "options.xml";
	/** States if visualization is used, or not. Can be changed via command line. */
	public static boolean useVisualization = true;
	/** States if statistic is used, or not. Can be changed via command line. */
	public static boolean useStatistic = true;
	/** The properties in the information file. */
	public static PropertyTreeModel ptmInformation;
	/** The properties in the information file. */
	public static PropertyTreeModel ptmOptions;
	/** States if the last loaded file should be loaded at startup. */
	private static boolean loadLast = false;
	/** The logger of the main class. */
	private static final Logger log = Logger.getGlobal();

	/**
	 * Creates a new instance of {@code ZETMain}
	 */
	private ZETLoader() {	}

	static void load( String[] args ) {
		loc.addSupportedLocale( Locale.GERMAN );
		loc.addSupportedLocale( Locale.ENGLISH );
		Menu.setLoc( loc );
		try {

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

		Switch optLoadLast = new Switch( "loadlast" ).setLongFlag( "loadlast" );
		optLoadLast.setHelp( loc.getString( "loadLast" ) );// TODO add text for load last
		jsap.registerParameter( optLoadLast );

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

		Switch optVerbose = new Switch( "verbose" ).setLongFlag( "verbose" ).setShortFlag( 'v' );
		optVerbose.setHelp( loc.getString( "verbose" ) );
		jsap.registerParameter( optVerbose );

		Switch optDebug = new Switch( "debug" ).setLongFlag( "debug" ).setShortFlag( 'd' );
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
			for( java.util.Iterator errs = config.getErrorMessageIterator(); errs.hasNext();)
				System.err.println( loc.getString( "error" ) + errs.next() );

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
			ZETMain.logFile = config.getString( "log" );
		if( config.contains( "err" ) )
			ZETMain.errFile = config.getString( "err" );
		ZETMain.setUpLog( config.contains( "log" ), config.contains(  "err" ), config.getBoolean( "autolog" ), config.getBoolean( "verbose" ) );

		if( config.getBoolean( "noVis" ) ) {
			System.err.println( loc.getString( "log.disableVisualization" ) );
			useVisualization = false;
		}

		if( config.getBoolean( "noStat" ) ) {
			log.severe( loc.getString( "log.disableStatistic" ) );
			useStatistic = false;
		}

		if( config.contains( "batch" ) )
			loadBatchProject( config.getString( "batch" ) );

		if( config.contains( "project" ) )
			loadedProject = config.getString( "project" );

		loadLast = config.contains( "loadlast" );

		if( config.contains( "property" ) ) {
			try {
				ZETProperties.setCurrentProperty( Paths.get( config.getString( "property" ) ) );
			} catch( PropertyLoadException ex ) {
				log.log( Level.SEVERE, "Property file ''{0}'' cound not be loaded. Continuing with default.", config.getString( "property" ));
			}
		}

		debug = config.getBoolean( "debug" );
		if( debug ) {
			//Debug.setDefaultLogLevel( Level.ALL );
			Debug.setDefaultLogLevel( Level.FINER );
			//log.log( Level.INFO, "{0}{1}", new Object[]{String.format( "%-25s", "Java-Version:" ), System.getProperty( "java.version" )});

			log.log(  Level.INFO, "{0}{1}", new Object[]{String.format( "%-25s", "Runtime:" ), System.getProperty( "java.runtime.name" )});
			log.log(  Level.INFO, "{0}{1}", new Object[]{String.format( "%-25s", "Java-Laufzeit-Version:" ), System.getProperty( "java.runtime.version" )});


			log.log(  Level.INFO, "{0}{1}", new Object[]{String.format( "%-25s", "VM:" ), System.getProperty( "java.vm.name" )});
			log.log(  Level.INFO, "{0}{1}", new Object[]{String.format( "%-25s", "VM-Version:" ), System.getProperty( "java.vm.version" )});
			log.log(  Level.INFO, "{0}{1}", new Object[]{String.format( "%-25s", "Hersteller:" ), System.getProperty( "java.vm.vendor" )});

			log.log(  Level.INFO, "{0}{1}", new Object[]{String.format( "%-25s", "Betriebssystem:" ), System.getProperty( "os.name" )});
			log.log(  Level.INFO, "{0}{1}", new Object[]{String.format( "%-25s", "Architektur:" ), System.getProperty( "os.arch" )});

			log.log( Level.INFO, "{0}{1}", new Object[]{String.format( "%-25s", "Encoding:" ), System.getProperty( "sun.jnu.encoding" )});

			boolean first = true;
			String[] paths = System.getProperty( "java.library.path" ).split( ":" );
			for( String s : paths ) {
				if( first ) {
					log.log(  Level.INFO, "{0}{1}", new Object[]{String.format( "%-25s", "Library Path:" ), s});
					first = false;
				} else
					log.log( Level.INFO, "                         {0}", s);
			}

			paths = System.getProperty( "java.class.path" ).split( ":" );
			first = true;
			for( String s : paths ) {
				if( first ) {
					log.log(  Level.INFO, "{0}{1}", new Object[]{String.format( "%-25s", "Class Path:" ), s});
					first = false;
				} else
					log.log( Level.INFO, "                         {0}", s);
			}
			log.log( Level.INFO, "{0}{1}", new Object[]{String.format( "%-25s", "Befehlszeile:" ), System.getProperty( "sun.java.command" )});
		}

		createEditor();
		} catch (JSAPException ex ) {

		}
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
				checkFile( informationFile, "Information file" );
				checkFile( optionFile, "Option file" );
				// Load properties
				try {
					if( ZETProperties.getCurrentPropertyTreeModel() == null )
						ZETProperties.setCurrentProperty( Paths.get( "./properties/properties.xml" ) );
				} catch( PropertyLoadException ex ) {
					exit( "Property file could not be loaded" );
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
				ZETLoader.ptmInformation.getRoot().reloadFromPropertyContainer();
				ZETLoader.ptmOptions.getRoot().reloadFromPropertyContainer();

				// Change look and feel to native
				GUIOptionManager.changeLookAndFeel( UIManager.getSystemLookAndFeelClassName() );

				GUIControl guiControl = new GUIControl();
				try {
					guiControl.createZETWindow();
				} catch( UnsatisfiedLinkError ex ) {
					// jogl naitive libraries do not seem to be present
					ex.printStackTrace( System.err );
					exit( "Could not load main window. \nPossibly native OpenGL libraries are not present.\n\nError:\n" + ex.getLocalizedMessage() );
				}
				try {
					guiControl.loadTemplates();
					log.config( "Templates loaded." );
				} catch( ParserConfigurationException | SAXException | IOException ex ) {
					log.log( Level.SEVERE, "Error loading templates!", ex );
				}

				// The control object for projects
				System.out.println( "ZET-Fenster geladen." );

				// load last used file, if necessary
				if( loadLast ) {
					loadedProject = PropertyContainer.getInstance().getAsString( "information.file.lastFile1" );
				}

				if( !loadedProject.equals( "" ) ) {
					File f = new File( loadedProject );
					if( !loadLast )
						checkFile( f, "Project file" );
					else {
						if( !f.exists() )
							f = null;
					}
					if( f != null) {
						guiControl.loadProject( f );
						log.log( Level.INFO, "Projekt {0} geladen.", f.getAbsolutePath());
						GUIOptionManager.setLastFile( 1, f.getAbsolutePath() );
					} else
						guiControl.newProject( true ); // Project supposed to load does not exist. Create empty
				} else {
					//guiControl.newProject();
					//zcontrol = new ZControl();
				}
				guiControl.showZETWindow();
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
		} catch( Exception ex ) {
			exit( "Error loading batch task file." );
		}
	}

	/**
	 * This method exits the program and prints an error message on the standard
	 * error stream. The return value is 1.
	 * @param errorMessage the error message
	 */
	public static void exit( String errorMessage ) {
		JOptionPane.showMessageDialog( null, errorMessage, "Exit of program due to error.", JOptionPane.ERROR_MESSAGE );
		log.severe( errorMessage );
		System.exit( 1 );
	}

	/**
	 * Checks if a file exists and quits with an error message and return code
	 * 1 if not.
	 * @param file the file that is to be checked
	 * @param fileType the file type that is checked. The error output will start with fileType
	 */
	public static void checkFile( File file, String fileType ) {
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
	public static void checkFile( File file ) {
		checkFile( file, "" );
	}

	/**
	 * Displays an error message in the left edge of the status bar
	 * @param msg the message
	 */
	public static void sendError( String msg ) {
		System.err.println( msg );
		EventServer.getInstance().dispatchEvent( new MessageEvent<JZetWindow>( null, MessageType.Error, msg ) );
	}

	/**
	 * Displays an error message in the middle of the status bar
	 * @param msg the message
	 */
	public static void sendMessage( String msg ) {
		System.out.println( msg );
		EventServer.getInstance().dispatchEvent( new MessageEvent<JZetWindow>( null, MessageType.Status, msg ) );
	}

	/**
	 * Indicates whether the program is in debug mode, or not.
	 * @return {@code true} if debug mode is on, {@code false} otherwise.
	 */
	public static boolean isDebug() {
		return debug;
	}

}
