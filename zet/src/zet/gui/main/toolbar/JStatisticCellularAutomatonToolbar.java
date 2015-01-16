/**
 * JStatisticCellularAutomatonToolbar.java
 * Created: 22.07.2010 11:40:25
 */
package zet.gui.main.toolbar;

import org.zetool.common.localization.Localization;
import org.zetool.common.localization.Localized;
import gui.GUIControl;
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
public class JStatisticCellularAutomatonToolbar extends JToolBar implements ActionListener, Localized {

	private final GUIControl control;
	/** The localization class. */
	static final Localization loc = GUILocalization.loc;
	private JButton btnExit;
	private JButton btnOpenResults;
	private JButton btnSaveResults;

	public JStatisticCellularAutomatonToolbar( GUIControl control ) {
		this.control = control;
		createStatisticsToolBar();
	}

	private void createStatisticsToolBar() {
		loc.setPrefix( "gui.toolbar." );

		btnExit = Button.newButton( ZETIconSet.Exit.icon(), this, "exit", loc.getString( "Exit" ) );
		add( btnExit );
		addSeparator();

		btnOpenResults = Button.newButton( ZETIconSet.Open.icon(), null, "loadBatchResult", loc.getString( "Open" ) );
		add( btnOpenResults );
		btnSaveResults = Button.newButton( ZETIconSet.Save.icon(), null, "saveResultAs", loc.getString( "Save" ) );
		add( btnSaveResults );
		loc.setPrefix( "" );
	}

	public void actionPerformed( ActionEvent e ) {
		if( e.getActionCommand().equals( "exit" ) )
			// quits the program
			control.exit();
		else if( e.getActionCommand().equals( "loadBatchResult" ) ) {
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
