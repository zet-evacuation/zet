/**
 * Class EvacuationOptimizationType
 * Erstellt 23.11.2008, 21:39:42
 */

package gui.batch;

/**
 * The different types of evacuation optimization supported by zet. If no
 * optimization is used, <code>None</code> should be used.
 * @author Jan-Philipp Kappmeier
 */
public enum EvacuationOptimizationType {
	/** Indicates that no evacuation optimization is performed. */
	None( "Keine" ),
	/** Personal optimization for each individual is performed (using earliest arrival transshipments). */
	PersonalEvacuationPlan( "Persönliche Fluchtpläne" ),
	/** Exit distribution using earliest arrival transshipments is calculated. */
	EarliestArrivalTransshipment( "Earliest Arrival Transhhipment" ),
	/** Exit distribution using minimum cost flow algorithm is calculated. */
        EAT_REDUCED( "Reduzierter EAT" ),
        EAT_SPG( "SPG-EAT" ),
	MinCost( "Min Cost" ),
	/** Exit distribution using shortest paths is calculated. */
	ShortestPaths( "Kürzeste Wege" ),
        /** Estimated Exit capacity by Max-Flow */
        BestResponse( "Best Response Dynamics" );

	/** The name (used for displaying in selection boxes). */
	private String name;
	
	/**
	 * Creates an enumeration instance and sets the name
	 * @param name the name
	 */
	private EvacuationOptimizationType( String name ) {
		this.name = name;
	}

	/**
	 * Returns the name of the optimization type. Used for displaying in selection boxes.
	 * @return the name of the optimization type
	 */
	public String getName() {
		return name;
	}

	/**
	 * The string representation. The same as the name.
	 * @return the string representation.
	 * @see{#.getName()}
	 */
	@Override
	public String toString() {
		return getName();
	}
}
