/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.action;

import de.tu_berlin.math.coga.batch.Computation;
import de.tu_berlin.math.coga.batch.gui.JBatch;
import de.tu_berlin.math.coga.batch.gui.dialog.NewComputationWizard;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *
 * @author gross
 */
public class NewComputationAction extends BatchAction {

    public NewComputationAction(JBatch batch) {
        super(batch, "New computation", "document_24.png");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        final NewComputationWizard wizard = new NewComputationWizard(batch);
        wizard.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                if (wizard.isAccepted() && wizard.getProblemType() != null) {
                   Computation computation = new Computation(wizard.getProblemType());
                   batch.setComputation(computation);
                }
            }
        });
        wizard.setVisible(true);
    }   
    
}
