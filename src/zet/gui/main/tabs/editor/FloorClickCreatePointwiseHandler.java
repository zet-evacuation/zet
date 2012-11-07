/**
 * FloorClickCreatePointwiseHandler.java
 * Created: Nov 7, 2012, 1:27:18 PM
 */
package zet.gui.main.tabs.editor;

import de.tu_berlin.math.coga.common.localization.DefaultLoc;
import ds.z.Area;
import ds.z.Barrier;
import ds.z.PlanPoint;
import ds.z.PlanPolygon;
import ds.z.Room;
import ds.z.ZControl;
import gui.ZETLoader;
import gui.editor.CoordinateTools;
import java.awt.Component;
import java.awt.Point;
import java.util.List;
import zet.gui.main.tabs.base.JPolygon;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FloorClickCreatePointwiseHandler extends FloorClickHandler {

	private boolean creationStarted = false;
	private PlanPoint last;

	public FloorClickCreatePointwiseHandler( EditStatus editStatus, ZControl control ) {
		super( editStatus, control );
		editStatus.setLastClick( null );
	}

	@Override
	public void mouseDown( Point p, List<JPolygon> elements ) {
		// do nothing here, as only for mouse up (e. g. click) something happens.
		// the lastly clicked point is used in visualization, so do not set it here!
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

		if( !creationStarted ) { // start crating a new polygon by the first click.
			// Get the model points which we clicked on
			ZetObjectTypes type = getEditStatus().getZetObjectType();
			if( type.isArea() ) {
				Room parent = getRoomUnderMouse( p, components );
				if( parent == null )
					return;
				getZControl().createNewPolygon( type.getObjectClass(), parent );
			} else
				getZControl().createNewPolygon( type.getObjectClass(), getEditStatus().getView() );

			PlanPoint p2 = new PlanPoint( CoordinateTools.translateToModel( getEditStatus().isRasterizedPaintMode() ? getEditStatus().getNextRasterPoint( p ) : p ) );
			getEditStatus().setLastClick( getEditStatus().isRasterizedPaintMode() ? getEditStatus().getNextRasterPoint( p ) : p );
			getZControl().addPoint( p2 );
			last = p2;
			creationStarted = true;
		} else {
			// adding a new point
			PlanPoint p2 = new PlanPoint( CoordinateTools.translateToModel( getEditStatus().isRasterizedPaintMode() ? getEditStatus().getNextRasterPoint( p ) : p ) );

			ZetObjectTypes type = getEditStatus().getZetObjectType();
			if( type.isArea() && !getRoomsUnderMouse( p, components ).contains( ((Area<?>)getZControl().latestPolygon()).getAssociatedRoom() ) )
					return; // return, if the necessary polygon was not clicked on

			// check if the new point will close the polygon or the area will be zero
			if( getZControl().latestPolygon().willClose( last, p2 ) && getZControl().latestPolygon().area() == 0 && !(getZControl().latestPolygon() instanceof Barrier) ) {
				ZETLoader.sendError( DefaultLoc.getSingleton().getString( "gui.error.RectangleCreationZeroArea" ) );
				return;
			}

			getEditStatus().setLastClick( getEditStatus().isRasterizedPaintMode() ? getEditStatus().getNextRasterPoint( p ) : p );
			if( getZControl().addPoint( p2 ) ) {
				// closed a polygon
				last = null;
				creationStarted = false;
				System.out.println( "Closed a polygon" );
				getEditStatus().setLastClick( null );
			} else
				last = p2;
		}
	}

	@Override
	public void rightClick() {
		if( creationStarted ) {
			System.out.println( "Closing a polygon" );
			getZControl().closePolygon();
			last = null;
			creationStarted = false;
			getEditStatus().setLastClick( null );
		}
	}


}
