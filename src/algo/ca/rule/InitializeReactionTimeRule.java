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
 * InitializeReactionTimeRule.java
 * Created: Nov 20, 2009,2:55:37 PM
 */

package algo.ca.rule;

import ds.ca.Cell;
import ds.ca.Individual;


/**
 * Sets a reaction time for an individual. The reaction time is not depending
 * on the age.
 * @author Jan-Philipp Kappmeier
 */
public class InitializeReactionTimeRule extends AbstractInitialRule {

	@Override
	protected void onExecute( Cell cell ) {
		Individual i = cell.getIndividual();
		i.setReactionTime( (int) caController().parameterSet.getReactionTime());
	}
}
