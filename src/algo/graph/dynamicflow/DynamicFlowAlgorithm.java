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
package algo.graph.dynamicflow;

import ds.graph.Localization;
import algo.graph.GraphAlgorithm;
import algo.graph.util.PathComposition;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.graph.Edge;
import ds.graph.flow.EdgeBasedFlowOverTime;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;

/**
 * This class represents a dynamic flow algorithm.
 * A dynamic flow algorithm is a special case of a graph algorithm
 * and therefore this class implements the <code>GraphAlgorithm</code>
 * interface. 
 */
public abstract class DynamicFlowAlgorithm extends GraphAlgorithm {

	/** Flow based result flow. */
	protected PathBasedFlowOverTime resultFlowPathBased = null;
	/** Edge based result flow. */
	protected EdgeBasedFlowOverTime resultFlowEdgeBased = null;
	/** The network used by the flow algorithm. */
	protected Network network = null;
	/** The transit times in the network. */
	protected IdentifiableIntegerMapping<Edge> transitTimes;
	/** The capacities of the edges in the network. */
	protected IdentifiableIntegerMapping<Edge> edgeCapacities;
	
	/**
	 * Creates a new algorithm object. 
	 * Can only be called for subclasses.
	 * @param network The network the dynamic flow algorithm shall work on.
	 * @param transitTimes The transit times the dynamic flow algorithm shall use.
	 * @param edgeCapacities The edge capacities the dynamic flow algorithm shall use.
	 */
	public DynamicFlowAlgorithm(Network network, IdentifiableIntegerMapping<Edge> transitTimes, IdentifiableIntegerMapping<Edge> capacities){
		this.network = network;
		this.transitTimes = transitTimes;
		this.edgeCapacities = capacities;
	}
	
	/**
	 * Returns the calculated flow as <code>PathBasedFlowOverTime</code> object.
	 * Returns null if the algorithm has not run yet, but throws an exception
	 * if it has run and only computed an edge based flow.
	 * @return the calculated flow as <code>PathBasedFlowOverTime</code> object.
	 */
	public final PathBasedFlowOverTime getResultFlowPathBased(){
		if (resultFlowPathBased != null){
			return resultFlowPathBased;
		}
		if (resultFlowEdgeBased != null){
			throw new AssertionError(Localization.getInstance (
			).getString ("algo.graph.dynmicflow.NoEdgeBasedFlowException"));
		}
		return null;
	}
	
	/**
	 * Tells whether a path based result flow is available.
	 * Returns true if the edge based result flow is available
	 * but the path based is not. 
	 * @return whether a path based result flow is available.
	 */
	public final boolean isPathBasedFlowAvailable(){
		return !(resultFlowPathBased == null && resultFlowEdgeBased != null);
	}
	
	/**
	 * Returns the calculated flow as <code>EdgeBasedFlowOverTime</code> object.
	 * If the algorithm has not run yet, null is returned. If the algorithm produced a path based flow, 
	 * the edge based flow is calculated and returned.
	 * @return the calculated flow as <code>EdgeBasedFlowOverTime</code> object.
	 */
	public final EdgeBasedFlowOverTime getResultFlowEdgeBased(){
		if (resultFlowEdgeBased != null){
			return resultFlowEdgeBased;
		}
		if (resultFlowPathBased != null){
			if (network == null){
				throw new AssertionError("The variable network is null.");
			}
			if (transitTimes == null){
				throw new AssertionError("The variable transitTimes is null.");
			}
		    PathComposition pathComposition = new PathComposition(network,transitTimes,resultFlowPathBased);	
		    pathComposition.run(); 
		    resultFlowEdgeBased = pathComposition.getEdgeFlows();
		    return resultFlowEdgeBased;
		}
		return null;

	}

}
