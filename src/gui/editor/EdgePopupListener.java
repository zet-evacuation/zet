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

/*
 * JPolygonPopupListener.java
 * Created on 28. Dezember 2007, 13:11
 */
package gui.editor;

import ds.z.Edge;
import ds.z.PlanPoint;
import ds.z.Room;
import ds.z.RoomEdge;
import ds.z.TeleportEdge;
import event.EventServer;
import event.MessageEvent;
import gui.JEditor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/** This popup listener is responsible for handling menu events
 *
 * @author Timon Kelter
 */
public class EdgePopupListener implements ActionListener {
	private Edge myEdge;
	private Point mousePosition;
	private boolean rasterizedPaintMode;

	/** This method should be called every time before the popup is shown.
	 * @param currentEdge The Edge on which the popup is shown.
	 * @param mousePosition the position at which the popup menu is shown with
	 * coordinates that must be relative to the whole Floor
	 * @param rasterizedPaintMode Whether we are painting rasterized
	 */
	public void setEdge( Edge currentEdge, Point mousePosition,
											 boolean rasterizedPaintMode ) {
		myEdge = currentEdge;
		this.mousePosition = mousePosition;
		this.rasterizedPaintMode = rasterizedPaintMode;
	}

	/**
	 * This method contains the event code that is executed when certain
	 * action commands (defined at the menu creation) are invoked.
	 * @param e 
	 */
	public void actionPerformed( ActionEvent e ) {
		try {
			if( e.getActionCommand().equals( "makePassable" ) )
				if( myEdge instanceof RoomEdge ) {
					Room myRoom = ((RoomEdge)myEdge).getRoom();
					RoomEdge partner = null;

					for( Room r : myRoom.getAssociatedFloor().getRooms() )
						if( r != myRoom )
							try {
								partner = r.getEdge( (RoomEdge)myEdge );
								break; // Break when successful
							} catch( IllegalArgumentException ex ) {
							}

					if( partner != null ) {
						((RoomEdge)myEdge).setLinkTarget( partner );
						partner.setLinkTarget( (RoomEdge)myEdge );
					} else
						EventServer.getInstance().dispatchEvent( new MessageEvent(
										this, MessageEvent.MessageType.Error,
										"Erzeugen Sie zuerst 2 übereinanderliegende Raumbegrenzungen!" ) );
				} else
					EventServer.getInstance().dispatchEvent( new MessageEvent(
									this, MessageEvent.MessageType.Error,
									"Nur Raumbegrenzungen können passierbar gemacht werden!" ) );
			else if( e.getActionCommand().equals( "insertPoint" ) ) {
				// Compute a point that is ON the edge (the click is not neccessarily)
				PlanPoint newPoint = new PlanPoint(
								CoordinateTools.translateToModel( mousePosition ) );
				newPoint = myEdge.getPointOnEdge( newPoint );
				if( rasterizedPaintMode ) {
					newPoint.x = (int)Math.round( (double)newPoint.x / 400.0d ) * 400;
					newPoint.y = (int)Math.round( (double)newPoint.y / 400.0d ) * 400;
				}

				// Replace the old edge
				ArrayList<PlanPoint> pointList = new ArrayList<PlanPoint>( 3 );
				pointList.add( myEdge.getSource() );
				pointList.add( newPoint );
				pointList.add( myEdge.getTarget() );
				myEdge.getAssociatedPolygon().replaceEdge( myEdge, pointList );
			} else if( e.getActionCommand().equals( "makeTeleport" ) )
				if( myEdge instanceof RoomEdge )
					if( GUIOptionManager.getEditMode() != EditMode.TeleportEdgeCreation ) {
						// Start teleport connection creation
						GUIOptionManager.setEditMode( EditMode.TeleportEdgeCreation );
						GUIOptionManager.getEditMode().getPayload().add( myEdge );

						EventServer.getInstance().dispatchEvent( new MessageEvent( this, MessageEvent.MessageType.Status, "Wählen Sie jetzt die Gegenseite aus (Rechtsklick+Menu)!" ) );
					} else {
						Room.connectToWithTeleportEdge(
										(RoomEdge)EditMode.TeleportEdgeCreation.getPayload().getFirst(),
										(RoomEdge)myEdge );
						GUIOptionManager.setEditMode( GUIOptionManager.getPreviousEditMode() );
					}
				else
					EventServer.getInstance().dispatchEvent( new MessageEvent( this, MessageEvent.MessageType.Error, "Nur Raumbegrenzungen können zu Stockwerksdurchgängen gemacht werden!" ) );
			else if( e.getActionCommand().equals( "makeEvacEdge" ) )
				if( myEdge instanceof RoomEdge )
					JEditor.getInstance().getEditView().getProjectControl().getProject().getBuildingPlan().getDefaultFloor().addEvacuationRoom( (RoomEdge)myEdge );
				else
					EventServer.getInstance().dispatchEvent( new MessageEvent( this, MessageEvent.MessageType.Error, "Nur Raumbegrenzungen können zu Evakuierungsausgängen gemacht werden!" ) );
			else if( e.getActionCommand().equals( "createPassageRoom" ) ) {
				if( GUIOptionManager.getEditMode() == EditMode.TeleportEdgeCreation ) {
					EventServer.getInstance().dispatchEvent( new MessageEvent( this, MessageEvent.MessageType.Status, "Erzeugen sie erst die Teleport-Kante!" ) );
					return;
				}
				if( GUIOptionManager.getEditMode() != EditMode.PassableRoomCreation ) {
					GUIOptionManager.setEditMode( EditMode.PassableRoomCreation );
					GUIOptionManager.getEditMode().getPayload().add( myEdge );
					EventServer.getInstance().dispatchEvent( new MessageEvent( this, MessageEvent.MessageType.Status, "Wählen Sie jetzt die Gegenseite aus (Rechtsklick+Menu)!" ) );
				} else {
					// Create new Room
					PlanPoint e1;
					PlanPoint e2;
					if( Math.abs( myEdge.getSource().getXInt() - myEdge.getTarget().getXInt() ) > Math.abs( myEdge.getSource().getYInt() - myEdge.getTarget().getYInt() ) )
						if( myEdge.getSource().getXInt() < myEdge.getTarget().getXInt() ) {
							e1 = new PlanPoint( myEdge.getSource() );
							e2 = new PlanPoint( myEdge.getTarget() );
						} else {
							e1 = new PlanPoint( myEdge.getTarget() );
							e2 = new PlanPoint( myEdge.getSource() );
						}
					else
						if( myEdge.getSource().getYInt() > myEdge.getTarget().getYInt() ) {
							e1 = new PlanPoint( myEdge.getSource() );
							e2 = new PlanPoint( myEdge.getTarget() );
						} else {
							e1 = new PlanPoint( myEdge.getTarget() );
							e2 = new PlanPoint( myEdge.getSource() );
						}
					PlanPoint f1;
					PlanPoint f2;
					RoomEdge edge = (RoomEdge)EditMode.PassableRoomCreation.getPayload().getFirst();
					if( Math.abs( edge.getSource().getXInt() - edge.getTarget().getXInt() ) > Math.abs( edge.getSource().getYInt() - edge.getTarget().getYInt() ) )
						if( edge.getSource().getXInt() < edge.getTarget().getXInt() ) {
							f1 = new PlanPoint( edge.getSource() );
							f2 = new PlanPoint( edge.getTarget() );
						} else {
							f1 = new PlanPoint( edge.getTarget() );
							f2 = new PlanPoint( edge.getSource() );
						}
					else
						if( edge.getSource().getYInt() > edge.getTarget().getYInt() ) {
							f1 = new PlanPoint( edge.getSource() );
							f2 = new PlanPoint( edge.getTarget() );
						} else {
							f1 = new PlanPoint( edge.getTarget() );
							f2 = new PlanPoint( edge.getSource() );
						}
					// create room
					Room room = new Room( edge.getRoom().getAssociatedFloor() );
					ArrayList<PlanPoint> points = new ArrayList<PlanPoint>( 6 );
					points.add( e1 );
					points.add( f1 );
					points.add( f2 );
					points.add( e2 );
					room.defineByPoints( points );
					//room.close();
					// connect
					room.connectTo( ((RoomEdge)myEdge).getRoom(), e1, e2 );
					room.connectTo( edge.getRoom(), f1, f2 );
					GUIOptionManager.setEditMode( GUIOptionManager.getPreviousEditMode() );
				}

			} else if( e.getActionCommand().equals( "showPassageTarget" ) ) {
				Room partnerRoom = ((TeleportEdge)myEdge).getLinkTarget().getRoom();
				// Show the right floor
				JEditor.getInstance().getEditView().changeFloor(
								partnerRoom.getAssociatedFloor() );
				// Show the right room
				JEditor.getInstance().getEditView().getFloor().showPolygon( partnerRoom );
			} else if( e.getActionCommand().equals( "revertPassage" ) )
				((RoomEdge)myEdge).makeImpassable();
		} catch( RuntimeException ex ) {
			EventServer.getInstance().dispatchEvent( new MessageEvent( this, MessageEvent.MessageType.Error, ex.getLocalizedMessage() ) );
		}
	}
}
