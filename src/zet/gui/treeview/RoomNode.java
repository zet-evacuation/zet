
package zet.gui.treeview;

import org.zetool.common.localization.LocalizationManager;
import de.zet_evakuierung.model.Room;
import de.zet_evakuierung.model.ZLocalization;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class RoomNode extends ProjectTreeNode<Room> {

	public RoomNode( Room zFormatData ) {
		super( zFormatData );
	}

	@Override
	public String getInformationText() {
		StringBuilder sb = new StringBuilder();
		sb.append( "Raum: " + zFormatData.getName() + "\n" );
		sb.append( "Koordinaten: " + zFormatData.getPolygon().getCoordinateString() + "\n" );
		sb.append( "Fläche: " + LocalizationManager.getManager().getFloatConverter().format( zFormatData.getPolygon().areaMeter() ) + "m²\n" );
		return sb.toString();
	}

	@Override
	public String toString() {
		return zFormatData.getName();
	}



}
