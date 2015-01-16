/**
 * JLogToolBar.java
 * Created: 22.07.2010 11:42:41
 */
package zet.gui.main.toolbar;

import org.zetool.common.localization.Localization;
import org.zetool.common.localization.Localized;
import gui.GUIControl;
import de.tu_berlin.math.coga.components.framework.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JToolBar;
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
