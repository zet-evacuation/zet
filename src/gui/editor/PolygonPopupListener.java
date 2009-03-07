/*
 * PolygonPopupListener.java
 *
 * Created on 28. Dezember 2007, 13:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gui.editor;

import ds.z.AssignmentArea;
import ds.z.AssignmentType;
import ds.z.PlanPoint;
import ds.z.PlanPolygon;
import ds.z.Room;
import gui.JEditor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/** This popup listener is responsible for handling menu events
 *
 * @author Timon Kelter
 */
public class PolygonPopupListener implements ActionListener {
	private AssignmentType myType;
	private PlanPolygon myPolygon;

	public PolygonPopupListener (AssignmentType myType) {
		this.myType = myType;
	}

	/** This method should be called every time before the popup is shown. 
	 * @param currentPolygon The PlanPolygon on which the popup is shown.
	 */
	public void setPolygon (PlanPolygon currentPolygon) {
		myPolygon = currentPolygon;
	}

	public void actionPerformed (ActionEvent e) {
		List<PlanPoint> points = myPolygon.getPolygonPoints();
		ArrayList<PlanPoint> newPoints = new ArrayList<PlanPoint>();

		for( int i = 0; i < points.size(); i++) {
			newPoints.add (new PlanPoint (points.get (i).getXInt (), points.get (i).getYInt ()));
		}
		AssignmentArea newAA = new AssignmentArea ((Room)myPolygon, myType);
		newAA.replace( newPoints );

		JEditor.getInstance().showArea( AreaVisibility.ASSIGNMENT );
		JEditor.getInstance ().getEditView ().getFloor ().setSelectedPolygon (newAA);
	}
}