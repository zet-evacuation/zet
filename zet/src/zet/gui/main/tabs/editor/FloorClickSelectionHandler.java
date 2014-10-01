/**
 * FloorClickSelectionHandler.java
 * Created: Nov 2, 2012, 4:37:50 PM
 */
package zet.gui.main.tabs.editor;

import de.tu_berlin.coga.zet.model.Area;
import de.tu_berlin.coga.zet.model.PlanEdge;
import de.tu_berlin.coga.zet.model.PlanPoint;
import de.tu_berlin.coga.zet.model.PlanPolygon;
import de.tu_berlin.coga.zet.model.Room;
import de.tu_berlin.coga.zet.model.ZControl;
import gui.editor.CoordinateTools;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
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

	private boolean potentialDragStart = false;
	private boolean dragStarted = false;

	public FloorClickSelectionHandler( EditStatus editStatus, ZControl control ) {
		super( editStatus, control );
	}

	@Override
	public void mouseDown( Point p, List<JPolygon> elements ) {
		super.mouseDown( p, elements );

		// if one of the clicked polygons was already selected, just set potential dragStart and return
		for( JPolygon poly : elements ) {		
      if( poly.isSelected() ) {
				potentialDragStart = true;
        System.out.println( "Potential drag start for polygon" );
				return;
			}
		}
		selectPolygon( p, elements );
	}

	@Override
	public void doubleClick( Point p, List<JPolygon> elements ) {
		selectPolygon( p, elements );
	}

	private void selectPolygon( Point p, List<JPolygon> elements ) {
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
        System.out.println( "Probably start dragging" );
				potentialDragStart = true;
				// We are gonna select the polygon "toSelect"
				Object clickedOn = null;
				JPolygon sel = toSelect;
				clickedOn = sel.findClickTargetAt( SwingUtilities.convertPoint( getEditStatus().getControlled(), p, sel ) );
				System.out.println( "clickedOn: " + clickedOn );

				if( clickedOn != null ) {
					if( clickedOn instanceof PlanPoint ) {
						PlanPoint dp = (PlanPoint) clickedOn;
					} else if( clickedOn instanceof PlanEdge ) {
						PlanEdge edge = (PlanEdge) clickedOn;
						getEditStatus().clearSelection();
						getEditStatus().selectEdge( toSelect, edge );
					} else if( clickedOn instanceof PlanPolygon ) {
						getEditStatus().selectPolygon( toSelect );
					} else {
						getEditStatus().clearSelection();            
          }
        }
			} else {
				getEditStatus().clearSelection();        
      }
}

	@Override
	public void mouseUp( Point p, List<Component> components ) {
		super.mouseUp( p, components );

    if( getEditStatus().isMouseSelecting() ) {
			// We have created a selection rectangle. select all the stuff in there
      // No negative width/height allowed here, so work around it with Math
      Rectangle selectionArea = new Rectangle( Math.min( getEditStatus().getLastClick().x, getEditStatus().getHighlightedPosition().x ), Math.min( getEditStatus().getLastClick().y, getEditStatus().getHighlightedPosition().y ), Math.abs( getEditStatus().getHighlightedPosition().x - getEditStatus().getLastClick().x ), Math.abs( getEditStatus().getHighlightedPosition().y - getEditStatus().getLastClick().y ) );
      getEditStatus().clearSelection();

      List<JPolygon> toAdd = new LinkedList<>();
      for( Component room : components ) {
        // Search for contained rooms
        Rectangle room_bounds = ((JPolygon) room).getBounds();
        if( selectionArea.contains( room_bounds ) ) {
          //getEditStatus().addPolygon( (JPolygon)room );
          toAdd.add( (JPolygon) room );
        } else if( selectionArea.intersects( room_bounds ) ) {
							// If the room as a whole is not contained, then at
          // least some areas within it may be inside the selection
          // area
          // Note that we do not explicitly select the areas
          // when the full room is selected, because areas always
          // follow the movement of the room automatically.
          for( Component area : ((JPolygon) room).getComponents() ) {
            Rectangle area_bounds = ((JPolygon) area).getBounds();
            area_bounds.translate( room.getX(), room.getY() );
            if( selectionArea.contains( area_bounds ) ) //getEditStatus().addPolygon( (JPolygon)area );
            {
              toAdd.add( (JPolygon) area );
            }
          }
        }
      }
      getEditStatus().addPolygon( toAdd );
      getEditStatus().setMouseSelecting( false );
		} else if( dragStarted ) {
			if( getEditStatus().getSelectedPolygons().size() > 0 ) {
        System.out.println( "Stopping dragging of polygon" );
				// we try to drag some polygons
				// Drag whole selection (>= 1 polygon)
				dragFinished( p, components );
			} else if( getEditStatus().getSelectedEdge() != null ) {
        System.out.println( "Stop dragging a polygon" );
        dragFinished( p, getEditStatus().getSelectedEdge().get( 0 ) );
        getEditStatus().getEdgePolygon().setSelectedEdge( null );
        //getEditStatus().getEdgePolygon().setDragOffset( dragOffset );
        getEditStatus().getEdgePolygon().setDragged( false );
      }
		}
		potentialDragStart = false;
		dragStarted = false;
	}

	@Override
	public void mouseMove( Point p ) {
		if( mouseDown ) {
			if( potentialDragStart ) {
				dragStarted = true;
				dragOngoing( p );
			} else {
				getEditStatus().setMouseSelecting( true );
				getEditStatus().setPointerPosition( p );
			}
		} else
			super.mouseMove( p );
	}

	private Point dragOffset( Point currentMouse ) {
		if( getEditStatus().isRasterizedPaintMode() ) {
			Point start = getEditStatus().getNextRasterPoint( getEditStatus().getLastClick() );
			Point end = getEditStatus().getNextRasterPoint( currentMouse );
			return new Point( end.x - start.x, end.y - start.y );
		} else
			return new Point( currentMouse.x - getEditStatus().getLastClick().x, currentMouse.y - getEditStatus().getLastClick().y );
	}

	private void dragOngoing( Point currentMouse ) {
		Point dragOffset = dragOffset( currentMouse );

		if( getEditStatus().getSelectedPolygons().size() > 0 ) {
      System.out.println( "Dragging some polygons" );
			// we try to drag some polygons
			for( JPolygon sel : getEditStatus().getSelectedPolygons() ) {
				sel.setDragged( true );
				sel.setDragOffset( dragOffset );
			}      
    } else if( getEditStatus().getSelectedEdge() != null ) {
      System.out.println( "Dragging an edge" );
      getEditStatus().getEdgePolygon().setSelectedEdge( getEditStatus().getSelectedEdge().get( 0 ) );
      getEditStatus().getEdgePolygon().setDragOffset( dragOffset );
      getEditStatus().getEdgePolygon().setDragged( true );
    }
	}

  private void dragFinished( Point currentMouse, PlanEdge edge ) {
		Point dragStart = CoordinateTools.translateToModel( getEditStatus().isRasterizedPaintMode() ? getEditStatus().getNextRasterPoint( getEditStatus().getLastClick() ) : getEditStatus().getLastClick() );
		Point dragEnd = CoordinateTools.translateToModel( getEditStatus().isRasterizedPaintMode() ? getEditStatus().getNextRasterPoint( currentMouse ) : currentMouse );
		Point translated = new Point( dragEnd.x - dragStart.x, dragEnd.y - dragStart.y );

 		List<PlanPoint> draggedPlanPoints = new LinkedList<>();
    draggedPlanPoints.add( edge.getSource() );
    draggedPlanPoints.add( edge.getTarget() );

		getZControl().movePoints( draggedPlanPoints, translated.x, translated.y );
}
  
	private void dragFinished( Point currentMouse, List<Component> components ) {
		Point dragStart = CoordinateTools.translateToModel( getEditStatus().isRasterizedPaintMode() ? getEditStatus().getNextRasterPoint( getEditStatus().getLastClick() ) : getEditStatus().getLastClick() );
		Point dragEnd = CoordinateTools.translateToModel( getEditStatus().isRasterizedPaintMode() ? getEditStatus().getNextRasterPoint( currentMouse ) : currentMouse );
		Point translated = new Point( dragEnd.x - dragStart.x, dragEnd.y - dragStart.y );

		// Check, if all selected polygons are areas:
		List<Area> areas = new LinkedList<>();
		for( JPolygon sel : getEditStatus().getSelectedPolygons() ) {
			if( sel.getPlanPolygon() instanceof Area ) {
				areas.add( (Area) sel.getPlanPolygon() );
      } else {
				areas = null;
				break;
			}
		}
		if( areas != null ) {
			Room r = getRoomUnderMouse( currentMouse, components );
			if( r != null ) {
				getZControl().moveAreas( areas, translated.x, translated.y, r );
				for( JPolygon sel : getEditStatus().getSelectedPolygons() )
					sel.setDragged( false );
				return;
			}
		}

		List<PlanPoint> draggedPlanPoints = new LinkedList<>();
		for( JPolygon sel : getEditStatus().getSelectedPolygons() ) {
			draggedPlanPoints.addAll( ((PlanPolygon)sel.getPlanPolygon()).getPlanPoints() );
		}

		getZControl().movePoints( draggedPlanPoints, translated.x, translated.y );

		for( JPolygon sel : getEditStatus().getSelectedPolygons() )
			sel.setDragged( false );
	}
}
