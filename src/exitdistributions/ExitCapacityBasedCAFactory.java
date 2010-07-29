/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exitdistributions;

import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphRasterContainer;
import ds.ca.CellularAutomaton;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.z.BuildingPlan;
import ds.z.ConcreteAssignment;
import evacuationplan.BidirectionalNodeCellMapping;
import evacuationplan.BidirectionalNodeCellMapping.CAPartOfMapping;

/**
 *
 * @author Joscha Kulbatzki
 */
public class ExitCapacityBasedCAFactory extends ZToCAConverter {

	private static ExitCapacityBasedCAFactory instance = null;
	private static GraphBasedExitToCapacityMapping graphBasedExitToCapacityMapping = null;

	protected ExitCapacityBasedCAFactory() {
	}

	public CellularAutomaton convertAndApplyConcreteAssignment( BuildingPlan buildingPlan, NetworkFlowModel model, ConcreteAssignment concreteAssignment, ZToGraphRasterContainer graphRaster ) throws de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter.ConversionNotSupportedException {
//		CellularAutomaton ca = super.convert( buildingPlan );
//		CAPartOfMapping caPartOfMapping = this.getLatestCAPartOfNodeCellMapping();
//		applyConcreteAssignment( concreteAssignment );
//		BidirectionalNodeCellMapping nodeCellMapping = new BidirectionalNodeCellMapping( graphRaster, caPartOfMapping );
//		graphBasedExitToCapacityMapping = new GraphBasedExitToCapacityMapping( ca, nodeCellMapping, model );
//		graphBasedExitToCapacityMapping.calculate();
//		ca.setExitToCapacityMapping( graphBasedExitToCapacityMapping.getExitCapacity() );
//		return ca;
		return new CellularAutomaton();
	}

	public static ExitCapacityBasedCAFactory getInstance() {
		if( instance == null )
			instance = new ExitCapacityBasedCAFactory();
		return instance;
	}
}
