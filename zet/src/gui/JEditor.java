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
package gui;

import algo.ca.CellularAutomatonInOrderExecution;
import batch.Batch;
import batch.BatchResult;
import batch.BatchResultEntry;
import batch.CellularAutomatonAlgorithm;
import batch.GraphAlgorithm;
import batch.load.BatchProjectEntry;
import control.ProjectControl;
import converter.ZToCAConverter;
import ds.Project;
import ds.PropertyContainer;
import ds.ca.CellularAutomaton;
import ds.GraphVisualizationResult;
import ds.z.AssignmentType;
import ds.z.Floor;
import ds.z.exception.TooManyPeopleException;
import event.EventListener;
import event.EventServer;
import event.MessageEvent;
import event.MessageEvent.MessageType;
import event.ProgressEvent;
import gui.batch.JBatchView;
import gui.components.ComboBoxRenderer;
import gui.components.JEventStatusBar;
import gui.components.framework.Button;
import gui.components.framework.IconSet;
import gui.components.framework.Menu;
import gui.editor.CoordinateTools;
import gui.editor.EditMode;
import gui.editor.GUIOptionManager;
import gui.ca.JCAView;
import gui.components.AbstractFloor.RasterPaintStyle;
import gui.components.NamedIndex;
import gui.components.progress.JProgressBarDialog;
import gui.components.progress.JRasterizeProgressBarDialog;
import gui.editor.AreaVisibility;
import gui.editor.JEditView;
import gui.editor.assignment.JAssignment;
import gui.editor.flooredit.FloorImportDialog;
import gui.editor.planimage.JPlanImageProperties;
import gui.editor.properties.JOptionsWindow;
import gui.editor.properties.JPropertySelectorWindow;
import gui.statistic.JGraphStatisticPanel;
import gui.statistic.JStatisticPanel;
import gui.visualization.AbstractVisualization;
import gui.visualization.JVisualizationView;
import gui.visualization.control.GLControl;
import gui.visualization.control.GLControl.CellInformationDisplay;
import io.DXFWriter;
import de.tu_berlin.math.coga.common.util.IOTools;
import io.movie.MovieManager;
import io.visualization.CAVisualizationResults;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.media.opengl.GLCapabilities;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;
import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.common.localization.Localized;
import statistic.ca.CAStatistic;
import statistic.graph.Controller;
import batch.tasks.AlgorithmTask;
import batch.tasks.CARealTime;
import batch.tasks.RasterizeTask;
import batch.tasks.VisualizationDataStructureTask;
import ds.z.AssignmentArea;
import ds.z.EvacuationArea;
import ds.z.Room;
import ds.z.ZControl;
import gui.editor.JLogView;
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
	/** The localization class. */
	static final Localization loc = Localization.getInstance();
	/** Stores the last mouse position if a mouse position event is sent. */
	private static Point lastMouse = new Point( 0, 0 );
	/** The delimter used if numbers are stored in a tuple. */
	final static String delimiter = Localization.getInstance().getStringWithoutPrefix( "numberSeparator" );
	/** Singleton instance variable. */
	private static final JEditor instance = new JEditor();

	/** Control class for projects and editing */
	private ProjectControl projectControl;

	private static boolean editing = false;
	private ZControl zcontrol;	// Task and execution stuff
	private CellularAutomatonInOrderExecution caAlgo = null;
	private AlgorithmTask worker;
	private BatchResult result;
	// Options
	private boolean createCopy;
	private boolean firstSwitch = false;
	private EditMode.Type creationType = EditMode.Type.CREATION_POINTWISE;
	/** The number format used to display the zoomfactor in the textfield. */
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
	private JLogView logView;
	private JMenu mFile;
	private JMenuItem mnuFileNew;
	private JMenuItem mnuFileOpen;
	private JMenuItem mnuFileSave;
	private JMenuItem mnuFileSaveAs;
	private JMenuItem mnuFileExportAsDXF;
	private JMenuItem mnuFileSaveResultAs;
	private JMenuItem mnuFileLoadResult;
	private JMenuItem mnuFileExit;
	private JMenu mEdit;
	private JMenuItem mnuEditUndo;
	private JMenuItem mnuEditGoTo;
	private JMenuItem mnuEditFloorNew;
	private JMenuItem mnuEditFloorUp;
	private JMenuItem mnuEditFloorDown;
	private JMenuItem mnuEditFloorDelete;
	private JMenuItem mnuEditFloorImport;
	private JMenuItem mnuEditFloorCopy;
	private JMenuItem mnuEditRasterize;
	private JMenuItem mnuEditDistributeEvacuees;
	private JMenuItem mnuEditDistribution;
	private JMenuItem mnuEditProperties;
	private JMenuItem mnuScreenshot;
	private JMenu mView;
	private JMenu mVisibleAreas;
	private JMenuItem mnuShowAllAreas;
	private JMenuItem mnuHideAllAreas;
	private JCheckBoxMenuItem mnuDelayArea;
	private JCheckBoxMenuItem mnuStairArea;
	private JCheckBoxMenuItem mnuEvacuationArea;
	private JCheckBoxMenuItem mnuInaccessibleArea;
	private JCheckBoxMenuItem mnuSaveArea;
	private JCheckBoxMenuItem mnuAssignmentArea;
	private JMenu mGrid;
	private JRadioButtonMenuItem mnuGridLines;
	private JRadioButtonMenuItem mnuGridPoints;
	private JRadioButtonMenuItem mnuGridNotVisible;
	private JCheckBoxMenuItem mnuPaintRasterized;
	private JCheckBoxMenuItem mnuHideDefaultFloor;
	private JMenu mExecute;
	private JMenu mSimulation;
	private JMenuItem mnuCreateCA;
	private JMenuItem mnuApplyAssignment;
	private JMenuItem mnuPauseSimulation;
	private JMenuItem mnuStepByStepSimulation;
	private JMenuItem mnuStartSimulation;
	private JMenuItem mnuQuickVisualization;
	private JMenu mOptimization;
	private JMenuItem mnuCreateGraph;
	private JMenuItem mnuQuickestTransshipment;
	private JMenuItem mnuMaxFlowOverTimeMC;
	private JMenuItem mnuMaxFlowOverTimeTEN;
	private JMenuItem mnuEarliestArrivalTransshipment;
	private JMenu mExtras;
	private JMenu mLanguage;
	private JRadioButtonMenuItem[] mnuLanguages = new JRadioButtonMenuItem[2];
	private JMenu mPlanImage;
	private JMenuItem mnuPlanImageLoad;
	private JMenuItem mnuPlanImageHide;
	private JMenuItem mnuPlanImageResize;
	private JMenuItem mnuPlanImageLocate;
	private JMenuItem mnuPlanImageTransparency;
	private JMenuItem mnuOptions;
	private JMenuItem mnuSettings;
	private JMenu mnuDebug;
	private JMenuItem mnuOutputInformation;
	private JMenu mWindow;
	private JMenu mHelp;
	private JMenuItem mnuHelpAbout;
	/** The tool bars */
	private JToolBar toolBarEdit;
	private JToolBar toolBarBatch;
	private JToolBar toolBarCA;
	private JToolBar toolBarVisualization;
	private JToolBar toolBarCAStats;
	private JToolBar toolBarGraphStats;
	/** The tool bar that is visible if the log view is active. */
	private JToolBar toolBarLog;
	private JToolBar currentToolbar;
	// Toolbar items
	// Edit toolbar
	private JButton btnExit1;
	private JButton btnOpen;
	private JButton btnSave;
	private JButton btnEditSelect;
	private JButton btnEditPointwise;
	private JButton btnEditRectangled;
	private JLabel lblAreaType;
	private JComboBox cbxEdit;
	private JButton btnZoomIn;
	private JButton btnZoomOut;
	private JTextField txtZoomFactor;
	private JButton btnRasterize;
	// Batch toolbar
	private JButton btnExit5;
	private JButton btnSaveResults1;
	private JButton btnOpenResults1;
	// Quick visualization toolbar
	private JButton btnExit6;
	// Visualization toolbar
	private JButton btnExit2;
	private JLabel labelBatchName1;
	private BatchResultEntryVisComboBoxModel entryModelVis;
	private JLabel labelBatchRun;
	private CycleComboBoxModel cycleModel;
	/** Allows switching between 3d (perspective) view and 2d (orthogonal/isometric) view. */
	private JButton btn2d3dSwitch;
	/** Allows switching orthogonal and isometric 2-dimensional view. */
	private JButton btn2dSwitch;
	private JButton btnVideo;
	private JButton btnPlayStart;
	private JButton btnPlay;
	private Icon playIcon;
	private Icon pauseIcon;
	private JButton btnPlayEnd;
	private JButton btnShowWalls;
	private JButton btnShowGraph;
	private JButton btnShowGraphGrid;
	private JButton btnShowCellularAutomaton;
	private JButton btnShowAllFloors;
	private JButton btnShowPotential;
	private JButton btnShowDynamicPotential;
	private JButton btnShowUtilization;
	private JButton btnShowWaiting;
	// Statistic Toolbar
	private JButton btnOpenResults2;
	private JButton btnExit3;
	private JButton btnExit7;
	private JButton btnSaveResults2;
	// Graph statistic Toolbar
	private JButton btnExit4;
	private JButton btnOpenResults3;
	private JButton btnSaveResults3;
	private JLabel labelBatchName2;
	private BatchResultEntryGRSComboBoxModel entryModelGraph;

	/** A tabbed pane that allows switching of the different views. */
	private JTabbedPane tabPane;
	// Additional GUI stuff
	/** Model for the edit-mode combo box. */
	private EditComboBoxModel editSelector;
	private boolean disableUpdate = false;
	private int currentMode = EDIT_FLOOR;
	private JAssignment distribution;

	/**
	 * Creates a new instance of <code>JEditor</code>.
	 * @param p the currentProject to display
	 * @param title the window title
	 * @param width the width of the window
	 * @param height the height of the window
	 */
	private JEditor() {
		super();

		loc.setLocale( Locale.getDefault() );

		nfZoom.setMaximumFractionDigits( 2 );

		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setSize( 800, 600 );
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation( (d.width - getSize().width) / 2, (d.height - getSize().height) / 2 );
		createMenuBar();

		getContentPane().setLayout( new BorderLayout() );

		initializeOptions();

		statusBar = new JEventStatusBar();
		add( statusBar, BorderLayout.SOUTH );

		createEditToolBar();
		createBatchToolBar();
		createQuickVisualizationToolBar();
		createVisualizationToolBar();
		createStatisticsToolBar();
		createLogToolBar();

		add( toolBarEdit, BorderLayout.NORTH );
		currentToolbar = toolBarEdit;

		EventServer.getInstance().registerListener( this, ProgressEvent.class );

		this.addWindowListener( new WindowListener() {
			public void windowOpened( WindowEvent e ) {
			}

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

			public void windowClosed( WindowEvent e ) {
			}

			public void windowIconified( WindowEvent e ) {
			}

			public void windowDeiconified( WindowEvent e ) {
			}

			public void windowActivated( WindowEvent e ) {
			}

			public void windowDeactivated( WindowEvent e ) {
			}
		} );
	}

	/**
	 * Sets a new project controller. For reasons of consistency the project
	 * currently controlled by the control class is loaded. It should not happen,
	 * that this project is <code>null</code>
	 * @param projectControl the projects control class
	 */
	public void setProjectControl( ProjectControl projectControl ) {
		this.projectControl = projectControl;
		loadProject();
	}

	/**
	 * Returns the control class controlling the currently visible project.
	 * @return the control class controlling the currently visible project
	 */
	public ProjectControl getProjectControl() {
		return projectControl;
	}

	/**
	 * Loads the project currently controlled by the project controller. Resets
	 * the view to edit window and resets the zoom factor to 10%
	 */
	private void loadProject() {
		zcontrol = projectControl.getZControl();

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
		setZoomFactor( 0.04d );

		// Set up the last camera position
		visualizationView.getGLContainer().getCamera().setPos( projectControl.getZControl().getProject().getVisualProperties().getCameraPosition().pos );
		visualizationView.getGLContainer().getCamera().setView( projectControl.getZControl().getProject().getVisualProperties().getCameraPosition().view );
		visualizationView.getGLContainer().getCamera().setUp( projectControl.getZControl().getProject().getVisualProperties().getCameraPosition().up );
		visualizationView.getGLContainer().setTexts( projectControl.getZControl().getProject().getVisualProperties().getTextureFontStrings() );
		visualizationView.getGLContainer().setView( projectControl.getZControl().getProject().getVisualProperties().getCurrentWidth(), projectControl.getZControl().getProject().getVisualProperties().getCurrentHeight() );
		visualizationView.updateCameraInformation();
	}

	public static JEditor getInstance() {
		return instance;
	}

	private void initializeOptions() {
		PropertyContainer pc = PropertyContainer.getInstance();
		createCopy = pc.getAsBoolean( "options.filehandling.createBackup" );
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
		EventServer.getInstance().dispatchEvent( new MessageEvent<JEditor>( getInstance(), MessageType.MousePosition, text ) );
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

	/**
	 * Displays a box displaying an error message.
	 * @param title the title of the message box
	 * @param message the message
	 */
	public static void showErrorMessage( String title, String message ) {
		JOptionPane.showMessageDialog( getInstance(), message, title, JOptionPane.ERROR_MESSAGE );
	}

	/*****************************************************************************
	 *                                                                           *
	 * GUI initialization                                                        *
	 *                                                                           *
	 ****************************************************************************/
	public void addMainComponents() {
		editView = new JEditView( );
		batchView = new JBatchView();
		visualizationView = new JVisualizationView( new GLCapabilities() );
		visualizationView.setFloorSelectorEnabled( !PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.floors" ) );
		if( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.isometric" ) ) {
			visualizationView.getGLContainer().setParallelViewMode( AbstractVisualization.ParallelViewMode.Isometric );
		} else {
			visualizationView.getGLContainer().setParallelViewMode( AbstractVisualization.ParallelViewMode.Orthogonal );
		}
		visualizationView.getGLContainer().setView( !PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.2d" ) );
					
		visualizationView.addPotentialItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e ) {
				if( e.getItem() == null || e.getStateChange() == ItemEvent.DESELECTED )
					return;
				btnShowPotential.setSelected( false );
				btnShowDynamicPotential.setSelected( false );
				btnShowUtilization.setSelected( false );
				btnShowWaiting.setSelected( false );
			}
		} );
		caView = new JCAView();
		caStatisticView = new JStatisticPanel();
		graphStatisticView = new JGraphStatisticPanel();
		logView = new JLogView( ZETMain.log );

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
		tabPane.addChangeListener( chlTab );
		loc.setPrefix( "" );

		getContentPane().add( tabPane, BorderLayout.CENTER );

		// Register Shortcuts (no-menu-shortcuts)
		KeyStroke up = KeyStroke.getKeyStroke( KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK);
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

		tabPane.registerKeyboardAction( acl, "test", up, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		ZETMain.sendMessage( loc.getString( "gui.status.EditorInitialized" ) );
	}

	/**
	 * Creates the menu.
	 */
	private void createMenuBar() {
		loc.setPrefix( "gui.editor.JEditor." );

		JMenuBar bar = new JMenuBar();
		mFile = Menu.addMenu( bar, loc.getString( "menuFile" ) );
		mEdit = Menu.addMenu( bar, loc.getString( "menuEdit" ) );
		mView = Menu.addMenu( bar, loc.getString( "menuView" ) );
		if( ZETMain.isDebug() )
			mExecute = Menu.addMenu( bar, loc.getString( "menuExecute" ) );
		mExtras = Menu.addMenu( bar, loc.getString( "menuExtras" ) );
		mHelp = Menu.addMenu( bar, loc.getString( "menuHelp" ) );

		// Dateimenue
		mnuFileNew = Menu.addMenuItem( mFile, loc.getString( "menuNew" ), 'N', aclFile, "newProject" );
		Menu.addMenuItem( mFile, "-" );
		mnuFileOpen = Menu.addMenuItem( mFile, loc.getString( "menuOpen" ), 'O', aclFile, "loadProject" );
		mnuFileSave = Menu.addMenuItem( mFile, loc.getString( "menuSave" ), 'S', aclFile, "saveProject" );
		mnuFileSaveAs = Menu.addMenuItem( mFile, loc.getString( "menuSaveAs" ), 'U', aclFile, "saveProjectAs" );
		Menu.addMenuItem( mFile, "-" );
		mnuFileExportAsDXF = Menu.addMenuItem( mFile, loc.getString( "menuDXF" ), aclFile, "saveAsDXF" );
		Menu.addMenuItem( mFile, "-" );
		mnuFileSaveResultAs = Menu.addMenuItem( mFile, loc.getString( "menuSaveResultAs" ), 'E', aclFile, "saveResultAs" );
		mnuFileSaveResultAs.setEnabled( false );
		mnuFileLoadResult = Menu.addMenuItem( mFile, loc.getString( "menuLoadBatchResult" ), 'B', aclFile, "loadBatchResult" );
		Menu.addMenuItem( mFile, "-" );
		mnuFileExit = Menu.addMenuItem( mFile, loc.getString( "menuExit" ), 'X' );
		mnuFileExit.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				System.exit( 0 );
			}
		} );

		// Bearbeiten menue
		mnuEditFloorNew = Menu.addMenuItem( mEdit, loc.getString( "menuFloorNew" ), aclFloor, "new" );
		mnuEditFloorUp = Menu.addMenuItem( mEdit, loc.getString( "menuFloorUp" ), aclFloor, "up" );
		mnuEditFloorDown = Menu.addMenuItem( mEdit, loc.getString( "menuFloorDown" ), aclFloor, "down" );
		mnuEditFloorDelete = Menu.addMenuItem( mEdit, loc.getString( "menuFloorDelete" ), aclFloor, "delete" );
		mnuEditFloorCopy = Menu.addMenuItem( mEdit, loc.getString( "menuFloorCopy" ), aclFloor, "copy" );
		mnuEditFloorImport = Menu.addMenuItem( mEdit, loc.getString( "menuFloorImport" ), aclFloor, "import" );
		Menu.addMenuItem( mEdit, "-" );
		mnuEditRasterize = Menu.addMenuItem( mEdit, loc.getString( "menuRasterize" ), 'R', InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK, aclStart, "rasterize" );
		Menu.addMenuItem( mEdit, "-" );
		mnuEditDistributeEvacuees = Menu.addMenuItem( mEdit, loc.getString( "menuDistributeEvacuees" ), aclStart, "distributeEvacuees" );
		Menu.addMenuItem( mEdit, "-" );
		mnuEditDistribution = Menu.addMenuItem( mEdit, loc.getString( "menuDistributions" ), 'V', aclDistribution );
		mnuEditProperties = Menu.addMenuItem( mEdit, loc.getString( "menuProperties" ), 'P', aclProperties, "properties" );

		// Anzeige-menue
		mVisibleAreas = Menu.addMenu( mView, loc.getString( "menuVisibleAreas" ) );
		mnuShowAllAreas = Menu.addMenuItem( mVisibleAreas, loc.getString( "menuShowAllAreas" ), aclAreaVisibility, "showAll" );
		mnuShowAllAreas.setEnabled( false );
		mnuHideAllAreas = Menu.addMenuItem( mVisibleAreas, loc.getString( "menuHideAllAreas" ), aclAreaVisibility, "hideAll" );
		mVisibleAreas.addSeparator();
		mnuDelayArea = Menu.addCheckMenuItem( mVisibleAreas, loc.getString( "menuShowDelayAreas" ), true, aclAreaVisibility );
		mnuStairArea = Menu.addCheckMenuItem( mVisibleAreas, loc.getString( "menuShowStairAreas" ), true, aclAreaVisibility );
		mnuEvacuationArea = Menu.addCheckMenuItem( mVisibleAreas, loc.getString( "menuShowEvacuationAreas" ), true, aclAreaVisibility );
		mnuInaccessibleArea = Menu.addCheckMenuItem( mVisibleAreas, loc.getString( "menuShowInaccessibleAreas" ), true, aclAreaVisibility );
		mnuSaveArea = Menu.addCheckMenuItem( mVisibleAreas, loc.getString( "menuShowSaveAreas" ), true, aclAreaVisibility );
		mnuAssignmentArea = Menu.addCheckMenuItem( mVisibleAreas, loc.getString( "menuShowAssignmentAreas" ), true, aclAreaVisibility );
		Menu.addMenuItem( mView, "-" );
		mGrid = Menu.addMenu( mView, loc.getString( "menuGridstyle" ) );
		mnuGridLines = Menu.addRadioButtonMenuItem( mGrid, loc.getString( "menuGridstyleLines" ), false, aclPaint, "gridLine" );
		mnuGridPoints = Menu.addRadioButtonMenuItem( mGrid, loc.getString( "menuGridstylePoints" ), true, aclPaint, "gridPoint" );
		mnuGridNotVisible = Menu.addRadioButtonMenuItem( mGrid, loc.getString( "menuGridstyleNone" ), false, aclPaint, "gridNo" );
		mnuPaintRasterized = Menu.addCheckMenuItem( mView, loc.getString( "menuDrawOnGrid" ), true, aclPaint, "grid" );
		mnuHideDefaultFloor = Menu.addCheckMenuItem( mView, loc.getString( "menuHideDefaultEvacuationFloor" ), PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" ), aclPaint, "defaultFloor" );
		mView.addSeparator();
		mnuScreenshot = Menu.addMenuItem( mView, loc.getString( "menuScreenshot" ), KeyEvent.VK_F12, aclScreenshot, "screenshot", 0 );

		// Ausfuehren menu
		if( ZETMain.isDebug() ) {
			mSimulation = Menu.addMenu( mExecute, loc.getString( "menuSimulation" ) );
			mnuStartSimulation = Menu.addMenuItem( mSimulation, loc.getString( "menuSimulationStart" ), aclExecute, "startSimulation" );
			mnuStepByStepSimulation = Menu.addMenuItem( mSimulation, loc.getString( "menuSimulationStepByStep" ), KeyEvent.VK_F8, aclExecute, "stepByStepSimulation", 0 );
			mnuQuickVisualization = Menu.addMenuItem( mSimulation, loc.getString( "menuSimulationQuickVisualization" ), KeyEvent.VK_F5, aclExecute, "visualization", 0 );
			mOptimization = Menu.addMenu( mExecute, loc.getString( "menuOptimization" ) );
			mnuQuickestTransshipment = Menu.addMenuItem( mOptimization, loc.getString( "menuOptAlgoQuickestTransshipment" ), aclExecute, "QT" );
			mnuMaxFlowOverTimeMC = Menu.addMenuItem( mOptimization, loc.getString( "menuOptAlgoMaxFlowOverTimeMinCost" ), aclExecute, "MFOTMC" );
			mnuMaxFlowOverTimeTEN = Menu.addMenuItem( mOptimization, loc.getString( "menuOptAlgoMaxFlowOverTimeTEN" ), aclExecute, "MFOTTEN" );
			mnuEarliestArrivalTransshipment = Menu.addMenuItem( mOptimization, loc.getString( "menuOptAlgoEATransshipment" ), aclExecute, "EAT" );
		}
		// Extras-Menu
		mLanguage = Menu.addMenu( mExtras, loc.getString( "menuLanguages" ) );
		ButtonGroup grpLanguage = new ButtonGroup();
		JRadioButtonMenuItem mnuGerman = Menu.addRadioButtonMenuItem( mLanguage, "_Deutsch", true, aclLanguage, "german" );
		grpLanguage.add( mnuGerman );
		JRadioButtonMenuItem mnuEnglish = Menu.addRadioButtonMenuItem( mLanguage, "_english", false, aclLanguage, "english" );
		grpLanguage.add( mnuEnglish );
		mnuLanguages[0] = mnuGerman;
		mnuLanguages[1] = mnuEnglish;
		Menu.addMenuItem( mExtras, "-" );
		mPlanImage = Menu.addMenu( mExtras, loc.getString( "menuPlanDisplaying" ) );
		mnuPlanImageLoad = Menu.addMenuItem( mPlanImage, loc.getString( "menuLoadPlan" ), aclPlanImage, "load" );
		mnuPlanImageHide = Menu.addMenuItem( mPlanImage, loc.getString( "menuHidePlan" ), aclPlanImage, "hide" );
		mnuPlanImageHide.setEnabled( false );
		mnuPlanImageResize = Menu.addMenuItem( mPlanImage, loc.getString( "menuResizePlan" ), aclPlanImage, "resize" );
		mnuPlanImageResize.setEnabled( false );
		mnuPlanImageLocate = Menu.addMenuItem( mPlanImage, loc.getString( "menuMovePlan" ), aclPlanImage, "move" );
		mnuPlanImageLocate.setEnabled( false );
		mnuPlanImageTransparency = Menu.addMenuItem( mPlanImage, loc.getString( "menuSetPlanTransparency" ), aclPlanImage, "transparency" );
		mnuPlanImageTransparency.setEnabled( false );
		Menu.addMenuItem( mExtras, "-" );
		mnuOptions = Menu.addMenuItem( mExtras, loc.getString( "menuOptions" ), 'T', aclProperties, "options" );
		if( ZETMain.isDebug() ) {
			mnuSettings = Menu.addMenuItem( mExtras, loc.getString( "menuSettings" ), aclProperties, "settings" );
			mnuDebug = Menu.addMenu( mExtras, "Debug" );
			mnuOutputInformation = Menu.addMenuItem( mnuDebug, "Ausgabe", aclDebug, "outputInformation" );
		}
		// Hilfe-menu
		mnuHelpAbout = Menu.addMenuItem( mHelp, loc.getString( "menuAbout" ), 'I', aclAbout );

		setJMenuBar( bar );

		loc.setPrefix( "" );
	}

	/**
	 * Creates the <code>JToolBar</code> for the edit mode.
	 */
	private void createEditToolBar() {
		loc.setPrefix( "gui.editor.JEditor." );

		toolBarEdit = new JToolBar();
		btnExit1 = Button.newButton( IconSet.Exit, new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				System.exit( 0 );
			}
		}, "", loc.getString( "toolbarTooltipExit" ) );
		toolBarEdit.add( btnExit1 );
		toolBarEdit.addSeparator();

		btnOpen = Button.newButton( IconSet.Open, aclFile, "loadProject", loc.getString( "toolbarTooltipOpen" ) );
		toolBarEdit.add( btnOpen );
		btnSave = Button.newButton( IconSet.Save, aclFile, "saveProject", loc.getString( "toolbarTooltipSave" ) );
		toolBarEdit.add( btnSave );
		toolBarEdit.addSeparator();

		btnEditSelect = Button.newButton( IconSet.EditSelect, new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				btnEditSelect.setSelected( true );
				btnEditPointwise.setSelected( false );
				btnEditRectangled.setSelected( false );
				editView.setEditMode( EditMode.Selection );
				sendReady();
			}
		}, "", loc.getString( "toolbarTooltipSelectionMode" ) );
		toolBarEdit.add( btnEditSelect );
		btnEditSelect.setSelected( true );
		btnEditPointwise = Button.newButton( IconSet.EditDrawPointwise, new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				btnEditSelect.setSelected( false );
				btnEditPointwise.setSelected( true );
				btnEditRectangled.setSelected( false );
				creationType = EditMode.Type.CREATION_POINTWISE;
				editSelector.rebuild();
				editView.setEditMode( (EditMode)editSelector.getSelectedItem() );
				ZETMain.sendMessage( "Wählen sie die Koordinaten." ); // TODO loc
			}
		}, "", loc.getString( "toolbarTooltipPointSequence" ) );
		toolBarEdit.add( btnEditPointwise );
		btnEditRectangled = Button.newButton( IconSet.EditDrawRectangled, new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				btnEditSelect.setSelected( false );
				btnEditPointwise.setSelected( false );
				btnEditRectangled.setSelected( true );
				creationType = EditMode.Type.CREATION_RECTANGLED;
				editSelector.rebuild();
				ZETMain.sendMessage( "Wählen sie die Koordinaten." ); // TODO loc
			}
		}, "", loc.getString( "toolbarTooltipDragCreate" ) );
		toolBarEdit.add( btnEditRectangled );

		toolBarEdit.add( new JLabel( " " ) ); //Spacer
		lblAreaType = new JLabel( loc.getString( "labelAreaType" ) );
		toolBarEdit.add( lblAreaType );
		editSelector = new EditComboBoxModel();
		cbxEdit = new JComboBox();
		cbxEdit.setMaximumRowCount( 25 );
		cbxEdit.setMaximumSize( new Dimension( 250, cbxEdit.getPreferredSize().height ) );
		cbxEdit.setPreferredSize( new Dimension( 250, cbxEdit.getPreferredSize().height ) );
		cbxEdit.setAlignmentX( 0 );
		loc.setPrefix( "gui.editor.JEditor." );
		cbxEdit.setToolTipText( loc.getString( "toolbarTooltipAreaType" ) );
		cbxEdit.setModel( editSelector );
		cbxEdit.setRenderer( new EditComboBoxRenderer() );
		// Don't use an item/change listener here, because then we can't capture the event
		// that the user re-selects the same entry as before
		cbxEdit.addPopupMenuListener( pmlEditMode );
		toolBarEdit.add( cbxEdit );
		toolBarEdit.addSeparator();

		btnZoomIn = Button.newButton( IconSet.ZoomIn, aclZoom, "zoomIn", loc.getString( "toolbarTooltipZoomIn" ) );
		toolBarEdit.add( btnZoomIn );
		btnZoomOut = Button.newButton( IconSet.ZoomOut, aclZoom, "zoomOut", loc.getString( "toolbarTooltipZoomOut" ) );
		toolBarEdit.add( btnZoomOut );
		toolBarEdit.add( new JLabel( " " ) );
		txtZoomFactor = new JTextField( nfZoom.format( CoordinateTools.getZoomFactor() ) );
		txtZoomFactor.setToolTipText( loc.getString( "toolbarTooltipZoomTextBox" ) );
		txtZoomFactor.setMaximumSize( new Dimension( 40, txtZoomFactor.getPreferredSize().height ) );
		txtZoomFactor.addKeyListener( kylZoom );
		txtZoomFactor.addFocusListener( new java.awt.event.FocusAdapter() {
			@Override
			public void focusLost( java.awt.event.FocusEvent evt ) {
				updateZoomFactor();
			}
		} );
		toolBarEdit.add( txtZoomFactor );
		toolBarEdit.addSeparator();

		btnRasterize = Button.newButton( IconSet.Rasterize, aclStart, "rasterize", loc.getString( "toolbarTooltipRasterize" ) );
		toolBarEdit.add( btnRasterize );

		loc.setPrefix( "" );
	}

	/**
	 * Creates the <code>JToolBar</code> for the visualization mode.
	 */
	private void createVisualizationToolBar() {
		// todo loc
		loc.setPrefix( "gui.editor.JEditor." );

		toolBarVisualization = new JToolBar();
		btnExit2 = Button.newButton( IconSet.Exit, new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				System.exit( 0 );
			}
		}, "", loc.getString( "toolbarTooltipExit" ) );
		toolBarVisualization.add( btnExit2 );
		toolBarVisualization.addSeparator();
		labelBatchName1 = new JLabel( loc.getString( "batchName" ) );
		toolBarVisualization.add( labelBatchName1 );
		entryModelVis = new BatchResultEntryVisComboBoxModel();
		JComboBox cbxBatchEntry = new JComboBox( entryModelVis );
		cbxBatchEntry.setLightWeightPopupEnabled( false );
		cbxBatchEntry.setMaximumRowCount( 10 );
		cbxBatchEntry.setMaximumSize( new Dimension( 250, cbxBatchEntry.getPreferredSize().height ) );
		cbxBatchEntry.setPreferredSize( new Dimension( 250, cbxBatchEntry.getPreferredSize().height ) );
		cbxBatchEntry.setAlignmentX( 0 );
		toolBarVisualization.add( cbxBatchEntry );
		labelBatchRun = new JLabel( loc.getString( "batchRun" ) );
		toolBarVisualization.add( labelBatchRun );
		cycleModel = new CycleComboBoxModel();
		JComboBox cbxBatchCycle = new JComboBox( cycleModel );
		cbxBatchCycle.setLightWeightPopupEnabled( false );
		cbxBatchCycle.setRenderer( new CycleComboBoxRenderer() );
		cbxBatchCycle.setMaximumRowCount( 20 );
		cbxBatchCycle.setMaximumSize( new Dimension( 120, cbxBatchCycle.getPreferredSize().height ) );
		cbxBatchCycle.setAlignmentX( 0 );
		toolBarVisualization.add( cbxBatchCycle );

		toolBarVisualization.add( new JLabel( " " ) );
		toolBarVisualization.addSeparator();
		toolBarVisualization.add( new JLabel( " " ) );

		btn2d3dSwitch = Button.newButton( IconSet.Toggle2D3D, aclVisualizationView, "2d3dSwitch", loc.getString( "switch2d3d" ) );
		btn2d3dSwitch.setSelected( !PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.2d" ) );
		toolBarVisualization.add( btn2d3dSwitch );
		btn2dSwitch = Button.newButton( IconSet.ToggleOrthogonalIsometric, aclVisualizationView, "2dSwitch", loc.getString( "switchIso" ) );
		btn2dSwitch.setSelected( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.isometric" ) );
		btn2dSwitch.setEnabled( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.2d" ) );
		toolBarVisualization.add( btn2dSwitch );
		toolBarVisualization.addSeparator();
		btnVideo = Button.newButton( IconSet.Video, aclScreenshot, "video", loc.getString( "saveVideo" ) );
		toolBarVisualization.add( btnVideo );
		btnPlayStart = Button.newButton( IconSet.PlayStart, aclPlay, "start", loc.getString( "playBackToStart" ) );
		toolBarVisualization.add( btnPlayStart );
		btnPlay = Button.newButton( IconSet.Play, aclPlay, "play", loc.getString( "playPause" ) );
		playIcon = gui.components.framework.Icon.newIcon( IconSet.Play );
		pauseIcon = gui.components.framework.Icon.newIcon( IconSet.PlayPause );
		toolBarVisualization.add( btnPlay );
		btnPlayEnd = Button.newButton( IconSet.PlayEnd, aclPlay, "end", loc.getString( "playToEnd" ) );
		toolBarVisualization.add( btnPlayEnd );
		toolBarVisualization.addSeparator();
		btnShowWalls = Button.newButton( IconSet.ShowWalls, aclViewUpdate, "walls", loc.getString( "showWalls" ) );
		btnShowWalls.setSelected( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.walls" ) );
		toolBarVisualization.add( btnShowWalls );
		btnShowGraph = Button.newButton( IconSet.ShowGraph, aclViewUpdate, "graph", loc.getString( "showGraph" ) );
		btnShowGraph.setSelected( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.graph" ) );
		toolBarVisualization.add( btnShowGraph );
		btnShowGraphGrid = Button.newButton( IconSet.ShowGraphGrid, aclViewUpdate, "graphgrid", loc.getString( "showGridRectangles" ) );
		btnShowGraphGrid.setSelected( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.nodeArea" ) );
		toolBarVisualization.add( btnShowGraphGrid );
		btnShowCellularAutomaton = Button.newButton( IconSet.ShowCellularAutomaton, aclViewUpdate, "ca", loc.getString( "showCellularAutomaton" ) );
		btnShowCellularAutomaton.setSelected( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.cellularAutomaton" ) );
		toolBarVisualization.add( btnShowCellularAutomaton );
		toolBarVisualization.addSeparator();
		btnShowAllFloors = Button.newButton( IconSet.ShowAllFloors, aclViewUpdate, "floors", loc.getString( "showAllFloors" ) );
		toolBarVisualization.add( btnShowAllFloors );
		btnShowAllFloors.setSelected( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.floors" ) );
		toolBarVisualization.addSeparator();

		btnShowPotential = Button.newButton( IconSet.ShowPotential, aclViewUpdate, "potential", loc.getString( "showPotential" ) );
		btnShowPotential.setSelected( PropertyContainer.getInstance().getAsInt( "settings.gui.visualization.floorInformation" ) == 1 );
		toolBarVisualization.add( btnShowPotential );
		btnShowDynamicPotential = Button.newButton( IconSet.ShowDynamicPotential, aclViewUpdate, "dynamic", loc.getString( "showDynamicPotential" ) );
		btnShowDynamicPotential.setSelected( PropertyContainer.getInstance().getAsInt( "settings.gui.visualization.floorInformation" ) == 2 );
		toolBarVisualization.add( btnShowDynamicPotential );
		btnShowUtilization = Button.newButton( IconSet.ShowUsage, aclViewUpdate, "utilization", loc.getString( "showUtilization" ) );
		btnShowUtilization.setSelected( PropertyContainer.getInstance().getAsInt( "settings.gui.visualization.floorInformation" ) == 3 );
		toolBarVisualization.add( btnShowUtilization );
		btnShowWaiting = Button.newButton( IconSet.ShowWaiting, aclViewUpdate, "waiting", loc.getString( "showWaitingTime" ) );
		btnShowWaiting.setSelected( PropertyContainer.getInstance().getAsInt( "settings.gui.visualization.floorInformation" ) == 4 );
		toolBarVisualization.add( btnShowWaiting );
		toolBarVisualization.addSeparator();

		loc.setPrefix( "" );
	}

	/**
	 * Creates the <code>JToolBar</code> for the statistic view.
	 */
	private void createStatisticsToolBar() {
		loc.setPrefix( "gui.editor.JEditor." );
		/** ########## CA Statistics Toolbar ############ */
		toolBarCAStats = new JToolBar();
		btnExit3 = Button.newButton( IconSet.Exit, new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				System.exit( 0 );
			}
		}, "", loc.getString( "toolbarTooltipExit" ) );
		toolBarCAStats.add( btnExit3 );
		toolBarCAStats.addSeparator();

		btnOpenResults2 = Button.newButton( IconSet.Open, aclFile, "loadBatchResult", loc.getString( "toolbarTooltipOpen" ) );
		toolBarCAStats.add( btnOpenResults2 );
		btnSaveResults2 = Button.newButton( IconSet.Save, aclFile, "saveResultAs", loc.getString( "toolbarTooltipSave" ) );
		toolBarCAStats.add( btnSaveResults2 );

		/** ########## Graph Statistics Toolbar ############ */
		toolBarGraphStats = new JToolBar();
		btnExit4 = Button.newButton( IconSet.Exit, new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				System.exit( 0 );
			}
		}, "", loc.getString( "toolbarTooltipExit" ) );
		toolBarGraphStats.add( btnExit4 );
		toolBarGraphStats.addSeparator();

		btnOpenResults3 = Button.newButton( IconSet.Open, aclFile, "loadBatchResult", loc.getString( "toolbarTooltipOpen" ) );
		toolBarGraphStats.add( btnOpenResults3 );
		btnSaveResults3 = Button.newButton( IconSet.Save, aclFile, "saveResultAs", loc.getString( "toolbarTooltipSave" ) );
		toolBarGraphStats.add( btnSaveResults3 );
		toolBarGraphStats.addSeparator();

		labelBatchName2 = new JLabel( loc.getString( "batchName" ) );
		toolBarGraphStats.add( labelBatchName2 );
		entryModelGraph = new BatchResultEntryGRSComboBoxModel();
		JComboBox cbxBatchEntry = new JComboBox( entryModelGraph );
		cbxBatchEntry.setMaximumRowCount( 10 );
		cbxBatchEntry.setMaximumSize( new Dimension( 250, cbxBatchEntry.getPreferredSize().height ) );
		cbxBatchEntry.setPreferredSize( new Dimension( 250, cbxBatchEntry.getPreferredSize().height ) );
		cbxBatchEntry.setAlignmentX( 0 );
		toolBarGraphStats.add( cbxBatchEntry );
		loc.setPrefix( "" );
	}

	/**
	 * Creates the <code>JToolBar</code> for the log view.
	 */
	private void createLogToolBar()	 {
		loc.setPrefix( "gui.editor.JEditor." );
		/** ########## Log View Statistics Toolbar ############ */
		toolBarLog = new JToolBar();
		btnExit7 = Button.newButton( IconSet.Exit, new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				System.exit( 0 );
			}
		}, "", loc.getString( "toolbarTooltipExit" ) );
		toolBarLog.add( btnExit7 );
		
		loc.setPrefix( "" );
	}

	/**
	 * Creates the <code>JToolBar</code> for the batch panel.
	 */
	private void createBatchToolBar() {
		loc.setPrefix( "gui.editor.JEditor." );
		toolBarBatch = new JToolBar();
		btnExit5 = Button.newButton( IconSet.Exit, new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				System.exit( 0 );
			}
		}, "", loc.getString( "toolbarTooltipExit" ) );
		toolBarBatch.add( btnExit5 );
		toolBarBatch.addSeparator();

		btnOpenResults1 = Button.newButton( IconSet.Open, aclFile, "loadBatchResult", loc.getString( "toolbarTooltipOpen" ) );
		toolBarBatch.add( btnOpenResults1 );
		btnSaveResults1 = Button.newButton( IconSet.Save, aclFile, "saveResultAs", loc.getString( "toolbarTooltipSave" ) );
		toolBarBatch.add( btnSaveResults1 );
		loc.setPrefix( "" );
	}

	/**
	 * Creates the <code>JToolBar</code> for the quick visualization.
	 */
	private final void createQuickVisualizationToolBar() {
		loc.setPrefix( "gui.editor.JEditor." );
		toolBarCA = new JToolBar();
		btnExit6 = Button.newButton( IconSet.Exit, new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				System.exit( 0 );
			}
		}, "", loc.getString( "toolbarTooltipExit" ) );
		toolBarCA.add( btnExit6 );
		loc.setPrefix( "" );
	}

	/**
	 * Changes the appearance of the gui to the selected language.
	 * @see de.tu_berlin.math.coga.common.localization.Localization
	 */
	public void localize() {
		editView.localize();
		visualizationView.localize();

		loc.setPrefix( "gui.editor.JEditor." );

		Menu.updateMenu( mFile, loc.getString( "menuFile" ) );
		Menu.updateMenu( mEdit, loc.getString( "menuEdit" ) );
		Menu.updateMenu( mView, loc.getString( "menuView" ) );
		Menu.updateMenu( mExtras, loc.getString( "menuExtras" ) );
		//Menu.updateMenu( mWindow, loc.getString( "menuWindow" ) );
		Menu.updateMenu( mHelp, loc.getString( "menuHelp" ) );

		// Dateimenu
		Menu.updateMenu( mnuFileNew, loc.getString( "menuNew" ) );
		Menu.updateMenu( mnuFileOpen, loc.getString( "menuOpen" ) );
		Menu.updateMenu( mnuFileSave, loc.getString( "menuSave" ) );
		Menu.updateMenu( mnuFileSaveAs, loc.getString( "menuSaveAs" ) );
		Menu.updateMenu( mnuFileExportAsDXF, loc.getString( "menuDXF" ) );
		Menu.updateMenu( mnuFileSaveResultAs, loc.getString( "menuSaveResultAs" ) );
		Menu.updateMenu( mnuFileLoadResult, loc.getString( "menuLoadBatchResult" ) );
		//Menu.updateMenu (mnuFileStart, loc.getString ("menuStart"));
		Menu.updateMenu( mnuFileExit, loc.getString( "menuExit" ) );

		// Bearbeiten menu
		//Menu.updateMenu( mnuEditUndo, loc.getString( "menuUndo" ) );
		//Menu.updateMenu( mnuEditGoTo, loc.getString( "menuGoToRoom" ) );
		Menu.updateMenu( mnuEditFloorNew, loc.getString( "menuFloorNew" ) );
		Menu.updateMenu( mnuEditFloorUp, loc.getString( "menuFloorUp" ) );
		Menu.updateMenu( mnuEditFloorDown, loc.getString( "menuFloorDown" ) );
		Menu.updateMenu( mnuEditFloorDelete, loc.getString( "menuFloorDelete" ) );
		Menu.updateMenu( mnuEditFloorCopy, loc.getString( "menuFloorCopy" ) );
		Menu.updateMenu( mnuEditFloorImport, loc.getString( "menuFloorImport" ) );
		Menu.updateMenu( mnuEditRasterize, loc.getString( "menuRasterize" ) );
		Menu.updateMenu( mnuEditDistributeEvacuees, loc.getString( "menuDistributeEvacuees" ) );
		Menu.updateMenu( mnuEditDistribution, loc.getString( "menuDistributions" ) );
		Menu.updateMenu( mnuEditProperties, loc.getString( "menuProperties" ) );
		Menu.updateMenu( mnuScreenshot, loc.getString( "menuScreenshot" ) );

		// Anzeige-menu
		Menu.updateMenu( mVisibleAreas, loc.getString( "menuVisibleAreas" ) );
		Menu.updateMenu( mnuShowAllAreas, loc.getString( "menuShowAllAreas" ) );
		Menu.updateMenu( mnuHideAllAreas, loc.getString( "menuHideAllAreas" ) );
		Menu.updateMenu( mnuDelayArea, loc.getString( "menuShowDelayAreas" ) );
		Menu.updateMenu( mnuStairArea, loc.getString( "menuShowStairAreas" ) );
		Menu.updateMenu( mnuEvacuationArea, loc.getString( "menuShowEvacuationAreas" ) );
		Menu.updateMenu( mnuInaccessibleArea, loc.getString( "menuShowInaccessibleAreas" ) );
		Menu.updateMenu( mnuSaveArea, loc.getString( "menuShowSaveAreas" ) );
		Menu.updateMenu( mnuAssignmentArea, loc.getString( "menuShowAssignmentAreas" ) );
		Menu.updateMenu( mGrid, loc.getString( "menuGridstyle" ) );
		Menu.updateMenu( mnuGridLines, loc.getString( "menuGridstyleLines" ) );
		Menu.updateMenu( mnuGridPoints, loc.getString( "menuGridstylePoints" ) );
		Menu.updateMenu( mnuGridNotVisible, loc.getString( "menuGridstyleNone" ) );
		Menu.updateMenu( mnuPaintRasterized, loc.getString( "menuDrawOnGrid" ) );
		Menu.updateMenu( mnuHideDefaultFloor, loc.getString( "menuHideDefaultEvacuationFloor" ) );

		// Execute menu (debug only)
		if( ZETMain.isDebug() ) {
			Menu.updateMenu( mExecute, loc.getString( "menuExecute" ) );
			Menu.updateMenu( mSimulation, loc.getString( "menuSimulation" ) );
			Menu.updateMenu( mnuCreateCA, loc.getString( "menuSimulationCreateCA" ) );
			Menu.updateMenu( mnuApplyAssignment, loc.getString( "menuSimulationApplyConcreteAssignment" ) );
			Menu.updateMenu( mnuStartSimulation, loc.getString( "menuSimulationStart" ) );
			//TODO if paused use other
			if( caAlgo != null )
				if( caAlgo.isPaused() )
					Menu.updateMenu( mnuPauseSimulation, loc.getString( "menuSimulationContinue" ) );
				else
					Menu.updateMenu( mnuPauseSimulation, loc.getString( "menuSimulationPause" ) );
			else
				Menu.updateMenu( mnuPauseSimulation, loc.getString( "menuSimulationPause" ) );
			Menu.updateMenu( mnuStepByStepSimulation, loc.getString( "menuSimulationStepByStep" ) );
			Menu.updateMenu( mnuQuickVisualization, loc.getString( "menuSimulationQuickVisualization" ) );
			Menu.updateMenu( mOptimization, loc.getString( "menuOptimization" ) );
			Menu.updateMenu( mnuCreateGraph, loc.getString( "menuOptimizationCreateGraph" ) );
			Menu.updateMenu( mnuQuickestTransshipment, loc.getString( "menuOptAlgoQuickestTransshipment" ) );
			Menu.updateMenu( mnuMaxFlowOverTimeMC, loc.getString( "menuOptAlgoMaxFlowOverTimeMinCost" ) );
			Menu.updateMenu( mnuMaxFlowOverTimeTEN, loc.getString( "menuOptAlgoMaxFlowOverTimeTEN" ) );
			Menu.updateMenu( mnuEarliestArrivalTransshipment, loc.getString( "menuOptAlgoEATransshipment" ) );
		}

		// Extras menu
		Menu.updateMenu( mLanguage, loc.getString( "menuLanguages" ) );
		Menu.updateMenu( mPlanImage, loc.getString( "menuPlanDisplaying" ) );
		Menu.updateMenu( mnuPlanImageLoad, loc.getString( "menuLoadPlan" ) );
		Menu.updateMenu( mnuPlanImageHide, loc.getString( "menuHidePlan" ) );
		Menu.updateMenu( mnuPlanImageResize, loc.getString( "menuResizePlan" ) );
		Menu.updateMenu( mnuPlanImageLocate, loc.getString( "menuMovePlan" ) );
		Menu.updateMenu( mnuPlanImageTransparency, loc.getString( "menuSetPlanTransparency" ) );
		Menu.updateMenu( mnuOptions, loc.getString( "menuOptions" ) );
		if( ZETMain.isDebug() ) {
			Menu.updateMenu( mnuSettings, loc.getString( "menuSettings" ) );
		}

		// Window menu

		// Help menu
		Menu.updateMenu( mnuHelpAbout, loc.getString( "menuAbout" ) );

		// Tabs in the tabbed view
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
		loc.setPrefix( "" );


		// Tool tips for buttons and text views in the menu bar
		loc.setPrefix( "gui.editor.JEditor." );
		cbxEdit.setToolTipText( loc.getString( "toolbarTooltipAreaType" ) );
		btnExit1.setToolTipText( loc.getString( "toolbarTooltipExit" ) );
		btnExit2.setToolTipText( loc.getString( "toolbarTooltipExit" ) );
		btnExit3.setToolTipText( loc.getString( "toolbarTooltipExit" ) );
		btnExit4.setToolTipText( loc.getString( "toolbarTooltipExit" ) );
		btnExit5.setToolTipText( loc.getString( "toolbarTooltipExit" ) );
		btnExit6.setToolTipText( loc.getString( "toolbarTooltipExit" ) );
		btnExit7.setToolTipText( loc.getString( "toolbarTooltipExit" ) );
		btnOpen.setToolTipText( loc.getString( "toolbarTooltipOpen" ) );
		btnSave.setToolTipText( loc.getString( "toolbarTooltipSave" ) );

		btnOpen.setToolTipText( loc.getString( "toolbarTooltipOpen" ) );
		btnOpenResults1.setToolTipText( loc.getString( "toolbarTooltipOpen" ) );
		btnOpenResults2.setToolTipText( loc.getString( "toolbarTooltipOpen" ) );
		btnOpenResults3.setToolTipText( loc.getString( "toolbarTooltipOpen" ) );
		btnSave.setToolTipText( loc.getString( "toolbarTooltipSave" ) );
		btnSaveResults1.setToolTipText( loc.getString( "toolbarTooltipSave" ) );
		btnSaveResults2.setToolTipText( loc.getString( "toolbarTooltipSave" ) );
		btnSaveResults3.setToolTipText( loc.getString( "toolbarTooltipSave" ) );

		btnEditSelect.setToolTipText( loc.getString( "toolbarTooltipSelectionMode" ) );
		btnEditPointwise.setToolTipText( loc.getString( "toolbarTooltipPointSequence" ) );
		btnEditRectangled.setToolTipText( loc.getString( "toolbarTooltipDragCreate" ) );
		lblAreaType.setText( loc.getString( "labelAreaType" ) );
		btnZoomIn.setToolTipText( loc.getString( "toolbarTooltipZoomIn" ) );
		btnZoomOut.setToolTipText( loc.getString( "toolbarTooltipZoomOut" ) );
		txtZoomFactor.setToolTipText( loc.getString( "toolbarTooltipZoomTextBox" ) );
		btnRasterize.setToolTipText( loc.getString( "toolbarTooltipRasterize" ) );

		labelBatchName1.setText( loc.getString( "batchName" ) );
		labelBatchName2.setText( loc.getString( "batchName" ) );
		labelBatchRun.setText( loc.getString( "batchRun" ) );

		// Visualization toolbar
		btn2d3dSwitch.setToolTipText( loc.getString( "switch2d3d" ) );
		btn2dSwitch.setToolTipText( loc.getString( "switchIso" ) );
		btnVideo.setToolTipText( loc.getString( "saveVideo" ) );
		btnPlayStart.setToolTipText( loc.getString( "playBackToStart" ) );
		btnPlay.setToolTipText( loc.getString( "playPause" ) );
		btnPlayEnd.setToolTipText( loc.getString( "playToEnd" ) );
		btnShowWalls.setToolTipText( loc.getString( "showWalls" ) );
		btnShowGraph.setToolTipText( loc.getString( "showGraph" ) );
		btnShowGraphGrid.setToolTipText( loc.getString( "showGridRectangles" ) );
		btnShowCellularAutomaton.setToolTipText( loc.getString( "showCellularAutomaton" ) );
		btnShowAllFloors.setToolTipText( loc.getString( "showAllFloors" ) );
		btnShowPotential.setToolTipText( loc.getString( "showPotential" ) );
		btnShowDynamicPotential.setToolTipText( loc.getString( "showDynamicPotential" ) );
		btnShowUtilization.setToolTipText( loc.getString( "showUtilization" ) );
		btnShowWaiting.setToolTipText( loc.getString( "showWaitingTime" ) );

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
	ActionListener aclAbout = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			CreditsDialog credits = new CreditsDialog( instance );
			credits.setVisible( true );
		}
	};
	ActionListener aclAreaVisibility = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			if( e.getActionCommand().equals( "showAll" ) ) {
				mnuDelayArea.setSelected( true );
				mnuStairArea.setSelected( true );
				mnuEvacuationArea.setSelected( true );
				mnuInaccessibleArea.setSelected( true );
				mnuSaveArea.setSelected( true );
				mnuAssignmentArea.setSelected( true );
			} else if( e.getActionCommand().equals( "hideAll" ) ) {
				mnuDelayArea.setSelected( false );
				mnuStairArea.setSelected( false );
				mnuEvacuationArea.setSelected( false );
				mnuInaccessibleArea.setSelected( false );
				mnuSaveArea.setSelected( false );
				mnuAssignmentArea.setSelected( false );
			} // do not check for other commands as the other select and unselect!
			updateAreaVisiblity();
		}
	};
	ActionListener aclDistribution = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			//if( distribution == null ) {
			distribution = new JAssignment( instance, getZControl().getProject(), loc.getString( "gui.editor.assignment.JAssignment.Title" ), 850, 400 );
			//}
			distribution.setVisible( true );
			distribution.dispose();
		}
	};
	ActionListener aclDebug = new ActionListener() {
    @Override
		public void actionPerformed( ActionEvent e ) {
			if( e.getActionCommand().equals( "outputInformation" ) ) {
				// Pro Stockwerk:
				System.out.println( "Personenverteilung im Gebäude: ");
				int overall = 0;
				for( Floor f : getZControl().getProject().getBuildingPlan() ) {
          int counter = 0;
					for( Room r : f ) {
						for( AssignmentArea a : r.getAssignmentAreas() ) {
							counter += a.getEvacuees();
						}
					}
					System.out.println( f.getName() + ": " + counter + " Personen" );
					overall += counter;
				}
				System.out.println( "Insgesamt: " + overall );
			}

			// Pro Ausgang:
			System.out.println( "Personenverteilung pro Ausgang: " );
			for( Floor f : getZControl().getProject().getBuildingPlan() )
				for( Room r : f )
					for( EvacuationArea ea : r.getEvacuationAreas() ) {
						int overall = 0;
						System.out.println( "" );
						System.out.println( ea.getName() );
						// Suche nach evakuierten pro etage für dieses teil
						for( Floor f2 : getZControl().getProject().getBuildingPlan() ) {
							int counter = 0;
							for( Room r2 : f2 )
								for( AssignmentArea a : r2.getAssignmentAreas() ) {
                  if( a.getExitArea().equals( ea ) )
										counter += a.getEvacuees();
								}
								System.out.println( f2.getName() + ": " + counter + " Personen" );
								overall += counter;
						}
						System.out.println( ea.getName() + " insgesamt: " + overall );
					}
		}
	};
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
	ActionListener aclFile = new ActionListener() {
		private JFileChooser jfcProject;
		private JFileChooser jfcResults;

		{
			jfcProject = new JFileChooser( GUIOptionManager.getSavePath() );
			jfcProject.setFileFilter( getProjectFilter() );
			jfcProject.setAcceptAllFileFilterUsed( false );

			jfcResults = new JFileChooser( GUIOptionManager.getSavePathResults() );
			jfcResults.setFileFilter( getResultsFilter() );
			jfcResults.setAcceptAllFileFilterUsed( false );
		}

		@Override
		public void actionPerformed( ActionEvent e ) {
			if( e.getActionCommand().equals( "loadProject" ) ) {
				if( jfcProject.showOpenDialog( getInstance() ) == JFileChooser.APPROVE_OPTION ) {
					projectControl.loadProject( jfcProject.getSelectedFile() );
					loadProject();	// Load the currently loaded project by the control file
					GUIOptionManager.setSavePath( jfcProject.getCurrentDirectory().getPath() );
				}
			} else if( e.getActionCommand().equals( "saveProjectAs" ) || (e.getActionCommand().equals( "saveProject" ) && getZControl().getProject().getProjectFile() == null) ) {
				if( jfcProject.showSaveDialog( getInstance() ) == JFileChooser.APPROVE_OPTION ) {
					GUIOptionManager.setSavePath( jfcProject.getCurrentDirectory().getPath() );
					if( jfcProject.getSelectedFile().exists() && createCopy )
						createBackup( jfcProject.getSelectedFile() );
					try {
						File target = jfcProject.getSelectedFile();
						if( !target.getName().endsWith( ".zet" ) )
							target = new File( target.getAbsolutePath() + ".zet" );
						getZControl().getProject().save( target );
					} catch( java.lang.StackOverflowError soe ) {
						showErrorMessage( loc.getString( "gui.editor.JEditor.error.stackOverflowTitle" ), loc.getString( "gui.editor.JEditor.error.stackOverflow" ) );
					} catch( Exception ex ) {
						showErrorMessage( loc.getString( "gui.editor.JEditor.error.SaveTitle" ), loc.getString( "gui.editor.JEditor.error.Save" ) );
					}
					editView.displayProject( zcontrol );
					ZETMain.sendMessage( loc.getString( "gui.editor.JEditor.message.saved" ) );
				}
			} else if( e.getActionCommand().equals( "saveProject" ) ) {
				if( createCopy == true )
					createBackup();
				try {
					getZControl().getProject().save();
				} catch( java.lang.StackOverflowError soe ) {
					showErrorMessage( loc.getString( "gui.editor.JEditor.error.stackOverflowTitle" ), loc.getString( "gui.editor.JEditor.error.stackOverflow" ) );
				} catch( Exception ex ) {
					showErrorMessage( loc.getString( "gui.editor.JEditor.error.SaveTitle" ), loc.getString( "gui.editor.JEditor.error.Save" ) );
					ex.printStackTrace();
					return;
				}
				ZETMain.sendMessage( loc.getString( "gui.editor.JEditor.message.saved" ) );
			} else if( e.getActionCommand().equals( "newProject" ) ) {
				String status = "";
				switch( JOptionPane.showOptionDialog( getInstance(),
								loc.getString( "gui.editor.JEditor.SaveQuestion" ),
								loc.getString( "gui.editor.JEditor.NewProject" ),
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								null, null, null ) ) {
					case 2:
					case -1:
						return;	// exit, do nothing
					case 0:
						// save
						if( getZControl().getProject().getProjectFile() == null ) {
							if( jfcProject.showSaveDialog( getInstance() ) == JFileChooser.APPROVE_OPTION ) {
								GUIOptionManager.setSavePath( jfcProject.getCurrentDirectory().getPath() );
								if( jfcProject.getSelectedFile().exists() && createCopy )
									createBackup( jfcProject.getSelectedFile() );
								try {
									File target = jfcProject.getSelectedFile();
									if( !target.getName().endsWith( ".zet" ) )
										target = new File( target.getAbsolutePath() + ".zet" );
									getZControl().getProject().save( target );
								} catch( java.lang.StackOverflowError soe ) {
									showErrorMessage( loc.getString( "gui.editor.JEditor.error.stackOverflowTitle" ), loc.getString( "gui.editor.error.JEditor.stackOverflow" ) );
								} catch( Exception ex ) {
									showErrorMessage( loc.getString( "gui.editor.JEditor.error.SaveTitle" ), loc.getString( "gui.editor.JEditor.error.Save" ) );
									ex.printStackTrace();
									return;
								}
							}
						} else {
							if( createCopy )
								createBackup();
							try {
								getZControl().getProject().save();
							} catch( java.lang.StackOverflowError soe ) {
								showErrorMessage( loc.getString( "gui.editor.JEditor.error.stackOverflowTitle" ), loc.getString( "gui.editor.JEditor.error.stackOverflow" ) );
							} catch( Exception ex ) {
								showErrorMessage( loc.getString( "gui.editor.JEditor.error.SaveTitle" ), loc.getString( "gui.editor.JEditor.error.Save" ) );
								ex.printStackTrace();
								return;
							}
						}
						status = loc.getString( "gui.editor.JEditor.status.newProject" );
						break;
					case 1:
						status = loc.getString( "gui.editor.JEditor.status.newProjectDiscard" );
				}
				// TODO: better (next 3 lines)
				Project p = ProjectControl.newProject();
				projectControl.setProject( p );
				zcontrol = projectControl.getZControl();

				distribution = null; // Throw away the old assignment window
				editView.displayProject( zcontrol );
				ZETMain.sendMessage( status );
			} else if( e.getActionCommand().equals( "loadBatchResult" ) ) {
				if( jfcResults.showOpenDialog( getInstance() ) == JFileChooser.APPROVE_OPTION ) {
					GUIOptionManager.setSavePathResults( jfcProject.getCurrentDirectory().getPath() );
					try {
						setBatchResult( BatchResult.load( (jfcResults.getSelectedFile()) ) );
						ZETMain.sendMessage( loc.getString( "gui.editor.JEditor.message.loaded" ) );
					} catch( Exception ex ) {
						showErrorMessage( loc.getString( "gui.editor.JEditor.error.SaveTitle" ), loc.getString( "gui.editor.JEditor.error.Save" ) );
						ex.printStackTrace();
					}
				}
			} else if( e.getActionCommand().equals( "saveResultAs" ) ) {
				if( jfcResults.showSaveDialog( instance ) == JFileChooser.APPROVE_OPTION ) {
					GUIOptionManager.setSavePathResults( jfcProject.getCurrentDirectory().getPath() );
					try {
						File target = jfcResults.getSelectedFile();
						if( !target.getName().endsWith( ".ers" ) )
							target = new File( target.getAbsolutePath() + ".ers" );
						result.save( target );
					} catch( java.lang.StackOverflowError soe ) {
						showErrorMessage( loc.getString( "gui.editor.JEditor.error.stackOverflowTitle" ), loc.getString( "gui.editor.JEditor.error.stackOverflow" ) );
					} catch( Exception ex ) {
						showErrorMessage( loc.getString( "gui.editor.JEditor.error.SaveTitle" ), loc.getString( "gui.editor.JEditor.error.Save" ) );
						ex.printStackTrace();
					}
					ZETMain.sendMessage( loc.getString( "gui.editor.JEditor.message.saved" ) );
				}
			} else if( e.getActionCommand().equals( "saveAsDXF" ) ) {
				String filename = getZControl().getProject().getProjectFile().getPath().substring( 0, getZControl().getProject().getProjectFile().getPath().length() - 3 ) + "dxf";
				try {
					DXFWriter.exportIntoDXF( filename, zcontrol.getProject().getBuildingPlan() );
				} catch( IOException ex ) {
					showErrorMessage( loc.getString( "gui.editor.JEditor.error.SaveTitle" ), loc.getString( "gui.editor.JEditor.error.Save" ) );
					ex.printStackTrace();
					return;
				}
				ZETMain.sendMessage( loc.getString( "gui.editor.JEditor.message.dxfComplete" ) );
			} else
				ZETMain.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
		}
	};
	ActionListener aclFloor = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			try {
				if( e.getActionCommand().equals( "new" ) ) {
					getZControl().getProject().getBuildingPlan().addFloor( new Floor( loc.getString( "ds.z.DefaultName.Floor" ) + " " + getZControl().getProject().getBuildingPlan().floorCount() ) );
					ZETMain.sendMessage( "Neue Etage angelegt." ); // TODO loc
				} else if( e.getActionCommand().equals( "up" ) ) {
					final int oldIndex = editView.getFloorID();
					projectControl.moveFloorUp( editView.getFloorID() + (ZETProperties.isDefaultFloorHidden() ? 1 : 0) );
					editView.setFloor( oldIndex + 1 );
				} else if( e.getActionCommand().equals( "down" ) ) {
					final int oldIndex = editView.getFloorID();
					projectControl.moveFloorDown( editView.getFloorID() + (ZETProperties.isDefaultFloorHidden() ? 1 : 0) );
					editView.setFloor( oldIndex - 1 );
				} else if( e.getActionCommand().equals( "delete" ) )
					getZControl().getProject().getBuildingPlan().removeFloor( editView.getCurrentFloor() );
				else if( e.getActionCommand().equals( "import" ) ) {
					FloorImportDialog floorImport = new FloorImportDialog( instance, getZControl().getProject(), "Importieren", 450, 250 );
					floorImport.setVisible( true );
				} else if( e.getActionCommand().equals( "copy" ) ) {
					final int oldIndex = editView.getFloorID();
					projectControl.copyFloor( editView.getCurrentFloor() );
					editView.setFloor( oldIndex );
				} else
					ZETMain.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
			} catch( IllegalArgumentException ex ) {
				ZETMain.sendError( ex.getLocalizedMessage() );
			} catch( Exception ex ) {
				JOptionPane.showMessageDialog( getInstance(),
								ex.getLocalizedMessage(), loc.getString( "gui.Error" ),
								JOptionPane.ERROR_MESSAGE );
				ex.printStackTrace();
			}
		}
	};
	ActionListener aclLanguage = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			try {
				if( e.getActionCommand().equals( "german" ) )
					loc.setLocale( Locale.GERMAN );
				else if( e.getActionCommand().equals( "english" ) )
					loc.setLocale( Locale.ENGLISH );
				else
					ZETMain.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
				localize();
			} catch( Exception ex ) {
				JOptionPane.showMessageDialog( getInstance(),
								ex.getLocalizedMessage(), loc.getString( "gui.Error" ),
								JOptionPane.ERROR_MESSAGE );
			}
		}
	};
	ActionListener aclPaint = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			try {
				if( e.getActionCommand().equals( "grid" ) )
					editView.getFloor().setRasterizedPaintMode( mnuPaintRasterized.isSelected() );
				else if( e.getActionCommand().equals( "defaultFloor" ) ) {
					ZETProperties.isDefaultFloorHidden();
					PropertyContainer.getInstance().set( "editor.options.view.hideDefaultFloor", mnuHideDefaultFloor.isSelected() );
					editView.displayProject();
				} else if( e.getActionCommand().equals( "gridLine" ) ) {
					mnuGridPoints.setSelected( false );
					mnuGridNotVisible.setSelected( false );
					editView.getFloor().setRasterPaintStyle( RasterPaintStyle.Lines );
				} else if( e.getActionCommand().equals( "gridPoint" ) ) {
					mnuGridLines.setSelected( false );
					mnuGridNotVisible.setSelected( false );
					editView.getFloor().setRasterPaintStyle( RasterPaintStyle.Points );
				} else if( e.getActionCommand().equals( "gridNo" ) ) {
					mnuGridLines.setSelected( false );
					mnuGridPoints.setSelected( false );
					editView.getFloor().setRasterPaintStyle( RasterPaintStyle.Nothing );
				} else
					ZETMain.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
			} catch( Exception ex ) {
				JOptionPane.showMessageDialog( getInstance(),
								ex.getLocalizedMessage(), loc.getString( "gui.Error" ),
								JOptionPane.ERROR_MESSAGE );
			}
		}
	};
	ActionListener aclPlanImage = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			try {
				if( e.getActionCommand().equals( "load" ) ) {
					JFileChooser d = new JFileChooser( GUIOptionManager.getBuildingPlanPath() );
					d.setFileFilter( new FileFilter() {
						@Override
						public boolean accept( File f ) {
							return f.isDirectory() || f.getName().toLowerCase().endsWith( ".jpg" ) || f.getName().toLowerCase().endsWith( ".png" ) || f.getName().toLowerCase().endsWith( ".gif" );
						}

						@Override
						public String getDescription() {
							return "Bilddateien (*.jpg;*.png;*.gif)";
						}
					} );
					d.showOpenDialog( null );
					File file = d.getSelectedFile();
					BufferedImage image = null;
					if( file != null ) {
						GUIOptionManager.setBuildingPlanPath( d.getCurrentDirectory().getPath() );
						try {
							image = ImageIO.read( file );
						} catch( IOException ex ) {
							ex.printStackTrace();
						}

						// Show Zoom/Size Dialogue
						JPlanImageProperties ip = new JPlanImageProperties( image );
						if( ip.showPlanImageZoomDialog( getInstance() ) == JPlanImageProperties.OK ) {
							CoordinateTools.setPictureZoomFactor( (double)ip.getMillimeterCount() / (double)ip.getPixelCount() );
							editView.getFloor().getPlanImage().setImage( image );
							mnuPlanImageHide.setEnabled( true );
							mnuPlanImageResize.setEnabled( true );
							mnuPlanImageLocate.setEnabled( true );
							mnuPlanImageTransparency.setEnabled( true );
							ZETMain.sendMessage( "Plan für Hintergrunddarstellung geladen." );
						}
					}
				} else if( e.getActionCommand().equals( "hide" ) ) {
					editView.getFloor().getPlanImage().setImage( (BufferedImage)null );
					mnuPlanImageHide.setEnabled( false );
					mnuPlanImageResize.setEnabled( false );
					mnuPlanImageLocate.setEnabled( false );
					mnuPlanImageTransparency.setEnabled( false );
				} else if( e.getActionCommand().equals( "resize" ) ) {
					BufferedImage image = editView.getFloor().getPlanImage().getImage();
					// Show Zoom/Size Dialogue
					JPlanImageProperties ip = new JPlanImageProperties( image );
					if( ip.showPlanImageZoomDialog( getInstance() ) == JPlanImageProperties.OK ) {
						CoordinateTools.setPictureZoomFactor( (double)ip.getMillimeterCount() / (double)ip.getPixelCount() );
						editView.getFloor().getPlanImage().resize();
					}
				} else if( e.getActionCommand().equals( "move" ) ) {
					JPlanImageProperties ip = new JPlanImageProperties();
					ip.setXOffset( editView.getFloor().getPlanImage().getImageX() );
					ip.setYOffset( editView.getFloor().getPlanImage().getImageY() );
					if( ip.showPlanMoveDialog( getInstance() ) == JPlanImageProperties.OK ) {
						editView.getFloor().getPlanImage().setImageX( ip.getXOffset() );
						editView.getFloor().getPlanImage().setImageY( ip.getYOffset() );
					}
				} else if( e.getActionCommand().equals( "transparency" ) ) {
					JPlanImageProperties ip = new JPlanImageProperties();
					ip.setAlpha( editView.getFloor().getPlanImage().getAlpha() );
					if( ip.showPlanAlphaDialog( getInstance() ) == JPlanImageProperties.OK )
						editView.getFloor().getPlanImage().setAlpha( ip.getAlpha() );
				} else
					ZETMain.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );

			} catch( Exception ex ) {
				JOptionPane.showMessageDialog( getInstance(),
								ex.getLocalizedMessage(), loc.getString( "gui.Error" ),
								JOptionPane.ERROR_MESSAGE );
			}
		}
	};
	ActionListener aclPlay = new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			if( e.getActionCommand().equals( "start" ) )
				ZETMain.sendError( "Not supported yet" );
			else if( e.getActionCommand().equals( "play" ) )
				if( visualizationView.getGLContainer().isAnimating() ) {
					btnPlay.setIcon( playIcon );
					btnPlay.setSelected( false );
					visualizationView.getGLContainer().stopAnimation();
				} else {
					btnPlay.setIcon( pauseIcon );
					btnPlay.setSelected( true );
					visualizationView.getGLContainer().startAnimation();
				}
			else if( e.getActionCommand().equals( "end" ) )
				ZETMain.sendError( "Not supported yet" );
			else
				ZETMain.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
		}
	};
	ActionListener aclProperties = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			if( e.getActionCommand().equals( "properties" ) ) {
				JPropertySelectorWindow propertySelector = new JPropertySelectorWindow( instance, loc.getString( "gui.editor.JPropertySelector.Title" ), 700, 500 );
				propertySelector.setVisible( true );
				System.out.println( "Properties saved." ); // TODO loc
			} else if( e.getActionCommand().equals( "options" ) ) {
				ZETMain.ptmOptions.getRoot().reloadFromPropertyContainer();
				JOptionsWindow propertySelector = new JOptionsWindow( instance, loc.getString( "gui.editor.JOptions.Title" ), 700, 500, ZETMain.ptmOptions );
				propertySelector.setVisible( true );
				try {	// Save results in options file
					PropertyContainer.saveConfigFile( ZETMain.ptmOptions, new File( ZETMain.optionFilename ) );
				} catch( IOException ex ) {
					ZETMain.sendError( "Error saving config file!" ); // TODO loc
				}
			} else if( e.getActionCommand().equals( "settings" ) ) {
				ZETMain.ptmInformation.getRoot().reloadFromPropertyContainer();
				JOptionsWindow propertySelector = new JOptionsWindow( instance, loc.getString( "gui.editor.settings.Title" ), 700, 500, ZETMain.ptmInformation );
				propertySelector.setVisible( true );
				try {	// Save results in settings file
					PropertyContainer.saveConfigFile( ZETMain.ptmInformation, new File( ZETMain.informationFilename ) );
				} catch( IOException ex ) {
					ZETMain.sendError( "Error saving settings file!" ); // TODO loc
				}
			}else
				ZETMain.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
		}
	};
	ActionListener aclScreenshot = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			if( e.getActionCommand().equals( "screenshot" ) ) {
				String path = PropertyContainer.getInstance().getAsString( "options.filehandling.screenshotPath" );
				if( !(path.endsWith( "/" ) || path.endsWith( "\\" )) )
					path = path + "/";
				String projectName;
				try {
					projectName = getZControl().getProject().getProjectFile().getName().substring( 0, getZControl().getProject().getProjectFile().getName().length() - 4 );
				} catch( NullPointerException ex ) {
					projectName = "untitled";
				}
				String newFilename = IOTools.getNextFreeNumberedFilepath( path, projectName, 3 ) + ".png";
				visualizationView.getGLContainer().takeScreenshot( newFilename );
			} else if( e.getActionCommand().equals( "video" ) ) {
				VideoOptions vo = new VideoOptions( instance );
				// Setze die erwartete Laufzeit
				vo.setEstimatedTime( visualizationView.getGLContainer().getControl().getEstimatedTime() );
				vo.setResolution( visualizationView.getGLContainer().getSize() );
				vo.setBitrate( 1000 );
				vo.setFramerate( 24 );
				vo.setTextureFontStrings( visualizationView.getGLContainer().getTexts() );
				vo.setVisible( true );
				vo.dispose();
				if( vo.getRetVal() == VideoOptions.OK ) {
					visualizationView.getGLContainer().setTexts( vo.getTextureFontStrings() );
					projectControl.getZControl().getProject().getVisualProperties().setTextureFontStrings( vo.getTextureFontStrings() );
					String movieFrameName = PropertyContainer.getInstance().getAsString( "options.filehandling.movieFrameName" );
					// TODO BUG: wenn ein projekt noch nicht gespeichert worden ist, liefert das hier iene null pointer exception. (tritt auf, wenn ein video gedreht werden soll)
					String projectName = getZControl().getProject().getProjectFile().getName().substring( 0, getZControl().getProject().getProjectFile().getName().length() - 4 );
					MovieManager movieCreator = visualizationView.getGLContainer().getMovieCreator();
					if( movieFrameName.equals( "" ) )
						movieCreator.setFramename( projectName );
					else
						movieCreator.setFramename( movieFrameName );
					String path = PropertyContainer.getInstance().getAsString( "options.filehandling.moviePath" );
					if( !(path.endsWith( "/" ) || path.endsWith( "\\" )) )
						path = path + "/";
					String movieFileName = IOTools.getNextFreeNumberedFilename( path, projectName, 3 );
					movieCreator.setFilename( movieFileName );
					movieCreator.setPath( PropertyContainer.getInstance().getAsString( "options.filehandling.moviePath" ) );
					movieCreator.setFramename( PropertyContainer.getInstance().getAsString( "options.filehandling.movieFrameName" ) );
					movieCreator.setMovieWriter( vo.getMovieWriter() );
					visualizationView.getGLContainer().setRecording( true, vo.getResolution() );
					movieCreator.setWidth( vo.getResolution().width );
					movieCreator.setHeight( vo.getResolution().height );
					movieCreator.setCreateMovie( vo.isMovieMode() );
					movieCreator.setDeleteFrames( vo.isDeleteFrames() );
					movieCreator.setMovieFormat( vo.getMovieFormat() );
					movieCreator.setFramerate( vo.getFramerate() );
					movieCreator.setBitrate( vo.getBitrate() );
					visualizationView.getGLContainer().setMovieFramerate( vo.getFramerate() );
					movieCreator.setFrameFormat( vo.getFrameFormat() );
					visualizationView.getGLContainer().startAnimation();
				}
			} else
				ZETMain.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
		}
	};
	ActionListener aclStart = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			try {
				if( e.getActionCommand().equals( "rasterize" ) )
					try {
						RasterizeTask rasterize = new RasterizeTask( getZControl().getProject() );
						JProgressBarDialog pbd = new JRasterizeProgressBarDialog( getInstance(), "Rastern", true, rasterize );
						pbd.executeTask();
						pbd.setVisible( true );
						ZETMain.sendMessage( loc.getString( "gui.message.RasterizationComplete" ) );
					} catch( Exception ex ) {
						ZETMain.sendError( ex.getLocalizedMessage() );
					}
				else if( e.getActionCommand().equals( "distributeEvacuees" ) )
					try {
						String res = JOptionPane.showInputDialog( instance,
										"Anzahl zu evakuierender Personen (maximal " +
										Integer.toString( getZControl().getProject().getBuildingPlan().maximalEvacuees() ) + ")", "Personen verteilen", JOptionPane.QUESTION_MESSAGE );

						if( res != null ) {
							getZControl().getProject().getBuildingPlan().distributeEvacuees( Integer.parseInt( res ) );
							ZETMain.sendMessage( loc.getString( "gui.message.RasterizationComplete" ) );
						}
					} catch( NumberFormatException ex ) {
						ZETMain.sendError( loc.getString( "gui.error.NonParsableNumber" ) );
					} catch( TooManyPeopleException ex ) {
						ZETMain.sendError( ex.getLocalizedMessage() );
					}
				else
					ZETMain.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
			} catch( Exception ex ) {
				JOptionPane.showMessageDialog( getInstance(),
								ex.getLocalizedMessage(), loc.getString( "gui.Error" ),
								JOptionPane.ERROR_MESSAGE );
			}
		}
	};
	ActionListener aclViewUpdate = new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			if( e.getActionCommand().equals( "graphgrid" ) ) {
				btnShowGraphGrid.setSelected( !btnShowGraphGrid.isSelected() );
				PropertyContainer.getInstance().set( "settings.gui.visualization.nodeArea", btnShowGraphGrid.isSelected() );
				control.showNodeRectangles( btnShowGraphGrid.isSelected() );
				visualizationView.getGLContainer().repaint();
			} else if( e.getActionCommand().equals( "walls" ) ) {
				btnShowWalls.setSelected( !btnShowWalls.isSelected() );
				PropertyContainer.getInstance().set( "settings.gui.visualization.walls", btnShowWalls.isSelected() );
				control.showWalls( btnShowWalls.isSelected() );
				visualizationView.getGLContainer().repaint();
			} else if( e.getActionCommand().equals( "graph" ) ) {
				btnShowGraph.setSelected( !btnShowGraph.isSelected() );
				PropertyContainer.getInstance().set( "settings.gui.visualization.graph", btnShowGraph.isSelected() );
				control.showGraph( btnShowGraph.isSelected() );
				visualizationView.getGLContainer().repaint();
			} else if( e.getActionCommand().equals( "ca" ) ) {
				btnShowCellularAutomaton.setSelected( !btnShowCellularAutomaton.isSelected() );
				PropertyContainer.getInstance().set( "settings.gui.visualization.cellularAutomaton", btnShowCellularAutomaton.isSelected() );
				control.showCellularAutomaton( btnShowCellularAutomaton.isSelected() );
				visualizationView.getGLContainer().repaint();
			} else if( e.getActionCommand().equals( "floors" ) ) {
				btnShowAllFloors.setSelected( !btnShowAllFloors.isSelected() );
				PropertyContainer.getInstance().set( "settings.gui.visualization.floors", btnShowAllFloors.isSelected() );
				visualizationView.setFloorSelectorEnabled( !btnShowAllFloors.isSelected() );
				if( btnShowAllFloors.isSelected() )
					control.showAllFloors();
				else
					control.showFloor( visualizationView.getSelectedFloorID() );
				visualizationView.getGLContainer().repaint();
			} else if( e.getActionCommand().equals( "potential" ) ) {
				if( btnShowPotential.isSelected() ) {
					btnShowPotential.setSelected( false );
					control.showPotential( CellInformationDisplay.NO_POTENTIAL );
					PropertyContainer.getInstance().set( "settings.gui.visualization.floorInformation", 0 );
				} else {
					btnShowPotential.setSelected( true );
					btnShowDynamicPotential.setSelected( false );
					btnShowUtilization.setSelected( false );
					btnShowWaiting.setSelected( false );
					PropertyContainer.getInstance().set( "settings.gui.visualization.floorInformation", 1 );
					visualizationView.unselectPotentialSelector();
					control.activateMergedPotential();
					control.showPotential( CellInformationDisplay.STATIC_POTENTIAL );
				}
				visualizationView.getGLContainer().repaint();
			} else if( e.getActionCommand().equals( "dynamic" ) ) {
				if( btnShowDynamicPotential.isSelected() ) {
					btnShowDynamicPotential.setSelected( false );
					control.showPotential( CellInformationDisplay.NO_POTENTIAL );
					PropertyContainer.getInstance().set( "settings.gui.visualization.floorInformation", 0 );
				} else {
					btnShowDynamicPotential.setSelected( true );
					btnShowPotential.setSelected( false );
					btnShowUtilization.setSelected( false );
					btnShowWaiting.setSelected( false );
					PropertyContainer.getInstance().set( "settings.gui.visualization.floorInformation", 2 );
					visualizationView.unselectPotentialSelector();
					control.showPotential( CellInformationDisplay.DYNAMIC_POTENTIAL );
				}
				visualizationView.getGLContainer().repaint();
			} else if( e.getActionCommand().equals( "utilization" ) ) {
				if( btnShowUtilization.isSelected() ) {
					btnShowUtilization.setSelected( false );
					PropertyContainer.getInstance().set( "settings.gui.visualization.floorInformation", 0 );
					control.showPotential( CellInformationDisplay.NO_POTENTIAL );
				} else {
					btnShowUtilization.setSelected( true );
					btnShowDynamicPotential.setSelected( false );
					btnShowPotential.setSelected( false );
					btnShowWaiting.setSelected( false );
					PropertyContainer.getInstance().set( "settings.gui.visualization.floorInformation", 3 );
					visualizationView.unselectPotentialSelector();
					control.showPotential( CellInformationDisplay.UTILIZATION );
				}
				visualizationView.getGLContainer().repaint();
			} else if( e.getActionCommand().equals( "waiting" ) ) {
				if( btnShowWaiting.isSelected() ) {
					btnShowWaiting.setSelected( false );
					PropertyContainer.getInstance().set( "settings.gui.visualization.floorInformation", 0 );
					control.showPotential( CellInformationDisplay.NO_POTENTIAL );
				} else {
					btnShowWaiting.setSelected( true );
					btnShowUtilization.setSelected( false );
					btnShowDynamicPotential.setSelected( false );
					btnShowPotential.setSelected( false );
					PropertyContainer.getInstance().set( "settings.gui.visualization.floorInformation", 4 );
					visualizationView.unselectPotentialSelector();
					control.showPotential( CellInformationDisplay.WAITING );
				}
				visualizationView.getGLContainer().repaint();
			} else
				ZETMain.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
		}
	};
	ActionListener aclVisualizationView = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			if( e.getActionCommand().equals( "2d3dSwitch" ) ) {
				btn2d3dSwitch.setSelected( !btn2d3dSwitch.isSelected() );
				btn2dSwitch.setEnabled( !btn2dSwitch.isEnabled() );
				PropertyContainer.getInstance().set( "settings.gui.visualization.2d", !btn2d3dSwitch.isSelected() );
				visualizationView.getGLContainer().toggleView();
				visualizationView.getGLContainer().repaint();
			} else if( e.getActionCommand().equals( "2dSwitch" ) ) {
				btn2dSwitch.setSelected( !btn2dSwitch.isSelected() );
				PropertyContainer.getInstance().set( "settings.gui.visualization.isometric", btn2dSwitch.isSelected() );
				if( visualizationView.getGLContainer().getParallelViewMode() == AbstractVisualization.ParallelViewMode.Orthogonal )
					visualizationView.getGLContainer().setParallelViewMode( AbstractVisualization.ParallelViewMode.Isometric );
				else
					visualizationView.getGLContainer().setParallelViewMode( AbstractVisualization.ParallelViewMode.Orthogonal );
				visualizationView.getGLContainer().repaint();
			}

		}
	};
	ActionListener aclZoom = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			try {
				if( e.getActionCommand().equals( "zoomIn" ) )
					setZoomFactor( Math.min( 0.4, CoordinateTools.getZoomFactor() * 2 ) );
				else if( e.getActionCommand().equals( "zoomOut" ) )
					setZoomFactor( Math.max( 0.00004, CoordinateTools.getZoomFactor() / 2 ) );
				else
					ZETMain.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
			} catch( Exception ex ) {
				JOptionPane.showMessageDialog( getInstance(),
								ex.getLocalizedMessage(), loc.getString( "gui.Error" ),
								JOptionPane.ERROR_MESSAGE );
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
			else
				ZETMain.sendError( "Unknown tab index:" + tabPane.getSelectedIndex() + ". " + loc.getString( "gui.ContactDeveloper" ) );
		}
	};
	KeyListener kylZoom = new KeyListener() {
		public void keyTyped( KeyEvent e ) {
		}

		public void keyPressed( KeyEvent e ) {
		}

		public void keyReleased( KeyEvent e ) {
			if( e.getKeyCode() != KeyEvent.VK_ENTER )
				return;
			updateZoomFactor();
		}
	};
	PopupMenuListener pmlEditMode = new PopupMenuListener() {
		private EditMode lastEditMode = null;

		public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
		}

		public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
			EditMode currentEditMode = (EditMode)editSelector.getSelectedItem();


			// Einblenden der gewählten Area, falls ausgeblendet
			switch( currentEditMode ) {
				case AssignmentAreaCreation:
				case AssignmentAreaCreationPointwise:
					showArea( AreaVisibility.ASSIGNMENT );
					break;
				case DelayAreaCreation:
				case DelayAreaCreationPointwise:
					showArea( AreaVisibility.DELAY );
					break;
				case StairAreaCreation:
				case StairAreaCreationPointwise:
					showArea( AreaVisibility.STAIR );
					break;
				case EvacuationAreaCreation:
				case EvacuationAreaCreationPointwise:
					showArea( AreaVisibility.EVACUATION );
					break;
				case InaccessibleAreaCreation:
				case InaccessibleAreaCreationPointwise:
					showArea( AreaVisibility.INACCESSIBLE );
					break;
				case SaveAreaCreation:
				case SaveAreaCreationPointwise:
					showArea( AreaVisibility.SAVE );
					break;
				default:
					break;
			}
			updateAreaVisiblity();
			if( editView != null && lastEditMode == currentEditMode ) {
				editView.setEditMode( currentEditMode );
				btnEditSelect.setSelected( false );
				btnEditPointwise.setSelected( creationType == EditMode.Type.CREATION_POINTWISE );
				btnEditRectangled.setSelected( creationType == EditMode.Type.CREATION_RECTANGLED );

			}
			lastEditMode = currentEditMode;
		}

		public void popupMenuCanceled( PopupMenuEvent e ) {
		}
	};

	/*****************************************************************************
	 *                                                                           *
	 * Algorithm starter methods                                                 *
	 *                                                                           *
	 ****************************************************************************/

	/**
	 * Adds a {@link BatchProjectEntry} that can be loaded from a batch task file
	 * into the batch view.
	 * @param batchProjectEntry
	 */
	void addBatchEntry( BatchProjectEntry batchProjectEntry ) {
		batchView.add( batchProjectEntry );
	}

	private void buildVisualizationDataStructure( BatchResultEntry e, int nrOfCycle ) {
		CAVisualizationResults caRes = e.getCaVis() != null ? e.getCaVis()[nrOfCycle] : null;
		CAStatistic caStatistic = e.getCaStatistics() != null ? e.getCaStatistics()[nrOfCycle] : null;

		GraphVisualizationResult graphRes = e.getGraphVis();

		VisualizationDataStructureTask visualizationDataStructure = new VisualizationDataStructureTask( caRes, graphRes, e.getBuildingResults(), caStatistic );
		JProgressBarDialog pbd = new JProgressBarDialog( getInstance(), loc.getStringWithoutPrefix( "batch.tasks.buildVisualizationDatastructure" ), true, visualizationDataStructure );
		pbd.executeTask();
		pbd.setVisible( true );
		ZETMain.sendMessage( loc.getStringWithoutPrefix( "batch.tasks.progress.visualizationDatastructureComplete" ) );
		control = visualizationDataStructure.getControl();

		visualizationView.getGLContainer().setControl( control );

		btnShowCellularAutomaton.setEnabled( control.hasCellularAutomaton() );
		btnShowGraph.setEnabled( control.hasGraph() );
		btnShowGraphGrid.setEnabled( control.hasGraph() );

		//control.showCellularWa( btnShowCellularAutomaton.isSelected() );
		//control.showCellularAutomaton( btnShowCellularAutomaton.isSelected() );
		//control.showGraph( btnShowGraph.isSelected() );
//		control.showFloor( visualizationView.getSelectedFloorID() );
		visualizationView.updateFloorSelector();
		visualizationView.updatePotentialSelector();
	}

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
			CARealTime caRealTime = (CARealTime)caAlgo;
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
		createBackup( getZControl().getProject().getProjectFile() );
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
				//Exceptions werfen, die jenige von 'out' geworfen wird.
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
		if( result != null && JOptionPane.showConfirmDialog( this, "Alte Ergebnisse werden " +
						"hiermit überschrieben. Wollen Sie fortfahren?", "Überschreiben",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE ) != JOptionPane.YES_OPTION )
			return;

		this.result = r;

		caStatisticView.setResult( result );
		entryModelGraph.rebuild( result );
		entryModelVis.rebuild( result );

		mnuFileSaveResultAs.setEnabled( result != null );
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

	/**
	 * Enables and disables the menu item that moves a floor up.
	 * @param enabled the enabled status
	 */
	public void enableMenuFloorUp( boolean enabled ) {
		mnuEditFloorUp.setEnabled( enabled );
	}

	/**
	 * Enables and disables the menu item that moves a floor down.
	 * @param enabled the enabled status
	 */
	public void enableMenuFloorDown( boolean enabled ) {
		mnuEditFloorDown.setEnabled( enabled );
	}

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

	/**
	 * Sets the Zoom factor on the currently shown shown JFloor.
	 * @param zoomFactor the zoom factor
	 */
	public void setZoomFactor( double zoomFactor ) {
		double zoomChange = zoomFactor / CoordinateTools.getZoomFactor();
		Rectangle oldView = new Rectangle( editView.getLeftPanel().getViewport().getViewRect() );
		oldView.x *= zoomChange;
		oldView.y *= zoomChange;
		if( zoomChange > 1 ) {
			// If we are zooming in, then we have to move our window to the "middle"
			// of what the user previously saw. Right now we are in the upper left edge
			// of what he previously saw, and now we are doing this "move"
			int widthIncrement = (int)(oldView.width * zoomChange) - oldView.width;
			int heightIncrement = (int)(oldView.height * zoomChange) - oldView.height;

			oldView.x += widthIncrement / 2;
			oldView.y += heightIncrement / 2;
		}

		CoordinateTools.setZoomFactor( zoomFactor );
		editView.getLeftPanel().setZoomFactor( zoomFactor );
		editView.getFloor().getPlanImage().update();
		editView.updateFloorView();
		if( worker != null ) {
			caView.getLeftPanel().setZoomFactor( zoomFactor );
			caView.updateFloorView();
		}

		// Pretend a smaller Zoom factor, so that the user is actually only zomming 
		// in the range [0,0.25]. This is still close enough for the user.
		txtZoomFactor.setText( nfZoom.format( zoomFactor * 2.5d ) );

		//Redisplay the same portion of the Floor as before (move scrollbars)
		editView.getLeftPanel().getViewport().setViewPosition( oldView.getLocation() );
	}

	public static void printException( Exception ex ) {
		System.out.println( "Eine Exception trat auf:" );
		System.out.println( "Message: " + ex.getMessage() );
		System.out.println( "Localized: " + ex.getLocalizedMessage() );
		System.out.println( "Cause: " + ex.getCause() );
		ex.printStackTrace();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ex.printStackTrace( new PrintStream( bos ) );
		JOptionPane.showMessageDialog( null, bos.toString(), "Error", JOptionPane.ERROR_MESSAGE );
	}

	/**
	 * Displays a specified type of areas. The selection parameter of the
	 * menu entry is set correct, too.
	 * @param areaType the are type
	 */
	public void showArea( AreaVisibility areaType ) {
		switch( areaType ) {
			case DELAY:
				mnuDelayArea.setSelected( true );
				break;
			case STAIR:
				mnuStairArea.setSelected( true );
				break;
			case EVACUATION:
				mnuEvacuationArea.setSelected( true );
				break;
			case INACCESSIBLE:
				mnuInaccessibleArea.setSelected( true );
				break;
			case SAVE:
				mnuSaveArea.setSelected( true );
				break;
			case ASSIGNMENT:
				mnuAssignmentArea.setSelected( true );
				break;
			default:
				showErrorMessage( "Error", "Dieser Area-Typ wird nicht unterstützt." );
		}
		updateAreaVisiblity();
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
	 * toolbar elements.
	 * @param tabID the tab id as specified in {@link JEditor}.
	 */
	private void switchTo( int tabID ) {
		if( tabID == CA_FLOOR && worker == null ) {
			ZETMain.sendError( loc.getStringWithoutPrefix( "gui.error.StartSimulation" ) );
			tabPane.setSelectedIndex( currentMode );
			return;
		}
		// TODO better implementation of this stuff for debug mode ?
		if( ((ZETMain.isDebug() && tabID > CA_FLOOR) || (!ZETMain.isDebug() && tabID > BATCH)) && result == null && tabID != LOG ) {
			ZETMain.sendError( loc.getStringWithoutPrefix( "gui.error.CreateBatch" ) );
			tabPane.setSelectedIndex( currentMode );
			return;
		}

		currentMode = tabID;
		// code using the switch-bar is disabled!
		if( visualizationView.getGLContainer().isAnimating() ) {
			btnPlay.setIcon( playIcon );
			visualizationView.getGLContainer().stopAnimation();
		}
		if( tabID == EDIT_FLOOR )
			showToolBar( toolBarEdit );
		else if( tabID == BATCH )
			showToolBar( toolBarBatch );
		else if( tabID == CA_FLOOR )
			showToolBar( toolBarCA );
		else if( tabID == VISUALIZATION ) {
			showToolBar( toolBarVisualization );
			visualizationView.requestFocusInWindow();
		} else if( tabID == STATISTIC )
			showToolBar( toolBarCAStats );
		else if( tabID == GRAPH_STATISTIC )
			showToolBar( toolBarGraphStats );
		else if( tabID == LOG )
			showToolBar( toolBarLog );
		else
			ZETMain.sendError( "Unbekannte TabID: " + Integer.toString( tabID ) + ". " + loc.getString( "gui.ContactDeveloper" ) );
		repaint();
		validate();
	}

	/**
	 * Hides and unhides the areas in the plan depending on the status of the
	 * associated menu entries. The menu entries to hide and show all areas
	 * are updated and, if neccessery, disabled or enabled.
	 */
	private void updateAreaVisiblity() {
		ArrayList<AreaVisibility> mode = new ArrayList<AreaVisibility>();
		if( mnuDelayArea.isSelected() )
			mode.add( AreaVisibility.DELAY );
		if( mnuStairArea.isSelected() )
			mode.add( AreaVisibility.STAIR );
		if( mnuEvacuationArea.isSelected() )
			mode.add( AreaVisibility.EVACUATION );
		if( mnuInaccessibleArea.isSelected() )
			mode.add( AreaVisibility.INACCESSIBLE );
		if( mnuSaveArea.isSelected() )
			mode.add( AreaVisibility.SAVE );
		if( mnuAssignmentArea.isSelected() )
			mode.add( AreaVisibility.ASSIGNMENT );
		mnuShowAllAreas.setEnabled( mode.size() != 6 );
		mnuHideAllAreas.setEnabled( mode.size() != 0 );
		editView.changeAreaView( mode );
	}

	/**
	 * Reads the current value of the Zoomfactor textfield and sets the
	 * zoomfactor. If the last character is '%' it is removed. It is possible to
	 * insert real and integer values.
	 */
	private void updateZoomFactor() {
		NumberFormat nf = NumberFormat.getNumberInstance( loc.getLocale() );
		String text = txtZoomFactor.getText();
		char c = text.charAt( text.length() - 1 );
		boolean percent = false;
		if( c == '%' ) {
			StringBuffer sb = new StringBuffer( text ).delete( text.length() - 1, text.length() );
			text = sb.toString();
			percent = true;
		}
		try {
			double val = nf.parse( text ).doubleValue();
			if( val < 1 && percent == false )
				val = val * 100;
			val = val / 2.5d;
			setZoomFactor( val / 100 );
		} catch( ParseException ex2 ) {
			ZETMain.sendError( loc.getString( "gui.error.NonParsableNumber" ) );
		} catch( IllegalArgumentException ex ) {
			ZETMain.sendError( loc.getString( ex.getLocalizedMessage() ) );
		}
	}

	public void disableProjectUpdate( boolean disableUpdate ) {
		System.out.println( "update wird auf " + disableUpdate + "gesetzt" );
		this.disableUpdate = disableUpdate;
	}

	public boolean isUpdateDisabled() {
		return this.disableUpdate;
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

	public final ZControl getZControl() {
		return zcontrol;
	}

	/**
	 * Returns a {@link FileFilter} that allows loading zet format files with ending ".zet"
	 * @return a {@link FileFilter} that allows loading zet format files
	 */
	public static FileFilter getProjectFilter() {
		return new FileFilter() {
			@Override
			public boolean accept( File f ) {
				return f.isDirectory() || f.getName().toLowerCase().endsWith( ".zet" );
			}

			@Override
			public String getDescription() {
				return "Evakuierungsprojekte";
			}
		};
	}

	/**
	 * Returns a {@link FileFilter} that allows loading zet result files with ending ".ers"
	 * @return a {@link FileFilter} that allows loading zet result files
	 */
	public static FileFilter getResultsFilter() {
		return new FileFilter() {
			@Override
			public boolean accept( File f ) {
				return f.isDirectory() || f.getName().toLowerCase().endsWith( ".ers" );
			}

			@Override
			public String getDescription() {
				return "Evakuierungsprojekte";
			}
		};
	}

	/*****************************************************************************
	 *                                                                           *
	 * Models and renderer for control elements.                                 *
	 *                                                                           *
	 ****************************************************************************/
	/** This class serves as a model for the JComboBox that contains the EditModes. */
	private class EditComboBoxModel extends DefaultComboBoxModel {
		/**
		 * Creates a new combo box model containing edit types that are of a
		 * specified type.
		 * @param type the type of the displayed edit modes
		 */
		public EditComboBoxModel() {
			rebuild();
		}

		public void rebuild() {
			// In case that the creationType really changed we must restore the partner edit mode.
			// If we change to the same creation type as before, we must restore the old selection itself.
			boolean restore_partner = getSelectedItem() != null &&
							creationType != ((EditMode)getSelectedItem()).getType();
			EditMode next_selection = restore_partner ? ((EditMode)getSelectedItem()).getPartnerMode() : (EditMode)getSelectedItem();

			// Build new edit mode list
			this.removeAllElements();
			for( EditMode e : EditMode.getCreationModes( creationType ) )
				addElement( e );

			// Restore the selection with the associated partner editmode if neccessary
			if( next_selection != null )
				setSelectedItem( next_selection );
		}

		// This was moved to a change listener too, to be able to capture selections that
		// dont change the selected value, but only re-select it. In any other case this part
		// of the code is used (that is also needed, because the popuplistener can't deal with
		// other selection types)
		@Override
		public void setSelectedItem( Object object ) {
			super.setSelectedItem( object );
			if( editView != null ) {
				editView.setEditMode( (EditMode)object );

				btnEditSelect.setSelected( false );
				btnEditPointwise.setSelected( creationType == EditMode.Type.CREATION_POINTWISE );
				btnEditRectangled.setSelected( creationType == EditMode.Type.CREATION_RECTANGLED );
			}
		}
	}

	/** This class can display EditMode Objects in a JComboBox. */
	private class EditComboBoxRenderer extends ComboBoxRenderer {
		@Override
		public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
			JLabel me = (JLabel)super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
			setHorizontalAlignment( LEFT );

			if( value != null ) {
				EditMode e = (EditMode)value;
				if( e.getEditorColor() != null &&
								!e.getEditorColor().equals( Color.BLACK ) )
					setBackground( e.getEditorColor() );
				setText( e.toString() );
			}
			return this;
		}
	}

	/** This class serves as a model for the JComboBox that contains the 
	 * BatchResultEntries for the Visualization Tab. */
	private class BatchResultEntryVisComboBoxModel extends DefaultComboBoxModel {
		BatchResult result;

		public void rebuild( BatchResult result ) {
			this.result = result;

			removeAllElements();
			int index = 0;
			for( String e : result.getEntryNames() )
				super.addElement( new NamedIndex( e, index++ ) );
		}

		@Override
		public void setSelectedItem( Object object ) {
			super.setSelectedItem( object );

			BatchResultEntry entry = (BatchResultEntry)getSelectedItem();
			if( visualizationView != null && visualizationView.getGLContainer().isAnimating() ) {
				btnPlay.setIcon( playIcon );
				visualizationView.getGLContainer().stopAnimation();
			}
			if( cycleModel != null )
				cycleModel.rebuild( entry );
		}

		@Override
		public Object getSelectedItem() {
			try {
				if( result != null && super.getSelectedItem() != null )
					return result.getResult( ((NamedIndex)super.getSelectedItem()).getIndex() );
				else
					return null;
			} catch( IOException ex ) {
				ZETMain.sendError( "Error while loading temp file: " + ex.getLocalizedMessage() );
				return null;
			}
		}
	}

	/** This class serves as a model for the JComboBox that contains the 
	 * BatchResultEntries for the Graph Stats Tab. */
	private class BatchResultEntryGRSComboBoxModel extends DefaultComboBoxModel {
		BatchResult result;

		public void rebuild( BatchResult result ) {
			this.result = result;

			removeAllElements();
			int index = 0;
			for( String e : result.getEntryNames() )
				super.addElement( new NamedIndex( e, index++ ) );
		}

		@Override
		public void setSelectedItem( Object object ) {
			super.setSelectedItem( object );

			BatchResultEntry entry = (BatchResultEntry)getSelectedItem();
			Controller.getInstance().setFlow( entry.getGraph(), entry.getFlow() );
		}

		@Override
		public Object getSelectedItem() {
			try {
				if( result != null && super.getSelectedItem() != null )
					return result.getResult( ((NamedIndex)super.getSelectedItem()).getIndex() );
				else
					return null;
			} catch( IOException ex ) {
				ZETMain.sendError( "Error while loading temp file: " + ex.getLocalizedMessage() );
				return null;
			}
		}
	}

	/** This class serves as a model for the JComboBox that contains the Cycles. */
	private class CycleComboBoxModel extends DefaultComboBoxModel {
		public void rebuild( BatchResultEntry e ) {
			int oldSize = getSize();

			removeAllElements();
			if( e != null ) {
				if( e.getCaVis() != null )
					for( int i = 0; i < e.getCaVis().length; i++ )
						addElement( new Integer( i ) ); // else { the box stays empty }
				fireIntervalAdded( this, 0, getSize() );
			} else
				fireIntervalRemoved( this, 0, oldSize );
		}

		@Override
		public void setSelectedItem( Object object ) {
			super.setSelectedItem( object );

			if( visualizationView.getGLContainer().isAnimating() ) {
				btnPlay.setIcon( playIcon );
				visualizationView.getGLContainer().stopAnimation();
			}
			buildVisualizationDataStructure( (BatchResultEntry)entryModelVis.getSelectedItem(),
							((Integer)object).intValue() );
		}
	}

	/** This class can display EditMode Objects in a JComboBox. */
	private class CycleComboBoxRenderer extends ComboBoxRenderer {
		@Override
		public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected,
						boolean cellHasFocus ) {
			JLabel me = (JLabel)super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
			setHorizontalAlignment( RIGHT );

			if( value != null ) {
				BatchResultEntry currentEntry = (BatchResultEntry)entryModelVis.getSelectedItem();
				Integer number = (Integer)value;

				try {
					setText( Integer.toString( number + 1 ) );
				} catch( java.lang.ClassCastException e ) {
					setText( (String)value );
				}

				// Paint Medians with inverted colors
				if( currentEntry != null && (number == currentEntry.getMedianIndex()) ) {
					Color foreground = getForeground();
					Color background = getBackground();
					setBackground( foreground );
					setForeground( background );
				}
			}

			return this;
		}
	}
}
