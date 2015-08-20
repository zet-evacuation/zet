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

import ds.ca.evac.EvacCell;
import ds.ca.results.VisualResultsRecorder;
import ds.ca.results.IndividualStateChangeAction;
import org.zetool.rndutils.RandomUtils;
import java.util.List;

/**
 *
 * @author Sylvie Temme
 */
public class ICEM09MovementRule extends SimpleMovementRule2 {

	public ICEM09MovementRule() {}
        
	/**
	 * An easier version of the rule ignoring the alarmed status of individuals.
	 * For the paper, it is not necessary to alarm people.
	 * @param cell 
	 */
	@Override
	protected void onExecute( ds.ca.evac.EvacCell cell ) {
		ind = cell.getIndividual();
		
		if( canMove( ind ) )
			if( isDirectExecute() ) {
				EvacCell targetCell = selectTargetCell( cell, computePossibleTargets( cell, true ) );
				setMoveRuleCompleted( true );
				move( targetCell );
			} else {
				computePossibleTargets( cell, false );
				setMoveRuleCompleted( true );
			}
		else // Individual can't move, it is already moving
			setMoveRuleCompleted( false );
		VisualResultsRecorder.getInstance().recordAction( new IndividualStateChangeAction( ind ) );
	}

	/**
	 * Given a starting cell, this method picks one 
	 * of its reachable neighbours at random. The i-th neighbour is 
	 * chosen with probability {@code p(i) := N * exp[mergePotentials(i, cell)]}
	 * where N is a constant used for normalisation. 
	 * 
	 * @param cell The starting cell
	 * @return A neighbour of {@code cell} chosen at random.
	 */
	@Override
	public EvacCell selectTargetCell( EvacCell cell, List<EvacCell> targets ) {
		if( targets.isEmpty() )
			return cell;

		double p[] = new double[targets.size()];

		for( int i = 0; i < targets.size(); i++ )
			p[i] = Math.exp( esp.parameterSet.effectivePotential( cell, targets.get( i ) ) );

		int number = RandomUtils.getInstance().chooseRandomlyAbsolute( p );
		return targets.get( number );
	}
}
