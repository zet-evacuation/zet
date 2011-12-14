/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui;

import de.tu_berlin.math.coga.batch.gui.input.FileTreeCellRenderer;
import de.tu_berlin.math.coga.batch.gui.input.InputFileNode;
import de.tu_berlin.math.coga.batch.gui.input.InputGroupNode;
import de.tu_berlin.math.coga.batch.gui.input.InputTreeTableModel;
import de.tu_berlin.math.coga.batch.input.FileCrawler;
import de.tu_berlin.math.coga.batch.input.InputFile;
import de.tu_berlin.math.coga.batch.input.InputGroup;
import de.tu_berlin.math.coga.batch.input.reader.DimacsMinimumCostFlowFileReader;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import javax.media.DeallocateEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.jdesktop.swingx.JXTableHeader;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

/**
 *
 * @author gross
 */
public class JInputView extends JPanel {

    public class Test extends DefaultTableCellRenderer {

        public Test() {
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Test result = (Test) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            result.setHorizontalAlignment(SwingConstants.RIGHT);
            return result;
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
    private TestTreeTable tree;

    /**
     * Creates the file tree panel.
     */
    public JInputView() {
        this.setLayout(new BorderLayout());

        File[] roots = File.listRoots();


        //DefaultMutableTreeTableNode root = (DefaultMutableTreeTableNode) model.getRoot();
        //root.setAllowsChildren(true);
        //InputGroup group = new InputGroup(InputFileType.DIMACS_MAXIMUM_FLOW_PROBLEM);


        FileCrawler crawler = new FileCrawler(false, false);
        LinkedList<String> ext = new LinkedList<>();
        ext.add("net");
        List<File> files = crawler.listFiles(new File("/homes/combi/gross/"), ext);
        InputGroup group = new InputGroup("Minimum Cost Flow Problems");
        for (File file : files) {
            DimacsMinimumCostFlowFileReader reader = new DimacsMinimumCostFlowFileReader();
            reader.setFile(file);
            //String[] properties = reader.getProperties();
            //System.out.println(file + ": " + properties[0] + " " + properties[1] + " " + properties[2]);
            InputFile inputFile = new InputFile(file);
            group.add(inputFile);
        }

        final InputGroupNode g = new InputGroupNode(group);
        InputTreeTableModel model = new InputTreeTableModel(g);
        //g.sort(1);


        //InputFile file = new InputFile(new File("/homes/combi/gross/sample.max"), new DimacsMaximumFlowFileReader());
        //n.add(fileNode = new InputFileNode(file)); 
        //n.add(new InputFileNode(new InputFile(null, null)));
        //rootTreeNode);

        this.tree = new TestTreeTable(model);
        this.tree.setRootVisible(true);
        tree.setClosedIcon(new ImageIcon("./icons/" + "folder_16.png"));
        tree.setLeafIcon(new ImageIcon("./icons/" + "graph_16.png"));
        tree.setOpenIcon(new ImageIcon("./icons/" + "folder_16.png"));
        tree.getColumnModel().getColumn(0).setHeaderValue("Files");
        //System.out.println(tree.getCellRenderer(0,0));
        //tree.getColumnModel().getColumn(1).getCellRenderer().        
        tree.getColumnModel().getColumn(1).setHeaderValue("Nodes");
        tree.getColumnModel().getColumn(2).setHeaderValue("Edges");
        tree.getColumnModel().getColumn(3).setHeaderValue("Total Supply");
        tree.getColumn(1).setCellRenderer(new Test());
        tree.getColumn(2).setCellRenderer(new Test());
        tree.getColumn(3).setCellRenderer(new Test());

        MouseListener l = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int index = tree.columnAtPoint(e.getPoint());
                g.sort(index);
                tree.setTreeTableModel(new InputTreeTableModel(g));
                tree.setRootVisible(true);
                //tree.setClosedIcon(new ImageIcon("./icons/" + "folder_16.png"));
                //tree.setLeafIcon(new ImageIcon("./icons/" + "graph_16.png"));
                //tree.setOpenIcon(new ImageIcon("./icons/" + "folder_16.png"));
                tree.getColumnModel().getColumn(0).setHeaderValue("Files");
                //System.out.println(tree.getCellRenderer(0,0));
                //tree.getColumnModel().getColumn(1).getCellRenderer().        
                tree.getColumnModel().getColumn(1).setHeaderValue("Nodes");
                tree.getColumnModel().getColumn(2).setHeaderValue("Edges");
                tree.getColumnModel().getColumn(3).setHeaderValue("Total Supply");
                tree.getColumn(1).setCellRenderer(new Test());
                tree.getColumn(2).setCellRenderer(new Test());
                tree.getColumn(3).setCellRenderer(new Test());
                initColumnSizes(tree);
                //tree.getTreeTableModel().
            }
        };
        tree.getTableHeader().addMouseListener(l);

        initColumnSizes(tree);

        //tree.setTableHeader(null);
        //DefaultTableCellRenderer r = (DefaultTableCellRenderer) tree.get.getTableHeader().getDefaultRenderer();
        //r.setHorizontalAlignment(SwingConstants.RIGHT);
        //(DefaultTableCellRenderer) getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);


        //this.tree.setCellRenderer(new FileTreeCellRenderer());

        final JScrollPane jsp = new JScrollPane(this.tree);
        jsp.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.add(jsp, BorderLayout.CENTER);

        //tree.getColumn(0).sizeWidthToFit();
        //tree.getColumn(1).sizeWidthToFit();
        //tree.getColumn(2).sizeWidthToFit();
        //tree.getColumn(3).sizeWidthToFit();        
    }

    private void initColumnSizes(TestTreeTable table) {
        InputTreeTableModel model = (InputTreeTableModel) table.getTreeTableModel();

        TableColumn column = null;
        Component comp = null;

        //Object[] longValues = model.longValues;

        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < model.getColumnCount(); i++) {
            int headerWidth = 0;
            int cellWidth = 0;
            column = table.getColumnModel().getColumn(i);

            comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            InputGroupNode w = (InputGroupNode) model.getRoot();
            comp = table.getDefaultRenderer(model.getColumnClass(i)).getTableCellRendererComponent(table, w.getValueAt(i), false, false, 0, i);
            cellWidth = Math.max(comp.getPreferredSize().width, cellWidth);
            Object value;
            for (int j = 0; j < model.getChildCount(model.getRoot()); j++) {
                InputFileNode v = (InputFileNode) model.getChild(model.getRoot(), j);
                if (i < v.getColumnCount()) {
                    //System.out.println(v.getValueAt(i));
                    comp = table.getDefaultRenderer(model.getColumnClass(i)).getTableCellRendererComponent(table, v.getValueAt(i), false, false, 0, i);
                    cellWidth = Math.max(comp.getPreferredSize().width, cellWidth);
                }
            }
            if (false) {
                System.out.println("Initializing width of column " + i + ". "
                        + "headerWidth = " + headerWidth + "; cellWidth = "
                        + cellWidth);
            }

// XXX: Before Swing 1.1 Beta 2, use setMinWidth instead.
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

                //v.tree.getColumn(0).sizeWidthToFit();
            }
        });
    }
}
