/**
 * JBatchToolBar.java
 * Created: 22.07.2010 11:46:08
 */
package zet.gui.main.toolbar;

import de.tu_berlin.math.coga.common.localization.Localized;
import gui.GUIControl;
import gui.ZETMain;
import de.tu_berlin.math.coga.components.framework.Button;
import gui.ZETLoader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JToolBar;
import zet.gui.GUILocalization;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JBatchToolBar extends JToolBar implements ActionListener, Localized {
	private final GUIControl control;
	/** The localization class. */
	static final GUILocalization loc = GUILocalization.getSingleton();
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

		btnExit = Button.newButton( IconSet.Exit.icon(), this, "exit", loc.getString( "Exit" ) );
		add( btnExit );
		addSeparator();

		btnOpenResults = Button.newButton( IconSet.Open.icon(), this, "loadBatchResult", loc.getString( "Open" ) );
		add( btnOpenResults );
		btnSaveResults = Button.newButton( IconSet.Save.icon(), this, "saveResultAs", loc.getString( "Save" ) );
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
