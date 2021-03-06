/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package gui.visualization.control;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import de.tu_berlin.math.coga.zet.ZETLocalization2;
import de.zet_evakuierung.visualization.network.control.GLFlowGraphControl;
import de.zet_evakuierung.visualization.network.control.GLGraphFloorControl;
import de.zet_evakuierung.visualization.network.control.GLNodeControl;
import de.zet_evakuierung.visualization.network.draw.GLGraph;
import ds.CompareVisualizationResults;
import ds.GraphVisualizationResults;
import ds.PropertyContainer;
import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.building.GLBuildingControl;
import gui.visualization.control.ca.GLCAFloorControl;
import gui.visualization.control.ca.GLCellControl;
import gui.visualization.control.ca.GLCellularAutomatonControl;
import gui.visualization.control.ca.GLIndividualControl;
import gui.visualization.control.ca.GLRoomControl;
import gui.visualization.draw.ca.GLCA;
import gui.visualization.draw.ca.GLIndividual;
import io.visualization.BuildingResults;
import io.visualization.BuildingResults.Floor;
import io.visualization.CellularAutomatonVisualizationResults;
import io.visualization.EvacuationSimulationResults;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.potential.Potential;
import org.zet.cellularautomaton.statistic.CAStatistic;
import org.zetool.common.localization.Localization;
import org.zetool.opengl.framework.abs.DrawableControlable;
import org.zetool.opengl.helper.Frustum;
import zet.gui.main.tabs.JVisualizationView;

/**
 * A control class for visualization in ZET. It combines three types of graphical objects: a graph, a cellular automaton
 * and a building data structure.
 *
 * @author Jan-Philipp Kappmeier
 */
public class ZETGLControl implements DrawableControlable {
    private static final Logger log = Logger.getGlobal();
    Frustum frustum;

    @Override
    public void setFrustum(Frustum frustum) {
        this.frustum = frustum;
        if (caControl != null) {
            caControl.setFrustum(frustum);
        }
        if (graphControl != null) {
            graphControl.setFrustum(frustum);
        }
    }

    @Override
    public Frustum getFrustum() {
        return frustum;
    }

    @Override
    public void delete() {
        buildingControl.delete();
        caControl.delete();
        graphControl.delete();
        compareControl.delete();
    }

    public Iterable<Potential> getPotentials() {
        return ca.getExits().stream().map(exit -> ca.getPotentialFor(exit)).collect(toList());
    }

    public void setEvacuationResults(EvacuationSimulationResults evacResults) {
        hasCellularAutomaton = true;

        // Also has simulation not only static
        ca = evacResults.getRecording().getInitialConfig().getCellularAutomaton();
        absoluteMaxSpeed = evacResults.getRecording().getInitialConfig().getAbsoluteMaxSpeed();
        
        caControl.setEvacuationSimulationResults(evacResults);

        estimatedTime = Math.max(estimatedTime, evacResults.getRecording().length() * caControl.getSecondsPerStep());
    }

    /**
     * Describes the different types of information which can be illustrated by different colors of the cells of the
     * cellular automaton.
     */
    public enum CellInformationDisplay {

        /** Disables displaying any potential on the floor of cells. */
        NoPotential(0),
        /** Enables displaying of static potential on the floor of cells. */
        StaticPotential(1),
        /** Enables displaying of the dynamic potential on the floor of cells. */
        DynamicPotential(2),
        /** Enables displaying usage statistic on the cells. */
        Utilization(3),
        /** Shows waiting times on cells. */
        Waiting(4);
        private int id;

        private CellInformationDisplay(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }
    }

    /**
     * Describes the different types of information which can be illustrated by different colors of the heads of the
     * individuals.
     */
//	public enum IndividualInformationDisplay {
//
//		/** Shows default individual */
//		NOTHING,
//		/** Shows panic at the head */
//		PANIC,
//		/** Shows speed at the head */
//		SPEED,
//		/** Shows exhaustion at the head */
//		EXHAUSTION,
//		/** Shows the alarm-status at the head */
//		ALARMED,
//		/** Shows the chosen exit at the head*/
//		CHOSEN_EXIT,
//		/** The rest of the reaction time */
//		REACTION_TIME
//	}
    /** The localization class. */
    private Localization loc = ZETLocalization2.loc;
    /** Indicates whether the graph is currently visible, or not. */
    private boolean showGraph;
    /** Indicates whether the cellular automaton is currently visible, or not. */
    private boolean showCA;
    /** Indicates whether the walls are drawn. */
    private boolean showWalls = true;
    /** Indicates whether the currently loaded visualization result contains a cellular automaton, or not. */
    private boolean hasCellularAutomaton;
    /** Indicates whether the currently loaded visualization result contains a graph, or not. */
    private boolean hasGraph;
    /** Indicates whether a comparison of the flowvalues for 2 different graphs is done. */
    private boolean iscompared;
    /** Represents the static structure of the building, e.g. walls. */
    private BuildingResults buildingResults;
    /** Represents the statistic. */
    private CAStatistic caStatistic;
    private GLCellularAutomatonControl caControl;
    private GLFlowGraphControl graphControl;
    private CompareControl compareControl;
    private GLBuildingControl buildingControl;
    /** The estimated time used for the whole visualization in seconds. */
    private double estimatedTime = 0;
    private long time;
    private double speedFactor = 1;
    private EvacuationCellularAutomaton ca;
    public final static double sizeMultiplicator = 0.01;

