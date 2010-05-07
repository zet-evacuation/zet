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
 * ParameterSet.java
 * Created on 23.01.2008
 */
package algo.ca.parameter;

import java.util.Collection;

import ds.ca.Cell;
import ds.ca.Individual;

/**
 * An abstract class defining all methods that parameter sets have to support.
 * @author Daniel Plümpe, Jan-Philipp Kappmeier
 */
public interface ParameterSet {
	/* Updating of dynamic parameters */

	public double updateExhaustion( Individual individual, Cell targetCell );

	public double updatePreferredSpeed( Individual individual );

	public double updatePanic( Individual individual, Cell targetCell, Collection<Cell> preferedCells );

	/* Threshold values for various decisions */
	public double changePotentialThreshold( Individual individual );

	public double idleThreshold( Individual individual );

	public double movementThreshold( Individual individual );

	/* Other dynamic parameters */
	public double effectivePotential( Cell referenceCell, Cell targetCell );

	/* Some constants*/
	public double dynamicPotentialWeight();

	public double staticPotentialWeight();

	public double probabilityDynamicIncrease();

	public double probabilityDynamicDecrease();

	public double probabilityChangePotentialFamiliarityOrAttractivityOfExitRule();

	/* Conversion parameters */
	public double getAbsoluteMaxSpeed();

	public double getSpeedFromAge( double pAge );

	public double getSlacknessFromDecisiveness( double pDecisiveness );

	public double getExhaustionFromAge( double pAge );

	public double getReactionTimeFromAge( double pAge );

	public double getReactionTime();
}
