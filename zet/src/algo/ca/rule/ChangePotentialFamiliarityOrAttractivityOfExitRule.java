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

import ds.ca.evac.EvacCell;
import de.tu_berlin.math.coga.rndutils.RandomUtils;

/**
 * This rule combines the to concrete rules "ChangePotentialFamiliarityRule" and 
 * "ChangePotentialAttractivityOfExitRule". With the probability defined in
 * "probFamiliarity" the first rule, which chooses the new StaticPotential according
 * to the Individual's familiarity value, is chosen, and with the complementary 
 * probability the second rule, which chooses the Individual's new StaticPotential 
 * according to the attractivity of the ExitCells belonging to the StaticPotentials, 
 * is chosen.
 * @author marcel
 *
 */
public class ChangePotentialFamiliarityOrAttractivityOfExitRule extends AbstractPotentialChangeRule {

	/**
	 * Checks whether the rule is executable or not.
	 * @param cell the cell on which the rule shall be executed
	 * @return Returns "false" if "cell" is not occupied by an Individual.
	 * Returns "true" if "cell" is occupied by an Individual and if this Individual 
	 * whishes to change its StaticPotential according to the probability of changing
	 * its StaticPotential.
	 */
	@Override
	public boolean executableOn( EvacCell cell ) {
		ChangePotentialAttractivityOfExitRule rule = new ChangePotentialAttractivityOfExitRule();
		rule.setEvacuationSimulationProblem( esp );
		return rule.executableOn( cell );
	}

	/**
	 * The concrete method changing the individuals StaticPotential.
	 * For a detailed description read the class description above.
	 * @param cell the cell on which the rule is executed
	 */
	@Override
	protected void onExecute( EvacCell cell ) {
		double prob = esp.parameterSet.probabilityChangePotentialFamiliarityOrAttractivityOfExitRule();
		if( RandomUtils.getInstance().binaryDecision( prob ) ) {
			ChangePotentialFamiliarityRule rule = new ChangePotentialFamiliarityRule();
			rule.setEvacuationSimulationProblem( esp );
			rule.onExecute( cell );
		} else {
			ChangePotentialAttractivityOfExitRule rule = new ChangePotentialAttractivityOfExitRule();
			rule.setEvacuationSimulationProblem( esp );
			rule.onExecute( cell );
		}
	}
}
