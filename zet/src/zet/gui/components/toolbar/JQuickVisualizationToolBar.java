/**
 * JQuickVisualizationToolBar.java
 * Created: 22.07.2010 12:18:04
 */
package zet.gui.components.toolbar;

import de.tu_berlin.math.coga.common.localization.DefaultLoc;
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
public class JQuickVisualizationToolBar extends JToolBar implements ActionListener, Localized {
	private final GUIControl control;
	/** The localization class. */
	static final Localization loc = DefaultLoc.getSingleton();
	private JButton btnExit;

	public JQuickVisualizationToolBar( GUIControl control ) {
		this.control = control;
		createQuickVisualizationToolBar();
	}

	/**
	 * Creates the <code>JToolBar</code> for the quick visualization.
	 */
	private void createQuickVisualizationToolBar() {
		loc.setPrefix( "gui.editor.JEditor." );

		btnExit = Button.newButton( IconSet.Exit, this, "exit", loc.getString( "toolbarTooltipExit" ) );
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