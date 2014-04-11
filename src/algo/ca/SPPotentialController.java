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

package algo.ca;

import algo.ca.util.PotentialUtils;
import de.tu_berlin.math.coga.zet.ZETLocalization2;
import de.tu_berlin.math.coga.rndutils.RandomUtils;
import de.tu_berlin.math.coga.rndutils.generators.GeneralRandom;
import ds.ca.evac.EvacCell;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.ca.evac.DynamicPotential;
import ds.ca.evac.ExitCell;
import ds.ca.evac.PotentialManager;
import ds.ca.evac.Room;
import ds.ca.evac.StaticPotential;
import ds.ca.evac.DoorCell;
import ds.ca.evac.TargetCell;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * This class provides general functionality for manipulation of PotentialMaps.
 * @author Matthias Woste
 *
 */
public class SPPotentialController implements PotentialController {

	/**
	 * reference to the cellular automaton
	 */
	private EvacuationCellularAutomaton ca;

	/**
	 * reference to a PotentialManager
	 */
	private PotentialManager pm; 

	
	/**
	 * Maps static potentials to their associated target cells
	 */
	private HashMap<TargetCell, StaticPotential> targetToPotentialMapping;
	
	/**
	 * Constructs an PotentialController instance for a given cellular automaton
	 * @param ca EvacuationCellularAutomaton instance to work with
	 */
	public SPPotentialController(EvacuationCellularAutomaton ca){
		this.ca = ca;
		this.pm = ca.getPotentialManager();
		this.targetToPotentialMapping = null;
	}

	/**
	 * Returns the associated cellular automaton
	 * @return associated cellular automaton
	 */
	public EvacuationCellularAutomaton getCA(){
		return this.ca;
	}

	/**
	 * Sets the reference to a cellular automaton
	 * @param ca EvacuationCellularAutomaton object
	 */
	public void setCA(EvacuationCellularAutomaton ca){
		this.ca = ca;
	}

	/**
	 * Returns the associated potential manager
	 * @return associated potential manager
	 */
	public PotentialManager getPm() {
		return pm;
	}

	/**
	 * Sets the reference to a potential manager
	 * @param pm PotentialManager object
	 */
	public void setPm(PotentialManager pm) {
		this.pm = pm;
	}
	
	public void generateSafePotential(){
		StaticPotential safePotential = new StaticPotential();
		Collection<Room> rooms = ca.getRooms();
		for(Room r : rooms){
			ArrayList<EvacCell>cells = r.getAllCells();
			for(EvacCell c : cells){
				safePotential.setPotential(c, 1);
			}
		}
		safePotential.setName("SafePotential");
		pm.setsafePotential(safePotential);
	}

	/**
	 * This method updates the values stored in the dynamic potential in the following way. With the probability
	 * decay a cell decreases its dynamic potential by one. Afterwards a cell with a dynamic potential greater than zero
	 * increases the dynamic potential of one of its neighbour cells by one.
	 * @param diffusion The probability of increasing the dynamic potential of one neighbour cell of a cell with a dynamic potential 
	 * greater than zero by one.
	 * @param decay The probability of decreasing the dynamic potential of a cell.
	 */
	public void updateDynamicPotential(double diffusion, double decay){
		GeneralRandom rnd = RandomUtils.getInstance().getRandomGenerator();
		DynamicPotential dynPot = pm.getDynamicPotential();
		//ArrayList<Cell> diffusionCells = new ArrayList<Cell>();		
		EvacCell[] cellsCopy = dynPot.getMappedCells().toArray( new EvacCell[dynPot.getMappedCells().size()]);
		/* NEW CODE */
		for( EvacCell c : cellsCopy ) {
			//System.out.println( "DynPot: "+ dynPot.getPotential(c));
			double randomNumber = rnd.nextDouble();
//			System.out.println( "Randomnumber " + randomNumber + " in updateDynamicPotential" );
			if( /*dynPot.getPotential(c) > 0 && */diffusion > randomNumber ) {
				// Potential diffuses to a a neighbour cell. It should not increase, so
				// reduce it afterwards on this cell!
				EvacCell randomNeighbour = null;
				while(randomNeighbour == null) {
							final int randomInt = rnd.nextInt((getNeighbours(c)).size());
							randomNeighbour = (getNeighbours(c)).get(randomInt);
				}
				decreaseDynamicPotential(c);
				// test, if now potential is 0 so the potential in the diffused cell can decrease already in this step.
				randomNumber = rnd.nextDouble();
				if( !(dynPot.getPotential(c) == 0 && decay > randomNumber) )
					increaseDynamicPotential(randomNeighbour);
			}
			randomNumber = rnd.nextDouble();
			if( dynPot.getPotential(c) > 0 && decay > randomNumber )
				decreaseDynamicPotential(c);
		}

	}

