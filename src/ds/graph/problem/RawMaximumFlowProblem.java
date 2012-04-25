/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ds.graph.problem;

/**
 *
 * @author gross
 */
public class RawMaximumFlowProblem {
    
    private int[] nodeStartIndices;
    private int[] edgeEndIDs;
    private int[] edgeCapacities;
    private int numberOfEdges;
    private int numberOfNodes;
    private int scaling;
    private int sinkID;
    private int sourceID;

    public RawMaximumFlowProblem(int numberOfNodes, int numberOfEdges) {
        this(numberOfNodes,numberOfEdges,null,null,null,-1,-1);
    }    
    
    public RawMaximumFlowProblem(int numberOfNodes, int numberOfEdges, int[] nodeStartIndices, int[] edgeEndIDs, int[] edgeCapacities, int sinkID, int sourceID) {
        this.numberOfNodes = numberOfNodes;
        this.numberOfEdges = numberOfEdges;
        this.nodeStartIndices = nodeStartIndices;
        this.edgeEndIDs = edgeEndIDs;
        this.edgeCapacities = edgeCapacities;
        this.sinkID = sinkID;
        this.sourceID = sourceID;
    }

    public RawMaximumFlowProblem(int numberOfNodes, int numberOfEdges, int[] nodeStartIndices, int[] edgeEndIDs, int[] edgeCapacities, int sinkID, int sourceID, int scaling) {
        this.numberOfNodes = numberOfNodes;
        this.numberOfEdges = numberOfEdges;
        this.nodeStartIndices = nodeStartIndices;
        this.edgeEndIDs = edgeEndIDs;
        this.edgeCapacities = edgeCapacities;
        this.sinkID = sinkID;
        this.sourceID = sourceID;
        this.scaling = scaling;
    }    
    
    public int[] getEdgeCapacities() {
        return edgeCapacities;
    }

    public int[] getEdgeEndIDs() {
        return edgeEndIDs;
    }

    public int[] getNodeStartIndices() {
        return nodeStartIndices;
    }

    public int getNumberOfEdges() {
        return numberOfEdges;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public int getScaling() {
        return scaling;
    }
    
    public int getSinkID() {
        return sinkID;
    }

    public int getSourceID() {
        return sourceID;
    }    
}
