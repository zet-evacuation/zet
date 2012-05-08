/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.input;

import de.tu_berlin.math.coga.batch.Computation;
import javax.swing.ImageIcon;

/**
 *
 * @author gross
 */
public class ComputationNode extends BatchTreeTableNode<Computation> {

    public ComputationNode(Computation computation) {
        super(computation, new String[0], new ImageIcon("./icons/cube_24.png"));
        add(new InputListNode(computation.getInput()));
        add(new AlgorithmListNode(computation.getAlgorithms()));
    }

    public Computation getComputation() {
        return getUserObject();
    }

    @Override
    public boolean isEditable(int column) {
        return column == 0;
    }

    @Override
    public void setValueAt(Object aValue, int column) {
        if (column == 0) {
            getComputation().setTitle(aValue.toString());
        }
    }   
    
    @Override
    public String toString() {
        return getComputation().getTitle();
    }    
}
