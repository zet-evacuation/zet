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
package zet.gui.main.tabs.base;

import ds.PropertyContainer;
import de.tu_berlin.coga.zet.model.Floor;
import gui.GUIOptionManager;
import gui.ZETProperties;
import gui.editor.CoordinateTools;
import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import zet.gui.main.JZetWindow;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings( "serial" )
public class AbstractFloor extends JPanel {
	private int rasterSnap;
	private int bigRaster;
	private int smallRaster;

	// Variables for floor viewing
	protected int min_x,  min_y,  max_x,  max_y;
	protected int floorMin_x,  floorMin_y,  floorMax_x,  floorMax_y;
	protected int xOffset,  yOffset;
	protected int borderWidth;

	public AbstractFloor() {
		super();
		enableEvents( AWTEvent.MOUSE_MOTION_EVENT_MASK );
		reloadValues();
		floorMin_x = min_x;
		floorMin_y = min_y;
		floorMax_x = max_x;
		floorMax_y = max_y;
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

	/**
	 * Returns the style of raster visualization, that is used.
	 * @return
	 */
	public RasterPaintStyle getRasterPaintType() {
		return rasterPaintType;
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
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

		//Clear background - do this only once - the polygons and edges won't do it.
		g2.setPaint( getBackground() );
		g2.fillRect( 0, 0, getWidth(), getHeight() );
	}

	/**
	 * Draws a raster on the floor. The raster can be drawn as lines or points.
	 * Additionally, it is possible to draw a smaller helping grid as points.
	 * @param g2
	 */
	public void drawRaster( Graphics2D g2 ) {
		final Point p = CoordinateTools.translateToModel( 0, 0 );
		final int startx = CoordinateTools.translateToScreen((int)(Math.floor( (double)p.x/bigRaster ))*bigRaster - p.x);
		final int starty = CoordinateTools.translateToScreen((int)(Math.floor( (double)p.y/bigRaster ))*bigRaster - p.y);

		if( getRasterPaintType() == RasterPaintStyle.Lines ) {
			g2.setPaint( GUIOptionManager.getEditorRasterColor() );
			final int rasterWidth = CoordinateTools.translateToScreen( bigRaster );
			for( int i = rasterWidth; i < getHeight(); i += rasterWidth )
				g2.drawLine( 0, i, getWidth(), i );
			for( int i = rasterWidth; i < getWidth(); i += rasterWidth )
				g2.drawLine( i, 0, i, getHeight() );
		} else if( getRasterPaintType() == RasterPaintStyle.Points ) {
			g2.setPaint( GUIOptionManager.getEditorRasterColor() );
			final int rasterWidth = CoordinateTools.translateToScreen( bigRaster );
			for( int i = starty; i < getHeight(); i += rasterWidth )
				for( int j = startx; j < getWidth(); j += rasterWidth )
					g2.fillRect( j-1, i-1, 3, 3 );
			g2.drawRect( 1, 2, 3, 4 );
		}
		// draw smaller one
		if( getRasterPaintType() != RasterPaintStyle.Nothing && bigRaster % smallRaster == 0 && smallRaster < bigRaster ) {
			g2.setPaint( GUIOptionManager.getEditorRasterColor() );
			final int rasterWidth = CoordinateTools.translateToScreen( smallRaster );
			for( int i = starty; i < getHeight(); i += rasterWidth )
				for( int j = startx; j < getWidth(); j += rasterWidth )
					g2.drawRect( j, i, 0, 0 );
			g2.drawRect( 1, 2, 3, 4 );
		}
	}
//
//	/**
//	 * Returns the size of one raster square.
//	 * @return the size of one raster square
//	 */
//	public int getBigRaster() {
//		return bigRaster;
//	}
//
//	/**
//	 * Sets the size of one raster square.
//	 * @param bigRaster the size of one raster (in millimeter)
//	 */
//	public void setBigRaster( int bigRaster ) {
//		this.bigRaster = bigRaster;
//	}
//
//	/**
//	 * Returns the size of the coordinate raster, to which the cursor is snapped to
//	 * @return the size of the raster in millimeter
//	 */
//	public int getRasterSnap() {
//		return rasterSnap;
//	}
//
//	/**
//	 * Returns the size of the coordinate raster.
//	 * @param rasterSnap the raster size in millimeter
//	 */
//	public void setRasterSnap( int rasterSnap ) {
//		this.rasterSnap = rasterSnap;
//	}

//	/**
//	 * Returns the size of a small helping raster.
//	 * @return the size of a small helping raster
//	 */
//	public int getSmallRaster() {
//		return smallRaster;
//	}
//
//	/**
//	 * Sets the size of a smaller helping raster.
//	 * @param smallRaster the size of a smaller helping raster
//	 */
//	public void setSmallRaster( int smallRaster ) {
//		this.smallRaster = smallRaster;
//	}

	/** Mouse Motion Event Handler
	 * @param e
	 */
	@Override
	protected void processMouseMotionEvent( MouseEvent e ) {
		final Point real = CoordinateTools.translateToModel( new Point( e.getX(), e.getY() ) );
		JZetWindow.sendMouse( real );
	}

	/**
	 * Reloads the minimum x and y positions, the border width and the grind sizes
	 * and updates the values. Call this, if some of them have been changed.
	 */
	final public void reloadValues() {
		min_x = PropertyContainer.getInstance().getAsInt( "editor.options.view.size.minx" );
		min_y = PropertyContainer.getInstance().getAsInt( "editor.options.view.size.miny" );
		max_x = PropertyContainer.getInstance().getAsInt( "editor.options.view.size.maxx" );
		max_y = PropertyContainer.getInstance().getAsInt( "editor.options.view.size.maxy" );
		borderWidth = PropertyContainer.getInstance().getAsInt( "editor.options.view.size.border" );
		rasterSnap = ZETProperties.getRasterSizeSnap();
		bigRaster = ZETProperties.getRasterSize();
		smallRaster = ZETProperties.getRasterSizeSmall();
	}

	/** Prohibits serialization. */
	private synchronized void writeObject( java.io.ObjectOutputStream s ) {
		throw new UnsupportedOperationException( "Serialization not supported" );
	}
}
