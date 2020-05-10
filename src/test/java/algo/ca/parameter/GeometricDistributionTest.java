/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package algo.ca.parameter;

import org.zetool.rndutils.distribution.discrete.GeometricDistribution;
import junit.framework.TestCase;

/**
 * The class {@code GeometricDistributionTest} ...
 *
 * @author Jan-Philipp Kappmeier
 */
public class GeometricDistributionTest extends TestCase {

    /**
     * Creates a new instance of {@code GeometricDistributionTest}.
     */
    public GeometricDistributionTest() {

    }

    public void testRandom1() {
        double p = 1. / 3;
        double q = 1 - p;

        int t = 4;
        int counter = 0;
        int gesamt = 1000000;
        double cumulative = 0;

        GeometricDistribution geom = new GeometricDistribution(0, 10000, p);
	//for( int k = 4; k <= 4; ++k ) {
        //System.out.println( k );
        counter = 0;
        for (int i = 1; i <= gesamt; ++i) {
            int X = geom.getNextRandom();
            int Y = geom.getNextRandom();

            if (X >= 2 * Y) {
                counter++;
            }

        }
        cumulative += ((double) counter / gesamt);
        //}

        System.out.println("P(X <= k) = " + cumulative);
        double p1 = Math.pow(q, (2 * t) - 1);
        double p2 = p * Math.pow(q, t - 1);
        System.out.println(p1 * p2);

        cumulative = 0;
        for (t = 1; t <= 10; ++t) {
            cumulative += p * Math.pow(q, 3 * t - 2);
            System.out.println(p * Math.pow(q, 3 * t - 2));
        }
        System.out.println("Kumuliert: " + cumulative);

        // Cumulative:
        System.out.println((-p * q) / (q * q * q - 1));

		// Andere Aufteilung
        // System.out.println( p * Math.pow( q, 3./2*t-2) );
        // X = k
        // System.out.println( p * Math.pow(q, k-1) );
        // X <= k
        // System.out.println( 1 -Math.pow(q,k));
        // X >= k
        // System.out.println( Math.pow( q, k-1 ) );
        // X > k
        // System.out.println( Math.pow( q, k ) );
        System.out.println(p / (q * (1 - Math.pow(q, 1.5))));
        System.out.println(p / (q - Math.pow(q, 2.5)));
    }

    /**
     * Returns the name of the class.
     *
     * @return the name of the class
     */
    @Override
    public String toString() {
        return "NewClass";
    }
}
