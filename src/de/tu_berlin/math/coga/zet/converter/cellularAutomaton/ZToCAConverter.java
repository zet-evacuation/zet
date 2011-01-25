/* zet evacuation tool copyright (ageCollector) 2007-10 zet evacuation team
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
 * ZToCAConverter.java
 *
 */

package de.tu_berlin.math.coga.zet.converter.cellularAutomaton;

import de.tu_berlin.math.coga.common.localization.DefaultLoc;
import ds.z.Project;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import algo.ca.PotentialController;
import algo.ca.SPPotentialController;
import batch.tasks.AlgorithmTask;
import de.tu_berlin.math.coga.zet.converter.RasterContainerCreator;
import de.tu_berlin.math.coga.zet.converter.RoomRasterSquare;
import de.tu_berlin.math.coga.common.util.Direction;
import de.tu_berlin.math.coga.common.util.Level;
import ds.z.BuildingPlan;
import ds.z.Floor;
import ds.ca.CellularAutomaton;
import ds.ca.Cell;
import ds.ca.ExitCell;
import ds.ca.StaticPotential;
import ds.ca.SaveCell;
import evacuationplan.BidirectionalNodeCellMapping;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import ds.z.TeleportArea;
import java.util.logging.Logger;
import static de.tu_berlin.math.coga.common.util.Direction.*;

/**
 * This singleton class converts a rasterized z-Project to a cellular automaton.
 * @author Daniel Plümpe, Jan-Philipp Kappmeier
 *
 */
public class ZToCAConverter extends Algorithm<BuildingPlan,ConvertedCellularAutomaton> {
	/** The private instance of this singleton. */
	//private static ZToCAConverter instance = null;
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

	@Override
	protected ConvertedCellularAutomaton runAlgorithm( BuildingPlan problem ) {
		try {
			convert( problem );
		} catch( ConversionNotSupportedException ex ) {
			Logger.getLogger( ZToCAConverter.class.getName() ).log( java.util.logging.Level.SEVERE, null, ex );
		}
		ConvertedCellularAutomaton cca = new ConvertedCellularAutomaton( lastCA, lastMapping, lastContainer );
		return cca;
	}

	public static class ConversionNotSupportedException extends Exception {

		private static final long serialVersionUID = 6776678031861741260L;

		/**
		 * Creates a new default instance of
		 * {@code ConversionNotSupportedException}.
		 * 
		 */
		public ConversionNotSupportedException() {
			super( DefaultLoc.getSingleton().getString( "converter.ZConversionException" ) );
		}

		/**
		 * Creates a new instance of
		 * {@code ConversionNotSupportedException} with a specified error
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
	public ZToCAConverter() {
		//lastMapping = new ZToCAMapping();
	}

	public ZToCAConverter( Project project ) {
		setProblem( project.getBuildingPlan() );
	}

	/**
	 * Provides the unique instance of this class. If no instance
	 * exists yet, this method will create one and return it.
	 * @return The unique instance of this class.
	 */
//	public static ZToCAConverter getInstance() {
//		if( instance == null ) {
//			instance = new ZToCAConverter();
//		}

//		return instance;
//	}

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
	private CellularAutomaton convert( BuildingPlan buildingPlan ) throws ConversionNotSupportedException {
		AlgorithmTask.getInstance().publish( "Starte Konvertierung", "" );
		CellularAutomaton convertedCA = new CellularAutomaton();
		lastMapping = new ZToCAMapping();
		AlgorithmTask.getInstance().publish( "Rastere den Gebäudeplan", "" );
		lastContainer = RasterContainerCreator.getInstance().ZToCARasterContainer( buildingPlan );
		exitCells = new ArrayList<ExitCell>();
		roomRasterRoomMapping = new HashMap<ZToCARoomRaster, ds.ca.Room>();


		AlgorithmTask.getInstance().publish( "Erzeuge Räume", "" );

		for( Floor floor : lastContainer.getFloors() )
			createAllRooms( floor, lastContainer.getAllRasteredRooms( floor ), buildingPlan.getFloorID( floor ) );

		AlgorithmTask.getInstance().publish( "Konvertiere Räume", "" );

		for( Floor floor : lastContainer.getFloors() ) {
			Collection<ZToCARoomRaster> rooms = lastContainer.getAllRasteredRooms( floor );
			if( rooms != null )
				for( ZToCARoomRaster rasteredRoom : rooms ) {
					ds.ca.Room convertedRoom = convertRoom( rasteredRoom, floor, buildingPlan.getFloorID( floor ) );
					convertedCA.addRoom( convertedRoom );
				}
		}

		// TODO: delete
		// Wird bereits von addRoom gemacht
		//for( ExitCell e : exitCells ) {
		//	convertedCA.addExit( e );
		//}

		AlgorithmTask.getInstance().publish( "Berechne statische Potenziale", "" );

		computeAndAddStaticPotentials( convertedCA );

		lastCA = convertedCA;
		return convertedCA;
	}
	
