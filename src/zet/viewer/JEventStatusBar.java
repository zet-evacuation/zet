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

/**
 * JEventStatusBar.java
 * Created: 16.03.2010, 11:04:02
 */

package zet.viewer;

import de.tu_berlin.math.coga.common.algorithm.AlgorithmEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmListener;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmProgressEvent;
import gui.components.JStatusBar;
import javax.swing.JProgressBar;

/**
 * @author Jan-Philipp Kappmeier
 */
public class JEventStatusBar extends JStatusBar implements AlgorithmListener {

	JProgressBar progressBar = new JProgressBar( 0, 100 );

	/**
	 * Initializes an empty status bar.
	 */
	public JEventStatusBar() {
		super();
		addElement( progressBar );
		rebuild();
	}

	public void eventOccurred( AlgorithmEvent event ) {
		if( event instanceof AlgorithmProgressEvent )
			progressBar.setValue( (int) (((AlgorithmProgressEvent) event).getProgress() * 100) );
	}
}
