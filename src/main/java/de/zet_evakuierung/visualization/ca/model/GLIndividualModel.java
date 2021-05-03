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
package de.zet_evakuierung.visualization.ca.model;

import java.util.ArrayList;
import java.util.function.Function;

import de.zet_evakuierung.visualization.VisHistoryTriple;
import de.zet_evakuierung.visualization.ca.CellularAutomatonVisualizationProperties;
import de.zet_evakuierung.visualization.ca.draw.GLIndividual;
import gui.visualization.control.AbstractZETVisualizationControl;
import gui.visualization.control.StepUpdateListener;
import gui.visualization.util.Tuple;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;

/**
 * This class controls an {@link Individual} in the
 * {@link org.zetool.simulation.cellularautomaton.CellularAutomaton cellular automaton} and is used by the visualization
 * class {@link GLIndividual} which draws the individual on the screen.
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLIndividualModel extends AbstractZETVisualizationControl<GLIndividualModel, GLIndividual, CellularAutomatonVisualizationModel>
        implements StepUpdateListener {

    private final Individual controlled;
    private final Function<GLCellModel, Tuple> query;
    /**
     * Access to properties of the visualization run.
     */
    private final CellularAutomatonVisualizationProperties properties;

    /** The history data structure that stores information about the positions of the individual at given times */
    private final ArrayList<VisHistoryTriple<Double, GLCellModel, GLCellModel>> path;
    /** The last time at that the individual moves. */
    private double lastEnd;
    /** The current index on the history data structure that is reached during linear search. */
    private int index;
    /** The time when the current step of the individual began. */
    private double startTimeOfMove;
    /** The time the individual needs to perform the current step. */
    double timeForMove;
    /** The current time. */
    private double step;
    /** The position where the individual has started the current step. */
    private Tuple sourcePos;
    /** The direction the individual walks. */
    private Tuple moveVector;
    /** Indicates that the individual shall not be drawn on screen. */
    private boolean invisible;
    /** The value used for the calculation of the current color. */
    private double headInformationValue;
    /** The type of the information displayed on the head. */
    private DynamicCellularAutomatonInformation.HeadInformation headInformationType = DynamicCellularAutomatonInformation.HeadInformation.PANIC;
    /** The floor on which the individual is standing in the current moment of simulation. */
    private int onFloor = 0;
    EvacuationState es;

    /**
     * Creates a new individual control class for an {@link Individual}.
     *
     * @param individual the controlled individual
     * @param properties properties of the visualization
     * @param visualizationModel model of the dynamic visualization
     * @param query queries the 2 dimensional {@code x}-{@code y} position of a cell
     */
    public GLIndividualModel(Individual individual, CellularAutomatonVisualizationProperties properties,
            CellularAutomatonVisualizationModel visualizationModel,
            Function<GLCellModel, Tuple> query) {
        super(visualizationModel);
        controlled = individual;
        this.properties = properties;
        this.query = query;
        this.setView(new GLIndividual(this, properties));
        visualizationModel.setFrustum(visualizationModel.getFrustum());
        path = new ArrayList<>();
        moveVector = new Tuple(0, 0);
        sourcePos = new Tuple(0, 0);
        onFloor = visualizationModel.floorFor(individual);
    }

    /**
     * Returns the number of the floor on which the individual stands currently. If the individual is moving, the
     * returned value equals the floor number of the starting cell.
     *
     * @return the number of the floor on which the individual stands
     */
    public int onFloor() {
        return onFloor;
    }

    /**
     * Retrieves the current positioning information from the history position data structure. This method must be
     * called if a new step of the cellular automaton is reached.
     */
    @Override
    public void stepUpdate() {
        if (visualizationModel.getStep() < step) {
            index = 0;
        }
        step = visualizationModel.getStep();
        getView().update();
        if (path.size() <= 0) {
            return;
        }
        double stepEnd = -1;
        double stepStart = -1;
        GLCellModel source = null;
        GLCellModel destination = null;
        while (index < path.size() && this.path.get(index).getFirstValue() <= step) {
            stepStart = this.path.get(index).getFirstValue();
            source = this.path.get(index).getSecondValue();
            destination = this.path.get(index).getThirdValue();
            index++;
            stepEnd = index < path.size() ? path.get(index).getFirstValue() : lastEnd;
        }
        calcHeadInformation();
        if (stepStart == -1) {
            return;
        }
        startTimeOfMove = stepStart;
        calcPos(step, stepStart, stepEnd, source, destination);
        invisible = source.getFloorID() != destination.getFloorID(); // set invisible if the individual is changing floor (or teleporting)
        onFloor = source.getFloorID();	// update the floor
    }

    /**
     * Returns the current position of the individual in absolute coordinates.
     *
     * @return the current position of the individual in absolute coordinates.
     */
    public Tuple getCurrentPosition() {
        double cellSize = 200 * properties.getScaling();
        double completedPartOfMove = (step - startTimeOfMove) / timeForMove;
        completedPartOfMove = Math.min(completedPartOfMove, 1.0);
        completedPartOfMove = Math.max(completedPartOfMove, 0.0);
        Tuple currentPosition = new Tuple(0, 0);
        if (timeForMove <= 0.001) {
            currentPosition.x = sourcePos.x + cellSize + moveVector.x;
            currentPosition.y = sourcePos.y - cellSize + moveVector.y;
        } else {
            currentPosition.x = sourcePos.x + cellSize + completedPartOfMove * moveVector.x;
            currentPosition.y = sourcePos.y - cellSize + completedPartOfMove * moveVector.y;
        }
        return currentPosition;
    }

    /**
     * Returns the value used for information displayed on the head
     *
     * @return the value used for information displayed on the head
     */
    public double getHeadInformation() {
        return headInformationValue;
    }

    /**
     * Sets the information type displayed on the individuals head
     *
     * @param idm the information type
     */
    public void setHeadInformation(DynamicCellularAutomatonInformation.HeadInformation idm) {
        this.headInformationType = idm;
    }

    /**
     * Calculates the current value for the head information display depending on the {@link headInformationType}.
     */
    private void calcHeadInformation() {
        switch (headInformationType) {
            default:
            case NOTHING:
                headInformationValue = 0;
                break;
            case PANIC:
                headInformationValue = 0;//es.propertyFor(controlled).getPanic();
                break;
            case SPEED:
                headInformationValue = 0;
                break;
            case EXHAUSTION:
                headInformationValue = 0;//es.propertyFor(controlled).getExhaustion();
                break;
            case ALARMED:
                headInformationValue = 0;//es.propertyFor(controlled).isAlarmed() ? 0 : 1;
                break;
            case CHOSEN_EXIT:
                headInformationValue = 0;
                // TODO visualization statistic
                //final int potentials = mainControl.getPotentialManager().getStaticPotentials().size();
                //headInformationValue = (controlled.getStaticPotential().getID() % potentials) / (double) potentials;
                break;
            case REACTION_TIME:
                headInformationValue = 1 - (visualizationModel.getTimeInSeconds() / controlled.getReactionTime());
                break;
        }
    }

    /**
     * Checks whether the individual is evacuated
     *
     * @return true if the individual is evacuated
     */
    public boolean isEvacuated() {
        return /*controlled.isEvacuated() &&*/ step > lastEnd;
    }

    /**
     * Checks whether the individual is dead
     *
     * @return true if the individual is dead
     */
    public boolean isDead() {
        return false; //es.propertyFor(controlled).isDead();
    }

    /**
     * Returns the maximal speed of the controlled individual.
     *
     * @return the maximal speed of the controlled individual
     */
    public double getMaxSpeed() {
        return controlled.getMaxSpeed();
    }

    /**
     * Calculates the current position of an Individual standing on this cell or leaving this cell, based on the current
     * time t of the cellular automaton, which is defined in the parameter of this method. The calculated values can be
     * retrieved by calling the methods getCurrentIndividualXPosition() and getCurrentIndividualYPosition().
     *
     * @param time The current time of the cellular automaton.
     * @param start the time when the individual starts moving
     * @param end the time when the individual arrives at the destination
     * @param source the source cell on which the individual starts moving
     * @param destination the destination cell of the individual
     */
    public void calcPos(double time, double start, double end, GLCellModel source, GLCellModel destination) {
        sourcePos = query.apply(source);
        Tuple destinationPos = query.apply(destination);
        moveVector = new Tuple(destinationPos.x - sourcePos.x, destinationPos.y - sourcePos.y);
        timeForMove = end - start;
    }

    /**
     * Adds a new position triple to the history data structure used to calculate the position of the individual at a
     * given time.
     *
     * @param from the source cell on which the individual starts moving
     * @param to the destination cell of the individual
     * @param start the time when the individual starts moving
     * @param arrival the time when the individual arrives at the destination
     */
    public void addHistoryTriple(GLCellModel from, GLCellModel to, double start, double arrival) {
        if (Math.abs(start - lastEnd) > 0.001) {
            path.add(new VisHistoryTriple<>(lastEnd, from, from));
        }
        path.add(new VisHistoryTriple<>(start, from, to));
        if (arrival > lastEnd) {
            lastEnd = arrival;
        }
    }

    /**
     * Returns the number of the controlled individual.
     *
     * @return the number of the controlled individual
     */
    public int getNumber() {
        return controlled.getNumber();
    }

    /**
     * Checks wheather the individual is invisible.
     *
     * @return true if the individual is invisible
     */
    public boolean isInvisible() {
        return invisible;
    }
}
