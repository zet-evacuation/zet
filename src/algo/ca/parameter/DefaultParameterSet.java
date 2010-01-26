/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
 * DefaultParameterSet.java
 * Created on 23.01.2008
 */

package algo.ca.parameter;

import ds.PropertyContainer;
import ds.ca.Cell;
import ds.ca.DynamicPotential;
import ds.ca.Individual;
import ds.ca.StaticPotential;
import java.util.Collection;
import java.util.List;
import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.rndutils.RandomUtils;
import de.tu_berlin.math.coga.rndutils.distribution.continuous.NormalDistribution;

/**
 * @author Daniel Pluempe, Jan-Philipp Kappmeier
 */
public class DefaultParameterSet extends AbstractDefaultParameterSet {
	final protected double PANIC_TO_PROB_OF_POTENTIAL_CHANGE_RATIO;
	final protected double SLACKNESS_TO_IDLE_RATIO;
	final protected double PANIC_DECREASE;
	final protected double PANIC_INCREASE;
	final protected double PANIC_WEIGHT_ON_SPEED;
	final protected double PANIC_WEIGHT_ON_POTENTIALS;
	final protected double EXHAUSTION_WEIGHT_ON_SPEED;
	final protected double PANIC_THRESHOLD;
	final protected double MINIMUM_PANIC = 0.0d;
	final protected double MAXIMUM_PANIC = 1.0d;

	/**
	 * Creates a new instance with some static values stored in the {@code PropertyContainer}.
	 */
	public DefaultParameterSet() {
		PANIC_TO_PROB_OF_POTENTIAL_CHANGE_RATIO = PropertyContainer.getInstance().getAsDouble( "algo.ca.PANIC_TO_PROB_OF_POTENTIAL_CHANGE_RATIO" );
		SLACKNESS_TO_IDLE_RATIO = PropertyContainer.getInstance().getAsDouble( "algo.ca.SLACKNESS_TO_IDLE_RATIO" );
		PANIC_DECREASE = PropertyContainer.getInstance().getAsDouble( "algo.ca.PANIC_DECREASE" );
		PANIC_INCREASE = PropertyContainer.getInstance().getAsDouble( "algo.ca.PANIC_INCREASE" );
		PANIC_WEIGHT_ON_SPEED = PropertyContainer.getInstance().getAsDouble( "algo.ca.PANIC_WEIGHT_ON_SPEED" );
		PANIC_WEIGHT_ON_POTENTIALS = PropertyContainer.getInstance().getAsDouble( "algo.ca.PANIC_WEIGHT_ON_POTENTIALS" );
		EXHAUSTION_WEIGHT_ON_SPEED = PropertyContainer.getInstance().getAsDouble( "algo.ca.EXHAUSTION_WEIGHT_ON_SPEED" );
		PANIC_THRESHOLD = PropertyContainer.getInstance().getAsDouble( "algo.ca.PANIC_THRESHOLD" );
	}

	/**
	 * {@inheritDoc}
	 * @param i
	 * @return
	 * @see algo.ca.parameter.AbstractDefaultParameterSet#changePotentialThreshold(ds.ca.Individual)
	 */
	@Override
	public double changePotentialThreshold( Individual i ) {
		if( !i.isSafe() )
			return i.getPanic() * panicToProbOfPotentialChangeRatio();
		return 0;
	}

