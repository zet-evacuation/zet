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
 * ChainDecomposition.java
 *
 */
package algo.graph.dynamicflow;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.graph.DynamicResidualNetwork;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Node;
import ds.graph.flow.FlowOverTimeCycle;
import ds.graph.flow.FlowOverTimeEdge;
import ds.graph.flow.FlowOverTimeEdgeSequence;
import ds.graph.flow.FlowOverTimePath;
import ds.graph.flow.PathBasedFlowOverTime;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author Martin Groß
 */
public class NewChainDecomposition extends Algorithm<ChainDecompositionProblem, PathBasedFlowOverTime> {

    private transient DynamicResidualNetwork network;
    private transient IdentifiableIntegerMapping<Edge> transitTimes;
    private transient List<FlowOverTimeEdgeSequence> cycles;
    private transient Deque<FlowOverTimeEdgeSequence> residualPaths;
    private transient IdentifiableObjectMapping<Edge, Queue[]> edgeSequencesUsingEdge;
    private transient IdentifiableObjectMapping<Node, Queue[]> edgeSequencesWaitingAtNode;
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_FINE = false;
    public PathBasedFlowOverTime pathBased;
    IdentifiableObjectMapping<Edge, Queue[]> pathsUsingEdge;
    IdentifiableObjectMapping<Node, Queue[]> pathsUsingNode;
    LinkedList<FlowOverTimeEdgeSequence> paths;

    @Override
    protected PathBasedFlowOverTime runAlgorithm(ChainDecompositionProblem problem) {
        transitTimes = problem.getNetwork().transitTimes();

        arrivalTime = new IdentifiableIntegerMapping<Node>(network.nodes());

        // Remove all cycles from the edge sequences
        cycles = new LinkedList<FlowOverTimeEdgeSequence>();
        for (FlowOverTimeEdgeSequence edgeSequence : problem.getEdgeSequences()) {
            FlowOverTimeEdgeSequence cycle = extractCycle(edgeSequence);
            while (cycle != null) {
                cycles.add(cycle);
                cycle = extractCycle(edgeSequence);
            }
        }
        residualPaths = new LinkedList(problem.getEdgeSequences());

        // List all usages of edges and waiting at nodes for uncrossing purposes
        edgeSequencesUsingEdge = new IdentifiableObjectMapping<Edge, Queue[]>(network.edges(), Queue[].class);
        edgeSequencesWaitingAtNode = new IdentifiableObjectMapping<Node, Queue[]>(network.nodes(), Queue[].class);
        for (FlowOverTimeEdgeSequence path : paths) {
            //addToUsageLists(path);
        }
        for (FlowOverTimeEdgeSequence cycle : cycles) {
            //addToUsageLists(cycle);
        }

        while (!residualPaths.isEmpty()) {
            FlowOverTimeEdgeSequence residualPath = residualPaths.poll();

            // Look for reverse edges
            FlowOverTimeEdge reverseEdge = null;
            boolean waiting = false;
            for (FlowOverTimeEdge edge : residualPath) {
                if (network.isReverseEdge(edge.getEdge())) {
                    reverseEdge = edge;
                    break;
                } else if (residualPath.delay(edge) < 0) {
                    reverseEdge = edge;
                    waiting = true;
                    break;
                }
            }

            // If the edge sequence does not contain reverse arcs or nodes with negative waiting times,
            // we are done with this residual path
            if (reverseEdge == null) {
                // Convert the edge sequence into a path
                FlowOverTimePath path = new FlowOverTimePath(residualPath);
                // Register the path for uncrossing purposes
                addPathToUsageLists(path);
                // Add it to the result
                pathBased.addPathFlow(path);
                // If the path contains no reverse arcs but negative waiting times
            } else {
                if (waiting) {
                    // Get the first node at which negative waiting occurs
                    int t = getArrivalTimeStart(residualPath, reverseEdge);
                    Queue<FlowOverTimePath> uncrossingPartners = pathsUsingNode.get(reverseEdge.getEdge().start())[t - 1];
                    do {
                        FlowOverTimePath partner = uncrossingPartners.peek();
                        uncrossPaths(residualPath, partner, reverseEdge.getEdge().start(), reverseEdge, t);
                    } while (residualPath.getRate() > 0);
                } else {
                    Edge edge = network.reverseEdge(reverseEdge.getEdge());
                    int t = getArrivalTime(residualPath, reverseEdge);
                    int t2 = getArrivalTimeEnd(residualPath, reverseEdge);
                    Queue<FlowOverTimePath> uncrossingPartners = pathsUsingEdge.get(edge)[t2];
                    do {
                        FlowOverTimePath partner = uncrossingPartners.peek();
                        uncrossPaths(residualPath, partner, reverseEdge, t, t2);
                    } while (residualPath.getRate() > 0);
                }
            }
        }
        return pathBased;
    }

