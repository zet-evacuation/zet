/**
 * Class JMFWrapper
 * Erstellt 09.11.2008, 23:26:46
 */

package io.movie;

import java.util.Vector;
import javax.media.MediaLocator;
import javax.swing.JLabel;
import javax.swing.JPanel;
import localization.Localization;

/**
 * <p>The JMFWriter can write files with arbitrary names.</p>
 *
 * @author Jan-Philipp Kappmeier
 */
public class JMFWrapper extends PicturesOnlyWriter {
	/**
	 * {@inheritDoc}
	 * <p>Creates a new quicktime movie out of the images in the submitted vector.
	 * The movie is only a sequence of jpegs played with the selected framerate.</p>
	 * @param images the vector of images (with full paths)
	 */
	@Override
	public void create( Vector<String> images, String filename ) {
		MediaLocator oml;
		String outputURL = "file:" + path + filename + "." + movieFormat.getEnding();
		if ( ( oml = JpegImagesToQuicktime.createMediaLocator( outputURL ) ) == null ) {
			System.err.println( "Cannot build media locator from: " + outputURL );
			System.exit( 0 );
		}
		JpegImagesToQuicktime imageToMovie = new JpegImagesToQuicktime();
		imageToMovie.doIt( width, height, framerate, images, oml  );
	}
	
	/**
	 * {@inheritDoc}
	 * @return a {@link JPanel} showing the information that now further
	 * configuration is supported.
	 */
	@Override
	public JPanel getAdvancedConfigurationPanel() {
		JPanel panel = new JPanel();
		panel.add( new JLabel( Localization.getInstance().getString( "gui.visualization.createMovieDialog.jmf.noSupport" ) ) );
		return panel;
	}

}
