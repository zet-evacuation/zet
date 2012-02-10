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
package algo.ca.rule;

import ds.ca.evac.StaticPotential;

/**
 * This class defines a tuple
 * of StaticPotentials and the Individuals distance from that ExitCell, 
 * to which the StaticPotentials refers. This class/tuple implements the
 * interface Comparable in order to sort a collection of tuples by their
 * distance to the ExitCell, to which the StaticPotential refers.
 * @author marcel
 *
 */
public class PotentialValueTuple implements Comparable<PotentialValueTuple>
{
	/**
	 * the Individuals distance from that ExitCell, 
	 * to which the StaticPotentials refers
	 */
	private int lengthOfWay;
	
	/**
	 * The StaticPotential of the tuple.
	 */
	private StaticPotential staticPotential;
	
	public PotentialValueTuple(int loW, StaticPotential sp)
	{
		this.lengthOfWay = loW;
		this.staticPotential = sp;
	}
	
	/**
	 * Returns the lenthOfWay attribute.
	 * @return The lenthOfWay attribute.
	 */
	public int getLengthOfWay()
	{
		return this.lengthOfWay;
	}
	
	/**
	 * Returns the StaticPotential attribute.
	 * @return The StaticPotential attribute.
	 */
	public StaticPotential getStaticPotential()
	{
		return this.staticPotential;
	}
	
	public int compareTo(PotentialValueTuple t)
	{
		if (t.getLengthOfWay() == this.getLengthOfWay())
			return 0;
		else if (t.getLengthOfWay() < this.getLengthOfWay())
			return 1;
		else
			return -1;
	}
}