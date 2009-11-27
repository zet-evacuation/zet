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

/**
 * TestEvacuationParameterSet.java
 * Created: Oct 29, 2009,12:56:41 PM
 */
package algo.ca.parameter;

import de.tu_berlin.math.coga.rndutils.distribution.continuous.NormalDistribution;
import util.random.distributions.ExponentialDistribution;


/**
 * A parameter set with relaxed settings. The speed is less and the waiting
 * times are higher. This can be used to model evacuation tests, where people
 * usually tend to not leave the building as fast as possible.
 * @author Jan-Philipp Kappmeier
 */
public class TestEvacuationParameterSet extends DefaultParameterSet {

	/**
	 * Returns a random reaction time. The reaction time is independendly selected
	 * from the age. The same result as {@link #getReactionTime()}
	 * @param age the age (is ignored!)
	 * @return a random reaction time (without influence of age).
	 */
	@Override
	public double getReactionTimeFromAge( double age ) {
		return getReactionTime();
	}

	/**
	 * Returns a random reaction time.
	 * @return
	 */
	@Override
	public double getReactionTime() {
		// At a first step use normal distributed values
		
		NormalDistribution normal = new NormalDistribution( 60, 900, 0, 180 );
		double randReactionTime = normal.getNextRandom();
		System.out.println( "Reactiontime: " + randReactionTime );

//		ExponentialDistribution exp = new ExponentialDistribution( 0.016666666667 );
//		exp.setParameter( 0, 180 );
//		double randReactionTime = exp.getNextRandom();
		System.out.println( "Reactiontime: " + randReactionTime );

		return randReactionTime;
	}

	/**
	 * Calculates the maximal speed for a person dependingon the speed-values
	 * from the rimea test suite.
	 * @param pAge
	 * @return the maximal speed as percentage of the overall maximal speed for the simulation run
	 */
//	static double cumulativeSpeed = 0;
//	static int counter = 0;

	@Override
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
		NormalDistribution normal = new NormalDistribution( maxSpeedExpected*0.9, 0.26, ageArray[0], ABSOLUTE_MAX_SPEED );
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

		System.out.println( "First one has age " + pAge + " and speed " + (maxSpeed * ABSOLUTE_MAX_SPEED) );
		System.out.println( "Average speed for " + counter + " persons: " + (cumulativeSpeed / counter) + " m/s. (Should be 1.3x)" );



		return maxSpeed;
	}
	

}
