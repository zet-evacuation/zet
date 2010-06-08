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
package ds.ca;

import java.util.ArrayList;

/**
 * A Door-Cell is special type of cell and therefore inherits properties and methods
 * from the abstract class Cell. Door-Cells are portals to move between two roomes.
 * Because of that they keep a reference to another Door-Cell, which belongs to the
 * room you can enter by using this Door-Cell of the current room.
 * @author marcel
 *
 */
public class DoorCell extends Cell implements Cloneable {

	/**
	 * Constant defining the standard Speed-Factor of a Door-Cell, which may be < 1
	 */
	public static final double STANDARD_DOORCELL_SPEEDFACTOR = 0.8d;
	/**
	 * Keeps the reference to the Door-Cell of the room you can enter by using the
	 * Door-Cell of the current room.
	 */
	private ArrayList<DoorCell> nextDoors;

	/**
	 * Constructs an empty DoorCell which is NOT connected with any other DoorCell
	 * and has the standard Speed-Factor "STANDARD_DOORCELL_SPEEDFACTOR".
	 * @param x x-coordinate of the cell in the room, 0 <= x <= width-1
	 * @param y y-coordinate of the cell in the room, 0 <= y <= height-1
	 */
	public DoorCell( int x, int y ) {
		this( null, DoorCell.STANDARD_DOORCELL_SPEEDFACTOR, x, y );
	}

	/**
	 * Constructs an empty DoorCell which is NOT connected with any other DoorCell
	 * and has a speed-factor of <code>speedfactor</code>
	 * @param speedFactor The speedfactor for this cell
	 * @param x x-coordinate of the cell in the room, 0 <= x <= width-1
	 * @param y y-coordinate of the cell in the room, 0 <= y <= height-1
	 */
	public DoorCell( double speedFactor, int x, int y ) {
		this( speedFactor, x, y, null );
	}

