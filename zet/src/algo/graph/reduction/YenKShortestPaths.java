/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algo.graph.reduction;

import de.tu_berlin.coga.container.priority.MinHeap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import de.tu_berlin.coga.graph.Node;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.coga.graph.Edge;
import de.tu_berlin.coga.netflow.ds.network.DynamicNetwork;
import java.util.*;
        

/**
 * @author <a href='mailto:Yan.Qi@asu.edu'>Yan Qi</a>
 * @version $Revision: 783 $
 * @latest $Id: YenTopKShortestPathsAlg.java 783 2009-06-19 19:19:27Z qyan $
 */
public class YenKShortestPaths
{
        private DynamicNetwork _graph = null;
        private NetworkFlowModel orig_graph;
	// intermediate variables
	private List<YenPath> _result_list = new Vector<>();
	private Map<YenPath, Node> _path_derivation_vertex_index = new HashMap<>();
	//private Queue<YenPath> _path_candidates = new LinkedList<>();
        MinHeap<YenPath, Double> _path_candidates = new MinHeap<>(1);
	
	// the ending vertices of the paths
	private Node _source_vertex = null;
	private Node _target_vertex = null;
	
	// variables for debugging and testing
	private int _generated_path_num = 0;
        private int length_bound = 0;
	
	/**
	 * Default constructor.
	 * 
	 * @param graph
	 * @param k
	 */
	public YenKShortestPaths(NetworkFlowModel o_graph)
	{
		this(o_graph, null, null);		
	}
	
	/**
	 * Constructor 2
	 * 
	 * @param graph
	 * @param source_vt
	 * @param target_vt
	 */
	public YenKShortestPaths(NetworkFlowModel graph, 
			Node source_vt, Node target_vt)
	{
		if(graph == null){
			throw new IllegalArgumentException("A NULL graph object occurs!");
		}
		_graph = new DynamicNetwork((DynamicNetwork)graph.graph());
                orig_graph = graph;
		_source_vertex = source_vt;
		_target_vertex = target_vt;
		_init(0);
	}
	
	/**
	 * Initiate members in the class. 
	 */
	private void _init(int top_k)
	{
		clear();
		// get the shortest path by default if both source and target exist
		if(_source_vertex != null && _target_vertex != null)
		{
			YenPath shortest_path = get_shortest_path(_source_vertex, _target_vertex);  
			if(!shortest_path.get_vertices().isEmpty())
			{
                                _path_candidates.insert(shortest_path, shortest_path.get_weight());
				_path_derivation_vertex_index.put(shortest_path, _source_vertex);				
			}
                        length_bound = (int)shortest_path.get_weight() + (int)Math.ceil(top_k/shortest_path.get_capacity()) -1;
		}
                
	}
	
	/**
	 * Clear the variables of the class. 
	 */
	public void clear()
	{
		_path_candidates = new MinHeap<>(1);
		_path_derivation_vertex_index.clear();
		_result_list.clear();
		_generated_path_num = 0;
	}
	
	/**
	 * Obtain the shortest path connecting the source and the target, by using the
	 * classical Dijkstra shortest path algorithm. 
	 * 
	 * @param source_vt
	 * @param target_vt
	 * @return
	 */
	public YenPath get_shortest_path(Node source_vt, Node target_vt)
	{
		YenDijkstra dijkstra_alg = new YenDijkstra(_graph,orig_graph,orig_graph.transitTimes());
		return dijkstra_alg.get_shortest_path(source_vt, target_vt);
	}
	
	/**
	 * Check if there exists a path, which is the shortest among all candidates.  
	 * 
	 * @return
	 */
	public boolean has_next()
	{
		return !_path_candidates.isEmpty();
	}
	
