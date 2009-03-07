/*
 * EarliestArrivalAugmentingPath.java
 *
 */

package algo.graph.dynamicflow.eat;

import ds.graph.Node;
import java.util.LinkedList;
import algo.graph.dynamicflow.eat.EarliestArrivalAugmentingPath.NodeTimePair;
import ds.graph.DynamicResidualNetwork;
import ds.graph.Edge;
import ds.graph.flow.FlowOverTimeEdge;
import ds.graph.flow.FlowOverTimeEdgeSequence;
import ds.graph.flow.FlowOverTimePath;

/**
 *
 * @author Martin Groß
 */
public class EarliestArrivalAugmentingPath extends LinkedList<NodeTimePair> {

    private int capacity;
    
    public EarliestArrivalAugmentingPath() {
        super();
        capacity = 0;
    }
    
    public int getArrivalTime() {
        if (isEmpty()) {
            return 0;
        } else {
            return getLast().getEnd();
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public void insert(int index, Node node, int start, int end) {
        add(index, new NodeTimePair(node, start, end));
    }

    @Override
    public String toString() {
        return capacity + ": " + super.toString();
    }
    
    public FlowOverTimeEdgeSequence getFlowOverTimeEdgeSequence(DynamicResidualNetwork network) {
        FlowOverTimeEdgeSequence dpf = new FlowOverTimeEdgeSequence();
        dpf.setRate(getCapacity());
        NodeTimePair first = getFirst(), previous = null;
        for (NodeTimePair ntp : this) {
            if (ntp == first) {
                previous = first;
                continue;
            } else {
                if (previous.getNode() == network.getSuperSource()) {
                    previous = ntp;
                    continue;
                }
                if (previous == first) {
                    dpf.addLast(new FlowOverTimeEdge(network.getEdge(previous.getNode(), ntp.getNode(), previous.getEnd(), ntp.getStart()), previous.getEnd()));
                    //dpf.getDynamicPath().addLastEdge(network.getEdge(previous.getNode(), ntp.getNode(), previous.getEnd(), ntp.getStart()), previous.getEnd());
                } else {
                    //System.out.println("Edges: " + network.getEdges(previous.getNode(), ntp.getNode()));
                    //for (Edge edge : network.getEdges(previous.getNode(), ntp.getNode())) {
                    //    System.out.println("Edge: " + edge + " TT = " + network.transitTimes().get(edge));
                    //}
                    dpf.addLast(new FlowOverTimeEdge(network.getEdge(previous.getNode(), ntp.getNode(), previous.getEnd(), ntp.getStart()), previous.getEnd() - previous.getStart()));
                    //dpf.getDynamicPath().addLastEdge(network.getEdge(previous.getNode(), ntp.getNode(), previous.getEnd(), ntp.getStart()), previous.getEnd() - previous.getStart());
                }
                previous = ntp;                
            }
        }        
        return dpf;
    }    
    
    public class NodeTimePair {
        
        private Node node;
        private int start;
        private int end;

        public NodeTimePair(Node node, int start, int end) {
            this.node = node;
            this.start = start;
            this.end = end;
        }

        public int getEnd() {
            return end;
        }

        public Node getNode() {
            return node;
        }

        public int getStart() {
            return start;
        }

        @Override
        public String toString() {
            return node + "(" + start + "," + end + ")";
        }       
    }
}
