/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.dialog;

import de.tu_berlin.math.coga.batch.input.ProblemType;
import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author gross
 */
public class AddFileDialog extends JFileChooser {
    
    private ProblemType problemType;
    
    public AddFileDialog(ProblemType problemType) {
        super();
        File directory = new File(System.getProperty("user.dir") + "/examples");
        if (!directory.exists()) {
            directory = new File(System.getProperty("user.dir"));
        }
        setCurrentDirectory(directory);
        this.problemType = problemType;
        for (ProblemType type : ProblemType.values()) {
            addChoosableFileFilter(type.getFileFilter());            
        }
        setAcceptAllFileFilterUsed(true);
        setFileSelectionMode(JFileChooser.FILES_ONLY);
        //setFileFilter(problemType.getFileFilter());
        setDialogTitle("Select input file(s)...");
        setMultiSelectionEnabled(true);        
    }

    public AddFileDialog() {
        this(null);
    }

    public ProblemType getProblemType() {
        return problemType;
    }

    public void setProblemType(ProblemType problemType) {
        this.problemType = problemType;
        if (problemType != null) {
            setFileFilter(problemType.getFileFilter());
        }
    }
}
