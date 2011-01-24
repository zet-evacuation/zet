/**
 * AreaNode.java
 * Created: 21.01.2011, 11:21:00
 */
package zet.gui.treeview;

import ds.z.Area;
import ds.z.ZLocalization;

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
		sb.append( "Fläche: " + ZLocalization.getSingleton().getFloatConverter().format( zFormatData.areaMeter() ) + "m²\n" );
		return sb.toString();
	}

	@Override
	public String toString() {
		return zFormatData.getAreaType().getTypeString();
	}
}
