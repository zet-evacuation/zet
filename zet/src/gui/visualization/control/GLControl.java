/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package gui.visualization.control;

import ds.PropertyContainer;
import ds.GraphVisualizationResult;
import ds.ca.CellularAutomaton;
import ds.ca.PotentialManager;
import ds.ca.StaticPotential;
import ds.ca.results.Action;
import ds.ca.results.InconsistentPlaybackStateException;
import ds.ca.results.MoveAction;
import ds.ca.results.VisualResultsRecording;
import gui.visualization.control.building.GLBuildingControl;
import gui.visualization.control.ca.GLCAControl;
import gui.visualization.control.ca.GLCAFloorControl;
import gui.visualization.control.ca.GLCellControl;
import gui.visualization.control.ca.GLIndividualControl;
import gui.visualization.control.ca.GLRoomControl;
import gui.visualization.control.graph.GLGraphControl;
import gui.visualization.control.graph.GLEdgeControl;
import gui.visualization.control.graph.GLGraphFloorControl;
import gui.visualization.control.graph.GLNodeControl;
import gui.visualization.draw.building.GLBuilding;
import gui.visualization.draw.ca.GLCA;
import gui.visualization.draw.ca.GLIndividual;
import gui.visualization.draw.graph.GLGraph;
import io.visualization.BuildingResults;
import io.visualization.CAVisualizationResults;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.media.opengl.GLAutoDrawable;
import localization.Localization;
import opengl.framework.abs.drawable;
import statistic.ca.CAStatistic;
import batch.tasks.AlgorithmTask;
import util.DebugFlags;

public class GLControl implements drawable {

	/**
	 * Describes the differend types of information which can be illustrated
	 * by different colors of the cells of the cellular automaton.
	 */
	public enum CellInformationDisplay {
		/** Disables displaying any potential on the floor of cells */
		NO_POTENTIAL,
		/** Enables displaying of static potential on the floor of cells */
		STATIC_POTENTIAL,
		/** Enables displaying of the dynamic potential on the floor of cells */
		DYNAMIC_POTENTIAL,
		/** Enables displaying usage statistic on the cells */
		UTILIZATION,
		/** Shows waiting times on cells. */
		WAITING
	}
	
	/** 
	 * Describes the different types of information which can be illustrated
	 * by different colors of the heads of the individuals.
	 */
	public enum IndividualInformationDisplay {
		/** Shows default individual */
		NOTHING,
		/** Shows panic at the head */
		PANIC,
		/** Shows speed at the head */
		SPEED,
		/** Shows exhaustion at the head */
		EXHAUSTION,
		/** Shows the alarm-status at the head */
		ALARMED,
		/** Shows the chosen exit at the head*/
		CHOSEN_EXIT
	}
	/** The localization class. */
	private Localization loc = Localization.getInstance();
	/** Indicates wheather the graph is currently visible, or not. */
	private boolean showGraph;
	/** Indicates wheather the cellular automaton is currently visible, or not. */
	private boolean showCA;
	/** Indicates wheather the walls are drawn. */
	private boolean showWalls = true;
	/** Indicates wheather the currently loaded visualizationresult contains a cellular automaton, or not. */
	private boolean hasCA;
	/** Indicates wheather the currently loaded visualizationresult contains a graph, or not. */
	private boolean hasGraph;
	/** Represents the static structure of the building, e.g. walls. */
	private BuildingResults buildingResults;
	/** Represents the statistic  */
	private CAStatistic caStatistic;
	/** The view object for the cellular automaton. */
	private GLCA caView;
	/** The view object for the graph. */
	private GLGraph graphView;
	private GLBuilding buildingView;
	private GLCAControl caControl;
	private GLGraphControl graphControl;
	private GLBuildingControl buildingControl;
	private double realStepCA;
	private double realStepGraph;
	private double secondsPerStepCA;
	private double secondsPerStepGraph;
	private long nanoSecondsPerStepCA;
	private long nanoSecondsPerStepGraph;
	/** The estimated time used for the whole visualization in seconds. */
	private double estimatedTime = 0;
	private long time;
	private long stepCA;
	private long stepGraph;
	private long timeSinceLastStepCA = 0;
	private long timeSinceLastStepGraph = 0;
	private double speedFactor = 1;
	private VisualResultsRecording visRecording;
	private ArrayList<GLCellControl> cells;
	private CellularAutomaton ca;
	private ArrayList<GLNodeControl> nodes;
	private ArrayList<GLEdgeControl> edges;
	private List<GLIndividual> individuals;
	/** The maximal time step used for the graph */
	private int graphStepCount = 0;
	/** The status of the cellular automaton visualization, true if ca is finished */
	private boolean caFinished = true;
	/** The status of flow visualization, true if graph is finished */
	private boolean graphFinished = true;
	/** The status of the simulation, true if all is finished */
	private boolean finished = true;
	private int cellCount;
	private int cellsDone;
	private int nodeCount;
	private int nodesDone;
	private int wallCount;
	private int wallsDone;
	private int recordingCount;
	private int recordingDone;
	
