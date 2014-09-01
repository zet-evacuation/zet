package de.tu_berlin.math.coga.batch.gui;

import de.tu_berlin.math.coga.batch.gui.input.ComputationListNode;
import javax.swing.tree.TreeNode;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

/**
 *
 * @author Martin Groß
 */
public class InputTreeTableModel extends DefaultTreeTableModel {

  public InputTreeTableModel( ComputationListNode rootNode ) {
    super( rootNode );
  }

  @Override
  public Object getChild( Object parent, int index ) {
    if( parent instanceof DefaultMutableTreeTableNode ) {
      return ((DefaultMutableTreeTableNode)parent).getChildAt( index );
    } else {
      throw new AssertionError( "This should not happen." );
    }
  }

  @Override
  public int getChildCount( Object parent ) {
    if( parent instanceof DefaultMutableTreeTableNode ) {
      return ((DefaultMutableTreeTableNode)parent).getChildCount();
    } else {
      throw new AssertionError( "This should not happen." );
    }
  }

  @Override
  public int getColumnCount() {
    /*
     Enumeration<? extends MutableTreeTableNode> children = ((DefaultMutableTreeTableNode) getRoot()).children();
     int result = getRoot().getColumnCount();
     while (children.hasMoreElements()) {
     MutableTreeTableNode child = children.nextElement();
     if (child instanceof MutableTreeTableNode) {
     result = Math.max(result, ((MutableTreeTableNode) child).getColumnCount());
     }
     }
     return result;*/
    return getRoot().getColumnCount();// getColumnCount((MutableTreeTableNode) getRoot());
  }
  /*
   public int getColumnCount(MutableTreeTableNode node) {
   Enumeration<? extends MutableTreeTableNode> children = node.children();
   int result = node.getColumnCount();
   while (children.hasMoreElements()) {
   MutableTreeTableNode child = children.nextElement();
   System.out.println("Processing " + child + " "  + child.children().hasMoreElements());
   if (child instanceof DefaultMutableTreeTableNode) {
   result = Math.max(result, getColumnCount(child));
   System.out.println(result);
   }
   }
   System.out.println("Child: " + node.getClass() + " - " + result);
   return result;
   } */

  @Override
  public int getIndexOfChild( Object parent, Object child ) {
    if( parent instanceof DefaultMutableTreeTableNode && child instanceof TreeNode ) {
      return ((DefaultMutableTreeTableNode)parent).getIndex( (TreeNode)child );
    } else {
      throw new AssertionError( "This should not happen." );
    }
  }

  @Override
  public Object getValueAt( Object parent, int column ) {
    if( parent instanceof DefaultMutableTreeTableNode ) {
      return ((DefaultMutableTreeTableNode)parent).getValueAt( column );
    } else {
      throw new AssertionError( "This should not happen." );
    }
  }
}
