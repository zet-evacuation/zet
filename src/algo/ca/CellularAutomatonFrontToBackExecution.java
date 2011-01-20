/*
 * CellularAutomatonFrontToBackExecution.java
 * Created 23.09.2009, 23:49:28
 */

package algo.ca;

import algo.ca.util.IndividualDistanceComparator;
import ds.ca.CellularAutomaton;
import ds.ca.Individual;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The class {@code CellularAutomatonFrontToBackExecution} ...
 * @author Jan-Philipp Kappmeier
 */
public class CellularAutomatonFrontToBackExecution extends CellularAutomatonInOrderExecution {

	/**
	 * Creates a new instance of {@code CellularAutomatonFrontToBackExecution}.
	 * @param ca the cellular automaton that is executed
	 */
	public CellularAutomatonFrontToBackExecution( CellularAutomaton ca ) {
		super( ca );
	}

	/**
	 * Sorts the (living and not save) individuals within the cellular automaton
	 * by increasing distance to the exit and returns a list of this individuals.
	 * @return the ordered list of individuals
	 */
	@Override
	public List<Individual> getIndividuals() {
		List<Individual> copy = new ArrayList<Individual>( super.getIndividuals() );
		IndividualDistanceComparator<Individual> idc = new IndividualDistanceComparator<Individual>();
		Collections.sort( copy, idc );
		return Collections.unmodifiableList( copy );
	}

	/**
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "CellularAutomatonFrontToBackExecution";
	}
}
