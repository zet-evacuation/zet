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

package zet.gui.main.tabs.editor;

import de.tu_berlin.math.coga.zet.ZETLocalization2;
import de.tu_berlin.coga.zet.model.Area;
import de.tu_berlin.coga.zet.model.Barrier;
import de.tu_berlin.coga.zet.model.Floor;
import de.tu_berlin.coga.zet.model.PlanPolygon;
import de.tu_berlin.coga.zet.model.Room;
import de.tu_berlin.coga.zet.model.ZModelAreaEvent;
import de.tu_berlin.coga.zet.model.ZModelRoomEvent;
import event.EventListener;
import gui.GUIControl;
import gui.GUIOptionManager;
import gui.ZETLoader;
import gui.editor.CoordinateTools;
import gui.editor.planimage.PlanImage;
import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import javax.swing.SwingUtilities;
import zet.gui.main.tabs.base.AbstractFloor;
import zet.gui.main.tabs.base.JPolygon;

/**
 * Graphical representation of a Floor from the BuildingPlan.
 * @author Timon Kelter, Jan-Philipp Kappmeier
 */
@SuppressWarnings( "serial" )
public class JFloor extends AbstractFloor implements EventListener<ZModelRoomEvent> {
	/** The background image */
	private PlanImage planImage;
	// Helper methods and vars.
	/** The standard stroke. */
	private final static BasicStroke stroke_standard = new BasicStroke( 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER );
	/** The stroke used for painting the selection rectangle. */
	private final static BasicStroke selection_stroke = new BasicStroke( 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3.0f, new float[]{4.0f, 2.0f}, 0.0f );
	private final GUIControl guiControl;
	private final EditStatus editStatus;

	/** The displayed floor. */
	private Floor myFloor;
	/**
	 * Creates a new instance of {@code JFloor} with an empty floor.
	 *
	 * @param editStatus
	 * @param guiControl
	 */
	public JFloor( EditStatus editStatus, GUIControl guiControl ) {
		super();
		this.guiControl = guiControl;
		this.editStatus = editStatus;
		setLayout( null );
		planImage = new PlanImage();
		setBackground( GUIOptionManager.getEditorBackgroundColor() );

		enableEvents( AWTEvent.MOUSE_EVENT_MASK );
		enableEvents( AWTEvent.KEY_EVENT_MASK );
		enableEvents( AWTEvent.MOUSE_WHEEL_EVENT_MASK );
	}

	/**
	 * Displays a floor.
	 * @param editStatus
	 * @param guiControl
	 * @param get
	 */
	public JFloor( EditStatus editStatus, GUIControl guiControl, Floor get ) {
		this( editStatus, guiControl );
		myFloor = Objects.requireNonNull( get, "Floor may not be null." );
		initialize();
	}

	HashMap<Room,JPolygon> roomToPolygonMapping = new HashMap<>();

	/**
	 * Initializes the floor. Objects for all the polygons and rooms on the floor
	 * are created.
	 */
	private void initialize() {
		Floor f = myFloor;

		updateOffsets( f );

		for( Room r : f.getRooms() ) {
			JPolygon roomPolygon = new JPolygon( this, GUIOptionManager.getRoomEdgeColor(), guiControl );
			add( roomPolygon );
			roomPolygon.displayPolygon( r );
			roomToPolygonMapping.put( r, roomPolygon );
		}

		planImage.setOffsetX( -floorMin_x );
		planImage.setOffsetY( -floorMin_y );

		ZETLoader.sendMessage( ZETLocalization2.loc.getString( "gui.message.FloorSuccessfullyLoaded" ) );

		revalidate();
		repaint();
	}

