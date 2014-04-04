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

package de.tu_berlin.math.coga.common.localization;

/**
 * A default implementation of the {@link AbstractLocalization} class that can be used
 * as a default by libraries that want to give users access to localization.
 * @author Jan-Philipp Kappmeier
 */
public final class DefaultLocalization extends AbstractLocalization {
	/** The singleton instance. */
	private volatile static DefaultLocalization singleton;

	/**
	 * Initializes the default localization instance.
	 */
	private DefaultLocalization() {
		super( "de.tu_berlin.math.coga.common.localization.default" );
	}

	/**
	 * Returns the singleton instance of the default localization.
	 * @return the singleton instance of the default localization
	 */
	public static DefaultLocalization getSingleton() {
		// needed because once there is singleton available no need to acquire
		// monitor again & again as it is costly
		if( singleton == null ) {
			synchronized( DefaultLocalization.class ) {
				// this is needed if two threads are waiting at the monitor at the
				// time when singleton was getting instantiated
				if( singleton == null ) {
					singleton = new DefaultLocalization();
				}
			}
		}
		return singleton;
	}
}
