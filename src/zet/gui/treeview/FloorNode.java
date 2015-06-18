
package zet.gui.treeview;

import de.zet_evakuierung.model.AbstractFloor;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FloorNode extends ProjectTreeNode<AbstractFloor> {

	public FloorNode( AbstractFloor zFormatData ) {
		super( zFormatData );
	}

	@Override
	public String getInformationText() {
		StringBuilder sb = new StringBuilder();

		sb.append( "Etage: " + zFormatData.getName() + "\n" );
		return sb.toString();
	}
}