	/**
	 * Given a cell <code>referenceCell</code> that is occupied by an 
	 * individual I, this method calculates the potential of a cell with respect 
	 * to I's panic and both the static and the dynamic potential. One can
	 * think of the resulting potential as an "average" of the static and the
	 * dynamic potential. However, the influence of the static and the
	 * dynamic potential on the average is determined by two constants 
	 * and I's panic. The higher the panic, the more important the 
	 * dynamic potential will become while the influence of the static 
	 * potential lessens. 
	 * 
	 * @param referenceCell A cell with an individual
	 * @param targetCell A neighbour of <code>cell</code>
	 * @return The potential between <code>referenceCell</code> and 
	 * <code>targetCell</code> with respect to the static and the 
	 * dynamic potential.  
	 */
	@Override
	public double effectivePotential( Cell referenceCell, Cell targetCell ) {
		if( referenceCell.getIndividual() == null ) {
			throw new IllegalArgumentException( Localization.getInstance().getString( "algo.ca.parameter.NoIndividualOnReferenceCellException" ) );
		}
		final double panic = referenceCell.getIndividual().getPanic();
		StaticPotential staticPotential = referenceCell.getIndividual().getStaticPotential();
		DynamicPotential dynamicPotential = referenceCell.getIndividual().getDynamicPotential();

		if( dynamicPotential != null ) {
			final double dynPotDiff = (-1) * (dynamicPotential.getPotential( referenceCell ) - dynamicPotential.getPotential( targetCell ));
			final double statPotlDiff = staticPotential.getPotential( referenceCell ) - staticPotential.getPotential( targetCell );
			return (Math.pow( panic, PANIC_WEIGHT_ON_POTENTIALS ) * dynPotDiff * dynamicPotentialWeight()) + ((1 - Math.pow( panic, PANIC_WEIGHT_ON_POTENTIALS )) * statPotlDiff * staticPotentialWeight());
		//return statPotlDiff * staticPotentialWeight();
		} else {
			//	System.out.println( "DynamicPotential = NULL!");
			final double statPotlDiff = staticPotential.getPotential( referenceCell ) - staticPotential.getPotential( targetCell );
			return Math.pow( 1 - panic, PANIC_WEIGHT_ON_POTENTIALS ) * statPotlDiff * staticPotentialWeight();
		}
	}

	/*
	 * {@inheritDoc}
	 * @see algo.ca.parameter.AbstractDefaultParameterSet#idleThreshold(ds.ca.Individual)
	 */
	@Override
	public double idleThreshold( Individual i ) {
		return i.getSlackness() * slacknessToIdleRatio();
	}

	/*
	 * {@inheritDoc}
	 * @see algo.ca.parameter.AbstractDefaultParameterSet#movementThreshold( ds.ca.Individual )
	 */
	// wird nur benutzt wenn die Geschwindigkeit der Individuen mit Wahrscheinlichkeiten simuliert wird
	// das ist in der NonWaitingMovementRule nicht der Fall
	@Override
	public double movementThreshold( Individual i ) {
		double individualSpeed = i.getCurrentSpeed();
		double cellSpeed = i.getCell().getSpeedFactor();
		// double exhaustion = i.getExhaustion();  brauchen wir nur wenn wir in 
		//currentspeed exhaustion nicht einrechenen
		return individualSpeed * cellSpeed;
	}

	/*
	 * {@inheritDoc}
	 * @see algo.ca.parameter.AbstractDefaultParameterSet#updateExhaustion(ds.ca.Individual)
	 */
	@Override
	public double updateExhaustion( Individual individual, Cell targetCell ) {
		// ExhaustionFactor depends from the age. currently it is always initialized with 1, so all individuals exhauste with the same
		// speed.
		// i hope the formular is right: it does the following
		// each individual looses a percentage of the exhauston factor, depending of the current speed:
		// currentSpeed / maxSpeed. this is a value between 0 and 1
		// each individual has an exhaustionfactor between 0 and 1 that describes the speed with which the exhaustion decreases.
		// the resulting value is a value between 0 and 1 and increases the exhaustion, so it is added to the old exhaustion value.
		final double MIN_EXHAUSTION = 0d;
		final double MAX_EXHAUSTION = 0.99d;
		double newExhaustion;
		if( individual.getCell().equals( targetCell ) ) {
			newExhaustion =
							(0 / individual.getMaxSpeed() - 0.5) * individual.getExhaustionFactor() + individual.getExhaustion();
		} else {
			newExhaustion =
							(individual.getCurrentSpeed() / individual.getMaxSpeed() - 0.5) * individual.getExhaustionFactor() + individual.getExhaustion();
		}


		if( newExhaustion < MIN_EXHAUSTION ) {
			newExhaustion = MIN_EXHAUSTION;
		} else if( newExhaustion > MAX_EXHAUSTION ) {
			newExhaustion = MAX_EXHAUSTION;
		}
		individual.setExhaustion( newExhaustion );

		return newExhaustion;
	}

