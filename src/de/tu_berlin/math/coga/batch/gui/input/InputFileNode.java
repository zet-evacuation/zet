/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.input;

import de.tu_berlin.math.coga.batch.input.InputFile;
import java.util.Arrays;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

/**
 *
 * @author gross
 */
public class InputFileNode extends DefaultMutableTreeTableNode {

    private String[] properties;
    
    public InputFileNode(InputFile file) {
        super(file, true);
        properties = file.getProperties();
    }

    @Override
    public int getColumnCount() {
        return properties.length + 1;
    }
    
    public InputFile getInputFile() {
        return (InputFile) getUserObject();
    }

    @Override
    public Object getValueAt(int column) {
        if (column == 0) {
            return getInputFile().getName();
        } else {
            return properties[column - 1];
        }
    }

    @Override
    public boolean isEditable(int column) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int column) {
        throw new UnsupportedOperationException();
    }    
}
