/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package exitdistributions;

import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphRasterContainer;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import de.zet_evakuierung.model.BuildingPlan;
import de.zet_evakuierung.model.ConcreteAssignment;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;

/**
 *
 * @author Joscha Kulbatzki
 */
public class ExitCapacityBasedCAFactory extends ZToCAConverter {

	private static ExitCapacityBasedCAFactory instance = null;
	private static GraphBasedExitToCapacityMapping graphBasedExitToCapacityMapping = null;

	protected ExitCapacityBasedCAFactory() {
	}

	public EvacuationCellularAutomaton convertAndApplyConcreteAssignment( BuildingPlan buildingPlan, NetworkFlowModel model, ConcreteAssignment concreteAssignment, ZToGraphRasterContainer graphRaster ) throws de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter.ConversionNotSupportedException {
//		EvacuationCellularAutomaton ca = super.convert( buildingPlan );
//		CAPartOfMapping caPartOfMapping = this.getLatestCAPartOfNodeCellMapping();
//		applyConcreteAssignment( concreteAssignment );
//		BidirectionalNodeCellMapping nodeCellMapping = new BidirectionalNodeCellMapping( graphRaster, caPartOfMapping );
//		graphBasedExitToCapacityMapping = new GraphBasedExitToCapacityMapping( ca, nodeCellMapping, model );
//		graphBasedExitToCapacityMapping.calculate();
//		ca.setExitToCapacityMapping( graphBasedExitToCapacityMapping.getExitCapacity() );
//		return ca;
		return new MultiFloorEvacuationCellularAutomaton();
	}

	public static ExitCapacityBasedCAFactory getInstance() {
		if( instance == null )
			instance = new ExitCapacityBasedCAFactory();
		return instance;
	}
}
