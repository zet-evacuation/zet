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

import java.util.Collection;
import java.util.HashMap;

/**
 * The PotentialManager manages a List of all StaticPotentials an one DynamicPotential.
 */
public class PotentialManager {

	/** A {@code TreeMap} of all StaticPotentials. */
	private HashMap<Integer, StaticPotential> staticPotentials;
	/** The safe potential*/
	private StaticPotential safePotential;
	/** The single DynamicPotential. */
	private DynamicPotential dynamicPotential;

	/**
	 * Creates a new PotentialManager.
	 */
	public PotentialManager() {
		staticPotentials = new HashMap<>();
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
	 * The method throws {@code IllegalArgumentException} if the StaticPtential already exists.
	 * @param potential The StaticPotential you want to add to the List.
	 * @throws IllegalArgumentException if the {@code StaticPotential} already exists
	 */
	public void addStaticPotential( StaticPotential potential ) throws IllegalArgumentException {
		if( staticPotentials.containsKey( potential.getID() ) )
			throw new IllegalArgumentException( "The StaticPtential already exists!" );
		Integer i = potential.getID();
		staticPotentials.put( i, potential );
	}

	/**
	 * Get the StaticPotential with the specified ID.
	 * The method throws {@code IllegalArgumentException} if the specified ID not exists.
	 * @param id
	 * @return The StaticPotential
	 * @throws IllegalArgumentException
	 */
	public StaticPotential getStaticPotential( int id ) throws IllegalArgumentException {
		if( !(staticPotentials.containsKey( id )) )
			throw new IllegalArgumentException( "No StaticPotential with this ID exists!" );
		return staticPotentials.get( id );
	}

	/**
	 * Set the dynamicPotential.
	 * @param potential The DynamicPotential you want to set.
	 */
	public void setDynamicPotential( DynamicPotential potential ) {
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

	public void setsafePotential( StaticPotential potential ) {
		safePotential = potential;
	}

	public int getMaxStaticPotential() {
		int maxPotential = 0;
		for( StaticPotential staticPotential : staticPotentials.values() )
			maxPotential = Math.max( maxPotential, staticPotential.getMaxPotential() );
		return maxPotential;
	}

	public Collection<Integer> getStaticPotentialIDs() {
		return staticPotentials.keySet();
	}
}
