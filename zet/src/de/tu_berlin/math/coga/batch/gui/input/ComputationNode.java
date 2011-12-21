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
        super(computation, new String[0], new ImageIcon("./icons/algo_24.png"));
    }    
}
