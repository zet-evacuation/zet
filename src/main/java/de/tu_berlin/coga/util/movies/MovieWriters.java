/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.tu_berlin.coga.util.movies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public enum MovieWriters {
	/** */
	FFmpeg( "FFmpeg Encoder", 0, new FFmpegWrapper() ),
	/** */
	JMF( "Java Media Files", 1, new JMFWrapper() );

	/** The name of the image format. The result of the {@link toString()} method */
	private final String name;
	private final int formatListsIndex;
	/** The movie writer for the selected enumeration item. */
	private final MovieWriter amw;
	
	private static final MovieFormat[][] movieFormats = {
		{MovieFormat.H264, MovieFormat.DIVX, MovieFormat.MPEG1, MovieFormat.MPEG2, MovieFormat.MPEG4, MovieFormat.XVID},
		{MovieFormat.MOV}
	};
	private static final ImageFormat[][] imageFormats = {
		{ImageFormat.PNG, ImageFormat.JPEG},
		{ImageFormat.JPEG}
	};

	/**
	 * Creates a new enumeration instance
	 * @param name the name of the image format
	 * @param ending the ending of the image format
	 */
	MovieWriters( String name, int index, MovieWriter amw ) {
		this.name = name;
		this.formatListsIndex = index;
		this.amw = amw;
	}
	
	public EnumSet<MovieFormat> getSupportedMovieFormats() {
		ArrayList<MovieFormat> al = new ArrayList<>( movieFormats[formatListsIndex].length);
		al.addAll( Arrays.asList( movieFormats[formatListsIndex] ) );
		return EnumSet.copyOf( al );
	}

	public EnumSet<ImageFormat> getSupportedImageFormats() {
		ArrayList<ImageFormat> al = new ArrayList<>( imageFormats[formatListsIndex].length);
		al.addAll( Arrays.asList( imageFormats[formatListsIndex] ) );
		return EnumSet.copyOf( al );
	}
	
	/**
	 * Returns the name of the image format
	 * @return the name of the image format
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the movie writer for the selected value.
	 * @return the movie writer for the selected value
	 */
	public MovieWriter getWriter() {
		return amw;
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
