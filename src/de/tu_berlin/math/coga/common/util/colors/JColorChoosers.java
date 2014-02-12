/**
 * JColorC.java
 * Created: 12.02.2014, 13:12:08
 */
package de.tu_berlin.math.coga.common.util.colors;

import java.awt.Color;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JColorChooser;
import static javax.swing.JColorChooser.createDialog;
import javax.swing.JDialog;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JColorChoosers {

	/**
	 * Displays a modal color {@link JColorChoosers} that only allows to use the
	 * RGB color table.
	 * @param component the parent component
	 * @param title the title of the dialog window
	 * @param initialColor the color which the chooser is initialized to
	 * @return the chosen color if any is select, the original color else
	 * @throws HeadlessException if {@link java.awt.GraphicsEnvironment.isHeadless()} returns {@code true}
	 */
	public static Color showRGBColorChooser( Component component, String title, Color initialColor ) throws HeadlessException {
		JColorChooser colorChooser = new RGBColorChooser( initialColor );
		OKListener ok = new OKListener( colorChooser );
		JDialog dialog = createDialog( component, title, true, colorChooser, ok, null );
		dialog.setVisible( true );
		dialog.dispose();
		return ok.getColor();
	}

	/**
	 * Private constructor avoids instantiation of utility class.
	 */
	private JColorChoosers() { }

	private static class OKListener implements ActionListener {
    private final JColorChooser colorChooser;
    private Color color;

		OKListener( JColorChooser cc ) {
        colorChooser = cc;
        color = cc.getColor();
    }

		@Override
    public void actionPerformed(ActionEvent e) {
			color = colorChooser.getColor();
    }

    Color getColor() {
        return color;
    }
	}
}
