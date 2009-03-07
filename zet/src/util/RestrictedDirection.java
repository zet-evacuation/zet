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
