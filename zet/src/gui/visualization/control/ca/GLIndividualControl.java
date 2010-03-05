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
/**
 * Class GLIndividualControl
 * Erstellt 10.06.2008, 22:22:41
 */
package gui.visualization.control.ca;

import ds.ca.Individual;
import gui.visualization.control.AbstractZETVisualizationControl;
import gui.visualization.control.GLControl;
import gui.visualization.control.GLControl.IndividualInformationDisplay;
import gui.visualization.control.StepUpdateListener;
import gui.visualization.control.VisHistoryTriple;
import gui.visualization.draw.ca.GLIndividual;
import gui.visualization.util.Tuple;
import java.util.ArrayList;

/**
 * This class controls an {@link Individual} in the {@link ds.ca.CellularAutomaton}
 * and is used by the visualization class {@link GLIndividual} which draws
 * the individual on the screen.
 * @author Jan-Philipp Kappmeier
 */
//public class GLIndividualControl extends AbstractControl<GLIndividual, Individual, CAVisualizationResults, GLIndividual, GLIndividualControl, GLControl> implements StepUpdateListener {
public class GLIndividualControl extends AbstractZETVisualizationControl<GLIndividualControl, GLIndividual> implements StepUpdateListener {

	/** The history data structure that stores information about the positions of the individual at given times */
	private ArrayList<VisHistoryTriple<Double, GLCellControl, GLCellControl>> path;
	/** The last time at that the individual moves. */
	private double lastEnd;
	/** The current index on the history data structure that is reached during linear search. */
	private int index;
	/** The time when the current step of the individual began */
	private double startTimeOfMove;
	/** The time the individual needs to perform the current step */
	double timeForMove;
	/** The current time */
	private double time;
	/** The position where the individual has started the current step */
	private Tuple sourcePos;
	/** The direction the individual walks */
	private Tuple moveVector;
	/** Indicates that the individual shall not be drawn on screen */
	private boolean invisible;
	/** The value used for the calculation of the current color */
	private double headInformationValue;
	/** The type of the information displayed on the head */
	private IndividualInformationDisplay headInformationType = IndividualInformationDisplay.PANIC;
	private Individual controlled;

	/**
	 * Creates a new individual control class for an {@link Individual}.
	 * @param caVisResults the visualization results for a simulation
	 * @param individual the controlled individual
	 * @param glControl the general control class
	 */
	public GLIndividualControl( Individual individual, GLControl glControl ) {
		super( glControl );
		this.setView( new GLIndividual( this ) );
		this.controlled = individual;
		path = new ArrayList<VisHistoryTriple<Double, GLCellControl, GLCellControl>>();
		moveVector = new Tuple( 0, 0 );
		sourcePos = new Tuple( 0, 0 );
	}

	/**
	 * Returns the number of the floor on which the individual stands
	 * @return  the number of the floor on which the individual stands
	 */
	public int onFloor() {
		return controlled.getCell().getRoom().getFloorID();
	}

	/**
	 * Retrieves the current positioning information from the history position
	 * data structure. This method must be called if a new step of the cellular
	 * automaton is reached.
	 */
	public void stepUpdate() {
		time = mainControl.getCaStep();
		getView().update();
		if( path.size() <= 0 )
			return;
		double stepEnd = -1;
		double stepStart = -1;
		GLCellControl source = null;
		GLCellControl destination = null;
		while(index < path.size() && this.path.get( index ).getFirstValue() <= time) {
			stepStart = this.path.get( index ).getFirstValue();
			source = this.path.get( index ).getSecondValue();
			destination = this.path.get( index ).getThirdValue();
			index++;
			if( index < path.size() )
				stepEnd = path.get( index ).getFirstValue();
			else
				stepEnd = lastEnd;
		}
		if( stepStart == -1 )
			return;
		startTimeOfMove = stepStart;
		calcPos( time, stepStart, stepEnd, source, destination );
		invisible = source.getFloorID() != destination.getFloorID();
		calcHeadInformation();
	}

