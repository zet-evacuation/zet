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
package de.tu_berlin.math.coga.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;

/**
 * Provides a scalable ruler. The unit can be scaled using different
 * measurements. Additional ticks and numbers on the ruler are added. The
 * current cursor position can be drawn as well.
 * @author Jan-Philipp Kappmeier
 */
public class JRuler extends JComponent {

	/**
	 * An enumeration containing several units to measure distance. All units
	 * are provided with a scale factor to the base unit 1 meter and a short
	 * text as abbreviation (e. g. m for meter).
	 */
	public enum RulerDisplayUnits {

		/** Micrometers. */
		Micrometer( 0.0001, "my" ),
		/** Millimeters. */
		Millimeter( 0.001, "mm" ),
		/** Centimeters. */
		Centimeter( 0.01, "cm" ),
		/** Two centimeters. */
		TwoCentimeter( 0.02, "2cm" ),
		/** Decimeters. */
		Decimeter( 0.1, "dm" ),
		/** Meters. */
		Meter( 1.0, "m" ),
		/** Inches. */
		Inch( 0.0254, "in" ),
		/** Feet. */
		Foot( 0.3048, "ft" ),
		/** Yards. */
		Yard( 0.9144, "yd" );
		/** The scaling value for the selected unit. */
		private final double unit;
		/** The short text for the selected unit. */
		private final String text;

		/**
		 * Creates a unit instance containing a scale factor and a short text.
		 * @param unit the scaling factor for the unit
		 * @param text the short text for the unit
		 */
		RulerDisplayUnits( double unit, String text ) {
			this.unit = unit;
			this.text = text;
		}

		/**
		 * Returns the scaling factor to meter from the unit.
		 * @return the scaling factor to meter from the unit
		 */
		double unit() {
			return unit;
		}

		/**
		 * Returns the short name of the unit as text.
		 * @return the short name of the unit as text
		 */
		@Override
		public String toString() {
			return text;
		}
	}

	/**
	 * An enumeration containing the two directions in which the ruler can be
	 * drawn, horizontally and vertically.
	 */
	public enum RulerOrientation {

		/** The ruler is horizontally drawn. */
		Horizontal,
		/** The ruler is vertically drawn. */
		Vertical;
	}
	/** The currently set unit of the ruler. */
	private RulerDisplayUnits unit = RulerDisplayUnits.Centimeter;
	/** The height of an horizontal ruler, or the width of a vertical, respectively. */
	private int size = 30;
	/** The orientation of the ruler. Can be horizontal or vertical. */
	private RulerOrientation orientation;
	/** The background color of the ruler. */
	public Color background = Color.WHITE;
	/** The foreground color of the ruler. */
	public Color foreground = Color.BLACK;
	/** The font for the numbers and units. */
	public Font font = new Font( "SansSerif", Font.PLAIN, 10 );
	/** The step wide that defines the scale elements painted. */
	public double scalePaintStep = 10;
	/** The most left position. */
	public double offset = 0;
	/** Private zoom factor */
	private double zoomFactor = 1000;
	static final int longTick = 10;
	private int bigScaleStep = 1;
	private int smallScaleStep = 1;

	/**
	 * Creates a ruler with the specified orientation and the specified distance
	 * measurement.
	 * @param orientation the direction of the ruler (horizontally or vertically)
	 * @param unit the unit that is used to measure the distance
	 */
	public JRuler( RulerOrientation orientation, RulerDisplayUnits unit ) {
		this.orientation = orientation;
		this.unit = unit;
	}

	public RulerDisplayUnits getDisplayUnit() {
		return this.unit;
	}

	@Override
	protected void paintComponent( Graphics g ) {
		// Get bounds
		Rectangle drawArea = g.getClipBounds();

		// Fill background
		g.setColor( background );
		g.fillRect( drawArea.x, drawArea.y, drawArea.width, drawArea.height );

		// Set font and foreground color
		g.setColor( foreground );
		g.setFont( font );

		// Draw border lines
		if( orientation == RulerOrientation.Horizontal )
			g.drawLine( drawArea.x, size - 1, drawArea.x + drawArea.width, size - 1 );
		else
			g.drawLine( size - 1, drawArea.y, size - 1, drawArea.y + drawArea.height );

		// Some vars we need.
		String text = null;
		int end = 0;
		int tickLength = 0;

		double increment = zoomFactor * unit.unit();

		// Use clipping bounds to calculate first and last tick locations.
		end = (int) Math.round( (((orientation == RulerOrientation.Horizontal ? drawArea.x + drawArea.width : drawArea.y + drawArea.height) / increment) + 1) * increment );

		// The 0-value is not explicitly drawn.

		double startOffset = (offset / unit.unit());
		int factor = (int) Math.floor( startOffset / (double) bigScaleStep );
		int drawPos = 0;
		int i = factor * bigScaleStep;
		int iOffset = i;
		double correction;
		correction = i - startOffset;

		//int i = (int)Math.ceil( start/increment );
		while( drawPos < end ) {
			drawPos = (int) Math.round( (i - iOffset) * increment + correction * increment );
			if( i % bigScaleStep == 0 ) {
				tickLength = 10;
				text = Integer.toString( i );
			} else {
				tickLength = 7;
				text = null;
			}
			if( tickLength != 0 )
				if( orientation == RulerOrientation.Horizontal ) {
					g.drawLine( drawPos, size - 1, drawPos, size - tickLength - 1 );
					if( text != null )
						g.drawString( text, drawPos - 3, 16 );
				} else {
					g.drawLine( size - 1, drawPos, size - tickLength - 1, drawPos );
					if( text != null )
						g.drawString( text, 7, drawPos + 3 );
				}
			i += smallScaleStep;
		}
	}

	public void setBigScaleStep( int scale ) {
		if( scale <= 0 )
			throw new java.lang.IllegalArgumentException( "Scale is negative or zero" );
		this.bigScaleStep = scale;
	}

	public void setDisplayUnit( RulerDisplayUnits unit ) {
		this.unit = unit;
	}

	public void setHeight( int pw ) {
		setPreferredHeight( (int) Math.ceil( pw * zoomFactor ) );
	}

	public void setPreferredHeight( int ph ) {
		setPreferredSize( new Dimension( size, ph ) );
	}

	public void setPreferredWidth( int pw ) {
		setPreferredSize( new Dimension( pw, size ) );
	}

	public void setSize( int size ) {
		if( size <= 0 )
			throw new java.lang.IllegalArgumentException( "Size not positive" );
		this.size = size;
	}

	public void setSmallScaleStep( int scale ) {
		if( scale <= 0 )
			throw new java.lang.IllegalArgumentException( "Scale is negative or zero" );
		this.smallScaleStep = scale;
	}

	public void setWidth( int ph ) {
		setPreferredWidth( (int) Math.ceil( ph * zoomFactor ) );
	}

	/**
	 * Sets a specified zoom factor for the ruler.
	 * A zoom factor of 1 means that one pixel represents 1 millimeter.
	 * @param zoomFactor the zoom factor
	 * @throws java.lang.IllegalArgumentException if the zoom factor is negative
	 */
	public void setZoomFactor( double zoomFactor ) {
		if( zoomFactor <= 0 )
			throw new java.lang.IllegalArgumentException( "Zoomfactor negative" );
		this.zoomFactor = 1000 * zoomFactor;
	}
}
