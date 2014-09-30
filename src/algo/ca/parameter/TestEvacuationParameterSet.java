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

import de.tu_berlin.math.coga.rndutils.RandomUtils;
import de.tu_berlin.math.coga.rndutils.distribution.continuous.NormalDistribution;


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
	 * @return a random reaction time
	 */
	@Override
	public double getReactionTime() {
		// At a first step use normal distributed values

		NormalDistribution normal = new NormalDistribution( 60, 30, 0, 180 );
		double randReactionTime = normal.getNextRandom();
		if( true )
			throw new IllegalStateException( "Diese Methode sollte nicht aufgerufen werden. Bitte setzen sie die Reaktionszeit im Editor." );
		return randReactionTime;
	}

	/**
	 * Calculates the maximal speed for a person dependingon the speed-values
	 * from the rimea test suite.
	 * @param pAge
	 * @return the maximal speed as percentage of the overall maximal speed for the simulation run
	 */
	@Override
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

		final NormalDistribution normal = new NormalDistribution( maxSpeedExpected, sigmaSquared, ageArray[16], ABSOLUTE_MAX_SPEED );
		double randSpeed = normal.getNextRandom();

		// Change speeds for male and female individuals:
		// + 5% for male, -5% for female
		if( RandomUtils.getInstance().binaryDecision( 0.5 ) ) {
			randSpeed *= 1.05;
			counterFemale++;
			cumulativeFemale += randSpeed;
		} else {
			randSpeed *= 0.95;
			counterMale++;
			cumulativeMale += randSpeed;
		}

		//double randSpeed = maxSpeedExpected;
		double maxSpeed = randSpeed / ABSOLUTE_MAX_SPEED;
		if( maxSpeed > 1 ) {
			maxSpeed = 1;
		//System.err.println( "Maximale geschw: " + maxSpeed );
		}

		// Correction of 0.2
//		maxSpeed = Math.max( 0.6, maxSpeed-0.4 );
//		cumulativeSpeed += (maxSpeed * ABSOLUTE_MAX_SPEED);

		//System.out.println( "First one has speed " + (maxSpeed * ABSOLUTE_MAX_SPEED) );
		//System.out.println( "Average speed for " + counter + " persons: " + (cumulativeSpeed / counter) + " counterMale/s. (Should be 1.3x)" );

    //randSpeed = 1.8120037611;
    
    speeds += randSpeed;
    count++;
    
    System.out.println( "Speed: " + randSpeed );
    
		return randSpeed;
	}
  
  public static double speeds;
  public static int count;

	@Override
	protected double exhaustionWeightOnSpeed() {
//		return super.exhaustionWeightOnSpeed();
		return 0;
	}

}
