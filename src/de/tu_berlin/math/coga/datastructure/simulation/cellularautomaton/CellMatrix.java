/**
 * CellMatrix.java
 * Created: 25.10.2012, 15:50:36
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.tu_berlin.math.coga.datastructure.simulation.cellularautomaton;

import ds.ca.evac.EvacCell;
import java.util.Collection;

/**
 * <E> the cell type
 * @param <E>
 * @author Jan-Philipp Kappmeier
 */
public interface CellMatrix<E extends Cell<?,?>> {

	public int getWidth();

	public int getHeight();

	public Collection<E> getAllCells();

	public E getCell( int x, int y );

}
