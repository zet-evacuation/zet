/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui;

import de.tu_berlin.math.coga.batch.gui.input.InputFileNode;
import de.tu_berlin.math.coga.batch.gui.input.InputRootNode;
import de.tu_berlin.math.coga.batch.gui.input.InputTreeTableModel;
import de.tu_berlin.math.coga.batch.input.Input;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.jdesktop.swingx.JXTreeTable;

/**
 *
 * @author gross
 */
public class JInputView extends JPanel {
    
    private static final Icon ROOT_NODE = new ImageIcon("./icons/" + "folder_16.png");
    private static final Icon FILE_NODE = new ImageIcon("./icons/" + "graph_16.png");

    public JXTreeTable getTree() {
        return tree;
    }

    public class Test extends DefaultTableCellRenderer {

        public Test(int alignment) {
            setHorizontalAlignment(alignment);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Test result = (Test) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);        
            return result;
        }
    }
    
    public class InputTreeCellRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if (value instanceof InputRootNode) { 
                renderer.setClosedIcon(ROOT_NODE);
                renderer.setLeafIcon(ROOT_NODE);
                renderer.setOpenIcon(ROOT_NODE);
                renderer.setFont(renderer.getFont().deriveFont(Font.BOLD));
            } else if (value instanceof InputFileNode) {
                renderer.setClosedIcon(FILE_NODE);
                renderer.setLeafIcon(FILE_NODE);
                renderer.setOpenIcon(FILE_NODE);                
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
    
    private Input input;

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
                    InputRootNode inputRootNode = (InputRootNode) tree.getTreeTableModel().getRoot();
                    inputRootNode.sort(index);
                    setInput(new InputTreeTableModel(inputRootNode));
                }
            }
        });
        tree.setRootVisible(true);
        tree.setTreeCellRenderer(new InputTreeCellRenderer());
        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setInput(Input input) {
        this.input = input;
        InputRootNode inputRootNode = new InputRootNode(input);
        InputTreeTableModel model = new InputTreeTableModel(inputRootNode);
        setInput(model);
    }

    protected void setInput(InputTreeTableModel model) {
        tree.setTreeTableModel(model);
        tree.getColumnModel().getColumn(0).setHeaderValue("Files");
        for (int i = 0; i < input.getComputation().getType().getPropertyNames().length; i++) {
             tree.getColumnModel().getColumn(1 + i).setHeaderValue(input.getComputation().getType().getPropertyNames()[i]);
             tree.getColumn(1 + i).setCellRenderer(new Test(SwingConstants.RIGHT));
        }        
        initColumnSizes(tree);
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

            InputRootNode w = (InputRootNode) model.getRoot();
            comp = table.getDefaultRenderer(model.getColumnClass(i)).getTableCellRendererComponent(table, w.getValueAt(i), false, false, 0, i);
            cellWidth = Math.max(comp.getPreferredSize().width, cellWidth);

            for (int j = 0; j < model.getChildCount(model.getRoot()); j++) {
                InputFileNode v = (InputFileNode) model.getChild(model.getRoot(), j);
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
