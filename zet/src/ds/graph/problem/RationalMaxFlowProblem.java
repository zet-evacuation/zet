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
 * RationalMaxFlowProblem.java
 *
 */

package ds.graph.problem;

import ds.graph.Edge;
import ds.graph.DoubleMap;
import ds.graph.DynamicNetwork;
import ds.graph.Node;


/**
 *
 * @author Sebastian Schenker
 */

public class RationalMaxFlowProblem {
    
    private DynamicNetwork network;
    private DoubleMap<Edge> capacities;
    private Node source;
    private Node sink;

    public RationalMaxFlowProblem(DynamicNetwork network, DoubleMap<Edge> capacities, Node source, Node sink) {
        this.network = network;
        this.capacities = capacities;
        this.source = source;
        this.sink = sink;
    }

    
    public DoubleMap<Edge> getCapacities() {
        return capacities;
    }

    public void setCapacities(DoubleMap<Edge> capacities) {
        this.capacities = capacities;
    }

    public DynamicNetwork getNetwork() {
        return network;
    }

    public void setNetwork(DynamicNetwork network) {
        this.network = network;
    }
    
    public Node getSink() {
        return sink;        
    }
    
    public void setSink(Node sink) {
        this.sink = sink;
    }

     
    public Node getSource() {
        return source;
    }
    
    public void setSource(Node source) {
        this.source = source;
    }    
    
        
}
