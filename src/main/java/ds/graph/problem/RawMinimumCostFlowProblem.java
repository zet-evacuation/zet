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
 * @author Martin Groß
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
