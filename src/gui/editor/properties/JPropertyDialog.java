
package gui.editor.properties;

import de.tu_berlin.math.coga.zet.ZETLocalization2;
import ds.PropertyContainer;
import gui.ZETProperties;
import gui.propertysheet.JOptionsDialog;
import gui.propertysheet.PropertyTreeModel;
import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.zetool.components.framework.Button;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings( "serial" )
public class JPropertyDialog extends JOptionsDialog {

	public JPropertyDialog( PropertyTreeModel ptm ) {
		super( ptm );

		int space = 10;
		JPanel buttonPanel = new JPanel( );
		JButton btnOK = Button.newButton( ZETLocalization2.loc.getString( "gui.OK" ), getDefaultButtonsListener(), "ok"  );
		JButton btnCancel = Button.newButton( ZETLocalization2.loc.getString( "gui.Cancel" ), getDefaultButtonsListener(), "cancel" );
		double size2[][] = { {TableLayout.FILL, TableLayout.PREFERRED, space, TableLayout.PREFERRED, space, TableLayout.PREFERRED, space }, {space, TableLayout.PREFERRED, space } };
		buttonPanel.setLayout( new TableLayout( size2 ) );

		PropertyFilesSelectionModel pfsm = new PropertyFilesSelectionModel();
		pfsm.setSelectedItem( ZETProperties.getCurrentPropertyFile() );
		final JPropertyComboBox jpc = new JPropertyComboBox( pfsm );
		jpc.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed( ActionEvent e ) {
				PropertyListEntry entry = (PropertyListEntry)jpc.getSelectedItem();
				try {
					PropertyTreeModel ptm2 = PropertyContainer.getInstance().applyParameters( entry.getFile() );
					init( ptm2 );
				} catch( PropertyLoadException ex ) {
					ex.printStackTrace();
				}
			}
		});
		btnOK.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed( ActionEvent e ) {
				try {
					ZETProperties.setCurrentProperty( jpc.getSelectedFile().toPath() );
				} catch( PropertyLoadException ex ) {
					System.out.println( "Could not set the new Properties as current." );
				}
			}
		});

		buttonPanel.add( jpc, "1,1" );
		buttonPanel.add( btnOK, "3,1" );
		buttonPanel.add( btnCancel, "5,1" );
		add( buttonPanel, BorderLayout.SOUTH );
	}
}