    public static void main(String[] args) {
        FlowOverTimeEdgeSequence sequence = new FlowOverTimeEdgeSequence();
        Node[] nodes = new Node[10];
        for (int i = 0; i < 10; i++) nodes[i] = new Node(i);

        sequence.add(new FlowOverTimeEdge(new Edge(0, nodes[0], nodes[1]), 0));
        sequence.add(new FlowOverTimeEdge(new Edge(1, nodes[1], nodes[2]), 1));
        sequence.add(new FlowOverTimeEdge(new Edge(2, nodes[2], nodes[3]), 0));
        sequence.add(new FlowOverTimeEdge(new Edge(3, nodes[3], nodes[1]), 0));
        sequence.add(new FlowOverTimeEdge(new Edge(4, nodes[1], nodes[4]), 1));
        sequence.add(new FlowOverTimeEdge(new Edge(5, nodes[4], nodes[5]), 0));
        sequence.add(new FlowOverTimeEdge(new Edge(6, nodes[5], nodes[1]), 0));
        sequence.add(new FlowOverTimeEdge(new Edge(7, nodes[1], nodes[6]), 0));

        NewChainDecomposition test = new NewChainDecomposition();
        System.out.println(test.extractCycle(sequence));
        System.out.println(test.extractCycle(sequence));
        System.out.println(sequence);
    }

    private transient IdentifiableIntegerMapping<Node> arrivalTime;

    protected FlowOverTimeCycle extractCycle(FlowOverTimeEdgeSequence edgeSequence) {
        // Check whether a node is visited twice        
        arrivalTime.initializeWith(-1);
        Node node = null;
        int time = 0;
        for (FlowOverTimeEdge edge : edgeSequence) {
            Edge e = edge.getEdge();
            if (arrivalTime.get(e.start()) != -1) {
                node = e.start();
                break;
            }
            arrivalTime.set(e.start(), time);
            time += edge.getDelay();
            time += transitTimes.get(edge.getEdge());
        }
        // The first node that is visited twice is now stored in node, if there is such a node
        if (node != null) {
            // Split the sequence at the points where the node is visited the first and second times
            FlowOverTimeEdgeSequence[] sequences = new FlowOverTimeEdgeSequence[3];
            for (int i = 0; i < 3; i++) {
                sequences[i] = new FlowOverTimeEdgeSequence();
            }
            int index = 0;
            for (FlowOverTimeEdge edge : edgeSequence) {
                if (edge.getEdge().start() == node && index < 2) {
                    index++;
                }
                sequences[index].add(edge);
            }
            // Remove the part in between the occurences of the node
            edgeSequence.clear();
            edgeSequence.append(sequences[0]);
            edgeSequence.append(sequences[2], time - arrivalTime.get(node) + sequences[2].delay(sequences[2].getFirst()));
            // Return the cycle
            int cycleBaseWaiting = sequences[1].getFirst().getDelay();
            sequences[1].getFirst().setDelay(arrivalTime.get(node) + cycleBaseWaiting - time);
            return new FlowOverTimeCycle(sequences[1], arrivalTime.get(node) + cycleBaseWaiting);
        } else {
            return null;
        }
    }

