/**
 * SquareCellularAutomaton.java
 * Created: 25.10.2012, 15:00:09
 */
package de.tu_berlin.math.coga.datastructure.simulation.cellularautomaton;

import de.tu_berlin.math.coga.datastructure.simulation.cellularautomaton.CellularAutomaton;
import de.tu_berlin.math.coga.datastructure.simulation.cellularautomaton.MooreNeighborhoodSquare;
import de.tu_berlin.math.coga.datastructure.simulation.cellularautomaton.SquareCell;


/**
 * @param <Ce> cell type
 * @param <St> status type
 * @author Jan-Philipp Kappmeier
 */
public abstract class SquareCellularAutomaton<Ce extends SquareCell<Ce,St>,St> implements CellularAutomaton<Ce, St, MooreNeighborhoodSquare<Ce>> {
	protected MooreNeighborhoodSquare<Ce> neighborhood;

	public SquareCellularAutomaton() {
		neighborhood = new MooreNeighborhoodSquare<>();
	}

	@Override
	public int getDimension() {
		return 2;
	}
}
