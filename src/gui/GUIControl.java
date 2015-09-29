
package gui;

import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import batch.BatchResult;
import batch.BatchResultEntry;
import batch.plugins.AlgorithmPlugin;
import batch.tasks.AlgorithmTask;
import batch.tasks.VisualizationDataStructureTask;
import org.zetool.common.algorithm.Algorithm;
import org.zetool.common.algorithm.AlgorithmEvent;
import org.zetool.common.algorithm.AlgorithmListener;
import org.zetool.common.algorithm.AlgorithmProgressEvent;
import org.zetool.common.algorithm.AlgorithmStartedEvent;
import org.zetool.common.debug.Debug;
import org.zetool.common.localization.LocalizationManager;
import org.zetool.common.util.IOTools;
import de.tu_berlin.math.coga.components.JVideoOptionsDialog;
import de.tu_berlin.math.coga.zet.converter.graph.GraphAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import ds.CompareVisualizationResults;
import ds.GraphVisualizationResults;
import de.zet_evakuierung.model.ProjectLoader;
import ds.PropertyContainer;
import ds.ca.evac.EvacuationCellularAutomaton;
import de.zet_evakuierung.model.AssignmentArea;
import de.zet_evakuierung.model.BuildingPlan;
import de.zet_evakuierung.model.ConcreteAssignment;
import de.zet_evakuierung.model.EvacuationArea;
import de.zet_evakuierung.model.Floor;
import de.zet_evakuierung.model.PlanPolygon;
import de.zet_evakuierung.model.Room;
import de.zet_evakuierung.model.ZControl;
import de.zet_evakuierung.model.exception.RoomEdgeInvalidTargetException;
import de.zet_evakuierung.model.exception.TooManyPeopleException;
import de.zet_evakuierung.template.Door;
import de.zet_evakuierung.template.ExitDoor;
import de.zet_evakuierung.template.TemplateLoader;
import de.zet_evakuierung.template.Templates;
import gui.components.progress.JProgressBarDialog;
import gui.components.progress.JRasterizeProgressBarDialog;
import org.zet.components.model.editor.CoordinateTools;
import gui.editor.flooredit.FloorImportDialog;
import gui.editor.planimage.JPlanImageProperties;
import gui.editor.properties.JPropertyDialog;
import gui.propertysheet.JOptionsDialog;
import gui.statistic.JGraphStatisticPanel;
import gui.statistic.JStatisticPanel;
import gui.statistic.JStatisticsPanel;
import gui.visualization.AbstractVisualization.ParallelViewMode;
import gui.visualization.Visualization.RecordingMode;
import gui.visualization.control.ZETGLControl;
import gui.visualization.control.ZETGLControl.CellInformationDisplay;
import io.DXFWriter;
import de.tu_berlin.coga.util.movies.MovieManager;
import org.zetool.components.batch.input.FileFormat;
import org.zetool.components.batch.input.InputDirectory;
import org.zetool.components.batch.input.InputFiles;
import org.zetool.components.batch.input.ProblemType;
import de.tu_berlin.math.coga.batch.input.reader.DimacsMaximumFlowFileReader;
import de.tu_berlin.math.coga.batch.input.reader.DimacsMinimumCostFlowFileReader;
import de.tu_berlin.math.coga.batch.input.reader.RMFGENMaximumFlowFileReader;
import de.tu_berlin.math.coga.batch.input.reader.ZETProjectFileReader;
import de.tu_berlin.math.coga.batch.operations.BasicOptimization;
import de.tu_berlin.math.coga.batch.operations.BasicSimulation;
import de.tu_berlin.math.coga.batch.operations.BestResponseOperation;
import de.tu_berlin.math.coga.batch.operations.ConversionOnly;
import de.tu_berlin.math.coga.batch.operations.ExitAssignmentOperation;
import de.tu_berlin.math.coga.batch.operations.MaximumFlowComputation;
import de.tu_berlin.math.coga.batch.output.OutputText;
import de.tu_berlin.math.coga.batch.output.OutputVisualization;
import de.tu_berlin.math.coga.batch.output.TikZOut;
import de.tu_berlin.math.coga.zet.converter.AssignmentConcrete;
import de.zet_evakuierung.model.AreaType;
import de.zet_evakuierung.model.FloorInterface;
import de.zet_evakuierung.model.ZModelRoomEvent;
import org.zetool.components.property.PropertyTreeModelWriter;
import event.EventServer;
import gui.editor.properties.ZETOptionsLocalization;
import gui.propertysheet.PropertyTreeNode;
import io.visualization.BuildingResults;
import io.visualization.EvacuationSimulationResults;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import org.xml.sax.SAXException;
import org.zetool.components.JLogPane;
import org.zetool.components.Localizer;
import org.zetool.components.batch.gui.JBatch;
import statistic.ca.CAStatistic;
import statistic.ca.MultipleCycleCAStatistic;
import zet.gui.GUILocalization;
import zet.gui.assignmentEditor.JAssignment;
import zet.gui.main.JZetWindow;
import zet.gui.main.menu.JZETMenuBar;
import org.zet.components.model.editor.floor.popup.PolygonPopup;
import org.zet.components.model.editor.editview.EditViewControl;
import org.zet.components.model.editor.editview.EditViewModel;
import org.zet.components.model.editor.editview.JEditView;
import zet.gui.main.tabs.JQuickVisualizationView;
import zet.gui.main.tabs.JVisualizationView;
import org.zet.components.model.editor.style.RasterPaintStyle;
import org.zet.components.model.editor.floor.EditMode;
import org.zet.components.model.editor.floor.SelectedFloorElements;
import org.zet.components.model.editor.panel.ChangeListener;
import zet.gui.main.tabs.visualization.ZETVisualization;
import zet.gui.main.toolbar.JBatchToolBar;
import zet.gui.main.toolbar.JEditToolbar;
import zet.gui.main.toolbar.JEditToolbar.ToolbarEvent;
import zet.gui.main.toolbar.JLogToolBar;
import zet.gui.main.toolbar.JQuickVisualizationToolBar;
import zet.gui.main.toolbar.JStatisticCellularAutomatonToolbar;
import zet.gui.main.toolbar.JStatisticGraphToolBar;
import zet.gui.main.toolbar.JVisualizationToolbar;
import zet.gui.main.toolbar.ZETIconSet;
import zet.tasks.CellularAutomatonAlgorithms;
import zet.tasks.RasterizeTask;

/**
 * The {@code GUIControl} class sets up and controls the main graphical user
 * interface of the ZET application.
 * This class receives commands and GUI changes from elements like tool bars,
 * menus etc. and delegates them to other classes.
 * @author Jan-Philipp Kappmeier
 */
public class GUIControl implements AlgorithmListener {


	/** The logger of the main class. */
	private static final Logger log = Logger.getGlobal();

	/** The editor. */
	public JZetWindow editor;

	/** The edit tool bar. */
	private JEditToolbar editToolBar;
	/** The visualization tool bar. */
	private JVisualizationToolbar visualizationToolBar;
	/** The graph statistic tool bar. */
	private JStatisticGraphToolBar graphStatisticToolBar;
	/** The menu bar. */
	private JZETMenuBar menuBar;
	private ZETVisualization visualization;

	private JEditView editview;
	private JQuickVisualizationView caView;
	private JVisualizationView visualizationView;
  private JStatisticPanel caStatisticView;
  
	private AlgorithmControl algorithmControl;
	private ArrayList<AreaType> mode = new ArrayList<>( Arrays.asList( AreaType.values() ) );
	private ZETGLControl control;
	private Templates<Door> doorTemplates = new Templates<>("empty");
	private Templates<ExitDoor> exitDoorTemplates = new Templates<>("empty");
	private PolygonPopup polygonPopup;
	//private EdgePopup edgePopup;
	//private PointPopup pointPopup;
	private SelectedFloorElements selection = new SelectedFloorElements();
    private EditViewControl evc;
	/**
	 *
	 */
	public GUIControl() { }

