/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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

package algo.ca.parameter;

import de.tu_berlin.math.coga.zet.ZETLocalization2;
import de.tu_berlin.math.coga.rndutils.RandomUtils;
import de.tu_berlin.math.coga.rndutils.distribution.continuous.NormalDistribution;
import ds.PropertyContainer;
import ds.ca.evac.EvacCell;
import ds.ca.evac.DynamicPotential;
import ds.ca.evac.Individual;
import ds.ca.evac.StaticPotential;
import java.util.Collection;
import java.util.List;

/**
 * @author Daniel R. Schmidt
 * @author Jan-Philipp Kappmeier
 */
public class DefaultParameterSet extends AbstractParameterSet {
  private final double PANIC_TO_PROB_OF_POTENTIAL_CHANGE_RATIO;
  final private double SLACKNESS_TO_IDLE_RATIO;
  final private double PANIC_DECREASE;
  final private double PANIC_INCREASE;
  final private double PANIC_WEIGHT_ON_SPEED;
  final private double PANIC_WEIGHT_ON_POTENTIALS;
  final private double EXHAUSTION_WEIGHT_ON_SPEED;
  final private double PANIC_THRESHOLD;
  final private double MINIMUM_PANIC = 0.0d;
  final private double MAXIMUM_PANIC = 1.0d;

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
	 * @return the individual threshold for the individual {@code i}
	 * @see algo.ca.parameter.AbstractDefaultParameterSet#changePotentialThreshold(ds.ca.Individual)
	 */
	@Override
	public double changePotentialThreshold( Individual i ) {
		if( !i.isSafe() )
			return i.getPanic() * panicToProbOfPotentialChangeRatio();
		return 0;
	}

