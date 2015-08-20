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
package algo.ca.rule;

import java.util.ArrayList;
import ds.ca.evac.EvacCell;
import ds.ca.evac.Individual;
import ds.ca.evac.StaticPotential;

/**
 *
 * @author Joscha Kulbatzki
 */
public class ChangePotentialInsufficientAdvancementRule extends AbstractPotentialChangeRule {

	private static final int CHANGE_THRESHOLD = 3;

	/**
	 *
	 * @param cell
	 * @return true, if the cange potential rule can be used
	 */
	@Override
	public boolean executableOn( EvacCell cell ) {
		if( cell.getIndividual() != null && !cell.getIndividual().isSafe() )
			return true;
		else
			return false;
	}

	/**
	 *
	 * @param cell
	 */
	@Override
	protected void onExecute( EvacCell cell ) {

		// Get the potential of the individual on the {@code cell} as well as some other concerning constants of the individual
		Individual individual = cell.getIndividual();
		StaticPotential sp = individual.getStaticPotential();
		int cellPotential = sp.getPotential( cell );
		int cellCountToChange = individual.getCellCountToChange();
		int memoryIndex = individual.getMemoryIndex();

		// Update the {@code potentialMemory}
		if( memoryIndex == 0 )
			individual.setPotentialMemoryStart( new PotentialValueTuple( cellPotential, sp ) );

		// Change potential, if not enough advancement to the designated exit cell has been made
		if( memoryIndex == cellCountToChange - 1 ) {

			/**
			 * Calibratingfactor -
			 * The smaller {@code epsilon}, the lower the probability of a potential-change
			 */
			int epsilon = 10;

			individual.setPotentialMemoryEnd( new PotentialValueTuple( cellPotential, sp ) );

			int potentialDifference = individual.getPotentialMemoryStart().getLengthOfWay() - individual.getPotentialMemoryEnd().getLengthOfWay();
			if( (potentialDifference < epsilon) && (sp == individual.getPotentialMemoryStart().getStaticPotential()) ) {

				// Calculate the second best Potential and the associated potential value on the {@code cell}
				ArrayList<StaticPotential> staticPotentials = new ArrayList<StaticPotential>();
				staticPotentials.addAll( esp.eca.getPotentialManager().getStaticPotentials() );
				StaticPotential minWayLengthPotential = sp;
				int lengthOfWayValue = Integer.MAX_VALUE;
				for( StaticPotential statPot : staticPotentials )
					if( (statPot.getPotential( cell ) < lengthOfWayValue) && (statPot != sp) ) {
						minWayLengthPotential = statPot;
						lengthOfWayValue = statPot.getPotential( cell );
					}

				// Check if the new potential is promising enough to change.
				// This is the case, if at least CHANGE_THRESHOLD cells of
				// the free neighbors have a lower potential (with respect
				// to the new static potential) than the current cell

				ArrayList<EvacCell> freeNeighbours = cell.getFreeNeighbours();
				int i = 0;
				int promisingNeighbours = 0;
				int curPotential = minWayLengthPotential.getPotential( cell );
				while(i < freeNeighbours.size() && promisingNeighbours <= CHANGE_THRESHOLD) {
					if( minWayLengthPotential.getPotential( freeNeighbours.get( i ) ) < curPotential )
						promisingNeighbours++;
					i++;
				}

				if( promisingNeighbours > CHANGE_THRESHOLD ) {
					individual.setStaticPotential( minWayLengthPotential );
					esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addChangedPotentialToStatistic( individual, esp.eca.getTimeStep() );
				}
			}
		}

		memoryIndex = (memoryIndex + 1) % cellCountToChange;
		individual.setMemoryIndex( memoryIndex );
	}
}
