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
package zet.gui.main.menu.popup;

import de.tu_berlin.coga.zet.model.Edge;
import de.tu_berlin.coga.zet.model.PlanPoint;
import de.tu_berlin.coga.zet.model.Room;
import de.tu_berlin.coga.zet.model.RoomEdgeA;
import de.tu_berlin.coga.zet.model.TeleportEdge;
import de.tu_berlin.coga.zet.model.ZControl;
import de.tu_berlin.coga.zet.template.Door;
import de.tu_berlin.coga.zet.template.ExitDoor;
import event.EventServer;
import event.MessageEvent;
import gui.GUIControl;
import gui.GUIOptionManager;
import gui.ZETProperties;
import gui.editor.CoordinateTools;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import zet.gui.main.tabs.editor.EditModeOld;

/**
 * This pop-up listener is responsible for handling menu events
 * @author Timon Kelter
 */
public class EdgePopupListener implements ActionListener {
	private Edge myEdge;
	private Point mousePosition;
	private boolean rasterizedPaintMode;
	private ZControl projectControl;
	private GUIControl guiControl;

	public EdgePopupListener( GUIControl guiControl, ZControl projectControl ) {
		this.guiControl = guiControl;
		this.projectControl = projectControl;
	}

	/** This method should be called every time before the pop-up is shown.
	 * @param currentEdge The Edge on which the pop-up is shown.
	 * @param mousePosition the position at which the pop-up menu is shown with
	 * coordinates that must be relative to the whole Floor
	 * @param rasterizedPaintMode Whether we are painting in raster mode
	 */
	public void setEdge( Edge currentEdge, Point mousePosition, boolean rasterizedPaintMode ) {
		myEdge = currentEdge;
		this.mousePosition = mousePosition;
		this.rasterizedPaintMode = rasterizedPaintMode;
	}

