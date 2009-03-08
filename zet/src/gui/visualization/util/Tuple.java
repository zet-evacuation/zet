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
 * Class Tupel
 * Erstellt 11.06.2008, 00:14:08
 */

package gui.visualization.util;

/**
 * Represents a simple tuple of two double values that can be directly accessed.
 * @author Jan-Philipp Kappmeier
 */
public class Tuple {
	/** The first value of the tuple */
	public double x;
	/** The second value of the tuple */
	public double y;

	/** Initializes a new instance of the tuple with two values 
	 * @param x the first value of the tuple
	 * @param y the second value of the tuple
	 */
	public Tuple( double x, double y ) {
		this.x = x;
		this.y = y;
	}
};
