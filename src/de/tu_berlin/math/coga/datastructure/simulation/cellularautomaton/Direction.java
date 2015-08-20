/**
 * Direction.java
 * Created: 25.10.2012, 14:14:08
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.tu_berlin.math.coga.datastructure.simulation.cellularautomaton;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface Direction<T> {
	T getClockwise();
	T getCounterClockwise();
}
