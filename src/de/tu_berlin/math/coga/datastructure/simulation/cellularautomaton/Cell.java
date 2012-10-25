/**
 * Cell.java
 * Created: 25.10.2012, 14:05:05
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.tu_berlin.math.coga.datastructure.simulation.cellularautomaton;

import java.util.Collection;
import java.util.Iterator;

/**
 * @param <E>
 * @param <S> state of the cell (probably an enum)
 * @param <D> direction
 * @author Jan-Philipp Kappmeier
 */
//public interface Cell<S,D> extends Iterable<Cell<S,D>> {
public interface Cell<E extends Cell<E,S,D>,S,D> extends Iterable<E> {
	/**
	 * Returns the neighbors of the cell.
	 * @return the neighbor cells
	 */
	public Collection<E> getDirectNeighbors();

	/**
	 * Returns the number of sides of the cell. Typically, a two dimensional cell
	 * is a triangle, a square or a hexagon.
	 * @return the number of sides of the cell
	 */
	public int getSides();

	public E getNeighbor( D dir );

}
