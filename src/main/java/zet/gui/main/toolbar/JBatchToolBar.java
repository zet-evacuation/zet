/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package zet.gui.main.toolbar;

import org.zetool.common.localization.Localization;
import org.zetool.common.localization.Localized;
import gui.GUIControl;
import gui.ZETLoader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JToolBar;
import org.zetool.components.framework.Button;
import zet.gui.GUILocalization;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JBatchToolBar extends JToolBar implements ActionListener, Localized {
	private final GUIControl control;
	/** The localization class. */
	static final Localization loc = GUILocalization.loc;
	private JButton btnExit;
	private JButton btnSaveResults;
	private JButton btnOpenResults;

	public JBatchToolBar( GUIControl control ) {
		this.control = control;
		createBatchToolBar();
	}

/**
	 * Creates the {@code JToolBar} for the batch panel.
	 */
	private void createBatchToolBar() {
		loc.setPrefix( "gui.toolbar." );

		btnExit = Button.newButton( ZETIconSet.Exit.icon(), this, "exit", loc.getString( "Exit" ) );
		add( btnExit );
		addSeparator();

		btnOpenResults = Button.newButton( ZETIconSet.Open.icon(), this, "loadBatchResult", loc.getString( "Open" ) );
		add( btnOpenResults );
		btnSaveResults = Button.newButton( ZETIconSet.Save.icon(), this, "saveResultAs", loc.getString( "Save" ) );
		add( btnSaveResults );
		loc.setPrefix( "" );
	}

	public void actionPerformed( ActionEvent e ) {
		if( e.getActionCommand().equals( "exit" ) ) {
			// quits the program
			control.exit();
		} else if( e.getActionCommand().equals( "loadBatchResult" ) ) {
			// nothing. see source code in JZETMenuBar
		} else if( e.getActionCommand().equals( "saveResultsAs" ) ) {
			// nothing. see source code in JZETMenuBar
		} else
			ZETLoader.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
	}

	public void localize() {
		loc.setPrefix( "gui.toolbar." );
		btnExit.setToolTipText( loc.getString( "Exit" ) );
		btnOpenResults.setToolTipText( loc.getString( "Open" ) );
		btnSaveResults.setToolTipText( loc.getString( "Save" ) );
		loc.clearPrefix();
	}


}
