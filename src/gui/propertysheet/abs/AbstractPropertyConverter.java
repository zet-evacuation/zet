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
 * Class AbstractPropertyConverter
 * Created 11.04.2008, 17:27:12
 */

package gui.propertysheet.abs;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import gui.propertysheet.BasicProperty;

/**
 *
 * @param <T> the property class
 * @param <U> the class of the property itself
 * @author Jan-Philipp Kappmeier
 */
public abstract class AbstractPropertyConverter<T extends BasicProperty<U>, U extends Object> implements Converter {
	protected T prop;

	public abstract String getNodeName();

	public abstract void createNewProp();

	public abstract void writeValue( MarshallingContext context );

	public abstract void readValue( UnmarshallingContext context );

	@Override
	public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
    prop = (T)source;
    writer.startNode( getNodeName() );
		writeAttributes( writer );
		writeValue( context );
    writer.endNode();
	}

	public void readAttributes( HierarchicalStreamReader reader ) {
    String name = reader.getAttribute( "name" );
		prop.setDisplayName( name );
    prop.useAsLocString( (reader.getAttribute( "useAsLocString" ).equals("true") ) );
		prop.setShortDescription( reader.getAttribute( "information" ) );
		prop.setName( reader.getAttribute( "parameter" ) );
	}

	public void writeAttributes( HierarchicalStreamWriter writer ) {
    writer.addAttribute( "name", prop.getDisplayNameTag() );
    writer.addAttribute( "useAsLocString", Boolean.toString( prop.isUsedAsLocString() ) );
		writer.addAttribute( "information", prop.getShortDescriptionTag() );
		writer.addAttribute( "parameter", prop.getName() );
	}

	@Override
	public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
		createNewProp();
		readAttributes( reader );
		readValue( context );
    return prop;
	}

}
