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
 * BuildingPlan.java
 * Created on 26. November 2007, 21:32
 */
package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import ds.z.exception.AreaNotInsideException;
import ds.z.exception.PolygonNotClosedException;
import ds.z.exception.RoomIntersectException;
import ds.z.exception.RoomIntersectException.RoomPair;
import ds.z.exception.TooManyPeopleException;
import io.z.BuildingPlanConverter;
import io.z.XMLConverter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import batch.tasks.AlgorithmTask;

/**
 * The <code>BuildingPlan</code> represents a complete building plan, consisting of
 * some floors which contains rooms themselves.
 * <p> Each plan is supposted to have at least two floors. One floor containing the rooms of
 * the building and another one which is the special outside floor. This outside
 * floor is used to automatically create rooms containing an {@link EvacuationArea}
 * to rescue people. </p>
 */
@XStreamAlias("buildingPlan")
@XMLConverter(BuildingPlanConverter.class)
public class BuildingPlan implements Serializable, Iterable<Floor> {
	/** The change listeners of the plan. */
//	@XStreamOmitField()
//	private transient ArrayList<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
	/** A list of all floors of the plan. */
	@XStreamImplicit()
	private ArrayList<Floor> floors;
	/** Indicates, if the BuildingPlan is rasterized, or at least should be. */
	private boolean rasterized;
	/** Static variable that stores the default-value for the rastersize in meter. */
	public static double rasterSize = 0.4;
  /** Transformation matrix that flips vertically (mirrors at the {@code x}-axis. */
	public static final int[][] flipXAxis = {{1, 0}, {0, -1}};
  /** Transformation matrix that flips horizontally (mirrors at the {@code y}-axis. */
	public static final int[][] flipYAxis = {{-1, 0}, {0, 1}};
  /** Transformation matrix that mirrors at the main diagonal. */
	public static final int[][] flipMainDiagonal = {{0, 1}, {1, 0}};
  /** Transformation matrix that does nothing. */
	public static final int[][] identity = {{1, 0}, {0, 1}};

	/**
	 * Creates a new instance of <code>BuildingPlan</code> that is not rasetrized
	 * by default and contains the {@link ds.z.DefaultEvacuationFloor}.
	 */
	public BuildingPlan() {
		floors = new ArrayList<Floor>( 10 );
		rasterized = false;
		// Add a new default-floor and sets its raster to 400 millimeter
		DefaultEvacuationFloor def = new DefaultEvacuationFloor();
		def.setRasterSize( zet.util.ConversionTools.floatToInt( rasterSize ) );
		addFloor( def );
	}

	/**
	 * Returns the id of the selected floor.
	 * @param floor the selected flor whose index should be computed
	 * @return the id of the selected floor
	 */
	public int getFloorID( Floor floor ) {
		return (floors.indexOf( floor ));
	}

//	/**
//	 * {@inheritDoc}
//	 * @param e the change event
//	 */
//	public void throwChangeEvent( ChangeEvent e ) {
//		for( ChangeListener c : changeListeners )
//			c.stateChanged( e );
//	}
//
//	/**
//	 * {@inheritDoc}
//	 * @param c the change listener
//	 */
//	public void addChangeListener( ChangeListener c ) {
//		if( !changeListeners.contains( c ) )
//			changeListeners.defineByPoints( c );
//	}
//
//	/**
//	 * {@inheritDoc}
//	 * @param c the change listener
//	 */
//	public void removeChangeListener( ChangeListener c ) {
//		changeListeners.remove( c );
//	}
//
//	/**
//	 * {@inheritDoc}
//	 * @param e the change event
//	 */
//	public void stateChanged( ChangeEvent e ) {
//		// The change may have broken the rasterization. We are pessimistic here and
//		// reset the "rasterized" flag in any case.
//		rasterized = false;
//
//		// Simply forward the event
//		throwChangeEvent( e );
//	}
//
	/**
	 * Adds the denoted floor to the building plan, only if it has not
	 * been present in the list of floors until now.
	 * @param f the new floor
	 * @return <code>true</code> if the floor was added, <code>false</code> otherwise
	 */
	public boolean addFloor( Floor f ) {
		if( !floors.contains( f ) ) {
			floors.add( f );
//			f.addChangeListener( this );
//			throwChangeEvent( new ChangeEvent( this ) );
			return true;
		}
		return false;
	}

	/**
	 * Moves the given floor one position further towards the beginning of the floor list. Only changes
	 * the order of the floors.
	 * @param level the index of the floor to move
	 * @throws IllegalArgumentException If the given floor is not in the list or if you try to move the 
	 * default evacuation floor
	 */
	public void moveFloorUp( int level ) {
		if( level == 0 )
			throw new IllegalArgumentException( "You may not move the default evacuation floor." );
		else if( level < 0 )
			throw new IllegalArgumentException( "There is no floor with negative id." );
		else if( level < floors.size() - 1 ) {
			final Floor f = floors.get( level );
			floors.set( level, floors.get( level + 1 ) );
			floors.set( level + 1, f );
//			throwChangeEvent( new ChangeEvent( this ) );
		} else
			throw new IllegalArgumentException( "The given floor is not on the list." );
	}

