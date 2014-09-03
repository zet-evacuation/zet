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

import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This abstract class PotentialMap describes the route that the indiviuals take to the exit.
 * For this a HashMap associates for each EvacCell a potential as int value.
 * It is kept abstract, because there are two special kinds of potentials, such as 
 * StaticPotential and DynamicPotential.
 */
public abstract class PotentialMap {
    
	protected final static int UNKNOWN_POTENTIAL_VALUE = -1;
	protected final static int INVALID_POTENTIAL_VALUE = Integer.MAX_VALUE;
	
	/** A {@code HashMap} that assign each EvacCell a Int value (the potential). */
	protected HashMap<EvacCell, Double> cellToPotential;
	/** Stores the maximal value of this potential map */
	private double maxPotential = -1;

	/**
	 * The constructor creates a PotentialMap with a new empty HashMap.
	 */
	public PotentialMap () {
			cellToPotential = new HashMap<EvacCell, Double>();
	}

	/**
	 * Associates the specified potential with the specified EvacCell in this PotentialMap.
	 * If a EvacCell is specified that exists already in this PotentialMap the value will be overwritten. Otherwise
	 * a new mapping is created.
	 * @param cell cell which has to be updated or mapped 
	 * @param i potential of the cell
	 */
	public void setPotential( EvacCell cell, double i ) {
		Double potential = new Double( i );
		if( !cellToPotential.containsKey( cell ) ) {
			cellToPotential.put( cell, potential );
		} else {
			cellToPotential.remove( cell );
			cellToPotential.put( cell, potential );
		}
		maxPotential = Math.max( maxPotential, i );
	}

	/**
	 * Get the potential of a specified EvacCell. The method returns -1 if the method is called to retrieve the potential
   * of a cell that does not exist.
	 * @param cell the cell which potential should be returned
	 * @return potential of the specified cell or -1 if the cell is not mapped by this potential
	 */
	public int getPotential( EvacCell cell ) {
		Double potential = cellToPotential.get( cell );
		if( potential == null ) {
			return -1;
		} else {
			return (int)Math.round( potential ); //potential.intValue();
		}
	}
	
	public double getPotentialDouble( EvacCell cell ) {
		Double potential = cellToPotential.get( cell );
		if( potential == null ) {
			return -1;
		} else {
			return potential;
		}
	}
		
	public int getMaxPotential() {
		Double d = maxPotential;
		return d.intValue();
	}

	/**
	 * Removes the mapping for the specified EvacCell.
	 * The method throws {@code IllegalArgumentExceptions} if you
	 * try to remove the mapping of a EvacCell that does not exists.
	 * @param cell A EvacCell that mapping you want to remove.
	 * @throws IllegalArgumentException if the cell is not contained in the map
	 */
	public void deleteCell( EvacCell cell ) throws IllegalArgumentException {
		if( !(cellToPotential.containsKey( cell )) )
			throw new IllegalArgumentException( "The Cell must be insert previously!" );
		cellToPotential.remove( cell );
	}
    
	/**
	 * Returns true if the mapping for the specified EvacCell exists.
	 * @param cell A EvacCell of that you want to know if it exists.
	 * @return true if the cell has a potential value in this map
	 */
	public boolean contains( EvacCell cell ) {
		if( cellToPotential.containsKey( cell ) ) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * <p>Returns a set of all cell which are mapped by this potential.</p>
	 * <p>It is secured that the elements in the set have the same ordering
	 * using a {@code SortedSet}. This is needed due to the fact that the
	 * keys can have different order even if the values are inserted using
	 * default hashcodes. The sorting ensures??? deterministic behaviour</p>
	 * @return set of mapped cells
	 */
	public Set<EvacCell> getMappedCells() {
		SortedSet<EvacCell> a = new TreeSet<EvacCell>();
		for( EvacCell cell : cellToPotential.keySet() )
			a.add( cell );
		return a;
	}

	public boolean hasValidPotential( EvacCell cell ) {
		return (getPotential( cell ) != INVALID_POTENTIAL_VALUE) && (getPotential( cell ) != UNKNOWN_POTENTIAL_VALUE);
	}
}

