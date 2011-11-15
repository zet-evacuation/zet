/*
 * GUIControl.java
 * Created 16.07.2010, 12:18:41
 */
package gui;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import batch.load.BatchProjectEntry;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter.ConversionNotSupportedException;
import java.beans.PropertyChangeEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;
import java.util.Locale;
import zet.gui.main.JEditor;
import batch.BatchResult;
import batch.BatchResultEntry;
import batch.tasks.AlgorithmTask;
import batch.tasks.BatchGraphCreateOnlyTask;
import batch.tasks.VisualizationDataStructureTask;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmListener;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmProgressEvent;
import de.tu_berlin.math.coga.common.util.IOTools;
import de.tu_berlin.math.coga.zet.DatFileReaderWriter;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.CompareVisualizationResults;
import ds.GraphVisualizationResults;
import ds.ProjectLoader;
import ds.PropertyContainer;
import ds.ca.CellularAutomaton;
import ds.z.Assignment;
import ds.z.AssignmentArea;
import ds.z.ConcreteAssignment;
import ds.z.EvacuationArea;
import ds.z.Floor;
import ds.z.PlanPolygon;
import ds.z.Room;
import ds.z.ZControl;
import ds.z.exception.RoomEdgeInvalidTargetException;
import ds.z.exception.TooManyPeopleException;
import gui.components.progress.JProgressBarDialog;
import gui.components.progress.JRasterizeProgressBarDialog;
import gui.editor.Areas;
import gui.editor.CoordinateTools;
import zet.gui.main.tabs.editor.EditMode;
import zet.gui.assignmentEditor.JAssignment;
import gui.editor.flooredit.FloorImportDialog;
import gui.editor.planimage.JPlanImageProperties;
import gui.editor.properties.JOptionsWindow;
import gui.editor.properties.JPropertySelectorWindow;
import zet.gui.main.menu.JZETMenuBar;
import zet.gui.main.toolbar.JEditToolbar;
import zet.gui.main.toolbar.JVisualizationToolbar;
import gui.visualization.AbstractVisualization.ParallelViewMode;
import gui.visualization.Visualization.RecordingMode;
import zet.gui.main.tabs.visualization.ZETVisualization;
import gui.visualization.control.GLControl.CellInformationDisplay;
import io.DXFWriter;
import io.movie.MovieManager;
import io.visualization.BuildingResults;
import io.visualization.CAVisualizationResults;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import statistic.ca.CAStatistic;
import zet.gui.GUILocalization;
import zet.gui.main.tabs.base.AbstractFloor.RasterPaintStyle;
import zet.gui.main.tabs.JEditView;
import zet.gui.main.toolbar.JStatisticGraphToolBar;
import zet.tasks.RasterizeTask;

/**
 * This class receives commands and GUI changes from elements like tool bars,
 * menus etc. and delegates them to other classes.
 * @author Jan-Philipp Kappmeier
 */
public class GUIControl implements AlgorithmListener {

	/** The editor. */
	public JEditor editor;

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
	private AlgorithmControl algorithmControl;
	private ArrayList<Areas> mode = new ArrayList<Areas>( Arrays.asList( Areas.values() ) );

	/**
	 * Creates a new instance of {@code GUIControl}.
	 */
	public GUIControl() { }

