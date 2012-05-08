/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.input;

import de.tu_berlin.math.coga.batch.input.InputFile;

/**
 *
 * @author gross
 */
public class InputNode extends BatchTreeTableNode<InputFile> {
    
    public InputNode(InputFile input) {
        super(input, input.getProperties(), input.getFormat().getIcon());
    }
    
    @Override
    public String getToolTipText() {
        return getUserObject().getTooltip();
    }
}
