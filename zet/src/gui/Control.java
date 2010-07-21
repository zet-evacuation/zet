/*
 * Control.java
 * Created 16.07.2010, 12:18:41
 */
package gui;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import java.util.Locale;
import zet.gui.JEditor;
import batch.BatchResult;
import batch.BatchResultEntry;
import batch.tasks.BatchGraphCreateOnlyTask;
import batch.tasks.RasterizeTask;
import batch.tasks.VisualizationDataStructureTask;
import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.common.util.IOTools;
import de.tu_berlin.math.coga.zet.DatFileReaderWriter;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.PropertyContainer;
import ds.z.Assignment;
import ds.z.AssignmentArea;
import ds.z.ConcreteAssignment;
import ds.z.EvacuationArea;
import ds.z.Floor;
import ds.z.Room;
import ds.z.exception.TooManyPeopleException;
import gui.components.progress.JProgressBarDialog;
import gui.components.progress.JRasterizeProgressBarDialog;
import gui.editor.AreaVisibility;
import gui.editor.CoordinateTools;
import gui.editor.EditMode;
import gui.editor.GUIOptionManager;
import gui.editor.flooredit.FloorImportDialog;
import zet.gui.components.JZETMenuBar;
import zet.gui.components.toolbar.JEditToolbar;
import zet.gui.components.toolbar.JVisualizationToolbar;
import gui.visualization.AbstractVisualization;
import gui.visualization.Visualization.RecordingMode;
import gui.visualization.control.GLControl.CellInformationDisplay;
import io.DXFWriter;
import io.movie.MovieManager;
import io.visualization.BuildingResults;
import io.visualization.CAVisualizationResults;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import statistic.ca.CAStatistic;

/**
 * This class receives commands and GUI changes from elements like tool bars,
 * menus etc. and delegates them to other classes.
 * @author Jan-Philipp Kappmeier
 */
public class Control {

	/** The editor. */
	public JEditor editor;
	/** The edit tool bar. */
	JEditToolbar editToolBar;
	/** The visualization tool bar. */
	private JVisualizationToolbar visualizationToolBar;
	/** The menu bar. */
	private JZETMenuBar menuBar;

	/**
	 * Creates a new instance of <code>Control</code>.
	 */
	public Control() {
	}

	/**
	 * Sets the Zoom factor on the currently shown shown JFloor.
	 * @param zoomFactor the zoom factor
	 */
	public void setZoomFactor( double zoomFactor ) {
		double zoomChange = zoomFactor / CoordinateTools.getZoomFactor();
		Rectangle oldView = new Rectangle( editor.getEditView().getLeftPanel().getViewport().getViewRect() );
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
		editor.getEditView().getLeftPanel().setZoomFactor( zoomFactor );
		editor.getEditView().getFloor().getPlanImage().update();
		editor.getEditView().updateFloorView();
//		if( worker != null ) {
//			 caView.getLeftPanel().setZoomFactor( zoomFactor );
//			caView.updateFloorView();
//		}

		//if( editToolBar != null )
		editToolBar.setZoomFactorText( zoomFactor );

		//Redisplay the same portion of the Floor as before (move scrollbars)
		editor.getEditView().getLeftPanel().getViewport().setViewPosition( oldView.getLocation() );
	}

	/**
	 * Displays a specified type of areas. The selection parameter of the
	 * menu entry is set correct, too.
	 * @param areaType the are type
	 */
	public void showArea( AreaVisibility areaType ) {
		switch( areaType ) {
			case Delay:
				//TODO Menu
				//editor.mnuDelayArea.setSelected( true );
				break;
			case Stair:
				//editor.mnuStairArea.setSelected( true );
				break;
			case Evacuation:
				//editor.mnuEvacuationArea.setSelected( true );
				break;
			case Inaccessible:
				//editor.mnuInaccessibleArea.setSelected( true );
				break;
			case Save:
				//editor.mnuSaveArea.setSelected( true );
				break;
			case Assignment:
				//editor.mnuAssignmentArea.setSelected( true );
				break;
			case Teleport:
				// TODO
				break;
			default:
				JEditor.showErrorMessage( "Error", "Dieser Area-Typ wird nicht unterstützt." );
		}
		editor.updateAreaVisiblity();
	}

