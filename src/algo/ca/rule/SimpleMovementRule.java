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

/*
 * SimpleMovementRule.java
 * Created on 26.01.2008, 17:27:04
 */

package algo.ca.rule;

import ds.ca.evac.Cell;
import ds.ca.evac.Individual;
import java.util.ArrayList;
import de.tu_berlin.math.coga.rndutils.RandomUtils;

/**
 * A simple movement rule that does not care about anything like slack, speed,
 * panic or anything else. Steps are always performed, there is no special
 * behaviour on {@link #isDirectExecute()}.
 * @author Jan-Philipp Kappmeier
 */
public class SimpleMovementRule extends AbstractMovementRule {
	/**
	 * Returns {@code true} if the rule can be executed. That is the case if an
	 * {@link ds.ca.Individual} stands on the specified {@link Cell}.
	 * @param cell the cell
	 * @return {@code true} if an individual stands on the cell, {@code false} otherwise
	 */
	@Override
	public boolean executableOn( Cell cell ) {
		return cell.getIndividual() != null;
	}

	/**
	 * 
	 * @param cell
	 */
	@Override
	protected void onExecute( Cell cell ) {
		Cell targetCell = selectTargetCell( cell, computePossibleTargets( cell, true ) );
		ind = cell.getIndividual();
		if( cell.equals( targetCell) )
			return;
		this.move( targetCell );
	}

	@Override
	public void move( Cell targetCell ) {
		esp.eca.moveIndividual( ind.getCell(), targetCell );
	}

	@Override
	public Cell selectTargetCell( Cell cell, ArrayList<Cell> targets ) {
		if( targets.isEmpty() )
			return cell;

		double p[] = new double[targets.size()];
		for( int i = 0; i < targets.size(); i++ )
			p[i] = Math.exp( esp.parameterSet.effectivePotential( cell, targets.get( i ) ) );

		return targets.get( RandomUtils.getInstance().chooseRandomlyAbsolute( p ) );
	}

	@Override
	public void swap( Cell cell1, Cell cell2 ) {
		throw new UnsupportedOperationException( "Not supported." );
	}
}
