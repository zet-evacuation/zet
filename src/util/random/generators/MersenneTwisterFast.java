/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/**
 * Class MersenneTwisterFast
 * Erstellt 04.05.2008, 23:47:48
 */

package util.random.generators;

import util.random.GeneralRandom;

/**
 * A wrapper class for the general use of random generators using {@link util.random.MersenneTwisterFast}.
 * @author Jan-Philipp Kappmeier
 */
public class MersenneTwisterFast extends util.random.MersenneTwisterFast implements GeneralRandom {
	/**
	 * @inheritDoc
	 */
	public MersenneTwisterFast() {
		super();
	}

	/**
	 * @inheritDoc
	 */
	public MersenneTwisterFast( final long seed ) {
		super( seed );
	}


	/**
	 * @inheritDoc
	 */
	public MersenneTwisterFast( final int[] array ) {
		super( array );
	}

	public String getName() {
		return "Mersene Twister MT19937 Fast";		
	}

	public String getDesc() {
		return "";
	}
}
