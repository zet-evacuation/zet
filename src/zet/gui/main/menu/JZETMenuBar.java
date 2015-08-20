
package zet.gui.main.menu;

import org.zetool.common.localization.Localization;
import org.zetool.common.localization.LocalizationManager;
import org.zetool.common.localization.Localized;
import ds.PropertyContainer;
import gui.GUIControl;
import gui.GraphConverterAlgorithms;
import gui.ZETLoader;
import gui.editor.Areas;
import gui.editor.properties.PropertyFilesSelectionModel;
import gui.editor.properties.PropertyListEntry;
import gui.editor.properties.PropertyLoadException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import org.zetool.components.framework.Menu;
import zet.gui.GUILocalization;
import zet.gui.main.tabs.base.RasterPaintStyle;
import zet.gui.treeview.JProjectTreeView;
import zet.tasks.CellularAutomatonAlgorithms;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings( "serial")
public class JZETMenuBar extends JMenuBar implements ActionListener, Localized {
	private final GUIControl control;
	/** The localization class. */
	static final Localization loc = GUILocalization.loc;
	private JMenuItem mnuFileSaveResultAs;
	private JMenuItem mnuFileLoadResult;
	private JMenuItem mnuShowAllAreas;
	private JMenuItem mnuHideAllAreas;
	private JCheckBoxMenuItem mnuDelayArea;
	private JCheckBoxMenuItem mnuStairArea;
	private JCheckBoxMenuItem mnuEvacuationArea;
	private JCheckBoxMenuItem mnuInaccessibleArea;
	private JCheckBoxMenuItem mnuSaveArea;
	private JCheckBoxMenuItem mnuAssignmentArea;
	private JRadioButtonMenuItem mnuGridLines;
	private JRadioButtonMenuItem mnuGridPoints;
	private JRadioButtonMenuItem mnuGridNotVisible;
	private JCheckBoxMenuItem mnuPaintRasterized;
	private JCheckBoxMenuItem mnuHideDefaultFloor;
	private List<PropertyListEntry> properties;
	private JRadioButtonMenuItem[] mnuLanguages = new JRadioButtonMenuItem[2];
	private JRadioButtonMenuItem[] mnuProperties = null;
	private JMenuItem mnuPlanImageHide;
	private JMenuItem mnuPlanImageResize;
	private JMenuItem mnuPlanImageLocate;
	private JMenuItem mnuPlanImageTransparency;
	static JProjectTreeView ptv;

	public JZETMenuBar( GUIControl control ) {
		this.control = control;
		createMenuBar();
		control.setMenuBar( this );
	}