    /**
     * Initializes a new empty instance of the general control class for the visualization of an evacuation simulation.
     * The instance does not contain any graph, building or cellular automaton data, its {@link #draw( GLAutoDrawable )}
     * method does nothing.
     */
    public ZETGLControl() {
        showCA = false;
        hasCellularAutomaton = false;
        showGraph = false;
        hasGraph = false;
        showWalls = false;
        iscompared = false;
    }
double absoluteMaxSpeed = 1;
    /**
     * Initializes a new instance of the general control class for the visualization of an evacuation simulation.
     *
     * @param caVisResults the visual results for cellular automatons
     * @param graphVisResult the visual results for graph
     * @param buildingResults the visual information about the building
     * @param caStatistic the calculated statistic for cellular automaton
     * @param compvisres the visual information to compare 2 different networks
     */
    public ZETGLControl(CellularAutomatonVisualizationResults caVisResults, EvacuationSimulationResults evacResults,
            GraphVisualizationResults graphVisResult, BuildingResults buildingResults, CAStatistic caStatistic, CompareVisualizationResults compvisres) {
        Logger.getGlobal().info("Setting up the ZETGLControl with the wrong constructor.");
        this.caStatistic = caStatistic;
        this.buildingResults = buildingResults;

        GLCellControl.invalidateMergedPotential();
        if (caVisResults != null) {
            hasCellularAutomaton = true;
            if (evacResults != null) {
                // Also has simulation not only static
                ca = evacResults.getRecording().getInitialConfig().getCellularAutomaton();
                absoluteMaxSpeed = evacResults.getRecording().getInitialConfig().getAbsoluteMaxSpeed();
            }
            caControl = new GLCellularAutomatonControl(caVisResults);
            caControl.setScaling(sizeMultiplicator);
            caControl.setDefaultFloorHeight(VisualizationOptionManager.getFloorDistance());
            caControl.build();

            estimatedTime = Math.max(estimatedTime, evacResults.getRecording().length() * caControl.getSecondsPerStep());
        } else {
            hasCellularAutomaton = false;
        }
        if (graphVisResult != null) {
            hasGraph = true;
            graphControl = new GLFlowGraphControl(graphVisResult);
            graphControl.setScaling(sizeMultiplicator);
            graphControl.setDefaultFloorHeight(VisualizationOptionManager.getFloorDistance());
            graphControl.build();

            this.secondsPerStepGraph();

            // TODO OpenGL equal-flow-arrival, see
            estimatedTime = Math.max(estimatedTime, graphControl.getStepCount() * graphControl.getSecondsPerStep());
        } else {
            hasGraph = false;
        }
        if (compvisres != null) {
            iscompared = true;
            compareControl = new CompareControl(compvisres);
            compareControl.build(compvisres);
        }
        time = 0;
        buildingControl = new GLBuildingControl(buildingResults);
        buildingControl.setScaling(sizeMultiplicator);
        buildingControl.build();
        //AlgorithmTask.getInstance().setProgress( 100, loc.getStringWithoutPrefix( "batch.tasks.progress.visualizationDatastructureComplete" ), "" );
        initSettings();
    }

    /**
     * Resets the building control for this graphics control class.
     *
     * @param buildingResults
     */
    public void setBuildingControl(BuildingResults buildingResults) {
        if (buildingControl != null) {
            buildingControl.delete();
        }
        System.gc();
        buildingControl = new GLBuildingControl(buildingResults);
        buildingControl.setScaling(sizeMultiplicator);
        buildingControl.build();
        showWalls(PropertyContainer.getGlobal().getAsBoolean("settings.gui.visualization.walls"));
    }

