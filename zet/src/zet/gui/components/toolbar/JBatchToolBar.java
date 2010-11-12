/**
 * JBatchToolBar.java
 * Created: 22.07.2010 11:46:08
 */
package zet.gui.components.toolbar;

import de.tu_berlin.math.coga.common.localization.DefaultLoc;
import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.common.localization.Localized;
import gui.GUIControl;
import gui.ZETMain;
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
public class JBatchToolBar extends JToolBar implements ActionListener, Localized {
	private final GUIControl control;
	/** The localization class. */
	static final Localization loc = DefaultLoc.getSingleton();
	private JButton btnExit;
	private JButton btnSaveResults;
	private JButton btnOpenResults;

	public JBatchToolBar( GUIControl control ) {
		this.control = control;
		createBatchToolBar();
	}

/**
	 * Creates the <code>JToolBar</code> for the batch panel.
	 */
	private void createBatchToolBar() {
		loc.setPrefix( "gui.editor.JEditor." );

		btnExit = Button.newButton( IconSet.Exit, this, "exit", loc.getString( "toolbarTooltipExit" ) );
		add( btnExit );
		addSeparator();

		btnOpenResults = Button.newButton( IconSet.Open, this, "loadBatchResult", loc.getString( "toolbarTooltipOpen" ) );
		add( btnOpenResults );
		btnSaveResults = Button.newButton( IconSet.Save, this, "saveResultAs", loc.getString( "toolbarTooltipSave" ) );
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
			ZETMain.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
	}

	public void localize() {
		btnExit.setToolTipText( loc.getString( "toolbarTooltipExit" ) );
		btnOpenResults.setToolTipText( loc.getString( "toolbarTooltipOpen" ) );
		btnSaveResults.setToolTipText( loc.getString( "toolbarTooltipSave" ) );

	}


}
