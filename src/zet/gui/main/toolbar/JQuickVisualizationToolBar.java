/**
 * JQuickVisualizationToolBar.java
 * Created: 22.07.2010 12:18:04
 */
package zet.gui.main.toolbar;

import de.tu_berlin.math.coga.common.localization.Localized;
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
public class JQuickVisualizationToolBar extends JToolBar implements ActionListener, Localized {
	private final GUIControl control;
	/** The localization class. */
	static final GUILocalization loc = GUILocalization.getSingleton();
	private JButton btnExit;

	public JQuickVisualizationToolBar( GUIControl control ) {
		this.control = control;
		createQuickVisualizationToolBar();
	}

	/**
	 * Creates the {@code JToolBar} for the quick visualization.
	 */
	private void createQuickVisualizationToolBar() {
		loc.setPrefix( "gui.toolbar." );
		btnExit = Button.newButton( IconSet.Exit.icon(), this, "exit", loc.getString( "Exit" ) );
		add( btnExit );
		loc.clearPrefix();
	}

	public void actionPerformed( ActionEvent e ) {
		control.exit();
	}

	public void localize() {
		btnExit.setToolTipText( loc.getString( "gui.toolbar.Exit" ) );
	}
}
