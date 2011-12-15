/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch;

import de.tu_berlin.math.coga.batch.input.Input;
import de.tu_berlin.math.coga.batch.input.ProblemType;

/**
 *
 * @author gross
 */
public class Computation {
    
    private Input input;
    private ProblemType type;

    public Computation(ProblemType problemType) {
        this.type = problemType;
        this.input = new Input(this);
    }

    public Input getInput() {
        return input;
    }

    public ProblemType getType() {
        return type;
    }

    public void setType(ProblemType type) {
        this.type = type;
    }

}
