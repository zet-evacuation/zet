/**
 * Class ReactionRuleOnePerson
 * Erstellt 13.10.2008, 23:39:45
 */

package algo.ca.rule;

import ds.ca.Individual;

/**
 * A rule that alarms an individual if its reaction time is over. No other
 * individuals nor the room will be alarmed.
 * @author Jan-Philipp Kappmeier
 */
public class ReactionRuleOnePerson extends AbstractReactionRule {

	/**
	 * Creates the instance.
	 */
	public ReactionRuleOnePerson() { }
	
	/**
	 * Executes the rule. The individual is alarmed if the time is over
	 * otherwise the remaining time is reduced by one.
	 * @param cell
	 */
	@Override
	protected void onExecute( ds.ca.Cell cell ) {
		Individual i = cell.getIndividual();
		if( i.getReactionTime() == 0 )
			i.setAlarmed( true );
                else i.setReactionTime( i.getReactionTime() - 1 );
	}
}
