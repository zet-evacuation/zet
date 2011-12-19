/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.action;

import de.tu_berlin.math.coga.batch.gui.JBatch;
import de.tu_berlin.math.coga.batch.gui.dialog.AddDirectoryWizard;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *
 * @author gross
 */
public class AddInputDirectoryAction extends BatchAction {

    public AddInputDirectoryAction(JBatch batch) {
        super(batch, "Add input directory", "folder_add_24.png");
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        final AddDirectoryWizard wizard = new AddDirectoryWizard(batch);
        wizard.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                if (wizard.isAccepted()) {
                   batch.addInputFiles(wizard.getSelectedFiles(),wizard.isRecursive(),wizard.isFollowingLinks());
                }
            }
        });
        wizard.setVisible(true);
    }
}
