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
package algo.ca.rule;

import ds.ca.Cell;

/* Veränderungen zur normalen SaveIndividualsRule:
- Panik wird nicht auf 0 gesetzt
- kein neues Potential, wenn man eine SaveArea betritt!!!!!
*/
public class PaperSaveIndividualsRule extends AbstractSaveRule {
	// muss VOR der EvacuateIndividualsRule aufgerufen werden!
	public PaperSaveIndividualsRule() {
	}

	@Override
	protected void onExecute( ds.ca.Cell cell ) {
		ds.ca.Individual savedIndividual = cell.getIndividual();
		if( !(savedIndividual.isSafe()) ) {
			caController().getCA().setIndividualSave( savedIndividual );
                        caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addSafeIndividualToStatistic( savedIndividual );
		}
                // else: nothing!
	}

	@Override
	public boolean executableOn( ds.ca.Cell cell ) {
		// Regel NUR anwendbar, wenn auf der Zelle ein Individuum steht
		// und die Zelle eine Exit- Savecell oder  ist
		return (cell.getIndividual() != null) && ((cell instanceof ds.ca.ExitCell) || (cell instanceof ds.ca.SaveCell));
	}
}

