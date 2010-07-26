/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/*
 * JZoomableRuler.java
 * Created on 14. Dezember 2007, 00:47
 */
package zet.gui.components;

import de.tu_berlin.math.coga.components.JRuler;
import gui.editor.CoordinateTools;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

/**
 * Displays a {@link JRuler} that is capable to perform zooming.
 * @author Jan-Philipp Kappmeier
 */
public class JZoomableRuler extends JRuler implements ChangeListener {
	/** Creates a new instance of JZoomableRuler
	 * @param orientation
	 * @param unit 
	 */
	public JZoomableRuler( RulerOrientation orientation, RulerDisplayUnits unit ) {
		super( orientation, unit );
	}

	/**
	 * Handles Swing events sent to the floor. Components adding some of the displaying styles of the
	 * floor can send their events to the <code>JFloor</code> class directly.
	 * <p>The possibility to handle events sent by slider events is implemented. The slider needs to
	 * have positive values (0 is not allowed, though). A value of 100 implies a zoom factor of 1, that
	 * is displayed as 1mm for 1 pixel.</p>
	 */
	public void stateChanged( javax.swing.event.ChangeEvent e ) {
		Object s = e.getSource();
		if( s instanceof JSlider ) {
			JSlider source = (JSlider)e.getSource();
			if( !source.getValueIsAdjusting() ) {
				CoordinateTools.setZoomFactor( source.getValue() * 0.01 );
				setZoomFactor( source.getValue() * 0.01 );
				repaint();
			}
		}

	}
}
