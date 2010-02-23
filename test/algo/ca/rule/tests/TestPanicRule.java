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
 * Created on 23.04.2008
 *
 */
package algo.ca.rule.tests;

import algo.ca.rule.NonWaitingMovementRule;
import ds.ca.Cell;
import ds.ca.Individual;

/**
 * @author Daniel Pluempe
 *
 */
public class TestPanicRule extends NonWaitingMovementRule {
        
    @Override
    protected void updateExhaustion(Individual i, Cell targetCell){
    }
    
    @Override
    protected boolean slack(Individual i){
        return false;
    }
    
    @Override
    protected void updatePanic(Individual i, Cell targetCell){
        double oldPanic = i.getPanic();
        super.updatePanic(i, targetCell);
        double newPanic = i.getPanic();
        
        if(oldPanic != newPanic){
            System.out.println("Individuum " 
                    + i.getNumber()
                    + " konnte nicht von Zelle "
                    + i.getCell().getX() + ", " + i.getCell().getY()
                    + " zu Zelle "
                    + targetCell.getX() + ", " + targetCell.getY()
                    + " gelangen. Panik aktualisiert von "
                    + oldPanic 
                    + " auf "
                    + newPanic
                    + "."
                    );            
        }
    }
}