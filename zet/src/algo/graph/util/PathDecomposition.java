/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
package algo.graph.util;

import ds.graph.Edge;
import ds.mapping.IdentifiableIntegerMapping;
import ds.mapping.IdentifiableObjectMapping;
import ds.graph.network.AbstractNetwork;
import ds.graph.Node;
import ds.graph.flow.PathBasedFlow;
import ds.graph.flow.StaticPathFlow;
import java.util.List;

/**
 *
 * @author Joscha
 */
public class PathDecomposition {        
	
	private static IdentifiableObjectMapping<Node,Boolean> visited;
    
    private static boolean DFS(AbstractNetwork network, Node x, List<Node> sinks, StaticPathFlow path){             
    	visited.set(x, true);
        boolean sackgasse = true;
        for (Node y : network.successorNodes(x)){
            //if (!getNodesOnPath(path).contains(y)){
            if (!visited.get(y)){
                path.getPath().addLastEdge(network.getEdge(x, y));
                if (!sinks.contains(y)){
                    sackgasse = DFS(network,y,sinks,path);
                    if (pathEndsOnSink(network,sinks,path)) {
                    	break;
                    }
                } else {
                	sackgasse = false;
                    break;
                }
            }
        }
        if (sackgasse && !path.edges().empty()) path.getPath().removeLastEdge();
     //   visited.set(x,false);
        return sackgasse;
    }
    
