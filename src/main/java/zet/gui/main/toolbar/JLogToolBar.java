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
public class JLogToolBar extends JToolBar implements ActionListener, Localized {

	private final GUIControl control;
	/** The localization class. */
	static final Localization loc = GUILocalization.loc;
	private JButton btnExit;

	public JLogToolBar( GUIControl control ) {
		this.control = control;
		createLogToolBar();
	}

	/**
	 * Creates the {@code JToolBar} for the log view.
	 */
	private void createLogToolBar() {
		loc.setPrefix( "gui.toolbar." );
		btnExit = Button.newButton( ZETIconSet.Exit.icon(), this, "", loc.getString( "Exit" ) );
		add( btnExit );

		loc.setPrefix( "" );
	}

	public void actionPerformed( ActionEvent e ) {
		control.exit();
	}

	public void localize() {
		btnExit.setToolTipText( loc.getString( "gui.toolbar.Exit" ) );
	}
}
