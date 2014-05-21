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
package ds.graph.network;

import de.tu_berlin.coga.graph.Edge;
import ds.graph.GraphLocalization;
import de.tu_berlin.coga.container.collection.IdentifiableCollection;
import de.tu_berlin.coga.graph.Node;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A utility class for obtaining the set of adjacent nodes from a set of
 * incident edges in O(1) time.
 */
public class OppositeNodeCollection implements IdentifiableCollection<Node> {

  /**
   * The base node for which the adjacent nodes are to be returned.
   */
  protected Node node;

  /**
   * The edges incident to {@code node}.
   */
  protected IdentifiableCollection<Edge> edges;

  /**
   * Creates a new {@code OppositeNodeCollection} containing all nodes that are
   * opposite to the specified node with regard to the specified collection of
   * edges. Runtime O(1).
   *
   * @param node the base node for which the adjacent nodes are to be returned.
   * @param edges the edges incident to {@code node}.
   */
  public OppositeNodeCollection( Node node, IdentifiableCollection<Edge> edges ) {
    this.node = node;
    this.edges = edges;
  }

  /**
   * This operation is not supported by this collection.
   *
   * @return
   * @exception UnsupportedOperationException if this method is called.
   */
  @Override
  public boolean add( Node element ) {
    throw new UnsupportedOperationException( GraphLocalization.loc.getString( "ds.graph.NotSupportedException" ) );
  }

  /**
   * This operation is not supported by this collection.
   *
   * @exception UnsupportedOperationException if this method is called.
   */
  @Override
  public void remove( Node element ) {
    throw new UnsupportedOperationException( GraphLocalization.loc.getString( "ds.graph.NotSupportedException" ) );
  }

  /**
   * This operation is not supported by this collection.
   *
   * @return
   * @exception UnsupportedOperationException if this method is called.
   */
  @Override
  public Node removeLast() {
    throw new UnsupportedOperationException( GraphLocalization.loc.getString( "ds.graph.NotSupportedException" ) );
  }

