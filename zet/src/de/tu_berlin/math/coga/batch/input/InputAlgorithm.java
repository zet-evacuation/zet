/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;

/**
 *
 * @author gross
 */
public class InputAlgorithm {
    
    private Class<? extends Algorithm> algorithm;

    public InputAlgorithm(Class<? extends Algorithm> algorithm) {
        this.algorithm = algorithm;
    }
}
