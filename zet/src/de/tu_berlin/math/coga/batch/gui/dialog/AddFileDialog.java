/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.dialog;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author gross
 */
public class AddFileDialog extends JFileChooser {

    private FileFilter[] filters = { 
        new FileNameExtensionFilter("ADVEST Instance", ".dat"), 
        new FileNameExtensionFilter("GZipped ZET Project", ".gzet"),
        new FileNameExtensionFilter("DIMACS Maximum Flow Problem", ".max"),
        new FileNameExtensionFilter("DIMACS Maximum Flow Solution", ".sol"),
        new FileNameExtensionFilter("ZET XML File", ".xml"),
        new FileNameExtensionFilter("ZET Project", ".zet"),
    };
    
    public AddFileDialog() {        
        super(new File(System.getProperty("user.dir")));
        for (FileFilter filter : filters) {
            addChoosableFileFilter(filter);
        }
        setAcceptAllFileFilterUsed(true);
        setFileSelectionMode(JFileChooser.FILES_ONLY);
        setDialogTitle("Select input file(s)...");
        setMultiSelectionEnabled(true);
    }
    
}
