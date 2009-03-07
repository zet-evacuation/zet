/**
 * Class StringListProperty
 * Erstellt 14.04.2008, 20:42:35
 */

package gui.editor.properties.types;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import gui.editor.properties.converter.StringListPropertyConverter;
import gui.editor.properties.framework.AbstractPropertyValue;
import info.clearthought.layout.TableLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias( "stringListNode" )
@XStreamConverter( StringListPropertyConverter.class )
public class StringListProperty extends AbstractPropertyValue<ArrayList<String>> {
	final DefaultListModel model = new DefaultListModel();
	final JList list = new JList( model );
	
	/**
	 * 
	 */
	public StringListProperty() {
		setValue( new ArrayList<String>() );
	}


	@Override
	public JPanel getPanel() {
		//throw new UnsupportedOperationException( "Not supported yet." );
		JPanel panel = new JPanel();

		int space = 16;
		double size[][] = {
			// Columns
			{ TableLayout.FILL, space, TableLayout.PREFERRED, space, TableLayout.PREFERRED, TableLayout.FILL },
			//Rows
			{
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				space,
				TableLayout.PREFERRED,
				TableLayout.FILL
			}
		};
		panel.setLayout( new TableLayout( size ) );		
		
		// create the elements
		final JTextField textBox = new JTextField();
		JLabel label = new JLabel( getName() );
		model.clear();
		for( String string : getValue() )
			model.addElement( string );
		JButton add = new JButton( "Add" );
		add.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent e ) {
				// Add the current text to the list, if it is not empty
				if( textBox.getText().equals( "" ) )
					return;
				model.addElement( textBox.getText() );
				updateModel();
			}
		});
		JButton delete = new JButton( "Delete");
		delete.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent e ) {
				// delete the current selected index, if it is not empty
				if( list.getSelectedIndex() == -1)
					return;
				model.removeElementAt( list.getSelectedIndex() );
				updateModel();
				if( model.size() > 0 )
					list.setSelectedIndex( 0 );
			}			
		});
		list.setVisibleRowCount( 8 );
		list.setMinimumSize( new Dimension( 130, 80  ) );
		list.setPreferredSize( new Dimension( 130, 80  ) );
		list.addListSelectionListener( new ListSelectionListener() {
      public void valueChanged( ListSelectionEvent e ) { 
        if ( e.getValueIsAdjusting() )
          return; 
				if( list.getSelectedIndex() != -1 )
					textBox.setText( model.get( list.getSelectedIndex() ).toString() );
      } 
    } );
		if( getValue().size() > 0 )
			list.setSelectedIndex( 0 );
		// Add the elements
		panel.add( label, "0, 0, 4, 0, left, top");
		panel.add( list, "0, 1, 0, 4" );
		panel.add( add, "2, 3" );
		panel.add( delete, "4, 3" );
		panel.add( textBox, "2, 1, 5, 1" );
		return panel;
	}
	
	protected void updateModel() {
		ArrayList<String> stringList = new ArrayList<String>();
		for( int i = 0; i < model.size(); i++ )
			stringList.add( (String)model.get( i ) );
		setValue( stringList );
	}
}