	/**
	 * This method merges StaticPotentials into a new one. The new potential
	 * is calculated for each cell by taking the minimum over all given static potentials.
	 * The attractiveness of the new static potential is the average over all attractiveness values given by the
	 * specified static potentials to merge.
	 * @param potentialsToMerge Contains an ArrayList with the StaticPotential object to merge
	 * @return the new potential
	 */
	public StaticPotential mergePotentials(ArrayList<StaticPotential> potentialsToMerge){
	    return PotentialUtils.mergePotentials(potentialsToMerge);
	}

	/**
	 * Increases the potential of the specified EvacCell about one.
	 * Associates the specified potential with the specified EvacCell in this PotentialMap.	
	 * @param cell A cell which potential you want to increase.  
	 */
	public void increaseDynamicPotential (EvacCell cell){
		int potential;
		DynamicPotential dynPot = pm.getDynamicPotential();
		if(dynPot.contains(cell)){
			potential = dynPot.getPotential(cell) + 1;
			dynPot.deleteCell(cell);
			dynPot.setPotential(cell, (double)potential);
		} else {
			potential =1;
			dynPot.setPotential(cell, (double)potential);
		}
	}

	/**
	 * Decreases the potential of the specified EvacCell about one if its dynamic potential is greater than zero.
	 * Associates the specified potential with the specified EvacCell in this PotentialMap.
	 * The method throws {@code IllegalArgumentExceptions} if you
	 * try to decrease the potential of a EvacCell that not exists in this PotentialMap.
	 * @param cell A cell which potential you want to decrease.  
	 */
	public void decreaseDynamicPotential (EvacCell cell) throws IllegalArgumentException{
		DynamicPotential dynPot = pm.getDynamicPotential();
		if(!(dynPot.contains(cell))){
			throw new IllegalArgumentException (ZETLocalization2.loc.getString ("algo.ca.InsertCellPreviouslyException"));
		}
		
		//if(cell.getIndividual() != null){
		//    return;
		//}

		if(dynPot.getPotential(cell) == 1) {
			dynPot.deleteCell(cell);
		} else {
			int potential = dynPot.getPotential(cell)-1;
			dynPot.setPotential(cell,potential);
		}
		/*if(dynPot.contains(cell)){
			if(dynPot.getPotential(cell) == 0){
				dynPot.deleteCell(cell);
			}
		}*/
	}
	
	/**
	 * Calculates a StaticPotential starting at the ExitCell specified in the parameter exitBlock. The potential 
	 * describes the distance between a cell an the given ExitCells. Such a potential uses some "Smoothing" to
	 * approximate the real distance.
	 * It calculates also a second StaticPotential which represents the distance nearly exactly. For the 
	 * diagonal distance between two cells 1.4 instead of sqrt(2) is used.
	 * @param exitBlock list of ExitCells
	 * @return the calculated StaticPotential
	 */
	public StaticPotential createStaticPotential( ArrayList<ExitCell> exitBlock ) {
		StaticPotential newSP = new StaticPotential();
		newSP.setAssociatedExitCells( exitBlock );
		newSP.setAttractivity( exitBlock.get( 0 ).getAttractivity() );
		ArrayList<? extends EvacCell> parentList;
		ArrayList<EvacCell> childList = new ArrayList<EvacCell>();
		HashMap<EvacCell, SmoothingTupel> childTupel = new HashMap<EvacCell, SmoothingTupel>();

		for( ExitCell c : exitBlock ) {
			newSP.setPotential( c, 0 );
			newSP.setDistance( c, 0.0 );
		}

		parentList = exitBlock;
		while( !parentList.isEmpty() ) {
			childList = new ArrayList<EvacCell>();
			childTupel = new HashMap<EvacCell, SmoothingTupel>();
			for(EvacCell p : parentList ) {
				for( EvacCell c : getNeighbours( p ) ) {
					if( !(c instanceof ExitCell) && !(newSP.contains( c )) ) {
						//check if there already exists a tuple for this cell
						if( childTupel.containsKey( c ) ) {
							childTupel.get( c ).addParent( newSP.getPotentialDouble( p ), calculateDistance( p, c ) );
							childTupel.get( c ).addDistanceParent( newSP.getDistance( p ), calculateRealDistance( p, c ) );
						} else {
							childTupel.put( c, new SmoothingTupel( c, calculateDistance( p, c ) + newSP.getPotentialDouble( p ), calculateRealDistance( p, c ) + newSP.getDistance( p ), 1, newSP.getPotentialDouble( p ) ) );
							
						}
					}
				}
			}
			for( SmoothingTupel sT : childTupel.values() ) {
				sT.applySmoothing();
				newSP.setPotential( sT.getCell(), sT.getValue() );
				newSP.setDistance( sT.getCell(), sT.getDistanceValue() );
				childList.add( sT.getCell() );
			}
			parentList = childList;
		}
		return newSP;
	}
	
