/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package gui.editor;

import gui.components.AbstractPolygon;
import gui.components.AbstractFloor;
import ds.z.Area;
import ds.z.Barrier;
import ds.z.Edge;
import ds.z.EvacuationArea;
import ds.z.PlanPolygon;
import ds.z.Room;
import ds.z.TeleportEdge;
import ds.z.PlanPoint;
import ds.z.RoomEdge;
import ds.z.StairArea;
import gui.JEditor;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.EnumSet;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Graphical representation of a {@link ds.z.PlanPolygon}. This class has the
 * special feature of forwarding mouse events to it's parent component before
 * dealing with them itself. This is different to the java standard behaviour,
 * where only the topmost component that has been clicked on is notified of the
 * event.
 * @author Timon Kelter
 */
public class JPolygon extends AbstractPolygon {
	protected static Point lastPosition = new Point();
	protected static boolean selectedUsed = false;
	/** The amount of space (in pixels) that is added on each side of all edges'
	 * bounding boxes, to enable them to paint themselves thicker when they are
	 * marked as selected. */
	public static final int EDGE_WIDTH_ADDITION = 2;
	/** The width of the edge when selected, in pixels. The unequality 
	 * 1 + 2 * EDGE_WIDTH_ADDITION >= EDGE_PAINT_WIDTH must hold. */
	public static final float EDGE_PAINT_WIDTH = 2.2f;
	/** The radius of the nodes on screen. This should be less than or equal to
	 * EDGE_WIDTH_ADDITION + 1 */
	public static final int NODE_PAINT_RADIUS = 3;
	/** The radius around the nodes of the edge in which a click on the edge is 
	 * also counted as a click on the node. This should be less than or equal to
	 * EDGE_WIDTH_ADDITION + 1 */
	public static final int NODE_SELECTION_RADIUS = 3;
	private final static float dash1[] = {10.0f};
	private final static BasicStroke stroke_standard = new BasicStroke( 1.0f,
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER );
	private final static BasicStroke stroke_dashed_slim = new BasicStroke( 1.0f,
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f );
	private final static BasicStroke stroke_dashed_thick = new BasicStroke( EDGE_PAINT_WIDTH,
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f );
	private final static BasicStroke stroke_thick = new BasicStroke( EDGE_PAINT_WIDTH );

	//############## EDGE RELATED FIELDS ###################
	private class EdgeData {
		/** The ds.z.Edges that eauch graphical edge representation is connected to. */
		public Edge myEdge;
		/** The location of PlanPoint1 of any edge in the <u>coordinate space of the JPolygon.</u> */
		public Point node1;
		/** The location of PlanPoint2 of any edge in the <u>coordinate space of the JPolygon.</u> */
		public Point node2;
		/** Indicates the direction in which each edge is drawn. See the internal 
		 * comments in the {@link #paintComponent(Graphics)} method for further details. */
		public boolean startDrawingAtNode1;
		/** The selectionPolygons of the edges. */
		private Polygon selectionPolygon;
	}
	/** A list of Edge Data that stores the Edge information in the order that the edges are 
	 * iterated through by the PlanPolygon iterator. */
	private LinkedList<EdgeData> edgeData = new LinkedList<EdgeData>();
	//############## POLYGON RELATED FIELDS ###################
	private Color transparentForeground;
	/** A helper variable that is used to prevent an event from being handled a second time.
	 *  This is made possible through the event pass-back code from 
	 * {@link JFloor#processMouseEvent(MouseEvent)} */
	private MouseEvent lastMouseEventToPassToFloor = null;
	private boolean selected = false;

	/**
	 * Creates a new instance of <code>JPolygon</code>.
	 * @param myFloor The {@link JFloor} on which this polygon is displayed
	 * @param foreground the border color of the polygon
	 */
	public JPolygon( AbstractFloor myFloor, Color foreground ) {
		super( foreground );

		this.myFloor = myFloor;
		setOpaque( false );

		// Create a transparent color with which we will fill out the polygon
		// in case it represents an area
		transparentForeground = new Color( getForeground().getColorSpace(),
						getForeground().getColorComponents( null ), 0.1f );

		enableEvents( AWTEvent.MOUSE_EVENT_MASK );
		enableEvents( AWTEvent.MOUSE_MOTION_EVENT_MASK );
	}

