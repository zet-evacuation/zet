/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.action;

import de.tu_berlin.math.coga.batch.Computation;
import de.tu_berlin.math.coga.batch.gui.JBatch;
import java.awt.event.ActionEvent;

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
        Computation computation = new Computation();
        computation.setTitle(batch.getComputationList().generateGenericComputationTitle());
        batch.addComputation(computation);
    }
}