    protected void addCycleToUsageLists(FlowOverTimeCycle cycle) {
        int time = cycle.getOffset();
        for (FlowOverTimeEdge edge : cycle) {
            Edge e = edge.getEdge();
            Node start = e.start();
            for (int t = time; t < time + cycle.delay(edge); t++) {
                if (!pathsUsingNode.isDefinedFor(start)) {
                    pathsUsingNode.set(start, new LinkedList[network.getTimeHorizon()]);
                }
                if (pathsUsingNode.get(start)[t] == null) {
                    pathsUsingNode.get(start)[t] = new LinkedList();
                }
                pathsUsingNode.get(start)[t].add(cycle);
            }
            time += cycle.delay(edge);
            if (!pathsUsingEdge.isDefinedFor(edge.getEdge())) {
                pathsUsingEdge.set(edge.getEdge(), new LinkedList[network.getTimeHorizon()]);
            }
            if (pathsUsingEdge.get(edge.getEdge())[time] == null) {
                pathsUsingEdge.get(edge.getEdge())[time] = new LinkedList();
            }
            pathsUsingEdge.get(edge.getEdge())[time].add(cycle);
            time += network.transitTimes().get(edge.getEdge());
        }
    }

    protected void addToUsageLists(FlowOverTimeEdgeSequence edgeSequence) {
        int time = 0;
        for (FlowOverTimeEdge edge : edgeSequence) {
            Edge e = edge.getEdge();
            Node start = e.start();
            for (int t = time; t < time + edgeSequence.delay(edge); t++) {
                if (!pathsUsingNode.isDefinedFor(start)) {
                    pathsUsingNode.set(start, new LinkedList[network.getTimeHorizon()]);
                }
                if (pathsUsingNode.get(start)[t] == null) {
                    pathsUsingNode.get(start)[t] = new LinkedList();
                }
                pathsUsingNode.get(start)[t].add(edgeSequence);
            }
            time += edgeSequence.delay(edge);
            if (!pathsUsingEdge.isDefinedFor(edge.getEdge())) {
                pathsUsingEdge.set(edge.getEdge(), new LinkedList[network.getTimeHorizon()]);
            }
            if (pathsUsingEdge.get(edge.getEdge())[time] == null) {
                pathsUsingEdge.get(edge.getEdge())[time] = new LinkedList();
            }
            pathsUsingEdge.get(edge.getEdge())[time].add(edgeSequence);
            time += network.transitTimes().get(edge.getEdge());
        }
    }

