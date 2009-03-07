package ds.ca.results;

import ds.ca.Cell;
import ds.ca.CellularAutomaton;
import ds.ca.Individual;
import ds.ca.InitialConfiguration;

/**
 * This abstract class represents an action on the cellular automaton.
 * The action is performed by an individual and starts and ends in a 
 * cell. It can start and end in the same cell.
 * 
 * @author Daniel Pluempe
 *
 */
public abstract class Action implements Cloneable{
    
    protected class CADoesNotMatchException extends RuntimeException{
        private static final long serialVersionUID = 1L;

        public CADoesNotMatchException(){
            super("The action could not be adopted to the new CA because the new CA is incompatible with the old one.");
        }
        
        public CADoesNotMatchException(Action action, String message){
            super("The action \""
                    + action 
                    + "could not be converted: "
                    + message
                    );
        }
    }
    
    /**
     * Updates all references in this action in a way that allows it to be
     * replayed based on the given cellular automaton.
     * @param targetCa The cellular automaton to which 
     * this action should be adopted. 
     * @return The adopted action
     */    
    abstract Action adoptToCA(CellularAutomaton targetCA) throws CADoesNotMatchException;
    
    /**
     * Executes the action with respect to the starting and ending cell
     * of the action. 
     */    
    public abstract void execute(CellularAutomaton onCA) throws InconsistentPlaybackStateException;
    
    /**
     * Every subclass of this class should override the <code>toString()</code>.
     */
    @Override
    public abstract String toString();
    
    protected Cell adoptCell(Cell cell, CellularAutomaton targetCA){
        ds.ca.Room newRoom = targetCA.getRoom(cell.getRoom().getID());
        ds.ca.Cell newCell = newRoom.getCell(cell.getX(), cell.getY());
        return newCell;
    }
}