  @SuppressWarnings("rawtypes")
  public void createZETWindow() {
		zcontrol = new ZControl();
		algorithmControl = new AlgorithmControl( zcontrol.getProject() );

		// Main window
		editor = Localizer.instance().registerNewComponent( new JZetWindow( this, zcontrol ) );

		// Menu bar
		menuBar = Localizer.instance().registerNewComponent( new JZETMenuBar( this ) );
		editor.setJMenuBar( menuBar );

		// Popups
		//polygonPopup = new PolygonPopup( this );
		//edgePopup = new EdgePopup( this );
		//pointPopup = new PointPopup( this );


		//EditStatus editStatus = new EditStatus( zcontrol, selection );

		// Tool bars
		editToolBar = Localizer.instance().registerNewComponent( new JEditToolbar( this ) );
		//guiControl.setEditToolbar( toolBarEdit );
		JBatchToolBar toolBarBatch = Localizer.instance().registerNewComponent( new JBatchToolBar( this ) );
		JQuickVisualizationToolBar toolBarCellularAutomatonQuickVisualization = Localizer.instance().registerNewComponent( new JQuickVisualizationToolBar( this ) );
		JVisualizationToolbar toolBarVisualization = Localizer.instance().registerNewComponent( new JVisualizationToolbar( this ) );
		JStatisticCellularAutomatonToolbar toolBarCAStats = Localizer.instance().registerNewComponent( new JStatisticCellularAutomatonToolbar( this ) );
		JStatisticGraphToolBar toolBarGraphStats = Localizer.instance().registerNewComponent( new JStatisticGraphToolBar( this ) );
		JLogToolBar toolBarLog = Localizer.instance().registerNewComponent( new JLogToolBar( this ) );

		// Components in tabs
                evc = new EditViewControl(zcontrol, zcontrol.getProject().getBuildingPlan().getFloors());
                EventServer.getInstance().registerListener(evc.getView(), ZModelRoomEvent.class);
                JEditView editView = evc.getView();
                
                
                // Add listener to toolbar
                ChangeListener<JEditToolbar.ToolbarEvent> tcl = new ChangeListener<JEditToolbar.ToolbarEvent>() {

                    @Override
                    public void changed(ToolbarEvent c) {
                        switch(c.getChangeType()) {
                            case Selection:
                                evc.setEditMode(EditMode.SELECTION);
                                break;
                            case CreatePointwise:
                                evc.setEditMode(EditMode.CREATE_POINTWISE);
                                break;
                            case CreateRectangle:
                                evc.setEditMode(EditMode.CREATE_RECTANGLE);
                                break;
                            case SelectZetObjectType:
                                evc.setZetObjectType(editToolBar.getZetObjectType());
                                break;
                            default:
                                throw new AssertionError();
                        }
                    }
                };
                editToolBar.addChangeListener( tcl );
                
		editview = Localizer.instance().registerNewComponent(editView );
    selection.addObserver( editview );
    caView = Localizer.instance().registerNewComponent( new JQuickVisualizationView( this ) );
    JBatch batchView = new JBatch();
    // Initialize batchView

    final ProblemType EVACUATION_PROJECT = new ProblemType( "Evacuation Project", "Number of Floors, Number of Exits, Maximal Number of Evacuees" );
    final ProblemType MAXIMUM_FLOW = new ProblemType( "Maximum Flow Problem", "Number of Nodes, Number of Edges" );
    final ProblemType MINIMUM_COST_FLOW = new ProblemType( "Minimum Cost Flow Problem", "Number of Nodes, Number of Edges, Total Supply" );

    final FileFormat DIMACS_MAXIMUM_FLOW = new FileFormat( MAXIMUM_FLOW, DimacsMaximumFlowFileReader.class, "DIMACS Maximum Flow Problem", "max" );
    final FileFormat DIMACS_MINIMUM_COST_FLOW = new FileFormat( MINIMUM_COST_FLOW, DimacsMinimumCostFlowFileReader.class, "DIMACS Minimum Cost Flow Problem", "min", "net" );
    final FileFormat RMFGEN_MAXIMUM_FLOW = new FileFormat( MAXIMUM_FLOW, RMFGENMaximumFlowFileReader.class, "RMFGEN Maximum Flow Problem", "rmf" );
    final FileFormat ZET_PROJECT = new FileFormat( EVACUATION_PROJECT, ZETProjectFileReader.class, "ZET Evacuation Project", new ImageIcon( "./icons/zet_24.png" ), "zet" );

    batchView.registerFileFormat( DIMACS_MAXIMUM_FLOW );
    batchView.registerFileFormat( DIMACS_MINIMUM_COST_FLOW );
    batchView.registerFileFormat( RMFGEN_MAXIMUM_FLOW );
    batchView.registerFileFormat( ZET_PROJECT );
  
    batchView.registerInputAction( new ProjectInput( this ), "Add current project", new ImageIcon("./icons/box_24.png" ) );
    batchView.registerInputAction( new InputFiles( batchView, batchView ), "Add input file(s)", new ImageIcon("./icons/document_add_24.png" ) );
    batchView.registerInputAction( new InputDirectory( batchView, batchView ), "Add input directory", new ImageIcon("./icons/folder_add_24.png" ) );

    // Load plugins
    
    PluginManager pm = ZETMain.pm;
    
    PluginManagerUtil pmu = new PluginManagerUtil( pm );
    Collection<AlgorithmPlugin> plugins = pmu.getPlugins( AlgorithmPlugin.class);
    
    for( AlgorithmPlugin p : plugins ) {
      log.log(Level.INFO, "Found algorithm plugin: {0}", p.toString());
      batchView.registerAlgorithm( p );
    }
    
    batchView.registerOperationAction( new BasicOptimization(), "Basic Optimization" );
    batchView.registerOperationAction( new BasicSimulation(), "Simulation" );
    batchView.registerOperationAction( new ExitAssignmentOperation(), "Exit Assignment" );
    batchView.registerOperationAction( new BestResponseOperation(), "Best Response" );
    batchView.registerOperationAction( new MaximumFlowComputation(), "Max Flow Optimization" );
    batchView.registerOperationAction( new ConversionOnly(), "Conversion" );

    batchView.registerOutputAction( new OutputVisualization( this ), "Generate Visualization", new ImageIcon( "./icons/dropbox-icon-24.png" ) );
    batchView.registerOutputAction( new OutputText(), "Commandline Output", new ImageIcon( "./icons/document_24.png" ) );
    batchView.registerOutputAction( new TikZOut(), "TikZ Output", new ImageIcon( "./icons/document_24.png" ) );

		visualizationView = Localizer.instance().registerNewComponent( new JVisualizationView( this ) );
		caStatisticView = new JStatisticPanel();
		JComponent graphStatisticView = new JGraphStatisticPanel();
		JComponent logView = new JLogPane( ZETMain.gl );
		JComponent statisticView = new JStatisticsPanel();

		// add toolbars and components to window
		editor.addMode( "gui.tabs.Edit", "gui.tabs.EditToolTip", null, editview, editToolBar );
		editor.addMode( "gui.tabs.CAView", "gui.tabs.CAViewToolTip", null, caView, toolBarCellularAutomatonQuickVisualization);
		editor.addMode( "gui.tabs.Batch", "gui.tabs.BatchToolTip", null, batchView, toolBarBatch );
		editor.addMode( "gui.tabs.Visualization", "gui.tabs.VisualizationToolTip", null, visualizationView, toolBarVisualization );
		editor.addMode( "gui.tabs.Statistic", "gui.tabs.StatisticToolTip", null, caStatisticView, toolBarCAStats );
		editor.addMode( "gui.tabs.GraphStatistic", "gui.tabs.GraphStatisticToolTip", null, graphStatisticView, toolBarGraphStats );
		editor.addMode( "gui.tabs.LogWindow", "gui.tabs.LogWindowToolTip", null, logView, toolBarLog );
		editor.addMode( "gui.tabs.Statistics", "gui.tabs.StatisticsToolTip", null, statisticView, toolBarLog );

		visualization = visualizationView.getGLContainer();
		updateVisualizationElements();
		visualization.setZcontrol( zcontrol );
		loadProjectInternal();
	}

	public void loadTemplates() throws ParserConfigurationException, SAXException, IOException {
		TemplateLoader tl = new TemplateLoader();
		tl.parse( new File( GUIOptionManager.getDoorTemplateFile() ) );
		doorTemplates = tl.getDoors();
//		tl.parse( new File( GUIOptionManager.getExitDoorTemplateFile() ) );
//		exitDoorTemplates = tl.getExitDoors();
		exitDoorTemplates = new Templates<>("empty");
	}

	public void showZETWindow() {
		editor.setVisible( true );
	}

	/**
	 * Sets the Zoom factor on the currently shown shown JFloor.
	 * @param zoomFactor the zoom factor
	 */
	public void setZoomFactor( double zoomFactor ) {
		double zoomChange = zoomFactor / CoordinateTools.getZoomFactor();
		Rectangle oldView = new Rectangle( editview.getLeftPanel().getViewport().getViewRect() );
		oldView.x *= zoomChange;
		oldView.y *= zoomChange;
		if( zoomChange > 1 ) {
			// If we are zooming in, then we have to move our window to the "middle"
			// of what the user previously saw. Right now we are in the upper left edge
			// of what he previously saw, and now we are doing this "move"
			int widthIncrement = (int) (oldView.width * zoomChange) - oldView.width;
			int heightIncrement = (int) (oldView.height * zoomChange) - oldView.height;

			oldView.x += widthIncrement / 2;
			oldView.y += heightIncrement / 2;
		}

		// TODO give direct access to the left edit panel
		CoordinateTools.setZoomFactor( zoomFactor );
		editview.setZoomFactor( zoomFactor );
		//editview.getFloor().getPlanImage().update();
		//editview.updateFloorView();
//		if( worker != null ) {
//			 caView.getLeftPanel().setZoomFactor( zoomFactor );
//			caView.updateFloorView();
//		}

		//if( editToolBar != null )
		editToolBar.setZoomFactorText( zoomFactor );

		//Redisplay the same portion of the Floor as before (move scrollbars)
		editview.getLeftPanel().getViewport().setViewPosition( oldView.getLocation() );
	}

