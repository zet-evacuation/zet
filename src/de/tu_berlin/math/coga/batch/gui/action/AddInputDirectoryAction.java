/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.action;

import com.l2fprod.common.swing.JDirectoryChooser;
import de.tu_berlin.math.coga.batch.gui.JBatch;
import de.tu_berlin.math.coga.batch.gui.dialog.AddDirectoryDialog;
import java.awt.event.ActionEvent;

/**
 *
 * @author gross
 */
public class AddInputDirectoryAction extends BatchAction {
    
    public AddInputDirectoryAction(JBatch batch) {
        super(batch, "Add input directory", "folder_add_24.png");
    }   
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        AddDirectoryDialog dialog = new AddDirectoryDialog();
        int decision = dialog.showOpenDialog(batch);
        if (decision == JDirectoryChooser.APPROVE_OPTION) {
            batch.addInputFiles(dialog.getSelectedFiles());
        }
    }        
}
