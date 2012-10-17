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

import ds.z.PlanPoint;
import java.io.IOException;

/**
 * This Exception has to be thrown, if the points of an edge are set via setPoints(), but the
 * points already have edges registered to them, that cannot be overwritten.
 */
@SuppressWarnings( "serial" )
public class PointsAlreadyConnectedException extends ValidationException {
	public PointsAlreadyConnectedException( PlanPoint connectedPoint ) {
		super( connectedPoint );
	}

	public PointsAlreadyConnectedException( PlanPoint connectedPoint, String s ) {
		super( connectedPoint, s );
	}

	/**
	 * Returns the point that caused the exception because it already was connected to some edge
	 * so that the new edge could not be added to it.
	 * @return an already connected point
	 */
	public PlanPoint getConnectedPoint() {
		return (PlanPoint) getSource();
	}
	
	/** Prohibits serialization. */
	private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException {
		throw new UnsupportedOperationException( "Serialization not supported" );
	}
}
