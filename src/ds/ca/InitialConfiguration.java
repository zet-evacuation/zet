package ds.ca;

import java.util.Collection;
import java.util.Vector;

import ds.ca.PotentialManager;

/**
 * This class is a container for an initial configuration of the cellular
 * automaton. The configuration is given by all of the automatons rooms (which 
 * include all cells and the initial placing of all individuals), its global 
 * potentials and its initial dynamic potential.   
 * 
 * @author Daniel Pluempe
 *
 */
public class InitialConfiguration{
    
    /**
     * The rooms of the cellular automaton, including cells
     */
    private Collection<Room> rooms;
    
    /**
     * The global potential of the cellular automaton
     */
    private PotentialManager potentialManager;
    
    private double absoluteMaxSpeed;
    
    /**
     * Constructs a new initial configuration of a cellular automaton.
     * @param rooms The automaton's rooms, including cells and the
     * initial placing of individuals
     * @param globalPotential The automaton's global potentials
     * @param dynamicPotential The initial dynamic potential of the automaton
     */
    public InitialConfiguration (Collection<Room> rooms, PotentialManager potentialManager, double absoluteMaxSpeed) {
        this.rooms = rooms;
        this.potentialManager = potentialManager;
        this.absoluteMaxSpeed = absoluteMaxSpeed;
    }

    public double getAbsoluteMaxSpeed(){
        return absoluteMaxSpeed;
    }
    
    /**
     * Get the global static potential layers of the automaton
     * @return The initial static potentials
     */
    public PotentialManager getPotentialManager () {
        return potentialManager;
    }

    /**
     * Get all rooms, including all cells and the initial placing 
     * of individuals
     * @return The rooms of the automaton
     */
    public Collection<Room> getRooms () {
        return rooms;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(){
        String representation = "";
               
        for(Room aRoom : rooms){
            representation += "\n Room (" + aRoom + "):\n";
            representation += aRoom.graphicalToString();
        }
                        
        return representation;        
    }

	void setAbsoluteMaxSpeed( double absoluteMaxSpeed ) {
		this.absoluteMaxSpeed = absoluteMaxSpeed;
	}
}

