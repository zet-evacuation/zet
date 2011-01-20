/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * PlanPoint.java
 * Created on 26. November 2007, 21:32
 */
package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import io.z.PlanPointConverter;
import io.z.XMLConverter;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import zet.util.ConversionTools;

/**
 * {@code PlanPoint} represents a point with integer coordinates. It represents a position
 * in a {@link BuildingPlan} to a accuracy to millimeters. It is possible to get and setLocation coordinates
 * as float values, rounded to three decimals, representing the current value in meters.
 *
 * PlanPoints are used to represent polygon nodes and to implement the polygon edge
 * list, planpoints know their incident polygon edges (always 0,1 or 2).
 *
 * @author Jan-Philipp Kappmeier, Timon Kelter
 */
@XStreamAlias( "planPoint" )
@XMLConverter( PlanPointConverter.class )
public class PlanPoint extends Point {

//	@XStreamOmitField()
//	private transient ArrayList<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
	/** The next incident edge. */
	@XStreamOmitField() // - is setLocation in Compact converter
	private Edge nextEdge;
	/** The previous incident edge. */
	@XStreamOmitField() // - is setLocation in Compact converter
	private Edge previousEdge;

	/**
	 * Creates a new instance of {@code PlanPoint} with default coordinates.
	 */
	public PlanPoint() {
		super();
	}

	/**
	 * Creates a new instance of {@code PlanPoint} with initialized coordinates.
	 * @param p the point position as an arbitrary point object
	 */
	public PlanPoint( Point p ) {
		super();
		setLocation( p );
	}

	/**
	 * Creates a new instance of {@code PlanPoint} with initialized coordinates. These values
	 * are are assumed to have accurancy of millimeter.
	 * @param x the {@code x}-coordinate of the point
	 * @param y the {@code y}-coordinate of the point
	 * @see #setLocation( double, double )
	 */
	public PlanPoint( int x, int y ) {
		super();
		setLocation( x, y );
	}

	public PlanPoint( PlanPoint p ) {
		super();
		setLocation( p.getX(), p.getY() );
	}

	/**
	 * Creates a new instance of {@code PlanPoint} with initialized coordinates. These values
	 * are assumed to be meters and tare transformed to meter.
	 * @param x the {@code x}-coordinate of the point
	 * @param y the {@code y}-coordinate of the point
	 * @see #setLocationMeter( double, double )
	 * @see util.ConversionTools#floatToInt(double)
	 */
	public PlanPoint( double x, double y ) {
		super();
		setLocationMeter( x, y );
	}

	/**
	 * Creates a new instance of {@code PlanPoint} with initialized coordinates. The coordinates
	 * can be specified as integer or double values, defining values in millimeter and meter.
	 * @param x the {@code x}-coordinate of the point
	 * @param y the {@code y}-coordinate of the point
	 * @param meter specifies if the coordinates should be millimeter or meter
	 * @see #setLocation( double, double )
	 * @see #setLocationMeter( double, double )
	 */
	public PlanPoint( double x, double y, boolean meter ) {
		super();
		if( meter ) {
			setLocationMeter( x, y );
		} else {
			setLocation( x, y );
		}
	}
//
//	void resetListener() {
//		changeListeners = new ArrayList<ChangeListener>();
//	}
//
//	/** {@inheritDoc}
//	 * @param e the event
//	 */
//	@Override
//	public void throwChangeEvent( ChangeEvent e ) {
//		// Workaround: Notify only the listeners who are registered at the time when this method starts
//		// This point may be thrown away when the resulting edge must be rastered, and then the list
//		// "changeListeners" will be altered during "c.stateChanged (e)", which produces exceptions.
//		ChangeListener[] listenerCopy = changeListeners.toArray( new ChangeListener[changeListeners.size()] );
//
//
//		for( ChangeListener c : listenerCopy ) {
//			c.stateChanged( e );
//		}
//	}
//
//	/** {@inheritDoc}
//	 * @param c the listener
//	 */
//	@Override
//	public void addChangeListener( ChangeListener c ) {
//		if( !changeListeners.contains( c ) ) {
//			changeListeners.add( c );
//		}
//	}
//
//	/** {@inheritDoc}
//	 * @param c the listener
//	 */
//	@Override
//	public void removeChangeListener( ChangeListener c ) {
//		changeListeners.remove( c );
//	}

	/**
	 * Checks if an object is equal to this instance of {@code PlanPoint}. A point
	 * can only be equal to variables of the same type. In that case two points are
	 * are considered equal, if and only if their {@code x} and {@code y}
	 * coordinates are equal.
	 * @param obj the {@code Object} that is compared to this {@code PlanPoint}
	 * @return true if obj is of {@code PlanPoint} type and both coordinates are equal.
	 */
	@Override
	public boolean equals( Object obj ) {
		if( obj instanceof PlanPoint ) {
			PlanPoint point = (PlanPoint) obj;
			return point.getX() == this.getX() && point.getY() == this.getY();
		} else {
			return false;
		}
	}

	/** This should ONLY be called from Edge.setPoints (). Unfortunately Java
	 * does not provide a mechanism to ensure this, so just don't use this method.
	 * 
	 * @param nextEdge the next incident (= outgoing) edge of this point. */
	protected void setNextEdge( Edge nextEdge ) {
		this.nextEdge = nextEdge;
	}

	/** This should ONLY be called from Edge.setPoints (). Unfortunately Java
	 * does not provide a mechanism to ensure this, so just don't use this method.
	 * 
	 * @param previousEdge the previous incident (= incoming) edge of this point. */
	protected void setPreviousEdge( Edge previousEdge ) {
		this.previousEdge = previousEdge;
	}

