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

import ds.ca.evac.EvacuationCellularAutomaton;
import ds.ca.evac.Individual;

/**
 * @author Daniel R. Schmidt
 */
public class IndividualStateChangeAction extends Action {

	private Individual individual;
	private double panic;
	private double exhaustion;
	private double currentSpeed;
	private boolean isAlarmed;

	public IndividualStateChangeAction( Individual individual ) {
		this( individual,
						individual.getPanic(),
						individual.getExhaustion(),
            individual.getRelativeSpeed(),
      						individual.isAlarmed() );
	}

	protected IndividualStateChangeAction( Individual individual, double panic, double exhaustion, double currentSpeed, boolean isAlarmed ) {
		this.individual = individual;
		this.panic = panic;
		this.exhaustion = exhaustion;
		this.currentSpeed = currentSpeed;
		this.isAlarmed = isAlarmed;
	}

	/**
	 * {@inheritDoc }
	 * @see ds.ca.results.Action#adoptToCA(ds.ca.EvacuationCellularAutomaton)
	 */
	@Override
	Action adoptToCA(  EvacuationCellularAutomaton targetCA ) throws CADoesNotMatchException {
		Individual adaptedIndividual = targetCA.getIndividual( individual.getNumber() );
		if( adaptedIndividual == null )
			throw new CADoesNotMatchException( this, "Could not find the individual with the unique id " + individual.getNumber() );
		return new IndividualStateChangeAction( adaptedIndividual, panic, exhaustion, currentSpeed, isAlarmed );
	}

	/**
	 * {@inheritDoc }
	 * @param onCA
	 * @throws InconsistentPlaybackStateException
	 * @see ds.ca.results.Action#execute(ds.ca.EvacuationCellularAutomaton)
	 */
	@Override
	public void execute( EvacuationCellularAutomaton onCA ) throws InconsistentPlaybackStateException {
		individual.setPanic( panic );
		individual.setExhaustion( exhaustion );
    individual.setRelativeSpeed( currentSpeed );
		if( isAlarmed && !individual.isAlarmed() )
			individual.setAlarmed( true );
	}

	/**
	 * {@inheritDoc }
	 * @see ds.ca.results.Action#toString()
	 */
	@Override
	public String toString() {
		return "The state of the individual " + individual.getNumber() + " changes to: " + " Panic: " + panic + ", Exhaustion: " + exhaustion + ", Speed: " + currentSpeed + ", Alarmed: " + (isAlarmed ? "yes" : "no");
	}
}
