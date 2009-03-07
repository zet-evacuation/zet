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
