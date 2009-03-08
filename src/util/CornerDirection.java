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
 * This enumerates the directions of the corners seen from the center
 * of a raster square. 
 *
 */
public enum CornerDirection{
            
    UPPER_LEFT(-1,1),
    UPPER_RIGHT(1,1),
    LOWER_LEFT(-1,-1),
    LOWER_RIGHT(1,-1);
    
    private final int xOffset;
    private final int yOffset;
    
    private CornerDirection(int xOffset, int yOffset){
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
