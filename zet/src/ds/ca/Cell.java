package ds.ca;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;

import util.Direction;
import util.Level;

/**
 * The Cellular Automaton devides a room into quadratic cells.
 * This abstract class "Cell" describes such a cell, which is a part of the room.
 * It is kept abstract, because there are several special kinds of cells, such as 
 * door cells, stair cells and exit cells.
 * Generally each cell can be occupied by an individual and can be crossed with a
 * certain speed.
 * @author marcel
 *
 */
public abstract class Cell implements Comparable<Cell> {

	/**
	 * This character is used for graphic-like ASCII-output 
	 */
	protected char graphicalRepresentation = ' ';
	/**
	 * Defines the Speed-Factor of the Cell. In other words it defines a value, how
	 * fast this cell can be crossed.
	 */
	protected double speedFactor;
	/**
	 * Manages the individual that occupies the cell.
	 */
	protected Individual individual;
	/**
	 * x-coordinate of the cell in the room
	 */
	protected int x;
	/**
	 * y-coordinate of the cell in the room
	 */
	protected int y;
	/**
	 * The room to which the cell belongs
	 */
	protected Room room;
	/**
	 * The bounds of the  cell
	 */
	protected EnumSet<Direction> bounds;
	/**
	 * Tells whether the surrounding squares are higher, equal or lower.
	 */
	protected EnumMap<Direction, Level> levels;
	/** Stores the hashCode of this cell. 
	 */
	protected int hash;

	/**
	 * Constructor defining the values of individual and speedFactor.
	 * @param individual Defines the individual that occupies the cell. If the cell
	 * is not occupied, the value is to be set to "null".
	 * @param speedFactor Defines how fast the cell can be crossed. The value should
	 * be a rational number greater than or equal to 0 and smaller or equal to 1. 
	 * Otherwise the standard value of the specific class which inherits from Cell 
	 * is set.
	 * @param x x-coordinate of the cell in the room
	 * @param y y-coordinate of the cell in the room
	 */
	public Cell( Individual individual, double speedFactor, int x, int y ) {
		this( individual, speedFactor, x, y, null );
	}

	public Cell( Individual individual, double speedFactor, int x, int y, Room room ) {
		this.individual = individual;
		this.setSpeedFactor( speedFactor );
		
		// Must be in this order
		this.x = x;
		this.y = y;
		setRoom (room);
		
		this.bounds = EnumSet.noneOf( Direction.class );
		this.levels = new EnumMap( Direction.class );
	}

	/**
	 * Returns the individual that currently occupies the cell.
	 * @return The individual that occupies the cell.
	 */
	public Individual getIndividual() {
		return individual;
	}
//     * Sets a new individual onto the cell and calls the Individual.setCell()-Method
//     * to register itself as the current cell of the individual.
	/**
	 * Sets an individual on the cell. It is <b>not</b> automatically removed
	 * from its source cell nor are rooms or any lists updated.
	 * @param individual The individual occupying the cell from now on. Set this 
	 * value to "null" in order to mark this cell as not occupied.
	 */
	void setIndividual( Individual i ) {
		if( i == null ) {
			throw new java.lang.NullPointerException( "Individual is null." );
		//Cell old = individual.getCell();
		}
		this.individual = i;
	//individual.setCell(this);
	// remove from old cell if it had an old cell
	//if(old != null && !(old == this))
	//	old.removeIndividual();
	}

	void removeIndividual() {
		individual = null;
	}

	/**
	 * Returns the Speed-Factor of the cell.
	 * @return The Speed-Factor of the cell.
	 */
	public double getSpeedFactor() {
		return speedFactor;
	}

	/**
	 * Changes the Speed-Factor of the Cell to the specified value. This method is 
	 * kept abstract because the standard values
	 * may differ in the specific classes which inherit from Cell.
	 * @param speedFactor Defines how fast the cell can be crossed. The value should
	 * be a rational number greater than or equal to 0 and smaller or equal to 1. 
	 * Otherwise the standard value of the specific class which inherits 
	 * from Cell is set.
	 */
	public abstract void setSpeedFactor( double speedFactor );

	/**
	 * Returns all existing direct-neighbour-cells that are reachable of 
	 * this cell
	 * @return ArrayList of direct-neighbour-cells of "cell"
	 */
	public ArrayList<Cell> getNeighbours() {

//    	int[][] neighbourCoord = {{this.getX()+0, this.getY()+1},
//    							  {this.getX()+1, this.getY()+1},
//    						   	  {this.getX()+1, this.getY()+0},
//    							  {this.getX()+1, this.getY()-1},
//    							  {this.getX()+0, this.getY()-1},
//    							  {this.getX()-1, this.getY()-1},
//    							  {this.getX()-1, this.getY()+0},
//    							  {this.getX()-1, this.getY()+1}};
//    	for (int i = 0 ; i < neighbourCoord.length ; i++)
//    		if (room.existsCellAt(neighbourCoord[i][0], neighbourCoord[i][1]))
//    			neighbours.add(room.getCell(neighbourCoord[i][0], neighbourCoord[i][1]));


		return getNeighbours( true, false );
	}

