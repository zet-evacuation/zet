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

/*
 * AbstractPolygon.java
 * Created on 28.01.2008, 00:10:40
 */

package zet.gui.components.tabs.base;

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
	public final PlanPolygon getPlanPolygon () { return myPolygon; }
	
	/**
	 * Returns the real polygon area within the coordinate space of the bounding
	 * box of this {@code AbstractPolygon}.
	 * @return the real polygon area of this polygon
	 */
	public final Polygon getDrawingPolygon () { return drawingPolygon; }
	
	/**
	 * 
	 * @param p
	 */
	public abstract void displayPolygon( PlanPolygon p );
	
	/**
	 * Draws the specified text in the polygon. The position is the middle of the
	 * bounding box. The text is not displayed if it is to large to fit into the
	 * bounding box.
	 * @param name the text displayed.
	 * @param g2 the graphics context on which the name is drawn
	 */
	public void drawName( String name, Graphics2D g2 ) {
		if( 1 == 1 )return;
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
	 * Draws the specified text with a specified color in the polygon. The
	 * position is the middle of the bounding box. The text is not displayed if it
	 * is to large to fit into the bounding box.
	 * @param name the text displayed.
	 * @param g2 the graphics context on which the name is drawn
	 * @param color the color that should be used for the text
	 */
	public void drawName( String name, Graphics2D g2, Color color ) {
		g2.setPaint( color );
		drawName( name, g2 );
	}
}
