package zet.gui.main.tabs.editor;

import de.zet_evakuierung.model.PlanPoint;
import de.zet_evakuierung.model.Room;
import de.zet_evakuierung.model.ZControl;
import gui.editor.CoordinateTools;
import java.awt.Component;
import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import zet.gui.main.tabs.base.JPolygon;


/**
 * A default floor click hander. The handler enables context popup menus for
 * floor components by default.
 * @author Jan-Philipp Kappmeier
 */
public abstract class FloorClickHandler {
	private EditStatus editStatus;
	protected List<JPolygon> elementsUnderMouse;
	private final ZControl zcontrol;

	boolean mouseDown = false;

	protected FloorClickHandler( EditStatus editStatus, ZControl control ) {
		this.editStatus = Objects.requireNonNull( editStatus );
		this.zcontrol = Objects.requireNonNull( control );
		elementsUnderMouse = new LinkedList<>();
		editStatus.setPopupEnabled( true );
	}

	public void mouseDown( Point p, List<JPolygon> elements ) {
		editStatus.setLastClick( p );
		this.elementsUnderMouse = Collections.unmodifiableList( elements );
		mouseDown = true;
	}

	public void mouseUp( Point p, List<Component> components ) {
		mouseDown = false;
	}

	public void doubleClick( Point p, List<JPolygon> elements ) { }

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
					if( r.getPolygon().contains( new PlanPoint( mousePosition ) ) )
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
					if( r.getPolygon().contains( new PlanPoint( mousePosition ) ) )
						ret.add( r );
				}
			}
		}
		return ret;
	}

	public void rightClick() { }
}
