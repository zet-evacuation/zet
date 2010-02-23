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
package converter;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import ds.z.Room;

public class ZToCARasterSquare extends RoomRasterSquare{

    private boolean isDoor;
    private Vector<ZToCARasterSquare> partnerDoors;
    
    
    public ZToCARasterSquare(Room r, int column, int row, int raster) {
        super(r, column, row, raster);
        partnerDoors = new Vector<ZToCARasterSquare>(2);
        this.isDoor = false;
    }
    
    public void addPartner(ZToCARasterSquare partnerDoor){
        partnerDoors.add(partnerDoor);
    }
    
    public List<ZToCARasterSquare> getPartners(){
        return Collections.unmodifiableList(partnerDoors);
    }
           
    public boolean isDoor(){
        return isDoor;
    }
    
    public void setIsDoor(){
        this.isDoor = true;
    }
    
    public void clearIsDoor(){
        this.isDoor = false;
    }
    
    public void setIsDoor(boolean isDoor){
        this.isDoor = isDoor;
    }
    
}
