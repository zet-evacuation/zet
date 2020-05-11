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
package batch.plugins.impl.eat;

import batch.plugins.AlgorithmPlugin;
import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import org.zetool.netflow.dynamic.eatapprox.EarliestArrivalApproximationAlgorithm;
import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.zetool.common.algorithm.Algorithm;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class Framework implements AlgorithmPlugin<EarliestArrivalFlowProblem, PathBasedFlowOverTime> {

    @Override
    public String getName() {
        return "2-Approx-Based";
    }

    @Override
    public Class<EarliestArrivalFlowProblem> accepts() {
        return EarliestArrivalFlowProblem.class;
    }

    @Override
    public Class<PathBasedFlowOverTime> generates() {
        return PathBasedFlowOverTime.class;
    }

    @Override
    public Algorithm<EarliestArrivalFlowProblem, PathBasedFlowOverTime> getAlgorithm() {
        Algorithm<EarliestArrivalFlowProblem, PathBasedFlowOverTime> algo = new AbstractAlgorithm<EarliestArrivalFlowProblem, PathBasedFlowOverTime>() {

            @Override
            protected PathBasedFlowOverTime runAlgorithm(EarliestArrivalFlowProblem problem) {
                EarliestArrivalApproximationAlgorithm algo = new EarliestArrivalApproximationAlgorithm();
                //problem.setTimeHorizon( 18 ); // 64: zeitpunkt 42 ist falsch, // 1676 for max flow
                algo.setProblem(problem);
                algo.run();

                System.out.println("Arrival pattern: ");
                System.out.println(algo.getSolution());
                int total = 0;
                for (int i = 1; i < algo.getSolution().getTimeHorizon(); ++i) {
                    total += (algo.getSolution().getValue(i) - algo.getSolution().getValue(i - 1)) * i;
                }
                System.out.println("Total cost: " + total);

                System.out.println("Runtime: " + algo.getRuntimeAsString());
                return null;
            }
        };
        return algo;
    }

    @Override
    public String toString() {
        return getName();
    }
}
