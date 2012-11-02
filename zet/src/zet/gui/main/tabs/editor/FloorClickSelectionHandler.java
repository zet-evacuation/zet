/**
 * FloorClickSelectionHandler.java
 * Created: Nov 2, 2012, 4:37:50 PM
 */
package zet.gui.main.tabs.editor;

import ds.z.Edge;
import ds.z.PlanPoint;
import ds.z.PlanPolygon;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.ListIterator;
import javax.swing.SwingUtilities;
import zet.gui.main.tabs.base.JPolygon;

/**
 * Handles actions that occur if mouse interaction with a {@link JFloor} occurs
 * in selection mode.
 * @author Jan-Philipp Kappmeier
 */
public class FloorClickSelectionHandler extends FloorClickHandler {

	public FloorClickSelectionHandler( EditStatus editStatus ) {
		super( editStatus );
	}

	@Override
	public void mouseDown( Point p, List<JPolygon> elements ) {
		super.mouseDown( p, elements );

			// Get the new selection
			ListIterator<JPolygon> itPoly = elements.listIterator();
			JPolygon toSelect = null;

			// If none of the polygons that we clicked on is selected,
			// then just select the top-level one. If a JPolygon is selected
			// then switch the selection over to the next JPolygon in
			// the given order of polygons
			while( itPoly.hasNext() ) {
				toSelect = itPoly.next();

				if( toSelect.isSelected() ) {
					toSelect = itPoly.hasNext() ? itPoly.next() : elements.get( 0 );
					break;
				}
			}

			// We have clicked on an object, now find out if it was a polygon, edge or point and select it
			if( toSelect != null ) {
				// We are gonna select the polygon "toSelect"
				Object clickedOn = null;
				JPolygon sel = toSelect;
				clickedOn = sel.findClickTargetAt( SwingUtilities.convertPoint( getEditStatus().getControlled(), p, sel ) );


				if( clickedOn != null )
					if( clickedOn instanceof PlanPoint ) {
						PlanPoint dp = (PlanPoint) clickedOn;

					} else if( clickedOn instanceof Edge ) {
						Edge edge = (Edge) clickedOn;
						getEditStatus().clearSelection();
						//selectedEdge = edge;
						//selectedElementPolygon = toSelect;
						//selectedElementPolygon.setSelectedEdge( edge );
						//fireActionEvent();
					} else if( clickedOn instanceof PlanPolygon )
						// Clear old selection & Select new
						//setSelectedPolygon( toSelect );
						getEditStatus().selectPolygon( toSelect );
					else
						getEditStatus().clearSelection();

			} else
				getEditStatus().clearSelection();
	}

	@Override
	public void mouseUp() {
		super.mouseUp();
		if( getEditStatus().isMouseSelecting() ) {
			// We have created a selection rectangle. select all the stuff in there
					// No negative width/height allowed here, so work around it with Math
					Rectangle selectionArea = new Rectangle( Math.min( getEditStatus().getLastClick().x, getEditStatus().getHighlightedPosition().x ), Math.min( getEditStatus().getLastClick().y, getEditStatus().getHighlightedPosition().y ), Math.abs( getEditStatus().getHighlightedPosition().x - getEditStatus().getLastClick().x ), Math.abs( getEditStatus().getHighlightedPosition().y - getEditStatus().getLastClick().y ) );
					getEditStatus().clearSelection();

//					for( Component room : getComponents() ) {
//						// Search for contained rooms
//						Rectangle room_bounds = ((JPolygon)room).getBounds();
//						if( selectionArea.contains( room_bounds ) )
//							selectPolygon( (JPolygon)room );
//						else if( selectionArea.intersects( room_bounds ) )
//							// If the room as a whole is not contained, then at
//							// least some areas within it may be inside the selection
//							// area
//							// Note that we do not explicitly select the areas
//							// when the full room is selected, because areas always
//							// follow the movement of the room automatically.
//							for( Component area : ((JPolygon)room).getComponents() ) {
//								Rectangle area_bounds = ((JPolygon)area).getBounds();
//								area_bounds.translate( room.getX(), room.getY() );
//								if( selectionArea.contains( area_bounds ) )
//									selectPolygon( (JPolygon)area );
//							}
//					}
			getEditStatus().setMouseSelecting( false );
		}
	}

	@Override
	public void mouseMove( Point p ) {
		if( mouseDown ) {
			getEditStatus().setMouseSelecting( true );
			getEditStatus().setPointerPosition( p );
		} else {
			if( getEditStatus().isRasterizedPaintMode() )
				getEditStatus().setPointerPosition( getEditStatus().getNextRasterPoint( p ) );
			else
				getEditStatus().setPointerPosition( p );
		}
	}



}
