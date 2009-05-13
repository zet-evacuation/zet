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
/*
 * Created on 27.06.2008
 *
 */
package ds.ca.results;

import ds.ca.CellularAutomaton;

/**
 * @author Daniel Pluempe
 *
 */
public class CAStateChangedAction extends Action {

	/** The new state of the cellular automaton */
	CellularAutomaton.State newState;

	public CAStateChangedAction( CellularAutomaton.State newState ) {
		this.newState = newState;
	}

	/** {@inheritDoc}
	 * @see ds.ca.results.Action#adoptToCA(ds.ca.CellularAutomaton)
	 */
	@Override
	Action adoptToCA(  CellularAutomaton targetCA ) throws CADoesNotMatchException {
		return this;
	}

	/** {@inheritDoc}
	 * @param onCA the cellular on which the rule is executed
	 * @throws InconsistentPlaybackStateException if an error during replay occured
	 * @see ds.ca.results.Action#execute(ds.ca.CellularAutomaton)
	 */
	@Override
	public void execute( CellularAutomaton onCA ) throws InconsistentPlaybackStateException {
		onCA.setState( newState );
	}

	/** {@inheritDoc}
	 * @see ds.ca.results.Action#toString()
	 */
	@Override
	public String toString() {
		switch( this.newState ) {
			case finish:
				return "finished";
			case ready:
				return "ready";
			case running:
				return "running";
			default:
				return "unknown state";
		}
	}
}
