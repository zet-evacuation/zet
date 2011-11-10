/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
 * SuccessiveEarliestArrivalAugmentingPathAlgorithm.java
 *
 */
package algo.graph.dynamicflow.eat;

import ds.graph.flow.EarliestArrivalAugmentingPath;
import ds.graph.ImplicitTimeExpandedResidualNetwork;
import ds.graph.Node;
import ds.graph.flow.FlowOverTime;
import java.util.LinkedList;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;

/**
 *
 * @author Martin Groß
 */
public class SuccessiveEarliestArrivalAugmentingPathAlgorithm extends Algorithm<EarliestArrivalFlowProblem, FlowOverTime> {

    @Override
    protected FlowOverTime runAlgorithm(EarliestArrivalFlowProblem problem) {
        ImplicitTimeExpandedResidualNetwork drn = new ImplicitTimeExpandedResidualNetwork(problem);
        EarliestArrivalAugmentingPathProblem pathProblem = new EarliestArrivalAugmentingPathProblem(drn, drn.superSource(), problem.getSink(), problem.getTimeHorizon());
        EarliestArrivalAugmentingPathAlgorithm pathAlgorithm = new EarliestArrivalAugmentingPathAlgorithm();
        pathAlgorithm.setProblem(pathProblem);
        pathAlgorithm.run();
        EarliestArrivalAugmentingPath path = pathAlgorithm.getSolution();
        int flowUnitsSent = 0;
        int flowUnitsTotal = 0;
        for (Node source : problem.getSources()) {
            flowUnitsTotal += problem.getSupplies().get(source);
        }
        LinkedList<EarliestArrivalAugmentingPath> paths = new LinkedList<EarliestArrivalAugmentingPath>();
        while (!path.isEmpty() && path.getCapacity() > 0) {
            flowUnitsSent += path.getCapacity();
            fireProgressEvent(flowUnitsSent * 1.0 / flowUnitsTotal, String.format("%1$s von %2$s Personen evakuiert.", flowUnitsSent, flowUnitsTotal));
            paths.add(path);
            drn.augmentPath(path);
            pathAlgorithm = new EarliestArrivalAugmentingPathAlgorithm();
            pathAlgorithm.setProblem(pathProblem);
            pathAlgorithm.run();
            path = pathAlgorithm.getSolution();
        }
        FlowOverTime flow = new FlowOverTime(drn, paths);
        return flow;
    }
}