	/**
	 * Initializes a new empty instance of the general control class for the
	 * visualization of an evacuation simulation. The instance does not contain
	 * any graph, building or cellular automaton data, its
	 * {@link #draw( GLAutoDrawable )} method doeas nothing.
	 */
	public GLControl( ) {
		showCA = false;
		hasCA = false;
		showGraph = false;
		hasGraph = false;
		showWalls = false;
	}

	/**
	 * Initializes a new instance of the general control class for the
	 * visualization of an evacuation simulation.
	 * @param caVisResults the visual results for cellular automatons
	 * @param graphVisResult the visual results for graph
	 * @param buildingResults the visual information about the building
	 * @param caStatistic the calculated statistic for cellular automaton
	 */
	public GLControl( CAVisualizationResults caVisResults, GraphVisualizationResult graphVisResult, BuildingResults buildingResults, CAStatistic caStatistic ) {
		this.caStatistic = caStatistic;
		this.buildingResults = buildingResults;
		GLCellControl.invalidateMergedPotential();
		if( caVisResults != null ) {
			caFinished = false;
			Runtime runtime = Runtime.getRuntime();
			long memStart = (runtime.totalMemory() - runtime.freeMemory());
			hasCA = true;
			ca = new CellularAutomaton( caVisResults.getRecording().getInitialConfig() );
			cellCount = ca.getCellCount();
			cellsDone = 0;
			recordingCount = caVisResults.getRecording().length();
			recordingDone = 0;
			AlgorithmTask.getInstance().setProgress( 0, loc.getStringWithoutPrefix( "batch.tasks.progress.createCellularAutomatonVisualizationDatastructure" ), "" );
			caControl = new GLCAControl( caVisResults, ca, this );
			long memEnd = (runtime.totalMemory() - runtime.freeMemory());
			if( DebugFlags.VIS_CA )
				System.out.println( "Speicher für ZA: " + (memEnd - memStart) + " Bytes" );
			individuals = caControl.getIndividuals();
			visRecording = caVisResults.getRecording();
			caView = caControl.getView();
			secondsPerStepCA = ca.getSecondsPerStep();
			estimatedTime = Math.max( estimatedTime, caVisResults.getRecording().length() * secondsPerStepCA );
			nanoSecondsPerStepCA = Math.round( secondsPerStepCA * 1000000000 );
			stepCA = 0;
			caVisResults.getRecording().rewind();
			for( Action action : caVisResults.getRecording().nextActions() ) {
				try {
					action.execute( ca );
					if( action instanceof MoveAction ) {
						System.out.println( action );
					}
				} catch( InconsistentPlaybackStateException e ) {
					e.printStackTrace();
				}
			}
			// füge alle zellen in die update-liste hinzu
			cells = new ArrayList<GLCellControl>();
			for( GLCAFloorControl floor : caControl.getAllFloors() ) {
				for( GLRoomControl room : floor ) {
					for( GLCellControl cell : room ) {
						cells.add( cell );	// TODO use addAll
					}
				}
				floor.getView().setIndividuals( caControl.getIndividualControls() );
			}
		} else {
			hasCA = false;
		}
		if( graphVisResult != null ) {
			graphFinished = false;
			AlgorithmTask.getInstance().setProgress( 0, loc.getStringWithoutPrefix( "batch.tasks.progress.createGraphVisualizationDataStructure" ), "" );
			hasGraph = true;
			nodeCount = graphVisResult.getNetwork().nodes().size();
			nodesDone = 0;

			stepGraph = 0;
			graphControl = new GLGraphControl( graphVisResult, this );
			graphView = graphControl.getView();
			nodes = new ArrayList<GLNodeControl>();
			edges = new ArrayList<GLEdgeControl>();

			for( GLGraphFloorControl g : graphControl.childControls ) {
				for( GLNodeControl node : g ) {
					for( GLEdgeControl edge : node ) {
						edges.add( edge );	// TODO use addAll
					}
					nodes.add( node );
				}
			}

			// Set speed such that it arrives when the last individual is evacuated.
			if( hasCA && PropertyContainer.getInstance().getAsBoolean( "options.visualization.flow.equalArrival" ) ) {
				nanoSecondsPerStepGraph = graphStepCount == 0 ? 0 : (nanoSecondsPerStepCA * caVisResults.getRecording().length()) / graphStepCount;
				secondsPerStepGraph = nanoSecondsPerStepGraph / (double)1000000000;
				System.err.println( "Für gleichzeitige Ankunft berechnete Geschwindigkeit: " + nanoSecondsPerStepGraph );
			} else {
				secondsPerStepGraph = secondsPerStepGraph();
				nanoSecondsPerStepGraph = Math.round( secondsPerStepGraph * 1000000000 );
				System.err.println( "Berechnete Geschwindigkeit (durchschnitt der ZA-Geschwindigkeiten): " + nanoSecondsPerStepGraph );
			}
			estimatedTime = Math.max( estimatedTime, graphStepCount * secondsPerStepGraph );
		} else {
			hasGraph = false;
		}
		time = 0;
		finished = caFinished && graphFinished;

		AlgorithmTask.getInstance().setProgress( 1, loc.getStringWithoutPrefix( "batch.tasks.progress.createBuildingVisualizationDataStructure" ), "" );
		wallCount = buildingResults.getWalls().size();
		wallsDone = 0;
		buildingControl = new GLBuildingControl( buildingResults, this );
		buildingView = buildingControl.getView();

		AlgorithmTask.getInstance().setProgress( 100, loc.getStringWithoutPrefix( "batch.tasks.progress.visualizationDatastructureComplete" ), "" );
		initSettings();
	}

