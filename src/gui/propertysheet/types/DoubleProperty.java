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
 * Class DoubleProperty
 * Created 11.04.2008, 18:26:57
 */

package gui.propertysheet.types;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import gui.propertysheet.BasicProperty;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias( "doubleNode" )
@XStreamConverter( IntegerPropertyConverter.class )
@SuppressWarnings( "serial" )
public class DoubleProperty extends BasicProperty<Double> {
	
	public DoubleProperty() {
		setValue( 1.0 );
		setType( Double.class );
	}
}
