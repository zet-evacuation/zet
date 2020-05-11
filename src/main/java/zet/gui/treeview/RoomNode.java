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
package zet.gui.treeview;

import org.zetool.common.localization.LocalizationManager;
import de.zet_evakuierung.model.Room;
import de.zet_evakuierung.model.ZLocalization;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class RoomNode extends ProjectTreeNode<Room> {

	public RoomNode( Room zFormatData ) {
		super( zFormatData );
	}

	@Override
	public String getInformationText() {
		StringBuilder sb = new StringBuilder();
		sb.append( "Raum: " + zFormatData.getName() + "\n" );
		sb.append( "Koordinaten: " + zFormatData.getPolygon().getCoordinateString() + "\n" );
		sb.append( "Fläche: " + LocalizationManager.getManager().getFloatConverter().format( zFormatData.getPolygon().areaMeter() ) + "m²\n" );
		return sb.toString();
	}

	@Override
	public String toString() {
		return zFormatData.getName();
	}



}
