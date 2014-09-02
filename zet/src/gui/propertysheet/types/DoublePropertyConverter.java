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
 * Class DoublePropertyConverter
 * Created 11.04.2008, 18:28:27
 */

package gui.propertysheet.types;

import gui.propertysheet.abs.AbstractPropertyConverter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class DoublePropertyConverter extends AbstractPropertyConverter<DoubleProperty, Double>{

	public boolean canConvert( Class type ) {
		return type.equals( DoubleProperty.class );
	}

	@Override
	public String getNodeName() {
		return "doubleNode";
	}

	@Override
	public void createNewProp() {
		prop = new DoubleProperty();
	}

	@Override
	public void writeValue( MarshallingContext context ) {
		context.convertAnother( new Double( prop.getValue() ) );
	}

	@Override
	public void readValue( UnmarshallingContext context ) {
		Double doubleV = (Double)context.convertAnother( prop, Double.class );
		prop.setValue( doubleV );
	}
}
