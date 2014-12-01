package algo.graph.reduction;

import de.tu_berlin.coga.container.priority.MinHeap;
import de.tu_berlin.coga.graph.DynamicNetwork;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import de.tu_berlin.coga.graph.Node;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.coga.graph.Edge;
import java.util.Objects;

/**
 * @author <a href='mailto:Yan.Qi@asu.edu'>Yan Qi</a>
 * @version $Revision: 783 $
 * @latest $Id: YenTopKShortestPathsAlg.java 783 2009-06-19 19:19:27Z qyan $
 */
public class YenKShortestPaths {
  private DynamicNetwork graph = null;
  private NetworkFlowModel networkFlowModel;
  // intermediate variables
  private List<YenPath> resultList = new Vector<>();
  private Map<YenPath, Node> pathDerivationNodeIndex = new HashMap<>();
  private MinHeap<YenPath, Double> pathCandidates = new MinHeap<>( 1 );

  // the ending vertices of the paths
  private Node source = null;
  private Node sink = null;

  // variables for debugging and testing
  private int generatedPathsCount = 0;
  private int lenghBound = 0;

  /**
   * @param networkModel
   */
  public YenKShortestPaths( NetworkFlowModel networkModel ) {
    this( networkModel, null, null );
  }

  /**
   * @param graph input network flow model
   * @param source
   * @param sink
   */
  public YenKShortestPaths( NetworkFlowModel graph, Node source, Node sink ) {
    Objects.requireNonNull( graph, "A NULL graph object occurs!" );
    this.graph = new DynamicNetwork( (DynamicNetwork)graph.graph() );
    networkFlowModel = graph;
    this.source = source;
    this.sink = sink;
    init( 0 );
  }

  /**
   * Initiate members in the class.
   */
  private void init( int top_k ) {
    clear();
    // get the shortest path by default if both source and target exist
    if( source != null && sink != null ) {
      YenPath shortestPath = getShortestPath( source, sink );
      if( !shortestPath.getNodes().isEmpty() ) {
        pathCandidates.insert( shortestPath, shortestPath.getWeight() );
        pathDerivationNodeIndex.put( shortestPath, source );
      }
      lenghBound = (int)shortestPath.getWeight() + (int)Math.ceil( top_k / shortestPath.getCapacity() ) - 1;
    }

  }

  /**
   * Clear the variables of the class.
   */
  public void clear() {
    pathCandidates = new MinHeap<>( 1 );
    pathDerivationNodeIndex.clear();
    resultList.clear();
    generatedPathsCount = 0;
  }

  /**
   * Obtain the shortest path connecting the source and the target, by using the classical Dijkstra shortest path
   * algorithm.
   *
   * @param source_vt
   * @param target_vt
   * @return
   */
  public YenPath getShortestPath( Node source_vt, Node target_vt ) {
    YenDijkstra dijkstra_alg = new YenDijkstra( graph, networkFlowModel, networkFlowModel.transitTimes() );
    return dijkstra_alg.get_shortest_path( source_vt, target_vt );
  }

  /**
   * Check if there exists a path, which is the shortest among all candidates.
   *
   * @return
   */
  public boolean hasNext() {
    return !pathCandidates.isEmpty();
  }

