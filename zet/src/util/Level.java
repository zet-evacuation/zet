/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
 * Level.java
 */
package util;

/**
 * Represents relative position in one dimension, the naming is about levels (of
 * height). Each level knows the opposite direction.
 * @author Jan-Philipp Kappmeier
 * @see Direction
 */
public enum Level {
  /** Higher level. */
	Higher(),
	/** The same level. */
	Equal(),
	/** Higher level. */
	Lower( Higher );

	/** The opposite level of this level. */
	private Level inverseLevel;

	/**
	 * Creates a new instance and sets the inverse level to the same.
	 */
	private Level() {
		this.inverseLevel = this;
	}

	/**
	 * Creates a new instance and sets the inverse level.
	 * @param inverseLevel the inverse level
	 */
	private Level( Level inverseLevel ) {
		this.inverseLevel = inverseLevel;
		inverseLevel.setInverse( this );
	}

	/**
	 * Sets the inverse level.
	 * @param inverseLevel the inverse level
	 */
	private void setInverse( Level inverseLevel ) {
		this.inverseLevel = inverseLevel;
	}

	/**
	 * Returns the inverse level.
	 * @return the inverse level
	 */
	public Level getInverse() {
		return inverseLevel;
	}
}
