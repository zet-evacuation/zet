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
package ds.ca.evac;

/**
 * A Save-Cell is special type of cell and therefore inherits properties and methods
 * from the abstract class Cell. When an individual enters this special cell, 
 * it is evacuated. But in contrast to a Room-Cell, individuals do not leave this
 * cell immediately. They wait until they become evacuated.
 * @author marcel
 *
 */
public class SaveCell extends TargetCell implements Cloneable {
	/**
	 * Constant defining the standard Speed-Factor of a Save-Cell, which may be < 1
	 */
	public static final double STANDARD_SAVECELL_SPEEDFACTOR = 0.8d;	
	
    /**
     * This constructor creates an empty Save-Cell with the standard 
     * SaveCell-Speed-Factor "STANDARD_SAVECELL_SPEEDFACTOR".
     * @param x x-coordinate of the cell in the room, 0 <= x <= width-1
     * @param y y-coordinate of the cell in the room, 0 <= y <= height-1
     */
	public SaveCell (int x, int y) 
    {
		this(SaveCell.STANDARD_SAVECELL_SPEEDFACTOR, x, y);
    }
	
	/**
	 * This constructor creates an empty Save-Cell with a manual set Speed-Factor.
	 * @param speedFactor Defines how fast the cell can be crossed. The value should
     * be a rational number greater than or equal to 0 and smaller or equal to 1. 
     * Otherwise the standard value "STANDARD_SAVECELL_SPEEDFACTOR" is set.
     * @param x x-coordinate of the cell in the room, 0 <= x <= width-1
     * @param y y-coordinate of the cell in the room, 0 <= y <= height-1
	 */
	public SaveCell(double speedFactor, int x, int y){
	    this(speedFactor, x, y, null);
	}
	
	
	public SaveCell(double speedFactor, int x, int y, Room room)
	{
		super(null, speedFactor, x, y, room);
	    graphicalRepresentation = '*';
			exitPotential = null;
	}
	
    /**
     * Changes the Speed-Factor of the Save-Cell to the specified value.
     * @param speedFactor Defines how fast the cell can be crossed. The value should
     * be a rational number greater than or equal to 0 and smaller or equal to 1. 
     * Otherwise the standard value "STANDARD_SAVECELL_SPEEDFACTOR" is set.
     */
	public void setSpeedFactor(double speedFactor)
    {
    	if ((speedFactor >= 0) && (speedFactor <= 1))
    		this.speedFactor = speedFactor;
    	else
    		this.speedFactor = SaveCell.STANDARD_SAVECELL_SPEEDFACTOR;
    }
	
    /**
     * Returns a copy of itself as a new Object.
     */
    public SaveCell clone(){
        return clone(false);
    }
    
    public SaveCell clone(boolean cloneIndividual){
    	SaveCell aClone = new SaveCell(this.getX(), this.getY());
    	basicClone(aClone, cloneIndividual);
    	return aClone;
    }
    
    public String toString(){
    	return "S;"+super.toString();
    }

		private StaticPotential exitPotential;
		
		public StaticPotential getExitPotential() {
			return exitPotential;
		}
		
		public void setExitPotential( StaticPotential sp ) {
			exitPotential = sp;
		}
		
    public String getName(){
    	return "Sicherheitsbereich";
    }
}

