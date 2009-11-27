/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
 * This class represents a room, which is a collection of Cells.
 * Individuals can stay in a room:
 * Each individual beeing in a room is standing on one Cell. 
 * @author marcel
 *
 */
public class Room {

	/** The id of the room (to calculate the hashCode). */
	private int id;
	/**
	 * Counts the number of existing Rooms. 
	 * Every new Room gets automatically a unique ID.
	 */
	private static int idCount = 0;
	/** Manages the DoorCells existing in this room. */
	private ArrayList<DoorCell> doors;
	/** Manages the individuals existing in this room. */
	private ArrayList<Individual> individuals;
	/** Manages the Cells into which the room is divided. */
	private Cell[][] cells;
	/** Number of Cells on the x-axis. */
	private int width;
	/** Description of Floor containing this room. */
	private String floor;
	/** Number of Cells on the y-axis. */
	private int height;
	
	private int floorID;
	
	private boolean isAlarmed;
	private int xOffset;
	private int yOffset;

	public Room( int width, int height, String floor, int floorID ) {
		this( width, height, floor, floorID, idCount);
		idCount++;

	}

	protected Room( int width, int height, String floor, int floorID, int id ) {
		this.width = width;
		this.height = height;
		this.floor = floor;
		this.floorID = floorID;
		doors = new ArrayList<DoorCell>();
		individuals = new ArrayList<Individual>();
		cells = new Cell[this.width][this.height];
		for( int i = 0; i < this.width; i++ ) {
			for( int j = 0; j < this.height; j++ ) {
				cells[i][j] = null;
			}
		}
		this.id = id;
		this.isAlarmed = false;
	}

	/**
	 * Returns the number of cells contained in this room. The parameter
	 * {@code allCells} indicates wheather the number of all cells is returned or
	 * the number of all cells that are not {@code null}. These cells can occur if
	 * there are "holes" in the room.
	 * @param allCells indicates wheather all cells are counted, or not
	 * @return the number of cells
	 */
	public int getCellCount( boolean allCells ) {
		int count = 0;
		if( allCells )
			count = width * height;
		else
			for( int i = 0; i < this.width; i++ )
				for( int j = 0; j < this.height; j++ )
					if( cells[i][j] != null )
						count++;
		return count;
	}

	/**
	 * Places the defined cell at Cell-Position (x,y) of the room.
	 * If parameter "cell" is a "DoorCell", it is added to the
	 * lists of Door-Cells. If a new Cell overwrites an old cell,
	 * it is checked whether the old cell was a DoorCell. In this 
	 * case the DoorCell will be removed from the list of DoorCells.
	 * @param cell The cell, which should be referenced at position (x,y).
	 * If position (x,y) shall be empty, set parameter cell = null.
	 * @throws IllegalArgumentException if the x- or the y-value of
	 * the parameter "cell" is out of bounds.
	 */
	public void setCell( Cell cell ) throws IllegalArgumentException {
		if( (cell.getX() < 0) || (cell.getX() > this.width - 1) ) {
			throw new IllegalArgumentException( "Invalid x-value in cell!" );
		}
		if( (cell.getY() < 0) || (cell.getY() > this.height - 1) ) {
			throw new IllegalArgumentException( "Invalid y-value in cell!" );
		}
		if( (cells[cell.getX()][cell.getY()] != null) &&
						(cells[cell.getX()][cell.getY()] instanceof DoorCell) ) {
			doors.remove( cell );
		}
		this.cells[cell.getX()][cell.getY()] = cell;
		if( cell instanceof DoorCell ) {
			doors.add( (DoorCell) cell );
		}
		cell.setRoom( this );
	}

	/**
	 * Returns the cell referenced at position (x,y)
	 * @param x x-coordinate of the cell. 0 <= x <= width-1
	 * @param y y-coordinate of the cell. 0 <= y <= height-1
	 * @return The cell referenced at position (x,y). If position (x,y) is empty
	 * (in other words: does not reference any cell) null is returned.
	 * @throws IllegalArgumentException if the x- or the y-parameter
	 * is out of bounds.
	 */
	public Cell getCell( int x, int y ) throws IllegalArgumentException {
		if( (x < 0) || (x > this.width - 1) ) {
			throw new IllegalArgumentException( "Invalid x-value!" );
		}
		if( (y < 0) || (y > this.height - 1) ) {
			throw new IllegalArgumentException( "Invalid y-value!" );
		}
		return this.cells[x][y];
	}

	/**
	 * Returns a list of all cells in the room.
	 * @return a list of all cells
	 */
	public ArrayList<Cell> getAllCells() {
		ArrayList<Cell> cells = new ArrayList<Cell>();
		for( int i = 0; i < getWidth(); i++)
			for( int j = 0; j < getHeight(); j++ ) {
				Cell t = getCell( i, j );
				if( t != null )
					cells.add( t );
			}
		return cells;	
	}
	
	/**
	 * Returns the setAlarmed status of this room.
	 * @return true if the setAlarmed status is true.
	 */
	public boolean getAlarmstatus() {
		return isAlarmed;
	}

