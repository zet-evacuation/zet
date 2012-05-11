/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.action;

import de.tu_berlin.math.coga.batch.gui.JBatch;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import java.awt.event.ActionEvent;

/**
 *
 * @author gross
 */
public class AddAlgorithmAction extends BatchAction {

    private Class<? extends Algorithm> algorithm;
    
    public AddAlgorithmAction(JBatch batch, Class<? extends Algorithm> algorithm) {
        super(batch, "Tjandra (Optimized)", "algo_24.png");
        setEnabled(false);
        this.algorithm = algorithm;
    }   
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        batch.addAlgorithm(algorithm);
    }    
}
