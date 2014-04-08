/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algo.graph.reduction;

import de.tu_berlin.coga.container.priority.MinHeap;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import ds.graph.Edge;
import de.tu_berlin.coga.container.collection.IdentifiableCollection;
import ds.graph.Node;
import ds.graph.network.DynamicNetwork;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import java.util.*;


/**
 * @author <a href='mailto:Yan.Qi@asu.edu'>Yan Qi</a>
 * @version $Revision: 430 $
 * @latest $Date: 2008-07-27 16:31:56 -0700 (Sun, 27 Jul 2008) $
 */
public class YenDijkstra
{
	// Input
	DynamicNetwork _graph = null;
        NetworkFlowModel orig_graph;
        IdentifiableIntegerMapping<Edge> costs;

	// Intermediate variables
	Set<Node> _determined_vertex_set = new HashSet<>();
	//PriorityQueue<Node> _vertex_candidate_queue = new PriorityQueue<>();
        MinHeap<Node, Double> _vertex_candidate_queue; 
        
	Map<Node, Double> _start_vertex_distance_index = new HashMap<>();
	
	Map<Node, Node> _predecessor_index = new HashMap<>();

	/**
	 * Default constructor.
	 * @param graph
	 */
	public YenDijkstra(final DynamicNetwork graph, NetworkFlowModel orig, IdentifiableIntegerMapping<Edge> cost)
	{
		_graph = graph;
                costs = cost;
                orig_graph = orig;
	}

	/**
	 * Clear intermediate variables.
	 */
	public void clear()
	{
		_determined_vertex_set.clear();
		_vertex_candidate_queue = new MinHeap<>(1);
		_start_vertex_distance_index.clear();
		_predecessor_index.clear();
	}

	/**
	 * Getter for the distance in terms of the start vertex
	 * 
	 * @return
	 */
	public Map<Node, Double> get_start_vertex_distance_index()
	{
		return _start_vertex_distance_index;
	}

	/**
	 * Getter for the index of the predecessors of vertices
	 * @return
	 */
	public Map<Node, Node> get_predecessor_index()
	{
		return _predecessor_index;
	}

	/**
	 * Construct a tree rooted at "root" with 
	 * the shortest paths to the other vertices.
	 * 
	 * @param root
	 */
	public void get_shortest_path_tree(Node root)
	{
		determine_shortest_paths(root, null, true);
	}
	
	/**
	 * Construct a flower rooted at "root" with 
	 * the shortest paths from the other vertices.
	 * 
	 * @param root
	 */
	public void get_shortest_path_flower(Node root)
	{
		determine_shortest_paths(null, root, false);
	}
	
	/**
	 * Do the work
	 */
	protected void determine_shortest_paths(Node source_vertex, 
			Node sink_vertex, boolean is_source2sink)
	{
		// 0. clean up variables
		clear();
		
		// 1. initialize members
		Node end_vertex = is_source2sink ? sink_vertex : source_vertex;
		Node start_vertex = is_source2sink ? source_vertex : sink_vertex;
		_start_vertex_distance_index.put(start_vertex, 0d);
		//start_vertex.set_weight(0d);
		_vertex_candidate_queue.insert(start_vertex,0.0);

		// 2. start searching for the shortest path
		while(!_vertex_candidate_queue.isEmpty())
		{
			Node cur_candidate = _vertex_candidate_queue.extractMin().getObject();

			if(cur_candidate.equals(end_vertex)) break;

			_determined_vertex_set.add(cur_candidate);

			_improve_to_vertex(cur_candidate, is_source2sink);
		}
	}

	/**
	 * Update the distance from the source to the concerned vertex.
	 * @param vertex
	 */
	private void _improve_to_vertex(Node vertex, boolean is_source2sink)
	{
                
		// 1. get the neighboring vertices 
		IdentifiableCollection<Node> neighbor_vertex_list = is_source2sink ? 
			_graph.temp_adjacentNodes(vertex)  : _graph.temp_predNodes(vertex);
                
		// 2. update the distance passing on current vertex
		for(Node cur_adjacent_vertex : neighbor_vertex_list)
		{
			// 2.1 skip if visited before
			if(_determined_vertex_set.contains(cur_adjacent_vertex)) continue;
			
			// 2.2 calculate the new distance
			double distance = _start_vertex_distance_index.containsKey(vertex)?
					_start_vertex_distance_index.get(vertex) : Double.MAX_VALUE;
                        //System.out.println("vertex: " + vertex + "curr_adj_vertex:" + cur_adjacent_vertex);
                        
			distance += is_source2sink ? costs.get( _graph.getEdge(vertex, cur_adjacent_vertex))
					: costs.get(_graph.getEdge(cur_adjacent_vertex, vertex));

			// 2.3 update the distance if necessary
			if(!_start_vertex_distance_index.containsKey(cur_adjacent_vertex) 
			|| _start_vertex_distance_index.get(cur_adjacent_vertex) > distance)
			{
				_start_vertex_distance_index.put(cur_adjacent_vertex, distance);

				_predecessor_index.put(cur_adjacent_vertex, vertex);
				
				//cur_adjacent_vertex.set_weight(distance);
				_vertex_candidate_queue.insert(cur_adjacent_vertex,distance);
			}
		}
	}
	
