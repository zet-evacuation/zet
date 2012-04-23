/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input.reader;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;

/**
 *
 * @author gross
 */
public abstract class InputReader<S,T> extends Algorithm<S,T> {
    
    public abstract String[] getProperties();    
}
