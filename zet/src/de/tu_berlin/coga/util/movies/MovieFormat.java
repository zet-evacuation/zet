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

package de.tu_berlin.coga.util.movies;

/**
 * An enumeration of some movie formats that can be written.
 * @author Jan-Philipp Kappmeier
 */
public enum MovieFormat {
	/** MPEG-4 movie, DIVX or DIVX compatible. */
	DIVX( "DivX", "avi"),
	/** H.264 format. */
	H264( "H.264", "mkv" ),
	/** MPEG-1 movie. */
	MPEG1( "MPEG-1", "mpg" ),
	/** MPEG-2 movie. */
	MPEG2( "MPEG-2", "mpg" ),
	/** MPEG-4 movie. */
	MPEG4( "MPEG-4", "mp4" ),
	/** MPEG-4 movie, XVID or XVID compatible. */
	XVID( "XviD", "avi"),
	/** Quicktime movie consisting of a collection of JPEG images. */
	MOV( "Quicktime", "mov" );

	/** The ending of the movie format. */
	private final String ending;	
	/** The name of the movie format. The result of the {@link toString()} method */
	private final String name;

	/**
	 * Creates a new enumeration instance
	 * @param name the name of the movie format
	 */
	MovieFormat( String name, String ending ) {
		this.ending = ending;
		this.name = name;
	}

	/**
	 * Returns the ending of the movie format.
	 * @return the ending of the movie format
	 */
	public String getEnding() {
		return ending;
	}
	
	/**
	 * Returns the name of the movie format.
	 * @return the name of the movie format
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the name of the movie format
	 * @return the name of the movie format
	 */
	@Override
	public String toString() {
		return name;
	}
}
