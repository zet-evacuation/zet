/**
 * ZetObjectTypes.java
 * Created: Nov 1, 2012, 6:15:44 PM
 */
package zet.gui.main.tabs.editor;

import de.zet_evakuierung.model.AssignmentArea;
import de.zet_evakuierung.model.Barrier;
import de.zet_evakuierung.model.DelayArea;
import de.zet_evakuierung.model.EvacuationArea;
import de.zet_evakuierung.model.InaccessibleArea;
import de.zet_evakuierung.model.Room;
import de.zet_evakuierung.model.SaveArea;
import de.zet_evakuierung.model.StairArea;
import de.zet_evakuierung.model.TeleportArea;
import gui.GUIOptionManager;
import java.awt.Color;
import java.util.Objects;


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
		this.objectClass = Objects.requireNonNull( c );
		this.editColor = Objects.requireNonNull( color );
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