	/**
	 * Calculates nearly the physical distance between two neighbour cells
	 * @param c one neighbour
	 * @param n the other neighbour
	 * @return 10 if the two cells are horizontal or vertical neighbours, 14 else
	 */
	public int calculateDistance(EvacCell c, EvacCell n){
		if( (c.getX() == n.getX()) || (c.getY() == n.getY()) || (c instanceof DoorCell && n instanceof DoorCell) ) {
			return 10;
		} else {
			return 14;
		}
	}

	/**
	 * Calculates nearly the physical distance between two neighbour cells
	 * @param c one neighbour
	 * @param n the other neighbour
	 * @return 10 if the two cells are horizontal or vertical neighbours, 14 else
	 */
	public double calculateRealDistance(EvacCell c, EvacCell n){
		if( (c.getX() == n.getX()) || (c.getY() == n.getY()) || (c instanceof DoorCell && n instanceof DoorCell) ) {
			return 0.4;
		} else {
			return Math.sqrt(2) * 0.4;
		}
	}
	
	/**
	 * Returns a random StaticPotential
	 * @return random StaticPotential
	 */
	public StaticPotential getRandomStaticPotential() {
		GeneralRandom rnd = RandomUtils.getInstance().getRandomGenerator();
		return pm.getStaticPotential( rnd.nextInt( pm.getStaticPotentials().size() ) );
	}

	/**
	 * Returns a StaticPotential which contains the lowest potential for the specified cell. If this cell is not in any staticPotetial
	 * null is returned.
	 * @param c EvacCell for which the lowest potential is searched
	 * @return StaticPotential that provides the fastest way out or null, if this cell is not mapped to any static potential
	 */
	public StaticPotential getNearestExitStaticPotential(EvacCell c){
		StaticPotential nearestPot = new StaticPotential();
		int distance = Integer.MAX_VALUE;
		int numberOfDisjunctStaticPotentials = 0;
		for(StaticPotential sP : pm.getStaticPotentials()){
			if(sP.getPotential(c) == -1){
				numberOfDisjunctStaticPotentials++;
			} else {
				if(sP.getPotential(c) < distance){
					nearestPot = sP;
					distance = sP.getPotential(c);
				}
			}
		}
		return (numberOfDisjunctStaticPotentials == pm.getStaticPotentials().size() ? null : nearestPot);
	}
	
	public String dynamicPotentialToString(){
	    String graphic = "";
	    for(ds.ca.evac.Room room : getCA().getRooms()){
    	    final int width = room.getWidth();
    	    final int height = room.getHeight();
    	    
	        graphic += "+---";
            for(int i=1; i < width; i++){
                graphic += "----";
            }
            graphic +="+\n";
            
            for(int y=0; y < height; y++){
                for(int x=0; x < width; x++){
                    if(room.getCell(x,y) != null){
                        graphic += "|";
                        int pot = getPm().getDynamicPotential().getPotential(room.getCell(x,y));
                        
                        if(pot < 100){
                            graphic += " ";
                        }                        
                        if(pot < 10){
                            graphic += " ";
                        }
                        
                        graphic += pot;
                    } else {
                        graphic += "|   ";
                    }
                }
                graphic += "|\n";
                graphic += "+---";
                for(int i=1; i < width; i++){
                    graphic += "----";
                }
                graphic += "+\n";
            }
            graphic +="\n\n";
	    }
			return graphic;        
    }

	/**
	 * Returns the neighbors of the cell. Uses the method of {@code EvacCell}.
	 * @param cell A cell in the cellular automaton.
	 * @return The neighbor cells of this cell.
	 */
	public ArrayList<EvacCell> getNeighbours( EvacCell cell ) {
		return cell.getNeighbours();
	}
}