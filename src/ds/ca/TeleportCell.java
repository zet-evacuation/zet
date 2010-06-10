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
 * TeleportCell.java
 * Created: Jun 8, 2010,5:20:12 PM
 */
package ds.ca;

import java.util.ArrayList;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TeleportCell extends DoorCell {

	/**
	 * Constructs an empty DoorCell which is NOT connected with any other DoorCell
	 * and has the standard Speed-Factor "STANDARD_DOORCELL_SPEEDFACTOR".
	 * @param x x-coordinate of the cell in the room, 0 <= x <= width-1
	 * @param y y-coordinate of the cell in the room, 0 <= y <= height-1
	 */
	public TeleportCell( int x, int y ) {
		this( null, DoorCell.STANDARD_DOORCELL_SPEEDFACTOR, x, y );
	}

	/**
	 * Constructs an empty DoorCell which is NOT connected with any other DoorCell
	 * and has a speed-factor of <code>speedfactor</code>
	 * @param speedFactor The speedfactor for this cell
	 * @param x x-coordinate of the cell in the room, 0 <= x <= width-1
	 * @param y y-coordinate of the cell in the room, 0 <= y <= height-1
	 */
	public TeleportCell( double speedFactor, int x, int y ) {
		this( speedFactor, x, y, null );
	}

	public TeleportCell( double speedFactor, int x, int y, Room room ) {
		super( speedFactor, x, y, room );
		graphicalRepresentation = '?';
	}

	/**
	 * Constructs an empty DoorCell which is connected with the DoorCell "nextDoor"
	 * and has the standard Speed-Factor "STANDARD_DOORCELL_SPEEDFACTOR".
	 * @param nextDoor Defines the reference to the Door-Cell of the room you can
	 * enter by using this Door-Cell of the current room.
	 * @param x x-coordinate of the cell in the room, 0 <= x <= width-1
	 * @param y y-coordinate of the cell in the room, 0 <= y <= height-1
	 */
	/*   public DoorCell(DoorCell nextDoor, int x, int y)
	{
	this(null, DoorCell.STANDARD_DOORCELL_SPEEDFACTOR, nextDoor, x, y);
	} */
	/**
	 * Constructs a DoorCell with the defined values.
	 * @param individual Defines the individual that occupies the cell. If the cell
	 * is not occupied, the value is set to "null".
	 * @param speedFactor Defines how fast the cell can be crossed. The value should
	 * be a rational number greater than or equal to 0 and smaller or equal to 1.
	 * Otherwise the standard value "STANDARD_DOORCELL_SPEEDFACTOR" is set.
	 * @param x x-coordinate of the cell in the room, 0 <= x <= width-1
	 * @param y y-coordinate of the cell in the room, 0 <= y <= height-1
	 */
	public TeleportCell( Individual individual, double speedFactor, int x, int y ) {
		super( individual, speedFactor, x, y );
		graphicalRepresentation = '?';
	}

	@Override
	protected ArrayList<Cell> getNeighbours( boolean passableOnly, boolean freeOnly ) {
		ArrayList<Cell> neighbours = new ArrayList<Cell>();

		if( nrOfNextDoors() == 0 )
			neighbours.add( this );
		else
			neighbours.add( getNextDoor( 0 ) );

		return neighbours;
	}

	/**
	 * Adds a DoorCell which is connected to this DoorCell and registers itself
	 * as a connected DoorCell in "door".
	 * @param door Defines the reference to the Door-Cell of the room you can
	 * enter by using this Door-Cell of the current room.
	 * @throws IllegalArgumentException if the parameter "Door" is null
	 * or if "Door" has already been added to the list of doors.
	 */
	@Override
	public void addNextDoor( DoorCell door ) throws IllegalArgumentException {
		if( nrOfNextDoors() == 0 )
			super.addTarget( door );
	}

}
