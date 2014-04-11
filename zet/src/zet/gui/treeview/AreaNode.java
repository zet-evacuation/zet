
package zet.gui.treeview;

import de.tu_berlin.coga.common.localization.LocalizationManager;
import de.tu_berlin.coga.zet.model.Area;
import de.tu_berlin.coga.zet.model.ZLocalization;

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
		sb.append( zFormatData.getAreaType().getTypeString() + "\n" );
		sb.append( zFormatData.getCoordinateString() + "\n" );
		sb.append( "Koordinaten: " + zFormatData.getCoordinateString() + "\n" );
		sb.append( "Fläche: " + LocalizationManager.getSingleton().getFloatConverter().format( zFormatData.areaMeter() ) + "m²\n" );
		return sb.toString();
	}

	@Override
	public String toString() {
		return zFormatData.getAreaType().getTypeString();
	}
}
