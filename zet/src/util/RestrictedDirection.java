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
 * The difference  to the enumeration <code>direction</code> is that
 * the <code>restrictedDirection</code> only allows up, down, left
 * and right and not upper_left and so on.
 *
 */
public enum RestrictedDirection{
            
    LEFT(-1,0),      
    RIGHT(1,0),
    UP(0,1),
    DOWN(1,0);
    
    private final int xOffset;
    private final int yOffset;
    
    private RestrictedDirection(int xOffset, int yOffset){
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
    
    public int xOffset(){
        return xOffset;
    }
    
    public int yOffset(){
        return yOffset;
    }
}