	public DoorCell( double speedFactor, int x, int y, Room room ) {
		super( null, speedFactor, x, y, room );
		graphicalRepresentation = '!';
		nextDoors = new ArrayList<DoorCell>();
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
	public DoorCell( Individual individual, double speedFactor, int x, int y ) {
		super( individual, speedFactor, x, y );
		this.nextDoors = new ArrayList<DoorCell>();
		graphicalRepresentation = '!';
	}

	/**
	 * Returns a door you can enter by using this DoorCell.
	 * @return The door defined by the parameter index.
	 * @param index The index which defines the requested DoorCell.
	 */
	public DoorCell getNextDoor( int index ) {
		return nextDoors.get( index );
	}

	/**
	 * Adds a DoorCell which is connected to this DoorCell and registers itself
	 * as a connected DoorCell in "door".
	 * @param door Defines the reference to the Door-Cell of the room you can 
	 * enter by using this Door-Cell of the current room.
	 * @throws IllegalArgumentException if the parameter "Door" is null
	 * or if "Door" has already been added to the list of doors.
	 */
	public void addNextDoor( DoorCell door ) throws IllegalArgumentException {
		internalAddNextDoor( door );
		door.internalAddNextDoor( this );
	}

	/**
	 * Removes the specified door.
	 * @param door The door which shall be removed.
	 */
	public void removeNextDoor( DoorCell door ) {
		this.internalRemoveNextDoor( door );
		door.internalRemoveNextDoor( this );
	}

	/** 
	 * Removes all next doors from this door cell and notifys the
	 * former next doors about the removal.
	 */
	public void removeAllNextDoors() {
		while( nextDoors.size() > 0 )
			this.removeNextDoor( nextDoors.get( 0 ) );
	}

	/**
	 * <p>Removes all next doors from this door cell but does <b>not</b> notify
	 * the next doors about the removal.</p>
	 * <p><b>Warning!</b> Use this method only if you know what you do. It may
	 * destroy the cosistency of the CA. This method is basically useful if a
	 * {@link ds.ca.CellularAutomaton} should be clonded. In that case the
	 * next doors have to be set to the new cloned instances and the old
	 * instances should be deleted without disturbing the original automaton.
	 * </p>
	 */
	public void removeAllNextDoorsWithoutNotify() {
		while( this.nrOfNextDoors() > 0 )
			internalRemoveNextDoor( this.getNextDoor( 0 ) );
	}

	/**
	 * Returns the number of DoorCells which are connected to this DoorCell.
	 * @return The number of DoorCells which are connected to this DoorCell.
	 */
	public int nrOfNextDoors() {
		return nextDoors.size();
	}

	/**
	 * Checks whether the list of nextDoorCells contains the specified door.
	 * @param door The door to checked (if in list or not).
	 * @return "True", if the list of nextDoors contains "door", false if not.
	 */
	public boolean containsNextDoor( DoorCell door ) {
		return nextDoors.contains( door );
	}

	/**
	 * Returns the index of the defined DoorCell "door".
	 * @param door The door whose index is requested.
	 * @return The index of the defined DoorCell "door", or -1, if the specified
	 * door is not contained in the list of DoorCells.
	 */
	public int getIndexOf( DoorCell door ) {
		return nextDoors.indexOf( door );
	}

	/**
	 * Changes the Speed-Factor of the Door-Cell to the specified value.
	 * @param speedFactor Defines how fast the cell can be crossed. The value should
	 * be a rational number greater than or equal to 0 and smaller or equal to 1. 
	 * Otherwise the standard value "STANDARD_DOORCELL_SPEEDFACTOR" is set.
	 */
	public void setSpeedFactor( double speedFactor ) {
		if( (speedFactor >= 0) && (speedFactor <= 1) ) {
			this.speedFactor = speedFactor;
		} else {
			this.speedFactor = DoorCell.STANDARD_DOORCELL_SPEEDFACTOR;
		}
	}

	/**
	 * Returns a copy of itself as a new Object.
	 */
	@Override
	public DoorCell clone() {
		return clone( false );
	}

	@Override
	public DoorCell clone( boolean cloneIndividual ) {
		DoorCell aClone = new DoorCell( this.getX(), this.getY() );
		basicClone( aClone, cloneIndividual );
		for( DoorCell cell : nextDoors ) {
			aClone.addNextDoor( cell );
		}
		return aClone;
	}

	@Override
	public String toString() {
		return "D;" + super.toString();
	}

	@Override
	protected ArrayList<Cell> getNeighbours( boolean passableOnly, boolean freeOnly ) {
		ArrayList<Cell> neighbours = super.getNeighbours( passableOnly, freeOnly );
		for( DoorCell door : this.nextDoors ) {
			if( !freeOnly || door.individual == null ) {
				neighbours.add( door );
			}
		}

		return neighbours;
	}

	/**
	 * This is a helper method for internal synchronisation. Call this to add
	 * a nextDoor without being called back.
	 * @param door a door you want to add to the nextDoors list.
	 */
	private void internalAddNextDoor( DoorCell door ) {
		if( door == null ) {
			throw new IllegalArgumentException( "Parameter Door must not be \"null\"" );
		}
		if( nextDoors.contains( door ) ) {
			if( !door.containsNextDoor( this ) ) {
				throw new IllegalArgumentException( "\"Door\" has already been added!" );
			}
		} else {
			nextDoors.add( door );
		}
	}

	/**
	 * This is a helper method for internal synchronisation. Call this to remove
	 * a nextDoor without being called back.
	 * @param door a door you want to remove from the nextDoors list.
	 */
	private void internalRemoveNextDoor( DoorCell door ) {
		if( this.nextDoors.remove( door ) == false ) {
			throw new IllegalArgumentException( "The door you tried to remove is not connected to this cell." );
		}
	}

	/**
	 * Adds a target cell only, but does not add this cell as a target of the
	 * target. Thus this does not implement a real door, but a one-way only door.
	 * @param door the target cell
	 */
	protected void addTarget( DoorCell door ) {
		if( !nextDoors.contains( door ) )
			nextDoors.add( door );
	}
}

