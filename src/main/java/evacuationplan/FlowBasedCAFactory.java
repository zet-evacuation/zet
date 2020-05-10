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
package evacuationplan;

import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphRasterContainer;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import de.zet_evakuierung.model.BuildingPlan;
import de.zet_evakuierung.model.ConcreteAssignment;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;

public class FlowBasedCAFactory extends ZToCAConverter {

    private static FlowBasedCAFactory instance = null;
    private static CAPathPassabilityChecker lastChecker = null;

    protected FlowBasedCAFactory() {

    }

    /**
     * Creates a new cellular automaton from a building plan, a transshipment and a concrete assignment. Within the
     * cellular automaton, each individual gets its own potential that is conform to one of the dynamic path flows in
     * the transshipment.
     *
     * @param buildingPlan The building in Z-Format.
     * @param transshipment A transshipment calculated for the graph in the same building with the same concrete
     * assignment.
     * @param concreteAssignment All individuals and their positions.
     * @return A cellular automation realizing evacuation plans according to the transshipment.
     * @throws ConversionNotSupportedException
     */
    public EvacuationCellularAutomaton convertAndApplyConcreteAssignment(BuildingPlan buildingPlan, PathBasedFlowOverTime transshipment, ConcreteAssignment concreteAssignment, ZToGraphRasterContainer graphRaster) throws ConversionNotSupportedException {
//		//CellularAutomaton ca = super.convert(buildingPlan);
//		CAPartOfMapping caPartOfMapping = this.getLatestCAPartOfNodeCellMapping();
//		//applyConcreteAssignment(concreteAssignment);
//		BidirectionalNodeCellMapping nodeCellMapping = new BidirectionalNodeCellMapping(graphRaster, caPartOfMapping);
//		CAPathPassabilityChecker checker = new CAPathPassabilityChecker(ca, nodeCellMapping, transshipment);
//		lastChecker = checker;
//		//List<Individual> individualList = ca.getIndividuals();
//		for (Individual ind : individualList){
//			EvacPotential ep = checker.getPotential(ind);
//			//ca.getPotentialManager().addStaticPotential(ep);
//			ind.setStaticPotential(ep);
//		}
        //return ca;
        return new MultiFloorEvacuationCellularAutomaton();
    }

    public static FlowBasedCAFactory getFlowBasedCAFactoryInstance() {
        if (instance == null) {
            instance = new FlowBasedCAFactory();
        }
        return instance;
    }

    /**
     * Returns the {@code CAPathPassabilityChecker} object created during the last call of
     * {@code convertAndApplyConcreteAssignment}.
     *
     * @return the {@code CAPathPassabilityChecker} object created during the last call of
     * {@code convertAndApplyConcreteAssignment}.
     */
    public CAPathPassabilityChecker getLatestCheckerInstance() {
        return lastChecker;
    }

}