	@Override
	public void handleEvent( ZModelRoomEvent e ) {
		if( e instanceof ZModelAreaEvent ) {
			ZModelAreaEvent e2 = (ZModelAreaEvent)e;
			Room room = e2.getAffectedRoom();
			// cannot create a new room here!
			JPolygon poly = roomToPolygonMapping.get( room );
			assert poly != null;
			poly.displayPolygon( room );
			JPolygon jp = findPolygonToArea( this, e2.getAffectedArea().getPolygon() );
			System.out.println( "Editing set to " + jp );
			editStatus.setCurrentEditing( jp );
		} else
			for( Room room : e.getAffectedRooms() ) {
				JPolygon poly = roomToPolygonMapping.get( room );
				if( poly != null )
					poly.displayPolygon( room );
				else { // we have a new room
					JPolygon roomPolygon = new JPolygon( this, GUIOptionManager.getRoomEdgeColor(), guiControl );
					add( roomPolygon );
					roomPolygon.displayPolygon( room );
					roomToPolygonMapping.put( room, roomPolygon );
					editStatus.setCurrentEditing( roomPolygon );
				}
			}
		redisplay();
	}

	private JPolygon findPolygonToArea( JFloor base, PlanPolygon poly ) {
		for( Component c : base.getComponents() ) {
			if( c instanceof JPolygon ) {
				JPolygon jp = (JPolygon) c;
				JPolygon ret = findPolygonToArea( jp, poly );
				if( ret != null )
					return ret;
			}
		}
		throw new AssertionError( "Created area not found!" );
	}


	private JPolygon findPolygonToArea( JPolygon base, PlanPolygon poly ) {
		if( base.getPlanPolygon() == poly )
			return base; // if we are assigned to the plan poly, return
		for( Component c : base.getComponents() ) { // otherwise search in the sub-components
			if( c instanceof JPolygon ) {
				JPolygon jp = (JPolygon) c;
				JPolygon ret = findPolygonToArea( jp, poly );
				if( ret != null )
					return ret;
			}
		}
		return null; // not contained, return null
	}
	/**
	 * Recreates the visual representations for the objects on the floor. Should
	 * be called if the properties of the representation (e. g. color, zoom factor, etc.)
	 * have been changed. This method recreates all the objects, thus it is quite
	 * costly and should be called reasonable.
	 */
	public void redisplay() {
		System.out.println( "REDISPLAY" );
		JPolygon wasEditing = editStatus.getCurrentEditing();
		PlanPolygon<?> editPoly = null;
		if( wasEditing != null )
			editPoly = wasEditing.getPlanPolygon();

		for( Entry<Room,JPolygon> e : roomToPolygonMapping.entrySet() ) {
			Room r = e.getKey();
			JPolygon poly = e.getValue();
			poly.displayPolygon( r );
		}
		getPlanImage().update();

		if( editPoly != null ) { // keep track of the editing-polygon. they are lost due to the current implementation of JPolygon. this is slow!
			JPolygon jp = findPolygonToArea( this, editPoly );
			editStatus.setCurrentEditing( jp ); // do not call redisplay in this case, as long as JPolygon.display is not reimplemented
		}
	}

	/**
	 * <p>Paints the panel in the graphics object. It is possible to pass any
	 * graphics object, but it is particularly used for painting this panel. This
	 * can be used to save as bitmap or JPEG.</p>
	 * <p>It draws points and a raster and a background image, if setLocation.</p>
	 * @param g The graphics object
	 */
	@Override
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		this.drawRaster( g2 );

		// Draw background image
		planImage.paintComponent( g2 );

		// draw a highlighted node at the mouse pointer
		if( editStatus.getHighlightedPosition() != null && !editStatus.isMouseSelecting() )
				g2.drawRect( editStatus.getHighlightedPosition().x - JPolygon.NODE_PAINT_RADIUS, editStatus.getHighlightedPosition().y - JPolygon.NODE_PAINT_RADIUS, 2 * JPolygon.NODE_PAINT_RADIUS, 2 * JPolygon.NODE_PAINT_RADIUS );

		// draw a dashed rectangle if selecting using mouse movements is active
		if( editStatus.isMouseSelecting() ) {
			g2.setPaint( GUIOptionManager.getDragNodeColor() );
			g2.setStroke( selection_stroke );
			// No negative width/height allowed here, so work around it with Math
			g2.drawRect( Math.min( editStatus.getLastClick().x, editStatus.getHighlightedPosition().x ), Math.min( editStatus.getLastClick().y, editStatus.getHighlightedPosition().y ), Math.abs( editStatus.getHighlightedPosition().x - editStatus.getLastClick().x ), Math.abs( editStatus.getHighlightedPosition().y - editStatus.getLastClick().y ) );
			g2.setStroke( stroke_standard );
		}

