/*
 * Created on 06.05.2008
 */
package ds.ca.results;

import ds.ca.Cell;
import ds.ca.CellularAutomaton;
import ds.ca.Individual.DeathCause;
import ds.ca.results.Action.CADoesNotMatchException;

/**
 * Represents the fact that an individual died.
 * @author Daniel Pluempe
 */
public class DieAction extends Action {

	/** The cell on which the individual stood when it died. */
	private Cell placeOfDeath;
	/** The cause which caused the individuals dead */
	private DeathCause cause;
	/** The number of the individual. Is needed for visualization. */
	private int individualNumber;

	/**
	 * Creates a new instance of the DyingAction which represents the dead of
	 * an individual during the evacuation.
	 * @param placeOfDeath the cell on which the individual stands
	 * @param cause the cause of the death
	 * @param individualNumber the individuals number
	 */
	public DieAction( Cell placeOfDeath, DeathCause cause, int individualNumber ) {
		this.placeOfDeath = placeOfDeath;
		this.cause = cause;
		this.individualNumber = individualNumber;
	}

	/**
	 * Returns the cell on which the individual stood
	 * @return the cell on which the individual stood
	 */
	public Cell placeOfDeath() {
		return placeOfDeath;
	}

	/**
	 * Returns the number of the individual, can be used to access the individuals
	 * as the number is unique.
	 * @return the number of the individual
	 */
	public int getIndividualNumber() {
		return individualNumber;
	}

	/**
	 * {@inheritDoc}
	 * @see ds.za.results.Action#adoptToCA(ds.za.CellularAutomaton)
	 */
	@Override
	Action adoptToCA( CellularAutomaton targetCA ) throws CADoesNotMatchException {
		Cell newCell = adoptCell( placeOfDeath, targetCA );
		if( newCell == null ) {
			throw new CADoesNotMatchException(
							this,
							"Could not find the cell " + placeOfDeath + " that this action uses in the new CA." );
		}

		return new DieAction( newCell, cause, individualNumber );
	}

	/** 
	 * {@inheritDoc}
	 * @param onCA the cellular automaton on which the action is replayed
	 * @throws InconsistentPlaybackStateException if the individual that is to die is not on the cell
	 * @see ds.za.results.Action#execute(ds.za.CellularAutomaton)
	 */
	@Override
	public void execute( CellularAutomaton onCA )
					throws InconsistentPlaybackStateException {

		if( placeOfDeath.getIndividual() == null ) {
			throw new InconsistentPlaybackStateException(
							"I could not mark the individual on cell " +
							"as dead because it was not there (someone was lucky there, hu?)" );

		}

		onCA.setIndividualDead( placeOfDeath.getIndividual(), cause );
	}

	/**
	 * {@inheritDoc}
	 * @see ds.za.results.Action#toString()
	 */
	@Override
	public String toString() {
		String representation = "";

		representation += "An individual dies on cell ";
		representation += placeOfDeath;
		representation += " because of ";
		representation += cause;

		return representation;
	}
}
