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
/*
 * ZToCAConverter.java
 *
 */
package converter;

import java.util.ArrayList;
import java.util.Collection;

import util.Direction;
import util.Level;

import ds.z.BuildingPlan;
import ds.z.ConcreteAssignment;
import ds.z.Floor;
import ds.ca.CellularAutomaton;
import ds.z.Person;
import ds.ca.Individual;
import ds.ca.Cell;
import ds.ca.ExitCell;
import ds.ca.StaticPotential;
import algo.ca.PotentialController;
import algo.ca.SPPotentialController;
import algo.ca.parameter.AbstractDefaultParameterSet;
import algo.ca.parameter.ParameterSet;
import ds.PropertyContainer;
import ds.ca.SaveCell;
import ds.ca.TargetCell;
import ds.z.PlanPoint;
import evacuationplan.BidirectionalNodeCellMapping;

import exitdistributions.ZToExitMapping;
import java.util.ArrayDeque;

import java.util.HashMap;
import java.util.List;
import localization.Localization;
import static util.Direction.*;

/**
 * This singleton class converts a rasterized z-Project to a cellular automaton.
 * @author Daniel Pluempe, Jan-Philipp Kappmeier
 *
 */
public class ZToCAConverter {
	/** The private instance of this singleton. */
	private static ZToCAConverter instance = null;
	/** The latest created mapping of the z-format to the cellular automaton. */
	private static ZToCAMapping lastMapping = null;
	/** The latest created container of rastered elements. */
	private static ZToCARasterContainer lastContainer = null;
	/** A list of all exit cells in the cellular automaton. */
	private static ArrayList<ExitCell> exitCells = null;
	/** The latest created cellular automaton. */
	private static CellularAutomaton lastCA = null;
	/** A map that maps rastered rooms to rooms in the cellular automaton. */
	private static HashMap<ZToCARoomRaster, ds.ca.Room> roomRasterRoomMapping = null;

	public static class ConversionNotSupportedException extends Exception {

		private static final long serialVersionUID = 6776678031861741260L;

		/**
		 * Creates a new default instance of
		 * <code>ConversionNotSupportedException</code>.
		 * 
		 */
		public ConversionNotSupportedException() {
			super( Localization.getInstance (
			).getString ("converter.ZConversionException"));
		}

		/**
		 * Creates a new instance of
		 * <code>ConversionNotSupportedException</code> with a specified error
		 * message.
		 * @param message the error message
		 */
		public ConversionNotSupportedException( String message ) {
			super( message );
		}
	}

	/**
	 * Creates a new instance of this singleton class.
	 */
	protected ZToCAConverter() {
		//lastMapping = new ZToCAMapping();
	}

	/**
	 * Provides the unique instance of this class. If no instance
	 * exists yet, this method will create one and return it.
	 * @return The unique instance of this class.
	 */
	public static ZToCAConverter getInstance() {
		if( instance == null ) {
			instance = new ZToCAConverter();
		}

		return instance;
	}

	/**
	 * Call this method to convert the rastered rooms of a z-project to
	 * a cellular automaton. The returned automaton is structure-only, i.e. 
	 * there are no individuals in the automaton. However, all cell types
	 * are being set correctly, the doors are being linked and obstacles
	 * are being set as well.  
	 * @param buildingPlan the building plan of a Z-project
	 * @return A cellular automaton corresponding to the rastered rooms.
	 * @throws converter.ZToCAConverter.ConversionNotSupportedException 
	 */
	public CellularAutomaton convert( BuildingPlan buildingPlan ) throws ConversionNotSupportedException {
		CellularAutomaton convertedCA = new CellularAutomaton();
		lastMapping = new ZToCAMapping();
		lastContainer = RasterContainerCreator.getInstance().ZToCARasterContainer( buildingPlan );
		exitCells = new ArrayList<ExitCell>();
		roomRasterRoomMapping = new HashMap<ZToCARoomRaster, ds.ca.Room>();


		for( Floor floor : lastContainer.getFloors() ) {
			createAllRooms( floor, lastContainer.getAllRasteredRooms( floor ), buildingPlan.getFloorID( floor ) );
		}
		
		for( Floor floor : lastContainer.getFloors() ) {
			Collection<ZToCARoomRaster> rooms = lastContainer.getAllRasteredRooms( floor );
			if( rooms != null ) {
				for( ZToCARoomRaster rasteredRoom : rooms ) {
					ds.ca.Room convertedRoom = convertRoom( rasteredRoom, floor, buildingPlan.getFloorID( floor ) );
					convertedCA.addRoom( convertedRoom );
				}
			}
		}

		// TODO: delete
		// Wird bereits von addRoom gemacht
		//for( ExitCell e : exitCells ) {
		//	convertedCA.addExit( e );
		//}
		
		calculateAndAddStaticPotentials(convertedCA);
		
		lastCA = convertedCA;
		return convertedCA;
	}
	