		for( JPolygon poly : editStatus.getSelectedPolygons() ) {
			if( poly.isDragged() )
				if( poly.getParent() == this )
					poly.paint( g2, poly.getLocation(), true );
				else
					poly.paint( g2, new Point( poly.getLocation().x + poly.getParent().getLocation().x, poly.getLocation().y + poly.getParent().getLocation().y ), true );
		}

		// If in PolygonCreationMode, draw help-line
		if( editStatus.getEditMode() == EditMode.CreationPointWise && editStatus.getLastClick() != null ) {
			Point p1 = editStatus.getLastClick();
			Point p2 = editStatus.getHighlightedPosition();
			Color t = editStatus.getZetObjectType().getEditorColor();
			g2.setPaint( t );;
			g2.setStroke( JPolygon.stroke_thick );
			g2.drawLine( p1.x, p1.y, p2.x, p2.y );
		}
	}

	@Override
	protected void processMouseEvent( MouseEvent e ) {
		if( e.getButton() == MouseEvent.BUTTON1 ) {
			if( e.getID() == MouseEvent.MOUSE_CLICKED ) {
				if(e.getClickCount() == 2 ) {
					List<JPolygon> clickedPolygons = findAllPolygonsAt( JFloor.this, e.getPoint() );
					editStatus.getCurrentHandler().doubleClick( e.getPoint(), clickedPolygons );
				}
			} else if( e.getID() == MouseEvent.MOUSE_PRESSED ) {
					List<JPolygon> clickedPolygons = findAllPolygonsAt( JFloor.this, e.getPoint() );
					editStatus.getCurrentHandler().mouseDown( e.getPoint(), clickedPolygons );
			} else if( e.getID() == MouseEvent.MOUSE_RELEASED ) {
				editStatus.getCurrentHandler().mouseUp( e.getPoint(), Arrays.asList( getComponents() ) );
			}
		} else if( e.getButton() == MouseEvent.BUTTON3 ) {
			if( e.getID() == MouseEvent.MOUSE_CLICKED )
				editStatus.getCurrentHandler().rightClick();
		}
		if( e.getButton() == 4 ) {
			guiControl.scrollHorizontal( -1 );
		}
		if( e.getButton() == 5 ) {
			guiControl.scrollHorizontal( 1 );
		}
		repaint();
	}

	/** Mouse Motion Event Handler */
	@Override
	protected void processMouseMotionEvent( MouseEvent e ) {
		editStatus.getCurrentHandler().mouseMove( e.getPoint() );
		repaint();
	}

	/** This method returns <b>all</b> JPolygons which are contained in "c"
	 * and contain the specified point. In contrast to that, the API method
	 * findComponentAt only returns <b>1</b> component which contains the given point.
	 *
	 * @param c The container in which the search should be performed
	 * @param p The point, which the sought-after components must contain. The
	 * coordinates must be relative to container c
	 * @return A list of JPolygons which contain point p. This list will be
	 * empty in the case that such JPolygons do not exist.
	 */
	private static List<JPolygon> findAllPolygonsAt( Container c, Point p ) {
		// For performance reasons we use only one single list for all recursive calls
		LinkedList<JPolygon> result = new LinkedList<>();
		findAllPolygonsAtImpl( c, p, result );
		return result;
	}

	/** Used for zooming in and out with the mouse wheel.
	 * @param e the mouse event
	 */
	@Override
	protected void processMouseWheelEvent( MouseWheelEvent e ) {
		if( !e.isControlDown() ) // move up/down
			guiControl.scrollVertical(e.getWheelRotation() );
		else {
			// zoom in and out
			double oldZoom = CoordinateTools.getZoomFactor();
			if( e.getScrollType() == MouseWheelEvent.WHEEL_BLOCK_SCROLL )
				oldZoom = e.getWheelRotation() < 0 ? Math.min( oldZoom * 2, 0.25d ) : Math.max( oldZoom * 2, 0.01d );
			else {
				double offset = (e.getScrollAmount() * Math.abs( e.getWheelRotation() )) / 100.0d;
				offset /= 4; // Make offset smaller, otherwise it's too fast
				oldZoom = e.getWheelRotation() < 0 ? Math.min( oldZoom + offset, 0.25d ) : Math.max( oldZoom - offset, 0.01d );
			}
			guiControl.setZoomFactor( oldZoom );
		}
	}

	/** This is an internal helper method. Never call it.
	 * Call findAllComponentsAt (Container c, Point p) instead. */
	private static void findAllPolygonsAtImpl( Container c, Point p, List<JPolygon> polygonList ) {
		for( Component comp : c.getComponents() ) {
			Point relative_point = SwingUtilities.convertPoint( c, p, comp );
			if( comp instanceof JPolygon ) {
				JPolygon poly = (JPolygon)comp;

				// Test if the Polygon has the point (Barriers pass a slightly less strict test)
				if( poly.getPlanPolygon() instanceof Barrier ) {
					if( poly.getBounds().contains( p ) )
						polygonList.add( poly );
				} else if( poly.getDrawingPolygon().contains( relative_point ) )
					polygonList.add( poly );

				// Recursively search the JPolygon. We can restrict ourselves to
				// searching recursively in all JPolygons, because Polygons can
				// only be placed upon the Floor (then we find them in the first
				// recursion level) or upon Rooms (then we find them in recursion
				// level 2). So, we don't have to check every instance of
				// java.awt.Container for new JPolygons. This also is a benefit
				// for the performance.

				// Second note: It is important that we search all JPolygons no
				// matter whether they contain the Point or not, because Rooms
				// may not contain the point but they may contain Areas which lie
				// outside of the room (though this is not accepted when you want
				// to transform to CA or Graph) and must be checked
				findAllPolygonsAtImpl( poly, relative_point, polygonList );
			}
		}
	}

	public EditStatus getEditStatus() {
		return editStatus;
	}
















	public void displayFloor() {
//		displayFloor( myFloor );
	}
