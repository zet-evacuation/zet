/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
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
