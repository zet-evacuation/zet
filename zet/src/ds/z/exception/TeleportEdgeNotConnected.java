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
/*
 * PolygonNotClosedException.java
 *
 * Created on 01. December 2007, 23:06
 */

package ds.z.exception;

import ds.z.*;

/**
 * The exception <code>TeleportEdgeNotconnected</code> indicates that a
 * teleport has not an associated teleport edge. It can also occur if
 * it is connected to an teleport edge, which is not connected
 * to the original one.
 * @author Jan-Philipp Kappmeier
 */
public class TeleportEdgeNotConnected extends ValidationException {
	
	public TeleportEdgeNotConnected (TeleportEdge invalidEdge) {
		super (invalidEdge);
	}
	
	public TeleportEdgeNotConnected (TeleportEdge invalidEdge, String s) {
		super (invalidEdge, s);
	}
	
	public TeleportEdge getInvalidEdge () {
		return (TeleportEdge)getSource ();
	}
}