	public double updatePanic( Individual individual, Cell targetCell, Collection<Cell> preferedCells ) {
		List<Cell> possibleNeighbours = individual.getCell().getNeighbours();

		double[] potentials = new double[possibleNeighbours.size()];
		int idx = 0;
		for( Cell cell : possibleNeighbours ) {
			double potentialDifference = individual.getStaticPotential().getPotential( individual.getCell() ) - individual.getStaticPotential().getPotential( cell );
			potentials[idx] = Math.exp( potentialDifference );
			idx++;
		}

		int failures = 0;

		int chosenNeighbour = RandomUtils.getInstance().chooseRandomlyAbsolute( potentials );
		while( possibleNeighbours.get( chosenNeighbour ).getIndividual() != null && failures <= possibleNeighbours.size() ) {
			failures++;
			potentials[chosenNeighbour] = 0;
			chosenNeighbour = RandomUtils.getInstance().chooseRandomlyAbsolute( potentials );
		}

		double newPanic = individual.getPanic();
		if( failures < PANIC_THRESHOLD ) {
			newPanic = newPanic - individual.getPanicFactor() * getPanicDecrease() * (PANIC_THRESHOLD - failures);
		} else {
			newPanic = newPanic + individual.getPanicFactor() * (failures - PANIC_THRESHOLD) * getPanicIncrease();
		}

		newPanic = Math.max( MINIMUM_PANIC, newPanic );
		newPanic = Math.min( MAXIMUM_PANIC, newPanic );

		individual.setPanic( newPanic );
		return newPanic;

	/* alter Code */
//		    // update panic only if the individual is not standing on a savecell or an exitcell
//	        if (! ( (individual.getCell() instanceof ds.ca.SaveCell) || (individual.getCell() instanceof ds.ca.ExitCell) )) {
//	        	
//	            double panic = individual.getPanic();
//	            
//	            //person will gar nicht laufen (slack usw.)
//				if( preferedCells.size() == 0 )
//					return panic;
//			
//				Iterator<Cell> it = preferedCells.iterator();
//				Cell neighbour = it.next();
//				double panicFactor = individual.getPanicFactor();
//				if(individual.getCell() != targetCell){
//					individual.setPanic(Math.max(panic - getPanicDecrease()*0.17, MINIMUM_PANIC));
//					return individual.getPanic();
//				}
//				
//				int skippedCells = 0;
//				while( it.hasNext() && neighbour != targetCell ) {
//					if( neighbour.getIndividual() != null ) {
//						panic += getPanicIncrease() * panicFactor / (2 << (preferedCells.size() - skippedCells));
//						skippedCells++;
//					}
//					neighbour = it.next();
//				}
//						
//				individual.setPanic( Math.min(panic, MAXIMUM_PANIC));
//			    }
//				return individual.getPanic();
			/* Ende alter Code */
	}

	/*
	 * {@inheritDoc}
	 * @see algo.ca.parameter.AbstractDefaultParameterSet#updateSpeed(ds.ca.Individual)
	 */
	@Override
	public double updatePreferredSpeed( Individual i ) {
		//double oldSpeed = i.getCurrentSpeed();
		double maxSpeed = i.getMaxSpeed();
		double newSpeed = maxSpeed + ((i.getPanic() * panicWeightOnSpeed()) - (i.getExhaustion() * exhaustionWeightOnSpeed()));
		i.setCurrentSpeed( Math.max( 0.0001, Math.min( maxSpeed, newSpeed ) ) );

		//		if( i.getMaxSpeed() < newSpeed )
//			i.setCurrentSpeed( i.getMaxSpeed() );
//		else
//			i.setCurrentSpeed( newSpeed );
		return i.getCurrentSpeed();
	}

	@Override
	public double getAbsoluteMaxSpeed() {
		return ABSOLUTE_MAX_SPEED;
	}

