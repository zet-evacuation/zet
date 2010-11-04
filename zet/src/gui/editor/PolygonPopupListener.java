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
 * PolygonPopupListener.java
 * Created on 28. Dezember 2007, 13:11
 */
package gui.editor;

import ds.z.AssignmentArea;
import ds.z.AssignmentType;
import ds.z.PlanPoint;
import ds.z.PlanPolygon;
import ds.z.Room;
import gui.GUIControl;
import zet.gui.JEditor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * This popup listener is responsible for handling menu events
 * @author Timon Kelter, Jan-Philipp Kappmeier
 */
public class PolygonPopupListener implements ActionListener {
	private AssignmentType myType;
	private PlanPolygon myPolygon;
	private final GUIControl guiControl;


	public PolygonPopupListener( AssignmentType myType, GUIControl guiControl ) {
		this.guiControl = guiControl;
		this.myType = myType;
	}

	/** This method should be called every time before the popup is shown. 
	 * @param currentPolygon The PlanPolygon on which the popup is shown.
	 */
	public void setPolygon( PlanPolygon currentPolygon ) {
		myPolygon = currentPolygon;
	}

	public void actionPerformed( ActionEvent e ) {
		List<PlanPoint> points = myPolygon.getPolygonPoints();
		ArrayList<PlanPoint> newPoints = new ArrayList<PlanPoint>();

		for( int i = 0; i < points.size(); i++ )
			newPoints.add( new PlanPoint( points.get( i ).getXInt(), points.get( i ).getYInt() ) );
		AssignmentArea newAA = new AssignmentArea( (Room)myPolygon, myType );
		newAA.replace( newPoints );

		//JEditor.getInstance().showArea( AreaVisibility.ASSIGNMENT );
		guiControl.showArea( AreaVisibility.Assignment );
		guiControl.setSelectedPolygon( newAA );
	}
}
