/**
 * Class CreditsDialog
 * Erstellt 18.05.2008, 20:03:38
 */

package gui;

import java.awt.Frame;
import javax.swing.JDialog;
import localization.Localization;

/**
 * A window that contains a {@link CreditsPanel} to represent the
 * credits for zet.
 * @author Jan-Philipp Kappmeier
 */
public class CreditsDialog extends JDialog {
	/**
	 * Creates the window and the {@link CreditsPanel}. The window has the
	 * program title and the version as defined by {@link EditorStart.version} as
	 * title, is modal and centered in the parent window.
	 * @param parent the parent window
	 */
	public CreditsDialog( Frame parent ) {
		super(parent, Localization.getInstance().getString( "AppTitle" ) + " v" + EditorStart.version, true);
		setSize( 480, 360 );
		setLocation ( parent.getX () + ((parent.getWidth() - getWidth()) / 2), parent.getY () + ((parent.getHeight() - getHeight()) / 2));
		CreditsPanel credits = new CreditsPanel();
		credits.setSize( 480, 360 );
		credits.startAnimation();
		add( credits );
	}
}
