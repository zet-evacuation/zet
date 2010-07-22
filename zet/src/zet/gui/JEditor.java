/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; witzethout even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/*
 * JEditor.java
 * Created on 4. Dezember 2007, 17:08
 */
package zet.gui;

import algo.ca.CellularAutomatonInOrderExecution;
import batch.Batch;
import batch.BatchResult;
import batch.CellularAutomatonAlgorithm;
import batch.GraphAlgorithm;
import converter.ZToCAConverter;
import ds.PropertyContainer;
import ds.ca.CellularAutomaton;
import ds.z.AssignmentType;
import ds.z.Floor;
import event.EventListener;
import event.EventServer;
import event.MessageEvent;
import event.MessageEvent.MessageType;
import event.ProgressEvent;
import gui.batch.JBatchView;
import gui.components.JEventStatusBar;
import gui.editor.EditMode;
import gui.ca.JCAView;
import gui.editor.JEditView;
import gui.editor.assignment.JAssignment;
import gui.statistic.JGraphStatisticPanel;
import gui.statistic.JStatisticPanel;
import gui.visualization.JVisualizationView;
import gui.visualization.control.GLControl;
import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.common.localization.Localized;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Locale;
import javax.media.opengl.GLCapabilities;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import batch.tasks.AlgorithmTask;
import batch.tasks.CARealTime;
import ds.z.ZControl;
import event.VisualizationEvent;
import gui.components.JLogPane;
import gui.statistic.JStatisticsPanel;
import zet.gui.components.toolbar.JEditToolbar;
import gui.Control;
import gui.ZETMain;
import java.awt.Image;
import javax.imageio.ImageIO;
import zet.gui.components.JZETMenuBar;
import zet.gui.components.toolbar.JBatchToolBar;
import zet.gui.components.toolbar.JLogToolBar;
import zet.gui.components.toolbar.JQuickVisualizationToolBar;
import zet.gui.components.toolbar.JStatisticCellularAutomatonToolbar;
import zet.gui.components.toolbar.JStatisticGraphToolBar;
import zet.gui.components.toolbar.JVisualizationToolbar;
import zet.util.ConversionTools;

/**
 * Displays a window with the editor.
 * @author Jan-Philipp Kappmeier, Timon Kelter
 */
public class JEditor extends JFrame implements Localized, EventListener<ProgressEvent> {

	public static final int EDIT_FLOOR = 0;
	public static final int BATCH = 1;
	public static final int CA_FLOOR = ZETMain.isDebug() ? 2 : -1;
	public static final int VISUALIZATION = ZETMain.isDebug() ? 3 : 2;
	public static final int STATISTIC = ZETMain.isDebug() ? 4 : 3;
	public static final int GRAPH_STATISTIC = ZETMain.isDebug() ? 5 : 4;
	public static final int LOG = ZETMain.isDebug() ? 6 : 5;
	public static final int STATISTICS = LOG + 1;
	/** The localization class. */
	static final Localization loc = Localization.getInstance();
	/** Stores the last mouse position if a mouse position event is sent. */
	private static Point lastMouse = new Point( 0, 0 );
	/** The delimiter used if numbers are stored in a tuple. */
	final static String delimiter = Localization.getInstance().getStringWithoutPrefix( "numberSeparator" );
	/** Control class for projects and editing */
	private ZControl zcontrol;
	private static boolean editing = false;
	private CellularAutomatonInOrderExecution caAlgo = null;
	private AlgorithmTask worker;
	private BatchResult result;
	// Options
	private boolean firstSwitch = false;
//	private EditMode.Type creationType = EditMode.Type.CreationPointwise;
	/** The number format used to display the zoom factor in the text field. */
	private NumberFormat nfZoom = NumberFormat.getPercentInstance();	// Main window components
	/** The status bar. */
	private JEventStatusBar statusBar;
	private JVisualizationView visualizationView;
	private GLControl control;
	/** The editor tab. */
	private JEditView editView;
	/** The batch view tab. */
	private JBatchView batchView;
	/** The tab for quick CA visualization. */
	private JCAView caView;
	/** The tab containing the CA statistic. */
	private JStatisticPanel caStatisticView;
	/** The tab containing the graph statistic. */
	private JGraphStatisticPanel graphStatisticView;	// Menu items
	/** The tab containing the log window. */
	private JLogPane logView;
	/** The general statistic tab. */
	public JStatisticsPanel statisticView;
	/** The tool bars */
	private JEditToolbar toolBarEdit;
	private JBatchToolBar toolBarBatch;
	private JQuickVisualizationToolBar toolBarCellularAutomatonQuickVisualization;
	private JVisualizationToolbar toolBarVisualization;
	private JStatisticCellularAutomatonToolbar toolBarCAStats;
	private JToolBar toolBarGraphStats;
	/** The tool bar that is visible if the log view is active. */
	private JLogToolBar toolBarLog;
	private JToolBar currentToolbar;
	/** A tabbed pane that allows switching of the different views. */
	private JTabbedPane tabPane;
	// Additional GUI stuff
	private boolean disableUpdate = false;
	private int currentMode = EDIT_FLOOR;
	private JAssignment distribution;
	/** Decides whether the visualization should be restarted if 'play' is pressed. */
	private boolean restartVisualization = false;
	/** Decides whether visualization runs in loop-mode, that means it automatically starts again. */
	private boolean loop = false;
	private Control guiControl;

