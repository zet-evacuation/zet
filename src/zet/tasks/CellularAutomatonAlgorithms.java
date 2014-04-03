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
 * Class CellularAutomatonAlgorithms Created 07.07.2008, 01:29:17
 */
package zet.tasks;

import algo.ca.algorithm.evac.EvacuationCellularAutomatonBackToFront;
import algo.ca.algorithm.evac.EvacuationCellularAutomatonFrontToBack;
import algo.ca.algorithm.evac.EvacuationCellularAutomatonInOrder;
import algo.ca.algorithm.evac.EvacuationCellularAutomatonRandom;
import algo.ca.algorithm.evac.SwapCellularAutomaton;
import algo.ca.framework.EvacuationCellularAutomatonAlgorithm;
import de.tu_berlin.math.coga.zet.ZETLocalization;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterContainer;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.z.ConcreteAssignment;
import io.visualization.CAVisualizationResults;

/**
 * Some cellular automaton simulation algorithms. Creates the algorithm objects
 * and supports displaying in gui elements.
 * @author Jan-Philipp Kappmeier
 */
public enum CellularAutomatonAlgorithms {
	/**
	 * A simulation algorithm where all individuals move in the order of
	 * decreasing distances.
	 */
	BackToFront( ZETLocalization.getSingleton().getString( "batch.caOrder.backToFront" ) ) {
		@Override
		public EvacuationCellularAutomatonAlgorithm getAlgorithm() {
			return new EvacuationCellularAutomatonBackToFront();
		}
	},
	/**
	 * A simulation algorithm where all individuals move in the order of
	 * increasing distances.
	 */
	FrontToBack( ZETLocalization.getSingleton().getString( "batch.caOrder.frontToBack" ) ) {
		@Override
		public EvacuationCellularAutomatonAlgorithm getAlgorithm() {
			return new EvacuationCellularAutomatonFrontToBack();
		}
	},
	/**
	 * A simulation algorithm where all individuals are simulated in a random order
	 * in each step.
	 */
	RandomOrder( ZETLocalization.getSingleton().getString( "batch.caOrder.random" ) ) {
		@Override
		public EvacuationCellularAutomatonAlgorithm getAlgorithm() {
			return new EvacuationCellularAutomatonRandom();
		}
	},
	/**
	 * A simulation algorithm where all individuals are simulated in a random order
	 * and where two individuals can swap position.
	 */
	Swap( ZETLocalization.getSingleton().getString( "batch.caOrder.swap" ) ) {
		@Override
		public EvacuationCellularAutomatonAlgorithm getAlgorithm() {
			return new SwapCellularAutomaton();
		}
	},
	/**
	 * A simulation algorithm where all individuals are simulated in the same order
	 * in each step.
	 */
	InOrder( ZETLocalization.getSingleton().getString( "batch.caOrder.unifom" ) ) {
		@Override
		public EvacuationCellularAutomatonAlgorithm getAlgorithm() {
			return new EvacuationCellularAutomatonInOrder();
		}
	};
	private String name;

	/**
	 * Creates a new cellular automaton algorithm instance.
	 * @param name
	 */
	CellularAutomatonAlgorithms( String name ) {
		this.name = name;
	}

	/**
	 * Returns the name of the algorithm. This is used to display it on gui
	 * elements.
	 * @return the name of the algorithm
	 */
	public String getName() {
		return name;
	}

	public abstract EvacuationCellularAutomatonAlgorithm getAlgorithm();
}