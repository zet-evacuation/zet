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
package ds.z.exception;

import ds.z.TeleportEdge;
import java.io.IOException;


/**
 * This Exception has to be thrown, if a teleport edge is connected to another teleport edge on the same floor.
 */
@SuppressWarnings( "serial" )
public class TeleportEdgeInvalidTargetException extends ValidationException {
	
	public TeleportEdgeInvalidTargetException (TeleportEdge invalidEdge) {
		super (invalidEdge);
	}
	
	public TeleportEdgeInvalidTargetException (TeleportEdge invalidEdge, String s) {
		super (invalidEdge, s);
	}
	
	public TeleportEdge getInvalidEdge () {
		return (TeleportEdge)getSource ();
	}
	
	/** Prohibits serialization. */
	private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException {
		throw new UnsupportedOperationException( "Serialization not supported" );
	}
}