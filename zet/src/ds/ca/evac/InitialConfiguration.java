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
package ds.ca.evac;

import java.util.Collection;
import java.util.Vector;

import ds.ca.evac.PotentialManager;

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
     * @param potentialManager the potential manager
     * @param absoluteMaxSpeed the maximal speed that any individual can have at maximum
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