    /**
     * Resets the cellular automaton control for this graphics control class.
     *
     * @param caVis
     * @param ca
     */
    public void setCellularAutomatonControl(CellularAutomatonVisualizationResults caVis) {
        if (caControl != null) {
            caControl.delete();
        }
        hasCellularAutomaton = true;
        this.ca = caVis.getCa();
        caControl = new GLCellularAutomatonControl(caVis);
        log.warning("Setting caControl to " + caControl);
        caControl.setScaling(sizeMultiplicator);
        caControl.setDefaultFloorHeight(VisualizationOptionManager.getFloorDistance());
        caControl.build();
        estimatedTime = 0;
        showCellularAutomaton(PropertyContainer.getGlobal().getAsBoolean("settings.gui.visualization.cellularAutomaton"));
        if (visibleFloor == -1) {
            showAllFloors();
        } else {
            showFloor(visibleFloor);
        }
        resetTime();
    }

    /**
     * Sets a new set of visualization results for a graph (network) and maybe a flow. All current visualization steps
     * are resetted.
     *
     * @param graphVisResult the datastructure containing visual information.
     */
    public void setGraphControl(GraphVisualizationResults graphVisResult) {
        if (graphControl != null) {
            graphControl.delete();
        }
        if (graphVisResult != null) {
            hasGraph = true;
            graphControl = new GLFlowGraphControl(graphVisResult);
            graphControl.setScaling(sizeMultiplicator);
            graphControl.setDefaultFloorHeight(VisualizationOptionManager.getFloorDistance());
            graphControl.build();

            this.secondsPerStepGraph();

            // TODO OpenGL equal-flow-arrival, see
            estimatedTime = Math.max(estimatedTime, graphControl.getStepCount() * graphControl.getSecondsPerStep());
            showGraph(PropertyContainer.getGlobal().getAsBoolean("settings.gui.visualization.graph"));
            showNodeRectangles(PropertyContainer.getGlobal().getAsBoolean("settings.gui.visualization.nodeArea"));
        } else {
            hasGraph = false;
        }
        resetTime();
    }

    public void setCompControl(CompareVisualizationResults compVisresult) {
        compareControl = new CompareControl(compVisresult);
        compareControl.build(compVisresult);
        /*for (CompareControl comp: compareControl.getChildControls())
            {
                comp.getView().update();
            }*/

    }

    int visibleFloor = -1;

    /**
     * Initializes the visualization settings with the values stored in the {@link PropertyContainer}.
     */
    private void initSettings() {
        showWalls(PropertyContainer.getGlobal().getAsBoolean("settings.gui.visualization.walls"));
        showGraph(PropertyContainer.getGlobal().getAsBoolean("settings.gui.visualization.graph"));
        showNodeRectangles(PropertyContainer.getGlobal().getAsBoolean("settings.gui.visualization.nodeArea"));
        showCellularAutomaton(PropertyContainer.getGlobal().getAsBoolean("settings.gui.visualization.cellularAutomaton"));
        if (PropertyContainer.getGlobal().getAsBoolean("settings.gui.visualization.floors")) {
            showAllFloors();
        } else {
            showFirstFloor();
        }

        switch (PropertyContainer.getGlobal().getAsInt("settings.gui.visualization.floorInformation")) {
            case 1:
                showPotential(CellInformationDisplay.StaticPotential);
                break;
            case 2:
                showPotential(CellInformationDisplay.DynamicPotential);
                break;
            case 3:
                showPotential(CellInformationDisplay.Utilization);
                break;
            case 4:
                showPotential(CellInformationDisplay.Waiting);
                break;
            case 0:
            default:
                showPotential(CellInformationDisplay.NoPotential);
                break;
        }
    }

    // TODO set secondsPerStep in the graph control!
    /**
     * Computes the average speed of all persons in the cellular automaton and sets calculates the seconds needed for
     * one graph step depending on this speed.
     *
     * @return the seconds needed for one graph step
     */
    private void secondsPerStepGraph() {
        // Set speed such that it arrives when the last individual is evacuated.
        if (hasCellularAutomaton && PropertyContainer.getGlobal().getAsBoolean("options.visualization.flow.equalArrival")) {
            graphControl.setNanoSecondsPerStep(graphControl.getStepCount() == 0 ? 0 : (caControl.getNanoSecondsPerStep() * caControl.getStepCount()) / graphControl.getStepCount());
        } else {
            if (ca == null) {
                graphControl.setSecondsPerStep(0.26425707443);
                return;
            }
            double maxSpeed = absoluteMaxSpeed;
            double average = 0;
            for (GLIndividual ind : this.getIndividuals()) {
                average += ind.getControl().getMaxSpeed() * maxSpeed;
            }
            average /= getIndividuals().size();
            double secondsPerStep = 0.4 / average;
            graphControl.setSecondsPerStep(secondsPerStep);
        }
    }

