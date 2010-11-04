/**
 * JLogToolBar.java
 * Created: 22.07.2010 11:42:41
 */
package zet.gui.components.toolbar;

import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.common.localization.Localized;
import gui.GUIControl;
import gui.components.framework.Button;
import gui.components.framework.IconSet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JLogToolBar extends JToolBar implements ActionListener, Localized {

	private final GUIControl control;
	/** The localization class. */
	static final Localization loc = Localization.getInstance();
	private JButton btnExit;

	public JLogToolBar( GUIControl control ) {
		this.control = control;
		createLogToolBar();
	}

	/**
	 * Creates the <code>JToolBar</code> for the log view.
	 */
	private void createLogToolBar() {
		loc.setPrefix( "gui.editor.JEditor." );
		//toolBarLog = new JToolBar();
		btnExit = Button.newButton( IconSet.Exit, this, "", loc.getString( "toolbarTooltipExit" ) );
		add( btnExit );

		loc.setPrefix( "" );
	}

	public void actionPerformed( ActionEvent e ) {
		control.exit();
	}

	public void localize() {
		btnExit.setToolTipText( loc.getString( "toolbarTooltipExit" ) );
	}
}
