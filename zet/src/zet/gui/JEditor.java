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
import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import batch.Batch;
import batch.BatchResult;
import batch.BatchResultEntry;
import batch.CellularAutomatonAlgorithm;
import batch.GraphAlgorithm;
import batch.load.BatchProjectEntry;
import converter.ZToCAConverter;
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
import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.common.localization.Localized;
import de.tu_berlin.math.coga.common.util.IOTools;
import io.movie.MovieManager;
import io.visualization.CAVisualizationResults;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.NumberFormat;
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
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import statistic.ca.CAStatistic;
import statistic.graph.Controller;
import batch.tasks.AlgorithmTask;
import batch.tasks.BatchGraphCreateOnlyTask;
import batch.tasks.CARealTime;
import batch.tasks.RasterizeTask;
import batch.tasks.VisualizationDataStructureTask;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.z.Assignment;
import ds.z.AssignmentArea;
import ds.z.ConcreteAssignment;
import ds.z.EvacuationArea;
import ds.z.Room;
import ds.z.ZControl;
import event.VisualizationEvent;
import gui.components.JLogPane;
import gui.statistic.JStatisticsPanel;
import zet.gui.components.toolbar.JEditToolbar;
import gui.visualization.Visualization.RecordingMode;
import io.visualization.BuildingResults;
import de.tu_berlin.math.coga.zet.DatFileReaderWriter;
import gui.Control;
import gui.CreditsDialog;
import gui.ZETMain;
import gui.ZETProperties;
import zet.gui.components.JZETMenuBar;
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
	/** The delimter used if numbers are stored in a tuple. */
	final static String delimiter = Localization.getInstance().getStringWithoutPrefix( "numberSeparator" );
	/** Singleton instance variable. */
	private static JEditor instance = null;

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
	private JToolBar toolBarBatch;
	private JToolBar toolBarCA;
	private JVisualizationToolbar toolBarVisualization;
	private JToolBar toolBarCAStats;
	private JToolBar toolBarGraphStats;
	/** The tool bar that is visible if the log view is active. */
	private JToolBar toolBarLog;
	private JToolBar currentToolbar;
	// Toolbar items
	// Batch toolbar
	private JButton btnExit5;
	private JButton btnSaveResults1;
	private JButton btnOpenResults1;
	// Quick visualization toolbar
	private JButton btnExit6;
	// Visualization toolbar
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
	private boolean disableUpdate = false;
	private int currentMode = EDIT_FLOOR;
	private JAssignment distribution;

	/** Decides whether the visualization should be restarted if 'play' is pressed. */
	private boolean restartVisualization = false;
	/** Decides whether visualization runs in loop-mode, that means it automatically starts again. */
	private boolean loop = false;

	private Control guiControl;

	/**
	 * Creates a new instance of <code>JEditor</code>.
	 * @param p the currentProject to display
	 * @param title the window title
	 * @param width the width of the window
	 * @param height the height of the window
	 */
	public JEditor( Control control ) {
		super();

		this.guiControl = control;
		control.editor = this;
		instance = this;

		loc.setLocale( Locale.getDefault() );

		nfZoom.setMaximumFractionDigits( 2 );

		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setSize( 800, 600 );
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation( (d.width - getSize().width) / 2, (d.height - getSize().height) / 2 );
		//createMenuBar();
		setJMenuBar( new JZETMenuBar( guiControl ) );

		getContentPane().setLayout( new BorderLayout() );

		statusBar = new JEventStatusBar();
		add( statusBar, BorderLayout.SOUTH );

		//createEditToolBar();
		toolBarEdit = new JEditToolbar( control );
		createBatchToolBar();
		createQuickVisualizationToolBar();
		toolBarVisualization = new JVisualizationToolbar( control );
		//createVisualizationToolBar();
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
	 * Loads the project currently controlled by the project controller. Resets
	 * the view to edit window and resets the zoom factor to 10%
	 */
	public void loadProject() {
		//zcontrol = projectControl.getZControl();

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

	public static JEditor getInstance() {
		return instance;
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
		editView = new JEditView( guiControl );
		batchView = new JBatchView();
		visualizationView = new JVisualizationView( new GLCapabilities() );
		visualizationView.getGLContainer().setControl( new GLControl() );
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
				// TODO das hier in die control klasse verschieben
//				btnShowPotential.setSelected( false );
//				btnShowDynamicPotential.setSelected( false );
//				btnShowUtilization.setSelected( false );
//				btnShowWaiting.setSelected( false );
			}
		} );
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

		tabPane.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				if( tabPane.getSelectedIndex() == LOG ) {
					logView.update();
				}
			}
		} );

		ZETMain.sendMessage( loc.getString( "gui.status.EditorInitialized" ) );
	}

	public void resetAssignment() {
		distribution = null;
	}

	public void showAssignmentDialog() {
		//if( distribution == null ) {
		distribution = new JAssignment( instance, zcontrol.getProject(), loc.getString( "gui.editor.assignment.JAssignment.Title" ), 850, 400 );
		//}
		distribution.setVisible( true );
		distribution.dispose();
	}

	/**
	 * Creates the <code>JToolBar</code> for the statistic view.
	 */
	private void createStatisticsToolBar() {
		loc.setPrefix( "gui.editor.JEditor." );
		/** ########## CA Statistics tool bar ############ */
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

		/** ########## Graph Statistics tool bar ############ */
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
		/** ########## Log View Statistics tool bar ############ */
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
	 * Changes the appearance of the GUI to the selected language.
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
/**/			Menu.updateMenu( mnuCreateCA, loc.getString( "menuSimulationCreateCA" ) );
/**/			Menu.updateMenu( mnuApplyAssignment, loc.getString( "menuSimulationApplyConcreteAssignment" ) );
			Menu.updateMenu( mnuStartSimulation, loc.getString( "menuSimulationStart" ) );
			//TODO if paused use other
			if( caAlgo != null )
				if( caAlgo.isPaused() )
/**/					Menu.updateMenu( mnuPauseSimulation, loc.getString( "menuSimulationContinue" ) );
				else
/**/					Menu.updateMenu( mnuPauseSimulation, loc.getString( "menuSimulationPause" ) );
			else
/**/				Menu.updateMenu( mnuPauseSimulation, loc.getString( "menuSimulationPause" ) );
			Menu.updateMenu( mnuStepByStepSimulation, loc.getString( "menuSimulationStepByStep" ) );
			Menu.updateMenu( mnuQuickVisualization, loc.getString( "menuSimulationQuickVisualization" ) );
			Menu.updateMenu( mOptimization, loc.getString( "menuOptimization" ) );
/**/			Menu.updateMenu( mnuCreateGraph, loc.getString( "menuOptimizationCreateGraph" ) );
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

		if( ZETMain.isDebug() ) {
			tabPane.setTitleAt( STATISTICS, loc.getString( "Statistics" ) );
			tabPane.setToolTipTextAt( STATISTICS, loc.getString( "StatisticsToolTip" ) );
		}
		loc.setPrefix( "" );


		// Tool tips for buttons and text views in the menu bar
		loc.setPrefix( "gui.editor.JEditor." );

		toolBarEdit.localize();

		toolBarVisualization.localize();

		labelBatchName2.setText( loc.getString( "batchName" ) );

		btnExit3.setToolTipText( loc.getString( "toolbarTooltipExit" ) );
		btnExit4.setToolTipText( loc.getString( "toolbarTooltipExit" ) );
		btnExit5.setToolTipText( loc.getString( "toolbarTooltipExit" ) );
		btnExit6.setToolTipText( loc.getString( "toolbarTooltipExit" ) );
		btnExit7.setToolTipText( loc.getString( "toolbarTooltipExit" ) );
		btnOpenResults1.setToolTipText( loc.getString( "toolbarTooltipOpen" ) );
		btnOpenResults2.setToolTipText( loc.getString( "toolbarTooltipOpen" ) );
		btnOpenResults3.setToolTipText( loc.getString( "toolbarTooltipOpen" ) );
		btnSaveResults1.setToolTipText( loc.getString( "toolbarTooltipSave" ) );
		btnSaveResults2.setToolTipText( loc.getString( "toolbarTooltipSave" ) );
		btnSaveResults3.setToolTipText( loc.getString( "toolbarTooltipSave" ) );
		


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
		}
	};
	ActionListener aclDebug = new ActionListener() {
 	@Override
		public void actionPerformed( ActionEvent e ) {
			if( e.getActionCommand().equals( "outputInformation" ) ) {
			} else if( e.getActionCommand().equals( "outputGraph" ) ) {
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

	/**
	 * Adds a {@link BatchProjectEntry} that can be loaded from a batch task file
	 * into the batch view.
	 * @param batchProjectEntry
	 */
	public void addBatchEntry( BatchProjectEntry batchProjectEntry ) {
		batchView.add( batchProjectEntry );
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
		if( result != null && JOptionPane.showConfirmDialog( this, "Alte Ergebnisse werden " +
						"hiermit überschrieben. Wollen Sie fortfahren?", "Überschreiben",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE ) != JOptionPane.YES_OPTION )
			return;

		this.result = r;

		caStatisticView.setResult( result );
		entryModelGraph.rebuild( result );
		//entryModelVis.rebuild( result );
		guiControl.rebuild( result );

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
	 * Displays a specified type of areas. The selection parameter of the
	 * menu entry is set correct, too.
	 * @param areaType the are type
	 */
	public void showArea( AreaVisibility areaType ) {
		switch( areaType ) {
			case Delay:
				mnuDelayArea.setSelected( true );
				break;
			case Stair:
				mnuStairArea.setSelected( true );
				break;
			case Evacuation:
				mnuEvacuationArea.setSelected( true );
				break;
			case Inaccessible:
				mnuInaccessibleArea.setSelected( true );
				break;
			case Save:
				mnuSaveArea.setSelected( true );
				break;
			case Assignment:
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
		else if( tabID == STATISTICS )
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
	public void updateAreaVisiblity() {
		ArrayList<AreaVisibility> mode = new ArrayList<AreaVisibility>();
		if( mnuDelayArea.isSelected() )
			mode.add( AreaVisibility.Delay );
		if( mnuStairArea.isSelected() )
			mode.add( AreaVisibility.Stair );
		if( mnuEvacuationArea.isSelected() )
			mode.add( AreaVisibility.Evacuation );
		if( mnuInaccessibleArea.isSelected() )
			mode.add( AreaVisibility.Inaccessible );
		if( mnuSaveArea.isSelected() )
			mode.add( AreaVisibility.Save );
		if( mnuAssignmentArea.isSelected() )
			mode.add( AreaVisibility.Assignment );
		mnuShowAllAreas.setEnabled( mode.size() != 6 );
		mnuHideAllAreas.setEnabled( mode.size() != 0 );
		editView.changeAreaView( mode );
	}

	public void disableProjectUpdate( boolean disableUpdate ) {
		System.out.println( "update wird auf " + disableUpdate + "gesetzt" );
		this.disableUpdate = disableUpdate;
	}

	public boolean isUpdateDisabled() {
		return this.disableUpdate;
	}


	/** This class serves as a model for the JComboBox that contains the
	 * BatchResultEntries for the Graph statistics Tab. */
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
			else {
				// TODO restartvisualization
//				this.restartVisualization = true;
//				btnPlay.setIcon( playIcon );
//				visualizationView.getGLContainer().stopAnimation();
//				btnPlay.setSelected( false );
				ZETMain.sendMessage( "Replaying visualization finished." );
			}
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
	public void setZControl( ZControl zcontrol ) {
		this.zcontrol = zcontrol;
		loadProject();
	}

	/**
	 * Returns the control class controlling the currently visible project.
	 * @return the control class controlling the currently visible project
	 */
	public final ZControl getZControl() {
		return zcontrol;
	}

}
