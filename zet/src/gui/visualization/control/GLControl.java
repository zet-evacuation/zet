/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
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
import gui.visualization.control.building.GLBuildingControl;
import gui.visualization.control.ca.GLCellularAutomatonControl;
import gui.visualization.control.ca.GLCAFloorControl;
import gui.visualization.control.ca.GLCellControl;
import gui.visualization.control.ca.GLIndividualControl;
import gui.visualization.control.ca.GLRoomControl;
import gui.visualization.control.graph.GLGraphControl;
import gui.visualization.control.graph.GLGraphFloorControl;
import gui.visualization.control.graph.GLNodeControl;
import gui.visualization.draw.ca.GLIndividual;
import io.visualization.BuildingResults;
import io.visualization.CAVisualizationResults;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.tu_berlin.math.coga.common.localization.Localization;
import opengl.helper.Frustum;
import statistic.ca.CAStatistic;
import batch.tasks.AlgorithmTask;
import javax.media.opengl.GL;
import opengl.framework.abs.DrawableControlable;

/**
 * A control class for visualization in ZET. It combines three types of
 * graphical objects: a graph, a cellular automaton and a building data
 * structure.
 * @author Jan-Philipp Kappmeier
 */
public class GLControl implements DrawableControlable {

	Frustum frustum;

	public void setFrustum( Frustum frustum ) {
		this.frustum = frustum;
		if( caControl != null )
			caControl.setFrustum( frustum );
		if( graphControl != null )
			graphControl.setFrustum( frustum );
	}

	public Frustum getFrustum() {
		return frustum;
	}

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
		CHOSEN_EXIT,
		/** The rest of the reaction time */
		REACTION_TIME
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
	private boolean hasCellularAutomaton;
	/** Indicates wheather the currently loaded visualizationresult contains a graph, or not. */
	private boolean hasGraph;
	/** Represents the static structure of the building, e.g. walls. */
	private BuildingResults buildingResults;
	/** Represents the statistic  */
	private CAStatistic caStatistic;
	private GLCellularAutomatonControl caControl;
	private GLGraphControl graphControl;
	private GLBuildingControl buildingControl;
	/** The estimated time used for the whole visualization in seconds. */
	private double estimatedTime = 0;
	private long time;
	private double speedFactor = 1;
	private CellularAutomaton ca;

