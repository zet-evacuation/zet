package algo.ca.rule;

import java.util.ArrayList;
import ds.ca.Cell;
import ds.ca.ExitCell;
import ds.ca.Individual;
import ds.ca.StaticPotential;
import ds.ca.Room;

/**
 *
 * @author Joscha
 */
public class ChangePotentialBestResponseOptimizedRule extends AbstractPotentialChangeRule{
	
	private static final double QUEUEING_TIME_WEIGHT_FACTOR = 0.5;
        private static final double MOVING_TIME_WEIGHT_FACTOR = 0.5;
        private static final int TIME_STEP_LIMIT_FOR_NASH_EQUILIBRIUM = 25;
        
	/**
	 * 
	 * @param cell
	 * @return
	 */
	@Override
	public boolean executableOn(Cell cell) {
            int timeStep = this.caController().getCA().getTimeStep();
            return ((timeStep < TIME_STEP_LIMIT_FOR_NASH_EQUILIBRIUM) & (cell.getIndividual() != null)) ? true : false;
            
	}
        
        private double getResponse(Cell cell, StaticPotential pot){
            
            // Constants
            Individual ind = cell.getIndividual();
            double speed = ind.getCurrentSpeed();            

            // Exit dependant values                        
            double distance = Double.MAX_VALUE;
            if (pot.getDistance(cell) >= 0){
                distance = pot.getDistance(cell);
            }
            double movingTime = distance / speed;                  
            
            double exitCapacity = this.caController().getCA().getExitToCapacityMapping().get(pot).doubleValue();    
            //System.out.println("Exit: " + pot.getID() + " : " + exitCapacity);            
            
            // calculate number of individuals that are heading to the same exit and closer to it            
            ArrayList<Individual> otherInds = new ArrayList<Individual>();
            //cell.getRoom().getIndividuals();
            ArrayList<Room> rooms = new ArrayList<Room>();
            rooms.addAll(this.caController().getCA().getRooms());
            for (Room room : rooms){
                for (Individual i : room.getIndividuals()){
                    otherInds.add(i);
                }                
            }
            
            int queueLength = 0;
            if (otherInds != null)
                for (Individual otherInd : otherInds){
                    if (!otherInd.equals(ind)){
                        if (otherInd.getStaticPotential() == pot){                            
                            if (otherInd.getStaticPotential().getDistance(otherInd.getCell()) >= 0)
                                if (otherInd.getStaticPotential().getDistance(otherInd.getCell()) < distance){
                                    queueLength++;
                                }
                        }
                    }
                }           
                //System.out.println("Potential = " + pot.getID());
                //System.out.println("Queue / Kapa = " + queueLength + " / " + exitCapacity + " = " + (queueLength / exitCapacity));
                //System.out.println("Dist / Speed = " + distance + " / " + speed + " = " + (distance / speed));
            
            // calculateEstimatedEvacuationTime
            return responseFunction1(queueLength,exitCapacity,movingTime);
            
        }
        
        private double responseFunction1(int queueLength, double exitCapacity, double movingTime){
            return (QUEUEING_TIME_WEIGHT_FACTOR * (queueLength / exitCapacity)) + (MOVING_TIME_WEIGHT_FACTOR * movingTime);
        }        

	/**
	 * 
	 * @param cell
	 */
	@Override
	protected void onExecute(Cell cell) {                                   
                        
            ArrayList<StaticPotential> exits = new ArrayList<StaticPotential>();
            exits.addAll(this.caController().getCA().getPotentialManager().getStaticPotentials());            
            StaticPotential newPot = cell.getIndividual().getStaticPotential();
            double response = Double.MAX_VALUE;
            for (StaticPotential pot : exits){                
                if (getResponse(cell,pot) < response){
                    response = getResponse(cell,pot);
                    newPot = pot;
                }
            }
            cell.getIndividual().setStaticPotential(newPot);            
            
	}
}