	/**
	 * Private method that calculates the static potentials for a
	 * converted ca and adds them to a (new) potential controller for the ca.
	 * @param convertedCA The cellular automaton that needs potentials.
	 */
	protected void computeAndAddStaticPotentials( CellularAutomaton convertedCA ) {
		//calculate and defineByPoints staticPotentials to CA
		PotentialController pc = new SPPotentialController( convertedCA );
		for( ArrayList<ExitCell> cells : convertedCA.clusterExitCells() ) {
			StaticPotential sp = pc.createStaticPotential( cells );
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
				if( c instanceof SaveCell && !V.contains( (SaveCell)c ) ) {
					Q.addLast( (SaveCell)c );
					V.add( (SaveCell)c );
				}
			}
		}
		while( Q.size() > 0 ) {
			SaveCell c = Q.pollFirst();
			if( c.getExitPotential() == null || c.getExitPotential().getPotential( c ) > sp.getPotential( c ) ) {
				c.setExitPotential( sp );
			}
			for( Cell cell : c.getNeighbours() ) {
				if( cell instanceof SaveCell && !V.contains( (SaveCell)cell ) ) {
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
	 * is stored. Each time you call {@link #convertRoom( ZToCARoomRaster, Floor, int)}, the stored mapping
	 * is overwritten. This method retrieves the mapping from the last 
	 * convert-operation. 
	 * @return The mapping that was created during the last conversion.
	 */
	public ZToCAMapping getMapping() {
		if( lastMapping == null )
			throw new IllegalStateException(DefaultLoc.getSingleton ().getString ("converter.CallConvertFirstException"));

		return lastMapping;
	}
	
	/**
	 * This method returns the data needed from thes ca converter
	 * to create a {@code BidirectionalNodeCellMapping}.
	 * @return A {@code BidirectionalNodeCellMapping.CAPartOfMapping} object
	 * containing a raster container and a {@code ZToCAMapping}.
	 */
	public BidirectionalNodeCellMapping.CAPartOfMapping getLatestCAPartOfNodeCellMapping() {
		return new BidirectionalNodeCellMapping.CAPartOfMapping( getContainer(), getMapping() );
	}

	/**
	 * Returns the last {@code ZToCARasterContainer} that has been created.
	 * @return the last {@code ZToCARasterContainer} that has been created.
	 * @throws IllegalStateException if no container has been created yet
	 */
	public ZToCARasterContainer getContainer() throws IllegalStateException {
		if( lastContainer == null )
			throw new IllegalStateException(DefaultLoc.getSingleton ().getString ("converter.CallConvertFirstException"));

		return lastContainer;
	}

	/**
	 * Returns the last {@code CellularAutomaton}that was created.
	 * @return the cellular automaton
	 */
	public CellularAutomaton getCellularAutomaton() {
		return lastCA;
	}

	/**
	 * Converts a rastered room (from z-format) to a {@code Room} used by the
	 * cellular automaton.
	 * @param rasteredRoom the rastered room
	 * @param onFloor the floor on which the room lies
	 * @param floorID the id of the floor (that means the level)
	 * @return the created {@link ds.ca.Room}
	 * @throws converter.ZToCAConverter.ConversionNotSupportedException if an error occurs
	 */
	protected ds.ca.Room convertRoom( ZToCARoomRaster rasteredRoom, Floor onFloor, int floorID ) throws ConversionNotSupportedException {
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
	 * collection, also the {@code Floor} and the corresponding id in the
	 * z-format have to be submitted to the method.
	 * @param onFloor the Floor that contains the rooms
	 * @param rooms a collection of rooms on the floor. This is not checked!
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
	protected ds.ca.Cell convertCell( ZToCARasterSquare square, int x, int y, ds.ca.Room convertedRoom ) throws ConversionNotSupportedException {
		if( square == null )
			return null;

		if( square.inaccessible() && square.isDoor() )
			throw new ConversionNotSupportedException(DefaultLoc.getSingleton ().getString ("algo.ca.NotInitializedException" + x + ", " + y ));

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
					//ZToCARoomRaster partnerRoom = getInstance().getContainer().getRasteredRoom( (ds.z.Room) (partner.getPolygon()) );
					ZToCARoomRaster partnerRoom = getContainer().getRasteredRoom( (ds.z.Room) ( partner.getPolygon()) );
					int newX = de.tu_berlin.math.coga.zet.converter.RasterTools.polyCoordToRasterCoord( partner.getX(), partnerRoom.getXOffset(), partnerRoom );
					int newY = de.tu_berlin.math.coga.zet.converter.RasterTools.polyCoordToRasterCoord( partner.getY(), partnerRoom.getYOffset(), partnerRoom );

					partnerDoor = new ds.ca.DoorCell( partner.getSpeedFactor(), newX, newY );
					ds.ca.Room newRoom = roomRasterRoomMapping.get( partnerRoom );
					newRoom.setCell( partnerDoor );
					lastMapping.insertTuple( partnerDoor, partner );
				}

				door.addTarget( partnerDoor );
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

		// TODO insertTuple is very inefficient!

		if( square.isStair() ) {
			ds.ca.Cell newCell = new ds.ca.StairCell( square.getSpeedFactor(), square.getUpSpeedFactor(), square.getDownSpeedFactor(), x, y );
			convertedRoom.setCell( newCell );
			lastMapping.insertTuple( newCell, square );
			return newCell;
		}

		if( square.isTeleport() ) {
			System.out.println( "this was a teleport cell" );


			ds.ca.TeleportCell teleport = (ds.ca.TeleportCell) lastMapping.get( square );
			// create only, if not already was created
			if( teleport == null ) {
				teleport = new ds.ca.TeleportCell( square.getSpeedFactor(), x, y );
				convertedRoom.setCell( teleport );
				lastMapping.insertTuple( teleport, square );
			}

			//System.out.println( square.getPolygon() );
			// Find the appropriate TeleportArea
			ds.z.Room r = (ds.z.Room) square.getPolygon();
			for( TeleportArea t : r.getTeleportAreas() ) {
				if( t.contains( square.getSquare() ) ) {
					System.out.println( "Teleport-Area gefunden: " + t.getName() );
					if( t.getTargetArea() == null ) {
						System.out.println( "Zielgebiet ist: " + " --- " );
					} else {
						System.out.println( "Zielgebiet ist: " + t.getTargetArea().getName() );
						if( t.equals( t.getTargetArea() ) ) {
							System.out.println( "Die gleichen Bereiche. Ignorieren. " );
						} else {
							System.out.println( "Suche die Zielzelle:" );

							ds.z.Room targetRoom = t.getAssociatedRoom();
							ZToCARoomRaster targetRoomRaster = lastContainer.getRasteredRoom( targetRoom);
							//ZToCARoomRaster roomRaster = lastMapping.get( lastContainer.getRasteredRoom( targetRoom) );
							for( ZToCARasterSquare sq : targetRoomRaster.getAccessibleSquares() ) {
								if( t.getTargetArea().contains( sq.getSquare() ) ) {
									// Die zielarea liegt im rastersquare sq
									int k = 3;
									k++;
									//Cell targetCell = lastMapping.get( sq );
									ds.ca.TeleportCell targetCell = (ds.ca.TeleportCell) lastMapping.get( sq );
									if( targetCell == null ) {
										// zielzelle muss erstellt werden
					//ZToCARoomRaster partnerRoom = getInstance().getContainer().getRasteredRoom( (ds.z.Room) (sq.getPolygon()) );
					int newX = de.tu_berlin.math.coga.zet.converter.RasterTools.polyCoordToRasterCoord( sq.getX(), targetRoomRaster.getXOffset(), targetRoomRaster );
					int newY = de.tu_berlin.math.coga.zet.converter.RasterTools.polyCoordToRasterCoord( sq.getY(), targetRoomRaster.getYOffset(), targetRoomRaster );
					targetCell = new ds.ca.TeleportCell( sq.getSpeedFactor(), newX, newY );
					ds.ca.Room newRoom = roomRasterRoomMapping.get( targetRoomRaster );
					newRoom.setCell( targetCell );
					lastMapping.insertTuple( targetCell, sq );

									}


									// Setze die partnerzelle
									teleport.addTarget( targetCell );

//									if( targetCell instanceof TeleportCell ) {
//										System.out.println( "Die Zielzelle ist vom Typ Teleport-Cell" );
//									}
								}
							}

							System.out.println( "" );
						}
					}
				}
			}


			// Find the partner cell
			//ZToCARoomRaster partnerRoom = getInstance().getContainer() .getRasteredRoom( (ds.z.Room) () );

			//getInstance().getContainer().getRasteredRoom( null )
			return teleport;
		}

		ds.ca.Cell newCell = new ds.ca.RoomCell( square.getSpeedFactor(), x, y );
		convertedRoom.setCell( newCell );
		lastMapping.insertTuple( newCell, square );
		return newCell;
	}

	/**
	 * Copies the bonds from a square to the corresponding cell. That means, if a
	 * direction of the square is impassable the direction is set impassable in the
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

	static final boolean walkDiagonalStrict = true;

	/**
	 * <p>Sets the bounds for the cells in a specified room. That means, that bounds
	 * are set, if a cell in a direction is not reachable.</p>
	 * <p>A cell is not reachable diagonally if only one of the horizontal or
	 * vertical neighbor cells is not reachable. For example, the upper Left cell
	 * is not reachable if the upper or the Left cell is not reachable.</p>
	 * @param room the room for that the borders are set up
	 */
	protected static void connectBounds( ds.ca.Room room ) {
		for( int x = 0; x < room.getWidth(); x++ ) {
			for( int y = 0; y < room.getHeight(); y++ ) {
				if( room.existsCellAt( x, y ) ) {
					ds.ca.Cell aCell = room.getCell( x, y );

					if( !aCell.isPassable( Left ) ) {
						if( room.existsCellAt( x, y + 1 ) && !room.getCell( x, y + 1 ).isPassable( Left ) ) {
							aCell.setUnPassable( DownLeft );
							room.getCell( x, y + 1 ).setUnPassable( TopLeft );
						}
					}

					if( !aCell.isPassable( Right ) ) {
						if( room.existsCellAt( x, y + 1 ) && !room.getCell( x, y + 1 ).isPassable( Right ) ) {
							aCell.setUnPassable( DownRight );
							room.getCell( x, y + 1 ).setUnPassable( TopRight );
						}
					}

					if( !aCell.isPassable( Top ) ) {
						if( room.existsCellAt( x + 1, y ) && !room.getCell( x + 1, y ).isPassable( Top ) ) {
							aCell.setUnPassable( TopRight );
							room.getCell( x + 1, y ).setUnPassable( TopLeft );
						}
					}

					if( !aCell.isPassable( Down ) ) {
						if( room.existsCellAt( x + 1, y ) && !room.getCell( x + 1, y ).isPassable( Down ) ) {
							aCell.setUnPassable( DownRight );
							room.getCell( x + 1, y ).setUnPassable( DownLeft );
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
					if( ( walkDiagonalStrict && (isDirectionBlocked( aCell, Top ) || isDirectionBlocked( aCell, Left ) ) ) || ( !walkDiagonalStrict && ( isDirectionBlocked( aCell, Top ) && isDirectionBlocked( aCell, Left ) ) ) ) {
//					if(  ) {
						aCell.setUnPassable( TopLeft );

						if( room.existsCellAt( x - 1, y - 1 ) ) {
							room.getCell( x - 1, y - 1 ).setUnPassable( DownRight );
						}
					}

//					if( isDirectionBlocked( aCell, Top ) || isDirectionBlocked( aCell, Right ) ) {
					if( ( walkDiagonalStrict && ( isDirectionBlocked( aCell, Top ) || isDirectionBlocked( aCell, Right ) ) ) || ( !walkDiagonalStrict && ( isDirectionBlocked( aCell, Top ) && isDirectionBlocked( aCell, Right ) ) ) ) {
//					if( isDirectionBlocked( aCell, Top ) && isDirectionBlocked( aCell, Right ) ) {
						aCell.setUnPassable( Direction.TopRight );

						if( room.existsCellAt( x + 1, y - 1 ) ) {
							room.getCell( x + 1, y - 1 ).setUnPassable( DownLeft );
						}
					}

//					if( isDirectionBlocked( aCell, Down ) || isDirectionBlocked( aCell, Left ) ) {
					if( ( walkDiagonalStrict && ( isDirectionBlocked( aCell, Down ) || isDirectionBlocked( aCell, Left ) ) ) || ( !walkDiagonalStrict && ( isDirectionBlocked( aCell, Down ) && isDirectionBlocked( aCell, Left ) ) ) ) {
//					if( isDirectionBlocked( aCell, Down ) && isDirectionBlocked( aCell, Left ) ) {
						aCell.setUnPassable( Direction.DownLeft );

						if( room.existsCellAt( x - 1, y + 1 ) ) {
							room.getCell( x - 1, y + 1 ).setUnPassable( TopRight );
						}
					}

//					if( isDirectionBlocked( aCell, Down ) || isDirectionBlocked( aCell, Right ) ) {
					if( ( walkDiagonalStrict && ( isDirectionBlocked( aCell, Down ) || isDirectionBlocked( aCell, Right ) ) ) || ( !walkDiagonalStrict && ( isDirectionBlocked( aCell, Down ) && isDirectionBlocked( aCell, Right ) ) ) ) {
//					if( isDirectionBlocked( aCell, Down ) && isDirectionBlocked( aCell, Right ) ) {
						aCell.setUnPassable( Direction.DownRight );

						if( room.existsCellAt( x + 1, y + 1 ) ) {
							room.getCell( x + 1, y + 1 ).setUnPassable( TopLeft );
						}
					}
				}
			}
		}
	}

	/**
	 * Checks whether a given direction for a cell is blocked, or not.
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