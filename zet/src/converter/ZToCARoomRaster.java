/*
 * ZToCARoomRaster.java
 *
 */
package converter;

import ds.z.Room;

/**
 * @author Daniel Pluempe
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