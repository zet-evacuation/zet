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

import ds.ca.Individual;

/**
 * A rule that evacuates the individuals.
 * @author Jan-Philipp Kappmeier
 */
public class EvacuateIndividualsRule extends AbstractEvacuationRule {

	public EvacuateIndividualsRule() {
	}

	@Override
	protected void onExecute( ds.ca.Cell cell ) {
		caController().getCA().markIndividualForRemoval( cell.getIndividual() );
		// Potential needed for statistics:
		ds.ca.StaticPotential exit = caController().getPotentialController().getNearestExitStaticPotential( cell );
		caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addExitToStatistic( cell.getIndividual(), exit );
		// safetyTime etc will be set in the SaveIndividualsRule
	}

	@Override
	public boolean executableOn( ds.ca.Cell cell ) {
		// Regel NUR anwendbar, wenn auf der Zelle ein Individuum steht
		// und die Zelle eine Exitcell ist
		
		Individual i = cell.getIndividual();
		//return (i != null) && (cell instanceof ds.ca.ExitCell) && ( i.getStepEndTime() <= caController().getCA().getTimeStep() );
		boolean testval = false;
		if( (i != null) && (cell instanceof ds.ca.ExitCell)) {
			if( i.getStepEndTime() >= caController().getCA().getTimeStep()+1 )
				testval = false;
			else
				testval = true;
		}
		return (i != null) && (cell instanceof ds.ca.ExitCell) && testval;
	}
}

