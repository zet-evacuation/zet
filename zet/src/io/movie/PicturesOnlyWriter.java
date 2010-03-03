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
 * Class PicturesOnlyWriter
 * Erstellt 17.11.2008, 00:58:01
 */

package io.movie;

import de.tu_berlin.math.coga.common.util.IOTools;
import java.util.Vector;
import javax.swing.JPanel;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PicturesOnlyWriter extends MovieWriter {

	/**
	 * <p>Creates a new filename, but ignores the actual frame number. The next free
	 * file with the suffix {@link #getFramename()} and {@link #FRAMEDIGITS} digits
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
