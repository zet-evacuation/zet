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
 * RoomRasterSquare.java
 *
 */
package de.tu_berlin.math.coga.zet.converter;

import ds.z.DelayArea;
import ds.z.EvacuationArea;
import ds.z.InaccessibleArea;
import ds.z.Room;
import ds.z.SaveArea;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import de.tu_berlin.math.coga.common.util.Direction8;
import de.tu_berlin.math.coga.common.util.Level;
import ds.z.TeleportArea;

/**
 * The {@code RoomRasterSquare} is a special type of {@link RasterSquare} containing
 * additional information for rooms. The isInaccessible areas and speed factors for
 * delayed areas are stored. Additionally, it has possibility to hold connections
 * up to four connected squares through doors.
 * @author Jan-Philipp Kappmeier
 */
public class RoomRasterSquare extends RasterSquare {
	/** The speed factor for this cell */
	private double speedFactor;
	/** The multiplicative speed factor if you are leaving this square going down (in height). */
	private double downSpeedFactor;
	/** The multiplicative speed factor if you are leaving this square going up (in height). */
	private double upSpeedFactor;	// Warning! Do never initialize attractivity here because it is initialized by the super!
	/** The attractiveness for this cell (only used by ExitCells) */
	private int attractiveness;
	/** The name of the cell. (Only used by ExitCells) */
	private String name;
	/** Contains all properties of the square */
	private EnumSet<RoomRasterProperty> properties;
	/** Contains whether the square can reach the neighbor squares in the matrix. */
	private EnumSet<Direction8> passable;
	/** Contains whether the neighbor squares in the matrix lie higher, equal or lower. */
	private EnumMap<Direction8, Level> levels;

	/**
	 * Creates a new instance of RoomRasterSquare
	 * @param r the room that is rasterized
	 * @param column the column in the virtual raster-array
	 * @param row the row in the virtual raster-array
	 * @param raster the width and height of the raster square
	 */
	public RoomRasterSquare( Room r, int column, int row, int raster ) {
		super( r, column, row, raster );

		passable = EnumSet.allOf( Direction8.class );
		levels = new EnumMap<Direction8, Level>( Direction8.class );
		for( Direction8 direction : Direction8.values() )
			levels.put( direction, Level.Equal );
		if( properties != null )
			throw new IllegalStateException( "wie kann das sein properties!=null RoomRasterSquare" );
		properties = EnumSet.noneOf( RoomRasterProperty.class );
		setProperty( RoomRasterProperty.ACCESSIBLE );
		this.downSpeedFactor = 1.0;
		this.upSpeedFactor = 1.0;
		
		this.check();
	}

	private void check() {
		Room r = (Room) getPolygon();
		// Exists: Array. Check for all Squares if they are contained in any Area
		// If true: calculate new delay or isAccessible
		this.speedFactor = 1.0;
		for( DelayArea d : r.getDelayAreas() )
			if( d.contains( this.getSquare() ) )
				this.speedFactor *= d.getSpeedFactor();

		for( InaccessibleArea i : r.getInaccessibleAreas() )
			setProperty( RoomRasterProperty.ACCESSIBLE, !i.contains( this.getSquare() ) & isAccessible() );

		for( SaveArea saveArea : r.getSaveAreas() )
			setProperty( RoomRasterProperty.SAVE, this.isSave() || (saveArea.contains( this.getSquare() ) & isAccessible()) );

		for( EvacuationArea evacArea : r.getEvacuationAreas() ) {
			if( this.isExit() || (evacArea.contains( this.getSquare() ) & isAccessible()) ) {
				setProperty( RoomRasterProperty.EXIT, true );
				if( evacArea.contains( this.getSquare() ) ) {
					setAttractivity( evacArea.getAttractivity() );
					setName( evacArea.getName() );
				}
			} else {
				setProperty( RoomRasterProperty.EXIT, false );
			}
		}

		for( TeleportArea teleportArea : r.getTeleportAreas() )
			setProperty( RoomRasterProperty.TELEPORT, this.isTeleport() || (teleportArea.contains( this.getSquare() ) & isAccessible()) );

	}

	private void clearProperty( RoomRasterProperty property ) {
		properties.remove( property );
	}
	
	private boolean getProperty( RoomRasterProperty property ) {
		return properties.contains( property );
	}

	final void setProperty( RoomRasterProperty property ) {
		properties.add( property );
	}

	private void setProperty( RoomRasterProperty property, boolean value ) {
		if( value )
			setProperty( property );
		else
			clearProperty( property );
	}

	void clearAccessible() {
		clearProperty( RoomRasterProperty.ACCESSIBLE );
	}

	/**
	 * Returns whether (the majority of) this square is isAccessible in the original Z-Plan. 
	 * @return whether (the majority of) this square is isAccessible in the original Z-Plan. 
	 */
	public boolean isAccessible() {
		return getProperty( RoomRasterProperty.ACCESSIBLE );
	}

	void setAccessible() {
		setProperty( RoomRasterProperty.ACCESSIBLE );
	}

	/**
	 * 
	 * @return
	 */
	public boolean isInaccessible() {
		return !getProperty( RoomRasterProperty.ACCESSIBLE );
	}

