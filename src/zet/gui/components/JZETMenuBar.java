/**
 * JZETMenuBar.java
 * Created: 21.07.2010 10:03:06
 */
package zet.gui.components;

import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.common.localization.Localized;
import ds.PropertyContainer;
import gui.Control;
import gui.ZETMain;
import zet.gui.components.tabs.base.AbstractFloor.RasterPaintStyle;
import gui.components.framework.Menu;
import gui.editor.AreaVisibility;
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

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JZETMenuBar extends JMenuBar implements ActionListener, Localized {
	private final Control control;
	/** The localization class. */
	static final Localization loc = Localization.getInstance();
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
	JCheckBoxMenuItem mnuDelayArea;
	JCheckBoxMenuItem mnuStairArea;
	JCheckBoxMenuItem mnuEvacuationArea;
	JCheckBoxMenuItem mnuInaccessibleArea;
	JCheckBoxMenuItem mnuSaveArea;
	JCheckBoxMenuItem mnuAssignmentArea;
	private JMenu mGrid;
	private JRadioButtonMenuItem mnuGridLines;
	private JRadioButtonMenuItem mnuGridPoints;
	private JRadioButtonMenuItem mnuGridNotVisible;
	private JCheckBoxMenuItem mnuPaintRasterized;
	private JCheckBoxMenuItem mnuHideDefaultFloor;
	private JMenu mExecute;
	private JMenuItem mnuExecuteCreateCellularAutomaton;
	private JMenuItem mnuExecuteCreateGraph;
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
	private JMenuItem mnuOutputInformation;
	private JMenuItem mnuOutputGraphAsText;
	private JMenu mWindow;
	private JMenu mHelp;
	private JMenuItem mnuHelpAbout;

	public JZETMenuBar( Control control ) {
		this.control = control;
		createMenuBar();
		control.setMenuBar( this );
	}


	/**
	 * Creates the menu.
	 */
	private void createMenuBar() {
		loc.setPrefix( "gui.editor.JEditor." );

		//JMenuBar bar = new JMenuBar();
		mFile = Menu.addMenu( this, loc.getString( "menuFile" ) );
		mEdit = Menu.addMenu( this, loc.getString( "menuEdit" ) );
		mView = Menu.addMenu( this, loc.getString( "menuView" ) );
		if( ZETMain.isDebug() )
			mExecute = Menu.addMenu( this, loc.getString( "menuExecute" ) );
		mExtras = Menu.addMenu( this, loc.getString( "menuExtras" ) );
		mHelp = Menu.addMenu( this, loc.getString( "menuHelp" ) );

		// Dateimenue
		mnuFileNew = Menu.addMenuItem( mFile, loc.getString( "menuNew" ), 'N', this, "newProject" );
		Menu.addMenuItem( mFile, "-" );
		mnuFileOpen = Menu.addMenuItem( mFile, loc.getString( "menuOpen" ), 'O', this, "loadProject" );
		mnuFileSave = Menu.addMenuItem( mFile, loc.getString( "menuSave" ), 'S', this, "saveProject" );
		mnuFileSaveAs = Menu.addMenuItem( mFile, loc.getString( "menuSaveAs" ), 'U', this, "saveProjectAs" );
		Menu.addMenuItem( mFile, "-" );
		mnuFileExportAsDXF = Menu.addMenuItem( mFile, loc.getString( "menuDXF" ), this, "saveAsDXF" );
		Menu.addMenuItem( mFile, "-" );
		mnuFileSaveResultAs = Menu.addMenuItem( mFile, loc.getString( "menuSaveResultAs" ), 'E', this, "saveResultAs" );
		mnuFileSaveResultAs.setEnabled( false );
		mnuFileLoadResult = Menu.addMenuItem( mFile, loc.getString( "menuLoadBatchResult" ), 'B', this, "loadBatchResult" );
		Menu.addMenuItem( mFile, "-" );
		mnuFileExit = Menu.addMenuItem( mFile, loc.getString( "menuExit" ), 'X', this, "exit" );

		// Bearbeiten menue
		mnuEditFloorNew = Menu.addMenuItem( mEdit, loc.getString( "menuFloorNew" ), this, "new" );
		mnuEditFloorUp = Menu.addMenuItem( mEdit, loc.getString( "menuFloorUp" ), this, "up" );
		mnuEditFloorDown = Menu.addMenuItem( mEdit, loc.getString( "menuFloorDown" ), this, "down" );
		mnuEditFloorDelete = Menu.addMenuItem( mEdit, loc.getString( "menuFloorDelete" ), this, "delete" );
		mnuEditFloorCopy = Menu.addMenuItem( mEdit, loc.getString( "menuFloorCopy" ), this, "copy" );
		mnuEditFloorImport = Menu.addMenuItem( mEdit, loc.getString( "menuFloorImport" ), this, "import" );

		Menu.addMenuItem( mEdit, "-" );
		mnuEditRasterize = Menu.addMenuItem( mEdit, loc.getString( "menuRasterize" ), 'R', InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK, this, "rasterize" );
		Menu.addMenuItem( mEdit, "-" );
		mnuEditDistributeEvacuees = Menu.addMenuItem( mEdit, loc.getString( "menuDistributeEvacuees" ), this, "distributeEvacuees" );
		Menu.addMenuItem( mEdit, "-" );
		mnuEditDistribution = Menu.addMenuItem( mEdit, loc.getString( "menuDistributions" ), 'V', this, "distribution" );
		mnuEditProperties = Menu.addMenuItem( mEdit, loc.getString( "menuProperties" ), 'P', this, "properties" );

		// Anzeige-menue
		mVisibleAreas = Menu.addMenu( mView, loc.getString( "menuVisibleAreas" ) );
		mnuShowAllAreas = Menu.addMenuItem( mVisibleAreas, loc.getString( "menuShowAllAreas" ), this, "showAll" );
		mnuShowAllAreas.setEnabled( false );
		mnuHideAllAreas = Menu.addMenuItem( mVisibleAreas, loc.getString( "menuHideAllAreas" ), this, "hideAll" );
		mVisibleAreas.addSeparator();
		mnuDelayArea = Menu.addCheckMenuItem( mVisibleAreas, loc.getString( "menuShowDelayAreas" ), true, this, "delayArea" );
		mnuStairArea = Menu.addCheckMenuItem( mVisibleAreas, loc.getString( "menuShowStairAreas" ), true, this, "stairArea" );
		mnuEvacuationArea = Menu.addCheckMenuItem( mVisibleAreas, loc.getString( "menuShowEvacuationAreas" ), true, this, "evacuationArea" );
		mnuInaccessibleArea = Menu.addCheckMenuItem( mVisibleAreas, loc.getString( "menuShowInaccessibleAreas" ), true, this, "inaccessibleArea" );
		mnuSaveArea = Menu.addCheckMenuItem( mVisibleAreas, loc.getString( "menuShowSaveAreas" ), true, this, "saveArea" );
		mnuAssignmentArea = Menu.addCheckMenuItem( mVisibleAreas, loc.getString( "menuShowAssignmentAreas" ), true, this, "assignmentArea" );
		Menu.addMenuItem( mView, "-" );
		mGrid = Menu.addMenu( mView, loc.getString( "menuGridstyle" ) );
		mnuGridLines = Menu.addRadioButtonMenuItem( mGrid, loc.getString( "menuGridstyleLines" ), false, this, "gridLine" );
		mnuGridPoints = Menu.addRadioButtonMenuItem( mGrid, loc.getString( "menuGridstylePoints" ), true, this, "gridPoint" );
		mnuGridNotVisible = Menu.addRadioButtonMenuItem( mGrid, loc.getString( "menuGridstyleNone" ), false, this, "gridNo" );
		mnuPaintRasterized = Menu.addCheckMenuItem( mView, loc.getString( "menuDrawOnGrid" ), true, this, "grid" );
		mnuHideDefaultFloor = Menu.addCheckMenuItem( mView, loc.getString( "menuHideDefaultEvacuationFloor" ), PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" ), this, "defaultFloor" );
		mView.addSeparator();
		mnuScreenshot = Menu.addMenuItem( mView, loc.getString( "menuScreenshot" ), KeyEvent.VK_F12, this, "screenshot", 0 );

		// execute menu
		if( ZETMain.isDebug() ) {
			mnuExecuteCreateCellularAutomaton = Menu.addMenuItem( mExecute, loc.getString( "menuExecuteCreateCellularAutomaton" ), this, "createCellularAutomaton" );
			mnuExecuteCreateGraph = Menu.addMenuItem( mExecute, loc.getString( "menuExecuteCreateGraph" ), this, "createGraph" );
			mnuExecuteApplyAssignment = Menu.addMenuItem( mExecute, loc.getString( "menuExecuteApplyConcreteAssignment" ), this, "applyConcreteAssignment" );

			mSimulation = Menu.addMenu( mExecute, loc.getString( "menuSimulation" ) );
			mnuSimulationQuickVisualization = Menu.addMenuItem( mSimulation, loc.getString( "menuSimulationQuickVisualization" ), KeyEvent.VK_F5, this, "visualization", 0 );
			mnuSimulationStart = Menu.addMenuItem( mSimulation, loc.getString( "menuSimulationStart" ), KeyEvent.VK_F5, this, "startSimulation", InputEvent.CTRL_DOWN_MASK );
			mnuSimulationPauseQuickVisualization = Menu.addMenuItem( mSimulation, loc.getString( "menuSimulationPauseQuickVisualization" ), KeyEvent.VK_F6, this, "visualizationPause", 0 );
			mnuStepByStepSimulation = Menu.addMenuItem( mSimulation, loc.getString( "menuSimulationStepByStep" ), KeyEvent.VK_F7, this, "stepByStepSimulation", 0 );

			mOptimization = Menu.addMenu( mExecute, loc.getString( "menuOptimization" ) );
			mnuOptimizationEarliestArrivalTransshipment = Menu.addMenuItem( mOptimization, loc.getString( "menuOptAlgoEATransshipment" ), KeyEvent.VK_F8, this, "EAT", 0 );
			//			mnuExecuteQuickestTransshipment = Menu.addMenuItem( mOptimization, loc.getString( "menuOptAlgoQuickestTransshipment" ), this, "QT" );
			//			mnuExecuteMaxFlowOverTimeMC = Menu.addMenuItem( mOptimization, loc.getString( "menuOptAlgoMaxFlowOverTimeMinCost" ), this, "MFOTMC" );
			//			mnuExecuteMaxFlowOverTimeTEN = Menu.addMenuItem( mOptimization, loc.getString( "menuOptAlgoMaxFlowOverTimeTEN" ), this, "MFOTTEN" );
		}

		// Extras-Menu
		mLanguage = Menu.addMenu( mExtras, loc.getString( "menuLanguages" ) );
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
		mPlanImage = Menu.addMenu( mExtras, loc.getString( "menuPlanDisplaying" ) );
		mnuPlanImageLoad = Menu.addMenuItem( mPlanImage, loc.getString( "menuLoadPlan" ), this, "loadBuildingPlan" );
		mnuPlanImageHide = Menu.addMenuItem( mPlanImage, loc.getString( "menuHidePlan" ), this, "hideBuildingPlan" );
		mnuPlanImageHide.setEnabled( false );
		mnuPlanImageResize = Menu.addMenuItem( mPlanImage, loc.getString( "menuResizePlan" ), this, "resizeBuildingPlan" );
		mnuPlanImageResize.setEnabled( false );
		mnuPlanImageLocate = Menu.addMenuItem( mPlanImage, loc.getString( "menuMovePlan" ), this, "moveBuildingPlan" );
		mnuPlanImageLocate.setEnabled( false );
		mnuPlanImageTransparency = Menu.addMenuItem( mPlanImage, loc.getString( "menuSetPlanTransparency" ), this, "transparencyBuldingPlan" );
		mnuPlanImageTransparency.setEnabled( false );
		Menu.addMenuItem( mExtras, "-" );
		mnuOptions = Menu.addMenuItem( mExtras, loc.getString( "menuOptions" ), 'T', this, "options" );
		if( ZETMain.isDebug() ) {
			mnuSettings = Menu.addMenuItem( mExtras, loc.getString( "menuSettings" ), this, "settings" );
			mnuDebug = Menu.addMenu( mExtras, "Debug" );
			mnuOutputInformation = Menu.addMenuItem( mnuDebug, "Ausgabe", this, "outputInformation" );
			mnuOutputGraphAsText = Menu.addMenuItem( mnuDebug, "Zugehöriger Graph als Datei ausgeben", this, "outputGraph" );
		}
		// Hilfe-menu
		mnuHelpAbout = Menu.addMenuItem( mHelp, loc.getString( "menuAbout" ), 'I', this, "about" );

		//setJMenuBar( bar );

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
			control.updateVisibility( AreaVisibility.Delay, mnuDelayArea.isSelected() );
		} else if( e.getActionCommand().equals( "stairArea" ) ) {
			control.updateVisibility( AreaVisibility.Stair, mnuStairArea.isSelected() );
		} else if( e.getActionCommand().equals( "evacuationArea" ) ) {
			control.updateVisibility( AreaVisibility.Evacuation, mnuEvacuationArea.isSelected() );
		} else if( e.getActionCommand().equals( "inaccessibleArea" ) ) {
			control.updateVisibility( AreaVisibility.Inaccessible, mnuInaccessibleArea.isSelected() );
		} else if( e.getActionCommand().equals( "saveArea" ) ) {
			control.updateVisibility( AreaVisibility.Save, mnuSaveArea.isSelected() );
		} else if( e.getActionCommand().equals( "assignmentArea" ) ) {
			control.updateVisibility( AreaVisibility.Assignment, mnuAssignmentArea.isSelected() );
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
		} else if( e.getActionCommand().equals( "createGraph" ) ) {
			control.createGraph();
		} else if( e.getActionCommand().equals( "applyConcreteAssignment" ) ) {
		} else if( e.getActionCommand().equals( "startSimulation" ) ) {
			control.performSimulation();
		} else if( e.getActionCommand().equals( "visualizationPause" ) ) {
		} else if( e.getActionCommand().equals( "stepByStepSimulation" ) ) {
			control.performOneStep();
		} else if( e.getActionCommand().equals( "EAT" ) ) {
			control.performOptimization();
		} else if( e.getActionCommand().equals( "options" ) ) {
			control.showOptionsDialog();
		} else if( e.getActionCommand().equals( "settings" ) ) {
			control.showSettingsDialog();
		} else if( e.getActionCommand().equals( "german" ) ) {
			control.switchToLanguage( Locale.GERMAN );
		} else if( e.getActionCommand().equals( "english" ) ) {
			control.switchToLanguage( Locale.ENGLISH );
		} else if( e.getActionCommand().equals( "outputInformation" ) ) {
			control.outputInformation();
		} else if( e.getActionCommand().equals( "outputGraph" ) ) {
			control.outputGraph();
		} else if( e.getActionCommand().equals( "about" ) ) {
			// show the about screen
			control.showAbout();
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

	public void localize() {
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
/**/			Menu.updateMenu( mnuExecuteCreateCellularAutomaton, loc.getString( "menuSimulationCreateCA" ) );
/**/			Menu.updateMenu( mnuExecuteApplyAssignment, loc.getString( "menuSimulationApplyConcreteAssignment" ) );
			//TODO if paused use other
			//if( caAlgo != null )
			//	if( caAlgo.isPaused() )
/*					Menu.updateMenu( mnuExecutePauseSimulation, loc.getString( "menuSimulationContinue" ) );
			//	else
					Menu.updateMenu( mnuExecutePauseSimulation, loc.getString( "menuSimulationPause" ) );
			//else
*/

			Menu.updateMenu( mnuSimulationQuickVisualization, loc.getString( "menuSimulationQuickVisualization" ) );
			Menu.updateMenu( mnuSimulationStart, loc.getString( "menuSimulationStart" ) );
			Menu.updateMenu( mnuSimulationPauseQuickVisualization, loc.getString( "menuSimulationPauseQuickVisualization" ) );
			Menu.updateMenu( mnuStepByStepSimulation, loc.getString( "menuSimulationStepByStep" ) );


//Menu.updateMenu( mnuExecutePauseSimulation, loc.getString( "menuSimulationPause" ) );
			Menu.updateMenu( mOptimization, loc.getString( "menuOptimization" ) );
			Menu.updateMenu( mnuExecuteCreateGraph, loc.getString( "menuOptimizationCreateGraph" ) );
//			Menu.updateMenu( mnuExecuteQuickestTransshipment, loc.getString( "menuOptAlgoQuickestTransshipment" ) );
//			Menu.updateMenu( mnuExecuteMaxFlowOverTimeMC, loc.getString( "menuOptAlgoMaxFlowOverTimeMinCost" ) );
//			Menu.updateMenu( mnuExecuteMaxFlowOverTimeTEN, loc.getString( "menuOptAlgoMaxFlowOverTimeTEN" ) );
			Menu.updateMenu( mnuOptimizationEarliestArrivalTransshipment, loc.getString( "menuOptAlgoEATransshipment" ) );
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
		loc.setPrefix( "" );
	}
}
