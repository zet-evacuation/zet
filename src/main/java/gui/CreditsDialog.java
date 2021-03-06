/* zet evacuation tool copyright © 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package gui;

import java.awt.Frame;
import javax.swing.JDialog;
import zet.gui.GUILocalization;

/**
 * A window that contains a {@link CreditsPanel} to represent the credits for zet evacuation tool.
 *
 * @author Jan-Philipp Kappmeier
 */
public class CreditsDialog extends JDialog {

    /**
     * Creates the window and the {@link CreditsPanel}. The window has the program title and the version as defined by
     * {@link ZETMain#version} as title, is modal and centered in the parent window.
     *
     * @param parent the parent window
     */
    public CreditsDialog(Frame parent) {
        super(parent, GUILocalization.loc.getString("AppTitle") + " v" + ZETMain.VERSION_FULL, true);
        setSize(640, 480);
        setLocation(parent.getX() + ((parent.getWidth() - getWidth()) / 2), parent.getY() + ((parent.getHeight() - getHeight()) / 2));
        CreditsPanel credits = new CreditsPanel();
        credits.setSize(480, 360);
        credits.startAnimation();
        add(credits);
    }
}
