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
		JPanel panel = new JPanel();
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