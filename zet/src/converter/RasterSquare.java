/*
 * RasterSquare.java
 */

package converter;

import ds.z.Edge;
import ds.z.PlanPoint;
import ds.z.PlanPolygon;
import java.util.ArrayList;

/**
 * A <code>RasterSquare</code> is an element of a {@link RoomRaster} of a {@link ds.z.PlanPolygon},
 * especially for a {@link ds.z.Room}.
 * The polygon is divided in a raster of squares. Each square can intersect the polyonom, or not and
 * has a position in the global coordinate system of the rasterized polygon.
 * <p>Basically a square consists of a <code>PlanPolygon</code> whose coordinates describe a square. It is unique
 * defined by the coordinates of the upper left edge and its height and width, which depends from the rasterization
 * grid.</p>
 * @author Jan-Philipp Kappmeier
 */
public class RasterSquare {

  /**
   * The <code>FieldIntersectType</code> enumeration stores the three different
	 * intersection types that an <code>RasterSquare</code> element of a rasterization
	 * can be.
   * @author Jan-Philipp Kappmeier
   */
  public enum FieldIntersectType {
    /** The {@link RasterSquare} is completely inside the {@link ds.z.PlanPolygon}. */
    Inside,
    /** The {@link RasterSquare} intersects the {@link ds.z.PlanPolygon} but is not contained completely in it. */
    Intersects,
    /** The {@link RasterSquare} is completely outside the {@link ds.z.PlanPolygon}. */
    Outside;
  }  
  
  /** The {@link ds.z.PlanPolygon} describing the coordinates of this square. */
  private PlanPolygon square;
  /** The {@link ds.z.PlanPolygon} that is rasterized. */
  private PlanPolygon p;

  /** Describes the the squares intersection status. */
  private FieldIntersectType intersectType;
  
  /** The column-index of the square in the raster array created during a rasterization of a polygon. */
  private int column;
  /** The row-index of the square in the raster array created during a rasterization of a polygon. */
  private int row;
  /** the <code>x</code>-coordiante of the upper left corner in the global coordinate system. */
  private int x;
  /** the <code>y</code>-coordiante of the upper left corner in the global coordinate system */
  private int y;
  
  /** The size of the raster used in rasterization process. Defines width and height of the square. */
  private int raster;
  
  private int stairPotential;
  
  /**
   * Creates a new instance of <code>RasterSquare</code> belonging to a {@link ds.z.PlanPolygon}.
   * @param p the polygon to which this square belongs
   * @param column the column-index of this square in the array of raster-squares, starting with 0
   * @param row the row-index of this square in the array of raster-squares, starting with 0
   * @param raster the grid size of the rasterization defines the width and height of the square
   */
  public RasterSquare(PlanPolygon p, int column, int row, int raster) {

	callAtConstructorStart();

	this.column = column;
    this.row = row;
    x = p.getxOffset() + column * raster;
    y = p.getyOffset() + row * raster;
    this.raster = raster;
    this.p = p;
    
    ArrayList<PlanPoint> squareList = new ArrayList<PlanPoint>();
    squareList.add( new PlanPoint( x, y ) );
    squareList.add( new PlanPoint( x + raster, y ) );
    squareList.add( new PlanPoint( x + raster, y + raster ) );
    squareList.add( new PlanPoint( x, y + raster ) );

    square = new PlanPolygon<Edge>( Edge.class );
    square.add( squareList );    

    stairPotential = -1;
    
    check();
  }
  
	/**
	 * Is called as first operation of the constructor.
	 */
	protected void callAtConstructorStart(){
  }
  
  /**
   * Checks whether this square intersects a {@link ds.z.PlanPolygon} or not. The
	 * status is stored and can be accessed via {@link #getIntersectType()}. The
	 * polygon has to be set in the constructor.
   */
  protected void check( ) {
    if( !p.intersects( square ) ) {
      // No intersection
      intersectType = FieldIntersectType.Outside;
    } else if( p.contains( square) ) {
      // Is inside
      intersectType = FieldIntersectType.Inside;
    } else {
      // Intersects only
      intersectType = FieldIntersectType.Intersects;
    }
  }

  /**
   * Returns the column-index of the square in the raster array created during a rasterization of a polygon.
   * @return The column-index of the square in the raster array created during a rasterization of a polygon.
   */
  public int getColumn() {
    return column;
  }
 
  /**
   * Returns the {@link FieldIntersectType} of the square
   * @return the intersect type of the square
   */
   public FieldIntersectType getIntersectType() {
    return intersectType;
  }
 
  protected PlanPolygon getPolygon() {
    return p;
  }
   
  /**
   * Returns the grid size of the raster
   * @return the grid size of the raster
   */
   public int getRaster() {
     return raster;
   }
  
  /**
   * Returns the row-index of the square in the raster array created during a rasterization of a polygon.
   * @return The row-index of the square in the raster array created during a rasterization of a polygon.
   */
  public int getRow(){
    return row;
  } 
  
  public PlanPolygon getSquare() {
    return square;
  }
  
  public void setStairPotential(int p){
      this.stairPotential = p;
  }
  
  public int getStairPotential(){
      return stairPotential;
  }
  
  
  /**
   * Returns the <code>x</code>-coordinate of the upper left corner.
   * @return the <code>x</code>-coordinate of the upper left corner
   */
   public int getX() { 
    return x;
   }
 
  /**
   * Returns the <code>y</code>-coordinate of the upper left corner
   * @return the <code>y</code>-coordinate of the upper left corner
   */
   public int getY() {
     return y;
   }
   
   /**
    * Returns the <code>x</code>-coordinate of the upper left corner
    * relative to the position of the sourrounding room.
    * @return the <code>x</code>-coordinate of the upper left corner
    */  
   public int getRelativeX(){
       return x - p.getxOffset();
   }

   /**
    * Returns the <code>y</code>-coordinate of the upper left corner
    * relative to the position of the sourrounding room.
    * @return the <code>y</code>-coordinate of the upper left corner
    */  
   public int getRelativeY(){
       return y - p.getyOffset();
   }   
   
   /**
    * Returns a String containing the coordinates of this raster square.
    * @return a String containing the coordinates of this raster square.
    */
	@Override
   public String toString(){
	   String result = "["+x+y+"]";
	   return result;
   }
   
}
