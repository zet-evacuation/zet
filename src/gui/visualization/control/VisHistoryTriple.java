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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package gui.visualization.control;

/**
 * This class manages Triple of 3 arbitrary values, each of arbitrary type.
 * 
 * @param <T1> 
 * @param <T2> 
 * @param <T3> 
 * @author marcel
 *
 */
public class VisHistoryTriple<T1 extends Comparable<T1>, T2, T3> implements Comparable<VisHistoryTriple<T1, T2, T3>> {

	private T1 value1;
	private T2 value2;
	private T3 value3;

	/**
	 * Creates a new Triple.
	 * @param v1 First Value of generic Type T1
	 * @param v2 Second Value of generic Type T2
	 * @param v3 Third Value of generic Type T3
	 */
	public VisHistoryTriple( T1 v1, T2 v2, T3 v3 ) {
		this.value1 = v1;
		this.value2 = v2;
		this.value3 = v3;
	}

	/**
	 * Returns the first Value of the triple.
	 * @return the first Value of the triple
	 */
	public T1 getFirstValue() {
		return this.value1;
	}

	/**
	 * Returns the second value of the triple.
	 * @return the second Value of the Triple
	 */
	public T2 getSecondValue() {
		return this.value2;
	}

	/**
	 * Returns the third value of the triple.
	 * @return the third value of the triple
	 */
	public T3 getThirdValue() {
		return this.value3;
	}

	/**
	 * Sets the first value to vale of parameter v1
	 * @param v1 the first parameter
	 */
	public void setFirstValue( T1 v1 ) {
		this.value1 = v1;
	}

	/**
	 * Sets the second value to vale of parameter v2
	 * @param v2 the second parameter
	 */
	public void setSecondValue( T2 v2 ) {
		this.value2 = v2;
	}

	/**
	 * Sets the third value to vale of parameter v3
	 * @param v3 the third parameter
	 */
	public void setThirdValue( T3 v3 ) {
		this.value3 = v3;
	}

	/**
	 * Compares to another {@code VisHistoryTriple}
	 * @param v1 the triple
	 */
	public int compareTo( VisHistoryTriple<T1, T2, T3> v1 ) {
		return value1.compareTo( v1.getFirstValue() );
	}
}