	/**
	 * Creates a new instance of <code>JEditor</code>. Sets the editor position
	 * and size, loads file icon, tool bars and menus.
	 * @param guiControl the control class for the ZET GUI
	 */
	public JEditor( Control guiControl ) {
		super();
		this.guiControl = guiControl;

		// Set up locale information
		loc.setLocale( Locale.getDefault() );
		nfZoom.setMaximumFractionDigits( 2 );

		// Set window position
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setSize( 800, 600 );
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation( (d.width - getSize().width) / 2, (d.height - getSize().height) / 2 );

		// Create elements: menu, toolbars, status bar
		setJMenuBar( new JZETMenuBar( guiControl ) );
		getContentPane().setLayout( new BorderLayout() );
		statusBar = new JEventStatusBar();
		add( statusBar, BorderLayout.SOUTH );
		toolBarEdit = new JEditToolbar( guiControl );
		toolBarBatch = new JBatchToolBar( guiControl );
		toolBarCellularAutomatonQuickVisualization = new JQuickVisualizationToolBar( guiControl );
		toolBarVisualization = new JVisualizationToolbar( guiControl );
		toolBarCAStats = new JStatisticCellularAutomatonToolbar( guiControl );
		toolBarGraphStats = new JStatisticGraphToolBar( guiControl );
		toolBarLog = new JLogToolBar( guiControl );
		currentToolbar = toolBarEdit;
		add( toolBarEdit, BorderLayout.NORTH );

		// register class for progress events
		EventServer.getInstance().registerListener( this, ProgressEvent.class );

		// window listener
		this.addWindowListener( new WindowListener() {

			public void windowOpened( WindowEvent e ) { }

			public void windowClosing( WindowEvent e ) {
				if( graphStatisticView != null )
					graphStatisticView.saveSettings();
				try {
					ZETMain.ptmInformation.getRoot().reloadFromPropertyContainer();
					ZETMain.ptmOptions.getRoot().reloadFromPropertyContainer();
					PropertyContainer.saveConfigFile( ZETMain.ptmInformation, new File( ZETMain.informationFilename ) );
					PropertyContainer.saveConfigFile( ZETMain.ptmOptions, new File( ZETMain.optionFilename ) );
				} catch( IOException ex ) {
					System.err.println( "Error saving information file." );
				}
			}

			public void windowClosed( WindowEvent e ) { }

			public void windowIconified( WindowEvent e ) { }

			public void windowDeiconified( WindowEvent e ) { }

			public void windowActivated( WindowEvent e ) { }

			public void windowDeactivated( WindowEvent e ) { }
		} );

		// set up the icon
		final File iconFile = new File( "./icon.gif" );
		ZETMain.checkFile( iconFile );
		try {
			setIconImage( ImageIO.read( iconFile ) );
		} catch( IOException e ) {
			ZETMain.exit( "Error loding icon." );
		}
	}