	/**
	 * Moves the given floor one position further towards the end of the floor list. Only changes
	 * the order of the floors.
	 * @param level the index of the floor to move
	 * @throws IllegalArgumentException If the given floor is not in the list or if you try to move the
	 * default evacuation floor
	 */
	public void moveFloorDown( int level ) {
		if( level < 0 )
			throw new IllegalArgumentException( "There is no floor with negative id." );
		else if( level <= 1 )
			throw new IllegalArgumentException( "You may not move the default evacuation floor." );
		else if( level <= floors.size() - 1 ) {
			final Floor f = floors.get( level );
			floors.set( level, floors.get( level - 1 ) );
			floors.set( level - 1, f );
//			throwChangeEvent( new ChangeEvent( this ) );
		} else
			throw new IllegalArgumentException( "The given floor is not on the list." );
	}

	/**
	 * Returns the default floor that exists in each {@link BuildingPlan} as  the first registered floor.
	 * @return the default floor that exists in each {@link BuildingPlan} as the first registered floor */
	public DefaultEvacuationFloor getDefaultFloor() {
		return (DefaultEvacuationFloor)floors.get( 0 );
	}

	/**
	 * Returns view of all <code>Floors</code> that this <code>BuildingPlan<code> contains.
	 * @return the list of {@link Floor} objects (including the default floor)
	 */
	public List<Floor> getFloors() {
		return Collections.unmodifiableList( floors );
	}

	/**
	 * Returns, if the Flag rasterized ist set to TRUE.
	 * @return True, if the Flag rasterized ist set to TRUE. False else.
	 */
	public boolean getRasterized() {
		return rasterized;
	}

	/** @return If the plan has any rooms at all, e.g. if it is empty or not. */
	public boolean isEmpty() {
		for( Floor f : getFloors() )
			if( f.getRooms().size() > 0 )
				return false;
		return true;
	}

	/** @return If the plan has any evacuation areas at all. */
	public boolean hasEvacuationAreas() {
		boolean hasEvac = false;
		for( Floor f : getFloors() ) {
			for( Room r : f.getRooms() )
				if( r.getEvacuationAreas().size() > 0 ) {
					hasEvac = true;
					break;
				}

			if( hasEvac )
				break;
		}
		return hasEvac;
	}

	/**
	 * Removes a specified {@link ds.z.Floor} from the floorlist. The
	 * {@link DefaultEvacuationFloor} cannot be removed. If the floor is the first
	 * (and only one), the floor is deleted and a new empty floor is added again
	 * as the first one.
	 * @param f the floor to be removed
	 * @throws java.lang.IllegalArgumentException if the default floor should be removed.
	 */
	public void removeFloor( Floor f ) throws java.lang.IllegalArgumentException {
		if( f.equals( floors.get( 0 ) ) )
			throw new java.lang.IllegalArgumentException( ZLocalization.getSingleton().getString( "ds.z.BuildingPlan.DeleteDefaultEvacuationFloorException" ) );
		else if( floors.size() == 2 ) {
			// Delete the floor and defineByPoints a new and empty one
			if( floors.remove( f) ) {
				f.delete();
//				f.removeChangeListener( this );
//				throwChangeEvent( new ChangeEvent( this ) );
				addFloor( new Floor( ZLocalization.getSingleton().getString( "ds.z.DefaultName.Floor" ) + " " + 1 ) );
			}
		} else if( floors.remove( f ) ) {
			f.delete();
//			f.removeChangeListener( this );
//			throwChangeEvent( new ChangeEvent( this ) );
		}
	}

	/**
	 * This method checks whether the current BuildingPlan is valid. It 
	 * essentially just delegates this task to the single floors, by calling
	 * their validation routines.
	 * @throws ds.z.exception.PolygonNotClosedException if the validation reveals
	 * that the polygon is not closed
	 * @throws ds.z.exception.AreaNotInsideException if the validation reveals
	 * that an area is not inside its associated room
	 * @throws ds.z.exception.RoomIntersectException  if two rooms intersect
	 */
	public void check() throws PolygonNotClosedException, AreaNotInsideException, RoomIntersectException {
		for( Floor f : floors )
			f.check( getRasterized() );
	}

	@Override
	public boolean equals( Object o ) {
		if( o instanceof BuildingPlan ) {
			BuildingPlan p = (BuildingPlan)o;

			//This is not an implementation error - Lists also have a proper equals method
			return (floors == null) ? p.floors == null : floors.equals( p.floors );
		} else
			return false;
	}

