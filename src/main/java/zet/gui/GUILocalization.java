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
package zet.gui;

import org.zetool.common.localization.Localization;
import org.zetool.common.localization.LocalizationManager;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GUILocalization {
	public final static Localization loc = LocalizationManager.getManager().getLocalization( "zet.gui.GUILocalization" );
					
//	private volatile static GUILocalization singleton;
//
//	private GUILocalization() throws MissingResourceException {
//		super( "zet.gui.GUILocalization" );
//	}
//
//	public static GUILocalization getSingleton() {
//		if( singleton == null )
//			synchronized( GUILocalization.class ) {
//				// thread safe: check again if pointer is null
//				if( singleton == null )
//					singleton = new GUILocalization();
//			}
//		return singleton;
//	}
}
