/**
 * GeneralCellularAutomatonSimulationAlgorithm.java
 * Created: 25.10.2012, 17:12:16
 */
package de.tu_berlin.math.coga.algorithm.simulation.cellularautomaton;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.datastructure.simulation.cellularautomaton.Cell;
import java.util.Iterator;


/**
 * @param <S> the simulation class
 * @param <Ce>
 * @param <St>
 * @param <Nb>
 * @author Jan-Philipp Kappmeier
 */
//public class GeneralCellularAutomatonSimulationAlgorithm<Ce extends Cell<Ce,St,?>,St,Nb extends Neighborhood<Ce>,T> extends Algorithm<CellularAutomatonSimulationProblem<Ce,St,Nb>,T> {
public abstract class GeneralCellularAutomatonSimulationAlgorithm<Ce extends Cell<Ce,St,?>,St,S extends CellularAutomatonSimulationProblem<Ce,St,?>, T> extends Algorithm<S,T> {
	protected abstract void initialize();

	protected abstract T performSimulation();

	protected abstract Iterator<Ce> iterateCells();

	@Override
	final protected T runAlgorithm( S problem ) {
		initialize();
		return performSimulation();
	}
}