	/**
	 * Private method that calculates the static potentials for a
	 * converted ca and adds them to a (new) potential controller for the ca.
	 * @param convertedCA The cellular automaton that needs potentials.
	 */
	protected void calculateAndAddStaticPotentials(CellularAutomaton convertedCA ){
		//calculate and add staticPotentials to CA
		PotentialController pc = new SPPotentialController( convertedCA );
		for( ArrayList<ExitCell> cells : convertedCA.clusterExitCells() ) {
			StaticPotential sp = pc.calculateStaticPotential( cells );
			convertedCA.getPotentialManager().addStaticPotential( sp );
			// Bestimme die angrenzenden Save-Cells
			saveCellSearch( cells, sp );
		}		
		pc.generateSafePotential();
	}

	/**
	 * <p>Searches all reachable save cells from a given bunch of exit cells. If
	 * a save cell that is found has no static potential assigned, the potential
	 * of the exit cells is assigned. If the save cell has already a static
	 * potential assigned, the potential of the exit cells is assigned if it is
	 * better.</p>
	 * <p>The cells are searched in a breadh first search manner, all found
	 * cells are stored in a queue. As the cells have no label, another list
	 * is used to retrieve all cells already labeled. This needs more space,
	 * otherwise wie would have to reset the labels after every call of the
	 * method.</p>
	 * @param exitCells the bunch of connected exit cells from that the search starts
	 * @param sp the potential starting at the exit cells
	 */
	private void saveCellSearch( ArrayList<ExitCell> exitCells, StaticPotential sp ) {
		ArrayDeque<SaveCell> Q = new ArrayDeque<SaveCell>();
		ArrayList<SaveCell> V = new ArrayList<SaveCell>();
		for( Cell cell : exitCells ) {
			for( Cell c : cell.getNeighbours() ) {
				if( c instanceof SaveCell && !V.contains( c ) ) {
					Q.addLast( (SaveCell) c );
					V.add( (SaveCell) c );
				}
			}
		}
		while( Q.size() > 0 ) {
			SaveCell c = Q.pollFirst();
			if( c.getExitPotential() == null || c.getExitPotential().getPotential( c ) > sp.getPotential( c ) ) {
				c.setExitPotential( sp );
			}
			for( Cell cell : c.getNeighbours() ) {
				if( cell instanceof SaveCell && !V.contains( cell ) ) {
					Q.addLast( (SaveCell) cell );
					V.add( (SaveCell) cell );
				}
			}
		}
	}

	/**
	 * Resets the converter. This is used if new project is loaded and the old
	 * one is invalidated.
	 */
	public void clear() {
		lastMapping = null;
		lastContainer = null;
		exitCells = null;
		lastCA = null;
	}

	/**
	 * When a {@link ZToCARasterContainer} is converted, a mapping between
	 * the squares in the raster and the cells of the cellular automaton
	 * is stored. Each time you call {@link convertRoom}, the stored mapping
	 * is overwritten. This method retrieves the mapping from the last 
	 * convert-operation. 
	 * @return The mapping that was created during the last conversion.
	 */
	public ZToCAMapping getLatestMapping() {
		if( lastMapping == null ) {
			throw new IllegalStateException(Localization.getInstance (
			).getString ("converter.CallConvertFirstException"));
		}

		return lastMapping;
	}
	
	/**
	 * This method returns the data needed from tha ca converter
	 * to create a <code>BidirectionalNodeCellMapping</code>.
	 * @return A <code>BidirectionalNodeCellMapping.CAPartOfMapping</code> object
	 * containing a raster container and a <code>ZToCAMapping</code>.
	 */
	public BidirectionalNodeCellMapping.CAPartOfMapping getLatestCAPartOfNodeCellMapping(){
		return new BidirectionalNodeCellMapping.CAPartOfMapping( getLatestContainer(), getLatestMapping() );
	}

