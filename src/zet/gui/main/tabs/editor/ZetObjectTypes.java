/**
 * ZetObjectTypes.java
 * Created: Nov 1, 2012, 6:15:44 PM
 */
package zet.gui.main.tabs.editor;

import ds.z.AssignmentArea;
import ds.z.Barrier;
import ds.z.DelayArea;
import ds.z.EvacuationArea;
import ds.z.InaccessibleArea;
import ds.z.Room;
import ds.z.SaveArea;
import ds.z.StairArea;
import ds.z.TeleportArea;
import gui.GUIOptionManager;
import java.awt.Color;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public enum ZetObjectTypes {
	Room( Room.class, GUIOptionManager.getRoomEdgeColor() ),
	Barrier( Barrier.class, GUIOptionManager.getRoomEdgeColor() ),
	Inaccessible( InaccessibleArea.class, GUIOptionManager.getInaccessibleAreaColor() ),
	Assignment( AssignmentArea.class, GUIOptionManager.getAssignmentAreaColor() ),
	Delay( DelayArea.class, GUIOptionManager.getDelayAreaColor() ),
	Stair( StairArea.class, GUIOptionManager.getStairAreaColor() ),
	Save( SaveArea.class, GUIOptionManager.getSaveAreaColor() ),
	Evacuation( EvacuationArea.class, GUIOptionManager.getEvacuationAreaColor() ),
	Teleport( TeleportArea.class, GUIOptionManager.getTeleportAreaColor() );

	private Class<?>objectClass;
	private Color editColor;

	private ZetObjectTypes( Class<?> c, Color color ) {
		this.objectClass = c;
		this.editColor = color;
	}

	public Color getEditorColor() {
		return editColor;
	}

	public boolean isArea() {
		return this == Room ? false : true;
	}

	public Class<?> getObjectClass() {
		return objectClass;
	}


}
