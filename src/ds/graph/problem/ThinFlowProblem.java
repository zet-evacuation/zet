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
/* ThinFlowProblem.java */


/**
 *
 * @author Sebastian Schenker
 */


package ds.graph.problem;

import ds.graph.DoubleMap;
import ds.graph.DynamicNetwork;
import ds.graph.Edge;
import ds.graph.Node;

public class ThinFlowProblem {

    private double flowAmount;
    private DynamicNetwork graph;
    private DoubleMap<Edge> capacities;
    private DoubleMap<Node> nodedemands;
    private Node sink;
    private Node source;
    private DoubleMap<Edge> edgeflowvalues;
    private DoubleMap<Node> nodelabels;
    
    
    public ThinFlowProblem(DynamicNetwork graph, DoubleMap<Edge> capacities, 
            Node source, Node sink, double flowvalue) {
        setGraph(graph);
        setCapacities(capacities);
        setSource(source);
        setSink(sink);
        flowAmount = flowvalue;
        //edgeflowvalues = new DoubleMap<Edge>(graph.numberOfNodes());
        //nodelabels = new DoubleMap<Node>(graph.numberOfNodes());
        nodedemands = new DoubleMap<Node>(graph.numberOfNodes());
        nodedemands.set(source, flowvalue);
        nodedemands.set(sink, -1*flowvalue);
    
    }
    
    public ThinFlowProblem(DynamicNetwork graph, DoubleMap<Edge> capacities,
            Node source, Node sink) {
        setGraph(graph);
        setCapacities(capacities);
        setSource(source);
        setSink(sink);
        nodedemands = new DoubleMap<Node>(graph.numberOfNodes());
        nodedemands.set(source,1.0);
        nodedemands.set(sink,-1.0);
    }
    
    public void setNodeLabels(DoubleMap<Node> nodemap) {
        nodelabels = nodemap;
    }
    
    public void setEdgeFlowValues(DoubleMap<Edge> edgemap) {
        edgeflowvalues = edgemap;
    }
    
    public DoubleMap<Edge> getCapacities() {
        return capacities;
    }
    
    public void setCapacities(DoubleMap<Edge> value) {
        capacities = value;
    }
    
    public double getFlowAmount() {
        return flowAmount;
    }
    
    public void setFlowAmount(double value) {
        flowAmount = value;
    }
    
    public DynamicNetwork getGraph() {
        return graph;
    }
    
    public void setGraph(DynamicNetwork value) {
        graph = value;
    }
    
    public Node getSink() {
        return sink;
    }
    
    public void setSink(Node value) {
        sink = value;
    }
    
    public Node getSource() {
        return source;
    }
    
    public void setSource(Node value) {
        source = value;
    }
    
    public DoubleMap<Node> getNodeLabels() {
       return nodelabels;
    }
    
    public DoubleMap<Edge> getEdgeFlowValues() {
        return edgeflowvalues;
    }
    
    public void setNodeLabel(Node n, double val) {
        nodelabels.set(n, val);
      }        
    
    public void setEdgeFlowValue(Edge e, double val) {
        edgeflowvalues.set(e,val);
    }
    
    public DoubleMap<Node> getNodeDemands() {
        return nodedemands;
       }

    public double getNodeDemand(Node n) {
        return nodedemands.get(n);
    }
    
    public void setNodeDemands(DoubleMap<Node> ndemands) {
        nodedemands = ndemands;
    }
    
    public void setNodeDemand(Node n, double val) {
        nodedemands.set(n, val);
    }
    
}