	/**
	 * Creates the menu.
	 */
	private void createMenuBar() {
		loc.setPrefix( "" );

		JMenu mFile = Menu.addMenu( this, "gui.menu.File" );
		JMenu mEdit = Menu.addMenu( this, "gui.menu.Edit" );
		JMenu mView = Menu.addMenu( this, "gui.menu.View" );
		JMenu mExecute = Menu.addMenu( this, "gui.menu.Execute" );
		JMenu mExtras = Menu.addMenu( this, "gui.menu.Extras" );
		JMenu mHelp = Menu.addMenu( this, "gui.menu.Help" );

		// Dateimenue
		Menu.addMenuItem( mFile, "gui.menu.File.New", 'N', this, "newProject" );
		Menu.addMenuItem( mFile, "-" );
		Menu.addMenuItem( mFile, "gui.menu.File.Open", 'O', this, "loadProject" );
		Menu.addMenuItem( mFile, "gui.menu.File.Save", 'S', this, "saveProject" );
		Menu.addMenuItem( mFile, "gui.menu.File.SaveAs", 'U', this, "saveProjectAs" );
		Menu.addMenuItem( mFile, "-" );
		Menu.addMenuItem( mFile, "gui.menu.File.DXF", this, "saveAsDXF" );
		Menu.addMenuItem( mFile, "-" );
		mnuFileSaveResultAs = Menu.addMenuItem( mFile, "gui.menu.File.SaveResultAs", 'E', this, "saveResultAs" );
		mnuFileSaveResultAs.setEnabled( false );
		mnuFileLoadResult = Menu.addMenuItem( mFile, "gui.menu.File.LoadBatchResult", 'B', this, "loadBatchResult" );
		mnuFileLoadResult.setEnabled( false );
		Menu.addMenuItem( mFile, "-" );
		Menu.addMenuItem( mFile, "gui.menu.File.Exit", 'X', this, "exit" );

		// Bearbeiten menue
		Menu.addMenuItem( mEdit, "gui.menu.Edit.FloorNew", this, "new" );
		Menu.addMenuItem( mEdit, "gui.menu.Edit.FloorUp", this, "up" );
		Menu.addMenuItem( mEdit, "gui.menu.Edit.FloorDown", this, "down" );
		Menu.addMenuItem( mEdit, "gui.menu.Edit.FloorDelete", this, "delete" );
		Menu.addMenuItem( mEdit, "gui.menu.Edit.FloorCopy", this, "copy" );
		Menu.addMenuItem( mEdit, "gui.menu.Edit.FloorImport", this, "import" );
		Menu.addMenuItem( mEdit, "-" );
		Menu.addMenuItem( mEdit, "gui.menu.Edit.Rasterize", 'R', InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK, this, "rasterize" );
		Menu.addMenuItem( mEdit, "-" );
		Menu.addMenuItem( mEdit, "gui.menu.Edit.DistributeEvacuees", this, "distributeEvacuees" );
		Menu.addMenuItem( mEdit, "-" );
		Menu.addMenuItem( mEdit, "gui.menu.Edit.Distributions", 'V', this, "distribution" );
		Menu.addMenuItem( mEdit, "gui.menu.Edit.Properties", 'P', this, "properties" );

		// Anzeige-menue
		JMenu mVisibleAreas = Menu.addMenu( mView, "gui.menu.View.VisibleAreas" );
		mnuShowAllAreas = Menu.addMenuItem( mVisibleAreas, "gui.menu.View.ShowAllAreas", this, "showAll" );
		mnuShowAllAreas.setEnabled( false );
		mnuHideAllAreas = Menu.addMenuItem( mVisibleAreas, "gui.menu.View.HideAllAreas", this, "hideAll" );
		mVisibleAreas.addSeparator();
		mnuDelayArea = Menu.addCheckMenuItem( mVisibleAreas, "gui.menu.View.ShowDelayAreas", true, this, "delayArea" );
		mnuStairArea = Menu.addCheckMenuItem( mVisibleAreas, "gui.menu.View.ShowStairAreas", true, this, "stairArea" );
		mnuEvacuationArea = Menu.addCheckMenuItem( mVisibleAreas, "gui.menu.View.ShowEvacuationAreas", true, this, "evacuationArea" );
		mnuInaccessibleArea = Menu.addCheckMenuItem( mVisibleAreas, "gui.menu.View.ShowInaccessibleAreas", true, this, "inaccessibleArea" );
		mnuSaveArea = Menu.addCheckMenuItem( mVisibleAreas, "gui.menu.View.ShowSaveAreas", true, this, "saveArea" );
		mnuAssignmentArea = Menu.addCheckMenuItem( mVisibleAreas, "gui.menu.View.ShowAssignmentAreas", true, this, "assignmentArea" );
		Menu.addMenuItem( mView, "-" );
		JMenu mGrid = Menu.addMenu( mView, "gui.menu.View.Gridstyle" );
		mnuGridLines = Menu.addRadioButtonMenuItem( mGrid, "gui.menu.View.GridstyleLines", false, this, "gridLine" );
		mnuGridPoints = Menu.addRadioButtonMenuItem( mGrid, "gui.menu.View.GridstylePoints", true, this, "gridPoint" );
		mnuGridNotVisible = Menu.addRadioButtonMenuItem( mGrid, "gui.menu.View.GridstyleNone", false, this, "gridNo" );
		mnuPaintRasterized = Menu.addCheckMenuItem( mView, "gui.menu.View.DrawOnGrid", true, this, "grid" );
		mnuHideDefaultFloor = Menu.addCheckMenuItem( mView, "gui.menu.View.HideDefaultEvacuationFloor", PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor") , this, "defaultFloor" );
		mView.addSeparator();
		Menu.addMenuItem( mView, "gui.menu.View.Screenshot", KeyEvent.VK_F12, this, "screenshot", 0 );

		// execute menu
		Menu.addMenuItem( mExecute, "gui.menu.Execute.CreateCellularAutomaton", this, "createCellularAutomaton" );
		//mnuExecuteCreateGraph = Menu.addMenuItem( mExecute, "gui.menu.Execute.CreateGraph", this, "createGraph" );
		JMenu mCreateGraph = Menu.addMenu( mExecute, "gui.menu.Execute.CreateGraph" );
		//mnuCreateGraphCompleteGraph = Menu.addMenuItem( mCreateGraph, "gui.menu.Execute.CreateGraph.CompleteGraph", this, "completeGraph" );
		JMenu mCreateGraphCompleteGraph = Menu.addMenu( mCreateGraph, "gui.menu.Execute.CreateGraph.CompleteGraph" );
		//mnuCreateGraphCompleteGrid = Menu.addMenuItem(mCreateGraphCompleteGraph, loc.getString("Execute.CreateGraph.CompleteGraph.Grid"), KeyEvent.VK_9, this, "completeGrid", InputEvent.CTRL_DOWN_MASK );
		Menu.addMenuItem( mCreateGraphCompleteGraph, "gui.menu.Execute.CreateGraph.CompleteGraph.Grid", KeyEvent.VK_6, this, "completeGrid", InputEvent.CTRL_DOWN_MASK );
		Menu.addMenuItem( mCreateGraphCompleteGraph, "gui.menu.Execute.CreateGraph.CompleteGraph.NonGrid", KeyEvent.VK_4, this, "completeNonGrid", InputEvent.CTRL_DOWN_MASK );
		if( ZETLoader.isDebug() ) {
			JMenu mCreateGraphSpannerUsingPrim = Menu.addMenu( mCreateGraph, "gui.menu.Execute.CreateGraph.Prim" );
			Menu.addMenuItem( mCreateGraphSpannerUsingPrim, "gui.menu.Execute.CreateGraph.Prim.Grid", KeyEvent.VK_7, this, "PrimGrid", InputEvent.CTRL_DOWN_MASK );
			Menu.addMenuItem( mCreateGraphSpannerUsingPrim, "gui.menu.Execute.CreateGraph.Prim.NonGrid", KeyEvent.VK_5, this, "PrimNonGrid", InputEvent.CTRL_DOWN_MASK );
			JMenu mCreateGraphSpannerUsingGreedy = Menu.addMenu( mCreateGraph, "gui.menu.Execute.CreateGraph.Greedy" );
			Menu.addMenuItem( mCreateGraphSpannerUsingGreedy, "gui.menu.Execute.CreateGraph.Greedy.Grid", KeyEvent.VK_2, this, "GreedyGrid", InputEvent.CTRL_DOWN_MASK );
			Menu.addMenuItem( mCreateGraphSpannerUsingGreedy, "gui.menu.Execute.CreateGraph.Greedy.NonGrid", KeyEvent.VK_3, this, "GreedyNonGrid", InputEvent.CTRL_DOWN_MASK );
			JMenu mCreateGraphSpannerUsingDijkstra = Menu.addMenu( mCreateGraph, "gui.menu.Execute.CreateGraph.ShortestPathTree" );
			Menu.addMenuItem( mCreateGraphSpannerUsingDijkstra, "gui.menu.Execute.CreateGraph.ShortestPathTree.Grid", KeyEvent.VK_9, this, "DijkstraGrid", InputEvent.CTRL_DOWN_MASK );
			Menu.addMenuItem( mCreateGraphSpannerUsingDijkstra, "gui.menu.Execute.CreateGraph.ShortestPathTree.NonGrid", KeyEvent.VK_6, this, "DijkstraNonGrid", InputEvent.CTRL_DOWN_MASK );
			JMenu mCreateSteinerTree = Menu.addMenu( mCreateGraph, "gui.menu.Execute.CreateGraph.Steiner" );
			Menu.addMenuItem( mCreateSteinerTree, "gui.menu.Execute.CreateGraph.Steiner.Grid", KeyEvent.VK_9, this, "SteinerGrid", InputEvent.CTRL_DOWN_MASK );
			
			Menu.addMenuItem( mCreateSteinerTree, "gui.menu.Execute.CreateGraph.Steiner.NonGrid", KeyEvent.VK_9, this, "SteinerNonGrid", InputEvent.CTRL_DOWN_MASK );
			JMenu mCreateClusterGraph = Menu.addMenu( mCreateGraph, "gui.menu.Execute.CreateGraph.ClusterGraph" );
			Menu.addMenuItem( mCreateClusterGraph, "gui.menu.Execute.CreateGraph.ClusterGraph.NonGrid", KeyEvent.VK_9, this, "ClusterNonGrid", InputEvent.CTRL_DOWN_MASK );
			Menu.addMenuItem( mCreateClusterGraph, "gui.menu.Execute.CreateGraph.ClusterGraph.Grid", KeyEvent.VK_9, this, "ClusterGrid", InputEvent.CTRL_DOWN_MASK );
			JMenu mCreateShortestPathGraph = Menu.addMenu( mCreateGraph, "gui.menu.Execute.CreateGraph.ShortestPathGraph" );
			Menu.addMenuItem( mCreateShortestPathGraph, "gui.menu.Execute.CreateGraph.ShortestPathGraph.Grid", KeyEvent.VK_9, this, "ShortestPathGraphGrid", InputEvent.CTRL_DOWN_MASK );
			Menu.addMenuItem( mCreateShortestPathGraph, "gui.menu.Execute.CreateGraph.ShortestPathGraph.NonGrid", KeyEvent.VK_9, this, "ShortestPathGraphNonGrid", InputEvent.CTRL_DOWN_MASK );
			JMenu mCreateAPSPGraph = Menu.addMenu( mCreateGraph, "gui.menu.Execute.CreateGraph.APSPGraph" );
			Menu.addMenuItem( mCreateAPSPGraph, "gui.menu.Execute.CreateGraph.APSPGraph.NonGrid", KeyEvent.VK_9, this, "APSPGraphNonGrid", InputEvent.CTRL_DOWN_MASK );
			Menu.addMenuItem( mCreateAPSPGraph, "gui.menu.Execute.CreateGraph.APSPGraph.Grid", KeyEvent.VK_9, this, "APSPGraphGrid", InputEvent.CTRL_DOWN_MASK );
			JMenu mCreateShortestPaths = Menu.addMenu( mCreateGraph, "gui.menu.Execute.CreateGraph.ShortestPaths" );
			Menu.addMenuItem( mCreateShortestPaths, "gui.menu.Execute.CreateGraph.ShortestPaths.Grid", KeyEvent.VK_9, this, "ShortestPathsGrid", InputEvent.CTRL_DOWN_MASK );
			Menu.addMenuItem( mCreateShortestPaths, "gui.menu.Execute.CreateGraph.ShortestPaths.NonGrid", KeyEvent.VK_9, this, "ShortestPathsNonGrid", InputEvent.CTRL_DOWN_MASK );
			Menu.addMenuItem( mCreateGraph, "gui.menu.Execute.CreateGraph.ThinNetwork", KeyEvent.VK_9, this, "ThinNet", InputEvent.CTRL_DOWN_MASK );
		}

		Menu.addMenuItem( mExecute, "gui.menu.Execute.ApplyConcreteAssignment", this, "applyConcreteAssignment" );
		JMenu mSimulation = Menu.addMenu( mExecute, "gui.menu.Execute.Simulation" );
		Menu.addMenuItem( mSimulation, "gui.menu.Execute.Simulation.QuickVisualization", KeyEvent.VK_F5, this, "quickVisualization", 0 );
		Menu.addMenuItem( mSimulation, "gui.menu.Execute.Simulation.Start", KeyEvent.VK_F5, this, "startSimulation", InputEvent.CTRL_DOWN_MASK );
		Menu.addMenuItem( mSimulation, "gui.menu.Execute.Simulation.PauseQuickVisualization", KeyEvent.VK_F6, this, "visualizationPause", 0 );
		Menu.addMenuItem( mSimulation, "gui.menu.Execute.Simulation.StepByStep", KeyEvent.VK_F7, this, "stepByStepSimulation", 0 );
		Menu.addMenuItem( mSimulation, "-" );
		JMenu mSimulationAlgorithm = Menu.addMenu( mSimulation, loc.getString("Execute.Simulation.Algorithms" ) );
		ButtonGroup grpCellularAutomatonAlgorithms = new ButtonGroup();
		CellularAutomatonAlgorithms active = control.getSimulationAlgorithm();
		for( CellularAutomatonAlgorithms algorithm : CellularAutomatonAlgorithms.values() ) {
			JRadioButtonMenuItem mnuCellularAutomatonAlgorithm = Menu.addRadioButtonMenuItem( mSimulationAlgorithm, algorithm.getName(), algorithm == active, simulationAlgorithmListener, algorithm.toString() );
			grpCellularAutomatonAlgorithms.add( mnuCellularAutomatonAlgorithm );
		}


		JMenu mOptimization = Menu.addMenu( mExecute, "gui.menu.Execute.Optimization" );
		Menu.addMenuItem( mOptimization, "gui.menu.Execute.Optimization.AlgoEATransshipment", KeyEvent.VK_F8, this, "EAT", 0 );
    if( ZETLoader.isDebug() )
			Menu.addMenuItem( mOptimization, "gui.menu.Execute.Optimization.EATCompare", KeyEvent.VK_F9, this, "EATCompare", 0 );
		//mnuExecuteQuickestTransshipment = Menu.addMenuItem( mOptimization, "gui.menu.Execute.Optimization.AlgoQuickestTransshipment", this, "QT" );
		//mnuExecuteMaxFlowOverTimeMC = Menu.addMenuItem( mOptimization, "gui.menu.Execute.Optimization.AlgoMaxFlowOverTimeMinCost", this, "MFOTMC" );
		//mnuExecuteMaxFlowOverTimeTEN = Menu.addMenuItem( mOptimization, "gui.menu.Execute.Optimization.AlgoMaxFlowOverTimeTEN", this, "MFOTTEN" );
		JMenu mExitAssignment = Menu.addMenu( mExecute, "gui.menu.Execute.ExitAssignment" );
		Menu.addMenuItem( mExitAssignment, "gui.menu.Execute.ExitAssignment.EAT", this, "ExitAssignmentEAT" );

		Menu.addMenuItem( mExecute, "-" );

		JMenu mProperties = Menu.addMenu( mExecute, "Properties" );
		ButtonGroup grpProperties = new ButtonGroup();

		properties = PropertyFilesSelectionModel.loadPath( Paths.get( "./properties" ) );

		mnuProperties = new JRadioButtonMenuItem[properties.size()];
		int counter = 0;
		for( PropertyListEntry e : properties ) {
			boolean marked = e.getName().equals( "Standard-Eigenschaften" ) ? true : false;
			mnuProperties[counter] = Menu.addRadioButtonMenuItem( mProperties, e.getName(), marked, this, "property" + counter );
			grpProperties.add( mnuProperties[counter++] );
		}

		// Extras-Menu
		JMenu mLanguage = Menu.addMenu( mExtras, "gui.menu.Extras.Languages" );
		ButtonGroup grpLanguage = new ButtonGroup();
		
		JRadioButtonMenuItem english = null;
		JRadioButtonMenuItem first = null;		
		boolean localeFound = true;
		for( Locale locale : loc.getSupportedLocales() ) {
			JRadioButtonMenuItem tmp = Menu.addRadioButtonMenuItem( mLanguage, locale.getDisplayName(), false, this, "locale" );
			grpLanguage.add( tmp );
			if( first == null )
				first = tmp;
			if( locale.getLanguage().equals( LocalizationManager.getManager().getLocale().getLanguage() ) ) {
				tmp.setSelected( true );
				localeFound = true;
			}
			tmp.putClientProperty( "locale", locale );
			if( locale.equals( Locale.ENGLISH ) )
				english = tmp;
		}
		if( !localeFound ) { // Safely set any locale, if existing.
			if( english != null )
				english.setSelected( true );
			else if( first != null )
				first.setSelected( true );
		}
		

		Menu.addMenuItem( mExtras, "-" );
		JMenu mPlanImage = Menu.addMenu( mExtras, "gui.menu.Extras.PlanDisplaying" );
		Menu.addMenuItem( mPlanImage, "gui.menu.Extras.LoadPlan", this, "loadBuildingPlan" );
		mnuPlanImageHide = Menu.addMenuItem( mPlanImage, "gui.menu.Extras.HidePlan", this, "hideBuildingPlan" );
		mnuPlanImageHide.setEnabled( false );
		mnuPlanImageResize = Menu.addMenuItem( mPlanImage, "gui.menu.Extras.ResizePlan", this, "resizeBuildingPlan" );
		mnuPlanImageResize.setEnabled( false );
		mnuPlanImageLocate = Menu.addMenuItem( mPlanImage, "gui.menu.Extras.MovePlan", this, "moveBuildingPlan" );
		mnuPlanImageLocate.setEnabled( false );
		mnuPlanImageTransparency = Menu.addMenuItem( mPlanImage, "gui.menu.Extras.SetPlanTransparency", this, "transparencyBuldingPlan" );
		mnuPlanImageTransparency.setEnabled( false );
		Menu.addMenuItem( mExtras, "-" );
		Menu.addMenuItem( mExtras, "gui.menu.Extras.Options", 'T', this, "options" );
		if( ZETLoader.isDebug() ) {
			Menu.addMenuItem( mExtras, "gui.menu.Extras.Settings", this, "settings" );
			JMenu mnuDebug = Menu.addMenu( mExtras, "gui.menu.Extras.Debug" );

			Menu.addMenuItem( mnuDebug, "Building status", this, "debugBuildingStatus" );
			Menu.addMenuItem( mnuDebug, "Door-Check (with auto correction)", this, "debugDoorCheck" );
			Menu.addMenuItem( mnuDebug, "Check", this, "debugCheck" );
			Menu.addMenuItem( mnuDebug, "Output graph as .dat", this, "outputGraph" );
		}
		Menu.addMenuItem( mHelp, "gui.menu.Help.About", 'I', this, "about" );

		loc.setPrefix( "" );
	}

	ActionListener simulationAlgorithmListener = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			final CellularAutomatonAlgorithms cellularAutomaton = CellularAutomatonAlgorithms.valueOf( e.getActionCommand() );
			if( cellularAutomaton != null )
				control.setSimulationAlgorithm( cellularAutomaton );
			else
				throw new AssertionError();
		}
	};

	@Override
	public void actionPerformed( ActionEvent e ) {
		if( e.getActionCommand().equals( "exit" ) ) {
			// quits the program
			control.exit();
		} else if( e.getActionCommand().equals( "loadProject" ) ) {
			control.loadProject();
		} else if( e.getActionCommand().equals( "saveProjectAs" ) ) {
			control.saveProjectAs();
		} else if( e.getActionCommand().equals( "saveProject" ) ) {
			control.saveProject();
		} else if( e.getActionCommand().equals( "newProject" ) ) {
			control.newProject();
		} else if( e.getActionCommand().equals( "loadBatchResult" ) ) {
			// TODO load batch result
		} else if( e.getActionCommand().equals( "saveResultAs" ) ) {
			// TODO save batch result
		} else if( e.getActionCommand().equals( "saveAsDXF" ) ) {
			control.saveAsDXF();
		} else if( e.getActionCommand().equals( "new" ) ) {
			control.newFloor();
		} else if( e.getActionCommand().equals( "up" ) ) {
			control.moveFloorUp();
		} else if( e.getActionCommand().equals( "down" ) ) {
			control.moveFloorDown();
		} else if( e.getActionCommand().equals( "delete" ) ) {
			control.deleteFloor();
		} else if( e.getActionCommand().equals( "import" ) ) {
			control.importFloor();
		} else if( e.getActionCommand().equals( "copy" ) ) {
			control.copyFloor();
		} else if( e.getActionCommand().equals( "rasterize" ) ) {
			control.rasterize();
		} else if( e.getActionCommand().equals( "distributeEvacuees" ) ) {
			control.distributeEvacuees();
		} else if( e.getActionCommand().equals( "distribution" ) ) {
			control.showAssignmentDialog();
		} else if( e.getActionCommand().equals( "properties" ) ) {
			control.showPropertiesDialog();
		} else if( e.getActionCommand().equals("showAll") ) {
			mnuDelayArea.setSelected( true );
			mnuStairArea.setSelected( true );
			mnuEvacuationArea.setSelected( true );
			mnuInaccessibleArea.setSelected( true );
			mnuSaveArea.setSelected( true );
			mnuAssignmentArea.setSelected( true );
			control.updateVisibility( true );
		} else if( e.getActionCommand().equals( "hideAll" ) ) {
			mnuDelayArea.setSelected( false );
			mnuStairArea.setSelected( false );
			mnuEvacuationArea.setSelected( false );
			mnuInaccessibleArea.setSelected( false );
			mnuSaveArea.setSelected( false );
			mnuAssignmentArea.setSelected( false );
			control.updateVisibility( false );
		} else if( e.getActionCommand().equals( "delayArea" ) ) {
			control.updateVisibility( Areas.Delay, mnuDelayArea.isSelected() );
		} else if( e.getActionCommand().equals( "stairArea" ) ) {
			control.updateVisibility( Areas.Stair, mnuStairArea.isSelected() );
		} else if( e.getActionCommand().equals( "evacuationArea" ) ) {
			control.updateVisibility( Areas.Evacuation, mnuEvacuationArea.isSelected() );
		} else if( e.getActionCommand().equals( "inaccessibleArea" ) ) {
			control.updateVisibility( Areas.Inaccessible, mnuInaccessibleArea.isSelected() );
		} else if( e.getActionCommand().equals( "saveArea" ) ) {
			control.updateVisibility( Areas.Save, mnuSaveArea.isSelected() );
		} else if( e.getActionCommand().equals( "assignmentArea" ) ) {
			control.updateVisibility( Areas.Assignment, mnuAssignmentArea.isSelected() );
		} else if( e.getActionCommand().equals( "grid" ) ) {
			control.setRasterizedPaintMode( mnuPaintRasterized.isSelected() );
		} else if( e.getActionCommand().equals( "gridLine" ) ) {
			control.setRasterPaintStyle( RasterPaintStyle.Lines );
		} else if( e.getActionCommand().equals( "gridPoint" ) ) {
			control.setRasterPaintStyle( RasterPaintStyle.Points );
		} else if( e.getActionCommand().equals( "gridNo" ) ) {
			control.setRasterPaintStyle( RasterPaintStyle.Nothing );
		} else if( e.getActionCommand().equals( "defaultFloor" ) ) {
			control.showDefaultFloor( mnuHideDefaultFloor.isSelected() );
		} else if( e.getActionCommand().equals( "loadBuildingPlan" ) ) {
			control.loadBuildingPlan();
		} else if( e.getActionCommand().equals( "hideBuildingPlan" ) ) {
			control.hideBuildingPlan();
		} else if( e.getActionCommand().equals( "resizeBuildingPlan" ) ) {
			control.resizeBuildingPlan();
		} else if( e.getActionCommand().equals( "moveBuildingPlan" ) ) {
			control.moveBuildingPlan();
		} else if( e.getActionCommand().equals( "transparencyBuldingPlan" ) ) {
			control.transparencyBuildingPlan();
		} else if( e.getActionCommand().equals( "screenshot" ) ) {
			control.takeScreenshot();
		} else if( e.getActionCommand().equals( "createCellularAutomaton" ) ) {
			System.out.println( "Creating buldingdatastructure" );
			control.createBuildingDataStructure();
			control.createCellularAutomaton();
		} /*else if( e.getActionCommand().equals( "createGraph" ) ) {
			control.createGraph("");
		}*/ else if( e.getActionCommand().equals( "completeGrid" ) )
			control.createGraph( GraphConverterAlgorithms.GridGraph );
		else if( e.getActionCommand().equals( "completeNonGrid" ) )
			control.createGraph( GraphConverterAlgorithms.NonGridGraph );
		else if( e.getActionCommand().equals( "PrimGrid" ) )
			control.createGraph( GraphConverterAlgorithms.MinSpanningTreeGrid );
		else if( e.getActionCommand().equals( "PrimNonGrid" ) )
			control.createGraph( GraphConverterAlgorithms.MinSpanningTreeNonGrid );
		else if( e.getActionCommand().equals( "GreedyGrid" ) )
			control.createGraph( GraphConverterAlgorithms.GreedyTSpannerGrid );
		else if( e.getActionCommand().equals( "GreedyNonGrid" ) )
			control.createGraph( GraphConverterAlgorithms.GreedyTSpannerNonGrid );
		else if( e.getActionCommand().equals( "DijkstraNonGrid" ) )
			control.createGraph( GraphConverterAlgorithms.DijkstraNonGrid );
		else if( e.getActionCommand().equals( "DijkstraGrid" ) )
			control.createGraph( GraphConverterAlgorithms.DijkstraGrid );
		else if( e.getActionCommand().equals( "SteinerNonGrid" ) )
			control.createGraph( GraphConverterAlgorithms.SteinerTreeNonGrid );
		else if( e.getActionCommand().equals( "SteinerGrid" ) )
			control.createGraph( GraphConverterAlgorithms.SteinerTreeGrid );
		else if( e.getActionCommand().equals( "ClusterNonGrid" ) )
			control.createGraph( GraphConverterAlgorithms.ClusterNonGrid );
		else if( e.getActionCommand().equals( "ClusterGrid" ) )
			control.createGraph( GraphConverterAlgorithms.ClusterGrid );
		else if( e.getActionCommand().equals( "ShortestPathGraphNonGrid" ) )
			control.createGraph( GraphConverterAlgorithms.ShortestPathGraphNonGrid );
		else if( e.getActionCommand().equals( "ShortestPathGraphGrid" ) )
			control.createGraph( GraphConverterAlgorithms.ShortestPathGraphGrid );
		else if( e.getActionCommand().equals( "APSPGraphNonGrid" ) )
			control.createGraph( GraphConverterAlgorithms.APSPGraphNonGrid );
		else if( e.getActionCommand().equals( "ShortestPathsNonGrid" ) )
			control.createGraph( GraphConverterAlgorithms.RepeatedShortestPaths );
		else if( e.getActionCommand().equals( "ThinNet" ) )
			control.createGraph( GraphConverterAlgorithms.ThinNetwork );
		else if( e.getActionCommand().equals( "applyConcreteAssignment" ) ) {
		} else if( e.getActionCommand().equals( "quickVisualization" ) ) {
			control.performQuickVisualization();
		} else if( e.getActionCommand().equals( "startSimulation" ) ) {
			control.performSimulation();
		} else if( e.getActionCommand().equals( "visualizationPause" ) ) {
			control.pauseSimulation();
		} else if( e.getActionCommand().equals( "stepByStepSimulation" ) ) {
			control.performOneStep();
		} else if( e.getActionCommand().equals( "EAT" ) ) {
			control.performOptimization();
		} else if( e.getActionCommand().equals( "EATCompare" ) )
			control.performOptimizationCompare();
		else if( e.getActionCommand().equals( "ExitAssignmentEAT" ) ) {
			control.performExitAssignmentEAT();
		} else if( e.getActionCommand().equals( "options" ) )
			control.showOptionsDialog();
		else if( e.getActionCommand().equals( "settings" ) ) {
			control.showSettingsDialog();
		} else if ( e.getActionCommand().equals( "debugBuildingStatus" ) ) {
			System.out.println( control.getZControl().getProject().getBuildingPlan().summary() );
			// Show window
			if( ptv == null )
				ptv = new JProjectTreeView( control.editor, "Baumansicht", 600, 450, control.getZControl() );
			ptv.setVisible( true );
		} else if( e.getActionCommand().equals( "debugDoorCheck" ) ) {
			control.getZControl().autoCorrectEdges();
		} else if( e.getActionCommand().equals( "debugCheck" ) ) {
			control.getZControl().checkDebugOut();
		} else if( e.getActionCommand().equals( "locale" ) ) {
			control.switchToLanguage( (Locale)((JRadioButtonMenuItem)e.getSource()).getClientProperty( "locale" ) );
		} else if( e.getActionCommand().equals( "outputInformation" ) ) {
			
			control.outputInformation();
		} else if( e.getActionCommand().equals( "outputGraph" ) ) {
			control.outputGraph();
		} else if( e.getActionCommand().equals( "about" ) ) {
			control.showAbout(); // show the about screen
		} else if( e.getActionCommand().startsWith( "property") ) {
			int p = Integer.parseInt( e.getActionCommand().substring( 8 ) );
				PropertyListEntry entry = properties.get( p );
				try {
					System.out.println( "Loading property " + entry.getName() );
					PropertyContainer.getInstance().applyParameters( entry.getFile() );
					//init( ptm2 );
				} catch( PropertyLoadException ex ) {
					ex.printStackTrace( System.err );
				}

		} else
			ZETLoader.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
	}

	public void setEnabledShowAllAreas( boolean b ) {
		mnuShowAllAreas.setEnabled( b );
	}

	public void setEnabledHideAllAreas( boolean b ) {
		mnuHideAllAreas.setEnabled( b );
	}

	public void setSelectedGridPoints( boolean b ) {
		mnuGridPoints.setSelected( b );
	}

	public void setSelectedGridLines( boolean b ) {
		mnuGridLines.setSelected( b );
	}

	public void setSelectedGridNotVisible( boolean b ) {
		mnuGridNotVisible.setSelected( b );
	}

	public void setEnabledBuildingPlan( boolean b ) {
		mnuPlanImageHide.setEnabled( b );
		mnuPlanImageResize.setEnabled( b);
		mnuPlanImageLocate.setEnabled( b);
		mnuPlanImageTransparency.setEnabled( b );
	}

	@Override
	public void localize() {
	}
}