	public void setEditMode( EditMode mode ) {
		if( editor.getEditView() != null ) {
			editor.getEditView().setEditMode( mode );

			editToolBar.setEditSelectionSelected( false );
			editToolBar.setEditPointwiseSelected( mode.getType() == EditMode.Type.CreationPointwise );
			editToolBar.setEditRectangledSelected( mode.getType() == EditMode.Type.CreationRectangled );
		}
	}

	/**
	 * Exits the program.
	 */
	public void exit() {
		System.exit( 0 );
	}

	public void setEditToolbar( JEditToolbar aThis ) {
		editToolBar = aThis;
	}

	public void setVisualizationToolbar( JVisualizationToolbar aThis ) {
		visualizationToolBar = aThis;
	}
	boolean visualization3D = true;
	boolean visualization2D = true;

	public void visualizationToggle2D3D() {
		visualization3D = !visualization3D;
		visualizationToolBar.setSelected2d3d( visualization3D );
		visualizationToolBar.setEnabled2d( visualization2D );
//				btn2d3dSwitch.setSelected( !btn2d3dSwitch.isSelected() );
//				btn2dSwitch.setEnabled( !btn2dSwitch.isEnabled() );
		PropertyContainer.getInstance().set( "settings.gui.visualization.2d", !visualization3D );
		editor.getVisualizationView().getGLContainer().toggleView();
		editor.getVisualizationView().getGLContainer().repaint();
	}

	public void visualizationToggle2D() {
		visualization2D = !visualization2D;
		visualizationToolBar.setSelected2d( visualization2D );
		//btn2dSwitch.setSelected( !btn2dSwitch.isSelected() );
		PropertyContainer.getInstance().set( "settings.gui.visualization.isometric", visualization2D );
		if( editor.getVisualizationView().getGLContainer().getParallelViewMode() == AbstractVisualization.ParallelViewMode.Orthogonal )
			editor.getVisualizationView().getGLContainer().setParallelViewMode( AbstractVisualization.ParallelViewMode.Isometric );
		else
			editor.getVisualizationView().getGLContainer().setParallelViewMode( AbstractVisualization.ParallelViewMode.Orthogonal );
		editor.getVisualizationView().getGLContainer().repaint();
	}

	// TODO move to visualization class (at least parts)
	public void createVideo() {
		if( editor.getVisualizationView().getGLContainer().isAnimating() )
			editor.getVisualizationView().getGLContainer().stopAnimation();
		VideoOptions vo = new VideoOptions( editor );
		// Setze die erwartete Laufzeit
		vo.setEstimatedTime( editor.getVisualizationView().getGLContainer().getControl().getEstimatedTime() );
		vo.setResolution( editor.getVisualizationView().getGLContainer().getSize() );
		vo.setBitrate( 1000 );
		vo.setFramerate( 24 );
		vo.setTextureFontStrings( editor.getVisualizationView().getGLContainer().getTexts() );
		vo.setVisible( true );
		vo.dispose();
		if( vo.getRetVal() == VideoOptions.OK ) {
			editor.getVisualizationView().getGLContainer().setTexts( vo.getTextureFontStrings() );
			editor.getZControl().getProject().getVisualProperties().setTextureFontStrings( vo.getTextureFontStrings() );
			String movieFrameName = PropertyContainer.getInstance().getAsString( "options.filehandling.movieFrameName" );
			// TODO BUG: wenn ein projekt noch nicht gespeichert worden ist, liefert das hier iene null pointer exception. (tritt auf, wenn ein video gedreht werden soll)
			String projectName = editor.getZControl().getProject().getProjectFile().getName().substring( 0, editor.getZControl().getProject().getProjectFile().getName().length() - 4 );
			MovieManager movieCreator = editor.getVisualizationView().getGLContainer().getMovieCreator();
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
			editor.getVisualizationView().getGLContainer().setRecording( RecordingMode.Recording, vo.getResolution() );
			movieCreator.setWidth( vo.getResolution().width );
			movieCreator.setHeight( vo.getResolution().height );
			movieCreator.setCreateMovie( vo.isMovieMode() );
			movieCreator.setDeleteFrames( vo.isDeleteFrames() );
			movieCreator.setMovieFormat( vo.getMovieFormat() );
			movieCreator.setFramerate( vo.getFramerate() );
			movieCreator.setBitrate( vo.getBitrate() );
			editor.getVisualizationView().getGLContainer().setMovieFramerate( vo.getFramerate() );
			movieCreator.setFrameFormat( vo.getFrameFormat() );
			visualizationToolBar.play();
			if( !editor.getVisualizationView().getGLContainer().isAnimating() )
				editor.getVisualizationView().getGLContainer().startAnimation();
		}
	}

