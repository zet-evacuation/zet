/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
 * Direction.java
 */
package de.tu_berlin.math.coga.common.util;

/**
 * This enumerates directions on an integral 2 dimensional room such as a
 * raster. Each direction has offsets for the {@code x} and {@code y}-direction
 * in \{-1,0,1\}. The {@code x}-coordinate increases in the <b>Right</b>
 * direction while the {@code y}-coordinate increases in the <b>downwards</b>
 * direction. Hence, if only positive coordinates are allowed, the point (0,0)
 * lies in the up-most, Left-most corner.
 * @author Daniel Plümpe
 */
public enum Direction {

	/** The Left direction. */
	Left( -1, 0 ),
	/** The Right direction. */
	Right( 1, 0, Left ),
	/** The upper direction. */
	Top( 0, -1 ),
	/** The lower direction. */
	Down( 0, 1, Top ),
	/** The upper Left direction. */
	TopLeft( -1, -1 ),
	/** The upper Right direction. */
	TopRight( 1, -1, TopLeft ),
	/** The lower Left direction. */
	DownLeft( -1, 1 ),
	/** The lower Left direction. */
	DownRight( 1, 1, DownLeft );
	
	/** The offset value in {@code x}-direction. */
	private final int xOffset;
	/** The offset value in {@code y}-direction. */
	private final int yOffset;
	/** The opposite direction of the current direction. */
	private Direction inverseDirection;

	/**
	 * The constructor for the direction. It needs to be called with the offsets
	 * and the opposite direction. It also sets the opposite direction for the
	 * opposite direction.
	 * @param xOffset the offset value in {@code x}-direction
	 * @param yOffset the offset value in {@code y}-direction
	 * @param inverseDirection the opposite direction
	 */
	private Direction( int xOffset, int yOffset, Direction inverseDirection ) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.inverseDirection = inverseDirection;
		inverseDirection.setInverse( this );
	}

	/**
	 * The constructor for the direction. It needs to be called with the offsets
	 * only. Note that the opposite direction is not set.
	 * @param xOffset the offset value in {@code x}-direction
	 * @param yOffset the offset value in {@code y}-direction
	 * @param inverseDirection the opposite direction
	 */
	private Direction( int xOffset, int yOffset ) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	/**
	 * Returns the offset value in {@code x}-direction.
	 * @return the offset value in {@code x}-direction
	 */
	public final int xOffset() {
		return xOffset;
	}

	/**
	 * Returns the offset value in {@code x}-direction.
	 * @return the offset value in {@code x}-direction
	 */
	public final int yOffset() {
		return yOffset;
	}

	/**
	 * Gives the opposite direction.
	 * @return the opposite direction
	 */
	public final Direction invert() {
		return inverseDirection;
	}

	/**
	 * Returns the enumeration item corresponding to the given x- and y-offsets.
	 * @param xOffset x-offset
	 * @param yOffset y-offset
	 * @return the enumeration item corresponding to the given x- and y-offsets.
	 */
	public final static Direction getDirection( int xOffset, int yOffset ) {
		int x = xOffset;
		int y = yOffset;
		if( x == -1 && y == 0 )
			return Left;
		if( x == 1 && y == 0 )
			return Right;
		if( x == 0 && y == -1 )
			return Top;
		if( x == 0 && y == 1 )
			return Down;
		if( x == -1 && y == -1 )
			return TopLeft;
		if( x == 1 && y == -1 )
			return TopRight;
		if( x == -1 && y == 1 )
			return DownLeft;
		if( x == 1 && y == 1 )
			return DownRight;
		throw new AssertionError( "Not a valid direction" );
	}

	/**
	 * Sets the opposite direction for the direction.
	 * @param inverseDirection the opposite direction
	 */
	private final void setInverse( Direction inverseDirection ) {
		this.inverseDirection = inverseDirection;
	}
}
