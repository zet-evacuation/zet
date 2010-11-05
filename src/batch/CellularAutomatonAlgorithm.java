/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/**
 * Class CellularAutomatonAlgorithm
 * Created 07.07.2008, 01:29:17
 */
package batch;

import algo.ca.CellularAutomatonBackToFrontExecution;
import algo.ca.CellularAutomatonFrontToBackExecution;
import algo.ca.CellularAutomatonInOrderExecution;
import algo.ca.CellularAutomatonRandomOrderExecution;
import algo.ca.EvacuationCellularAutomatonAlgorithm;
import algo.ca.SwapCellularAutomaton;
import ds.ca.CellularAutomaton;
import de.tu_berlin.math.coga.common.localization.Localization;

/**
 * Some cellular automaton simulation algorithms. Creates the algorithm objects
 * and supports displaying in gui elements.
 * @author Jan-Philipp Kappmeier
 */
public enum CellularAutomatonAlgorithm {
	/** A simulation algorithm where all individuals move in the order of decreasing distances. */
	BackToFront( Localization.getInstance().getString( "batch.caOrder.backToFront" ) ) {
		public EvacuationCellularAutomatonAlgorithm createTask( CellularAutomaton ca ) {
			return new CellularAutomatonBackToFrontExecution( ca );
		}
	},
	/** A simulation algorithm where all individuals move in the order of increasing distances. */
	FrontToBack( Localization.getInstance().getString( "batch.caOrder.frontToBack" ) ) {
		public EvacuationCellularAutomatonAlgorithm createTask( CellularAutomaton ca ) {
			return new CellularAutomatonFrontToBackExecution( ca );
		}
	},
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
		@Override
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
