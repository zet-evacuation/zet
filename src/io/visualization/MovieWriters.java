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
/**
 * Class MovieWriters
 * Erstellt 12.11.2008, 12:23:15
 */

package io.visualization;

import io.movie.MovieWriter;
import io.movie.FFmpegWrapper;
import io.movie.JMFWrapper;
import java.util.ArrayList;
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
	private String name;
	private int formatListsIndex;
	/** The movie writer for the selected enumeration item. */
	private MovieWriter amw;
	
	private static final MovieFormat[][] movieFormats = {
		{MovieFormat.DIVX, MovieFormat.MPEG1, MovieFormat.MPEG2, MovieFormat.MPEG4, MovieFormat.XVID},
		{MovieFormat.MOV}
	};
	private static final ImageFormat[][] imageFormats = {
		{ImageFormat.JPEG, ImageFormat.PNG},
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
		ArrayList<MovieFormat> al = new ArrayList<MovieFormat>( movieFormats[formatListsIndex].length);
		for( int i = 0; i < movieFormats[formatListsIndex].length; i++ )
			al.add( movieFormats[formatListsIndex][i] );
		return EnumSet.copyOf( al );
	}

	public EnumSet<ImageFormat> getSupportedImageFormats() {
		ArrayList<ImageFormat> al = new ArrayList<ImageFormat>( imageFormats[formatListsIndex].length);
		for( int i = 0; i < imageFormats[formatListsIndex].length; i++ )
			al.add( imageFormats[formatListsIndex][i] );
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
