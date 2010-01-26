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
package converter;

/**
 * The class <code>ZToGraphRasteredDoor</code> represents a rasterized door
 * in a format that the graph conversion needs.
 * A door connects two different raster squares in rooms that may be in two different floors
 * (actually these doors would be teleport edges ;-)). 
 */
public class ZToGraphRasteredDoor {
	
	ZToGraphRasterSquare first, second;
	
	public ZToGraphRasteredDoor(ZToGraphRasterSquare first, ZToGraphRasterSquare second){
		this.first = first;
		this.second = second;
	}
	
	public ZToGraphRasterSquare getFirstDoorPart(){
		return first;
	}
	
	public ZToGraphRasterSquare getSecondDoorPart(){
		return second;
	}
	
}