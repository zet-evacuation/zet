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