	/**
	 * This method contains the event code that is executed when certain
	 * action commands (defined at the menu creation) are invoked.
	 * @param e
	 */
	@Override
	public void actionPerformed( ActionEvent e ) {
		try {
			if( e.getActionCommand().equals( "makePassable" ) )
				if( myEdge instanceof RoomEdgeA ) {
					Room myRoom = ((RoomEdgeA)myEdge).getRoom();
					RoomEdgeA partner = null;

					for( Room r : myRoom.getAssociatedFloor().getRooms() )
						if( r != myRoom )
							try {
								partner = r.getEdge( (RoomEdgeA)myEdge );
								break; // Break when successful
							} catch( IllegalArgumentException ex ) { }
					if( partner != null ) {
						((RoomEdgeA)myEdge).setLinkTarget( partner );
						partner.setLinkTarget( (RoomEdgeA)myEdge );
					} else
						EventServer.getInstance().dispatchEvent( new MessageEvent( this, MessageEvent.MessageType.Error, "Erzeugen Sie zuerst 2 übereinanderliegende Raumbegrenzungen!" ) );
				} else
					EventServer.getInstance().dispatchEvent( new MessageEvent( this, MessageEvent.MessageType.Error, "Nur Raumbegrenzungen können passierbar gemacht werden!" ) );
			else if( e.getActionCommand().equals( "insertPoint" ) ) {
				// Compute a point that is ON the edge (the click is not neccessarily)

				PlanPoint newPoint = new PlanPoint( CoordinateTools.translateToModel( mousePosition ) );
				newPoint = myEdge.getPointOnEdge( newPoint );
				final double rasterSnap = ZETProperties.getRasterSizeSnap();
				if( rasterizedPaintMode ) {
					newPoint.x = (int)Math.round( (double)newPoint.x / rasterSnap ) * (int)rasterSnap;
					newPoint.y = (int)Math.round( (double)newPoint.y / rasterSnap ) * (int)rasterSnap;
				}

				projectControl.insertPoint( myEdge, newPoint );
			} else if( e.getActionCommand().equals( "makeTeleport" ) )
				if( myEdge instanceof RoomEdgeA )
//					if( GUIOptionManager.getEditMode() != EditModeOld.TeleportEdgeCreation ) {
					if( null != EditModeOld.TeleportEdgeCreation ) {
						// Start teleport connection creation
//						GUIOptionManager.setEditMode( EditModeOld.TeleportEdgeCreation );
//						GUIOptionManager.getEditMode().getPayload().add( myEdge );

						EventServer.getInstance().dispatchEvent( new MessageEvent( this, MessageEvent.MessageType.Status, "Wählen Sie jetzt die Gegenseite aus (Rechtsklick+Menu)!" ) );
					} else {
						Room.connectToWithTeleportEdge( (RoomEdgeA)EditModeOld.TeleportEdgeCreation.getPayload().getFirst(), (RoomEdgeA)myEdge );
						GUIOptionManager.setEditMode( GUIOptionManager.getPreviousEditMode() );
					}
				else
					EventServer.getInstance().dispatchEvent( new MessageEvent( this, MessageEvent.MessageType.Error, "Nur Raumbegrenzungen können zu Stockwerksdurchgängen gemacht werden!" ) );
			else if( e.getActionCommand().equals( "makeEvacEdge" ) )
				if( myEdge instanceof RoomEdgeA )
					guiControl.getZControl().getProject().getBuildingPlan().getDefaultFloor().addEvacuationRoom( (RoomEdgeA)myEdge );
				else
					EventServer.getInstance().dispatchEvent( new MessageEvent( this, MessageEvent.MessageType.Error, "Nur Raumbegrenzungen können zu Evakuierungsausgängen gemacht werden!" ) );
			else if( e.getActionCommand().equals( "createPassageRoom" ) ) {
//				if( GUIOptionManager.getEditMode() == EditModeOld.TeleportEdgeCreation ) {
				if( null == EditModeOld.TeleportEdgeCreation ) {
					EventServer.getInstance().dispatchEvent( new MessageEvent( this, MessageEvent.MessageType.Status, "Erzeugen sie erst die Teleport-Kante!" ) );
					return;
				}
//				if( GUIOptionManager.getEditMode() != EditModeOld.PassableRoomCreation ) {
				if( null != EditModeOld.PassableRoomCreation ) {
//					GUIOptionManager.setEditMode( EditModeOld.PassableRoomCreation );
//					GUIOptionManager.getEditMode().getPayload().add( myEdge );
					EventServer.getInstance().dispatchEvent( new MessageEvent( this, MessageEvent.MessageType.Status, "Wählen Sie jetzt die Gegenseite aus (Rechtsklick+Menu)!" ) );
				} else {
					projectControl.connectRooms( (RoomEdgeA)EditModeOld.PassableRoomCreation.getPayload().getFirst(), (RoomEdgeA)myEdge );
					GUIOptionManager.setEditMode( GUIOptionManager.getPreviousEditMode() );
				}
			} else if( e.getActionCommand().equals( "showPassageTarget" ) ) {
				Room partnerRoom = ((TeleportEdge)myEdge).getLinkTarget().getRoom();
				// Show the right floor
				guiControl.showFloor( partnerRoom.getAssociatedFloor() );
				// Show the right room
				guiControl.showPolygon( partnerRoom );
			} else if( e.getActionCommand().equals( "revertPassage" ) )
				projectControl.disconnectAtEdge( (RoomEdgeA)myEdge);
			else if( e.getActionCommand().startsWith( "createDoor" ) ) {
				if( !(myEdge instanceof RoomEdgeA ) )
					throw new IllegalStateException( "Doors can only be created in rooms!" );
				System.out.println( "Try to create a door" );

				int index = Integer.parseInt( e.getActionCommand().substring( 10 ) );
				System.out.println( "Load index " + index );

				Door d = guiControl.getDoorTemplates().getDoor( index );

				Room myRoom = ((RoomEdgeA)myEdge).getRoom();

				PlanPoint newPoint = new PlanPoint( CoordinateTools.translateToModel( mousePosition ) );
				newPoint = myEdge.getPointOnEdge( newPoint );
				final double rasterSnap = ZETProperties.getRasterSizeSnap();
				if( rasterizedPaintMode ) {
					newPoint.x = (int)Math.round( (double)newPoint.x / rasterSnap ) * (int)rasterSnap;
					newPoint.y = (int)Math.round( (double)newPoint.y / rasterSnap ) * (int)rasterSnap;
				}
				//PlanPoint newPoint = new PlanPoint( CoordinateTools.translateToModel( mousePosition ) );
				projectControl.createDoor( (RoomEdgeA)myEdge, newPoint, d.getSize() );
			} else if( e.getActionCommand().startsWith( "createExitDoor" ) ) {
				if( !(myEdge instanceof RoomEdgeA ) )
					throw new IllegalStateException( "Doors can only be created in rooms!" );
				System.out.println( "Try to create a door" );
				int index = Integer.parseInt( e.getActionCommand().substring( 14 ) );
				System.out.println( "Load index " + index );

				ExitDoor d = guiControl.getExitDoorTemplates().getDoor( index );
				Room myRoom = ((RoomEdgeA)myEdge).getRoom();

				PlanPoint newPoint = new PlanPoint( CoordinateTools.translateToModel( mousePosition ) );
				newPoint = myEdge.getPointOnEdge( newPoint );
				final double rasterSnap = ZETProperties.getRasterSizeSnap();
				if( rasterizedPaintMode ) {
					newPoint.x = (int)Math.round( (double)newPoint.x / rasterSnap ) * (int)rasterSnap;
					newPoint.y = (int)Math.round( (double)newPoint.y / rasterSnap ) * (int)rasterSnap;
				}
				projectControl.createExitDoor( (RoomEdgeA)myEdge, newPoint, d.getSize() );
			}
		} catch( RuntimeException ex ) {
			EventServer.getInstance().dispatchEvent( new MessageEvent( this, MessageEvent.MessageType.Error, ex.getLocalizedMessage() ) );
		}
	}
}