	/**
	 * Initializes a new empty instance of the general control class for the
	 * visualization of an evacuation simulation. The instance does not contain
	 * any graph, building or cellular automaton data, its
	 * {@link #draw( GLAutoDrawable )} method doeas nothing.
	 */
	public GLControl() {
		showCA = false;
		hasCellularAutomaton = false;
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
			hasCellularAutomaton = true;
			ca = new CellularAutomaton( caVisResults.getRecording().getInitialConfig() );
			caControl = new GLCellularAutomatonControl( caVisResults, ca );
			estimatedTime = Math.max( estimatedTime, caVisResults.getRecording().length() * caControl.getSecondsPerStep() );
		} else
			hasCellularAutomaton = false;
		if( graphVisResult != null ) {
			hasGraph = true;
			graphControl = new GLGraphControl( graphVisResult );

			this.secondsPerStepGraph();
			
			// TODO OpenGL equal-flow-arrival, see
			estimatedTime = Math.max( estimatedTime, graphControl.getStepCount() * graphControl.getSecondsPerStep() );
		} else
			hasGraph = false;
		time = 0;
		buildingControl = new GLBuildingControl( buildingResults );
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
		if( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.floors" ) )
			showAllFloors();
		else
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

	// TODO set secondsPerStep in the graph control!
	/**
	 * Computes the average speed of all persons in the cellular automaton and
	 * sets calculates the seconds needed for one graph step depending on this
	 * speed.
	 * @return the seconds needed for one graph step
	 */
	private void secondsPerStepGraph() {
		// Set speed such that it arrives when the last individual is evacuated.
		if( hasCellularAutomaton && PropertyContainer.getInstance().getAsBoolean( "options.visualization.flow.equalArrival" ) )
			graphControl.setNanoSecondsPerStep( graphControl.getStepCount() == 0 ? 0 : (caControl.getNanoSecondsPerStep() * caControl.getStepCount()) / graphControl.getStepCount() );
		else {
			if( ca == null ) {
				graphControl.setSecondsPerStep( 1 );
				return;
			}
			double maxSpeed = ca.getAbsoluteMaxSpeed();
			double average = 0;
			for( GLIndividual ind : this.getIndividuals() )
				average += ind.getControl().getMaxSpeed() * maxSpeed;
			average /= getIndividuals().size();
			double secondsPerStep = 0.4 / average;
			graphControl.setSecondsPerStep( secondsPerStep );
		}
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
	 * Checks wheather all parts of the simulation are finished, or not.
	 *  @return true if the simulation is finished, false otherwise
	 */
	public boolean isFinished() {
		return graphControl != null && graphControl.isFinished() && caControl != null && caControl.isFinished();
	}

	/**
	 * Checks wheather the replay of the cellular automaton simulation is finished, or not.
	 * @return true if the cellular automaton is finished, false otherwise
	 */
	public boolean isCaFinshed() {
		return caControl.isFinished();
	}

	/**
	 * Checks wheather the replay of the dynamic flow is finished, or not
	 * @return true if the flow has completely reached the sink, false otherwise
	 */
	public final boolean isGraphFinished() {
		return graphControl.isFinished();
	}

	/**
	 * Returns the current step of the graph. The step counter is stopped if the
	 * graph is finished.
	 * @return the current step of the graph
	 */
	public final double getGraphStep() {
		return graphControl.getStep();
	}

	/**
	 * Returns the current step of the cellular automaton. The step counter is
	 * stopped if the cellular automaton is finished.
	 * @return the current step of the cellular automaton
	 */
	public final double getCaStep() {
		return caControl.getStep();
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
	@Override
	public final void addTime( long timeNanoSeconds ) {
		time += timeNanoSeconds;
		if( hasCellularAutomaton && !caControl.isFinished() )
			caControl.addTime( timeNanoSeconds );
		if( hasGraph && !graphControl.isFinished() )
			graphControl.addTime( timeNanoSeconds );
	}

	public void setTime( long timeNanoSeconds ) {
		if( hasCellularAutomaton )
			caControl.setTime( timeNanoSeconds );
		if( hasGraph )
			graphControl.setTime( timeNanoSeconds );
	}

	public void resetTime() {
		if( hasCellularAutomaton )
			caControl.resetTime();
		if( hasGraph )
			graphControl.resetTime();
	}

	/**
	 * Returns the (exact) time needed by one step of the cellular automaton.
	 * @return the time needed by one step of the cellular automaton
	 */
	public double getCaSecondsPerStep() {
		return caControl.getSecondsPerStep();
	}

	/**
	 * Returns the (exact) time needed by one step of the graph. Note that ghe
	 * graph has no implicit time model. The time for one step is calculated
	 * depending on the cellular automaton and/or the original z-model.
	 * @return the time needed by one step of the graph
	 */
	public double getGraphSecondsPerStep() {
		return graphControl.getSecondsPerStep();
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
		if( hasCellularAutomaton )
			caControl.setSpeedFactor( speedFactor );
		if( hasGraph )
			graphControl.setSpeedFactor( speedFactor );
	}

	/**
	 * Checks wheather a cellular automaton is present in the current visualization run.
	 * @return true if a cellular automaton is present, otherwise false
	 */
	public boolean hasCellularAutomaton() {
		return hasCellularAutomaton;
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
		if( hasCellularAutomaton )
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
		if( hasCellularAutomaton )
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
		for( GLGraphFloorControl g : graphControl.getChildControls() )
			for( GLNodeControl node : g ) {
				node.setRectangleVisible( selected );
				g.getView().update();
			}
	}

	/**
	 * Sets the specified {@link IndividualInformationDisplay} as value that is
	 * displayed using different colors on the individual heads.
	 * @param idm the information.
	 */
	public void showIndividualInformation( IndividualInformationDisplay idm ) {
		if( !hasCellularAutomaton )
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
		if( !hasCellularAutomaton )
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
	 * @param Drawable
	 */
	@Override
	public final void draw( GL gl ) {
		if( showCA && hasCellularAutomaton )
			caControl.getView().draw( gl );
		if( showGraph && hasGraph )
			graphControl.draw( gl );
		if( showWalls )
			buildingControl.getView().draw( gl );
	}

	/** 
	 * {@inheritDoc}
	 * @see opengl.framework.abs.Drawable#update()
	 */
	@Override
	public void update() {
		for( GLCAFloorControl floor : caControl.getChildControls() )
			for( GLRoomControl room : floor )
				for( GLCellControl cell : room )
					//cells.add( cell );	// TODO use addAll
					cell.getView().update();

		//		for( GLCellControl cell : cells )
//			cell.getView().update();
	}

	/**
	 * Returns a {@code Map} that assigns floor names to their ids.
	 * @return a map that assigns floor names to their ids
	 */
	public Map<Integer, String> getFloorNames() {
		HashMap<Integer, String> floorNames = new HashMap<Integer, String>( buildingResults.getFloors().size() );
		for( BuildingResults.Floor floor : buildingResults.getFloors() )
			floorNames.put( floor.id(), floor.name() );
		return Collections.unmodifiableMap( floorNames );
	}

	public final List<GLIndividual> getIndividuals() {
		return caControl.getIndividuals();
	}

	public List<GLIndividualControl> getIndividualControls() {
		return caControl.getIndividualControls();
	}

	public PotentialManager getPotentialManager() {
		return hasCellularAutomaton ? ca.getPotentialManager() : null;
	}

	/**
	 * Returns the statistic object for the current cellularautomaton.
	 * @return the statistic object for the current cellularautomaton.
	 */
	public CAStatistic getCAStatistic() {
		return hasCellularAutomaton ? caStatistic : null;
	}

}
