/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
package ds.ca.results;

import ds.ca.evac.EvacCell;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.ca.evac.ExitCell;
import ds.ca.results.Action.CADoesNotMatchException;

/**
 * Represents the fact that an individual leaves the simulation. Note that this
 * action starts and ends on the same cell. The performing individual is the
 * individual that occupies the exit cell.
 *
 * @author Daniel R. Schmidt
 */
public class ExitAction extends Action {

	/** The cell where an individual leaves the simulation */
	protected ExitCell exit;

	/**
	 * Creates a new Exit action.
	 * @param exit The cell from where the individual leaves
	 * the system.
	 */
	public ExitAction( ExitCell exit ) {
		this.exit = exit;
	}

	@Override
	public void execute( ds.ca.evac.EvacuationCellularAutomaton onCA ) throws InconsistentPlaybackStateException {
		if( exit.getIndividual() == null )
			throw new InconsistentPlaybackStateException( "Could not evacuate an individual from cell " + exit + "(" + exit.hashCode() + ") because there was none." );
		onCA.setIndividualEvacuated( exit.getIndividual() );
	}

	@Override
	public String toString() {
		String representation = "An individual leaves the simulation from cell " + exit;
		return representation;
	}

	@Override
	Action adoptToCA(  EvacuationCellularAutomaton targetCA ) throws CADoesNotMatchException {
		EvacCell newExit = adoptCell( exit, targetCA );
		if( newExit == null )
			throw new CADoesNotMatchException( this, "Could not find the exit " + exit + " that this action uses in the new CA." );
		return new ExitAction( (ExitCell) newExit );
	}
}

