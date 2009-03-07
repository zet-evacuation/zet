/*
 * ZToGraphRoomRaster.java
 * 
 */
package converter;

import ds.graph.Node;
import ds.z.Room;

/**
 * The class <code>ZToGraphRoomRaster</code> represents a rastered room.
 * It extends the class <code>RoomRaster</code> by specifying the generic type
 * of raster squares to <code>ZToGraphRasterSquare</code>.
 *
 */
public class ZToGraphRoomRaster extends RoomRaster<ZToGraphRasterSquare> {

	/**
	 * Creates a new <code>ZToGraphRoomRaster</code> object connected with the
	 * room <code>room</code>.
	 * @param room the room connected to the new object.
	 */
	public ZToGraphRoomRaster(Room room) {
		super(ZToGraphRasterSquare.class, room);
	}

	@Override
	/**
	 * Returns the <code>ZToGraphRasterSquare</code> at position <code>(x,y)</code>.
	 * @return the <code>ZToGraphRasterSquare</code> at position <code>(x,y)</code>.
	 */
	public ZToGraphRasterSquare getSquare(int x, int y) {
		return super.getSquare(x, y);
	}
	
	/**
	 * Returns the <code>ZToGraphRasterSquare</code> that includes the point (x,y)
	 * (global coordinates).
	 * @return  the <code>ZToGraphRasterSquare</code> that includes the point (x,y)
	 * (global coordinates).
	 */
	public ZToGraphRasterSquare getSquareWithGlobalCoordinates(int x, int y){
		int localX = x - this.getXOffset();
		int localY = y - this.getYOffset();
		int rasterX = (int) Math.floor(((double)localX / (double)getRaster()));
		int rasterY = (int) Math.floor(((double)localY / (double)getRaster()));
		return getSquare(rasterX,rasterY);
	}

	@Override
    /**
     * Returns the number of columns of the <code>RoomRaster</code>.
     * @return the number of columns of the <code>RoomRaster</code>.
     */
	public int getColumnCount() {
		return super.getColumnCount();
	}

	@Override
    /**
     * Returns the number of rows of the <code>RoomRaster</code>.
     * @return the number of rows of the <code>RoomRaster</code>.
     */
	public int getRowCount() {
		return super.getRowCount();
	}
	
    /**
     * Sets the mark flag of all squares in this rastered room to <code>false</code>.
     */
	public void unmarkAllSquares(){
		for (int i = 0; i < getColumnCount(); i++){
			for (int j = 0; j < getRowCount(); j++){
				getSquare(i,j).unmark();
			}
		}
	}

	@Override
    /**
     * Returns a String containing a description of the underlying <code>RoomRaster</code>.
     * @return a String containing a description of the underlying <code>RoomRaster</code>.
     */
	public String toString() {
			  String result = "";
			  for (int i = 0; i < rasterSquares[0].length+2; i++)		  
				  result += "-";
			  result += "\n";
			  for (int j = 0; j < rasterSquares[0].length; j++){
				  result += "|";
				  for (int i = 0; i < rasterSquares.length; i++){
					  ZToGraphRasterSquare square = getSquare(i,j);
					  if (square.accessible()){
						  Node node = square.getNode();
						  if (node != null){
							  if (node.id() < 10)
								  result += " ";
							  result+= node.toString() + " ";
						  }
						  else 
							  result+= "  ";
					  }
					  else
						  result+=" X ";
				  }
					  result += "|\n";
			  }
			  for (int i = 0; i < rasterSquares.length+2; i++)		  
				  result += "-";
			  result += "\n";
			  return result;
	}
	
	public String superToString(){
		String result = "";
		for( int i = 0; i < rasterSquares[0].length + 2; i++ ) {
			result += "-";
		}
		result += "\n";
		for( int j = 0; j < rasterSquares[0].length; j++ ) {
			result += "|";
			for( int i = 0; i < rasterSquares.length; i++ ) {
				if( getSquare( i, j ).accessible() ) {
					if (getSquare(i, j).isExit())
						result += "e";
					else {
						if (getSquare(i, j).getSave())
							result += "s";
						else {
							if (getSquare(i, j).isSource())
								result += "a";
							else {
								if (getSquare(i, j).getSpeedFactor() < 1.0) {
									result += "d";
								} else {
									result += " ";
								}
							}
						}
					}
				} else {
					result += "X";
				}
			}
			result += "|\n";
		}
		for( int i = 0; i < rasterSquares.length + 2; i++ ) {
			result += "-";
		}
		result += "\n";
		return result;
	}

}