	/**
	 * Get the shortest path among all that connecting source with target. 
	 * 
	 * @return
	 */
	public YenPath next()
	{
		//3.1 prepare for removing vertices and arcs
		YenPath cur_path = _path_candidates.extractMin().getObject();
                double Min = Double.MAX_VALUE;
                for (int i=0; i<cur_path.get_vertices().size()-1; i++)
                {
                    Node n = cur_path.get_vertices().get(i);
                    Node n2 = cur_path.get_vertices().get(i+1);
                    double cap = orig_graph.getEdgeCapacity(orig_graph.getEdge(n, n2));
                    if (cap < Min)
                    {
                        Min = cap;
                        cur_path.set_capacity(cap);
                    }
                }
		_result_list.add(cur_path);
		Node cur_derivation = _path_derivation_vertex_index.get(cur_path);
		int cur_path_hash = 
			cur_path.get_vertices().subList(0, cur_path.get_vertices().indexOf(cur_derivation)).hashCode();
		
		int count = _result_list.size();
		
		//3.2 remove the vertices and arcs in the graph
		for(int i=0; i<count-1; ++i)
		{
			YenPath cur_result_path = _result_list.get(i);
							
			int cur_dev_vertex_id = 
				cur_result_path.get_vertices().indexOf(cur_derivation);
			
			if(cur_dev_vertex_id < 0) continue;

			// Note that the following condition makes sure all candidates should be considered. 
			/// The algorithm in the paper is not correct for removing some candidates by mistake. 
			int path_hash = cur_result_path.get_vertices().subList(0, cur_dev_vertex_id).hashCode();
			if(path_hash != cur_path_hash) continue;

			Node cur_succ_vertex = cur_result_path.get_vertices().get(cur_dev_vertex_id+1);
                        
			
                        Edge e = orig_graph.getEdge(cur_derivation, cur_succ_vertex);
                        _graph.remove_edge_temp(e);
			//_graph.remove_edge(new Pair<Integer,Integer>(
			//		cur_derivation.id(), cur_succ_vertex.id()));
		}
		
		int path_length = cur_path.get_vertices().size();
		List<Node> cur_path_vertex_list = cur_path.get_vertices();
		for(int i=0; i<path_length-1; ++i)
		{
			_graph.remove_node_temp(cur_path_vertex_list.get(i));
                        Edge e = orig_graph.getEdge(cur_path_vertex_list.get(i), cur_path_vertex_list.get(i+1));
			_graph.remove_edge_temp(e);
		}
		
		//3.3 calculate the shortest tree rooted at target vertex in the graph
		YenDijkstra reverse_tree = new YenDijkstra(_graph,orig_graph,orig_graph.transitTimes());
		reverse_tree.get_shortest_path_flower(_target_vertex);

		//3.4 recover the deleted vertices and update the cost and identify the new candidate results
		boolean is_done = false;
		for(int i=path_length-2; i>=0 && !is_done; --i)
		{
			//3.4.1 get the vertex to be recovered
			Node cur_recover_vertex = cur_path_vertex_list.get(i);			
			_graph.add_node_temp(cur_recover_vertex);
			
			//3.4.2 check if we should stop continuing in the next iteration
			if(cur_recover_vertex.id() == cur_derivation.id()) 
			{
				is_done = true;
			}
			
			//3.4.3 calculate cost using forward star form
			YenPath sub_path = reverse_tree.update_cost_forward(cur_recover_vertex);
                        
			//3.4.4 get one candidate result if possible
			if(sub_path != null) 
			{
				++_generated_path_num;
				
				//3.4.4.1 get the prefix from the concerned path
				double cost = 0; 
				List<Node> pre_path_list = new Vector<>();
				reverse_tree.correct_cost_backward(cur_recover_vertex);
				
				for(int j=0; j<path_length; ++j)
				{
					Node cur_vertex = cur_path_vertex_list.get(j);
					if(cur_vertex.id() == cur_recover_vertex.id())
					{
						j=path_length;
					}else
					{
                                                Edge e = orig_graph.getEdge(cur_path_vertex_list.get(j), cur_path_vertex_list.get(j+1));
                                                double trans = orig_graph.getTransitTime(e);
						cost += trans;
						pre_path_list.add(cur_vertex);
					}
				}
				pre_path_list.addAll(sub_path.get_vertices());

				//3.4.4.2 compose a candidate
				sub_path.set_weight(cost+sub_path.get_weight());
				sub_path.get_vertices().clear();
				sub_path.get_vertices().addAll(pre_path_list);
				
				//3.4.4.3 put it in the candidate pool if new
				if(!_path_derivation_vertex_index.containsKey(sub_path))
				{
					//_path_candidates.add(sub_path);
                                        _path_candidates.insert(sub_path, sub_path.get_weight());
					_path_derivation_vertex_index.put(sub_path, cur_recover_vertex);
				}
			}
			
			//3.4.5 restore the edge
			Node succ_vertex = cur_path_vertex_list.get(i+1); 
                        Edge e = orig_graph.getEdge(cur_recover_vertex, succ_vertex);
                        _graph.add_edge_temp(e);	
			
			//3.4.6 update cost if necessary
			double cost_1 = orig_graph.getTransitTime(e)
				+ reverse_tree.get_start_vertex_distance_index().get(succ_vertex);
			
			if(reverse_tree.get_start_vertex_distance_index().get(cur_recover_vertex) >  cost_1)
			{
				reverse_tree.get_start_vertex_distance_index().put(cur_recover_vertex, cost_1);
				reverse_tree.get_predecessor_index().put(cur_recover_vertex, succ_vertex);
				reverse_tree.correct_cost_backward(cur_recover_vertex);
			}
		}
		
		//3.5 restore everything
		_graph.recover_temp_removed_edges();
		_graph.recover_temp_removed_nodes();
		
		//
		return cur_path;
	}
	
	/**
	 * Get the top-K shortest paths connecting the source and the target.  
	 * This is a batch execution of top-K results.
	 * 
	 * @param source
	 * @param sink
	 * @param top_k
	 * @return
	 */
	public List<YenPath> get_shortest_paths(Node source_vertex, 
			Node target_vertex, int top_k)
	{
		_source_vertex = source_vertex;
		_target_vertex = target_vertex;
		
		_init(top_k);
		int count = 0;
                // while count < top_k 
		while(has_next() && _path_candidates.getMin().getObject().get_weight() <= length_bound)
		{                    
			next();                  
			++count;
		}
		
		return _result_list;
	}
		
	/**
	 * Return the list of results generated on the whole.
	 * (Note that some of them are duplicates)
	 * @return
	 */
	public List<YenPath> get_result_list()
	{
		return _result_list;
	}

	/**
	 * The number of distinct candidates generated on the whole. 
	 * @return
	 */
	public int get_cadidate_size()
	{
		return _path_derivation_vertex_index.size();
	}

	public int get_generated_path_size()
	{
		return _generated_path_num;
	}
}