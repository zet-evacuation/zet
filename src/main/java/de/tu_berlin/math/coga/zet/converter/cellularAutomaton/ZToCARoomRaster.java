/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.tu_berlin.math.coga.zet.converter.cellularAutomaton;

import de.tu_berlin.math.coga.zet.converter.RoomRaster;
import de.zet_evakuierung.model.Room;

/**
 * @author Daniel R. Schmidt
 *
 */
public class ZToCARoomRaster extends RoomRaster<ZToCARasterSquare>{

    /**
     * @param r
     */
    public ZToCARoomRaster(Room r) {
        super(ZToCARasterSquare.class, r);
    }
    
    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public void rasterize(){
        super.rasterize();
//        ds.z.Room room = this.getRoom();
//        for(ds.z.Edge edge : room.getEdges()){
//            RoomEdge rEdge = (RoomEdge)edge;
//            if((rEdge).isPassable()){
//                List<ZToCARasterSquare> squares = getSquaresAlongEdge(rEdge);
//                RoomEdge partnerEdge;
//                if(rEdge.getRoom1() == room){
//                    partnerEdge = (RoomEdge)rEdge.getRoom2().getEdge(rEdge);
//                } else {
//                    partnerEdge = (RoomEdge)rEdge.getRoom1().getEdge(rEdge);
//                }
//                
//                if(partnerEdge == null){
//                    throw new RuntimeException("Inconsistency found: There is a passible edge that does not lie in two rooms!");
//                }
//                
//                List<ZToCARasterSquare> partnerSquares = getSquaresAlongEdge(partnerEdge);
//                
//                Iterator<ZToCARasterSquare> myIt = squares.iterator();
//                Iterator<ZToCARasterSquare> partnerIt = partnerSquares.iterator();
//                
//                while(myIt.hasNext()){
//                    ZToCARasterSquare nextSquare = myIt.next();
//                    if(nextSquare != null){
//                        nextSquare.setIsDoor();
//                        nextSquare.addPartner(partnerIt.next());
//                    }
//                }
//            }
//        }
    }
    
    
}