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
package algo.ca.rule;

import ds.ca.evac.Cell;

/* Veränderungen zur normalen SaveIndividualsRule:
- Panik wird nicht auf 0 gesetzt
- kein neues Potential, wenn man eine SaveArea betritt!!!!!
*/
public class ICEM09SaveIndividualsRule extends AbstractSaveRule {
	// muss VOR der EvacuateIndividualsRule aufgerufen werden!
	public ICEM09SaveIndividualsRule() {
	}

	@Override
	protected void onExecute( ds.ca.evac.Cell cell ) {
		ds.ca.evac.Individual savedIndividual = cell.getIndividual();
		if( !(savedIndividual.isSafe()) ) {
			esp.eca.setIndividualSave( savedIndividual );
                        esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addSafeIndividualToStatistic( savedIndividual );
		}
                // else: nothing!
	}

	@Override
	public boolean executableOn( ds.ca.evac.Cell cell ) {
		// Regel NUR anwendbar, wenn auf der Zelle ein Individuum steht
		// und die Zelle eine Exit- Savecell oder  ist
		return (cell.getIndividual() != null) && ((cell instanceof ds.ca.evac.ExitCell) || (cell instanceof ds.ca.evac.SaveCell));
	}
}

