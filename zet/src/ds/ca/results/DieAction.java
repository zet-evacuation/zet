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
/*
 * Created on 06.05.2008
 */
package ds.ca.results;

import ds.ca.evac.EvacCell;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.ca.evac.DeathCause;
import ds.ca.results.Action.CADoesNotMatchException;

/**
 * Represents the fact that an individual died.
 * @author Daniel Pluempe
 */
public class DieAction extends Action {

	/** The cell on which the individual stood when it died. */
	private EvacCell placeOfDeath;
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
	public DieAction( EvacCell placeOfDeath, DeathCause cause, int individualNumber ) {
		this.placeOfDeath = placeOfDeath;
		this.cause = cause;
		this.individualNumber = individualNumber;
	}

	/**
	 * Returns the cell on which the individual stood
	 * @return the cell on which the individual stood
	 */
	public EvacCell placeOfDeath() {
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
	 * @see ds.ca.results.Action#adoptToCA(ds.ca.EvacuationCellularAutomaton)
	 */
	@Override
	Action adoptToCA( EvacuationCellularAutomaton targetCA ) throws CADoesNotMatchException {
		EvacCell newCell = adoptCell( placeOfDeath, targetCA );
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
	 * @see ds.ca.results.Action#execute(ds.ca.EvacuationCellularAutomaton)
	 */
	@Override
	public void execute( EvacuationCellularAutomaton onCA ) throws InconsistentPlaybackStateException {
		if( placeOfDeath.getIndividual() == null ) {
			throw new InconsistentPlaybackStateException(
							"I could not mark the individual on cell " +
							"as dead because it was not there (someone was lucky there, hu?)" );
		}

		onCA.setIndividualDead( placeOfDeath.getIndividual(), cause );
	}

	/**
	 * {@inheritDoc}
	 * @see ds.ca.results.Action#toString()
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
