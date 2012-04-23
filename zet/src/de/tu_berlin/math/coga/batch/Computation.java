/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch;

import de.tu_berlin.math.coga.batch.input.InputList;
import de.tu_berlin.math.coga.batch.input.ProblemType;

/**
 *
 * @author gross
 */
public class Computation {
    
    private InputList input;
    private ProblemType type;

    public Computation(ProblemType problemType) {
        this.type = problemType;
        this.input = new InputList(this);
    }

    public InputList getInput() {
        return input;
    }

    public ProblemType getType() {
        return type;
    }

    public void setType(ProblemType type) {
        this.type = type;
    }

}
