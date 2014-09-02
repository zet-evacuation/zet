/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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

import evacuationplan.CAPathPassabilityChecker;
import evacuationplan.EvacPotential;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * A StaticPotential is special type of PotentialMap, of that exists several in one PotentialManager.
 * Therefore it has a unique ID.
 * The StaticPotential consists of two Potentials. Both describe the distance to an ExitCell. But the first
 * one represents this distance with an smoothly calculated value while the other on does it by the exact 
 * distance.
 */
public class StaticPotential extends PotentialMap {
    
	protected String name="DefaultNameForStaticPotential";


    /**
     * Counts the number of existing StaticPotentials. 
     * Every new StaticPotential gets automatically a unique ID.
     */
    protected static int idCount = 0;
    
    /**
     * Attractivity for this cell
     */
    protected int attractivity;
    
    /**
     * Id of the StaticPotential.
     */
    protected int id;
    
    /**
	 * contains the associated ExitCells
	 */
    protected ArrayList<ExitCell> associatedExitCells;
    
    /**
     * A HashMap that assign each EvacCell a Int value which represents the real distance).
     */
    protected HashMap<EvacCell, Double> cellToDistance;

    /**
     * Creates a StaticPotential with a automatic generated unique ID, that can not be changed.
     */
    public StaticPotential () {
        super();
        this.id = idCount;
        idCount++;
        cellToDistance = new HashMap<>();
        associatedExitCells = new ArrayList<>();
    }

    /**
     * Get the ID of this StaticPotential.
     * @return ID of this StaticPotential   
     */
    public int getID (){
        return id;
    }

	public int getAttractivity() {
		return attractivity;
	}

	public void setAttractivity(int attractivity) {
		this.attractivity = attractivity;
	}
	
	/**
     * Associates the specified distance with the specified EvacCell in this StaticPotential.
     * If a EvacCell is specified that exists already in this StaticPotential the value will be overwritten. Otherwise
     * a new mapping is created.
     * @param cell cell which has to be updated or mapped 
     * @param i distance of the cell
     */
    public void setDistance (EvacCell cell, double i) throws IllegalArgumentException{
    	Double distance = new Double(i);
        if(!cellToDistance.containsKey(cell)){
        	cellToDistance.put(cell,distance);
        } else {
        	cellToDistance.remove(cell);
        	cellToDistance.put(cell,distance);
        }
    }    
    
    /**
     * Gets the distance of a specified EvacCell.
     * The method returns -1 if you
     * try to get the distance of a cell that does not exists.
     * @param cell A cell which distance you want to know.
     * @return distance of the specified cell or -1 if the cell is not mapped by this potential
     */
    public double getDistance (EvacCell cell) throws IllegalArgumentException{
    	Double distance = cellToDistance.get(cell);
    	if(distance== null){
    		return -1.0;
    	} else {
    		return distance;
    	}
    }
		
	public double getMaxDistance() {
		double max = 0;
		Set<EvacCell> cells = getMappedDistanceCells();
		for( EvacCell cell : cells ) {
			if( getDistance( cell ) > max )
				max = getDistance( cell );
		}
		return max;
	}
    
    /**
     * Removes the mapping for the specified EvacCell.
     * The method throws {@code IllegalArgumentExceptions} if you
     * try to remove the mapping of a EvacCell that does not exists.
     * @param cell A EvacCell that mapping you want to remove.
     */
    public void deleteDistanceCell(EvacCell cell) throws IllegalArgumentException{
        if(!(cellToDistance.containsKey(cell))){
            throw new IllegalArgumentException("The Cell must be insert previously!");
        }
        cellToDistance.remove(cell);
    }
    
    /**
     * Returns true if the mapping for the specified EvacCell exists.
     * @param cell A EvacCell of that you want to know if it exists.
     */
    public boolean containsDistance(EvacCell cell){
        return cellToDistance.containsKey(cell); 	
    }
    
    /**
     * Returns a set of all cell which are mapped by this distance
     * @return set of mapped cells
     */
    public Set<EvacCell> getMappedDistanceCells(){
    	return cellToDistance.keySet();
    }

	public ArrayList<ExitCell> getAssociatedExitCells() {
		return associatedExitCells;
	}

	public void setAssociatedExitCells(ArrayList<ExitCell> associatedExitCells) {
		this.associatedExitCells = associatedExitCells;
                setName(associatedExitCells.get(0).getName());
	}
        
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public int getTruePotential(EvacCell cell){
        return getPotential(cell);
    }
    
    public EvacPotential getAsEvacPotential(Individual i, CAPathPassabilityChecker checker){
    	EvacPotential evacPotential = new EvacPotential(i, checker);
    	evacPotential.associatedExitCells = this.associatedExitCells;
    	evacPotential.attractivity = this.attractivity;
    	evacPotential.cellToDistance = this.cellToDistance;
    	evacPotential.cellToPotential = this.cellToPotential;
    	evacPotential.id = this.id;
    	evacPotential.name = this.name;
    	return evacPotential;
    }

        
}

