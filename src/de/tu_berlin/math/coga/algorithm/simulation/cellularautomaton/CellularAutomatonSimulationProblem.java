/**
 * CellularAutomatonSimulationProblem.java
 * Created: 10.02.2012, 17:36:04
 */
package de.tu_berlin.math.coga.algorithm.simulation.cellularautomaton;

import de.tu_berlin.math.coga.datastructure.simulation.cellularautomaton.Cell;
import de.tu_berlin.math.coga.datastructure.simulation.cellularautomaton.CellularAutomaton;

/**
 *
 * @param <Ce> the cell type of the cellular automaton
 * @author Jan-Philipp Kappmeier
 */
public class CellularAutomatonSimulationProblem<Ce extends Cell<Ce,?>> {
	CellularAutomaton<Ce,?> ca;

	public CellularAutomatonSimulationProblem( CellularAutomaton<Ce,?> ca ) {
		this.ca = ca;
	}

	public CellularAutomaton<Ce, ?> getCa() {
		return ca;
	}


}
