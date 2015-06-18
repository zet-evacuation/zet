
package de.tu_berlin.math.coga.zet.converter;

import de.zet_evakuierung.model.Assignment;
import de.zet_evakuierung.model.AssignmentArea;
import de.zet_evakuierung.model.AssignmentType;
import de.zet_evakuierung.model.ConcreteAssignment;
import de.zet_evakuierung.model.Floor;
import de.zet_evakuierung.model.Person;
import de.zet_evakuierung.model.PlanPoint;
import static de.zet_evakuierung.model.ZLocalization.loc;
import de.zet_evakuierung.model.exception.TooManyPeopleException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.zetool.rndutils.RandomUtils;
import org.zetool.rndutils.distribution.continuous.NormalDistribution;
import statistics.Statistic;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public final class AssignmentConcrete {  
  /**
	 * Creates an concrete assignment based upon the assignment areas specified in this assignment.
	 * It generates objects of type Person and locates them onto RasterSquares. These squares are generated
	 * by the Rasterer class after converting diagonal edges to horizontal or vertical ones. The persons 
	 * will get concrete property-values according to the assignment.
	 * @param raster the width of a raster square
	 * @return the concrete assignment with the generated persons
	 * @throws TooManyPeopleException if the number of squares is smaller than the number of persons
	 */
	public static ConcreteAssignment createConcreteAssignment( Assignment a, int raster ) throws TooManyPeopleException {
		ConcreteAssignment ca = new ConcreteAssignment();
		// stores all used positions of persons
		// this is done by saving the absolute coordinates of the middle of a raster square and the accordings current floor
		HashMap<PlanPoint, HashSet<Floor>> usedPositions = new HashMap<>();
		PlanPoint location; // the positions of an individual
		RandomUtils random = RandomUtils.getInstance(); // get random number generator
    System.out.println( "SEED: " + random.getSeed() );
		ArrayList<RasterSquare> rasterSquaresInArea; // ArrayList containing all available RasterSquares in an Area
		HashSet<Floor> floors;

//				ageCollector = lastMapping.get( room.getSquare( x, y ) );


		// iterate over all assignment types
		for( AssignmentType assignmentType : a.getAssignmentTypes() )
			// Gehe alle Assignment-Areas durch
			for( AssignmentArea assignmentArea : assignmentType.getAssignmentAreas() ) {
				// Erzeuge Personen in der Area
				int numberOfPersons = assignmentArea.getEvacuees();
				//rasterize the area to have a raster which the Rasterer afterwards can easily generate
				// TODO correct use of rasterization!
				//assignmentArea.rasterize();
				// Rasterization of Area
				//Raster<RasterSquare, PlanPolygon> rasterer = new Raster( RasterSquare.class, PlanPolygon.class, assignmentArea, raster );  //new Rasterization();
				Raster<RasterSquare> rasterer = new Raster( RasterSquare.class, assignmentArea.getPolygon(), raster );  //new Rasterization();
				rasterer.rasterize();
				int squareCount = rasterer.insideSquares().size();
				rasterSquaresInArea = new ArrayList<>( squareCount );

				//List<RasterSquare> unmodifiableInsideSquares = rasterer.insideSquares();
				//copy all RasterSquares. necessary because rasterer.insideSquares() returns an unmodifialbe list :(
				for( RasterSquare s : rasterer.insideSquares() )
					//public RasterSquare(PlanPolygon p, int column, int row, int raster)
					rasterSquaresInArea.add( new RasterSquare( assignmentArea, s.getColumn(), s.getRow(), s.getRaster() ) );

				if( squareCount < numberOfPersons )
					throw new TooManyPeopleException( assignmentArea, loc.getString( "ds.z.Assignment.NotEnoughSpaceException" ) );

				while(numberOfPersons > 0) {
					// this case could occur, if all available cells are already used by an overlaying area
					if( rasterSquaresInArea.isEmpty() )
						throw new TooManyPeopleException( assignmentArea, loc.getString( "ds.z.Assignment.NotEnoughSpaceException" ) );
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
							floors = new HashSet<>();
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
					final double age = assignmentType.getAge().getNextRandom();

					person.setAge( age );
					Statistic.instance.collectAge( age, ((NormalDistribution)assignmentType.getAge()).getRounds() );
					person.setDecisiveness( assignmentType.getDecisiveness().getNextRandom() );
					person.setDiameter( assignmentType.getDiameter().getNextRandom() );
					person.setFamiliarity( assignmentType.getFamiliarity().getNextRandom() );
					person.setPanic( assignmentType.getPanic().getNextRandom() );
					person.setReaction( assignmentType.getReaction().getNextRandom() );
					person.setUid( assignmentType.getUid() );
					person.setSaveArea( assignmentArea.getExitArea() );
					ca.addPerson( person );
					numberOfPersons--;
				}
			}
		return ca;
	}
}
