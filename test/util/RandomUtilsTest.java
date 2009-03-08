/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package util;

import static org.junit.Assert.*;

import org.junit.Test;

public class RandomUtilsTest {

	@Test
	public void testChooseRandomlyAbsolute() {
		final double NUMBER_OF_TRIES = 1000000;
		double[] probabilities = new double[10];
		double[] frequencies = new double[10];
		
		probabilities[0] =  5.0;
		probabilities[1] =  5.0;
		probabilities[2] =  5.0;
		probabilities[3] = 20.0;
		probabilities[4] = 15.0;
		probabilities[5] =  5.0;
		probabilities[6] =  0.0;
		probabilities[7] = 10.0;
		probabilities[8] = 20.0;
		probabilities[9] = 25.0;
		
		for(int i=0; i < NUMBER_OF_TRIES; i++){
			int randomIndex = util.random.RandomUtils.getInstance().chooseRandomlyAbsolute(probabilities);
			frequencies[randomIndex]++;
		}
		
		double sum = 0.0;
		for(int i=0; i < probabilities.length; i++){
			sum += probabilities[i];
		}
				
		for(int i=0; i < probabilities.length; i++){
			System.out.print("Index " + i + " "); 
			System.out.print("absolute value " + probabilities[i] + " ");
			System.out.print("probability " + (probabilities[i]/sum) + " ");
			System.out.println("frequency " + (frequencies[i] / (double)NUMBER_OF_TRIES));
		}
	}

}
