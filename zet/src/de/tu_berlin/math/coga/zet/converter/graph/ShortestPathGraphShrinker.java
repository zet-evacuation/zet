/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;


import algo.graph.shortestpath.DijkstraWithRationalDistances;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.common.util.Level;
import ds.collection.ListSequence;
import ds.graph.Edge;
import ds.graph.Forest;
import ds.graph.IdentifiableCollection;
import ds.graph.MinSpanningTree;
import ds.graph.Node;
import ds.graph.NodeRectangle;
import ds.graph.network.DynamicNetwork;
import ds.graph.problem.MinSpanningTreeProblem;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Marlen Schwengfelder
 */
public class ShortestPathGraphShrinker extends Algorithm<NetworkFlowModel,NetworkFlowModel>{
    public NetworkFlowModel minspanmodel;
    public MinSpanningTreeProblem minspanprob;
    public MinSpanningTree spantree;
    private Map<Edge,Double> costs;
    public IdentifiableCollection<Edge> ForestEdges;
    public Forest forest;
    public Edge neu;
    public Edge neureverse;
    public Edge neu2;
    public int NumEdges = 0;
    IdentifiableCollection<Edge> solEdges = new ListSequence<Edge>();
    
    @Override
    protected NetworkFlowModel runAlgorithm( NetworkFlowModel problem ) {
			ZToGraphMapping originalMapping = problem.getZToGraphMapping();

			minspanmodel = new NetworkFlowModel( problem );
			ZToGraphMapping newMapping = minspanmodel.getZToGraphMapping();

			Node Super = minspanmodel.getSupersink();
			newMapping.setNodeSpeedFactor( Super, 1 );
			newMapping.setNodeRectangle( Super, new NodeRectangle( 0, 0, 0, 0 ) );
			newMapping.setFloorForNode( Super, -1 );
			
			for (Node node: problem )
			{
					if (node.id()!= 0)
					{
							minspanmodel.setNodeCapacity(node, problem.getNodeCapacity(node));
							newMapping.setNodeSpeedFactor(node, originalMapping.getNodeSpeedFactor(node));
							newMapping.setNodeUpSpeedFactor(node, originalMapping.getUpNodeSpeedFactor(node));
							newMapping.setNodeDownSpeedFactor(node, originalMapping.getDownNodeSpeedFactor(node));   
					}
			}

			costs = new HashMap<>(problem.numberOfEdges());
			for (Edge edge: problem.graph().edges())
			{
					costs.put(edge, (double)problem.getTransitTime(edge));                   
			}

			DijkstraWithRationalDistances dijkstra = new DijkstraWithRationalDistances((DynamicNetwork)problem.graph(), costs, problem.getSupersink());
			dijkstra.run();
			DynamicNetwork netw = dijkstra.getShortestPathGraph();

			System.out.println("Number of Original Edges: " + problem.numberOfEdges());

			for (Edge edge: netw.edges() )
			{
				Edge create = new Edge(NumEdges++, edge.start(), edge.end());
				solEdges.add(create);
			}

			for (Edge sinkedge: problem.graph().incidentEdges(problem.getSupersink()))
			{
				if (sinkedge.start() == problem.getSupersink())
				{
					Edge add = new Edge(NumEdges++, Super, sinkedge.end());
					Edge add2 = new Edge(NumEdges++, sinkedge.end(), Super);
					solEdges.add(add);
					solEdges.add(add2);
				}
				else
				{
					Edge add = new Edge(NumEdges++, Super, sinkedge.start());
					Edge add2 = new Edge(NumEdges++, sinkedge.start(), Super);                       
					solEdges.add(add);
					solEdges.add(add2);
				}
			}

			for (Edge create: solEdges)
			{
				if (create.start() == Super || create.end() == Super)
				{
					minspanmodel.addEdge( create, Integer.MAX_VALUE, 0, 0 );
					newMapping.setEdgeLevel(create,Level.Higher);  
				}
				else
				{    
					minspanmodel.addEdge( create, problem.getEdgeCapacity( create), problem.getTransitTime( create), problem.getExactTransitTime( create) );
					newMapping.setEdgeLevel(create,originalMapping.getEdgeLevel(create) );  
				}
			}

			//values from mapping of original graph
			newMapping.raster = originalMapping.getRaster();
			newMapping.nodeRectangles = originalMapping.getNodeRectangles();
			newMapping.nodeFloorMapping = originalMapping.getNodeFloorMapping();
			newMapping.isDeletedSourceNode = originalMapping.isDeletedSourceNode;
			newMapping.exitName = originalMapping.exitName;

			minspanmodel.resetAssignment();
			return minspanmodel;
    }
}


