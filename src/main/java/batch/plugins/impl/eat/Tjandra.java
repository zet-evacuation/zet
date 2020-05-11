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
import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import org.zetool.netflow.dynamic.earliestarrival.SEAAPAlgorithm;
import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class Tjandra implements AlgorithmPlugin<EarliestArrivalFlowProblem, PathBasedFlowOverTime> {

  @Override
  public String getName() {
    return "Tjandra";
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
  public AbstractAlgorithm<EarliestArrivalFlowProblem, PathBasedFlowOverTime> getAlgorithm() {

    AbstractAlgorithm<EarliestArrivalFlowProblem, PathBasedFlowOverTime> algo = new AbstractAlgorithm<EarliestArrivalFlowProblem, PathBasedFlowOverTime>() {
      @Override
      protected PathBasedFlowOverTime runAlgorithm( EarliestArrivalFlowProblem problem ) {
        System.err.println( "Running the plugin for Tjandras SEAAP-Algorithm with an estimated time horizon of "
                + problem.getTimeHorizon() );
        SEAAPAlgorithm algo = new SEAAPAlgorithm();
        algo.setProblem( problem );
        algo.run();

        PathBasedFlowOverTime df = algo.getSolution().getPathBased();
        String result = String.format( "Sent %1$s of %2$s flow units in %3$s time units successfully.",
                algo.getSolution().getFlowAmount(), problem.getTotalSupplies(), algo.getSolution().getTimeHorizon() );
        System.out.println( result );
        System.out.println( "Total cost: " + algo.getSolution().getTotalCost() );
        //AlgorithmTask.getInstance().publish(100, result, "");
        System.out.println( "Sending the flow units required " + algo.getRuntime() );
        return df;
      }
    };
    return algo;
  }

  @Override
  public String toString() {
    return getName();
  }
}
