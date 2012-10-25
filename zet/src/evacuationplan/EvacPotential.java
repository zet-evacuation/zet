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
package evacuationplan;

import ds.ca.evac.EvacCell;
import ds.ca.evac.Individual;
import ds.ca.evac.StaticPotential;

public class EvacPotential extends StaticPotential {
	
	Individual ind;
	CAPathPassabilityChecker checker;
	
	public EvacPotential(Individual ind, CAPathPassabilityChecker checker){
		this.ind = ind;
		this.checker = checker;
	}
	
	@Override
	public int getPotential (EvacCell cell) throws IllegalArgumentException{
		if(cell != null){
			Double potential = cellToPotential.get(cell);
			if(potential != null){
				if(checker.canPass(ind, ind.getCell(), cell)) {
					// TODO Potential Long
					return (int)Math.round( potential );
				} else {
					return Integer.MAX_VALUE;
				}
			} else {
				return Integer.MAX_VALUE;
			}
		}
		else {
			return Integer.MAX_VALUE;
		}
    }
	
	@Override
	public int getTruePotential(EvacCell cell){
	    return super.getPotential(cell);
	}
	
}
