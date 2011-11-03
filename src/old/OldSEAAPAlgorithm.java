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

package old;

import algo.graph.dynamicflow.eat.EarliestArrivalAugmentingPathAlgorithm;
import algo.graph.dynamicflow.eat.EarliestArrivalAugmentingPathProblem;
import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
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
public class OldSEAAPAlgorithm extends Algorithm<EarliestArrivalFlowProblem, FlowOverTime> {

    private int arrivalTime;
    private int[] distances;
    private int flowUnitsSent;
    private EarliestArrivalAugmentingPath path;
    private EarliestArrivalAugmentingPathAlgorithm pathAlgorithm;
    private EarliestArrivalAugmentingPathProblem pathProblem;

    private boolean autoConvert = true;
    
    public OldSEAAPAlgorithm() {
        pathAlgorithm = new EarliestArrivalAugmentingPathAlgorithm();
    }

    public OldSEAAPAlgorithm(boolean b) {
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
        flowUnitsSent = 0;
        calculateShortestPathLengths();        
        drn = new DynamicResidualNetwork(problem.getNetwork(), problem.getEdgeCapacities(), problem.getNodeCapacities(), problem.getTransitTimes(), problem.getSources(), problem.getSupplies(), problem.getTimeHorizon());
        pathProblem = new EarliestArrivalAugmentingPathProblem(drn, drn.getSuperSource(), problem.getSink(), getNextDistance(0) + 1);        
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
					// save to x-stream
//					PrintWriter output;
//			try {
//				output = new PrintWriter( new File( "debug_dynamic_residual_network.txt" ) );
//				XStream xml_convert = new XStream();
//				xml_convert.toXML( drn, output );
//				output = new PrintWriter( new File( "debug_paths.txt"));
//				xml_convert.toXML( paths, output );
//			} catch( FileNotFoundException ex ) {
//				System.err.println( "XSTREAM-out did not work." );
//			}
					fireEvent( new AlgorithmStatusEvent( this, "INIT_PATH_DECOMPOSITION" ) );
					FlowOverTime flow		= new FlowOverTime(drn, paths);
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

