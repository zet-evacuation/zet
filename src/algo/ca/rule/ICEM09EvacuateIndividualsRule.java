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

import ds.ca.evac.Individual;

// dieselbe wie die normale EvacuateIndividualsRule!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
public class ICEM09EvacuateIndividualsRule extends AbstractEvacuationRule {

	public ICEM09EvacuateIndividualsRule() {
	}

	@Override
	protected void onExecute( ds.ca.evac.EvacCell cell ) {
		esp.eca.markIndividualForRemoval( cell.getIndividual() );
		// Potential needed for statistics:
		ds.ca.evac.StaticPotential exit = esp.potentialController.getNearestExitStaticPotential( cell );
		esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addExitToStatistic( cell.getIndividual(), exit );
		// safetyTime etc will be set in the SaveIndividualsRule
	}

	@Override
	public boolean executableOn( ds.ca.evac.EvacCell cell ) {
		// Regel NUR anwendbar, wenn auf der Zelle ein Individuum steht
		// und die Zelle eine Exitcell ist
		
		Individual i = cell.getIndividual();
		//return (i != null) && (cell instanceof ds.ca.ExitCell) && ( i.getStepEndTime() <= esp.eca.getTimeStep() );
		boolean testval = false;
		if( (i != null) && (cell instanceof ds.ca.evac.ExitCell)) {
			if( i.getStepEndTime() >= esp.eca.getTimeStep()+1)
				testval = false;
			else
				testval = true;
		}
		return (i != null) && (cell instanceof ds.ca.evac.ExitCell) && testval;
	}
}

