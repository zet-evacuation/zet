/* zet evacuation tool copyright © 2007-20 zet evacuation team
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

import algo.graph.exitassignment.ExitCapacityEstimator;
import de.zet_evakuierung.network.model.NetworkFlowModel;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.TargetCell;
import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.graph.Node;
import evacuationplan.BidirectionalNodeCellMapping;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import static java.util.stream.Collectors.toList;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Exit;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.potential.Potential;

/**
 *
 */
public class GraphBasedExitToCapacityMapping {

    private HashMap<Exit, Double> exitToCapacityMapping;
    private boolean isInitialized;
    private NetworkFlowModel model;
    BidirectionalNodeCellMapping nodeCellMapping;
    EvacuationCellularAutomaton ca;

    public GraphBasedExitToCapacityMapping(EvacuationCellularAutomaton ca, BidirectionalNodeCellMapping nodeCellMapping, NetworkFlowModel model) {
        exitToCapacityMapping = new HashMap<>();
        isInitialized = false;
        this.model = model;
        this.nodeCellMapping = nodeCellMapping;
        this.ca = ca;
    }

    public HashMap<Exit, Double> getExitCapacity() {
        if (!isInitialized) {
            initialize();
        }
        return exitToCapacityMapping;
    }

    public void calculate() {
        initialize();
    }

    private void initialize() {
        calculateExitToCapacityMapping();
        isInitialized = true;
    }

    private void calculateExitToCapacityMapping() {

        IdentifiableCollection<Node> sinks = model.graph().predecessorNodes(model.getSupersink());
        ArrayList<Potential> potentials = new ArrayList<>();
        potentials.addAll(ca.getExits().stream().map(exit -> ca.getPotentialFor(exit)).collect(toList()));
        ExitCapacityEstimator estimator = new ExitCapacityEstimator();
        for (Node sink : sinks) {
            ArrayList<EvacCellInterface> sinkCells = nodeCellMapping.getCells(sink);
            TargetCell cell = findATargetCell(sinkCells);
            for (Exit e : ca.getExits()) {
                if (cell instanceof ExitCell && e.getExitCluster().contains((ExitCell) cell)) {
                    double value = estimator.estimateCapacityByMaximumFlow(model, sink);
                    double previousValue = 0;
                    if (exitToCapacityMapping.get(e) != null) {
                        previousValue = exitToCapacityMapping.get(e);
                    }
                    Double newValue = value + previousValue;
                    exitToCapacityMapping.put(e, newValue);
                }
            }
        }
    }

    private TargetCell findATargetCell(Iterable<EvacCellInterface> cellList) {
        TargetCell targetCell = null;
        boolean targetCellFound = false;
        Iterator<EvacCellInterface> it = cellList.iterator();
        while (!targetCellFound && it.hasNext()) {
            EvacCellInterface possibleTargetCell = it.next();
            if (possibleTargetCell instanceof TargetCell) {
                targetCellFound = true;
                targetCell = (TargetCell) possibleTargetCell;
            }
        }

        if (targetCellFound) {
            return (TargetCell) targetCell;
        } else {
            return null;
        }
    }
}