	/**
	 * Displays a specified type of areas. The selection parameter of the
	 * menu entry is set correct, too.
	 * @param areaType the are type
	 */
	public void showArea( AreaType areaType ) {
		updateVisibility( areaType, true );
		// TODO make them visible!
//		switch( areaType ) {
//			case Delay:
//				//TODO Menu
//				//editor.mnuDelayArea.setSelected( true );
//				break;
//			case Stair:
//				//editor.mnuStairArea.setSelected( true );
//				break;
//			case Evacuation:
//				//editor.mnuEvacuationArea.setSelected( true );
//				break;
//			case Inaccessible:
//				//editor.mnuInaccessibleArea.setSelected( true );
//				break;
//			case Save:
//				//editor.mnuSaveArea.setSelected( true );
//				break;
//			case Assignment:
//				//editor.mnuAssignmentArea.setSelected( true );
//				break;
//			case Teleport:
//				// TODO
//				break;
//			default:
//				JZetWindow.showErrorMessage("Error", "Dieser Area-Typ wird nicht unterstützt.");
//		}
		//editor.updateAreaVisiblity();
	}

	/**
	 * Exits the program.
	 */
	public void exit() {
		System.exit( 0 );
	}

	public void setVisualizationToolbar( JVisualizationToolbar toolbar ) {
		visualizationToolBar = toolbar;
	}

	public void setGraphStatisticToolBar( JStatisticGraphToolBar graphStatisticToolBar ) {
		this.graphStatisticToolBar = graphStatisticToolBar;
	}

	public void visualizationToggle2D3D() {
		PropertyContainer.getGlobal().toggle( "settings.gui.visualization.2d" );
		updateVisualizationElements();
	}

	public void visualizationToggle2D() {
		PropertyContainer.getGlobal().toggle( "settings.gui.visualization.isometric" );
		updateVisualizationElements();
	}

	public void updateVisualizationElements() {
		final boolean visualization3D = PropertyContainer.getGlobal().getAsBoolean( "settings.gui.visualization.2d" );
		final boolean visualization2D = PropertyContainer.getGlobal().getAsBoolean( "settings.gui.visualization.isometric" );
		visualizationToolBar.setSelected2d3d( visualization3D );
		visualizationToolBar.setEnabled2d( visualization2D );
		visualizationToolBar.setSelected2d( visualization2D );
		visualization.setParallelViewMode( visualization2D ? ParallelViewMode.Isometric : ParallelViewMode.Orthogonal );
		visualization.setView( !visualization3D );
		visualization.repaint();
	}

	// TODO move to visualization class (at least parts)
	public void createVideo() {
		if( visualization.isAnimating() )
			visualization.stopAnimation();
		String path = PropertyContainer.getGlobal().getAsString( "options.filehandling.moviePath" );
		JVideoOptionsDialog vo = new JVideoOptionsDialog( editor );
		// Setze die erwartete Laufzeit
		vo.setEstimatedTime( visualization.getControl().getEstimatedTime() );
		vo.setResolution( visualization.getSize() );
		vo.setBitrate( 1000 );
		vo.setFramerate( 24 );
		vo.setMoviePath( path );
		vo.setTextureFontStrings( visualization.getTexts() );
		vo.setVisible( true );
		vo.dispose();
		if( vo.getRetVal() == JOptionPane.OK_OPTION ) {
			visualization.setTexts( vo.getTextureFontStrings() );
			zcontrol.getProject().getVisualProperties().setTextureFontStrings( vo.getTextureFontStrings() );
			String movieFrameName = PropertyContainer.getGlobal().getAsString( "options.filehandling.movieFrameName" );
			// TODO BUG: wenn ein projekt noch nicht gespeichert worden ist, liefert das hier iene null pointer exception. (tritt auf, wenn ein video gedreht werden soll)
			String projectName = zcontrol.getProject().getProjectFile().getName().substring( 0, zcontrol.getProject().getProjectFile().getName().length() - 4 );
			MovieManager movieCreator = visualization.getMovieCreator();
			if( movieFrameName.isEmpty(  ) )
				movieCreator.setFramename( projectName );
			else
				movieCreator.setFramename( movieFrameName );
			path = vo.getMoviePath();
			PropertyContainer.getGlobal().set( "options.filehandling.moviePath", path );
			if( !(path.endsWith( "/" ) || path.endsWith( "\\" )) )
				path += "/";
			String movieFileName = IOTools.getNextFreeNumberedFilename( path, projectName, 3 );
			movieCreator.setFilename( movieFileName );
			movieCreator.setPath( PropertyContainer.getGlobal().getAsString( "options.filehandling.moviePath" ) );
			movieCreator.setFramename( PropertyContainer.getGlobal().getAsString( "options.filehandling.movieFrameName" ) );
			movieCreator.setMovieWriter( vo.getMovieWriter() );
			visualization.setRecording( RecordingMode.Recording, vo.getResolution() );
			movieCreator.setWidth( vo.getResolution().width );
			movieCreator.setHeight( vo.getResolution().height );
			movieCreator.setCreateMovie( vo.isMovieMode() );
			movieCreator.setDeleteFrames( vo.isDeleteFrames() );
			movieCreator.setMovieFormat( vo.getMovieFormat() );
			movieCreator.setFramerate( vo.getFramerate() );
			movieCreator.setBitrate( vo.getBitrate() );
			visualization.setMovieFramerate( vo.getFramerate() );
			movieCreator.setFrameFormat( vo.getFrameFormat() );
			visualizationToolBar.play();
			if( !visualization.isAnimating() )
				visualization.startAnimation();
		}
	}

	public void takeScreenshot() {
		String path = PropertyContainer.getGlobal().getAsString( "options.filehandling.screenshotPath" );
		if( !(path.endsWith( "/" ) || path.endsWith( "\\" )) )
			path += "/";
		String projectName;
		try {
			projectName = zcontrol.getProject().getProjectFile().getName().substring( 0, zcontrol.getProject().getProjectFile().getName().length() - 4 );
		} catch( NullPointerException ex ) {
			projectName = "untitled";
		}
		String newFilename = IOTools.getNextFreeNumberedFilepath( path, projectName, 3 ) + ".png";
		visualization.takeScreenshot( newFilename );

	}

	public void visualizationTurnBackToStart() {
		visualization.getControl().resetTime();
		visualizationToolBar.setPlayButtonEnabled( true );
		visualizationToolBar.pause();
		visualization.repaint();
	}

	public void visualizationPause() {
		if( visualization.isAnimating() ) {
			visualizationToolBar.pause();
			visualization.stopAnimation();
		}
	}

	public void visualizationPlay() {
		if( visualization.isAnimating() ) {
			visualizationToolBar.pause();
			visualization.stopAnimation();
			if( visualization.getRecording() == RecordingMode.Recording )
				visualization.setRecording( RecordingMode.SkipFrame );
		} else {
			visualizationToolBar.play();
			if( visualization.getRecording() == RecordingMode.SkipFrame )
				visualization.setRecording( RecordingMode.Recording );
			visualization.startAnimation();
		}
	}
	private boolean loop = false;

	public void visualizationLoop() {
		loop = !loop;
		visualizationToolBar.setSelectedLoop( loop );
		visualization.setLoop( loop );
	}

	public void visualizationStop() {
		visualizationToolBar.pause();
		visualization.getControl().resetTime();
		visualizationToolBar.setPlayButtonEnabled( true );
		// create a movie, if movie-creation was active.
		if( visualization.getRecording() != RecordingMode.NotRecording )
			visualization.createMovie();
		// stop animation if still animation
		if( visualization.isAnimating() )
			visualization.stopAnimation();
		// repaint once
		visualization.repaint();
	}
	private boolean showWalls = PropertyContainer.getGlobal().getAsBoolean( "settings.gui.visualization.walls" );

	public void visualizationShowWalls() {
		showWalls = !showWalls;
		visualizationToolBar.setSelectedShowWalls( showWalls );
		PropertyContainer.getGlobal().set( "settings.gui.visualization.walls", showWalls );
		visualization.getControl().showWalls( showWalls );
		visualization.repaint();
	}

	private boolean showGraph = PropertyContainer.getGlobal().getAsBoolean( "settings.gui.visualization.graph" );

	public void visualizationShowGraph() {
		showGraph = !showGraph;
		visualizationToolBar.setSelectedShowGraph( showGraph );
		PropertyContainer.getGlobal().set( "settings.gui.visualization.graph", showGraph );
		visualization.getControl().showGraph( showGraph );
		visualization.repaint();
	}
	public boolean showGraphGrid = PropertyContainer.getGlobal().getAsBoolean( "settings.gui.visualization.nodeArea" );

