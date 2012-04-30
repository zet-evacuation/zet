/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.input;

import de.tu_berlin.math.coga.batch.input.InputFile;
import de.tu_berlin.math.coga.batch.input.InputList;
import de.tu_berlin.math.coga.batch.input.ProblemType;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

/**
 *
 * @author gross
 */
public class InputRootNode extends DefaultMutableTreeTableNode {
    
    private int currentSortIndex = 0;
    private boolean ascending = true;
    
    private Map<ProblemType, InputTypeNode> typeNodes;

    public InputRootNode(InputList input) {
        super(input, true);
        typeNodes = new HashMap<>();
        for (InputFile file : input) {
            if (!typeNodes.containsKey(file.getProblemType())) {
                InputTypeNode node = new InputTypeNode(file.getProblemType(), file.getPropertyNames());
                add(node);
                typeNodes.put(file.getProblemType(), node);
            } 
            InputTypeNode node = typeNodes.get(file.getProblemType());
            node.add(new InputNode(file));
        }
        sort();
    }

    @Override
    public int getColumnCount() {
        return 1 + getInput().getComputation().getType().getPropertyNames().length;
    }

    public InputList getInput() {
        return (InputList) getUserObject();
    }

    @Override
    public Object getValueAt(int column) {
        if (column == 0) {
            return getInput().getText();
        } else {
            return "";
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

    private void sort() {
        Comparator<MutableTreeTableNode> comparator = new ComparatorImpl(currentSortIndex, ascending);
        Collections.sort(children, comparator);
    }

    public void sort(int column) {
        if (column == currentSortIndex) {
            ascending = !ascending;
        }
        Comparator<MutableTreeTableNode> comparator = new ComparatorImpl(column, ascending);
        Collections.sort(children, comparator);
        currentSortIndex = column;
    }

    @Override
    public String toString() {
        return getInput().getText();
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
            if (o1 instanceof InputNode && o2 instanceof InputNode) {
                InputNode f1 = (InputNode) o1;
                InputNode f2 = (InputNode) o2;
                String v1 = (String) f1.getValueAt(column);
                String v2 = (String) f2.getValueAt(column);
                if (v1.matches("[0-9]*") && v2.matches("[0-9]*")) {
                    return ((ascending) ? 1 : -1) * (Integer.parseInt(v1) - (Integer.parseInt(v2)));
                } else {
                    return ((ascending) ? 1 : -1) * ((Comparable) f1.getValueAt(column)).compareTo(f2.getValueAt(column));
                }
            } else {
                return 0;
            }
        }
    }
}
