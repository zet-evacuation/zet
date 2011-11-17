/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.action;

import de.tu_berlin.math.coga.batch.gui.JBatch;
import de.tu_berlin.math.coga.batch.gui.dialog.AddFileDialog;
import java.awt.event.ActionEvent;
import javax.swing.JFileChooser;

/**
 *
 * @author Martin Groß
 */
public class AddInputFilesAction extends BatchAction {

    public AddInputFilesAction(JBatch batch) {
        super(batch, "Add input file(s)", "document_add_24.png");
    }   
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        AddFileDialog dialog = new AddFileDialog();
        int decision = dialog.showOpenDialog(batch);
        if (decision == JFileChooser.APPROVE_OPTION) {
            batch.addInputFiles(dialog.getSelectedFiles());
        }
    }    
}
