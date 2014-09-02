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
 * Class IntegerProperty
 * Created 11.04.2008, 00:04:36
 */

package gui.propertysheet.types;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import gui.propertysheet.BasicProperty;
import gui.propertysheet.types.IntegerPropertyConverter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias( "intNode" )
@XStreamConverter( IntegerPropertyConverter.class )
@SuppressWarnings( "serial" )
public class IntegerProperty extends BasicProperty<Integer> {

	public IntegerProperty() {
		setValue( 1 );
		setType( Integer.class );
	}
}