	/**
	 * Sets the setAlarmed status of this room. If set to true, the individuals are
	 * <b>not</b> automatically alarmed.
	 * @param status the setAlarmed status
	 */
	public void setAlarmstatus( boolean status ) {
		this.isAlarmed = status;
	}

	/**
	 * Returns the number of the floor containing this room
	 * @return Floornumber
	 */
	public String getFloor() {
		return floor;
	}

	/**
	 * Sets the name of the floor that contains this room.
	 * @param floorName
	 */
	public void setFloor( String floorName ) {
		this.floor = floorName;
	}

	/**
	 * Returns an ArrayList containing the doors of the room.
	 * @return An ArrayList containing the doors of the room.
	 */
	public ArrayList<DoorCell> getDoors() {
		return doors;
	}

	/**
	 * Returns an ArrayList containing the individuals being the room.
	 * @return An ArrayList containing the individuals being the room.
	 */
	public ArrayList<Individual> getIndividuals() {
		return individuals;
	}

	public int getXOffset() {
		return xOffset;
	}
	
	public void setXOffset( int xOffset ) {
		this.xOffset = xOffset;
	}

	public int getYOffset() {
		return yOffset;
	}
	
	public void setYOffset( int yOffset ) {
		this.yOffset = yOffset;
	}

	/**
	 * This method allows an individual to enter the room by using a DoorCell.
	 * The entering individual is registered in the list of individuals staying in
	 * the room.
	 * @throws The method throws an <code>IllegalArgumentException</code> if you
	 * try to enter this Room with a Individual not standing on a DoorCell, if the 
	 * DoorCell does not belong to this Room, or if the current DoorCell of the 
	 * individual does not have a reference to the the defined "nextDoor"-Cell.
	 * @param who The individual entering the room.
	 */
//	 void enter( Individual who, DoorCell nextDoor ) throws IllegalArgumentException {
//		if( !(who.getCell() instanceof DoorCell) ) {
//			throw new IllegalArgumentException( "The Individual does not stand on a DoorCell!" );
//		}
//		if( nextDoor.getRoom() != this ) {
//			throw new IllegalArgumentException( "The DoorCell of the Individual does not belong to this Room!" );
//		}
//		if( !(((DoorCell) (who.getCell())).containsNextDoor( nextDoor )) ) {
//			throw new IllegalArgumentException( "The current DoorCell of the individual is not connected to ''nextDoor''!" );
//		}
//		nextDoor.setIndividual( who );
//		who.setCell( nextDoor );
//		individuals.add( who );
//	}

	/**
	 * The individual standing on the DoorCell "exit" leaves the room. This individual
	 * is removed from the list of individuals staying in the room and it is also
	 * removed from the DoorCell "exit" and then returned by this method.
	 * @throws The method throws an <code>IllegalArgumentException</code>
	 * if you try to call "leave()" with a 
	 * DoorCell which is not occupied by an Individual.
	 * @throws Throws an <code>IllegalArgumentException</code>, if the cell, from
	 * which the individual should leave the room, is not of type "ExitCell" or "DoorCell".
	 * @param exit The DoorCell the individual uses for leaving the room.
	 * @return The individual leaving the room by using the DoorCell "exit".
	 */
//	Individual leave( Cell exit ) throws IllegalArgumentException {
//		if( exit.getIndividual() == null ) {
//			throw new IllegalArgumentException( "No Individual standing on this DoorCell!" );
//		}
//		if( !((exit instanceof DoorCell) || (exit instanceof ExitCell)) ) {
//			throw new IllegalArgumentException( "Exit must be of type DoorCell or ExitCell!" );
//		}
//		Individual leavingIndividual = exit.getIndividual();
//		individuals.remove( leavingIndividual );
//		//exit.setIndividual(null);
//		exit.removeIndividual(); // individual is entirely removed!
//		return leavingIndividual;
//	}

	/**
	 * The individual defined by the Parameter "individual" leaves the room.
	 * @param individual The Individual which shall leave the room.
	 * @throws IllegalArgumentException Throws an IllegalArgumentException, if the individual
	 * is not standing on a DoorCell or an ExitCell, which is neccesary for leaving a room.
	 */
//	public void leave( Individual individual ) throws IllegalArgumentException {
//		if( !(individual.getCell() instanceof DoorCell) &&
//						!(individual.getCell() instanceof ExitCell) ) {
//			throw new IllegalArgumentException( "The individual is not standing on a cell, from which it can leave the Room!" );
//		}
//		this.leave( individual.getCell() );
//	}

	/**
	 * 
	 * @param c
	 * @param i
	 */
	public void addIndividual( Cell c, Individual i ) {
		if( !c.getRoom().equals(  this ) ) {
			throw new IllegalStateException( "The cell does not belong to this room." );
		}
		c.setIndividual( i );
		i.setCell( c );
		individuals.add( i );
	}

	
	private void checkIndividual( Individual i ) throws IllegalStateException {
		if( !individuals.contains( i ) ) {
			throw new IllegalStateException( "Individual " + i.id() + " is not in the room." );
		}		
	}
	
