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
