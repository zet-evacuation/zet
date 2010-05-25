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
 * Fujishige.java
 */

package algo.graph.staticflow.maxflow;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.graph.problem.RationalMaxFlowProblem;
import ds.graph.flow.RationalMaxFlow;
import ds.graph.DoubleMap;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.ResidualGraph;
import java.util.List;
import java.util.Collections;


/**
 *
 * @author Sebastian Schenker
 */

public class Fujishige extends Algorithm<RationalMaxFlowProblem,RationalMaxFlow> {

    private final static double EPSILON = 0.00001;
    
    private ResidualGraph resGraph;
    
    private double maxflowvalue;
    
    private void initializeDatastructures() {
        int mnodeid=0,medgeid=0;
        for(Node n: getProblem().getNetwork().nodes())
            if(n.id()>mnodeid)
                mnodeid = n.id();
        for(Edge e: getProblem().getNetwork().edges())
            if(e.id()>medgeid)
                medgeid = e.id();
        resGraph = new ResidualGraph(getProblem().getNetwork(),getProblem().getCapacities(),mnodeid,medgeid);
    }
    
    @Override
    public RationalMaxFlow runAlgorithm(RationalMaxFlowProblem problem) {
        initializeDatastructures();
        runFujishige();
        return new RationalMaxFlow(getProblem(),getFlow());
    }
    
        
    
    
    public ResidualGraph getResidualGraph() {
        return resGraph;
    }
    
    private void setMaxFlowValue(double val) {
        maxflowvalue = val;
    }
    
    public double getMaxFlowValue() {
        return maxflowvalue;
    }
    
    public DoubleMap<Edge> getFlow() {
        return resGraph.getFlow();
    }
    
    private DoubleMap<Node> setBeta(Double d) {
        DoubleMap<Node> betamap = new DoubleMap<Node>(getProblem().getNetwork().numberOfNodes());
        for(Node n : getProblem().getNetwork().nodes())
            if(n == getProblem().getSink()) 
                betamap.set(n, d);
            else   
                betamap.set(n, 0.0);
        return betamap;
    }
    
    private void runFujishige() {
        double delta;
        DoubleMap<Node> betaMap;
        double y;
        while(true) {
            MAordering MAord = new MAordering(getResidualGraph(),getProblem().getSource(),getProblem().getSink(),getResidualGraph().getResidualCapacities());
            MAord.computeMAordering();
            
            delta = Double.MAX_VALUE;
            List<Node> ordering = MAord.getMAordering();
            ordering.remove(0);
            for(Node node: ordering) {
                if(MAord.getDemand(node) < delta)
                    delta = MAord.getDemand(node);
            }
            //System.out.println("DELTA = " + delta);
            if(delta < EPSILON)
                break;
            else
                betaMap = setBeta(delta);
            
            Collections.reverse(ordering);
            for(Node node: ordering) {
                for(Edge edge : MAord.getEdgeList(node)) {
                    y = Math.min(betaMap.get(node), resGraph.getResidualCapacities().get(edge));
                    if(y>EPSILON) {
                        resGraph.augmentFlow(edge, y);
                        betaMap.decrease(node, y);
                        betaMap.increase(edge.start(), y);
                    }
                }
            }
            
        }
        //System.out.println("FUJIOUT");
        double flowvalue = 0.0;
        for(Edge e: resGraph.getGraph().outgoingEdges(getProblem().getSource())) {
            flowvalue += resGraph.getFlow().get(e);
        }
        for(Edge e: resGraph.getGraph().incomingEdges(getProblem().getSource())) {
            flowvalue -= resGraph.getFlow().get(e);
        }
        setMaxFlowValue(flowvalue);
        
    }
    
}