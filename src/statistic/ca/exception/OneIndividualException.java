/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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


package statistic.ca.exception;
import ds.ca.evac.Individual;
/**
 *
 * @author Sylvie
 */

public class OneIndividualException extends RuntimeException {
	protected Individual ind;
	
	public OneIndividualException () {
		this (null, null);
	}
	/** @param ind The Individual that caused the exception. */
	public OneIndividualException (Individual ind) {
		this (ind, null);
	}
	/** @param message A message that further explains this exception. */
	public OneIndividualException ( String message ) {
		this (null, message);
	}
	/** @param ind The Individual that caused the exception.
	 * @param message A message that further explains this exception. */
	public OneIndividualException (Individual ind, String message) {
		super (message);
		this.ind = ind;
	}
	
	/** @return ind The Individual that caused the exception. */
	public Object getIndividual () {
		return ind;
	}
}