/**
 * Class CAAlgo
 * Erstellt 07.07.2008, 01:29:17
 */
package batch;

import algo.ca.CellularAutomatonInOrderExecution;
import algo.ca.CellularAutomatonRandomOrderExecution;
import algo.ca.EvacuationCellularAutomatonAlgorithm;
import algo.ca.SwapCellularAutomaton;
import ds.ca.CellularAutomaton;
import localization.Localization;

/**
 * Some cellular automaton simulation algorithms. Creates the algorithm objects
 * and supports displaying in gui elements.
 * @author Jan-Philipp Kappmeier
 */
public enum CellularAutomatonAlgorithm {
	/** A simulation algorithm where all individuals are simulated in a random order in each step. */
	RandomOrder( Localization.getInstance().getString( "batch.caOrder.random" ) ) {
		public EvacuationCellularAutomatonAlgorithm createTask( CellularAutomaton ca ) {
			return new CellularAutomatonRandomOrderExecution( ca );
		}
	},
	/** A simulation algorithm where all individuals are simulated in a random order and where two individuals can swap position. */
	Swap( Localization.getInstance().getString( "batch.caOrder.swap" ) ) {
		public EvacuationCellularAutomatonAlgorithm createTask( CellularAutomaton ca ) {
			return new SwapCellularAutomaton( ca );
		}
	},
	/** A simulation algorithm where all individuals are simulated in the same order in each step. */
	InOrder( Localization.getInstance().getString( "batch.caOrder.unifom" ) ) {
		public EvacuationCellularAutomatonAlgorithm createTask( CellularAutomaton ca ) {
			return new CellularAutomatonInOrderExecution( ca );
		}
	};
	private String name;

	/**
	 * Creates a new cellular automaton algorithm instance.
	 * @param name
	 */
	CellularAutomatonAlgorithm( String name ) {
		this.name = name;
	}

	/**
	 * Returns the name of the algorithm. This is used to display it on gui elements.
	 * @return the name of the algorithm
	 */
	public String getName() {
		return name;
	}

	/**
	 * The string representation of the algorithms. This is the same as the name.
	 * @return the string representation of the algorithm.
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Creates a new instance of an cellular automaton algorithm.
	 * @param ca the cellular automaton that is used by the simulation algorithm
	 * @return a new instance of an cellular automaton algorithm
	 */
	public abstract EvacuationCellularAutomatonAlgorithm createTask( CellularAutomaton ca );
}
