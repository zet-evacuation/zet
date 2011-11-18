/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.input;

import de.tu_berlin.math.coga.batch.input.InputGroup;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

/**
 *
 * @author gross
 */
public class InputGroupNode extends DefaultMutableTreeTableNode {
    
    private String[] properties;
    
    public InputGroupNode(InputGroup group) {
        super(group, false);
        properties = group.getType().getPropertyNames();
    }

    @Override
    public int getColumnCount() {
        return properties.length + 1;
    }
    
    public InputGroup getInputGroup() {
        return (InputGroup) getUserObject();
    }

    @Override
    public Object getValueAt(int column) {
        if (column == 0) {
            return getInputGroup().getType().getDescription();
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
