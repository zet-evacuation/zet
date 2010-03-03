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
 * Class CullingLocation
 * Erstellt 02.05.2008, 17:56:36
 */

package opengl.helper;

/**
 * This enumeration describes the possible types of relation between a
 * {@link Frustum} and an object.
 * @author Jan-Philipp Kappmeier
 */
public enum CullingLocation {
    outside,
		inside,
		intersect;
}
