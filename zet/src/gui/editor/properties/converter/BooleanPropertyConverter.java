/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
 * Class BoolNodeConverter
 * Erstellt 09.04.2008, 22:08:24
 */

package gui.editor.properties.converter;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import gui.editor.properties.framework.AbstractPropertyConverter;
import gui.editor.properties.types.BooleanProperty;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BooleanPropertyConverter extends AbstractPropertyConverter<BooleanProperty, Boolean> {

	public boolean canConvert( Class type ) {
		return type.equals( BooleanProperty.class );
	}

	public String getNodeName() {
		return "boolNode";
	}
	
	public void createNewProp() {
		prop = new BooleanProperty();
	}
	
	public void writeValue( MarshallingContext context ) {
		context.convertAnother( new Boolean( prop.getValue() ) );
	}

	public void readValue( UnmarshallingContext context ) {
		Boolean bool = (Boolean)context.convertAnother( prop, Boolean.class );
		prop.setValue( bool );
	}
}
