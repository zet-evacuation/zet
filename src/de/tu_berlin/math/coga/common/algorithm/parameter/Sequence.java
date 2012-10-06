/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.common.algorithm.parameter;

/**
 * An extension of the <code>Iterable</code> interface that supports 
 * <code>size()</code> queries.
 * 
 * @param <T> the type of element contained in the sequence.
 * @author Martin Groß
 */
public interface Sequence<T> extends Iterable<T> {
    
    /**
     * Returns the number of elements in the sequence.
     * @return the number of elements in the sequence. 
     */
    int size();
}
