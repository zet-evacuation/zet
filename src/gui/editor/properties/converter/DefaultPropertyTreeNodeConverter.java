/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
 * Erstellt 22.02.2008, 01:37:09
 */

package gui.editor.properties.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import gui.editor.properties.framework.AbstractPropertyValue;
import gui.editor.properties.types.BooleanProperty;
import gui.editor.properties.types.IntegerProperty;
import gui.editor.properties.types.DoubleProperty;
import gui.editor.properties.types.IntegerRangeProperty;
import gui.editor.properties.types.StringProperty;
import gui.editor.properties.types.StringListProperty;
import gui.editor.properties.PropertyTreeNode;
import gui.editor.properties.types.QualitySettingProperty;
import javax.swing.tree.DefaultMutableTreeNode;

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
  public boolean canConvert( Class type ) {
    return type.equals( PropertyTreeNode.class );
  }

  /**
   * Writes a node to the XML-file, beginning with writing the start node.
   * @param source
   * @param writer
   * @param context
   */
  public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
    PropertyTreeNode node = (PropertyTreeNode) source;
    writer.startNode( "treeNode" );
    writer.addAttribute( "name", node.getNameTag() );
    writer.addAttribute( "useAsLocString", Boolean.toString( node.isUsedAsLocString() ) );
    for( int i=0; i < node.getChildCount(); i++ ) {
      DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt( i );
      context.convertAnother( child );
    }
		for( AbstractPropertyValue property : node.getProperties()	) {
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
		}
    writer.endNode();
  }

  /**
   * Reads a treeNode element from a XML-file.
   * @param reader
   * @param context
   * @return
   */
  public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
    String name = reader.getAttribute( "name" );
    PropertyTreeNode node = new PropertyTreeNode( name );
    node.useAsLocString( (reader.getAttribute( "useAsLocString" ).equals("true")?true:false ) );
    while( reader.hasMoreChildren() ) {
      reader.moveDown();
			String nodeName = reader.getNodeName();
      if( nodeName.equals( "treeNode" ) ) {
        PropertyTreeNode child = (PropertyTreeNode)context.convertAnother( node, PropertyTreeNode.class, new DefaultPropertyTreeNodeConverter() );
        node.add( child );
      } else if( nodeName.equals( "boolNode" ) ) {
				BooleanProperty bool = (BooleanProperty)context.convertAnother( node, BooleanProperty.class, new BooleanPropertyConverter() );
				node.addProperty( bool );
			} else if( nodeName.equals( "intNode" ) ) {
				IntegerProperty intP = (IntegerProperty)context.convertAnother( node, IntegerProperty.class, new IntegerPropertyConverter() );
				node.addProperty( intP );
			} else if( nodeName.equals( "intRangeNode" ) ) {
				IntegerRangeProperty intP = (IntegerRangeProperty)context.convertAnother( node, IntegerRangeProperty.class, new IntegerRangePropertyConverter() );
				node.addProperty( intP );
			} else if( nodeName.equals( "doubleNode" ) ) {
				DoubleProperty doubleP = (DoubleProperty)context.convertAnother( node, DoubleProperty.class, new DoublePropertyConverter() );
				node.addProperty( doubleP );
			} else if( nodeName.equals( "stringNode" ) ) {
				StringProperty stringP = (StringProperty)context.convertAnother( node, StringProperty.class, new StringPropertyConverter() );
				node.addProperty( stringP );
			} else if( nodeName.equals( "stringListNode" ) ) {
				StringListProperty stringP = (StringListProperty)context.convertAnother( node, StringListProperty.class, new StringListPropertyConverter() );
				node.addProperty( stringP );
			} else if( nodeName.equals( "comboBoxNodeQuality" ) ) {
				QualitySettingProperty qualityP = (QualitySettingProperty)context.convertAnother( node, QualitySettingProperty.class, new QualitySettingPropertyConverter() );
				node.addProperty( qualityP );
			}
      reader.moveUp();
    }
    return node;
  }

}