    private static boolean pathEndsOnSink(AbstractNetwork network, List<Node> sinks, StaticPathFlow path){
        if (!path.edges().empty()){
            for (Node t : sinks){
                Edge last = path.lastEdge();
                Node NodeA = last.start();
                Node NodeB = last.end();
                if (network.adjacentNodes(NodeA).contains(t) || network.adjacentNodes(NodeB).contains(t)){
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }
    
/*    private static ArrayList<Node> getNodesOnPath(StaticPathFlow path){
        ArrayList<Node> nodeList = new ArrayList<Node>();
        for (Edge edge : path.edges()){
            nodeList.add(edge.start());
            nodeList.add(edge.end());
        }
        return nodeList;
    }*/
    
    private static int getMinPathCapacity(StaticPathFlow path, IdentifiableIntegerMapping<Edge> flow){
        int minCapacity = Integer.MAX_VALUE;
        for (Edge edge : path.edges()){
            if (flow.get(edge) < minCapacity) minCapacity = flow.get(edge);
        }
        return minCapacity;
    }
    
    private static void updateCapacities(AbstractNetwork network, StaticPathFlow path, IdentifiableIntegerMapping<Edge> flow){
        int minCapacity = getMinPathCapacity(path,flow);        
        for (Edge edge : path.edges()){
            flow.decrease(edge, minCapacity);
            if (flow.get(edge) == 0) network.setHidden(edge, true);
        }
    }
    
    private static int saveFlowAmount(int givenSupply, StaticPathFlow path, IdentifiableIntegerMapping<Edge> flow){
        int minCapacity = getMinPathCapacity(path,flow);
        /* Calculate minimum because there may only leave the source as much flow as it has supply. */
        int result = Math.min(minCapacity, givenSupply);
        path.setAmount(result);
        return result;
    }
    
    private static void saveFlowAmount(StaticPathFlow path, IdentifiableIntegerMapping<Edge> flow){
        int minCapacity = getMinPathCapacity(path,flow);
        path.setAmount(minCapacity);
    }
    
    private static void restoreNetwork(AbstractNetwork network){        
        network.showAllEdges();
    }
    
/*    private static int getMaxNetworkCapacity(AbstractNetwork network, IdentifiableIntegerMapping<Edge> flow){
        int maxCapacity = 0;
        for (Edge edge : network.edges()){
            if (flow.get(edge) > maxCapacity) maxCapacity = flow.get(edge);
        }
        return maxCapacity;
    }*/
    
    private static void capacityScaling(AbstractNetwork network, IdentifiableIntegerMapping<Edge> flow, int minCapacity){
        restoreNetwork(network);
        for (Edge edge : network.edges()){
            if (flow.get(edge) < minCapacity){
                network.setHidden(edge, true);
            }
        }
    }
    
/*    public static StaticPathFlow calculateThickesPath(AbstractNetwork network, List<Node> sources, List<Node> sinks, IdentifiableIntegerMapping<Edge> flow){        
        StaticPathFlow path = new StaticPathFlow();
        for (int i=getMaxNetworkCapacity(network,flow); i>=0; i--){
            capacityScaling(network,flow,i);                        
            for (Node s : sources){                
                DFS(network,s,sinks,path);                
                if (!path.edges().empty()){
                    saveFlowAmount(path,flow);          
                    break;
                }
            }                
            if (!path.edges().empty()) break;
        }            
        restoreNetwork(network);
        return path;
    }*/
            
    public static PathBasedFlow calculatePathDecomposition(AbstractNetwork network, IdentifiableIntegerMapping<Node> supplies, List<Node> sources, List<Node> sinks, IdentifiableIntegerMapping<Edge> startFlow){
    	if (startFlow == null){
    		throw new IllegalArgumentException("The given startflow is null.");
    	}
    	IdentifiableIntegerMapping<Node> restSupplies = new IdentifiableIntegerMapping<Node>(supplies.getDomainSize());
    	for (Node node:network.nodes()){
    		restSupplies.set(node,supplies.get(node));
    	}
        PathBasedFlow pathDecomposition = new PathBasedFlow();        
        IdentifiableIntegerMapping<Edge> flow = (IdentifiableIntegerMapping<Edge>)startFlow.clone();
        visited = new IdentifiableObjectMapping<Node,Boolean>(network.nodes().size(), Boolean.class);
    	for (Node n : network.nodes())
    		visited.set(n, false);
        StaticPathFlow path = new StaticPathFlow();
        capacityScaling(network,flow,1);
        for (Node s : sources){
            DFS(network,s,sinks,path);
            while (!path.edges().empty() && restSupplies.get(s) > 0){     
            	int decreasedBy = saveFlowAmount(restSupplies.get(s),path,flow);            	
                restSupplies.decrease(s,decreasedBy);
                updateCapacities(network,path,flow);                
                pathDecomposition.addPathFlow(path);
                path = new StaticPathFlow();
            	for (Node n : network.nodes())
            		visited.set(n, false);
                DFS(network,s,sinks,path);
                if (!path.edges().empty() && restSupplies.get(s) == 0){
                	for (Node n : network.nodes())
                		visited.set(n, false);
                    path = new StaticPathFlow();
                }
            }
        }
        restoreNetwork(network);
        return pathDecomposition;
    }
    
    public static PathBasedFlow calculatePathDecomposition(AbstractNetwork network, List<Node> sources, List<Node> sinks, IdentifiableIntegerMapping<Edge> startFlow){
        PathBasedFlow pathDecomposition = new PathBasedFlow();        
        IdentifiableIntegerMapping<Edge> flow = (IdentifiableIntegerMapping<Edge>)startFlow.clone();
        visited = new IdentifiableObjectMapping<Node,Boolean>(network.nodes().size(), Boolean.class);
    	for (Node n : network.nodes())
    		visited.set(n, false);
        StaticPathFlow path = new StaticPathFlow();
        capacityScaling(network,flow,1);
        for (Node s : sources){
            DFS(network,s,sinks,path);
            while (!path.edges().empty()){     
            	saveFlowAmount(path,flow);            	
                updateCapacities(network,path,flow);                
                pathDecomposition.addPathFlow(path);
                path = new StaticPathFlow();
            	for (Node n : network.nodes())
            		visited.set(n, false);
                DFS(network,s,sinks,path);
            }
        }
        restoreNetwork(network);
        return pathDecomposition;
    }
}