/* zet evacuation tool copyright (ageCollector) 2007-10 zet evacuation team
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
 * Assignment.java
 * Created on 26. November 2007, 21:32
 */
package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import de.tu_berlin.math.coga.zet.converter.Raster;
import de.tu_berlin.math.coga.zet.converter.RasterSquare;
import de.tu_berlin.math.coga.rndutils.RandomUtils;
import ds.z.exception.TooManyPeopleException;
import io.z.AssignmentConverter;
import io.z.XMLConverter;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import de.tu_berlin.math.coga.rndutils.distribution.continuous.NormalDistribution;
import statistics.Statistic;

/**
 * The {@code Assignment} class holds all information about one assignment
 * in the Z-format. It is a container for some assignment types and can submit
 * lists of all assignments.
 * @author Sylvie Temme
 */
@XStreamAlias("assignment")
@XMLConverter(AssignmentConverter.class)
//public class Assignment implements Serializable, ChangeReporter, ChangeListener {
public class Assignment implements Serializable {

//	@XStreamOmitField()
//	private transient ArrayList<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
	/** The name of the assignment. */
	@XStreamAsAttribute()
	private String name;
	/** The List of assignmentTypes, which belong to this assignment. */
	private ArrayList<AssignmentType> assignmentTypes;
	/**
	 * Static variable that stores the default-value for the area, that a person needs.
	 * Its unit is squaremillimeter.
	 */
	public static int spacePerPerson = 160000;

	/**
	 * Creates a new instance of {@link Assignment}.
	 * Sets the name of the assignment.
	 * @param name The name of the Assignment.
	 */
	public Assignment( String name ) {
		setName( name );
		assignmentTypes = new ArrayList<AssignmentType>();
	}

	/**
	 * Returns the name of the assignment.
	 * @return The name of the assignment.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the assignment.
	 * @param val The name of the assignment.
	 * @throws IllegalArgumentException If the given name is nul or "".
	 */
	public void setName( String val ) throws IllegalArgumentException {
		if( val == null || val.equals( "" ) )
			throw new IllegalArgumentException( ZLocalization.getSingleton().getString( "ds.z.Assignment.NoNameException" ) );
		name = val;
	}

	/**
	 * Returns all assignmentTypes of this assignment.
	 * @return All assignmentTypes of this assignment.
	 */
	public List<AssignmentType> getAssignmentTypes() {
		return Collections.unmodifiableList( assignmentTypes );
	}

	/**
	 * Adds a new assignmentType to this assignment.
	 * @param val The assignmentType to be added.
	 * @throws java.lang.IllegalArgumentException If the new assignmentType already is an assignmentType of this assignment.
	 */
	public void addAssignmentType( AssignmentType val ) throws IllegalArgumentException {
		if( assignmentTypes.contains( val ) )
			throw new IllegalArgumentException( ZLocalization.getSingleton().getString( "ds.z.Assignment.DoubleAssignmentTypeException" ) );
		else {
			assignmentTypes.add( val );
//			val.addChangeListener( this );
//			throwChangeEvent( new ChangeEvent( this ) );
		}
	}

	/**
	 * Removes an assignmentType from the list of assignmentTypes of this assignment.
	 * @param val The assignmentType to be removed.
	 * @throws java.lang.IllegalArgumentException If the assignmentType is not in the list of assignmentTypes of this assignment.
	 */
	public void deleteAssignmentType( AssignmentType val ) throws IllegalArgumentException {
		if( !assignmentTypes.contains( val ) )
			throw new IllegalArgumentException( ZLocalization.getSingleton().getString( "ds.z.Assignment.AssignmentTypeNotNotFoundException" ) );
		else {
			assignmentTypes.remove( val );
//			val.removeChangeListener( this );
//			throwChangeEvent( new ChangeEvent( this ) );

			// Delete corresponding assignment areas
			// The areas deregister themselves out of the getAssignmentAreas() list, so
			// this list has to be copied before deleting
			AssignmentArea[] areaCopy = val.getAssignmentAreas().toArray(
							new AssignmentArea[val.getAssignmentAreas().size()] );
			for( AssignmentArea a : areaCopy )
				a.delete();
		}
	}

	/**
	 * Deletes all references from this assignment.
	 * (Sets the list of assignmentTypes of this assignment to null.)
	 */
	public void delete() {
		// Delete all assignment areas
		for( AssignmentType t : assignmentTypes )
			// Don't use for loop here - will throw a concurrent modification exception - TIMON
			//for( AssignmentArea a : t.getAssignmentAreas() )
			while(!t.getAssignmentAreas().isEmpty())
				t.getAssignmentAreas().get( 0 ).delete();
		assignmentTypes.clear();
		assignmentTypes = null;
	}

	@Override
	public boolean equals( Object o ) {
		if( o instanceof Assignment ) {
			Assignment p = (Assignment) o;
			return assignmentTypes.equals( p.getAssignmentTypes() ) &&
							((name == null) ? p.getName() == null : name.equals( p.getName() ));
		} else
			return false;
	}

	/**
	 * Gives notice to all the assignmentAreas of this assignment
	 * that this assignment is now the current assignment of the project.
	 */
	public void setActive() {
		// Iterate over all assignments and their assignment types
		for( AssignmentType assignmentType : getAssignmentTypes() )
			for( AssignmentArea assignmentArea : assignmentType.getAssignmentAreas() )
				assignmentArea.setActive();
	}

	/**
	 * Gives notice to all the assignmentAreas of this assignment
	 * that this assignment is not any more the current assignment of the project.
	 */
	public void setInactive() {
		// Iterate over all assignments and their assignment types
		for( AssignmentType assignmentType : getAssignmentTypes() )
			for( AssignmentArea assignmentArea : assignmentType.getAssignmentAreas() )
				assignmentArea.setInactive();
	}

