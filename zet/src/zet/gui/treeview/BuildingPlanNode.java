/**
 * BuildingPlanNode.java
 * Created: 21.01.2011, 11:07:03
 */
package zet.gui.treeview;

import de.zet_evakuierung.model.BuildingPlan;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BuildingPlanNode extends ProjectTreeNode<BuildingPlan> {

	public BuildingPlanNode( BuildingPlan zFormatData ) {
		super( zFormatData );
	}

	@Override
	public String getInformationText() {
		return "Raumplan";
	}

	@Override
	public String toString() {
		return "Raumplan";
	}





}
