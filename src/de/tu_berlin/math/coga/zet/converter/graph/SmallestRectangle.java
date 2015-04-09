/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import org.zetool.math.geom.ArbitraryRectangle;
import org.zetool.math.geom.Rectangle;
import de.tu_berlin.math.coga.math.vectormath.Vector2;
import de.tu_berlin.coga.zet.model.PlanPoint;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A class that represents the smallest rectangle containing a certain room axis of the smallest rectangle are
 * directions found by a principal component analysis (PCA), thereby directions are eigenvectors of the covariance
 * matrix of all points representing the room
 * @author Marlen Schwengfelder
 */
public class SmallestRectangle implements Rectangle {

  Vector2 center, xAxis, yAxis;
  double x_extent, y_extent;
  Vector2[] axis = new Vector2[2];
  List<PlanPoint> recPoints;

  public void initialize() {
    center = new Vector2( 0, 0 );
    xAxis = new Vector2( 0, 0 );
    yAxis = new Vector2( 0, 0 );
    x_extent = 0.0;
    y_extent = 0.0;
  }

  /*
   * Computes the smallest rectangle containing all points of a given room
   * @param numPoints the number of given points
   * @param points all points representing a given room, each point is stored as a {@code Vector2}
   */
  public List<PlanPoint> computeSmallestRectangle( int numPoints, List<Vector2> points ) {
    initialize();

    //compute mean of points
    center.setX( points.get( 0 ).getX() );
    center.setY( points.get( 0 ).getY() );

    for( int i = 1; i < points.size(); i++ ) {
      center.addTo( points.get( i ) );
    }
    double inv = 1.0 / ((double)numPoints);
    center.scalarMultiplicateTo( inv );

    //Compute the covariance matrix of the points
    double sumXX = 0.0;
    double sumXY = 0.0;
    double sumYY = 0.0;

    for( int i = 0; i < numPoints; i++ ) {
      Vector2 diff = points.get( i ).sub( center );
      sumXX += (diff.getX() * diff.getX());
      sumXY += (diff.getX() * diff.getY());
      sumYY += (diff.getY() * diff.getY());
    }
    sumXX *= inv;
    sumXY *= inv;
    sumYY *= inv;

    //set up the covariance matrix of given points
    Matrix Cov = new Matrix( 2, 2 );
    Cov.set( 0, 0, sumXX );
    Cov.set( 0, 1, sumXY );
    Cov.set( 1, 0, sumXY );
    Cov.set( 1, 1, sumYY );

       //System.out.println("Matrix 1: " + Cov.get(0,0) + Cov.get(0,1)+ Cov.get(1,0) + Cov.get(1,1));
    //Compute Eigenvectors and values of covariance matrix
    EigenvalueDecomposition eigen = Cov.eig();
    Matrix eigval = eigen.getD();
    Matrix eigvec = eigen.getV();

       //System.out.println("eigenvalues 1: " + eigval.get(0,0) + eigval.get(0,1) + eigval.get(1,0) + eigval.get(1,1));
    x_extent = eigval.get( 0, 0 );
    y_extent = eigval.get( 1, 1 );
    xAxis = new Vector2( eigvec.get( 0, 0 ), eigvec.get( 0, 1 ) );
    yAxis = new Vector2( eigvec.get( 1, 0 ), eigvec.get( 1, 1 ) );

    Vector2 diff = points.get( 0 ).sub( center );
    axis[0] = xAxis;
    axis[1] = yAxis;
    Vector2 min = new Vector2( diff.dotProduct( xAxis ), diff.dotProduct( yAxis ) );
    Vector2 max = new Vector2();
    max.setX( min.getX() );
    max.setY( min.getY() );

    for( int i = 0; i < numPoints; i++ ) {
      diff = points.get( i ).sub( center );
      for( int j = 0; j < 2; j++ ) {
        double d = diff.dotProduct( axis[j] );
        if( j == 0 ) {
          if( d < min.getX() ) {
            min.setX( d );
          } else if( d > max.getX() ) {
            max.setX( d );
          }
        } else {
          if( d < min.getY() ) {
            min.setY( d );
          } else if( d > max.getY() ) {
            max.setY( d );
          }
        }
      }

    }
    Vector2 vect = axis[0].scalarMultiplicate( 0.5 * (min.getX() + max.getX()) ).add( axis[0].scalarMultiplicate( 0.5 * (min.getY() + max.getY()) ) );
    center.addTo( vect );
    x_extent = 0.5 * (max.getX() - min.getX());
    y_extent = 0.5 * (max.getY() - min.getY());

    computeRectanglePoints();
    computeNodeRectanglePoints();

    return recPoints;

  }