	protected double slacknessToIdleRatio() {
		return SLACKNESS_TO_IDLE_RATIO;
	}

	protected double panicToProbOfPotentialChangeRatio() {
		return PANIC_TO_PROB_OF_POTENTIAL_CHANGE_RATIO;
	}

	protected double getPanicIncrease() {
		return PANIC_INCREASE;
	}

	protected double getPanicDecrease() {
		return PANIC_DECREASE;
	}

	protected double panicWeightOnSpeed() {
		return PANIC_WEIGHT_ON_SPEED;
	}

	protected double exhaustionWeightOnSpeed() {
		return EXHAUSTION_WEIGHT_ON_SPEED;
	}

	public double getExhaustionFromAge( double age ) {
		//minum Exhaustion: individual is fully exhausted
		//after about 450 meter        
		final double MIN_EXHAUSTION = 0.0018d;

		//minum Exhaustion: individual is fully exhausted
		//after about 200 meter
		final double MAX_EXHAUSTION = 0.004d;
		if( age < 10d ) {
			return MAX_EXHAUSTION;
		} else if( age >= 90d ) {
			return MAX_EXHAUSTION;
		//assume exhaustion is at the least on age of 20
		} else if( age <= 20 ) {
			double ageRatio = (age - 10d) / 10;
			double ret = MAX_EXHAUSTION -
							ageRatio * (MAX_EXHAUSTION - MIN_EXHAUSTION);
			return ret;

		} else {
			double ageRatio = (age - 25d) / (90d - 25d);
			double ret = MIN_EXHAUSTION +
							ageRatio * (MAX_EXHAUSTION - MIN_EXHAUSTION);
			return ret;
		}

	}

	@Override
	public double getReactionTimeFromAge( double age ) {
		return age / 10;
	}

	@Override
	public double getReactionTime( ) {
		return 1;
	}

	/**
	 * Calculates the maximal speed for a person dependingon the speed-values
	 * from the rimea test suite.
	 * @param pAge
	 * @return the maximal speed as percentage of the overall maximal speed for the simulation run
	 */
	static double cumulativeSpeed = 0;
	static int counter = 0;

	public double getSpeedFromAge( double pAge ) {
		// additional: calculate the average speed.
		double ageArray[] = { 0.6, 1.15, 1.42, 1.61, 1.55, 1.51, 1.5, 1.48, 1.47, 1.4, 1.33, 1.29, 1.2, 1.08, 0.85, 0.7 };
		int i2 = (int) Math.floor( pAge / 5 );
		int i1 = i2 - 1;
		double maxSpeedExpected = 0;
		if( pAge <= 5 ) {
			maxSpeedExpected = ageArray[0];
		} else if( pAge >= 80 ) {
			maxSpeedExpected = ageArray[15];
		} else {
			double diff = pAge - i2 * 5;
			double slope = (ageArray[i2] - ageArray[i1]) / 5;
			maxSpeedExpected = ageArray[i1] + diff * slope;
		}
		NormalDistribution normal = new NormalDistribution( maxSpeedExpected, 0.1, ageArray[0], ABSOLUTE_MAX_SPEED );
		double randSpeed = normal.getNextRandom();
		double maxSpeed = randSpeed / ABSOLUTE_MAX_SPEED;
		if( maxSpeed > 1 ) {
			maxSpeed = 1;
		//System.err.println( "Maximale geschw: " + maxSpeed );
		}

		// Correction of 0.2
//		maxSpeed = Math.max( 0.6, maxSpeed-0.4 );

		counter++;
		cumulativeSpeed += (maxSpeed * ABSOLUTE_MAX_SPEED);

		System.out.println( "First one has speed " + (maxSpeed * ABSOLUTE_MAX_SPEED) );
		System.out.println( "Average speed for " + counter + " persons: " + (cumulativeSpeed / counter) + " m/s. (Should be 1.3x)" );



		return maxSpeed;
	}

	public double getSlacknessFromDecisiveness( double pDecisiveness ) {
		return (1 - pDecisiveness) * 0.25;
	}
}