	public void removeIndividual( Individual i ) {
		checkIndividual( i );
		if( i.getCell() == null ) {
		} else {
			if( !i.getCell().getRoom().equals(  this ) ) {
				throw new IllegalStateException( "Individual is in the room, but the cell is in another room." );
			}
			i.getCell().removeIndividual();
			i.setCell( null );		
		}
		individuals.remove( i );
	}
	
	void moveIndividual( Cell from, Cell to ) throws IllegalStateException {
		Individual i = from.getIndividual();
		checkIndividual( i );
		to.setIndividual( from.getIndividual() ); // removeIndividual is implicitly called
		i.setCell( to );
		from.removeIndividual();
	}
	
	void swapIndividuals( Cell cell1, Cell cell2 ) throws IllegalStateException {
		Individual c1i = cell1.getIndividual();
		Individual c2i = cell2.getIndividual();
		checkIndividual( c1i );
		checkIndividual( c2i );
		c1i.setCell( null );
		c2i.setCell( null );
		cell1.setIndividual( c2i );
		cell2.setIndividual( c1i );
		c1i.setCell( cell2 );
		c2i.setCell( cell1 );
	}
	
	/**
	 * Removes the individual from the room`s list of individuals.
	 * Is used when an individual is taken out of the simulation
	 * @param individual The Individual which shall leave
	 */
	//public void takeIndividualOut( Individual i ) {
	//	individuals.remove( i );
	//}

	/**
	 * Returns the number of cells on the x-axis of the room.
	 * @return The number of cells on the x-axis of the room.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the number of cells on the y-axis of the room.
	 * @return The number of cells on the y-axis of the room.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Checks whether the cell at position (x,y) of the room 
	 * exists or not
	 * @param x x-coordinate of the cell to be checked
	 * @param y y-coordinate of the cell to be checked
	 * @return "true", if the cell at position (x,y) exists,
	 * "false", if not
	 */
	public boolean existsCellAt( int x, int y ) {
		if( (x < 0) || (x > (this.getWidth() - 1)) ) {
			return false;
		} else if( (y < 0) || (y > (this.getHeight() - 1)) ) {
			return false;
		} else if( this.getCell( x, y ) == null ) {
			return false;
		} else {
			return true;
		}
	}

//  HashCode und Equals auskommentiert: Wenn zwei Räume gleich sind,
//  wenn sie die gleiche ID haben verlieren wird die Moeglichkeit, 
//	Raeume unter Erhaltung der ID zu Klonen und in einer HashMap Klone 
//	auf ihre Originale abzubilden. Dies wird an mehreren Stellen 
//  benoetigt. Die oben beschriebene Gleichheit wird nirgendwo benutzt und
//  war fuer mehrere Bugs verantwortlich.      

	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj){
	    Room room = (Room)obj;
	    return room.getID() == id;
	}

	public int getID() {
		return id;
	}
	
	public int getFloorID(){
	    return floorID;
	}

	/**
	 * Returns the id, width, height and floor
	 */
	@Override
	public String toString() {
		return "id=" + id + ";width=" + width + ";height=" + height + ";floor=" + floor;
	}

	public String graphicalToString() {
		String graphic = "+---";
		for( int i = 1; i < width; i++ ) {
			graphic += "----";
		}
		graphic += "+\n";

		for( int y = 0; y < height; y++ ) {
			for( int x = 0; x < width; x++ ) {
				if( getCell( x, y ) != null ) {
					graphic += "|";
					graphic += getCell( x, y ).graphicalToString();
				} else {
					graphic += "| X ";
				}
			}
			graphic += "|\n";
			graphic += "+---";
			for( int i = 1; i < width; i++ ) {
				graphic += "----";
			}
			graphic += "+\n";
		}

		graphic += "\n\n";

		return graphic;
	}

	public void clear() {
		this.doors.clear();
		//for( Individual i : individuals ) {
		//	i.getCell().removeIndividual();
		//}
		//this.individuals.clear();

		for( int i = 0; i < cells.length; i++ ) {
			for( int j = 0; j < cells[i].length; j++ ) {
				cells[i][j] = null;
			}
		}
	}

	@Override
	public Room clone() {
		Room clone = new Room( width, height, floor, floorID, id );

		clone.isAlarmed = this.isAlarmed;
		for( DoorCell door : this.doors ) {
			clone.doors.add( door );
		}

		for( Individual individual : this.individuals ) {
			clone.individuals.add( individual );
		}

		for( int x = 0; x < cells.length; x++ ) {
			for( int y = 0; y < cells[x].length; y++ ) {
				clone.cells[x][y] = this.cells[x][y];
			}
		}

		return clone;
	}
        
        public String getIdentificationForStatistic() {
            return("in floor "+getFloor()+", width=" + width + ";height=" + height);
        }
}
