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
 * AbstractFloor.java
 * Created on 27.01.2008, 19:39:18
 */
package gui.components;

import ds.PropertyContainer;
import ds.z.Floor;
import gui.JEditor;
import gui.editor.CoordinateTools;
import gui.editor.GUIOptionManager;
import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class AbstractFloor extends JPanel {
	/**
	 * An enumeration describing all possible styles for painting a raster on the
	 * editor background.
	 */
	public enum RasterPaintStyle {
		/** Draw a grid. */
		Lines,
		/** Draw points at the nodes of the grid. */
		Points,
		/** Do not draw anything. */
		Nothing;
	}

	// Variables for floor viewing
	protected int min_x,  min_y,  max_x,  max_y;
	protected int floorMin_x,  floorMin_y,  floorMax_x,  floorMax_y;
	protected int xOffset,  yOffset;
	protected int borderWidth;

	public AbstractFloor() {
		super();
		enableEvents( AWTEvent.MOUSE_MOTION_EVENT_MASK );
		min_x = PropertyContainer.getInstance().getAsInt( "editor.options.view.size.minx" );
		min_y = PropertyContainer.getInstance().getAsInt( "editor.options.view.size.miny" );
		max_x = PropertyContainer.getInstance().getAsInt( "editor.options.view.size.maxx" );
		max_y = PropertyContainer.getInstance().getAsInt( "editor.options.view.size.maxy" );
		floorMin_x = min_x;
		floorMin_y = min_y;
		floorMax_x = max_x;
		floorMax_y = max_y;
		borderWidth = PropertyContainer.getInstance().getAsInt( "editor.options.view.size.border" );
	}

	/**
	 * Updates the floor offset values. The size is, if
	 * necessary, enlarged by the value given by getBorderWidth().</p>
	 * 
	 * @param floor The Floor that should be displayed
	 */
	public void updateOffsets( Floor floor ) {
		floorMax_x = Math.max( max_x, floor.getxOffset() + floor.getWidth() );
		floorMax_y = Math.max( max_y, floor.getyOffset() + floor.getHeight() );
		floorMin_x = Math.min( min_x, floor.getxOffset() );
		floorMin_y = Math.min( min_y, floor.getyOffset() );

		CoordinateTools.setOffsets( floorMin_x - borderWidth, floorMin_y - borderWidth );
		setPreferredSize( new Dimension(
						CoordinateTools.translateToScreen( floorMax_x - floorMin_x + 2 * borderWidth ),
						CoordinateTools.translateToScreen( floorMax_y - floorMin_y + 2 * borderWidth ) ) );
	}
	/** The paint style for the grid. */
	private RasterPaintStyle rasterPaintType = RasterPaintStyle.Points;

	public RasterPaintStyle getRasterPaintType() {
		return rasterPaintType;
	}

	/**
	 * Returns a raster point that is closest to the given point.
	 * @param p the point
	 * @return a raster point that is closest to the given point
	 */
	public static Point getNextRasterPoint( Point p ) {
		int rasterWidth = CoordinateTools.translateToScreen( 200 );
		return new Point(
						(int)Math.round( p.getX() / (double)rasterWidth ) * rasterWidth,
						(int)Math.round( p.getY() / (double)rasterWidth ) * rasterWidth );
	}

	/** Sets the style in which the raster shall be painted.
	 * @param rasterPaintType 
	 */
	public void setRasterPaintStyle( RasterPaintStyle rasterPaintType ) {
		this.rasterPaintType = rasterPaintType;
		repaint();
	}

	/**
	 * Clears the background of the floor and draws the raster.
	 * @param g
	 */
	@Override
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );

		Graphics2D g2 = (Graphics2D)g;
		//g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

		//Clear background - do this only once - the polygons and edges won't do it.
		g2.setPaint( getBackground() );
		g2.fillRect( 0, 0, getWidth(), getHeight() );
	}

	public void drawRaster( Graphics2D g2 ) {
		if( getRasterPaintType() == RasterPaintStyle.Lines ) {
			g2.setPaint( GUIOptionManager.getEditorRasterColor() );
			int rasterWidth = CoordinateTools.translateToScreen( 400 );
			for( int i = rasterWidth; i < getHeight(); i += rasterWidth )
				g2.drawLine( 0, i, getWidth(), i );
			for( int i = rasterWidth; i < getWidth(); i += rasterWidth )
				g2.drawLine( i, 0, i, getHeight() );
		} else if( getRasterPaintType() == RasterPaintStyle.Points ) {
			g2.setPaint( GUIOptionManager.getEditorRasterColor() );
			int rasterWidth = CoordinateTools.translateToScreen( 400 );
			for( int i = 0; i < getHeight(); i = i + rasterWidth )
				for( int j = 0; j < getWidth(); j += rasterWidth )
					g2.drawRect( j, i, 1, 1 );
			g2.drawRect( 1, 2, 3, 4 );
		}
	}

	/** Mouse Motion Event Handler */
	@Override
	protected void processMouseMotionEvent( MouseEvent e ) {
		Point real = CoordinateTools.translateToModel( new Point( e.getX(), e.getY() ) );
		JEditor.sendMouse( real );
	}
}
