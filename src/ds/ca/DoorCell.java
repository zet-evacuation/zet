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
 * @author Marcel Preuß, Jan-Philipp Kappmeier
 *
 */
public class DoorCell extends BaseTeleportCell<DoorCell> {

	/** Constant defining the standard Speed-Factor of a Door-Cell, which may be < 1 */
	public static final double STANDARD_DOORCELL_SPEEDFACTOR = 0.8d;

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
		super( speedFactor, x, y, room );
	}

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
	}

	/**
	 * Adds a DoorCell which is connected to this DoorCell and registers itself
	 * as a connected DoorCell in "door".
	 * @param door Defines the reference to the Door-Cell of the room you can
	 * enter by using this Door-Cell of the current room.
	 * @throws IllegalArgumentException if the parameter "Door" is null
	 * or if "Door" has already been added to the list of doors.
	 */
	public void addTarget( DoorCell door ) throws IllegalArgumentException {
		internalAddNextDoor( door );
		door.internalAddNextDoor( this );
	}

	/**
	 * Removes the specified door.
	 * @param door The door which shall be removed.
	 */
	public void removeTarget( DoorCell door ) {
		this.internalRemoveNextDoor( door );
		door.internalRemoveNextDoor( this );
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
		while( this.targetCount() > 0 )
			internalRemoveNextDoor( this.getTarget( 0 ) );
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
		for( DoorCell cell : teleportTargets )
			aClone.addTarget( cell );
		return aClone;
	}

	@Override
	public String toString() {
		return "D;" + super.toString();
	}

	@Override
	protected ArrayList<Cell> getNeighbours( boolean passableOnly, boolean freeOnly ) {
		ArrayList<Cell> neighbours = super.getNeighbours( passableOnly, freeOnly );
		for( DoorCell door : this.teleportTargets )
			if( !freeOnly || door.individual == null )
				neighbours.add( door );

		return neighbours;
	}

	/**
	 * This is a helper method for internal synchronisation. Call this to add
	 * a nextDoor without being called back.
	 * @param door a door you want to add to the teleportTargets list.
	 */
	private void internalAddNextDoor( DoorCell door ) {
		if( door == null )
			throw new IllegalArgumentException( "Parameter Door must not be \"null\"" );
		if( teleportTargets.contains( door ) ) {
			if( !door.containsTarget( this ) )
				throw new IllegalArgumentException( "\"Door\" has already been added!" );
		} else
			teleportTargets.add( door );
	}

	/**
	 * This is a helper method for internal synchronisation. Call this to remove
	 * a nextDoor without being called back.
	 * @param door a door you want to remove from the teleportTargets list.
	 */
	private void internalRemoveNextDoor( DoorCell door ) {
		if( this.teleportTargets.remove( door ) == false )
			throw new IllegalArgumentException( "The door you tried to remove is not connected to this cell." );
	}

	/**
	 * Changes the Speed-Factor of the Door-Cell to the specified value.
	 * @param speedFactor Defines how fast the cell can be crossed. The value should
	 * be a rational number greater than or equal to 0 and smaller or equal to 1.
	 * Otherwise the standard value "STANDARD_DOORCELL_SPEEDFACTOR" is set.
	 */
	@Override
	public void setSpeedFactor( double speedFactor ) {
		this.speedFactor = (speedFactor >= 0) && (speedFactor <= 1) ? speedFactor : DoorCell.STANDARD_DOORCELL_SPEEDFACTOR;
	}
}

