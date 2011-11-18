/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.text.Document;
import javax.swing.tree.TreeNode;
import org.jdesktop.swingx.treetable.TreeTableModel;

/**
 *
 * @author gross
 */
public interface FileSystemTreeModel extends TreeTableModel {

    public ArrayList getParentList();

    public Document getDocument();

    public TreeNode[] getPathToRoot(TreeNode en);

    public void setRowMapper(HashMap mapper);

    public void setParentList(ArrayList plist);

    public Object getRoot();

    public boolean getRootVisible();

    public HashMap getRowMapper();

    public Object getValueAt(Object node, int row, int col);

    public boolean isCellEditable(Object node, int row, int col);
}
