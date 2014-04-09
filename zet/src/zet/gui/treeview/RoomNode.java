/**
 * RoomNode.java
 * Created: 21.01.2011, 11:07:14
 */
package zet.gui.treeview;

import de.tu_berlin.coga.zet.model.Room;
import de.tu_berlin.coga.zet.model.ZLocalization;

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
		sb.append( "Koordinaten: " + zFormatData.getCoordinateString() + "\n" );
		sb.append( "Fläche: " + ZLocalization.getSingleton().getFloatConverter().format( zFormatData.areaMeter() ) + "m²\n" );
		return sb.toString();
	}

	@Override
	public String toString() {
		return zFormatData.getName();
	}



}
