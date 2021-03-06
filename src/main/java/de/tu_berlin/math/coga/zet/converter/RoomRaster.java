/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.tu_berlin.math.coga.zet.converter;

import de.zet_evakuierung.model.FloorInterface;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.zetool.common.util.Direction8;
import org.zetool.common.util.Level;
import de.zet_evakuierung.model.Barrier;
import de.zet_evakuierung.model.PlanEdge;
import de.zet_evakuierung.model.Floor;
import de.zet_evakuierung.model.Room;
import de.zet_evakuierung.model.StairArea;
import java.util.Iterator;

/**
 * The {@code RoomRaster} class provides rasterization of a {@link de.zet_evakuierung.model.Room}
 * object. The squares of the raster have to be of the type {@link RoomRaster}
 * which allows to describe the {@link de.zet_evakuierung.model.Area} in which a square lies.
 *
 * @param <T> the type of the used raster, have to be at least
 * {@link RoomRasterSquare} objects
 * @see Raster
 */
public class RoomRaster<T extends RoomRasterSquare> extends Raster<T> {

	/** The {@link de.zet_evakuierung.model.Floor} of this room. */
	FloorInterface floor;
	/** The {@link de.zet_evakuierung.model.Room} that is rastered. Redundant save helps avoiding
	 * casts. */
	Room r;
	LinkedList<T> accessibleSquares;

	/**
	 * Creates a new instance of {@code RoomRaster}
	 *
	 * @param squareClassType the type of the raster squares
	 * @param r the room
	 */
	public RoomRaster( Class<T> squareClassType, Room r ) {
		//super( squareClassType, Room.class, r );
		super( squareClassType, r.getPolygon() );
		this.r = r;
		floor = r.getAssociatedFloor();
		accessibleSquares = null;
	}

	/**
	 * Creates a new instance of {@code RoomRaster}.
	 *
	 * @param squareClassType the type of the squares used during rasterization
	 * @param r the room that will be rastered
	 * @param raster the raster size
	 * @throws java.lang.IllegalArgumentException if raster is negative or zero
	 */
	public RoomRaster( Class<T> squareClassType, Room r, int raster ) throws IllegalArgumentException {
		//super( squareClassType, Room.class, r, raster );
		super( squareClassType, r.getPolygon(), raster );
		this.r = r;
		floor = r.getAssociatedFloor();
		accessibleSquares = null;
	}

	/**
	 * Creates a new instance of {@code RoomRaster}
	 *
	 * @param squareClassType the type of the squares used during rasterization
	 * @param r the room that will be rastered
	 * @param raster the raster size
	 * @throws java.lang.IllegalArgumentException if raster is negative or zero
	 */
	public RoomRaster( Class<T> squareClassType, Room r, double raster ) throws IllegalArgumentException {
		//super( squareClassType, Room.class, r, raster );
		super( squareClassType, r.getPolygon(), raster );
		this.r = r;
		floor = r.getAssociatedFloor();
		accessibleSquares = null;
	}

	/**
	 * Returns the {@link Room} that is rastered
	 *
	 * @return the room
	 */
	public Room getRoom() {
		return r;
	}

	/**
	 * Returns the {@link Floor} this room belongs to.
	 *
	 * @return the {@link Floor} this room belongs to.
	 */
	public FloorInterface getFloor() {
		return floor;
	}

	/**
	 * Returns the raster square at the specified position in the array of
	 * squares.
	 *
	 * @param x the x position of the square, starts with 0
	 * @param y the y position of the square, starts with 0 in the upper corner
	 * @return the {@link RoomRasterSquare} object
	 */
	@Override
	public T getSquare( int x, int y ) {
		return super.getSquare( x, y ); //return rasterSquares[x][y];
	}

	/**
	 * Returns the squares intersecting the polygon.
	 *
	 * @return the squares intersecting the polygon
	 */
	@Override
	public List<T> insideSquares() {
		return super.insideSquares(); //Collections.unmodifiableList(insideSquares);
	}

