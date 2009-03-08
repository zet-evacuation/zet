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
package util;

/**
 * This enumerates directions on a raster. The naming is such that the 
 * x-coordinate increases in the <code>right</code> direction while the 
 * y-coordinate increases in the <code>down</code> direction. 
 * Hence, if only positive coordinates are allowed, the point (0,0) 
 * lies in the up-most, left-most corner.
 * 
 * @author Daniel Pluempe
 *
 */
public enum Direction{
            
    LEFT(-1,0),  
    RIGHT(1,0, LEFT),           
    UP(0,-1),
    DOWN(0,1, UP),
    UPPER_LEFT(-1,-1),
    UPPER_RIGHT(1,-1, UPPER_LEFT),
    LOWER_LEFT(-1,1),
    LOWER_RIGHT(1,1, LOWER_LEFT);
    
    private final int xOffset;
    private final int yOffset;
    private Direction inverseDirection;
    
    private Direction(int xOffset, int yOffset, Direction inverseDirection){
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.inverseDirection = inverseDirection;
        inverseDirection.setInverse(this);        
    } 
    
    private Direction(int xOffset, int yOffset){
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
    
    public int xOffset(){
        return xOffset;
    }
    
    public int yOffset(){
        return yOffset;
    }
    
    public Direction invert(){
        return inverseDirection;
    }
    
    /**
     * Returns the enumeration item corresponding to the given x- and y-offsets.
     * @param xOffset x-offset
     * @param yOffset y-offset
     * @return the enumeration item corresponding to the given x- and y-offsets.
     */
    public static Direction getDirection(int xOffset, int yOffset){
    	int x = xOffset; int y = yOffset;
    	if (x == -1 && y ==  0) return LEFT;
    	if (x ==  1 && y ==  0) return RIGHT;
    	if (x ==  0 && y == -1) return UP;
    	if (x ==  0 && y ==  1) return DOWN;
    	if (x == -1 && y == -1) return UPPER_LEFT;
    	if (x ==  1 && y == -1) return UPPER_RIGHT;
    	if (x == -1 && y ==  1) return LOWER_LEFT;
    	if (x ==  1 && y ==  1) return LOWER_RIGHT;
    	throw new AssertionError("Not a valid direction");
    }
    
    private void setInverse(Direction inverseDirection){
        this.inverseDirection = inverseDirection;
    }
}
