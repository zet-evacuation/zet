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

package algo.ca;

import ds.ca.evac.EvacCell;

/**
 * The data structure used by the algorithm for calculating the static potentials.
 * @author Matthias Woste
 *
 */
public class SmoothingTuple {
	/** Reference to the cell. */
	EvacCell cell;
	/** potential value. */
	double value;
	/** Real distance. */
	double distanceValue;
	/** Number of the cells that could affect this cell. */
	int numberOfParents;
	/** Sum of the potentials of the parent cells. */
	double sumOfValuesOfParents;

	/**
	 * Constructs a new SmoothingTuple for a cell.
	 * @param c the cell
	 * @param v the initial potential: the distance (diagonal: 14, horizontal or vertical: 10)
   * @param d
	 * @param n initial 1
	 * @param s initial the potential of the first parent cell
	 */
	public SmoothingTuple( EvacCell c, double v, double d, int n, double s) {
		cell = c;
		value = v;
		distanceValue = d;
		numberOfParents = n;
		sumOfValuesOfParents = s;
	}

	/**
	 * Updates the values of this tuple.
	 * @param valueOfParent potential of the parent
	 * @param distance distance between this cell and its parent (diagonal: 14, horizontal or vertical: 10)
	 */
	public void addParent(double valueOfParent, int distance){
		value = Math.min(value, valueOfParent + distance);
		numberOfParents++;
		sumOfValuesOfParents += valueOfParent;
	}

	public void addDistanceParent(double valueOfParent, double distance) {
		distanceValue = Math.min(distanceValue, valueOfParent + distance);
	}

	/**
	 * Returns the EvacCell of the SmoothingTuple.
	 * @return the EvacCell
	 */
	public EvacCell getCell() {
		return cell;
	}

	/**
	 * Returns the Potential of the EvacCell specified by this SmoothingTuple.
	 * @return the potential
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Returns the distance of the EvacCell specified by this SmoothingTuple.
	 * @return the distance
	 */
	public double getDistanceValue() {
		return distanceValue;
	}

	/**
	 * Applies the smoothing-algorithm to this tuple. It's based on the formula:
	 * potential = Math.round((3*value+sumOfValuesOfParents)/(3+numberOfParents))
	 */
	public void applySmoothing() {
		value = (3*value+sumOfValuesOfParents)/(3+numberOfParents);
	}
}