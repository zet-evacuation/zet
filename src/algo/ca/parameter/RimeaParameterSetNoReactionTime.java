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

import ds.ca.evac.Individual;

/**
 * A {@link ParameterSet} that sets the parameter of the simulation to values
 * that allow passing the RIMEA tests.
 * @author Melanie Schmidt
 */
public class RimeaParameterSetNoReactionTime extends RimeaParameterSet {

	/**
	 *
	 * @param age
	 * @return 0.0
	 */
	@Override
	public double getReactionTimeFromAge( double age ) {
		return 0.0;
	}

	/**
	 *
	 * @param individual
	 * @return 0.0
	 */
	@Override
	public double idleThreshold(Individual individual){
		return 0.0;
	}

	@Override
	public double getSpeedFromAge( double pAge ) {
		return 0.595;
	}
}
