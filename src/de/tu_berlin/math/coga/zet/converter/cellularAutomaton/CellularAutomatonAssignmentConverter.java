/**
 * CellularAutomatonAssignmentConverter.java
 * Created: Jul 28, 2010,5:15:56 PM
 */
package de.tu_berlin.math.coga.zet.converter.cellularAutomaton;

import algo.ca.parameter.AbstractDefaultParameterSet;
import algo.ca.parameter.ParameterSet;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.ZETLocalization;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter.ConversionNotSupportedException;
import ds.PropertyContainer;
import ds.ca.evac.EvacCell;
import ds.ca.evac.ExitCell;
import ds.ca.evac.Individual;
import ds.ca.evac.TargetCell;
import de.tu_berlin.coga.zet.model.ConcreteAssignment;
import de.tu_berlin.coga.zet.model.Person;
import de.tu_berlin.coga.zet.model.PlanPoint;
import exitdistributions.ZToExitMapping;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import statistics.Statistic;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class CellularAutomatonAssignmentConverter extends Algorithm<AssignmentApplicationInstance, ConvertedCellularAutomaton> {

	@Override
	protected ConvertedCellularAutomaton runAlgorithm( AssignmentApplicationInstance problem ) {
		try {
			applyConcreteAssignment( problem.getV(), problem.getU() );
		} catch( IllegalArgumentException | ConversionNotSupportedException ex ) {
			Logger.getLogger( CellularAutomatonAssignmentConverter.class.getName() ).log( Level.SEVERE, null, ex );
		}
		return problem.getU();
	}

	/**
	 * Puts persons which are specified in an concrete assignment into the Right
	 * cells of the cellular automaton. This is done by first get the rastered
	 * square version of the room specified in the person object. Afterwards the
	 * cell, in which the person has to be located, is calculated in the following
	 * way: (coordinate-200)/400
	 * @param concreteAssignment the concrete assignment containing the individuals
	 * @throws java.lang.IllegalArgumentException if the calculated cell is not in the room of the cellular automaton
	 */
	private static void applyConcreteAssignment( ConcreteAssignment concreteAssignment, ConvertedCellularAutomaton cca ) throws IllegalArgumentException, ConversionNotSupportedException {
		ZToCARoomRaster room;
		// Create ZToExitMapping
		HashMap<Individual, TargetCell> individualExitMapping = new HashMap<>();
		EvacCell c;
		int x, y;
		int individualCounter = 1;
		for( Person p : concreteAssignment.getPersons() ) {
			room = cca.getContainer().getRasteredRoom( p.getRoom() );
			x = Math.round( (p.getPosition().getXInt() - 200 - p.getRoom().getxOffset()) / 400 );
			y = Math.round( (p.getPosition().getYInt() - 200 - p.getRoom().getyOffset()) / 400 );
			if( !room.isValid( x, y ) )
				throw new IllegalArgumentException( ZETLocalization.getSingleton().getString( "converter.IndividualOutsideException" ) );
			c = cca.getMapping().get( room.getSquare( x, y ) );
			if( c == null ) {
				//JEditor.showErrorMessage( "Fehler", "Individuen konnten nicht erzeugt werden in Raum '" + room.getRoom().getName() + "'. Eventuell keine freien Plätze durch unbetretbare Bereiche?" );
				//throw new ConversionNotSupportedException();
				// Individual mapped to inaccessible area
				// skip
			} else {
				Individual i = generateIndividual( p );
				i.setNumber( individualCounter++ );

				List<ExitCell> exits = cca.getCellularAutomaton().getExits();
				for( ExitCell e : exits ) {
					// Calculate absolute position
					// TODO 400 hardcoded hier!
					int cellCenterX = (e.getX() + e.getRoom().getXOffset()) * 400 + 200;
					int cellCenterY = (e.getY() + e.getRoom().getYOffset()) * 400 + 200;
					if( p.getSaveArea() != null && p.getSaveArea().contains( new PlanPoint( cellCenterX, cellCenterY ) ) ) {
						individualExitMapping.put( i, e );
						break;
					}
				}

				cca.getCellularAutomaton().addIndividual( c, i );
			}
		}
		ZToExitMapping mapping = new ZToExitMapping( individualExitMapping );
		cca.getCellularAutomaton().setIndividualToExitMapping( mapping );
	}

	/**
	 * This method returns an individual whose attributes are based upon die assignment specified by the Z-Assignment group.<br>
	 * To do this the different assignments could be extracted form the passed assignment object. With these values the specific
	 * random attributes for the generated individual can be calculated.
	 * @param assignment The specific Assignment for the new created individual
	 * @return Returns an individual of type Individual
	 */
	private static Individual generateIndividual( Person p ) {
		String parameterName = PropertyContainer.getInstance().getAsString( "algo.ca.parameterSet" );
		AbstractDefaultParameterSet.createParameterSet( "DefaultParameterSet" );
		ParameterSet ps = AbstractDefaultParameterSet.createParameterSet( parameterName );

		double pDecisiveness = p.getDecisiveness();
		double pFamiliarity = p.getFamiliarity();
		double pAge = p.getAge();
		//double pDiameter = p.getDiameter(); // not used in current implementation

		// here is specified how the different properties of an individual are calculated
		double familiarity = pFamiliarity;
		double panicFactor = p.getPanic();
		double slackness = ps.getSlacknessFromDecisiveness( pDecisiveness );
		double exhaustionFactor = ps.getExhaustionFromAge( pAge );
		double maxSpeed = ps.getSpeedFromAge( pAge );

		// Collect statistic
		Statistic.instance.collectAgeSpeed( pAge, maxSpeed );

		// Reaction time is now (since 1.1) computed directly from the correct
		// individual assignment parameter.
		double reactiontime = p.getReaction();
		return new Individual( (int) pAge, familiarity, panicFactor, slackness, exhaustionFactor, maxSpeed, reactiontime, p.getUid() );
	}
}
