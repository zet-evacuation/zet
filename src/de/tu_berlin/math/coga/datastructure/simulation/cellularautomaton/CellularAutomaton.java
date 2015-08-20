/**
 * CellularAutomaton.java
 * Created: 25.10.2012, 14:03:39
 */

package de.tu_berlin.math.coga.datastructure.simulation.cellularautomaton;

/**
 *
 * @param <Ce> the cell type
 * @param <St> the state in the cells
 * @author Jan-Philipp Kappmeier
 */
public interface CellularAutomaton<Ce extends Cell<Ce,St>,St> {
	/**
	 * Retruns the dimension of the cellular automaton. Typically the dimension of
	 * a cellular automton is 1 or 2.
	 * @return the dimension of the cellular automaton
	 */
	public int getDimension();
}
