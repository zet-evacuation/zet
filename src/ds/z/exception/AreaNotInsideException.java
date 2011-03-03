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
package ds.z.exception;

import ds.z.Area;
import ds.z.Room;

/** Is thrown when an area is not located inside the room which is is associated to.
 * @author Joscha Kulbatzki, Jan-Philipp Kappmeier
 */
public class AreaNotInsideException extends ValidationException {
	Area<?> area;

	public AreaNotInsideException( Room room, Area<?> area ) {
		this( room, area, "" );
	}

	public AreaNotInsideException( Room room, Area<?> area, String s ) {
		super( room, s );
		this.area = area;
	}

	public Area<?> getArea() {
		return (Area<?>)area;
	}

	/**
	 * @return the room which contains an area that is not inside.
	 */
	@Override
	public Room getSource() {
		return (Room)source;
	}
}
