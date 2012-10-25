/**
 * CellularAutomatonSimulationProblem.java
 * Created: 10.02.2012, 17:36:04
 */
package de.tu_berlin.math.coga.algorithm.simulation.cellularautomaton;

import de.tu_berlin.math.coga.datastructure.simulation.cellularautomaton.Cell;
import de.tu_berlin.math.coga.datastructure.simulation.cellularautomaton.CellularAutomaton;
import de.tu_berlin.math.coga.datastructure.simulation.cellularautomaton.Neighborhood;

/**
 *
 * @param <Ce>
 * @param <St>
 * @param <Nb>
 * @author Jan-Philipp Kappmeier
 */
public class CellularAutomatonSimulationProblem<Ce extends Cell<Ce,St,?>,St,Nb extends Neighborhood<Ce>> {
	CellularAutomaton<Ce,St,Nb> ca;

	public CellularAutomatonSimulationProblem( CellularAutomaton<Ce,St,Nb> ca ) {
		this.ca = ca;
	}

	public CellularAutomaton<Ce, St, Nb> getCa() {
		return ca;
	}


}
