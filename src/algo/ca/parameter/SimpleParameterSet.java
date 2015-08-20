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

package algo.ca.parameter;

import ds.ca.evac.EvacCell;
import ds.ca.evac.Individual;
import ds.ca.evac.StaticPotential;
import java.util.Collection;

/**
 * @author Jan-Philipp Kappmeier
 */
public class SimpleParameterSet extends AbstractParameterSet {

	@Override
	public double changePotentialThreshold( Individual individual ) {
		return 0;
	}

	/**
	 *
	 * @param referenceCell
	 * @param targetCell
	 * @return the potential difference between the two cells
	 */
	@Override
	public double effectivePotential( EvacCell referenceCell, EvacCell targetCell ) {
		StaticPotential staticPotential = referenceCell.getIndividual().getStaticPotential();
		final double statPotlDiff = staticPotential.getPotential( referenceCell ) - staticPotential.getPotential( targetCell );
		return statPotlDiff;
	}

	@Override
	public double idleThreshold( Individual i ) {
		return i.getSlackness() * 0.4;
	}

	@Override
	public double movementThreshold( Individual i ) {
		double individualSpeed = i.getRelativeSpeed();
		double cellSpeed = i.getCell().getSpeedFactor();
		return individualSpeed * cellSpeed;
	}

	@Override
	public double updateExhaustion( Individual individual, EvacCell targetCell ) {
		return 0;
	}

	@Override
	public double updatePanic( Individual individual, EvacCell targetCell, Collection<EvacCell> preferedCells ) {
		return 0;
	}

	@Override
	public double updatePreferredSpeed( Individual individual ) {
		return 0;
	}

	public double getAbsoluteMaxSpeed() {
		return 1.8;
	}

	public double getSpeedFromAge( double pAge ) {
		return 1;
	}

	public double getSlacknessFromDecisiveness( double pDecisiveness ) {
		return (1-pDecisiveness)*0.25;
	}
	public double getExhaustionFromAge( double pAge){
		return 0.1;
	}

  /**
	 * Returns a reactin time of 5.
	 * @return 5
	 */
	public double getReactionTimeFromAge( double pAge){
		return 5;
	}

	@Override
	public double getReactionTime() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	/**
	 * Returns a reactin time of 5.
	 * @return 5
	 */
	//public double getReactionTime() {
	//	return 5;
	//}
}
