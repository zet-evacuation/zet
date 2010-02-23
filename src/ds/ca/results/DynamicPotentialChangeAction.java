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
 * Created on 01.05.2008
 *
 */
package ds.ca.results;

import ds.ca.Cell;
import ds.ca.CellularAutomaton;

/**
 * @author Daniel Pluempe
 *
 */
public class DynamicPotentialChangeAction extends Action {

    protected double newPotential;
    protected Cell affectedCell;
    
    public DynamicPotentialChangeAction(Cell affectedCell, double newPotential){
        this.affectedCell = affectedCell;
        this.newPotential = newPotential;        
    }
    
    @Override
    Action adoptToCA(CellularAutomaton targetCA) throws CADoesNotMatchException {
        Cell newAffectedCell = adoptCell(affectedCell, targetCA);
        return new DynamicPotentialChangeAction(newAffectedCell, newPotential);
    }

    @Override
    public void execute(CellularAutomaton onCA)
            throws InconsistentPlaybackStateException {
        onCA.setDynamicPotential(affectedCell, newPotential);        
    }

    @Override
    public String toString() {
        return "The dynamic potential of cell " 
            + affectedCell 
            + " ist set to "
            + newPotential
            + ".";
    }
    
    public long getNewPotentialValue(){
        return Math.round( newPotential );
    }
}
