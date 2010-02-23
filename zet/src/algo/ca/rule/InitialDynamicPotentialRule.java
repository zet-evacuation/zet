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
 * Class InitialDynamicPotentialRule
 * Erstellt 28.04.2008, 21:45:37
 */

package algo.ca.rule;

import ds.ca.Cell;

/**
 * An initialization rule that assings the dynamic potential to individuals.
 * @author Jan-Philipp Kappmeier
 */
public class InitialDynamicPotentialRule extends AbstractInitialRule {

	/**
	 * Checks if an {@link ds.ca.Individual} is assigned to the cell.
	 * @param cell the cell
	 * @return true if an individual is on the cell.
	 */
	@Override
	public boolean executableOn( Cell cell ) {
		if(cell.getIndividual() == null)
			return false;    		
		else
			return true;
	}

	/**
	 * Assigns the dynamic potential to the cell's individual. It is found in the
	 * {@link PotentialController}.
	 * @param cell
	 */
	@Override
	protected void onExecute( Cell cell ) {
		cell.getIndividual().setDynamicPotential(this.caController().getPotentialController().getPm().getDynamicPotential());
	}
	
}
