/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zet.gui.main.menu.popup;

import de.tu_berlin.math.coga.components.framework.Menu;
import ds.z.Assignment;
import ds.z.AssignmentType;
import ds.z.PlanPolygon;
import gui.GUIControl;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import zet.gui.GUILocalization;
import zet.gui.main.menu.popup.PolygonPopupListener;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PolygonPopup extends JPopupMenu {
	/** The localization class. */
	private GUILocalization loc = GUILocalization.getSingleton();;
	/** All JPolygons share the same pop-up menu listeners, which are stored here. */
	private List<PolygonPopupListener> polygonPopupListeners = new LinkedList<>();
	private GUIControl guiControl;

	public PolygonPopup( GUIControl control) {
		super();
		this.guiControl = control;
	}
	
	public void recreate( Assignment assignment ) {
		polygonPopupListeners.clear();
		removeAll();
		
		JMenu jmnCreateAssArea = Menu.addMenu( this, loc.getString( "gui.editor.JEditorPanel.popupDefaultAssignmentArea" ) );
		jmnCreateAssArea.setEnabled( assignment != null );
		if( assignment != null ) {
			System.out.println( assignment.toString() );
			PolygonPopupListener listener;
			for( AssignmentType t : assignment.getAssignmentTypes() ) {
				listener = new PolygonPopupListener( t , guiControl );
				polygonPopupListeners.add( listener );
				Menu.addMenuItem( jmnCreateAssArea, t.getName(), listener );
			}
		}		
	}

	/** This method should be called every time before the JPolygon pop-up menu
	 * is shown.
	 * @param currentPolygon The PlanPolygon that is displayed by the JPolygon
	 * on which the PopupMenu shall be shown. */
	public void setPopupPolygon( PlanPolygon<?> currentPolygon ) {
		System.out.println( "Popup now belongs to " + currentPolygon.toString() );
		for( PolygonPopupListener p : polygonPopupListeners )
			p.setPolygon( currentPolygon );
	}
}
