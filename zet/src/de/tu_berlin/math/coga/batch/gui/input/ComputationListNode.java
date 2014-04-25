/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.input;

import de.tu_berlin.math.coga.batch.Computation;
import de.tu_berlin.math.coga.batch.ComputationList;

/**
 *
 * @author gross
 */
public class ComputationListNode extends BatchTreeTableNode<ComputationList> {
    
    public ComputationListNode(ComputationList computations) {
        super(computations, new String[0]);
        for (Computation computation : computations) {
            add(new ComputationNode(computation));
        }
    }

    public ComputationList getComputations() {
        return getUserObject();
    }
}