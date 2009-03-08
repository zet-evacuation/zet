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
 * Created on 04.06.2008
 *
 */
package ds.ca.results;

import ds.ca.CellularAutomaton;
import ds.ca.Individual;

/**
 * @author Daniel Pluempe
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
						individual.getCurrentSpeed(),
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
	 * @see ds.za.results.Action#adoptToCA(ds.za.CellularAutomaton)
	 */
	@Override
	Action adoptToCA(  CellularAutomaton targetCA ) throws CADoesNotMatchException {
		Individual adaptedIndividual = targetCA.getIndividual( individual.getNumber() );
		if( adaptedIndividual == null )
			throw new CADoesNotMatchException( this, "Could not find the individual with the unique id " + individual.getNumber() );
		return new IndividualStateChangeAction( adaptedIndividual, panic, exhaustion, currentSpeed, isAlarmed );
	}

	/**
	 * {@inheritDoc }
	 * @param onCA 
	 * @throws InconsistentPlaybackStateException 
	 * @see ds.za.results.Action#execute(ds.za.CellularAutomaton)
	 */
	@Override
	public void execute( CellularAutomaton onCA ) throws InconsistentPlaybackStateException {
		individual.setPanic( panic );
		individual.setExhaustion( exhaustion );
		individual.setCurrentSpeed( currentSpeed );
		if( isAlarmed && !individual.isAlarmed() )
			individual.setAlarmed( true );
	}

	/**
	 * {@inheritDoc }
	 * @see ds.za.results.Action#toString()
	 */
	@Override
	public String toString() {
		return "The state of the individual " + individual.getNumber() + " changes to: " + " Panic: " + panic + ", Exhaustion: " + exhaustion + ", Speed: " + currentSpeed + ", Alarmed: " + (isAlarmed ? "yes" : "no");
	}
}
