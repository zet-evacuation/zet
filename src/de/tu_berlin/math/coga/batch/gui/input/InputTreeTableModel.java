/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.input;

import java.util.Enumeration;
import javax.swing.tree.TreeNode;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

/**
 *
 * @author gross
 */
public class InputTreeTableModel extends DefaultTreeTableModel {    

    public InputTreeTableModel(InputRootNode inputRootNode) {
        super(inputRootNode);
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent instanceof DefaultMutableTreeTableNode) {
            return ((DefaultMutableTreeTableNode) parent).getChildAt(index);
        } else {
            throw new AssertionError("This should not happen.");
        }
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof DefaultMutableTreeTableNode) {
            return ((DefaultMutableTreeTableNode) parent).getChildCount();
        } else {
            throw new AssertionError("This should not happen.");
        }
    }

    @Override
    public int getColumnCount() {
        Enumeration<? extends MutableTreeTableNode> children = ((DefaultMutableTreeTableNode) getRoot()).children();
        int result = getRoot().getColumnCount();
        while (children.hasMoreElements()) {
            MutableTreeTableNode child = children.nextElement();
            if (child instanceof InputNode) {
                result = Math.max(result, ((InputNode) child).getColumnCount());
            }
        }
        return result;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof DefaultMutableTreeTableNode && child instanceof TreeNode) {
            return ((DefaultMutableTreeTableNode) parent).getIndex((TreeNode) child);
        } else {
            throw new AssertionError("This should not happen.");
        }
    }

    @Override
    public Object getValueAt(Object parent, int column) {
        if (parent instanceof DefaultMutableTreeTableNode) {
            return ((DefaultMutableTreeTableNode) parent).getValueAt(column);
        } else {
            throw new AssertionError("This should not happen.");
        }
    }
}