	public void visualizationShowGraphGrid() {
		showGraphGrid = !showGraphGrid;
		visualizationToolBar.setSelectedShowGraphGrid( showGraphGrid );
		PropertyContainer.getGlobal().set( "settings.gui.visualization.nodeArea", showGraphGrid );
		visualization.getControl().showNodeRectangles( showGraphGrid );
		visualization.repaint();

	}
	public boolean showCellularAutomaton = PropertyContainer.getGlobal().getAsBoolean( "settings.gui.visualization.cellularAutomaton" );

	public void visualizationShowCellularAutomaton() {
		showCellularAutomaton = !showCellularAutomaton;
		visualizationToolBar.setSelectedShowCellularAutomaton( showCellularAutomaton );
		PropertyContainer.getGlobal().set( "settings.gui.visualization.cellularAutomaton", showCellularAutomaton );
		visualization.getControl().showCellularAutomaton( showCellularAutomaton );
		visualization.repaint();

	}

	public void visualizationShowAllFloors() {
		final boolean showAllFloors = PropertyContainer.getGlobal().toggle( "settings.gui.visualization.floors" );
		visualizationToolBar.setSelectedAllFloors( showAllFloors );
		visualizationView.setFloorSelectorEnabled( !showAllFloors );
		if( showAllFloors )
			visualization.getControl().showAllFloors();
		else
			visualization.getControl().showFloor( visualizationView.getSelectedFloorID() );
		visualization.repaint();
	}

//	public void visualizationShowStaticPotential() {
//		final int oldValue = PropertyContainer.getGlobal().getAsInt( "settings.gui.visualization.floorInformation" );
//				if( oldValue == 1 ) {
//					btnShowPotential.setSelected( false );
//					control.showPotential( CellInformationDisplay.NoPotential );
//					PropertyContainer.getGlobal().set( "settings.gui.visualization.floorInformation", 0 );
//				} else {
//					btnShowPotential.setSelected( true );
//					btnShowDynamicPotential.setSelected( false );
//					btnShowUtilization.setSelected( false );
//					btnShowWaiting.setSelected( false );
//					PropertyContainer.getGlobal().set( "settings.gui.visualization.floorInformation", 1 );
//					visualizationView.unselectPotentialSelector();
//					control.activateMergedPotential();
//					control.showPotential( CellInformationDisplay.StaticPotential );
//				}
//				visualizationView.getGLContainer().repaint();
//
//	}
	public void visualizationShowCellInformation( CellInformationDisplay cid ) {
		final int oldValue = PropertyContainer.getGlobal().getAsInt( "settings.gui.visualization.floorInformation" );
		if( oldValue == cid.id() ) {
			// die werte waren gleich. schalte aus.
			visualizationToolBar.setSelectedCellInformationDisplay( CellInformationDisplay.NoPotential );
			PropertyContainer.getGlobal().set( "settings.gui.visualization.floorInformation", CellInformationDisplay.NoPotential.id() );
			visualization.getControl().showPotential( CellInformationDisplay.NoPotential );
		} else {
			visualizationToolBar.setSelectedCellInformationDisplay( cid );
			PropertyContainer.getGlobal().set( "settings.gui.visualization.floorInformation", cid.id() );
			visualizationView.unselectPotentialSelector();
			if( cid == CellInformationDisplay.StaticPotential )
				visualization.getControl().activateMergedPotential();
			visualization.getControl().showPotential( cid );
		}
		visualization.repaint();
	}

  public void buildVisualizationDataStructure( BatchResultEntry e, int nrOfCycle ) {
    EvacuationSimulationResults caRes = e.getCaVis() != null ? e.getCaVis()[nrOfCycle] : null;
		CAStatistic caStatistic = e.getCaStatistics() != null ? e.getCaStatistics()[nrOfCycle] : null;
		caRes.statistic = caStatistic;

		ds.GraphVisualizationResults graphRes = e.getGraphVis();

		VisualizationDataStructureTask visualizationDataStructure = new VisualizationDataStructureTask( caRes, graphRes, e.getBuildingResults(), caStatistic );
		JProgressBarDialog pbd = new JProgressBarDialog( editor, GUILocalization.loc.getStringWithoutPrefix( "batch.tasks.buildVisualizationDatastructure" ), true, visualizationDataStructure );
		pbd.executeTask();
		pbd.setVisible( true );
		ZETLoader.sendMessage( GUILocalization.loc.getStringWithoutPrefix( "batch.tasks.progress.visualizationDatastructureComplete" ) );

		visualization.setControl( visualizationDataStructure.getControl() );
		// create a copy here:
		control = visualizationDataStructure.getControl();

		visualizationToolBar.setEnabledVisibleElements( visualizationDataStructure.getControl() );

		//control.showCellularWa( btnShowCellularAutomaton.isSelected() );
		//control.showCellularAutomaton( btnShowCellularAutomaton.isSelected() );
		//control.showGraph( btnShowGraph.isSelected() );
//		control.showFloor( visualizationView.getSelectedFloorID() );
		visualizationView.updateFloorSelector();
		visualizationView.updatePotentialSelector();
	}

	public void rebuild( BatchResult result ) {
		visualizationToolBar.rebuild( result );
		graphStatisticToolBar.rebuild( result );
	}

	public void setMenuBar( JZETMenuBar aThis ) {
		menuBar = aThis;
	}

	public void showAbout() {
		CreditsDialog credits = new CreditsDialog( editor );
		credits.setVisible( true );
	}

	public void outputInformation() {
		// TODO move to another class
		// Pro Stockwerk:
		log.info( "Personenverteilung im Gebäude: " );
		int overall = 0;
		for( FloorInterface f : zcontrol.getProject().getBuildingPlan() ) {
			int counter = 0;
			for( Room r : f )
				for( AssignmentArea a : r.getAssignmentAreas() )
					counter += a.getEvacuees();
			log.log( Level.INFO, "{0}: {1} Personen", new Object[]{f.getName(), counter});
			overall += counter;
		}
		log.log( Level.INFO, "Insgesamt: {0}", overall);

		// Pro Ausgang:
		log.info( "Personenverteilung pro Ausgang: " );
		for( FloorInterface f : zcontrol.getProject().getBuildingPlan() )
			for( Room r : f )
				for( EvacuationArea ea : r.getEvacuationAreas() ) {
					overall = 0;
					log.info( "" );
					log.info( ea.getName() );
					// Suche nach evakuierten pro etage für dieses teil
					for( FloorInterface f2 : zcontrol.getProject().getBuildingPlan() ) {
						int counter = 0;
						for( Room r2 : f2 )
							for( AssignmentArea a : r2.getAssignmentAreas() )
								if( a.getExitArea().equals( ea ) )
									counter += a.getEvacuees();
						log.info( f2.getName() + ": " + counter + " Personen" );
						overall += counter;
					}
					log.info( ea.getName() + " insgesamt: " + overall );
				}

	}

	public void outputGraph() {
		GraphConverterAlgorithms last = GraphConverterAlgorithms.NonGridGraph;
		final Algorithm<BuildingPlan,NetworkFlowModel> conv = last.converter();

		conv.setProblem( zcontrol.getProject().getBuildingPlan() );

		conv.run();
		//final SerialTask st = new SerialTask( conv );
		//st.addPropertyChangeListener( new PropertyChangeListener() {
		//	@Override
		//	public void propertyChange( PropertyChangeEvent pce ) {
		//		if( st.isDone() )
						NetworkFlowModel originalProblem = conv.getSolution();
		//	}
		//} );
		//if( propertyChangeListener != null )
		//	st.addPropertyChangeListener( propertyChangeListener );
		//st.execute();

		ConcreteAssignment concreteAssignment = AssignmentConcrete.createConcreteAssignment( zcontrol.getProject().getCurrentAssignment(), 400 );

		GraphAssignmentConverter cav = new GraphAssignmentConverter( originalProblem );

		cav.setProblem( concreteAssignment );
		cav.run();
		originalProblem = cav.getSolution();


		//BatchResultEntry ca_res = new BatchResultEntry( zcontrol.getProject().getProjectFile().getName(), new BuildingResults( zcontrol.getProject().getBuildingPlan() ) );
		//ConcreteAssignment[] concreteAssignments = new ConcreteAssignment[1];
		//Assignment assignment = zcontrol.getProject().getCurrentAssignment();
		//concreteAssignments[0] = assignment.createConcreteAssignment( 400 );
		//new BatchGraphCreateOnlyTask( ca_res, 0, zcontrol.getProject(), assignment, concreteAssignments ).run();
		//NetworkFlowModel originalProblem = ca_res.getNetworkFlowModel();

		EarliestArrivalFlowProblem problem = originalProblem.getEAFP();
		try {
			if( true )
				throw new UnsupportedOperationException( "Mapping is not in NFM any more." );
			//DatFileReaderWriter.writeFile( zcontrol.getProject().getName(), problem, zcontrol.getProject().getProjectFile().getName().substring( 0, zcontrol.getProject().getProjectFile().getName().length() - 4 ) + ".dat", originalProblem.getZToGraphMapping() );
			throw new FileNotFoundException();
		} catch( FileNotFoundException ex ) {
			ZETLoader.sendError( "FileNotFoundException" );
			ex.printStackTrace( System.err );
		}

	}

