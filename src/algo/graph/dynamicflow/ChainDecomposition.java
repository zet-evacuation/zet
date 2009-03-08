/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
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

import ds.graph.DynamicResidualNetwork;
import ds.graph.Edge;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Node;
import ds.graph.flow.FlowOverTimeEdge;
import ds.graph.flow.FlowOverTimeEdgeSequence;
import ds.graph.flow.FlowOverTimePath;
import ds.graph.flow.PathBasedFlowOverTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Martin Groß
 */
public class ChainDecomposition {

    private static final boolean DEBUG = false;
    private static final boolean DEBUG_FINE = false;
    public PathBasedFlowOverTime pathBased;
    IdentifiableObjectMapping<Edge, Queue[]> pathsUsingEdge;
    IdentifiableObjectMapping<Node, Queue[]> pathsUsingNode;
    DynamicResidualNetwork network;
    Queue<FlowOverTimeEdge> reverseEdges;
    Queue<FlowOverTimeEdge> reverseNodes;
    LinkedList<FlowOverTimeEdgeSequence> paths;

    public void uncrossPaths(DynamicResidualNetwork network, LinkedList<FlowOverTimeEdgeSequence> edgeSequences) {
        this.network = network;
        this.paths = edgeSequences;
        pathsUsingEdge = new IdentifiableObjectMapping<Edge, Queue[]>(network.edges(), Queue[].class);
        pathsUsingNode = new IdentifiableObjectMapping<Node, Queue[]>(network.nodes(), Queue[].class);
        /*
        edgeCaps = new IdentifiableObjectMapping<Edge, IntegerIntegerArrayMapping>(network.edges(), IntegerIntegerArrayMapping.class);
        for (Edge edge : network.edges()) {
            edgeCaps.set(edge, new IntegerIntegerArrayMapping(network.getTimeHorizon()));
        }
        nodeCaps = new IdentifiableObjectMapping<Node, IntegerIntegerArrayMapping>(network.nodes(), IntegerIntegerArrayMapping.class);
        for (Node node : network.nodes()) {
            nodeCaps.set(node, new IntegerIntegerArrayMapping(network.getTimeHorizon()));
        }*/
        reverseEdges = new LinkedList<FlowOverTimeEdge>();
        reverseNodes = new LinkedList<FlowOverTimeEdge>();
        if (DEBUG) System.out.println("Supersource: " + network.getSuperSource());
        if (DEBUG) System.out.println("Source: " + network.successorNodes(network.getSuperSource()));
        //if (DEBUG) System.out.println("Capacities: " + network.capacities());
        if (DEBUG) System.out.println("TransitTimes: " + network.transitTimes());
        int index = 0;
        while (!edgeSequences.isEmpty()) {
            reverseEdges.clear();
            reverseNodes.clear();
            if (DEBUG_FINE) System.out.println(" Paths: " + paths);            
            FlowOverTimeEdgeSequence edgeSequence = edgeSequences.poll();
            if (DEBUG) 
                System.out.println("Processing Edge Sequence: " + (index++) + ": " + edgeSequence);
            //test(edgeSequence);
            int time = 0;
            for (FlowOverTimeEdge edge : edgeSequence) {
                time += edgeSequence.delay(edge);
                if (edgeSequence.delay(edge) < 0) {
                    for (int t = time; t > time + edgeSequence.delay(edge); t--) {
                        if (DEBUG_FINE) System.out.println(" Cancel waiting in " + edge.getEdge().start() + " at time " + t + ": " + edgeSequence);
                    }
                    reverseNodes.add(edge);
                } else if (edgeSequence.delay(edge) > 0) {
                    for (int i = time - edgeSequence.delay(edge); i < time; i++) {
                        if (DEBUG_FINE) System.out.println(" Waiting in " + edge.getEdge().start() + " at time " + i + ": " + edgeSequence);                        
                    }
                }
                time += network.transitTimes().get(edge.getEdge());
                if (network.isReverseEdge(edge.getEdge())) {
                    reverseEdges.add(edge);
                }
            }
            if (DEBUG) System.out.println(" Reverse edges: " + reverseEdges);
            if (DEBUG) System.out.println(" Reverse nodes: " + reverseNodes);
            if (reverseEdges.isEmpty() && reverseNodes.isEmpty()) {
                if (DEBUG) System.out.println(" Add path to path based flow.");
                FlowOverTimePath path = new FlowOverTimePath(edgeSequence);
                addPathToUsageLists(path);
                pathBased.addPathFlow(path);
            } else if (reverseEdges.isEmpty() && !reverseNodes.isEmpty()) {
                FlowOverTimeEdge reverseNodeEdge = reverseNodes.poll();
                int t = getArrivalTimeStart(edgeSequence, reverseNodeEdge);
                if (DEBUG) System.out.println(" Try to uncross reverse node " + reverseNodeEdge.getEdge().start() + " at time " + t);
                if (DEBUG_FINE) System.out.println(" Uncrossing partners: " + Arrays.deepToString(pathsUsingNode.get(reverseNodeEdge.getEdge().start())));
                Queue<FlowOverTimePath> uncrossingPartners = pathsUsingNode.get(reverseNodeEdge.getEdge().start())[t - 1];
                if (uncrossingPartners == null && DEBUG) {
                    System.out.println(Arrays.deepToString(pathsUsingNode.get(reverseNodeEdge.getEdge().start())));
                }
                do {
                    FlowOverTimePath partner = uncrossingPartners.peek();
                    if (DEBUG) System.out.println(" Using path for uncrossing: " + partner);
                    uncrossPaths(edgeSequence, partner, reverseNodeEdge.getEdge().start(), reverseNodeEdge, t);
                } while (edgeSequence.getRate() > 0);
            } else {
                FlowOverTimeEdge reverseEdge = reverseEdges.poll();
                Edge edge = network.reverseEdge(reverseEdge.getEdge());
                int t = getArrivalTime(edgeSequence, reverseEdge);
                int t2 = getArrivalTimeEnd(edgeSequence, reverseEdge);
                if (DEBUG) System.out.println(" Try to uncross reverse edge " + reverseEdge + " at time " + t + " with the normal edge " + edge + " at time " + t2);
                if (DEBUG_FINE) System.out.println(" Uncrossing partners: " + Arrays.deepToString(pathsUsingEdge.get(edge)));
                Queue<FlowOverTimePath> uncrossingPartners = pathsUsingEdge.get(edge)[t2];
                do {
                    FlowOverTimePath partner = uncrossingPartners.peek(); 
                    if (DEBUG) System.out.println(" Using path for uncrossing: " + partner);
                    uncrossPaths(edgeSequence, partner, reverseEdge, t, t2);
                } while (edgeSequence.getRate() > 0);
            }
            if (DEBUG) System.out.println("");
        }
    }
    
