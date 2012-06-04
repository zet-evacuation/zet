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
 * Class CellularAutomatonAlgorithmEnumeration
 * Created 07.07.2008, 01:29:17
 */
package zet.tasks;

import algo.ca.algorithm.evac.EvacuationCellularAutomatonBackToFront;
import algo.ca.algorithm.evac.EvacuationCellularAutomatonFrontToBack;
import algo.ca.algorithm.evac.EvacuationCellularAutomatonInOrder;
import algo.ca.algorithm.evac.EvacuationCellularAutomatonRandom;
import algo.ca.algorithm.evac.EvacuationCellularAutomatonAlgorithm;
import algo.ca.algorithm.evac.SwapCellularAutomaton;
import de.tu_berlin.math.coga.common.localization.DefaultLoc;

/**
 * Some cellular automaton simulation algorithms. Creates the algorithm objects
 * and supports displaying in gui elements.
 * @author Jan-Philipp Kappmeier
 */
public enum CellularAutomatonAlgorithmEnumeration {
	/** A simulation algorithm where all individuals move in the order of decreasing distances. */
	BackToFront( DefaultLoc.getSingleton().getString( "batch.caOrder.backToFront" ), new EvacuationCellularAutomatonBackToFront() ),
	/** A simulation algorithm where all individuals move in the order of increasing distances. */
	FrontToBack( DefaultLoc.getSingleton().getString( "batch.caOrder.frontToBack" ), new EvacuationCellularAutomatonFrontToBack() ),
	/** A simulation algorithm where all individuals are simulated in a random order in each step. */
	RandomOrder( DefaultLoc.getSingleton().getString( "batch.caOrder.random" ), new EvacuationCellularAutomatonRandom( ) ),
	/** A simulation algorithm where all individuals are simulated in a random order and where two individuals can swap position. */
	Swap( DefaultLoc.getSingleton().getString( "batch.caOrder.swap" ),new SwapCellularAutomaton( ) ),
	/** A simulation algorithm where all individuals are simulated in the same order in each step. */
	InOrder( DefaultLoc.getSingleton().getString( "batch.caOrder.unifom" ),new EvacuationCellularAutomatonInOrder( ) ) {};
	private String name;
	private EvacuationCellularAutomatonAlgorithm eca;
	/**
	 * Creates a new cellular automaton algorithm instance.
	 * @param name
	 */
	CellularAutomatonAlgorithmEnumeration( String name, EvacuationCellularAutomatonAlgorithm eca ) {
		this.name = name;
		this.eca = eca;
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

	public EvacuationCellularAutomatonAlgorithm getAlgorithm() {
		return eca;
	}
}