	public void takeScreenshot() {
		String path = PropertyContainer.getInstance().getAsString( "options.filehandling.screenshotPath" );
		if( !(path.endsWith( "/" ) || path.endsWith( "\\" )) )
			path = path + "/";
		String projectName;
		try {
			projectName = editor.getZControl().getProject().getProjectFile().getName().substring( 0, editor.getZControl().getProject().getProjectFile().getName().length() - 4 );
		} catch( NullPointerException ex ) {
			projectName = "untitled";
		}
		String newFilename = IOTools.getNextFreeNumberedFilepath( path, projectName, 3 ) + ".png";
		editor.getVisualizationView().getGLContainer().takeScreenshot( newFilename );

	}

	public void visualizationTurnBackToStart() {
		editor.getVisualizationView().getGLContainer().getControl().resetTime();
		//control.resetTime();
		editor.getVisualizationView().getGLContainer().repaint();
	}

	public void visualizationPause() {
		if( editor.getVisualizationView().getGLContainer().isAnimating() ) {
			visualizationToolBar.pause();
			editor.getVisualizationView().getGLContainer().stopAnimation();
		}
	}

	public void visualizationPlay() {
		// TODO restartVisualization
//				if( restartVisualization ) {
//					editor.getVisualizationView().getGLContainer().getControl().resetTime();
//					restartVisualization = false;
//				}
		if( editor.getVisualizationView().getGLContainer().isAnimating() ) {
			visualizationToolBar.pause();
			//btnPlay.setIcon( playIcon );
			//btnPlay.setSelected( false );
			editor.getVisualizationView().getGLContainer().stopAnimation();
			if( editor.getVisualizationView().getGLContainer().getRecording() == RecordingMode.Recording )
				editor.getVisualizationView().getGLContainer().setRecording( RecordingMode.SkipFrame );
		} else {
			visualizationToolBar.play();
			//btnPlay.setIcon( pauseIcon );
			//btnPlay.setSelected( true );
			if( editor.getVisualizationView().getGLContainer().getRecording() == RecordingMode.SkipFrame )
				editor.getVisualizationView().getGLContainer().setRecording( RecordingMode.Recording );
			editor.getVisualizationView().getGLContainer().startAnimation();
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
		editor.getVisualizationView().getGLContainer().getControl().resetTime();
		// create a movie, if movie-creation was active.
		if( editor.getVisualizationView().getGLContainer().getRecording() != RecordingMode.NotRecording )
			editor.getVisualizationView().getGLContainer().createMovie();
		// stop animation if still animation
		if( editor.getVisualizationView().getGLContainer().isAnimating() )
			editor.getVisualizationView().getGLContainer().stopAnimation();
		// repaint once
		editor.getVisualizationView().getGLContainer().repaint();
	}

	private boolean showWalls = true;

	public void visualizationShowWalls() {
		showWalls = !showWalls;
		visualizationToolBar.setSelectedShowWalls( showWalls );
		//btnShowWalls.setSelected( !btnShowWalls.isSelected() );
		PropertyContainer.getInstance().set( "settings.gui.visualization.walls", showWalls );
		editor.getVisualizationView().getGLContainer().getControl().showWalls( showWalls );
		editor.getVisualizationView().getGLContainer().repaint();
	}

	private boolean showGraph = false;

	public void visualizationShowGraph() {
		showGraph = !showGraph;
		visualizationToolBar.setSelectedShowGraph( showGraph );
		//btnShowGraph.setSelected( !btnShowGraph.isSelected() );
		PropertyContainer.getInstance().set( "settings.gui.visualization.graph", showGraph );
		editor.getVisualizationView().getGLContainer().getControl().showGraph( showGraph );
		editor.getVisualizationView().getGLContainer().repaint();
	}

	public boolean showGraphGrid = false;

	public void visualizationShowGraphGrid() {
		showGraphGrid = !showGraphGrid;
		visualizationToolBar.setSelectedShowGraphGrid( showGraphGrid );
		//btnShowGraphGrid.setSelected( !btnShowGraphGrid.isSelected() );
		PropertyContainer.getInstance().set( "settings.gui.visualization.nodeArea", showGraphGrid );
		editor.getVisualizationView().getGLContainer().getControl().showNodeRectangles( showGraphGrid );
		editor.getVisualizationView().getGLContainer().repaint();

	}

	public boolean showCellularAutomaton = true;

	public void visualizationShowCellularAutomaton() {
		showCellularAutomaton = !showCellularAutomaton;
		visualizationToolBar.setSelectedShowCellularAutomaton( showCellularAutomaton );
		//btnShowCellularAutomaton.setSelected( !btnShowCellularAutomaton.isSelected() );
		PropertyContainer.getInstance().set( "settings.gui.visualization.cellularAutomaton", showCellularAutomaton );
		editor.getVisualizationView().getGLContainer().getControl().showCellularAutomaton( showCellularAutomaton );
		editor.getVisualizationView().getGLContainer().repaint();

	}

	public void visualizationShowAllFloors() {
		final boolean showAllFloors = PropertyContainer.getInstance().toggle( "settings.gui.visualization.floors" );
		visualizationToolBar.setSelectedAllFloors( !showAllFloors );
		//btnShowAllFloors.setSelected( !btnShowAllFloors.isSelected() );
		editor.getVisualizationView().setFloorSelectorEnabled( !showAllFloors );
		if( showAllFloors )
			editor.getVisualizationView().getGLContainer().getControl().showAllFloors();
		else
			editor.getVisualizationView().getGLContainer().getControl().showFloor( editor.getVisualizationView().getSelectedFloorID() );
		editor.getVisualizationView().getGLContainer().repaint();
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
			editor.getVisualizationView().getGLContainer().getControl().showPotential( CellInformationDisplay.NoPotential );
		} else {
			visualizationToolBar.setSelectedCellInformationDisplay( cid );
			PropertyContainer.getInstance().set( "settings.gui.visualization.floorInformation", cid.id() );
			editor.getVisualizationView().unselectPotentialSelector();
			if( cid == CellInformationDisplay.StaticPotential )
				editor.getVisualizationView().getGLContainer().getControl().activateMergedPotential();
			editor.getVisualizationView().getGLContainer().getControl().showPotential( cid );
		}
		editor.getVisualizationView().getGLContainer().repaint();
	}



