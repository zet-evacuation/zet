/*
 * AbstractPolygon.java
 * Created on 28.01.2008, 00:10:40
 */

package gui.components;

import ds.z.Edge;
import ds.z.PlanPolygon;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import javax.swing.JPanel;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
abstract public class AbstractPolygon extends JPanel {
	protected Polygon drawingPolygon;
	/** Most global super type of PlanPolygon. */
	protected PlanPolygon<Edge> myPolygon;
	//private JFloor myFloor;
	protected AbstractFloor myFloor;	

	public AbstractPolygon( Color foreground ) {
		super( null );
		setForeground( foreground );
	}

	/**
	 * Returns the {@link ds.z.PlanPolygon} represented by this instance.
	 * @return the polygon
	 */
	public PlanPolygon getPlanPolygon () { return myPolygon; }
	
	/**
	 * Returns the real polygon area within the coordinate space  of this
	 * JCellPolygons bounding box.
	 * @return  */
	public Polygon getDrawingPolygon () { return drawingPolygon; }
	
	/**
	 * 
	 * @param p
	 */
	public abstract void displayPolygon( PlanPolygon p );
	
	/**
	 * 
	 * @param name
	 * @param g2
	 */
	public void drawName( String name, Graphics2D g2 ) {
		FontMetrics metrics = g2.getFontMetrics();
		Rectangle nameBounds = metrics.getStringBounds( name, g2 ).getBounds();
		Rectangle myBounds = getBounds();
		Rectangle stringBounds = new Rectangle( (myBounds.width - nameBounds.width) / 2, (myBounds.height - nameBounds.height) / 2, nameBounds.width, nameBounds.height );
		Rectangle normalizedBounds = new Rectangle( 0, 0, myBounds.width, myBounds.height );
		// Only paint if there is enough screen space for the string. We check it with the
		// correct height values. But the nameBounds are baseline-relative so we need to
		// subtract the negative y-offset for the baseline.
		if( normalizedBounds.contains( stringBounds ) ) {
			g2.drawString( name, stringBounds.x, stringBounds.y -nameBounds.y);
		}
	}
	
	/**
	 * 
	 * @param name
	 * @param g2
	 * @param color
	 */
	public void drawName( String name, Graphics2D g2, Color color ) {
		g2.setPaint( color );
		drawName( name, g2 );
	}
}
