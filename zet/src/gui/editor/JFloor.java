package gui.editor;

import gui.components.AbstractFloor;
import ds.z.Area;
import ds.z.Assignment;
import ds.z.AssignmentArea;
import ds.z.Barrier;
import ds.z.DelayArea;
import ds.z.PlanPoint;
import ds.z.PlanPolygon;
import ds.z.Edge;
import ds.z.EvacuationArea;
import ds.z.Floor;
import ds.z.InaccessibleArea;
import ds.z.Room;
import ds.z.RoomEdge;
import ds.z.SaveArea;
import ds.z.StairArea;
import gui.JEditor;
import gui.editor.planimage.PlanImage;
import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import localization.Localization;

/**
 * Graphical representation of a Floor from the BuildingPlan. Also offers features
 * for editing the displayed floor.
 * @author Timon Kelter, Jan-Philipp Kappmeier
 */
public class JFloor extends AbstractFloor implements ds.z.event.ChangeListener {

	// Main objects
	/** The displayed floor. */
	private Floor myFloor;
	/** The currently selected polygons. */
	private LinkedList<JPolygon> selectedPolygons = new LinkedList<JPolygon> ();

	// User interaction
	/** This field stored where the new PlanPoint would be inserted in rasterized
	 * paint mode if the user clicked into the {@link JFloor}. */
	private Point newRasterizedPoint;
	/** The last point that was clicked in GUI coordinate space. This is used during
	 * the creation of new polygons. It is redundant, because it's information could be
	 * gathered by using CoordinateTools.translateToScreen (lastPlanClick), but this 
	 * would slow down the paint method, which must be fast. So we included this variable
	 * for a better efficiency, despite it's redundancy. */
	private Point lastClick = null;
	/** The last point that was clicked in model coordinate space. This is used during
	 * the creation of new polygons, to let the event handler know where the next edge
	 * that he creates must start (pointwise creation) or where the starting point of
	 * the polygon is (rectangled creation). */
	private PlanPoint lastPlanClick = null;
	/** The current position of the mouse.  This is used during the creation of new 
	 * polygons to paint the preview of the next edge (pointwise creation) or the
	 * preview of the whole polygon (rectangled creation). */
	private Point mousePos = null;
	/** The point of the mouse click with which the user started dragging something. */
	private Point dragStart = null;
	/** The points were the objects that are to be dragged are located. This is
	 * approximately equal to dragStart when dragging a single PlanPoint, but when
	 * dragging a PlanPolygon f.e. the dragStarts are the screen locations of the
	 * PlanPoints of the PlanPolygon, and the dragStart variable in contrast to that
	 * is the position of the mouse when the drag started. So these are different
	 * variables in general. */
	private List<Point> dragStarts = null;
	/** This field stores where the dragged {@link PlanPoint}s will be located if the user
	 * releases the mouse button immediately. */
	private List<Point> dragTargets;
	/** The PlanPoints that are currently dragged. */
	private List<PlanPoint> draggedPlanPoints = null;
	/** The new polygon, if the user is in PolygonCreation EditMode .*/
	private PlanPolygon newPolygon = null;
	
	// Paint Styles
	/** The background image */
	private PlanImage planImage;
	/** Whether we are painting new mode in rasterized mode. */
	private boolean rasterizedPaintMode = true;
	
