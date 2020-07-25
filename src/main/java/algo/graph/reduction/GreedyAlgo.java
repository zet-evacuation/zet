/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package algo.graph.reduction;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import algo.graph.spanningtree.MinSpanningTreeProblem;
import algo.graph.spanningtree.UndirectedTree;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import org.zetool.algorithm.shortestpath.Dijkstra;
import org.zetool.algorithm.shortestpath.IntegralSingleSourceShortestPathProblem;
import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.container.collection.ListSequence;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.DefaultDirectedGraph;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;

/**
 *
 * @author Marlen Schwengfelder
 */
public class GreedyAlgo extends AbstractAlgorithm<MinSpanningTreeProblem, UndirectedTree> {

  double t = 3;
  int[][] used;
  int Min = 100000;
  //int overalldist = 0;
  int NumEdges = 0;
  int count = 0;
  Edge MinEdge;
  Edge currentEdge;
  NetworkFlowModel OriginNetwork;
  DefaultDirectedGraph network;
  IdentifiableIntegerMapping<Edge> TransitForEdge;
  IdentifiableIntegerMapping<Edge> capForEdge;
  /* gives distance of nodes in current graph G'. */
  IdentifiableIntegerMapping<Edge> currentTransitForEdge;
  IdentifiableIntegerMapping<Edge> currentCapForEdge;
  Collection<Edge> origedges = new LinkedList<>();
  //stores exactTransitTimes
  HashMap<Edge, Double> extransitTimes = new HashMap<>();
  IdentifiableCollection<Edge> sortededges = new ListSequence<>();
  IdentifiableCollection<Edge> solEdges = new ListSequence<>();
  Node supersink;
  Edge supersinkedge;

  IdentifiableIntegerMapping<Edge> trans = new IdentifiableIntegerMapping<>( 1 );
  IdentifiableIntegerMapping<Edge> cap = new IdentifiableIntegerMapping<>( 1 );

  @Override
  public UndirectedTree runAlgorithm( MinSpanningTreeProblem minspan ) {
    try {
      OriginNetwork = minspan.getNetworkFlowModel();
      supersink = OriginNetwork.getSupersink();
      int numNodes = OriginNetwork.numberOfNodes();
      TransitForEdge = OriginNetwork.transitTimes();
      capForEdge = OriginNetwork.edgeCapacities();
      currentTransitForEdge = new IdentifiableIntegerMapping<>( OriginNetwork.numberOfEdges() );
      currentCapForEdge = new IdentifiableIntegerMapping<>( OriginNetwork.numberOfEdges() );

      for( Edge edge : OriginNetwork.graph().edges() ) {
        if( (edge.start() != supersink) && (edge.end() != supersink) ) {
          origedges.add( edge );
          // no edges existent --> high transit times
          currentTransitForEdge.add( edge, 100000 );
          currentCapForEdge.add( edge, 100000 );
        }
      }

      //store the exact TransitTimes
      List<Double> tr = new LinkedList<>();
      for( Edge e : origedges ) {
        tr.add( OriginNetwork.getExactTransitTime( e ) );
        extransitTimes.put( e, OriginNetwork.getExactTransitTime( e ) );
      }

      Collections.sort( tr );

      for( int i = 0; i < tr.size(); i++ ) {
        Edge found = null;
        for( Edge e : extransitTimes.keySet() ) {
          if( extransitTimes.get( e ).equals( tr.get( i ) ) && (!sortededges.contains( e )) ) {
            sortededges.add( e );
            found = e;
          }
        }
        if( found != null ) {
          extransitTimes.remove( found );
        }
      }

      /*for (int k=0; k< sortededges.size();k++)
       {
       System.out.println("Kante: " + sortededges.get(k) + "für Transitzeit: "
      + OriginNetwork.getExactTransitTime(sortededges.get(k)));
       }*/
      network = new DefaultDirectedGraph( OriginNetwork.numberOfNodes(), OriginNetwork.numberOfEdges() );
      for( Node node : OriginNetwork ) {
        network.setNode( node );
      }
      network.setEdges( OriginNetwork.graph().edges() );
      //Array stores if nodes in a certain combination are already used as an edge
      used = new int[numNodes][numNodes];
      for( int i = 0; i < numNodes; i++ ) {
        for( int j = 0; j < numNodes; j++ ) {
          used[i][j] = 0;
        }
      }
      //look at sorted edges
      while( sortededges.size() > 0 ) {
        int capacity = 0, Min = 100000;
        // Calculates distance from a node of the current considered edge to every other node
        Dijkstra dijkstra = new Dijkstra( true );
        dijkstra.setProblem(new IntegralSingleSourceShortestPathProblem(network, currentTransitForEdge, sortededges.first().end()));
        dijkstra.run();
        int dist = dijkstra.getSolution().getDistance( sortededges.first().start() );
        Node current = sortededges.first().start();
        Node next = dijkstra.getSolution().getLastEdges().get( sortededges.first().start() ).opposite( sortededges.first().start() );
        if( next.equals( sortededges.first().end() ) ) {
          Edge e = network.getEdge( current, next );
          capacity = currentCapForEdge.get( e );
        } else {
          while( next != sortededges.first().end() ) {
            Edge e = network.getEdge( current, next );
            if( currentCapForEdge.get( e ) < Min ) {
              Min = currentCapForEdge.get( e );
              capacity = Min;
            }
            current = next;
            next = dijkstra.getSolution().getLastEdges().get( current ).opposite( current );
          }
        }

        currentEdge = sortededges.first();

        if( (dist > t * TransitForEdge.get( currentEdge ))
                || (capacity > t * capForEdge.get( currentEdge ))
                || (capacity < (1 / t) * capForEdge.get( currentEdge )) ) {
          if( (capacity > t * capForEdge.get( currentEdge ))
                  || (capacity < (1 / t) * capForEdge.get( currentEdge )) ) {
                        //System.out.println("Kapazitäten wichtig");
            //System.out.println("capacity: " + capacity + "capForEdge" + capForEdge.get(currentEdge));
          }
          currentTransitForEdge.set( currentEdge, TransitForEdge.get( currentEdge ) );
          currentCapForEdge.set( currentEdge, capForEdge.get( currentEdge ) );
                    //verhindere, dass zugehoerige Rueckwaertskanten eingefuegt werden

          if( (used[currentEdge.start().id()][currentEdge.end().id()]) == 0 ) {
            Edge edge = new Edge( NumEdges++, currentEdge.start(), currentEdge.end() );
            solEdges.add( edge );
            trans.add( edge, minspan.getNetworkFlowModel().getTransitTime( currentEdge ) );
            cap.add( edge, minspan.getNetworkFlowModel().getEdgeCapacity( currentEdge ) );
            used[edge.start().id()][edge.end().id()] = 1;
            used[edge.end().id()][edge.start().id()] = 1;
            count++;
          }
        }

        sortededges.remove( currentEdge );

      }

      IdentifiableCollection<Edge> addEdges = OriginNetwork.graph().incidentEdges( supersink );
      for( Edge edge : addEdges ) {
        supersinkedge = new Edge( NumEdges++, edge.start(), edge.end() );
        solEdges.add( supersinkedge );
      }

    } catch( Exception e ) {
      System.out.println( "Fehler in runGreedyAlgo " + e.toString() );
    }

    return new UndirectedTree( solEdges );

  }
}
