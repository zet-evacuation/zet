package de.tu_berlin.math.coga.batch.gui.input;

import de.tu_berlin.math.coga.batch.input.InputFile;

/**
 *
 * @author Martin Groß
 */
public class InputNode extends BatchTreeTableNode<InputFile> {

    public InputNode(InputFile input) {
        super(input, input.getProperties(), input.getIcon() );
    }

    @Override
    public String getToolTipText() {
        return getUserObject().getDescription();
    }
}
