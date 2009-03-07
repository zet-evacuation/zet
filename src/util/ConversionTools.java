/*
 * ConversionTools.java
 * Created on 3. Dezember 2007, 23:34
 */
package util;

import java.util.LinkedList;
import java.util.List;

import converter.RoomRaster;
import converter.RoomRasterSquare;

/**
 * Static class that provides some functions calculating from discrete millimeter
 * values to floating point meter values and vice versa.
 * @author Jan-Philipp Kappmeier
 */
public final class ConversionTools {

  /** No instantiating of <code>ConversationTools</code> possible. */
  private ConversionTools() {
  }

  /**
   * <p>Translates a specified integer value to a double value. The integer 
   * value is assumed to have the millimeter unit and the returned value has
   * the unit meter.</p>
	 * <p>The resulting value is rounded to three decimal places, which is the
	 * default accurancy in <code>z</code>-format.</p>
   * @param mm the millimeter value
   * @return the meter value for the millimeter
   */
  public static double toMeter( int mm ) {
    return roundScale3( mm  / 1000.0 );
  }
  
	/**
	 * Converts a float value to an int value, using three decimal places. The
	 * floating point values are rounded fair using {@link roundScale3( double )}.
	 * This represents the accurancy used by the <code>z</code>-format.
	 * @param x
	 * @return
	 */
  public static int floatToInt( double x ) {
    return (int)Math.round( roundScale3( x ) * 1000.0f );
  }
	
	

  /**
   * Rounds a given Double to three valid decimal places. The used rounding-method
   * is {@link Math#rint( double )}, which rounds fair. That means, the functions
   * rounds normal in most cases. If the last rounding-significant place is 5, it
   * rounds up if the next number is odd and it rounds down if the next number is
   * even.
   * @param d the value to be rounded
   * @return a fair rounded value to three valid decimal places
   */
  public static double roundScale3( double d ) {
    return Math.rint( d * 1000 ) / 1000.0f;
  }
  
  private static <T extends RoomRasterSquare> void addIfExistent(LinkedList<T> adjacentSquares, RoomRaster<T> raster, int a, int b){
	  if (raster.isValid(a,b) && raster.getSquare(a, b).accessible()){
		  T square = raster.getSquare(a,b);
          adjacentSquares.add(square);
	  }
			  
  }
  
  /**
   * Gets all squares that are adjacent to an edge that lies
   * on the raster (i.e. for e={(x1,y1), (x2,y2)}, x1==x2 or 
   * y1==y2 holds). The squares are guaranteed to be in the 
   * following order:
   * Let e be an edge from (x1, y1) to (y1, y2) that lies on 
   * the raster. For simplicity, assume that y1==y2 and that
   * x1 &lt; x2. Otherwise, relabel the coordinate system and/or
   * relabel x1 and x2.  
   * Then the squares that lie above the edge (i.e. those that
   * have a y-coordinate &gt; y1) will be at the beginning of
   * the list in increasing order with respect to their 
   * x-coordinate (i.e. from left to right). They are 
   * followed by the squares below the edge (i.e. those that
   * have a y-coordinate &lt; y1) in increasing order (i.e. also
   * from left to right). 
   * 
   * @param edge An edge of a room
   * @return A list of all squares adjacent to the edge.
   */
  public static <T extends RoomRasterSquare> List<T> getSquaresAlongEdge(ds.z.Edge edge, RoomRaster<T> raster){
      LinkedList<T> adjacentSquares = new LinkedList<T>();
      
      adjacentSquares.addAll(getSquaresAboveEdge(edge, raster));
      adjacentSquares.addAll(getSquaresBelowEdge(edge, raster));
      adjacentSquares.addAll(getSquaresLeftOfEdge(edge, raster));
      adjacentSquares.addAll(getSquaresRightOfEdge(edge, raster));
      
      return adjacentSquares;
  }
  
  /**
   * Given a raster and an edge on the raster, this method returns all squares 
   * in the raster that have at least two common points with the edge and whose 
   * upper left corner has a y-coordinate that is strictly greater than the
   * maximum of the y-coordinates of the endpoints of the edge.  
   * 
   * @param <T> A <code>RoomRasterSquare</code>-Type
   * @param edge An edge that lies on the <code>raster</code>
   * @param raster a rastering of a room. 
   * @return All adjacent raster squares above this edge. If the edge is parallel to
   * the y-axis, the returned list is empty.
   */
  public static <T extends RoomRasterSquare> List<T> getSquaresAboveEdge(ds.z.Edge edge, RoomRaster<T> raster){
      int rasterX1 = ConversionTools.polyCoordToRasterCoord(edge.boundLeft(), raster.getXOffset(), raster);
      int rasterX2 = ConversionTools.polyCoordToRasterCoord(edge.boundRight(), raster.getXOffset(), raster);
      
      int rasterY1 = ConversionTools.polyCoordToRasterCoord(edge.boundUpper(), raster.getYOffset(), raster);
      int rasterY2 = ConversionTools.polyCoordToRasterCoord(edge.boundLower(), raster.getYOffset(), raster);
      
      LinkedList<T> adjacentSquares = new LinkedList<T>();
      
      if(rasterX1 == rasterX2){
          return adjacentSquares;
      }
      
      if(rasterY1 != rasterY2){
          throw new IllegalArgumentException("The edge does not lie on the raster!");
      }      
      
      for(int x=rasterX1; x < rasterX2; x++){
          addIfExistent(adjacentSquares, raster, x, rasterY1-1);
      }
      
      return adjacentSquares;
  }
  
