/*
 * EvacuationCellularAutomatonBackToFront.java
 * Created 23.09.2009, 23:49:47
 */

package algo.ca.algorithm.evac;

import algo.ca.algorithm.evac.EvacuationCellularAutomatonInOrder;
import algo.ca.util.IndividualDistanceComparator;
import ds.ca.evac.Individual;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The class {@code EvacuationCellularAutomatonBackToFront} changes the
 * execution order of the individuals using an overwritten method
 * {@link #getIndividuals()}. The individuals are sorted in the reverse order of
 * their distance (by means of their currently selected potential field).
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationCellularAutomatonBackToFront extends EvacuationCellularAutomatonInOrder {

	/**
	 * Creates a new instance of {@code EvacuationCellularAutomatonBackToFront}.
	 * @param ca the cellular automaton that is executed
	 */
	public EvacuationCellularAutomatonBackToFront( ) {
	}

	/**
	 * Sorts the (living and not save) individuals within the cellular automaton
	 * by decreasing distance to the exit and returns a list of this individuals.
	 * @return the ordered list of individuals
	 */
	@Override
	public List<Individual> getIndividuals() {
		List<Individual> copy = new ArrayList<>( super.getIndividuals() );
		IndividualDistanceComparator<Individual> idc = new IndividualDistanceComparator<>();
		Collections.sort( copy, idc );
		Collections.reverse( copy );
		return Collections.unmodifiableList( copy );
	}

	/**
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "CellularAutomatonBackToFrontExecution";
	}
}