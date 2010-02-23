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
 * Class MovieWriter
 * Erstellt 09.11.2008, 22:43:29
 */

package io.movie;

import io.visualization.ImageFormat;
import io.visualization.MovieFormat;
import java.util.Vector;
import javax.swing.JPanel;

/**
 * A <code>MovieWriter</code> is a class that allows to save a list of files
 * to a movie. It can work as a writer itself or as a wrapper for some
 * external movie encoder. It has support to create new filenames used to
 * store new movie frames and also provides a graphical framework allowing
 * to set up advanced settings.
 * @author Jan-Philipp Kappmeier
 */
public abstract class MovieWriter {
	/** The number of digits that count the frame files. */
	protected final int FRAMEDIGITS = 5;
	/** The path to the movie and the frame images. */
	protected String path;
	/** The filename used for the movie images. The frame number with {@link #FRAMEDIGITS} digits is added. */
	protected String framename;
	/** The format in which the movie is encoded. */
	protected MovieFormat movieFormat;
	/** The format used for the images. */
	protected ImageFormat frameFormat;
	/** The bitrate of the movie. */
	protected int bitrate;
	/** The framerate of the movie. */
	protected int framerate;
	/** The height of the movie. */
	protected int height;
	/** The with of the movie. */
	protected int width;
	/** The command used to encode the video. */
	protected String command;



	/**
	 * <p>Returns a filename for the frame with the specified number. The wrapper
	 * can create files in the temp directory, but is not supposed to do so as
	 * the files maybe should remain in the video directory after the video is
	 * created.</p>
	 * <p>The filenames do not neccessary need to have the given number as
	 * a suffix or prefix or whatever.</p>
	 * @param number the frame number
	 * @return the filename including the path
	 */
	public abstract String getFilename( int number );
	/**
	 * Creates the video with the specified filename with the {@code #path}. The
	 * filenames should be submitted, but, however, needn't be neccessaryly
	 * used.
	 * @param inputFiles the filenames of the video frames
	 * @param filename the movie output filename (without path)
	 */
	public abstract void create( Vector<String> inputFiles, String filename );

	/**
	 * Returns a {@link JPanel} that allows setting up some advanced features for
	 * the given movie writer.
	 * @return the panel
	 */
	public abstract JPanel getAdvancedConfigurationPanel();
	
	/**
	 * Returns the selected bitrate used for movie encoding, if the writer supports.
	 * @return the bitrate
	 */
	public int getBitrate() {
		return bitrate;
	}

	/**
	 * Sets the bitrate used for movie encoding, if the writer supports.
	 * @param bitrate the bitrate in kilobits per second
	 */
	public void setBitrate( int bitrate ) {
		this.bitrate = bitrate;
	}

	/**
	 * Returns the command used to encode the video.
	 * @return the command used to encode the video
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Returns the format in which the frame images are saved.
	 * @return the format in which the frame images are saved
	 */
	public ImageFormat getFrameFormat() {
		return frameFormat;
	}

	/**
	 * Sets the format in which the frame images are saved.
	 * @param imageFormat the image format
	 */
	public void setFrameFormat( ImageFormat imageFormat ) {
		this.frameFormat = imageFormat;
	}


	/**
	 * Returns the filename used as prefix for the frame images.
	 * @return the frame image prefix
	 */
	public String getFramename() {
		return framename;
	}

	/**
	 * Sets the framename used as a prefix for the frame images.
	 * @param framename the frame name prefix.
	 */
	public void setFramename( String framename ) {
		this.framename = framename;
	}

	/**
	 * Returns the framerate used for movie encoding.
	 * @return the framerate used for movie encoding
	 */
	public int getFramerate() {
		return framerate;
	}

	/**
	 * Sets the framerate used for movie encoding.
	 * @param framerate the framerate used for movie encoding
	 */
	public void setFramerate( int framerate ) {
		this.framerate = framerate;
	}

	/**
	 * Returns the height of the movie.
	 * @return the height of the movie
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the height of the movie.
	 * @param height the movie height
	 */
	public void setHeight( int height ) {
		this.height = height;
	}

	/**
	 * Sets the format in which the movie is encoded.
	 * @param movieFormat the movie format
	 */
	public void setMovieFormat( MovieFormat movieFormat ) {
		this.movieFormat = movieFormat;
	}

	/**
	 * Returns the format in which the movie is encoded.
	 * @return the the movie format
	 */
	public MovieFormat getMovieFormat() {
		return movieFormat;
	}

	/**
	 * Returns the path where the images and the movie are saved.
	 * @return the path where the images and the movie are saved
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Setsthe path where the images and the movie are saved.
	 * @param path the path where the images and the movie are saved
	 */
	public void setPath( String path ) {
		this.path = path;
	}

	/**
	 * Returns the width of the movie.
	 * @return the width of the movie
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the width of the movie.
	 * @param width the movie width
	 */
	public void setWidth( int width ) {
		this.width = width;
	}
}