    private void uncrossPaths(FlowOverTimeEdgeSequence pathWithReverseEdge, FlowOverTimePath partner, FlowOverTimeEdge reverseEdge, int reverseTime, int normalTime) {
        //assert partner != null : "Trying to uncross with a null partner";
        FlowOverTimeEdgeSequence start1 = getPathUntil(pathWithReverseEdge, reverseEdge, reverseTime);
        FlowOverTimeEdgeSequence start2 = new FlowOverTimeEdgeSequence(getPathUntil(partner, network.reverseEdge(reverseEdge.getEdge()), normalTime));
        FlowOverTimeEdgeSequence end1 = getPathFrom(pathWithReverseEdge, reverseEdge, reverseTime);
        FlowOverTimePath end2 = getPathFrom(partner, network.reverseEdge(reverseEdge.getEdge()), normalTime);
        if (DEBUG) {
            System.out.println(" Path is split to:\n  " + start1 + "\n  " + end1);
        }
        if (DEBUG) {
            System.out.println(" Partner is split to:\n  " + start2 + "\n  " + end2);
        }
        Edge lastEdge = network.reverseEdge(reverseEdge.getEdge());
        while (!start2.isEmpty() && !end1.isEmpty() && network.reverseEdge(start2.getLastEdge().getEdge()).equals(end1.getFirstEdge().getEdge())) {
            if (DEBUG) {
                System.out.println(" Cleaning: " + getArrivalTimeEnd(pathWithReverseEdge, end1.getFirstEdge()) + " " + getArrivalTime(partner, start2.getLastEdge().getEdge()));
            }
            if (getArrivalTimeEnd(pathWithReverseEdge, end1.getFirstEdge()) != getArrivalTime(partner, start2.getLastEdge().getEdge())) {
                break;
            }
            lastEdge = start2.getLastEdge().getEdge();
            start2.removeLast();
            end1.removeFirst();
        }
        if (DEBUG) {
            System.out.println(" Path is cleaned to:\n  " + start1 + "\n  " + end1);
        }
        if (DEBUG) {
            System.out.println(" Partner is cleaned to:\n  " + start2 + "\n  " + end2);
        }
        if (!end2.isEmpty()) {
            int secondStart = getArrivalTime(partner, end2.getFirstEdge());
            int firstEnd = 0;
            if (!start1.isEmpty()) {
                firstEnd = getArrivalTimeEnd(pathWithReverseEdge, start1.getLastEdge());
            } else {
                firstEnd = getArrivalTime(pathWithReverseEdge, reverseEdge.getEdge().start());
            }
            //end2.getDynamicPath().setDelay(end2.firstEdge(), secondStart - firstEnd);
            start1.append(end2, secondStart - firstEnd);
        }
        if (!end1.isEmpty()) {
            int secondStart = getArrivalTime(pathWithReverseEdge, end1.getFirstEdge());

            int firstEnd = 0;
            if (!start2.isEmpty()) {
                firstEnd = getArrivalTimeEnd(partner, start2.getLastEdge().getEdge());
            } else {
                firstEnd = getArrivalTime(partner, lastEdge.start());

            }
            //if (DEBUG) System.out.println(" Delay of " + end1.getFirstEdge() + " is " + (secondStart - firstEnd));
            //end1.getFirstEdge().setDelay(secondStart - firstEnd);
            start2.append(end1, secondStart - firstEnd);
        }
        int rate = Math.min(pathWithReverseEdge.getRate(), partner.getRate());
        start1.setRate(rate);
        start2.setRate(rate);
        if (DEBUG) {
            System.out.println(" The new paths are:\n  " + start1.getRate() + ": " + start1 + "\n  " + start2.getRate() + ": " + start2);
        }
        pathWithReverseEdge.setRate(pathWithReverseEdge.getRate() - rate);
        partner.setRate(partner.getRate() - rate);
        if (partner.getRate() == 0) {
            removePathFromUsageLists(partner);
            pathBased.remove(partner);
        }
        FlowOverTimePath newPath = new FlowOverTimePath(start1);
        addPathToUsageLists(newPath);
        pathBased.addPathFlow(newPath);
        paths.addFirst(start2);
        if (DEBUG_FINE) {
            System.out.println(" Paths: " + paths);
        }
    }

