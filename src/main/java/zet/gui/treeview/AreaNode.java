
package zet.gui.treeview;

import org.zetool.common.localization.LocalizationManager;
import de.zet_evakuierung.model.Area;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class AreaNode extends ProjectTreeNode<Area> {

	public AreaNode( Area zFormatData ) {
		super( zFormatData );
	}

	@Override
	public String getInformationText() {
		StringBuilder sb = new StringBuilder();
		sb.append( zFormatData.getAreaType().getTypeString() ).append( "\n");
		sb.append( zFormatData.getPolygon().getCoordinateString() ).append( "\n");
		sb.append( "Koordinaten: " ).append( zFormatData.getPolygon().getCoordinateString() ).append( "\n");
		sb.append( "Fläche: " ).append( LocalizationManager.getManager().getFloatConverter().format( zFormatData.areaMeter() ) ).append( "m²\n");
		return sb.toString();
	}

	@Override
	public String toString() {
		return zFormatData.getAreaType().getTypeString();
	}
}
