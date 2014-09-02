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
 * Class StringPropertyConverter
 * Erstellt 14.04.2008, 14:02:24
 */

package gui.propertysheet.types;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import gui.propertysheet.abs.AbstractPropertyConverter;

/**
 * A property that allows to store one {@code String}.
 * @author Jan-Philipp Kappmeier
 */
public class StringPropertyConverter extends AbstractPropertyConverter<StringProperty, String> {

	public boolean canConvert( Class type ) {
		return type.equals( StringProperty.class );
	}

	@Override
	public String getNodeName() {
		return "stringNode";
	}

	@Override
	public void createNewProp() {
		prop = new StringProperty();
	}

	@Override
	public void writeValue( MarshallingContext context ) {
		context.convertAnother( prop.getValue() );
	}

	@Override
	public void readValue( UnmarshallingContext context ) {
		String string = (String)context.convertAnother( prop, String.class );
		prop.setValue( string );
	}
}