	/**
	 * Creates an concrete assignment based upon the assignment areas specified in this assignment.
	 * It generates objects of type Person and locates them onto RasterSquares. These squares are generated
	 * by the Rasterer class after converting diagonal edges to horizontal or vertical ones. The persons 
	 * will get concrete property-values according to the assignment.
	 * @param raster the width of a raster square
	 * @return the concrete assignment with the generated persons
	 * @throws ds.z.exception.TooManyPeopleException if the number of squares is smaller than the number of persons
	 */
	public ConcreteAssignment createConcreteAssignment( int raster ) throws TooManyPeopleException {
		ConcreteAssignment ca = new ConcreteAssignment();
		// stores all used positions of persons
		// this is done by saving the absolute coordinates of the middle of a raster square and the accordings current floor
		HashMap<PlanPoint, HashSet<Floor>> usedPositions = new HashMap<PlanPoint, HashSet<Floor>>();
		PlanPoint location; // the positions of an individual
		RandomUtils random = RandomUtils.getInstance(); // get random number generator
		ArrayList<RasterSquare> rasterSquaresInArea; // ArrayList containing all available RasterSquares in an Area
		HashSet<Floor> floors;

//				ageCollector = lastMapping.get( room.getSquare( x, y ) );


		// iterate over all assignment types
		for( AssignmentType assignmentType : getAssignmentTypes() )
			// Gehe alle Assignment-Areas durch
			for( AssignmentArea assignmentArea : assignmentType.getAssignmentAreas() ) {
				// Erzeuge Personen in der Area
				int numberOfPersons = assignmentArea.getEvacuees();
				//rasterize the area to have a raster which the Rasterer afterwards can easily generate
				// TODO correct use of rasterization!
				//assignmentArea.rasterize();
				// Rasterization of Area
				Raster<RasterSquare, PlanPolygon> rasterer = new Raster( RasterSquare.class, PlanPolygon.class, assignmentArea, raster );  //new Rasterization();
				rasterer.rasterize();
				int squareCount = rasterer.insideSquares().size();
				rasterSquaresInArea = new ArrayList<RasterSquare>( squareCount );

				//List<RasterSquare> unmodifiableInsideSquares = rasterer.insideSquares();
				//copy all RasterSquares. necessary because rasterer.insideSquares() returns an unmodifialbe list :(
				for( RasterSquare s : rasterer.insideSquares() )
					//public RasterSquare(PlanPolygon p, int column, int row, int raster)
					rasterSquaresInArea.add( new RasterSquare( assignmentArea, s.getColumn(), s.getRow(), s.getRaster() ) );

				if( squareCount < numberOfPersons )
					throw new TooManyPeopleException( assignmentArea, ZLocalization.getSingleton().getString( "ds.z.Assignment.NotEnoughSpaceException" ) );

				while(numberOfPersons > 0) {
					// this case could occur, if all available cells are already used by an overlaying area
					if( rasterSquaresInArea.size() == 0 )
						throw new TooManyPeopleException( assignmentArea, ZLocalization.getSingleton().getString( "ds.z.Assignment.NotEnoughSpaceException" ) );
					int squareNumber = random.getRandomGenerator().nextInt( rasterSquaresInArea.size() );
					RasterSquare square = rasterSquaresInArea.get( squareNumber );

					int xPos = square.getX() + raster / 2;
					int yPos = square.getY() + raster / 2;
					location = new PlanPoint( xPos, yPos );

					Person person;
					if( square.getIntersectType() == RasterSquare.FieldIntersectType.Inside ) {
						person = new Person( new PlanPoint( xPos, yPos ), assignmentArea.getAssociatedRoom() );
						//case: raster square with this location was never used before
						if( !usedPositions.containsKey( location ) ) {
							floors = new HashSet<Floor>();
							floors.add( assignmentArea.getAssociatedRoom().getAssociatedFloor() );
							usedPositions.put( new PlanPoint( xPos, yPos ), floors );
							rasterSquaresInArea.remove( squareNumber );
						} else
							//case: raster square with this location was used before but on another floor
							if( !usedPositions.get( location ).contains( assignmentArea.getAssociatedRoom().getAssociatedFloor() ) ) {
								floors = usedPositions.get( location );
								floors.add( assignmentArea.getAssociatedRoom().getAssociatedFloor() );
								rasterSquaresInArea.remove( squareNumber );
							} else {
								rasterSquaresInArea.remove( squareNumber );
								continue;
							}
					} else {
						rasterSquaresInArea.remove( squareNumber );
						continue;
					}

					// set properties for the persons
					Statistic.instance.addPerson();
					final double age = assignmentType.getAge().getNextRandom().doubleValue();

					person.setAge( age );
					Statistic.instance.collectAge( age, ((NormalDistribution)assignmentType.getAge()).getRounds() );
					person.setDecisiveness( assignmentType.getDecisiveness().getNextRandom().doubleValue() );
					person.setDiameter( assignmentType.getDiameter().getNextRandom().doubleValue() );
					person.setFamiliarity( assignmentType.getFamiliarity().getNextRandom().doubleValue() );
					person.setPanic( assignmentType.getPanic().getNextRandom().doubleValue() );
					person.setReaction( assignmentType.getReaction().getNextRandom().doubleValue() );
					person.setUid( assignmentType.getUid() );
					person.setSaveArea( assignmentArea.getExitArea() );
					ca.addPerson( person );
					numberOfPersons--;
				}
			}
		return ca;
	}
}
