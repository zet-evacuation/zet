package ds.ca;

import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This abstract class PotentialMap describes the route that the indiviuals take to the exit.
 * For this a HashMap associates for each Cell a potential as int value.
 * It is kept abstract, because there are two special kinds of potentials, such as 
 * StaticPotential and DynamicPotential.
 */
public abstract class PotentialMap {
    
	protected final static int UNKNOWN_POTENTIAL_VALUE = -1;
	protected final static int INVALID_POTENTIAL_VALUE = Integer.MAX_VALUE;
	
	/** A <code>HashMap</code> that assign each Cell a Int value (the potential). */
	protected HashMap<Cell, Double> cellToPotential;
	/** Stores the maximal value of this potential map */
	private double maxPotential = -1;

	/**
	 * The constructor creates a PotentialMap with a new empty HashMap.
	 */
	public PotentialMap () {
			cellToPotential = new HashMap<Cell, Double>();
	}

	/**
	 * Associates the specified potential with the specified Cell in this PotentialMap.
	 * If a Cell is specified that exists already in this PotentialMap the value will be overwritten. Otherwise
	 * a new mapping is created.
	 * @param cell cell which has to be updated or mapped 
	 * @param i potential of the cell
	 */
	public void setPotential( Cell cell, double i ) {
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
	 * Get the potential of a specified Cell.
	 * The method returns -1 if you
	 * try to get the potential of a cell that does not exists.
	 * @param cell A cell which potential you want to know.
	 * @return potential of the specified cell or -1 if the cell is not mapped by this potential
	 */
	public int getPotential( Cell cell ) {
		Double potential = cellToPotential.get( cell );
		if( potential == null ) {
			return -1;
		} else {
			return (int)Math.round( potential ); //potential.intValue();
		}
	}
	
	public double getPotentialDouble( Cell cell ) {
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
	 * Removes the mapping for the specified Cell.
	 * The method throws <code>IllegalArgumentExceptions</code> if you
	 * try to remove the mapping of a Cell that does not exists.
	 * @param cell A Cell that mapping you want to remove.
	 * @throws IllegalArgumentException if the cell is not contained in the map
	 */
	public void deleteCell( Cell cell ) throws IllegalArgumentException {
		if( !(cellToPotential.containsKey( cell )) )
			throw new IllegalArgumentException( "The Cell must be insert previously!" );
		cellToPotential.remove( cell );
	}
    
	/**
	 * Returns true if the mapping for the specified Cell exists.
	 * @param cell A Cell of that you want to know if it exists.
	 * @return true if the cell has a potential value in this map
	 */
	public boolean contains( Cell cell ) {
		if( cellToPotential.containsKey( cell ) ) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * <p>Returns a set of all cell which are mapped by this potential.</p>
	 * <p>It is secured that the elements in the set have the same ordering
	 * using a <code>SortedSet</code>. This is needed due to the fact that the
	 * keys can have different order even if the values are inserted using
	 * default hashcodes. The sorting ensures??? deterministic behaviour</p>
	 * @return set of mapped cells
	 */
	public Set<Cell> getMappedCells() {
		SortedSet<Cell> a = new TreeSet<Cell>();
		for( Cell cell : cellToPotential.keySet() )
			a.add( cell );
		return a;
	}

	public boolean hasValidPotential( Cell cell ) {
		return (getPotential( cell ) != INVALID_POTENTIAL_VALUE) && (getPotential( cell ) != UNKNOWN_POTENTIAL_VALUE);
	}
}

