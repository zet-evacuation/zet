/**
 * FloorClickSelectionHandler.java
 * Created: Nov 2, 2012, 4:37:50 PM
 */
package zet.gui.main.tabs.editor;

import ds.z.Edge;
import ds.z.PlanPoint;
import ds.z.PlanPolygon;
import ds.z.ZControl;
import gui.editor.CoordinateTools;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
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
				potentialDragStart = true;
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
					} else if( clickedOn instanceof PlanPolygon ) {
						// Clear old selection & Select new
						//setSelectedPolygon( toSelect );
						getEditStatus().clearSelection();
						getEditStatus().selectPolygon( toSelect );
					} else
						getEditStatus().clearSelection();

			} else
				getEditStatus().clearSelection();
}

	@Override
	public void mouseUp( Point p, List<Component> components ) {
		super.mouseUp( p, components );

		if( getEditStatus().isMouseSelecting() ) {
			// We have created a selection rectangle. select all the stuff in there
					// No negative width/height allowed here, so work around it with Math
					Rectangle selectionArea = new Rectangle( Math.min( getEditStatus().getLastClick().x, getEditStatus().getHighlightedPosition().x ), Math.min( getEditStatus().getLastClick().y, getEditStatus().getHighlightedPosition().y ), Math.abs( getEditStatus().getHighlightedPosition().x - getEditStatus().getLastClick().x ), Math.abs( getEditStatus().getHighlightedPosition().y - getEditStatus().getLastClick().y ) );
					getEditStatus().clearSelection();

					for( Component room : components ) {
						// Search for contained rooms
						Rectangle room_bounds = ((JPolygon)room).getBounds();
						if( selectionArea.contains( room_bounds ) )
							getEditStatus().selectPolygon( (JPolygon)room );
						else if( selectionArea.intersects( room_bounds ) )
							// If the room as a whole is not contained, then at
							// least some areas within it may be inside the selection
							// area
							// Note that we do not explicitly select the areas
							// when the full room is selected, because areas always
							// follow the movement of the room automatically.
							for( Component area : ((JPolygon)room).getComponents() ) {
								Rectangle area_bounds = ((JPolygon)area).getBounds();
								area_bounds.translate( room.getX(), room.getY() );
								if( selectionArea.contains( area_bounds ) )
									getEditStatus().selectPolygon( (JPolygon)area );
							}
					}
			getEditStatus().setMouseSelecting( false );
		} else if( dragStarted ) {
			if( getEditStatus().getSelectedPolygons().size() > 0 ) {
				System.out.println( "We have been dragging..." );
				// we try to drag some polygons
				// Drag whole selection (>= 1 polygon)
				dragFinished( p );
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
		} else {
			if( getEditStatus().isRasterizedPaintMode() )
				getEditStatus().setPointerPosition( getEditStatus().getNextRasterPoint( p ) );
			else
				getEditStatus().setPointerPosition( p );
		}
	}

	private Point dragOffset( Point currentMouse ) {
		return new Point( currentMouse.x - getEditStatus().getLastClick().x, currentMouse.y - getEditStatus().getLastClick().y );
	}

	ArrayList<Point> dragStarts;
	ArrayList<Point> dragTargets;

	private void dragOngoing( Point currentMouse ) {
		Point dragOffset = dragOffset( currentMouse );
		if( getEditStatus().getSelectedPolygons().size() > 0 )
			// we try to drag some polygons
			for( JPolygon sel : getEditStatus().getSelectedPolygons() ) {
				sel.setDragged( true );
				sel.setDragOffset( dragOffset );
			}

		// set temp-drag-points to editStatus
//				if( draggedPlanPoints == null ) {
//
////					if( selectedPoint != null ) {
////						// we try to drag a point
////					} else if( selectedEdge != null ) {
////						// we try to drag an edge
////						//Edge edge = (Edge)clickedOn;
////						Edge edge = selectedEdge;
////						// Drag whole edge
////						draggedPlanPoints = edge.getPlanPoints();
////
////						// Get the affected barrier (if there is one)
////						Barrier barrier = (edge.getAssociatedPolygon() instanceof Barrier) ? (Barrier)edge.getAssociatedPolygon() : null;
////						if( barrier != null )
////							for( Edge be : barrier )
////								if( be != edge && be.equals( edge ) ) {
////									draggedPlanPoints.addAll( be.getPlanPoints() );
////									break;
////								}
////					} else
//
//					//	if( selectedPolygons.size() > 0 ) {
//					if( getEditStatus().getSelectedPolygons().size() > 0 ) {
//						// we try to drag some polygons
//						// Drag whole selection (>= 1 polygon)
//						draggedPlanPoints = new LinkedList<>();
//						for( JPolygon sel : getEditStatus().getSelectedPolygons() ) {
//							sel.setDragged( true );
//							draggedPlanPoints.addAll( ((PlanPolygon)sel.getPlanPolygon()).getPlanPoints() );
//						}
//					}
//					if( draggedPlanPoints != null ) {
//						// Initialize DragTargets & Starts (on-screen coordinates)
//						dragStarts = new ArrayList<>( draggedPlanPoints.size() );
//						dragTargets = new ArrayList<>( draggedPlanPoints.size() );
//						Point translated;
//						for( PlanPoint p : draggedPlanPoints ) {
//							translated = CoordinateTools.translateToScreen( p );
//							dragStarts.add( translated );
//							dragTargets.add( new Point( translated ) );
//						}
//
//					}
//
//					// create the dragged plan points
////				// a) Start dragging, if a polygon was already selected
////				if( clickedOn != null ) {
////					dragStart = point;
////
////					if( clickedOn instanceof PlanPoint ) {
////						PlanPoint dp = (PlanPoint)clickedOn;
////
////						// Drag single Point
////						draggedPlanPoints = new ArrayList<>( 1 );
////						draggedPlanPoints.add( dp );
////
////						// Get the affected barrier (if there is one)
////						Barrier barrier = null;
////						if( dp.getNextEdge() != null &&
////										dp.getNextEdge().getAssociatedPolygon() instanceof Barrier )
////							barrier = (Barrier)dp.getNextEdge().getAssociatedPolygon();
////						else if( dp.getPreviousEdge() != null &&
////										dp.getPreviousEdge().getAssociatedPolygon() instanceof Barrier )
////							barrier = (Barrier)dp.getPreviousEdge().getAssociatedPolygon();
////						if( barrier != null ) {
////							Iterator<PlanPoint> iP = barrier.pointIterator( false );
////							while( iP.hasNext() ) {
////								PlanPoint p = iP.next();
////								if( dp != p && p.equals( dp ) ) {
////									draggedPlanPoints.add( p );
////									break;
////								}
////							}
////						}
////					} else if( clickedOn instanceof Edge ) {
////					} else if( clickedOn instanceof PlanPolygon ) {
////
//				}
////			}
//	}
	}

	private void dragFinished( Point currentMouse ) {
		Point dragStart = CoordinateTools.translateToModel( getEditStatus().getLastClick() );
		Point dragEnd = CoordinateTools.translateToModel( currentMouse );
		Point translated = new Point( dragEnd.x - dragStart.x, dragEnd.y - dragStart.y );

		List<PlanPoint> draggedPlanPoints = new LinkedList<>();
		for( JPolygon sel : getEditStatus().getSelectedPolygons() ) {
			draggedPlanPoints.addAll( ((PlanPolygon)sel.getPlanPolygon()).getPlanPoints() );
		}

		System.out.println( "Call ZControl and change in Model. Tranlate by + " + translated );
		getZControl().movePoints( draggedPlanPoints, translated.x, translated.y );

		for( JPolygon sel : getEditStatus().getSelectedPolygons() )
			sel.setDragged( false );
	}
}
