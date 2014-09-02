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

/*
 * Areas.java
 * Created on 11. Dezember 2007, 21:56
 */

package gui.editor;

/**
 * This enumeration gives all types of areas that exists (except barriers). It
 * is used to mark what kind of areas shall be displayed on the screen. The user
 * may want some kinds of areas to be hidden if he is not interested in them for
 * some reason.
 * @author Timon Kelter, Jan-Philipp Kappmeier
 * @deprecated Should use the ds.z version of areas
 */
@Deprecated
public enum Areas {
	/** Delay areas. */
	Delay,
	/** Stair areas. */
	Stair,
	/** Inaccessible areas. */
	Inaccessible,
	/** Save areas. */
	Save,
	/** Evacuation areas. */
	Evacuation,
	/** Assignment areas. */
	Assignment,
	/** Teleportation areas. */
	Teleportation;
}