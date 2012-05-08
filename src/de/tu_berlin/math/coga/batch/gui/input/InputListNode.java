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
import javax.swing.ImageIcon;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

/**
 *
 * @author gross
 */
public class InputListNode extends BatchTreeTableNode<InputList> {
    
    private int currentSortIndex = 0;
    private boolean ascending = true;
    
    private Map<ProblemType, InputTypeNode> typeNodes;

    public InputListNode(InputList input) {
        super(input, new String[0], new ImageIcon("./icons/folder_24.png"));
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
        int max = 0;
        for (ProblemType type : typeNodes.keySet()) {
            if (max < type.getPropertyNames().length) {
                max = type.getPropertyNames().length;
            }
        }
        return 1 + max; 
    }

    public InputList getInput() {
        return (InputList) getUserObject();
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
        return "Input";
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