	/**
	 * Loads the project currently controlled by the project controller. Resets
	 * the view to edit window and resets the zoom factor to 10%
	 */
	public void loadProject() {

		if( tabPane.getSelectedIndex() > 1 )
			tabPane.setSelectedIndex( 0 );
		editView.displayProject( zcontrol );
		// Löschen eingestellter parameter
		ZToCAConverter.getInstance().clear();
		firstSwitch = true;
		if( !PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" ) )
			editView.setFloor( 1 );
		// Updaten der gui
		this.getEditView().update();

		editView.setEditMode( EditMode.Selection );
		guiControl.setZoomFactor( 0.04d );

		// Set up the last camera position
		visualizationView.getGLContainer().getCamera().setPos( zcontrol.getProject().getVisualProperties().getCameraPosition().pos );
		visualizationView.getGLContainer().getCamera().setView( zcontrol.getProject().getVisualProperties().getCameraPosition().view );
		visualizationView.getGLContainer().getCamera().setUp( zcontrol.getProject().getVisualProperties().getCameraPosition().up );
		visualizationView.getGLContainer().setTexts( zcontrol.getProject().getVisualProperties().getTextureFontStrings() );
		visualizationView.getGLContainer().setView( zcontrol.getProject().getVisualProperties().getCurrentWidth(), zcontrol.getProject().getVisualProperties().getCurrentHeight() );
		visualizationView.updateCameraInformation();
	}

	/**
	 * Displays the mouse position in the right edge of the status bar
	 * @param position the mouse position in millimeter.
	 */
	public static void sendMouse( Point position ) {
		lastMouse = position;
		//String pixelCoords = "(" + Integer.toString (e.getX ()) + "," + Integer.toString (e.getY ()) + ")";
		String realCoordsMillimeter = "(" + Integer.toString( position.x ) + delimiter + Integer.toString( position.y ) + ")";
		String realCoordsMeter = "(" + Localization.getInstance().getFloatConverter().format( ConversionTools.toMeter( position.x ) ) + delimiter + Localization.getInstance().getFloatConverter().format( ConversionTools.toMeter( position.y ) ) + ")";
		//String text = /*"Pixel: " + pixelCoords + " - */ "Millimeter: " + realCoordsMillimeter + " - Meter: " + realCoordsMeter;
		String text = String.format( Localization.getInstance().getString( "gui.mousePositionMillimeterMeter" ), realCoordsMillimeter, realCoordsMeter );
		EventServer.getInstance().dispatchEvent( new MessageEvent<JEditor>( null, MessageType.MousePosition, text ) );
	}

	/**
	 * Displays an error message in the left edge of the status bar
	 * @return true if the editor is in editing mode.
	 */
	public static boolean isEditing() {
		return editing;
	}

	/**
	 * Enables or disables the flag for the editing mode.
	 * @param editing the status
	 */
	public static void setEditing( boolean editing ) {
		JEditor.editing = editing;
	}

	/**
	 * Sends a ready-message using {@link ZETMain#sendMessage(java.lang.String) }.
	 */
	public static void sendReady() {
		ZETMain.sendMessage( loc.getString( "gui.message.ready" ) );
	}

	/*****************************************************************************
	 *                                                                           *
	 * GUI initialization                                                        *
	 *                                                                           *
	 ****************************************************************************/
	/**
	 * Adds the main views to the window. These views are all included in
	 * tabs.
	 */
	public void addMainComponents() {
		editView = new JEditView( guiControl );
		batchView = new JBatchView( guiControl );
		visualizationView = new JVisualizationView( new GLCapabilities(), guiControl );
		visualizationView.setFloorSelectorEnabled( !PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.floors" ) );

		caView = new JCAView();
		caStatisticView = new JStatisticPanel();
		graphStatisticView = new JGraphStatisticPanel();
		logView = new JLogPane( ZETMain.log );
		statisticView = new JStatisticsPanel();

		tabPane = new JTabbedPane();

		loc.setPrefix( "gui.editor.JEditor.tab" );
		tabPane.addTab( loc.getString( "Edit" ), null, editView, loc.getString( "EditToolTip" ) );
		tabPane.addTab( loc.getString( "Batch" ), null, batchView, loc.getString( "BatchToolTip" ) );
		if( ZETMain.isDebug() )
			tabPane.addTab( loc.getString( "CAView" ), null, caView, loc.getString( "CAViewToolTip" ) );
		tabPane.addTab( loc.getString( "Visualization" ), null, visualizationView, loc.getString( "VisualizationToolTip" ) );
		tabPane.addTab( loc.getString( "Statistic" ), null, caStatisticView, loc.getString( "StatisticToolTip" ) );
		tabPane.addTab( loc.getString( "GraphStatistic" ), null, graphStatisticView, loc.getString( "GraphStatisticToolTip" ) );
		tabPane.addTab( loc.getString( "LogWindow" ), null, logView, loc.getString( "LogWindowToolTip" ) );
		if( ZETMain.isDebug() )
			tabPane.addTab( loc.getString( "Statistics" ), null, statisticView, loc.getString( "StatisticsToolTip" ) );
		tabPane.addChangeListener( chlTab );
		loc.setPrefix( "" );

		getContentPane().add( tabPane, BorderLayout.CENTER );

		tabPane.addChangeListener( new ChangeListener() {

			public void stateChanged( ChangeEvent e ) {
				if( tabPane.getSelectedIndex() == LOG )
					logView.update();
			}
		} );

		ZETMain.sendMessage( loc.getString( "gui.status.EditorInitialized" ) );
	}


	public void setUpKeyStrokes() {
		// Register Shortcuts (no-menu-shortcuts)
		KeyStroke up = KeyStroke.getKeyStroke( KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK );
		ActionListener acl = new ActionListener() {

			public void actionPerformed( ActionEvent e ) {
				switch( editView.getEastPanelType() ) {
					case JEditView.FLOOR_PANEL:
						editView.setFloorNameFocus();
						break;
					case JEditView.ROOM_PANEL:
						editView.setRoomNameFocus();
						break;
					default:
						System.out.println( "Nothing" );
				}
			}
		};
		tabPane.registerKeyboardAction( acl, "test", up, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
	}


	public void resetAssignment() {
		distribution = null;
	}

	public void showAssignmentDialog() {
		//if( distribution == null ) {
		distribution = new JAssignment( this, zcontrol.getProject(), loc.getString( "gui.editor.assignment.JAssignment.Title" ), 850, 400 );
		//}
		distribution.setVisible( true );
		distribution.dispose();
	}

	/**
	 * Changes the appearance of the GUI to the selected language.
	 * @see de.tu_berlin.math.coga.common.localization.Localization
	 */
	public void localize() {
		// Localize tool bars
		loc.setPrefix( "gui.editor.JEditor." );
		toolBarEdit.localize();
		toolBarVisualization.localize();
		toolBarLog.localize();
		toolBarBatch.localize();
		toolBarCellularAutomatonQuickVisualization.localize();
		loc.setPrefix( "" );

		// Localize other main components
		editView.localize();
		visualizationView.localize();

		loc.setPrefix( "gui.editor.JEditor." );



		// Localize tabs
		loc.setPrefix( "gui.editor.JEditor.tab" );
		tabPane.setTitleAt( EDIT_FLOOR, loc.getString( "Edit" ) );
		tabPane.setToolTipTextAt( EDIT_FLOOR, loc.getString( "EditToolTip" ) );
		tabPane.setTitleAt( BATCH, loc.getString( "Batch" ) );
		tabPane.setToolTipTextAt( BATCH, loc.getString( "BatchToolTip" ) );
		if( ZETMain.isDebug() ) {
			tabPane.setTitleAt( CA_FLOOR, loc.getString( "CAView" ) );
			tabPane.setToolTipTextAt( CA_FLOOR, loc.getString( "CAViewToolTip" ) );
		}
		tabPane.setTitleAt( VISUALIZATION, loc.getString( "Visualization" ) );
		tabPane.setToolTipTextAt( VISUALIZATION, loc.getString( "VisualizationToolTip" ) );
		tabPane.setTitleAt( STATISTIC, loc.getString( "Statistic" ) );
		tabPane.setToolTipTextAt( STATISTIC, loc.getString( "StatisticToolTip" ) );
		tabPane.setTitleAt( GRAPH_STATISTIC, loc.getString( "GraphStatistic" ) );
		tabPane.setToolTipTextAt( GRAPH_STATISTIC, loc.getString( "GraphStatisticToolTip" ) );
		tabPane.setTitleAt( LOG, loc.getString( "LogWindow" ) );
		tabPane.setToolTipTextAt( LOG, loc.getString( "LogWindowToolTip" ) );
		if( ZETMain.isDebug() ) {
			tabPane.setTitleAt( STATISTICS, loc.getString( "Statistics" ) );
			tabPane.setToolTipTextAt( STATISTICS, loc.getString( "StatisticsToolTip" ) );
		}
		loc.setPrefix( "" );

		sendMouse( lastMouse );
		ZETMain.sendError( "" );
		ZETMain.sendMessage( loc.getStringWithoutPrefix( "gui.status.LanguageChangedTo" ) );
	}
	/*****************************************************************************
	 *                                                                           *
	 * Some listener for needed updates                                          *
	 *                                                                           *
	 ****************************************************************************/
	// TODO move to control/JZETMEnuBar and reimplement that stuff...
	ActionListener aclExecute = new ActionListener() {

		@Override
		public void actionPerformed( ActionEvent e ) {
			if( e.getActionCommand().equals( "startSimulation" ) )
				simulateCA();
			else if( e.getActionCommand().equals( "stepByStepSimulation" ) ) {
				stepByStepSimulation();
				if( tabPane.getSelectedIndex() != CA_FLOOR )
					tabPane.setSelectedIndex( CA_FLOOR );
			} else if( e.getActionCommand().equals( "visualization" ) ) {
				quickVisualization();
				if( tabPane.getSelectedIndex() != CA_FLOOR )
					tabPane.setSelectedIndex( CA_FLOOR );
			} else if( e.getActionCommand().equals( "QT" ) )
				//createGraph();	// nf wird gesetzt
				quickestTransshipment();
			else if( e.getActionCommand().equals( "MFOTMC" ) )
				maxFlowOverTimeMinCost();
			else if( e.getActionCommand().equals( "MFOTTEN" ) )
				maxFlowOverTimeTimeExpanded();
			else if( e.getActionCommand().equals( "EAT" ) )
				earliestArrivalTransshipment();
			else
				ZETMain.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
		}

		private void earliestArrivalTransshipment() {
			try {
				Batch res = new Batch();
				res.addEntry( "EA Transshipment with SSSP", zcontrol.getProject(), 1, GraphAlgorithm.EarliestArrivalTransshipmentSuccessiveShortestPaths, CellularAutomatonAlgorithm.Swap );
				res.getEntries().get( 0 ).setUseCa( false );
				setBatchResult( res.execute( false ) );
				tabPane.setSelectedIndex( VISUALIZATION );
			} catch( Exception ex ) {
				ZETMain.sendError( ex.getLocalizedMessage() );
			}
		}

		private void maxFlowOverTimeMinCost() {
			try {
				Batch res = new Batch();
				res.addEntry( "MaxFlow", zcontrol.getProject(), 1, GraphAlgorithm.MaxFlowOverTimeMinCost, CellularAutomatonAlgorithm.Swap );
				res.getEntries().get( 0 ).setUseCa( false );
				setBatchResult( res.execute( false ) );
				tabPane.setSelectedIndex( VISUALIZATION );
			} catch( Exception ex ) {
				ZETMain.sendError( ex.getLocalizedMessage() );
			}
		}

		private void maxFlowOverTimeTimeExpanded() {
			try {
				Batch res = new Batch();
				res.addEntry( "MaxFlow", zcontrol.getProject(), 1, GraphAlgorithm.MaxFlowOverTimeTimeExpanded, CellularAutomatonAlgorithm.Swap );
				res.getEntries().get( 0 ).setUseCa( false );
				setBatchResult( res.execute( false ) );
				tabPane.setSelectedIndex( VISUALIZATION );
			} catch( Exception ex ) {
				ZETMain.sendError( ex.getLocalizedMessage() );
			}
		}

		private void quickestTransshipment() {
			try {
				Batch res = new Batch();
				res.addEntry( "Quickest Transshipment", zcontrol.getProject(), 1, GraphAlgorithm.QuickestTransshipment, CellularAutomatonAlgorithm.Swap );
				res.getEntries().get( 0 ).setUseCa( false );
				setBatchResult( res.execute( false ) );
				tabPane.setSelectedIndex( VISUALIZATION );
			} catch( Exception ex ) {
				ZETMain.sendError( ex.getLocalizedMessage() );
			}
		}

		private void simulateCA() {
			try {
				Batch res = new Batch();
				res.addEntry( "CA", zcontrol.getProject(), 1, GraphAlgorithm.EarliestArrivalTransshipmentSuccessiveShortestPaths, CellularAutomatonAlgorithm.Swap );
				res.getEntries().get( 0 ).setUseGraph( false );
				setBatchResult( res.execute( false ) );
				tabPane.setSelectedIndex( VISUALIZATION );
			} catch( Exception ex ) {
				ZETMain.sendError( ex.getLocalizedMessage() );
			}
		}
	};
	ChangeListener chlTab = new ChangeListener() {

		public void stateChanged( ChangeEvent e ) {
			final int i = tabPane.getSelectedIndex();
			if( i == EDIT_FLOOR ) {
				switchTo( EDIT_FLOOR );
				editView.updateFloorView();
			} else if( i == BATCH ) {
				// Add curent project, if not already done
				if( firstSwitch ) {
					batchView.addProject( zcontrol.getProject() );
					firstSwitch = false;
				}
				switchTo( BATCH );
			} else if( i == CA_FLOOR )
				switchTo( CA_FLOOR );
			else if( i == VISUALIZATION )
				switchTo( VISUALIZATION );
			else if( i == STATISTIC )
				switchTo( STATISTIC );
			else if( i == GRAPH_STATISTIC )
				switchTo( GRAPH_STATISTIC );
			else if( i == LOG )
				switchTo( LOG );
			else if( i == STATISTICS )
				switchTo( STATISTICS );
			else
				ZETMain.sendError( "Unknown tab index:" + tabPane.getSelectedIndex() + ". " + loc.getString( "gui.ContactDeveloper" ) );
		}
	};

	/*****************************************************************************
	 *                                                                           *
	 * Algorithm starter methods                                                 *
	 *                                                                           *
	 ****************************************************************************/

	private boolean continueTask() {
		if( JOptionPane.showOptionDialog( this,
						"Es läuft ein Task. Soll der aktuelle Lauf abgebrochen werden und ein neuer gestartet werden?", //TODO loc
						"Task läuft", // TODO loc
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null, null, null ) == JOptionPane.YES_OPTION ) {
			// Task abbrechen
			caAlgo.cancel();
			worker.cancel( true );
			caAlgo = null;
			return false;
		} else
			return true;
	}

	private void quickVisualization() {
		if( worker != null && !worker.isDone() && continueTask() )
			return;

		try {
			CellularAutomaton ca = ZToCAConverter.getInstance().convert( zcontrol.getProject().getBuildingPlan() );
			for( AssignmentType at : zcontrol.getProject().getCurrentAssignment().getAssignmentTypes() )
				ca.setAssignmentType( at.getName(), at.getUid() );
			ZToCAConverter.applyConcreteAssignment(
							zcontrol.getProject().getCurrentAssignment().createConcreteAssignment( 400 ) );
			caAlgo = new CellularAutomatonInOrderExecution( ca );
			caAlgo.setMaxTimeInSeconds( PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" ) );

			caAlgo = new CARealTime( ca );
			CARealTime caRealTime = (CARealTime) caAlgo;
			caRealTime.setStepTime( 550 );

			worker = AlgorithmTask.getNewInstance();
			worker.setTask( caRealTime );
			try {
				worker.executeAlgorithm( true );
			} catch( Exception ex ) {
				printException( ex );
			}
		} catch( ZToCAConverter.ConversionNotSupportedException ex ) {
			ZETMain.sendError( ex.getLocalizedMessage() );
		}
	}

	public void createBackup() {
		createBackup( zcontrol.getProject().getProjectFile() );
	}

	public static void createBackup( File file ) {
		if( file != null && !file.getPath().equals( "" ) ) {
			String source = file.getPath();
			String dest = source.substring( 0, source.length() - 3 ) + "bak";
			try {
				copyFile( file, new File( dest ), 100, true );
			} catch( Exception e ) {
				ZETMain.sendError( "Fehler beim anlegen der Sicherungskopie" );
			}
		}
	}

	public static void copyFile( File src, File dest, int bufSize, boolean force ) throws IOException {
		if( dest.exists() )
			if( force )
				dest.delete();
			else
				throw new IOException( "Cannot overwrite existing file: " + dest.getName() );
		byte[] buffer = new byte[bufSize];
		int read = 0;
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream( src );
			out = new FileOutputStream( dest );
			while( true ) {
				read = in.read( buffer );
				if( read == -1 )
					//-1 bedeutet EOF
					break;
				out.write( buffer, 0, read );
			}
		} finally {
			// Sicherstellen, dass die Streams auch
			// bei einem throw geschlossen werden.
			// Falls in null ist, ist out auch null!
			if( in != null )
				//Falls tatsächlich in.close() und out.close()
				//Exceptions werfen, diejenige von 'out' geworfen wird.
				try {
					in.close();
				} finally {
					if( out != null )
						out.close();
				}
		}
	}

	/**
	 * 
	 * @param r
	 */
	public void setBatchResult( BatchResult r ) {
		if( result != null && JOptionPane.showConfirmDialog( this, "Alte Ergebnisse werden "
						+ "hiermit überschrieben. Wollen Sie fortfahren?", "Überschreiben",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE ) != JOptionPane.YES_OPTION )
			return;

		this.result = r;

		// TODO move setResult from caStatisticView call from here to control
		caStatisticView.setResult( result );
		guiControl.rebuild( result );

//		mnuFileSaveResultAs.setEnabled( result != null );
	}

	private void stepByStepSimulation() {
		if( worker != null && !worker.isDone() && continueTask() )
			return;

		try {
			CellularAutomaton ca = ZToCAConverter.getInstance().convert( zcontrol.getProject().getBuildingPlan() );
			for( AssignmentType at : zcontrol.getProject().getCurrentAssignment().getAssignmentTypes() )
				ca.setAssignmentType( at.getName(), at.getUid() );
			ZToCAConverter.applyConcreteAssignment( zcontrol.getProject().getCurrentAssignment().createConcreteAssignment( 400 ) );
			caAlgo = new CellularAutomatonInOrderExecution( ca );
			caAlgo.setMaxTimeInSeconds( PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" ) );

			caAlgo = new CARealTime( ca );
			caAlgo.setStepByStep( true );

			worker = AlgorithmTask.getNewInstance();
			worker.setTask( caAlgo );
			try {
				worker.executeAlgorithm( true );
			} catch( Exception ex ) {
				printException( ex );
			}
		} catch( ZToCAConverter.ConversionNotSupportedException ex ) {
			ZETMain.sendError( ex.getLocalizedMessage() );
		}
	}
	PropertyChangeListener pclStepByStep = new PropertyChangeListener() {

		public void propertyChange( PropertyChangeEvent evt ) {
			if( evt.getPropertyName().equals( "progress" ) ) {
				//int progress = (Integer)evt.getNewValue();
			}
		}
	};

	/*****************************************************************************
	 *                                                                           *
	 * Some helper and tool methods                                              *
	 *                                                                           *
	 ****************************************************************************/
//	/**
//	 * Enables and disables the menu item that moves a floor up.
//	 * @param enabled the enabled status
//	 */
//	public void enableMenuFloorUp( boolean enabled ) {
//		mnuEditFloorUp.setEnabled( enabled );
//	}
//
//	/**
//	 * Enables and disables the menu item that moves a floor down.
//	 * @param enabled the enabled status
//	 */
//	public void enableMenuFloorDown( boolean enabled ) {
//		mnuEditFloorDown.setEnabled( enabled );
//	}
	/**
	 * Returns the edit view component.
	 * @return the edit view component.
	 */
	public JEditView getEditView() {
		return editView;
	}

	/**
	 * Returns the visualization view component.
	 * @return the visualization view component.
	 */
	public JVisualizationView getVisualizationView() {
		return visualizationView;
	}

	public static void printException( Exception ex ) {
		System.out.println( "Eine Exception trat auf:" );
		System.out.println( "Message: " + ex.getMessage() );
		System.out.println( "Localized: " + ex.getLocalizedMessage() );
		System.out.println( "Cause: " + ex.getCause() );
		ex.printStackTrace( System.err );
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ex.printStackTrace( new PrintStream( bos ) );
		JOptionPane.showMessageDialog( null, bos.toString(), "Error", JOptionPane.ERROR_MESSAGE );
	}

	/**
	 * Shows a <code>JToolBar</code> and hides all others.
	 * @param toolBar the tool bar that is shown
	 */
	private void showToolBar( JToolBar newToolbar ) {
		getContentPane().remove( currentToolbar );
		getContentPane().add( newToolbar, BorderLayout.NORTH );
		currentToolbar = newToolbar;
	}

	/**
	 * Sets the visible tab. Enables and disables all necessary menu and
	 * tool bar elements.
	 * @param tabID the tab id as specified in {@link JEditor}.
	 */
	private void switchTo( int tabID ) {
		if( tabID == CA_FLOOR && worker == null ) {
			ZETMain.sendError( loc.getStringWithoutPrefix( "gui.error.StartSimulation" ) );
			tabPane.setSelectedIndex( currentMode );
			return;
		}
		// TODO better implementation of this stuff for debug mode ?
		if( ((ZETMain.isDebug() && tabID > CA_FLOOR) || (!ZETMain.isDebug() && tabID > BATCH)) && result == null && tabID != LOG && tabID != STATISTICS ) {
			ZETMain.sendError( loc.getStringWithoutPrefix( "gui.error.CreateBatch" ) );
			tabPane.setSelectedIndex( currentMode );
			return;
		}

		currentMode = tabID;
		// code using the switch-bar is disabled!
		guiControl.visualizationPause();
		if( tabID == EDIT_FLOOR )
			showToolBar( toolBarEdit );
		else if( tabID == BATCH )
			showToolBar( toolBarBatch );
		else if( tabID == CA_FLOOR )
			showToolBar( toolBarCellularAutomatonQuickVisualization );
		else if( tabID == VISUALIZATION ) {
			showToolBar( toolBarVisualization );
			visualizationView.requestFocusInWindow();
		} else if( tabID == STATISTIC )
			showToolBar( toolBarCAStats );
		else if( tabID == GRAPH_STATISTIC )
			showToolBar( toolBarGraphStats );
		else if( tabID == LOG )
			showToolBar( toolBarLog );
		else if( tabID == STATISTICS )
			showToolBar( toolBarLog );
		else
			ZETMain.sendError( "Unbekannte TabID: " + Integer.toString( tabID ) + ". " + loc.getString( "gui.ContactDeveloper" ) );
		repaint();
		validate();
	}

	public void disableProjectUpdate( boolean disableUpdate ) {
		System.out.println( "update wird auf " + disableUpdate + "gesetzt" );
		this.disableUpdate = disableUpdate;
	}

	public boolean isUpdateDisabled() {
		return this.disableUpdate;
	}

	public JBatchView getBatchView() {
		return batchView;
	}


	/*****************************************************************************
	 *                                                                           *
	 * Event handler                                                             *
	 *                                                                           *
	 ***************************************************************************
	/**
	 * @param event 
	 */
	public void handleEvent( ProgressEvent event ) {
		if( event instanceof VisualizationEvent ) {
			if( loop )
				control.resetTime();
			else
				// TODO restartvisualization
//				this.restartVisualization = true;
//				btnPlay.setIcon( playIcon );
//				visualizationView.getGLContainer().stopAnimation();
//				btnPlay.setSelected( false );
				ZETMain.sendMessage( "Replaying visualization finished." );
			return;
		}
//		if( stepByStep ) {
//			if( event.getProcessMessage().progress >= 0 )
//				progressBar.setValue( event.getProcessMessage().progress );
//			lblTaskMain.setText( event.getProcessMessage().taskName );
//			lblTaskInfo.setText( event.getProcessMessage().taskProgressInformation );
//			//JEditorPanel.instance.getRasterizedFloor().displayFloor( myProject.getBuildingPlan().getFloors().get(1), ZToCAConverter.getInstance().getLatestMapping(), ZToCAConverter.getInstance().getLatestContainer() );
		//updateFloorView();
		//caView.updateFloorView();
		if( currentMode == CA_FLOOR ) {
			Floor floor = editView.getCurrentFloor();
			caView.getLeftPanel().getMainComponent().displayFloor( floor, ZToCAConverter.getInstance().getLatestMapping(), ZToCAConverter.getInstance().getLatestContainer() );
		}
		//editView.updateFloorView();
//		} else {
//			if( event.getProcessMessage().progress >= 0 )
//				progressBar.setValue( event.getProcessMessage().progress );
		ZETMain.sendMessage( event.getProcessMessage().taskName );
//			lblTaskInfo.setText( event.getProcessMessage().taskProgressInformation );
//		}
	}

	public void setControl( GLControl control ) {
		this.control = control;
	}

	/**
	 * Sets a new project controller. For reasons of consistency the project
	 * currently controlled by the control class is loaded. It should not happen,
	 * that this project is <code>null</code>
	 * @param zcontrol the zet model control class
	 */
	public void setZControl2( ZControl zcontrol ) {
		this.zcontrol = zcontrol;
		loadProject();
	}

	/**
	 * Returns the control class controlling the currently visible project.
	 * @return the control class controlling the currently visible project
	 */
//	public final ZControl getZControl() {
//		return zcontrol;
//	}
}
