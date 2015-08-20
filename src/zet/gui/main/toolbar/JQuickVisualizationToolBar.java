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
public class JQuickVisualizationToolBar extends JToolBar implements ActionListener, Localized {
	private final GUIControl control;
	/** The localization class. */
	static final Localization loc = GUILocalization.loc;
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
		btnExit = Button.newButton( ZETIconSet.Exit.icon(), this, "exit", loc.getString( "Exit" ) );
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
