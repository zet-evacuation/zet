/**
 * EvacuationCellularAutomatonAlgorithm.java
 * Created: 25.10.2012, 17:26:13
 */
package algo.ca.framework;

import algo.ca.framework.EvacuationCellState;
import algo.ca.algorithm.evac.EvacuationSimulationProblem;
import algo.ca.algorithm.evac.EvacuationSimulationResult;
import de.tu_berlin.math.coga.algorithm.simulation.cellularautomaton.GeneralCellularAutomatonSimulationAlgorithm;
import ds.ca.evac.EvacCell;
import java.util.Iterator;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationCellularAutomatonAlgorithm extends GeneralCellularAutomatonSimulationAlgorithm<EvacCell, EvacuationCellState, EvacuationSimulationProblem, EvacuationSimulationResult> {

	@Override
	protected void initialize() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	protected EvacuationSimulationResult performSimulation() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	protected Iterator<EvacCell> iterateCells() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}


}
