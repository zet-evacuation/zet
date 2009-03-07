/**
 * Class PicturesOnlyWriter
 * Erstellt 17.11.2008, 00:58:01
 */

package io.movie;

import io.IOTools;
import java.util.Vector;
import javax.swing.JPanel;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PicturesOnlyWriter extends MovieWriter {

	/**
	 * <p>Creates a new filename, but ignores the actual frame number. The next free
	 * file with the suffix {@link getFramename()} and {@link FRAMEDIGITS} digits
	 * is returned.</p>
	 * @param frameNumber the (ignored) frame number
	 * @return the next filename
	 */
	public String getFilename( int frameNumber ) {
		return IOTools.getNextFreeNumberedFilepath( path, framename, FRAMEDIGITS  ) + "." + frameFormat.getEnding();
	}

	/**
	 * {@inheritDoc}
	 * @throws java.lang.UnsupportedOperationException because the <code>PicturesOnlyWriter</code> does not support movie encoding.
	 */
	@Override
	public void create( Vector<String> inputFiles, String filename ) throws java.lang.UnsupportedOperationException {
		throw new UnsupportedOperationException( "This wrapper does not support movie encoding." );
	}

	/**
	 * {@inheritDoc}
	 * @return an empty configarion {@link JPanel}
	 */
	@Override
	public JPanel getAdvancedConfigurationPanel() {
		return new JPanel();
	}

}
