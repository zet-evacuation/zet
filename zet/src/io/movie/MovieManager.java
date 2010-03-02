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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io.movie;

import io.visualization.ImageFormat;
import io.visualization.MovieFormat;
import java.io.File;
import java.util.Vector;

/**
 * A <code>MovieManager</code> controls {@link MovieWriter} objects,
 * which basically only create movie files out of a list of files. The
 * controller allows the {@link gui.visualization.Visualization} easyly to access different
 * writer to support a wide variation of both, image and movie formats.
 * @author Jan-Philipp Kappmeier
 */
public class MovieManager {
	/** The width of the video. */
	private int width = 640;
	/** The height of the video. */
	private int height = 480;
	/** The framerate, that means number of pictures used for one second of video. */
	private int framerate = 1;
	/** The bitrate used for video encoding (if the writer supports). */
	private int bitrate = 1000;
	/** The filename of the movie without ending. */
	private String filename = "out";
	/** The path in which the movies and frame screenshots are stored. */
	private String path = "./";
	/** The movie filename. */
	private String fullFilePath = path + filename;
	/** The name of the movie frames. */
	private String framename = "frame";
	/** A vector containing the complete filenames of the video frames. */
	private Vector<String> inputFiles;
	/** The format of the video. */
	private MovieFormat movieFormat = MovieFormat.DIVX;
	/** The format of the images. */
	private ImageFormat frameFormat = ImageFormat.JPEG;
	/** The writer used to write the actual movie. */
	private MovieWriter writer;
	/** Decides wheather a video should be created out of the video frames. */
	private boolean createVideo = true;
	/** Decides wheather the video frames should be deleted after the video is created. */
	private boolean deleteFrames = true;
	
	/**
	 * Creates a new instance of <code>MovieManager</code> with default settings.
	 * The default writer for the movies is the {@link FFmpegWrapper} and the
	 * format is {@link MovieFormat#DIVX}.
	 */
	public MovieManager() {
		inputFiles = new Vector<String>();
		if( !(path.endsWith( "/" ) || path.endsWith( "\\" ) ) )
			path = path + "/";
		writer = new FFmpegWrapper();
		movieFormat = MovieFormat.DIVX;
	}
	
	/**
	 * Adds an frame image to the video.
	 * @param path the path to a file of the image, must be jpg
	 */
	public void addImage( String path ) {
		inputFiles.add( path );
	}
	
	/**
	 * Creates a new filename. The new filename is created by the movie writer
	 * following their special treatment of the filenames. The filename is
	 * <b>not</b> added to the vector of files.
	 * @return the new filename
	 */
	public String nextFilename() {
		String newFilename;
		newFilename = writer.getFilename( inputFiles.size()+1 );
		return newFilename;
	}
	
	/**
	 * Clears all images.
	 */
	public void clear() {
		inputFiles.clear();
	}
	
	/**
	 * Create the video using the selected writer.
	 */
	public void create() {
		writer.create( inputFiles, filename );
	}

	/**
	 * Deletes all frame files from harddisk.
	 * @return true if no error occured, false otherwise
	 */
	public boolean deleteFrameFiles() {
		boolean success = true;
		for( String inputFilename : inputFiles ) {
			File file = new File( inputFilename );
			success = file.delete() && success;
		}
		return success;
	}

	/**
	 * Call this method after all video frames have been created. The method
	 * will create a video if {@link #isCreateVideo()} returns <code>true</code>
	 * and will delete. At last the current list of frames is cleared, if
	 * {@link #isDeleteFrames()} returns <code>true</code>.
	 */
	public void performFinishingActions() {
		if( createVideo ) {
			create();
			// remove frames
			if( deleteFrames )
				deleteFrameFiles();
		}
    clear();
	}

	/**
	 * Returns the command used to encode the video.
	 * @return the command used to encode the video
	 */
	public String getCommands() {
		return writer.getCommand();
	}

	/**
	 * Returns the video create status, i.e. if a video is created when
	 * {@link #performFinishingActions()} is called.
	 * @return <code>true</code>, if a video is created
	 */
	public boolean isCreateVideo() {
		return createVideo;
	}

	/**
	 * Sets the video create status, i.e. if a video is created when
	 * {@link #performFinishingActions()} is called.
	 * @param createVideo true if a video should be created
	 */
	public void setCreateMovie( boolean createVideo ) {
		this.createVideo = createVideo;
	}

	/**
	 * Returns the frame files delete status, i.e. if the frames are deleted when
	 * {@link #performFinishingActions()} is called.
	 * @return true, if the videos are deleted
	 */
	public boolean isDeleteFrames() {
		return deleteFrames;
	}