	public void switchToLanguage( Locale locale ) {
		LocalizationManager.getManager().setLocale( locale );
		Localizer.instance().updateLocalization();
	}

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

	// TODO move loading/storing stuff to own class...
	public void loadProject() {
		if( jfcProject.showOpenDialog( editor ) == JFileChooser.APPROVE_OPTION ) {
			loadProject( jfcProject.getSelectedFile() );
			//zcontrol.loadProject( jfcProject.getSelectedFile() );
			//editor.loadProject();	// Load the currently loaded project by the control file
			//algorithmControl.setProject( zcontrol.getProject() );
			GUIOptionManager.setSavePath( jfcProject.getCurrentDirectory().getPath() );
			GUIOptionManager.setLastFile( 1, jfcProject.getSelectedFile().getAbsolutePath() );
		}
	}

	public void loadProject( File f ) {
		if( f == null )
			zcontrol.newProject( );
		else
			zcontrol.loadProject( f );
		loadProjectInternal();
	}

	/**
	 * Loads the project that is currently stored in zcontrol.
	 */
	private void loadProjectInternal() {
		// Reset GUI components
		//if( tabPane.getSelectedIndex() > 1 )
		//	tabPane.setSelectedIndex( 0 );
                Floor f = zcontrol.getProject().getBuildingPlan().getFloors().get(1);
                evc.setControlledProject(zcontrol.getProject().getBuildingPlan().getFloors());
		//editview.displayProject( zcontrol );
		caView.displayProject( zcontrol );
		// Löschen eingestellter parameter
//		ZToCAConverter.getInstance().clear();
		//firstSwitch = true;
		// Updaten der gui
		editview.update();
		caView.update();

		setZoomFactor( 0.04d );

		// Set up the last camera position
		visualization.getCamera().setPos( zcontrol.getProject().getVisualProperties().getCameraPosition().pos );
		visualization.getCamera().setView( zcontrol.getProject().getVisualProperties().getCameraPosition().view );
		visualization.getCamera().setUp( zcontrol.getProject().getVisualProperties().getCameraPosition().up );
		visualization.setTexts( zcontrol.getProject().getVisualProperties().getTextureFontStrings() );
		visualization.setView( zcontrol.getProject().getVisualProperties().getCurrentWidth(), zcontrol.getProject().getVisualProperties().getCurrentHeight() );
		visualizationView.updateCameraInformation();

		algorithmControl.setProject( zcontrol.getProject() );
	}

