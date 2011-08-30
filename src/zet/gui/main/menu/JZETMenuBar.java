/**
 * JZETMenuBar.java
 * Created: 21.07.2010 10:03:06
 */
package zet.gui.main.menu;

import de.tu_berlin.math.coga.common.localization.Localized;
import ds.PropertyContainer;
import ds.z.exception.RoomIntersectException;
import gui.GUIControl;
import gui.ZETMain;
import gui.components.framework.Menu;
import gui.editor.Areas;
import zet.gui.treeview.JProjectTreeView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Locale;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import zet.gui.main.tabs.base.AbstractFloor.RasterPaintStyle;
import zet.gui.GUILocalization;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JZETMenuBar extends JMenuBar implements ActionListener, Localized {
	private final GUIControl control;
	/** The localization class. */
	static final GUILocalization loc = GUILocalization.getSingleton();
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
	private JMenuItem mnuExecuteCreateCellularAutomaton;
	//private JMenuItem mnuExecuteCreateGraph;
        private JMenu mCreateGraph;
        private JMenu mCreateGraphCompleteGraph;
        private JMenuItem mnuCreateGraphCompleteGrid;
        private JMenuItem mnuCreateGraphCompleteNonGrid;
        private JMenu mCreateGraphSpannerUsingPrim;
        private JMenuItem mnuCreateGraphSpannerUsingPrimGrid;
        private JMenuItem mnuCreateGraphSpannerUsingPrimNonGrid;
        private JMenu mCreateGraphSpannerUsingGreedy;
        private JMenuItem mnuCreateGraphSpannerUsingGreedyGrid;
        private JMenuItem mnuCreateGraphSpannerUsingGreedyNonGrid;
        private JMenu mCreateGraphSpannerUsingDijkstra;
        private JMenuItem mnuCreateGraphSpannerUsingDijkstraGrid;
        private JMenuItem mnuCreateGraphSpannerUsingDijkstraNonGrid;
	private JMenuItem mnuExecuteApplyAssignment;
	private JMenu mSimulation;
	private JMenuItem mnuSimulationQuickVisualization;;
	private JMenuItem mnuSimulationStart;
	private JMenuItem mnuSimulationPauseQuickVisualization;
	private JMenuItem mnuStepByStepSimulation;

	//private JMenuItem mnuExecutePauseSimulation;
	private JMenu mOptimization;
	private JMenuItem mnuOptimizationEarliestArrivalTransshipment;
	//private JMenuItem mnuExecuteQuickestTransshipment;
	//private JMenuItem mnuExecuteMaxFlowOverTimeMC;
	//private JMenuItem mnuExecuteMaxFlowOverTimeTEN;
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
	private JMenu mHelp;
	private JMenuItem mnuHelpAbout;
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
		loc.setPrefix( "gui.menu." );

		//JMenuBar bar = new JMenuBar();
		mFile = Menu.addMenu( this, loc.getString( "File" ) );
		mEdit = Menu.addMenu( this, loc.getString( "Edit" ) );
		mView = Menu.addMenu( this, loc.getString( "View" ) );
		if( ZETMain.isDebug() )
		mExecute = Menu.addMenu( this, loc.getString( "Execute" ) );
		mExtras = Menu.addMenu( this, loc.getString( "Extras" ) );
		mHelp = Menu.addMenu( this, loc.getString( "Help" ) );

		// Dateimenue
		mnuFileNew = Menu.addMenuItem( mFile, loc.getString( "File.New" ), 'N', this, "newProject" );
		Menu.addMenuItem( mFile, "-" );
		mnuFileOpen = Menu.addMenuItem( mFile, loc.getString( "File.Open" ), 'O', this, "loadProject" );
		mnuFileSave = Menu.addMenuItem( mFile, loc.getString( "File.Save" ), 'S', this, "saveProject" );
		mnuFileSaveAs = Menu.addMenuItem( mFile, loc.getString( "File.SaveAs" ), 'U', this, "saveProjectAs" );
		Menu.addMenuItem( mFile, "-" );
		mnuFileExportAsDXF = Menu.addMenuItem( mFile, loc.getString( "File.DXF" ), this, "saveAsDXF" );
		Menu.addMenuItem( mFile, "-" );
		mnuFileSaveResultAs = Menu.addMenuItem( mFile, loc.getString( "File.SaveResultAs" ), 'E', this, "saveResultAs" );
		mnuFileSaveResultAs.setEnabled( false );
		mnuFileLoadResult = Menu.addMenuItem( mFile, loc.getString( "File.LoadBatchResult" ), 'B', this, "loadBatchResult" );
		mnuFileLoadResult.setEnabled( false );
		Menu.addMenuItem( mFile, "-" );
		mnuFileExit = Menu.addMenuItem( mFile, loc.getString( "File.Exit" ), 'X', this, "exit" );

		// Bearbeiten menue
		mnuEditFloorNew = Menu.addMenuItem( mEdit, loc.getString( "Edit.FloorNew" ), this, "new" );
		mnuEditFloorUp = Menu.addMenuItem( mEdit, loc.getString( "Edit.FloorUp" ), this, "up" );
		mnuEditFloorDown = Menu.addMenuItem( mEdit, loc.getString( "Edit.FloorDown" ), this, "down" );
		mnuEditFloorDelete = Menu.addMenuItem( mEdit, loc.getString( "Edit.FloorDelete" ), this, "delete" );
		mnuEditFloorCopy = Menu.addMenuItem( mEdit, loc.getString( "Edit.FloorCopy" ), this, "copy" );
		mnuEditFloorImport = Menu.addMenuItem( mEdit, loc.getString( "Edit.FloorImport" ), this, "import" );

		Menu.addMenuItem( mEdit, "-" );
		mnuEditRasterize = Menu.addMenuItem( mEdit, loc.getString( "Edit.Rasterize" ), 'R', InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK, this, "rasterize" );
		Menu.addMenuItem( mEdit, "-" );
		mnuEditDistributeEvacuees = Menu.addMenuItem( mEdit, loc.getString( "Edit.DistributeEvacuees" ), this, "distributeEvacuees" );
		Menu.addMenuItem( mEdit, "-" );
		mnuEditDistribution = Menu.addMenuItem( mEdit, loc.getString( "Edit.Distributions" ), 'V', this, "distribution" );
		mnuEditProperties = Menu.addMenuItem( mEdit, loc.getString( "Edit.Properties" ), 'P', this, "properties" );

		// Anzeige-menue
		mVisibleAreas = Menu.addMenu( mView, loc.getString( "View.VisibleAreas" ) );
		mnuShowAllAreas = Menu.addMenuItem( mVisibleAreas, loc.getString( "View.ShowAllAreas" ), this, "showAll" );
		mnuShowAllAreas.setEnabled( false );
		mnuHideAllAreas = Menu.addMenuItem( mVisibleAreas, loc.getString( "View.HideAllAreas" ), this, "hideAll" );
		mVisibleAreas.addSeparator();
		mnuDelayArea = Menu.addCheckMenuItem( mVisibleAreas, loc.getString( "View.ShowDelayAreas" ), true, this, "delayArea" );
		mnuStairArea = Menu.addCheckMenuItem( mVisibleAreas, loc.getString( "View.ShowStairAreas" ), true, this, "stairArea" );
		mnuEvacuationArea = Menu.addCheckMenuItem( mVisibleAreas, loc.getString( "View.ShowEvacuationAreas" ), true, this, "evacuationArea" );
		mnuInaccessibleArea = Menu.addCheckMenuItem( mVisibleAreas, loc.getString( "View.ShowInaccessibleAreas" ), true, this, "inaccessibleArea" );
		mnuSaveArea = Menu.addCheckMenuItem( mVisibleAreas, loc.getString( "View.ShowSaveAreas" ), true, this, "saveArea" );
		mnuAssignmentArea = Menu.addCheckMenuItem( mVisibleAreas, loc.getString( "View.ShowAssignmentAreas" ), true, this, "assignmentArea" );
		Menu.addMenuItem( mView, "-" );
		mGrid = Menu.addMenu( mView, loc.getString( "View.Gridstyle" ) );
		mnuGridLines = Menu.addRadioButtonMenuItem( mGrid, loc.getString( "View.GridstyleLines" ), false, this, "gridLine" );
		mnuGridPoints = Menu.addRadioButtonMenuItem( mGrid, loc.getString( "View.GridstylePoints" ), true, this, "gridPoint" );
		mnuGridNotVisible = Menu.addRadioButtonMenuItem( mGrid, loc.getString( "View.GridstyleNone" ), false, this, "gridNo" );
		mnuPaintRasterized = Menu.addCheckMenuItem( mView, loc.getString( "View.DrawOnGrid" ), true, this, "grid" );
		mnuHideDefaultFloor = Menu.addCheckMenuItem( mView, loc.getString( "View.HideDefaultEvacuationFloor" ), PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" ), this, "defaultFloor" );
		mView.addSeparator();
		mnuScreenshot = Menu.addMenuItem( mView, loc.getString( "View.Screenshot" ), KeyEvent.VK_F12, this, "screenshot", 0 );

		// execute menu
		mnuExecuteCreateCellularAutomaton = Menu.addMenuItem( mExecute, loc.getString( "Execute.CreateCellularAutomaton" ), this, "createCellularAutomaton" );
		//mnuExecuteCreateGraph = Menu.addMenuItem( mExecute, loc.getString( "Execute.CreateGraph" ), this, "createGraph" );
                mCreateGraph = Menu.addMenu(mExecute, loc.getString("Execute.CreateGraph"));
                //mnuCreateGraphCompleteGraph = Menu.addMenuItem( mCreateGraph, loc.getString( "Execute.CreateGraph.CompleteGraph" ), this, "completeGraph" );
                mCreateGraphCompleteGraph = Menu.addMenu(mCreateGraph, loc.getString("Execute.CreateGraph.CompleteGraph"));
                //mnuCreateGraphCompleteGrid = Menu.addMenuItem(mCreateGraphCompleteGraph, loc.getString("Execute.CreateGraph.CompleteGraph.Grid"), KeyEvent.VK_9, this, "completeGrid");
                mnuCreateGraphCompleteGrid = Menu.addMenuItem(mCreateGraphCompleteGraph, loc.getString("Execute.CreateGraph.CompleteGraph.Grid"), KeyEvent.VK_6,this, "completeGrid",0);
                mnuCreateGraphCompleteNonGrid = Menu.addMenuItem(mCreateGraphCompleteGraph, loc.getString("Execute.CreateGraph.CompleteGraph.NonGrid"), KeyEvent.VK_4, this, "completeNonGrid",0);
                mCreateGraphSpannerUsingPrim = Menu.addMenu(mCreateGraph, loc.getString("Execute.CreateGraph.Prim"));
                mnuCreateGraphSpannerUsingPrimGrid = Menu.addMenuItem(mCreateGraphSpannerUsingPrim, loc.getString("Execute.CreateGraph.Prim.Grid"), KeyEvent.VK_7, this, "PrimGrid",0);
                mnuCreateGraphSpannerUsingPrimNonGrid = Menu.addMenuItem(mCreateGraphSpannerUsingPrim, loc.getString("Execute.CreateGraph.Prim.NonGrid"), KeyEvent.VK_5, this, "PrimNonGrid",0);
                mCreateGraphSpannerUsingGreedy = Menu.addMenu(mCreateGraph, loc.getString("Execute.CreateGraph.Greedy"));
                mnuCreateGraphSpannerUsingGreedyGrid = Menu.addMenuItem(mCreateGraphSpannerUsingGreedy, loc.getString("Execute.CreateGraph.Greedy.Grid"), KeyEvent.VK_2, this, "GreedyGrid",0);
                mnuCreateGraphSpannerUsingGreedyNonGrid = Menu.addMenuItem(mCreateGraphSpannerUsingGreedy, loc.getString("Execute.CreateGraph.Greedy.NonGrid"), KeyEvent.VK_7, this, "GreedyNonGrid",0);
                mCreateGraphSpannerUsingDijkstra = Menu.addMenu(mCreateGraph, loc.getString("Execute.CreateGraph.ShortestPathTree"));
                mnuCreateGraphSpannerUsingDijkstraGrid = Menu.addMenuItem(mCreateGraphSpannerUsingDijkstra, loc.getString("Execute.CreateGraph.ShortestPathTree.Grid"), KeyEvent.VK_9, this, "DijkstraGrid",0);
                mnuCreateGraphSpannerUsingDijkstraNonGrid = Menu.addMenuItem(mCreateGraphSpannerUsingDijkstra, loc.getString("Execute.CreateGraph.ShortestPathTree.NonGrid"), KeyEvent.VK_6, this, "DijkstraNonGrid",0);
		mnuExecuteApplyAssignment = Menu.addMenuItem( mExecute, loc.getString( "Execute.ApplyConcreteAssignment" ), this, "applyConcreteAssignment" );
		mSimulation = Menu.addMenu( mExecute, loc.getString( "Execute.Simulation" ) );
		mnuSimulationQuickVisualization = Menu.addMenuItem( mSimulation, loc.getString( "Execute.Simulation.QuickVisualization" ), KeyEvent.VK_F5, this, "quickVisualization", 0 );
		mnuSimulationStart = Menu.addMenuItem( mSimulation, loc.getString( "Execute.Simulation.Start" ), KeyEvent.VK_F5, this, "startSimulation", InputEvent.CTRL_DOWN_MASK );
		mnuSimulationPauseQuickVisualization = Menu.addMenuItem( mSimulation, loc.getString( "Execute.Simulation.PauseQuickVisualization" ), KeyEvent.VK_F6, this, "visualizationPause", 0 );
		mnuStepByStepSimulation = Menu.addMenuItem( mSimulation, loc.getString( "Execute.Simulation.StepByStep" ), KeyEvent.VK_F7, this, "stepByStepSimulation", 0 );

		mOptimization = Menu.addMenu( mExecute, loc.getString( "Execute.Optimization" ) );
		mnuOptimizationEarliestArrivalTransshipment = Menu.addMenuItem( mOptimization, loc.getString( "Execute.Optimization.AlgoEATransshipment" ), KeyEvent.VK_F8, this, "EAT", 0 );
		//mnuExecuteQuickestTransshipment = Menu.addMenuItem( mOptimization, loc.getString( "Execute.Optimization.AlgoQuickestTransshipment" ), this, "QT" );
		//mnuExecuteMaxFlowOverTimeMC = Menu.addMenuItem( mOptimization, loc.getString( "Execute.Optimization.AlgoMaxFlowOverTimeMinCost" ), this, "MFOTMC" );
		//mnuExecuteMaxFlowOverTimeTEN = Menu.addMenuItem( mOptimization, loc.getString( "Execute.Optimization.AlgoMaxFlowOverTimeTEN" ), this, "MFOTTEN" );

		// Extras-Menu
		mLanguage = Menu.addMenu( mExtras, loc.getString( "Extras.Languages" ) );
		ButtonGroup grpLanguage = new ButtonGroup();

		JRadioButtonMenuItem mnuGerman;
		JRadioButtonMenuItem mnuEnglish;
		if( loc.getLocale().equals( Locale.GERMAN ) ) {
			mnuGerman = Menu.addRadioButtonMenuItem( mLanguage, "_Deutsch", true, this, "german" );
			mnuEnglish = Menu.addRadioButtonMenuItem( mLanguage, "_english", false, this, "english" );
		} else {
			mnuGerman = Menu.addRadioButtonMenuItem( mLanguage, "_Deutsch", false, this, "german" );
			mnuEnglish = Menu.addRadioButtonMenuItem( mLanguage, "_english", true, this, "english" );
		}
		grpLanguage.add( mnuGerman );
		grpLanguage.add( mnuEnglish );

		mnuLanguages[0] = mnuGerman;
		mnuLanguages[1] = mnuEnglish;
		Menu.addMenuItem( mExtras, "-" );
		mPlanImage = Menu.addMenu( mExtras, loc.getString( "Extras.PlanDisplaying" ) );
		mnuPlanImageLoad = Menu.addMenuItem( mPlanImage, loc.getString( "Extras.LoadPlan" ), this, "loadBuildingPlan" );
		mnuPlanImageHide = Menu.addMenuItem( mPlanImage, loc.getString( "Extras.HidePlan" ), this, "hideBuildingPlan" );
		mnuPlanImageHide.setEnabled( false );
		mnuPlanImageResize = Menu.addMenuItem( mPlanImage, loc.getString( "Extras.ResizePlan" ), this, "resizeBuildingPlan" );
		mnuPlanImageResize.setEnabled( false );
		mnuPlanImageLocate = Menu.addMenuItem( mPlanImage, loc.getString( "Extras.MovePlan" ), this, "moveBuildingPlan" );
		mnuPlanImageLocate.setEnabled( false );
		mnuPlanImageTransparency = Menu.addMenuItem( mPlanImage, loc.getString( "Extras.SetPlanTransparency" ), this, "transparencyBuldingPlan" );
		mnuPlanImageTransparency.setEnabled( false );
		Menu.addMenuItem( mExtras, "-" );
		mnuOptions = Menu.addMenuItem( mExtras, loc.getString( "Extras.Options" ), 'T', this, "options" );
		if( ZETMain.isDebug() ) {
			mnuSettings = Menu.addMenuItem( mExtras, loc.getString( "Extras.Settings" ), this, "settings" );
			mnuDebug = Menu.addMenu( mExtras, loc.getString( "Extras.Debug" ) );

			Menu.addMenuItem( mnuDebug, "Building status", this, "debugBuildingStatus" );
			Menu.addMenuItem( mnuDebug, "Door-Check (with auto correction)", this, "debugDoorCheck" );
			Menu.addMenuItem( mnuDebug, "Check", this, "debugCheck" );


		}
		// Hilfe-menu
		mnuHelpAbout = Menu.addMenuItem( mHelp, loc.getString( "Help.About" ), 'I', this, "about" );

		loc.setPrefix( "" );
	}

//			} else if( e.getActionCommand().equals( "loadBatchResult" ) ) {
//				if( jfcResults.showOpenDialog( getInstance() ) == JFileChooser.APPROVE_OPTION ) {
//					GUIOptionManager.setSavePathResults( jfcProject.getCurrentDirectory().getPath() );
//					try {
//						setBatchResult( BatchResult.load( (jfcResults.getSelectedFile()) ) );
//						ZETMain.sendMessage( loc.getString( "gui.editor.JEditor.message.loaded" ) );
//					} catch( Exception ex ) {
//						showErrorMessage( loc.getString( "gui.editor.JEditor.error.SaveTitle" ), loc.getString( "gui.editor.JEditor.error.Save" ) );
//						ex.printStackTrace();
//					}
//				}
//			} else if( e.getActionCommand().equals( "saveResultAs" ) ) {
//				if( jfcResults.showSaveDialog( instance ) == JFileChooser.APPROVE_OPTION ) {
//					GUIOptionManager.setSavePathResults( jfcProject.getCurrentDirectory().getPath() );
//					try {
//						File target = jfcResults.getSelectedFile();
//						if( !target.getName().endsWith( ".ers" ) )
//							target = new File( target.getAbsolutePath() + ".ers" );
//						result.save( target );
//					} catch( java.lang.StackOverflowError soe ) {
//						showErrorMessage( loc.getString( "gui.editor.JEditor.error.stackOverflowTitle" ), loc.getString( "gui.editor.JEditor.error.stackOverflow" ) );
//					} catch( Exception ex ) {
//						showErrorMessage( loc.getString( "gui.editor.JEditor.error.SaveTitle" ), loc.getString( "gui.editor.JEditor.error.Save" ) );
//						ex.printStackTrace();
//					}
//					ZETMain.sendMessage( loc.getString( "gui.editor.JEditor.message.saved" ) );
//				}


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
                }*/else if (e.getActionCommand().equals("completeGrid")) {     
                        control.createGraph("GridGraph");
                }  
                else if (e.getActionCommand().equals("completeNonGrid")) {     
                        control.createGraph("NonGridGraph");
                }          
                else if (e.getActionCommand().equals("PrimGrid")) {     
                        control.createGraph("MinSpanTree(Grid)");
                }
                else if (e.getActionCommand().equals("PrimNonGrid")) {     
                        control.createGraph("MinSpanTree(NonGrid)");
                }else if (e.getActionCommand().equals("GreedyGrid")) {     
                        control.createGraph("Greedy t-Spanner(Grid)");       
                }else if (e.getActionCommand().equals("GreedyNonGrid")) {     
                        control.createGraph("Greedy t-Spanner(NonGrid)");       
                }else if (e.getActionCommand().equals("DijkstraNonGrid")) {     
                        control.createGraph("Dijkstra For Non Grid");       
                }else if (e.getActionCommand().equals("DijkstraGrid")) {     
                        control.createGraph("Dijkstra For Grid");       
                }else if( e.getActionCommand().equals( "applyConcreteAssignment" ) ) {
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
		} else if( e.getActionCommand().equals( "options" ) ) {
			control.showOptionsDialog();
		} else if( e.getActionCommand().equals( "settings" ) ) {
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
		} else if( e.getActionCommand().equals( "german" ) ) {
			control.switchToLanguage( Locale.GERMAN );
		} else if( e.getActionCommand().equals( "english" ) ) {
			control.switchToLanguage( Locale.ENGLISH );
		} else if( e.getActionCommand().equals( "outputInformation" ) ) {
			control.outputInformation();
		} else if( e.getActionCommand().equals( "outputGraph" ) ) {
			control.outputGraph();
		} else if( e.getActionCommand().equals( "about" ) ) {
			control.showAbout(); // show the about screen
		} else
			ZETMain.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
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

	/**
	 * Changes the localization texts for the menu items.
	 */
	@Override
	public void localize() {
		loc.setPrefix( "gui.menu." );
		Menu.updateMenu( mFile, loc.getString( "File" ) );
		Menu.updateMenu( mEdit, loc.getString( "Edit" ) );
		Menu.updateMenu( mView, loc.getString( "View" ) );
		Menu.updateMenu( mExtras, loc.getString( "Extras" ) );
		Menu.updateMenu( mHelp, loc.getString( "Help" ) );

		// Dateimenu
		Menu.updateMenu( mnuFileNew, loc.getString( "File.New" ) );
		Menu.updateMenu( mnuFileOpen, loc.getString( "File.Open" ) );
		Menu.updateMenu( mnuFileSave, loc.getString( "File.Save" ) );
		Menu.updateMenu( mnuFileSaveAs, loc.getString( "File.SaveAs" ) );
		Menu.updateMenu( mnuFileExportAsDXF, loc.getString( "File.DXF" ) );
		Menu.updateMenu( mnuFileSaveResultAs, loc.getString( "File.SaveResultAs" ) );
		Menu.updateMenu( mnuFileLoadResult, loc.getString( "File.LoadBatchResult" ) );
		Menu.updateMenu( mnuFileExit, loc.getString( "File.Exit" ) );

		// Bearbeiten menu
		Menu.updateMenu( mnuEditFloorNew, loc.getString( "Edit.FloorNew" ) );
		Menu.updateMenu( mnuEditFloorUp, loc.getString( "Edit.FloorUp" ) );
		Menu.updateMenu( mnuEditFloorDown, loc.getString( "Edit.FloorDown" ) );
		Menu.updateMenu( mnuEditFloorDelete, loc.getString( "Edit.FloorDelete" ) );
		Menu.updateMenu( mnuEditFloorCopy, loc.getString( "Edit.FloorCopy" ) );
		Menu.updateMenu( mnuEditFloorImport, loc.getString( "Edit.FloorImport" ) );
		Menu.updateMenu( mnuEditRasterize, loc.getString( "Edit.Rasterize" ) );
		Menu.updateMenu( mnuEditDistributeEvacuees, loc.getString( "Edit.DistributeEvacuees" ) );
		Menu.updateMenu( mnuEditDistribution, loc.getString( "Edit.Distributions" ) );
		Menu.updateMenu( mnuEditProperties, loc.getString( "Edit.Properties" ) );

		// Anzeige-menu
		Menu.updateMenu( mVisibleAreas, loc.getString( "View.VisibleAreas" ) );
		Menu.updateMenu( mnuShowAllAreas, loc.getString( "View.ShowAllAreas" ) );
		Menu.updateMenu( mnuHideAllAreas, loc.getString( "View.HideAllAreas" ) );
		Menu.updateMenu( mnuDelayArea, loc.getString( "View.ShowDelayAreas" ) );
		Menu.updateMenu( mnuStairArea, loc.getString( "View.ShowStairAreas" ) );
		Menu.updateMenu( mnuEvacuationArea, loc.getString( "View.ShowEvacuationAreas" ) );
		Menu.updateMenu( mnuInaccessibleArea, loc.getString( "View.ShowInaccessibleAreas" ) );
		Menu.updateMenu( mnuSaveArea, loc.getString( "View.ShowSaveAreas" ) );
		Menu.updateMenu( mnuAssignmentArea, loc.getString( "View.ShowAssignmentAreas" ) );
		Menu.updateMenu( mGrid, loc.getString( "View.Gridstyle" ) );
		Menu.updateMenu( mnuGridLines, loc.getString( "View.GridstyleLines" ) );
		Menu.updateMenu( mnuGridPoints, loc.getString( "View.GridstylePoints" ) );
		Menu.updateMenu( mnuGridNotVisible, loc.getString( "View.GridstyleNone" ) );
		Menu.updateMenu( mnuPaintRasterized, loc.getString( "View.DrawOnGrid" ) );
		Menu.updateMenu( mnuHideDefaultFloor, loc.getString( "View.HideDefaultEvacuationFloor" ) );
		Menu.updateMenu( mnuScreenshot, loc.getString( "View.Screenshot" ) );

		// Execute menu (debug only)
		Menu.updateMenu( mExecute, loc.getString( "Execute" ) );
		Menu.updateMenu( mSimulation, loc.getString( "Execute.Simulation" ) );
		Menu.updateMenu( mnuExecuteCreateCellularAutomaton, loc.getString( "Execute.CreateCellularAutomaton" ) );
		Menu.updateMenu( mnuExecuteApplyAssignment, loc.getString( "Execute.ApplyConcreteAssignment" ) );

		Menu.updateMenu( mnuSimulationQuickVisualization, loc.getString( "Execute.Simulation.QuickVisualization" ) );
		Menu.updateMenu( mnuSimulationStart, loc.getString( "Execute.Simulation.Start" ) );
		Menu.updateMenu( mnuSimulationPauseQuickVisualization, loc.getString( "Execute.Simulation.PauseQuickVisualization" ) );
		Menu.updateMenu( mnuStepByStepSimulation, loc.getString( "Execute.Simulation.StepByStep" ) );


		Menu.updateMenu(mOptimization, loc.getString( "Execute.Optimization" ) );
		Menu.updateMenu(mCreateGraph, loc.getString( "Execute.CreateGraph" ) );
                Menu.updateMenu(mCreateGraphCompleteGraph, loc.getString("Execute.CreateGraph.CompleteGraph"));
                Menu.updateMenu(mnuCreateGraphCompleteGrid, loc.getString("Execute.CreateGraph.CompleteGraph.Grid"));
                Menu.updateMenu(mnuCreateGraphCompleteNonGrid, loc.getString("Execute.CreateGraph.CompleteGraph.NonGrid"));
                Menu.updateMenu(mCreateGraphSpannerUsingPrim , loc.getString("Execute.CreateGraph.Prim"));
                Menu.updateMenu(mnuCreateGraphSpannerUsingPrimGrid, loc.getString("Execute.CreateGraph.Prim.Grid"));
                Menu.updateMenu(mnuCreateGraphSpannerUsingPrimNonGrid , loc.getString("Execute.CreateGraph.Prim.NonGrid"));
                Menu.updateMenu(mCreateGraphSpannerUsingGreedy , loc.getString("Execute.CreateGraph.Greedy"));
                Menu.updateMenu(mnuCreateGraphSpannerUsingGreedyGrid , loc.getString("Execute.CreateGraph.Greedy.Grid"));
                Menu.updateMenu(mnuCreateGraphSpannerUsingGreedyNonGrid , loc.getString("Execute.CreateGraph.Greedy.NonGrid"));
                Menu.updateMenu(mCreateGraphSpannerUsingDijkstra, loc.getString("Execute.CreateGraph.ShortestPathTree"));
                Menu.updateMenu(mnuCreateGraphSpannerUsingDijkstraGrid, loc.getString("Execute.CreateGraph.ShortestPathTree.Grid"));
                Menu.updateMenu(mnuCreateGraphSpannerUsingDijkstraNonGrid, loc.getString("Execute.CreateGraph.ShortestPathTree.NonGrid"));
                
                
                //Menu.updateMenu( mCreateGraph, loc.getString( "Execute.Optimization.CreateGraph" ) );
		Menu.updateMenu( mnuOptimizationEarliestArrivalTransshipment, loc.getString( "Execute.Optimization.AlgoEATransshipment" ) );

		// Extras menu
		Menu.updateMenu( mLanguage, loc.getString( "Extras.Languages" ) );
		Menu.updateMenu( mPlanImage, loc.getString( "Extras.PlanDisplaying" ) );
		Menu.updateMenu( mnuPlanImageLoad, loc.getString( "Extras.LoadPlan" ) );
		Menu.updateMenu( mnuPlanImageHide, loc.getString( "Extras.HidePlan" ) );
		Menu.updateMenu( mnuPlanImageResize, loc.getString( "Extras.ResizePlan" ) );
		Menu.updateMenu( mnuPlanImageLocate, loc.getString( "Extras.MovePlan" ) );
		Menu.updateMenu( mnuPlanImageTransparency, loc.getString( "Extras.SetPlanTransparency" ) );
		Menu.updateMenu( mnuOptions, loc.getString( "Extras.Options" ) );

		if( ZETMain.isDebug() ) {
			Menu.updateMenu( mnuSettings, loc.getString( "Extras.Settings" ) );
			Menu.updateMenu( mnuDebug, loc.getString( "Extras.Debug" ) );
		}

		// Help menu
		Menu.updateMenu( mnuHelpAbout, loc.getString( "Help.About" ) );
		loc.setPrefix( "" );
	}
}
