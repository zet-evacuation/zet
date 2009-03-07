/**
 * Class JOptionsWindow
 * Erstellt 22.04.2008, 18:34:20
 */

package gui.editor.properties;

import event.EventServer;
import event.OptionsChangedEvent;
import gui.components.framework.Button;
import info.clearthought.layout.TableLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import localization.Localization;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JOptionsWindow extends JAbstractPropertyWindow {
	
	/**
	 * Creates a new instance  of the dialog. It loads the file "zetoptions.xml"
	 * and shows the property tree.
	 * @param owner the owner of the window
	 * @param title the title
	 * @param width the width
	 * @param height the height
	 * @param file the options file
	 */
	public JOptionsWindow( JFrame owner, String title, int width, int height, String file ) {
		super( owner, title, width, height, file );
		setDefaultConfigFile( "optionsWorking.xml" );
	}

	public JOptionsWindow( JFrame owner, String title, int width, int height ) {
		super( owner, title, width, height, "optionsWorking.xml" );
		setDefaultConfigFile( "optionsWorking.xml" );
	}
	
	@Override
	protected JPanel createButtonPanel() {
		// Create Buttons
		JPanel buttonPanel = new JPanel( );
		JButton btnQuit = Button.newButton( Localization.getInstance().getString( "gui.Quit" ), aclButton, "quit"  );
		JButton btnOK = Button.newButton( Localization.getInstance().getString( "gui.OK" ), aclButton, "ok"  );
		double size2[][] = { {TableLayout.FILL, TableLayout.PREFERRED, space, TableLayout.PREFERRED, space }, {TableLayout.PREFERRED, space} };
		buttonPanel.setLayout( new TableLayout( size2 ) );
		buttonPanel.add( btnQuit, "1,0" );
		buttonPanel.add( btnOK, "3,0" );
		return buttonPanel;
	}

	@Override
	public void performOK() {
		EventServer.getInstance().dispatchEvent( new OptionsChangedEvent<JOptionsWindow>( this ) );
	}
}
