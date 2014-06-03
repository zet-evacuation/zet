/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.coga.graph.Node;
import java.awt.Point;

/**
 *
 * @author marlenschwengfelder
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
