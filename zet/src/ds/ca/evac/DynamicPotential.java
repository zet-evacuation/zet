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
package ds.ca.evac;

import ds.ca.results.DynamicPotentialChangeAction;
import ds.ca.results.VisualResultsRecorder;

/**
 * A DynamicPotential is special type of PotentialMap, of that exists only once in the PotentialManager.
 */

public class DynamicPotential extends PotentialMap {
    
   /**
    * Creates a DynamicPotential.
    */
   public DynamicPotential () {
        super();    
        this.clear();
   }
   
   @Override
   public void setPotential(EvacCell cell, double value) {
       super.setPotential(cell, value);
       VisualResultsRecorder.getInstance().recordAction(new DynamicPotentialChangeAction(cell, value));
   }

 	/**
	 * {@inheritDoc}
	 * The Potential value of the removed cell is saved as 0 in the
	 * {@link VisualResultsRecorder}.
	 * @param cell A EvacCell whose mapping should be removed
	 * @throws IllegalArgumentException if the cell is not contained in the map
	 */
	@Override
	public void deleteCell( EvacCell cell ) throws IllegalArgumentException {
		super.deleteCell( cell );
		VisualResultsRecorder.getInstance().recordAction( new DynamicPotentialChangeAction( cell, 0 ) );
	}
	 
   /**
    * Get the potential of a specified EvacCell.
    * The method returns 0 if you
    * try to get the potential of a cell that does not exists.
    * @param cell A cell which potential you want to know.
    * @return potential of the specified cell or -1 if the cell is not mapped by this potential
    */
   @Override
   public int getPotential (EvacCell cell) throws IllegalArgumentException{
       Double potential = cellToPotential.get(cell);
       if(potential == null){
           return 0;
       } else {
				 // TODO Potential long
           return (int)Math.round( potential );
       }
   }
   
   public void clear(){
	   cellToPotential.clear();
   }
}