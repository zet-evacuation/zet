package algo.ca.rule;

import ds.ca.Cell;
import util.random.RandomUtils;

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
	public boolean executableOn( Cell cell ) {
		ChangePotentialAttractivityOfExitRule rule = new ChangePotentialAttractivityOfExitRule();
		rule.setCAController( this.caController() );
		return rule.executableOn( cell );
	}

	/**
	 * The concrete method changing the individuals StaticPotential.
	 * For a detailed description read the class description above.
	 * @param cell the cell on which the rule is executed
	 */
	@Override
	protected void onExecute( Cell cell ) {
		double prob = this.caController().getParameterSet().probabilityChangePotentialFamiliarityOrAttractivityOfExitRule();
		if( RandomUtils.getInstance().binaryDecision( prob ) ) {
			ChangePotentialFamiliarityRule rule = new ChangePotentialFamiliarityRule();
			rule.setCAController( this.caController() );
			rule.onExecute( cell );
		} else {
			ChangePotentialAttractivityOfExitRule rule = new ChangePotentialAttractivityOfExitRule();
			rule.setCAController( this.caController() );
			rule.onExecute( cell );
		}
	}
}
