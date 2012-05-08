/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.input;

import de.tu_berlin.math.coga.batch.Computation;
import de.tu_berlin.math.coga.batch.ComputationList;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

/**
 *
 * @author gross
 */
public class ComputationListNode extends DefaultMutableTreeTableNode {

    private ComputationList computations;
    
    public ComputationListNode(ComputationList computations) {
        this.computations = computations;
        for (Computation computation : computations) {
            add(new ComputationNode(computation));
        }
    }

    public ComputationList getComputations() {
        return computations;
    }

    public void setComputations(ComputationList computations) {
        this.computations = computations;
    }    
}