//
//	/**
//	 * Only call this after the component has been added to a container. Otherwise
//	 * operations like setBounds, which are called herein, will fail.
//	 * @param f the floor
//	 */
//	public void displayFloor( Floor f ) {
//		boolean showDifferentFloor = (myFloor != f);
//		LinkedList<PlanPolygon> old_selection = new LinkedList<>();
//
//		if( myFloor != null ) {
//
//			// Clear & Save old selection
//			for( JPolygon p : selectedPolygons )
//				old_selection.add( p.getPlanPolygon() );
//			clearSelection();
//
//			// Delete old polygons
//			for( Component c : getComponents() )
//				if( c instanceof JPolygon )
//					((JPolygon)c).displayPolygon( null );
//			removeAll();
//		}
//
//		myFloor = f;
//
//		if( f != null ) {
//			updateOffsets( f );
//
//			// TODO: Provide better implementation - Do not recreate everything each time
//			for( Room r : f.getRooms() ) {
//				JPolygon roomPolygon = new JPolygon( this, GUIOptionManager.getRoomEdgeColor(), guiControl );
//				add( roomPolygon );
//				roomPolygon.displayPolygon( r );
//			}
//
//			// Recreate/Clear the click values and other temporary variables
//			if( showDifferentFloor )
//				resetEdit();
//			else {
//				//showing the same floor again
//
//				// Recreate the selection
//				if( !old_selection.isEmpty() )
//					recreateSelection( this, old_selection );
//
//				// mind a possibly new zoom factor when still creating a polygon
//				//if( lastPlanClick != null )
//				//	lastClick = CoordinateTools.translateToScreen( lastPlanClick );
//
//				// abort temporary painting helper variables
//				newRasterizedPoint = null;
//				mousePos = null;
//
//				// Abort drag processes
//				dragStart = null;
//				dragTargets = null;
//				dragStarts = null;
//				draggedPlanPoints = null;
//			}
//
//			planImage.setOffsetX( -floorMin_x );
//			planImage.setOffsetY( -floorMin_y );
//
//			if( showDifferentFloor )
//				ZETLoader.sendMessage( DefaultLoc.getSingleton().getString( "gui.message.FloorSuccessfullyLoaded" ) );
//		}
//
//		revalidate();
//		repaint();
//	}
//
//	/** Selects the given PlanPolygon. */
//	private void recreateSelection( JComponent c, List<PlanPolygon> old_selection ) {
//		for( PlanPolygon p : old_selection )
//			for( Component x : c.getComponents() ) {
//				if( x instanceof JPolygon && ((JPolygon)x).getPlanPolygon() == p ) {
//
//					selectPolygon( (JPolygon)x );
//					break;
//				}
//
//				recreateSelection( (JComponent)x, old_selection );
//			}
//	}
//
//	/**
//	 * Resets all temporary data concerning dragging processes, selection
//	 * and click values and deletes polygons which are in creation.
//	 */
//	public void resetEdit() {
//		lastClick = null;
//		//lastPlanClick = null;
//		clearSelection();
//
//		newRasterizedPoint = null;
//		dragTargets = null;
//		mousePos = null;
//		dragStart = null;
//		dragStarts = null;
//		draggedPlanPoints = null;
//		if( newPolygon != null ) {
//			guiControl.getZControl().deletePolygon( newPolygon );
//			newPolygon = null;
//		}
//	}
//

	public PlanImage getPlanImage() {
		return planImage;
	}

	/** Show the given polygon to the user by scrolling until it is visible and
	 * setting it as the selected polygon (clears the previous selection). If the
	 * given polygon is not shown on this JFloor nothing will happen.
	 * @param p the polygon that is shown
	 */
	public void showPolygon( Room p ) {
		//if( p instanceof Area )
		//	p = ((Area)p).getAssociatedRoom();
		JPolygon jp = roomToPolygonMapping.get( p );//getJPolygon( p );

		if( jp != null ) {
			scrollRectToVisible( jp.getBounds() );
			//setSelectedPolygon( jp );
			editStatus.selectPolygon( jp );
		}
	}

	public void showPolygon( Area a ) {
		showPolygon( a.getAssociatedRoom() );
	}