	/**
	 * Sets the frame files delete status, i.e. if the frames are deleted when
	 * {@link #performFinishingActions()} is called.
	 * @param deleteFrames true if the files should be deleted
	 */
	public void setDeleteFrames( boolean deleteFrames ) {
		this.deleteFrames = deleteFrames;
	}

	/**
	 * Returns the bitrate used for video encoding.
	 * @return the bitrate in kilobits
	 */
	public int getBitrate() {
		return bitrate;
	}

	/**
	 * Sets the bitrate used for video encoding, if the wrapper supports
	 * writing in different bitrates.
	 * @param bitrate the bitrate in kilobits
	 */
	public void setBitrate( int bitrate ) {
		this.bitrate = bitrate;
		writer.setBitrate( bitrate );
	}

	/**
	 * Returns the video filename without ending.
	 * @return the video filename without ending
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Sets the video file without ending.
	 * @param filename the filename, but without ending.
	 */
	public void setFilename( String filename ) {
		this.filename = filename;
		fullFilePath = path + filename;
	}

	/**
	 * Returns the complete path, including filename.
	 * @return the complete path, including filename
	 */
	public String getFullFilePath() {
		return fullFilePath;
	}

	/**
	 * Returns the file format used for the movie frames.
	 * @return the file format used for the movie frames
	 */
	public ImageFormat getFrameFormat() {
		return frameFormat;
	}

	/**
	 * Sets a new file format for the movie frames.
	 * @param frameFormat the new file format
	 */
	public void setFrameFormat( ImageFormat frameFormat ) {
		this.frameFormat = frameFormat;
		writer.setFrameFormat( frameFormat );
	}
	
	/**
	 * Returns the filename used for the movie frame screenshots.
	 * @return the filename
	 */
	public String getFramename() {
		return framename;
	}

	/**
	 * Sets the filename used for the movie frame screenshots.
	 * @param movieFramename the filename
	 */
	public void setFramename( String movieFramename ) {
		this.framename = movieFramename;
		writer.setFramename( framename );
	}

	/**
	 * Returns the currently set framerate.
	 * @return the currently set framerate
	 */
	public int getFramerate() {
		return framerate;
	}

	/**
	 * Sets a framerate, that means the number of pictures used for one second of
	 * the movie.
	 * @param framerate the framerate
	 */
	public void setFramerate( int framerate ) {
		this.framerate = framerate;
		writer.setFramerate( framerate );
	}

	/**
	 * Returns the height used to create the next video.
	 * @return the height used to create the next video.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the new height for the next video
	 * @param height the height
	 */
	public void setHeight( int height ) {
		this.height = height;
		writer.setHeight( height );
	}

	/**
	 * Returns the currently selected movie format.
	 * @return the currently selected movie format
	 */
	public MovieFormat getMovieFormat() {
		return movieFormat;
	}

	/**
	 * Sets a new movie format.
	 * @param movieFormat the new movie format
	 */
	public void setMovieFormat( MovieFormat movieFormat ) {
		this.movieFormat = movieFormat;
		writer.setMovieFormat( movieFormat );
	}

	/**
	 * Sets a new movie writer. The movie writer is initialized with the settings
	 * stored in the <code>MovieManager</code>. <b>Note, that the original
	 * settings are overwritten and the settings of the manager are used!</b>
	 * @param writer the new movie writer
	 */
	public void setMovieWriter( MovieWriter writer ) {
		this.writer = writer;
		this.writer.setBitrate( bitrate );
		this.writer.setFrameFormat( frameFormat );
		this.writer.setFramename( framename );
		this.writer.setFramerate( framerate );
		this.writer.setHeight( height );
		this.writer.setMovieFormat( movieFormat );
		this.writer.setPath( path );
		this.writer.setWidth( width );
	}
	
	/**
	 * Returns the path to the movies and images.
	 * @return the path to the movies and images
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the path to the movies and images. If the path does not end with a
	 * slash, it is added.
	 * @param path the path to the movies and images
	 */
	public void setPath( String path ) {
		if( !(path.endsWith( "/" ) || path.endsWith( "\\" ) ) )
			path = path + "/";
		this.path = path;
		fullFilePath = path + filename;
		writer.setPath( path );
	}

	/**
	 * Returns the width used to create the next video.
	 * @return the width used to create the next video.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the new width for the next video
	 * @param width the width
	 */
	public void setWidth( int width ) {
		this.width = width;
		writer.setWidth( width );
	}
}