	/**
	 * Returns all existing direct-neighbour-cells cell (even those that
	 * are not reachable).
	 * @return ArrayList of direct-neighbour-cells of "cell"
	 */
	public ArrayList<Cell> getAllNeighbours() {
		return getNeighbours( false, false );
	}

	public ArrayList<Cell> getFreeNeighbours() {
		return getNeighbours( true, true );
	}

	/**
	 * Returns the x-coordinate of the cell.
	 * @return The x-coordinate of the cell.
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * Returns the y-coordinate of the cell.
	 * @return The y-coordinate of the cell.
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * Returns the room to which the cell belongs
	 * @return The room to which the cell belongs
	 */
	public Room getRoom() {
		return room;
	}

	/**
	 * Manages the room to which the cell belongs. This method can only be called 
	 * by classes belonging to the same package in order to prevent misuse.
	 * It should only be called by Room.add(cell) in order so set the room
	 * corresponding to the cell automatically.
	 * @param room The room to which the cell belongs
	 */
	void setRoom( Room room ) {
		this.room = room;
		
		// Recompute the hash code
		String s = ((room != null) ? room.getID() : "") + "-" + y + "-" + x;
		hash = s.hashCode ();
	}

	/**
	 * Specifies the level difference between this cell and the cell
	 * at the relative position <code>relPosition</code>.
	 * @param relPosition The relative position of the wished neighbour cell.
	 * @param level The level of the other cell according to this cell, can be higher, equal or lower.
	 */
	public void setLevel( Direction relPosition, Level level ) {
		if( this.room.existsCellAt( x + relPosition.xOffset(), y + relPosition.yOffset() ) ) {
			this.room.getCell( x + relPosition.xOffset(), y + relPosition.yOffset() ).internalSetLevel( relPosition.invert(), level.getInverse() );
		}

		internalSetLevel( relPosition, level );

	}

	public void internalSetLevel( Direction relPosition, Level level ) {
		levels.put( relPosition, level );
	}

	/**
	 * Specifies that this cell is separated from one of its
	 * neighbour cells by an unpenetrable bound and that thus
	 * this neighbour cell cannot be directly reached from 
	 * this cell.
	 * @param relPosition The relative position  of the 
	 * unreachable neighbour.  
	 */
	public void setUnPassable( Direction relPosition ) {
		if( this.room.existsCellAt( x + relPosition.xOffset(), y + relPosition.yOffset() ) ) {
			this.room.getCell( x + relPosition.xOffset(), y + relPosition.yOffset() ).internalSetUnPassable( relPosition.invert() );
		}

		internalSetUnPassable( relPosition );
	}

	private void internalSetUnPassable( Direction relPosition ) {
		bounds.add( relPosition );
	}

	/**
	 * Specifies that the way from this cell to one
	 * of its neighbour cells is clear.
	 * @param relPosition The relative position of 
	 * the neighbour cell.
	 */
	public void setPassable( Direction relPosition ) {
		if( this.room.existsCellAt( x + relPosition.xOffset(), y + relPosition.yOffset() ) ) {
			this.room.getCell( x + relPosition.xOffset(), y + relPosition.yOffset() ).internalSetPassable( relPosition.invert() );
		}
		internalSetPassable( relPosition );
	}

	private void internalSetPassable( Direction relPosition ) {
		bounds.remove( relPosition );
	}

	/**
	 * Asks whether the direct way to a neighbour
	 * cell of this cell is clear.
	 * @param relPosition The relative position of
	 * the neighbour cell
	 * @return <code>true</code> if the way is
	 * clear or <code>false</code> if the way
	 * is blocked.
	 */
	public boolean isPassable( Direction relPosition ) {
		return !bounds.contains( relPosition );
	}

	/**
	 * Returns the level difference between this cell and the cell 
	 * at the relative position <code>relPosition</code>.
	 * If the level has never been set explicitly, EQUAL is returned.
	 * @param direction The square in this direction is considered.
	 * @return the level of the square in direction <code>direction</code> (higher, equal or lower).
	 */
	public Level getLevel( Direction direction ) {
		if( levels.containsKey( direction ) ) {
			return levels.get( direction );
		} else {
			return Level.EQUAL;
		}
	}

	@Override
	/**
	 * Returns a copy of itself as a new Object.
	 */
	public abstract Cell clone();

