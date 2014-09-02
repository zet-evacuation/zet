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
 * Class DefaultPropertyTreeNodeConverter
 * Created 22.02.2008, 01:37:09
 */

package gui.propertysheet.abs;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import gui.propertysheet.BasicProperty;
import gui.propertysheet.types.BooleanProperty;
import gui.propertysheet.types.IntegerProperty;
import gui.propertysheet.types.DoubleProperty;
import gui.propertysheet.types.IntegerRangeProperty;
import gui.propertysheet.types.StringProperty;
import gui.propertysheet.types.StringListProperty;
import gui.propertysheet.PropertyTreeNode;
import gui.propertysheet.types.QualitySettingProperty;
import gui.propertysheet.types.BooleanPropertyConverter;
import gui.propertysheet.types.ColorProperty;
import gui.propertysheet.types.ColorPropertyConverter;
import gui.propertysheet.types.DoublePropertyConverter;
import gui.propertysheet.types.IntegerPropertyConverter;
import gui.propertysheet.types.IntegerRangePropertyConverter;
import gui.propertysheet.types.QualitySettingPropertyConverter;
import gui.propertysheet.types.StringListPropertyConverter;
import gui.propertysheet.types.StringPropertyConverter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class DefaultPropertyTreeNodeConverter implements Converter {

  /**
   * Returns true, if the given type can be converted.
   * {@link PropertyTreeConverter}
   * @param type
   * @return true, if the given type can be converted.
   */
	@Override
  public boolean canConvert( Class type ) {
    return type.equals( PropertyTreeNode.class );
  }

  /**
   * Writes a node to the XML-file, beginning with writing the start node.
   * @param source
   * @param writer
   * @param context
   */
	@Override
  public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
    PropertyTreeNode node = (PropertyTreeNode) source;
    writer.startNode( "treeNode" );
    writer.addAttribute( "name", node.getDisplayNameTag() );
    writer.addAttribute( "useAsLocString", Boolean.toString( node.isUsedAsLocString() ) );
    for( int i=0; i < node.getChildCount(); i++ ) {
      PropertyTreeNode child = node.getChildAt( i ); // to do: iterator
      context.convertAnother( child );
    }
		for( BasicProperty<?> property : node.getProperties()	) {
			if( property instanceof BooleanProperty )
				context.convertAnother( property, new BooleanPropertyConverter() );
			else if( property instanceof IntegerRangeProperty )
				context.convertAnother( property, new IntegerRangePropertyConverter() );
			else if( property instanceof IntegerProperty )
				context.convertAnother( property, new IntegerPropertyConverter() );
			else if( property instanceof DoubleProperty )
				context.convertAnother( property, new DoublePropertyConverter() );
			else if( property instanceof StringProperty )
				context.convertAnother( property, new StringPropertyConverter() );
			else if( property instanceof StringListProperty )
				context.convertAnother( property, new StringListPropertyConverter() );
			else if( property instanceof QualitySettingProperty )
				context.convertAnother( property, new QualitySettingPropertyConverter() );
			else if( property instanceof ColorProperty )
				context.convertAnother( property, new ColorPropertyConverter() );
		}
    writer.endNode();
  }

  /**
   * Reads a treeNode element from a XML-file.
   * @param reader
   * @param context
   * @return the converted property tree node
   */
	@Override
  public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
    String name = reader.getAttribute( "name" );
    PropertyTreeNode node = new PropertyTreeNode( name ); // this is the displayName
    node.useAsLocString( (reader.getAttribute( "useAsLocString" ).equals( "true" ) ) );
    while( reader.hasMoreChildren() ) {
      reader.moveDown();
			String nodeName = reader.getNodeName();
			switch( nodeName ) {
				case "treeNode":
					PropertyTreeNode child = (PropertyTreeNode)context.convertAnother( node, PropertyTreeNode.class, new DefaultPropertyTreeNodeConverter() );
					node.add( child );
					break;
				case "boolNode":
					BooleanProperty bool = (BooleanProperty)context.convertAnother( node, BooleanProperty.class, new BooleanPropertyConverter() );
					node.addProperty( bool );
					break;
				case "intNode": {
					IntegerProperty intP = (IntegerProperty)context.convertAnother( node, IntegerProperty.class, new IntegerPropertyConverter() );
					node.addProperty( intP );
					break;
				}
				case "intRangeNode": {
					IntegerRangeProperty intP = (IntegerRangeProperty)context.convertAnother( node, IntegerRangeProperty.class, new IntegerRangePropertyConverter() );
					node.addProperty( intP );
					break;
				}
				case "doubleNode":
					DoubleProperty doubleP = (DoubleProperty)context.convertAnother( node, DoubleProperty.class, new DoublePropertyConverter() );
					node.addProperty( doubleP );
					break;
				case "stringNode": {
					StringProperty stringP = (StringProperty)context.convertAnother( node, StringProperty.class, new StringPropertyConverter() );
					node.addProperty( stringP );
					break;
				}
				case "stringListNode": {
					StringListProperty stringP = (StringListProperty)context.convertAnother( node, StringListProperty.class, new StringListPropertyConverter() );
					node.addProperty( stringP );
					break;
				}
				case "comboBoxNodeQuality":
					QualitySettingProperty qualityP = (QualitySettingProperty)context.convertAnother( node, QualitySettingProperty.class, new QualitySettingPropertyConverter() );
					node.addProperty( qualityP );
					break;
				case "colorNode":
					ColorProperty colorP = (ColorProperty)context.convertAnother( node, ColorProperty.class, new ColorPropertyConverter() );
					node.addProperty( colorP );
					break;
				default:
					throw new UnsupportedOperationException( "Unknown type: " + nodeName );
			}
      reader.moveUp();
    }
    return node;
  }

}
