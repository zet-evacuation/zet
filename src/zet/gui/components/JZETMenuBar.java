/**
 * JZETMenuBar.java
 * Created: Jul 21, 2010,10:03:06 AM
 */
package zet.gui.components;

import de.tu_berlin.math.coga.common.localization.Localization;
import ds.PropertyContainer;
import gui.Control;
import gui.CreditsDialog;
import gui.ZETMain;
import gui.components.framework.Menu;
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
public class JZETMenuBar extends JMenuBar implements ActionListener {
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
//		mnuFileExit.addActionListener( new ActionListener() {
//			@Override
//			public void actionPerformed( ActionEvent e ) {
//				System.exit( 0 );
//			}
//		} );

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
		mnuScreenshot = Menu.addMenuItem( mView, loc.getString( "menuScreenshot" ), KeyEvent.VK_F12, this, "screenshot", 0 );

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
			control.distribution();
		} else if( e.getActionCommand().equals( "properties" ) ) {
			
		} else if( e.getActionCommand().equals( "screenshot" ) ) {
			control.takeScreenshot();
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
}
