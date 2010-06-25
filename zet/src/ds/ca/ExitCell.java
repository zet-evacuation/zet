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

/**
 * An Exit-Cell is special type of cell and therefore inherits properties and methods
 * from the abstract class Cell. When an individual enters this special cell, 
 * it is evacuated.
 * @author marcel
 *
 */
public class ExitCell extends TargetCell implements Cloneable {

	/**
	 * Constant defining the standard Speed-Factor of an Exit-Cell, which may be < 1
	 */
	public static final double STANDARD_EXITCELL_SPEEDFACTOR = 0.8d;
	/**
	 * Attractivity for this ExitCell
	 */
	private int attractivity;
	private String name = "ExitCell";

	/**
	 * This constructor creates an empty Exit-Cell with the standard Speed-Factor.
	 * @param x x-coordinate of the cell in the room, 0 <= x <= width-1
	 * @param y y-coordinate of the cell in the room, 0 <= y <= height-1
	 */
	public ExitCell( int x, int y ) {
		this( ExitCell.STANDARD_EXITCELL_SPEEDFACTOR, x, y, null );
	}

	public ExitCell( double speedFactor, int x, int y ) {
		this( speedFactor, x, y, null );
	}

	/**
	 * This constructor creates an empty ExitCell with a manual set Speed-Factor.
	 * @param speedFactor Defines how fast the cell can be crossed. The value should
	 * be a rational number greater than or equal to 0 and smaller or equal to 1.
	 * Otherwise the standard value "STANDARD_EXITCELL_SPEEDFACTOR" is set.
	 * @param x x-coordinate of the cell in the room, 0 <= x <= width-1
	 * @param y y-coordinate of the cell in the room, 0 <= y <= height-1
	 */
	public ExitCell( double speedFactor, int x, int y, Room room ) {
		super( null, speedFactor, x, y, room );
		graphicalRepresentation = '#';
	}

	/**
	 * Changes the Speed-Factor of the Exit-Cell to the specified value.
	 * @param speedFactor Defines how fast the cell can be crossed. The value should
	 * be a rational number greater than or equal to 0 and smaller or equal to 1.
	 * Otherwise the standard value "STANDARD_EXITCELL_SPEEDFACTOR" is set.
	 */
	public void setSpeedFactor( double speedFactor ) {
		if( (speedFactor >= 0) && (speedFactor <= 1) )
			this.speedFactor = speedFactor;
		else
			this.speedFactor = ExitCell.STANDARD_EXITCELL_SPEEDFACTOR;
	}

	/**
	 * Returns a copy of itself as a new Object.
	 */
	public ExitCell clone() {
		return clone( false );
	}

	public ExitCell clone( boolean cloneIndividual ) {
		ExitCell aClone = new ExitCell( this.getX(), this.getY() );
		basicClone( aClone, cloneIndividual );
		aClone.setName( getName() );
		return aClone;
	}

	public String toString() {
		return "E;" + super.toString();
	}

	public int getAttractivity() {
		return attractivity;
	}

	public void setAttractivity( int attractivity ) {
		this.attractivity = attractivity;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}
}
