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
package ds.graph;

import localization.Localization;

/**
 * Represents a percentage value, i.e. the contained double value
 * must lie in [0,100].
 */
public class Percentage {
	
	/**
	 * The contained double value.
	 */
	private double percentage;
	
	/**
	 * Creates a new <code>Percentage</code> object containing
	 * the double value <code>percentage</code>. 
	 * If <code>percentage</code> is not within [0,100],
	 * an exception is thrown.
	 */
	public Percentage(double percentage){
		if (percentage < 0){
			throw new AssertionError(Localization.getInstance (
			).getString ("ds.PercentageNegativeException"));
		}
		if (percentage > 100){
			throw new AssertionError(Localization.getInstance (
			).getString ("ds.PercentageGreater100Exception"));
		}
		this.percentage = percentage;
	}
	
	/**
	 * Returns the contained double value. Result is guaranteed to be
	 * within [0,100].
	 * @return the contained double value lying in [0,100].
	 */
	public double getValue(){
		return percentage;
	}

}
