/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

import de.tu_berlin.math.coga.batch.Computation;
import java.util.LinkedList;

/**
 *
 * @author Martin Groß
 */
public class InputList extends LinkedList<InputFile> {

    private Computation computation;
    
    public InputList(Computation computation) {
        this.computation = computation;
    }

    public Computation getComputation() {
        return computation;
    }
    
    public String getText() {
        return computation.getType().getDescription();
    }
}
