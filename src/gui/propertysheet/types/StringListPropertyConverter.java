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
 * Class StringListPropertyConverter
 * Created 14.04.2008, 20:42:46
 */

package gui.propertysheet.types;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import gui.propertysheet.abs.AbstractPropertyConverter;
import java.util.ArrayList;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class StringListPropertyConverter extends AbstractPropertyConverter<StringListProperty, ArrayList<String>>  {

	public boolean canConvert( Class type ) {
		return type.equals( StringListProperty.class );
	}

	@Override
	public String getNodeName() {
		return "stringListNode";
	}

	@Override
	public void createNewProp() {
		prop = new StringListProperty();
	}

	@Override
	public void writeValue( MarshallingContext context ) {
		context.convertAnother( prop.getValue() );
	}

	@Override
	public void readValue( UnmarshallingContext context ) {
		ArrayList<String> string = (ArrayList<String>)context.convertAnother( prop, ArrayList.class );
		prop.setValue( string );
	}

}
