package ds.ca;

import java.util.Collection;
import java.util.HashMap;

/**
 * The PotentialManager manages a List of all StaticPotentials an one DynamicPotential.
 */
public class PotentialManager {

	/** A <code>TreeMap</code> of all StaticPotentials. */
	private HashMap<Integer, StaticPotential> staticPotentials;
	/** The safepotential*/
	private StaticPotential safePotential;
	/** The single DynamicPotential. */
	private DynamicPotential dynamicPotential;

	/**
	 * Creates a new PotentialManager.
	 */
	public PotentialManager() {
		staticPotentials = new HashMap<Integer, StaticPotential>();
		dynamicPotential = new DynamicPotential();
		safePotential = new StaticPotential();
	}

    /**
     * Get a Collection of all staticPotentials.
     * @return The Collection of all staticPotentials
     */
    public Collection<StaticPotential> getStaticPotentials() {
        return staticPotentials.values();
    }
    
	/**
	 * Adds the StaticPotential into the List of staticPotentials.
	 * The method throws <code>IllegalArgumentException</code> if the StaticPtential already exists.
	 * @param potential The StaticPotential you want to add to the List.
	 * @throws IllegalArgumentException if the <code>StaticPotential</code> already exists
	 */
	public void addStaticPotential( StaticPotential potential ) throws IllegalArgumentException {
		if( staticPotentials.containsKey( potential.getID() ) ) {
			throw new IllegalArgumentException( "The StaticPtential already exists!" );
		}
		Integer i = new Integer( potential.getID() );
		staticPotentials.put( i, potential );
	}

    /**
     * Get the StaticPotential with the specified ID.
     * The method throws <code>IllegalArgumentException</code> if the specified ID not exists.
		 * @param id 
		 * @return The StaticPotential
		 * @throws IllegalArgumentException 
     */
    public StaticPotential getStaticPotential(int id) throws IllegalArgumentException{
			if( !(staticPotentials.containsKey( id )) )
				throw new IllegalArgumentException( "No StaticPotential with this ID exists!" );
			return staticPotentials.get(id);
    }
    
    /**
     * Set the dynamicPotential.
     * @param potential The DynamicPotential you want to set.
     */ 
    public void setDynamicPotential(DynamicPotential potential) {
        dynamicPotential = potential;
    }
    
    /**
     * Get the dynamicPotential. Returns null if the DynamicPotential not exists.
     * @return The DynamicPotential
     */
    public DynamicPotential getDynamicPotential() {
        return dynamicPotential;
    }

    public StaticPotential getSafePotential() {
        return safePotential;
    }

    public void setsafePotential(StaticPotential potential) {
        safePotential = potential;
    }
    
	public int getMaxStaticPotential() {
		int maxPotential = 0;
		for( StaticPotential staticPotential : staticPotentials.values() )
			maxPotential = Math.max( maxPotential, staticPotential.getMaxPotential() );
		return maxPotential;
	}

	public Collection<Integer> getStaticPotentialIDs(){
		return staticPotentials.keySet();
	}
}

