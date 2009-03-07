/*
 * RoomRaster.java
 */
package converter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import util.Direction;
import util.Level;
import converter.RoomRasterSquare.Property;
import ds.z.Barrier;
import ds.z.Edge;
import ds.z.Floor;
import ds.z.Room;
import ds.z.StairArea;
import java.util.Iterator;

/**
 * The <code>RoomRaster</code> class provides rasterization of a {@link ds.z.Room} object.
 * The squares of the raster have to be of the type {@link RoomRaster} which allows
 * to describe the {@link ds.z.Area} in which a square lies.
 * @param T the type of the used raster, have to be at least {@link RoomRasterSquare} objects
 * @see Raster
 */
public class RoomRaster<T extends RoomRasterSquare> extends Raster<T, Room> {

	/** The {@link ds.z.Floor} of this room. */
	Floor floor;
	/** The {@link ds.z.Room} that is rasterized. Redundant save helps avoiding casts. */
	Room r;
	LinkedList<T> accessibleSquares;

	/** Creates a new instance of <code>RoomRaster</code>
	 * @param squareClassType the type of the raster squares
	 * @param r the room
	 */
	public RoomRaster (Class<T> squareClassType, Room r) {
		super (squareClassType, Room.class, r);
		this.r = r;
		floor = r.getAssociatedFloor ();
		accessibleSquares = null;
	}

	/**
	 * Creates a new instance of <code>RoomRaster</code>.
	 * @param squareClassType the type of the squares used during rasterization
	 * @param r the room that will be rasterized
	 * @param raster the raster size
	 * @throws java.lang.IllegalArgumentException if raster is negative or zero
	 */
	public RoomRaster (Class<T> squareClassType, Room r, int raster) throws IllegalArgumentException {
		super (squareClassType, Room.class, r, raster);
		this.r = r;
		floor = r.getAssociatedFloor ();
		accessibleSquares = null;
	}

	/**
	 * Creates a new instance of <code>RoomRaster</code>
	 * @param squareClassType the type of the squares used during rasterization
	 * @param r the room that will be rasterized
	 * @param raster the raster size
	 * @throws java.lang.IllegalArgumentException if raster is negative or zero
	 */
	public RoomRaster (Class<T> squareClassType, Room r, double raster) throws IllegalArgumentException {
		super (squareClassType, Room.class, r, raster);
		this.r = r;
		floor = r.getAssociatedFloor ();
		accessibleSquares = null;
	}

	/**
	 * Returns the {@link Room} that is rasterized
	 * @return the room
	 */
	public Room getRoom () {
		return r;
	}

	/**
	 * Returns the {@link Floor} this room belongs to.
	 * @returnthe {@link Floor} this room belongs to.
	 */
	public Floor getFloor () {
		return floor;
	}

	/**
	 * Returns the raster square at the specified position in the array of squares.
	 * @param x the x position of the square, starts with 0
	 * @param y the y position of the square, starts with 0 in the upper corner
	 * @return the {@link RoomRasterSquare} object
	 */
	@Override
	public T getSquare (int x, int y) {
		return super.getSquare (x, y); //return rasterSquares[x][y];
	}

	/**
	 * Returns the squares intersecting the polygon.
	 * @return the squares intersecting the polygon
	 */
	@Override
	public List<T> insideSquares () {
		return super.insideSquares (); //Collections.unmodifiableList(insideSquares);
	}

	public LinkedList<T> getAccessibleSquares () {
		LinkedList<T> result = new LinkedList<T> ();
		for (int j = 0; j < rasterSquares[0].length; j++) {
			for (int i = 0; i < rasterSquares.length; i++) {
				T square = getSquare (i, j);
				if (square.accessible ()) {
					result.add (square);
				}
			}
		}
		return result;
	}

	@Override
	public void rasterize () {
		super.rasterize ();

		// Barrieren durchgehen und entsprechende übergänge zwischen den Rasterquadraten sperren.
		for (Barrier barrier : getRoom ().getBarriers ()) {
			// Jede Kante der aktuellen Barriere durchgehen und an der Kante liegende übergänge sperren.
			for (Edge barrierEdge : barrier.getEdges ()) {

				// Für über der Kante liegende Rastersquares den Übergang nach unten sperren.
				List<T> squares = util.ConversionTools.getSquaresAboveEdge (barrierEdge, this);
				for (T square : squares) {
					square.blockDirection (Direction.DOWN);
				}

				// Für unter der Kante liegende Rastersquares den Übergang nach oben sperren.
				squares = util.ConversionTools.getSquaresBelowEdge (barrierEdge, this);
				for (T square : squares) {
					square.blockDirection (Direction.UP);
				}

				// Für links von der Kante liegende Rastersquares den Übergang nach rechts sperren.
				squares = util.ConversionTools.getSquaresLeftOfEdge (barrierEdge, this);
				for (T square : squares) {
					square.blockDirection (Direction.RIGHT);
				}

				// Für rechts von der Kante liegende Rastersquares den Übergang nach links sperren.
				squares = util.ConversionTools.getSquaresRightOfEdge (barrierEdge, this);
				for (T square : squares) {
					square.blockDirection (Direction.LEFT);
				}
			}
		}

		// Alle Rasterquadrate als unbetretbar markieren, die au�erhalb des Raumes liegen
		// (nicht blockiert durch Inaccessible Areas, sondern einfach au�erhalb des Raumumrisses).
		List<T> insides = this.insideSquares ();
		for (int i = 0; i < getColumnCount (); i++) {
			for (int j = 0; j < getRowCount (); j++) {
				RoomRasterSquare square = getSquare (i, j);
				if (!insides.contains (square)) {
					square.clearProperty (Property.ACCESSIBLE); // NOT accessible
				}

			}
		}

		// Handle all stair areas
		for (StairArea stair : getRoom ().getStairAreas ()) {
			generateStair (stair);
		}
		generateLevels ();
	}
	
