/**
 * MooreNeighborhoodSquare.java
 * Created: 25.10.2012, 14:55:07
 */
package de.tu_berlin.math.coga.datastructure.simulation.cellularautomaton;

import java.util.Collection;


/**
 *
 * @param <Ce>
 * @author Jan-Philipp Kappmeier
 */
public class MooreNeighborhoodSquare<Ce extends SquareCell<Ce,?>> implements Neighborhood<Ce> {

	@Override
	public Collection<Ce> getNeighbors( Ce cell ) {
		return cell.getDirectNeighbors();
	}
}