	/**
	 * <p>Given a cell {@code referenceCell} that is occupied by an
	 * individual I, this method calculates the potential of a cell with respect
	 * to I's panic and both the static and the dynamic potential. One can
	 * think of the resulting potential as an "average" of the static and the
	 * dynamic potential. However, the influence of the static and the
	 * dynamic potential on the average is determined by two constants
	 * and I's panic. The higher the panic, the more important the
	 * dynamic potential will become while the influence of the static
	 * potential lessens.</p>
	 * @param referenceCell A cell with an individual
	 * @param targetCell A neighbour of {@code cell}
	 * @return The potential between {@code referenceCell} and  {@code targetCell} with respect to the static and the  dynamic potential.
	 */
	@Override
	public double effectivePotential( EvacCell referenceCell, EvacCell targetCell ) {
		if( referenceCell.getIndividual() == null ) {
			throw new IllegalArgumentException( ZETLocalization2.loc.getString( "algo.ca.parameter.NoIndividualOnReferenceCellException" ) );
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
	 * @see algo.ca.parameter.AbstractParameterSet#idleThreshold(ds.ca.Individual)
	 */
	@Override
	public double idleThreshold( Individual i ) {
		return i.getSlackness() * slacknessToIdleRatio();
	}

	/*
	 * {@inheritDoc}
	 * @see algo.ca.parameter.AbstractParameterSet#movementThreshold( ds.ca.Individual )
	 */
	// wird nur benutzt wenn die Geschwindigkeit der Individuen mit Wahrscheinlichkeiten simuliert wird
	// das ist in der NonWaitingMovementRule nicht der Fall
	@Override
	public double movementThreshold( Individual i ) {
		double individualSpeed = i.getRelativeSpeed();
		double cellSpeed = i.getCell().getSpeedFactor();
		// double exhaustion = i.getExhaustion();  brauchen wir nur wenn wir in
		//currentspeed exhaustion nicht einrechenen
		return individualSpeed * cellSpeed;
	}

	/*
	 * {@inheritDoc}
	 * @see algo.ca.parameter.AbstractParameterSet#updateExhaustion(ds.ca.Individual)
	 */
	@Override
	public double updateExhaustion( Individual individual, EvacCell targetCell ) {
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
							(individual.getRelativeSpeed() / individual.getMaxSpeed() - 0.5) * individual.getExhaustionFactor() + individual.getExhaustion();
		}


		if( newExhaustion < MIN_EXHAUSTION ) {
			newExhaustion = MIN_EXHAUSTION;
		} else if( newExhaustion > MAX_EXHAUSTION ) {
			newExhaustion = MAX_EXHAUSTION;
		}
		individual.setExhaustion( newExhaustion );

		return newExhaustion;
	}

	public double updatePanic( Individual individual, EvacCell targetCell, Collection<EvacCell> preferedCells ) {
		List<EvacCell> possibleNeighbours = individual.getCell().getNeighbours();

		double[] potentials = new double[possibleNeighbours.size()];
		int idx = 0;
		for( EvacCell cell : possibleNeighbours ) {
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
//				EvacCell neighbour = it.next();
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
	 * @see algo.ca.parameter.AbstractParameterSet#updateSpeed(ds.ca.Individual)
	 */
	@Override
	public double updatePreferredSpeed( Individual i ) {
		//double oldSpeed = i.getRelativeSpeed();
		double maxSpeed = i.getMaxSpeed();
		double newSpeed = maxSpeed + ((i.getPanic() * panicWeightOnSpeed()) - (i.getExhaustion() * exhaustionWeightOnSpeed()));
		i.setRelativeSpeed( Math.max( 0.0001, Math.min( maxSpeed, newSpeed ) ) );

		//		if( i.getMaxSpeed() < newSpeed )
//			i.setRelativeSpeed( i.getMaxSpeed() );
//		else
//			i.setRelativeSpeed( newSpeed );
		return i.getRelativeSpeed();
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

	public double cumulativeSpeed = 0;

		public double cumulativeFemale = 0;
		public double cumulativeMale = 0;
		public int counterFemale = 0;
		public int counterMale = 0;

		protected final static double sigmaSquared = 0.26*0.26;

	/**
	 * Calculates the maximal speed for a person dependingon the speed-values
	 * from the rimea test suite.
	 * @param pAge
	 * @return the maximal speed as percentage of the overall maximal speed for the simulation run
	 */
	public double getSpeedFromAge( double pAge ) {
		// additional: calculate the average speed.
		double ageArray[] = {
			0.58, // 5  years
			1.15, // 10
			1.42, // 15
			1.61, // 20
			1.55, // 25
			1.54, // 30
			1.5,  // 35
			1.48, // 40
			1.47, // 45
			1.41, // 50
			1.33, // 55
			1.29, // 60
			1.2,  // 65
			1.08, // 70
			0.85, // 75
			0.68, // 80
			0.5   // 85 // guessed, value not based on weidmann
		};
		final int right = (int) Math.floor( pAge / 5 );
		final int left = right - 1;
		double maxSpeedExpected = 0;
		if( pAge <= 5 ) {
			maxSpeedExpected = ageArray[0];
		} else if( pAge >= 85 ) {
			maxSpeedExpected = ageArray[16];
		} else {
			final double slope = (ageArray[right] - ageArray[left]);
			maxSpeedExpected = slope * (pAge - ((int)pAge/5)*5)/5  + ageArray[left];
		}

		boolean male = RandomUtils.getInstance().binaryDecision( 0.5 );

		// Change speeds for male and female individuals:
		// + 5% for male, -5% for female
		maxSpeedExpected *= male ? 1.05 : 0.95;

		// Generate the random speed with a deviation around the expected speed for the person
		if( maxSpeedExpected < ageArray[16] )
			maxSpeedExpected = ageArray[16];
		else if( maxSpeedExpected > ABSOLUTE_MAX_SPEED )
			maxSpeedExpected = ABSOLUTE_MAX_SPEED;
		final NormalDistribution normal = new NormalDistribution( maxSpeedExpected, sigmaSquared, ageArray[16], ABSOLUTE_MAX_SPEED );
		double randSpeed = normal.getNextRandom();

		if( !male ) {
			counterFemale++;
			cumulativeFemale += randSpeed;
		} else {
			counterMale++;
			cumulativeMale += randSpeed;
		}

//		System.out.println( "Berechnete Speed: " + randSpeed );

//		if(true )
//			return 1.33;
		return randSpeed;
	}

	/**
	 * Returns the invertec probability of decisiveness. This is due to the fact
	 * that slackness is a value to stop an individual while decisiveness gives
	 * a value how fast an individual performs an action.
	 * @param pDecisiveness the decisiveness of the person
	 * @return the inverted probability.
	 */
	public double getSlacknessFromDecisiveness( double pDecisiveness ) {
		return 1 - pDecisiveness;
	}
}
