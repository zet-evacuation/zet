/**
 * FloorClickHandler.java
 * Created: Nov 2, 2012, 4:37:22 PM
 */
package zet.gui.main.tabs.editor;

import ds.z.PlanPoint;
import ds.z.Room;
import ds.z.ZControl;
import gui.editor.CoordinateTools;
import java.awt.Component;
import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import zet.gui.main.tabs.base.JPolygon;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class FloorClickHandler {
	private EditStatus editStatus;
	protected List<JPolygon> elementsUnderMouse;
	private final ZControl zcontrol;

	boolean mouseDown = false;

	protected FloorClickHandler( EditStatus editStatus, ZControl control ) {
		this.editStatus = editStatus;
		this.zcontrol = control;
		elementsUnderMouse = new LinkedList<>();
	}

	public void mouseDown( Point p, List<JPolygon> elements ) {
		editStatus.setLastClick( p );
		this.elementsUnderMouse = Collections.unmodifiableList( elements );
		mouseDown = true;
	}

	public void mouseUp( Point p, List<Component> components ) {
		mouseDown = false;
	}

	public void doubleClick( Point p, List<JPolygon> elements ) {

	}

	public void mouseMove( Point p ) {
		if( editStatus.isRasterizedPaintMode() )
			editStatus.setPointerPosition( editStatus.getNextRasterPoint( p ) );
		else
			editStatus.setPointerPosition( p );
	}

	public EditStatus getEditStatus() {
		return editStatus;
	}

	protected ZControl getZControl() {
		return zcontrol;
	}

	protected Room getRoomUnderMouse( Point currentMouse, List<Component> components  ) {
		for( Component c : components ) {
			if( c instanceof JPolygon ) {
				JPolygon poly = (JPolygon)c;
				if( poly.getPlanPolygon() instanceof Room ) {
					Room r = (Room)poly.getPlanPolygon();
					Point mousePosition = CoordinateTools.translateToModel( currentMouse );
					if( r.contains( new PlanPoint( mousePosition ) ) )
						return r;
				}
			}
		}
		return null;
	}

	protected List<Room> getRoomsUnderMouse( Point currentMouse, List<Component> components  ) {
		List<Room> ret = new LinkedList<>();
		for( Component c : components ) {
			if( c instanceof JPolygon ) {
				JPolygon poly = (JPolygon)c;
				if( poly.getPlanPolygon() instanceof Room ) {
					Room r = (Room)poly.getPlanPolygon();
					Point mousePosition = CoordinateTools.translateToModel( currentMouse );
					if( r.contains( new PlanPoint( mousePosition ) ) )
						ret.add( r );
				}
			}
		}
		return ret;
	}

	public void rightClick() { }
}