  public Vector2 getCenter() {
    return this.center;
  }

  /*
   * Given a center and the axis of a rectangle, method computes the 4 corners of that
   */
  private void computeRectanglePoints() {
    recPoints = new ArrayList<>( 4 );

    Vector2 extxAxis = axis[0].scalarMultiplicate( x_extent );
    Vector2 extyAxis = axis[1].scalarMultiplicate( y_extent );

    Vector2 p1 = center.sub( extxAxis ).sub( extyAxis );
    PlanPoint po1 = new PlanPoint( (int)p1.getX(), (int)p1.getY() );
    Vector2 p2 = center.add( extxAxis ).sub( extyAxis );
    PlanPoint po2 = new PlanPoint( (int)p2.getX(), (int)p2.getY() );
    Vector2 p3 = center.add( extxAxis ).add( extyAxis );
    PlanPoint po3 = new PlanPoint( (int)p3.getX(), (int)p3.getY() );
    Vector2 p4 = center.sub( extxAxis ).add( extyAxis );
    PlanPoint po4 = new PlanPoint( (int)p4.getX(), (int)p4.getY() );

    recPoints.add( po1 );
    recPoints.add( po2 );
    recPoints.add( po3 );
    recPoints.add( po4 );
  }

  org.zetool.math.geom.Point getPoint( int i ) {
    return recPoints.get( i );
  }

  /*
   * Given 4 corners of a rectangle, method computes the north east, north west, south east and
   * south west point. Thereby the north west corner is always the one with the smallest x-value
   * and the smallest y-value
   */
  public void computeNodeRectanglePoints() {
    double xmax = Double.MIN_VALUE;
    double ymax = Double.MIN_VALUE;
    double xmin = Double.MAX_VALUE;
    double ymin = Double.MAX_VALUE;
    PlanPoint miny = new PlanPoint( 0, 0 );
    PlanPoint minx = new PlanPoint( 0, 0 );
    PlanPoint maxy = new PlanPoint( 0, 0 );
    PlanPoint maxx = new PlanPoint( 0, 0 );
    int numxval = 0;
    int numyval = 0;
    //saves the different x and y-points
    Set<Double> xpoints = new HashSet<>();
    Set<Double> ypoints = new HashSet<>();

    for( PlanPoint p : recPoints ) {
      if( !(xpoints.contains( p.getX() )) ) {
        xpoints.add( p.getX() );
        numxval++;
      }
      if( !(ypoints.contains( p.getY() )) ) {
        ypoints.add( p.getY() );
        numyval++;
      }
      if( p.getX() < xmin ) {
        xmin = p.getX();
        minx = p;
      }
      if( p.getY() < ymin ) {
        ymin = p.getY();
        miny = p;
      }
      if( p.getX() > xmax ) {
        xmax = p.getX();
        maxx = p;
      }
      if( p.getY() > ymax ) {
        ymax = p.getY();
        maxy = p;
      }
    }

    recPoints = new ArrayList<>( 4 );

    if( numxval == 4 && numyval == 4 ) { //no rectangle edge is axis aligned
      recPoints.add( minx );
      recPoints.add( miny );
      recPoints.add( maxy );
      recPoints.add( maxx );
    } else { //rectangle edges are axis aligned
      recPoints.add( new PlanPoint( (int)xmin, (int)ymin ) );
      recPoints.add( new PlanPoint( (int)xmax, (int)ymin ) );
      recPoints.add( new PlanPoint( (int)xmin, (int)ymax ) );
      recPoints.add( new PlanPoint( (int)xmax, (int)ymax ) );
    }

  }

  public double width() {
    Point nw = recPoints.get( 0 );
    Point ne = recPoints.get( 1 );
    return Math.sqrt( Math.pow( nw.getX() - ne.getX(), 2 )
            + Math.pow( nw.getY() - ne.getY(), 2 ) );
  }

  public double height() {
    Point nw = recPoints.get( 0 );
    Point sw = recPoints.get( 2 );
    return Math.sqrt( Math.pow( nw.getX() - sw.getX(), 2 ) + Math.pow( nw.getY() - sw.getY(), 2 ) );
  }

  @Override
  public org.zetool.math.geom.Point getCoordinate( CornerCoordinates c ) {
    return getPoint( c.ordinal() );
  }

  @Override
  public Rectangle rotate( double angle, org.zetool.math.geom.Point rotation ) {
    return ArbitraryRectangle.rotate( this, angle, rotation );
  }

  @Override
  public Iterator<org.zetool.math.geom.Point> iterator() {
    throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
  }
}
