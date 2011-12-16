/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.input;

import de.tu_berlin.math.coga.batch.input.InputFile;
import de.tu_berlin.math.coga.batch.input.Input;
import java.util.Collections;
import java.util.Comparator;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

/**
 *
 * @author gross
 */
public abstract class InputTreeTableNode<T> extends DefaultMutableTreeTableNode {

    private int currentSortIndex = 0; 
    private boolean ascending = true;
    
    public InputTreeTableNode(T userObject) {
        super(userObject, true);
        //for (InputFile file : input) {
        //    add(new InputFileNode(file));
        //}
        //sort();
    }

    @Override
    public int getColumnCount() {
        int result = 1;
        for (int i = 0; i < getChildCount(); i++) {
            result = Math.max(result, getChildAt(i).getColumnCount());
        }
        return result;
    }
    
    @Override
    public boolean isEditable(int column) {
        return false;
    }    
    
    public T getUserObject() {
        return (T) getUserObject();
    }

    @Override
    public Object getValueAt(int column) {
        if (column == 0) {
            return getUserObject().toString();
        } else {
            return "";
        }
    }

    @Override
    public void setValueAt(Object aValue, int column) {
        throw new UnsupportedOperationException();
    }

    private void sort() {
        Comparator<MutableTreeTableNode> comparator = new ComparatorImpl(currentSortIndex, ascending);
        Collections.sort(children,comparator);        
    }    
    
    public void sort(int column) {
        if (column == currentSortIndex) {
            ascending = !ascending;
        }
        Comparator<MutableTreeTableNode> comparator = new ComparatorImpl(column, ascending);
        Collections.sort(children,comparator);        
        currentSortIndex = column;
    }

    @Override
    public String toString() {
        return getUserObject().toString();
    }

    private static class ComparatorImpl implements Comparator<MutableTreeTableNode> {

        private boolean ascending;
        private int column;
        
        public ComparatorImpl() {
        }

        private ComparatorImpl(int column, boolean ascending) {
            this.column = column;
            this.ascending = ascending;
        }

        @Override
        public int compare(MutableTreeTableNode o1, MutableTreeTableNode o2) {
            if (o1 instanceof InputFileNode && o2 instanceof InputFileNode) {
                InputFileNode f1 = (InputFileNode) o1;
                InputFileNode f2 = (InputFileNode) o2;
                String v1 = (String) f1.getValueAt(column);
                String v2 = (String) f2.getValueAt(column);
                if (v1.matches("[0-9]*") && v2.matches("[0-9]*")) {
                    return ((ascending)? 1 : -1) * (Integer.parseInt(v1) - (Integer.parseInt(v2)));
                } else {
                    return ((ascending)? 1 : -1) * ((Comparable )f1.getValueAt(column)).compareTo(f2.getValueAt(column));
                }
            } else {
                return 0;
            }
        }
    }
}
