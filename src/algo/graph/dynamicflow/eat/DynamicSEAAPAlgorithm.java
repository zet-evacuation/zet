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
 * SEAAPAlgorithm.java
 *
 */
package algo.graph.dynamicflow.eat;

import ds.graph.flow.EarliestArrivalAugmentingPath;
import algo.graph.shortestpath.Dijkstra;
import ds.graph.ImplicitTimeExpandedResidualNetwork;
import ds.graph.Node;
import ds.graph.flow.FlowOverTime;
import java.util.Arrays;
import java.util.LinkedList;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;

/**
 *
 * @author Martin Groß
 */
public class DynamicSEAAPAlgorithm extends Algorithm<EarliestArrivalFlowProblem, FlowOverTime> {

    private int arrivalTime;
    private int[] distances;
    private int flowUnitsSent;
    private EarliestArrivalAugmentingPath path;
    private EarliestArrivalAugmentingPathAlgorithm pathAlgorithm;
    private EarliestArrivalAugmentingPathProblem pathProblem;

    public DynamicSEAAPAlgorithm() {
        pathAlgorithm = new EarliestArrivalAugmentingPathAlgorithm();
    }
    
    @Override
    protected FlowOverTime runAlgorithm(EarliestArrivalFlowProblem problem) {
        flowUnitsSent = 0;
        calculateShortestPathLengths();        
        ImplicitTimeExpandedResidualNetwork drn = new ImplicitTimeExpandedResidualNetwork(problem);
        pathProblem = new EarliestArrivalAugmentingPathProblem(drn, drn.superSource(), problem.getSink(), getNextDistance(0) + 1);        
        pathAlgorithm.setProblem(pathProblem);
        calculateEarliestArrivalAugmentingPath();
        LinkedList<EarliestArrivalAugmentingPath> paths = new LinkedList<EarliestArrivalAugmentingPath>();
        while (!path.isEmpty() && path.getCapacity() > 0) {
            flowUnitsSent += path.getCapacity();
            fireProgressEvent(flowUnitsSent * 1.0 / problem.getTotalSupplies());
            paths.add(path);
            drn.augmentPath(path);
            calculateEarliestArrivalAugmentingPath();
        }
        FlowOverTime flow = new FlowOverTime(drn, paths);
        return flow;
    }
    
    private void calculateEarliestArrivalAugmentingPath() {
        if (flowUnitsSent == getProblem().getTotalSupplies()) {
            path = new EarliestArrivalAugmentingPath();
            return;
        }
        boolean pathFound = false;
        while (!pathFound) {
            pathAlgorithm.run();
            path = pathAlgorithm.getSolution();
            if (path.isEmpty() || path.getCapacity() == 0) {
                pathProblem.setTimeHorizon(getNextDistance(arrivalTime) + 1);
            } else {
                pathFound = true;
            }
        }
        if (!path.isEmpty() && path.getCapacity() > 0) {
            arrivalTime = path.getArrivalTime();
        }
        if (path.getArrivalTime() == pathProblem.getTimeHorizon() - 1) {
            pathProblem.setTimeHorizon(pathProblem.getTimeHorizon() + 1);
        }
    }
    
    private void calculateShortestPathLengths() {
        distances = new int[getProblem().getSources().size()];
        int index = 0;
        for (Node source : getProblem().getSources()) {
            Dijkstra dijkstra = new Dijkstra(getProblem().getNetwork(), getProblem().getTransitTimes(), source);
            dijkstra.run();
            distances[index++] = dijkstra.getDistance(getProblem().getSink());
        }
        Arrays.sort(distances);
    }
    
    public int getCurrentArrivalTime() {
        return arrivalTime;
    }
    
    private int getNextDistance(int currentDistance) {
        int index = Arrays.binarySearch(distances, currentDistance + 1);
        if (index >= 0) {
            return currentDistance + 1;
        } else {
            return distances[-index - 1]; 
        }
    }
}
