/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
package de.tu_berlin.math.coga.zet;

import org.zetool.common.localization.AbstractLocalization;
import org.zetool.common.localization.Localization;
import org.zetool.common.localization.LocalizationManager;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ZETLocalization2 {
	//private volatile static ZETLocalization singleton;
	public final static Localization loc = LocalizationManager.getManager().getLocalization( "de.tu_berlin.math.coga.zet.zevacuate" );

//
//	public static ZETLocalization getSingleton() {
//		// needed because once there is singleton available no need to acquire
//		// monitor again & again as it is costly
//		if( singleton == null )
//			synchronized( ZLocalization.class ) {
//				// this is needed if two threads are waiting at the monitor at the
//				// time when singleton was getting instantiated
//				if( singleton == null )
//					singleton = new ZETLocalization();
//			}
//		return singleton;
//	}
}