	/** @return The next incident (= outgoing) incident edge of this point. */
	public Edge getNextEdge() {
		return nextEdge;
	}

	/** @return The previous incident (= incoming) incident edge of this point. */
	public Edge getPreviousEdge() {
		return previousEdge;
	}

	/**
	 * Returns the second edge that is incident to this point
	 * @param e An edge that must be incident to this point
	 * @return The second edge that is incident to this point. 
	 * @throws IllegalArgumentException Is thrown when {@code e} is not
	 * incident to the plan point.
	 */
	public Edge getOtherEdge( Edge e ) throws IllegalArgumentException {
		// Do not use Edge.equals here. The orientation of the edge is not taken into 
		// account in Edge.equals, but here is plays an important role, especially when
		// we have a point p1 who has incident edges of the form (p2,p1),(p1,p2).
		if( absoluteEqual( nextEdge, e ) ) {
			return previousEdge;
		} else if( absoluteEqual( previousEdge, e ) ) {
			return nextEdge;
		} else {
			throw new IllegalArgumentException( ZLocalization.getSingleton().getString( "ds.z.NotIncidentException" ) );
		}
	}

	/** Private helper method that compares two edges with respect to their orientation. */
	private static boolean absoluteEqual( Edge e1, Edge e2 ) {
		return ((e1.getSource() != null) ? e1.getSource().equals( e2.getSource() ) : e2.getSource() == null) &&
						((e1.getTarget() != null) ? e1.getTarget().equals( e2.getTarget() ) : e2.getTarget() == null);
	}

	/**
	 * Returns the value of this point als float value. Due to the limitations of
	 * integer it is possible to setLocation coordinates between about -2147483 and
	 * 2147483 meters.
	 * @return integer transformed {@code x}-coordinate
	 */
	public double getXMeter() {
		return ConversionTools.roundScale3( getX() / 1000.0 );
	}

	public int getXInt() {
		return (int)x;
	}

	/**
	 * Returns the value of this point als float value. Due to the limitations of
	 * integer it is possible to setLocation coordinates between about -2147483 and
	 * 2147483 meters.
	 * @return integer transformed {@code y}-coordinate
	 */
	public double getYMeter() {
		return ConversionTools.roundScale3( getY() / 1000.0 );
	}

	public int getYInt() {
		return (int)y;
	}

	// q is the point in common with both edges
	public static int orientation( PlanPoint p, PlanPoint q, PlanPoint r ) {
		double u1 = p.getX() - q.getX();
		double u2 = p.getY() - q.getY();
		double v1 = r.getX() - q.getX();
		double v2 = r.getY() - q.getY();
		double det = v1 * u2 - v2 * u1;
		return (int) Math.signum( det );
	}

	/**
	 * Sets the new location of this point. The coordinates are (even if they are double values)
	 * assumed to be integers refering to millimeter positions. Thus the real parts of the
	 * values are cutted off.
	 * @param x the <cide>x}-coordinate of the point
	 * @param y the {@code y}-coordinate of the point
	 */
	@Override
	public void setLocation( double x, double y ) {
		// Do not remove this query for a "real" change, it saves running time
		// and is needed in RoomEdge.stateChanged () (indirectly)
		if( this.x != x || this.y != y ) {
			// Values are already rounded in superimplementation of setLocation
			// -> No need to round them here
			super.setLocation( x, y );
//			throwChangeEvent( new ChangeEvent( this ) );
		}
	}

	/**
	 * Sets the new location of this point. The coordinates are (even if they are double values)
	 * assumed to be integers refering to millimeter positions. Thus the real parts of the
	 * values are cutted off.
	 */
	@Override
	public void setLocation( Point2D p ) {
		setLocation( p.getX(), p.getY() );
	}

	/**
	 * @param x the {@code x}-coordinate of the point
	 * @param y the {@code y}-coordinate of the point
	 */
	public void setLocationMeter( double x, double y ) {
		setLocation( ConversionTools.floatToInt( x ), ConversionTools.floatToInt( y ) );
	}

	public void setLocationMeter( Point2D p ) {
		setLocationMeter( p.getX(), p.getY() );
	}

	@Override
	public void setLocation( Point point ) {
		setLocation( point.x, point.y );
	}

	@Override
	public void setLocation( int x, int y ) {
		setLocation( (double) x, (double) y );
	}

	@Override
	/** {@inheritDoc}
	 */
	public void translate( int x, int y ) {
		super.translate( x, y );
//		throwChangeEvent( new ChangeEvent( this ) );
	}

	/**
	 * Creates a copy of a list of {@code PlanPoint} objects. The copies are
	 * new instances.
	 * @param original the original list of points
	 * @return the new list containing the copies
	 */
	public static List<PlanPoint> pointCopy( List<PlanPoint> original ) {
		final List<PlanPoint> points = new ArrayList<PlanPoint>();
		for( PlanPoint p : original )
			points.add( new PlanPoint( p ) );
		return points;
	}

	/**
	 * Returns a string representation of the {@code PlanPoint} that
	 * textually represents the point.
	 * <p>A point is represented as a tupel of its coordinates like this:</p>
	 * <blockquote><pre>
	 * (x,y)
	 * </pre></blockquote>
	 * @return a string representation of the point
	 */
	@Override
	public String toString() {
		return "(" + Integer.toString( x ) + "," + Integer.toString( y ) + ")";
	}
}
