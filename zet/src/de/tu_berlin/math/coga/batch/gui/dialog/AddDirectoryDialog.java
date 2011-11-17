/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.dialog;

import com.l2fprod.common.swing.JDirectoryChooser;
import java.io.File;

/**
 *
 * @author gross
 */
public class AddDirectoryDialog extends JDirectoryChooser {

    public AddDirectoryDialog() {
        super(new File(System.getProperty("user.dir")));
        this.setAutoscrolls(true);
        this.setShowingCreateDirectory(false);
    }
    
}
