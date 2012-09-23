/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zet.gui.main.menu;

import de.tu_berlin.math.coga.components.framework.Menu;
import ds.PropertyContainer;
import ds.z.Edge;
import ds.z.RoomEdge;
import ds.z.TeleportEdge;
import ds.z.template.Door;
import ds.z.template.Templates;
import gui.GUIControl;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import zet.gui.GUILocalization;
import zet.gui.main.menu.popup.EdgePopupListener;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EdgePopup extends JPopupMenu {
		/** The localization class. */
	private GUILocalization loc = GUILocalization.getSingleton();;
	/** All JPolygons share the same pop-up menu listeners, which are stored here. */
	private List<EdgePopupListener> edgePopupListeners;
	private GUIControl guiControl;

	public EdgePopup( GUIControl control) {
		super();
		this.edgePopupListeners = new LinkedList<>();
		this.guiControl = control;
	}

	/**
	 * This method is called internally to recreate an up-to-date JPopupMenu
	 * for the JEdge objects. It also recreates the EdgePopupListeners. 
	 *
	 * This whole method is superfluous until now, because the JEdges' PopupMenu
	 * does not include any dynamic elements till now. To keep up the consistency 
	 * with the JPolygon PopupMenu we nevertheless created this method.
	 */
	public void recreate() {
		edgePopupListeners.clear();
		removeAll();
		edgePopupListeners.add( new EdgePopupListener( guiControl, guiControl.getZControl() ) );

		loc.setPrefix( "gui.editor.JEditorPanel." );
		Menu.addMenuItem( this, loc.getString( "popupInsertNewPoint" ), edgePopupListeners.get( 0 ), "insertPoint" );
		Menu.addMenuItem( this, loc.getString( "popupCreatePassage" ), edgePopupListeners.get( 0 ), "makePassable" );
		Menu.addMenuItem( this, loc.getString( "popupCreatePassageRoom" ), edgePopupListeners.get( 0 ), "createPassageRoom" );
		Menu.addMenuItem( this, loc.getString( "popupCreateFloorPassage" ), edgePopupListeners.get( 0 ), "makeTeleport" );
		Menu.addMenuItem( this, loc.getString( "popupCreateEvacuationPassage" ), edgePopupListeners.get( 0 ), "makeEvacEdge" );
		Menu.addMenuItem( this, loc.getString( "popupShowPassageTarget" ), edgePopupListeners.get( 0 ), "showPassageTarget" );
		Menu.addMenuItem( this, loc.getString( "popupRevertPassage" ), edgePopupListeners.get( 0 ), "revertPassage" );
		JMenu mCreateDoors = Menu.addMenu( this, "Tür erzeugen" );
		Templates<Door> doors = guiControl.getDoorTemplates();
		int i = 0;
		for( Door d : doors )
			Menu.addMenuItem( mCreateDoors, d.getName() + " (" + d.getSize() + ")", edgePopupListeners.get( 0 ), "createDoor" + i++ );

		loc.setPrefix( "" );
	}
	/** This method should be called every time before the JEdge popup menu
	 * is shown.
	 * @param currentEdge The Edge that is displayed by the JEdge
	 * on which the PopupMenu shall be shown. 
	 * @param mousePosition the position at which the popup menu is shown with
	 * coordinates that must be relative to the whole Floor
	 */
	public void setPopupEdge( Edge currentEdge, Point mousePosition ) {
		boolean passable = (currentEdge instanceof RoomEdge) && ((RoomEdge)currentEdge).isPassable();
		// passage-Creation
		((JMenuItem)this.getComponent( 1 )).setVisible( !passable );
		// passage-room creation
		((JMenuItem)this.getComponent( 2 )).setVisible( !passable );
		// Teleportation-Creation
		((JMenuItem)this.getComponent( 3 )).setVisible( !passable );
		// EvacuationEdge-Creation
		((JMenuItem)this.getComponent( 4 )).setVisible( !passable );
		// Show Partner edge
		((JMenuItem)this.getComponent( 5 )).setVisible( currentEdge instanceof TeleportEdge );
		// revert passage
		((JMenuItem)this.getComponent( 6 )).setVisible( passable );
		// create door 
		((JMenuItem)this.getComponent( 6 )).setVisible( !passable );

		for( EdgePopupListener p : edgePopupListeners )
			p.setEdge( currentEdge, mousePosition, PropertyContainer.getInstance().getAsBoolean( "editor.options.view.rasterizedPaintMode" ) );
	}

}
