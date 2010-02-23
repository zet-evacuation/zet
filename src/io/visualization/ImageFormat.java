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
package io.visualization;

/**
 * An enumeration with some image formats that can be written.
 * @author Jan-Philipp Kappmeier
 */
public enum ImageFormat {
	/** Bitmap format */
	BMP( "Bitmap", "bmp" ),
	/** Graphics interchange format */
	GIF( "GIF", "gif" ),
	/** JPEG format */
	JPEG( "JPEG", "jpg" ),
	/** Portable network graphics, recommended by the www consortium. */
	PNG( "PNG", "png" );

	/** The ending of the movie format. */
	private String ending;	
	/** The name of the image format. The result of the {@link toString()} method */
	private String name;
	

	/**
	 * Creates a new enumeration instance
	 * @param name the name of the image format
	 * @param ending the ending of the image format
	 */
	ImageFormat( String name, String ending ) {
		this.ending = ending;
		this.name = name;
	}
	
	/**
	 * Returns the ending of the image format.
	 * @return the ending of the image format
	 */
	public String getEnding() {
		return ending;
	}

	/**
	 * Returns the name of the image format
	 * @return the name of the image format
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the name of the image format
	 * @return the name of the image format
	 */
	@Override
	public String toString() {
		return name;
	}
}
