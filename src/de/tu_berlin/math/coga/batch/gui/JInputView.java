/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui;



import de.tu_berlin.math.coga.batch.gui.input.InputFileNode;
import de.tu_berlin.math.coga.batch.gui.input.InputGroupNode;
import de.tu_berlin.math.coga.batch.gui.input.InputTreeTableModel;
import de.tu_berlin.math.coga.batch.input.FileCrawler;
import de.tu_berlin.math.coga.batch.input.reader.DimacsMaximumFlowFileReader;
import de.tu_berlin.math.coga.batch.input.InputFile;
import de.tu_berlin.math.coga.batch.input.InputGroup;
import de.tu_berlin.math.coga.batch.input.reader.DimacsMinimumCostFlowFileReader;
import java.awt.BorderLayout;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

/**
 *
 * @author gross
 */
public class JInputView extends JPanel {

    /**
     * The file tree.
     */
    private JXTreeTable tree;


    /**
     * Creates the file tree panel.
     */
    public JInputView() {
        this.setLayout(new BorderLayout());

        File[] roots = File.listRoots();

        InputTreeTableModel model = new InputTreeTableModel();
        DefaultMutableTreeTableNode root = (DefaultMutableTreeTableNode) model.getRoot();
        root.setAllowsChildren(true);
        //InputGroup group = new InputGroup(InputFileType.DIMACS_MAXIMUM_FLOW_PROBLEM);
        InputGroupNode n;
        
        
        FileCrawler crawler = new FileCrawler(false, false);
        LinkedList<String> ext = new LinkedList<>();
        ext.add("net");
        List<File> files = crawler.listFiles(new File("/homes/combi/gross/"), ext);
        for (File file : files) {
            DimacsMinimumCostFlowFileReader reader = new DimacsMinimumCostFlowFileReader();
            reader.setFile(file);
            String[] properties = reader.getProperties();
            //System.out.println(file + ": " + properties[0] + " " + properties[1] + " " + properties[2]);
            
            root.add(new InputFileNode(new InputFile(file)));
        }        
        
        
        //InputFile file = new InputFile(new File("/homes/combi/gross/sample.max"), new DimacsMaximumFlowFileReader());
        //n.add(fileNode = new InputFileNode(file)); 
        //n.add(new InputFileNode(new InputFile(null, null)));
        //rootTreeNode);
        
        this.tree = new JXTreeTable(model);/*
        
        tree.getColumnModel().getColumn(0).setHeaderValue("Files");
        
        TableColumn col = new TableColumn();
        col.setHeaderValue("Nodes");
        tree.getColumnModel().addColumn(col);
        
        col = new TableColumn();
        col.setHeaderValue("Edges");
        tree.getColumnModel().addColumn(col);
        
        col = new TableColumn();
        col.setHeaderValue("Supply");
        tree.getColumnModel().addColumn(col);*/
        
        
        
        //this.tree.setCellRenderer(new FileTreeCellRenderer());
        this.tree.setRootVisible(true);
        final JScrollPane jsp = new JScrollPane(this.tree);
        jsp.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.add(jsp, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                JFrame frame = new JFrame("File tree");
                frame.setSize(500, 400);
                frame.setLocationRelativeTo(null);
                frame.add(new JInputView());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
