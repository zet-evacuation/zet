package de.tu_berlin.math.coga.batch.gui.input;

import de.tu_berlin.math.coga.batch.output.TreeListItem;
import java.awt.Font;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.Icon;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

/**
 *
 * @param <T> the data stored in the nodes
 * @author Martin Groß
 */
public class BatchTreeTableNode<T> extends DefaultMutableTreeTableNode {

  protected Icon icon;
  protected String[] properties;

  public BatchTreeTableNode( T data ) {
    this( data, null, null );
  }

  public BatchTreeTableNode( T data, Icon icon ) {
    this( data, new String[0], icon );
  }

  public BatchTreeTableNode( T data, String[] properties ) {
    this( data, properties, null );
  }

  public BatchTreeTableNode( T data, String[] properties, Icon icon ) {
    super( data, true );
    this.icon = icon;
    this.properties = properties;
  }

  public Font deriveFont( Font font ) {
    return font;
  }

  @Override
  public int getColumnCount() {
    int result = properties.length + 1;
    for( int i = 0; i < getChildCount(); i++ ) {
      result = Math.max( result, getChildAt( i ).getColumnCount() );
    }
    return result;
  }

  public Icon getIcon() {
    return icon;
  }

  public void setIcon( Icon icon ) {
    this.icon = icon;
  }

  public String getToolTipText() {
    return getValueAt( 0 );
  }

  @Override
  public T getUserObject() {
    return (T)super.getUserObject();
  }

  @Override
  public String getValueAt( int column ) {
    if( column == 0 ) {
      return toString();
    } else if( column <= properties.length ) {
      return properties[column - 1];
    } else {
      return "";
    }
  }

  @Override
  public boolean isEditable( int column ) {
    return false;
  }

  @Override
  public void setValueAt( Object aValue, int column ) {
    throw new UnsupportedOperationException();
  }

  public void sort( int column, boolean ascending ) {
    Comparator<MutableTreeTableNode> comparator = new ColumnComparator( column, ascending );
    Collections.sort( children, comparator );
    for( MutableTreeTableNode child : children ) {
      if( child instanceof BatchTreeTableNode ) {
        ((BatchTreeTableNode)child).sort( column, ascending );
      }
    }
  }

  @Override
  public String toString() {
    if( getUserObject() != null ) {
      if( getUserObject() instanceof TreeListItem ) {
        return ((TreeListItem)getUserObject()).getTitle();
      } else {
        return getUserObject().toString();
      }
    } else {
      return "";
    }
  }

  private static class ColumnComparator implements Comparator<MutableTreeTableNode> {

    private boolean ascending;
    private int column;

    public ColumnComparator() {
    }

    private ColumnComparator( int column, boolean ascending ) {
      this.column = column;
      this.ascending = ascending;
    }

    @Override
    public int compare( MutableTreeTableNode o1, MutableTreeTableNode o2 ) {
      if( o1 == null || o2 == null || !(o1 instanceof BatchTreeTableNode) || !(o2 instanceof BatchTreeTableNode) ) {
        return 0;
      }
      String v1 = (String)o1.getValueAt( column );
      String v2 = (String)o2.getValueAt( column );
      v1 = v1.toLowerCase();
      v2 = v2.toLowerCase();
      if( v1.matches( "[0-9]*" ) && v2.matches( "[0-9]*" ) ) {
        return ((ascending) ? 1 : -1) * (Integer.parseInt( v1 ) - (Integer.parseInt( v2 )));
      } else {
        return ((ascending) ? 1 : -1) * v1.compareTo( v2 );
      }
    }
  }
}