    private void uncrossPaths(FlowOverTimeEdgeSequence pathWithReverseNode, FlowOverTimePath partner, Node reverseNode, FlowOverTimeEdge reverseNodeEdge, int t) {
        FlowOverTimeEdgeSequence start1 = getPathUntil(pathWithReverseNode, reverseNode, t);
        FlowOverTimeEdgeSequence start2 = new FlowOverTimeEdgeSequence(getPathUntil(partner, reverseNode, t - 1));
        FlowOverTimeEdgeSequence end1 = getPathFrom(pathWithReverseNode, reverseNode, t);
        FlowOverTimePath end2 = getPathFrom(partner, reverseNode, t - 1);
        if (DEBUG) {
            System.out.println(" Path is split to:\n  " + start1 + "\n  " + end1);
        }
        if (DEBUG) {
            System.out.println(" Partner is split to:\n  " + start2 + "\n  " + end2);
        }
        //if (DEBUG) System.out.println(" Paths[0]a: " + paths.getFirst());
        if (!end2.isEmpty()) {
            int time = getArrivalTime(partner, end2.getFirstEdge()) - getArrivalTimeStart(pathWithReverseNode, reverseNodeEdge);
            //if (DEBUG) System.out.println(" Paths[0]b: " + paths.getFirst());
            //end2.getDynamicPath().setDelay(end2.firstEdge(), time);
            //if (DEBUG) System.out.println(" Paths[0]c: " + paths.getFirst());
            start1.append(end2, time);
        }
        //if (DEBUG) System.out.println(" Paths[0]d: " + paths.getFirst());
        if (!end1.isEmpty()) {
            int time = 0;
            if (!start2.isEmpty()) {
                time = getArrivalTime(pathWithReverseNode, end1.getFirstEdge()) - getArrivalTimeEnd(partner, start2.getLastEdge().getEdge());
                if (DEBUG) {
                    System.out.println(" Delay: " + time + " " + getArrivalTime(pathWithReverseNode, end1.getFirstEdge()) + " " + getArrivalTimeEnd(partner, start2.getLastEdge().getEdge()));
                }
            } else {
                time = getArrivalTime(pathWithReverseNode, end1.getFirstEdge()) - getArrivalTime(partner, reverseNode);
            }
            //if (DEBUG) System.out.println(" Paths[0]e: " + paths.getFirst());
            //end1.getFirstEdge().setDelay(time);
            //if (DEBUG) System.out.println(" Paths[0]f: " + paths.getFirst());
            start2.append(end1, time);
        }
        //if (DEBUG) System.out.println(" Paths[0]g: " + paths.getFirst());
        int rate = Math.min(pathWithReverseNode.getRate(), partner.getRate());
        start1.setRate(rate);
        start2.setRate(rate);
        if (DEBUG) {
            System.out.println(" The new paths are:\n  " + start1.getRate() + ": " + start1 + "\n  " + start2.getRate() + ": " + start2);
        }
        pathWithReverseNode.setRate(pathWithReverseNode.getRate() - rate);
        partner.setRate(partner.getRate() - rate);
        if (partner.getRate() == 0) {
            if (DEBUG) {
                System.out.println(" Removing partner (no rate left)");
            }
            removePathFromUsageLists(partner);
            pathBased.remove(partner);
        }
        FlowOverTimePath newPath = new FlowOverTimePath(start1);
        addPathToUsageLists(newPath);
        pathBased.addPathFlow(newPath);
        paths.addFirst(start2);
        //System.out.println(" Paths: " + paths);
    }

    private void addPathToUsageLists(FlowOverTimePath path) {
        int time = 0;
        for (Edge edge : path) {
            for (int t = time; t < time + path.delay(edge); t++) {
                if (!pathsUsingNode.isDefinedFor(edge.start())) {
                    pathsUsingNode.set(edge.start(), new LinkedList[network.getTimeHorizon()]);
                }
                if (pathsUsingNode.get(edge.start())[t] == null) {
                    pathsUsingNode.get(edge.start())[t] = new LinkedList();
                }
                pathsUsingNode.get(edge.start())[t].add(path);
            }
            time += path.delay(edge);
            if (!pathsUsingEdge.isDefinedFor(edge)) {
                pathsUsingEdge.set(edge, new LinkedList[network.getTimeHorizon()]);
            }
            if (pathsUsingEdge.get(edge)[time] == null) {
                pathsUsingEdge.get(edge)[time] = new LinkedList();
            }
            pathsUsingEdge.get(edge)[time].add(path);
            time += network.transitTimes().get(edge);
        }
    }

