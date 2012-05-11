/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.input;

import de.tu_berlin.math.coga.batch.algorithm.AlgorithmList;
import de.tu_berlin.math.coga.batch.input.InputAlgorithm;
import de.tu_berlin.math.coga.batch.input.InputFile;
import java.util.HashMap;
import javax.swing.ImageIcon;

/**
 *
 * @author gross
 */
public class AlgorithmListNode extends BatchTreeTableNode<AlgorithmList> {

    public AlgorithmListNode(AlgorithmList data) {
        super(data, new String[0], new ImageIcon("./icons/gear_24.png"));
        //typeNodes = new HashMap<>();
        for (InputAlgorithm algorithm : data) {
            /*
            if (!typeNodes.containsKey(file.getProblemType())) {
                InputTypeNode node = new InputTypeNode(file.getProblemType(), file.getPropertyNames());
                add(node);
                typeNodes.put(file.getProblemType(), node);
            } */
            //InputTypeNode node = typeNodes.get(file.getProblemType());
            add(new InputAlgorithmNode(algorithm));
        }
        //sort();        
    }

    @Override
    public String toString() {
        return "Algorithms";
    }   
}
