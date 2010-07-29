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
import de.tu_berlin.math.coga.common.util.Direction;
import de.tu_berlin.math.coga.common.util.Level;
import ds.z.TeleportArea;

/**
 * The <code>RoomRasterSquare</code> is a special type of {@link RasterSquare} containing
 * additional information for rooms. The inaccessible areas and speed factors for
 * delayed areas are stored. Additionally, it has possibility to hold connections
 * up to four connected squares through doors.
 * @author Jan-Philipp Kappmeier
 */
public class RoomRasterSquare extends RasterSquare {

	public static enum Property {
		/* RasterSquare is accessible */
		ACCESSIBLE,
		/* RasterSquare is a stair */
		STAIR,
		/* RasterSquare belongs to a save area*/
		SAVE,
		/* RasterSquare belongs to an evacuation area*/
		EXIT,
		/* RasterSquare is a teleport cell. */
		TELEPORT
	}
	/** The {@link ds.z.Room} that is rasterized. Redundant save helps avoiding casts. */
	//Room r;
	/** The speed factor for this cell */
	private double speedFactor;
	/** The multiplicative speed factor if you are leaving this square going down (in height). */
	private double downSpeedFactor;
	/** The multiplicative speed factor if you are leaving this square going up (in height). */
	private double upSpeedFactor;	// Warning! Do never initialize attractiveness here because it is initialized by the super!
	// constructor and any initialization here will override the correct value by means of check().
	/** The attractiveness for this cell (only used by ExitCells) */
	private int attractiveness;
	/** The name of the cell. (Only used by ExitCells) */
	private String name;
	/** Contains all properties of the square */
	protected EnumSet<Property> properties;
	/** Contains whether the square can reach the neighbor squares in the matrix. */
	private EnumSet<Direction> passable;
	/** Contains whether the neighbor squares in the matrix lie higher, equal or lower. */
	private EnumMap<Direction, Level> levels;

	/** Contains all properties of the square */
	// private EnumSet<Property> properties = EnumSet.noneOf(Property.class);
	/** Creates a new instance of RoomRasterSquare
	 * @param r the room that is rasterized
	 * @param column the column in the virtual raster-array
	 * @param row the row in the virtual raster-array
	 * @param raster the width and height of the raster square
	 */
	public RoomRasterSquare( Room r, int column, int row, int raster ) {
		super( r, column, row, raster );
	}

	@Override
	protected void callAtConstructorStart() {
		passable = EnumSet.allOf( Direction.class );
		levels = new EnumMap<Direction, Level>( Direction.class );
		for( Direction direction : Direction.values() ) {
			levels.put( direction, Level.Equal );
		}
		if( properties != null ) {
			System.out.println( "wie kann das sein properties!=null RoomRasterSquare" );
		}
		properties = EnumSet.noneOf( Property.class );
		setProperty( Property.ACCESSIBLE );
		this.downSpeedFactor = 1.0;
		this.upSpeedFactor = 1.0;
	}

	public void setProperty( Property property ) {
		properties.add( property );
	}

	public void clearProperty( Property property ) {
		properties.remove( property );
	}

	public void setProperty( Property property, boolean value ) {
		if( value ) {
			setProperty( property );
		} else {
			clearProperty( property );
		}
	}

	public boolean getProperty( Property property ) {
		return properties.contains( property );
	}

	public void setAccessible() {
		setProperty( Property.ACCESSIBLE );
	}

	public void clearAccessible() {
		clearProperty( Property.ACCESSIBLE );
	}

	public boolean getAccessible() {
		return getProperty( Property.ACCESSIBLE );
	}

	public void setStair() {
		setProperty( Property.STAIR );
	}

	public void clearStair() {
		clearProperty( Property.STAIR );
	}

	public boolean isStair() {
		return getProperty( Property.STAIR );
	}

	public void setSave() {
		setProperty( Property.SAVE );
	}

	public void clearSave() {
		clearProperty( Property.SAVE );
	}

	public boolean getSave() {
		return getProperty( Property.SAVE );
	}

	public void setExit() {
		setProperty( Property.EXIT );
	}

	public void clearExit() {
		clearProperty( Property.EXIT );
	}

	/**
	 * Returns wheather this square is an exit, or not.
	 * @return true if this square is an exit
	 */
	public boolean isExit() {
		return getProperty( Property.EXIT );
	}

	public boolean accessible() {
		return getProperty( Property.ACCESSIBLE );
	}

	public void setTeleport() {
		setProperty( Property.TELEPORT );
	}

	public void clearTeleport() {
		clearProperty( Property.TELEPORT );
	}

	public boolean isTeleport() {
		return getProperty( Property.TELEPORT );
	}