	/**
	 * Returns a {@link FileFilter} that allows loading zet format files with ending ".zet"
	 * @return a {@link FileFilter} that allows loading zet format files
	 */
	public static FileFilter getProjectFilter() {
		return new FileFilter() {

			@Override
			public boolean accept( File f ) {
				return f.isDirectory() || f.getName().toLowerCase().endsWith( ".zet" ) || f.getName().toLowerCase().endsWith( ".gzet" );
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
	private boolean createCopy = PropertyContainer.getGlobal().getAsBoolean( "options.filehandling.createBackup" );

	public void saveProjectAs() {
		if( jfcProject.showSaveDialog( editor ) == JFileChooser.APPROVE_OPTION ) {
			GUIOptionManager.setSavePath( jfcProject.getCurrentDirectory().getPath() );
			GUIOptionManager.setLastFile( 1, jfcProject.getSelectedFile().getAbsolutePath() );
			try {
				if( jfcProject.getSelectedFile().exists() && createCopy )
					IOTools.createBackup( jfcProject.getSelectedFile() );
				File target = jfcProject.getSelectedFile();
				if( !target.getName().endsWith( ".zet" ) && !target.getName().endsWith( ".gzet" ) )
					target = new File( target.getAbsolutePath() + ".zet" );
				ProjectLoader.save( zcontrol.getProject(), target );
				//zcontrol.getProject().save( target );
			} catch( java.lang.StackOverflowError soe ) {
				showErrorMessage( GUILocalization.loc.getString( "gui.editor.JEditor.error.stackOverflowTitle" ), GUILocalization.loc.getString( "gui.editor.JEditor.error.stackOverflow" ) );
			} catch( Exception ex ) {
				showErrorMessage( GUILocalization.loc.getString( "gui.editor.JEditor.error.SaveTitle" ), GUILocalization.loc.getString( "gui.editor.JEditor.error.Save" ) );
			}
			editview.displayProject( zcontrol );
			ZETLoader.sendMessage( GUILocalization.loc.getString( "gui.editor.JEditor.message.saved" ) );
		}

	}

	public void saveProject() {
		if( zcontrol.getProject().getProjectFile() == null )
			saveProjectAs();
		else {
			try {
				if( createCopy == true )
					IOTools.createBackup( zcontrol.getProject().getProjectFile() );
				ProjectLoader.save( zcontrol.getProject() );
				//zcontrol.getProject().save();
			} catch( java.lang.StackOverflowError soe ) {
				showErrorMessage( GUILocalization.loc.getString( "gui.editor.JEditor.error.stackOverflowTitle" ), GUILocalization.loc.getString( "gui.editor.JEditor.error.stackOverflow" ) );
			} catch( Exception ex ) {
				showErrorMessage( GUILocalization.loc.getString( "gui.editor.JEditor.error.SaveTitle" ), GUILocalization.loc.getString( "gui.editor.JEditor.error.Save" ) );
				ex.printStackTrace( System.err );
				return;
			}
			ZETLoader.sendMessage( GUILocalization.loc.getString( "gui.editor.JEditor.message.saved" ) );
		}
	}

	public void newProject() {
		newProject( false );
	}

	// TODO auslagern von save und methoden aufrufen!
	public void newProject( boolean overwrite ) {
		String status = "";
		int res = overwrite ? 1 : JOptionPane.showOptionDialog( editor,
						GUILocalization.loc.getString( "gui.editor.JEditor.SaveQuestion" ),
						GUILocalization.loc.getString( "gui.editor.JEditor.NewProject" ),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null, null, null );
		switch( res ) {
			case 2:
			case -1:
				return;	// exit, do nothing
			case 0:
				// save
				if( zcontrol.getProject().getProjectFile() == null ) {
					if( jfcProject.showSaveDialog( editor ) == JFileChooser.APPROVE_OPTION ) {
						GUIOptionManager.setSavePath( jfcProject.getCurrentDirectory().getPath() );
						try {
							if( jfcProject.getSelectedFile().exists() && createCopy )
								IOTools.createBackup( jfcProject.getSelectedFile() );
							File target = jfcProject.getSelectedFile();
							if( !target.getName().endsWith( ".zet" ) && !target.getName().endsWith( ".gzet" ) )
								target = new File( target.getAbsolutePath() + ".zet" );
							ProjectLoader.save( zcontrol.getProject(), target );
							//zcontrol.getProject().save( target );
						} catch( java.lang.StackOverflowError soe ) {
							showErrorMessage( GUILocalization.loc.getString( "gui.editor.JEditor.error.stackOverflowTitle" ), GUILocalization.loc.getString( "gui.editor.error.JEditor.stackOverflow" ) );
						} catch( Exception ex ) {
							showErrorMessage( GUILocalization.loc.getString( "gui.editor.JEditor.error.SaveTitle" ), GUILocalization.loc.getString( "gui.editor.JEditor.error.Save" ) );
							ex.printStackTrace( System.err );
							return;
						}
					}
				} else {
					try {
						if( createCopy )
							IOTools.createBackup( zcontrol.getProject().getProjectFile() );
						ProjectLoader.save( zcontrol.getProject() );
						//zcontrol.getProject().save();
					} catch( java.lang.StackOverflowError soe ) {
						showErrorMessage( GUILocalization.loc.getString( "gui.editor.JEditor.error.stackOverflowTitle" ), GUILocalization.loc.getString( "gui.editor.JEditor.error.stackOverflow" ) );
					} catch( Exception ex ) {
						showErrorMessage( GUILocalization.loc.getString( "gui.editor.JEditor.error.SaveTitle" ), GUILocalization.loc.getString( "gui.editor.JEditor.error.Save" ) );
						ex.printStackTrace( System.err );
						return;
					}
				}
				status = GUILocalization.loc.getString( "gui.editor.JEditor.status.newProject" );
				break;
			case 1:
				status = GUILocalization.loc.getString( "gui.editor.JEditor.status.newProjectDiscard" );
		}
		loadProject( null );
		//zcontrol.newProject();
		//editor.loadProject();	// Load the currently loaded project by the control file
		//algorithmControl.setProject( zcontrol.getProject() );
		ZETLoader.sendMessage( status );
	}

	public void saveAsDXF() {
		String filename = zcontrol.getProject().getProjectFile().getPath().substring( 0, zcontrol.getProject().getProjectFile().getPath().length() - 3 ) + "dxf";
		try {
			DXFWriter.exportIntoDXF( filename, zcontrol.getProject().getBuildingPlan() );
		} catch( IOException ex ) {
			showErrorMessage( GUILocalization.loc.getString( "gui.editor.JEditor.error.SaveTitle" ), GUILocalization.loc.getString( "gui.editor.JEditor.error.Save" ) );
			ex.printStackTrace( System.err );
			return;
		}
		ZETLoader.sendMessage( GUILocalization.loc.getString( "gui.editor.JEditor.message.dxfComplete" ) );
	}

	/**
	 * Deletes some polygons from the underlying model and updates the graphical
	 * user interface.
	 * @param toDelete
	 */
	public void deletePolygon( List<PlanPolygon<?>> toDelete ) {
		for( PlanPolygon<?> p : toDelete )
			zcontrol.deletePolygon( p );
		editview.getLeftPanel().getMainComponent().displayFloor();
	}

	/**
	 * Creates a new floor in the z-format and sets its initial size to some value
	 * that is defined in the options.
	 */
	public void newFloor() {
		Floor f = zcontrol.createNewFloor();
		f.setMinimumSize( ZETProperties.getDefaultFloorSizeMinX(), ZETProperties.getDefaultFloorSizeMinY(), ZETProperties.getDefaultFloorSizeMaxX()-ZETProperties.getDefaultFloorSizeMinX(), ZETProperties.getDefaultFloorSizeMaxY()-ZETProperties.getDefaultFloorSizeMinY() );
		ZETLoader.sendMessage( "Neue Etage angelegt." ); // TODO loc
		editview.updateFloorList(); // update the floor-boxes in the GUI
		editview.changeFloor( f );
		caView.updateQuickFloorlist();
		caView.changeQuickFloor( f );

	}

	/**
	 * Removes a floor from the z format,updates the list of floors and shows a
	 * another floor in the edit area. If the default evacuation floor is to be
	 * deleted, nothing happens.
	 */
	public void deleteFloor() {
		final int oldIndex = editview.getFloorID();
		if( zcontrol.deleteFloor( editview.getCurrentFloor() ) ) {
			editview.updateFloorList();
			editview.setFloor( oldIndex-1 );
		}
	}

	public void importFloor() {
		FloorImportDialog floorImport = new FloorImportDialog( editor, zcontrol.getProject(), "Importieren", 450, 250 );
		floorImport.setVisible( true );
	}

	public void copyFloor() {
		final int oldIndex = editview.getFloorID();
		zcontrol.copyFloor( editview.getCurrentFloor() );
		editview.updateFloorList();
		editview.setFloor( oldIndex );
	}

	public void rasterize() {
		try {
			RasterizeTask rasterize = new RasterizeTask( zcontrol.getProject() );
			JProgressBarDialog pbd = new JRasterizeProgressBarDialog( editor, "Rastern", true, rasterize );
			pbd.executeTask();
			pbd.setVisible( true );
			ZETLoader.sendMessage( GUILocalization.loc.getString( "gui.message.RasterizationComplete" ) );
		} catch( Exception ex ) {
			ZETLoader.sendError( ex.getLocalizedMessage() );
		}
	}

	// TODO dieses feature überarbeiten
	public void distributeEvacuees() {
		try {
			String res = JOptionPane.showInputDialog( editor,
							"Anzahl zu evakuierender Personen (maximal "
							+ Integer.toString( zcontrol.getProject().getBuildingPlan().maximalEvacuees() ) + ")", "Personen verteilen", JOptionPane.QUESTION_MESSAGE );

			if( res != null ) {
				zcontrol.getProject().getBuildingPlan().distributeEvacuees( Integer.parseInt( res ) );
				ZETLoader.sendMessage( GUILocalization.loc.getString( "gui.message.RasterizationComplete" ) );
			}
		} catch( NumberFormatException ex ) {
			ZETLoader.sendError( GUILocalization.loc.getString( "gui.error.NonParsableNumber" ) );
		} catch( TooManyPeopleException ex ) {
			ZETLoader.sendError( ex.getLocalizedMessage() );
		}
	}

	public void showAssignmentDialog() {
		JAssignment distribution = new JAssignment( editor, zcontrol, GUILocalization.loc.getString( "gui.AssignmentEditor.Title" ), 850, 400 );
		distribution.setVisible( true );
		distribution.dispose();
	}

	/**
	 * Hides and shows the areas in the plan depending on the status of the
	 * associated menu entries. The menu entries to hide and show all areas
	 * are updated and, if necessary, disabled or enabled.
	 * @param areaVisibility the type of area that should be hidden or shown
	 * @param value shows the specified type of area if {@code true}, hides it otherwise
	 */
	public void updateVisibility( AreaType areaVisibility, boolean value ) {
		if( value && !mode.contains( areaVisibility ) )
			mode.add( areaVisibility );
		else if( !value )
			mode.remove( areaVisibility );
		editview.changeAreaView( mode );
		menuBar.setEnabledShowAllAreas( mode.size() != AreaType.values().length );
		menuBar.setEnabledHideAllAreas( !mode.isEmpty() );
	}

	/**
	 * Enables and disables visibility of areas in the ZET editor. The boolean
	 * parameter decides whether all areas should be visible or hidden.
	 * @param b shows all types of areas if {@code true}, hides them otherwise
	 */
	public void updateVisibility( boolean b ) {
		mode.clear();
		if( b )
			mode.addAll( Arrays.asList( AreaType.values() ) );
		editview.changeAreaView( mode );
		menuBar.setEnabledShowAllAreas( mode.size() != AreaType.values().length );
		menuBar.setEnabledHideAllAreas( !mode.isEmpty() );
	}

	public void showPropertiesDialog() {
		JPropertyDialog jd = new JPropertyDialog( ZETProperties.getCurrentPropertyTreeModel() );


		jd.setModal( true );
		jd.setVisible( true );
		log.info( "Properties saved." ); // TODO loc
	}

	public void setRasterizedPaintMode( boolean selected ) {
//		editview.getFloor().setRasterizedPaintMode( select );
	}

	public void setRasterPaintStyle( RasterPaintStyle rasterPaintStyle ) {
		//editview.getFloor().setRasterPaintStyle( rasterPaintStyle );
                System.err.println("TODO: pass new rasterpaint style to edit view" );
		menuBar.setSelectedGridLines( rasterPaintStyle == RasterPaintStyle.LINES );
		menuBar.setSelectedGridPoints( rasterPaintStyle == RasterPaintStyle.POINTS );
		menuBar.setSelectedGridNotVisible( rasterPaintStyle == RasterPaintStyle.NOTHING );
	}

	public void showDefaultFloor( boolean b ) {
		ZETProperties.isDefaultFloorHidden();
		PropertyContainer.getGlobal().set( "editor.options.view.hideDefaultFloor", b );
		editview.displayProject();

	}

	public void loadBuildingPlan() {
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
				ex.printStackTrace( System.err );
			}

			// Show Zoom/Size Dialogue
			JPlanImageProperties ip = new JPlanImageProperties( image );
			if( ip.showPlanImageZoomDialog( editor ) == JPlanImageProperties.OK ) {
				CoordinateTools.setPictureZoomFactor( (double) ip.getMillimeterCount() / (double) ip.getPixelCount() );
				editview.getFloor().getPlanImage().setImage( image );
				menuBar.setEnabledBuildingPlan( true );
				ZETLoader.sendMessage( "Plan für Hintergrunddarstellung geladen." );
			}
		}
	}

	public void hideBuildingPlan() {
		editview.getFloor().getPlanImage().setImage( (BufferedImage) null );
		menuBar.setEnabledBuildingPlan( false );
	}

	public void resizeBuildingPlan() {
		BufferedImage image = editview.getFloor().getPlanImage().getImage();
		// Show Zoom/Size Dialogue
		JPlanImageProperties ip = new JPlanImageProperties( image );
		if( ip.showPlanImageZoomDialog( editor ) == JPlanImageProperties.OK ) {
			CoordinateTools.setPictureZoomFactor( (double) ip.getMillimeterCount() / (double) ip.getPixelCount() );
			editview.getFloor().getPlanImage().resize();
		}
	}

	public void moveBuildingPlan() {
		JPlanImageProperties ip = new JPlanImageProperties();
		ip.setXOffset( editview.getFloor().getPlanImage().getImageX() );
		ip.setYOffset( editview.getFloor().getPlanImage().getImageY() );
		if( ip.showPlanMoveDialog( editor ) == JPlanImageProperties.OK ) {
			editview.getFloor().getPlanImage().setImageX( ip.getXOffset() );
			editview.getFloor().getPlanImage().setImageY( ip.getYOffset() );
		}

	}

	public void transparencyBuildingPlan() {
		JPlanImageProperties ip = new JPlanImageProperties();
		ip.setAlpha( editview.getFloor().getPlanImage().getAlpha() );
		if( ip.showPlanAlphaDialog( editor ) == JPlanImageProperties.OK )
			editview.getFloor().getPlanImage().setAlpha( ip.getAlpha() );
	}

    private Function<PropertyTreeNode,Icon> iconSupplier = (PropertyTreeNode p) -> {
        HashMap<String,Icon> iconMapping = new HashMap<>();
        iconMapping.put("options.view.editor", ZETIconSet.OptionsEditor.icon());
        iconMapping.put("options.filehandling", ZETIconSet.OptionsFileHandling.icon());
        iconMapping.put("options.quickvisualization", ZETIconSet.OptionsQuickVisualization.icon());
        iconMapping.put("options.visualization", ZETIconSet.OptionsVisualization.icon());
        iconMapping.put("options.statistic", ZETIconSet.OptionsStatistic.icon());

        System.out.println(p.getDisplayNameTag());
        return iconMapping.get(p.getDisplayNameTag());
    };
    
	public void showOptionsDialog() {
		ZETLoader.ptmOptions.getRoot().reloadFromPropertyContainer();

                
                ZETLoader.ptmOptions.setLoc(ZETOptionsLocalization.loc);
		JOptionsDialog opt = new JOptionsDialog( ZETLoader.ptmOptions, iconSupplier );
                //opt.setIconSupplier(iconSupplier);
		opt.setModal( true );
		opt.setVisible( true );

		try {	// Save results in options file
                    PropertyTreeModelWriter writer = new PropertyTreeModelWriter();
			writer.saveConfigFile( ZETLoader.ptmOptions, new FileWriter( ZETLoader.optionFilename ) );
		} catch( IOException ex ) {
			ZETLoader.sendError( "Error saving config file!" ); // TODO loc
		}

	}

	public void showSettingsDialog() {
		ZETLoader.ptmInformation.getRoot().reloadFromPropertyContainer();

		JOptionsDialog opt = new JOptionsDialog( ZETLoader.ptmInformation );
		opt.setModal( true );
		opt.setVisible( true );

		try {	// Save results in settings file
                    PropertyTreeModelWriter writer = new PropertyTreeModelWriter();
			writer.saveConfigFile( ZETLoader.ptmInformation, new FileWriter( ZETLoader.informationFilename ) );
		} catch( IOException ex ) {
			ZETLoader.sendError( "Error saving settings file!" ); // TODO loc
		}
	}

	/** GUIControl class for projects and editing */
	private ZControl zcontrol;

	public ZControl getZControl() {
		return zcontrol;
	}

	/**
	 * Displays a box displaying an error message.
	 * @param title the title of the message box
	 * @param message the message
	 */
	public void showErrorMessage( String title, String message ) {
		JOptionPane.showMessageDialog( editor, message, title, JOptionPane.ERROR_MESSAGE );
	}

	public void updateCameraInformation() {
		visualizationView.updateCameraInformation();
	}

	public void setZETWindowTitle( String additionalTitleBarText ) {
		String titleBarText = zcontrol.getProject().getProjectFile() != null ? zcontrol.getProject().getProjectFile().getName() : GUILocalization.loc.getString( "NewFile" );
		titleBarText += " " + additionalTitleBarText + " - " + GUILocalization.loc.getString( "AppTitle" );
		editor.setTitle( titleBarText );
	}

	public void showFloor( FloorInterface floor ) {
		editview.changeFloor( floor );
	}

	public void showPolygon( Room room ) {
		editview.getFloor().showPolygon( room );
	}

	public void setSelectedPolygon() {

	}

	public void setSelectedPolygon( PlanPolygon<?> poly ) {
//		editview.getFloor().setSelectedPolygon( poly );
	}


	public void createBuildingDataStructure() {
		algorithmControl.convertBuildingPlan( new PropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				if( isDone( pce ) ) {
					visualization.getControl().setBuildingControl( algorithmControl.getBuildingResults() );
					visualizationView.updateFloorSelector( editview.getFloorID() );
				}
			}
		});
	}

	private boolean isDone( PropertyChangeEvent pce ) {
		if( pce.getPropertyName().equals( "state" ) )
			if( pce.getNewValue().equals( SwingWorker.StateValue.DONE ) )
				return true;
		return false;
	}

  public void addVisualization( BuildingResults br ) {
    visualization.getControl().setBuildingControl( br );
    visualizationView.updateFloorSelector( 0 );
  }

  public void addVisualization( GraphVisualizationResults gvr ) {
    visualization.getControl().setGraphControl( gvr );
  }

  public void addVisualization( EvacuationSimulationResults cav ) {
    visualization.getControl().setCellularAutomatonControl( cav );
  }

	public void createCellularAutomaton() {
		algorithmControl.convertCellularAutomaton( new PropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				if( isDone( pce ) ) {
					if( algorithmControl.isError() ) {
						try {
							algorithmControl.throwError();
						} catch( RoomEdgeInvalidTargetException ex ) {
							if( ZETLoader.isDebug() ) {
								log.warning( "DEBUG-out:" );
								log.warning( "Error during conversion in Room " + ex.getInvalidEdge().getRoom().getName() + ". " + ex.getMessage() );
								log.warning( "" );
							}
							log.severe( ex.getMessage() );
							JOptionPane.showMessageDialog( null, " Fehler in Raum " + ex.getInvalidEdge().getRoom().getName() + ". \n" + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE );

							int ret = JOptionPane.showOptionDialog( editor, "Soll die Kante unpassierbar gemacht werden? \n (Dies in den meisten Fällen sinnvoll.)", "Fehler kann behoben werden.", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null );
							switch( ret ) {
								case JOptionPane.YES_OPTION:
									log.info( "YES" );
									zcontrol.disconnectAtEdge( ex.getInvalidEdge() );
									break;
								case JOptionPane.NO_OPTION:
									log.info( "NO" );
									break;
								default:
									throw new IllegalStateException( "A selection that is not supported has been made." );
							}
						}
            EvacuationSimulationResults caVis = new EvacuationSimulationResults( algorithmControl.getMapping(), algorithmControl.getCellularAutomaton().getPotentialManager(), algorithmControl.getCellularAutomaton() );

						visualization.getControl().setCellularAutomatonControl( caVis );
						visualizationView.updatePotentialSelector();
//						visualizationView.updateFloorSelector();
						caView.getLeftPanel().getMainComponent().setSimulationData( algorithmControl.getCellularAutomaton(), algorithmControl.getContainer(), algorithmControl.getMapping() );
						caView.displayFloor( editview.getCurrentFloor() );
						firstProgress = true;
					}
				}
			}
		});
	}

	public void createConcreteAssignment() {
		throw new UnsupportedOperationException( "Cannot create concrete assignments for fun." );
//		try {
//			algorithmControl.createConcreteAssignment();
//		} catch( IllegalArgumentException | ConversionNotSupportedException ex ) {
//			Logger.getLogger( GUIControl.class.getName() ).log( Level.SEVERE, null, ex );
//		}
	}

	public void performSimulation() {
		createBuildingDataStructure(); // kein task
		//editor.enableProgressBar();
		algorithmControl.performSimulation( new PropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				if( isDone( pce ) ) {
					if( algorithmControl.isError() ) {
						Debug.globalLogger.log( Level.SEVERE, "Exception in execution", algorithmControl.getError() );
						//Debug.printException( algorithmControl.getError() );
						algorithmControl.getError().printStackTrace( System.err );
					} else {
						log.log( Level.INFO, "Left individuals: {0}", algorithmControl.getCellularAutomaton().getIndividualCount());
						visualization.getControl().setCellularAutomatonControl( algorithmControl.getCaVisResults() );
						visualizationView.updatePotentialSelector();
						visualizationToolBar.setEnabledPlayback( true );
						caView.getLeftPanel().getMainComponent().setSimulationData( algorithmControl.getCellularAutomaton(), algorithmControl.getContainer(), algorithmControl.getMapping() );
						caView.displayFloor( editview.getCurrentFloor() ); // hier startet ein task!
						ZETLoader.sendMessage( "Simulation beendet" );
            
            // Send data to statistic
            caStatisticView.setCellularAutomaton( algorithmControl.getCellularAutomaton() );
            caStatisticView.setCA( algorithmControl.getCaVisResults() );
            
            BatchResult result = new BatchResult( false );

            // Assume, building results are available here. Should be the case.
            BatchResultEntry entry = new BatchResultEntry( "Simulation Result", algorithmControl.getBuildingResults() );
            
            entry.setCellularAutomatonStatistic( 0, algorithmControl.getCaVisResults().statistic );
            entry.setCellularAutomatonVisualization( 0, algorithmControl.getCaVisResults() );
            entry.setCellularAutomaton( 0, algorithmControl.getCellularAutomaton() );

            
            MultipleCycleCAStatistic mcc = new MultipleCycleCAStatistic( 1 );
            mcc.addCycle( algorithmControl.getCaVisResults().statistic );
            entry.setMultipleCycleCAStatistic( mcc );
            try {
              //algorithmControl.getCaVisResults().statistic;
              
              //CAStatistic cas = new CAStatistic(null );
              
              ///entry.setCellularAutomatonStatistic( 1, new CAStatistic( caAlgo.getesp.caStatisticWriter.getStoredCAStatisticResults() ) );
              //new CAStatistic (caAlgo.getCaController ().getCaStatisticWriter ().getStoredCAStatisticResults ());

              result.addResult( entry );
            } catch( IOException ex ) {
              Logger.getLogger( GUIControl.class.getName() ).log( Level.SEVERE, null, ex );
              throw new IllegalStateException( "WTF???" );
            }
            caStatisticView.setResult( result );
            
            
						//EventServer.getInstance().dispatchEvent( new MessageEvent<>( this, MessageType.Status, "Simulation finished" ) );
					}
				}
			}
		}, this );
	}

	public void performQuickVisualization() {
		algorithmControl.performSimulationQuick( stepByStepListener );
	}

	boolean init = false;

	public void pauseSimulation() {
		algorithmControl.pauseSimulation();
	}

	public void performOneStep() {
		algorithmControl.performOneStep( stepByStepListener );
	}

	/**
	 *
	 * @param algo
	 */
	public void createGraph( GraphConverterAlgorithms algo ) {
		algorithmControl.convertGraph( new PropertyChangeListener(){

			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				if( isDone( pce ) ) {
					GraphVisualizationResults gvr = new GraphVisualizationResults( algorithmControl.getNetworkFlowModel(), algorithmControl.getNetworkFlowModel().getNodeCoordinates() );
					visualization.getControl().setGraphControl( gvr );

				}
			}
		}, algo);
	}

	public void performOptimization() {
		algorithmControl.performOptimization( new PropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				if( isDone( pce ) ) {
					GraphVisualizationResults gvr = algorithmControl.getGraphVisResults();
					visualization.getControl().setGraphControl( gvr );
				}
			}
		}, this );
	}

	public void performExitAssignmentEAT() {
		algorithmControl.performExitAssignmentEAT( new PropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				if( isDone( pce ) ) {
					//GraphVisualizationResults gvr = algorithmControl.getGraphVisResults();
					//visualization.getControl().setGraphControl( gvr );
					log.info( "ExitAssignmentEAT is done." );
				}
			}
		}, this );

		log.log( Level.INFO, "Left individuals: {0}", algorithmControl.getCellularAutomaton().getIndividualCount());
		visualization.getControl().setCellularAutomatonControl( algorithmControl.getCaVisResults() );
		visualizationView.updatePotentialSelector();
		visualizationToolBar.setEnabledPlayback( true );
		caView.getLeftPanel().getMainComponent().setSimulationData( algorithmControl.getCellularAutomaton(), algorithmControl.getContainer(), algorithmControl.getMapping() );
		caView.displayFloor( editview.getCurrentFloor() ); // hier startet ein task!
		ZETLoader.sendMessage( "Simulation beendet" );
	}

	public void performOptimizationCompare() {
		algorithmControl.performOptimizationCompare( new PropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				if( isDone( pce ) ) {
					GraphVisualizationResults gvr = algorithmControl.getGraphVisResults();
					CompareVisualizationResults cvr = algorithmControl.getCompVisResults();
					//visualization.getControl().setGraphControl( gvr );
					visualization = visualizationView.getGLContainer();
					/*visualization.getControl().setCompControl(cvr);
					 visualization.repaint();*/
				}
			}
		} );
	}

	boolean firstProgress = false;
	AlgorithmListener stepByStepListener = new AlgorithmListener() {
		@Override
		public synchronized void eventOccurred( AlgorithmEvent event ) {
			if( event instanceof AlgorithmStartedEvent ) {
				while( algorithmControl.getCellularAutomaton() == null ) // the algorithm was started. we have to wait, until the
					try {																										 // computed data, as the CA and so on are available, if
						Thread.sleep( 100 );																		 // this event is dispatched earlier.
						System.out.println( "We are waiting here..." );
					} catch( InterruptedException ex ) {
						Logger.getLogger( AlgorithmControl.class.getName() ).log( Level.SEVERE, null, ex );
					}
				EvacuationCellularAutomaton ca = algorithmControl.getCellularAutomaton();
				if( ca != null ) {
					caView.getLeftPanel().getMainComponent().setSimulationData( algorithmControl.getCellularAutomaton(), algorithmControl.getContainer(), algorithmControl.getMapping() );
					caView.displayFloor( editview.getCurrentFloor() );
					caView.getLeftPanel().getMainComponent().update();
					firstProgress = true;
					log.info( "First time floor drawn." );
				}
			} else if( event instanceof AlgorithmProgressEvent ) {
				AlgorithmProgressEvent ape = (AlgorithmProgressEvent)event;
				log.log( Level.INFO, "Progress: {0}", ape.getProgress() );
				editor.setProgressValue( ((int)(ape.getProgress() * 100)) );
				caView.getLeftPanel().getMainComponent().update();
				caView.repaint();
			}
		}
	};

	@Override
	public synchronized void eventOccurred( AlgorithmEvent event ) {
		if( event instanceof AlgorithmProgressEvent ) {
			AlgorithmProgressEvent ape = (AlgorithmProgressEvent) event;
			log.log( Level.INFO, "Progress: {0}", ape.getProgress());
			editor.setProgressValue( ((int)(ape.getProgress() * 100)) );
		}
	}

	final static int scrollCount = 20;

	/**
	 * <p>Adjusts the horizontal scroll bar position. Change is a value given by how
	 * much the value is adjusted. The sign decides whether it is scrolled up or
	 * down. The wheel rotation value of the wheel event can directly be used
	 * here.</p>
	 * <p>Note that the given {@code change} value is scaled so that the scrolling
	 * is done in 20 ticks.</p>
	 * @param change by how much the scroll bar value should be changed
	 */
	public void scrollHorizontal( int change ) {
		int fact = (editview.getLeftPanel().getHorizontalScrollBar().getMaximum() - editview.getLeftPanel().getHorizontalScrollBar().getMinimum())/scrollCount;
		int value = editview.getLeftPanel().getHorizontalScrollBar().getValue();
		editview.getLeftPanel().getHorizontalScrollBar().setValue( value + change*fact );
	}

	/**
	 * <p>Adjusts the vertical scroll bar position. Change is a value given by how
	 * much the value is adjusted. The sign decides whether it is scrolled up or
	 * down. The wheel rotation value of the wheel event can directly be used
	 * here.</p>
	 * <p>Note that the given {@code change} value is scaled so that the scrolling
	 * is done in 20 ticks.</p>
	 * @param change by how much the scroll bar value should be changed
	 */
	public void scrollVertical( int change ) {
		int fact = (editview.getLeftPanel().getVerticalScrollBar().getMaximum() - editview.getLeftPanel().getVerticalScrollBar().getMinimum())/scrollCount;
		int value = editview.getLeftPanel().getVerticalScrollBar().getValue();
		editview.getLeftPanel().getVerticalScrollBar().setValue( value + change*fact );
	}

	protected synchronized void handleProgressEvent( int progress ) {
		editor.setProgressValue( progress );
//		if( progress == 100 )
//			editor.disableProgressBar();
	}

	public void executeTask( Runnable task ) {
		// Execute task
		//editor.enableProgressBar();
		AlgorithmTask worker = AlgorithmTask.getNewInstance();
		worker.setTask( task );
		worker.addPropertyChangeListener( pcl );
		try {
			worker.executeAlgorithm( true );
			//AlgorithmTask.getInstance().setProgress( 100, "", "" );
		} catch( Exception ex ) {
			log.info( "Fehler trat auf" );
		} finally { }
	}

	/**
	 *
	 */
	protected PropertyChangeListener pcl = new PropertyChangeListener() {
		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			if( evt.getPropertyName().equals( "progress" ) ) {
				int progress = (Integer) evt.getNewValue();
				handleProgressEvent( progress );
			}
		}
	};


	/**
	 * Sends an error to the main window such that the error area in the
	 * bar is highlighted.
	 * @param message the error message
	 */
	public void alertError( String message ) {
		editor.sendError( message );
	}

	public void animationFinished() {
			if( loop )
				control.resetTime();
			else {
				visualizationToolBar.setPlayButtonEnabled( false );
				ZETLoader.sendMessage( "Replaying visualization finished." );
			}
	}

	public Templates<Door> getDoorTemplates() {
		return doorTemplates;
	}

	public Templates<ExitDoor> getExitDoorTemplates() {
		return exitDoorTemplates;
	}

	public void setSimulationAlgorithm( CellularAutomatonAlgorithms cellularAutomaton ) {
		algorithmControl.setSimulationAlgorithm( cellularAutomaton );
	}

	public CellularAutomatonAlgorithms getSimulationAlgorithm() {
		return algorithmControl.getSimulationAlgorithm();
	}

	public JEditView getEditView() {
		return editview;
	}

    public EditViewModel getViewModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void moveFloorUp() {
        System.err.print("Implement move floor up");
    }

    public void moveFloorDown() {
        System.err.print("Implement move floor up");
    }

}
