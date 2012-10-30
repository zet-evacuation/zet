/*
 * EvacuationCellularAutomatonFrontToBack.java
 * Created 23.09.2009, 23:49:28
 */

package algo.ca.algorithm.evac;

import algo.ca.util.IndividualDistanceComparator;
import ds.ca.evac.Individual;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The class {@code EvacuationCellularAutomatonFrontToBack} ...
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationCellularAutomatonFrontToBack extends algo.ca.framework.EvacuationCellularAutomatonAlgorithm {

	/**
	 * Sorts the (living and not save) individuals within the cellular automaton
	 * by increasing distance to the exit and returns a list of this individuals.
	 * @return the ordered list of individuals
	 */
	@Override
	public List<Individual> getIndividuals() {
		List<Individual> copy = new ArrayList<>( getProblem().eca.getIndividuals() );
		IndividualDistanceComparator<Individual> idc = new IndividualDistanceComparator<>();
		Collections.sort( copy, idc );
		return Collections.unmodifiableList( copy );
	}

	/**
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "EvacuationCellularAutomatonFrontToBack";
	}
}
