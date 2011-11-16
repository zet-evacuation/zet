/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.actions;

import de.tu_berlin.math.coga.batch.gui.JBatch;
import java.awt.event.ActionEvent;

/**
 *
 * @author gross
 */
public class ChooseAlgorithmAction extends BatchAction {

    public ChooseAlgorithmAction(JBatch batch) {
        super(batch, "Choose algorithm", "algo_24.png");
    }   
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        throw new UnsupportedOperationException("Not supported yet.");
    }    
}
