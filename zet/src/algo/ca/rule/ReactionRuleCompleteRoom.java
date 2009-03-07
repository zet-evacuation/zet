package algo.ca.rule;

import ds.ca.Individual;

/**
 * A rule that alarms an individual if its reaction time is over. After that 
 * the room of the individual is alarmed, too. This alarms all individuals in
 * the room not later than the next step of the {@link CellularAutomaton}.
 * @author Jan-Philipp Kappmeier
 */
public class ReactionRuleCompleteRoom extends AbstractReactionRule {

	/**
	 * Creates the instance.
	 */
	public ReactionRuleCompleteRoom() {
	}

	/**
	 * Executes the rule. If the room is alarmed, the individual is alarmed, too.
	 * If the room is not alarmed, the individual is alarmed if the time is over
	 * otherwise the remaining time is reduced by one.
	 * @param cell
	 */
	@Override
	protected void onExecute( ds.ca.Cell cell ) {
		Individual i = cell.getIndividual();
		if( i.getCell().getRoom().getAlarmstatus() )
			i.setAlarmed( true );
		else {
			if( i.getReactionTime() == 0 ) {
				i.setAlarmed( true );
				cell.getRoom().setAlarmstatus( true );
			} else
				i.setReactionTime( i.getReactionTime() - 1 );
		}
	}
}
