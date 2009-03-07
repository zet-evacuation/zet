/**
 * Class PropertyLoader
 * Erstellt 21.02.2008, 23:37:19
 */
package gui.editor.properties.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import gui.editor.properties.PropertyTreeNode;
import gui.editor.properties.PropertyTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Jan-Philipp Kappmeier, Martin Groß
 */
public class PropertyTreeConverter implements Converter {

  /**
   * 
   * @param type
   * @return
   */
  public boolean canConvert( Class type ) {
    return type.equals( PropertyTreeModel.class );
  }

  /**
   * 
   * @param source
   * @param writer
   * @param context
   */
  public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
    PropertyTreeModel treeModel = (PropertyTreeModel) source;
    // Root node has at most one property!
		PropertyTreeNode root = treeModel.getRoot();
    writer.addAttribute( "name", root.getNameTag() );
    writer.addAttribute( "useAsLocString", Boolean.toString( root.isUsedAsLocString() ) );
		writer.addAttribute( "propertyName", treeModel.getPropertyName() );
    for( int i = 0; i < root.getChildCount(); i++ ) {
      DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt( i );
      context.convertAnother( child, new DefaultPropertyTreeNodeConverter() );
    }
  }

  /**
   * 
   * @param reader
   * @param context
   * @return
   */
  public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
    PropertyTreeNode root = new PropertyTreeNode( "" );
    root.setName( reader.getAttribute( "name" ) );
    root.useAsLocString( (reader.getAttribute( "useAsLocString" ).equals("true")?true:false ) );
		String propertyName = reader.getAttribute( "propertyName" );
    while( reader.hasMoreChildren() ) {
      reader.moveDown();
      PropertyTreeNode node = (PropertyTreeNode)context.convertAnother( root, PropertyTreeNode.class, new DefaultPropertyTreeNodeConverter() );
      root.add( node );
      reader.moveUp();
    }
		PropertyTreeModel ptm = new PropertyTreeModel( root );
		ptm.setPropertyName( propertyName );
		
    return ptm;
  }
}
