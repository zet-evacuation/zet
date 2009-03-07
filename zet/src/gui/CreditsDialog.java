/**
 * Class CreditsDialog
 * Erstellt 18.05.2008, 20:03:38
 */

package gui;

import java.awt.Frame;
import javax.swing.JDialog;

/**
 * A window that contains a {@link CreditsPanel} to represent the
 * credits for zet.
 * @author Jan-Philipp Kappmeier
 */
public class CreditsDialog extends JDialog {
	/**
	 * Creates the window and the panel.
	 * @param parent the parent window
	 */
	public CreditsDialog( Frame parent ) {
		super(parent, true);
		setSize( 320, 240 );
		setLocation ( parent.getX () + ((parent.getWidth() - getWidth()) / 2),
			parent.getY () + ((parent.getHeight() - getHeight()) / 2));
		
		CreditsPanel credits = new CreditsPanel();
		credits.setSize( 640, 480 );
		credits.startAnimation();
		add( credits );
	}
}
