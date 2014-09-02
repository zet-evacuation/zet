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

package algo.ca;

import algo.ca.rule.AbstractMovementRule;
import algo.ca.rule.Rule;
import de.tu_berlin.math.coga.zet.ZETLocalization2;
import ds.PropertyContainer;
import java.util.ArrayList;

/**
 * <p>The {@code DefaultRuleSet} is a {@link RuleSet} consisting of two
 * sets, the <b>primary rules</b> and the <b>loop rules</b>. The primary rules
 * are supposed to be only executed once to initialize the {@link ds.ca.CellularAutomaton} while
 * the loop rules are supposed to be executed in every step.</p>
 * <p>It is only allowed to have one instance of {@link AbstractMovementRule}
 * which describes the behaviour of the individuals while they are moving.
 * However, the rule can be added to both the primary set and the loop set.</p>
 * @author Jan-Philipp Kappmeier
 */
public class DefaultRuleSet extends RuleSet {
	private AbstractMovementRule movementRule = null;

	public DefaultRuleSet() {
		// Build RuleSet
		super();
	}

	/**
	 * Inserts the rule to both the primary set and the loop set.
	 * @param rule the rule that is to be inserted
	 * @throws java.lang.IllegalArgumentException if two movement rules are inserted
	 */
	@Override
	public void add( Rule rule ) throws IllegalArgumentException {
		if( rule instanceof AbstractMovementRule ) {
			if( movementRule == null )
				movementRule = (AbstractMovementRule)rule;
			else
				throw new IllegalArgumentException( ZETLocalization2.loc.getString( "algo.ca.RuleSet.MovementRuleException" ) );
		}
		super.add( rule );
	}

	/**
	 *
	 * @param rule the rule that is to be inserted
	 * @param useInPrimarySet true if the rule should be added to the primary set
	 * @param useInLoopSet true if the rule should be added to the loop set
	 * @throws java.lang.IllegalArgumentException if two movement rules are inserted
	 */
	@Override
	public void add( Rule rule, boolean useInPrimarySet, boolean useInLoopSet  ) throws IllegalArgumentException{
		if( rule instanceof AbstractMovementRule ) {
			if( movementRule == null )
				movementRule = (AbstractMovementRule)rule;
			else
				throw new IllegalArgumentException( ZETLocalization2.loc.getString( "algo.ca.RuleSet.MovementRuleException" ) );
		}
		super.add( rule, useInPrimarySet, useInLoopSet );
	}

	/**
	 * Returns the {@code MovementRule} of this ruleset, and {@code null}
	 * if no such rule is set.
	 * @return the movement rule
	 */
	public AbstractMovementRule getMovementRule() {
		return movementRule;
	}

	/**
	 *
	 */
	@Override
	protected void selfInit() {
		PropertyContainer pc = PropertyContainer.getInstance();
		ArrayList<String> initRules = (ArrayList<String>) pc.get( "algo.ca.defaultRuleSet.init" );
		ArrayList<String> loopRules = (ArrayList<String>) pc.get( "algo.ca.defaultRuleSet.loop" );
		for( String ruleName : initRules )
			add( createRule( ruleName ), true, false );
		for( String ruleName : loopRules )
			add( createRule( ruleName ), false, true );
	}
}
