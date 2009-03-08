/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * JPolygonPopupListener.java
 * Created on 28. Dezember 2007, 13:11
 */

package gui.editor;

import ds.z.Edge;
import ds.z.PlanPoint;
import ds.z.PlanPolygon;
import event.EventServer;
import event.MessageEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** This popup listener is responsible for handling menu events
 *
 * @author Timon Kelter
 */
public class PointPopupListener implements ActionListener {
	private Edge myEdge;
	private PlanPoint myPoint;

	/** This method should be called every time before the popup is shown. 
	 * @param currentEdge The Edge on which the popup is shown.
	 * @param currentPoint The PlanPoint on which the popup is shown.
	 */
	public void setPoint (Edge currentEdge, PlanPoint currentPoint) {
		myEdge = currentEdge;
		myPoint = currentPoint;
	}

	/** This method contains the event code that is executed when certain 
	 * action commands (defined at the menu creation) are invoked. */
	public void actionPerformed (ActionEvent e) {
		if (e.getActionCommand ().equals ("deletePoint")) {
			try {
				PlanPolygon poly = myEdge.getAssociatedPolygon ();
				Edge neighbourEdge = myPoint.getOtherEdge (myEdge);

				poly.combineEdges (myEdge, neighbourEdge, true);
			} catch (RuntimeException ex) {
				EventServer.getInstance ().dispatchEvent (new MessageEvent (this, 
					MessageEvent.MessageType.Error, ex.getLocalizedMessage ()));
			}
		}
	}
}