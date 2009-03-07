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
            System.out.println("Ersch�pfung von Individuum " 
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
        
        if(oldSpeed != newSpeed){
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
