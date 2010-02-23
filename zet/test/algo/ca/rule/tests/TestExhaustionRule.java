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

import ds.ca.Cell;
import ds.ca.Individual;
import algo.ca.rule.NonWaitingMovementRule;

/**
 * @author Daniel Pluempe
 *
 */
public class TestExhaustionRule extends NonWaitingMovementRule {
    @Override
    protected void updateExhaustion(Individual i, Cell targetCell){
        double oldExhaustion = i.getExhaustion();
        super.updateExhaustion(i, targetCell);
        double newExhaustion = i.getExhaustion();
        
        if(oldExhaustion != newExhaustion){
            System.out.println("Erschöpfung von Individuum "
                    + i.getNumber()
                    + " aktualisiert von "
                    + oldExhaustion
                    + " auf "
                    + newExhaustion
                    + "."
                    );                           
        }
    }
    
    @Override
    protected void updateSpeed(Individual i){
        double oldSpeed = i.getCurrentSpeed();
        super.updateSpeed(i);
        double newSpeed= i.getCurrentSpeed();
        
        if(oldSpeed != newSpeed) {
            System.out.println("Geschwindigkeit von Individuum " 
                    + i.getNumber()
                    + " aktualisiert von "
                    + oldSpeed
                    + " auf "
                    + newSpeed
                    + "(MaxSpeed = " 
                    + i.getMaxSpeed() 
                    + ")."
                    );                           
        }
    }
    
    @Override
    protected boolean slack(Individual i){
        return false;
    }
    
    @Override
    protected void updatePanic(Individual i, Cell targetCell){
    }
}
