package algo.graph.reduction;

import de.tu_berlin.coga.container.priority.MinHeap;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.coga.graph.Edge;
import de.tu_berlin.coga.container.collection.IdentifiableCollection;
import de.tu_berlin.coga.graph.Node;
import de.tu_berlin.coga.netflow.ds.network.DynamicNetwork;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * @author <a href='mailto:Yan.Qi@asu.edu'>Yan Qi</a>
 * @version $Revision: 430 $
 * @latest $Date: 2008-07-27 16:31:56 -0700 (Sun, 27 Jul 2008) $
 */
public class YenDijkstra {
  // Input
  DynamicNetwork graph = null;
  NetworkFlowModel networkFlowModel;
  IdentifiableIntegerMapping<Edge> costs;

  // Intermediate variables
  Set<Node> determinedNodes = new HashSet<>();
  MinHeap<Node, Double> candidateNodes;

  Map<Node, Double> startNodeDistanceIndex = new HashMap<>();

  Map<Node, Node> predecessorIndex = new HashMap<>();

  /**
   * @param graph
   */
  public YenDijkstra( final DynamicNetwork graph, NetworkFlowModel orig, IdentifiableIntegerMapping<Edge> cost ) {
    this.graph = graph;
    this.costs = cost;
    this.networkFlowModel = orig;
  }

  /**
   * Clear intermediate variables.
   */
  public void clear() {
    determinedNodes.clear();
    candidateNodes = new MinHeap<>( 1 );
    startNodeDistanceIndex.clear();
    predecessorIndex.clear();
  }

  /**
   * Returns the distance in terms of the start vertex.
   * @return the distance in terms of the start vertex
   */
  public Map<Node, Double> getStartNodeDistanceIndex() {
    return startNodeDistanceIndex;
  }

  /**
   * Returns the index of the predecessors of vertices.
   * @return the index of the predecessors of vertices
   */
  public Map<Node, Node> getPredecessorIndex() {
    return predecessorIndex;
  }

  /**
   * Construct a tree rooted at "root" with the shortest paths to the other vertices.
   * @param root
   */
  public void computeShortestPathTree( Node root ) {
    determineShortestPaths( root, null, true );
  }

  /**
   * Construct a flower rooted at "root" with the shortest paths from the other vertices.
   *
   * @param root
   */
  public void getShortestPathFlower( Node root ) {
    determineShortestPaths( null, root, false );
  }

  /**
   * Do the work
   */
  protected void determineShortestPaths( Node source,
          Node sink, boolean sourceToSink ) {
    // 0. clean up variables
    clear();

    // 1. initialize members
    Node startNode = sourceToSink ? sink : source;
    Node endNode = sourceToSink ? source : sink;
    startNodeDistanceIndex.put( endNode, 0d );
    //start_vertex.setWeight(0d);
    candidateNodes.insert( endNode, 0.0 );

    // 2. start searching for the shortest path
    while( !candidateNodes.isEmpty() ) {
      Node candidate = candidateNodes.extractMin().getObject();

      if( candidate.equals( startNode ) ) {
        break;
      }

      determinedNodes.add( candidate );

      improveToNode( candidate, sourceToSink );
    }
  }

  /**
   * Update the distance from the source to the concerned vertex.
   * @param node
   */
  private void improveToNode( Node node, boolean sourceToSink ) {

    // 1. get the neighboring vertices
    IdentifiableCollection<Node> neighborNodes = sourceToSink
            ? graph.temp_adjacentNodes( node ) : graph.temp_predNodes( node );

    // 2. update the distance passing on current vertex
    for( Node neighbor : neighborNodes ) {
      // 2.1 skip if visited before
      if( determinedNodes.contains( neighbor ) ) {
        continue;
      }

      // 2.2 calculate the new distance
      double distance = startNodeDistanceIndex.containsKey( node )
              ? startNodeDistanceIndex.get( node ) : Double.MAX_VALUE;
                        //System.out.println("vertex: " + vertex + "curr_adj_vertex:" + cur_adjacent_vertex);

      distance += sourceToSink ? costs.get( graph.getEdge( node, neighbor ) )
              : costs.get( graph.getEdge( neighbor, node ) );

      // 2.3 update the distance if necessary
      if( !startNodeDistanceIndex.containsKey( neighbor )
              || startNodeDistanceIndex.get( neighbor ) > distance ) {
        startNodeDistanceIndex.put( neighbor, distance );

        predecessorIndex.put( neighbor, node );

        //cur_adjacent_vertex.setWeight(distance);
        candidateNodes.insert( neighbor, distance );
      }
    }
  }

