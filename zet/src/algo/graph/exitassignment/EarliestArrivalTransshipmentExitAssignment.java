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
 * EarliestArrivalTransshipmentExitAssignment.java
 *
 */

package algo.graph.exitassignment;

import de.tu_berlin.coga.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import de.tu_berlin.coga.netflow.dynamic.LongestShortestPathTimeHorizonEstimator;
import de.tu_berlin.coga.netflow.dynamic.earliestarrival.SEAAPAlgorithm;
import de.tu_berlin.coga.netflow.dynamic.TimeHorizonBounds;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.coga.graph.Node;
import de.tu_berlin.coga.netflow.ds.flow.FlowOverTimeImplicit;
import de.tu_berlin.coga.netflow.ds.structure.FlowOverTimePath;
import de.tu_berlin.coga.netflow.ds.flow.PathBasedFlowOverTime;

/**
 *
 * @author Martin Groß
 */
public class EarliestArrivalTransshipmentExitAssignment extends Algorithm<NetworkFlowModel, ExitAssignment> implements Assignable {

    @Override
    protected ExitAssignment runAlgorithm(NetworkFlowModel model) {
        ExitAssignment solution = new ExitAssignment(model.graph().nodes());

        EarliestArrivalFlowProblem problem;// = new EarliestArrivalFlowProblem(model.getEdgeCapacities(), model.getNetwork(), model.getNodeCapacities(), model.getSupersink(), model.getSources(), 0, model.getTransitTimes(), model.getCurrentAssignment());
				problem = model.getEAFP();
        Algorithm<EarliestArrivalFlowProblem, TimeHorizonBounds> estimator = new LongestShortestPathTimeHorizonEstimator();
        estimator.setProblem(problem);
        estimator.run();

        //problem = new EarliestArrivalFlowProblem(model.getEdgeCapacities(), model.getNetwork(), model.getNodeCapacities(), model.getSupersink(), model.getSources(), estimator.getSolution().getUpperBound(), model.getTransitTimes(), model.getCurrentAssignment());
				problem = model.getEAFP(estimator.getSolution().getUpperBound());
        Algorithm<EarliestArrivalFlowProblem, FlowOverTimeImplicit> algorithm = new SEAAPAlgorithm();
        algorithm.setProblem(problem);
        algorithm.run();

        PathBasedFlowOverTime paths = algorithm.getSolution().getPathBased();
        for (FlowOverTimePath path : paths) {
            Node start = path.firstEdge().start();
            Node exit = path.lastEdge().start();
            for (int i = 0; i < path.getRate(); i++) {
                solution.assignIndividualToExit(start, exit);
            }
        }
        return solution;
    }

	/**
	 * Returns the calculated exit assignment.
	 * @return the calculated exit assignment.
	 */
	@Override
	public ExitAssignment getExitAssignment() {
		return getSolution();
	}
}
