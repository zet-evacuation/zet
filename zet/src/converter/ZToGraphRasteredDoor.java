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
