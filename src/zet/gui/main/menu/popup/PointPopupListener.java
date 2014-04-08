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
 * JPolygonPopupListener.java
 * Created on 28. Dezember 2007, 13:11
 */
package zet.gui.main.menu.popup;

import de.tu_berlin.coga.zet.model.Edge;
import de.tu_berlin.coga.zet.model.PlanPoint;
import de.tu_berlin.coga.zet.model.PlanPolygon;
import de.tu_berlin.coga.zet.model.ZControl;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This pop-up listener is responsible for handling menu events
 * @author Timon Kelter, Jan-Philipp Kappmeier
 */
public class PointPopupListener implements ActionListener {
	/** The control class for changing the z data structure. */
	ZControl projectControl;
	/** The edge on which the point lies. */
	private Edge currentEdge;
	/** The point for which the pop-up is used. */
	private PlanPoint currentPoint;

	public PointPopupListener( ZControl projectControl ) {
		this.projectControl = projectControl;
	}

	/**
	 * This method should be called every time before the pop-up is shown.
	 * @param currentEdge one edge to that the point is adjacent
	 * @param currentPoint the PlanPoint on which the pop-up is shown.
	 */
	public void setPoint( Edge currentEdge, PlanPoint currentPoint ) {
		this.currentEdge = currentEdge;
		this.currentPoint = currentPoint;
	}

	/**
	 * This method contains the event code that is executed when certain
	 * action commands (defined at the menu creation) are invoked.
	 */
	public void actionPerformed( ActionEvent e ) {
		if( e.getActionCommand().equals( "deletePoint" ) ) {
			PlanPolygon poly = currentEdge.getAssociatedPolygon();
			projectControl.deletePoint( poly, currentPoint );
		}
	}
}
