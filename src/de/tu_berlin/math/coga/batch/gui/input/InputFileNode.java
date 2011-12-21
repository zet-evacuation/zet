/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.input;

import de.tu_berlin.math.coga.batch.input.InputFile;
import javax.swing.ImageIcon;

/**
 *
 * @author gross
 */
public class InputFileNode extends BatchTreeTableNode<InputFile> {
    
    public InputFileNode(InputFile file) {
        super(file, file.getProperties(), new ImageIcon("./icons/graph_16.png"));
    }

    @Override
    public String getToolTipText() {
        return getUserObject().getFile().getPath();
    }
}
