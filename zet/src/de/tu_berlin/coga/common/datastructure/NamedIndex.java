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
package de.tu_berlin.coga.common.datastructure;

/**
 * A string that is connected to an integer.
 * @author Timon Kelter
 */
public class NamedIndex extends Tuple<String, Integer> {
	public NamedIndex( String name, int index ) {
		super( name, index );
	}

	public String getName() {
		return getU();
	}

	public int getIndex() {
		return getV();
	}

	@Override
	public String toString() {
		return getU();
	}
}