  /**
   * Note that, the source should not be as same as the sink! (we could extend this later on)
   *
   * @param source_vertex
   * @param sink
   * @return
   */
  public YenPath get_shortest_path( Node source, Node sink ) {
    determineShortestPaths( source, sink, true );
    //
    List<Node> nodes = new Vector<>();
    double min = Integer.MAX_VALUE;
    double weight = startNodeDistanceIndex.containsKey( sink )
            ? startNodeDistanceIndex.get( sink ) : Double.MAX_VALUE;
    //System.out.println("weight: " + weight);
    if( weight != Double.MAX_VALUE ) {
      Node currentNode = sink;
      do {
        nodes.add( currentNode );
        Node n = currentNode;
        currentNode = predecessorIndex.get( currentNode );
        if( networkFlowModel.getEdgeCapacity( networkFlowModel.getEdge( currentNode, n ) ) < min ) {
          min = networkFlowModel.getEdgeCapacity( networkFlowModel.getEdge( n, currentNode ) );
        }
      } while( currentNode != null && currentNode != source );
      //
      nodes.add( source );
      Collections.reverse( nodes );
    }
    //
    return new YenPath( nodes, weight, min );
  }

	/// for updating the cost
  /**
   * Calculate the distance from the target vertex to the input vertex using forward star form. (FLOWER)
   *
   * @param node
   */
  public YenPath updateCostForward( Node node ) {
    double cost = Double.MAX_VALUE;
    // 1. get the set of successors of the input vertex
    IdentifiableCollection<Node> adjacentVertices = graph.temp_adjacentNodes( node );

    // 2. make sure the input vertex exists in the index
    if( !startNodeDistanceIndex.containsKey( node ) ) {
      startNodeDistanceIndex.put( node, Double.MAX_VALUE );
    }

    // 3. update the distance from the root to the input vertex if necessary
    for( Node currentVertex : adjacentVertices ) {
      // 3.1 get the distance from the root to one successor of the input vertex
      double distance = startNodeDistanceIndex.containsKey( currentVertex )
              ? startNodeDistanceIndex.get( currentVertex ) : Double.MAX_VALUE;

      // 3.2 calculate the distance from the root to the input vertex
      distance += costs.get( networkFlowModel.getEdge( node, currentVertex ) );

      // 3.3 update the distance if necessary
      double nodeCost = startNodeDistanceIndex.get( node );

      if( nodeCost > distance ) {
        startNodeDistanceIndex.put( node, distance );
        predecessorIndex.put( node, currentVertex );
        cost = distance;
      }
    }

    // 4. create the sub_path if exists
    YenPath subPath = null;
    if( cost < Double.MAX_VALUE ) {
      subPath = new YenPath();
      subPath.setWeight( cost );
      List<Node> nodes = subPath.getNodes();
      nodes.add( node );

      Node selectedVertex = predecessorIndex.get( node );
      while( selectedVertex != null ) {
        nodes.add( selectedVertex );
        selectedVertex = predecessorIndex.get( selectedVertex );
      }
    }

    return subPath;
  }

  /**
   * Correct costs of successors of the input vertex using backward star form. (FLOWER)
   *
   * @param node
   */
  public void correctCostBackward( Node node ) {
    // 1. initialize the list of vertex to be updated
    List<Node> nodes = new LinkedList<>();
    nodes.add( node );

    // 2. update the cost of relevant precedents of the input vertex
    while( !nodes.isEmpty() ) {
      Node currentNode = nodes.remove( 0 );
      double cost = startNodeDistanceIndex.get( currentNode );

      IdentifiableCollection<Node> predecessorNodes = graph.temp_predNodes( currentNode );
      for( Node predecessorNode : predecessorNodes ) {
        double predecessorCosts = startNodeDistanceIndex.containsKey( predecessorNode )
                ? startNodeDistanceIndex.get( predecessorNode ) : Double.MAX_VALUE;

        double updatedCosts = cost + costs.get( graph.getEdge( predecessorNode, currentNode ) );
        if( predecessorCosts > updatedCosts ) {
          startNodeDistanceIndex.put( predecessorNode, updatedCosts );
          predecessorIndex.put( predecessorNode, currentNode );
          nodes.add( predecessorNode );
        }
      }
    }
  }

}
