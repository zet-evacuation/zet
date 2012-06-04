/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.input;

import de.tu_berlin.math.coga.batch.algorithm.AlgorithmList;
import de.tu_berlin.math.coga.batch.input.InputAlgorithm;
import javax.swing.ImageIcon;

/**
 *
 * @author gross
 */
public class AlgorithmListNode extends BatchTreeTableNode<AlgorithmList> {

    public AlgorithmListNode(AlgorithmList data) {
        super(data, new String[0], new ImageIcon("./icons/gear_24.png"));
        for (InputAlgorithm algorithm : data) {
            add(new InputAlgorithmNode(algorithm));
        }      
    }

    @Override
    public String toString() {
        return "Algorithms";
    }   
}
