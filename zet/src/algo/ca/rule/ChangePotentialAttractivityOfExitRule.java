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

import java.util.ArrayList;

import ds.ca.Cell;
import ds.ca.ExitCell;
import ds.ca.Individual;
import ds.ca.SaveCell;
import ds.ca.StaticPotential;
import util.random.RandomUtils;

/**
 * This rule changes the Individuals StaticPotential. It chooses the admissible 
 * StaticPotential with the highest attractivity value. A StaticPotential is
 * admissible, if it leads the Individual to an ExitCell.
 * Be careful using this rule: Using this rule excessively might have the effect 
 * that all individuals run to the same exit with the highest attractivity value!
 * @author marcel
 *
 */
public class ChangePotentialAttractivityOfExitRule extends AbstractPotentialChangeRule {
	private static final int CHANGE_THRESHOLD = 4;

	/**
	 * Checks whether the rule is executable or not.
	 * @return Returns "false" if "cell" is not occupied by an Individual.
	 * Returns "true" if "cell" is occupied by an Individual and if this Individual 
	 * whishes to change its StaticPotential according to the probability of changing
	 * its StaticPotential.
	 */
	@Override
	public boolean executableOn( Cell cell ) {
		if( cell.getIndividual() == null )
			return false;
		else if( (cell instanceof ExitCell) || (cell instanceof SaveCell) )
			return false;
		else {
			RandomUtils rnd = RandomUtils.getInstance();
			double changePotentialThreshold = this.caController().getParameterSet().changePotentialThreshold( cell.getIndividual() );
			if( rnd.binaryDecision( changePotentialThreshold ) )
				return true;
			else
				return false;
		}
	}

	/**
	 * The concrete method changing the individuals StaticPotential.
	 * For a detailed description read the class description above.
	 */
	@Override
	protected void onExecute( Cell cell ) {
		Individual individual = cell.getIndividual();
		if( !individual.isSafe() ) {
			ArrayList<StaticPotential> staticPotentials = new ArrayList<StaticPotential>();
			staticPotentials.addAll( this.caController().getCA().getPotentialManager().getStaticPotentials() );
			boolean initialPotentialFound = false;
			StaticPotential mostAttractiveSP = null;
			// Find any admissible StaticPotential for this Individual
				int nrOfPotential = 0;
			while( !initialPotentialFound ) {
				mostAttractiveSP = staticPotentials.get( nrOfPotential );
				if( mostAttractiveSP.getPotential( individual.getCell() ) >= 0 )
					initialPotentialFound = true;
				nrOfPotential++;
			}
			// Find the best admissible StaticPotential for this Individual
			for( StaticPotential sp : staticPotentials )
				if( (sp.getAttractivity() > mostAttractiveSP.getAttractivity()) && (sp.getPotential( individual.getCell() ) >= 0) )
					mostAttractiveSP = sp;

			// Check if the new potential is promising enough to change.
			// This is the case, if at least CHANGE_THRESHOLD cells of
			// the free neighbors have a lower potential (with respect
			// to the new static potential) than the current cell

			ArrayList<Cell> freeNeighbours = cell.getFreeNeighbours();
			int i = 0;
			int promisingNeighbours = 0;
			int curPotential = mostAttractiveSP.getPotential( cell );
			while( i < freeNeighbours.size() && promisingNeighbours <= CHANGE_THRESHOLD ) {
				if( mostAttractiveSP.getPotential( freeNeighbours.get( i ) ) < curPotential )
					promisingNeighbours++;
				i++;
			}

			if( promisingNeighbours > CHANGE_THRESHOLD ) {
				individual.setStaticPotential( mostAttractiveSP );
				caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addChangedPotentialToStatistic( individual, caController().getCA().getTimeStep() );
			}
		}
	}
}