	public LinkedList<T> getAccessibleSquares() {
		LinkedList<T> result = new LinkedList<>();
		if( rasterSquares.length == 0 ) {
			return result;
		}
		for( int j = 0; j < rasterSquares[0].length; j++ ) {
			for( int i = 0; i < rasterSquares.length; i++ ) {
				T square = getSquare( i, j );
				if( square.isAccessible() ) {
					result.add( square );
				}
			}
		}
		return result;
	}

	@Override
	public void rasterize() {
		super.rasterize();

		// Barrieren durchgehen und entsprechende übergänge zwischen den Rasterquadraten sperren.
		for( Barrier barrier : getRoom().getBarriers() ) {
			// Jede Kante der aktuellen Barriere durchgehen und an der Kante liegende übergänge sperren.
			for( PlanEdge barrierEdge : barrier.getEdges() ) {

				// Für über der Kante liegende Rastersquares den Übergang nach unten sperren.
				List<T> squares = de.tu_berlin.math.coga.zet.converter.RasterTools.getSquaresAboveEdge( barrierEdge, this );
				for( T square : squares ) {
					square.blockDirection( Direction8.Down );
				}

				// Für unter der Kante liegende Rastersquares den Übergang nach oben sperren.
				squares = de.tu_berlin.math.coga.zet.converter.RasterTools.getSquaresBelowEdge( barrierEdge, this );
				for( T square : squares ) {
					square.blockDirection( Direction8.Top );
				}

				// Für links von der Kante liegende Rastersquares den Übergang nach rechts sperren.
				squares = de.tu_berlin.math.coga.zet.converter.RasterTools.getSquaresLeftOfEdge( barrierEdge, this );
				for( T square : squares ) {
					square.blockDirection( Direction8.Right );
				}

				// Für rechts von der Kante liegende Rastersquares den Übergang nach links sperren.
				squares = de.tu_berlin.math.coga.zet.converter.RasterTools.getSquaresRightOfEdge( barrierEdge, this );
				for( T square : squares ) {
					square.blockDirection( Direction8.Left );
				}
			}
		}

		// Alle Rasterquadrate als unbetretbar markieren, die außerhalb des Raumes liegen
		// (nicht blockiert durch Inaccessible Areas, sondern einfach außerhalb des Raumumrisses).
		List<T> insides = this.insideSquares();
		for( int i = 0; i < getColumnCount(); i++ ) {
			for( int j = 0; j < getRowCount(); j++ ) {
				RoomRasterSquare square = getSquare( i, j );
				if( !insides.contains( square ) ) {
					square.clearAccessible(); // NOT isAccessible

				}

			}
		}

		// Handle all stair areas
		for( StairArea stair : getRoom().getStairAreas() ) {
			generateStair( stair );
		}
		generateLevels();
	}

	/**
	 * Looks for all squares that lie along the lower level edge of the given
	 * stair area and are inside the area. Help method for generateStair().
	 *
	 * @param stair The stair area whose lower-level raster squares shall be
	 * returned.
	 * @return The raster squares which are inside the stair area and mark the low
	 * level of the stair.
	 */
	private List<RoomRasterSquare> generateLowBlock( StairArea stair ) {
		LinkedList<RoomRasterSquare> result = new LinkedList<>();
		Iterator<PlanEdge> itEd = stair.edgeIterator( stair.getLowerLevelStart(), stair.getLowerLevelEnd(), false );

		while( itEd.hasNext() ) {
			PlanEdge e = itEd.next();

			List<T> squaresAlongE = de.tu_berlin.math.coga.zet.converter.RasterTools.getSquaresAlongEdge( e, this );
			for( T square : squaresAlongE ) {
				if( stair.contains( square.getSquare() ) ) {
					result.add( square );
				}
			}
		}
		return result;
	}

