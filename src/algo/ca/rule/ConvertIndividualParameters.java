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
 * Class ConvertIndividualParameters
 * Erstellt 13.10.2008, 23:17:03
 */

package algo.ca.rule;

import ds.ca.Cell;
import ds.ca.Individual;

/**
 * Converts some values of the individuals just before the execution of the
 * ca starts. It is supposed to execute this rule first and after all values
 * of the {@link CellularAutomaton} are initialized correctly.
 * @author Jan-Philipp Kappmeier
 */
public class ConvertIndividualParameters extends AbstractRule {

	/**
	 * Updates the reaction time which is should calculated in seconds which needs
	 * the correct stepcount per second.
	 * {@inheritDoc}
	 * @param cell
	 */
	@Override
	protected void onExecute( Cell cell ) {
		// Convert the reaction time. Is set in seconds and therefore must be
		// multiplied with the stepsPerSeconds value of the CA. During individual
		// creation this value is not available, thus we need to do it here.
		Individual i = cell.getIndividual();
		double sps = caController().getCA().getStepsPerSecond();
		i.setReactionTime( (int)(i.getReactionTime() * sps ) );
	}

}