//	/** Mouse Event Handler
//	 * @param e
//	 */
//	@Override
//	protected void processMouseEvent( MouseEvent e ) {
//		if( e.getID() == MouseEvent.MOUSE_CLICKED ) {
//			//if( e.getClickCount() == 1 )
//			//	System.out.println( "A single mouse klick occured" );
//			//else
//			//	System.out.println( "Multi-click occured: " + e.getClickCount() );
//			System.out.println( "Mouse click" );
//			if( e.getButton() == MouseEvent.BUTTON1 ) {
//				// left button
//				this.processLeftClick( e.getPoint() );
//			} else if(e.getButton() == MouseEvent.BUTTON3 ) {
//				// right button
//				this.processRightClick( e.getPoint() );
//			}
//		} else if( e.getID() == MouseEvent.MOUSE_PRESSED ) {
//			if( e.getButton() == MouseEvent.BUTTON1 ) {
//				this.processPotentialDragStart( e.getPoint() );
//				dragStart = e.getPoint();
//			}
//		}
//		if( e.getID() == MouseEvent.MOUSE_PRESSED ) {
//			// Clear status bar
//			ZETLoader.sendError( "" );
//
//			if( e.getButton() == MouseEvent.BUTTON1 )
//				;
//			else if( e.getButton() == MouseEvent.BUTTON3 )
//				;
//				// This method already contains the EditModeOld analysis
//
//		} else if( e.getID() == MouseEvent.MOUSE_RELEASED )
//			// Complete the drag if you've begun one
//			if( dragStart != null ) {
//				// a) Dragged some polygons
//				if( dragTargets != null ) {
//					boolean trueDrag = false;
//
//					try {
//						// Translate the drag targets into model coordinates
//						// this must be done beforehand, because the GUI coords are
//						// worthless if the floor offsets change while
//						// setting the single points to their new locations
//						// (which is almost guaranteed at the first drawn polygons)
//						for( Point p : dragTargets )
//							p.setLocation( CoordinateTools.translateToModel( p ) );
//
//						// compute the difference
//						final int xOld = draggedPlanPoints.get( 0 ).x;
//						final int yOld = draggedPlanPoints.get( 0 ).y;
//						final int xNew = dragTargets.get( 0 ).x;
//						final int yNew = dragTargets.get( 0 ).y;
//						zcontrol.movePoints( draggedPlanPoints, xNew - xOld, yNew - yOld );
//					} catch( Exception ex ) {
//						ZETLoader.sendError( DefaultLoc.getSingleton().getString( "gui.message.ExceptionDuringDrag" ) );
//						ex.printStackTrace( System.err );
//					}
//
//					// The event that is thrown by setLocation triggers an automatic
//					// redisplay of the JFloor and the drag mode is switched off during
//					// this repaint redisplay (@see #displayFloor(Floor))
//
//					// Unfortunately this event is only thrown in case that at least one
//					// point really changed it's position which might be false. In this case
//					// we repaint manually to erase the dragTarget painting on screen
//					if( !trueDrag ) {
//						dragStart = null;
//						dragTargets = null;
//						dragStarts = null;
//						draggedPlanPoints = null;
//						repaint();
//					}
//
//					// b) Dragged a selection rectangle
//				} else if( !dragStart.equals( e.getPoint() ) ) {
//					// No negative width/height allowed here, so work around it with Math
//					Rectangle selectionArea = new Rectangle( Math.min( dragStart.x, mousePos.x ), Math.min( dragStart.y, mousePos.y ), Math.abs( mousePos.x - dragStart.x ), Math.abs( mousePos.y - dragStart.y ) );
//					clearSelection();
//
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
//				}
//				dragStart = null;
//				repaint(); // Clear selection shape
//			}
//
//		// Enable all JPolygons which contain the given point to process this
//		// event (Swing normally only informs the first JPolygon that it finds)
//
//		// Do not use e.isPopupTrigger() here - Won't work under linux
//		if( e.getButton() == MouseEvent.BUTTON3 && !eventRedirectionGoingOn )
//			try {
//				eventRedirectionGoingOn = true;
//				Room clickedOn = findParent( (Component) e.getSource() );
//
//				if( clickedOn != null ) {
//					Component c = (Component)e.getSource();
//					//for( Component c : getComponents() )
//						if( (c instanceof JPolygon) && c.getBounds().contains( e.getPoint() ) ) {
//							// Dispatch only to an area or to a room
//							boolean area_popup = false;
//
//							Point translated = SwingUtilities.convertPoint( this, e.getPoint(), c );
//							for( Component component : ((JPolygon) c).getComponents() ) {
//								Point tempTrans = SwingUtilities.convertPoint( c, translated, component );
//								MouseEvent areaEvent = new MouseEvent( component, e.getID(), e.getWhen(), e.getModifiers(), tempTrans.x, tempTrans.y, e.getClickCount(), e.isPopupTrigger() );
//								if( component.contains( tempTrans ) && component instanceof JPolygon && ((JPolygon) component).isPopupTrigger( areaEvent ) ) {
//									// Dispatch to the area
//									component.dispatchEvent( areaEvent );
//									area_popup = true;
//									break;
//								}
//							}
//
//							// Dispatch to the room
//							MouseEvent roomEvent = new MouseEvent( c, e.getID(), e.getWhen(), e.getModifiers(), translated.x, translated.y, e.getClickCount(), e.isPopupTrigger() );
//							if( !area_popup && ((JPolygon) c).isPopupTrigger( roomEvent ) )
//								c.dispatchEvent( roomEvent );
//						}
//				}
//			} finally {
//				eventRedirectionGoingOn = false;
//			}
//	}

	//	/** Mouse Motion Event Handler */
