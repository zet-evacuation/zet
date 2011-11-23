/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ds.graph.problem;

/**
 *
 * @author gross
 */
public class RawMinimumCostFlowProblem {

    private int[] nodeStartIndices;
    private int[] edgeEndIDs;
    private int[] edgeCapacities;
    private int[] edgeCosts;
    private int numberOfEdges;
    private int numberOfNodes;
    private int numberOfSupply;
    private int[] supply;

    public RawMinimumCostFlowProblem(int numberOfNodes, int numberOfEdges, int numberOfSupply) {
        this(numberOfNodes, numberOfEdges, numberOfSupply, null, null, null, null, null);
    }

    public RawMinimumCostFlowProblem(int numberOfNodes, int numberOfEdges, int numberOfSupply, int[] nodeStartIndices, int[] edgeEndIDs, int[] edgeCapacities, int[] edgeCosts, int[] supply) {
        this.numberOfNodes = numberOfNodes;
        this.numberOfEdges = numberOfEdges;
        this.numberOfSupply = numberOfSupply;
        this.nodeStartIndices = nodeStartIndices;
        this.edgeEndIDs = edgeEndIDs;
        this.edgeCapacities = edgeCapacities;
        this.edgeCosts = edgeCosts;
        this.supply = supply;
    }

    public int[] getEdgeCapacities() {
        return edgeCapacities;
    }

    public int[] getEdgeCosts() {
        return edgeCosts;
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

    public int getNumberOfSupply() {
        return numberOfSupply;
    }

    public int[] getSupply() {
        return supply;
    }
}
