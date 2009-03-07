/*
 * SEAAPAlgorithm.java
 *
 */
package algo.graph.dynamicflow.eat;

import algo.graph.shortestpath.Dijkstra;
import ds.graph.DynamicResidualNetwork;
import ds.graph.Node;
import ds.graph.flow.FlowOverTime;
import java.util.Arrays;
import java.util.LinkedList;
import sandbox.Algorithm;

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
        flowUnitsSent = 0;
        calculateShortestPathLengths();        
        drn = new DynamicResidualNetwork(problem.getNetwork(), problem.getEdgeCapacities(), problem.getNodeCapacities(), problem.getTransitTimes(), problem.getSources(), problem.getSupplies(), problem.getTimeHorizon());
        pathProblem = new EarliestArrivalAugmentingPathProblem(drn, drn.getSuperSource(), problem.getSink(), getNextDistance(0) + 1);        
        pathAlgorithm.setProblem(pathProblem);
        //System.out.println(drn);
        //System.out.println(drn.capacities());
        //System.out.println(drn.transitTimes());
        //System.out.println("A");
        calculateEarliestArrivalAugmentingPath();
        //System.out.println("B");
        paths = new LinkedList<EarliestArrivalAugmentingPath>();
        while (!path.isEmpty() && path.getCapacity() > 0) {
            //System.out.println(path);
            flowUnitsSent += path.getCapacity();
            fireProgressEvent(flowUnitsSent * 1.0 / problem.getTotalSupplies());
            paths.add(path);
            drn.augmentPath(path);
            calculateEarliestArrivalAugmentingPath();
        }
        if (autoConvert) {
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