	public void createZETWindow() {
		zcontrol = new ZControl();
		editor = new JEditor( this, zcontrol );
		editor.addMainComponents();
		visualization = editor.getVisualizationView().getGLContainer();
		updateVisualizationElements();
		editview = editor.getEditView();
		algorithmControl = new AlgorithmControl( zcontrol.getProject() );
		visualization.setZcontrol( zcontrol );
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
		//CoordinateTools.setZoomFactor( zoomFactor );
		editview.getLeftPanel().setZoomFactor( zoomFactor );
		editview.getFloor().getPlanImage().update();
		editview.updateFloorView();
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
	public void showArea( Areas areaType ) {
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
//				JEditor.showErrorMessage("Error", "Dieser Area-Typ wird nicht unterstützt.");
//		}
		//editor.updateAreaVisiblity();
	}

	public void setEditMode( EditMode editMode ) {
		if( editview != null ) {
			editview.setEditMode( editMode );
			editToolBar.setEditSelectionSelected( false );
			editToolBar.setEditPointwiseSelected( editMode.getType() == EditMode.Type.CreationPointwise );
			editToolBar.setEditRectangledSelected( editMode.getType() == EditMode.Type.CreationRectangled );
		}
	}

	/**
	 * Exits the program.
	 */
	public void exit() {
		System.exit( 0 );
	}

	public void setEditToolbar( JEditToolbar toolbar ) {
		editToolBar = toolbar;
	}

	public void setVisualizationToolbar( JVisualizationToolbar toolbar ) {
		visualizationToolBar = toolbar;
	}

	public void setGraphStatisticToolBar( JStatisticGraphToolBar graphStatisticToolBar ) {
		this.graphStatisticToolBar = graphStatisticToolBar;
	}

	public void visualizationToggle2D3D() {
		PropertyContainer.getInstance().toggle( "settings.gui.visualization.2d" );
		updateVisualizationElements();
	}

	public void visualizationToggle2D() {
		PropertyContainer.getInstance().toggle( "settings.gui.visualization.isometric" );
		updateVisualizationElements();
	}

	public void updateVisualizationElements() {
		final boolean visualization3D = PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.2d" );
		final boolean visualization2D = PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.isometric" );
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
		VideoOptions vo = new VideoOptions( editor );
		// Setze die erwartete Laufzeit
		vo.setEstimatedTime( visualization.getControl().getEstimatedTime() );
		vo.setResolution( visualization.getSize() );
		vo.setBitrate( 1000 );
		vo.setFramerate( 24 );
		vo.setTextureFontStrings( visualization.getTexts() );
		vo.setVisible( true );
		vo.dispose();
		if( vo.getRetVal() == VideoOptions.OK ) {
			visualization.setTexts( vo.getTextureFontStrings() );
			zcontrol.getProject().getVisualProperties().setTextureFontStrings( vo.getTextureFontStrings() );
			String movieFrameName = PropertyContainer.getInstance().getAsString( "options.filehandling.movieFrameName" );
			// TODO BUG: wenn ein projekt noch nicht gespeichert worden ist, liefert das hier iene null pointer exception. (tritt auf, wenn ein video gedreht werden soll)
			String projectName = zcontrol.getProject().getProjectFile().getName().substring( 0, zcontrol.getProject().getProjectFile().getName().length() - 4 );
			MovieManager movieCreator = visualization.getMovieCreator();
			if( movieFrameName.isEmpty(  ) )
				movieCreator.setFramename( projectName );
			else
				movieCreator.setFramename( movieFrameName );
			String path = PropertyContainer.getInstance().getAsString( "options.filehandling.moviePath" );
			if( !(path.endsWith( "/" ) || path.endsWith( "\\" )) )
				path += "/";
			String movieFileName = IOTools.getNextFreeNumberedFilename( path, projectName, 3 );
			movieCreator.setFilename( movieFileName );
			movieCreator.setPath( PropertyContainer.getInstance().getAsString( "options.filehandling.moviePath" ) );
			movieCreator.setFramename( PropertyContainer.getInstance().getAsString( "options.filehandling.movieFrameName" ) );
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
		String path = PropertyContainer.getInstance().getAsString( "options.filehandling.screenshotPath" );
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
		//control.resetTime();
		visualization.repaint();
	}

	public void visualizationPause() {
		if( visualization.isAnimating() ) {
			visualizationToolBar.pause();
			visualization.stopAnimation();
		}
	}

	public void visualizationPlay() {
		// TODO restartVisualization
//				if( restartVisualization ) {
//					visualization.getControl().resetTime();
//					restartVisualization = false;
//				}
		System.out.println( "animation started" );
		if( visualization.isAnimating() ) {
			visualizationToolBar.pause();
			//btnPlay.setIcon( playIcon );
			//btnPlay.setSelected( false );
			visualization.stopAnimation();
			if( visualization.getRecording() == RecordingMode.Recording )
				visualization.setRecording( RecordingMode.SkipFrame );
		} else {
			visualizationToolBar.play();
			//btnPlay.setIcon( pauseIcon );
			//btnPlay.setSelected( true );
			if( visualization.getRecording() == RecordingMode.SkipFrame )
				visualization.setRecording( RecordingMode.Recording );
			visualization.startAnimation();
		}
	}
	private boolean loop = false;

	public void visualizationLoop() {
		loop = !loop;
		visualizationToolBar.setSelectedLoop( loop );
	}

	public void visualizationStop() {
		visualizationToolBar.pause();
		//btnPlay.setIcon( playIcon );
		//btnPlay.setSelected( false );
		visualization.getControl().resetTime();
		// create a movie, if movie-creation was active.
		if( visualization.getRecording() != RecordingMode.NotRecording )
			visualization.createMovie();
		// stop animation if still animation
		if( visualization.isAnimating() )
			visualization.stopAnimation();
		// repaint once
		visualization.repaint();
	}
	private boolean showWalls = PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.walls" );

	public void visualizationShowWalls() {
		showWalls = !showWalls;
		visualizationToolBar.setSelectedShowWalls( showWalls );
		//btnShowWalls.setSelected( !btnShowWalls.isSelected() );
		PropertyContainer.getInstance().set( "settings.gui.visualization.walls", showWalls );
		visualization.getControl().showWalls( showWalls );
		visualization.repaint();
	}

	private boolean showGraph = PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.graph" );

	public void visualizationShowGraph() {
		showGraph = !showGraph;
		visualizationToolBar.setSelectedShowGraph( showGraph );
		//btnShowGraph.setSelected( !btnShowGraph.isSelected() );
		PropertyContainer.getInstance().set( "settings.gui.visualization.graph", showGraph );
		visualization.getControl().showGraph( showGraph );
		visualization.repaint();
	}
	public boolean showGraphGrid = PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.nodeArea" );

	public void visualizationShowGraphGrid() {
		showGraphGrid = !showGraphGrid;
		visualizationToolBar.setSelectedShowGraphGrid( showGraphGrid );
		//btnShowGraphGrid.setSelected( !btnShowGraphGrid.isSelected() );
		PropertyContainer.getInstance().set( "settings.gui.visualization.nodeArea", showGraphGrid );
		visualization.getControl().showNodeRectangles( showGraphGrid );
		visualization.repaint();

	}
	public boolean showCellularAutomaton = PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.cellularAutomaton" );

	public void visualizationShowCellularAutomaton() {
		showCellularAutomaton = !showCellularAutomaton;
		visualizationToolBar.setSelectedShowCellularAutomaton( showCellularAutomaton );
		//btnShowCellularAutomaton.setSelected( !btnShowCellularAutomaton.isSelected() );
		PropertyContainer.getInstance().set( "settings.gui.visualization.cellularAutomaton", showCellularAutomaton );
		visualization.getControl().showCellularAutomaton( showCellularAutomaton );
		visualization.repaint();

	}

	public void visualizationShowAllFloors() {
		final boolean showAllFloors = PropertyContainer.getInstance().toggle( "settings.gui.visualization.floors" );
		visualizationToolBar.setSelectedAllFloors( !showAllFloors );
		//btnShowAllFloors.setSelected( !btnShowAllFloors.isSelected() );
		editor.getVisualizationView().setFloorSelectorEnabled( !showAllFloors );
		if( showAllFloors )
			visualization.getControl().showAllFloors();
		else
			visualization.getControl().showFloor( editor.getVisualizationView().getSelectedFloorID() );
		visualization.repaint();
	}

//	public void visualizationShowStaticPotential() {
//		final int oldValue = PropertyContainer.getInstance().getAsInt( "settings.gui.visualization.floorInformation" );
//				if( oldValue == 1 ) {
//					btnShowPotential.setSelected( false );
//					control.showPotential( CellInformationDisplay.NoPotential );
//					PropertyContainer.getInstance().set( "settings.gui.visualization.floorInformation", 0 );
//				} else {
//					btnShowPotential.setSelected( true );
//					btnShowDynamicPotential.setSelected( false );
//					btnShowUtilization.setSelected( false );
//					btnShowWaiting.setSelected( false );
//					PropertyContainer.getInstance().set( "settings.gui.visualization.floorInformation", 1 );
//					visualizationView.unselectPotentialSelector();
//					control.activateMergedPotential();
//					control.showPotential( CellInformationDisplay.StaticPotential );
//				}
//				visualizationView.getGLContainer().repaint();
//
//	}
	public void visualizationShowCellInformation( CellInformationDisplay cid ) {
		final int oldValue = PropertyContainer.getInstance().getAsInt( "settings.gui.visualization.floorInformation" );
		if( oldValue == cid.id() ) {
			// die werte waren gleich. schalte aus.
			visualizationToolBar.setSelectedCellInformationDisplay( CellInformationDisplay.NoPotential );
			PropertyContainer.getInstance().set( "settings.gui.visualization.floorInformation", CellInformationDisplay.NoPotential.id() );
			visualization.getControl().showPotential( CellInformationDisplay.NoPotential );
		} else {
			visualizationToolBar.setSelectedCellInformationDisplay( cid );
			PropertyContainer.getInstance().set( "settings.gui.visualization.floorInformation", cid.id() );
			editor.getVisualizationView().unselectPotentialSelector();
			if( cid == CellInformationDisplay.StaticPotential )
				visualization.getControl().activateMergedPotential();
			visualization.getControl().showPotential( cid );
		}
		visualization.repaint();
	}

	public void buildVisualizationDataStructure( BatchResultEntry e, int nrOfCycle ) {
		CAVisualizationResults caRes = e.getCaVis() != null ? e.getCaVis()[nrOfCycle] : null;
		CAStatistic caStatistic = e.getCaStatistics() != null ? e.getCaStatistics()[nrOfCycle] : null;
		caRes.statistic = caStatistic;

		ds.GraphVisualizationResults graphRes = e.getGraphVis();

		VisualizationDataStructureTask visualizationDataStructure = new VisualizationDataStructureTask( caRes, graphRes, e.getBuildingResults(), caStatistic );
		JProgressBarDialog pbd = new JProgressBarDialog( editor, GUILocalization.getSingleton().getStringWithoutPrefix( "batch.tasks.buildVisualizationDatastructure" ), true, visualizationDataStructure );
		pbd.executeTask();
		pbd.setVisible( true );
		ZETMain.sendMessage( GUILocalization.getSingleton().getStringWithoutPrefix( "batch.tasks.progress.visualizationDatastructureComplete" ) );

		editor.setControl( visualizationDataStructure.getControl() );

		//control = visualizationDataStructure.getControl();

		visualization.setControl( visualizationDataStructure.getControl() );

		visualizationToolBar.setEnabledVisibleElements( visualizationDataStructure.getControl() );

		//control.showCellularWa( btnShowCellularAutomaton.isSelected() );
		//control.showCellularAutomaton( btnShowCellularAutomaton.isSelected() );
		//control.showGraph( btnShowGraph.isSelected() );
//		control.showFloor( visualizationView.getSelectedFloorID() );
		editor.getVisualizationView().updateFloorSelector();
		editor.getVisualizationView().updatePotentialSelector();
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
		System.out.println( "Personenverteilung im Gebäude: " );
		int overall = 0;
		for( Floor f : zcontrol.getProject().getBuildingPlan() ) {
			int counter = 0;
			for( Room r : f )
				for( AssignmentArea a : r.getAssignmentAreas() )
					counter += a.getEvacuees();
			System.out.println( f.getName() + ": " + counter + " Personen" );
			overall += counter;
		}
		System.out.println( "Insgesamt: " + overall );

		// Pro Ausgang:
		System.out.println( "Personenverteilung pro Ausgang: " );
		for( Floor f : zcontrol.getProject().getBuildingPlan() )
			for( Room r : f )
				for( EvacuationArea ea : r.getEvacuationAreas() ) {
					overall = 0;
					System.out.println( "" );
					System.out.println( ea.getName() );
					// Suche nach evakuierten pro etage für dieses teil
					for( Floor f2 : zcontrol.getProject().getBuildingPlan() ) {
						int counter = 0;
						for( Room r2 : f2 )
							for( AssignmentArea a : r2.getAssignmentAreas() )
								if( a.getExitArea().equals( ea ) )
									counter += a.getEvacuees();
						System.out.println( f2.getName() + ": " + counter + " Personen" );
						overall += counter;
					}
					System.out.println( ea.getName() + " insgesamt: " + overall );
				}

	}

	public void outputGraph() {
		BatchResultEntry ca_res = new BatchResultEntry( zcontrol.getProject().getProjectFile().getName(), new BuildingResults( zcontrol.getProject().getBuildingPlan() ) );
		ConcreteAssignment[] concreteAssignments = new ConcreteAssignment[1];
		Assignment assignment = zcontrol.getProject().getCurrentAssignment();
		concreteAssignments[0] = assignment.createConcreteAssignment( 400 );
		new BatchGraphCreateOnlyTask( ca_res, 0, zcontrol.getProject(), assignment, concreteAssignments ).run();
		NetworkFlowModel originalProblem = ca_res.getNetworkFlowModel();
		EarliestArrivalFlowProblem problem = new EarliestArrivalFlowProblem( originalProblem.getEdgeCapacities(), originalProblem.getNetwork(), originalProblem.getNodeCapacities(), originalProblem.getSupersink(), originalProblem.getSources(), 0, originalProblem.getTransitTimes(), originalProblem.getCurrentAssignment() );
		try {
			DatFileReaderWriter.writeFile( zcontrol.getProject().getName(), problem, zcontrol.getProject().getProjectFile().getName().substring( 0, zcontrol.getProject().getProjectFile().getName().length() - 4 ) + ".dat", originalProblem.getZToGraphMapping() );
		} catch( FileNotFoundException ex ) {
			ZETMain.sendError( "FileNotFoundException" );
			ex.printStackTrace( System.err );
		} catch( IOException ex ) {
			ZETMain.sendError( "IOException" );
			ex.printStackTrace( System.err );
		}

	}

	public void switchToLanguage( Locale locale ) {
		GUILocalization.getSingleton().setLocale( locale );
		editor.localize();
		menuBar.localize();
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
			zcontrol.loadProject( jfcProject.getSelectedFile() );
			editor.loadProject();	// Load the currently loaded project by the control file
			algorithmControl.setProject( zcontrol.getProject() );
			GUIOptionManager.setSavePath( jfcProject.getCurrentDirectory().getPath() );
			GUIOptionManager.setLastFile( 1, jfcProject.getSelectedFile().getAbsolutePath() );
		}
	}

	public void loadProject( File f ) {
		zcontrol.loadProject( f );
		editor.loadProject();
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
	private boolean createCopy = PropertyContainer.getInstance().getAsBoolean( "options.filehandling.createBackup" );

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
				showErrorMessage( GUILocalization.getSingleton().getString( "gui.editor.JEditor.error.stackOverflowTitle" ), GUILocalization.getSingleton().getString( "gui.editor.JEditor.error.stackOverflow" ) );
			} catch( Exception ex ) {
				showErrorMessage( GUILocalization.getSingleton().getString( "gui.editor.JEditor.error.SaveTitle" ), GUILocalization.getSingleton().getString( "gui.editor.JEditor.error.Save" ) );
			}
			editview.displayProject( zcontrol );
			ZETMain.sendMessage( GUILocalization.getSingleton().getString( "gui.editor.JEditor.message.saved" ) );
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
				showErrorMessage( GUILocalization.getSingleton().getString( "gui.editor.JEditor.error.stackOverflowTitle" ), GUILocalization.getSingleton().getString( "gui.editor.JEditor.error.stackOverflow" ) );
			} catch( Exception ex ) {
				showErrorMessage( GUILocalization.getSingleton().getString( "gui.editor.JEditor.error.SaveTitle" ), GUILocalization.getSingleton().getString( "gui.editor.JEditor.error.Save" ) );
				ex.printStackTrace( System.err );
				return;
			}
			ZETMain.sendMessage( GUILocalization.getSingleton().getString( "gui.editor.JEditor.message.saved" ) );
		}
	}

	// TODO auslagern von save und methoden aufrufen!
	public void newProject() {
		String status = "";
		switch( JOptionPane.showOptionDialog( editor,
						GUILocalization.getSingleton().getString( "gui.editor.JEditor.SaveQuestion" ),
						GUILocalization.getSingleton().getString( "gui.editor.JEditor.NewProject" ),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null, null, null ) ) {
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
							showErrorMessage( GUILocalization.getSingleton().getString( "gui.editor.JEditor.error.stackOverflowTitle" ), GUILocalization.getSingleton().getString( "gui.editor.error.JEditor.stackOverflow" ) );
						} catch( Exception ex ) {
							showErrorMessage( GUILocalization.getSingleton().getString( "gui.editor.JEditor.error.SaveTitle" ), GUILocalization.getSingleton().getString( "gui.editor.JEditor.error.Save" ) );
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
						showErrorMessage( GUILocalization.getSingleton().getString( "gui.editor.JEditor.error.stackOverflowTitle" ), GUILocalization.getSingleton().getString( "gui.editor.JEditor.error.stackOverflow" ) );
					} catch( Exception ex ) {
						showErrorMessage( GUILocalization.getSingleton().getString( "gui.editor.JEditor.error.SaveTitle" ), GUILocalization.getSingleton().getString( "gui.editor.JEditor.error.Save" ) );
						ex.printStackTrace( System.err );
						return;
					}
				}
				status = GUILocalization.getSingleton().getString( "gui.editor.JEditor.status.newProject" );
				break;
			case 1:
				status = GUILocalization.getSingleton().getString( "gui.editor.JEditor.status.newProjectDiscard" );
		}
		zcontrol.newProject();
		editor.loadProject();	// Load the currently loaded project by the control file
		algorithmControl.setProject( zcontrol.getProject() );
		ZETMain.sendMessage( status );
	}

	public void saveAsDXF() {
		String filename = zcontrol.getProject().getProjectFile().getPath().substring( 0, zcontrol.getProject().getProjectFile().getPath().length() - 3 ) + "dxf";
		try {
			DXFWriter.exportIntoDXF( filename, zcontrol.getProject().getBuildingPlan() );
		} catch( IOException ex ) {
			showErrorMessage( GUILocalization.getSingleton().getString( "gui.editor.JEditor.error.SaveTitle" ), GUILocalization.getSingleton().getString( "gui.editor.JEditor.error.Save" ) );
			ex.printStackTrace( System.err );
			return;
		}
		ZETMain.sendMessage( GUILocalization.getSingleton().getString( "gui.editor.JEditor.message.dxfComplete" ) );
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
		ZETMain.sendMessage( "Neue Etage angelegt." ); // TODO loc
		editor.getEditView().updateFloorList(); // update the floor-boxes in the GUI
		editor.getEditView().changeFloor( f );
		editor.getQuickVisualizationView().updateQuickFloorlist();
		editor.getQuickVisualizationView().changeQuickFloor( f );

	}

	public void moveFloorUp() {
		final int oldIndex = editview.getFloorID();
		zcontrol.moveFloorUp( editview.getFloorID() + (ZETProperties.isDefaultFloorHidden() ? 1 : 0) );
		editview.setFloor( oldIndex + 1 );
	}

	public void moveFloorDown() {
		final int oldIndex = editview.getFloorID();
		zcontrol.moveFloorDown( editview.getFloorID() + (ZETProperties.isDefaultFloorHidden() ? 1 : 0) );
		editview.setFloor( oldIndex - 1 );

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
			ZETMain.sendMessage( GUILocalization.getSingleton().getString( "gui.message.RasterizationComplete" ) );
		} catch( Exception ex ) {
			ZETMain.sendError( ex.getLocalizedMessage() );
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
				ZETMain.sendMessage( GUILocalization.getSingleton().getString( "gui.message.RasterizationComplete" ) );
			}
		} catch( NumberFormatException ex ) {
			ZETMain.sendError( GUILocalization.getSingleton().getString( "gui.error.NonParsableNumber" ) );
		} catch( TooManyPeopleException ex ) {
			ZETMain.sendError( ex.getLocalizedMessage() );
		}
	}

	public void showAssignmentDialog() {
		JAssignment distribution = new JAssignment( editor, zcontrol.getProject(), GUILocalization.getSingleton().getString( "gui.AssignmentEditor.Title" ), 850, 400 );
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
	public void updateVisibility( Areas areaVisibility, boolean value ) {
		if( value && !mode.contains( areaVisibility ) )
			mode.add( areaVisibility );
		else if( !value )
			mode.remove( areaVisibility );
		editview.changeAreaView( mode );
		menuBar.setEnabledShowAllAreas( mode.size() != Areas.values().length );
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
			mode.addAll( Arrays.asList( Areas.values() ) );
		editview.changeAreaView( mode );
		menuBar.setEnabledShowAllAreas( mode.size() != Areas.values().length );
		menuBar.setEnabledHideAllAreas( !mode.isEmpty() );
	}

	public void showPropertiesDialog() {
		JPropertySelectorWindow propertySelector = new JPropertySelectorWindow( editor, GUILocalization.getSingleton().getString( "gui.editor.JPropertySelector.Title" ), 700, 500 );
		propertySelector.setVisible( true );
		System.out.println( "Properties saved." ); // TODO loc
	}

	public void setRasterizedPaintMode( boolean selected ) {
		editview.getFloor().setRasterizedPaintMode( selected );
	}

	public void setRasterPaintStyle( RasterPaintStyle rasterPaintStyle ) {
		editview.getFloor().setRasterPaintStyle( rasterPaintStyle );
		menuBar.setSelectedGridLines( rasterPaintStyle == RasterPaintStyle.Lines );
		menuBar.setSelectedGridPoints( rasterPaintStyle == RasterPaintStyle.Points );
		menuBar.setSelectedGridNotVisible( rasterPaintStyle == RasterPaintStyle.Nothing );
	}

	public void showDefaultFloor( boolean b ) {
		ZETProperties.isDefaultFloorHidden();
		PropertyContainer.getInstance().set( "editor.options.view.hideDefaultFloor", b );
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
				ZETMain.sendMessage( "Plan für Hintergrunddarstellung geladen." );
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

	public void showOptionsDialog() {
		ZETMain.ptmOptions.getRoot().reloadFromPropertyContainer();
		JOptionsWindow propertySelector = new JOptionsWindow( editor, GUILocalization.getSingleton().getString( "gui.editor.JOptions.Title" ), 700, 500, ZETMain.ptmOptions );
		propertySelector.setVisible( true );
		try {	// Save results in options file
			PropertyContainer.saveConfigFile( ZETMain.ptmOptions, new File( ZETMain.optionFilename ) );
		} catch( IOException ex ) {
			ZETMain.sendError( "Error saving config file!" ); // TODO loc
		}

	}

	public void showSettingsDialog() {
		ZETMain.ptmInformation.getRoot().reloadFromPropertyContainer();
		JOptionsWindow propertySelector = new JOptionsWindow( editor, GUILocalization.getSingleton().getString( "gui.editor.settings.Title" ), 700, 500, ZETMain.ptmInformation );
		propertySelector.setVisible( true );
		try {	// Save results in settings file
			PropertyContainer.saveConfigFile( ZETMain.ptmInformation, new File( ZETMain.informationFilename ) );
		} catch( IOException ex ) {
			ZETMain.sendError( "Error saving settings file!" ); // TODO loc
		}
	}

	/**
	 * Adds a {@link BatchProjectEntry} that can be loaded from a batch task file
	 * into the batch view.
	 * @param batchProjectEntry
	 */
	public void addBatchEntry( BatchProjectEntry batchProjectEntry ) {
		editor.getBatchView().add( batchProjectEntry );
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
		editor.getVisualizationView().updateCameraInformation();
	}

	public void setZETWindowTitle( String additionalTitleBarText ) {
		String titleBarText = zcontrol.getProject().getProjectFile() != null ? zcontrol.getProject().getProjectFile().getName() : GUILocalization.getSingleton().getString( "NewFile" );
		titleBarText += " " + additionalTitleBarText + " - " + GUILocalization.getSingleton().getString( "AppTitle" );
		editor.setTitle( titleBarText );
	}

	public void showFloor( Floor floor ) {
		editview.changeFloor( floor );
	}

	public void showPolygon( PlanPolygon<?> room ) {
		editview.getFloor().showPolygon( room );
	}

	public void setSelectedPolygon() {

	}

	public void setSelectedPolygon( PlanPolygon<?> poly ) {
		editview.getFloor().setSelectedPolygon( poly );
	}


	public void createBuildingDataStructure() {
		algorithmControl.convertBuildingPlan( new PropertyChangeListener() {

			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				if( isDone( pce ) )
					visualization.getControl().setBuildingControl( algorithmControl.getBuildingResults() );
			}
		});
	}
	
	private boolean isDone( PropertyChangeEvent pce ) {
		if( pce.getPropertyName().equals( "state" ) )
			if( pce.getNewValue().equals( SwingWorker.StateValue.DONE ) )
				return true;
		return false;
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
							if( ZETMain.isDebug() ) {
								System.err.println( "DEBUG-out:" );
								System.err.println( "Error during conversion in Room " + ex.getInvalidEdge().getRoom().getName() + ". " + ex.getMessage() );
								System.err.println();
							}
							System.err.println( ex.getMessage() );
							JOptionPane.showMessageDialog( null, " Fehler in Raum " + ex.getInvalidEdge().getRoom().getName() + ". \n" + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE );

							
							int ret = JOptionPane.showOptionDialog( editor, "Soll die Kante unpassierbar gemacht werden? \n (Dies in den meisten Fällen sinnvoll.)", "Fehler kann behoben werden.", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null );
							switch( ret ) {
								case JOptionPane.YES_OPTION:
									System.out.println( "YES" );
									zcontrol.disconnectAtEdge( ex.getInvalidEdge() );
									break;
								case JOptionPane.NO_OPTION:
									System.out.println( "NO" );
									break;
								default:
									throw new IllegalStateException( "A selection that is not supported has been made." );
							}
						}
					} else {
						CAVisualizationResults caVis = new CAVisualizationResults( algorithmControl.getMapping(), algorithmControl.getCellularAutomaton().getPotentialManager() );

						visualization.getControl().setCellularAutomatonControl( caVis, algorithmControl.getCellularAutomaton() );
						editor.getVisualizationView().updatePotentialSelector();
	//					editor.getVisualizationView().updateFloorSelector();
						editor.getQuickVisualizationView().getLeftPanel().getMainComponent().setSimulationData( algorithmControl.getCellularAutomaton(), algorithmControl.getContainer(), algorithmControl.getMapping() );
						editor.getQuickVisualizationView().displayFloor( editview.getCurrentFloor() );
						firstProgress = true;
					}
				}
			}
		});
	}

	public void createConcreteAssignment() {
		try {
			algorithmControl.createConcreteAssignment();
		} catch( IllegalArgumentException ex ) {
			Logger.getLogger( GUIControl.class.getName() ).log( Level.SEVERE, null, ex );
		} catch( ConversionNotSupportedException ex ) {
			Logger.getLogger( GUIControl.class.getName() ).log( Level.SEVERE, null, ex );
		}
	}

	public void performSimulation() {
		createBuildingDataStructure(); // kein task
		algorithmControl.performSimulation( new PropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				if( isDone( pce ) ) {
					System.out.println( "Left individuals: " + algorithmControl.getCellularAutomaton().getIndividualCount() );
					visualization.getControl().setCellularAutomatonControl( algorithmControl.getCaVisResults(), algorithmControl.getCellularAutomaton() );
					editor.getVisualizationView().updatePotentialSelector();
					visualizationToolBar.setEnabledPlayback( true );
					editor.getQuickVisualizationView().getLeftPanel().getMainComponent().setSimulationData( algorithmControl.getCellularAutomaton(), algorithmControl.getContainer(), algorithmControl.getMapping() );
					editor.getQuickVisualizationView().displayFloor( editview.getCurrentFloor() ); // hier startet ein task!
					System.out.println( "Simulation beendet." );
				}
			}
		} );
	}

	public void performQuickVisualization() {
		//createBuildingDataStructure();
		System.out.println( "QUICK" );
		algorithmControl.performSimulationQuick( new PropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent pce ) {
				System.out.println( "Property Change Event" );
			}

		}, this );
	}

	public void setUpSimulationAlgorithm() {
		algorithmControl.setUpSimulationAlgorithm();
	}

	boolean init = false;

	public void pauseSimulation() {
		algorithmControl.pauseStepByStep();
	}
	
	public void performOneStep() {
		final AlgorithmListener listener = this;
		try {
			algorithmControl.performOneStep( new PropertyChangeListener() {
				private boolean first = true;
				@Override
				public void propertyChange( PropertyChangeEvent pce ) {
					if( first ) {
						while( algorithmControl.getCellularAutomaton() == null ) {
							try {
								Thread.sleep( 100 );
							} catch( InterruptedException ex ) {
								Logger.getLogger( AlgorithmControl.class.getName() ).log( Level.SEVERE, null, ex );
							}
						}
						editor.getQuickVisualizationView().getLeftPanel().getMainComponent().setSimulationData( algorithmControl.getCellularAutomaton(), algorithmControl.getContainer(), algorithmControl.getMapping() );
						editor.getQuickVisualizationView().displayFloor( editview.getCurrentFloor() );
						editor.getQuickVisualizationView().getLeftPanel().getMainComponent().update();
					} else {
						editor.getQuickVisualizationView().getLeftPanel().getMainComponent().update();
					}
				}

		}, listener );
		} catch( ConversionNotSupportedException ex ) {
			ex.printStackTrace( System.err );
		}
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
					GraphVisualizationResults gvr = new GraphVisualizationResults( algorithmControl.getNetworkFlowModel() );
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
		} );
	}
        
        public void performOptimizationCompare() {
                algorithmControl.performOptimizationCompare(new PropertyChangeListener(){
                
                @Override
			public void propertyChange( PropertyChangeEvent pce ) {
				if( isDone( pce ) ) {
					GraphVisualizationResults gvr = algorithmControl.getGraphVisResults();
                                        CompareVisualizationResults cvr = algorithmControl.getCompVisResults();
					//visualization.getControl().setGraphControl( gvr );
                                        visualization = editor.getVisualizationView().getGLContainer();
                                        /*visualization.getControl().setCompControl(cvr);
                                        visualization.repaint();*/
                                        
                                        
				}
			}
            
            });
        }

	boolean firstProgress = false;

	@Override
	public void eventOccurred( AlgorithmEvent event ) {
		if( event instanceof AlgorithmProgressEvent ) {
			AlgorithmProgressEvent ape = (AlgorithmProgressEvent) event;
			System.out.println( "Progress: " + ape.getProgress() );

		if( !firstProgress ) {
			CellularAutomaton ca = algorithmControl.getCellularAutomaton();
			if( ca != null ) {
					editor.getQuickVisualizationView().getLeftPanel().getMainComponent().setSimulationData( algorithmControl.getCellularAutomaton(), algorithmControl.getContainer(), algorithmControl.getMapping() );
				editor.getQuickVisualizationView().displayFloor( editview.getCurrentFloor() );
				editor.getQuickVisualizationView().getLeftPanel().getMainComponent().update();
				firstProgress = true;
				System.out.println( "First time floor drawn." );
			}
		} else
			editor.getQuickVisualizationView().getLeftPanel().getMainComponent().update();
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

	protected void handleProgressEvent( int progress ) {
		editor.setProgressValue( progress );
		if( progress == 100 )
			editor.disableProgressBar();
	}

	public void executeTask( Runnable task ) {
		// Execute task
		editor.enableProgressBar();
		AlgorithmTask worker = AlgorithmTask.getNewInstance();
		worker.setTask( task );
		worker.addPropertyChangeListener( pcl );
		try {
			worker.executeAlgorithm( true );
			AlgorithmTask.getInstance().setProgress( 100, "", "" );
		} catch( Exception ex ) {
			System.out.println( "Fehler trat auf" );
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
}



// TODO get status out of property container, without creating new class variables!
