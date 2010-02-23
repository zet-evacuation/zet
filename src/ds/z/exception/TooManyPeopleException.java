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
/**
 * Class ToManyPeopleException
 * Erstellt 19.05.2008, 14:32:11
 */

package ds.z.exception;

import ds.z.PlanPolygon;

/**
 * An exception, that is thrown when too many persons are in a {@link ds.z.Room} or {@link ds.z.Area}.
 * @author Jan-Philipp Kappmeier
 */
public class TooManyPeopleException extends ValidationException {

	/**
	 * @param polygon The Poylgon which contains too much persons. 
	 * @param message A message that further describes the exception. */
	public TooManyPeopleException (PlanPolygon polygon, String message) {
		super (polygon, message);
	}
        
	/**
	 * @param polygon the Poylgon which contains too much persons.
	 */
	public TooManyPeopleException ( PlanPolygon polygon ) {
		super ( polygon );
	}
	
	/**
	 * @return The Poylgon which contains too much persons.
	 */
	public PlanPolygon getPolygon () {
		return (PlanPolygon)getSource ();
	}
}
