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
 * EarliestArrivalAugmentingPathAlgorithm.java
 *
 */
package algo.graph.dynamicflow.eat;

import ds.graph.flow.EarliestArrivalAugmentingPath;
import ds.graph.DynamicResidualNetwork;
import ds.graph.Edge;
import ds.graph.IdentifiableCollection;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.IntegerIntegerArrayMapping;
import ds.graph.IntegerIntegerMapping;
import ds.graph.Node;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;

/**
 *
 * @author Martin Groß
 */
public class EarliestArrivalAugmentingPathAlgorithm extends Algorithm<EarliestArrivalAugmentingPathProblem, EarliestArrivalAugmentingPath> {

    private static final Logger LOGGER = Logger.getLogger(EarliestArrivalAugmentingPathAlgorithm.class.getCanonicalName());
    
    private static final boolean DEBUG = false;
    
    private static final Node SOURCE = new Node(-1);
    private transient Queue<Node> candidates;
    private transient IdentifiableObjectMapping<Node, IntegerIntegerMapping> departureTimes;
    private transient IdentifiableIntegerMapping<Node> labels;
    private transient DynamicResidualNetwork network;
    private transient IdentifiableObjectMapping<Node, Node[]> predecessorNodes;
    private transient IdentifiableObjectMapping<Node, Edge[]> predecessorEdges;

