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
 * Class BoolNode
 * Erstellt 09.04.2008, 22:05:39
 */
package gui.editor.properties.types;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import gui.editor.properties.converter.BooleanPropertyConverter;
import gui.editor.properties.framework.AbstractPropertyValue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias( "boolNode" )
@XStreamConverter( BooleanPropertyConverter.class )
public class BooleanProperty extends AbstractPropertyValue<Boolean> {
	final JCheckBox checkBox = new JCheckBox();

	public BooleanProperty() {
		setValue( false );
	}

	public JPanel getPanel() {
		final JPanel panel = new JPanel();
		checkBox.setText( this.getName() );
		checkBox.setSelected( getValue() );
		checkBox.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				updateModel();
			}
		} );
		panel.add( checkBox );
		return panel;
	}

	@Override
	protected void updateModel() {
		setValue( checkBox.isSelected() );
	}
}