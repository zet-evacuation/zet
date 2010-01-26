/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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

import de.tu_berlin.math.coga.rndutils.RandomUtils;
import de.tu_berlin.math.coga.rndutils.generators.GeneralRandom;
import ds.ca.Cell;
import ds.ca.Individual;
import ds.ca.StaticPotential;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This rule changes an Individual's StaticPotential according to the Individual's
 * familiarity value. If this familiarity value is high, the individual chooses a 
 * good StaticPotential, which will lead the Individual to a near-by exit.
 * If the familiarity value is low the individual will choose more or less randomly
 * a StaticPotential, which will not neccessarily guide the individual to an ExitCell
 * in a short time.
 * @author marcel
 *
 */
public class ChangePotentialFamiliarityRule extends AbstractPotentialChangeRule
{

	private static final int CHANGE_THRESHOLD = 4;

	/**
	 * Checks whether the rule is executable or not.
	 * @return Returns "false" if "cell" is not occupied by an Individual.
	 * Returns "true" if "cell" is occupied by an Individual and if this Individual 
	 * whishes to change its StaticPotential according to the probability of changing
	 * its StaticPotential.
	 */
	@Override
	public boolean executableOn(Cell cell) 
	{
		ChangePotentialAttractivityOfExitRule rule = 
			new ChangePotentialAttractivityOfExitRule();
		rule.setCAController(this.caController());
		return rule.executableOn(cell);
	}

	/**
	 * The concrete method changing the individuals StaticPotential.
	 * For a detailed description read the class description above.
	 */
	@Override
	protected void onExecute(Cell cell) 
	{
		Individual individual = cell.getIndividual();
		if(!individual.isSafe()){
			ArrayList<PotentialValueTuple> potentialToLengthOfWayMapper = new ArrayList<PotentialValueTuple>();
			ArrayList<StaticPotential> staticPotentials = new ArrayList<StaticPotential>();
			staticPotentials.addAll(this.caController().getCA().getPotentialManager().getStaticPotentials());
			for (StaticPotential sp : staticPotentials)
			{
				int lengthOfWayValue = sp.getPotential(individual.getCell());
				if (lengthOfWayValue >= 0)  // if this StaticPotential can lead the individual to an ExitCell
					potentialToLengthOfWayMapper.add(new PotentialValueTuple(lengthOfWayValue, sp));
			}
			// Sort the Individual's StaticPotentials according to their familarity value
			Collections.sort(potentialToLengthOfWayMapper);
			int nrOfPossiblePotentials = (int)(Math.round(
					(1 - individual.getFamiliarity()) 
					* potentialToLengthOfWayMapper.size()
			));

			if (nrOfPossiblePotentials < 1)
				nrOfPossiblePotentials = 1;
			GeneralRandom rnd = (RandomUtils.getInstance()).getRandomGenerator();
			int randomPotentialNumber = rnd.nextInt(nrOfPossiblePotentials);

			StaticPotential newPotential = potentialToLengthOfWayMapper.get(randomPotentialNumber).getStaticPotential();

			// Check if the new potential is promising enough to change.
			// This is the case, if at least CHANGE_THRESHOLD cells of
			// the free neighbors have a lower potential (with respect
			// to the new static potential) than the current cell

			ArrayList<Cell> freeNeighbours = cell.getFreeNeighbours();
			int i = 0; 
			int promisingNeighbours = 0;
			int curPotential = newPotential.getPotential(cell);
			while(i < freeNeighbours.size() && promisingNeighbours <= CHANGE_THRESHOLD){
				if(newPotential.getPotential(freeNeighbours.get(i)) < curPotential){
					promisingNeighbours++;
				}					
				i++;
			}

			if(promisingNeighbours > CHANGE_THRESHOLD){
				individual.setStaticPotential(newPotential);
				caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addChangedPotentialToStatistic(individual, caController().getCA().getTimeStep());
			}
		}
	}

}