    @Override
    protected EarliestArrivalAugmentingPath runAlgorithm(EarliestArrivalAugmentingPathProblem problem) {
        network = problem.getNetwork();
        Node source = problem.getSource();
        Node sink = problem.getSink();
        int timeHorizon = problem.getTimeHorizon();
        candidates = new LinkedList<Node>();
        labels = new IdentifiableIntegerMapping(network.nodes());
        departureTimes = new IdentifiableObjectMapping<Node, IntegerIntegerMapping>(network.nodes(), IntegerIntegerMapping.class);
        predecessorNodes = new IdentifiableObjectMapping<Node, Node[]>(network.nodes(), Node[].class);
        predecessorEdges = new IdentifiableObjectMapping<Node, Edge[]>(network.nodes(), Edge[].class);
        for (Node node : network.nodes()) {
            predecessorNodes.set(node, new Node[timeHorizon]);
            predecessorEdges.set(node, new Edge[timeHorizon]);
            departureTimes.set(node, new IntegerIntegerMapping());
        }
        candidates.add(source);
        for (Node node : network.nodes()) {
            setLabel(node, Integer.MAX_VALUE);
        }
        setLabel(source, 0);
        for (Node node : network.nodes()) {
            setPredecessorNode(node, 0, null);
            setDepartureTime(node, 0, Integer.MAX_VALUE);
        }
        setPredecessorNode(source, 0, SOURCE);
        for (int time = 0; time < timeHorizon; time++) {
            setDepartureTime(source, time, time);
        }
        if (DEBUG) System.out.println("WCCs: " + network.waitCancellingCapacities());
        while (!candidates.isEmpty()) {
            Node node = candidates.poll();
            if (DEBUG) System.out.println("Processing node: " + node);
            for (Edge edge : network.outgoingEdges(node)) {
                if (DEBUG) System.out.println(" Processing edge: " + edge);
                int transitTime = transitTime(edge);
                int lastTime = timeHorizon;
                if (transitTime > 0) {
                    lastTime -= transitTime;
                }
                IntegerIntegerArrayMapping caps = network.capacities().get(edge);
                Node[] predStart = predecessorNodes.get(edge.start());
                for (int time = getLabel(edge.start()); time < lastTime; time++) {
                    //System.out.println("1: " + time + " " + timeHorizon + " " + transitTime);
                    //System.out.println("2: " + caps.get(time));
                    //System.out.println("3a: " + predStart.length);
                    //System.out.println("3b: " + predStart[time]);
                    if (caps.get(time) == 0 || predStart[time] == null) {
                        continue;
                    }
                    Node[] predEnd = predecessorNodes.get(edge.end());
                    //System.out.println("  Edge ist available at time " + time);
                    //if (getPredecessorNode(edge.end(), time + transitTime) == null) {
                    if (predEnd[time + transitTime] == null) {
                        if (getLabel(edge.end()) > time + transitTime) {
                            setLabel(edge.end(), time + transitTime);
                        }
                        //setPredecessorNode(edge.end(), time + transitTime, edge.start());
                        predEnd[time + transitTime] = edge.start();
                        predecessorEdges.get(edge.end())[time + transitTime] = edge;
                        setDepartureTime(edge.end(), time + transitTime, time);
                        candidates.add(edge.end());
                        int newTime = time + transitTime + 1;
                        //while (newTime <= timeHorizon && waitCapacity(edge.end(), newTime - 1) > 0 && getPredecessorNode(edge.end(), newTime) == null) {
                        while (newTime < timeHorizon && waitCapacity(edge.end(), newTime - 1) > 0 && predEnd[newTime] == null) {
                            //setPredecessorNode(edge.end(), newTime, edge.end());
                            predEnd[newTime] = edge.end();
                            predecessorEdges.get(edge.end())[newTime] = null;
                            setDepartureTime(edge.end(), newTime, newTime - 1);
                            newTime++;
                        }
                        newTime = time + transitTime - 1;
                        //while (newTime >= 0 && waitCancellingCapacity(edge.end(), newTime + 1) > 0 && getPredecessorNode(edge.end(), newTime) == null) {
                        while (newTime >= 0 && waitCancellingCapacity(edge.end(), newTime + 1) > 0 && predEnd[newTime] == null) {
                            if (DEBUG) System.out.println(String.format("    waitCancellingCapacity(%1$s,%2$s) = %3$s", edge.end(), newTime+1, waitCancellingCapacity(edge.end(), newTime + 1)));
                            if (getLabel(edge.end()) > newTime) {
                                setLabel(edge.end(), newTime);
                            }
                            //setPredecessorNode(edge.end(), newTime, edge.end());
                            predEnd[newTime] = edge.end();
                            predecessorEdges.get(edge.end())[newTime] = null;
                            setDepartureTime(edge.end(), newTime, newTime + 1);
                            newTime--;
                        }
                    }
                }
            }
        }
        if (DEBUG) System.out.println("Predecessors: " + predecessorNodes);
        if (getLabel(sink) >= timeHorizon) {
            return new EarliestArrivalAugmentingPath();
        } else {
            Node node = sink;
            int time = getLabel(node);
            if (DEBUG) System.out.println("Constructing paths");
            if (DEBUG) System.out.println("Current time/node: " + time + " / " + node);
            Node pred = getPredecessorNode(node, time);
            Edge predEdge = predecessorEdges.get(node)[time];
            EarliestArrivalAugmentingPath path = new EarliestArrivalAugmentingPath();
            path.insert(0, node, time, time);
            int capacity = Integer.MAX_VALUE;
            int predecessorDepartureTime, nextTime = 0;
            while (node != SOURCE && pred != SOURCE) {
                predecessorDepartureTime = getDepartureTime(node, time);
                if (DEBUG) System.out.println("Current predecessor time/node: " + predecessorDepartureTime + " / " + pred);
                int cap;
                if (node != pred) {
                    /*
                    IdentifiableCollection<Edge> edges = network.getEdges(pred, node);
                    Edge edge = null;
                    if (edges.size() == 1) {
                        edge = edges.first();
                    } else if (edges.size() == 2) {
                        if (capacity(edges.first(), predecessorDepartureTime) > 0) {
                        //if (predecessorDepartureTime < time) {
                            edge = edges.first();
                        } else {
                            edge = edges.last();                                
                        }
                    }*/
                    Edge edge = predEdge;
                            
                    cap = capacity(edge, predecessorDepartureTime);
                    if (cap == 0) {
                        //System.out.println("Case 1: " + edges + " " + capacity(edges.first(), predecessorDepartureTime) + " " + capacity(edges.last(), predecessorDepartureTime));
                        //System.out.println(transitTime(edges.first(), predecessorDepartureTime));
                    }
                    nextTime = predecessorDepartureTime;
                } else if (time > predecessorDepartureTime) {
                    cap = waitCapacity(pred, predecessorDepartureTime);
                } else {
                    cap = waitCancellingCapacity(pred, predecessorDepartureTime);               
                    //System.out.println("Get: " + pred + " " + predecessorDepartureTime + " " + cap);                    
                }
                if (cap < capacity) {
                    capacity = cap;
                }
                //System.out.println("Edge capacity is " + cap + ", Path capacity is now " + capacity);
                Node oldPred = pred;
                node = pred;
                pred = getPredecessorNode(node, predecessorDepartureTime);
                predEdge = predecessorEdges.get(node)[predecessorDepartureTime];
                time = predecessorDepartureTime;
                if (DEBUG) System.out.println("Current time/node: " + time + "/" + node);
                if (oldPred != pred) {   
                    if (DEBUG) System.out.println(predecessorDepartureTime + " vs " + nextTime);
                    int newDepTime = getDepartureTime(node, time);
                    if (DEBUG) System.out.println("newDepTime " + newDepTime);
                    int tt;
                    if (pred != SOURCE && node != pred) {
                        tt = transitTime(predEdge);
                        /*
                        IdentifiableCollection<Edge> edges = network.getEdges(pred, node);
                        if (edges.size() == 1) {
                            if (DEBUG) System.out.println("Case 1");
                            tt = transitTime(edges.first());
                        } else if (edges.size() == 2) {
                            if (DEBUG) System.out.println("Case 2");
                            int tt1 = transitTime(edges.first());
                            int tt2 = transitTime(edges.last());
                            if (newDepTime+tt1 <= predecessorDepartureTime) {                        // TEST!
                                tt = tt1;
                            } else if (newDepTime+tt2 <= predecessorDepartureTime) {
                                tt = tt2;
                            } else {
                                if (DEBUG) System.out.println("tt1 " + tt1);
                                if (DEBUG) System.out.println("tt2 " + tt2);
                                throw new AssertionError("This should not happen");
                            }
                            /*
                            if (newDepTime+tt1 <= nextTime) {
                                tt = tt1;
                            } else if (newDepTime+tt2 <= nextTime) {
                                tt = tt2;
                            } else {
                                throw new AssertionError("This should not happen.");
                            }*//*
                        } else {
                            throw new AssertionError("This should not happen.");
                        }*/                    
                    } else {
                        if (DEBUG) System.out.println("Case 3");
                        tt = 0;
                    }
                    if (DEBUG) System.out.println("tt " + tt);
                    if (DEBUG) System.out.println("Adding NTP: " + oldPred + "(" + (newDepTime+tt) + "," + nextTime + ")");
                    if (newDepTime + tt > nextTime) {
                        if (DEBUG) System.out.println("Adding NTP: " + oldPred + "(" + (newDepTime+tt) + "," + nextTime + ")");
                    }
                    //path.insert(0, oldPred, predecessorDepartureTime, nextTime);
                    path.insert(0, oldPred, newDepTime+tt, nextTime);
                }
                //System.out.println("");
            }
            path.setCapacity(capacity);
            return path;
        }
    }

    private int getDepartureTime(Node node, int time) {
        return departureTimes.get(node).get(time);
    }

    private void setDepartureTime(Node node, int time, int value) {
        departureTimes.get(node).set(time, value);
    }

    private int getLabel(Node node) {
        return labels.get(node);
    }

    private void setLabel(Node node, int value) {
        if (DEBUG) System.out.println(String.format("    pi(%1$s) := %2$s", node, value));
        labels.set(node, value);
    }

    private Node getPredecessorNode(Node node, int time) {
        return predecessorNodes.get(node)[time];
    }

    private void setPredecessorNode(Node node, int time, Node predecessor) {
        if (DEBUG) System.out.println(String.format("    pred(%1$s,%2$s) := %3$s", node, time, predecessor));
        predecessorNodes.get(node)[time] = predecessor;
    }

    private int capacity(Edge edge, int time) {
        return network.capacities().get(edge).get(time);
    }

    private int transitTime(Edge edge) {
        return network.transitTimes().get(edge);
    }

    private int waitCapacity(Node node, int time) {
        return network.waitCapacities().get(node).get(time);
    }

    private int waitCancellingCapacity(Node node, int time) {
        return network.waitCancellingCapacities().get(node).get(time);
    }
}
