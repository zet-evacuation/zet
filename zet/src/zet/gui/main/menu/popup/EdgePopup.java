
package zet.gui.main.menu.popup;

import org.zetool.common.localization.Localization;
import de.tu_berlin.math.coga.components.framework.Menu;
import ds.PropertyContainer;
import de.zet_evakuierung.model.PlanEdge;
import de.zet_evakuierung.model.RoomEdge;
import de.zet_evakuierung.model.TeleportEdge;
import de.zet_evakuierung.template.Door;
import de.zet_evakuierung.template.ExitDoor;
import de.zet_evakuierung.template.Templates;
import gui.GUIControl;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import zet.gui.GUILocalization;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EdgePopup extends JPopupMenu {
		/** The localization class. */
	private Localization loc = GUILocalization.loc;
	/** All JPolygons share the same pop-up menu listeners, which are stored here. */
	private List<EdgePopupListener> edgePopupListeners = new LinkedList<>();
	private GUIControl guiControl;

	public EdgePopup( GUIControl control) {
		super();
		this.guiControl = control;
	}

	/**
	 * This method is called internally to recreate an up-to-date JPopupMenu
	 * for the JEdge objects. It also recreates the EdgePopupListeners. 
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
		
		JMenu mCreateExitDoors = Menu.addMenu( this, "Ausgang erzeugen" );
		Templates<ExitDoor> exitDoors = guiControl.getExitDoorTemplates();
		i = 0;
		for( ExitDoor d : exitDoors )
			Menu.addMenuItem( mCreateExitDoors, d.getName() + " (" + d.getSize() + ")", edgePopupListeners.get( 0 ), "createExitDoor" + i++ );
	
		loc.setPrefix( "" );
	}
	/** This method should be called every time before the JEdge popup menu
	 * is shown.
	 * @param currentEdge The Edge that is displayed by the JEdge
	 * on which the PopupMenu shall be shown. 
	 * @param mousePosition the position at which the popup menu is shown with
	 * coordinates that must be relative to the whole Floor
	 */
	public void setPopupEdge( PlanEdge currentEdge, Point mousePosition ) {
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
		((JMenuItem)this.getComponent( 7 )).setVisible( !passable );
		// create exit door 
		((JMenuItem)this.getComponent( 7 )).setVisible( !passable );

		for( EdgePopupListener p : edgePopupListeners )
			p.setEdge( currentEdge, mousePosition, PropertyContainer.getInstance().getAsBoolean( "editor.options.view.rasterizedPaintMode" ) );
	}

}