    private void removePathFromUsageLists(FlowOverTimePath path) {
        int time = 0;
        for (Edge edge : path) {
            for (int t = time; t < time + path.delay(edge); t++) {
                pathsUsingNode.get(edge.start())[t].remove(path);
            }
            time += path.delay(edge);
            pathsUsingEdge.get(edge)[time].remove(path);
            time += network.transitTimes().get(edge);
        }
    }

    private int getArrivalTime(FlowOverTimePath path, Edge edge) {
        int time = 0;
        for (Edge e : path) {
            time += path.delay(e);
            if (e == edge) {
                return time;
            }
            time += network.transitTimes().get(e);
        }
        return -1;
    }

    private int getArrivalTimeEnd(FlowOverTimePath path, Edge edge) {
        int time = 0;
        for (Edge e : path) {
            time += path.delay(e);
            time += network.transitTimes().get(e);
            if (e == edge) {
                return time;
            }
        }
        throw new AssertionError("This should not happen.");
    }

    private int getArrivalTime(FlowOverTimeEdgeSequence path, FlowOverTimeEdge edge) {
        int time = 0;
        for (FlowOverTimeEdge e : path) {
            time += path.delay(e);
            if (e.equals(edge)) {
                return time;
            }
            time += network.transitTimes().get(e.getEdge());
        }
        return -1;
    }

    private int getArrivalTimeStart(FlowOverTimeEdgeSequence path, FlowOverTimeEdge edge) {
        int time = 0;
        for (FlowOverTimeEdge e : path) {
            if (e.equals(edge)) {
                return time;
            }
            time += path.delay(e);
            time += network.transitTimes().get(e.getEdge());
        }
        throw new AssertionError("This should not happen.");
    }

    private int getArrivalTimeEnd(FlowOverTimeEdgeSequence path, FlowOverTimeEdge edge) {
        int time = 0;
        for (FlowOverTimeEdge e : path) {
            time += path.delay(e);
            time += network.transitTimes().get(e.getEdge());
            if (e.equals(edge)) {
                return time;
            }
        }
        throw new AssertionError("This should not happen.");
    }

    private int getArrivalTime(FlowOverTimePath path, Node node) {
        int time = 0;
        for (Edge e : path) {
            if (e.start().equals(node)) {
                return time;
            }
            time += path.delay(e);
            time += network.transitTimes().get(e);
        }
        throw new AssertionError("This should not happen.");
    }

    private int getArrivalTime(FlowOverTimeEdgeSequence path, Node node) {
        int time = 0;
        for (FlowOverTimeEdge e : path) {
            if (e.getEdge().start().equals(node)) {
                return time;
            }
            time += path.delay(e);
            time += network.transitTimes().get(e.getEdge());
        }
        throw new AssertionError("This should not happen.");
    }

    public FlowOverTimePath getPathFrom(FlowOverTimePath path, Edge edge, int time) {
        FlowOverTimePath result = new FlowOverTimePath();
        result.setAmount(path.getAmount());
        result.setRate(path.getRate());
        boolean edgeReached = false;
        int t = 0;
        for (Edge e : path) {
            t += path.delay(e);
            if (e.equals(edge) && t == time) {
                edgeReached = true;
                continue;
            }
            t += network.transitTimes().get(e);
            if (edgeReached) {
                result.getDynamicPath().addLastEdge(e, path.delay(e));
            }
        }
        return result;
    }

    public FlowOverTimeEdgeSequence getPathFrom(FlowOverTimeEdgeSequence path, FlowOverTimeEdge edge, int time) {
        FlowOverTimeEdgeSequence result = new FlowOverTimeEdgeSequence();
        result.setRate(path.getRate());
        boolean edgeReached = false;
        int t = 0;
        for (FlowOverTimeEdge e : path) {
            t += path.delay(e);
            if (e.equals(edge) && t == time) {
                edgeReached = true;
                continue;
            }
            t += network.transitTimes().get(e.getEdge());
            if (edgeReached) {
                result.addLast(e);
            }
        }
        return result;
    }