	/**
	 * Looks for all squares that lie along the lower level edge of the given stair area 
	 * and are inside the area. Help method for generateStair().
	 * @param stair The stair area whose lower-level raster squares shall be returned.
	 * @return The raster squares which are inside the stair area and mark the low level of the stair.
	 */
	private List<RoomRasterSquare> generateLowBlock (StairArea stair) {
		LinkedList<RoomRasterSquare> result = new LinkedList<RoomRasterSquare> ();
		Iterator<Edge> itEd = stair.edgeIterator(stair.getLowerLevelStart(), stair.getLowerLevelEnd(), false);
		
		while (itEd.hasNext()){
			Edge e = itEd.next();
			
			List<T> squaresAlongE = util.ConversionTools.getSquaresAlongEdge(e, this);
			for (T square : squaresAlongE){				
				if (stair.contains(square.getSquare())){
					result.add(square);
				}
			}
		}
		return result;
	}

	/**
	 * Generates the Levels of RasterSquares. 
	 */
	private void generateLevels () {
		for (int row = 0; row < getRowCount (); row++) {
			for (int column = 0; column < getColumnCount (); column++) {
				RoomRasterSquare akt = getSquare (column, row);
				RoomRasterSquare temp;

				if (isValid (column - 1, row - 1)) {
					temp = getSquare (column - 1, row - 1);
					if (temp.isStair () && !akt.isBlocked (Direction.UPPER_LEFT)) {//Richtung UPPER_LEFT
						if (akt.getStairPotential () < temp.getStairPotential ()) {
							akt.setLevel (Direction.UPPER_LEFT, Level.HIGHER);
						} else {
							if (akt.getStairPotential () > temp.getStairPotential ()) {
								akt.setLevel (Direction.UPPER_LEFT, Level.LOWER);
							}
						}
					}
				}

				if (isValid (column, row - 1)) {
					temp = getSquare (column, row - 1);
					if (temp.isStair () && !akt.isBlocked (Direction.UP)) {//Richtung UP
						if (akt.getStairPotential () < temp.getStairPotential ()) {
							akt.setLevel (Direction.UP, Level.HIGHER);
						} else {
							if (akt.getStairPotential () > temp.getStairPotential ()) {
								akt.setLevel (Direction.UP, Level.LOWER);
							}
						}
					}
				}

				if (isValid (column + 1, row - 1)) {
					temp = getSquare (column + 1, row - 1);
					if (temp.isStair () && !akt.isBlocked (Direction.UPPER_RIGHT)) {//Richtung UPPER_RIGHT
						if (akt.getStairPotential () < temp.getStairPotential ()) {
							akt.setLevel (Direction.UPPER_RIGHT, Level.HIGHER);
						} else {
							if (akt.getStairPotential () > temp.getStairPotential ()) {
								akt.setLevel (Direction.UPPER_RIGHT, Level.LOWER);
							}
						}
					}
				}

				if (isValid (column - 1, row)) {
					temp = getSquare (column - 1, row);
					if (temp.isStair () && !akt.isBlocked (Direction.LEFT)) {//Richtung LEFT
						if (akt.getStairPotential () < temp.getStairPotential ()) {
							akt.setLevel (Direction.LEFT, Level.HIGHER);
						} else {
							if (akt.getStairPotential () > temp.getStairPotential ()) {
								akt.setLevel (Direction.LEFT, Level.LOWER);
							}
						}
					}
				}

				if (isValid (column + 1, row)) {
					temp = getSquare (column + 1, row);
					if (temp.isStair () && !akt.isBlocked (Direction.RIGHT)) {//Richtung RIGHT
						if (akt.getStairPotential () < temp.getStairPotential ()) {
							akt.setLevel (Direction.RIGHT, Level.HIGHER);
						} else {
							if (akt.getStairPotential () > temp.getStairPotential ()) {
								akt.setLevel (Direction.RIGHT, Level.LOWER);
							}
						}
					}
				}

				if (isValid (column - 1, row + 1)) {
					temp = getSquare (column - 1, row + 1);
					if (temp.isStair () && !akt.isBlocked (Direction.LOWER_LEFT)) {//Richtung LOWER_LEFT
						if (akt.getStairPotential () < temp.getStairPotential ()) {
							akt.setLevel (Direction.LOWER_LEFT, Level.HIGHER);
						} else {
							if (akt.getStairPotential () > temp.getStairPotential ()) {
								akt.setLevel (Direction.LOWER_LEFT, Level.LOWER);
							}
						}
					}
				}

				if (isValid (column, row + 1)) {
					temp = getSquare (column, row + 1);
					if (temp.isStair () && !akt.isBlocked (Direction.DOWN)) {//Richtung DOWN
						if (akt.getStairPotential () < temp.getStairPotential ()) {
							akt.setLevel (Direction.DOWN, Level.HIGHER);
						} else {
							if (akt.getStairPotential () > temp.getStairPotential ()) {
								akt.setLevel (Direction.DOWN, Level.LOWER);
							}
						}
					}
				}

				if (isValid (column + 1, row - 1)) {
					temp = getSquare (column + 1, row - 1);
					if (temp.isStair () && !akt.isBlocked (Direction.LOWER_RIGHT)) {//Richtung LOWER_RIGHT
						if (akt.getStairPotential () < temp.getStairPotential ()) {
							akt.setLevel (Direction.LOWER_RIGHT, Level.HIGHER);
						} else {
							if (akt.getStairPotential () > temp.getStairPotential ()) {
								akt.setLevel (Direction.LOWER_RIGHT, Level.LOWER);
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
	private void generateStair (StairArea stair) {
		List<RoomRasterSquare> lowBlock = generateLowBlock (stair);

		List<RoomRasterSquare> aktList = new LinkedList<RoomRasterSquare> ();
		List<RoomRasterSquare> nextList = new LinkedList<RoomRasterSquare> ();
		int potential = 0;

		// Initiate
		for (RoomRasterSquare c : lowBlock) {
			c.setStair (); // Mark the squares as stair squares
			c.setStairPotential (potential);
		}
		nextList.addAll (lowBlock);
		
		// Start propagation of potential
		while (!nextList.isEmpty ()) {
			aktList = new LinkedList (nextList);
			nextList = new LinkedList ();
			potential++;
			
			for (RoomRasterSquare c : aktList) {
				for (RoomRasterSquare n : getNeighbours (c)) {
					if (stair.contains (n.getSquare ()) && n.getStairPotential () == -1) {
						nextList.add (n);
						n.setStair (); // Mark the squares as stair squares
						n.setUpSpeedFactor(stair.getSpeedFactorUp());
						n.setDownSpeedFactor(stair.getSpeedFactorDown());
						n.setStairPotential (potential);
					}
				}
			}
		}
	}

	/** @return All neighbour squares of square c. */
	private ArrayList<RoomRasterSquare> getNeighbours (RoomRasterSquare c) {

		int column = c.getColumn ();
		int row = c.getRow ();
		ArrayList<RoomRasterSquare> list = new ArrayList<RoomRasterSquare> ();

		if (isValid (column + 1, row)) {
			list.add (getSquare (column+1, row));
		}
		if (isValid (column, row + 1)) {
			list.add (getSquare (column, row+1));
		}
		if (isValid (column - 1, row)) {
			list.add (getSquare (column-1, row));
		}
		if (isValid (column, row - 1)) {
			list.add (getSquare (column, row-1));
		}
		if (isValid (column + 1, row + 1)) {
			list.add (getSquare (column+1, row+1));
		}
		if (isValid (column - 1, row - 1)) {
			list.add (getSquare (column-1, row-1));
		}
		if (isValid (column + 1, row - 1)) {
			list.add (getSquare (column+1, row-1));
		}
		if (isValid (column - 1, row + 1)) {
			list.add (getSquare (column-1, row+1));
		}

		return list;
	}

	/**
	 * Returns a String containing blanks for accessible and not delayed squares, 'x' for
	 * accessible but delayed squares and 'X' for inaccessible squares. 
	 * The room is limited by '-' and '|' characters. 
	 * @return a String containing blanks for accessible and not delayed squares, 'x' for
	 * accessible but delayed squares and 'X' for inaccessible squares. 
	 */
	@Override
	public String toString () {
		String result = "";
		for (int i = 0; i < rasterSquares[0].length + 2; i++) {
			result += "-";
		}
		result += "\n";
		for (int j = 0; j < rasterSquares[0].length; j++) {
			result += "|";
			for (int i = 0; i < rasterSquares.length; i++) {
				if (getSquare (i, j).accessible ()) {
					if (getSquare (i, j).isExit ()) {
						result += "e";
					} else {
						if (getSquare (i, j).getSave ()) {
							result += "s";
						} else {
							if (getSquare (i, j).getSpeedFactor () < 1.0) {
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
		for (int i = 0; i < rasterSquares.length + 2; i++) {
			result += "-";
		}
		result += "\n";
		return result;
	}
}
