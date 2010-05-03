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
 * A rule that alarms an individual if its reaction time is over. After that 
 * the room of the individual is alarmed, too. This alarms all individuals in
 * the room not later than the next step of the {@link ds.ca.CellularAutomaton}.
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
		else
			if( i.getReactionTime()-1 <= 0 ) {
				i.setAlarmed( true );
				cell.getRoom().setAlarmstatus( true );
			} else
				i.setReactionTime( i.getReactionTime() - 1 );
	}
}
