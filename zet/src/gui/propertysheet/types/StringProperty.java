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
 * Class StringProperty
 * Created 14.04.2008, 14:02:05
 */

package gui.propertysheet.types;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import gui.propertysheet.BasicProperty;
import gui.propertysheet.types.StringPropertyConverter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias( "stringNode" )
@XStreamConverter( StringPropertyConverter.class )
public class StringProperty extends BasicProperty<String> {
	
	public StringProperty() {
		setValue( "" );
		setType( String.class );
	}

}