	void clearStair() {
		clearProperty( RoomRasterProperty.STAIR );
	}

	public boolean isStair() {
		return getProperty( RoomRasterProperty.STAIR );
	}

	void setStair() {
		setProperty( RoomRasterProperty.STAIR );
	}

	void clearSave() {
		clearProperty( RoomRasterProperty.SAVE );
	}

	public boolean isSave() {
		return getProperty( RoomRasterProperty.SAVE );
	}

	void setSave() {
		setProperty( RoomRasterProperty.SAVE );
	}

	void clearExit() {
		clearProperty( RoomRasterProperty.EXIT );
	}

	/**
	 * Returns wheather this square is an exit, or not.
	 * @return true if this square is an exit
	 */
	public boolean isExit() {
		return getProperty( RoomRasterProperty.EXIT );
	}

	void setExit() {
		setProperty( RoomRasterProperty.EXIT );
	}

	void clearTeleport() {
		clearProperty( RoomRasterProperty.TELEPORT );
	}

	public boolean isTeleport() {
		return getProperty( RoomRasterProperty.TELEPORT );
	}

	void setTeleport() {
		setProperty( RoomRasterProperty.TELEPORT );
	}

	public int getAttractivity() {
		return attractiveness;
	}

	void setAttractivity( int attractiveness ) {
		this.attractiveness = attractiveness;
	}

	/**
	 * Returns the speed factor of this square. The speed factor describes how fast
	 * the square can be traversed (averaged). It lies between zero and one and is 
	 * one if the square has no delay factor.
	 * @return the speed factor of this square.
	 */
	public double getSpeedFactor() {
		return speedFactor;
	}

	/**
	 * Returns the speed factor that is multiplied with the velocity if the square is Left going down.
	 * @return the speed factor that is multiplied with the velocity if the square is Left going down.
	 */
	public double getDownSpeedFactor() {
		return downSpeedFactor;
	}

	/**
	 * Sets the speed factor that is multiplied with the velocity if the square is Left going up.
	 * @param downSpeedFactor the speed factor that is multiplied with the velocity if the square is Left going up.
	 */
	void setDownSpeedFactor( double downSpeedFactor ) {
		this.downSpeedFactor = downSpeedFactor;
	}

	/**
	 * Returns the speed factor that is multiplied with the velocity if the square is Left going up.
	 * @return the speed factor that is multiplied with the velocity if the square is Left going up.
	 */
	public double getUpSpeedFactor() {
		return upSpeedFactor;
	}

	/**
	 * Sets the speed factor that is multiplied with the velocity if the square is Left going up.
	 * @param upSpeedFactor the speed factor that is multiplied with the velocity if the square is Left going up.
	 */
	void setUpSpeedFactor( double upSpeedFactor ) {
		this.upSpeedFactor = upSpeedFactor;
	}

	/**
	 * Returns whether the square in direction {@code direction} lies
	 * higher, equal or lower than this square.
	 * If the level has never been set explicitly, Equal is returned.
	 * @param direction The square in this direction is considered.
	 * @return whether the square in direction {@code direction} lies
	 * higher, equal or lower than this square.
	 */
	public Level getLevel( Direction8 direction ) {
		return levels.containsKey( direction ) ? levels.get( direction ) : Level.Equal;
	}

	/**
	 * Defines that the square in direction {@code direction}
	 * lies higher, equal or lower.
	 * @param direction The square in this direction is considered.
	 * @param level Gives the level of the other square according to this square.
	 */
	void setLevel( Direction8 direction, Level level ) {
		this.levels.put( direction, level );
	}

	public Set<Direction8> getPassableDirections() {
		return Collections.unmodifiableSet( passable );
	}

	/**
	 * Returns whether the square is connected to the neighbor in the
	 * direction {@code direction}.
	 * @param direction the neighbor in this direction is checked
	 * @return whether the square is connected to the neighbor in the
	 * direction {@code direction}.
	 */
	public boolean isBlocked( Direction8 direction ) {
		return !passable.contains( direction );
	}

	/**
	 * Defines that the current square is not connected to the square that lies in 
	 * direction {@code direction}.
	 * @param direction the direction of the partner square that shall be disconnected
	 */
	void blockDirection( Direction8 direction ) {
		passable.remove( direction );
	}

	/**
	 * Defines that the current square is connected to the square that lies in 
	 * direction {@code direction}.
	 * @param direction the direction of the partner square that shall be connected
	 */
	void openDirection( Direction8 direction ) {
		passable.add( direction );
	}

	public String getName() {
		return name;
	}

	void setName( String name ) {
		this.name = name;
	}

	/**
	 * Returns a String containing the coordinates, the accessibility and the speed factor
	 * of this {@code RoomRasterSquare}.
	 * @return a String containing the coordinates, the accessibility and the speed factor
	 * of this {@code RoomRasterSquare}.
	 */
	@Override
	public String toString() {
		String result = "[(" + getX() + "," + getY() + ")" + ";";
		result += isAccessible() ? "+" : "-";
		result += String.format( Locale.ENGLISH, "%1$.2f", speedFactor ); //String.format("%1$.2f", speedFactor);
		result += "]";
		return result;
	}
}