	/**
	 * Generates the Levels of RasterSquares.
	 */
	private void generateLevels() {
		for( int row = 0; row < getRowCount(); row++ ) {
			for( int column = 0; column < getColumnCount(); column++ ) {
				RoomRasterSquare akt = getSquare( column, row );
				RoomRasterSquare temp;

				if( isValid( column - 1, row - 1 ) ) {
					temp = getSquare( column - 1, row - 1 );
					if( temp.isStair() && !akt.isBlocked( Direction8.TopLeft ) ) {//Richtung TopLeft
						if( akt.getStairPotential() < temp.getStairPotential() ) {
							akt.setLevel( Direction8.TopLeft, Level.Higher );
						} else {
							if( akt.getStairPotential() > temp.getStairPotential() ) {
								akt.setLevel( Direction8.TopLeft, Level.Lower );
							}
						}
					}
				}

				if( isValid( column, row - 1 ) ) {
					temp = getSquare( column, row - 1 );
					if( temp.isStair() && !akt.isBlocked( Direction8.Top ) ) {//Richtung Top
						if( akt.getStairPotential() < temp.getStairPotential() ) {
							akt.setLevel( Direction8.Top, Level.Higher );
						} else {
							if( akt.getStairPotential() > temp.getStairPotential() ) {
								akt.setLevel( Direction8.Top, Level.Lower );
							}
						}
					}
				}

				if( isValid( column + 1, row - 1 ) ) {
					temp = getSquare( column + 1, row - 1 );
					if( temp.isStair() && !akt.isBlocked( Direction8.TopRight ) ) {//Richtung TopRight
						if( akt.getStairPotential() < temp.getStairPotential() ) {
							akt.setLevel( Direction8.TopRight, Level.Higher );
						} else {
							if( akt.getStairPotential() > temp.getStairPotential() ) {
								akt.setLevel( Direction8.TopRight, Level.Lower );
							}
						}
					}
				}

				if( isValid( column - 1, row ) ) {
					temp = getSquare( column - 1, row );
					if( temp.isStair() && !akt.isBlocked( Direction8.Left ) ) {//Richtung Left
						if( akt.getStairPotential() < temp.getStairPotential() ) {
							akt.setLevel( Direction8.Left, Level.Higher );
						} else {
							if( akt.getStairPotential() > temp.getStairPotential() ) {
								akt.setLevel( Direction8.Left, Level.Lower );
							}
						}
					}
				}

				if( isValid( column + 1, row ) ) {
					temp = getSquare( column + 1, row );
					if( temp.isStair() && !akt.isBlocked( Direction8.Right ) ) {//Richtung Right
						if( akt.getStairPotential() < temp.getStairPotential() ) {
							akt.setLevel( Direction8.Right, Level.Higher );
						} else {
							if( akt.getStairPotential() > temp.getStairPotential() ) {
								akt.setLevel( Direction8.Right, Level.Lower );
							}
						}
					}
				}

				if( isValid( column - 1, row + 1 ) ) {
					temp = getSquare( column - 1, row + 1 );
					if( temp.isStair() && !akt.isBlocked( Direction8.DownLeft ) ) {//Richtung DownLeft
						if( akt.getStairPotential() < temp.getStairPotential() ) {
							akt.setLevel( Direction8.DownLeft, Level.Higher );
						} else {
							if( akt.getStairPotential() > temp.getStairPotential() ) {
								akt.setLevel( Direction8.DownLeft, Level.Lower );
							}
						}
					}
				}

				if( isValid( column, row + 1 ) ) {
					temp = getSquare( column, row + 1 );
					if( temp.isStair() && !akt.isBlocked( Direction8.Down ) ) {//Richtung Down
						if( akt.getStairPotential() < temp.getStairPotential() ) {
							akt.setLevel( Direction8.Down, Level.Higher );
						} else {
							if( akt.getStairPotential() > temp.getStairPotential() ) {
								akt.setLevel( Direction8.Down, Level.Lower );
							}
						}
					}
				}

				if( isValid( column + 1, row - 1 ) ) {
					temp = getSquare( column + 1, row - 1 );
					if( temp.isStair() && !akt.isBlocked( Direction8.DownRight ) ) {//Richtung DownRight
						if( akt.getStairPotential() < temp.getStairPotential() ) {
							akt.setLevel( Direction8.DownRight, Level.Higher );
						} else {
							if( akt.getStairPotential() > temp.getStairPotential() ) {
								akt.setLevel( Direction8.DownRight, Level.Lower );
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Generates the stairPotential of RasterSquares for a single stair area.
	 */
	private void generateStair( StairArea stair ) {
		List<RoomRasterSquare> lowBlock = null;
		try {
			lowBlock = generateLowBlock( stair );
		} catch( Exception e ) {
			System.err.println( "Exception: " + e.getMessage() );
			System.err.println( stair.toString() );
			System.err.println( stair.getAssociatedRoom().toString() );
			return;
		}

		List<RoomRasterSquare> nextList = new LinkedList<>();
		int potential = 0;

		// Initiate
		for( RoomRasterSquare c : lowBlock ) {
			c.setStair(); // Mark the squares as stair squares
      c.setUpSpeedFactor( stair.getSpeedFactorUp() );
      c.setDownSpeedFactor( stair.getSpeedFactorDown() );
			c.setStairPotential( potential );
		}
		nextList.addAll( lowBlock );

		// Start propagation of potential
		while( !nextList.isEmpty() ) {
			List<RoomRasterSquare> aktList = new LinkedList<>( nextList );
			nextList = new LinkedList<>();
			potential++;

			for( RoomRasterSquare c : aktList ) {
				for( RoomRasterSquare n : getNeighbours( c ) ) {
					if( stair.contains( n.getSquare() ) && n.getStairPotential() == -1 ) {
						nextList.add( n );
						n.setStair(); // Mark the squares as stair squares
						n.setUpSpeedFactor( stair.getSpeedFactorUp() );
						n.setDownSpeedFactor( stair.getSpeedFactorDown() );
						n.setStairPotential( potential );
					}
				}
			}
		}
	}

	/**
	 * @return All neighbor squares of square c.
	 */
	private ArrayList<RoomRasterSquare> getNeighbours( RoomRasterSquare c ) {

		int column = c.getColumn();
		int row = c.getRow();
		ArrayList<RoomRasterSquare> list = new ArrayList<>();

		if( isValid( column + 1, row ) ) {
			list.add( getSquare( column + 1, row ) );
		}
		if( isValid( column, row + 1 ) ) {
			list.add( getSquare( column, row + 1 ) );
		}
		if( isValid( column - 1, row ) ) {
			list.add( getSquare( column - 1, row ) );
		}
		if( isValid( column, row - 1 ) ) {
			list.add( getSquare( column, row - 1 ) );
		}
		if( isValid( column + 1, row + 1 ) ) {
			list.add( getSquare( column + 1, row + 1 ) );
		}
		if( isValid( column - 1, row - 1 ) ) {
			list.add( getSquare( column - 1, row - 1 ) );
		}
		if( isValid( column + 1, row - 1 ) ) {
			list.add( getSquare( column + 1, row - 1 ) );
		}
		if( isValid( column - 1, row + 1 ) ) {
			list.add( getSquare( column - 1, row + 1 ) );
		}

		return list;
	}

	/**
	 * Returns a String containing blanks for isAccessible and not delayed
	 * squares, 'x' for isAccessible but delayed squares and 'X' for
	 * isInaccessible squares. The room is limited by '-' and '|' characters.
	 *
	 * @return a String containing blanks for isAccessible and not delayed
	 * squares, 'x' for isAccessible but delayed squares and 'X' for
	 * isInaccessible squares.
	 */
	@Override
	public String toString() {
		String result = "";
		for( int i = 0; i < rasterSquares[0].length + 2; i++ ) {
			result += "-";
		}
		result += "\n";
		for( int j = 0; j < rasterSquares[0].length; j++ ) {
			result += "|";
			for( int i = 0; i < rasterSquares.length; i++ ) {
				if( getSquare( i, j ).isAccessible() ) {
					if( getSquare( i, j ).isExit() ) {
						result += "e";
					} else {
						if( getSquare( i, j ).isSave() ) {
							result += "s";
						} else {
							if( getSquare( i, j ).getSpeedFactor() < 1.0 ) {
								result += "d";
							} else {
								result += " ";
							}
						}
					}
				} else {
					result += "X";
				}
			}
			result += "|\n";
		}
		for( int i = 0; i < rasterSquares.length + 2; i++ ) {
			result += "-";
		}
		result += "\n";
		return result;
	}
}