	/**
	 * Returns the current position of the individual in absolute coordinates.
	 * @return the current position of the individual in absolute coordinates.
	 */
	public Tuple getCurrentPosition() {
		double cellSize = 20;
		double completedPartOfMove = (time - startTimeOfMove) / timeForMove;
		completedPartOfMove = Math.min( completedPartOfMove, 1.0 );
		completedPartOfMove = Math.max( completedPartOfMove, 0.0 );
		Tuple currentPosition = new Tuple( 0, 0 );
		if( timeForMove <= 0.001 ) {
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
	 * @return the value used for information displayed on the head
	 */
	public double getHeadInformation() {
		return headInformationValue;
	}

	/**
	 * Sets the information type displayed on the individuals head
	 * @param idm the information type
	 */
	public void setHeadInformation( GLControl.IndividualInformationDisplay idm ) {
		this.headInformationType = idm;
	}

	/**
	 * Calculates the current value for the head information display depending on 
	 * the {@link headInformationType}.
	 */
	private void calcHeadInformation() {
		switch( headInformationType ) {
			default:
			case NOTHING:
				headInformationValue = 0;
				break;
			case PANIC:
				headInformationValue = controlled.getPanic();
				break;
			case SPEED:
				headInformationValue = 0;
				break;
			case EXHAUSTION:
				headInformationValue = controlled.getExhaustion();
				break;
			case ALARMED:
				headInformationValue = controlled.isAlarmed() ? 0 : 1;
				break;
			case CHOSEN_EXIT:
				final int potentials = mainControl.getPotentialManager().getStaticPotentials().size();
				headInformationValue = (controlled.getStaticPotential().getID() % potentials) / (double) potentials;
				break;
		}
	}

	/**
	 * Checks wheather the individual is evacuated
	 * @return true if the individual is evacuated
	 */
	public boolean isEvacuated() {
		return controlled.isEvacuated() && time > lastEnd;
	}

	/**
	 * Checks wheather the individual is dead
	 * @return true if the individual is dead
	 */
	public boolean isDead() {
		return controlled.isDead();
	}

	/**
	 * Returns the maximal speed of the controlled individual.
	 * @return the maximal speed of the controlled individual
	 */
	public double getMaxSpeed() {
		return controlled.getMaxSpeed();
	}

	/**
	 * Calculates the current position of an Individual standing on this cell or leaving this cell,
	 * based on the current time t of the cellular automaton, which is defined in the parameter of this method.
	 * The calculated values can be retrieved by calling the methods 
	 * getCurrentIndividualXPosition() and getCurrentIndividualYPosition().
	 * @param time The current time of the cellular automaton.
	 * @param start the time when the individual starts moving
	 * @param end the time when the individual arrives at the destination
	 * @param source the source cell on which the individual starts moving
	 * @param destination the destination cell of the individual
	 */
	public void calcPos( double time, double start, double end, GLCellControl source, GLCellControl destination ) {
		sourcePos = source.getAbsolutePosition();
		Tuple destinationPos = destination.getAbsolutePosition();
		moveVector = new Tuple( destinationPos.x - sourcePos.x, destinationPos.y - sourcePos.y );
		timeForMove = end - start;
	}

	/**
	 * Adds a new position triple to the history data structure used
	 * to calculate the position of the individual at a given time.
	 * @param from the source cell on which the individual starts moving
	 * @param to the destination cell of the individual
	 * @param start the time when the individual starts moving
	 * @param arrival the time when the individual arrives at the destination
	 */
	public void addHistoryTriple( GLCellControl from, GLCellControl to, double start, double arrival ) {
		path.add( new VisHistoryTriple<Double, GLCellControl, GLCellControl>( start, from, to ) );
		lastEnd = arrival;
	}

	/**
	 * Returns the number of the controlled individual.
	 * @return the number of the controlled individual
	 */
	public int getNumber() {
		return this.controlled.getNumber();
	}

	/**
	 * Checks wheather the individual is invisible.
	 * @return true if the individual is invisible
	 */
	public boolean isInvisible() {
		return invisible;
	}
}
