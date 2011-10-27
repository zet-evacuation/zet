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
import ds.graph.DynamicResidualNetwork;
import ds.graph.Node;
import ds.graph.flow.FlowOverTime;
import java.util.Arrays;
import java.util.LinkedList;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmStatusEvent;

/**
 *
 * @author Martin Groß
 */
public class SEAAPAlgorithm extends Algorithm<EarliestArrivalFlowProblem, FlowOverTime> {

    private int arrivalTime;
    private int[] distances;
    private int flowUnitsSent;
    private EarliestArrivalAugmentingPath path;
    private EarliestArrivalAugmentingPathAlgorithm pathAlgorithm;
    private EarliestArrivalAugmentingPathProblem pathProblem;
    private boolean autoConvert = true;
    private int OriginTime;
    
    public SEAAPAlgorithm() {
        pathAlgorithm = new EarliestArrivalAugmentingPathAlgorithm();
    }

    public SEAAPAlgorithm(boolean b) {
        pathAlgorithm = new EarliestArrivalAugmentingPathAlgorithm();
        autoConvert = b;
    }

    @Override
    protected FlowOverTime runAlgorithm(EarliestArrivalFlowProblem problem) {
        if (problem.getTotalSupplies() == 0) {
            drn = new DynamicResidualNetwork(problem.getNetwork(), problem.getEdgeCapacities(), problem.getNodeCapacities(), problem.getTransitTimes(), problem.getSources(), problem.getSupplies(), problem.getTimeHorizon());
            paths = new LinkedList<EarliestArrivalAugmentingPath>();
            return new FlowOverTime(drn, paths);
        } 
        OriginTime = problem.getTimeHorizon();
        flowUnitsSent = 0;        
        calculateShortestPathLengths();
        drn = new DynamicResidualNetwork(problem.getNetwork(), problem.getEdgeCapacities(), problem.getNodeCapacities(), problem.getTransitTimes(), problem.getSources(), problem.getSupplies(), problem.getTimeHorizon()); 
        if (getNextDistance(0)+1 > OriginTime)
        {
            pathProblem = new EarliestArrivalAugmentingPathProblem(drn, drn.getSuperSource(), problem.getSink(), OriginTime);
        }
        else
        {    
            pathProblem = new EarliestArrivalAugmentingPathProblem(drn, drn.getSuperSource(), problem.getSink(), getNextDistance(0) + 1);
        }
        pathAlgorithm.setProblem(pathProblem);
        calculateEarliestArrivalAugmentingPath();
        paths = new LinkedList<EarliestArrivalAugmentingPath>();
        while (!path.isEmpty() && path.getCapacity() > 0) {
            flowUnitsSent += path.getCapacity();
            fireProgressEvent(flowUnitsSent * 1.0 / problem.getTotalSupplies(), String.format("%1$s von %2$s Personen evakuiert.", flowUnitsSent, problem.getTotalSupplies()));
            paths.add(path);
            drn.augmentPath(path);
            calculateEarliestArrivalAugmentingPath();
        }
        if (autoConvert) {
            fireEvent(new AlgorithmStatusEvent(this, "INIT_PATH_DECOMPOSITION"));
            FlowOverTime flow = new FlowOverTime(drn, paths);
            return flow;
            
        } else {
            return null;
        }
    }
    public LinkedList<EarliestArrivalAugmentingPath> paths;
    public DynamicResidualNetwork drn;

    private void calculateEarliestArrivalAugmentingPath() {
        if (flowUnitsSent == getProblem().getTotalSupplies()) {
            path = new EarliestArrivalAugmentingPath();
            return;
        }
        boolean pathFound = false;
        while (!pathFound) {
            pathAlgorithm.run();
            path = pathAlgorithm.getSolution();
            //System.out.println("Path: " + path);
            //System.out.println("arrival Time: " + path.getArrivalTime());
            //System.out.println("Capacity: " + path.getCapacity());
            if (path.isEmpty() || path.getCapacity() == 0) {
                if (getNextDistance(arrivalTime) + 1 > OriginTime)
                {
                   path = new EarliestArrivalAugmentingPath();
                   return; 
                }
                else
                {
                    pathProblem.setTimeHorizon(getNextDistance(arrivalTime) + 1);
                }
            } else {
                pathFound = true;
            }
        }
        if (!path.isEmpty() && path.getCapacity() > 0) {
             arrivalTime = path.getArrivalTime();
             //System.out.println("2");
        }
        if (path.getArrivalTime() == pathProblem.getTimeHorizon() - 1) {           
            pathProblem.setTimeHorizon(pathProblem.getTimeHorizon() + 1);
             //System.out.println("3");
            if (pathProblem.getTimeHorizon() > OriginTime)
            {
                path = new EarliestArrivalAugmentingPath();
                 //System.out.println("4");
                return;
            }
           
        }
    }

    private void calculateShortestPathLengths() {
        distances = new int[getProblem().getSources().size()];
        int index = 0;
        Dijkstra dijkstra = new Dijkstra(getProblem().getNetwork(), getProblem().getTransitTimes(), getProblem().getSink(), true);
        dijkstra.run();
        for (Node source : getProblem().getSources()) {
            distances[index++] = dijkstra.getDistance(source);
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
