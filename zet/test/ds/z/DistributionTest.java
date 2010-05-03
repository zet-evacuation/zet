/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
 * DistributionTest.java
 *
 * Created on 27. November 2007, 22:18
 */

package ds.z;

import de.tu_berlin.math.coga.rndutils.distribution.continuous.NormalDistribution;
import de.tu_berlin.math.coga.rndutils.distribution.continuous.UniformDistribution;
import junit.framework.TestCase;

/**
 * Tests the distribution classes.
 * @author Jan-Philipp Kappmeier
 */
public class DistributionTest extends TestCase {
  
  /** Creates a new instance of Test1 */
  public DistributionTest() {
    super();
  }
  
  protected void setUp() throws Exception {
  }
    
  protected void tearDown() throws Exception {
  }
  
  public void testNormalDistribution() throws Exception {
    NormalDistribution n1;
    n1 = new NormalDistribution( -1, 2 );
    assertEquals( n1.getExpectedValue(), 0.0 );
    assertEquals( n1.getVariance(), 1.0 );
    assertEquals( n1.getMin(), -1.0 );
    assertEquals( n1.getMax(), 2.0 );

    n1 = new NormalDistribution();
    assertEquals( n1.getExpectedValue(), 0.0 );
    assertEquals( n1.getVariance(), 1.0 );
    assertEquals( n1.getMin(), -3.0 );
    assertEquals( n1.getMax(), 3.0 );
  }
  
  public void testUniformDistribution() throws Exception {
    UniformDistribution u1;
    u1 = new UniformDistribution();
    assertEquals( u1.getMin(), 0.0 );
    assertEquals( u1.getMax(), 1.0 );
    
    u1 = new UniformDistribution( 1, 2 );
    assertEquals( u1.getMin(), 1.0 );
    assertEquals( u1.getMax(), 2.0 );
  }
}