	// Helper methods and vars.
	/** The last color used for painting helping lines. */
	private Color lastColor = GUIOptionManager.getRoomEdgeColor ();
	/** Helper variable for redirecting events to all JPolygons on screen. */
	private boolean eventRedirectionGoingOn = false;
	/** The standard stroke. */
	private final static BasicStroke stroke_standard = new BasicStroke (1.0f,
		BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	/** The stroke used for painting the selection recatngle. */
	private final static BasicStroke selection_stroke = new BasicStroke (1.0f,
		BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3.0f, new float[]{4.0f, 2.0f}, 0.0f);
	
	
	/**
	 * Creates a new instance of <code>JFloor</code>.
	 */
	public JFloor () {
		super();
		setLayout (null);
		planImage = new PlanImage();
		setBackground( GUIOptionManager.getEditorBackgroundColor() );

		enableEvents (AWTEvent.MOUSE_EVENT_MASK);
		enableEvents (AWTEvent.KEY_EVENT_MASK);
		enableEvents (AWTEvent.MOUSE_WHEEL_EVENT_MASK);
	}

	/** Only call this after the component has been added to a container. Otherwise
	 * operations like setBounds, which are called herein, will fail.
	 * @param f the floor
	 */
	public void displayFloor( Floor f ) {
		boolean showDifferentFloor = (myFloor != f);
		LinkedList<PlanPolygon> old_selection = new LinkedList<PlanPolygon> ();
		
		if( myFloor != null ) {
			if( showDifferentFloor ) {
				myFloor.removeChangeListener( this );
			}

			// Clear & Save old selection
			for (JPolygon p : selectedPolygons) {
				old_selection.add (p.getPlanPolygon ());
			}
			clearSelection ();
			
			// Delete old polygons
			for( Component c : getComponents() ) {
				if( c instanceof JPolygon ) {
					((JPolygon) c).displayPolygon( null );
				}
			}
			removeAll();
		}

		myFloor = f;

		if( f != null ) {
			if( showDifferentFloor ) {
				myFloor.addChangeListener( this );
			}
			updateOffsets( f );
			
			// TODO: Provide better implementation - Do not recreate everything each time			
			for( Room r : f.getRooms() ) {
				JPolygon poly = new JPolygon( this,
								GUIOptionManager.getRoomEdgeColor() );
				add( poly );
				poly.displayPolygon( r );
			}

			// Recreate/Clear the click values and other temporary variables
			if( showDifferentFloor ) {
				resetEdit();
			} else {
				//showing the same floor again

				// Recreate the selection
				if (!old_selection.isEmpty ()) {
					recreateSelection( this, old_selection );
				}

				// mind a possibly new zoom factor when still creating a polygon
				if( lastPlanClick != null ) {
					lastClick = CoordinateTools.translateToScreen( lastPlanClick );
				}

				// abort temporary painting helper variables
				newRasterizedPoint = null;
				mousePos = null;

				// Abort drag processes
				dragStart = null;
				dragTargets = null;
				dragStarts = null;
				draggedPlanPoints = null;
			}

			planImage.setOffsetX( -floorMin_x );
			planImage.setOffsetY( -floorMin_y );

			if( showDifferentFloor )
				JEditor.sendMessage( Localization.getInstance (
						).getString ("gui.message.FloorSuccessfullyLoaded"));
		}
		
		revalidate();
		repaint();
	}

	/** Selects the given PlanPolygon. */
	private void recreateSelection (JComponent c, List<PlanPolygon> old_selection) {
		for (PlanPolygon p : old_selection) {
			for (Component x : c.getComponents ()) {
				if (x instanceof JPolygon &&
					((JPolygon) x).getPlanPolygon () == p) {

					selectPolygon ((JPolygon) x);
					break;
				}

				recreateSelection ((JComponent) x, old_selection);
			}
		}
	}

	/** Resets all temporary data concerning dragging processes, selection
	 * and click values and deletes polygons which are in creation. */
	public void resetEdit () {
		lastClick = null;
		lastPlanClick = null;
		clearSelection ();
		
		newRasterizedPoint = null;
		dragTargets = null;
		mousePos = null;
		dragStart = null;
		dragStarts = null;
		draggedPlanPoints = null;
		if (newPolygon != null) {
			newPolygon.delete ();
			newPolygon = null;
		}
	}
	
	@Override
	public void stateChanged (ds.z.event.ChangeEvent event) {
		if( !JEditor.getInstance().isUpdateDisabled() )
			displayFloor (myFloor);
	}

	/**
	 * <p>Paints the panel in the graphics object. It is possible to pass any
	 * graphics object, but it is particularly used for painting this panel. This
	 * can be used to save as bitmap or jpeg.</p>
	 * <p>It draws points and a raster and a background image, if set.</p>
	 * @param g The graphics object
	 */
	@Override
	public void paintComponent (Graphics g) {
		super.paintComponent (g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.drawRaster(g2);
		
		// Draw background image if set
		planImage.paintComponent( g2 );

		// Draw drag point and helping point in rasterizedPaintMode
		if (dragStart == null) {
			// Paint new Raster Point when in normal edit mode
			if (rasterizedPaintMode && newRasterizedPoint != null) {
				g2.drawRect (newRasterizedPoint.x - JPolygon.NODE_PAINT_RADIUS,
						newRasterizedPoint.y - JPolygon.NODE_PAINT_RADIUS,
						2 * JPolygon.NODE_PAINT_RADIUS, 2 * JPolygon.NODE_PAINT_RADIUS);
			}
		} else {
			// a) Paint drag Targets when dragging real objects
			if (dragTargets != null) {
				g2.setPaint (GUIOptionManager.getDragNodeColor ());
				for (Point p : dragTargets) {
					g2.drawRect (p.x - JPolygon.NODE_PAINT_RADIUS,
						p.y - JPolygon.NODE_PAINT_RADIUS,
						2 * JPolygon.NODE_PAINT_RADIUS, 2 * JPolygon.NODE_PAINT_RADIUS);
				}
			// b) Paint selection rectangle when dragging in selection mode
			} else {
				g2.setPaint (GUIOptionManager.getDragNodeColor ());
				g2.setStroke (selection_stroke);
				// No negative width/height allowed here, so work around it with Math
				g2.drawRect (Math.min (dragStart.x, mousePos.x), 
						Math.min (dragStart.y, mousePos.y), 
						Math.abs (mousePos.x - dragStart.x), 
						Math.abs (mousePos.y - dragStart.y));
				g2.setStroke (stroke_standard);
			}
		}

		// If in PolygonCreationMode, draw help-line
		if (GUIOptionManager.getEditMode ().getType () == EditMode.Type.CREATION_POINTWISE ||
				GUIOptionManager.getEditMode ().getType () == EditMode.Type.CREATION_RECTANGLED) {
			if (lastClick != null & mousePos != null) {
				Point p1;
				Point p2;
				if (rasterizedPaintMode) {
					p1 = getNextRasterPoint( lastClick );
					p2 = getNextRasterPoint( mousePos );
				} else {
					p1 = lastClick;
					p2 = mousePos;
				}
				Color t = GUIOptionManager.getEditMode ().getEditorColor ();
				if (t != null) {
					lastColor = t;
				}
				g2.setPaint (lastColor);
				
				// Now draw the single line or the rectangle (depending on the edit mode)
				if (GUIOptionManager.getEditMode ().getType () == EditMode.Type.CREATION_POINTWISE) {
					g2.drawLine (p1.x, p1.y, p2.x, p2.y);
				} else if (GUIOptionManager.getEditMode ().getType () == EditMode.Type.CREATION_RECTANGLED) {
					g2.drawRect (Math.min (p1.x, p2.x), Math.min (p1.y, p2.y), 
							Math.abs (p1.x - p2.x), Math.abs (p1.y - p2.y));
				}
			}
		}
	}

	
	/** Determines whether rasterized paint mode is active or not.
	 * @return true if rasterized mode is active, false otherwise
	 */
	public boolean getRasterizedPaintMode () {
		return rasterizedPaintMode;
	}
	
	public PlanImage getPlanImage() {
		return planImage;
	}

	/**
	 * Returns the currently selected polygons on the floor. These are 
	 * <code>JPolygon</code>s which can contain any polygon, such as rooms, areas
	 * or any general {@link PlanPolygon}.
	 * @return the selected polygons
	 */
	public List<JPolygon> getSelectedPolygons() {
		return Collections.unmodifiableList (selectedPolygons);
	}

	/**
	 * Sets the rasterize mode active or inactive.
	 * @param rasterizedPaintMode indicates the status
	 */
	public void setRasterizedPaintMode (boolean rasterizedPaintMode) {
		this.rasterizedPaintMode = rasterizedPaintMode;
	}

	/** Searches for a JPolygon on screen that shows the given PlanPolygon
	 * 
	 * @param poly the polygon
	 * @return The matching JPolygon or null if PlanPolygon is not shown by any JPolygon on this floor.
	 */
	public JPolygon getJPolygon (PlanPolygon poly) {
		for (Component c : getComponents ()) {
			// Room level
			if (c instanceof JPolygon) {
				if (((JPolygon)c).getPlanPolygon () == poly) {
					return (JPolygon)c;
				}
				
				for (Component cx : ((JPolygon)c).getComponents ()) {
					// Area level
					if (cx instanceof JPolygon && 
						((JPolygon)cx).getPlanPolygon () == poly) {
						return (JPolygon)cx;
					}
				}
			}
		}
		return null;
	}
	
	/** Sets the given Polygon to be the one and only 
	 * selected polygon on the JFloor
	 * @param p the polygon
	 */
	public void setSelectedPolygon (JPolygon p) {
		clearSelection ();
		selectPolygon (p);
	}
	
	/** Sets the given Polygon to be the one and only selected polygon on the
	 * <code>JFloor</code> if it is shown on this <code>JFloor</code> at all.
	 * @param p the polygon
	 */
	public void setSelectedPolygon (PlanPolygon p) {
		JPolygon jp = getJPolygon (p);
		if (jp != null) {
			setSelectedPolygon (jp);
		}
	}
	
	/** Selects the given polygon on the screen
	 * @param p The polygon to be selected. May be null.
	 */
	public void selectPolygon (JPolygon p) {
		if (p != null && !selectedPolygons.contains (p)) {
			selectedPolygons.add (p);
			p.setSelected (true);
			fireActionEvent();
		}
	}
	
	/** Selects the given polygon on the screen if it's on this JFloor
	 * @param p The polygon to be selected. May be null.
	 */
	public void selectPolygon (PlanPolygon p) {
		if (p != null) {
			JPolygon jp = getJPolygon (p);
			if (jp != null) {
				selectPolygon (jp);
			}
		}
	}
	
	/** De-selects the given polygon on the screen
	 * @param p The polygon to be unselected. May be null.
	 */
	public void unselectPolygon (JPolygon p) {
		if (p != null) {
			selectedPolygons.remove (p);
			p.setSelected (false);
			fireActionEvent();
		}
	}
	
	/** De-selects the given polygon on the screen if it's on this JFloor
	 * @param p The polygon to be unselected. May be null.
	 */
	public void unselectPolygon (PlanPolygon p) {
		if (p != null) {
			JPolygon jp = getJPolygon (p);
			if (jp != null) {
				unselectPolygon (jp);
			}
		}
	}
	
	/** Efficiently De-selects all selected polygons on the screen
	 */
	public void clearSelection () {
		for (JPolygon p : selectedPolygons) {
			p.setSelected (false);
		}
		selectedPolygons.clear ();
		fireActionEvent();
	}
	
	/** Show the given polygon to the user by scrolling until it is visible and
	 * setting it as the selected polygon (clears the previous selection). If the
	 * given polygon is not shown on this JFloor nothing will happen. */
	public void showPolygon (PlanPolygon p) {
		if (p instanceof Area) {
			p = ((Area)p).getAssociatedRoom ();
		}
		JPolygon jp = getJPolygon (p);
		
		if (jp != null) {
			scrollRectToVisible( jp.getBounds() );
			setSelectedPolygon( jp );
		}
	}
	
	/** This method returns <b>all</b> JPolygons which are contained in "c"
	 * and contain the specified point. In contrast to that, the API method 
	 * findComponentAt only returns <b>1</b> component which contains the given point.
	 *
	 * @param c The container in which the search should be performed
	 * @param p The point, which the sought-after components must contain. The 
	 * coordinates must be relative to Conatiner c
	 * @return A list of JPolygons which contain point p. This list will be
	 * empty in the case that such JPolygons do not exist.
	 */
	public static List<JPolygon> findAllPolygonsAt (Container c, Point p) {
		// For performance reasons we use only one single list for all recursive calls
		LinkedList<JPolygon> result = new LinkedList<JPolygon> ();
		findAllPolygonsAtImpl (c, p, result);
		return result;
	}
	
	/** This is an internal helper method. Never call it. 
	 * Call findAllComponentsAt (Container c, Point p) instead. */
	private static void findAllPolygonsAtImpl (Container c, Point p, 
			List<JPolygon> polygonList) {
		for (Component comp : c.getComponents ()) {
			Point relative_point = SwingUtilities.convertPoint (c, p, comp);
			if (comp instanceof JPolygon) {
				JPolygon poly = (JPolygon)comp;
				
				// Test if the Polygon has the point (Barriers pass a slightly less strict test)
				if (poly.getPlanPolygon () instanceof Barrier) {
					if (poly.getBounds ().contains (p)) {
						polygonList.add (poly);
					}
				} else {
					if (poly.getDrawingPolygon ().contains (relative_point)) {
						polygonList.add (poly);
					}
				}
				
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
				findAllPolygonsAtImpl (poly, relative_point, polygonList);
			}
		}
	}

	/** Mouse Event Handler */
	@Override
	protected void processMouseEvent (MouseEvent e) {
		if (e.getID () == MouseEvent.MOUSE_PRESSED) {
			// Clear status bar
			JEditor.sendError( "" );
			JEditor.sendReady();

			if (e.getButton () == MouseEvent.BUTTON1) {

				if (GUIOptionManager.getEditMode () == EditMode.Selection) {
					// Double clicks select polygons
					// Single clicks start dragging when a polygon was selected

					if (e.getClickCount () == 1) {
						// Single click in Selection Mode: 
						Object clickedOn = null;
						for (JPolygon sel : selectedPolygons) {
							clickedOn = sel.findClickTargetAt (
								SwingUtilities.convertPoint (JFloor.this, 
								e.getPoint (), sel));
							if (clickedOn != null) {
								break;
							}
						}
							
						// a) Start dragging, if a polygon was already selected
						if (clickedOn != null) {
							dragStart = e.getPoint ();

							if (clickedOn instanceof PlanPoint) {
								PlanPoint dp = (PlanPoint)clickedOn;
								
								// Drag single Point
								draggedPlanPoints = new ArrayList<PlanPoint> (1);
								draggedPlanPoints.add (dp);
								
								// Get the affected barrier (if there is one)
								Barrier barrier = null;
								if (dp.getNextEdge () != null && 
										dp.getNextEdge ().getAssociatedPolygon () instanceof Barrier) {
									barrier = (Barrier) dp.getNextEdge ().getAssociatedPolygon ();
								} else if (dp.getPreviousEdge () != null && 
										dp.getPreviousEdge ().getAssociatedPolygon () instanceof Barrier) {
									barrier = (Barrier) dp.getPreviousEdge ().getAssociatedPolygon ();
								} 
								if (barrier != null) {
									Iterator<PlanPoint> iP = barrier.pointIterator (false);
									while (iP.hasNext ()) {
										PlanPoint p = iP.next ();
										if (dp != p && p.equals (dp)) {
											draggedPlanPoints.add (p);
											break;
										}
									}
								}
							} else if (clickedOn instanceof Edge) {
								Edge edge = (Edge)clickedOn;
								// Drag whole edge
								draggedPlanPoints = edge.getPlanPoints ();
								
								// Get the affected barrier (if there is one)
								Barrier barrier = (edge.getAssociatedPolygon () instanceof Barrier) ?
									(Barrier)edge.getAssociatedPolygon () : null;
								if (barrier != null) {
									for (Edge be : barrier) {
										if (be != edge && be.equals (edge)) {
											draggedPlanPoints.addAll (be.getPlanPoints ());
											break;
										}
									}
								}
							} else if (clickedOn instanceof PlanPolygon) {
								// Drag whole selection (>= 1 polygon)
								draggedPlanPoints = new LinkedList<PlanPoint> ();
								for (JPolygon sel : selectedPolygons) {
									draggedPlanPoints.addAll(((PlanPolygon)
											sel.getPlanPolygon ()).getPlanPoints ());
								}
							}

							// Initialize DragTargets & Starts (on-screen coordinates)
							dragStarts = new ArrayList<Point> (draggedPlanPoints.size ());
							dragTargets = new ArrayList<Point> (draggedPlanPoints.size ());
							Point translated;
							for (PlanPoint p : draggedPlanPoints) {
								translated = CoordinateTools.translateToScreen (p);
								dragStarts.add (translated);
								dragTargets.add (new Point (translated));
							}
						} else { //b) Start to select multiple polygons by dragging 
								 //	  a rectangle around them
							dragStart = e.getPoint ();
						}
					} else {
						// Double click in SelectionMode: Select a polygon
						
						List<JPolygon> clickedPolygons = findAllPolygonsAt (JFloor.this, e.getPoint ());

						// Get the new selection
						ListIterator<JPolygon> itPoly = clickedPolygons.listIterator ();
						JPolygon toSelect = null;

						// If none of the polygons that we clicked on is selected,
						// then just select the top-level one. If a JPolygon is selected
						// then switch the selection over to the next JPolygon in 
						// the given order of polygons
						while (itPoly.hasNext ()) {
							toSelect = itPoly.next ();

							if (toSelect.isSelected ()) {
								toSelect = itPoly.hasNext () ? itPoly.next () :
									clickedPolygons.get (0);
								break;
							}
						}
						
						// Clear old selection & Select new
						setSelectedPolygon (toSelect);
					}		
				} else if (GUIOptionManager.getEditMode () == EditMode.StairAreaMarkLowerLevel) {
					// The stair must be selected when we enter this code
					JPolygon stair = selectedPolygons.getFirst ();
					
					Object clickedOn = stair.findClickTargetAt (
							SwingUtilities.convertPoint (JFloor.this, e.getPoint (), 
							stair));
					if (clickedOn instanceof Edge && ((Edge)clickedOn).getAssociatedPolygon (
							) == stair.getPlanPolygon ()) {
						StairArea a = (StairArea)stair.getPlanPolygon ();
						Edge ne = (Edge)clickedOn;
						try {
							if (a.getLowerLevelStart () == null) {
								// Initial edge
								a.setLowerLevel (ne.getSource (), ne.getTarget ());
							} else {
								// Further edges
								if (ne.getSource () == a.getLowerLevelEnd ()) {
									a.setLowerLevel (a.getLowerLevelStart (), ne.getTarget ());
								} else if (ne.getTarget () == a.getLowerLevelStart ()) {
									a.setLowerLevel (ne.getSource (), a.getLowerLevelEnd ());
								} else {
									JEditor.sendError( Localization.getInstance ().getString (
											"gui.error.ConnectedEdgeTrailsOnly") );
								}
							}
							if (!e.isControlDown ()) {
								// Workaround to keep the previous edit mode setting
								GUIOptionManager.setEditMode(GUIOptionManager.getPreviousEditMode ());

								GUIOptionManager.setEditMode(EditMode.StairAreaMarkUpperLevel);
								JEditor.sendMessage( Localization.getInstance (
									).getString ("gui.message.SelectUpperStairLevel") );
							} else {
								// Refresh our select lower level message
								JEditor.sendMessage( Localization.getInstance (
									).getString ("gui.message.SelectLowerStairLevel"));
							}
						} catch (IllegalArgumentException ex) {
							JEditor.sendError( ex.getLocalizedMessage () );
						}
					} else {
						JEditor.sendError( Localization.getInstance ().getString (
											"gui.error.ClickOnStairEdge")) ;
					}
				} else if (GUIOptionManager.getEditMode () == EditMode.StairAreaMarkUpperLevel) {
					// The stair must be selected when we enter this code
					JPolygon stair = selectedPolygons.getFirst ();
					
					Object clickedOn = stair.findClickTargetAt (
							SwingUtilities.convertPoint (JFloor.this, e.getPoint (), 
							stair));
					if (clickedOn instanceof Edge && ((Edge)clickedOn).getAssociatedPolygon (
							) == stair.getPlanPolygon ()) {
						StairArea a = (StairArea)stair.getPlanPolygon ();
						Edge ne = (Edge)clickedOn;
						try {
							if (a.getUpperLevelStart () == null) {
								// Initial edge
								a.setUpperLevel (ne.getSource (), ne.getTarget ());
							} else {
								// Further edges
								if (ne.getSource () == a.getUpperLevelEnd ()) {
									a.setUpperLevel (a.getUpperLevelStart (), ne.getTarget ());
								} else if (ne.getTarget () == a.getUpperLevelStart ()) {
									a.setUpperLevel (ne.getSource (), a.getUpperLevelEnd ());
								} else {
									JEditor.sendError( Localization.getInstance ().getString (
											"gui.error.ConnectedEdgeTrailsOnly") );
								}
							}
							if (!e.isControlDown ()) {
								GUIOptionManager.setEditMode(GUIOptionManager.getPreviousEditMode ());
								JEditor.sendMessage( Localization.getInstance ().getString (
											"gui.message.StairSuccessfullyCreated") );
							} else {
								// Refresh our select upper level message
								JEditor.sendMessage( Localization.getInstance (
									).getString ("gui.message.SelectUpperStairLevel"));
							}
						} catch (IllegalArgumentException ex) {
							JEditor.sendError( ex.getLocalizedMessage () );
						}
					} else {
						JEditor.sendError( Localization.getInstance ().getString (
											"gui.error.ClickOnStairEdge") );
					}
				} else {
					// Click in non-selection mode: Start creating a polygon or 
					// continue creating one if newPolygon is already != null
					
					// Get the model points which we clicked on
					PlanPoint p1 = lastPlanClick;
					PlanPoint p2 = new PlanPoint (CoordinateTools.translateToModel ( rasterizedPaintMode ? getNextRasterPoint (e.getPoint ()) : e.getPoint ()));

					// In case we are editing an area, check whether the clicks are valid 
					// (within the containing polygon)
					Room parent = null;
					if (GUIOptionManager.getEditMode ().doesCreateSubpolygons ()) {
						//parent = findParent (findComponentAt (e.getPoint ()));
						parent = findParent( findRoomAt( e.getPoint() ) );
						if (parent == null) {
							JEditor.sendError( Localization.getInstance ().getString ( "gui.error.SelectARoom") );
							return;
						}

						// Check whether we clicked into the same room as before
						if (lastClick != null) {
							if (!parent.equals (((Area)newPolygon).getAssociatedRoom ())) {
								JEditor.sendError( Localization.getInstance ().getString (
										"gui.error.SelectCoordinatesInSameRoom") );
								return;
							}
						}
					}

					if (lastClick == null) {
						// First create a new room / area - this is the same for RECTANGLED and POINTWISE
						switch (GUIOptionManager.getEditMode ()) {
							case RoomCreationPointwise:
							case RoomCreation:
								newPolygon = new Room (myFloor); break;
							case AssignmentAreaCreationPointwise:
							case AssignmentAreaCreation:
								Assignment cur = JEditor.getInstance().getEditView().getProject().getCurrentAssignment ();
								if (cur != null) {
									if (cur.getAssignmentTypes ().size () > 0) {
										newPolygon = new AssignmentArea (parent, cur.getAssignmentTypes ().get (0)); 
									} else {
										JEditor.sendError( Localization.getInstance ().getString (
											"gui.error.CreateAnAssignmentFirst") );
											return;
									}
								} else {
									JEditor.sendError( Localization.getInstance ().getString (
											"gui.error.SetCurrentAssignmentFirst") );
									return;
								}
								break;
							case BarrierCreationPointwise:
								newPolygon = new Barrier (parent); break;
							case DelayAreaCreationPointwise:
							case DelayAreaCreation:
								newPolygon = new DelayArea (parent, DelayArea.DelayType.OBSTACLE, 0.7d); break;
							case StairAreaCreationPointwise:
							case StairAreaCreation:
								newPolygon = new StairArea (parent); break;
							case EvacuationAreaCreationPointwise:
							case EvacuationAreaCreation:
								newPolygon = new EvacuationArea (parent);
								int count = JEditor.getInstance().getProject().getPlan().getEvacuationAreasCount();
								String name = Localization.getInstance().getString( "ds.z.DefaultName.EvacuationArea" ) + " " + count;
								((EvacuationArea)newPolygon).setName( name );
								break;
							case InaccessibleAreaCreationPointwise:
							case InaccessibleAreaCreation:
								newPolygon = new InaccessibleArea (parent); break;
							case SaveAreaCreationPointwise:
							case SaveAreaCreation:
								newPolygon = new SaveArea (parent); break;
							default:
								break;
						}
					} else {
						// The commands for the different types of areas/rooms differ only in the creation phase
						if (GUIOptionManager.getEditMode ().getType () == EditMode.Type.CREATION_RECTANGLED) {
							try {
								completeRectangledPolygon (newPolygon, p1, p2);
							} catch( IllegalArgumentException ex ) {
								JEditor.sendError( Localization.getInstance ().getString (
											"gui.error.RectangleCreationZeroArea") );
								return;
							}
						} else if (GUIOptionManager.getEditMode ().getType () == EditMode.Type.CREATION_POINTWISE) {
							try {
								if (newPolygon instanceof Room) {
									new RoomEdge (p1, p2, (Room)newPolygon);
								} else {
									new Edge (p1, p2, newPolygon);
								}
							} catch( IllegalArgumentException ex ) {
								// Tried to select equal points
								JEditor.sendError( Localization.getInstance ().getString (
											"gui.error.ChooseDifferentPoint") );
							}
						}
								// Set the correct number of persons to assignment areas
								if( newPolygon instanceof AssignmentArea ) 
									((AssignmentArea)newPolygon).setEvacuees( Math.min( newPolygon.getMaxEvacuees(), ((AssignmentArea)newPolygon).getAssignmentType().getDefaultEvacuees() ) );
					}

					if (newPolygon != null && newPolygon.isClosed ()) {
						 // Room closed, with next click create new instance
						
						polygonFinishedHandler ();
					} else {
						// Polygon not closed - prepare next point
						lastClick = e.getPoint ();
						lastPlanClick = p2;
					}

				}
			} else if (e.getButton () == MouseEvent.BUTTON3) {
				// This method already contains the EditMode analysis
				if (GUIOptionManager.getEditMode ().getType () == EditMode.Type.CREATION_POINTWISE) {
					// Create last Edge and close the polygon

					// FALSE: Special case: Barriers are left open ( as a polygon )
					// This is no longer the case, because unclosed polygons won't work eith the rasterization
					if (newPolygon != null) {
						if( newPolygon.getNumberOfEdges() == 0 ) {
							// Delete empty, aborted polygons
							newPolygon.delete ();
						} else {
							if (newPolygon.getNumberOfEdges() >= ((newPolygon instanceof Barrier) ? 1 : 3)) {
								newPolygon.close ();
							} else {
								JEditor.sendError ( Localization.getInstance ().getString (
										"gui.error.CreateAtLeastThreeEdges"));
								return;
							}
						}
					}

					polygonFinishedHandler ();
				}
			}
		} else if (e.getID () == MouseEvent.MOUSE_RELEASED) {
			// Complete the drag if you've begun one
			if (dragStart != null) {
				// a) Dragged some polygons
				if (dragTargets != null) {
					boolean trueDrag = false;

					try {
						// Translate the drag targets into model coordinates
						// this must be done beforehand, because the GUI coords are
						// worthless if the floor offsets change while
						// setting the single points to their new locations
						// (which is almost guaranteed at the first drawn polygons)
						for (Point p : dragTargets) {
							p.setLocation (CoordinateTools.translateToModel(p));
						}

						Iterator<PlanPoint> itPP = draggedPlanPoints.iterator ();
						Iterator<Point> itDT = dragTargets.iterator ();
						Point newLocation;
						PlanPoint planPoint;
						
						// The algorithm for detecting the affected areas will only
						// produce a list that is free of duplicate entries if all
						// plan points that belong to a single polygon follow each
						// other in the list of the dragged points
						HashSet<Area> affected_areas = new HashSet<Area> ();
						PlanPolygon lastPolygon = null;
						while (itPP.hasNext () && itDT.hasNext ()) {
							// The drag targets are already rasterized, if neccessary
							planPoint = itPP.next ();
							newLocation = itDT.next ();
							if (!trueDrag && !newLocation.equals (planPoint.getLocation ())) {
								// Check for !trueDrag to update "trueDrag" only once
								trueDrag = true;
							}

							planPoint.setLocation (newLocation);
							
							// Keep track of the areas that we move
							PlanPolygon currentPolygon = 
								planPoint.getNextEdge () != null ?
								planPoint.getNextEdge ().getAssociatedPolygon () : 
								planPoint.getPreviousEdge () != null ?
								planPoint.getPreviousEdge ().getAssociatedPolygon () : null;
							
							// Add the last polygon (whose modification should be complete
							// by now to avoid duplicate entries in the area hashset)
							if (currentPolygon != lastPolygon) {
								if (lastPolygon != null && lastPolygon instanceof Area) {
									affected_areas.add ((Area)lastPolygon);
								}
								lastPolygon = currentPolygon;
							}
						}
						
						// Add the last considered polygon to the area set (if it is an area)
						if (lastPolygon != null && lastPolygon instanceof Area) {
							affected_areas.add ((Area)lastPolygon);
						}
						
						// If the user dragged areas into a different room, then we
						// must assign the affected areas to their new rooms
						for (Area a : affected_areas) {
							// If the area has left its room:
							if (!a.getAssociatedRoom ().contains(a)) {
								// 1) Search for the new room
								Room newRoom = null;
								
								for (Component c : getComponents ()) {
									if (((JPolygon)c).getPlanPolygon ().contains (a)) {
										newRoom = (Room)((JPolygon)c).getPlanPolygon ();
										break;
									}
								}
								
								if (newRoom != null) {
									// Assign new room if we found one
									a.setAssociatedRoom (newRoom);
									selectPolygon (a);
								} else {
									// Delete the area if it was dragged out of 
									// its old room and not into any new one
									a.delete ();
								}
							}
						}
					} catch( Exception ex ) {
						JEditor.sendError( Localization.getInstance ().getString (
								"gui.message.ExceptionDuringDrag") );
						ex.printStackTrace ();
					}

					// The event that is thrown by setLocation triggers an automatic
					// redisplay of the JFloor and the drag mode is switched off during 
					// this repaint redisplay (@see #displayFloor(Floor))

					// Unfortunately this event is only thrown in case that at least one
					// point really changed it's position which might be false. In this case
					// we repaint manually to erase the dragTarget painting on screen
					if (!trueDrag) {
						dragStart = null;
						dragTargets = null;
						dragStarts = null;
						draggedPlanPoints = null;
						repaint ();
					}
					
				// b) Dragged a selection rectangle
				} else if (!dragStart.equals (e.getPoint ())) {
					// No negative width/height allowed here, so work around it with Math
					Rectangle selectionArea = new Rectangle (
						Math.min (dragStart.x, mousePos.x), 
						Math.min (dragStart.y, mousePos.y), 
						Math.abs (mousePos.x - dragStart.x), 
						Math.abs (mousePos.y - dragStart.y));
					clearSelection ();
					
					for (Component room : getComponents ()) {
						// Search for contained rooms
						Rectangle room_bounds = ((JPolygon)room).getBounds ();
						if (selectionArea.contains (room_bounds)) {
							selectPolygon ((JPolygon)room);
						} else if (selectionArea.intersects (room_bounds)) {
							// If the room as a whole is not contained, then at
							// least some areas within it may be inside the selection 
							// area
							// Note that we do not explicitly select the areas
							// when the full room is selected, because areas always
							// follow the movement of the room automatically.
							for (Component area : ((JPolygon)room).getComponents ()) {
								Rectangle area_bounds = ((JPolygon)area).getBounds ();
								area_bounds.translate(room.getX (), room.getY ());
								if (selectionArea.contains (area_bounds)) {
									selectPolygon ((JPolygon)area);
								}
							}
						}
					}
				}
				dragStart = null;
				repaint (); // Clear selection shape
			}
		}
		
		// Enable all JPolygons which contain the given point to process this 
		// event (Swing normally only informs the first JPolygon that it finds)
		
		// Do not use e.isPopupTrigger() here - Won't work under linux
		if (e.getButton () == MouseEvent.BUTTON3 && !eventRedirectionGoingOn) {
			try {
				eventRedirectionGoingOn = true;
				Room clickedOn = findParent ((Component)e.getSource ());

				if (clickedOn != null) {
					for (Component c : getComponents ()) {
						if ((c instanceof JPolygon) && 
								c.getBounds ().contains (e.getPoint ())) {
							// Dispatch only to an area or to a room
							boolean area_popup = false;

							Point translated = SwingUtilities.convertPoint (this, e.getPoint (), c);
							for (Component ed : ((JPolygon)c).getComponents ()) {
								Point tempTrans = SwingUtilities.convertPoint (c, translated, ed);
								MouseEvent areaEvent = new MouseEvent (ed, 
										e.getID (), e.getWhen (), e.getModifiers (), tempTrans.x, 
										tempTrans.y, e.getClickCount (), e.isPopupTrigger ());
								if (ed.contains (tempTrans) && ed instanceof JPolygon &&
										((JPolygon)ed).isPopupTrigger (areaEvent)) {
									// Dispatch to the area
									ed.dispatchEvent (areaEvent);
									area_popup = true;
									break;
								}
							}

							// Dispatch to the room
							MouseEvent roomEvent = new MouseEvent (c, 
									e.getID (), e.getWhen (), e.getModifiers (), translated.x, 
									translated.y, e.getClickCount (), e.isPopupTrigger ());
							if (!area_popup && ((JPolygon)c).isPopupTrigger (roomEvent)) {
								c.dispatchEvent (roomEvent);
							}
						}
					}
				}
			} finally {
				eventRedirectionGoingOn = false;
			}
		}
	}
	
	/**
	 * <p>Tries to find the <code>ds.z.Room</code> that lies at a given point p.
	 * The point is assumed to be in the coordinate system of the floor and thus
	 * transformed to model coordinates first.</p>
	 * <p>The first room in which the point is contained is returned. </p>
	 * @param p the point
	 * @return the room containing the point or null
	 */
	private Component findRoomAt( Point p ) {
		Point model = CoordinateTools.translateToModel( p );
		Component found = null;
		for( Component c : this.getComponents() ) {
			if( c instanceof JPolygon ) {
				PlanPolygon poly = ((JPolygon)c).getPlanPolygon();
				if( poly.contains( new PlanPoint( model ) ) ) {
					found = c;
				}
			}
		}
		return found;
	}
	
	/** Only internal: To be called after a polygon was created to clean up our data structures. */
	private void polygonFinishedHandler () {
		// Select the newly created polygon
		setSelectedPolygon (newPolygon);

		newPolygon = null;
		lastClick = null;
		lastPlanClick = null;
		JEditor.sendMessage( Localization.getInstance ().getString ("gui.message.PolgonSuccessfullyCreated"));
		if (GUIOptionManager.getEditMode () == EditMode.StairAreaCreationPointwise ||
			GUIOptionManager.getEditMode () == EditMode.StairAreaCreation) {
			GUIOptionManager.setEditMode(EditMode.StairAreaMarkLowerLevel);
			JEditor.sendMessage( Localization.getInstance ().getString ("gui.message.SelectLowerStairLevel"));
		}
	}
	
	/** Mouse Motion Event Handler */
	@Override
	protected void processMouseMotionEvent (MouseEvent e) {
		if( JEditor.isEditing() )
			return;
		if (e.getID () == MouseEvent.MOUSE_DRAGGED || e.getID () == MouseEvent.MOUSE_MOVED) {
			// Grab focus to deliver new key events to this JFloor
			requestFocusInWindow ();
			
			// Mouse movements are reflected on the status bar (coordinates). In case
			// of painting rasterized, we must first find the next raster point.
			mousePos = e.getPoint ();
			Point real;
			if (rasterizedPaintMode) {
				newRasterizedPoint = getNextRasterPoint (mousePos);
				real = CoordinateTools.translateToModel (newRasterizedPoint);
			} else {
				real = CoordinateTools.translateToModel (new Point (e.getX (),e.getY ()));
			}
			JEditor.sendMouse( real );
		}
		if (e.getID () == MouseEvent.MOUSE_DRAGGED) {
			if (dragStart != null) {
				// a) Drag a selected object
				if (dragTargets != null) {
					// Mouse drags must update the displayed drag target icons but only if
					// there is really a dragging process going on (click on draggable object
					// must precede this method and initialize dragStart to != null) 
					int x_offset = e.getX () - dragStart.x;
					int y_offset = e.getY () - dragStart.y;

					Iterator<Point> itDS = dragStarts.iterator ();
					Iterator<Point> itDT = dragTargets.iterator ();
					Point p;	

					if (rasterizedPaintMode) {
						int rasterWidth = CoordinateTools.translateToScreen (400);

						while (itDS.hasNext () && itDT.hasNext ()) {
							p = itDS.next ();
							itDT.next ().setLocation (
								(int) Math.round ((p.getX () + x_offset) / 
									(double) rasterWidth) * rasterWidth,
								(int) Math.round ((p.getY () + y_offset) / 
									(double) rasterWidth) * rasterWidth);
						}
					} else {
						while (itDS.hasNext () && itDT.hasNext ()) {
							p = itDS.next ();
							itDT.next ().setLocation (p.getX () + x_offset,
								p.getY () + y_offset);
						}
					}
				}
			}
		}
		repaint ();
	}
		
	/** This method adds Edges to the Polygon p so that is gets into a rectangular 
	 * shape where p1 is the top left point and p2 the bottom right point of the resulting 
	 * rectangle. 
	 * @param p This must be an empty PlanPolygon (with no edges)
	 * @param p1 / p2 These have to be coordinates in the model space
	 */
	private void completeRectangledPolygon (PlanPolygon p, Point p1, Point p2) {
		if( p1.getX() == p2.getX() || p1.getY() == p2.getY() ) {
			throw new IllegalArgumentException( Localization.getInstance ().getString (
					"gui.error.RectangleCreationZeroArea") );
		}
		LinkedList<PlanPoint> points = new LinkedList<PlanPoint> ();
		points.add ( new PlanPoint (p1.x, p1.y) );
		points.add ( new PlanPoint (p1.x, p2.y) );
		points.add ( new PlanPoint (p2.x, p2.y) );
		points.add ( new PlanPoint (p2.x, p1.y) );
		p.add (points);
	}

	/** @return The Room that is connected to the JPolygon which must be a 
	 * parent of the given Component <code>clickedOn</code>. Returns 'null' 
	 * if such a Room does not exist */
	private Room findParent (Component clickedOn) {
		if (clickedOn instanceof JPolygon) {
			PlanPolygon poly = ((JPolygon) clickedOn).getPlanPolygon ();
			if (poly instanceof Area) { // find parent area
				return ((Area) poly).getAssociatedRoom ();
			} else if (poly instanceof Room) {
				return (Room) poly;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Key Event Handler
	 * Delete deletes selected polygons
	 */
	@Override
	protected void processKeyEvent (KeyEvent e) {
		if (e.getID () == KeyEvent.KEY_PRESSED) {
			// Escape finishes the currently edited polygon
			switch( e.getKeyCode () ) {
				case KeyEvent.VK_ESCAPE:
					if (newPolygon != null) {
						newPolygon.delete ();
						newPolygon = null;
						lastClick = null;
						lastPlanClick = null;
					}
					break;
				case KeyEvent.VK_DELETE:
					if (!selectedPolygons.isEmpty ()) {
						List<PlanPolygon> toDelete = new LinkedList<PlanPolygon> ();
						for (JPolygon sel : selectedPolygons) {
							toDelete.add (sel.getPlanPolygon ());
						}
						for (PlanPolygon p : toDelete) {
							p.delete ();
						}
					}
					break;
				default:
					super.processKeyEvent( e );
			}
		}
			
	}
	
	/** Used for zooming in and out with the mouse wheel. */
	@Override
	protected void processMouseWheelEvent (MouseWheelEvent e) {
		double oldZoom = CoordinateTools.getZoomFactor ();
		if (e.getScrollType () == MouseWheelEvent.WHEEL_BLOCK_SCROLL) {
			if (e.getWheelRotation () < 0) {
				oldZoom = Math.min (oldZoom * 2, 0.25d);
			} else {
				oldZoom = Math.max (oldZoom * 2, 0.01d);
			}
		} else {
			double offset = (e.getScrollAmount () * Math.abs (e.getWheelRotation ())) / 100.0d;
			offset /= 4; // Make offset smaller, otherwise it's too fast
			
			if (e.getWheelRotation () < 0) {
				oldZoom = Math.min (oldZoom + offset, 0.25d);
			} else {
				oldZoom = Math.max (oldZoom - offset, 0.01d);
			}
		}
		JEditor.getInstance ().setZoomFactor (oldZoom);
	}
	
	// ACTION LISTENER STUFF
	/** 
	 * Adds an <code>ActionListener</code>. 
	 * <p>
	 * The <code>ActionListener</code> will receive an <code>ActionEvent</code>
	 * when a selection has been made. If the combo box is editable, then
	 * an <code>ActionEvent</code> will be fired when editing has stopped.
	 *
	 * @param l  the <code>ActionListener</code> that is to be notified
	 * @see #setSelectedItem
	 */
	public void addActionListener( ActionListener l ) {
		listenerList.add( ActionListener.class, l );
	}

	/** Removes an <code>ActionListener</code>.
	 *
	 * @param l  the <code>ActionListener</code> to remove
	 */
	public void removeActionListener( ActionListener l ) {
		listenerList.remove( ActionListener.class, l );
	}

	/**
	 * Returns an array of all the <code>ActionListener</code>s added
	 * to this JComboBox with addActionListener().
	 *
	 * @return all of the <code>ActionListener</code>s added or an empty
	 *         array if no listeners have been added
	 * @since 1.4
	 */
	public ActionListener[] getActionListeners() {
		return listenerList.getListeners( ActionListener.class );
	}

	// Flag to ensure that infinite loops do not occur with ActionEvents.
	private boolean firingActionEvent = false;

	/**
	 * Notifies all listeners that have registered interest for
	 * notification on this event type.
	 *  
	 * @see EventListenerList
	 */
	protected void fireActionEvent() {
		if( !firingActionEvent ) {
			// Set flag to ensure that an infinite loop is not created
			firingActionEvent = true;
			ActionEvent e = null;
			// Guaranteed to return a non-null array
			Object[] listeners = listenerList.getListenerList();
			long mostRecentEventTime = EventQueue.getMostRecentEventTime();
			int modifiers = 0;
			AWTEvent currentEvent = EventQueue.getCurrentEvent();
			if( currentEvent instanceof InputEvent ) {
				modifiers = ((InputEvent) currentEvent).getModifiers();
			} else if( currentEvent instanceof ActionEvent ) {
				modifiers = ((ActionEvent) currentEvent).getModifiers();
			}
			// Process the listeners last to first, notifying
			// those that are interested in this event
			for( int i = listeners.length - 2; i >= 0; i -= 2 ) {
				if( listeners[i] == ActionListener.class ) {
					// Lazily create the event:
					if( e == null )
						e = new ActionEvent( this, ActionEvent.ACTION_PERFORMED,
										getActionCommand(),
										mostRecentEventTime, modifiers );
					((ActionListener) listeners[i + 1]).actionPerformed( e );
				}
			}
			firingActionEvent = false;
		}
	}

	/** 
	 * Returns the action command that is included in the event sent to
	 * action listeners.
	 *
	 * @return  the string containing the "command" that is sent
	 *          to action listeners.
	 */
	public String getActionCommand() {
		return actionCommand;
	}
	protected String actionCommand = "roomSelected";

		
}
