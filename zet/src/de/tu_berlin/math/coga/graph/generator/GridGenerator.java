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

package de.tu_berlin.math.coga.graph.generator;

import de.tu_berlin.coga.container.mapping.Identifiable;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import de.tu_berlin.coga.graph.Node;
import de.tu_berlin.coga.graph.DefaultDirectedGraph;
import de.tu_berlin.coga.graph.DirectedGraph;
import java.util.Random;

/**
 *
 */
public class GridGenerator {

  public static DirectedGraph generateGridGraph( int width, int height ) {
    DefaultDirectedGraph network = new DefaultDirectedGraph( width * height, 4 * width * height - 2 * (width + height) );
    for( int row = 0; row < height; row++ ) {
      for( int column = 1; column < width; column++ ) {
        network.createAndSetEdge( network.getNode( row * width + column - 1 ),
                network.getNode( row * width + column ) );
        network.createAndSetEdge( network.getNode( row * width + column ),
                network.getNode( row * width + column - 1 ) );
      }
    }
    for( int column = 0; column < width; column++ ) {
      for( int row = 1; row < height; row++ ) {
        network.createAndSetEdge( network.getNode( (row - 1) * width + column ),
                network.getNode( row * width + column ) );
        network.createAndSetEdge( network.getNode( row * width + column ),
                network.getNode( (row - 1) * width + column ) );
      }
    }
    return network;
  }

  public static DirectedGraph generateDualGridGraph( int width, int height ) {
    width--;
    height--;
    DefaultDirectedGraph network = new DefaultDirectedGraph( width * height + 2, 2 * width * height + (width + height) );
    Node source = network.getNode( network.nodeCount() - 2 );
    Node sink = network.getNode( network.nodeCount() - 1 );
    for( int row = 0; row < height; row++ ) {
      for( int column = 1; column < width; column++ ) {
        network.createAndSetEdge( network.getNode( row * width + column - 1 ), network.getNode( row * width + column ) );
      }
    }
    for( int column = 0; column < width; column++ ) {
      for( int row = 1; row < height; row++ ) {
        network.createAndSetEdge( network.getNode( (row - 1) * width + column ), network.getNode( row * width + column ) );
      }
    }
    for( int column = 0; column < width; column++ ) {
      network.createAndSetEdge( source, network.getNode( column ) );
    }
    for( int row = 0; row < height; row++ ) {
      network.createAndSetEdge( source, network.getNode( row * width + width - 1 ) );
    }
    for( int column = 0; column < width; column++ ) {
      network.createAndSetEdge( network.getNode( (height - 1) * width + column ), sink );
    }
    for( int row = 0; row < height; row++ ) {
      network.createAndSetEdge( network.getNode( row * width ), sink );
    }
    return network;
  }

  public static <T extends Identifiable> IdentifiableIntegerMapping<T>
        createCostFunction( int width, int height, DefaultDirectedGraph network, IdentifiableIntegerMapping<T> capacities ) {
    IdentifiableIntegerMapping<T> costs = null;
    Node source = network.getNode( network.nodeCount() - 2 );
    Node sink = network.getNode( network.nodeCount() - 1 );
    for( int row = 0; row < height; row++ ) {
      for( int column = 1; column < width; column++ ) {
         //network.createAndSetEdge(network.getNode(row * width + column - 1), network.getNode(row * width + column));
        //capacities.get(identifiableObject);
      }
    }
    for( int column = 0; column < width; column++ ) {
      for( int row = 1; row < height; row++ ) {
        network.createAndSetEdge( network.getNode( (row - 1) * width + column ),
                network.getNode( row * width + column ) );
      }
    }
    for( int column = 0; column < width; column++ ) {
      network.createAndSetEdge( source, network.getNode( column ) );
    }
    for( int row = 0; row < height; row++ ) {
      network.createAndSetEdge( source, network.getNode( row * width + width - 1 ) );
    }
    for( int column = 0; column < width; column++ ) {
      network.createAndSetEdge( network.getNode( (height - 1) * width + column ), sink );
    }
    for( int row = 0; row < height; row++ ) {
      network.createAndSetEdge( network.getNode( row * width ), sink );
    }
    return costs;
  }

  public static <T extends Identifiable> IdentifiableIntegerMapping<T>
        generateMappingWithUniformlyDistributedFunctionValues( Iterable<T> domain, int min, int max ) {
    long seed = System.nanoTime();
    System.out.println( "Seed: " + seed );
    return generateMappingWithUniformlyDistributedFunctionValues( domain, min, max, seed );
  }

  public static <T extends Identifiable> IdentifiableIntegerMapping<T>
        generateMappingWithUniformlyDistributedFunctionValues( Iterable<T> domain, int min, int max, long seed ) {
    int largestID = 0;
    for( T x : domain ) {
      if( largestID < x.id() ) {
        largestID = x.id();
      }
    }
    IdentifiableIntegerMapping<T> mapping = new IdentifiableIntegerMapping<>( largestID + 1 );
    Random rng = new Random( seed );
    for( T x : domain ) {
      mapping.set( x, rng.nextInt( max - min + 1 ) + min );
    }
    return mapping;
  }

}
