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
import java.util.ArrayList;

/**
 *
 * @author Sylvie
 */
public class GroupOfIndividualsException extends RuntimeException {

	protected ArrayList<Individual> indgroup;

	public GroupOfIndividualsException() {
		this( null, null );
	}

	/**
	 * @param indgroup The Individual that caused the exception.
	 */
	public GroupOfIndividualsException( ArrayList<Individual> indgroup ) {
		this( indgroup, null );
	}

	/**
	 * @param message A message that further explains this exception.
	 */
	public GroupOfIndividualsException( String message ) {
		this( null, message );
	}

	/**
	 * @param indgroup The Individual that caused the exception.
	 * @param message A message that further explains this exception.
	 */
	public GroupOfIndividualsException( ArrayList<Individual> indgroup, String message ) {
		super( message );
		this.indgroup = indgroup;
	}

	/**
	 * @return ind The Individual that caused the exception.
	 */
	public ArrayList<Individual> getGroupOfIndividuals() {
		return indgroup;
	}
}
