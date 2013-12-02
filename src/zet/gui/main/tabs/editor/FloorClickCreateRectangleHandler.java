/**
 * FloorClickCreateRectangleHandler.java Created: Nov 6, 2012, 4:12:17 PM
 */
package zet.gui.main.tabs.editor;

import de.tu_berlin.math.coga.zet.ZETLocalization;
import ds.z.PlanPoint;
import ds.z.PlanPolygon;
import ds.z.Room;
import ds.z.ZControl;
import gui.ZETLoader;
import gui.editor.CoordinateTools;
import java.awt.Component;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import zet.gui.main.tabs.base.JPolygon;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FloorClickCreateRectangleHandler extends FloorClickHandler {
	public FloorClickCreateRectangleHandler( EditStatus editStatus, ZControl control ) {
		super( editStatus, control );
	}

	@Override
	public void mouseDown( Point p, List<JPolygon> elements ) {
		super.mouseDown( getEditStatus().isRasterizedPaintMode() ? getEditStatus().getNextRasterPoint( p ) : p, elements );
	}

	/**
	 * If the mouse is moved up, a click has happened and either a new
	 * {@link PlanPolygon} is created or points are added.
	 * @param p
	 * @param components
	 */
	@Override
	public void mouseUp( Point p, List<Component> components ) {
		super.mouseUp( p, components );
		System.out.println( "Click occured. Create rectangle." );
		getEditStatus().setMouseSelecting( false );

		ZetObjectTypes type = getEditStatus().getZetObjectType();
		if( type.isArea() ) {
			Room parent = getRoomUnderMouse( p, components );
			if( parent == null )
				return;
			getZControl().createNewPolygon( type.getObjectClass(), parent );
		} else
			getZControl().createNewPolygon( type.getObjectClass(), getEditStatus().getView() );

		PlanPoint p1 = new PlanPoint( CoordinateTools.translateToModel( getEditStatus().isRasterizedPaintMode() ? getEditStatus().getNextRasterPoint( getEditStatus().getLastClick() ) : getEditStatus().getLastClick() ) );
		PlanPoint p2 = new PlanPoint( CoordinateTools.translateToModel( getEditStatus().isRasterizedPaintMode() ? getEditStatus().getNextRasterPoint( p ) : p ) );
		if( p1.getX() == p2.getX() || p1.getY() == p2.getY() ) {
			ZETLoader.sendError( ZETLocalization.getSingleton().getString( "gui.error.RectangleCreationZeroArea" ) );
			return;
		}
		LinkedList<PlanPoint> points = new LinkedList<>();
		points.add( new PlanPoint( p1.x, p1.y ) );
		points.add( new PlanPoint( p1.x, p2.y ) );
		points.add( new PlanPoint( p2.x, p2.y ) );
		points.add( new PlanPoint( p2.x, p1.y ) );
		points.add( new PlanPoint( p1.x, p1.y ) );
		if( getZControl().addPoints( points ) ) {
			//polygonFinishedHandler();
			//setFloorMode( FloorMode.RectangleStart );
			//lastClick = null;
		} else
			throw new AssertionError( "Creating of the polygon failed." );
	}

	@Override
	public void mouseMove( Point p ) {
		if( mouseDown ) {
			getEditStatus().setMouseSelecting( true );
			getEditStatus().setPointerPosition( getEditStatus().isRasterizedPaintMode() ? getEditStatus().getNextRasterPoint( p ) : p );
		} else
			super.mouseMove( p );
	}

	/**
	 * Double click does nothing.
	 * @param p
	 * @param elements
	 */
	@Override
	public void doubleClick( Point p, List<JPolygon> elements ) { }
}
