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
package util.random;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface GeneralRandom {

    public void setSeed( long seed );

		// do not give access to this method. it should be implemented by a
		// random generator, not by a wrapper interface.
		//protected int next(int bits);

		void nextBytes( byte[] bytes );

    int nextInt();

    int nextInt( int n );

		long nextLong();

    boolean nextBoolean();

    float nextFloat();

    double nextDouble();

    double nextGaussian();
	
		String getName();
		
		String getDesc();
}
