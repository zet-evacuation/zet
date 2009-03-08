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
package algo.ca;

import ds.ca.Cell;

/**
 * The data structure used by the algorithm for calculating the static potentials
 * @author Matthias Woste
 *
 */
public class SmoothingTupel {

	/**
	 * Reference to the cell
	 */
	Cell cell;
	
	/**
	 * potential value
	 */
	double value;
	
	/**
	 * real distance
	 */
	double distanceValue;
	
	/**
	 * number of the cells that could affect this cell
	 */	
	int numberOfParents;
	
	/**
	 * sum of the potentials of the parent cells
	 */
	double sumOfValuesOfParents;
	
	/**
	 * Constructs a new SmoothingTupel for a cell
	 * @param c the cell
	 * @param v the initial potential: the distance (diagonal: 14, horizontal or vertical: 10)
	 * @param n initial 1
	 * @param s initial the potential of the first parent cell
	 */
	public SmoothingTupel(Cell c, double v, double d, int n, double s){
		cell = c;
		value = v;
		distanceValue = d;
		numberOfParents = n;
		sumOfValuesOfParents = s;
	}
	
	/**
	 * Updates the values of this tupel
	 * @param valueOfParent potential of the parent
	 * @param distance distance between this cell and its parent (diagonal: 14, horizontal or vertical: 10)
	 */
	public void addParent(double valueOfParent, int distance){
		value = Math.min(value, valueOfParent + distance);
		numberOfParents++;
		sumOfValuesOfParents += valueOfParent;
	}
	
	public void addDistanceParent(double valueOfParent, double distance){
		distanceValue = Math.min(distanceValue, valueOfParent + distance);
	}
	
	/**
	 * Returns the Cell of the SmoothingTupel
	 * @return the Cell
	 */
	public Cell getCell(){
		return cell;
	}
	
	/**
	 * Returns the Potential of the Cell specified by this SmoothingTupel
	 * @return the potential
	 */
	public double getValue(){
		return value;
	}
	
	/**
	 * Returns the distance of the Cell specified by this SmoothingTupel
	 * @return the distance
	 */
	public double getDistanceValue(){
		return distanceValue;
	}
	
	/**
	 * Applies the smoothing-algorithm to this tupel. It's based on the formula:
	 * potential = Math.round((3*value+sumOfValuesOfParents)/(3+numberOfParents))
	 */
	public void applySmoothing(){
		value = (3*value+sumOfValuesOfParents)/(3+numberOfParents);
	}
}