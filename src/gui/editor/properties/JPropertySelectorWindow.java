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
 * Class propertySelector.java
 * Erstellt 19.02.2008, 23:11:33
 */
package gui.editor.properties;

import ds.PropertyContainer;
import gui.components.framework.Button;
import gui.editor.properties.types.StringProperty;
import info.clearthought.layout.TableLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import de.tu_berlin.math.coga.common.localization.Localization;

/**
 *
 * @author Jan-Philipp Kappmeier, Martin Groß
 */
public class JPropertySelectorWindow extends JAbstractPropertyWindow {

	JPropertyComboBox cbx;

	/**
	 * Creates a new instance  of the dialog. It loads the file "properties.xml"
	 * and shows the property tree.
	 * @param owner the owner of the window
	 * @param title the title
	 * @param width the width
	 * @param height the height
	 * @param propertyFile 
	 */
	public JPropertySelectorWindow( JFrame owner, String title, int width, int height, String propertyFile ) {
		super( owner, title, width, height, propertyFile );
		PropertyTreeNode root = getRoot();
		StringProperty name = new StringProperty();
		name.setName( "Name der Eigenschaften" );
		name.setValue( getPropertyName() );
				root.clearProperties();
		root.addProperty( name );

		setDefaultConfigFile( "propertiesWorking.xml" );
		cbx.setSelectedItem( getPropertyName() );
	}

	public JPropertySelectorWindow( JFrame owner, String title, int width, int height ) {
		super( owner, title, width, height, "propertiesWorking.xml" );
		PropertyTreeNode root = getRoot();
		StringProperty name = new StringProperty();
		name.setName( "Name der Eigenschaften" );
		name.setValue( getPropertyName() );
				root.clearProperties();
		root.addProperty( name );
		setDefaultConfigFile( "propertiesWorking.xml" );
		cbx.setSelectedItem( getPropertyName() );
	}

	@Override
	protected JPanel createButtonPanel() {
		// Create Buttons
		JPanel buttonPanel = new JPanel();

		JButton btnOpen = Button.newButton( Localization.getInstance().getString( "gui.Open" ), aclButton, "open" );
		JButton btnSave = Button.newButton( Localization.getInstance().getString( "gui.Save" ), aclButton, "save" );
		JButton btnQuit = Button.newButton( Localization.getInstance().getString( "gui.Quit" ), aclButton, "quit" );
		JButton btnOK = Button.newButton( Localization.getInstance().getString( "gui.OK" ), aclButton, "ok" );
		double size2[][] = {{TableLayout.FILL, TableLayout.PREFERRED, space, TableLayout.PREFERRED, space, TableLayout.PREFERRED, space, TableLayout.PREFERRED, space, TableLayout.PREFERRED, space}, {TableLayout.PREFERRED, space}};
		buttonPanel.setLayout( new TableLayout( size2 ) );
		//buttonPanel.add( btnOpen, "3,0" );
		buttonPanel.add( btnSave, "5,0" );
		buttonPanel.add( btnQuit, "7,0" );
		buttonPanel.add( btnOK, "9,0" );

		cbx = new JPropertyComboBox( );
		cbx.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent e ) {
				File configFile = cbx.getSelectedFile();
				System.out.println( "Lade Datei " + configFile.getPath() );
				PropertyTreeModel propertyTreeModel;
				try {
					propertyTreeModel = PropertyContainer.loadConfigFile( configFile );
				} catch( PropertyLoadException ex ) {
					System.err.println( "Property-Datei wird nicht geladen!" );
					return;
				}
				setModel( propertyTreeModel );
				String ptname = propertyTreeModel.getPropertyName();
				PropertyTreeNode root = getRoot();
				StringProperty name = new StringProperty();
				name.setName( "Name der Eigenschaften" );
				name.setValue( ptname );
				root.clearProperties();
				root.addProperty( name );
			}
		} );

		buttonPanel.add( cbx, "1,0" );
		return buttonPanel;
	}

	@Override
	public void performOK() { }
}