	/**
	 * This method returns the ds.z object at the given point. In contrast to 
	 * findComponentAt it not just checks whether the point is inside the
	 * bounding box of the Component, but also whether the point is inside
	 * the shape of the <code>Component</code>
	 * (only works for {@link JEdge} / {@link JPolygon}). 
	 * @param p 
	 * @return The topmost plan component that was clicked on, e.g. a PlanPoint,
	 * a Edge/RoomEdge or a PlanPolygon. If no such component can be found null is returned.
	 */
	public Object findClickTargetAt( Point p ) {
		// Check sub-objects
		for( Component c : getComponents() ) {
			Point transPoint = SwingUtilities.convertPoint( this, p, c );

			if( c.contains( transPoint ) )
				//if( c instanceof JPolygon ) {		
				if( c instanceof JPolygon ) {
					if( ((JPolygon)c).getDrawingPolygon().contains( transPoint ) )
						return ((JPolygon)c).findClickTargetAt( transPoint );
				} else
					return null;
		}
		// Check edges
		for( EdgeData ed : edgeData )
			if( ed.selectionPolygon.contains( p ) ) {
				PlanPoint point_hit = clickHitsPlanPoint( ed, p );
				if( point_hit != null )
					return point_hit;
				else
					return ed.myEdge;
			}
		// Check if click is in own drawing area (most general option)
		return (drawingPolygon.contains( p )) ? this.myPolygon : null;
	}

