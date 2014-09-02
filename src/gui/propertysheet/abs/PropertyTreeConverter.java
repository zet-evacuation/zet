/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/**
 * Class PropertyLoader
 * Created 21.02.2008, 23:37:19
 */
package gui.propertysheet.abs;

import gui.propertysheet.abs.DefaultPropertyTreeNodeConverter;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import gui.propertysheet.PropertyTreeNode;
import gui.propertysheet.PropertyTreeModel;

/**
 *
 * @author Jan-Philipp Kappmeier, Martin Groß
 */
public class PropertyTreeConverter implements Converter {

  /**
   *
   * @param type
   * @return {@code true} if the class can be converted, {@code false} otherwise
   */
	@Override
  public boolean canConvert( Class type ) {
    return type.equals( PropertyTreeModel.class );
  }

  /**
   *
   * @param source
   * @param writer
   * @param context
   */
	@Override
  public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
    PropertyTreeModel treeModel = (PropertyTreeModel) source;
    // Root node has at most one property!
		PropertyTreeNode root = treeModel.getRoot();
    writer.addAttribute( "name", root.getDisplayNameTag() );
    writer.addAttribute( "useAsLocString", Boolean.toString( root.isUsedAsLocString() ) );
		writer.addAttribute( "propertyName", treeModel.getPropertyName() );
    for( int i = 0; i < root.getChildCount(); i++ ) {
      PropertyTreeNode child =  root.getChildAt( i );
      context.convertAnother( child, new DefaultPropertyTreeNodeConverter() );
    }
  }

  /**
   *
   * @param reader
   * @param context
   * @return the converted property tree node
   */
	@Override
  public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
    PropertyTreeNode root = new PropertyTreeNode( "" );
    root.setDisplayName( reader.getAttribute( "name" ) );
    root.useAsLocString( (reader.getAttribute( "useAsLocString" ).equals("true") ) );
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
