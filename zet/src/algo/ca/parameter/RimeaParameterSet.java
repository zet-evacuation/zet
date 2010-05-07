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
 * Class RimeaParameterSet
 * Erstellt 13.10.2008, 21:28:10
 */

package algo.ca.parameter;

import ds.ca.Cell;
import ds.ca.Individual;
import java.util.Collection;

/**
 * A {@link ParameterSet} that sets the parameter of the simulation to values
 * that allow passing the RIMEA tests.
 * @author Jan-Philipp Kappmeier
 */
public class RimeaParameterSet extends DefaultParameterSet {
	
	public RimeaParameterSet() {
		super();
	}

	/**
	 * Updates the exhaustion. Disabled for rimea parameter set.
	 * @param individual
	 * @param targetCell
	 * @return
	 */
	@Override
	public double updateExhaustion( Individual individual, Cell targetCell ) {
		individual.setExhaustion( 0 );
		return 0;
	}

	/**
	 * Updates the panic. Disabled for rimea parameter set.
	 * @param individual
	 * @param targetCell
	 * @param preferedCells
	 * @return
	 */
	@Override
	public double updatePanic( Individual individual, Cell targetCell, Collection<Cell> preferedCells ) {
		individual.setPanic( 0 );
		return 0;
	}

	/**
	 * {@inheritDoc }
	 * @param i
	 * @return
	 */
	@Override
	public double updatePreferredSpeed( Individual i ) {
		i.setCurrentSpeed( i.getMaxSpeed() );
		return i.getMaxSpeed();
	}
	
	/**
	 * Sets the reaction time depending from age. This is disabled in rimea
	 * profile, the reaction time is set by the default individual reaction time
	 * distribution instead.
	 * @param age
	 * @return
	 */
	@Override
	public double getReactionTimeFromAge( double age ) {
		throw new IllegalStateException( "ReactionTimeFromAge not allowed in Rimea Parameter set." );
	}


	/**
	 * Disable idling in RiMEA test suite.
	 * @param individual
	 * @return
	 */
//	@Override
//	public double idleThreshold(Individual individual) {
//		return 0;
//	}

	/**
	 * Compute the speed according to the recommendation of the rimea guidelines.
	 * The probability of an individual of beeing male or female is 50% each.
	 *
	 * @param pAge
	 * @return
	 */
//	@Override
//	public double getSpeedFromAge( double pAge ) {
//		return super.getSpeedFromAge( pAge );
//	}


}