	/**
	 * Returns the last <code>ZToCARasterContainer</code> that has been created.
	 * @return the last <code>ZToCARasterContainer</code> that has been created.
	 * @throws IllegalStateException if no container has been created yet
	 */
	public ZToCARasterContainer getLatestContainer() throws IllegalStateException {
		if( lastContainer == null ) {
			throw new IllegalStateException(Localization.getInstance (
			).getString ("converter.CallConvertFirstException"));
		}

		return lastContainer;
	}

	/**
	 * Returns the last <code>CellularAutomaton</code>that was created.
	 * @return the cellular automaton
	 */
	public CellularAutomaton getLatestCellularAutomaton() {
		return lastCA;
	}

	/**
	 * Converts a rastered room (from z-format) to a <code>Room</code> used by the
	 * cellular automaton.
	 * @param rasteredRoom the rastered room
	 * @param onFloor the floor on which the room lies
	 * @param floorID the id of the floor (that means the level)
	 * @return the created {@link ds.ca.Room}
	 * @throws converter.ZToCAConverter.ConversionNotSupportedException if an error occurs
	 */
	protected static ds.ca.Room convertRoom( ZToCARoomRaster rasteredRoom, Floor onFloor, int floorID ) throws ConversionNotSupportedException {
		final int width = rasteredRoom.getColumnCount();
		final int height = rasteredRoom.getRowCount();
		ds.ca.Room convertedRoom = roomRasterRoomMapping.get( rasteredRoom );
		int xOffset = rasteredRoom.getRoom().getxOffset() / 400;
		int yOffset = rasteredRoom.getRoom().getyOffset() / 400;
		convertedRoom.setXOffset( xOffset );
		convertedRoom.setYOffset( yOffset );

		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				ds.ca.Cell aCell = convertCell( rasteredRoom.getSquare( x, y ), x, y, convertedRoom );
				if( aCell != null ) {
					//convertedRoom.setCell( aCell );
					copyBounds( rasteredRoom.getSquare( x, y ), aCell );
					copyLevels( rasteredRoom.getSquare( x, y ), aCell );
				}
			}
		}

		connectBounds( convertedRoom );
		lastMapping.insertTuple( rasteredRoom, convertedRoom );

		return convertedRoom;
	}
	
	/**
	 * Creates all rooms for a given floor. The rooms have to be submitted as a
	 * collection, also the <code>Floor</code> and the corresponding id in the
	 * z-format have to be submitted to the method.
	 * @param onFloor the Floor that contains the rooms
	 * @param rooms a colleciton of rooms on the floor. This is not checked!
	 * @param floorID the id of the floor
	 */
	protected static void createAllRooms( Floor onFloor, Collection<ZToCARoomRaster> rooms, int floorID ) {
		if( rooms != null ) {
			for( ZToCARoomRaster rasteredRoom : rooms ) {
				final int width = rasteredRoom.getColumnCount();
				final int height = rasteredRoom.getRowCount();
				ds.ca.Room room = new ds.ca.Room( width, height, onFloor.getName(), floorID );
				roomRasterRoomMapping.put( rasteredRoom, room );
			}
		}	
	}

	/**
	 * <p>Converts a {@link ZToCARasterSquare} to a {@link ds.ca.Cell} of the appropriate type
	 * e.g. DoorCell or ExitCell. The position in the room has to be submitted and the
	 * converted Room which should contain the created cell.</p>
	 * <p>During creation of door cells another cell can be created (the partner).
	 * In that case, the cell already existing is returned.</p>
	 * @param square the square
	 * @param x the column of the cell in the room
	 * @param y the row of the cell in the room
	 * @param convertedRoom the room that contains the cell
	 * @return the new (or already existing) cell or null if the square is inaccessible
	 * @throws ConversionNotSupportedException if an initialization error occured.
	 */
	protected static ds.ca.Cell convertCell( ZToCARasterSquare square, int x, int y, ds.ca.Room convertedRoom ) throws ConversionNotSupportedException {
		if( square == null )
			return null;

		if( square.inaccessible() && square.isDoor() )
			throw new ConversionNotSupportedException(Localization.getInstance ().getString ("algo.ca.NotInitializedException" + x + ", " + y ));

//        if(square.isExit() && square.isDoor()){
//            throw new ConversionNotSupportedException("Doors in exit areas are currently not supported.");
//        }
//        
//        if(square.getSave() && square.isDoor()){
//            throw new ConversionNotSupportedException("Doors in save areas are currently not supported.");
//        }

		if( square.inaccessible() )
			return null;

		if( square.isDoor() ) {
			ds.ca.DoorCell door = (ds.ca.DoorCell) lastMapping.get( square );
			if( door == null ) {
				door = new ds.ca.DoorCell( square.getSpeedFactor(), x, y );
				//convertedRoom.setCell( aCell );
				convertedRoom.setCell( door );
				lastMapping.insertTuple( door, square );
			}

			for( ZToCARasterSquare partner : square.getPartners() ) {
				ds.ca.DoorCell partnerDoor = (ds.ca.DoorCell) lastMapping.get( partner );
				if( partnerDoor == null ) {
					ZToCARoomRaster partnerRoom = getInstance().getLatestContainer().getRasteredRoom( (ds.z.Room) (partner.getPolygon()) );
					int newX = util.ConversionTools.polyCoordToRasterCoord(
									partner.getX(),
									partnerRoom.getXOffset(),
									partnerRoom );
					int newY = util.ConversionTools.polyCoordToRasterCoord(
									partner.getY(),
									partnerRoom.getYOffset(),
									partnerRoom );

					partnerDoor = new ds.ca.DoorCell(
									partner.getSpeedFactor(),
									newX,
									newY );
					ds.ca.Room newRoom = roomRasterRoomMapping.get( partnerRoom );
					newRoom.setCell( partnerDoor );
					lastMapping.insertTuple( partnerDoor, partner );
				}

				door.addNextDoor( partnerDoor );
			}

			return door;
		}

		if( square.isExit() ) {
			ds.ca.ExitCell newCell = new ds.ca.ExitCell( square.getSpeedFactor(), x, y );
			convertedRoom.setCell( newCell );
			newCell.setAttractivity( square.getAttractivity() );
			newCell.setName( square.getName() );
			lastMapping.insertTuple( newCell, square );
			exitCells.add( newCell );
			return newCell;
		}

		if( square.getSave() ) {
			ds.ca.Cell newCell = new ds.ca.SaveCell( square.getSpeedFactor(), x, y );
			convertedRoom.setCell( newCell );
			lastMapping.insertTuple( newCell, square );
			return newCell;
		}

		if( square.isStair() ) {
			ds.ca.Cell newCell = new ds.ca.StairCell( square.getSpeedFactor(), square.getUpSpeedFactor(), square.getDownSpeedFactor(), x, y );
			convertedRoom.setCell( newCell );
			lastMapping.insertTuple( newCell, square );
			return newCell;
		}

		ds.ca.Cell newCell = new ds.ca.RoomCell( square.getSpeedFactor(), x, y );
		convertedRoom.setCell( newCell );
		lastMapping.insertTuple( newCell, square );
		return newCell;
	}

	/**
	 * This method returns an individual whose attributes are based upon die assignment specified by the Z-Assignment group.<br>
	 * To do this the different assignments could be extracted form the passed assignment object. With these values the specific 
	 * random attributes for the generated individual can be calculated.
	 * @param assignment The specific Assignment for the new created individual
	 * @return Returns an individual of type Individual
	 */
	private static Individual generateIndividual( Person p ) {
		ParameterSet ps = AbstractDefaultParameterSet.createParameterSet( PropertyContainer.getInstance().getAsString( "algo.ca.parameterSet" ) );

		double pDecisiveness = p.getDecisiveness();
		double pFamiliarity = p.getFamiliarity();
		double pAge = p.getAge();
		//double pDiameter = p.getDiameter(); // not used in current implementation

		// here is specified how the different properties of an individual are calculated
		double familiarity = pFamiliarity;
		double panicFactor = p.getPanic();
		double slackness = ps.getSlacknessFromDecisiveness( pDecisiveness );
		double exhaustionFactor = ps.getExhaustionFromAge( pAge );
		double maxSpeed = ps.getSpeedFromAge( pAge );

		int reactiontime = (int) ps.getReactiontimeFromAge( pAge );
		return new Individual( (int) pAge, familiarity, panicFactor, slackness, exhaustionFactor, maxSpeed, reactiontime, p.getUid() );
	}

	/**
	 * Puts persons which are specified in an concrete assignment into the right
	 * cells of the cellular automaton. This is done by first get the rastered
	 * square version of the room specified in the person object. Afterwards the
	 * cell, in which the person has to be located, is calculated in the following
	 * way: (coordinate-200)/400
	 * @param concreteAssignment the concrete assignment containing the individuals
	 * @throws java.lang.IllegalArgumentException if the calculated cell is not in the room of the cellular automaton
	 */
	public static void applyConcreteAssignment( ConcreteAssignment concreteAssignment ) throws IllegalArgumentException {
		ZToCARoomRaster room;
		// Create ZToExitMapping
		HashMap<Individual, TargetCell> individualExitMapping = new HashMap<Individual, TargetCell>();
		Cell c;
		int x, y;
		int individualCounter = 1;
		for( Person p : concreteAssignment.getPersons() ) {
			room = lastContainer.getRasteredRoom( p.getRoom() );
			x = Math.round( (p.getPosition().getXInt() - 200 - p.getRoom().getxOffset()) / 400 );
			y = Math.round( (p.getPosition().getYInt() - 200 - p.getRoom().getyOffset()) / 400 );
			if( !room.isValid( x, y ) ) {
				throw new IllegalArgumentException(Localization.getInstance().getString ( "converter.IndividualOutsideException" ));
			}
			c = lastMapping.get( room.getSquare( x, y ) );
			Individual i = generateIndividual( p );
			i.setNumber( individualCounter++ );
			
			List<ExitCell> exits = lastCA.getExits();
			for( ExitCell e : exits ) {
				// Calculate absolute position
				// TODO 400 hardcoded hier!
				int cellCenterX = (e.getX() + e.getRoom().getXOffset())*400 + 200;
				int cellCenterY = (e.getY() + e.getRoom().getYOffset())*400 + 200;
				if( p.getSaveArea() != null && p.getSaveArea().contains(  new PlanPoint( cellCenterX, cellCenterY ) ) ) {
					individualExitMapping.put( i, e );
					break;
				}
			}
			
			lastCA.addIndividual( c, i );
		}
		ZToExitMapping mapping = new ZToExitMapping( individualExitMapping );
		lastCA.setIndividualToExitMapping( mapping );
	}

	/**
	 * Copies the bonds from a square to the corrisponding cell. That means, if a
	 * direction of the square is unpassable the direction is set unpassable in the
	 * cell, too and vice versa.
	 * @param fromSquare the square
	 * @param toCell the cell
	 */
	protected static void copyBounds( RoomRasterSquare fromSquare, ds.ca.Cell toCell ) {
		if( toCell == null ) {
			return;
		}
		for( Direction direction : Direction.values() ) {
			if( fromSquare.isBlocked( direction ) ) {
				toCell.setUnPassable( direction );
			} else {
				toCell.setPassable( direction );
			}
		}
	}

	/**
	 * Copies the level from a square to the corrisponding cell.
	 * @param fromSquare the square
	 * @param toCell the cell
	 */
	protected static void copyLevels( RoomRasterSquare fromSquare, ds.ca.Cell toCell ) {
		if( toCell == null ) {
			return;
		}
		for( Direction direction : Direction.values() ) {
			Level level = fromSquare.getLevel( direction );
			toCell.setLevel( direction, level );
		}
	}

	/**
	 * <p>Sets the bounds for the cells in a specified room. That means, that bounds
	 * are set, if a cell in a direction is not reachable.</p>
	 * <p>A cell is not reachable diagonally if only one of the horizontal or
	 * vertical neighbor cells is not reachable. For example, the upper left cell
	 * is not reachable if the upper or the left cell is not reachable.</p>
	 * @param room the room for that the borders are set up
	 */
	protected static void connectBounds( ds.ca.Room room ) {
		for( int x = 0; x < room.getWidth(); x++ ) {
			for( int y = 0; y < room.getHeight(); y++ ) {
				if( room.existsCellAt( x, y ) ) {
					ds.ca.Cell aCell = room.getCell( x, y );

					if( !aCell.isPassable( LEFT ) ) {
						if( room.existsCellAt( x, y + 1 ) && !room.getCell( x, y + 1 ).isPassable( LEFT ) ) {
							aCell.setUnPassable( LOWER_LEFT );
							room.getCell( x, y + 1 ).setUnPassable( UPPER_LEFT );
						}
					}

					if( !aCell.isPassable( RIGHT ) ) {
						if( room.existsCellAt( x, y + 1 ) && !room.getCell( x, y + 1 ).isPassable( RIGHT ) ) {
							aCell.setUnPassable( LOWER_RIGHT );
							room.getCell( x, y + 1 ).setUnPassable( UPPER_RIGHT );
						}
					}

					if( !aCell.isPassable( UP ) ) {
						if( room.existsCellAt( x + 1, y ) && !room.getCell( x + 1, y ).isPassable( UP ) ) {
							aCell.setUnPassable( UPPER_RIGHT );
							room.getCell( x + 1, y ).setUnPassable( UPPER_LEFT );
						}
					}

					if( !aCell.isPassable( DOWN ) ) {
						if( room.existsCellAt( x + 1, y ) && !room.getCell( x + 1, y ).isPassable( DOWN ) ) {
							aCell.setUnPassable( LOWER_RIGHT );
							room.getCell( x + 1, y ).setUnPassable( LOWER_LEFT );
						}
					}
				}
			}
		}

		// Disable diagonally reachable cells if one edge is blocked. Behaviour changed:
		// it is not neccessary to have two sides blocked, but only one.
		for( int x = 0; x < room.getWidth(); x++ ) {
			for( int y = 0; y < room.getHeight(); y++ ) {
				if( room.existsCellAt( x, y ) ) {
					Cell aCell = room.getCell( x, y );
					if( isDirectionBlocked( aCell, UP ) || isDirectionBlocked( aCell, LEFT ) ) {
//					if( isDirectionBlocked( aCell, UP ) && isDirectionBlocked( aCell, LEFT ) ) {
						aCell.setUnPassable( UPPER_LEFT );

						if( room.existsCellAt( x - 1, y - 1 ) ) {
							room.getCell( x - 1, y - 1 ).setUnPassable( LOWER_RIGHT );
						}
					}

					if( isDirectionBlocked( aCell, UP ) || isDirectionBlocked( aCell, RIGHT ) ) {
//					if( isDirectionBlocked( aCell, UP ) && isDirectionBlocked( aCell, RIGHT ) ) {
						aCell.setUnPassable( Direction.UPPER_RIGHT );

						if( room.existsCellAt( x + 1, y - 1 ) ) {
							room.getCell( x + 1, y - 1 ).setUnPassable( LOWER_LEFT );
						}
					}

					if( isDirectionBlocked( aCell, DOWN ) || isDirectionBlocked( aCell, LEFT ) ) {
//					if( isDirectionBlocked( aCell, DOWN ) && isDirectionBlocked( aCell, LEFT ) ) {
						aCell.setUnPassable( Direction.LOWER_LEFT );

						if( room.existsCellAt( x - 1, y + 1 ) ) {
							room.getCell( x - 1, y + 1 ).setUnPassable( UPPER_RIGHT );
						}
					}

					if( isDirectionBlocked( aCell, DOWN ) || isDirectionBlocked( aCell, RIGHT ) ) {
//					if( isDirectionBlocked( aCell, DOWN ) && isDirectionBlocked( aCell, RIGHT ) ) {
						aCell.setUnPassable( Direction.LOWER_RIGHT );

						if( room.existsCellAt( x + 1, y + 1 ) ) {
							room.getCell( x + 1, y + 1 ).setUnPassable( UPPER_LEFT );
						}
					}
				}
			}
		}
	}

	/**
	 * Checks wether a given direction for a cell is blocked, or not.
	 * @param aCell the cell
	 * @param direction the direction
	 * @return true if the cell cannot be leaved in the given direction.
	 */
	private static boolean isDirectionBlocked( Cell aCell, Direction direction ) {
		int x = aCell.getX();
		int y = aCell.getY();
		ds.ca.Room room = aCell.getRoom();

		return !aCell.isPassable( direction ) || !room.existsCellAt( x + direction.xOffset(), y + direction.yOffset() );
	}
}