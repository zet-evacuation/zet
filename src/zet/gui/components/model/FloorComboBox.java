/**
 * FloorComboBox.java
 * Created: 16.02.2012, 14:32:42
 */
package zet.gui.components.model;

import de.tu_berlin.coga.zet.model.AbstractFloor;
import java.awt.Component;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;

/**
 * @param <U>
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings( "serial" )
public class FloorComboBox<U extends AbstractFloor> extends JComboBox<U>{

	public FloorComboBox() {
		super( new DefaultComboBoxModel<U>() );
		setRenderer( new FloorComboBoxRenderer() );
	}

	public void clear() {
		((DefaultComboBoxModel<U>)getModel()).removeAllElements();

	}

	public void setSelectedItem( U selectedItem ) {
		getModel().setSelectedItem( selectedItem );
	}

	public void displayFloors( Iterable<U> p ) {
		displayFloors( p, false );
	}

	public void displayFloors( Iterable<U> p, boolean skipFirst ) {
		clear();
		boolean first = true;
		if( p != null )
			for( U f : p )
				if( !(skipFirst && first) ) {
					((DefaultComboBoxModel<U>)getModel()).addElement( f );
					first = false;
				} else
					first = false;
	}

	private class FloorComboBoxRenderer extends ComboBoxRenderer<U> {
		@Override
		public Component getListCellRendererComponent( JList<? extends U> list, U value, int index, boolean isSelected, boolean cellHasFocus ) {
				super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus ); // Needed for correct displaying! Forget return
				if( value != null )
					setText( value.getName() );
				return this;
		}
	}
}

