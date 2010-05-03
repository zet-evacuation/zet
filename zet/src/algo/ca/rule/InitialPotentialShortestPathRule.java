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

import ds.ca.CAController;
import java.util.ArrayList;
import ds.ca.Cell;
import ds.ca.StaticPotential;
import ds.ca.Individual;

/**
 * This rule chooses an Individual's (the one standing on the current cell) initial
 * StaticPotential according to the distances to the exits.
 * If the Individual standing on "cell" is caged (it cannot leave the building, 
 * because there is now passable way to an exit), this Individual has to die
 * because it cannot be evacuated.
 * @author Marcel Preuß, Sylvie Temme
 *
 */
public class InitialPotentialShortestPathRule extends AbstractInitialRule {

	/**
	 * This rule chooses an Individual's (the one standing on "cell") initial
	 * StaticPotential. This Potential leads to the nearest exit.
	 * If the Individual standing on "cell" is caged (it cannot leave the building, 
	 * because there is now passable way to an ExitCell), this Individual has to die
	 * because it cannot be evacuated.
	 * @param cell the cell
	 */
	@Override
	protected void onExecute( Cell cell ) {
		assignShortestPathPotential( cell, this.caController() );
	}

	public static void assignShortestPathPotential( Cell cell, CAController caController ) {
		Individual individual = cell.getIndividual();
		ArrayList<StaticPotential> staticPotentials = new ArrayList<StaticPotential>();
		staticPotentials.addAll( caController.getCA().getPotentialManager().getStaticPotentials() );
		StaticPotential initialPotential = new StaticPotential();
		double minDistanceToEvacArea = Double.MAX_VALUE;
		double distanceToEvacArea;
		for( StaticPotential sp : staticPotentials ) {
			distanceToEvacArea = sp.getDistance( individual.getCell() );
			if( distanceToEvacArea >= 0 && distanceToEvacArea <= minDistanceToEvacArea ) {
				minDistanceToEvacArea = sp.getDistance( individual.getCell() );
				initialPotential = sp;
			}
		}

		// Check whether the individual is caged and cannot leave the building -> it has to die
		if( minDistanceToEvacArea == Double.MAX_VALUE )
			caController.getCA().setIndividualDead( individual, Individual.DeathCause.EXIT_UNREACHABLE );

		individual.setStaticPotential( initialPotential );
		caController.getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addMinDistancesToStatistic( individual, minDistanceToEvacArea, initialPotential.getDistance( cell ) );
		caController.getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addChangedPotentialToStatistic( individual, 0 );
		caController.getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addExhaustionToStatistic( individual, 0, individual.getExhaustion() );
		caController.getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addPanicToStatistic( individual, 0, individual.getPanic() );
		// ToDo: Potential des Individuums im VisualResultsRecorder speichern	}
	}
}