//	@Override
//	protected void processMouseMotionEvent( MouseEvent e ) {
//		if( e.getID() == MouseEvent.MOUSE_DRAGGED ) {
//			// Dragging only allowed in selection mode
//			if( floorMode == FloorMode.Select ) {
//				if( draggedPlanPoints == null ) {
//
//					if( selectedPoint != null ) {
//						// we try to drag a point
//					} else if( selectedEdge != null ) {
//						// we try to drag an edge
//						//Edge edge = (Edge)clickedOn;
//						Edge edge = selectedEdge;
//						// Drag whole edge
//						draggedPlanPoints = edge.getPlanPoints();
//
//						// Get the affected barrier (if there is one)
//						Barrier barrier = (edge.getAssociatedPolygon() instanceof Barrier) ? (Barrier)edge.getAssociatedPolygon() : null;
//						if( barrier != null )
//							for( Edge be : barrier )
//								if( be != edge && be.equals( edge ) ) {
//									draggedPlanPoints.addAll( be.getPlanPoints() );
//									break;
//								}
//					} else if( selectedPolygons.size() > 0 ) {
//						// we try to drag some polygons
//						// Drag whole selection (>= 1 polygon)
//						draggedPlanPoints = new LinkedList<>();
//						for( JPolygon sel : selectedPolygons )
//							draggedPlanPoints.addAll( ((PlanPolygon)sel.getPlanPolygon()).getPlanPoints() );
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
//			}
//		}
//
//
//		if( JZetWindow.isEditing() )
//			return;
//		if( e.getID() == MouseEvent.MOUSE_DRAGGED || e.getID() == MouseEvent.MOUSE_MOVED ) {
//			// Grab focus to deliver new key events to this JFloor
//			requestFocusInWindow();
//
//			// Mouse movements are reflected on the status bar (coordinates). In case
//			// of painting rasterized, we must first find the next raster point.
//			mousePos = e.getPoint();
//			Point real;
//			if( rasterizedPaintMode ) {
//				newRasterizedPoint = getNextRasterPoint( mousePos );
//				real = CoordinateTools.translateToModel( newRasterizedPoint );
//			} else
//				real = CoordinateTools.translateToModel( new Point( e.getX(), e.getY() ) );
//			JZetWindow.sendMouse( real );
//		}
//		if( e.getID() == MouseEvent.MOUSE_DRAGGED )
//			if( dragStart != null )
//				// a) Drag a selected object
//				if( dragTargets != null ) {
//					// Mouse drags must update the displayed drag target icons but only if
//					// there is really a dragging process going on (click on draggable object
//					// must precede this method and initialize dragStart to != null)
//					int x_offset = e.getX() - dragStart.x;
//					int y_offset = e.getY() - dragStart.y;
//
//					Iterator<Point> itDS = dragStarts.iterator();
//					Iterator<Point> itDT = dragTargets.iterator();
//					Point p;
//
//					if( rasterizedPaintMode ) {
//						int rasterWidth = CoordinateTools.translateToScreen( ZETProperties.getRasterSizeSnap() );
//
//						while( itDS.hasNext() && itDT.hasNext() ) {
//							p = itDS.next();
//							itDT.next().setLocation(
//											(int)Math.round( (p.getX() + x_offset) / (double)rasterWidth ) * rasterWidth,
//											(int)Math.round( (p.getY() + y_offset) / (double)rasterWidth ) * rasterWidth );
//						}
//					} else
//						while( itDS.hasNext() && itDT.hasNext() ) {
//							p = itDS.next();
//							itDT.next().setLocation( p.getX() + x_offset, p.getY() + y_offset );
//						}
//				}
//		repaint();
//	}
//
//	/**
//	 * Returns the Room that is connected to the {@link JPolygon} which must be a
//	 * parent of the given Component {@code clickedOn}. Returns {@code null}
//	 * if such a Room does not exist
//	 * @return the connected room or {@code null} if no such room exists
//	 */
//	private Room findParent( Component clickedOn ) {
//		if( clickedOn instanceof JPolygon ) {
//			PlanPolygon poly = ((JPolygon)clickedOn).getPlanPolygon();
//			if( poly instanceof Area ) // find parent area
//				return ((Area)poly).getAssociatedRoom();
//			else
//				return poly instanceof Room ? (Room)poly : null;
//		} else
//			return null;
//	}
//
//	boolean strgPressed = false;
//
//	/**
//	 * Key Event Handler
//	 * Delete deletes selected polygons
//	 * @param e
//	 */
//	@Override
//	protected void processKeyEvent( KeyEvent e ) {
//		if( e.getID() == KeyEvent.KEY_PRESSED )
//			// Escape finishes the currently edited polygon
//			switch( e.getKeyCode() ) {
//				case KeyEvent.VK_CONTROL:
//					strgPressed = true;
//					break;
//				case KeyEvent.VK_ESCAPE:
//					if( newPolygon != null ) {
//						guiControl.getZControl().deletePolygon( newPolygon );
//						newPolygon = null;
//						lastClick = null;
//						//lastPlanClick = null;
//						ZETLoader.sendMessage( DefaultLoc.getSingleton().getString( "gui.message.PolgonCreationAborted" ) );
//					}
//					break;
//				case KeyEvent.VK_DELETE:
//					if( !selectedPolygons.isEmpty() ) {
//						List<PlanPolygon<?>> toDelete = new LinkedList<PlanPolygon<?>>();
//						for( JPolygon sel : selectedPolygons )
//							toDelete.add( sel.getPlanPolygon() );
//						guiControl.deletePolygon( toDelete );
//					}
//					break;
//				default:
//					super.processKeyEvent( e );
//			}
//		else if( e.getID() == KeyEvent.KEY_RELEASED )
//			switch( e.getKeyCode() ) {
//				case KeyEvent.VK_CONTROL:
//					strgPressed = false;
//					break;
//			}
//
//	}


