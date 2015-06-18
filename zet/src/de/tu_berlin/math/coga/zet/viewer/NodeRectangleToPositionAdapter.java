/* copyright 2014-2015
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

package de.tu_berlin.math.coga.zet.viewer;

import ds.graph.NodeRectangle;
import java.util.Iterator;
import org.zetool.container.mapping.IdentifiableMapping;
import org.zetool.container.mapping.IdentifiableObjectMapping;
import org.zetool.container.util.IteratorAdapter;
import org.zetool.graph.Node;
import org.zetool.math.geom.DiscretePoint;
import org.zetool.math.geom.NDimensional;

/**
 * An adapter that allows to use a {@link Node} to {@link NodeRectangle}, where a mapping to integral coordinates
 * is necessary. The mapping computes the center of the {@link NodeRectangle} and returns this as position.
 * @author Jan-Philipp Kappmeier
 */
public class NodeRectangleToPositionAdapter implements IdentifiableMapping<Node,NDimensional<Integer>>{
  private final IdentifiableObjectMapping<Node,NodeRectangle> nodePositions;

  public NodeRectangleToPositionAdapter( IdentifiableObjectMapping<Node, NodeRectangle> nodePositions ) {
    this.nodePositions = nodePositions;
  }

  @Override
  public NDimensional<Integer> get( Node node ) {
		int nwX = nodePositions.get( node ).get_nw_point().getX();
		int nwY = nodePositions.get( node ).get_nw_point().getY();
		int seX = nodePositions.get( node ).get_se_point().getX();
		int seY = nodePositions.get( node ).get_se_point().getY();

		int xPosition = (int)(nwX + 0.5 * (seX - nwX));
		int yPosition = (int)(nwY + 0.5 * (seY - nwY));
    return new DiscretePoint(xPosition, yPosition);
  }

  @Override
  public int getDomainSize() {
    return nodePositions.getDomainSize();
  }

  @Override
  public void setDomainSize( int value ) {
    nodePositions.setDomainSize( value );
  }

  @Override
  public boolean isDefinedFor( Node identifiableObject ) {
    return nodePositions.isDefinedFor( identifiableObject );
  }

  /**
   * Setting is unsupported operation for a read-only adapter.
   * @param identifiableObject unused
   * @param value unused
   * @throws UnsupportedOperationException whenever called
   */
  @Override
  public void set( Node identifiableObject, NDimensional<Integer> value ) {
    throw new UnsupportedOperationException( "Setting for an adapter is not allowed!" );
  }

  @Override
  public Iterator<NDimensional<Integer>> iterator() {
    return new IteratorAdapter<>( nodePositions, NodeRectangleToPositionAdapter::adapt );
  }
  
  /**
   * Adapts a node rectangle to an integral point by using the center of the rectangle.
   * @param nodeRectangle the rectangle
   * @return the adapted two dimensional point in the rectangle's center
   */
  public static NDimensional<Integer> adapt( NodeRectangle nodeRectangle ) {
    int nwX = nodeRectangle.get_nw_point().getX();
    int nwY = nodeRectangle.get_nw_point().getY();
    int seX = nodeRectangle.get_se_point().getX();
    int seY = nodeRectangle.get_se_point().getY();

    int xPosition = (int) (nwX + 0.5 * (seX - nwX));
    int yPosition = (int) (nwY + 0.5 * (seY - nwY));
    return new DiscretePoint( xPosition, yPosition );
  }
}
