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

import ds.ca.Cell;
import ds.ca.Individual.DeathCause;
import ds.ca.StaticPotential;
import java.util.ArrayList;
import de.tu_berlin.math.coga.rndutils.RandomUtils;

/**
 * Sets a random exit to an individual.
 */
public class InitialPotentialRandomRule extends AbstractInitialRule {
	/**	 
	 * @param cell the cell
	 */
	@Override
	protected void onExecute( Cell cell ) {
		ArrayList<StaticPotential> exits = new ArrayList<StaticPotential>();
		exits.addAll( this.caController().getCA().getPotentialManager().getStaticPotentials() );
		int numberOfExits = exits.size();
		RandomUtils random = RandomUtils.getInstance();
		int randomExitNumber = random.getRandomGenerator().nextInt( numberOfExits );

		boolean exitFound = false;
		if( exits.get( randomExitNumber ).getPotential( cell ) < 0 ) {
			for( StaticPotential exit : exits )
				if( exit.getPotential( cell ) >= 0 ) {
					cell.getIndividual().setStaticPotential( exit );
					exitFound = true;
					break;
				}
			if( !exitFound )
				this.caController().getCA().setIndividualDead( cell.getIndividual(), DeathCause.EXIT_UNREACHABLE );
		} else
			cell.getIndividual().setStaticPotential( exits.get( randomExitNumber ) );
	}
}
