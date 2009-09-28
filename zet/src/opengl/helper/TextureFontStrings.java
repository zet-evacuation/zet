/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * TextureFontStrings.java
 * Created 28.09.2009, 21:04:24
 */
package opengl.helper;

import java.util.ArrayList;

/**
 * The class <code>TextureFontStrings</code> ...
 * @author Jan-Philipp Kappmeier
 */
/**
 * A class that represents a line of text. It saves
 * the information if the line is left-aligned or centered. The
 * with of the window has to be given from outside.
 */
public class TextureFontStrings {
	/** Indicates wheather the text is centered, or not. */
	private ArrayList<Boolean> centered;
	/** The content of the line. */
	private ArrayList<String> strings;
	/** The visible size. */
	private double width;
	/** */
	private final boolean ortho;
	private ArrayList<Double> x;
	private ArrayList<Double> y;
	private double xoffset = 0;

	/**
	 * Creates a new <code>TextureFontStrings</code> object.
	 * @param ortho specifies if an orthogonal view is used
	 */
	public TextureFontStrings( boolean ortho ) {
		this.ortho = ortho;
		this.strings = new ArrayList<String>();
		this.centered = new ArrayList<Boolean>();
		this.x = new ArrayList<Double>();
		this.y = new ArrayList<Double>();
	}

	/**
	 * Creates a new line with alignment information.
	 * @param string the text of the line
	 * @param centered indicates wheather the line is centered, or not
	 * @param ortho specifies if an orthogonal view is used
	 */
	public TextureFontStrings( String string, boolean centered, boolean ortho ) {
		this( ortho );
		add( string, centered );
	}

	public void add( String string ) {
		add( string, false, 0 );
	}

	public void add( String string, boolean centered ) {
		add( string, centered, 0 );
	}

	public void add( String string, boolean centered, double y ) {
		strings.add( string );
		this.centered.add( centered );

		// auto calc x-position
		if( ortho )
//			x.add( centered ? (width - (string.length() * 9))/2 : (width - (40*9))/2 );
			// TODO
			x.add( centered ? (width - (string.length() * 9))/2 : xoffset );
		else
			x.add( (centered ? ((((0.7f * 3) / 4) * (string.length())) * 0.5) : xoffset) );
		this.y.add( y );
	}

	/**
	 * Returns the <code>x</code>-position of the line
	 * @param i the index of the string
	 * @return the <code>x</code>-position of the line
	 */
	public float getX( int i ) {
		return x.get( i ).floatValue();
	}

	/**
	 * Returns the <code>y</coide>-position of the line.
	 * @param i the index of the string
	 * @return the <code>y</coide>-position of the line
	 */
	public float getY( int i ) {
		return y.get( i ).floatValue();
	}

	public double getWidth() {
		return width;
	}

	public void setWidth( double width ) {
		this.width = width;
	}

	public double getXoffset() {
		return xoffset;
	}

	public void setXoffset( double xoffset ) {
		this.xoffset = xoffset;
	}

	/**
	 * Returns the number of strings stored.
	 * @return the number of strings stored
	 */
	public int size() {
		return strings.size();
	}

	/**
	 * Returns the content of the specified line.
	 * @param i the index of the string
	 * @return the content of the specified line
	 */
	public String text( int i ) {
		return strings.get( i );
	}

	/**
	 * Returns the string represented by this <code>TextureFontStrings</code>.
	 * @return the string represented by this <code>TextureFontStrings</code>
	 */
	@Override
	public String toString() {
		return strings.toString();
	}
}