  /**
   * Returns whether the specified node is contained in this collection. Runtime
   * O(degree(node)).
   *
   * @param element the node to be checked.
   * @return {@code true} if the specified node is contained in this collection,
   * false {@code otherwise}.
   */
  @Override
  public boolean contains( Node element ) {
    for( Node n : this ) {
      if( n.equals( element ) ) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns whether this {@code IdentifiableCollection} is empty. Runtime O(1).
   *
   * @return whether this {@code IdentifiableCollection} is empty.
   */
  @Override
  public boolean empty() {
    return edges.empty();
  }

  /**
   * Returns the number of nodes adjacent to the base node. Runtime
   * O(degree(node)).
   *
   * @return the number of nodes adjacent to the base node.
   */
  @Override
  public int size() {
    int sum = 0;
    for( Node n : this ) {
      sum++;
    }
    return sum;
  }

  /**
   * This operation is not supported by this collection.
   *
   * @exception UnsupportedOperationException if this method is called.
   */
  @Override
  public Node get( int id ) {
    throw new UnsupportedOperationException( GraphLocalization.loc.getString( "ds.graph.NotSupportedException" ) );
  }

  /**
   * Returns the node opposite to the base node with regard to the first edge.
   * Runtime O(1).
   *
   * @return the node opposite to the base node with regard to the first edge.
   */
  @Override
  public Node first() {
    return edges.first().opposite( node );
  }

  /**
   * Returns the node opposite to the base node with regard to the last edge.
   * Runtime O(1).
   *
   * @return the node opposite to the base node with regard to the last edge.
   */
  @Override
  public Node last() {
    return edges.last().opposite( node );
  }

  /**
   * Returns the predecessor of the specified node with regard to the order
   * implements imposed by this collections iterator. If such an element does
   * not exists, {@code null} is returned. Runtime O(degree(node)).
   *
   * @param element the node for which the predecessor is to be returned.
   * @return the predecessor of the specified node.
   */
  @Override
  public Node predecessor( Node element ) {
    Node last = null;
    for( Node n : this ) {
      if( n.equals( element ) ) {
        break;
      }
      last = n;
    }
    return last;
  }

  /**
   * Returns the successor of the specified node with regard to the order
   * implements imposed by this collections iterator. If such an element does
   * not exists, {@code null} is returned. Runtime O(degree(node)).
   *
   * @param element the node for which the successor is to be returned.
   * @return the successor of the specified node.
   */
  @Override
  public Node successor( Node element ) {
    Node last = null;
    for( Node n : this ) {
      if( last != null && last.equals( element ) ) {
        return n;
      }
    }
    return null;
  }

  /**
   * Returns an iterator iterating over this set of nodes adjacent to the base
   * nodes. The returned iterator does not return the same node twice, even if
   * the graph has multiple edges. Runtime O(1).
   *
   * @return an iterator iterating over this set of nodes adjacent to the base
   * nodes.
   */
  @Override
  public Iterator<Node> iterator() {
    return new OppositeNodeIterator( node, edges.iterator() );
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append( "[" );
    boolean empty = true;
    for( Node n : this ) {
      empty = false;
      builder.append( n ).append( ", " );
    }
    if( !empty ) {
      builder.delete( builder.length() - 2, builder.length() );
    }
    builder.append( "]" );
    return builder.toString();
  }

  /**
   * The iterator belongs to the collection class above. Required for iterating
   * over the set of adjacent nodes obtained by the class above.
   */
  private static class OppositeNodeIterator implements Iterator<Node> {
    /** The base node for which the adjacent nodes are to be iterated. */
    private final Node node;
    /** An iterator iterating over all edges leading to adjacent nodes. */
    private final Iterator<Edge> edgeIterator;
    /** The node to be returned when {@code next} is called the first time. */
    private transient Node next;
    /**
     * A map for marking nodes which have already been returned by this
     * iterator. This is required for preventing a node to be returned twice.
     */
    private transient final HashMap<Node, Boolean> visited;

    /**
     * Creates a new {@code OppositeNodeIterator} iterating over all nodes that
     * are opposite to the specified node with regard to the specified
     * collection of edges. Runtime O(1).
     *
     * @param node the base node for which the adjacent nodes are to be
     * returned.
     * @param edgeIterator the edges incident to {@code node}.
     */
    private OppositeNodeIterator( Node node, Iterator<Edge> edgeIterator ) {
      this.node = node;
      this.edgeIterator = edgeIterator;
      this.visited = new HashMap<>();
    }

    /**
     * Checks whether there is a node adjacent to the base node which has not
     * been returned by a call to {@code next} yet.
     *
     * @return whether there is a node adjacent to the base node which has not
     * been returned by a call to {@code next} yet.
     */
    @Override
    public boolean hasNext() {
      if( next != null ) {
        return true;
      } else if( edgeIterator.hasNext() ) {
        do {
          next = edgeIterator.next().opposite( node );
        } while( visited.containsKey( next ) && edgeIterator.hasNext() );
        return !visited.containsKey( next );
      } else {
        return false;
      }
    }

    /**
     * Returns a node adjacent to the base node which has not been returned by a
     * call to {@code next} yet, or {@code null
     * } if no such node exists. Runtime O(1).
     *
     * @return a node adjacent to the base node which has not been returned by a
     * call to {@code next} yet, or {@code null
     * } if no such node exists.
     */
    @Override
    public Node next() {
      if( next == null ) {
        hasNext();
      }
      Node result = next;
      visited.put( result, Boolean.TRUE );
      next = null;
      return result;
    }

    /**
     * This operation is not supported by this iterator.
     *
     * @exception UnsupportedOperationException if this method is called.
     */
    @Override
    public void remove() {
      throw new UnsupportedOperationException( GraphLocalization.loc.getString( "ds.graph.NotSupportedException" ) );
    }
  }
}
