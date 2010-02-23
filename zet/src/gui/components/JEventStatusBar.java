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
 * JEventStatusBar.java
 * Created on 19.12.2007, 02:16:58
 */

package gui.components;

import event.EventListener;
import event.EventServer;
import event.MessageEvent;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * The status bar for the Z-Editor. Consisting (basically) of 4 fields used to
 * display error- status- mouse- and editmode messages.
 * TODO: resize (has to be done in base class)
 * @author Jan-Philipp Kappmeier
 */
public class JEventStatusBar extends JStatusBar implements EventListener<MessageEvent> {
	private Timer blinkTimer;
	private Color blinkColor = Color.RED;
	private int blinkCount = 0;
			
	private Color labelBackground;
	
	/**
	 * Initializes an empty status bar.
	 */
	public JEventStatusBar () {
		super();
		EventServer.getInstance ().registerListener ( this, MessageEvent.class );
		addElement();
		addElement();
		rebuild();
		
		labelBackground = labels.get (0).getBackground ();
		
		blinkTimer = new Timer ( 200, new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				if (blinkCount == 0) {
					labels.get (0).setOpaque (true);
				}
				
				if (blinkCount < 6) {
					if (labels.get (0).getBackground () == labelBackground) {
						labels.get (0).setBackground (blinkColor);
					} else {
						labels.get (0).setBackground (labelBackground);
					}
					blinkCount++;
				} else {
					stopBlinking ();
				}
			}
		});
		blinkTimer.setInitialDelay (0);
		blinkTimer.setRepeats (true);
	}

	private void stopBlinking () {
		labels.get (0).setBackground (labelBackground);
		blinkTimer.stop ();
		blinkCount = 0;
		labels.get (0).setOpaque (false);
	}
	
	/**
	 * Handles events sent from the {@link event.EventServer}, if the event is of
	 * one of the types {@code Error}, {@code Status}, {@code MousePosition} or
	 * {@code EditMode} the message is displayed on one of the four initial
	 * elements.
	 * @param event
	 * @see event.MessageEvent.MessageType
	 */
	public void handleEvent ( MessageEvent event ) {
		switch( event.getType () ) {
			case Error:
				setStatusText ( 0, event.getMessage () );
				if (event.getMessage () != null && !event.getMessage ().equals ("")) {
					blinkTimer.start ();
				} else {
					stopBlinking ();
				}
				break;
			case Status:
				setStatusText ( 1, event.getMessage () );
				break;
			case MousePosition:
				setStatusText ( 2, event.getMessage () );
				break;
			//case EditMode:
			//	setStatusText ( 3, event.getMessage () );
			//	break;
		}
	}
}
