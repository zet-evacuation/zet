/*
 * DistributionTest.java
 *
 * Created on 27. November 2007, 22:18
 */

package ds.z;

import junit.framework.TestCase;
import util.random.distributions.NormalDistribution;
import util.random.distributions.UniformDistribution;

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