	/**
	 * Only call this after the component has been added to a container. Otherwise
	 * operations like setBounds, which are called herein, will fail.
	 * @param p the polygon
	 */
	public void displayPolygon( PlanPolygon p ) {
		if( myPolygon != null ) {
			removeAll();
			edgeData.clear();
		}

		myPolygon = p;

		if( p != null ) {
			// Display myself

			// Contains absolute bounds
			Rectangle areabounds = CoordinateTools.translateToScreen( p.bounds() );
			// Translate start point only for non-area JPolygons - see below
			areabounds.width += 2 * EDGE_WIDTH_ADDITION;
			areabounds.height += 2 * EDGE_WIDTH_ADDITION;

			// Contains room-relative bounds
			Rectangle bounds = new Rectangle( areabounds );
			if( myPolygon instanceof Area ) {
				Rectangle translatedBounds = CoordinateTools.translateToScreen(
								((Area)myPolygon).getAssociatedRoom().bounds() );
				bounds.x -= translatedBounds.x;
				bounds.y -= translatedBounds.y;
			} else {
				bounds.x -= EDGE_WIDTH_ADDITION;
				bounds.y -= EDGE_WIDTH_ADDITION;
			}
			setBounds( bounds );

			// This already copies the polygon
			drawingPolygon = CoordinateTools.translateToScreen(
							myPolygon.getAWTPolygon() );
			// We only want to paint the space within the area polygon. The 
			// drawingpolygon already contains the appropriate size to do that,
			// because it comes from the model and has no idea of EDGE_WIDTH_ADDITION
			// But we have to shift the polygon (1) into the coordinate space of the
			// area and (2) by EDGE_WIDTH_ADDITION because we want to paint inside
			// the painted edges, not only inside the JEdge Objects.
			drawingPolygon.translate( -areabounds.x + EDGE_WIDTH_ADDITION,
							-areabounds.y + EDGE_WIDTH_ADDITION );

			// Display subobjects
			// TODO: Provide better implementation - Do not recreate everything each time

			if( Room.class.isInstance( myPolygon ) ) {
				Room room = Room.class.cast( myPolygon );

				// Display areas
				EnumSet<AreaVisibility> areaVisibility = GUIOptionManager.getAreaVisibility();
				if( areaVisibility.contains( AreaVisibility.ASSIGNMENT ) )
					for( Area a : room.getAssignmentAreas() ) {
						JPolygon ne = new JPolygon( myFloor, GUIOptionManager.getAssignmentAreaColor() );
						add( ne );
						ne.displayPolygon( a );
					}
				if( areaVisibility.contains( AreaVisibility.DELAY ) )
					for( Area a : room.getDelayAreas() ) {
						JPolygon ne = new JPolygon( myFloor, GUIOptionManager.getDelayAreaColor() );
						add( ne );
						ne.displayPolygon( a );
					}
				if( areaVisibility.contains( AreaVisibility.EVACUATION ) )
					for( Area a : room.getSaveAreas() )
						if( a instanceof EvacuationArea ) {
							JPolygon ne = new JPolygon( myFloor, GUIOptionManager.getEvacuationAreaColor() );
							add( ne );
							ne.displayPolygon( a );
						}
				if( areaVisibility.contains( AreaVisibility.SAVE ) )
					for( Area a : room.getSaveAreas() )
						if( !(a instanceof EvacuationArea) ) {
							JPolygon ne = new JPolygon( myFloor, GUIOptionManager.getSaveAreaColor() );
							add( ne );
							ne.displayPolygon( a );
						}
				if( areaVisibility.contains( AreaVisibility.STAIR ) )
					for( Area a : room.getStairAreas() ) {
						JPolygon ne = new JPolygon( myFloor, GUIOptionManager.getStairAreaColor() );
						add( ne );
						ne.displayPolygon( a );
					}
				if( areaVisibility.contains( AreaVisibility.INACCESSIBLE ) ) {
					for( Area a : room.getInaccessibleAreas() ) {
						JPolygon ne = new JPolygon( myFloor, GUIOptionManager.getInaccessibleAreaColor() );
						add( ne );
						ne.displayPolygon( a );
					}
					for( Area a : room.getBarriers() ) {
						JPolygon ne = new JPolygon( myFloor, GUIOptionManager.getRoomEdgeColor() );
						add( ne );
						ne.displayPolygon( a );
					}
				}
			}

			// Display own edges - This must come after the areas have been created,
			// otherwise the room edges will dominate the area edges.
			for( Edge e : myPolygon.getEdges() ) {
				EdgeData ed = new EdgeData();
				ed.myEdge = e;

				Point p1 = CoordinateTools.translateToScreen( e.getSource() );
				Point p2 = CoordinateTools.translateToScreen( e.getTarget() );

				// Compute coordinates of the PlanPoints within! the JEdge's coordinate space
				ed.node1 = new Point(
								(int)p1.getX() - areabounds.x + EDGE_WIDTH_ADDITION,
								(int)p1.getY() - areabounds.y + EDGE_WIDTH_ADDITION );
				ed.node2 = new Point(
								(int)p2.getX() - areabounds.x + EDGE_WIDTH_ADDITION,
								(int)p2.getY() - areabounds.y + EDGE_WIDTH_ADDITION );

				// Create the selection shape
				Point pLeft = (ed.node1.x <= ed.node2.x) ? ed.node1 : ed.node2;
				Point pRight = (ed.node1.x <= ed.node2.x) ? ed.node2 : ed.node1;
				Point pTop = (ed.node1.y <= ed.node2.y) ? ed.node1 : ed.node2;
				Point pBottom = (ed.node1.y <= ed.node2.y) ? ed.node2 : ed.node1;

				ed.selectionPolygon = new Polygon();
				if( pLeft == pTop ) {
					// Edge from left top to right bottom
					ed.selectionPolygon.addPoint( pLeft.x - EDGE_WIDTH_ADDITION,
									pLeft.y - EDGE_WIDTH_ADDITION );
					ed.selectionPolygon.addPoint( pLeft.x + EDGE_WIDTH_ADDITION,
									pLeft.y - EDGE_WIDTH_ADDITION );
					ed.selectionPolygon.addPoint( pRight.x + EDGE_WIDTH_ADDITION,
									pRight.y - EDGE_WIDTH_ADDITION );
					ed.selectionPolygon.addPoint( pRight.x + EDGE_WIDTH_ADDITION,
									pRight.y + EDGE_WIDTH_ADDITION );
					ed.selectionPolygon.addPoint( pRight.x - EDGE_WIDTH_ADDITION,
									pRight.y + EDGE_WIDTH_ADDITION );
					ed.selectionPolygon.addPoint( pLeft.x - EDGE_WIDTH_ADDITION,
									pLeft.y + EDGE_WIDTH_ADDITION );
				} else {
					// Edge from left bottom to right top
					ed.selectionPolygon.addPoint( pLeft.x - EDGE_WIDTH_ADDITION,
									pLeft.y - EDGE_WIDTH_ADDITION );
					ed.selectionPolygon.addPoint( pRight.x - EDGE_WIDTH_ADDITION,
									pRight.y - EDGE_WIDTH_ADDITION );
					ed.selectionPolygon.addPoint( pRight.x + EDGE_WIDTH_ADDITION,
									pRight.y - EDGE_WIDTH_ADDITION );
					ed.selectionPolygon.addPoint( pRight.x + EDGE_WIDTH_ADDITION,
									pRight.y + EDGE_WIDTH_ADDITION );
					ed.selectionPolygon.addPoint( pLeft.x + EDGE_WIDTH_ADDITION,
									pLeft.y + EDGE_WIDTH_ADDITION );
					ed.selectionPolygon.addPoint( pLeft.x - EDGE_WIDTH_ADDITION,
									pLeft.y + EDGE_WIDTH_ADDITION );
				}

				// Always start at the leftmost or, if that is not applicable, at 
				// the topmost node. Either the topmost or the leftmost node must 
				// exist, because both nodes may not have the same coodinates.
				if( ed.node1.x != ed.node2.x )
					ed.startDrawingAtNode1 = ed.node1.x < ed.node2.x;
				else
					ed.startDrawingAtNode1 = ed.node1.y < ed.node2.y;

				// Add the new EdgeData to our list of EdgeData
				edgeData.add( ed );
			}
		}

	// Don't repaint here - Always repaint the whole Floor. This is
	// necessary because otherwise the background will not be cleaned
	//repaint ();
	}

