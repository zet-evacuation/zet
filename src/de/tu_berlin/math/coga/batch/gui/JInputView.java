/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui;

import de.tu_berlin.math.coga.batch.ComputationList;
import de.tu_berlin.math.coga.batch.gui.input.BatchTreeTableNode;
import de.tu_berlin.math.coga.batch.gui.input.ComputationListNode;
import de.tu_berlin.math.coga.batch.gui.input.ComputationNode;
import de.tu_berlin.math.coga.batch.gui.input.InputListNode;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

/**
 *
 * @author gross
 */
public class JInputView extends JPanel {

    public JXTreeTable getTree() {
        return tree;
    }

    public class AlignedTableCellRenderer extends DefaultTableCellRenderer {

        public AlignedTableCellRenderer(int alignment) {
            setHorizontalAlignment(alignment);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            AlignedTableCellRenderer result = (AlignedTableCellRenderer) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);        
            return result;
        }
    }
    
    public class InputTreeCellRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if (value instanceof BatchTreeTableNode) { 
                BatchTreeTableNode node = (BatchTreeTableNode) value;
                renderer.setClosedIcon(node.getIcon());
                renderer.setLeafIcon(node.getIcon());
                renderer.setOpenIcon(node.getIcon());
                renderer.setFont(node.deriveFont(renderer.getFont()));
                renderer.setToolTipText(node.getToolTipText());
            }
            return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        }        
    }

    public class TestTreeTable extends JXTreeTable {

        private TestTreeTable(InputTreeTableModel model) {
            super(model);
        }
    }
    /**
     * The file tree.
     */
    private JXTreeTable tree;
    
    //private InputList input;

    /**
     * Creates the file tree panel.
     */
    public JInputView() {
        setLayout(new BorderLayout());        
        tree = new JXTreeTable();
        tree.getTableHeader().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {                
                int index = tree.columnAtPoint(e.getPoint());
                if (index >= 0) {
                    ComputationListNode rootNode = (ComputationListNode) tree.getTreeTableModel().getRoot();
                    Enumeration<? extends MutableTreeTableNode> computationNodes = rootNode.children();
                    while (computationNodes.hasMoreElements()) {
                        ComputationNode computationNode = (ComputationNode) computationNodes.nextElement();
                        InputListNode inputListNode = (InputListNode) computationNode.getChildAt(0);
                        inputListNode.sort(index);
                    }
                    setInput(new InputTreeTableModel(rootNode));
                }
            }
        });
        tree.setRootVisible(false);
        tree.setRowHeight(24);
        tree.setTreeCellRenderer(new InputTreeCellRenderer());
        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);        
    }

    public void setInput(ComputationList computations) {
        ComputationListNode rootNode = new ComputationListNode(computations);
        InputTreeTableModel model = new InputTreeTableModel(rootNode);
        setInput(model);        
    }

    protected void setInput(InputTreeTableModel model) {
        tree.setTreeTableModel(model);
        tree.getColumnModel().getColumn(0).setHeaderValue("Files");        
        for (int i = 1; i < model.getRoot().getColumnCount(); i++) {
             tree.getColumnModel().getColumn(i).setHeaderValue("");
             tree.getColumn(i).setCellRenderer(new AlignedTableCellRenderer(SwingConstants.RIGHT));
        }
        initColumnSizes(tree);
        tree.expandAll();
    }    
    
    private void initColumnSizes(JXTreeTable table) {
        InputTreeTableModel model = (InputTreeTableModel) table.getTreeTableModel();

        TableColumn column = null;
        Component comp = null;

        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < model.getColumnCount(); i++) {
            int headerWidth = 0;
            int cellWidth = 0;
            column = table.getColumnModel().getColumn(i);

            comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            ComputationListNode w = (ComputationListNode) model.getRoot();
            comp = table.getDefaultRenderer(model.getColumnClass(i)).getTableCellRendererComponent(table, w.getValueAt(i), false, false, 0, i);
            cellWidth = Math.max(comp.getPreferredSize().width, cellWidth);

            for (int j = 0; j < model.getChildCount(model.getRoot()); j++) {
                DefaultMutableTreeTableNode v = (DefaultMutableTreeTableNode) model.getChild(model.getRoot(), j);
                if (i < v.getColumnCount()) {
                    comp = table.getDefaultRenderer(model.getColumnClass(i)).getTableCellRendererComponent(table, v.getValueAt(i), false, false, 0, i);
                    cellWidth = Math.max(comp.getPreferredSize().width, cellWidth);
                }
            }
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
            column.setMinWidth(Math.max(headerWidth, cellWidth));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame frame = new JFrame("File tree");
                frame.setSize(500, 400);
                frame.setLocationRelativeTo(null);
                JInputView v;
                frame.add(v = new JInputView());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