	/**
	 * Initializes the visualization settings with the values stored in the
	 * {@link PropertyContainer}.
	 */
	private void initSettings() {
		showWalls( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.walls" ) );
		showGraph( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.graph" ) );
		showNodeRectangles( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.nodeArea" ) );
		showCellularAutomaton( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.cellularAutomaton" ) );
		if( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.floors" ) ) {
			showAllFloors();
		} else
			showFirstFloor();

		switch( PropertyContainer.getInstance().getAsInt( "settings.gui.visualization.floorInformation" ) ) {
			case 1:
				showPotential( CellInformationDisplay.STATIC_POTENTIAL );
				break;
			case 2:
				showPotential( CellInformationDisplay.DYNAMIC_POTENTIAL );
				break;
			case 3:
				showPotential( CellInformationDisplay.UTILIZATION );
				break;
			case 4:
				showPotential( CellInformationDisplay.WAITING );
				break;
			case 0:
			default:
				showPotential( CellInformationDisplay.NO_POTENTIAL );
				break;
		}
	}

	/**
	 * <p>This method increases the number of cells that are created and
	 * calculates a new progress. The progress will at most reach 99% so that
	 * after all objects are created a final "Done" message can be submitted.</p>
	 * <p>Note that before this method can be used in the proper way the private
	 * variable <code>cellsDone</code> and <code>cellCount</code> should be
	 * initialized correct. However, it is guaranteed to calculate a value from
	 * 0 to 99.
	 */
	public void cellProgress() {
		cellsDone++;
		int progress = Math.max( 0, Math.min( (int) Math.round( ((double) cellsDone / cellCount) * 100 ), 99 ) );
		AlgorithmTask.getInstance().setProgress( progress, "Erzeuge Zellen...", "Zelle " + cellsDone + " von " + cellCount + " erzeugt." );
	}

	/**
	 * <p>This method increases the number of individuals that are created and
	 * calculates a new progress. The progress will at most reach 99% so that
	 * after all objects are created a final "Done" message can be submitted.</p>
	 * <p>Note that before this method can be used in the proper way the private
	 * variable <code>cellsDone</code> and <code>cellCount</code> should be
	 * initialized correct. However, it is guaranteed to calculate a value from
	 * 0 to 99.
	 */
	public void recordingProgress() {
		recordingDone++;
		int progress = Math.max( 0, Math.min( (int) Math.round( ((double) recordingDone / recordingCount) * 100 ), 99 ) );
		AlgorithmTask.getInstance().setProgress( progress, "Erzeuge Individuen-Bewegungen...", "Recording-Schritt " + recordingDone + " von " + recordingCount + " abgearbeitet." );
	}

	/**
	 * <p>This method increases the number of nodes that are already created and
	 * calculates a new progress. The progress will at most reach 99% so that
	 * after all objects are created a final "Done" message can be submitted.</p>
	 * <p>Note that before this method can be used in the proper way the private
	 * variable <code>nodesDone</code> and <code>nodeCount</code> should be
	 * initialized correct. However, it is guaranteed to calculate a value from
	 * 0 to 99.
	 */
	public void nodeProgress() {
		nodesDone++;
		int progress = Math.max( 0, Math.min( (int) Math.round( ((double) nodesDone / nodeCount) * 100 ), 99 ) );
		AlgorithmTask.getInstance().setProgress( progress, "Erzeuge Graph...", "Knoten " + nodesDone + " von " + nodeCount + " erzeugt." );
	}

	/**
	 * <p>This method increases the number of cells that are created and
	 * calculates a new progress. The progress will at most reach 99% so that
	 * after all objects are created a final "Done" message can be submitted.</p>
	 * <p>Note that before this method can be used in the proper way the private
	 * variable <code>wallsDone</code> and <code>WallCount</code> should be
	 * initialized correct. However, it is guaranteed to calculate a value from
	 * 0 to 99.
	 */
	public void wallProgress() {
		wallsDone++;
		int progress = Math.max( 0, Math.min( (int) Math.round( ((double) wallsDone / wallCount) * 100 ), 99 ) );
		AlgorithmTask.getInstance().setProgress( progress, "Erzeuge Gebäude...", "Wand " + wallsDone + " von " + wallCount + " erzeugt." );
	}

	/**
	 * Calculates the average speed of all persons in the cellular automaton and
	 * sets calculates the seconds needed for one graph step depending on this
	 * speed.
	 * @return the seconds needed for one graph step
	 */
	private double secondsPerStepGraph() {
		if( ca == null )
			return 1;
		double maxSpeed = ca.getAbsoluteMaxSpeed();
		double average = 0;
		for( GLIndividual ind : this.getIndividuals() ) {
			average += ind.getControl().getControlled().getMaxSpeed() * maxSpeed;
		}
		average /= getIndividuals().size();
		double secondsPerStep = 0.4 / average;
		return secondsPerStep;
	}
	
	/**
	 * Returns the current step of the cellular automaton. The step counter is
	 * stopped if the cellular automaton is finished.
	 * @return the current step of the cellular automaton
	 */
	public double getCaStep() {
		return realStepCA;
	}

	/**
	 * Returns the estimated time needed to play the complete visualization,
	 * including cellular automaton and dynamic flow, if present.
	 * @return tge estimated visualization time.
	 */
	public double getEstimatedTime() {
		return estimatedTime;
	}
	
	/**
	 * Returns the current step of the graph. The step counter is stopped if the graph is finished.
	 * @return the current step of the graph
	 */
	public double getGraphStep() {
		return realStepGraph;
	}
	
	/**
	 * Checks wheather all parts of the simulation are finished, or not.
	 *  @return true if the simulation is finished, false otherwise
	 */
	public boolean isFinished() {
		return finished;
	}
	
	/**
	 * Checks wheather the replay of the cellular automaton simulation is finished, or not.
	 * @return true if the cellular automaton is finished, false otherwise
	 */
	public boolean isCaFinshed() {
		return caFinished;
	}

	/**
	 * Checks wheather the replay of the dynamic flow is finished, or not
	 * @return true if the flow has completely reached the sink, false otherwise
	 */
	public boolean isGraphFinished() {
		return graphFinished;
	}

	/**
 * Returns the time of the model in nanoseconds.
 * @return the time of the model in nanoseconds
 */
	public long getTime() {
		return time;
	}
	
	/**
	 * Sets the new time in the model and updates the gl datastructure.
	 * @param timeNanoSeconds
	 */
	public final void addTime( long timeNanoSeconds ) {
		time += timeNanoSeconds;
		timeSinceLastStepCA += timeNanoSeconds;
		timeSinceLastStepGraph += timeNanoSeconds;
		if( hasCA && !caFinished ) {
			if( timeSinceLastStepCA >= nanoSecondsPerStepCA ) {
				long elapsedSteps = (timeSinceLastStepCA / nanoSecondsPerStepCA);
				stepCA += elapsedSteps;
				for( int i = 1; i <= elapsedSteps; i++ ) {
					ca.nextTimeStep();
					if( visRecording.hasNext() ) {
						try {
							Vector<Action> actions = visRecording.nextActions();
							for( Action action : actions )
								action.execute( ca );
						} catch( InconsistentPlaybackStateException ex ) {
							ex.printStackTrace();
						}
					}
				}
				timeSinceLastStepCA = timeSinceLastStepCA % nanoSecondsPerStepCA; //elapsedTime -  step*nanoSecondsPerStep;
				for( GLCellControl cell : cells ) {
					cell.stepUpdate();
				}
			}
			realStepCA = stepCA + (double)timeSinceLastStepCA/nanoSecondsPerStepCA;
			if( ca.getState() == CellularAutomaton.State.finish ) {
				caFinished = true;
				finished = true && graphFinished;
			}
		}
		if( hasGraph && !graphFinished ) {
			long elapsedSteps = (timeSinceLastStepGraph / nanoSecondsPerStepGraph);
			stepGraph += elapsedSteps;
			timeSinceLastStepGraph = timeSinceLastStepGraph % nanoSecondsPerStepGraph;
			realStepGraph = ( (double) time / (double) nanoSecondsPerStepGraph );
			for( GLNodeControl node : nodes )
				node.stepUpdate( (int) stepGraph );
			for( GLEdgeControl edge : edges )
				edge.stepUpdate();
			if( stepGraph > graphStepCount ) {
				graphFinished = true;
				finished = caFinished && true;
			}
		}
	}

	/**
	 * Returns the (exact) time needed by one step of the cellular automaton.
	 * @return the time needed by one step of the cellular automaton
	 */
	public double getCaSecondsPerStep() {
		return secondsPerStepCA;
	}

	/**
	 * Returns the (exact) time needed by one step of the graph. Note that ghe
	 * graph has no implicit time model. The time for one step is calculated
	 * depending on the cellular automaton and/or the original z-model.
	 * @return the time needed by one step of the graph
	 */
	public double getGraphSecondsPerStep() {
		return secondsPerStepGraph;
	}

	/**
	 * Sets the maximal time used by the graph
	 * @param maxT
	 */
	public void setGraphMaxTime( int maxT ) {
		this.graphStepCount = Math.max( graphStepCount, maxT );
	}

	/**
	 * Returns the current factor of the visualization speed.
	 * @return the current factor of the visualization speed
	 */
	public double getSpeedFactor() {
		return speedFactor;
	}

	/**
	 * Sets a factor that is multiplicated with the visualization speed. Use
	 * <code>1.0</code> for normal (real-time) speed.
	 * @param speedFactor the speed factor
	 */
	public void setSpeedFactor( double speedFactor ) {
		this.speedFactor = speedFactor;
		secondsPerStepCA = ca.getSecondsPerStep();
		nanoSecondsPerStepCA = (long)(Math.round( secondsPerStepCA * 1000000000 ) / speedFactor);
		nanoSecondsPerStepGraph = (long)(Math.round( secondsPerStepGraph * 1000000000 ) / speedFactor);
	}
	
	/**
	 * Checks wheather a cellular automaton is present in the current visualization run.
	 * @return true if a cellular automaton is present, otherwise false
	 */
	public boolean hasCellularAutomaton() {
		return hasCA;
	}

	/**
	 * Checks wheather a graph and dynamic flow are present in the current visualization run.
	 * @return true if a graph and dynamic flow are present, otherwise false
	 */
	public boolean hasGraph() {
		return hasGraph;
	}
	
	public void activateMergedPotential() {
		GLCellControl.setActivePotential( GLCellControl.getMergedPotential() );
	}
	
	public void activatePotential( StaticPotential potential ) {
		GLCellControl.setActivePotential( potential );
	}
	
	/**
	 * Activates the visualization of all floors, if a cellular automaton is present.
	 */
	public void showAllFloors() {
		if( hasCA )
			caControl.showAllFloors();
		if( hasGraph )
			graphControl.showAllFloors();
		buildingControl.showAllFloors();
	}

	/**
	* Shows the first floor, ignoring the default evacuation floor.
	*/
	public void showFirstFloor() {
		showFloor( 0 );
	}

	/**
	 * Enables and disables drawing of the cellular automaton
	 * @param val indicates wheather the cellular automaton is shown or not
	 */
	public void showCellularAutomaton( boolean val ) {
		showCA = val;
	}

	/**
	 * Displays a floor with the specified id in the visualization.
	 * @param id the floor id
	 */
	public void showFloor( int id ) {
		if( hasCA )
			caControl.showOnlyFloor( id );
		if( hasGraph )
			graphControl.showOnlyFloor( id );
		buildingControl.showOnlyFloor( id );
	}

	/**
	 * Enables and disables drawing of the graph.
	 * @param val indicates wheather the graph is shown or not
	 */
	public void showGraph( boolean val ) {
		showGraph = val;
	}

	/**
	 * Enables drawing of the rectangles defining the area which a node occupies.
	 * @param selected decides wheather the node rectangles are visible, or not.
	 */
	public void showNodeRectangles( boolean selected ) {
		if( !hasGraph() )
			return;
		for( GLGraphFloorControl g : graphControl.childControls ) {
			for( GLNodeControl node : g ) {
				node.setRectangleVisible( selected );
				g.getView().update();
			}
		}
	}
	
	/**
	 * Sets the specified {@link IndividualInformationDisplay} as value that is
	 * displayed using different colors on the individual heads.
	 * @param idm the information.
	 */
	public void showIndividualInformation( IndividualInformationDisplay idm ) {
		if( !hasCA )
			return;
		for( GLIndividualControl control : getIndividualControls() )
			control.setHeadInformation( idm );
		update();
	}

	/**
	 * Sets a type of potential that is visualized if a cellular automaton is present.
	 * @param pdm the type of potential
	 */
	public void showPotential( CellInformationDisplay pdm ) {
		if( !hasCA )
			return;
		caControl.setPotentialDisplay( pdm );
		update();
	}


	/**
	 * Enables and disables drawing of the cellular automaton
	 * @param val indicates wheather the cellular automaton is shown or not
	 */
	public void showWalls( boolean val ) {
		showWalls = val;
	}

	/**
	 * This method draws the scene. That means it calls the {@link GLCA } and
	 * {@link GLGraph} objects and calls their drawing routines.
	 * @param drawable
	 */
	public final void draw( GLAutoDrawable drawable ) {
		if( showCA && hasCA )
			caView.draw( drawable );
		if( showGraph && hasGraph )
			graphView.draw( drawable );
		if( showWalls )
			buildingView.draw( drawable );
	}

	/** 
	 * {@inheritDoc}
	 * @see opengl.framework.abs.drawable#update()
	 */
	@Override
	public void update() {
		for( GLCellControl cell : cells )
			cell.getView().update();
	}

	/**
	 * Returns a {@code Map} that assigns floor names to their ids.
	 * @return a map that assigns floor names to their ids
	 */
	public Map<Integer, String> getFloorNames() {
		HashMap<Integer, String> floorNames = new HashMap<Integer, String>(  buildingResults.getFloors().size() );
		for( BuildingResults.Floor floor : buildingResults.getFloors() ) {
			floorNames.put( floor.id(), floor.name() );
		}
		return Collections.unmodifiableMap( floorNames );		
	}

	public final GLIndividual getIndividualControl( int number ) {
		return individuals.get( number - 1 );
	}

	public final List<GLIndividual> getIndividuals() {
		return caControl.getIndividuals();
	}

	public List<GLIndividualControl> getIndividualControls() {
		return caControl.getIndividualControls();
	}

	public PotentialManager getPotentialManager() {
		if( hasCA )
			return ca.getPotentialManager();
		else
			return null;
	}
	
	/**
	 * Returns the statistic object for the current cellularautomaton.
	 * @return the statistic object for the current cellularautomaton.
	 */
	public CAStatistic getCAStatistic() {
		if( hasCA )
			return caStatistic;
		else
			return null;
	}
}
