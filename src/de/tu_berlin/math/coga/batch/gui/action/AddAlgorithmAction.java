/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.action;

import de.tu_berlin.math.coga.batch.gui.JBatch;
import de.tu_berlin.math.coga.batch.gui.dialog.AddAlgorithmWizard;
import java.awt.event.ActionEvent;

/**
 *
 * @author gross
 */
public class AddAlgorithmAction extends BatchAction {

    public AddAlgorithmAction(JBatch batch) {
        super(batch, "Add algorithm", "algo_24.png");
        setEnabled(false);
    }   
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        AddAlgorithmWizard wizard = new AddAlgorithmWizard(batch);
        wizard.setVisible(true);
    }    
}
