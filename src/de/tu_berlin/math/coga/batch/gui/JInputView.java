/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui;



import de.tu_berlin.math.coga.batch.gui.input.FileTreeNode;
import de.tu_berlin.math.coga.batch.gui.input.InputFileNode;
import de.tu_berlin.math.coga.batch.gui.input.InputGroupNode;
import de.tu_berlin.math.coga.batch.gui.input.InputTreeTableModel;
import de.tu_berlin.math.coga.batch.input.InputFile;
import de.tu_berlin.math.coga.batch.input.InputFileType;
import de.tu_berlin.math.coga.batch.input.InputGroup;
import java.awt.BorderLayout;
import java.io.File;
//import java.util.*;

//import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.FileSystemModel;

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
        InputGroup group = new InputGroup(InputFileType.DIMACS_MAXIMUM_FLOW_PROBLEM);
        InputGroupNode n;
        root.add(n = new InputGroupNode(group));
        //n.add(new InputFileNode(new InputFile(null, null)));
        this.tree = new JXTreeTable(model);//rootTreeNode);
        //this.tree.setCellRenderer(new FileTreeCellRenderer());
        this.tree.setRootVisible(false);
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
