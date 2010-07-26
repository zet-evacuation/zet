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
 * JCellPolygon.java
 * Created on 28.01.2008, 00:41:39
 */
package zet.gui.components.tabs.quickVisualization;

import ds.z.PlanPolygon;
import zet.gui.components.tabs.base.AbstractFloor;
import zet.gui.components.tabs.base.AbstractPolygon;
import gui.editor.CoordinateTools;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.EnumSet;
import de.tu_berlin.math.coga.common.util.Direction;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JCellPolygon extends AbstractPolygon {

	private Color fillColor;
	EnumSet<Direction> borders = EnumSet.noneOf( Direction.class );

	/**
	 * @param myFloor the JFloor on which this polygon is displayed.
	 * @param fillColor the border color
	 */
	public JCellPolygon( AbstractFloor myFloor, Color fillColor ) {
		super( Color.black );

		this.myFloor = myFloor;
		this.fillColor = fillColor;
		setOpaque( false );

		// Create a transparent color with which we will fill out the polygon
		// in case it represents an area
		// transparentForeground = new Color( fillColor.getColorSpace(), fillColor.getColorComponents( null ), 0.75f );
	//transparentForeground = fillColor;
	}

	/**
	 * @param myFloor the JFloor on which this polygon is displayed.
	 * @param fillColor the border color
	 * @param lineColor 
	 */
	public JCellPolygon( AbstractFloor myFloor, Color fillColor, Color lineColor ) {
		super( lineColor );

		this.myFloor = myFloor;
		this.fillColor = fillColor;
		setOpaque( false );

		// Create a transparent color with which we will fill out the polygon
		// in case it represents an area
	}

	protected void setFillColor( Color fillColor ) {
		this.fillColor = fillColor;
	}
	
	/**
	 * Only call this after the component has been added to a container. Otherwise
	 * operations like setBounds, which are called herein, will fail.
	 * @param p the polygon
	 */
	public void displayPolygon( PlanPolygon p ) {
		myPolygon = p;

		if( p != null ) {
			// Contains absolute bounds
			Rectangle areabounds = CoordinateTools.translateToScreen( p.bounds() );
			setBounds( areabounds );

			// This already copies the polygon
			drawingPolygon = CoordinateTools.translateToScreen( myPolygon.getAWTPolygon() );
			drawingPolygon.translate( -areabounds.x, -areabounds.y);
		}
	}

	@Override
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );

		Graphics2D g2 = (Graphics2D) g;

		g2.setPaint( fillColor );
		//if( drawingPolygon != null )
		if( drawingPolygon.npoints > 0 ) {
			g2.fillPolygon( drawingPolygon );
		}

		g2.setPaint( getForeground() );
		g2.drawPolygon( drawingPolygon );
		
		g2.setPaint( getBackground() );
		Rectangle bounds = drawingPolygon.getBounds();
		BasicStroke thick = new BasicStroke ( 5 );
		g2.setStroke( thick );
		if( borders.contains( Direction.Top ) )
			g2.drawLine( bounds.x, bounds.y, bounds.x + bounds.width, bounds.y );
		if( borders.contains( Direction.Down ) )
			g2.drawLine( bounds.x, bounds.y + bounds.height, bounds.x + bounds.width, bounds.y + bounds.height );
		if( borders.contains( Direction.Left ) )
			g2.drawLine( bounds.x, bounds.y, bounds.x, bounds.y + bounds.height );
		if( borders.contains( Direction.Right ) )
			g2.drawLine( bounds.x + bounds.width, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height );

	//g2.fillPolygon( drawingPolygon );

//		if ( Room.class.isInstance( myPolygon ) ) {//myPolygon instanceof Room) {
//			// Paint the name of the room
//			Font originalFont = g2.getFont ();
//			g2.setFont (GUIOptionManager.getRoomNameFont ());
//			String name = Room.class.cast( myPolygon ).getName(); //((Room)myPolygon).getName ();
//			FontMetrics metrics = g2.getFontMetrics ();
//			
//			Rectangle name_bounds = metrics.getStringBounds (name, g2).getBounds ();
//			Rectangle my_bounds = getBounds ();
//			Rectangle my_normalized_bounds = new Rectangle (
//				0, 0, my_bounds.width, my_bounds.height);
//			
//			int pos_x = (my_bounds.width - name_bounds.width) / 2;
//			int pos_y = (my_bounds.height - name_bounds.height) / 2;
//			name_bounds.x += pos_x;
//			name_bounds.y += pos_y;
//			// Only paint if there is enough screen space for the string
//			if (my_normalized_bounds.contains (name_bounds)) {
//				g2.setPaint (GUIOptionManager.getRoomEdgeColor ());
//				// *5/6 because the name_bounds' height is inaccurate (too big)
//				g2.drawString (name, pos_x, pos_y + name_bounds.height*5/6);
//			}
//			
//			g2.setFont (originalFont);
//		}	
	}
	
	public void addWall( Direction direction ) {
   borders.add( direction );
	}
	
	/**
	 * This function does nothing. Overwrite this function in derived components
	 * to automatically set an appropriate tooltip.
	 */
	protected void setToolTipText() { }
}
