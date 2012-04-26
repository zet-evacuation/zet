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
public class InputNode extends BatchTreeTableNode<InputFile> {
    
    public InputNode(InputFile input) {
        super(input, input.getProperties());
        if (input instanceof InputFile) {
            setIcon(new ImageIcon("./icons/graph_16.png"));
        } else if (input instanceof InputFile) {
            setIcon(new ImageIcon("./icons/box_16.png"));
        }
    }
    
    @Override
    public String getToolTipText() {
        return getUserObject().getTooltip();
    }
}
