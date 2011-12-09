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

package ds.graph.problem;

import ds.graph.DoubleMap;
import ds.graph.network.DynamicNetwork;
import ds.graph.Edge;
import ds.mapping.IdentifiableDoubleMapping;
import ds.graph.Node;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Sebastian Schenker
 */
public class NashFlowProblem {

    private double initcap = 0.0;
    private DynamicNetwork graph;
    private IdentifiableDoubleMapping<Edge> capacities;
    private IdentifiableDoubleMapping<Edge> transitTimes;
    //private DoubleMap<Edge> capacities;
    //private DoubleMap<Edge> transitTimes;
    private Node sink;
    private Node source;
    private Node origSource;
    private List<Node> origSources = new ArrayList<Node>();
    private Edge edgeSourceToOrigSource;
    private DoubleMap<Node> nodelabelderivations;
    private HashMap<Node,Double> sourceDemands;
    private List<Edge> edgesToOrigSources = new ArrayList<Edge>();
    
    public NashFlowProblem() {
    }
    
    public NashFlowProblem(DynamicNetwork graph, IdentifiableDoubleMapping<Edge> capacities,
        IdentifiableDoubleMapping<Edge> transit, Node oldsource, Node sink) {
        DynamicNetwork graphWithNewSource = new DynamicNetwork(graph);
        Node newSource = new Node(graph.numberOfNodes());
        Edge newEdge = new Edge(graph.numberOfEdges(),newSource,oldsource);
        edgeSourceToOrigSource = newEdge;
        graphWithNewSource.addNode(newSource);
        graphWithNewSource.addEdge(newEdge);
        //DoubleMap<Edge> newCapacities = new DoubleMap(capacities,Double.MAX_VALUE);
        //DoubleMap<Edge> newTransitTimes = new DoubleMap(transit,0.0);
        IdentifiableDoubleMapping<Edge> newCapacities = new IdentifiableDoubleMapping(capacities,Double.MAX_VALUE);
        IdentifiableDoubleMapping<Edge> newTransitTimes = new IdentifiableDoubleMapping(transit,0.0);
        setGraph(graphWithNewSource);
        setCapacities(newCapacities);
        setTransitTimes(newTransitTimes);
        setOrigSource(oldsource);
        setSource(newSource);
        setSink(sink);
    }
    
    public NashFlowProblem(DynamicNetwork graph, IdentifiableDoubleMapping<Edge> capacities,
        IdentifiableDoubleMapping<Edge> transit, Node oldsource, Node sink, double initcapacity) {
        DynamicNetwork graphWithNewSource = new DynamicNetwork(graph);
        Node newSource = new Node(graph.numberOfNodes());
        Edge newEdge = new Edge(graph.numberOfEdges(),newSource,oldsource);
        edgeSourceToOrigSource = newEdge;
        graphWithNewSource.addNode(newSource);
        graphWithNewSource.addEdge(newEdge);
        //DoubleMap<Edge> newCapacities = new DoubleMap(capacities,initcapacity);
        //DoubleMap<Edge> newTransitTimes = new DoubleMap(transit,0.0);
        IdentifiableDoubleMapping<Edge> newCapacities = new IdentifiableDoubleMapping(capacities,initcapacity);
        IdentifiableDoubleMapping<Edge> newTransitTimes = new IdentifiableDoubleMapping(transit,0.0);
        setGraph(graphWithNewSource);
        setCapacities(newCapacities);
        setTransitTimes(newTransitTimes);
        setOrigSource(oldsource);
        setSource(newSource);
        setSink(sink);
        initcap = initcapacity;
        
    }
    
    public NashFlowProblem(DynamicNetwork graph, IdentifiableDoubleMapping<Edge> capacities,
            IdentifiableDoubleMapping<Edge> transit, Node source, Node sink, boolean test) {
        setGraph(graph);
        setCapacities(capacities);
        setTransitTimes(transit);
        setSource(source);
        setSink(sink);
        initcap = Double.MAX_VALUE;
    }
    
    public NashFlowProblem(DynamicNetwork graph, IdentifiableDoubleMapping<Edge> capacities,
            IdentifiableDoubleMapping<Edge> transit, HashMap<Node,Double> sourceDemands, Node sink) {
        
        Node SuperSource = new Node(graph.numberOfNodes());
        graph.addNode(SuperSource);
        for(Node origsource : sourceDemands.keySet()) {
            Edge newEdge = new Edge(graph.numberOfEdges(),SuperSource,origsource);
            graph.addEdge(newEdge);
            edgesToOrigSources.add(newEdge);
            capacities.set(newEdge,Double.MAX_VALUE);
            transit.set(newEdge,Double.MAX_VALUE);
            origSources.add(origsource);
        } 
        setSource(SuperSource);
        setSink(sink);
        setGraph(graph);
        setCapacities(capacities);
        setTransitTimes(transit);
    }
    
    public HashMap<Node,Double> getSourceDemands() {
        return sourceDemands;
    }
    
    public List<Node> getOriginalSources() {
        return origSources;
    }
    
    public List<Edge> getEdgesToOrigSources() {
        return edgesToOrigSources;
    }
    
    public Edge getEdgeSourceToOrigSource() {
        return edgeSourceToOrigSource;
    }
    
    public void setNodeLabelDerivations(DoubleMap<Node> thinflowNodeLabels) {
      nodelabelderivations = thinflowNodeLabels;
    }
    
    public void setCapacityForEdgeFromSourceToOrigSource(double value) throws Exception {
        if(graph.outgoingEdges(source).size() != 1) 
            throw new Exception("added source has more than one outgoing edge");
        capacities.set(graph.outgoingEdges(source).first(), value);
    }
    
    
    public DoubleMap<Node> getNodeLabelDerivations() {
        return nodelabelderivations;
    }
    
    public void setNodeDerivationLabel(Node n, double label) {
        getNodeLabelDerivations().set(n, label);
    }
    
    public IdentifiableDoubleMapping<Edge> getCapacities() {
        return capacities;
    }
    
    public double getEdgeCapacity(Edge edge) {
        return capacities.get(edge);
    }
    
    public Double getEdgeTransitTime(Edge e) {
        return transitTimes.get(e);
    }
    
    public void setTransitTimes(IdentifiableDoubleMapping<Edge> value) {
        transitTimes = value;
    }
    
    public IdentifiableDoubleMapping<Edge> getTransitTimes() {
        return transitTimes;
    }
    
    public void setCapacities(IdentifiableDoubleMapping<Edge> value) {
        capacities = value;
    }
    
    public double getInitCap() {
        return initcap;
    }
    
    public void setInitCap(double value) {
        initcap = value;
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
    
    public void setOrigSource(Node value) {
        origSource = value;
    }
    
    public Node getOrigSource() {
        return origSource;
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
    
    
}