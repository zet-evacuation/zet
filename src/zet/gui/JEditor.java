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
 * Created 04.12.2007, 17:08
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
import zet.gui.components.JEventStatusBar;
import zet.gui.components.tabs.editor.EditMode;
import zet.gui.components.tabs.JQuickVisualizationView;
import zet.gui.components.tabs.JEditView;
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
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
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
import de.tu_berlin.math.coga.common.debug.Debug;
import ds.z.ZControl;
import event.VisualizationEvent;
import de.tu_berlin.math.coga.components.JLogPane;
import gui.statistic.JStatisticsPanel;
import zet.gui.components.toolbar.JEditToolbar;
import gui.Control;
import gui.ZETMain;
import java.util.ArrayList;
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

	public enum ZETWindowTabs {
		EditFloor( true, "Edit" ),
		QuickView( ZETMain.isDebug(), "CAView" ),
		Batch( true, "Batch" ),
		Visualization( true, "Visualization" ),
		CellularAutomatonStatistic( true, "Statistic" ),
		GraphStatistic( true, "GraphStatistic" ),
		Log( true, "LogWindow" ),
		Statistic( true, "Statistics" );
		private boolean visible;
		private String name;

		ZETWindowTabs( boolean visible, String name ) {
			this.visible = visible;
			this.name = name;
		}

		public boolean isVisible() {
			return visible;
		}

		public String getName() {
			return name;
		}
	}
	ArrayList<ZETWindowTabs> tabs = new ArrayList<ZETWindowTabs>( ZETWindowTabs.values().length );
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
	private JQuickVisualizationView caView;
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
	private ZETWindowTabs currentMode = ZETWindowTabs.EditFloor;
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
	public JEditor( Control guiControl, ZControl zcontrol ) {
		super();
		this.guiControl = guiControl;
		this.zcontrol = zcontrol;

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
	 * Adds the main views to the window. These views are all included in
	 * tabs.
	 */
	public void addMainComponents() {
		editView = new JEditView( guiControl );
		caView = new JQuickVisualizationView();
		batchView = new JBatchView( guiControl );
		visualizationView = new JVisualizationView( new GLCapabilities(), guiControl );
		caStatisticView = new JStatisticPanel();
		graphStatisticView = new JGraphStatisticPanel();
		logView = new JLogPane( ZETMain.log );
		statisticView = new JStatisticsPanel();

		tabPane = new JTabbedPane();

		loc.setPrefix( "gui.editor.JEditor.tab" );		
		for( ZETWindowTabs tab : ZETWindowTabs.values() ) {
			if( tab.isVisible() ) {
				tabPane.addTab( loc.getString( tab.getName() ), null, getComponent( tab ), loc.getString( tab.getName() + "ToolTip" ) );
				tabs.add( tab );
			}
		}
		tabPane.addChangeListener( chlTab );
		loc.setPrefix( "" );

		getContentPane().add( tabPane, BorderLayout.CENTER );

		tabPane.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				if( tabs.get( tabPane.getSelectedIndex() ) == ZETWindowTabs.Log )
					logView.update();
			}
		} );

		ZETMain.sendMessage( loc.getString( "gui.status.EditorInitialized" ) );
	}

	private JComponent getComponent( ZETWindowTabs tab ) {
		switch( tab ) {
			case Batch:
				return batchView;
			case CellularAutomatonStatistic:
				return caStatisticView;
			case EditFloor:
				return editView;
			case Visualization:
				return visualizationView;
			case GraphStatistic:
				return graphStatisticView;
			case Log:
				return logView;
			case QuickView:
				return caView;
			case Statistic:
				return statisticView;
		}
		throw new IllegalArgumentException( "Unknown tab component: " + tab.getName() + "!" );
	}

	/**
	 * Sets up shortcuts for several actions.
	 */
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

		// Localize tabs
		loc.setPrefix( "gui.editor.JEditor.tab" );

		for( int i = 0; i < tabs.size(); ++i ) {
			tabPane.setTitleAt( i, loc.getString( tabs.get( i ).getName() ) );
			tabPane.setToolTipTextAt( i, loc.getString( tabs.get( i ).getName() + "ToolTip" ) );
		}
		loc.setPrefix( "" );

		sendMouse( lastMouse );
		ZETMain.sendError( "" );
		ZETMain.sendMessage( loc.getStringWithoutPrefix( "gui.status.LanguageChangedTo" ) );
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
		editView.update();

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
				if( tabs.get( tabPane.getSelectedIndex() ) != ZETWindowTabs.QuickView )
					tabPane.setSelectedIndex( tabs.indexOf( ZETWindowTabs.QuickView ) );
			} else if( e.getActionCommand().equals( "visualization" ) ) {
				quickVisualization();



				if( tabs.get( tabPane.getSelectedIndex() ) != ZETWindowTabs.QuickView )
					tabPane.setSelectedIndex( tabs.indexOf( ZETWindowTabs.QuickView ) );
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
				tabPane.setSelectedIndex( tabs.indexOf( ZETWindowTabs.Visualization ) );
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
				tabPane.setSelectedIndex( tabs.indexOf( ZETWindowTabs.Visualization ) );
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
				tabPane.setSelectedIndex( tabs.indexOf( ZETWindowTabs.Visualization ) );
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
				tabPane.setSelectedIndex( tabs.indexOf( ZETWindowTabs.Visualization ) );
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
				tabPane.setSelectedIndex( tabs.indexOf( ZETWindowTabs.Visualization ) );
			} catch( Exception ex ) {
				ZETMain.sendError( ex.getLocalizedMessage() );
			}
		}
	};
	ChangeListener chlTab = new ChangeListener() {

		public void stateChanged( ChangeEvent e ) {
			final int i = tabPane.getSelectedIndex();
			switch( tabs.get( i ) ) {
				case EditFloor:
					switchTo( i );
					editView.updateFloorView();
					break;
				case Batch:
				if( firstSwitch ) {
					batchView.addProject( zcontrol.getProject() );
					firstSwitch = false;
				}
				switchTo( i );
					break;
				default:
					switchTo( i );
			}
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
				Debug.printException( ex );
			}
		} catch( ZToCAConverter.ConversionNotSupportedException ex ) {
			ZETMain.sendError( ex.getLocalizedMessage() );
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
				Debug.printException( ex );
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

	public JQuickVisualizationView getQuickVisualizationView() {
		return caView;
	}

	/**
	 * Shows a <code>JToolBar</code> and hides all others.
	 * @param toolBar the tool bar that is shown
	 */
	private void showToolBar( JToolBar newToolbar ) {
		if( currentToolbar != null )
			getContentPane().remove( currentToolbar );
		if( newToolbar != null )
			getContentPane().add( newToolbar, BorderLayout.NORTH );
		currentToolbar = newToolbar;
	}

	/**
	 * Sets the visible tab. Enables and disables all necessary menu and
	 * tool bar elements.
	 * @param tabID the tab id as specified in {@link JEditor}.
	 */
	private void switchTo( int tabIndex ) {
		ZETWindowTabs tab = tabs.get( tabIndex );
//		if( tab == ZETWindowTabs.QuickView && worker == null ) {
//			ZETMain.sendError( loc.getStringWithoutPrefix( "gui.error.StartSimulation" ) );
//			tabPane.setSelectedIndex( tabIndex );
//			return;
//		}
		// TODO better implementation of this stuff for debug mode ?
		//if( ((ZETMain.isDebug() && tabID > CA_FLOOR) || (!ZETMain.isDebug() && tabID > BATCH)) && result == null && tabID != LOG && tabID != STATISTICS ) {
		//	ZETMain.sendError( loc.getStringWithoutPrefix( "gui.error.CreateBatch" ) );
		//	tabPane.setSelectedIndex( tabIndex );
		//	return;
		//}

		currentMode = tab;
		// code using the switch-bar is disabled!
		guiControl.visualizationPause();
		switch( tab ) {
			case EditFloor:
				showToolBar( toolBarEdit );
				break;
			case Batch:
				showToolBar( toolBarBatch );
				break;
			case QuickView:
				showToolBar( toolBarCellularAutomatonQuickVisualization );
				break;
			case Visualization:
				showToolBar( toolBarVisualization );
				visualizationView.requestFocus();
				break;
			case CellularAutomatonStatistic:
				showToolBar( toolBarCAStats );
				break;
			case GraphStatistic:
				showToolBar( toolBarGraphStats );
				break;
			case Log:
				showToolBar( toolBarLog );
				break;
			case Statistic:
				showToolBar( null );
		}
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
		if( currentMode == ZETWindowTabs.QuickView ) {
			Floor floor = editView.getCurrentFloor();
			caView.getLeftPanel().getMainComponent().displayFloor( floor );
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
}
