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
 * Class ZToExitMapping
 * Erstellt 29.11.2008, 23:23:53
 */

package exitdistributions;

import ds.ca.evac.Individual;
import ds.ca.evac.TargetCell;
import java.util.HashMap;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ZToExitMapping  extends IndividualToExitMapping {
	/**
	 * This mapping is the main part of the {@code GraphBasedIndividualToExitMapping}.
	 * It maps individuals to {@link ds.ca.TargetCell} objects, i.e. it maps
	 * an individual to the cell it should go to. The target cell represents 
	 * the exit the individual shall go to.
	 */
	private HashMap<Individual, TargetCell> individualToExitMapping; 

	public ZToExitMapping( HashMap<Individual, TargetCell> individualToExitMapping ) {
		this.individualToExitMapping = individualToExitMapping;		
	}
	
	/**
	 * Returns the corresponding target cell for each individual.
	 * The target cell represents the static potential it belongs to.
	 * @param individual An individual that belongs to the cellular automaton that was set in the constructor.
	 * @return The corresponding target cell for this individual, representing the static potential leading to this target cell 
	 * (and those target cells belonging to the same exit).
	 */
	public TargetCell getExit(Individual individual){
		return individualToExitMapping.get( individual );
	}
}