  /**
   * Get the shortest path among all that connecting source with target.
   *
   * @return
   */
  public YenPath next() {
    //3.1 prepare for removing vertices and arcs
    YenPath currentPath = pathCandidates.extractMin().getObject();
    double Min = Double.MAX_VALUE;
    for( int i = 0; i < currentPath.getNodes().size() - 1; i++ ) {
      Node n = currentPath.getNodes().get( i );
      Node n2 = currentPath.getNodes().get( i + 1 );
      double cap = networkFlowModel.getEdgeCapacity( networkFlowModel.getEdge( n, n2 ) );
      if( cap < Min ) {
        Min = cap;
        currentPath.setCapacity( cap );
      }
    }
    resultList.add( currentPath );
    Node currentDeviation = pathDerivationNodeIndex.get( currentPath );
    int currentPathHash
            = currentPath.getNodes().subList( 0, currentPath.getNodes().indexOf( currentDeviation ) ).hashCode();

    int count = resultList.size();

    //3.2 remove the vertices and arcs in the graph
    for( int i = 0; i < count - 1; ++i ) {
      YenPath currentResultPath = resultList.get( i );

      int cur_dev_vertex_id
              = currentResultPath.getNodes().indexOf( currentDeviation );

      if( cur_dev_vertex_id < 0 ) {
        continue;
      }

			// Note that the following condition makes sure all candidates should be considered.
      /// The algorithm in the paper is not correct for removing some candidates by mistake.
      int pathHash = currentResultPath.getNodes().subList( 0, cur_dev_vertex_id ).hashCode();
      if( pathHash != currentPathHash ) {
        continue;
      }

      Node currentSuccessorNode = currentResultPath.getNodes().get( cur_dev_vertex_id + 1 );

      Edge e = networkFlowModel.getEdge( currentDeviation, currentSuccessorNode );
      graph.remove_edge_temp( e );
    }

    int path_length = currentPath.getNodes().size();
    List<Node> currentPathNodes = currentPath.getNodes();
    for( int i = 0; i < path_length - 1; ++i ) {
      graph.remove_node_temp( currentPathNodes.get( i ) );
      Edge e = networkFlowModel.getEdge( currentPathNodes.get( i ), currentPathNodes.get( i + 1 ) );
      graph.remove_edge_temp( e );
    }

    //3.3 calculate the shortest tree rooted at target vertex in the graph
    YenDijkstra reverseShortestPathsTree = new YenDijkstra( graph, networkFlowModel, networkFlowModel.transitTimes() );
    reverseShortestPathsTree.getShortestPathFlower( sink );

    //3.4 recover the deleted vertices and update the cost and identify the new candidate results
    boolean is_done = false;
    for( int i = path_length - 2; i >= 0 && !is_done; --i ) {
      //3.4.1 get the vertex to be recovered
      Node currentRecoverNode = currentPathNodes.get( i );
      graph.add_node_temp( currentRecoverNode );

      //3.4.2 check if we should stop continuing in the next iteration
      if( currentRecoverNode.id() == currentDeviation.id() ) {
        is_done = true;
      }

      //3.4.3 calculate cost using forward star form
      YenPath subPath = reverseShortestPathsTree.updateCostForward( currentRecoverNode );

      //3.4.4 get one candidate result if possible
      if( subPath != null ) {
        ++generatedPathsCount;

        //3.4.4.1 get the prefix from the concerned path
        double cost = 0;
        List<Node> prefixPathList = new Vector<>();
        reverseShortestPathsTree.correctCostBackward( currentRecoverNode );

        for( int j = 0; j < path_length; ++j ) {
          Node currentNode = currentPathNodes.get( j );
          if( currentNode.id() == currentRecoverNode.id() ) {
            j = path_length;
          } else {
            Edge e = networkFlowModel.getEdge( currentPathNodes.get( j ), currentPathNodes.get( j + 1 ) );
            double trans = networkFlowModel.getTransitTime( e );
            cost += trans;
            prefixPathList.add( currentNode );
          }
        }
        prefixPathList.addAll( subPath.getNodes() );

        //3.4.4.2 compose a candidate
        subPath.setWeight( cost + subPath.getWeight() );
        subPath.getNodes().clear();
        subPath.getNodes().addAll( prefixPathList );

        //3.4.4.3 put it in the candidate pool if new
        if( !pathDerivationNodeIndex.containsKey( subPath ) ) {
          //_path_candidates.add(sub_path);
          pathCandidates.insert( subPath, subPath.getWeight() );
          pathDerivationNodeIndex.put( subPath, currentRecoverNode );
        }
      }

      //3.4.5 restore the edge
      Node successorNode = currentPathNodes.get( i + 1 );
      Edge e = networkFlowModel.getEdge( currentRecoverNode, successorNode );
      graph.add_edge_temp( e );

      //3.4.6 update cost if necessary
      double cost_1 = networkFlowModel.getTransitTime( e )
              + reverseShortestPathsTree.getStartNodeDistanceIndex().get( successorNode );

      if( reverseShortestPathsTree.getStartNodeDistanceIndex().get( currentRecoverNode ) > cost_1 ) {
        reverseShortestPathsTree.getStartNodeDistanceIndex().put( currentRecoverNode, cost_1 );
        reverseShortestPathsTree.getPredecessorIndex().put( currentRecoverNode, successorNode );
        reverseShortestPathsTree.correctCostBackward( currentRecoverNode );
      }
    }

    //3.5 restore everything
    graph.recover_temp_removed_edges();
    graph.recover_temp_removed_nodes();

    //
    return currentPath;
  }

  /**
   * Get the top-K shortest paths connecting the source and the target. This is a batch execution of top-K results.
   *
   * @param source the start node
   * @param sink the end node of the shortest paths
   * @param k the number of shortest paths to be returned
   * @return
   */
  public List<YenPath> getShortestPaths( Node source,
          Node sink, int k ) {
    this.source = source;
    this.sink = sink;

    init( k );
    int count = 0;
    // while count < top_k
    while( hasNext() && pathCandidates.getMin().getObject().getWeight() <= lenghBound ) {
      next();
      ++count;
    }

    return resultList;
  }

  /**
   * Return the list of results generated on the whole. (Note that some of them are duplicates)
   * @return
   */
  public List<YenPath> getGeneratedPaths() {
    return resultList;
  }

  /**
   * The number of distinct candidates generated on the whole.
   * @return
   */
  public int getCandidateCount() {
    return pathDerivationNodeIndex.size();
  }

  public int getGeneratedPathsCount() {
    return generatedPathsCount;
  }
}
