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
 * EarliestArrivalFlowProblem.java
 *
 */
package algo.graph.dynamicflow.eat;

import algo.graph.dynamicflow.DynamicTransshipmentProblem;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.network.AbstractNetwork;
import ds.mapping.IdentifiableIntegerMapping;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Groß
 */
public class EarliestArrivalFlowProblem extends DynamicTransshipmentProblem {

//    private IdentifiableIntegerMapping<Edge> edgeCapacities;
//		private AbstractNetwork network;
//    private IdentifiableIntegerMapping<Edge> transitTimes;

//    private IdentifiableIntegerMapping<Node> nodeCapacities;
    private Node sink;
    private List<Node> sources;
//    private IdentifiableIntegerMapping<Node> supplies;
//    private int timeHorizon;
    private int totalSupplies;
    
    public EarliestArrivalFlowProblem(IdentifiableIntegerMapping<Edge> edgeCapacities, AbstractNetwork network, IdentifiableIntegerMapping<Node> nodeCapacities, Node sink, List<Node> sources, int timeHorizon, IdentifiableIntegerMapping<Edge> transitTimes, IdentifiableIntegerMapping<Node> supplies) {
			super( edgeCapacities, network, nodeCapacities, timeHorizon, transitTimes, supplies );
			//this.edgeCapacities = edgeCapacities;
      //  this.network = network;
 //       this.nodeCapacities = nodeCapacities;
        this.sink = sink;
        this.sources = sources;
 //       this.supplies = supplies;
 //       this.timeHorizon = timeHorizon;
      //  this.transitTimes = transitTimes;
        for (Node source : sources) {
            totalSupplies += supplies.get(source);
        }
    }

	public EarliestArrivalFlowProblem( DynamicTransshipmentProblem dyn ) {
		super( dyn.getEdgeCapacities(), dyn.getNetwork(), dyn.getNodeCapacities(), dyn.getTimeHorizon(), dyn.getTransitTimes(), dyn.getSupplies() );

            sources = new LinkedList<>();
            Node supersink = null;
            for (Node node : getNetwork().nodes()) {
                if ( getSupplies().get(node) > 0) {
                    sources.add(node);
                    totalSupplies += getSupplies().get(node);
                }
                if (getSupplies().get(node) < 0) supersink = node;
            }
						this.sink = supersink;
	}
		
		

		/**
		 * Sets a new time horizon for the instance. Use this if a time horizon
		 * has changed, for example if an estimator has been used.
		 * @param timeHorizon the new time horizon
		 */
//		public void setTimeHorizon( int timeHorizon ) {
//			this.timeHorizon = timeHorizon;
//		}

//    public IdentifiableIntegerMapping<Edge> getEdgeCapacities() {
//        return edgeCapacities;
//    }

//    public AbstractNetwork getNetwork() {
//        return network;
//    }

//    public IdentifiableIntegerMapping<Node> getNodeCapacities() {
//        return nodeCapacities;
//    }

    public Node getSink() {
        return sink;
    }

    public List<Node> getSources() {
        return sources;
    }

//    public IdentifiableIntegerMapping<Node> getSupplies() {
//        return supplies;
//    }    
//    
//    public int getTimeHorizon() {
//        return timeHorizon;
//    }

    public int getTotalSupplies() {
        return totalSupplies;
    }

//    public IdentifiableIntegerMapping<Edge> getTransitTimes() {
//        return transitTimes;
//    }

	public String toString() {
		return "EarliestArrivalFlowProblem{\n" + "edgeCapacities=" + getEdgeCapacities() + "\n, network=" + getNetwork() + "\n, nodeCapacities=" + getNodeCapacities() + "\n, sink=" + sink + "\n, sources=" + sources + "\n, supplies=" + getSupplies() + "\n, timeHorizon=" + getTimeHorizon() + "\n, totalSupplies=" + totalSupplies + "\n, transitTimes=" + getTransitTimes() + '}';
	}
}
