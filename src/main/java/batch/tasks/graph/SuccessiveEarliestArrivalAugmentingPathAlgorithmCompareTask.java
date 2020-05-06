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
package batch.tasks.graph;

import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import org.zetool.netflow.dynamic.earliestarrival.old.SEAAPAlgoWithTH;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import ds.NetworkFlowModelAlgorithm;
import org.zetool.netflow.ds.flow.FlowOverTimeImplicit;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;

/**
 *
 * @author Marlen Schwengfelder
 */
public class SuccessiveEarliestArrivalAugmentingPathAlgorithmCompareTask extends NetworkFlowModelAlgorithm {

    int globalTime;
    int[] TimeFlowPair;

    public SuccessiveEarliestArrivalAugmentingPathAlgorithmCompareTask(int Timehorizon) {
        //setAlgorithm(new SEAAPAlgoWithTH());
        globalTime = Timehorizon;
        //saves the flow values for different time horizons
        TimeFlowPair = new int[globalTime + 1];
    }

    @Override
    public PathBasedFlowOverTime runAlgorithm(NetworkFlowModel model) {

        System.out.println("Time: " + this.globalTime);
        TimeFlowPair[0] = 0;
        for (int t = 1; t < this.globalTime + 1; t++) {
            SEAAPAlgoWithTH seaap = new SEAAPAlgoWithTH();
            EarliestArrivalFlowProblem problem = model.getEAFP(t);
            seaap.setProblem(problem);
            seaap.run();
            FlowOverTimeImplicit flow = seaap.getSolution();
			//System.out.println("Calculation done for t= " + t);
            //System.out.println("Flow Value: " + flow.getFlowAmount());         
            //System.out.println("Total Supply: " + problem.getTotalSupplies());
            //System.out.println("Time Needed: " + flow.getTimeHorizon());
            TimeFlowPair[t] = flow.getFlowAmount();
			//System.out.println("Zeit: : " + t + "Flow: " +  TimeFlowPair[t]);
            //System.out.println("___________________________________________");
        }

        return new PathBasedFlowOverTime();
    }

    public int[] getTimeFlowPair() {
        return this.TimeFlowPair;
    }
}