    private FlowOverTimeEdgeSequence getPathUntil(FlowOverTimeEdgeSequence path, FlowOverTimeEdge edge, int time) {
        FlowOverTimeEdgeSequence result = new FlowOverTimeEdgeSequence();
        result.setRate(path.getRate());
        int t = 0;
        for (FlowOverTimeEdge e : path) {
            t += path.delay(e);
            if (e.equals(edge) && t == time) {
                break;
            }
            t += network.transitTimes().get(e.getEdge());
            result.addLast(e);
        }
        return result;
    }

    private FlowOverTimePath getPathUntil(FlowOverTimePath path, Edge edge, int time) {
        FlowOverTimePath result = new FlowOverTimePath();
        result.setAmount(path.getAmount());
        result.setRate(path.getRate());
        int t = 0;
        for (Edge e : path) {
            t += path.delay(e);
            if (e.equals(edge) && t == time) {
                break;
            }
            t += network.transitTimes().get(e);
            result.getDynamicPath().addLastEdge(e, path.delay(e));
        }
        return result;
    }

    public FlowOverTimePath getPathFrom(FlowOverTimePath path, Node node, int time) {
        FlowOverTimePath result = new FlowOverTimePath();
        result.setAmount(path.getAmount());
        result.setRate(path.getRate());
        boolean nodeReached = false;
        int t = 0;
        for (Edge e : path) {
            if (e.start().equals(node) && isIn(time, t, t + path.delay(e))) {
                nodeReached = true;
                result.getDynamicPath().addLastEdge(e, 0);
                continue;
            }
            t += path.delay(e);
            t += network.transitTimes().get(e);
            if (nodeReached) {
                result.getDynamicPath().addLastEdge(e, path.delay(e));
            }
        }
        return result;
    }

    public FlowOverTimeEdgeSequence getPathFrom(FlowOverTimeEdgeSequence path, Node node, int time) {
        FlowOverTimeEdgeSequence result = new FlowOverTimeEdgeSequence();
        result.setRate(path.getRate());
        boolean nodeReached = false;
        int t = 0;
        for (FlowOverTimeEdge e : path) {
            if (e.getEdge().start().equals(node) && isIn(time, t, t + path.delay(e))) {
                nodeReached = true;
                //e.setDelay(0);
                result.addLast(e);
                //result.getDynamicPath().addLastEdge(e, 0);
                continue;
            }
            t += path.delay(e);
            t += network.transitTimes().get(e.getEdge());
            if (nodeReached) {
                result.addLast(e);
            }
        }
        return result;
    }

    public FlowOverTimeEdgeSequence getPathUntil(FlowOverTimeEdgeSequence path, Node node, int time) {
        FlowOverTimeEdgeSequence result = new FlowOverTimeEdgeSequence();
        result.setRate(path.getRate());
        int t = 0;
        for (FlowOverTimeEdge e : path) {
            if (e.getEdge().start().equals(node) && isIn(time, t, t + path.delay(e))) {
                break;
            }
            t += path.delay(e);
            t += network.transitTimes().get(e.getEdge());
            result.addLast(e);//getDynamicPath().addLastEdge(e, path.delay(e));
        }
        return result;
    }

    public FlowOverTimePath getPathUntil(FlowOverTimePath path, Node node, int time) {
        FlowOverTimePath result = new FlowOverTimePath();
        result.setAmount(path.getAmount());
        result.setRate(path.getRate());
        int t = 0;
        for (Edge e : path) {
            if (e.start().equals(node) && isIn(time, t, t + path.delay(e))) {
                break;
            }
            t += path.delay(e);
            t += network.transitTimes().get(e);
            result.getDynamicPath().addLastEdge(e, path.delay(e));
        }
        return result;
    }

    private boolean isIn(int i, int v1, int v2) {
        int min, max;
        if (v1 < v2) {
            min = v1;
            max = v2;
        } else {
            min = v2;
            max = v1;
        }
        return min <= i && i <= max;
    }
}