	/**
	 * Returns the number of floors of the building plan, <b>including</b> default
	 * floor.
	 * @return the number of floors
	 */
	public int floorCount() {
		return floors.size();
	}

	/**
	 * Returns the number of floors of the building plan. If the default floor
	 * counts, is decided by a specified boolean parameter.
	 * @param defaultFloor specifies if the default floor is counted, or not
	 * @return the number of floors
	 */
	public int floorCount( boolean defaultFloor ) {
		if( defaultFloor )
			return floors.size();
		else
			return floors.size() - 1;
	}

	/**
	 * Returns the number of all evacuation areas in the building.
	 * @return the evacuation area count
	 */
	public int getEvacuationAreasCount() {
		int count = 0;
		for( Floor floor : floors )
			for( Room room : floor.getRooms() )
				for( EvacuationArea evac : room.getEvacuationAreas() )
					count++;
		return count;
	}

	/**
	 * Rasterizes each Room / Area on every Floor. 
	 * Sets the rasterized flag to "true" upon completion.
	 */
	public void rasterize() {
		try {
			check();
		} catch( ds.z.exception.RoomIntersectException e ) {
			RoomPair rooms = e.getIntersectingRooms();
			System.out.println( "Es schneiden sich die Räume: " + rooms.room1.getName() + " - " + rooms.room2.getName() );
		}
		for( Floor f : floors )
			for( Room r : f.getRooms() ) {
				AlgorithmTask.getInstance().setProgress( 100 / (Math.max( f.roomCount(), 1 )),
								ZLocalization.getSingleton().getString( "ds.z.floor" ) + ":" + f.getName(), r.getName() );

				// Checking if r is rasterized before rasterizing it makes no sense??
				// but it makes sense to ensure that all polygons are closed!!
				r.check( rasterized );
				r.rasterize();
				//r.cleanUpThornsAndNormalEdgesForRooms ();
				r.cleanUpPassableEdgesForRooms();

			}
		AlgorithmTask.getInstance().setProgress( 100, ZLocalization.getSingleton().getString( "ds.z.RasterizeFinished" ), "" );
		rasterized = true;
	}

	/** A convenience method that automatically distributes the given number of evcauees
	 * among all assignment areas that were created in the building. Each area gets a 
	 * share of the total number of evacuees which is proportional to it's share of the
	 * total surface area of all assignment areas. All preexisting evacuee numbers
	 * are overwritten.
	 * 
	 * @param nrOfEvacuees
	 * @throws TooManyPeopleException If you specify a number of evacuees that
	 * exceeds the total space in all assignment areas.
	 */
	public void distributeEvacuees( int nrOfEvacuees ) throws TooManyPeopleException {
		// Get the total assignment area size
		int max_persons = 0;
		int nr_of_assignment_areas = 0;
		for( Floor f : floors )
			for( Room r : f.getRooms() )
				for( AssignmentArea a : r.getAssignmentAreas() ) {
					max_persons += a.getMaxEvacuees();
					nr_of_assignment_areas++;
				}

		if( max_persons < nrOfEvacuees )
			throw new TooManyPeopleException( null, ZLocalization.getSingleton().getString( "ds.TooManyEvacuees" ) );

		// Try to distribute the persons and gather all ass. areas
		int already_distributed = 0;
		int index = 0;
		AssignmentArea[] aareas = new AssignmentArea[nr_of_assignment_areas];
		for( Floor f : floors )
			for( Room r : f.getRooms() )
				for( AssignmentArea a : r.getAssignmentAreas() ) {
					int evacs_for_a = (int)(((double)a.getMaxEvacuees() / (double)max_persons) * nrOfEvacuees);
					a.setEvacuees( evacs_for_a );
					already_distributed += evacs_for_a;

					aareas[index++] = a;
				}

		// Distribute the rest of the eacs (we round down, so possibly there are some
		// of them left) randomly among all ass. areas
		while( already_distributed < nrOfEvacuees ) {
			// We don't use the Random Utils here, because this is not a simulation feature
			// but an editor feature and thus it mustn't forcedly be reproducable
			Random rand = new Random();
			index = rand.nextInt( aareas.length - 1 );
			aareas[index].setEvacuees( aareas[index].getEvacuees() + 1 );
			already_distributed++;
		}
	}

	/**
	 * Returns the maximum number of evacuees that can be placed in all the assignment
	 * areas in the building
	 * @return the maximal number of evacuees in the building */
	public int maximalEvacuees() {
		int maxPersons = 0;
		for( Floor f : floors )
			for( Room r : f.getRooms() )
				for( AssignmentArea a : r.getAssignmentAreas() )
					maxPersons += a.getMaxEvacuees();
		return maxPersons;
	}

	/**
	 * Returns an iterator over the floors of this building plan.
	 * @return an iterator over the floors of this building plan.
	 */
	public Iterator<Floor> iterator() {
		return getFloors().iterator();
	}

}
