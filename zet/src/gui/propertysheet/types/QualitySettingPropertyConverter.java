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
 * Erstellt 05.11.2009, 17:42:17
 */

package gui.propertysheet.types;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import gui.propertysheet.abs.AbstractPropertyConverter;
import gui.visualization.QualityPreset;

/**
 * A converter that reads and writes {@link QualitySettingProperty} to
 * XML-files.
 * @author Jan-Philipp Kappmeier
 */
public class QualitySettingPropertyConverter extends AbstractPropertyConverter<QualitySettingProperty, QualityPreset> {

	@Override
	public String getNodeName() {
		return "comboBoxNodeQuality";
	}

	@Override
	public void createNewProp() {
		prop = new QualitySettingProperty();
	}

	@Override
	public void writeValue( MarshallingContext context ) {
		context.convertAnother( prop.getValue() );
	}

	@Override
	public void readValue( UnmarshallingContext context ) {
		QualityPreset qp = (QualityPreset)context.convertAnother( prop, QualityPreset.class );
		prop.setValue( qp );

	}

	public boolean canConvert( Class type ) {
		return type.equals( QualitySettingProperty.class );
	}

}