	@Override
	protected void check() {
		super.check();

		Room r = (Room) getPolygon();
		// Exists: Array. Check for all Squares if they are contained in any Area
		// If true: calculate new delay or isAccessible
		this.speedFactor = 1.0;
		for( DelayArea d : r.getDelayAreas() ) {
			if( d.contains( this.getSquare() ) ) {
				this.speedFactor *= d.getSpeedFactor();
			}
		}

		for( InaccessibleArea i : r.getInaccessibleAreas() ) {
			setProperty( Property.ACCESSIBLE, !i.contains( this.getSquare() ) & accessible() );
		}

		for( SaveArea saveArea : r.getSaveAreas() ) {
			setProperty( Property.SAVE, this.getSave() || (saveArea.contains( this.getSquare() ) & accessible()) );
		}

		for( EvacuationArea evacArea : r.getEvacuationAreas() ) {
			if( this.isExit() || (evacArea.contains( this.getSquare() ) & accessible()) ) {
				setProperty( Property.EXIT, true );
				if( evacArea.contains( this.getSquare() ) ) {
					setAttractiveness( evacArea.getAttractivity() );
					setName( evacArea.getName() );
				}
			} else {
				setProperty( Property.EXIT, false );
			}
		}

		for( TeleportArea teleportArea : r.getTeleportAreas() ) {
			setProperty( Property.TELEPORT, this.isTeleport() || (teleportArea.contains( this.getSquare() ) & accessible()) );
		}

	//for(Barrier barrier : r.getBarriers()){
	//	List<Edge> edgeList = this.getSquare().getEdges();
	//	for (Edge edge : edgeList){
	//PlanPolygon<Edge> p = new PlanPolygon<Edge>(Edge.class, edge);
	//System.out.println(edge);
	//if (barrier.intersects(p)) {
	//	System.out.println("Überschneidet sich mit "+edge);
	//}
	//	}
	//}
	}

	public boolean inaccessible() {
		return !getProperty( Property.ACCESSIBLE );
	}

	/**
	 * Returns the speed factor of this square.
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
	 * Returns the speed factor that is multiplied with the velocity if the square is Left going up.
	 * @return the speed factor that is multiplied with the velocity if the square is Left going up.
	 */
	public double getUpSpeedFactor() {
		return upSpeedFactor;
	}

	/**
	 * Sets the speed factor that is multiplied with the velocity if the square is Left going up.
	 * @param downSpeedFactor the speed factor that is multiplied with the velocity if the square is Left going up.
	 */
	public void setDownSpeedFactor( double downSpeedFactor ) {
		this.downSpeedFactor = downSpeedFactor;
	}

	/**
	 * Sets the speed factor that is multiplied with the velocity if the square is Left going up.
	 * @param upSpeedFactor the speed factor that is multiplied with the velocity if the square is Left going up.
	 */
	public void setUpSpeedFactor( double upSpeedFactor ) {
		this.upSpeedFactor = upSpeedFactor;
	}

	/**
	 * Defines that the square in direction <code>direction</code>
	 * lies higher, equal or lower.
	 * @param direction The square in this direction is considered.
	 * @param level Gives the level of the other square according to this square.
	 */
	public void setLevel( Direction direction, Level level ) {
		this.levels.put( direction, level );
	}

	/**
	 * Returns whether the square in direction <code>direction</code> lies
	 * higher, equal or lower than this square.
	 * If the level has never been set explicitly, Equal is returned.
	 * @param direction The square in this direction is considered.
	 * @return whether the square in direction <code>direction</code> lies
	 * higher, equal or lower than this square.
	 */
	public Level getLevel( Direction direction ) {
		if( levels.containsKey( direction ) ) {
			return levels.get( direction );
		} else {
			return Level.Equal;
		}
	}

	/**
	 * Defines that the current square is not connected to the square that lies in 
	 * direction <code>direction</code>.
	 * @param direction the direction of the partner square that shall be disconnected
	 */
	public void blockDirection( Direction direction ) {
		passable.remove( direction );
	}

	/**
	 * Defines that the current square is connected to the square that lies in 
	 * direction <code>direction</code>.
	 * @param direction the direction of the partner square that shall be connected
	 */
	public void openDirection( Direction direction ) {
		passable.add( direction );
	}

	/**
	 * Returns whether the square is connected to the neighbor in the
	 * direction <code>direction</code>.
	 * @param direction the neighbor in this direction is checked
	 * @return whether the square is connected to the neighbor in the
	 * direction <code>direction</code>.
	 */
	public boolean isBlocked( Direction direction ) {
		return !passable.contains( direction );
	}

	@Override
	/**
	 * Returns a String containing the coordinates, the accessibility and the speed factor
	 * of this <code>RoomRasterSquare</code>.
	 * @return a String containing the coordinates, the accessibility and the speed factor
	 * of this <code>RoomRasterSquare</code>.
	 */
	public String toString() {
		String result = "[(" + getX() + "," + getY() + ")" + ";";
		if( accessible() ) {
			result += "+;";
		} else {
			result += "-;";
		}
		result += String.format( Locale.ENGLISH, "%1$.2f", speedFactor ); //String.format("%1$.2f", speedFactor);
		result += "]";
		return result;
	}

	public int getAttractivity() {
		return attractiveness;
	}

	public void setAttractiveness( int attractiveness ) {
		this.attractiveness = attractiveness;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public Set<Direction> getPassableDirections() {
		return Collections.unmodifiableSet( passable );
	}
}