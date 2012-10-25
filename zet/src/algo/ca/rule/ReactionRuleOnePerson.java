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

/**
 * Class ReactionRuleOnePerson
 * Erstellt 13.10.2008, 23:39:45
 */
package algo.ca.rule;

import ds.ca.evac.Individual;

/**
 * A rule that alarms an individual if its reaction time is over. No other
 * individuals nor the room will be alarmed.
 * @author Jan-Philipp Kappmeier
 */
public class ReactionRuleOnePerson extends AbstractReactionRule {

	/**
	 * Creates the instance of this rule.
	 */
	public ReactionRuleOnePerson() { }

	/**
	 * Executes the rule. The individual is alarmed if the time is over
	 * otherwise the remaining time is reduced by one. No other individuals are
	 * infected from the alerting of the individual.
	 * @param cell the cell on which the rule is executed
	 */
	@Override
	protected void onExecute( ds.ca.evac.EvacCell cell ) {
		Individual i = cell.getIndividual();
		if( i.getReactionTime()-1 <= 0 )
			i.setAlarmed( true );
		else
			i.setReactionTime( i.getReactionTime() - 1 );
	}
}
