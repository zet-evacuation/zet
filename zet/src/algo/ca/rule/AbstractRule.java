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

package algo.ca.rule;

import algo.ca.algorithm.evac.EvacuationSimulationProblem;
import de.tu_berlin.math.coga.zet.ZETLocalization2;
import ds.ca.evac.EvacCell;
import ds.ca.evac.Individual;

/**
 * @author Daniel R. Schmidt
 */
public abstract class AbstractRule implements Rule {
	protected EvacuationSimulationProblem esp;

	/**
	 * Returns if the rule is executable on the cell. The default behavior is,
	 * that a rule is executable if an {@link Individual} is standing on it.
	 * @param cell the cell that is checked
	 * @return {@code true} if an individual is standing on the cell, {@code false} otherwise
	 */
	@Override
	public boolean executableOn( EvacCell cell ) {
		return cell.getIndividual() != null;
	}

	@Override
	final public void execute( EvacCell cell ) {
		if( !executableOn( cell ) )
			return;

		onExecute( cell );
	}

	abstract protected void onExecute( EvacCell cell );

	@Override
	public void setEvacuationSimulationProblem( EvacuationSimulationProblem esp ) {
		if( this.esp != null )
			throw new RuntimeException( ZETLocalization2.loc.getString( "algo.ca.rule.RuleAlreadyHaveCAControllerException" ) );

		if( esp == null )
			throw new RuntimeException( ZETLocalization2.loc.getString( "algo.ca.rule.CAControllerIsNullException" ) );

		this.esp = esp;
	}
	
//	public void setCAController( CAController caController ) {
//		if( this.caController != null )
//			throw new RuntimeException( DefaultLoc.getSingleton().getString( "algo.ca.rule.RuleAlreadyHaveCAControllerException" ) );
//
//		if( caController == null )
//			throw new RuntimeException( DefaultLoc.getSingleton().getString( "algo.ca.rule.CAControllerIsNullException" ) );
//
//		this.caController = caController;
//		this.parameterSet = caController.getParameterSet();
//	}
}
