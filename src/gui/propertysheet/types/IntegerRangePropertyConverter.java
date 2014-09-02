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
 * Class IntegerRangePropertyConverter
 * Created 11.04.2008, 17:58:57
 */

package gui.propertysheet.types;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class IntegerRangePropertyConverter extends IntegerPropertyConverter {

	@Override
	public boolean canConvert( Class type ) {
		return type.equals( IntegerRangeProperty.class );
	}

	@Override
	public String getNodeName() {
		return "intRangeNode";
	}
	
	@Override
	public void createNewProp() {
		prop = new IntegerRangeProperty();
	}
	
	@Override
	public void readAttributes( HierarchicalStreamReader reader ) {
		super.readAttributes( reader );
		((IntegerRangeProperty)prop).setMinValue( Integer.parseInt( reader.getAttribute( "minValue" ) ) );
		((IntegerRangeProperty)prop).setMaxValue( Integer.parseInt( reader.getAttribute( "maxValue" ) ) );
		((IntegerRangeProperty)prop).setMinorTick( Integer.parseInt( reader.getAttribute( "minorTick" ) ) );
		((IntegerRangeProperty)prop).setMajorTick( Integer.parseInt( reader.getAttribute( "majorTick" ) ) );
	}

	@Override
	public void writeAttributes( HierarchicalStreamWriter writer ) {
		super.writeAttributes( writer );
		writer.addAttribute( "minValue", Integer.toString( ((IntegerRangeProperty)prop).getMinValue() ) );
		writer.addAttribute( "maxValue", Integer.toString( ((IntegerRangeProperty)prop).getMaxValue() ) );
		writer.addAttribute( "minorTick", Integer.toString( ((IntegerRangeProperty)prop).getMinorTick() ) );
		writer.addAttribute( "majorTick", Integer.toString( ((IntegerRangeProperty)prop).getMajorTick() ) );
	}
}
