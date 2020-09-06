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
package batch.tasks.graph;

import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import org.zetool.netflow.dynamic.LongestShortestPathTimeHorizonEstimator;
import org.zetool.netflow.dynamic.earliestarrival.SuccessiveEarliestArrivalAugmentingPathAlgorithm;
import de.zet_evakuierung.network.model.NetworkFlowModel;
import ds.NetworkFlowModelAlgorithm;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;

/**
 *
 */
public class SuccessiveEarliestArrivalAugmentingPathAlgorithm2Task extends NetworkFlowModelAlgorithm {

    @Override
    protected PathBasedFlowOverTime runAlgorithm(NetworkFlowModel model) {
        EarliestArrivalFlowProblem problem = model.getEAFP();
        LongestShortestPathTimeHorizonEstimator estimator = new LongestShortestPathTimeHorizonEstimator();
        estimator.setProblem(problem);
        estimator.run();
        System.out.println(estimator.getSolution());
        problem = model.getEAFP(estimator.getSolution().getUpperBound());
        SuccessiveEarliestArrivalAugmentingPathAlgorithm algo = new SuccessiveEarliestArrivalAugmentingPathAlgorithm();
        algo.setProblem(problem);
        algo.run();
        PathBasedFlowOverTime df = algo.getSolution().getPathBased();
        String result = String.format("Sent %1$s of %2$s flow units in %3$s time units successfully.", algo.getSolution().getFlowAmount(), problem.getTotalSupplies(), algo.getSolution().getTimeHorizon());
        System.out.println(result);
        //AlgorithmTask.getInstance().publish(100, result, "");
        System.out.println(String.format("Sending the flow units required %1$s ms.", algo.getRuntime().getValue() / 1000000));
        return df;
    }
}
