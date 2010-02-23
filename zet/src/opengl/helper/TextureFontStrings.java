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
 * TextureFontStrings.java
 * Created 28.09.2009, 21:04:24
 */
package opengl.helper;

import java.util.ArrayList;

/**
 * A class that represents a line of getText. It saves
 * the information if the line is left-aligned or centered. The
 * with of the window has to be given from outside.
 * @author Jan-Philipp Kappmeier
 */
public class TextureFontStrings {
	/** Indicates wheather the getText is centered, or not. */
	private ArrayList<Boolean> centered;
	/** The content of the line. */
	private ArrayList<String> strings;
	/** The visible size. */
	private double width;
	/** Decides wheather the string is used in an orthogonal view, or not (otherwise it is perspective). */
	private final boolean ortho;
	/** Decides the <code>x</code>-position of a line. */
	private ArrayList<Double> x;
	/** Decides the <code>y</code>-position of a line. */
	private ArrayList<Double> y;
	/** Decides wheather a line is bold, or not. */
	private ArrayList<Boolean> bold;
	/** Decides the default <code>x</code>-position of a line. */
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
		this.bold = new ArrayList<Boolean>();
	}

	/**
	 * Creates a new line with alignment information.
	 * @param string the getText of the line
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
		this.bold.add( false );
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

	/**
	 * Sets a new <code>y</code>-position to a specified text element.
	 * @param i the index of the text
	 * @param y the new position
	 */
	public void setY( int i, double y ) {
		this.y.set( i, y );
	}

	/**
	 * Returns if the iondicated line is drawn bold.
	 * @param i specifies the line
	 * @return <code>true</code> if the specified line is drawn bold, <code>false</code> otherwise
	 */
	public boolean getBold( int i ) {
		return bold.get( i );
	}

	/**
	 * Sets a new value that indicates of a line is drawn bold.
	 * @param i the line
	 * @param bold indicates if the line is drawn bold
	 */
	public void setBold( int i, boolean bold ) {
		this.bold.set( i, bold );
	}

	/**
	 * Returns the maximum width that should be used to display the lines.
	 * @return the maximum width that should be used to display the lines.
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Sets the maximum width that should be used to display the lines.
	 * @param width the with
	 */
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
	public String getText( int i ) {
		return strings.get( i );
	}

	/**
	 * Sets a new value to a specified position.
	 * @param i the position in the list of texts
	 * @param text the new text
	 */
	public void setText( int i, String text ) {
		strings.set( i, text );
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