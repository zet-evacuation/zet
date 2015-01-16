/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

import org.zetool.common.algorithm.Algorithm;

/**
 *
 * @author gross
 */
public class InputAlgorithm {
    
    private Class<? extends Algorithm> algorithm;
    private String title;

    public InputAlgorithm(Class<? extends Algorithm> algorithm, String title) {
        this.algorithm = algorithm;
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
