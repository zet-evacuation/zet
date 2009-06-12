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
 * Class JOptionsWindow
 * Created 22.04.2008, 18:34:20
 */

package gui.editor.properties;

import event.EventServer;
import event.OptionsChangedEvent;
import gui.components.framework.Button;
import info.clearthought.layout.TableLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import localization.Localization;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JOptionsWindow extends JAbstractPropertyWindow {
	
	/**
	 * Creates a new instance  of the dialog. It loads the file "zetoptions.xml"
	 * and shows the property tree.
	 * @param owner the owner of the window
	 * @param title the title
	 * @param width the width
	 * @param height the height
	 * @param file the options file
	 */
	public JOptionsWindow( JFrame owner, String title, int width, int height, String file ) {
		super( owner, title, width, height, file );
		setDefaultConfigFile( "basezetoptions.xml" );
	}

	public JOptionsWindow( JFrame owner, String title, int width, int height, PropertyTreeModel ptm ) {
		super( owner, title, width, height, ptm );
	}

	public JOptionsWindow( JFrame owner, String title, int width, int height ) {
		super( owner, title, width, height, "basezetoptions.xml" );
		setDefaultConfigFile( "basezetoptions.xml" );
	}
	
	@Override
	protected JPanel createButtonPanel() {
		// Create Buttons
		JPanel buttonPanel = new JPanel( );
		JButton btnQuit = Button.newButton( Localization.getInstance().getString( "gui.Quit" ), aclButton, "quit"  );
		JButton btnOK = Button.newButton( Localization.getInstance().getString( "gui.OK" ), aclButton, "ok"  );
		double size2[][] = { {TableLayout.FILL, TableLayout.PREFERRED, space, TableLayout.PREFERRED, space }, {TableLayout.PREFERRED, space} };
		buttonPanel.setLayout( new TableLayout( size2 ) );
		buttonPanel.add( btnQuit, "1,0" );
		buttonPanel.add( btnOK, "3,0" );
		return buttonPanel;
	}

	@Override
	public void performOK() {
		EventServer.getInstance().dispatchEvent( new OptionsChangedEvent<JOptionsWindow>( this ) );
	}
}
