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

import org.zetool.common.localization.Localization;
import org.zetool.common.localization.LocalizationManager;
import java.util.List;
import java.util.Vector;
import javax.media.MediaLocator;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * <p>The JMFWriter can write files with arbitrary names.</p>
 * @author Jan-Philipp Kappmeier
 */
public class JMFWrapper extends PicturesOnlyWriter {
	private final static Localization loc = LocalizationManager.getManager().getLocalization( "io.movie.movies" );
	/**
	 * {@inheritDoc}
	 * <p>Creates a new quicktime movie out of the images in the submitted vector.
	 * The movie is only a sequence of jpegs played with the selected framerate.</p>
	 * @param images the vector of images (with full paths)
	 * @param filename the output file name
	 */
	@Override
	public void create( List<String> images, String filename ) {
		String outputURL = "file:" + path + filename + "." + movieFormat.getEnding();
		MediaLocator oml = JpegImagesToQuicktime.createMediaLocator( outputURL );
		if ( oml == null ) {
			System.err.println( "Cannot build media locator from: " + outputURL );
			System.exit( 0 );
		}
		JpegImagesToQuicktime imageToMovie = new JpegImagesToQuicktime();
		@SuppressWarnings( "UseOfObsoleteCollectionType" ) // legacy code uses vector
		Vector<String> fileList = new Vector<>( images );
		imageToMovie.doIt( width, height, framerate, fileList, oml  );
	}
	
	/**
	 * {@inheritDoc}
	 * @return a {@link JPanel} showing the information that now further
	 * configuration is supported.
	 */
	@Override
	public JPanel getAdvancedConfigurationPanel() {
		JPanel panel = new JPanel();
		panel.add( new JLabel( loc.getString( "gui.visualization.createMovieDialog.jmf.noSupport" ) ) );
		return panel;
	}
}