    //IdentifiableObjectMapping<Node, IntegerIntegerArrayMapping> nodeCaps;
    //IdentifiableObjectMapping<Edge, IntegerIntegerArrayMapping> edgeCaps;

    private void uncrossPaths(FlowOverTimeEdgeSequence pathWithReverseEdge, FlowOverTimePath partner, FlowOverTimeEdge reverseEdge, int reverseTime, int normalTime) {
        //assert partner != null : "Trying to uncross with a null partner";
        FlowOverTimeEdgeSequence start1 = getPathUntil(pathWithReverseEdge, reverseEdge, reverseTime);
        FlowOverTimeEdgeSequence start2 = new FlowOverTimeEdgeSequence(getPathUntil(partner, network.reverseEdge(reverseEdge.getEdge()), normalTime));
        FlowOverTimeEdgeSequence end1 = getPathFrom(pathWithReverseEdge, reverseEdge, reverseTime);
        FlowOverTimePath end2 = getPathFrom(partner, network.reverseEdge(reverseEdge.getEdge()), normalTime);
        if (DEBUG) System.out.println(" Path is split to:\n  " + start1 + "\n  " + end1);
        if (DEBUG) System.out.println(" Partner is split to:\n  " + start2 + "\n  " + end2);
        Edge lastEdge = network.reverseEdge(reverseEdge.getEdge());
        while (!start2.isEmpty() && !end1.isEmpty() && network.reverseEdge(start2.getLastEdge().getEdge()).equals(end1.getFirstEdge().getEdge())) {
            if (DEBUG) System.out.println(" Cleaning: " + getArrivalTimeEnd(pathWithReverseEdge, end1.getFirstEdge()) + " " + getArrivalTime(partner, start2.getLastEdge().getEdge()));
            if (getArrivalTimeEnd(pathWithReverseEdge, end1.getFirstEdge()) != getArrivalTime(partner, start2.getLastEdge().getEdge())) {
                break;
            }
            lastEdge = start2.getLastEdge().getEdge();
            start2.removeLast();
            end1.removeFirst();
        }
        if (DEBUG) System.out.println(" Path is cleaned to:\n  " + start1 + "\n  " + end1);
        if (DEBUG) System.out.println(" Partner is cleaned to:\n  " + start2 + "\n  " + end2);        
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
            }  else {
                firstEnd = getArrivalTime(partner, lastEdge.start());;
            }     
            //if (DEBUG) System.out.println(" Delay of " + end1.getFirstEdge() + " is " + (secondStart - firstEnd));
          //end1.getFirstEdge().setDelay(secondStart - firstEnd);
            start2.append(end1, secondStart - firstEnd);        
        }
        int rate = Math.min(pathWithReverseEdge.getRate(), partner.getRate());
        start1.setRate(rate);
        start2.setRate(rate);
        if (DEBUG) System.out.println(" The new paths are:\n  " + start1.getRate() + ": " + start1 + "\n  " + start2.getRate() + ": " + start2);
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
        if (DEBUG_FINE) System.out.println(" Paths: " + paths);                
    }

    private void uncrossPaths(FlowOverTimeEdgeSequence pathWithReverseNode, FlowOverTimePath partner, Node reverseNode, FlowOverTimeEdge reverseNodeEdge, int t) {
        FlowOverTimeEdgeSequence start1 = getPathUntil(pathWithReverseNode, reverseNode, t);
        FlowOverTimeEdgeSequence start2 = new FlowOverTimeEdgeSequence(getPathUntil(partner, reverseNode, t - 1));
        FlowOverTimeEdgeSequence end1 = getPathFrom(pathWithReverseNode, reverseNode, t);
        FlowOverTimePath end2 = getPathFrom(partner, reverseNode, t - 1);
        if (DEBUG) System.out.println(" Path is split to:\n  " + start1 + "\n  " + end1);
        if (DEBUG) System.out.println(" Partner is split to:\n  " + start2 + "\n  " + end2);
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
            if (DEBUG) System.out.println(" Delay: " + time + " " + getArrivalTime(pathWithReverseNode, end1.getFirstEdge()) + " " + getArrivalTimeEnd(partner, start2.getLastEdge().getEdge()));
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
        if (DEBUG) System.out.println(" The new paths are:\n  " + start1.getRate() + ": " + start1 + "\n  " + start2.getRate() + ": " + start2);
        pathWithReverseNode.setRate(pathWithReverseNode.getRate() - rate);
        partner.setRate(partner.getRate() - rate);
        if (partner.getRate() == 0) {
            if (DEBUG) System.out.println(" Removing partner (no rate left)");
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
                if (DEBUG_FINE) System.out.println(" Waiting in " + edge.start() + " at time " + t + ": " + path);                
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
            if (DEBUG) {
            if (edge.id() == 4278 && time == 16) {
                System.out.println("4278@16: " + path);
            } else {
                System.out.println("Added: " + edge.id() + "@" + time + ":" + path);
            }
            }
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
            if (edge.id() == 4278 && time == 16 && DEBUG) {
                System.out.println("4278@16: remove " + path);
            }            
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
    
    private boolean isIn(int i, int v1, int v2)  {
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