	public void buildVisualizationDataStructure( BatchResultEntry e, int nrOfCycle ) {
		CAVisualizationResults caRes = e.getCaVis() != null ? e.getCaVis()[nrOfCycle] : null;
		CAStatistic caStatistic = e.getCaStatistics() != null ? e.getCaStatistics()[nrOfCycle] : null;
caRes.statistic = caStatistic;

		ds.GraphVisualizationResult graphRes = e.getGraphVis();

		VisualizationDataStructureTask visualizationDataStructure = new VisualizationDataStructureTask( caRes, graphRes, e.getBuildingResults(), caStatistic );
		JProgressBarDialog pbd = new JProgressBarDialog( editor, Localization.getInstance().getStringWithoutPrefix( "batch.tasks.buildVisualizationDatastructure" ), true, visualizationDataStructure );
		pbd.executeTask();
		pbd.setVisible( true );
		ZETMain.sendMessage( Localization.getInstance().getStringWithoutPrefix( "batch.tasks.progress.visualizationDatastructureComplete" ) );

		editor.setControl( visualizationDataStructure.getControl() );

		//control = visualizationDataStructure.getControl();

		editor.getVisualizationView().getGLContainer().setControl( visualizationDataStructure.getControl() );

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
		//entryModelVis.rebuild( result );
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
				for( Floor f : editor.getZControl().getProject().getBuildingPlan() ) {
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
				for( Floor f : editor.getZControl().getProject().getBuildingPlan() )
					for( Room r : f )
						for( EvacuationArea ea : r.getEvacuationAreas() ) {
							overall = 0;
							System.out.println( "" );
							System.out.println( ea.getName() );
							// Suche nach evakuierten pro etage für dieses teil
							for( Floor f2 : editor.getZControl().getProject().getBuildingPlan() ) {
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
				BatchResultEntry ca_res = new BatchResultEntry( editor.getZControl().getProject().getProjectFile().getName(), new BuildingResults( editor.getZControl().getProject().getBuildingPlan() ) );
				ConcreteAssignment[] concreteAssignments = new ConcreteAssignment[1];
				Assignment assignment = editor.getZControl().getProject().getCurrentAssignment();
				concreteAssignments[0] = assignment.createConcreteAssignment( 400 );
				new BatchGraphCreateOnlyTask( ca_res, 0, editor.getZControl().getProject(), assignment, concreteAssignments ).run();
				NetworkFlowModel originalProblem = ca_res.getNetworkFlowModel();
				EarliestArrivalFlowProblem problem = new EarliestArrivalFlowProblem( originalProblem.getEdgeCapacities(), originalProblem.getNetwork(), originalProblem.getNodeCapacities(), originalProblem.getSupersink(), originalProblem.getSources(), 0, originalProblem.getTransitTimes(), originalProblem.getCurrentAssignment() );
				try {
					DatFileReaderWriter.writeFile( editor.getZControl().getProject().getName(), problem, editor.getZControl().getProject().getProjectFile().getName().substring( 0, editor.getZControl().getProject().getProjectFile().getName().length()-4 ) + ".dat", originalProblem.getZToGraphMapping() );
				} catch( FileNotFoundException ex ) {
					ZETMain.sendError( "FileNotFoundException" );
					ex.printStackTrace( System.err );
				} catch( IOException ex ) {
					ZETMain.sendError( "IOException" );
					ex.printStackTrace( System.err );
				}

	}

	public void switchToLanguage( Locale locale ) {
		Localization.getInstance().setLocale( locale );
		editor.localize();
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
			editor.getZControl().loadProject( jfcProject.getSelectedFile() );
			editor.loadProject();	// Load the currently loaded project by the control file
			GUIOptionManager.setSavePath( jfcProject.getCurrentDirectory().getPath() );
			GUIOptionManager.setLastFile( 1, jfcProject.getSelectedFile().getAbsolutePath() );
		}

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
			if( jfcProject.getSelectedFile().exists() && createCopy )
				JEditor.createBackup( jfcProject.getSelectedFile() );
			try {
				File target = jfcProject.getSelectedFile();
				if( !target.getName().endsWith( ".zet" ) && !target.getName().endsWith( ".gzet" ) )
					target = new File( target.getAbsolutePath() + ".zet" );
				editor.getZControl().getProject().save( target );
			} catch( java.lang.StackOverflowError soe ) {
				JEditor.showErrorMessage( Localization.getInstance().getString( "gui.editor.JEditor.error.stackOverflowTitle" ), Localization.getInstance().getString( "gui.editor.JEditor.error.stackOverflow" ) );
			} catch( Exception ex ) {
				JEditor.showErrorMessage( Localization.getInstance().getString( "gui.editor.JEditor.error.SaveTitle" ), Localization.getInstance().getString( "gui.editor.JEditor.error.Save" ) );
			}
			editor.getEditView().displayProject( editor.getZControl() );
			ZETMain.sendMessage( Localization.getInstance().getString( "gui.editor.JEditor.message.saved" ) );
		}

	}

	public void saveProject() {
		if( editor.getZControl().getProject().getProjectFile() == null )
			saveProjectAs();
		else {
			if( createCopy == true )
				editor.createBackup();
			try {
				editor.getZControl().getProject().save();
			} catch( java.lang.StackOverflowError soe ) {
				JEditor.showErrorMessage( Localization.getInstance().getString( "gui.editor.JEditor.error.stackOverflowTitle" ), Localization.getInstance().getString( "gui.editor.JEditor.error.stackOverflow" ) );
			} catch( Exception ex ) {
				JEditor.showErrorMessage( Localization.getInstance().getString( "gui.editor.JEditor.error.SaveTitle" ), Localization.getInstance().getString( "gui.editor.JEditor.error.Save" ) );
				ex.printStackTrace( System.err );
				return;
			}
			ZETMain.sendMessage( Localization.getInstance().getString( "gui.editor.JEditor.message.saved" ) );
		}
	}

	// TODO auslagern von save und methoden aufrufen!
	public void newProject() {
				String status = "";
				switch( JOptionPane.showOptionDialog( editor,
								Localization.getInstance().getString( "gui.editor.JEditor.SaveQuestion" ),
								Localization.getInstance().getString( "gui.editor.JEditor.NewProject" ),
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								null, null, null ) ) {
					case 2:
					case -1:
						return;	// exit, do nothing
					case 0:
						// save
						if( editor.getZControl().getProject().getProjectFile() == null ) {
							if( jfcProject.showSaveDialog( editor ) == JFileChooser.APPROVE_OPTION ) {
								GUIOptionManager.setSavePath( jfcProject.getCurrentDirectory().getPath() );
								if( jfcProject.getSelectedFile().exists() && createCopy )
									JEditor.createBackup( jfcProject.getSelectedFile() );
								try {
									File target = jfcProject.getSelectedFile();
									if( !target.getName().endsWith( ".zet" ) && !target.getName().endsWith( ".gzet" ))
										target = new File( target.getAbsolutePath() + ".zet" );
									editor.getZControl().getProject().save( target );
								} catch( java.lang.StackOverflowError soe ) {
									JEditor.showErrorMessage( Localization.getInstance().getString( "gui.editor.JEditor.error.stackOverflowTitle" ), Localization.getInstance().getString( "gui.editor.error.JEditor.stackOverflow" ) );
								} catch( Exception ex ) {
									JEditor.showErrorMessage( Localization.getInstance().getString( "gui.editor.JEditor.error.SaveTitle" ), Localization.getInstance().getString( "gui.editor.JEditor.error.Save" ) );
									ex.printStackTrace( System.err );
									return;
								}
							}
						} else {
							if( createCopy )
								editor.createBackup();
							try {
								editor.getZControl().getProject().save();
							} catch( java.lang.StackOverflowError soe ) {
								JEditor.showErrorMessage( Localization.getInstance().getString( "gui.editor.JEditor.error.stackOverflowTitle" ), Localization.getInstance().getString( "gui.editor.JEditor.error.stackOverflow" ) );
							} catch( Exception ex ) {
								JEditor.showErrorMessage( Localization.getInstance().getString( "gui.editor.JEditor.error.SaveTitle" ), Localization.getInstance().getString( "gui.editor.JEditor.error.Save" ) );
								ex.printStackTrace( System.err );
								return;
							}
						}
						status = Localization.getInstance().getString( "gui.editor.JEditor.status.newProject" );
						break;
					case 1:
						status = Localization.getInstance().getString( "gui.editor.JEditor.status.newProjectDiscard" );
				}
				// TODO: better (next 3 lines)
				editor.getZControl().newProject();

				//distribution = null; // Throw away the old assignment window
				editor.resetAssignment(); // TODO warum so?
				editor.getEditView().displayProject( editor.getZControl() );
				ZETMain.sendMessage( status );
	}

	public void saveAsDXF() {
				String filename = editor.getZControl().getProject().getProjectFile().getPath().substring( 0, editor.getZControl().getProject().getProjectFile().getPath().length() - 3 ) + "dxf";
				try {
					DXFWriter.exportIntoDXF( filename, editor.getZControl().getProject().getBuildingPlan() );
				} catch( IOException ex ) {
					JEditor.showErrorMessage( Localization.getInstance().getString( "gui.editor.JEditor.error.SaveTitle" ), Localization.getInstance().getString( "gui.editor.JEditor.error.Save" ) );
					ex.printStackTrace( System.err );
					return;
				}
				ZETMain.sendMessage( Localization.getInstance().getString( "gui.editor.JEditor.message.dxfComplete" ) );

	}

	public void newFloor() {
		editor.getZControl().getProject().getBuildingPlan().addFloor( new Floor( Localization.getInstance().getString( "ds.z.DefaultName.Floor" ) + " " + editor.getZControl().getProject().getBuildingPlan().floorCount() ) );
		ZETMain.sendMessage( "Neue Etage angelegt." ); // TODO loc
	}

	public void moveFloorUp() {
		final int oldIndex = editor.getEditView().getFloorID();
		editor.getZControl().moveFloorUp( editor.getEditView().getFloorID() + (ZETProperties.isDefaultFloorHidden() ? 1 : 0) );
		editor.getEditView().setFloor( oldIndex + 1 );
	}

	public void moveFloorDown() {
		final int oldIndex = editor.getEditView().getFloorID();
		editor.getZControl().moveFloorDown( editor.getEditView().getFloorID() + (ZETProperties.isDefaultFloorHidden() ? 1 : 0) );
		editor.getEditView().setFloor( oldIndex - 1 );

	}

	public void deleteFloor() {
		editor.getZControl().getProject().getBuildingPlan().removeFloor( editor.getEditView().getCurrentFloor() );
	}

	public void importFloor() {
		FloorImportDialog floorImport = new FloorImportDialog( editor, editor.getZControl().getProject(), "Importieren", 450, 250 );
		floorImport.setVisible( true );
	}

	public void copyFloor() {
		final int oldIndex = editor.getEditView().getFloorID();
		editor.getZControl().copyFloor( editor.getEditView().getCurrentFloor() );
		editor.getEditView().setFloor( oldIndex );
	}

	public void rasterize() {
		try {
			RasterizeTask rasterize = new RasterizeTask( editor.getZControl().getProject() );
			JProgressBarDialog pbd = new JRasterizeProgressBarDialog( editor, "Rastern", true, rasterize );
			pbd.executeTask();
			pbd.setVisible( true );
			ZETMain.sendMessage( Localization.getInstance().getString( "gui.message.RasterizationComplete" ) );
		} catch( Exception ex ) {
			ZETMain.sendError( ex.getLocalizedMessage() );
		}

	}

	// TODO dieses feature überarbeiten
	public void distributeEvacuees() {
		try {
			String res = JOptionPane.showInputDialog( editor,
							"Anzahl zu evakuierender Personen (maximal "
							+ Integer.toString( editor.getZControl().getProject().getBuildingPlan().maximalEvacuees() ) + ")", "Personen verteilen", JOptionPane.QUESTION_MESSAGE );

			if( res != null ) {
				editor.getZControl().getProject().getBuildingPlan().distributeEvacuees( Integer.parseInt( res ) );
				ZETMain.sendMessage( Localization.getInstance().getString( "gui.message.RasterizationComplete" ) );
			}
		} catch( NumberFormatException ex ) {
			ZETMain.sendError( Localization.getInstance().getString( "gui.error.NonParsableNumber" ) );
		} catch( TooManyPeopleException ex ) {
			ZETMain.sendError( ex.getLocalizedMessage() );
		}

	}

	public void distribution() {
		editor.showAssignmentDialog();
	}

}
// TODO get status out of property container, without creating new class variables!
/*
editor.getEditView()
Localization.getInstance()
editor.getVisualizationView()
editor.getVisualizationView().getGLContainer().getControl()
 */