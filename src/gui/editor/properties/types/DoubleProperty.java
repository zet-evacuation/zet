/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/**
 * Class DoubleProperty
 * Erstellt 11.04.2008, 18:26:57
 */

package gui.editor.properties.types;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import gui.editor.properties.converter.IntegerPropertyConverter;
import gui.editor.properties.framework.AbstractPropertyValue;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import localization.Localization;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias( "doubleNode" )
@XStreamConverter( IntegerPropertyConverter.class )
public class DoubleProperty extends AbstractPropertyValue<Double> {
	final JTextField textBox = new JTextField();
	
	public DoubleProperty() {
		setValue( 1.0 );
	}
	
	public JPanel getPanel() {
		JPanel panel = new JPanel();
		JLabel label = new JLabel( getName() );
		textBox.setText( Localization.getInstance().getFloatConverter().format( getValue() ) );
		textBox.setMinimumSize( new Dimension(100, textBox.getMinimumSize().height));
		textBox.setPreferredSize( new Dimension(100, textBox.getPreferredSize().height));
		textBox.addKeyListener( new KeyListener() {
			public void keyTyped( KeyEvent e ) { }
			public void keyPressed( KeyEvent e ) { }
			public void keyReleased( KeyEvent e ) {
				updateModel();
			}
		} );
		panel.add( label );
		panel.add( textBox );
		return panel;
	}

	@Override
	protected void updateModel() {
		try {
			double val = Localization.getInstance().getFloatConverter().parse( textBox.getText() ).doubleValue();
			setValue( val );
		} catch( ParseException ex ) {
			System.out.println( "Parse Exception in IntegerProperty.java during double cast" );
			return;
		}
	}
}
