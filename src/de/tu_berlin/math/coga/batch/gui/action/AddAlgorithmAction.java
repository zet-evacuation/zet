/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.action;

import de.tu_berlin.math.coga.batch.gui.JBatch;
import de.tu_berlin.math.coga.batch.gui.dialog.AddAlgorithmWizard;
import de.tu_berlin.math.coga.batch.input.InputFile;
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
        System.out.println(batch.getComputationList().get(0).getInput().size());
        for (InputFile file : batch.getComputationList().get(0).getInput()) {
            System.out.println(file);
        }
        //AddAlgorithmWizard wizard = new AddAlgorithmWizard(batch);
        //wizard.setVisible(true);
    }    
}
