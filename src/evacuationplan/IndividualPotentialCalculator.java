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
package evacuationplan;

import java.util.ArrayList;

import ds.ca.evac.EvacCell;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.ca.evac.Individual;
import algo.ca.SPPotentialController;

/**
 * This class provides the possibility to calculate a individual specific potential.
 * It extends the {@code SPPotentialController} and only changes
 * the {@code getNeighbor()} method to forbid transitions that
 * the individual may not do according to the {@code checker}. 
 */
public class IndividualPotentialCalculator extends SPPotentialController {

	Individual i;
	CAPathPassabilityChecker checker;
	
	/**
	 * Create a new calculator that can calculate a individual specific potential for 
	 * the cellular automaton {@code ca} and a given individual {@code i}.
	 * The {@code checker} is used to check which transitions are forbidden for 
	 * the individual. 
	 * @param ca A cellular automaton.
	 * @param i A certain individual that is already placed in the cellular automaton.
	 * @param checker A checker to say which transitions are forbidden for the individual.
	 */
	public IndividualPotentialCalculator(EvacuationCellularAutomaton ca, Individual i, CAPathPassabilityChecker checker){
		super(ca);
		this.i = i;
		this.checker = checker;
	}
	
	@Override
	/**
	 * Overwritten method that obtains all original neighbors of a cell and sorts
	 * those out that the individual may not reach from {@code cell} 
	 * because the transition is forbidden for the individual (according
	 * to the {@code checker} object).
	 */
	public ArrayList<EvacCell> getNeighbours(EvacCell cell){
            ArrayList<EvacCell> evacPlanNeighbours = new ArrayList<EvacCell>();
            for (EvacCell target: cell.getNeighbours()) {
                if (checker.canPass(i, cell, target)) {
                    evacPlanNeighbours.add(target);
                }
            }
            return evacPlanNeighbours;
	}
}
