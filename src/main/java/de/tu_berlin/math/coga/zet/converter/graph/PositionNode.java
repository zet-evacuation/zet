/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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
package de.tu_berlin.math.coga.zet.converter.graph;

import org.zetool.graph.Node;
import java.awt.Point;

/**
 *
 * @author Marlen Schwengfelder
 */
class PositionNode {
    
    public Point p; 
    public Node n;
    public int doorWidth; 
    
    public PositionNode(Node nod, Point pos, int width)
    {
        this.p = pos;
        this.n = nod;
        this.doorWidth = width;
    }
    
    public PositionNode(Node nod, Point pos)
    {
        this.p = pos;
        this.n = nod;
        this.doorWidth = 0;
    }
    
    public Point getPositionForNode(PositionNode node)
    {
        return node.p;
    }
    public Point getPosition()
    {
        return this.p;
    }
    
    public Node getNode()
    {
       return n;
    }
    public Node getNodeForPosition(PositionNode node)
    {
        return node.n;
    }

    public int getWidth()
    {
        return doorWidth;
    }
    
    public String toString(PositionNode node)
    {
        return String.format("%1$s, (%2$s,%3$s), %4$s", node.getNode().id(),node.getPosition().getX(),node.getPosition().getY(), node.getWidth() );
    }
}
