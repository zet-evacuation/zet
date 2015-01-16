/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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

package algo.graph.exitassignment;

import org.zetool.algorithm.shortestpath.Dijkstra;
import org.zetool.netflow.classic.maxflow.PathDecomposition;
import org.zetool.netflow.classic.maxflow.PushRelabelHighestLabelGlobalGapRelabelling;
import org.zetool.netflow.classic.mincost.SuccessiveShortestPath;
import org.zetool.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import org.zetool.graph.Edge;
import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.graph.Node;
import org.zetool.netflow.ds.flow.MaximumFlow;
import org.zetool.netflow.ds.flow.PathBasedFlow;
import org.zetool.netflow.ds.structure.StaticFlowPath;
import org.zetool.graph.DefaultDirectedGraph;
import org.zetool.netflow.classic.problems.MaximumFlowProblem;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.DirectedGraph;
import org.zetool.netflow.classic.maxflow.FordFulkerson;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Groß
 */
public class MinimumCostTransshipmentExitAssignment extends Algorithm<NetworkFlowModel, ExitAssignment> implements Assignable {

  @Override
  protected ExitAssignment runAlgorithm( NetworkFlowModel model ) {
    ExitAssignment solution = new ExitAssignment( model.graph().nodes() );

    DirectedGraph network = model.graph();
    IdentifiableCollection<Node> sinks = network.predecessorNodes( model.getSupersink() );

    Dijkstra dijkstra = new Dijkstra( network, model.transitTimes(), null, true );
    int[][] distances = new int[network.nodeCount()][network.nodeCount()];
    for( Node sink : sinks ) {
      dijkstra.setSource( sink );
      dijkstra.run();
      for( Node source : model.getSources() ) {
        distances[source.id()][sink.id()] = dijkstra.getDistance( source );
      }
    }

    DefaultDirectedGraph reducedNetwork = new DefaultDirectedGraph( sinks.size() + model.getSources().size(), sinks.size() * model.getSources().size() );
    IdentifiableIntegerMapping<Edge> reducedTransitTimes = new IdentifiableIntegerMapping<>( sinks.size() * model.getSources().size() );
    IdentifiableIntegerMapping<Edge> reducedCapacities = new IdentifiableIntegerMapping<>( sinks.size() * model.getSources().size() );
    IdentifiableIntegerMapping<Node> reducedBalances = new IdentifiableIntegerMapping<>( sinks.size() + model.getSources().size() );
    List<Node> reducedSources = new LinkedList<>();
    List<Node> reducedSinks = new LinkedList<>();
    int index = 0;
    for( Node source : model.getSources() ) {
      int sinkIndex = 0;
      for( Node sink : sinks ) {
        if( distances[source.id()][sink.id()] == 0 ) {
          // No path between the source and sink pair in the given network.
          continue;
        }
        Edge edge = reducedNetwork.createAndSetEdge( reducedNetwork.getNode( index ), reducedNetwork.getNode( model.getSources().size() + sinkIndex ) );
        reducedTransitTimes.set( edge, distances[source.id()][sink.id()] );
        
        System.out.println( "From " + index + " to " + (model.getSources().size() + sinkIndex) +" with cost " + distances[source.id()][sink.id()] );
        
        reducedCapacities.set( edge, Integer.MAX_VALUE );
        sinkIndex++;
      }
      reducedBalances.set( reducedNetwork.getNode( index ), model.currentAssignment().get( source ) );
      reducedSources.add( reducedNetwork.getNode( index ) );
      index++;
    }
    int totalCapacities = 0;
    IdentifiableIntegerMapping<Node> estimatedCapacities = new IdentifiableIntegerMapping<>( model.graph().nodes() );
    for( Node sink : sinks ) {
      estimatedCapacities.set( sink, estimateCapacityByMaximumFlow( model, sink ) );
      totalCapacities += estimatedCapacities.get( sink );
    }
    int totalSupplies = 0;
    for( Node source : model.getSources() ) {
      totalSupplies += model.currentAssignment().get( source );
    }
    int sinkIndex = 0;
    for( Node sink : sinks ) {
      reducedBalances.set( reducedNetwork.getNode( index + sinkIndex ), (int)-Math.ceil( estimatedCapacities.get( sink ) * 1.0 / totalCapacities * totalSupplies ) );
      reducedSinks.add( reducedNetwork.getNode( index + sinkIndex ) );
      sinkIndex++;
    }
    SuccessiveShortestPath ssp = new SuccessiveShortestPath( reducedNetwork, reducedBalances, reducedCapacities, reducedTransitTimes, true );
    ssp.run();

    PathBasedFlow pathDecomposition = PathDecomposition.calculatePathDecomposition( reducedNetwork, reducedBalances, reducedSources, reducedSinks, ssp.getFlow() );
    LinkedList<Node> sinks2 = new LinkedList<>();
    for( Node sink : sinks ) {
      sinks2.add( sink );
    }
    int costs = 0;
    for( StaticFlowPath path : pathDecomposition ) {
      Edge edge = path.firstEdge();
      Node source = model.getSources().get( edge.start().id() );
      Node sink = sinks2.get( edge.end().id() - model.getSources().size() );
      for( int i = 0; i < path.getAmount(); i++ ) {
        solution.assignIndividualToExit( source, sink );
        System.out.println( "Assign from " + source + " to " + sink + " with cost " + distances[source.id()][sink.id()] );
        costs += distances[source.id()][sink.id()];
      }
    }
    System.out.println( "Costs of min cost assignment: " + costs );
    return solution;
  }

  protected int estimateCapacityByIncomingEdges( NetworkFlowModel model, Node sink ) {
    IdentifiableCollection<Node> sinks = model.graph().predecessorNodes( model.getSupersink() );
    int result = 0;
    for( Edge edge : model.graph().incomingEdges( sink ) ) {
      if( sinks.contains( edge.start() ) ) {
        continue;
      }
      result += model.getEdgeCapacity( edge );
    }
    return result;
  }

  protected int estimateCapacityByMaximumFlow( NetworkFlowModel model, Node sink ) {
    IdentifiableCollection<Node> sinks = model.graph().predecessorNodes( model.getSupersink() );
    IdentifiableIntegerMapping<Edge> newCapacities = new IdentifiableIntegerMapping<>( model.edgeCapacities() );
    for( Node s : sinks ) {
      for( Edge edge : model.graph().outgoingEdges( s ) ) {
        //if (sinks.contains(edge.start())) {
        newCapacities.set( edge, 0 );
        //}
      }
    }
    MaximumFlowProblem problem = new MaximumFlowProblem( model.graph(), newCapacities, model.getSources(), sink );
    //Algorithm<MaximumFlowProblem, MaximumFlow> algorithm = new PushRelabelHighestLabelGlobalGapRelabelling();
    Algorithm<MaximumFlowProblem, MaximumFlow> algorithm = new FordFulkerson();
    algorithm.setProblem( problem );
    algorithm.run();
    return algorithm.getSolution().getFlowValue();
  }

  /**
   * Returns the calculated exit assignment.
   * @return the calculated exit assignment.
   */
  @Override
  public ExitAssignment getExitAssignment() {
    return getSolution();
  }
}
