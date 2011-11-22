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
    
    private int[] edgeCapacities;
    private int[] edgeEndIDs;
    private int[] edgeStartIDs;
    private int numberOfEdges;
    private int numberOfNodes;
    private int sinkID;
    private int sourceID;

    public RawMaximumFlowProblem(int numberOfNodes, int numberOfEdges) {
        this(numberOfNodes,numberOfEdges,null,null,null,-1,-1);
    }    
    
    public RawMaximumFlowProblem(int numberOfNodes, int numberOfEdges, int[] edgeCapacities, int[] edgeEndIDs, int[] edgeStartIDs, int sinkID, int sourceID) {
        this.numberOfNodes = numberOfNodes;
        this.numberOfEdges = numberOfEdges;
        this.edgeCapacities = edgeCapacities;
        this.edgeEndIDs = edgeEndIDs;
        this.edgeStartIDs = edgeStartIDs;
        this.sinkID = sinkID;
        this.sourceID = sourceID;
    }

    public int[] getEdgeCapacities() {
        return edgeCapacities;
    }

    public int[] getEdgeEndIDs() {
        return edgeEndIDs;
    }

    public int[] getEdgeStartIDs() {
        return edgeStartIDs;
    }

    public int getNumberOfEdges() {
        return numberOfEdges;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public int getSinkID() {
        return sinkID;
    }

    public int getSourceID() {
        return sourceID;
    }    
}