    /**
     * Returns the estimated time needed to play the complete visualization, including cellular automaton and dynamic
     * flow, if present.
     *
     * @return the estimated visualization time.
     */
    public double getEstimatedTime() {
        return estimatedTime;
    }

    /**
     * Checks whether all parts of the simulation are finished, or not.
     *
     * @return true if the simulation is finished, false otherwise
     */
    @Override
    public boolean isFinished() {
        return (graphControl == null || graphControl.isFinished()) && (caControl == null || caControl.isFinished());
    }

    /**
     * Checks whether the replay of the cellular automaton simulation is finished, or not.
     *
     * @return true if the cellular automaton is finished, false otherwise
     */
    public boolean isCaFinshed() {
        return caControl.isFinished();
    }

    /**
     * Checks whether the replay of the dynamic flow is finished, or not
     *
     * @return true if the flow has completely reached the sink, false otherwise
     */
    public final boolean isGraphFinished() {
        return graphControl.isFinished();
    }

    /**
     * Returns the current step of the graph. The step counter is stopped if the graph is finished.
     *
     * @return the current step of the graph
     */
    public final double getGraphStep() {
        return graphControl.getStep();
    }

    /**
     * Returns the current step of the cellular automaton. The step counter is stopped if the cellular automaton is
     * finished.
     *
     * @return the current step of the cellular automaton
     */
    public final double getCaStep() {
        return caControl.getStep();
    }

    /**
     * Returns the time of the model in nanoseconds.
     *
     * @return the time of the model in nanoseconds
     */
    public long getTime() {
        return time;
    }

    /**
     * Sets the new time in the model and updates the gl data structure.
     *
     * @param timeNanoSeconds
     */
    @Override
    public final void addTime(long timeNanoSeconds) {
        time += timeNanoSeconds;
        if (hasCellularAutomaton && !caControl.isFinished()) {
            caControl.addTime(timeNanoSeconds);
        }
        if (hasGraph && !graphControl.isFinished()) {
            graphControl.addTime(timeNanoSeconds);
        }
    }

    @Override
    public void setTime(long timeNanoSeconds) {
        if (hasCellularAutomaton) {
            caControl.setTime(timeNanoSeconds);
        }
        if (hasGraph) {
            graphControl.setTime(timeNanoSeconds);
        }
    }

    @Override
    public void resetTime() {
        if (hasCellularAutomaton) {
            caControl.resetTime();
        }
        if (hasGraph) {
            graphControl.resetTime();
        }
    }

    /**
     * Returns the (exact) time needed by one step of the cellular automaton.
     *
     * @return the time needed by one step of the cellular automaton
     */
    public double getCaSecondsPerStep() {
        return caControl.getSecondsPerStep();
    }

    /**
     * Returns the (exact) time needed by one step of the graph. Note that ghe graph has no implicit time model. The
     * time for one step is calculated depending on the cellular automaton and/or the original z-model.
     *
     * @return the time needed by one step of the graph
     */
    public double getGraphSecondsPerStep() {
        return graphControl.getSecondsPerStep();
    }

    /**
     * Returns the current factor of the visualization speed.
     *
     * @return the current factor of the visualization speed
     */
    public double getSpeedFactor() {
        return speedFactor;
    }

    /**
     * Sets a factor that is multiplicated with the visualization speed. Use {@code 1.0} for normal (real-time) speed.
     *
     * @param speedFactor the speed factor
     */
    public void setSpeedFactor(double speedFactor) {
        this.speedFactor = speedFactor;
        if (hasCellularAutomaton) {
            caControl.setSpeedFactor(speedFactor);
        }
        if (hasGraph) {
            graphControl.setSpeedFactor(speedFactor);
        }
    }

    /**
     * Checks whether a cellular automaton is present in the current visualization run.
     *
     * @return true if a cellular automaton is present, otherwise false
     */
    public boolean hasCellularAutomaton() {
        return hasCellularAutomaton;
    }

    /**
     * Checks whether a graph and dynamic flow are present in the current visualization run.
     *
     * @return true if a graph and dynamic flow are present, otherwise false
     */
    public boolean hasGraph() {
        return hasGraph;
    }

    public void activateMergedPotential() {
        GLCellControl.setActivePotential(GLCellControl.getMergedPotential());
    }

    public void activatePotential(Potential potential) {
        GLCellControl.setActivePotential(potential);
    }

    /**
     * Activates the visualization of all floors, if a cellular automaton is present.
     */
    public void showAllFloors() {
        visibleFloor = -1;
        if (hasCellularAutomaton) {
            caControl.showAllFloors();
        }
        if (hasGraph) {
            graphControl.showAllFloors();
        }
        if (buildingControl != null) {
            buildingControl.showAllFloors();
        }
    }

