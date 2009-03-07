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