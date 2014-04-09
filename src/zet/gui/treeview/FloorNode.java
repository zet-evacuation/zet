/**
 * FloorNode.java
 * Created: 21.01.2011, 11:07:08
 */
package zet.gui.treeview;

import de.tu_berlin.coga.zet.model.Floor;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FloorNode extends ProjectTreeNode<Floor> {

	public FloorNode( Floor zFormatData ) {
		super( zFormatData );
	}

	@Override
	public String getInformationText() {
		StringBuilder sb = new StringBuilder();

		sb.append( "Etage: " + zFormatData.getName() + "\n" );
		return sb.toString();
	}



}