//
//	// ACTION LISTENER STUFF
//	/**
//	 * Adds an {@code ActionListener}.
//	 * <p>
//	 * The {@code ActionListener} will receive an {@code ActionEvent}
//	 * when a selection has been made. If the combo box is editable, then
//	 * an {@code ActionEvent} will be fired when editing has stopped.
//	 *
//	 * @param l the {@code ActionListener} that is to be notified
//	 * @see javax.swing.JComboBox#setSelectedItem(java.lang.Object)
//	 */
//	public void addActionListener( ActionListener l ) {
//		listenerList.add( ActionListener.class, l );
//	}
//
//	/** Removes an {@code ActionListener}.
//	 *
//	 * @param l  the {@code ActionListener} to remove
//	 */
//	public void removeActionListener( ActionListener l ) {
//		listenerList.remove( ActionListener.class, l );
//	}
//
//	/**
//	 * Returns an array of all the {@code ActionListener}s added
//	 * to this JComboBox with addActionListener().
//	 *
//	 * @return all of the {@code ActionListener}s added or an empty
//	 *         array if no listeners have been added
//	 * @since 1.4
//	 */
//	public ActionListener[] getActionListeners() {
//		return listenerList.getListeners( ActionListener.class );
//	}
//	// Flag to ensure that infinite loops do not occur with ActionEvents.
//	private boolean firingActionEvent = false;
//
//	/**
//	 * Notifies all listeners that have registered interest for
//	 * notification on this event type.
//	 */
//	protected void fireActionEvent() {
//		if( !firingActionEvent ) {
//			// Set flag to ensure that an infinite loop is not created
//			firingActionEvent = true;
//			ActionEvent e = null;
//			// Guaranteed to return a non-null array
//			Object[] listeners = listenerList.getListenerList();
//			long mostRecentEventTime = EventQueue.getMostRecentEventTime();
//			int modifiers = 0;
//			AWTEvent currentEvent = EventQueue.getCurrentEvent();
//			if( currentEvent instanceof InputEvent )
//				modifiers = ((InputEvent)currentEvent).getModifiers();
//			else if( currentEvent instanceof ActionEvent )
//				modifiers = ((ActionEvent)currentEvent).getModifiers();
//			// Process the listeners last to first, notifying
//			// those that are interested in this event
//			for( int i = listeners.length - 2; i >= 0; i -= 2 )
//				if( listeners[i] == ActionListener.class ) {
//					// Lazily create the event:
//					if( e == null )
//						e = new ActionEvent( this, ActionEvent.ACTION_PERFORMED,
//										getActionCommand(),
//										mostRecentEventTime, modifiers );
//					((ActionListener)listeners[i + 1]).actionPerformed( e );
//				}
//			firingActionEvent = false;
//		}
//	}
//
//	/**
//	 * Returns the action command that is included in the event sent to
//	 * action listeners.
//	 * @return  the string containing the "command" that is sent to action listeners
//	 */
//	public String getActionCommand() {
//		return actionCommand;
//	}
//
//	protected String actionCommand = "roomSelected";
//}

}