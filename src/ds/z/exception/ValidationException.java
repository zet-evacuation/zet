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

package ds.z.exception;

/** An exception that is thrown during the execution of the check-methods in
 * Floor and Room. It indicates that something is wrong with the current
 * configuration of the z-model. Details can be extracted from the exception
 * message.
 *
 * @author Timon Kelter
 */
public class ValidationException extends RuntimeException {
	protected Object source;
	
	public ValidationException () {
		this (null, null);
	}
	/** @param source The 'ds.z.'- object that caused the exception. */
	public ValidationException (Object source) {
		this (source, null);
	}
	/** @param message A message that further explains this exception. */
	public ValidationException ( String message ) {
		this (null, message);
	}
	/** @param source The 'ds.z.'- object that caused the exception.
	 * @param message A message that further explains this exception. */
	public ValidationException (Object source, String message) {
		super (message);
		this.source = source;
	}
	
	/** @return The 'ds.z.'- object that caused the exception. */
	public Object getSource () {
		return source;
	}
}