	public Cell clone( boolean cloneIndividual ) {
		return clone();
	}
//  HashCode und Equals auskommentiert: Wenn zwei Zellen schon gleich sind,
//  wenn sie im gleichen Raum liegen und die gleichen Koordinaten haben, 
//  verlieren wir die MÃ¶glichkeit, Zellen zu Klonen und in einer HashMap
//  Klone auf ihre Originale abzubilden. Dies wird an mehreren Stellen 
//  benoetigt. Die oben beschriebene Gleichheit wird nirgendwo benutzt und
//  war fuer mehrere Bugs verantwortlich.      
//    
//    
	@Override
	public boolean equals( Object obj ) {
		if( !(obj instanceof Cell) ) {
			return false;
		}

		Cell c = (Cell) obj;

		if( c.getRoom() == null && this.getRoom() == null ) {
			return (c == this);
		}

		if( c.getRoom() == null || this.getRoom() == null ) {
			return false;
		}

		if( this.room.equals( c.getRoom() ) && this.x == c.getX() && this.y == c.getY() ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		// This result must be brought independently of the current value of the
		// room variable because this obejct must be able to be serialized using
		// XStream, and this causes an error because it invokes this method before
		// the room field is filled with its former content again.
		return hash;
	}

	@Override
	/**
	 * Returns the type of the cell (D for door-, E for exit-, R for room-, S for save-, T for stair-cell), coordinates of the cell, the speedfactor, if it's occupied and its room
	 */
	public String toString() {
		return "(" + x + "," + y + "),speedfactor=" + speedFactor + ";is occupied=" + (individual == null ? "false;" : "true;") + "id=" + hashCode() + " R: " + room + ";";
	}

	/**
	 * A string representation that only consists of the coordinates.
	 * @return a tupel of the <code>x</code> and <code>y</code> coordinates.
	 */
	public String coordToString() {
		return "(" + x + "," + y + ")";
	}

	protected ArrayList<Cell> getNeighbours( boolean passableOnly, boolean freeOnly ) {
		ArrayList<Cell> neighbours = new ArrayList<Cell>();
		Room cellRoom = this.getRoom();
		for( Direction direction : Direction.values() ) {
			int cellx = this.getX() + direction.xOffset();
			int celly = this.getY() + direction.yOffset();
			if( cellRoom.existsCellAt( cellx, celly ) && (!passableOnly || !bounds.contains( direction )) && (!freeOnly || cellRoom.getCell( cellx, celly ).individual == null) ) {

				neighbours.add( cellRoom.getCell( cellx, celly ) );
			}
		}

		return neighbours;
	}

	/**
	 * Returns the neighbour in a given direction.
	 * @param dir the direction.
	 * @return the neighbour, if exists. null else.
	 */
	public Cell getNeighbour( Direction dir ) {
		int cellx = getX() + dir.xOffset();
		int celly = getY() + dir.yOffset();
		if( getRoom().existsCellAt( cellx, celly ) ) {
			return getRoom().getCell( cellx, celly );
		} else {
			return null;
		}
	}

	/**
	 * Returns a string with a graphic like representation of this room.
	 * @return A string representing this room.
	 */
	public String graphicalToString() {
		String graphic = "";
		if( individual == null ) {
			graphic = graphicalRepresentation + " " + graphicalRepresentation;
		} else {
			graphic = graphicalRepresentation + "I" + graphicalRepresentation;
		}

		return graphic;
	}

	/**
	 * Removes the individual from the cell.
	 * Is used when an individual is taken out of the simulation
	 */
//	public void takeIndividualOut() {
//		removeIndividual();
	//setIndividual(null);
//	}

	public boolean equals( Cell c ) {
		return (this.getX() == c.getX() && this.getY() == c.getY() && this.hashCode() == c.hashCode());
	}

	protected <T extends Cell> T basicClone( T aClone, boolean cloneIndividual ) {
		aClone.setSpeedFactor( this.getSpeedFactor() );

		if( cloneIndividual && this.getIndividual() != null ) {
			aClone.individual = this.getIndividual().clone();
			aClone.individual.setCell( aClone );
		} else {
			aClone.individual = this.getIndividual();
		}

		aClone.room = this.room;

		for( Direction bound : this.bounds ) {
			aClone.bounds.add( bound );
		}

		return aClone;
	}
	// ob das so funktioniert?
	/**
	 * @param c the cell that is compared to
	 * @return -1, 0 or 1
	 */
	public int compareTo( Cell c ) {
		if( c.x == x && c.y == y ) {
			if( c.hashCode() == hashCode() ) {
				return 0;
			} else if( c.hashCode() < hashCode() ) {
				return 1;
			} else {
				return -1;
			}
		} //if( c.x == x && c.y == y )
		//	return 0;
		else if( c.x < x || (c.x == x && c.y < y) ) {
			return 1;
		} else {
			return -1;
		}
	}
}
