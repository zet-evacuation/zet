/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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

package de.tu_berlin.math.coga.zet.viewer;

import org.zetool.common.algorithm.AlgorithmEvent;
import org.zetool.common.algorithm.AlgorithmListener;
import org.zetool.common.algorithm.AlgorithmProgressEvent;
import org.zetool.common.util.Formatter;
import de.tu_berlin.math.coga.components.JStatusBar;
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

	/**
	 * Listener is called if the algorithm sends an event. When the algorithm
	 * runs in another thread (the {@link SwingWorker} thread), the progress bar
	 * updates itself during the runtime.
	 * @param event the event thrown. not only progress events. take care.
	 */
	public void eventOccurred( AlgorithmEvent event ) {
		if( event instanceof AlgorithmProgressEvent ) {
			final double progress = ((AlgorithmProgressEvent) event).getProgress();
			progressBar.setValue( (int)(progress*100) );
			progressBar.setToolTipText( Formatter.formatPercent( progress ) );
		}
	}
}