    /**
     * Shows the first floor, ignoring the default evacuation floor.
     */
    public void showFirstFloor() {
        showFloor(1);
    }

    /**
     * Enables and disables drawing of the cellular automaton
     *
     * @param val indicates whether the cellular automaton is shown or not
     */
    public void showCellularAutomaton(boolean val) {
        showCA = val;
    }

    /**
     * Displays a floor with the specified id in the visualization.
     *
     * @param id the floor id
     */
    public void showFloor(int id) {
        visibleFloor = id;
        if (hasCellularAutomaton) {
            caControl.showOnlyFloor(id);
        }
        if (hasGraph) {
            graphControl.showOnlyFloor(id);
        }
        if (buildingControl != null) {
            buildingControl.showOnlyFloor(id);
        }
    }

    /**
     * Enables and disables drawing of the graph.
     *
     * @param val indicates whether the graph is shown or not
     */
    public void showGraph(boolean val) {
        showGraph = val;
    }

    /**
     * Enables drawing of the rectangles defining the area which a node occupies.
     *
     * @param selected decides whether the node rectangles are visible, or not.
     */
    public void showNodeRectangles(boolean selected) {
        if (!hasGraph()) {
            return;
        }
        for (GLGraphFloorControl g : graphControl.getChildControls()) {
            for (GLNodeControl node : g) {
                node.setRectangleVisible(selected);
                g.getView().update();
            }
        }
    }

    /**
     * Sets the specified {@link IndividualInformationDisplay} as value that is displayed using different colors on the
     * individual heads.
     *
     * @param idm the information.
     */
    public void showIndividualInformation(JVisualizationView.HeadInformation idm) {
        if (!hasCellularAutomaton) {
            return;
        }
        for (GLIndividualControl control : getIndividualControls()) {
            control.setHeadInformation(idm);
        }
        update();
    }

    /**
     * Sets a type of potential that is visualized if a cellular automaton is present.
     *
     * @param pdm the type of potential
     */
    public void showPotential(CellInformationDisplay pdm) {
        if (!hasCellularAutomaton) {
            return;
        }
        caControl.setPotentialDisplay(pdm);
        update();
    }

    /**
     * Enables and disables drawing of the cellular automaton
     *
     * @param val indicates whether the cellular automaton is shown or not
     */
    public void showWalls(boolean val) {
        showWalls = val;
    }

    /**
     * This method draws the scene. That means it calls the {@link GLCA } and {@link GLGraph} objects and calls their
     * drawing routines.
     *
     * @param gl the graphics context on which the object is drawn
     */
    @Override
    public final void draw(GL2 gl) {
        if (showCA && hasCellularAutomaton) {
            caControl.getView().draw(gl);
        }
        if (showGraph && hasGraph) {
            graphControl.draw(gl);
        }
        if (showWalls && buildingControl != null) {
            buildingControl.getView().draw(gl);
        }
        if (iscompared) {
            compareControl.getView().draw(gl);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see opengl.framework.abs.Drawable#update()
     */
    @Override
    public void update() {
        for (GLCAFloorControl floor : caControl.getChildControls()) {
            for (GLRoomControl room : floor) {
                for (GLCellControl cell : room) {
                    cell.getView().update();
                }
            }
        }
        //		for( GLCellControl cell : cells )
//			cell.getView().update();
    }

    /**
     * Returns a {@code Map} that assigns floor names to their ids. Floor ids are enumerated from 0 to n-1. if there are
     * n floors. Note that the (possibly) hidden evacuation floor is also includeded in the n floors.
     *
     * @return a map that assigns floor names to their ids
     */
    public Collection<Floor> getFloorNames() {
        //List<String> floorNames = new LinkedList<>( );
        //for( BuildingResults.Floor floor : buildingResults.getFloors() )
        //	floorNames.add( floor.getName() );
        return buildingControl.getFloors();
        //return buildingResults.getFloors();
        //return Collections.unmodifiableCollection( buildingResults.getFloors() );
        //return Collections.unmodifiableCollection( floors.values() );
    }

    public final List<GLIndividual> getIndividuals() {
        return caControl.getIndividuals();
    }

    public List<GLIndividualControl> getIndividualControls() {
        return caControl.getIndividualControls();
    }

    /**
     * Returns the statistic object for the current cellular automaton.
     *
     * @return the statistic object for the current cellular automaton.
     */
    public CAStatistic getCAStatistic() {
        return hasCellularAutomaton ? caStatistic : null;
    }

}
