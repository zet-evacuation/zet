
package zet.gui.main.menu.popup;

import org.zetool.common.localization.Localization;
import de.tu_berlin.math.coga.components.framework.Menu;
import de.zet_evakuierung.model.PlanEdge;
import de.zet_evakuierung.model.PlanPoint;
import gui.GUIControl;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPopupMenu;
import zet.gui.GUILocalization;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PointPopup extends JPopupMenu {
	/** The localization class. */
	private Localization loc = GUILocalization.loc;
	/** All JPolygons share the same pop-up menu listeners, which are stored here. */
	private List<PointPopupListener> pointPopupListeners = new LinkedList<>();
	private GUIControl guiControl;

	public PointPopup( GUIControl control) {
		super();
		this.guiControl = control;
	}
	
	/**
	 * This method is called internally to recreate an up-to-date JPopupMenu for
	 * the JEdge (Point) objects. It also recreates the JEdgePopupListeners.
	 */
	public void recreate() {
		pointPopupListeners.clear();
		removeAll();
		pointPopupListeners.add( new PointPopupListener( guiControl.getZControl() ) );
		Menu.addMenuItem( this, loc.getString( "gui.editor.JEditorPanel.popupDeletePoint" ), pointPopupListeners.get( 0 ), "deletePoint" );
	}

	/**
	 * This method should be called every time before the JEdge point pop-up menu
	 * is shown.
	 *
	 * @param currentEdge The Edge on which the PointPopupMenu shall be shown.
	 * @param currentPoint The PlanPoint on which the PointPopupMenu shall be
	 * shown.
	 */
	public void setPopupPoint( PlanEdge currentEdge, PlanPoint currentPoint ) {
		for( PointPopupListener p : pointPopupListeners )
			p.setPoint( currentEdge, currentPoint );
	}
}
