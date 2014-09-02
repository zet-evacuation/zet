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
 * Erstellt 05.09.2009, 17:42:52
 */

package gui.propertysheet.types;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import gui.propertysheet.BasicProperty;
import gui.propertysheet.types.QualitySettingPropertyConverter;
import gui.visualization.QualityPreset;
import javax.swing.JPanel;


/**
 * A property that allows to switch the quality of visualization. The quality
 * is stored in an enumeration {@link QualityPreset}. There, several presets are
 * defined. The property allows to store one of the presets and provides a
 * {@link JPanel} containing a combo box to select the quality.
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias( "comboBoxNodeQuality" )
@XStreamConverter( QualitySettingPropertyConverter.class )
public class QualitySettingProperty extends BasicProperty<QualityPreset> {

	/**
	 * Creates a panel containing explaining labels and a combo box for the
	 * provided qualities.
	 * @return the panel to select a quality.
	 */
//	@Override
//	public JPanel getPanel() {
//		JPanel panel = new JPanel();
//		JLabel label = new JLabel( getDisplayName() );
//
//		comboBox.setMinimumSize( new Dimension( 100, comboBox.getMinimumSize().height) );
//		comboBox.setPreferredSize( new Dimension( 250, comboBox.getPreferredSize().height) );
//		comboBox.addItem( QualityPreset.LowQuality );
//		comboBox.addItem( QualityPreset.MediumQuality );
//		comboBox.addItem( QualityPreset.HighQuality );
//		comboBox.addItem( QualityPreset.VeryHighQuality );
//		comboBox.setSelectedItem( getValue() );
//		comboBox.addActionListener( new ActionListener() {
//
//			public void actionPerformed( ActionEvent arg0 ) {
//				updateModel();
//			}
//		});
//		panel.add( label );
//		panel.add( comboBox );
//		return panel;
//	}

	/**
	 * Updates the underlying model, that means the selected quality in the
	 * combo box is stored.
	 */
//	@Override
//	protected void updateModel() {
//		setValue( (QualityPreset)comboBox.getSelectedItem() );
//	}
//
//	@Override
//	public BasicProperty getProperty() {
//		BasicProperty def = new BasicProperty();
//		def.setType( String.class );
//		def.setValue( "" );
//		return def;
//
//	}

}