	/**
	 * Note that, the source should not be as same as the sink! (we could extend 
	 * this later on)
	 *  
	 * @param source_vertex
	 * @param sink_vertex
	 * @return
	 */
	public YenPath get_shortest_path(Node source_vertex, Node sink_vertex)
	{
		determine_shortest_paths(source_vertex, sink_vertex, true);
		//
		List<Node> vertex_list = new Vector<>();
                double Min = Integer.MAX_VALUE;
		double weight = _start_vertex_distance_index.containsKey(sink_vertex) ?  
			_start_vertex_distance_index.get(sink_vertex) : Double.MAX_VALUE;
                //System.out.println("weight: " + weight);
		if(weight != Double.MAX_VALUE)
		{
			Node cur_vertex = sink_vertex;
			do{
				vertex_list.add(cur_vertex);
                                Node n = cur_vertex;
				cur_vertex = _predecessor_index.get(cur_vertex);
                                if (orig_graph.getEdgeCapacity(orig_graph.getEdge(cur_vertex, n)) < Min)
                                {
                                    Min = orig_graph.getEdgeCapacity(orig_graph.getEdge(n, cur_vertex));
                                }
			}while(cur_vertex != null && cur_vertex != source_vertex);
			//
			vertex_list.add(source_vertex);
			Collections.reverse(vertex_list);
		}
		//
		return new YenPath(vertex_list, weight, Min);
	}
	
	/// for updating the cost
	
	/**
	 * Calculate the distance from the target vertex to the input 
	 * vertex using forward star form. 
	 * (FLOWER)
	 * 
	 * @param vertex
	 */
	public YenPath update_cost_forward(Node vertex)
	{
		double cost = Double.MAX_VALUE;
		// 1. get the set of successors of the input vertex
		IdentifiableCollection<Node> adj_vertex_set = _graph.temp_adjacentNodes(vertex);
            
		// 2. make sure the input vertex exists in the index
		if(!_start_vertex_distance_index.containsKey(vertex))
		{
			_start_vertex_distance_index.put(vertex, Double.MAX_VALUE);
		}
		
		// 3. update the distance from the root to the input vertex if necessary
		for(Node cur_vertex : adj_vertex_set)
		{
			// 3.1 get the distance from the root to one successor of the input vertex
			double distance = _start_vertex_distance_index.containsKey(cur_vertex)?
					_start_vertex_distance_index.get(cur_vertex) : Double.MAX_VALUE;
	
			// 3.2 calculate the distance from the root to the input vertex
			distance += costs.get(orig_graph.getEdge(vertex, cur_vertex));
			//distance += ((VariableGraph)_graph).get_edge_weight_of_graph(vertex, cur_vertex);
			
			// 3.3 update the distance if necessary 
			double cost_of_vertex = _start_vertex_distance_index.get(vertex);

			if(cost_of_vertex > distance)
			{
				_start_vertex_distance_index.put(vertex, distance);
				_predecessor_index.put(vertex, cur_vertex);
				cost = distance;
			}
		}
		
		// 4. create the sub_path if exists
		YenPath sub_path = null;
		if(cost < Double.MAX_VALUE) 
		{
			sub_path = new YenPath();
			sub_path.set_weight(cost);
			List<Node> vertex_list = sub_path.get_vertices();
			vertex_list.add(vertex);
			
			Node sel_vertex = _predecessor_index.get(vertex);
			while(sel_vertex != null)
			{
				vertex_list.add(sel_vertex);
				sel_vertex = _predecessor_index.get(sel_vertex);
			}
		}
		
		return sub_path;
	}
	
	/**
	 * Correct costs of successors of the input vertex using backward star form.
	 * (FLOWER)
	 * 
	 * @param vertex
	 */
	public void correct_cost_backward(Node vertex)
	{
		// 1. initialize the list of vertex to be updated
		List<Node> vertex_list = new LinkedList<>();
		vertex_list.add(vertex);   
		
		// 2. update the cost of relevant precedents of the input vertex
		while(!vertex_list.isEmpty())
		{
			Node cur_vertex = vertex_list.remove(0);
			double cost_of_cur_vertex = _start_vertex_distance_index.get(cur_vertex);
			
			IdentifiableCollection<Node> pre_vertex_set = _graph.temp_predNodes(cur_vertex);
			for(Node pre_vertex : pre_vertex_set)
			{
				double cost_of_pre_vertex = _start_vertex_distance_index.containsKey(pre_vertex) ?
						_start_vertex_distance_index.get(pre_vertex) : Double.MAX_VALUE;
						
				double fresh_cost = cost_of_cur_vertex + costs.get(_graph.getEdge(pre_vertex, cur_vertex));
				//double fresh_cost = cost_of_cur_vertex + ((VariableGraph)_graph).get_edge_weight_of_graph(pre_vertex, cur_vertex);
				if(cost_of_pre_vertex > fresh_cost)
				{
					_start_vertex_distance_index.put(pre_vertex, fresh_cost);
					_predecessor_index.put(pre_vertex, cur_vertex);
					vertex_list.add(pre_vertex);
				}
			}
		}
	}
	
}