  /**
   * Given a raster and an edge on the raster, this method returns all squares 
   * in the raster that have at least two common points with the edge and whose 
   * upper left corner has a y-coordinate that is equal to the
   * maximum of the y-coordinates of the endpoints of the edge.  
   * 
   * @param <T> A <code>RoomRasterSquare</code>-Type
   * @param edge An edge that lies on the <code>raster</code>
   * @param raster a rastering of a room. 
   * @return All adjacent raster squares below this edge. If the edge is parallel to
   * the y-axis, the returned list is empty.
   */
  public static <T extends RoomRasterSquare> List<T> getSquaresBelowEdge(ds.z.Edge edge, RoomRaster<T> raster){
      int rasterX1 = ConversionTools.polyCoordToRasterCoord(edge.boundLeft(), raster.getXOffset(), raster);
      int rasterX2 = ConversionTools.polyCoordToRasterCoord(edge.boundRight(), raster.getXOffset(), raster);
      
      int rasterY1 = ConversionTools.polyCoordToRasterCoord(edge.boundUpper(), raster.getYOffset(), raster);
      int rasterY2 = ConversionTools.polyCoordToRasterCoord(edge.boundLower(), raster.getYOffset(), raster);
      
      LinkedList<T> adjacentSquares = new LinkedList<T>();
      
      if(rasterX1 == rasterX2){
          return adjacentSquares;
      }
      
      if(rasterY1 != rasterY2){
          throw new IllegalArgumentException("The edge does not lie on the raster!");
      }      
      
      for(int x=rasterX1; x < rasterX2; x++){
          addIfExistent(adjacentSquares, raster, x, rasterY1);
      }
      
      return adjacentSquares;
  }  
  
  /**
   * Given a raster and an edge on the raster, this method returns all squares 
   * in the raster that have at least two common points with the edge and whose 
   * upper left corner has an x-coordinate that is strictly greater than the
   * maximum of the x-coordinates of the endpoints of the edge.  
   * 
   * @param <T> A <code>RoomRasterSquare</code>-Type
   * @param edge An edge that lies on the <code>raster</code>
   * @param raster a rastering of a room. 
   * @return All adjacent raster squares left of this edge. If the edge is parallel to
   * the x-axis, the returned list is empty.
   */
  public static <T extends RoomRasterSquare> List<T> getSquaresLeftOfEdge(ds.z.Edge edge, RoomRaster<T> raster){
      int rasterX1 = ConversionTools.polyCoordToRasterCoord(edge.boundLeft(), raster.getXOffset(), raster);
      int rasterX2 = ConversionTools.polyCoordToRasterCoord(edge.boundRight(), raster.getXOffset(), raster);
      
      int rasterY1 = ConversionTools.polyCoordToRasterCoord(edge.boundUpper(), raster.getYOffset(), raster);
      int rasterY2 = ConversionTools.polyCoordToRasterCoord(edge.boundLower(), raster.getYOffset(), raster);
      
      LinkedList<T> adjacentSquares = new LinkedList<T>();
      
      if(rasterY1 == rasterY2){
          return adjacentSquares;
      }
      
      if(rasterX1 != rasterX2){
          throw new IllegalArgumentException("The edge does not lie on the raster!");
      }      
      
      for(int y=rasterY1; y < rasterY2; y++){
          addIfExistent(adjacentSquares, raster, rasterX1 - 1, y);
      }
      
      return adjacentSquares;
  }  
  
  /**
   * Given a raster and an edge on the raster, this method returns all squares 
   * in the raster that have at least two common points with the edge and whose 
   * upper left corner has an x-coordinate that is equal to the
   * maximum of the x-coordinates of the endpoints of the edge.  
   * 
   * @param <T> A <code>RoomRasterSquare</code>-Type
   * @param edge An edge that lies on the <code>raster</code>
   * @param raster a rastering of a room. 
   * @return All adjacent raster squares right of this edge. If the edge is parallel to
   * the x-axis, the returned list is empty.
   */
  public static <T extends RoomRasterSquare> List<T> getSquaresRightOfEdge(ds.z.Edge edge, RoomRaster<T> raster){
      int rasterX1 = ConversionTools.polyCoordToRasterCoord(edge.boundLeft(), raster.getXOffset(), raster);
      int rasterX2 = ConversionTools.polyCoordToRasterCoord(edge.boundRight(), raster.getXOffset(), raster);
      
      int rasterY1 = ConversionTools.polyCoordToRasterCoord(edge.boundUpper(), raster.getYOffset(), raster);
      int rasterY2 = ConversionTools.polyCoordToRasterCoord(edge.boundLower(), raster.getYOffset(), raster);
      
      LinkedList<T> adjacentSquares = new LinkedList<T>();
      
      if(rasterY1 == rasterY2){
          return adjacentSquares;
      }
      
      if(rasterX1 != rasterX2){
          throw new IllegalArgumentException("The edge does not lie on the raster!");
      }      
      
      for(int y=rasterY1; y < rasterY2; y++){
          addIfExistent(adjacentSquares, raster, rasterX1, y);
      }
      
      return adjacentSquares;
  }  
  
  /**
   * Converts coordinates of the z-format in millimeters to coordinates on
   * the raster of a room.
   * @param polyCoord A coordinate, given in millimeters
   * @param offset The offset of a room 
	 * @param raster 
	 * @return The coordinate of the cell that contains the given coordinate.
   * If you supply an x-coordinate, you will get the x-coordinate of the
   * cell; if you supply a y-coordinate, you will get the y-coordinate
   * of the cell.
   */
  public static int polyCoordToRasterCoord(int polyCoord, int offset, RoomRaster<?> raster){
      polyCoord -= offset;        
      return (int)(Math.floor(polyCoord/(double)raster.getRaster()));   
  }
}