	@Override
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );

		// Guard - May be able to prevent some bizarre exception to occur
		// (when the number of edgeDatas is not equal to the edge count due
		// to some unknown reason. This exception is hard to reproduce)
		if( myPolygon == null || myPolygon.getNumberOfEdges() != edgeData.size() ) {
			SwingUtilities.invokeLater( new Thread() {
				@Override
				public void run() {
					try {
						sleep( 300 );
					} catch( InterruptedException ex ) {
					}
					repaint();
				}
			} );
			return;
		}

		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

		// Option flags for Stairs
		boolean lowerPart = false;
		boolean upperPart = false;

		// ### Paint the Edges ### 
		Iterator<EdgeData> itEdgeData = edgeData.iterator();
		for( Edge myEdge : myPolygon ) {
			assert ( itEdgeData.hasNext() );
			EdgeData ed = itEdgeData.next();

			// Set various paint options
			g2.setPaint( (myEdge instanceof TeleportEdge) ? GUIOptionManager.getTeleportEdgeColor() : getForeground() );
			if( myEdge instanceof RoomEdge && ((RoomEdge)myEdge).isPassable() )
				// Paint dashed line to indicate passability
				if( selected )
					g2.setStroke( stroke_dashed_thick );
				else
					g2.setStroke( stroke_dashed_slim );
			else if( selected )
				g2.setStroke( stroke_thick );
			else
				g2.setStroke( stroke_standard );

			// Set stair options
			if( myPolygon instanceof StairArea ) {
				StairArea sme = (StairArea)myPolygon;
				if( myEdge.getSource() == sme.getLowerLevelStart() )
					lowerPart = true;
				if( myEdge.getSource() == sme.getLowerLevelEnd() )
					lowerPart = false;
				if( myEdge.getSource() == sme.getUpperLevelStart() )
					upperPart = true;
				if( myEdge.getSource() == sme.getUpperLevelEnd() )
					upperPart = false;

				String toDraw = null;
				if( lowerPart )
					toDraw = "L";
				else if( upperPart )
					toDraw = "U";

				if( toDraw != null ) {
					FontMetrics metrics = g2.getFontMetrics();
					Rectangle nameBounds = metrics.getStringBounds( toDraw, g2 ).getBounds();
					int edge_center_x = (ed.node1.x + ed.node2.x) / 2;
					int edge_center_y = (ed.node1.y + ed.node2.y) / 2;
					Paint oldPaint = g2.getPaint();
					g2.setPaint( GUIOptionManager.getRoomEdgeColor() );
					g2.drawString( toDraw, edge_center_x - nameBounds.width / 2,
									edge_center_y + nameBounds.height / 2 );
					g2.setPaint( oldPaint );
				}
			}

			// Drawing coordinates for nodes are node1 / node 2
			g2.fillRect( ed.node1.x - NODE_PAINT_RADIUS, ed.node1.y - NODE_PAINT_RADIUS, 2 * NODE_PAINT_RADIUS, 2 * NODE_PAINT_RADIUS );
			g2.fillRect( ed.node2.x - NODE_PAINT_RADIUS, ed.node2.y - NODE_PAINT_RADIUS, 2 * NODE_PAINT_RADIUS, 2 * NODE_PAINT_RADIUS );

			// Consider the case, that there is a passable edge whose target
			// has node1 and node2 in the reversed order (this is absolutely
			// legal since we are using undirected edges). In this case we have
			// to make sure that the edge is always drawn in the same direction
			// because otherwise the line segments of the dashed lines will
			// overlap and form a solid line. Therefore we introduced the 
			// field startAtNode1
			if( ed.startDrawingAtNode1 )
				g2.drawLine( ed.node1.x, ed.node1.y, ed.node2.x, ed.node2.y );
			else
				g2.drawLine( ed.node2.x, ed.node2.y, ed.node1.x, ed.node1.y );
		}

		// ### Paint Polygon-specific stuff like the room name or the area filling ###
		if( Room.class.isInstance( myPolygon ) ) {
			// Paint the name of the room
			Font originalFont = g2.getFont();
			g2.setFont( GUIOptionManager.getRoomNameFont() );
			drawName( Room.class.cast( myPolygon ).getName(), g2, GUIOptionManager.getRoomEdgeColor() );
			g2.setFont( originalFont );
		}

		// If the polygons are of the type area, fill them. This won't work for
		// barriers as they only represent lines.
		if( (myPolygon instanceof Area) && !(myPolygon instanceof Barrier) ) {
			// Paint the background with the area color
			g2.setPaint( transparentForeground );
			//if( drawingPolygon != null )
			if( drawingPolygon.npoints > 0 )
				g2.fillPolygon( drawingPolygon );
		}

		// Redraw the polygon if it is selected. This will give better 
		// if many polygons are visible at the same time.
		if( isSelected() && !(myPolygon instanceof Barrier) ) {
			g2.setPaint( transparentForeground );
			g2.fillPolygon( drawingPolygon );
		}

	}

	/**
	 * Indicates whether the <code>JPolygon</code> is selected or not.
	 * @return true if the polygon is selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Determines if the <code>JPolygon</code> is selected. Other polygons that
	 * my be selected are not touched, thus it is possible to select more than
	 * one.
	 * @param selected the selection state, true if it is selected
	 */
	public void setSelected( boolean selected ) {
		this.selected = selected;
		repaint();
	}

	/**
	 * MouseEvents occuring on this component are forwarded to the parent 
	 * component, if the click does not trigger the PopupMenu of the component. 
	 * @param e the <code>MouseEvent</code>
	 */
	@Override
	protected void processMouseEvent( MouseEvent e ) {
		// Check the events in the following order (small objects before big objects):
		// 1. Check whether a Point Popup is triggered
		// 2. Check whether an Edge Popup is triggered
		// 3. Check whether a Polygon Popup is triggered
		// 4. Else forward the event to parent object

		// Do not use e.isPopupTrigger() here - Won't work under linux
		if( e.getID() == MouseEvent.MOUSE_RELEASED && e.getButton() == MouseEvent.BUTTON3 ) {
			if( !lastPosition.equals( e.getLocationOnScreen() ) ) {
				lastPosition = e.getLocationOnScreen();
				selectedUsed = false;
			}

			EdgeData hitEdge = null;
			// First check whether at least one edge was hit by the click
			for( EdgeData ed : edgeData )
				if( ed.selectionPolygon.contains( e.getPoint() ) ) {
					hitEdge = ed;
					break;
				}

			if( hitEdge != null ) {
				PlanPoint hitPoint = clickHitsPlanPoint( hitEdge, e.getPoint() );
				if( hitPoint == null ) {
					// Show edge popup
					if( selectedUsed == false ) {
						JEditor.getInstance().getEditView().setPopupEdge( hitEdge.myEdge,
									JEditor.getInstance().getEditView().convertPointToFloorCoordinates(
									(Component)e.getSource(), e.getPoint() ) );
						JEditor.getInstance().getEditView().getEdgePopup().show( this, e.getX(), e.getY() );
					}
					selectedUsed = isSelected();
				} else {
					// Show point popup
					if( selectedUsed == false ) {
						JEditor.getInstance().getEditView().setPopupPoint( hitEdge.myEdge, hitPoint );
						JEditor.getInstance().getEditView().getPointPopup().show( this, e.getX(), e.getY() );
					}
					selectedUsed = isSelected();
				}
			} else if( Room.class.isInstance( myPolygon ) && drawingPolygon.contains( e.getPoint() ) ) {
				if( selectedUsed == false ) {
					JEditor.getInstance().getEditView().setPopupPolygon( myPolygon );
					JEditor.getInstance().getEditView().getPolygonPopup().show( this, e.getX(), e.getY() );
				}
					selectedUsed = isSelected();
			}
		}

		if( !e.equals( lastMouseEventToPassToFloor ) && getParent() != null ) {
			// Keep the polygon as the source and translate the coordinates
			// Do not use SwingUtilities.convertMouseEvent here! - Timon
			Point translated = SwingUtilities.convertPoint( this, e.getPoint(), getParent() );
			getParent().dispatchEvent( new MouseEvent( (Component)e.getSource(), e.getID(),
							e.getWhen(), e.getModifiers(), translated.x, translated.y, e.getClickCount(),
							e.isPopupTrigger() ) );

			lastMouseEventToPassToFloor = e;
		}
	}

	/** MouseEvents occuring on this component are also forwarded to the parent component. */
	@Override
	protected void processMouseMotionEvent( MouseEvent e ) {
		if( getParent() != null ) {
			// Keep the polygon as the source and translate the coordinates
			// Do not use SwingUtilities.convertMouseEvent here! - Timon
			Point translated = SwingUtilities.convertPoint( this, e.getPoint(), getParent() );
			getParent().dispatchEvent( new MouseEvent( (Component)e.getSource(), e.getID(),
							e.getWhen(), e.getModifiers(), translated.x, translated.y, e.getClickCount(),
							e.isPopupTrigger() ) );
		}
	}

	/**
	 * Determines whether the given <code>MouseEvent</code> will lead to a popup
	 * menu when dispatched to this JPolygon.
	 * @param e the mouse event that occured
	 * @return <code>true</code> if the popup is displayed, <code>false</code> otherwise
	 */
	public boolean isPopupTrigger( MouseEvent e ) {
		// Do not use e.isPopupTrigger() here - Won't work under linux
		if( e.getID() == MouseEvent.MOUSE_RELEASED && e.getButton() == MouseEvent.BUTTON3 ) {
			EdgeData hitEdge = null;
			// First check whether at least one edge was hit by the click
			for( EdgeData ed : edgeData )
				if( ed.selectionPolygon.contains( e.getPoint() ) ) {
					hitEdge = ed;
					break;
				}

			return (hitEdge != null) || (Room.class.isInstance( myPolygon ) && drawingPolygon.contains( e.getPoint() ));
		} else
			return false;
	}

	/**
	 * This is a helper method, to determine whether the user clicked on a
	 * planPoint (the nodes of an edge) and not only into the middle of an
	 * edge.
	 * @param ed The edge on which the user clicked
	 * @param click The click coordinates <u>in the coordinate space of the JPolygon</u>
	 * @return The {@link ds.z.PlanPoint} that the user clicked on, if such a point exists, <code>null</code> if no point was hit.
	 */
	private PlanPoint clickHitsPlanPoint( EdgeData ed, Point click ) {
		if( ed.node1.distance( click ) <= NODE_SELECTION_RADIUS )
			return ed.myEdge.getSource();
		else if( ed.node2.distance( click ) <= NODE_SELECTION_RADIUS )
			return ed.myEdge.getTarget();
		else
			return null;
	}
}