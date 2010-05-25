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

package algo.graph.thinflow;




import ds.graph.Node;
import ds.graph.Edge;
import java.util.HashSet;
import ds.graph.DoubleMap;
import ds.graph.DynamicNetwork;
import algo.graph.traverse.BFS;

import ds.graph.ResidualGraph;

import algo.graph.staticflow.maxflow.Fujishige;
import ds.graph.problem.RationalMaxFlowProblem;


/* SparsestCut.java */

/**
 *
 * @author Sebastian Schenker
 */

public class SparsestCut {

    private static final double EPSILON = 0.000001;
    
    private DynamicNetwork dgraph; 
    private DoubleMap<Edge> cap; //capacities
    private DoubleMap<Node> dem; //demands
    
    private HashSet<Node> mincut;
    private double congestion;
    
    private Node supersink;
    
    private DoubleMap<Edge> mincutflows;
    
    private int maxNodeidOfGraph;
    private int maxEdgeidOfGraph;
    
    public SparsestCut(DynamicNetwork gr, DoubleMap<Edge> capacities, DoubleMap<Node> demands, Node source) {
        dgraph = new DynamicNetwork(gr);
        cap = new DoubleMap(capacities);
        dem = new DoubleMap(demands);
        congestion = -1.0;
        int maxnodeid=-1;
        for(Node n: dgraph.nodes()) {
            if(n.id()>maxnodeid)
                maxnodeid = n.id();
        }
        int maxedgeid = -1;
        for(Edge e: dgraph.edges()) {
            if(e.id() > maxedgeid)
                maxedgeid = e.id();
        }
        
        
        triple tr = insertSuperSink(maxnodeid,maxedgeid);
        
        supersink = tr.getNode();
        maxNodeidOfGraph = tr.getNodeid();
        maxEdgeidOfGraph = tr.getEdgeid();
        computeSparsestCut(source);    
        
    }
  
    
    public HashSet<Node> getMinCut() {
        return mincut;
    } 
    
    public double getCongestion() {
        return congestion;
    }
    
    public double getMinCutEdgeFlowValue(Edge e) {
            return mincutflows.get(e);
        }
    
    class MinCut {
        private double maxflowvalue;
        private HashSet<Node> cutset;
        private DoubleMap<Edge> flows;
        
        public MinCut (double value, HashSet<Node> set, DoubleMap<Edge> f) {
            maxflowvalue = value;
            cutset = set;
            flows = f;
        }
        
        public double getMinCutValue() {
            return maxflowvalue;         
        }
        
        public HashSet<Node> getCutSet() {
            return cutset;
        }
        
        public DoubleMap<Edge> getFlow() {
            return flows;
        }
        
        public Double getEdgeFlow(Edge e) {
            return flows.get(e);
        }
        
        public void addNodeToCut(Node node) {
            cutset.add(node);
        }
        
    }
     
    
           
    private MinCut getCut (Node source, Node sink) {
          
          RationalMaxFlowProblem instance = new RationalMaxFlowProblem(new DynamicNetwork(dgraph),cap,source,sink);
          
          Fujishige fuji = new Fujishige();
          fuji.setProblem(instance);
          fuji.run();
          
          double maxflow = fuji.getSolution().getFlowValue(); 
          
          ResidualGraph resgraph = fuji.getResidualGraph();
          
          
          
        
          BFS reachable = new BFS(resgraph);
          
          HashSet<Node> cut;
          cut = reachable.getReachableNodes(source);
          
          
          
          return new MinCut(maxflow,cut,fuji.getFlow());
    }
 
    private class triple {
        private Node node;
        private int nodeid;
        private int edgeid;
        
        triple(Node n, int nval, int edval) {
            node = n;
            nodeid = nval;
            edgeid = edval;
        }
        
        public Node getNode() {
            return node;
        }
        
        public int getNodeid() {
            return nodeid;
        }
        
        public int getEdgeid() {
            return edgeid;
        }
        
    }
    
    
    //insert supersink connected to all "sink" nodes
     private triple insertSuperSink(int nodeid, int edgeid) {
        int mnodeid = ++nodeid;
        Node ssink = new Node(mnodeid); 
        dgraph.addNode(ssink);
        dem.set(ssink,0.0);
        int medgeid = edgeid;
        for(Node n : dgraph.nodes()) {
            
                if(n!=ssink && dem.get(n) < 0.0) //node n is a "sink" 
                {
                   
                Edge newedge = new Edge(++medgeid,n,ssink);
                
                dgraph.addEdge(newedge);
                
                double newcap = 0.0;
                
                //define capacities of new edges
                for(Edge ed : dgraph.incomingEdges(n)) {
                    newcap += cap.get(ed);
                }
                
                cap.set(newedge,newcap+1.0);
                }
                    
        }
        
        return new triple(ssink,mnodeid,medgeid);
    }
 
   
    
    private void computeSparsestCut(Node source) {
        double throughput = 1.0;
        MinCut cut;
        HashSet<Node> mincutset;
        
        while(true) {
            
            cut = getCut(source,supersink);
            
            mincutset = cut.getCutSet();
            
            double demandvalue = 0.0;
            double cutvalue = cut.getMinCutValue();
           
            Node start;
            for(Edge e : dgraph.incomingEdges(supersink)) {
                start = e.start();
                if(!mincutset.contains(start)) {
                    demandvalue += dem.get(start);
                }
                else {
                    cutvalue -= cut.getEdgeFlow(e);
                }
            }
        
        double frac;
       
        if(Math.abs((frac = cutvalue/demandvalue)- throughput) > EPSILON ) { 
            throughput = frac;
        }
        else {
            break;  
        }
        
        
        for(Edge ed : dgraph.incomingEdges(supersink)) {
            start = ed.start();
            cap.set(ed,(Math.abs(dem.get(start)*throughput)));
            
        } 
     }
    
     congestion = -1/throughput;
     mincut = mincutset; 
     mincutflows = cut.getFlow();
    
    } 
}